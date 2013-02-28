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

package pl.nask.hsn2.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.tools.json.JSONReader;
import com.rabbitmq.tools.json.JSONWriter;

/**
 * Check JSON parsers / seializers to find the one, which will handle (remove) additional ',' from the end of the json
 * list. This suite contains test for JSONObject (which does the job) to make sure, that none of possible library
 * updates breake this functionality. The suite also contains tests for other JSON parsers, which are available through
 * dependencies, to check if any of the future updates makes them usable for this purpose.
 * 
 */
public class JsonRenderingResultTest {
	/**
	 * Results.
	 */
	private JsonRenderingResult fullJsonResult;
	/**
	 * JSON string.
	 */
	private String simpleJson;

	/**
	 * Before test initialization.
	 * 
	 * @throws IOException
	 *             IO exception.
	 */
	@BeforeTest
	public final void beforeTest() throws IOException {
		Reader reader = new FileReader("src/test/resources/correctWebclientTemplate");
		fullJsonResult = new JsonRenderingResult(IOUtils.toString(reader), new ArrayList<JsonAttachment>());

		reader = new FileReader("src/test/resources/simpleJsonWithAdditionalComma");
		simpleJson = IOUtils.toString(reader);
	}

	/**
	 * Performs validation.
	 */
	@Test
	public final void validate() {
		try {
			fullJsonResult.validate();
		} catch (ParseException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * This parser/serializer does not handle addiditonal comas.
	 */
	@Test(expectedExceptions = ClassCastException.class)
	public final void rabbitSerialization() {
		Object o = new JSONReader().read(simpleJson);
		new JSONWriter().write(o);
	}

	/**
	 * This parser/serializer does not handle addiditonal comas.
	 */
	@Test(expectedExceptions = JsonSyntaxException.class)
	public final void gsonSerialization() {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(simpleJson);
		System.out.println(element.getAsString());

	}

	/**
	 * The only implementation, which actualy does the job.
	 */
	@Test
	public final void jsonUtilsSerialization() {
		JSONObject jo = (JSONObject) JSONValue.parse(simpleJson);
		String validJson = jo.toString();
		Assert.assertEquals(countChars(validJson, ','), 2);
	}

	/**
	 * Counts character occurrences in a string.
	 * 
	 * @param s
	 *            Input string.
	 * @param ch
	 *            Char to count.
	 * @return Number of character occurrences.
	 */
	private int countChars(final String s, final char ch) {
		int counter = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ch) {
				counter++;
			}
		}
		return counter;
	}
}
