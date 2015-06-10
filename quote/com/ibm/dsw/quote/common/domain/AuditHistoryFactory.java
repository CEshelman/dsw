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
public abstract class AuditHistoryFactory {
    private static AuditHistoryFactory singleton = null;

    public AuditHistoryFactory() {
    }
    
    public abstract AuditHistory createAuditHistory(String webQuoteNum, Integer lineItemNum, String userEmail,
            String userAction, String oldValue, String newValue) throws TopazException;
    
    public abstract AuditHistory createAuditHistory(String webQuoteNum, Integer lineItemNum, String userEmail,
            String userAction, int apprvlLvl,String oldValue, String newValue) throws TopazException;
    
    public static AuditHistoryFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();

        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        if (AuditHistoryFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        AuditHistoryFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                AuditHistoryFactory.singleton = (AuditHistoryFactory) factoryClass.newInstance();
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
