package com.ibm.dsw.quote.common.domain;

import java.util.List;

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
 * This <code>QuoteStatusFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-6
 */

public abstract class QuoteStatusFactory {

    private static QuoteStatusFactory singleton = null;

    public abstract List getStatusByQuoteNum(String creatorId, String webQuoteNum) throws TopazException;

    public abstract List getSapStatusByQuoteNum(String webQuoteNum) throws TopazException;
    
    public abstract void validateAndSaveRQStatus(String quoteNum, String statusCode, int statusType, boolean active)
            throws TopazException;
    
    public abstract void UpdateWebQuoteStatus(String webQuoteNum, String activeStatusList, String inactStatusList)
            throws TopazException;

    /**
     *  
     */
    public QuoteStatusFactory() {
        super();
    }

    public static QuoteStatusFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteStatusFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton()
                        .getDefaultClassName(QuoteStatusFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteStatusFactory.singleton = (QuoteStatusFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteStatusFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteStatusFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteStatusFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
