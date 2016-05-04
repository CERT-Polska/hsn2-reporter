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
    private static final String TEMPLATE_PARAM = "template";
    private static final String SERVICE_NAME_PARAM = "serviceName";
    private static DataStoreConnector dsConnector;
    private static JsonRenderer jsonRenderer;
    private static String couchDbServerHostname;

    public ReporterTaskFactory() {
    }

    @Override
    public final Task newTask(TaskContext jobContext, ParametersWrapper parameters, ObjectDataWrapper data) throws ParameterException {
        long jobId = jobContext.getJobId();

        try {
        	//TODO: probably this should be create only for each thread
        	CouchDbClient couchDbClient = ReporterService.prepareCouchDbClient(couchDbServerHostname);
        	CouchDbConnector couchDbConnector = new CouchDbConnectorImpl(couchDbClient, dsConnector, jobId, data.getId(), parameters.get(SERVICE_NAME_PARAM));

			return new ReporterTask(parameters.get(TEMPLATE_PARAM), new ObjectDataReportingWrapper(jobId, data, dsConnector), jsonRenderer, couchDbConnector);
		} catch (IOException e) {
			throw new ParameterException("Could not connect to CouchDB. Is database hostname address valid?", e);
		}
    }

	public static void prepereForAllThreads(JsonRenderer jsonRenderer, DataStoreConnector dsConnector, String couchDbServerHostname) {
		ReporterTaskFactory.dsConnector = dsConnector;
		ReporterTaskFactory.jsonRenderer = jsonRenderer;
		ReporterTaskFactory.couchDbServerHostname = couchDbServerHostname;
	}
}
