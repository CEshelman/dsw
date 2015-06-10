package com.ibm.dsw.quote.common.domain;

import java.util.Map;

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
 * This <code>QuoteAccessFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Jun 12, 2009
 */

public abstract class QuoteAccessFactory {
    
    private static QuoteAccessFactory singleton = null;
    
    public abstract Map getRenwlQuoteStatus(String quoteNum, String userIdList, int qaNeeded, int steNeeded,
            int duration, boolean checkSalesRep) throws TopazException;
    
    public abstract QuoteAccess getRenwlQuoteAccess(String quoteNum, String userIdList, int qaNeeded, int steNeeded,
            int duration, boolean checkSalesRep) throws TopazException;

    public QuoteAccessFactory() {
        super();
    }
    
    public static QuoteAccessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteAccessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton()
                        .getDefaultClassName(QuoteAccessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteAccessFactory.singleton = (QuoteAccessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteAccessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteAccessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteAccessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
