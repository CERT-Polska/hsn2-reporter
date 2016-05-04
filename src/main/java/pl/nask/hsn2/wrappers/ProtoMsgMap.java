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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;

/**
 * Produces a map based on a GeneratedMessage using field names as keys.
 *
 *
 */
public final class ProtoMsgMap extends HashMap<String, Object> implements HsnContext {

    private final long jobId;
    private final long objectId;
    private GeneratedMessage originalMsg;

    private ProtoMsgMap(long jobId, long objectId, GeneratedMessage msg) {
        super();
        this.originalMsg = msg;
        this.jobId = jobId;
        this.objectId = objectId;

        for (Map.Entry<FieldDescriptor, Object> entry: msg.getAllFields().entrySet()) {
            put(entry.getKey().getName(), convert(entry.getKey(), entry.getValue()));
        }
    }

    public static Map<String, Object> asMap(long jobId, long objectId, GeneratedMessage msg) {
        return new ProtoMsgMap(jobId, objectId, msg);
    }


    private Object convert(FieldDescriptor desc, Object value) {
      boolean isRepeated = desc.isRepeated();

      // either return the value itself, or a wrapped value
      if (isRepeated) {
          List<Object> list = new ArrayList<Object>();
          List<Object> vlist = (List<Object>) value;
          for (Object item: vlist) {
              list.add(wrap(desc, item));
          }
          return list;
      } else {
          return wrap(desc, value);
      }

    }

    private Object wrap(FieldDescriptor desc, Object value) {
        switch (desc.getType()) {
        case BYTES:
        case MESSAGE: return ProtoMsgMap.asMap(jobId, objectId, (GeneratedMessage) value);
        case ENUM: return ((EnumValueDescriptor) value).getName();

        default:
            return value;
        }
    }

    @Override
    public long getObjectId() {
        return objectId;
    }

    @Override
    public long getJobId() {
        return jobId;
    }

    public GeneratedMessage getOriginalMsg() {
        return originalMsg;
    }
}
