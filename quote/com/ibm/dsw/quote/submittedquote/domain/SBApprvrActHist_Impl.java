package com.ibm.dsw.quote.submittedquote.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SBApprvrActHist_Impl<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public abstract class SBApprvrActHist_Impl implements SBApprvrActHist {

    public String webQuoteNum;

    public String userEmail;

    public String userRole;

    public String userAction;

    public String quoteTxt;
    
    public String returnReason;

    public String getWebQuoteNum() {
        return webQuoteNum;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUserAction() {
        return userAction;
    }

    public String getQuoteTxt() {
        return quoteTxt;
    }
    
    public String getReturnReason() {
		return returnReason;
	}

	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("webQuoteNum = ").append(webQuoteNum).append("\n");
        buffer.append("userEmail = ").append(userEmail).append("\n");
        buffer.append("userRole = ").append(userRole).append("\n");
        buffer.append("userAction = ").append(userAction).append("\n");
        buffer.append("returnReason = ").append(returnReason).append("\n");
        return buffer.toString();
    }

}
