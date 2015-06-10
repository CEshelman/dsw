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
 * This <code>QuoteAttachmentFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 17, 2008
 */

public abstract class QuoteAttachmentFactory {
    
    private static QuoteAttachmentFactory singleton = null;

    public QuoteAttachmentFactory() {
    }
    
    public abstract List getRQSalesCommentAttachments(String webQuoteNum) throws TopazException;
    
    public abstract List getSpecialBidAttachments(String webQuoteNum) throws TopazException;
    
    public abstract List getFctNonStdTcAttachments(String webQuoteNum) throws TopazException;
    
    public abstract void removeQuoteAttachment(String webQuoteNum, String attchSeqNum, String userId, int stageCode) throws TopazException;
    
    public abstract int getAttchmtRefNum(String attchSeqNum) throws TopazException;
    
    public abstract QuoteAttachment getQuoteAttachmentInfo(String attchUUID) throws TopazException;
    
    public static QuoteAttachmentFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteAttachmentFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(QuoteAttachmentFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteAttachmentFactory.singleton = (QuoteAttachmentFactory) factoryClass.newInstance();
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
