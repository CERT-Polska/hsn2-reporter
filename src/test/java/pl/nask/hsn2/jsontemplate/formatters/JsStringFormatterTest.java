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

public class JsStringFormatterTest {

	public JsStringFormatter formatter = new JsStringFormatter();

	private static final String BACKSLASH = "\\";
	private static final String SLASH = "/";
	private static final String QUOT = "\"";
	private static final String CONTROL = "\b";
	private static final String UNICODE = "\uffff";
	private static final String LOW_UNICODE = "\u0099";

	@Test
	public void formatBackSlash() {
		Assert.assertEquals(formatter.format(BACKSLASH), "\"\\\\\"");
	}

	@Test
	public void formatSlash() {
		Assert.assertEquals(formatter.format(SLASH), "\"\\/\"");
	}

	@Test
	public void formatQuot() {
		Assert.assertEquals(formatter.format(QUOT), "\"\\\"\"");
	}

	@Test
	public void formatControl() {
		Assert.assertEquals(formatter.format(CONTROL), "\"\\b\"");
	}

	@Test
	public void formatUnicode() {
		Assert.assertEquals(formatter.format(UNICODE), "\"\\\\uffff\"");
	}
	
	@Test
	public void formatLowUnicode() {
		Assert.assertEquals(formatter.format(LOW_UNICODE), "\"\\\\u0099\"");
	}
}
