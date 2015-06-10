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
 * The <code>SpecialBidInfoFactory</code> class . 
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 20, 2007
 */
public abstract class SpecialBidInfoFactory {
    private static SpecialBidInfoFactory singleton = null;

    public abstract SpecialBidInfo createSpecialBidInfo(String webQuoteNum) throws TopazException;
    
    public abstract SpecialBidInfo findByQuoteNum(String webQuoteNum) throws TopazException;
    
    public abstract SpecialBidInfo getSpeclBidInfoHeader(String webQuoteNum) throws TopazException;
    
    public static SpecialBidInfoFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (SpecialBidInfoFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(SpecialBidInfoFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                SpecialBidInfoFactory.singleton = (SpecialBidInfoFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteLineItemFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteLineItemFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteLineItemFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
