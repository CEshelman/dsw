package com.ibm.dsw.quote.base.viewbean;

import java.util.Locale;
import java.util.TimeZone;

import com.ibm.dsw.common.base.util.TimeZoneUtils;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.ead4j.jade.bean.ViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>BaseViewBean</code> class is the base class for viewBeans
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public class BaseViewBean extends ViewBean {

    private static final long serialVersionUID = -3513805047261327666L;
	protected Locale locale = null;
    protected TimeZone timeZone = null;
    protected int dayLightSaving = 0;
    protected static TimeZone gmtTimeZone = TimeZone.getTimeZone(ParamKeys.PARAM_GMT_TIMEZONE);

    protected static TimeZone displayTimeZone = TimeZoneUtils.getTimeZone();
    
    protected QuoteUserSession quoteUserSession = null;
    
    private String ajaxJsonResult = "{}";
    private String[] data = null;
    
	public void collectResults(Parameters params) throws ViewBeanException {
    	
        locale = (Locale) params.getParameter(ParamKeys.PARAM_LOCAL); 
        
        Object timeZoneObj = params.getParameter(ParamKeys.PARAM_TIMEZONE);
        timeZone = timeZoneObj == null ? TimeZone.getTimeZone(ParamKeys.PARAM_GMT_TIMEZONE) : (TimeZone)timeZoneObj;      
        this.quoteUserSession = (QuoteUserSession)params.getParameter(ParamKeys.PARAM_SESSION_QUOTE_USER);
        if(params.getParameter(ParamKeys.PARAM_SPECIAL_BID_REASON_TEXT) != null)
        	this.data = (String[]) params.getParameter(ParamKeys.PARAM_SPECIAL_BID_REASON_TEXT);
    }
   
    public Locale getLocale() {
        return locale;
    }
    
    public TimeZone getTimeZone() {
    	return timeZone;
    }
    
    public TimeZone getGMTTimeZone()
    {
        return gmtTimeZone;
    }
   
    public TimeZone getDisplayTimeZone(){
        
        return displayTimeZone;
    }

	public QuoteUserSession getQuoteUserSession() {
		return quoteUserSession;
	}

	public void setQuoteUserSession(QuoteUserSession quoteUserSession) {
		this.quoteUserSession = quoteUserSession;
	}

	public boolean isTier1Reseller() {
		return this.getQuoteUserSession().isHouseAccountFlag();
	}

	public boolean isTier2Reseller() {
		return QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO.equalsIgnoreCase(getQuoteUserSession().getBpTierModel());
	}

	public boolean isDistributor() {
		return QuoteConstants.BPTierModel.BP_TIER_MODEL_ONE.equalsIgnoreCase(getQuoteUserSession().getBpTierModel()) && !isTier1Reseller();
	}
    public boolean isPGSFlag() {
		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode());
	}

	public String getAjaxJsonResult() {
		return ajaxJsonResult;
	}

	public void setAjaxJsonResult(String ajaxJsonResult) {
		this.ajaxJsonResult = ajaxJsonResult;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}
    
    
}
