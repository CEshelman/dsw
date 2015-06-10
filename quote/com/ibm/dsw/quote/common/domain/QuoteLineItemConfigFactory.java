package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.common.util.GlobalContext;
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
 * The <code>QuoteLineItemConfigFactory</code> class is quote line item config
 * factory.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public abstract class QuoteLineItemConfigFactory {
    private static QuoteLineItemConfigFactory singleton;

    public abstract QuoteLineItemConfig create(String webQuoteNum, int seqNum, String userID) throws TopazException;

    public static QuoteLineItemConfigFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteLineItemConfigFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        QuoteLineItemConfigFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteLineItemConfigFactory.singleton = (QuoteLineItemConfigFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteLineItemConfigFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteLineItemConfigFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteLineItemConfigFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
