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
import org.lightcouch.DocumentConflictException;
import org.lightcouch.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.connector.REST.RestRequestor;
import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;

public class CouchDbConnectorImpl implements CouchDbConnector {
    private final static Logger LOGGER = LoggerFactory.getLogger(CouchDbConnectorImpl.class);
    private final DataStoreConnector dsConnector;
    private final String documentId;
    private final CouchDbClient client;

    public CouchDbConnectorImpl(CouchDbClient client, DataStoreConnector dsConnector, long jobId, long objectId, String serviceName) {
        this.client = client;
        this.dsConnector = dsConnector;

        if (serviceName.isEmpty()) {
            this.documentId = String.format("%s:%s", jobId, objectId);
        }
        else {
            this.documentId = String.format("%s:%s:%s", jobId, objectId, serviceName);
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
		addAttachments(attachments,revision);
    }

	/**
	 * Checks if document exists.
	 * 
	 * @return Revision if it exists or null if it does not exists.
	 * @throws StorageException
	 */
	private String doesDocumentExists() throws StorageException {
		RestRequestor restClient = null;
		try {
			restClient = RestRequestor.head(client.getDBUri().toString() + "/" + documentId);

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
		LOGGER.info("Will use {} (PUT) to update document", client.getDBUri().toString());
		JSONObject json = (JSONObject) JSONValue.parse(jsonDocument);
		json.put("_id", documentId);
		json.put("_rev", revision);
		try {
			Response response = client.update(json);
			String newRevision = response.getRev();
			LOGGER.info("Document updated (old revision = {}, new revision = {})\n{}", new Object[] { revision, newRevision, jsonDocument });
			return newRevision;
		} catch (org.lightcouch.CouchDbException e) {
			throw new StorageException("Exception while updating CouchDB document", e);
		}
	}

    private String addDocument(String document) throws StorageException {
        RestRequestor restClient = null;
        try {
            LOGGER.debug("Will use {} (PUT) to add document", client.getDBUri().toString());
            restClient = RestRequestor.put(client.getDBUri().toString() + "/" + documentId, "application/json;charset=utf-8", document.getBytes());
            int code = restClient.getResponseCode();
            String msg = restClient.getResponseMessage();
            LOGGER.debug("CouchDB responded with {}, {}", code, msg);
            if (code != 201) {
            	throw new StorageException(String.format("Expected database to respond with code 201, got %s. Message is: %s", code, msg));
            }
            LOGGER.debug("Saved: \n{}", document);
            String revision = readRevision(restClient.getInputStream());
            return revision;
        } catch (IOException e) {
            throw new StorageException("Error while connecting to the database", e);
        } finally {
            if (restClient != null){
            	restClient.close();
            }
        }
    }

    private String readRevision(InputStream inputStream) throws IOException{
    	Reader reader = new InputStreamReader(inputStream);
    	JSONObject object = (JSONObject) JSONValue.parse(reader);
    	return (String) object.get("rev");
    }

    private void addAttachments(List<JsonAttachment> attachments, String revision) throws ResourceException, StorageException {
    	for (JsonAttachment attachment: attachments) {
    		try {
                revision = saveAttachment(attachment, revision);
	        } catch (DocumentConflictException e) {
	            throw new StorageException("Error adding attachment: database reported a document conflict", e);
	        }
    	}
    }

    private String saveAttachment(JsonAttachment attachment, String revision) throws StorageException, ResourceException{
    	int repeatCount = 10;
    	Exception exception = null;
    	InputStream is = null;
    	try{
	    	for(int i = 0; i < repeatCount; i++){
	    		is = dsConnector.getResourceAsStream(attachment.getJobId(), attachment.getKey());
	    		try{
	    			Response res = client.saveAttachment(is, attachment.getName(), "application/octet-stream", documentId, revision);
	                return res.getRev();
	    		} catch (Exception e) {
					IOUtils.closeQuietly(is);
	    			if(i < repeatCount){
						try {
							Thread.sleep(4000);
						} catch (InterruptedException e1) {	}
						LOGGER.debug("Problem with adding attachment:{}, to doc:{} rev:{}. attempts left: {}",new Object[]{attachment.getName(), documentId, revision, (repeatCount - i)});
						LOGGER.debug(e.getMessage(),e);
					}
					else{
						exception = e;
					}
				}
	    	}
	    	throw new StorageException("Error adding attachment!",exception);
    	}
    	finally{
    		IOUtils.closeQuietly(is);
    	}
    }

    @Override
    public void clientShutdown() {
    	this.client.shutdown();
    }
}