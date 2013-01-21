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

public class SectionClosingTest {

    @Test
    public void doTestTwoMisingSections() throws Exception {
        String strTemplate = loadTemplate("twoSections.template");

        Template template = createTemplate(strTemplate, new ArrayList<JsonAttachment>());

        Map<String, Object> testObject = new HashMap<String, Object>();

        String res = template.expand(testObject);

        System.out.println(res);
    }

    @Test
    public void doTestTwoSections() throws Exception {
        String strTemplate = loadTemplate("twoSections.template");

        Template template = createTemplate(strTemplate, new ArrayList<JsonAttachment>());

        Map<String, Object> testObject = new HashMap<String, Object>();
        testObject.put("malicious_keywords", "mkeywords");
        testObject.put("suspicious_keywords", "skeywords");

        String res = template.expand(testObject);

        System.out.println(res);
    }

    @Test
    public void doTestMissingFirstSection() throws Exception {
        String strTemplate = loadTemplate("twoSections.template");

        Template template = createTemplate(strTemplate, new ArrayList<JsonAttachment>());

        Map<String, Object> testObject = new HashMap<String, Object>();
        testObject.put("suspicious_keywords", "skeywords");

        String res = template.expand(testObject);

        System.out.println(res);
    }

    @Test
    public void doTestMissingSecondSection() throws Exception {
        String strTemplate = loadTemplate("twoSections.template");

        Template template = createTemplate(strTemplate, new ArrayList<JsonAttachment>());

        Map<String, Object> testObject = new HashMap<String, Object>();
        testObject.put("malicious_keywords", "mkeywords");

        String res = template.expand(testObject);

        System.out.println(res);
    }

    @Test
    public void doTestMissingSectionWithOrStatement() throws Exception {
        String strTemplate = loadTemplate("twoSectionsWithOr.template");

        Template template = createTemplate(strTemplate, new ArrayList<JsonAttachment>());

        Map<String, Object> testObject = new HashMap<String, Object>();
        testObject.put("suspicious_keywords", "skeywords");

        String res = template.expand(testObject);

        System.out.println(res);
    }
}
