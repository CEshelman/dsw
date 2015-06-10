package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AddRenewalPartsContract</code> class.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 10, 2007
 */
public class AddRenewalPartsContract extends QuoteBaseContract {

    private String[] lineSeqNum;

    private String rqNum;

    private String qteRptURL;

    private String renewalQuoteNum;

    private String requestedAction;

    private String p1;

    private String listActionName;

    private String searchActionName;

    private String searchCriteriaUrlParam;

    private String sortBy;

    /**
     * @return Returns the renewalQuoteNum.
     */
    public String getRenewalQuoteNum() {
        return renewalQuoteNum;
    }

    /**
     * @param renewalQuoteNum
     *            The renewalQuoteNum to set.
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
     * @param requestedAction
     *            The requestedAction to set.
     */
    public void setRequestedAction(String requestedAction) {
        this.requestedAction = requestedAction;
    }

    /**
     * @return Returns the listActionName.
     */
    public String getListActionName() {
        return listActionName;
    }

    /**
     * @param listActionName
     *            The listActionName to set.
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
     * @param p1
     *            The p1 to set.
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
     * @param searchActionName
     *            The searchActionName to set.
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
     * @param searchCriteriaUrlParam
     *            The searchCriteriaUrlParam to set.
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
     * @param sortBy
     *            The sortBy to set.
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String[] getLineSeqNum() {
        return lineSeqNum;
    }

    public String getRqNum() {
        return rqNum;
    }

    /**
     * @param rqNum
     *            The rqNum to set.
     */
    public void setRqNum(String rqNum) {
        this.rqNum = rqNum;
    }

    /**
     *
     */

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        lineSeqNum = parameters.getParameterWithMultiValues(DraftQuoteParamKeys.LINE_SEQ_NUM);
    }
}
