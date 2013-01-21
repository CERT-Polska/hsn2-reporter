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

import java.util.List;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;

public class JsonRenderingResult {
	private final static Logger LOG = LoggerFactory.getLogger(JsonRenderingResult.class);

	private final String document;
	private final List<JsonAttachment> attachments;

	public JsonRenderingResult(String document, List<JsonAttachment> attachments) {
		this.document = document;
		this.attachments = attachments;
	}

	public List<JsonAttachment> getAttachments() {
		return attachments;
	}

	public String getDocument() {
		return document;
	}

	public void validate() throws ParseException {
		String trimedDocument = document.trim();
		LOG.debug("Got document to validate: \n{}\n", trimedDocument);
		try {
			JSONValue.parseWithException(trimedDocument);
		} catch (ParseException pe) {
			throw pe;
		}
	}
}