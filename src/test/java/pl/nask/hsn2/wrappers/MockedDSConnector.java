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

package pl.nask.hsn2.wrappers;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.REST.DataResponse;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.protobuff.Resources.JSContextResults;

import com.google.protobuf.GeneratedMessage;

public class MockedDSConnector implements DataStoreConnector {
    private Map<Long, GeneratedMessage> registry = new HashMap<Long, GeneratedMessage>();

    public void addData(long id, JSContextResults msg) {
        registry.put(id, msg);
    }

    @Override
    public GeneratedMessage getResourceAsMsg(long jobId, long key, String msgType) throws ResourceException {
        if (registry.containsKey(key)) {
            GeneratedMessage obj = registry.get(key);
            if (obj.getClass().getSimpleName().equals(msgType)) {
                return obj;
            } else {
                throw new ResourceException(String.format("Unexpected object type. Got %s, expected %s", obj.getClass().getSimpleName(), msgType));
            }
        } else {
            throw new ResourceException("No such object in DS: " + key);
        }
    }

    @Override
    public InputStream sendGet(long jobId, long dataId) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataResponse sendPost(byte[] data, long jobId) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataResponse sendPost(InputStream dataInputStream, long jobId)
            throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getResourceAsStream(long jobId, long referenceId)
            throws ResourceException, StorageException {
        throw new UnsupportedOperationException();
    }

	@Override
	public boolean ping() {
		return true;
	}

}
