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
 * This <code>QuoteTxtFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 16, 2008
 */

public abstract class QuoteTxtFactory {
    
    private static QuoteTxtFactory singleton = null;

    public QuoteTxtFactory() {
        super();
    }
    
    public abstract QuoteTxt createQuoteComment() throws TopazException;
    
    public abstract QuoteTxt getRenewalQuoteDetailComments(String webQuoteNum) throws TopazException;
    
    public abstract QuoteTxt updateQuoteComment(String webQuoteNum, int qtTxtId, String qtTxtTypeCode,
            String qtTxt, String userEmail, String sectnId) throws TopazException;
    
    public abstract List getQuoteTxtHistory(String webQuoteNum, String quoteTxtType, int txtFlag) throws TopazException;
    
    public abstract List getUserDraftComments(String webQuoteNum, String quoteTxtType) throws TopazException;
    
    public static QuoteTxtFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteTxtFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(QuoteTxtFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteTxtFactory.singleton = (QuoteTxtFactory) factoryClass.newInstance();
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

	/**
	 * @param webQuoteNum
	 * @param userId
	 * @param deleteType
	 */
	public abstract void deleteDraftComments(String webQuoteNum, String userId, int deleteType) throws TopazException;

}
