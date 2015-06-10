package com.ibm.dsw.quote.draftquote.viewbean;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RedirectViewBean<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 17, 2007
 */

public class RedirectViewBean extends BaseViewBean {
    private boolean forward;

    private String redirectURL;

    private transient Map redirectParams;
    
    public void collectResults(Parameters param) throws ViewBeanException {
        super.collectResults(param);
        redirectURL = (String) param.getParameter(ParamKeys.PARAM_REDIRECT_URL);
        List redirectMsgList = (List)param.getParameter(ParamKeys.PARAM_REDIRECT_MSG);
        if ( redirectMsgList != null && redirectMsgList.size() > 0 )
        {
        	try
			{
	        	StringBuffer buff = new StringBuffer();
	        	for ( int i = 0; i < redirectMsgList.size(); i++ )
	        	{
	        		if ( i != 0 )
	        		{
	        			buff.append(":");
	        		}
	        		buff.append((String)redirectMsgList.get(i));
	        	}
	        	String redirectMsg = URLEncoder.encode(buff.toString(), "UTF-8");
	            redirectURL += "&" + ParamKeys.PARAM_REDIRECT_MSG + "=" + redirectMsg;
			}
        	catch ( Throwable t )
			{
        		LogContextFactory.singleton().getLogContext().error(this, t);
			}
        }
        if (redirectURL != null)
            redirectURL = redirectURL.replaceAll("&amp;", "&");
        forward = BooleanUtils.toBoolean((Boolean) param.getParameter(ParamKeys.PARAM_FORWARD_FLAG));
        
        Object it = param.getParameter(DraftQuoteParamKeys.PARAM_REDIRECT_PARAMS);
        
        if(it instanceof HashMap){
        	this.redirectParams = (HashMap)it;     	
        }

    }

    public Map getRedirectParams() {
		return redirectParams;
	}

	/**
     * @return Returns the redirectAction.
     */
    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     * @return Returns the forward.
     */
    public boolean isForward() {
        return forward;
    }
}
