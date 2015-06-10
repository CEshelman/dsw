package com.ibm.dsw.quote.draftquote.contract;

import is.domainx.User;

import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>EditRQContract<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 6, 2007
 */

public class EditRQContract extends QuoteBaseContract {
    
    private User user;
    
    private String renewalQuoteNum;
    
    private String requestedAction;
    
    private String p1;
    
    private String listActionName;
    
    private String searchActionName; 

    private String searchCriteriaUrlParam;
    
    private String sortBy;
    
    private String quoteNum;
    
    private String orignFromCustChgReqFlag;
    
    private String requestorEMail;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        user = (User)session.getAttribute(SessionKeys.SESSION_USER);
    }
    
    /**
     * @return Returns the renewalQuoteNum.
     */
    public String getRenewalQuoteNum() {
        return renewalQuoteNum;
    }
    /**
     * @param renewalQuoteNum The renewalQuoteNum to set.
     */
    public void setRenewalQuoteNum(String renewalQuoteNum) {
        this.renewalQuoteNum = renewalQuoteNum;
    }
    /**
     * @return Returns the requestedAction.
     */
    public String getRequestedAction() {
        return requestedAction;
    }
    /**
     * @param requestedAction The requestedAction to set.
     */
    public void setRequestedAction(String requestedAction) {
        this.requestedAction = requestedAction;
    }
    /**
     * @return Returns the user.
     */
    public User getUser() {
        return user;
    }
    
    /**
     * @return Returns the listActionName.
     */
    public String getListActionName() {
        return listActionName;
    }
    /**
     * @param listActionName The listActionName to set.
     */
    public void setListActionName(String listActionName) {
        this.listActionName = listActionName;
    }
    /**
     * @return Returns the p1.
     */
    public String getP1() {
        return p1;
    }
    /**
     * @param p1 The p1 to set.
     */
    public void setP1(String p1) {
        this.p1 = p1;
    }
    /**
     * @return Returns the searchActionName.
     */
    public String getSearchActionName() {
        return searchActionName;
    }
    /**
     * @param searchActionName The searchActionName to set.
     */
    public void setSearchActionName(String searchActionName) {
        this.searchActionName = searchActionName;
    }
    /**
     * @return Returns the searchCriteriaUrlParam.
     */
    public String getSearchCriteriaUrlParam() {
        return searchCriteriaUrlParam;
    }
    /**
     * @param searchCriteriaUrlParam The searchCriteriaUrlParam to set.
     */
    public void setSearchCriteriaUrlParam(String searchCriteriaUrlParam) {
        this.searchCriteriaUrlParam = searchCriteriaUrlParam;
    }
    /**
     * @return Returns the sortBy.
     */
    public String getSortBy() {
        return sortBy;
    }
    /**
     * @param sortBy The sortBy to set.
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getQuoteNum() {
        return quoteNum;
    }
    public void setQuoteNum(String quoteNum) {
        this.quoteNum = quoteNum;
    }

    /**
     * @return Returns the orignFromCustChgReqFlag.
     */
    public String getOrignFromCustChgReqFlag() {
        return orignFromCustChgReqFlag;
    }
    /**
     * @param orignFromCustChgReqFlag The orignFromCustChgReqFlag to set.
     */
    public void setOrignFromCustChgReqFlag(String fromCustActionFlag) {
        this.orignFromCustChgReqFlag = fromCustActionFlag;
    }
    /**
     * @return Returns the requestorEMail.
     */
    public String getRequestorEMail() {
        return requestorEMail;
    }
    /**
     * @param requestorEMail The requestorEMail to set.
     */
    public void setRequestorEMail(String requestorEMail) {
        this.requestorEMail = requestorEMail;
    }
}
