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


public class HsnProgramBuilder extends DefaultProgramBuilder {
    public HsnProgramBuilder(IFormatterResolver moreFormatters) {
        super(moreFormatters);
    }

    @Override
    public void newSection(boolean repeated, String sectionName,
            String extraParams) {
        Section newBlock = new Section(sectionName);
        if (repeated) {
            this.currentBlock.append(new RepeatedSectionStatement(newBlock));
        } else {
            this.currentBlock.append(new HsnSectionStatement(newBlock, extraParams));
        }
        this.stack.add(newBlock);
        this.currentBlock = newBlock;
    }
}
