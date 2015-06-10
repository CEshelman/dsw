package com.ibm.dsw.quote.draftquote.viewbean;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>OverwrittenQuoteViewBean</code> class is to check if there is currently a quote.
 * 
 * @author: wangshp@cn.ibm.com
 * 
 * Created on Jan. 24, 2013
 */
public class OverwrittenQuoteViewBean extends DisplayQuoteBaseViewBean {
    
    
    protected String redirectURL = "";
    protected String overwrittenMsg = "";
    protected boolean paramForwardFlag;
    
    
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        redirectURL = (String) params.getParameter(ParamKeys.PARAM_REDIRECT_URL);
        overwrittenMsg = this.getOverwrittenMsg(DraftQuoteMessageKeys.MSG_OVERWRITTEN_QUOTE);
        paramForwardFlag = (Boolean) params.getParameter(ParamKeys.PARAM_FORWARD_FLAG);
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean#getPostTabAction()
     */
    public String getPostTabAction() {
        return null;
    }
    
    private String getOverwrittenMsg(String msgKey) {
        String msgInfo = "";
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        msgInfo = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, msgKey);
        return msgInfo;
    }

	@Override
	public String getDisplayTabAction() {
		return null;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public String getOverwrittenMsg() {
		return overwrittenMsg;
	}

	public boolean isParamForwardFlag() {
		return paramForwardFlag;
	}
	
}
