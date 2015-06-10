package com.ibm.dsw.quote.appcache.domain;


import java.util.Map;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PrintSPTimeTraceFactory</code> class is factory populating SP name
 * and execution time threshold/List.
 * 
 * 
 * @author <a href="machaomc@cn.ibm.com"> Ma Chao </a> <br/>
 * 
 * Creation date: Oct.21,2013
 */

public abstract class PrintSPTimeTraceFactory {

	private static PrintSPTimeTraceFactory singleton = null;
	
	public abstract Map<String, Integer> findAllSPinCache() throws TopazException; 
	
    public static PrintSPTimeTraceFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (PrintSPTimeTraceFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		PrintSPTimeTraceFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                PrintSPTimeTraceFactory.singleton = (PrintSPTimeTraceFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(PrintSPTimeTraceFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(PrintSPTimeTraceFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(PrintSPTimeTraceFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
