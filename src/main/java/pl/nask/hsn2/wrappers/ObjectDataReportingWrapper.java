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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.protobuff.Object.Reference;

import com.google.protobuf.GeneratedMessage;

public class ObjectDataReportingWrapper extends IncompleteMap<String, Object> implements HsnContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectDataReportingWrapper.class);

    private final ObjectDataWrapper data;
    private final DataStoreConnector dsConnector;

    private final Map<String, String> typeMappings = new HashMap<String, String>();

    private Map<String, Object> dataMap;
    private final Map<String, Object> expandedObjectCache = new HashMap<String, Object>();
    private final long jobId;

    public ObjectDataReportingWrapper(long jobId, ObjectDataWrapper data, DataStoreConnector dsConnector) {
        this.jobId = jobId;
        this.data = data;
        this.dsConnector = dsConnector;
    }

    public final long getId() {
        return data.getId();
    }

    private void ensureDataMapInitialized() {
        if (dataMap == null) {
            dataMap = new HashMap<String, Object>();
            for (String attributeName: data.getAttributeNames()) {
                dataMap.put(attributeName, get(attributeName));
            }
        }
    }

    @Override
    public final boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public final boolean containsKey(Object key) {
        if (key instanceof String) {
            return data.getAttributeWrapper((String) key) != null;
        } else {
            return false;
        }
    }

    @Override
    public final Object get(Object key) {
        if (containsKey(key)) {
            AttributeWrapper attrWrapper = data.getAttributeWrapper((String) key);
            Object value = attrWrapper.getValue();
            if (attrWrapper.isReference()) {
                return expandReference((String) key, value);
            } else {
                return value;
            }
        } else {
            return null;
        }
    }

    private Object expandReference(String key, Object value) {
        Object result = expandedObjectCache.get(key);
        if (result != null) {
			return result;
		}

        String msgType = typeMappings.get(key);
        if (msgType == null) {
            if (value instanceof Reference) {
                // corner case: don't cache it in case if someone sets the struct type later!
                return ProtoMsgMap.asMap(jobId, data.getId(), (Reference) value);
            } else {
                return value;
            }
        }

        if (value instanceof Reference) {
            Reference ref = (Reference) value;
            ref.getKey();
            ref.getStore();
            try {
                GeneratedMessage msg = dsConnector.getResourceAsMsg(jobId, ref.getKey(), msgType);
                Map<String, Object> map = ProtoMsgMap.asMap(jobId, getId(), msg);
                expandedObjectCache.put(key, map);
                return map;
            } catch (ResourceException e) {
                LOGGER.error("Error expanding reference (value=(key:{}, store:{}), key={}, type={}.", new Object[]{ref.getKey(), ref.getStore(), key, msgType });
                LOGGER.error("Exception", e);
                return value;
            } catch (StorageException e) {
                LOGGER.error("Error expanding reference (value=(key:{}, store:{}), key={}, type={}.", new Object[]{ref.getKey(), ref.getStore(), key, msgType });
                LOGGER.error("Exception", e);
                return value;
            } catch (Exception e) {
                LOGGER.error("Error expanding reference (value=(key:{}, store:{}), key={}, type={}.", new Object[]{ref.getKey(), ref.getStore(), key, msgType });
                LOGGER.error("Exception", e);
                return value;
            }
        } else {
            return value;
        }
    }

    @Override
    public final Set<java.util.Map.Entry<String, Object>> entrySet() {
        ensureDataMapInitialized();
        return dataMap.entrySet();
    }

    public final void setStructType(String fieldName, String msgTypeName) {
        LOGGER.debug("Setting proto message type '{}' for '{}'", fieldName, msgTypeName);
        typeMappings.put(fieldName, msgTypeName);
    }

    public final long getJobId() {
       return jobId;
    }

    @Override
    public final long getObjectId() {
        return getId();
    }
}
