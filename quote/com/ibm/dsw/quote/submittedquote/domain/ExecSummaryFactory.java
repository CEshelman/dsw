/*
 * Created on Feb 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.domain;

import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author Nan CDL Wang (wnan@cn.ibm.com)
 *
 * Created At: 2009-2-5
 */

public abstract class ExecSummaryFactory {
	private static ExecSummaryFactory singleton = null;

    public ExecSummaryFactory() {
    }
    
    public abstract ExecSummary findExecSummaryByQuoteNum(String webQuoteNumber) throws TopazException;
    
    public abstract ExecSummary create();
    
    public static ExecSummaryFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (ExecSummaryFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(ExecSummaryFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                ExecSummaryFactory.singleton = (ExecSummaryFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteHeaderFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteHeaderFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteHeaderFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
