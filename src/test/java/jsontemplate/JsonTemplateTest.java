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

import static jsontemplate.TemplateUtils.createTemplate;
import static jsontemplate.TemplateUtils.loadTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;
import pl.nask.hsn2.wrappers.WrapperUtils;

public class JsonTemplateTest {

    @Test
    public void doTestWithMap() throws Exception {
        String strTemplate = loadTemplate("template-js-sta");
        Template t = createTemplate(strTemplate, new ArrayList<JsonAttachment>());

        Map<String, Object> object = new HashMap<String, Object>();
        object.put("js_classification", "//js_classification");
        object.put("js_malicious_keywords", true);
        object.put("js_suspicious_keywords", true);
        Map<String, Object> js_sta_results = new HashMap<String, Object>();

        js_sta_results.put("id", "id//id");
        js_sta_results.put("classification", "//classification");
        object.put("js_sta_results", js_sta_results);

        String res = t.expand(object);

        System.out.println(res);
    }

    @Test
    public void doTestWithWrappedMsg() throws Exception {
        String strTemplate = loadTemplate("template-js-sta");
        Template t = createTemplate(strTemplate, new ArrayList<JsonAttachment>());
        Object wrappedMsg = WrapperUtils.prepareWrapper(WrapperUtils.prepareTestMsg());

        String res = t.expand(wrappedMsg);

        System.out.println(res);
    }
}
