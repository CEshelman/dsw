package com.ibm.dsw.quote.findquote.viewbean;

import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteBySiebelNumContract;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindBySiebelNumViewBean</code> class.
 * 
 * @author whlihui@cn.ibm.com
 * 
 * Created on 2012-10-15
 */
public class DisplayFindBySiebelNumViewBean extends DisplayFindViewBean {

    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return ((DisplayFindQuoteBySiebelNumContract) findContract).getNumber();
    }
    
    public String getCommonCriteriaFlag() {
        return ((DisplayFindQuoteBySiebelNumContract) findContract).getCommonCriteriaFlag();
    }

    public String getRelatedQuoteFlag() {
        return ((DisplayFindQuoteBySiebelNumContract) findContract).getRelatedQuoteFlag();
    }

    public String getViewBeanName() {
        return "bysiebelnumber";
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
