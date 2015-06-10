package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>LoadSavedQuoteAction</code> class is to load a saved draft quote
 * into a session quote.
 * 
 * @author: cyxu@cn.ibm.com
 * 
 * Created on May 11, 2010
 */

public class UnlockQuoteContract extends DraftQuoteBaseContract {

    private String redirectURL;
    private String forwardFlag;
    private String buttonName;
    private String webQuoteNum;
    private String quoteId;
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }
    
    /**
     * @return Returns the redirectURL.
     */
    public String getRedirectURL() {
        return redirectURL;
    }
    /**
     * @param redirectURL The redirectURL to set.
     */
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
    /**
     * @return Returns the forwardFlag.
     */
    public String getForwardFlag() {
        return forwardFlag;
    }
    /**
     * @param forwardFlag The forwardFlag to set.
     */
    public void setForwardFlag(String forwardFlag) {
        this.forwardFlag = forwardFlag;
    }
    /**
     * @return Returns the buttonName.
     */
    public String getButtonName() {
        return buttonName;
    }
    /**
     * @param buttonName The buttonName to set.
     */
    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
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
     * @return Returns the quoteId.
     */
    public String getQuoteId() {
        return quoteId;
    }
    /**
     * @param quoteId The quoteId to set.
     */
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }
}
