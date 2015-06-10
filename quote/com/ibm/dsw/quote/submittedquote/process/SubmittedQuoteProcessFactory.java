package com.ibm.dsw.quote.submittedquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
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
 * The <code>SubmittedQuoteProcessFactory</code> class is the abstract process
 * factory for Submitted quote process.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public abstract class SubmittedQuoteProcessFactory {

    private static SubmittedQuoteProcessFactory singleton = null;

    public abstract SubmittedQuoteProcess create() throws QuoteException;

    public static SubmittedQuoteProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (SubmittedQuoteProcessFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(SubmittedQuoteProcessFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                SubmittedQuoteProcessFactory.singleton = (SubmittedQuoteProcessFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(SubmittedQuoteProcessFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(SubmittedQuoteProcessFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(SubmittedQuoteProcessFactory.class, ie, ie.getMessage());
            }
        }

        return singleton;
    }
    
}