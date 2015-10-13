/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2.service;

import java.util.ArrayList;
import java.util.List;

import jsontemplate.HsnFormatters;
import jsontemplate.HsnProgramBuilder;
import jsontemplate.IProgramBuilder;
import jsontemplate.Template;
import jsontemplate.TemplateCompileOptions;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;
import pl.nask.hsn2.wrappers.ObjectDataReportingWrapper;

public class JsonRendererImpl implements JsonRenderer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonRendererImpl.class);
	
    private final TemplateRegistry registry;

    public JsonRendererImpl(TemplateRegistry templateRegistry) {
        this.registry = templateRegistry;
    }

    @Override
    public JsonRenderingResult render(ObjectDataReportingWrapper dataWrapper, String templateName) throws ResourceException {
        TemplateCompileOptions options = templateCompileOptions();
        List<JsonAttachment> attachments = new ArrayList<JsonAttachment>();
        IProgramBuilder programBuilder = new HsnProgramBuilder(HsnFormatters.getInstance(attachments));

        String template = registry.getTemplate(templateName);
        LOGGER.debug("Template content: {}", template);
        
        Template t = new Template(template, programBuilder, options);
        
        String jsonWithAdditionalCommas = t.expand(dataWrapper);
        LOGGER.debug("jsonWithAdditionalCommas: {}", jsonWithAdditionalCommas);

        JSONObject jo;
		try {
			jo = (JSONObject) JSONValue.parseWithException(jsonWithAdditionalCommas);
		} catch (ParseException e) {
			throw new ResourceException("Cannot parse object data! id: " + dataWrapper.getId(), e);
		} catch (Exception e) {
			throw new ResourceException("Error! id: " + dataWrapper.getId(), e);
		}
        String jsonToString = jo.toString();
        LOGGER.debug("jsonToString: {}", jsonToString);
        return new JsonRenderingResult(jsonToString, attachments);
    }

    private TemplateCompileOptions templateCompileOptions() {
        TemplateCompileOptions options = new TemplateCompileOptions();
        options.setMeta("<<>>");
        options.setDefaultFormatter("json");
        return options;
    }
}
