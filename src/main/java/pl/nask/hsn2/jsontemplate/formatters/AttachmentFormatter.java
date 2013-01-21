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

package pl.nask.hsn2.jsontemplate.formatters;

import java.util.List;

import jsontemplate.IFormatter;
import pl.nask.hsn2.protobuff.Object.Reference;
import pl.nask.hsn2.wrappers.ProtoMsgMap;

import com.google.protobuf.GeneratedMessage;

public class AttachmentFormatter implements IFormatter {

    private final List<JsonAttachment> attachments;

    public AttachmentFormatter(List<JsonAttachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public Object format(Object value) {
        if (value instanceof ProtoMsgMap) {
            ProtoMsgMap protoMap = (ProtoMsgMap) value;
            GeneratedMessage msg = protoMap.getOriginalMsg();
            if (msg instanceof Reference) {
                Reference ref = (Reference) msg;
                JsonAttachment att = new JsonAttachment(protoMap, ref.getStore(), ref.getKey());
                attachments.add(att);
                return "\"" + att.getName() + "\"";
            }
        }

        return value;
    }

}
