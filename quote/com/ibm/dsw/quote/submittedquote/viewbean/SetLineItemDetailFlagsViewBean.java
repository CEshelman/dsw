package com.ibm.dsw.quote.submittedquote.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SetLineItemDetailFlagsViewBean</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-5-14
 */
public class SetLineItemDetailFlagsViewBean extends BaseViewBean {
    private String lineItemDetailFlag;

    public void collectResults(Parameters parameters) throws ViewBeanException {
        super.collectResults(parameters);
        this.lineItemDetailFlag = parameters.getParameterAsString(SubmittedQuoteParamKeys.LINE_ITEM_DETAIL_FALG);

    }

    /**
     * @return Returns the lineItemDetailFlag.
     */
    public String getLineItemDetailFlag() {
        return lineItemDetailFlag;
    }

    /**
     * @param lineItemDetailFlag
     *            The lineItemDetailFlag to set.
     */
    public void setLineItemDetailFlag(String lineItemDetailFlag) {
        this.lineItemDetailFlag = lineItemDetailFlag;
    }
}
