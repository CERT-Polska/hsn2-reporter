/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
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

package jsontemplate;

import java.util.List;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;
import pl.nask.hsn2.service.CachingTemplateRegistry;
import pl.nask.hsn2.service.TemplateRegistry;

/**
 * Template utils.
 */
public final class TemplateUtils {
	/**
	 * Private constructor to prevent instantiation.
	 */
	private TemplateUtils() {
	}

    private static TemplateRegistry registry = new CachingTemplateRegistry(true, null);

    public static  Template createTemplate(String strTemplate, List<JsonAttachment> attachments) {
        TemplateCompileOptions options = new TemplateCompileOptions();
        options.setMeta("<<>>");
        options.setDefaultFormatter("json");
        IFormatterResolver moreFormatters = HsnFormatters.getInstance(attachments);
        IProgramBuilder programBuilder = new HsnProgramBuilder(moreFormatters);
        return new Template(strTemplate, programBuilder, options);
    }

    public static String loadTemplate(String templateName) throws ResourceException {
       return registry.getTemplate(templateName);
    }
}
