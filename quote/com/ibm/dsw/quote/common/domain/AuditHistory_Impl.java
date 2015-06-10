package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AuditHistory_Impl</code> class is the abstract implementation for
 * the AuditHistory domain object.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 14, 2007
 */
public abstract class AuditHistory_Impl implements AuditHistory {
    public String webQuoteNum;

    public Integer lineItemNum;

    public String userEmail;

    public String userAction;

    public String oldValue;

    public String newValue;
    
    public Integer apprvrLvl;

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.AuditHistory#getWebQuoteNum()
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.AuditHistory#getLineItemNum()
     */
    public Integer getLineItemNum() {
        return lineItemNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.AuditHistory#getUserEmail()
     */
    public String getUserEmail() {
        return userEmail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.AuditHistory#getUserAction()
     */
    public String getUserAction() {
        return userAction;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.AuditHistory#getOldValue()
     */
    public String getOldValue() {
        return oldValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.AuditHistory#getNewValue()
     */
    public String getNewValue() {
        return newValue;
    }
    
    public Integer getApprvrLvl(){
    	return this.apprvrLvl;
    }

}
