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

package pl.nask.hsn2.wrappers;


import java.io.IOException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ObjectDataReportingWrapperTest {

    /*
     * check if a substitution was made in 'classification' (assume, that str formatter is ussed)
     */
    @Test
    public void testSimpleGet() throws IOException {
        ObjectDataReportingWrapper wrapper = WrapperUtils.prepareSimpleWrapper();
        Assert.assertEquals(wrapper.get("js_classification"), "malicious");
        Assert.assertEquals(wrapper.get("bool_value"), true);
    }

    @Test
    public void testSubMapWithStructType() throws IOException {

        ObjectDataReportingWrapper wrapper = WrapperUtils.prepareSimpleWrapper();
        // instruct wrapper, that 'js_sta-results' is a reference to 'JSContextResults')
        wrapper.setStructType("js_sta_results", "JSContextResults");

        Map res = (Map) wrapper.get("js_sta_results");
        // check message id
        Assert.assertEquals(res.get("id"), 1);

        Assert.assertNull(res.get("key"));
    }

    @Test
    public void testSubMapNoStructType() throws IOException {

        ObjectDataReportingWrapper wrapper = WrapperUtils.prepareSimpleWrapper();
        Map res = (Map) wrapper.get("js_sta_results");

        Assert.assertNull(res.get("id"));
        Assert.assertEquals(res.get("key"), 1L);
    }
}
