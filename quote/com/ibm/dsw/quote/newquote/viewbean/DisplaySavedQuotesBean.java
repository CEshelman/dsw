package com.ibm.dsw.quote.newquote.viewbean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.home.viewbean.QuoteRightColumnViewBean;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.newquote.contract.DisplaySavedQuoteContract;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.taglib.html.SelectOption;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplaySavedQuotesBean</code> class.
 * 
 * @author: wangxu@cn.ibm.com
 * 
 * Created on: Mar 13, 2007
 */
public class DisplaySavedQuotesBean extends QuoteRightColumnViewBean {
	transient List savedQuotes;

    DisplaySavedQuoteContract draftSalesQuoteContract;

    transient List timeFilterList;

    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        locale = (Locale) params.getParameter(FrameworkKeys.JADE_LOCALE_KEY);
        this.savedQuotes = (List) params.getParameter(NewQuoteParamKeys.PARAM_DRAFT_SALES_QUOTE_LIST);
        this.draftSalesQuoteContract = (DisplaySavedQuoteContract) params
                .getParameter(NewQuoteParamKeys.PARAM_DISPLAYED_DRAFT_SALES_QUOTE_CONTRACT);

        timeFilterList = new ArrayList();
        
        SelectOption so = new SelectOptionImpl(getI18NString(NewQuoteMessageKeys.ONE_WEEK,
                I18NBundleNames.NEW_QUOTE_BASE, locale), NewQuoteParamKeys.PARAM_ONE_WEEK_VALUE,
                NewQuoteParamKeys.PARAM_ONE_WEEK_VALUE.equals(draftSalesQuoteContract.getTimeFilter()));
        timeFilterList.add(so);
        
        so = new SelectOptionImpl(getI18NString(NewQuoteMessageKeys.ONE_MONTH,
                I18NBundleNames.NEW_QUOTE_BASE, locale), NewQuoteParamKeys.PARAM_ONE_MONTH_VALUE,
                NewQuoteParamKeys.PARAM_ONE_MONTH_VALUE.equals(draftSalesQuoteContract.getTimeFilter()));
        timeFilterList.add(so);
        
        so = new SelectOptionImpl(getI18NString(NewQuoteMessageKeys.THREE_MONTHS,
                I18NBundleNames.NEW_QUOTE_BASE, locale), NewQuoteParamKeys.PARAM_THREE_MONTHS_VALUE,
                NewQuoteParamKeys.PARAM_THREE_MONTHS_VALUE.equals(draftSalesQuoteContract.getTimeFilter()));
        timeFilterList.add(so);
        
        so = new SelectOptionImpl(getI18NString(NewQuoteMessageKeys.SIX_MONTHS,
                I18NBundleNames.NEW_QUOTE_BASE, locale), NewQuoteParamKeys.PARAM_SIX_MONTHS_VALUE,
                NewQuoteParamKeys.PARAM_SIX_MONTHS_VALUE.equals(draftSalesQuoteContract.getTimeFilter()));
        timeFilterList.add(so);
        
        so = new SelectOptionImpl(getI18NString(NewQuoteMessageKeys.PARAM_TIME_ALL,
                I18NBundleNames.NEW_QUOTE_BASE, locale), NewQuoteParamKeys.PARAM_TIME_ALL_VALUE,
                NewQuoteParamKeys.PARAM_TIME_ALL_VALUE.equals(draftSalesQuoteContract.getTimeFilter()));
        timeFilterList.add(so);
    }

    /**
     * @return Returns the draftSalesQuoteContract.
     */
    public DisplaySavedQuoteContract getDraftSalesQuoteContract() {
        return draftSalesQuoteContract;
    }

    public List getSavedQuotes() {
        return this.savedQuotes;
    }

    /**
     * @return Returns the timeFilterList.
     */
    public List getTimeFilterList() {
        return timeFilterList;
    }
    
    public int getDeleteDate(){
    	int interval = 0;
        try{
           interval = CacheProcessFactory.singleton().create().getSavedQuoteDeleteDate();
	    } catch (QuoteException e) {
	        LogContextFactory.singleton().getLogContext().error(this, e.getMessage());
	    }
	    return interval;
    }

    protected String getI18NString(String key, String basename, Locale locale) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        Object i18nValue = appCtx.getI18nValue(basename, locale, key);
        if (i18nValue instanceof String) {
            return (String) i18nValue;
        } else {
            return key;
        }
    }

}
