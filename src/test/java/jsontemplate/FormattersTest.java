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

package jsontemplate;

import static jsontemplate.TemplateUtils.createTemplate;
import static jsontemplate.TemplateUtils.loadTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.testng.annotations.Test;

import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;
import pl.nask.hsn2.wrappers.WrapperUtils;

/**
 * Formatters test.
 */
public class FormattersTest {
	/**
	 * Test with JSON formatter.
	 * 
	 * @throws IOException
	 *             Exception.
	 */
	@Test
	public final void doTestWithJsonFormatter() throws IOException {
		String strTemplate = "\"jsonMsg\": <<value|json>>";

		Template t = createTemplate(strTemplate, new ArrayList<JsonAttachment>());

		Map<String, String> object = new HashMap<String, String>();
		object.put("value", "value");
		String res = t.expand(object);

		Assert.assertEquals("\"jsonMsg\": \"value\"", res);
	}

	/**
	 * Test wrapper with JSON formatter.
	 * 
	 * @throws Exception
	 *             Exception.
	 */
	@Test
	public final void doTestWrapperWithJsonFormatter() throws Exception {
		String strTemplate = loadTemplate("template-js-sta");
		Template t = createTemplate(strTemplate, new ArrayList<JsonAttachment>());
		Object wrappedMsg = WrapperUtils.prepareWrapper(WrapperUtils.prepareTestMsg());

		String res = t.expand(wrappedMsg);

		System.out.println(res);
	}

	/**
	 * Test wrapper with HSN attachment.
	 * 
	 * @throws Exception
	 *             Exception.
	 */
	@Test
	public final void doTestWrapperWithHsnAttachment() throws Exception {
		String strTemplate = "\"file\": <<js_sta_results|hsn-attachment>>";
		ArrayList<JsonAttachment> attachments = new ArrayList<JsonAttachment>();
		Template t = createTemplate(strTemplate, attachments);
		Object wrappedMsg = WrapperUtils.prepareWrapper(WrapperUtils.prepareTestMsg());

		String res = t.expand(wrappedMsg);
		Assert.assertEquals(1, attachments.size());
		JsonAttachment att = attachments.get(0);
		Assert.assertEquals("1:0:1", att.getName());
		Assert.assertEquals("\"file\": \"1:0:1\"", res);

		System.out.println(res);
	}
}
