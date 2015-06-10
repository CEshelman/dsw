package com.ibm.dsw.quote.submittedquote.domain;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SBApprvrActHist<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public interface SBApprvrActHist {

    public String getWebQuoteNum();

    public String getUserEmail();

    public String getUserRole();

    public String getUserAction();

    public String getQuoteTxt();
    
    public String getReturnReason();

    public void setWebQuoteNum(String webQuoteNum) throws TopazException;

    public void setUserEmail(String userEmail) throws TopazException;

    public void setUserRole(String userRole) throws TopazException;

    public void setUserAction(String userAction) throws TopazException;

    public void setQuoteTxt(String quoteTxt) throws TopazException;
    
    public void setReturnReason(String returnReason) throws TopazException;
}
