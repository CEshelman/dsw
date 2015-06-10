package com.ibm.dsw.quote.draftquote.viewbean;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteLockInfo;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>LockedQuoteViewBean</code> class is to check if others is locking the quote.
 * 
 * @author: cyxu@cn.ibm.com
 * 
 * Created on May 11, 2010
 */
public class LockedQuoteViewBean extends DisplayQuoteBaseViewBean {
    
    
    protected String lockedBy = "";
    protected String unLockQuoteBtnParams = "";
    protected String redirectURL = "";
    protected QuoteLockInfo quoteLockInfo = null;
    
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        quoteLockInfo = (QuoteLockInfo) params.getParameter(ParamKeys.PARAM_QUOTE_LOCK_INFO);
        redirectURL = (String) params.getParameter(ParamKeys.PARAM_REDIRECT_URL);
        
        if (quoteLockInfo == null){
            return;
        }
        webQuoteNum = quoteLockInfo.getWebQuoteNum();
        isLockedFlag = quoteLockInfo.isLockedFlag();
        lockedBy = quoteLockInfo.getLockedBy();
        if (isLockedFlag) {
            lockedQuoteMsg = handleLockedQuoteMessage(lockedBy, webQuoteNum, DraftQuoteMessageKeys.MSG_LOCKED_QUOTE);
        }
    }
        
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        return null;
    }

    public String getUnLockQuoteBtnParams() {
        
        String[] paramKeys = { DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM, ParamKeys.PARAM_FORWARD_FLAG };
        String[] paramValues = { webQuoteNum, "true" };
        return this.getActionURL(DraftQuoteActionKeys.UNLOCK_QUOTE_ACTION, redirectURL, paramKeys, paramValues);
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean#getPostTabAction()
     */
    public String getPostTabAction() {
        return null;
    }
    
    public String getActionURL(String action1, String redirectURL, String[] paramKeys, String[] paramValues) {
        String actionURL = HtmlUtil.getURLForAction(action1);
        StringBuffer sb = new StringBuffer(actionURL);
        if(StringUtils.isNotBlank(redirectURL)){
            HtmlUtil.addURLParam(sb, ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        }
        if (paramKeys != null && paramValues != null) {
            for (int i = 0; i < paramKeys.length; i++)
                HtmlUtil.addURLParam(sb, paramKeys[i], paramValues[i]);
        }
        
        return sb.toString();
    }    
    
}
