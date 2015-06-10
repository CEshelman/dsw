package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.ead4j.common.session.Session;
import com.ibm.ead4j.common.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DraftQuoteSubmitBaseContract<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-16
 */

public class DraftQuoteSubmitBaseContract extends QuoteBaseCookieContract {
    
    private String searchCriteriaUrlParam;
    private String requestorEMail;
    private String curTabDisAct;
    
    /* (non-Javadoc)
     * @see com.ibm.ead4j.common.contract.ProcessContract#load(com.ibm.ead4j.common.util.Parameters, com.ibm.ead4j.common.session.Session)
     */
    public void load(Parameters parameters, Session session) {
        super.load(parameters, session);
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

	public String getCurTabDisAct() {
		return curTabDisAct;
	}

	public void setCurTabDisAct(String curTabDisAct) {
		this.curTabDisAct = curTabDisAct;
	}
}
