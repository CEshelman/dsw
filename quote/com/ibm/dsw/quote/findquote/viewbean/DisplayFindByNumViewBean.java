package com.ibm.dsw.quote.findquote.viewbean;

import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByNumContract;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByNumViewBean</code> class.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-4-27
 */
public class DisplayFindByNumViewBean extends DisplayFindViewBean {

    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return ((DisplayFindQuoteByNumContract) findContract).getNumber();
    }

    public String getCommonCriteriaFlag() {
        return ((DisplayFindQuoteByNumContract) findContract).getCommonCriteriaFlag();
    }

    public String getRelatedQuoteFlag() {
        return ((DisplayFindQuoteByNumContract) findContract).getRelatedQuoteFlag();
    }

    public String getViewBeanName() {
        return "bynumber";
    }

    public String getNumPageURL() {
        String numURL = "";

        numURL += "&amp;" + FindQuoteParamKeys.NUMBER + "=" + this.getNumber();

        return numURL;

    }

    public String getPrePageURL() {
        String prePageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_NUM");
        prePageURL += super.getPrePageURL();
        prePageURL += this.getNumPageURL();

        return prePageURL;
    }

    public String getNextPageURL() {
        String nextPageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_NUM");
        nextPageURL += super.getNextPageURL();
        nextPageURL += this.getNumPageURL();

        return nextPageURL;
    }

    public String getChangeCriteriaURL() {
        String criteriaURL = HtmlUtil.getURLForAction("DISPLAY_FIND_QUOTE_BY_NUM_CSC");
        criteriaURL += super.getChangeCriteriaURLDetails();
        criteriaURL += this.getNumPageURL();
        return criteriaURL;
    }

    public String getQuoteNumAddDesc() {
        StringBuffer sb = new StringBuffer();

        sb.append("1".equals(getRelatedQuoteFlag()) || "1".equals(getCommonCriteriaFlag()) ? "(" : "");
        sb.append("1".equals(getRelatedQuoteFlag()) ? getI18NString(ApplicationContextFactory.singleton()
                .getApplicationContext().getValueAsString("find.quote.i18n.basename"), locale,
                FindQuoteParamKeys.RELATED_QUOTE_FLAG) : "");
        sb.append("1".equals(getRelatedQuoteFlag()) && "1".equals(getCommonCriteriaFlag()) ? ", " : "");
        sb.append("1".equals(getCommonCriteriaFlag()) ? getI18NString(ApplicationContextFactory.singleton()
                .getApplicationContext().getValueAsString("find.quote.i18n.basename"), locale,
                FindQuoteParamKeys.COMMON_CRITERIA_FLAG) : "");
        sb.append("1".equals(getRelatedQuoteFlag()) || "1".equals(getCommonCriteriaFlag()) ? ")" : "");

        return sb.toString().toLowerCase();

    }
}
