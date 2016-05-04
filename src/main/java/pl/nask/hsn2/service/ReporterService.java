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
import java.net.MalformedURLException;
import java.net.URL;

import jsontemplate.HsnFormatters;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.CommandLineParams;
import pl.nask.hsn2.ServiceMain;
import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.connector.AMQP.ObjectStoreConnectorImpl;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.connector.REST.DataStoreConnectorImpl;
import pl.nask.hsn2.task.TaskFactory;

public final class ReporterService extends ServiceMain{
	private static final Logger LOGGER = LoggerFactory.getLogger(ReporterService.class);

	public static void main(final String[] args) {
		ReporterService rs = new ReporterService();
		rs.init(new DefaultDaemonContext(args));
		rs.start();
	}

	//TODO: this method probably should be in different place
	public static CouchDbClient prepareCouchDbClient(String hostname) throws IOException {
		try {
			URL url = new URL(hostname);
			return new CouchDbClient(url.getPath(), true, url.getProtocol(), url.getHost(), url.getPort(), null, null);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL: " + hostname, e);
		} catch (CouchDbException e) {
			LOGGER.error("Cannot connect do CouchDB server: {}", hostname);
			throw new IOException(e.getMessage(), e);
		}
	}

	//TODO: this method probably should be in different place
	private static void connectAndSetObjectStoreConnectorInFormatters(ReporterCommandLineParams cmd) {
		try {
			HsnFormatters.setOsConnector(new ObjectStoreConnectorImpl(cmd.getConnectorAddress(), cmd.getObjectStoreQueueName()));
		} catch (BusException e) {
			LOGGER.error("Error in connection with ObjectStore.", e);
		}
	}

	//TODO: there should be one client for couch - delete this method
	private void checkCouchDB() {
		ReporterCommandLineParams cmd  = (ReporterCommandLineParams)getCommandLineParams();

		CouchDbClient couchDbClient = null;
		try {
			couchDbClient = prepareCouchDbClient(cmd.getDatabaseAddress());
			LOGGER.debug("CouchDB connect test - OK.");
		} catch (IOException e) {
			LOGGER.error("Shutting down {} service.Error connecting CouchDB", cmd.getServiceName());
			throw new IllegalStateException(e);
		} finally {
			if (couchDbClient != null) {
				couchDbClient.shutdown();
				couchDbClient = null;
			}
		}
	}

	@Override
	protected void prepareService() {
	}

	@Override
	protected Class<? extends TaskFactory> initializeTaskFactory() {
		checkCouchDB();
		ReporterCommandLineParams cmd  = (ReporterCommandLineParams)getCommandLineParams();

		TemplateRegistry templateRegistry = new CachingTemplateRegistry(cmd.getUseCacheForTemplates(), cmd.getJsontPath());
		JsonRenderer jsonRenderer = new JsonRendererImpl(templateRegistry);

		DataStoreConnector dsConnector = new DataStoreConnectorImpl(cmd.getFormattedDataStoreAddress());

		connectAndSetObjectStoreConnectorInFormatters(cmd);
		ReporterTaskFactory.prepereForAllThreads(jsonRenderer, dsConnector, cmd.getDatabaseAddress());
		return ReporterTaskFactory.class;
	}

	@Override
	protected CommandLineParams newCommandLineParams() {
		return new ReporterCommandLineParams();
	}
}
