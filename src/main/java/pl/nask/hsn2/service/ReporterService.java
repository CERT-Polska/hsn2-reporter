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
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.URL;
import jsontemplate.HsnFormatters;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.GenericService;
import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.connector.AMQP.ObjectStoreConnectorImpl;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.connector.REST.DataStoreConnectorImpl;

public final class ReporterService implements Daemon {

	private final static Logger LOG = LoggerFactory.getLogger(ReporterService.class);

	private ReporterCommandLineParams cmd = null;
	private Thread serviceRunner = null;
	
	public static void main(final String[] args) throws DaemonInitException, Exception {
		ReporterService rs = new ReporterService();
		rs.init(new DaemonContext() {
			
			@Override
			public DaemonController getController() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String[] getArguments() {
				return args;
			}
		});
		rs.start();
		rs.serviceRunner.join();
		rs.stop();
		rs.destroy();
	}

	public static CouchDbClient prepareCouchDbClient(String hostname) throws IOException {
		try {
			URL url = new URL(hostname);
			CouchDbClient client = new CouchDbClient(url.getPath(), true, url.getProtocol(), url.getHost(), url.getPort(), null, null);
			return client;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed URL: " + hostname, e);
		} catch (CouchDbException e) {
			LOG.error("Cannot connect do CouchDB server: {}", hostname);
			throw new IOException(e.getMessage());
		}
	}

	private static void connectAndSetObjectStoreConnectorInFormatters(ReporterCommandLineParams cmd){
		try {
			HsnFormatters.setOsConnector(new ObjectStoreConnectorImpl(cmd.getConnectorAddress(), cmd.getObjectStoreQueueName()));
		} catch (BusException e) {
			LOG.error("Error in connection with ObjectStore.",e);
		}
	}

	private static ReporterCommandLineParams parseArguments(String[] args) {
		ReporterCommandLineParams params = new ReporterCommandLineParams();
		params.parseParams(args);

		return params;
	}

	

	@Override
	public void init(DaemonContext context) throws DaemonInitException,	Exception {
		 this.cmd  = parseArguments(context.getArguments());

		CouchDbClient couchDbClient = null;
		try {
			couchDbClient = prepareCouchDbClient(cmd.getDatabaseAddress());
			LOG.debug("CouchDB connect test - OK.");
		} catch (IOException e) {
			LOG.error("Shutting down {} service.Error connecting CouchDB",cmd.getServiceName());
//			System.exit(1);
			throw e;
		} finally {
			if (couchDbClient != null) {
				couchDbClient.shutdown();
				couchDbClient = null;
			}
		}

		
	}

	@Override
	public void start() throws Exception {
		TemplateRegistry templateRegistry = new CachingTemplateRegistry(cmd.getUseCacheForTemplates(), cmd.getJsontPath());
		JsonRenderer jsonRenderer = new JsonRendererImpl(templateRegistry);

		DataStoreConnector dsConnector = new DataStoreConnectorImpl(cmd.getFormattedDataStoreAddress());

		connectAndSetObjectStoreConnectorInFormatters(cmd);


		final GenericService service = new GenericService(new ReporterTaskFactory(jsonRenderer, dsConnector, cmd.getDatabaseAddress()), cmd.getMaxThreads(), cmd.getRbtCommonExchangeName(), cmd.getRbtNotifyExchangeName());
		cmd.applyArguments(service);
		serviceRunner = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						System.exit(1);
					}
				});
				try {
					service.run();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new RuntimeException(e);
				}
				
			}
		},"Reporter-Service");
		serviceRunner.start();
		
		
		
	}

	@Override
	public void stop() throws InterruptedException {
		serviceRunner.interrupt();
		serviceRunner.join(10000);
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
