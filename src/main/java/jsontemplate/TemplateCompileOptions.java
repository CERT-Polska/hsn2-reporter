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

public class TemplateCompileOptions {

	private IFormatterResolver moreFormatters;
	private String meta;
	private char formatChar;
	private String defaultFormatter;

	public TemplateCompileOptions() {
		this.moreFormatters = new EmptyFormatterResolver();
		this.meta = "{}";
		this.formatChar = '|';
		this.defaultFormatter = "str";
	}

	public IFormatterResolver getMoreFormatters() {
		return this.moreFormatters;
	}

	static class EmptyFormatterResolver implements IFormatterResolver {
		public IFormatter getFormatter(String formatterName) {
			return null;
		}
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public String getMeta() {
		return this.meta;
	}

	public char getFormatChar() {
		return this.formatChar;
	}

	public void setFormatChar(char formatChar) {
		this.formatChar = formatChar;
	}

	public String getDefaultFormatter() {
		return this.defaultFormatter;
	}

	public void setDefaultFormatter(String defaultFormatter) {
		this.defaultFormatter = defaultFormatter;

	}

}
