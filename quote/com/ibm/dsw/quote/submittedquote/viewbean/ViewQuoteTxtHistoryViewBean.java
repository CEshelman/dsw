package com.ibm.dsw.quote.submittedquote.viewbean;

import java.util.List;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ViewQuoteTxtHistoryViewBean</code> class .
 * 
 * 
 * @author qinfengc@cn.ibm.com
 * 
 * Creation date: 2010-12-21
 */
public class ViewQuoteTxtHistoryViewBean extends BaseViewBean {
	
	private static final long serialVersionUID = 932275993241279839L;
	private transient List txtList = null;
	private String txtTypeCode = null;
	
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        txtList = (List)params.getParameter(SpecialBidParamKeys.PARAM_QUOTE_TXT_HISTORY);
        txtTypeCode = params.getParameterAsString(SpecialBidParamKeys.PARAM_QUOTE_TXT_TYPE);
    }

	public List getTxtList()
	{
		return txtList;
	}

	public String getTxtTypeCode()
	{
		return txtTypeCode;
	}
}
