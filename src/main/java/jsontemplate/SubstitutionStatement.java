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

class SubstitutionStatement implements IStatement {

	private IFormatter[] formatters;
	private String name;

	public SubstitutionStatement(String name, IFormatter... formatters) {
		this.name = name;
		this.formatters = formatters;
	}

	public void execute(ScopedContext context, ITemplateRenderCallback callback) {
		Object value;
		if ("@".equals(this.name)) {
			value = context.getCursorValue();
		} else {
			value = context.lookup(this.name);
		}
		for (IFormatter f : formatters) {
			try {
				value = f.format(value);
			} catch (RuntimeException e) {
				throw new EvaluationError(
						String
								.format(
										"Formatting value %s with formatter %s raised exception: %s",
										value.toString(), f.getClass().getName(), e
												.getClass().getSimpleName()), e);
			}
		}
		callback.templateDidRender(value.toString());
	}

}
