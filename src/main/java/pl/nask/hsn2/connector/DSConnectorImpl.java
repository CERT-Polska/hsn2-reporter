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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.connector.REST.DataStoreConnectorImpl;
import pl.nask.hsn2.protobuff.DataStore.DataResponse;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;

public class DSConnectorImpl implements DSConnector {

    private DataStoreConnector dataStoreConnector;

    public DSConnectorImpl(String address) {
        this.dataStoreConnector = new DataStoreConnectorImpl(address);
    }

    @Override
    public GeneratedMessage getObject(long jobId, long key, String msgType) throws ResourceException, StorageException {
        Class<GeneratedMessage> clazz = null;
        try {
            DataResponse res = dataStoreConnector.sendGet(jobId, key);

            clazz = (Class<GeneratedMessage>) Class.forName("pl.nask.hsn2.protobuff.Resources$" + msgType);

            Method parseFrom = clazz.getMethod("parseFrom", ByteString.class);
            GeneratedMessage msg = (GeneratedMessage) parseFrom.invoke(null, res.getData());

            return msg;
        } catch (IOException e) {
            throw new StorageException("Storage error (IOException)", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Incorrect msgType", e);
        } catch (SecurityException e) {
            throw new IllegalStateException("Couldn't load a class for " + msgType + " due to security exception", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Couldn't run a parseFrom method from " + msgType + " - no such method in class " + clazz, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("InvocationTargetException", e);
        }
    }
}
