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

import jsontemplate.IFormatter;

import org.json.simple.JSONValue;

public class JsStringFormatter implements IFormatter {

	@Override
	public Object format(Object value) {
		String jsonString = JSONValue.toJSONString(value);
		String result = escapeSlashAndUnicode(jsonString);

		// If value is Number result will be JSON number, which means no quotation marks. Return value has to be JSON
		// text, so it have to be enclosed within quotation marks.
		if (!result.startsWith("\"")) {
			result = "\"" + result + "\"";
		}

		return result;
	}

	// This code was taken from org.apache.commons.jexl2.parser.StringParser (org.apache.commons-jexl:2.0.1) and
	// modified

	/** The length of an escaped unicode sequence. */
	private static final int UCHAR_LEN = 4;

	/** The last 7bits ascii character. */
	private static final char LAST_ASCII = 127;

	private static String escapeSlashAndUnicode(String str) {
		if (str == null) {
			return null;
		}
		final int length = str.length();
		StringBuilder strb = new StringBuilder(length);

		for (int i = 0; i < length; ++i) {
			char c = str.charAt(i);
			if (c < LAST_ASCII) {
				if (c == '\\') {
					int next = i + 1;
					if (next < length && str.charAt(next) == 'u') {
						strb.append('\\');
						strb.append('\\');
						continue;
					}
				}
				strb.append(c);
			} else {
				strb.append('\\');
				strb.append('\\');
				strb.append('u');
				String hex = Integer.toHexString(c);
				for (int h = hex.length(); h < UCHAR_LEN; ++h) {
					strb.append('0');
				}
				strb.append(hex);
			}
		}
		return strb.toString();
	}
}
