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

package pl.nask.hsn2.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.connector.REST.RestRequestor;
import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;

public class CouchDbConnectorImpl implements CouchDbConnector {
    private static final int SLEEP_TIME = 4000;
	private static final int SAVE_ATTACHMENT_REPEAT_COUNT = 10;
	private static final int COUCHDB_CREATED_STATUS_CODE = 201;
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbConnectorImpl.class);
    private final DataStoreConnector dsConnector;
    private final String documentId;
    private final CouchDbClient client;
    private final String couchURI;

    public CouchDbConnectorImpl(CouchDbClient couchDbClient, DataStoreConnector dsConnector, long jobId, long objectId, String serviceName) {
        this.client = couchDbClient;
        this.dsConnector = dsConnector;
        this.couchURI = client.getDBUri().toString();
        
        if (serviceName.isEmpty()) {
            documentId = String.format("%s:%s", jobId, objectId);
        } else {
            documentId = String.format("%s:%s:%s", jobId, objectId, serviceName);
        }
    }

    @Override
    public void saveDocument(String document, List<JsonAttachment> attachments) throws ResourceException, StorageException {
		String revision = doesDocumentExists();
		if (revision == null) {
			revision = addDocument(document);
		} else {
			revision = updateDocument(document, revision);
		}
		addAttachments(attachments, revision);
    }

	/**
	 * Checks if document exists.
	 * 
	 * @return Revision if it exists or null if it does not exists.
	 * @throws StorageException Storage exception.
	 */
	private String doesDocumentExists() throws StorageException {
		RestRequestor restClient = null;
		try {
			restClient = RestRequestor.head(couchURI + "/" + documentId);

			// get revision - if document has not been found it will be null
			String revision = restClient.getHeaderField("Etag");
			if (revision != null) {
				// revision in header contains quotation marks
				// so we have to remove it
				revision = revision.substring(1, revision.length() - 1);
			}
			return revision;
		} catch (IOException e) {
			throw new StorageException("Exception while checking for CouchDB document", e);
		} finally {
			if (restClient != null) {
				restClient.close();
			}
		}
	}

	private String updateDocument(String jsonDocument, String revision) throws StorageException {
		LOGGER.info("Will use {} (PUT) to update document", couchURI);
		JSONObject json = (JSONObject) JSONValue.parse(jsonDocument);
		json.put("_id", documentId);
		json.put("_rev", revision);
		try {
			Response response = client.update(json);
			String newRevision = response.getRev();
			LOGGER.debug("Updated: \n{}", jsonDocument);
			LOGGER.info("Document updated:  (old revision = {}, new revision = {})\n{}", new Object[] {documentId, revision, newRevision});
			return newRevision;
		} catch (org.lightcouch.CouchDbException e) {
			throw new StorageException("Exception while updating CouchDB document", e);
		}
	}

    private String addDocument(String document) throws StorageException {
        RestRequestor restClient = null;
        try {
            LOGGER.info("Will use {} (PUT) to add document", couchURI);
            restClient = RestRequestor.put(couchURI + "/" + documentId, "application/json;charset=utf-8", document.getBytes());
            int code = restClient.getResponseCode();
            String msg = restClient.getResponseMessage();
            LOGGER.debug("CouchDB responded with {}, {}", code, msg);
            if (code != COUCHDB_CREATED_STATUS_CODE) {
            	throw new StorageException(String.format("Expected database to respond with code 201, got %s. Message is: %s", code, msg));
            }
            LOGGER.debug("Saved: \n{}", document);
            String revision = readRevision(restClient.getInputStream());
            LOGGER.info("Document saved: id = {} revision = {}", revision, documentId);
            return revision;
        } catch (IOException e) {
            throw new StorageException("Error while connecting to the database", e);
        } finally {
            if (restClient != null) {
            	restClient.close();
            }
        }
    }

    private String readRevision(InputStream inputStream) throws IOException {
    	Reader reader = new InputStreamReader(inputStream);
    	JSONObject object = (JSONObject) JSONValue.parse(reader);
    	return (String) object.get("rev");
    }

    private void addAttachments(List<JsonAttachment> attachments, String revision) throws ResourceException, StorageException {
    	for (JsonAttachment attachment: attachments) {
    		revision = saveAttachment(attachment, revision);
    	}
    }

	private String saveAttachment(JsonAttachment attachment, String revision) throws StorageException, ResourceException {
		Exception exception = null;
		InputStream is = null;
		try {
			for (int i = 0; i < SAVE_ATTACHMENT_REPEAT_COUNT; i++) {
				is = dsConnector.getResourceAsStream(attachment.getJobId(), attachment.getKey());
				try {
					Response res = client.saveAttachment(is, attachment.getName(), "application/octet-stream", documentId, revision);
					return res.getRev();
				} catch (Exception e) {
					IOUtils.closeQuietly(is);
					
					Object[] warnDetails = {attachment.getName(), documentId, revision, (SAVE_ATTACHMENT_REPEAT_COUNT - i), e.getMessage()};
					LOGGER.warn("Problem with adding attachment:{}, to doc:{} rev:{}. attempts left: {} cause: {}",	warnDetails);
					LOGGER.debug(e.getMessage(), e);
					
					if (i < SAVE_ATTACHMENT_REPEAT_COUNT) {
						try {
							Thread.sleep(SLEEP_TIME);
						} catch (InterruptedException e1) {
							LOGGER.debug("Sleep interrupted", e1);
						}
					} else {
						exception = e;
					}
				}
			}
			throw new StorageException("Error adding attachment!", exception);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

    @Override
    public void clientShutdown() {
    	client.shutdown();
    }
}
