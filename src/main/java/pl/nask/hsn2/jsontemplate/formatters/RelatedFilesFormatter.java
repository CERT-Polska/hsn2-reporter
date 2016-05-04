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

package pl.nask.hsn2.jsontemplate.formatters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsontemplate.IFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.ObjectStoreConnector;
import pl.nask.hsn2.protobuff.Object.Attribute;
import pl.nask.hsn2.protobuff.Object.Attribute.Type;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure.QueryType;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;
import pl.nask.hsn2.wrappers.ProtoMsgMap;

public class RelatedFilesFormatter implements IFormatter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RelatedFilesFormatter.class);
	private final ObjectStoreConnector osConnector;

	public RelatedFilesFormatter(ObjectStoreConnector osConnector) {
		this.osConnector = osConnector;
	}

	@Override
	public final Object format(Object value) {
		Set<Long> result = new HashSet<Long>();
		StringBuilder jsResult = new StringBuilder("[");

		if (value instanceof ProtoMsgMap) {
			ProtoMsgMap protoMap = (ProtoMsgMap) value;
			Long jobId = protoMap.getJobId();
			Long objectId = protoMap.getObjectId();

			try {
				receiveNoDuplicatedRelFilesId(jobId, objectId, result);
				receiveDuplicatedRelFilesId(jobId, objectId, result);
			} catch (StorageException e) {
				LOGGER.error("Error in communication with ObjectStore!", e);
			}
		}
		jsResult.append(transformResultToJsResult(result));
		jsResult.append("]");
		return jsResult.toString();
	}

	private void receiveNoDuplicatedRelFilesId(long jobId, long objectId, Set<Long> result) throws StorageException {
		List<QueryStructure> queryStructures = buildQueryList(objectId, true);
		ObjectResponse objectResponse = osConnector.query(jobId, queryStructures);
		result.addAll(objectResponse.getObjectsList());
	}

	private void receiveDuplicatedRelFilesId(long jobId, long objectId, Set<Long> result) throws StorageException {
		List<QueryStructure> queryStructures = buildQueryList(objectId, false);
		ObjectResponse objectResponse = osConnector.query(jobId, queryStructures);
		objectResponse = osConnector.getObjectStoreData(jobId, objectResponse.getObjectsList());

		for (ObjectData objectData : objectResponse.getDataList()) {
			for (Attribute attribute : objectData.getAttrsList()) {
				if ("duplicate".equals(attribute.getName())) {
					result.add(attribute.getDataObject());
					break;
				}
			}
		}
	}

	private List<QueryStructure> buildQueryList(long objectId, boolean withoutDuplicate) {
		List<QueryStructure> queryStructures = new ArrayList<QueryStructure>();

		queryStructures.add(buildValueQueryStructure("parent", Type.OBJECT, objectId));
		queryStructures.add(buildValueQueryStructure("type", Type.STRING, "file"));

		QueryStructure.Builder duplicateQueryStructure = QueryStructure.newBuilder().setType(QueryType.BY_ATTR_NAME)
				.setAttrName("duplicate").setNegate(withoutDuplicate);
		queryStructures.add(duplicateQueryStructure.build());
		return queryStructures;
	}

	private QueryStructure buildValueQueryStructure(String attrName, Type type, Object attrValue) {
		Attribute.Builder attribute = Attribute.newBuilder().setName(attrName);
		switch (type) {
		case OBJECT:
			attribute.setType(Type.OBJECT).setDataObject((Long) attrValue);
			break;
		case STRING:
			attribute.setType(Type.STRING).setDataString((String) attrValue);
			break;
		default:
			throw new IllegalArgumentException("Unsupported type: " + type);
		}

		return QueryStructure.newBuilder().setType(QueryType.BY_ATTR_VALUE).setAttrName(attrName).setAttrValue(attribute).build();
	}

	private StringBuilder transformResultToJsResult(Set<Long> result) {
		StringBuilder jsResult = new StringBuilder();
		String separator = "";
		for (Long id : result) {
			jsResult.append(separator).append(id);
			separator = ",";
		}
		return jsResult;
	}

}
