package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DraftQuoteBaseContract<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-28
 */

public class DraftQuoteBaseContract extends QuoteBaseCookieContract {

    private String messageInfo;
    private String messageError;
    private String saveSuccess;
    private String searchCriteriaUrlParam;
    private String requestorEMail;
    private String redirectMsg = null;
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        redirectMsg = parameters.getParameterAsString(SpecialBidParamKeys.PARAM_REDIRECT_MSG);
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("DraftQuoteBaseContract: ");
        return buffer.toString();
    }
    /**
     * @return Returns the messageError.
     */
    public String getMessageError() {
        return messageError;
    }
    /**
     * @param messageError The messageError to set.
     */
    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }
    /**
     * @return Returns the messageInfo.
     */
    public String getMessageInfo() {
        return messageInfo;
    }
    /**
     * @param messageInfo The messageInfo to set.
     */
    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
    }
    /**
     * @return Returns the saveSuccess.
     */
    public String getSaveSuccess() {
        return saveSuccess;
    }
    /**
     * @param saveSuccess The saveSuccess to set.
     */
    public void setSaveSuccess(String saveSuccess) {
        this.saveSuccess = saveSuccess;
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
    
    public String getRedirectMsg() {
        return redirectMsg;
    }
}
