package com.ibm.dsw.quote.findquote.contract;

import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindQuoteBySiebelNumContract</code> class.
 * 
 * @author whlihui@cn.ibm.com
 * 
 * Created on 2012-10-15
 */
public class DisplayFindQuoteBySiebelNumContract extends FindQuoteContract {

    String number;
    String commonCriteriaFlag;
    String relatedQuoteFlag;

    /**
     * @return Returns the commonCriteriaFlag.
     */
    public String getCommonCriteriaFlag() {
        return commonCriteriaFlag==null?"0":commonCriteriaFlag;
    }
    /**
     * @param commonCriteriaFlag The commonCriteriaFlag to set.
     */
    public void setCommonCriteriaFlag(String commonCriteriaFlag) {
        this.commonCriteriaFlag = commonCriteriaFlag;
    }
    /**
     * @return Returns the relatedQuoteFlag.
     */
    public String getRelatedQuoteFlag() {
        return relatedQuoteFlag==null?"0":relatedQuoteFlag;
    }
    /**
     * @param relatedQuoteFlag The relatedQuoteFlag to set.
     */
    public void setRelatedQuoteFlag(String relatedQuoteFlag) {
        this.relatedQuoteFlag = relatedQuoteFlag;
    }
    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return notNullString(number);
    }

    /**
     * @param number
     *            The number to set.
     */
    public void setNumber(String number) {
        this.number = number;
    }

    public void load(Parameters parameters, JadeSession session) {
        this.loadFromCookie(parameters, session);
    }

    public void loadFromRequest(Parameters parameters, JadeSession session) {
        super.loadFromRequest(parameters, session);
        this.setNumber(parameters.getParameterAsString(FindQuoteParamKeys.NUMBER));
        this.setRelatedQuoteFlag(parameters.getParameterAsString(FindQuoteParamKeys.RELATED_QUOTE_FLAG));
        this.setCommonCriteriaFlag(parameters.getParameterAsString(FindQuoteParamKeys.COMMON_CRITERIA_FLAG));
    }

    public void loadFromCookie(Parameters parameters, JadeSession session) {
        super.loadFromCookie(parameters, session);
    }

}
