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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * JavaScript formatter test.
 */
public class JsStringFormatterTest {
	/**
	 * Formatter.
	 */
	private static final JsStringFormatter FORMATTER = new JsStringFormatter();
	/**
	 * Backslash.
	 */
	private static final String BACKSLASH = "\\";
	/**
	 * Slash.
	 */
	private static final String SLASH = "/";
	/**
	 * Quotation mark.
	 */
	private static final String QUOT = "\"";
	/**
	 * Control character.
	 */
	private static final String CONTROL = "\b";
	/**
	 * Unicode.
	 */
	private static final String UNICODE = "\uffff";
	/**
	 * Unicode lower case.
	 */
	private static final String LOW_UNICODE = "\u0099";

	/**
	 * Backslash format test.
	 */
	@Test
	public final void formatBackSlash() {
		Assert.assertEquals(FORMATTER.format(BACKSLASH), "\"\\\\\"");
	}

	/**
	 * Slash format test.
	 */
	@Test
	public final void formatSlash() {
		Assert.assertEquals(FORMATTER.format(SLASH), "\"\\/\"");
	}

	/**
	 * Quatation format test.
	 */
	@Test
	public final void formatQuot() {
		Assert.assertEquals(FORMATTER.format(QUOT), "\"\\\"\"");
	}

	/**
	 * Control character format test.
	 */
	@Test
	public final void formatControl() {
		Assert.assertEquals(FORMATTER.format(CONTROL), "\"\\b\"");
	}

	/**
	 * Unicode format test.
	 */
	@Test
	public final void formatUnicode() {
		Assert.assertEquals(FORMATTER.format(UNICODE), "\"\\\\uffff\"");
	}

	/**
	 * Unicode lower case format test.
	 */
	@Test
	public final void formatLowUnicode() {
		Assert.assertEquals(FORMATTER.format(LOW_UNICODE), "\"\\\\u0099\"");
	}
}
