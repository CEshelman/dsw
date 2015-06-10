package com.ibm.dsw.quote.draftquote.viewbean;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>MigrationConfirmScreenViewBean<code> class.
 *    
 * @author: xiongxj@cn.ibm.com
 * 
 * Creation date: 2012-5-15
 */
public class MigrationConfirmScreenViewBean extends MigrationReqBaseViewBean {

	private static final long serialVersionUID = 1L;
    
    private String sapConfirmNum = "";
    
	public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        sapConfirmNum = (String) params.getParameter(ParamKeys.PARPM_SAP_CONFIRM_NUM);
	}
	
    public String getViewCustRptHostedServicesURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_HTSV_RPT);
        StringBuffer sb = new StringBuffer(url);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, sapCustNum);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, sapCtrctNum);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.HIGHLIGHT_ID, highlightId);

        return sb.toString();
    }

	public String getSapConfirmNum() {
		return sapConfirmNum;
	}

	public void setSapConfirmNum(String sapConfirmNum) {
		this.sapConfirmNum = sapConfirmNum;
	}


	
}
