package com.ibm.dsw.quote.base.contract;

import is.domainx.User;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.contract.ProcessContractAbstract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteBaseContract</code> is the base contract class
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public class QuoteBaseContract extends ProcessContractAbstract {
    private String userId;
    private String tranMessage;
    private Locale locale; 
    private User user;
    private String httpRequestMethod;
	protected QuoteUserSession quoteUserSession = null;
	private HttpServletRequest httpServletRequest = null;
    

	/*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        userId = (String)session.getAttribute(SessionKeys.SESSION_USER_ID);
        locale = getLocale(session);
        user = (User)session.getAttribute(SessionKeys.SESSION_USER);
        httpRequestMethod = parameters.getParameterAsString(ParamKeys.PARAM_HTTP_REQUEST_METHOD);
        quoteUserSession = (QuoteUserSession) session.getAttribute(SessionKeys.SESSION_QUOTE_USER);
        httpServletRequest = (HttpServletRequest)parameters.getParameter(ParamKeys.PARAM_HTTP_REQUEST);
    }
    
    public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}
    
    /**
     * @return Returns the creatorId.
     */
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId){
    	this.userId=userId;
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String ls = System.getProperty("line.separator");
        sb.append(ls);
        HashMap hMap = this.getValidationDataMap();
        if (hMap != null) {
            sb.append("Here is the validation data map:");
            sb.append(ls);
            Iterator i = hMap.keySet().iterator();
            String key = "";
            String value = "";
            while (i.hasNext()) {
                key = (String) i.next();
                value = (String) hMap.get(key);
                sb.append("Key=>" + key + " Value=>" + value + ls);
            }

        } else {
            sb.append("Validation data map is null");
        }

        return sb.toString();
    }
    /**
     * @return Returns the tranMessage.
     */
    public String getTranMessage() {
        return tranMessage;
    }
    /**
     * @param tranMessage The tranMessage to set.
     */
    public void setTranMessage(String tranMessage) {
        this.tranMessage = tranMessage;
    }

    public Locale getLocale(JadeSession jadeSession) {

        Locale locale = (Locale) jadeSession.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
        if (locale == null) {
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        }
        return locale;
    }
    /**
     * @return Returns the locale.
     */
    public Locale getLocale() {
        return locale;
    }
    
    public void setLocale(Locale locale) {
        this.locale=locale;
    }
    /**
     * @return Returns the user.
     */
    public User getUser() {
        return user;
    }

    public String getHttpRequestMethod() {
        return httpRequestMethod;
    }
    
    public boolean isHttpGETRequest() {
        return QuoteConstants.HTTP_METHOD_GET.equalsIgnoreCase(getHttpRequestMethod());
    }

	/**
	 * @return Returns the quoteUserSession.
	 */
	public QuoteUserSession getQuoteUserSession() {
	    return quoteUserSession;
	}
	
	public boolean isSQOEnv()
	{
		return QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equalsIgnoreCase(this.getQuoteUserSession().getAudienceCode());
	}
	public boolean isPGSEnv(){
		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(this.getQuoteUserSession().getAudienceCode());
	}
}
