package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
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

public class CheckQuoteLockContract extends DraftQuoteBaseContract {

    private String redirectURL;
    private String forwardFlag;
    private String buttonName;
    private String webQuoteNum;
    
    private String ownerFilter;
    private String timeFilter;
    private String formFlag;
    private String quoteSort=null;
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        if(parameters.hasParameter(NewQuoteParamKeys.PARAM_FORM_FLAG)){
            loadFromRequest(parameters);
        }
    }
    
    private void loadFromRequest(Parameters parameters) {
        this.setFormFlag(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_FORM_FLAG));
        this.setTimeFilter(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_TIME_FILTER)); 
        this.setQuoteSort(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_QUOTE_SORT));
        this.setOwnerFilter(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_OWNER_FILTER));
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
     * @return Returns the formFlag.
     */
    public String getFormFlag() {
        return formFlag;
    }
    /**
     * @param formFlag The formFlag to set.
     */
    public void setFormFlag(String formFlag) {
        this.formFlag = formFlag;
    }
    /**
     * @return Returns the ownerFilter.
     */
    public String getOwnerFilter() {
        return ownerFilter;
    }
    /**
     * @param ownerFilter The ownerFilter to set.
     */
    public void setOwnerFilter(String ownerFilter) {
        this.ownerFilter = ownerFilter;
    }
    /**
     * @return Returns the quoteSort.
     */
    public String getQuoteSort() {
        return quoteSort;
    }
    /**
     * @param quoteSort The quoteSort to set.
     */
    public void setQuoteSort(String quoteSort) {
        this.quoteSort = quoteSort;
    }
    /**
     * @return Returns the timeFilter.
     */
    public String getTimeFilter() {
        return timeFilter;
    }
    /**
     * @param timeFilter The timeFilter to set.
     */
    public void setTimeFilter(String timeFilter) {
        this.timeFilter = timeFilter;
    }
}
