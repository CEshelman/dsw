package com.ibm.dsw.quote.findquote.viewbean;

import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByOrderNumContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByOrderNumViewBean</code> class.
 * 
 * @author whlihui@cn.ibm.com
 * 
 * Created on 2012-10-15
 */
public class DisplayFindByOrderNumViewBean extends DisplayFindViewBean {

    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return ((DisplayFindQuoteByOrderNumContract) findContract).getNumber();
    }

    public String getViewBeanName() {
        return "byordernumber";
    }

    public String getNumPageURL() {
        String numURL = "";

        numURL += "&amp;" + FindQuoteParamKeys.NUMBER + "=" + this.getNumber();

        return numURL;

    }

    public String getPrePageURL() {
        String prePageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_SIEBEL_NUM");
        prePageURL += super.getPrePageURL();
        prePageURL += this.getNumPageURL();

        return prePageURL;
    }

    public String getNextPageURL() {
        String nextPageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_SIEBEL_NUM");
        nextPageURL += super.getNextPageURL();
        nextPageURL += this.getNumPageURL();

        return nextPageURL;
    }

    public String getChangeCriteriaURL() {
        String criteriaURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_SIEBEL_NUM_CSC");
        criteriaURL += super.getChangeCriteriaURLDetails();
        criteriaURL += this.getNumPageURL();
        return criteriaURL;
    }
}
