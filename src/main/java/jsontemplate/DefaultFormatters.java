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

import org.apache.commons.codec.binary.Base64;

public class DefaultFormatters {

	private static HashMap<String, IFormatter> LOOKUP = new HashMap<String, IFormatter>();
	static {
		LOOKUP.put("str", new StrFormatter());
		LOOKUP.put("raw", new RawFormatter());
		LOOKUP.put("html", new HtmlFormatter());
		LOOKUP.put("html-attr-value", new HtmlAttrValueFormatter());
		LOOKUP.put("htmltag", new HtmlTagFormatter());
		LOOKUP.put("base64", new Base64Formatter());
	}

	public static IFormatter get(String formatterName) {
		return LOOKUP.get(formatterName);
	}

	private static class RawFormatter implements IFormatter {
		public Object format(Object input) {
			return input;
		}
	}

	private static class HtmlFormatter implements IFormatter {
		public Object format(Object value) {
			String s = value.toString();
			return s.replace("&", "&amp;").replace("<", "&lt;").replace(">",
					"&gt;");
		}
	}

	private static class Base64Formatter implements IFormatter {

		@Override
		public Object format(Object value) {
			String s = value.toString();
			return Base64.encodeBase64String(s.getBytes());
		}

	}

	private static class HtmlTagFormatter extends HtmlFormatter {
	}

	private static class HtmlAttrValueFormatter implements IFormatter {
		public Object format(Object value) {
			String s = value.toString();
			return s.replace("&", "&amp;").replace("<", "&lt;").replace(">",
					"&gt;").replace("\"", "&quot;").replace("'", "&apos;");
		}
	}

	private static class StrFormatter implements IFormatter {
		public Object format(Object value) {
			return value.toString();
		}
	};

}
