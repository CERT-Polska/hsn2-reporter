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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ResourceException;

/**
 * Provides a thread save registry of json templates. A template would be loaded on demand, which would allow the
 * service to be provided with new templates without being stopped.
 */
public class CachingTemplateRegistry implements TemplateRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(CachingTemplateRegistry.class);
    private ConcurrentMap<String, Future<String>> templates = new ConcurrentHashMap<String, Future<String>>();
    private final boolean useCache;
	private String jsontPath;

    public CachingTemplateRegistry(boolean useCache, String jsontPath) {
        this.useCache = useCache;
        this.jsontPath = jsontPath;
    }

    /* (non-Javadoc)
     * @see pl.nask.hsn2.service.TemplateRegistry#getTemplate(java.lang.String)
     */
    @Override
    public String getTemplate(final String templateName) throws ResourceException {
        if (useCache) {
            return getTemplateUsingCache(templateName);
        } else {
            return readFileAsString(templateName);
        }
    }

    String getTemplateUsingCache(final String templateName) throws ResourceException {
        Future<String> f = templates.get(templateName);
        if (f == null) {
            FutureTask<String> future = new FutureTask<String>(
                 new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        return readFileAsString(templateName);
                    }
                 }
            );
            f = templates.putIfAbsent(templateName, future);
            if (f == null) {
                f = future;
                future.run();
            }
        }

        try {
            return f.get();
        } catch (InterruptedException e) {
            throw new ResourceException("Interrupted while waiting for the resource", e);
        } catch (ExecutionException e) {
            throw new ResourceException("Exception while producing the resource", e);
        }
    }

    String readFileAsString(String templateName) throws ResourceException {
        LOG.debug("Reading {}", templateName);
        try {
            return readFromFilesystem(templateName);
        } catch (IOException e) {
        	LOG.warn("Couldn't read {} from the working directory, trying classpath", templateName);
            try {
                return readFromClasspath(templateName);
            } catch (IOException e1) {
                LOG.warn("Couldn't read {} from the classpath", templateName);
                throw new ResourceException("Couldn't read " + templateName, e1);
            }
        }
    }

    private String readFromClasspath(String templateName) throws IOException {
        InputStream inputStream = null;
    	try {
    		inputStream = getClass().getResourceAsStream("/" + templateName);
    		if (inputStream != null) {
	    		return IOUtils.toString(new InputStreamReader(inputStream));
	    	} else {
	    		throw new IOException();
	    	}
    	} finally {
    		IOUtils.closeQuietly(inputStream);
    	}
    }

    private String readFromFilesystem(String templateName) throws IOException {
        File template = new File(jsontPath, templateName);
    	InputStream inputStream = null;
        try {
	    	if (template.exists()) {
	    		inputStream = new FileInputStream(template);
	    		return IOUtils.toString(inputStream);
	    	}
	    	LOG.warn("Couldn't read {} from '{}', trying working directory", templateName, jsontPath);
	    	inputStream = new FileInputStream(templateName);
	    	return IOUtils.toString(inputStream);
        } finally {
        	IOUtils.closeQuietly(inputStream);
        }
    }
}
