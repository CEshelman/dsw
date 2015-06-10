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
 * This <code>QuoteRightColumnFactory<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 5, 2007
 */
public abstract class QuoteRightColumnFactory {
    private static QuoteRightColumnFactory singleton = null;

    /**
     *  
     */
    public QuoteRightColumnFactory() {
        super();
    }

    public abstract QuoteRightColumn findQuoteRightColumnByID(String creatorId)
            throws TopazException;
    
    public static QuoteRightColumnFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteRightColumnFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(QuoteRightColumnFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteRightColumnFactory.singleton = (QuoteRightColumnFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteRightColumnFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteRightColumnFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteRightColumnFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
