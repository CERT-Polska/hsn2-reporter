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

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;
import pl.nask.hsn2.wrappers.ObjectDataReportingWrapper;

public class JsonRendererImpl implements JsonRenderer {
    private final TemplateRegistry registry;

    public JsonRendererImpl(TemplateRegistry templateRegistry) {
        this.registry = templateRegistry;
    }

    @Override
    public JsonRenderingResult render(ObjectDataReportingWrapper dataWrapper, String templateName) throws ResourceException {

        TemplateCompileOptions options = templateCompileOptions();
        List<JsonAttachment> attachments = new ArrayList<JsonAttachment>();
        IProgramBuilder programBuilder = new HsnProgramBuilder(HsnFormatters.getInstance(attachments) );
        String template = registry.getTemplate(templateName);
        Template t = new Template(template, programBuilder, options);
        
        String jsonWithAdditionalCommas = t.expand(dataWrapper);
        JSONObject jo = (JSONObject) JSONValue.parse(jsonWithAdditionalCommas);
        String jsonToString = jo.toString();
        return new JsonRenderingResult(jsonToString, attachments);
    }

    private TemplateCompileOptions templateCompileOptions() {
        TemplateCompileOptions options = new TemplateCompileOptions();
        options.setMeta("<<>>");
        options.setDefaultFormatter("json");

        return options;
    }
}
