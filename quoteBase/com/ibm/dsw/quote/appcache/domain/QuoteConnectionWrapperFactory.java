package com.ibm.dsw.quote.appcache.domain;

import java.sql.Connection;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteConnectionWrapperFactory<code> class is Create abstract Factory.
 *    
 * @author: <a href="jiewbj@cn.ibm.com">Crespo </a>
 * 
 * Creation date: October 1, 2013
 */
public abstract class QuoteConnectionWrapperFactory {

    private static QuoteConnectionWrapperFactory singleton = null ;
    
    public abstract QuoteConnectionWrapper create(Connection conn) throws TopazException;
    
    
    public  static QuoteConnectionWrapperFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteConnectionWrapperFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                		QuoteConnectionWrapperFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteConnectionWrapperFactory.singleton = (QuoteConnectionWrapperFactory)factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteConnectionWrapperFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteConnectionWrapperFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteConnectionWrapperFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
