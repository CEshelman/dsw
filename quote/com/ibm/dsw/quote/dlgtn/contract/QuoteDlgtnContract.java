package com.ibm.dsw.quote.dlgtn.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteDlgtnContract</code>
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-3-13
 */
public class QuoteDlgtnContract extends QuoteBaseContract {
    private String webQuoteNum = null;
    
    /**
     * @return Returns the quoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    /**
     * @param quoteNum The quoteNum to set.
     */
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
    
    private String delegateId = null;

    /**
     * @return Returns the delegateId.
     */
    public String getDelegateId() {
        return delegateId;
    }
    
    /**
     * @param delegateId The delegateId to set.
     */
    public void setDelegateId(String delegateId) {
        this.delegateId = delegateId;
    }
    
    private String targetAction = null;
    
    /**
     * @return Returns the targetAction.
     */
    public String getTargetAction() {
        return targetAction;
    }
    /**
     * @param targetAction The targetAction to set.
     */
    public void setTargetAction(String targetAction) {
        this.targetAction = targetAction;
    }
}
