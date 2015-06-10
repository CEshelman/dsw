package com.ibm.dsw.quote.common.domain; 

import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>SQOHeadLineFactory<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: 2010-7-13
 */
public abstract class SQOHeadLineFactory {
    protected LogContext logContext = LogContextFactory.singleton().getLogContext();
    private static SQOHeadLineFactory singleton;
    
    public abstract List getSQOHeadLineMsg(String applCode) throws TopazException;
    
    public List getSQOHeadLineMsg() throws TopazException {
        return this.getSQOHeadLineMsg(QuoteConstants.APP_CODE_SQO);
    }
    
    public static SQOHeadLineFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        if (SQOHeadLineFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        SQOHeadLineFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                SQOHeadLineFactory.singleton = (SQOHeadLineFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(SQOHeadLineFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(SQOHeadLineFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(SQOHeadLineFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
 