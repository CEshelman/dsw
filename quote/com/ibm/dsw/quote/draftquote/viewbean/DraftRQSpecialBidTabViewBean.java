package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SpecialBidCommon;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DraftRQSpecialBidTabViewBean</code> class .
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 23, 2007
 */
public class DraftRQSpecialBidTabViewBean extends DraftRQBaseViewBean {

   
    public SpecialBidCommon common = null;

    private String downloadFileURL = null;
    
    public String getDisplayTabAction() {
        return DraftQuoteActionKeys.DISPLAY_SPECIAL_BID_TAB;
    }

    /**
     *  
     */

    public String getPostTabAction() {
        return DraftQuoteActionKeys.POST_SPECIAL_BID_TAB;
    }

    public void collectResults(Parameters params) throws ViewBeanException{
        super.collectResults(params);
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        String userId = params.getParameterAsString(DraftQuoteParamKeys.SESSION_USER_ID);
        try {
			common = new SpecialBidCommon(this.renewalQuote, userId);
		} catch (QuoteException e) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
		}
        
        downloadFileURL = HtmlUtil.getURLForAction(ActionKeys.DOWNLOAD_ATTACHMENT);
    }

    public boolean isQuoteEditable(){        
        return renewalQuote.getQuoteAccess().isCanEditRQ();        
    }
    
    public Collection generateRegionOptions() throws Exception {
        Collection collection = new ArrayList();
        String sbRgn = getQuote().getSpecialBidInfo().getSpBidRgn();
        String val = "";
        int count = this.common.regions.size();

        if (count == 0) {
            String custCntryCode = getQuote().getCustomer().getCountryCode();
            String custCntry = getCustCntry();
            collection.add(new SelectOptionImpl(custCntry, custCntryCode, true));
        } else if (count == 1) {
            collection.add(new SelectOptionImpl((String) this.common.regions.get(0), (String) this.common.regions
                    .get(0), true));
        } else if (count > 1) {
            ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
            String boDefaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                    DraftQuoteMessageKeys.DEAFULT_OPTION);
            if (StringUtils.isBlank(sbRgn)) {
                collection.add(new SelectOptionImpl(boDefaultOption, "", true));
            } else {
                collection.add(new SelectOptionImpl(boDefaultOption, "", false));
            }
            Iterator itr = this.common.regions.iterator();
            while (itr.hasNext()) {
                val = (String) itr.next();
                if (sbRgn != null && val != null && sbRgn.trim().equalsIgnoreCase(val.trim())) {
                    collection.add(new SelectOptionImpl(val, val, true));
                } else {
                    collection.add(new SelectOptionImpl(val, val, false));
                }
            }
        }

        return collection;
    }

    public Collection generateDistrictOptions() throws Exception {
        Collection collection = new ArrayList();
        String sbDst = getQuote().getSpecialBidInfo().getSpBidDist();
        String val = "";
        Iterator itr = this.common.districts.iterator();
        int count = this.common.districts.size();
        if (count == 0) {
            String custCntryCode = getQuote().getCustomer().getCountryCode();
            String custCntry = getCustCntry();
            collection.add(new SelectOptionImpl(custCntry, custCntryCode, true));
        } else if (count == 1) {
            val = (String)this.common.districts.get(0);
            collection.add(new SelectOptionImpl(val, val, true));
        } else if (count > 1) {
            ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
            String boDefaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                    DraftQuoteMessageKeys.DEAFULT_OPTION);
            if (StringUtils.isBlank(sbDst)) {
                collection.add(new SelectOptionImpl(boDefaultOption, "", true));
            } else {
                collection.add(new SelectOptionImpl(boDefaultOption, "", false));
            }

            while (itr.hasNext()) {
                val = (String) itr.next();
                if (sbDst != null && val != null && sbDst.trim().equalsIgnoreCase(val.trim())) {
                    collection.add(new SelectOptionImpl(val, val, true));
                } else {
                    collection.add(new SelectOptionImpl(val, val, false));
                }
            }
        }
        return collection;
    }
    
    public Collection generateIndustryOptions() throws Exception {
        Collection collection = new ArrayList();
        String sbInduSeg = getQuote().getSpecialBidInfo().getSpBidCustIndustryCode();
        String val = "";
        Iterator itr = this.common.industrySegments.iterator();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String boDefaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.DEAFULT_OPTION);
        if (StringUtils.isBlank(sbInduSeg)) {
            collection.add(new SelectOptionImpl(boDefaultOption, "", true));
        } else {
            collection.add(new SelectOptionImpl(boDefaultOption, "", false));
        }
        
        while (itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj) itr.next();
            val = codeDescObj.getCode();
            String desc = codeDescObj.getCodeDesc();
            if (sbInduSeg != null && val != null && sbInduSeg.trim().equalsIgnoreCase(val.trim())) {
                collection.add(new SelectOptionImpl(desc, val, true));
            } else {
                collection.add(new SelectOptionImpl(desc, val, false));
            }
            
        }
        return collection;
    }
    /**
     * @return Returns the downloadFileURL.
     */
    public String getDownloadFileURL() {
        return downloadFileURL;
    }
    
    public Collection generateSbTypeOptions() throws Exception {
        Collection collection = new ArrayList();
        String sbType = getQuote().getSpecialBidInfo().getSpBidType();
        String val = "";
        Iterator itr = this.common.specialBidTypes.iterator();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String boDefaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.DEAFULT_OPTION);

        while (itr.hasNext()) {
            val = (String) itr.next();
            if (sbType != null && val != null && sbType.trim().equalsIgnoreCase(val.trim())) {
                collection.add(new SelectOptionImpl(val, val, true));
            } else {
                collection.add(new SelectOptionImpl(val, val, false));
            }
        }
        return collection;
    }
    
    protected SpecialBidCommon getSpecialBidCommon()
    {
    	return common;
    }
}
