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

import java.util.ArrayList;

public class DefaultProgramBuilder implements IProgramBuilder {

	Section currentBlock;
	protected ArrayList<Section> stack;
	private IFormatterResolver moreFormatters;

	public DefaultProgramBuilder(IFormatterResolver moreFormatters) {
		this.currentBlock = new Section();
		this.stack = new ArrayList<Section>();
		this.stack.add(this.currentBlock);
		this.moreFormatters = moreFormatters;
	}

	public DefaultProgramBuilder() {
		this(null);
	}

	public Section getRoot() {
		return this.currentBlock;
	}

	public void append(IStatement statement) {
		this.currentBlock.append(statement);
	}

	IFormatter getFormatter(String formatterName) {
		IFormatter formatter = null;
		if (this.moreFormatters != null) {
			formatter = moreFormatters.getFormatter(formatterName);
		}
		if (formatter == null) {
			formatter = DefaultFormatters.get(formatterName);
		}
		if (formatter == null) {
			throw new BadFormatterError(formatterName);
		}
		return formatter;
	}

	public void appendSubstitution(String name, String... formatterNames) {
		ArrayList<IFormatter> formatterObjects = new ArrayList<IFormatter>();
		for (String formatterName : formatterNames) {
			formatterObjects.add(this.getFormatter(formatterName));
		}
		this.currentBlock.append(new SubstitutionStatement(name,
				(IFormatter[]) formatterObjects
						.toArray(new IFormatter[formatterObjects.size()])));

	}

	public void newSection(boolean repeated, String sectionName, String extraParams) {
		Section newBlock = new Section(sectionName);
		if (repeated) {
			this.currentBlock.append(new RepeatedSectionStatement(newBlock));
		} else {
			this.currentBlock.append(new SectionStatement(newBlock));
		}
		this.stack.add(newBlock);
		this.currentBlock = newBlock;

	}

	public void newClause(String name) {
		this.currentBlock.newClause(name);
	}

	public void endSection() {
		this.stack.remove(this.stack.size() - 1);
		this.currentBlock = this.stack.get(this.stack.size() - 1);
	}

}
