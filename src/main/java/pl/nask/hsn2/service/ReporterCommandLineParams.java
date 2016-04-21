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

package pl.nask.hsn2.service;

import pl.nask.hsn2.CommandLineParams;

public class ReporterCommandLineParams extends CommandLineParams {
	private static final OptionNameWrapper COUCH_DB_ADDRESS = new OptionNameWrapper("dbAddr", "databaseAddress");
	private static final OptionNameWrapper USE_TEMPLATES_CACHE = new OptionNameWrapper("tCache", "useTemplatesCache");
	private static final OptionNameWrapper JSONT_PATH = new OptionNameWrapper("jsontPath", "jsonTemplatesPath");

    @Override
    public final void initOptions() {
        super.initOptions();
        addOption(COUCH_DB_ADDRESS, "url", "The full address of the CouchDB instance");
        addOption(USE_TEMPLATES_CACHE, "flag", "cache template files (true|false)");
        addOption(JSONT_PATH, "path", "Path to json templates directory");
    }

    @Override
    protected final void initDefaults() {
        super.initDefaults();
        setDefaultValue(USE_TEMPLATES_CACHE, "false");
        setDefaultValue(COUCH_DB_ADDRESS, "http://localhost:5984/hsn/");
        setDefaultValue(JSONT_PATH, "");
        setDefaultServiceNameAndQueueName("reporter");
        setDefaultDataStoreAddress("http://localhost:8080/");
    }

    public final String getDatabaseAddress() {
        return getOptionValue(COUCH_DB_ADDRESS);
    }


    public final boolean getUseCacheForTemplates() {
        return "true".equalsIgnoreCase(getOptionValue(USE_TEMPLATES_CACHE));
    }

    public final String getJsontPath() {
		return getOptionValue(JSONT_PATH);
	}
}
