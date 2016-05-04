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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.wrappers.ObjectDataReportingWrapper;

public class HsnSectionStatement implements IStatement {
    private static final Logger LOG = LoggerFactory.getLogger(HsnSectionStatement.class);
    private final Section block;
    private final String structureType;

    public HsnSectionStatement(Section sectionBlock, String structureType) {
        block = sectionBlock;
        String st;
        if (structureType == null) {
        	st = "";
        } else {
        	st = structureType.trim();
        }
        if (st.isEmpty()) {
        	this.structureType = null;
        } else {
        	this.structureType = st;
        }
    }

    @Override
    public void execute(ScopedContext context, ITemplateRenderCallback callback) {
        // push a context first
        setStructType(context.getCursorValue());
        Object cursorPosition = context
                .pushSection(this.block.getSectionName());
        if (!context.isEmptyContext(cursorPosition)) {
            TemplateExecutor.execute(this.block.getStatements(), context,
                    callback);
        } else {
            TemplateExecutor.execute(this.block.getStatements("or"), context,
                    callback);
        }

        context.pop();
    }

    private void setStructType(Object cursorPosition) {
        if (structureType != null) {
            if (cursorPosition instanceof ObjectDataReportingWrapper) {
                ((ObjectDataReportingWrapper) cursorPosition).setStructType(block.getSectionName(), structureType);
            } else {
                LOG.warn("Trying to set section type to {} for non-reference object: {}", structureType, cursorPosition);
            }
        }
    }


}
