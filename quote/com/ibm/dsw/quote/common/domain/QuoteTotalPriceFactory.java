
package com.ibm.dsw.quote.common.domain;

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
 * This <code>QuoteTotalPriceFactory<code> class is the factory to create QuoteTotalPrice
 * 
 * @author <a href="jiamengz@cn.ibm.com">Linda Jia </a> <br/>
 * 
 * Creation date: Dec 09, 2013
 */

public abstract class QuoteTotalPriceFactory {
    
    
    private static QuoteTotalPriceFactory singleton = null;
   
    public abstract Map<String,QuoteTotalPrice> getQuoteTotalPriceByWebQuoteNum(String webQuoteNum) throws TopazException;

    public static QuoteTotalPriceFactory singleton() {
        
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteTotalPriceFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(QuoteTotalPriceFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteTotalPriceFactory.singleton = (QuoteTotalPriceFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteTotalPriceFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteTotalPriceFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteTotalPriceFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
