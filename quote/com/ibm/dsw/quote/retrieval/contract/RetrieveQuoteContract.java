package com.ibm.dsw.quote.retrieval.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.retrieval.config.RetrieveQuoteConstant;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteContract</code> class is the contract for the quote
 * retrieval service.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 25, 2007
 */
public class RetrieveQuoteContract extends QuoteBaseContract {
    /**
     * @return Returns the sapQuoteIDoc.
     */
    public String getSapQuoteIDoc() {
        return sapQuoteIDoc;
    }
    /**
     * @param sapQuoteIDoc The sapQuoteIDoc to set.
     */
    public void setSapQuoteIDoc(String sapQuoteIDoc) {
        this.sapQuoteIDoc = sapQuoteIDoc;
    }
    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    /**
     * @param webQuoteNum The webQuoteNum to set.
     */
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
    /**
     * @return Returns the fulfillment.
     */
    public String getFulfillment() {
        return fulfillment;
    }

    /**
     * @param fulfillment
     *            The fulfillment to set.
     */
    public void setFulfillment(String fulfillment) {
        this.fulfillment = fulfillment;
    }

    /**
     * @return Returns the sapQuoteNum.
     */
    public String getSapQuoteNum() {
        return sapQuoteNum;
    }

    /**
     * @param sapQuoteNum
     *            The sapQuoteNum to set.
     */
    public void setSapQuoteNum(String sapQuoteNum) {
        this.sapQuoteNum = sapQuoteNum;
    }

    /**
     * @return Returns the userFlag.
     */
    public Boolean getUserFlag() {
        return userFlag;
    }

    /**
     * @param userFlag
     *            The userFlag to set.
     */
    public void setUserFlag(Boolean userFlag) {
        this.userFlag = userFlag;
    }

    /**
     * @return Returns the userID.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID
     *            The userID to set.
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return Returns the docType.
     */
    public String getDocType() {
        return docType;
    }
    /**
     * @param docType The docType to set.
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }
    
    private String sapQuoteNum = null;

    private String sapQuoteIDoc = null;
    
    private String webQuoteNum = null;
    
    private String fulfillment = null;

    private String docType = null;
    
    private String userID = null;

    private Boolean userFlag = null;

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        userFlag = (Boolean)parameters.getParameter(RetrieveQuoteConstant.PARAM_USER_FLAG);
    }
}
