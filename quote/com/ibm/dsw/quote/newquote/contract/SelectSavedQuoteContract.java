package com.ibm.dsw.quote.newquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SelectSavedQuoteContract</code> class
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-3-16
 */
public class SelectSavedQuoteContract extends QuoteBaseCookieContract {

    private String savedQuoteNum = null;
    private String openAsNew = "";

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        this.savedQuoteNum = parameters.getParameterAsString(NewQuoteParamKeys.PARAM_QUOTE_ID);

    }
    /**
     * @return Returns the savedQuoteNum.
     */
    public String getSavedQuoteNum() {
        return savedQuoteNum;
    }

    /**
     * @param savedQuoteNum
     *            The savedQuoteNum to set.
     */
    public void setSavedQuoteNum(String savedQuoteNum) {
        this.savedQuoteNum = savedQuoteNum;
    }
    /**
     * @return Returns the openAsNew.
     */
    public String getOpenAsNew() {
        return openAsNew;
    }
    /**
     * @param openAsNew The openAsNew to set.
     */
    public void setOpenAsNew(String openAsNew) {
        this.openAsNew = openAsNew;
    }
}
