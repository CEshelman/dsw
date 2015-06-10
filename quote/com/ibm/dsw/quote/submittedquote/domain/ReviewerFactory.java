package com.ibm.dsw.quote.submittedquote.domain;

import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
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
 * The <code>ReviewerFactory</code> class is the abstract domain factory for
 * Reviewer domain object.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public abstract class ReviewerFactory {

    private static ReviewerFactory singleton = null;

    public ReviewerFactory() {
    }

    public abstract Reviewer addReviewer(String webQuoteNum, String userEmail, String rvwrEmail,int quoteTxtId) throws TopazException;

    public static ReviewerFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (ReviewerFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(ReviewerFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                ReviewerFactory.singleton = (ReviewerFactory) factoryClass.newInstance();
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