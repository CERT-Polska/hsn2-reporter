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

package jsontemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import pl.nask.hsn2.connector.ObjectStoreConnector;
import pl.nask.hsn2.jsontemplate.formatters.AttachmentFormatter;
import pl.nask.hsn2.jsontemplate.formatters.JobIdFormatter;
import pl.nask.hsn2.jsontemplate.formatters.JsStringFormatter;
import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;
import pl.nask.hsn2.jsontemplate.formatters.JsonFormatter;
import pl.nask.hsn2.jsontemplate.formatters.NodeRefFormatter;
import pl.nask.hsn2.jsontemplate.formatters.RelatedFilesFormatter;


public final class HsnFormatters implements IFormatterResolver {

    private final static ConcurrentMap<String, IFormatter> FORMATTERS = new ConcurrentHashMap<String, IFormatter>();

    private static ObjectStoreConnector osConnector;
    static {
        FORMATTERS.put("json", new JsonFormatter());
        FORMATTERS.put("js-string", new JsStringFormatter());
        FORMATTERS.put("hsn-node-ref", new NodeRefFormatter());
        FORMATTERS.put("hsn-job-id", new JobIdFormatter());
        FORMATTERS.put("hsn-related-files", new RelatedFilesFormatter(osConnector));
    }

    private Map<String, IFormatter> contextFormatters = new HashMap<String, IFormatter>();

    public static void setOsConnector(ObjectStoreConnector osConnector) {
        HsnFormatters.osConnector = osConnector;
    }

    public HsnFormatters(List<JsonAttachment> attachments) {
        contextFormatters.put("hsn-attachment", new AttachmentFormatter(attachments));
    }

    public static IFormatterResolver getInstance(List<JsonAttachment> attachments) {
        return new HsnFormatters(attachments);
    }

    @Override
    public IFormatter getFormatter(String formatterName) {
        IFormatter f = contextFormatters.get(formatterName);
        if (f == null) {
            f = FORMATTERS.get(formatterName);
        }

        return f;
    }

}
