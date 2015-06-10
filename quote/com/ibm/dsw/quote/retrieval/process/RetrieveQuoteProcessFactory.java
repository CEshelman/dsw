package com.ibm.dsw.quote.retrieval.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteProcessFactory</code> class is the process factory
 * for quote retrieval.
 * 
 * @author: <a href="tom_boulet@us.ibm.com">Tom Boulet</a>
 * 
 * Creation date: 2007-05-08
 */
public abstract class RetrieveQuoteProcessFactory {

    private static RetrieveQuoteProcessFactory singleton = null;

    /**
     * Constructor
     */
    public RetrieveQuoteProcessFactory() {
        super();
    }

    public abstract RetrieveQuoteProcess create() throws QuoteException;

    /**
     * 
     * @return
     */
    public static RetrieveQuoteProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (RetrieveQuoteProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        RetrieveQuoteProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                RetrieveQuoteProcessFactory.singleton = (RetrieveQuoteProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteProcessFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
