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

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.InputDataException;
import pl.nask.hsn2.ParameterException;
import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.CouchDbConnector;
import pl.nask.hsn2.task.Task;
import pl.nask.hsn2.wrappers.ObjectDataReportingWrapper;

public class ReporterTask implements Task {
	private static final Logger LOG = LoggerFactory.getLogger(ReporterTask.class);
	private final String templateName;
	private final JsonRenderer jsonRenderer;
	private final CouchDbConnector couchDbConnector;
	private final ObjectDataReportingWrapper data;

	public ReporterTask(String templateName, ObjectDataReportingWrapper data, JsonRenderer jsonRenderer, CouchDbConnector couchDbConnector) {
		this.templateName = templateName;
		this.data = data;
		this.jsonRenderer = jsonRenderer;
		this.couchDbConnector = couchDbConnector;
	}

	@Override
	public boolean takesMuchTime() {
		return false;
	}

	@Override
	public void process() throws ParameterException, ResourceException, StorageException, InputDataException {
		try {
			// try to render json document
			JsonRenderingResult result = null;
			try {
				LOG.info("Processing object with id={} using template {}", data.getId(), templateName);
				result = jsonRenderer.render(data, templateName);
			} catch (Exception e) {
				LOG.warn("Error rendering JSON document (will send TaskCompleted anyway...) !", e);
				throw new InputDataException("Error rendering JSON document", e);
			}

			// process rendered document
			try {
				result.validate();
				couchDbConnector.saveDocument(result.getDocument(), result.getAttachments());
			} catch (ParseException e) {
				String msg = "Error processing generated JSON document: " + e.getMessage();
				LOG.error(msg, e);
				throw new ResourceException(msg, e);
			}
		} finally {
			couchDbConnector.clientShutdown();
		}
	}
}
