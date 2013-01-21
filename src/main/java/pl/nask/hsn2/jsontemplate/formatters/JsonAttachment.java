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

import pl.nask.hsn2.wrappers.HsnContext;

public class JsonAttachment {

    private final HsnContext ctx;
    private final int store;
    private final long key;

    public JsonAttachment(HsnContext ctx, int store, long key) {
        this.ctx = ctx;
        this.store = store;
        this.key = key;
    }

    public String getName() {
        return String.format("%s:%s:%s", ctx.getJobId(), store, key);
    }

    /**
     *
     * @return (Reference.key)
     */
    public long getKey() {
        return key;
    }

    /**
     *
     * @return (Reference.store)
     */
    public int getStore() {
        return store;
    }

    public long getJobId() {
        return ctx.getJobId();
    }

}
