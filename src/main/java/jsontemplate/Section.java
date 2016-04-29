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

import java.util.HashMap;
import java.util.Map;

class Section {
	private String sectionName;
	private StatementList currentClause;
	private Map<String, StatementList> statements;

	public Section(String sectionName) {
		this.sectionName = sectionName;
		this.currentClause = new StatementList();
		this.statements = new HashMap<String, StatementList>();
		this.statements.put("default", this.currentClause);
	}

	public Section() {
		this(null);
	}

	public StatementList getStatements() {
		return this.getStatements("default");
	}

	public StatementList getStatements(String clauseName) {
		if (this.statements.containsKey(clauseName)) {
			return this.statements.get(clauseName);
		}
		return new StatementList();
	}

	public String toString() {
		return "<Block " + sectionName + ">";
	}

	public void append(IStatement statement) {
		this.currentClause.add(statement);
	}

	public String getSectionName() {
		return sectionName;
	}

	public void newClause(String name) {
		StatementList newClause = new StatementList();
		this.statements.put(name, newClause);
		this.currentClause = newClause;
	}


}
