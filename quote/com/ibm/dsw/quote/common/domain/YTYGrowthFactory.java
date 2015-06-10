package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AuditHistoryFactory</code> class is the abstract domain factory
 * for AuditHistory domain object.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 14, 2007
 */
public abstract class YTYGrowthFactory {
    private static YTYGrowthFactory singleton = null;

    public YTYGrowthFactory() {
    }
    //create a new yty
    public abstract YTYGrowth createYTYGrowth(String webQuoteNum, int lineItemSeqNum) throws TopazException; 
    
    public static YTYGrowthFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();

        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        if (YTYGrowthFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        YTYGrowthFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                YTYGrowthFactory.singleton = (YTYGrowthFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(PartnerFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(PartnerFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(PartnerFactory.class, ie, ie.getMessage());
            }
        }

        return singleton;
    }
}
