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
 * This <code>QuoteContactFactory<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 14, 2007
 */

public abstract class QuoteContactFactory {

    private static QuoteContactFactory singleton = null;

    public QuoteContactFactory() {
        super();
    }
    
    // Create a QuoteContact
    public abstract QuoteContact updateQuoteContact(String creatorId)throws TopazException;
    
    // find the contact of the quote
    public abstract QuoteContact findQuoteContact(String quoteNum, String cntPrtnrFuncCode)throws TopazException;
    
    public static QuoteContactFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteContactFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        QuoteContactFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteContactFactory.singleton = (QuoteContactFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteContactFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteContactFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteContactFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
