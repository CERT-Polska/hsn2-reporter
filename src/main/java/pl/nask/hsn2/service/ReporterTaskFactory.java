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

import java.io.IOException;

import org.lightcouch.CouchDbClient;

import pl.nask.hsn2.ParameterException;
import pl.nask.hsn2.TaskContext;
import pl.nask.hsn2.connector.CouchDbConnector;
import pl.nask.hsn2.connector.CouchDbConnectorImpl;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.task.Task;
import pl.nask.hsn2.task.TaskFactory;
import pl.nask.hsn2.wrappers.ObjectDataReportingWrapper;
import pl.nask.hsn2.wrappers.ObjectDataWrapper;
import pl.nask.hsn2.wrappers.ParametersWrapper;

public class ReporterTaskFactory implements TaskFactory {
    private final static String TEMPLATE_PARAM = "template";
    private final static String SERVICE_NAME_PARAM = "serviceName";

    private final DataStoreConnector dsConnector;
    private final JsonRenderer jsonRenderer;
    private final String couchDbServerHostname;

    public ReporterTaskFactory(JsonRenderer jsonRenderer, DataStoreConnector dsConnector, String couchDbServerHostname) {
        this.dsConnector = dsConnector;
        this.jsonRenderer = jsonRenderer;
        this.couchDbServerHostname = couchDbServerHostname;
    }

    @Override
    public Task newTask(TaskContext jobContext, ParametersWrapper parameters, ObjectDataWrapper data) throws ParameterException {
        long jobId = jobContext.getJobId();

        try {
        	CouchDbClient couchDbClient = ReporterService.prepareCouchDbClient(couchDbServerHostname);
			CouchDbConnector couchDbConnector = new CouchDbConnectorImpl(couchDbClient, dsConnector, jobId, data.getId(), parameters.get(SERVICE_NAME_PARAM));
			return new ReporterTask(jobContext, parameters.get(TEMPLATE_PARAM), new ObjectDataReportingWrapper(jobId, data, dsConnector), jsonRenderer, couchDbConnector);
		} catch (IOException e) {
			throw new ParameterException("Could not connect to CouchDB. Is database hostname address valid?");
		}
    }
}