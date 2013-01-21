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

package pl.nask.hsn2.wrappers;

import java.util.Arrays;

import pl.nask.hsn2.protobuff.Object.Attribute;
import pl.nask.hsn2.protobuff.Object.Attribute.Type;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.Object.Reference;
import pl.nask.hsn2.protobuff.Resources.JSContextResults;
import pl.nask.hsn2.protobuff.Resources.JSContextResults.JSClass;

public class WrapperUtils {
    static MockedDSConnector dsConnector = new MockedDSConnector();
    static {
        initDsConnector();
    }

    private static void initDsConnector() {
        JSContextResults.Builder jscrBuilder = JSContextResults.newBuilder()
            .setId(1)
            .addAllMaliciousKeywords(Arrays.asList("mk1", "mk2"))
            .addAllSuspiciousKeywords(Arrays.asList("sk1", "sk2"))
            .setClassification(JSClass.MALICIOUS)
            .setWhitelisted(false);

        dsConnector.addData(1, jscrBuilder.build());
    }

    public static ObjectData prepareSimpleTestMsg() {
        ObjectData.Builder builder = ObjectData.newBuilder();
        builder.setId(1);
        builder.addAttrs(Attribute.newBuilder().setType(Type.STRING).setDataString("malicious").setName("js_classification"));
        builder.addAttrs(Attribute.newBuilder().setType(Type.BOOL).setDataBool(true).setName("bool_value"));
        builder.addAttrs(Attribute.newBuilder().setType(Type.BYTES).setDataBytes(Reference.newBuilder().setKey(1).setStore(0)).setName("js_sta_results"));

        return builder.build();
    }

    public static ObjectData prepareTestMsg() {
        ObjectData.Builder builder = ObjectData.newBuilder();
        builder.setId(1);
        builder.addAttrs(Attribute.newBuilder().setType(Type.STRING).setDataString("malicious").setName("js_classification"));
        builder.addAttrs(Attribute.newBuilder().setType(Type.BOOL).setDataBool(true).setName("js_malicious_keywords"));
        builder.addAttrs(Attribute.newBuilder().setType(Type.BOOL).setDataBool(true).setName("js_suspicious_keywords"));

        builder.addAttrs(Attribute.newBuilder().setType(Type.BYTES).setDataBytes(Reference.newBuilder().setKey(1).setStore(0)).setName("js_sta_results"));

        return builder.build();
    }

    public static ObjectDataReportingWrapper prepareSimpleWrapper() {
        return new ObjectDataReportingWrapper(1, new ObjectDataWrapper(prepareSimpleTestMsg()), dsConnector);
    }

    public static ObjectDataReportingWrapper prepareWrapper(ObjectData msg) {
        return new ObjectDataReportingWrapper(1, new ObjectDataWrapper(msg), dsConnector);
    }
}
