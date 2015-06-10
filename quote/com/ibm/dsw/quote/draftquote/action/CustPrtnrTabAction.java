package com.ibm.dsw.quote.draftquote.action;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.CustPrtnrTabContract;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustPrtnrTabAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-22
 */

public class CustPrtnrTabAction extends DraftQuoteBaseAction {

    protected static final String UPLOAD_WARNING_SPLITTER = ",,";

    protected static final String CUST_NUM = "customernumber";

    protected static final String CTRCT_NUM = "contractnumber";

    protected static final String PARTLIST = "partlist,";
    
    protected static final String OCSPARTLIST = "ocspartlist,";
    
    protected static final String SAASPARTLIST = "saaspartlist,";
    
    protected static final String SWSAASPARTLIST = "sw_including_saaspartlist,";
    
    protected static final String SAASSWPARTLIST = "saas_including_swpartlist,";
    
    protected static final String FREQUENCYPARTLIST = "frequencypartlist,";
    
    protected static final String INCLUDINGSAASPARTLIST = "including_saaspartlist";
    
    protected static final String DUPLICATEDSAASPARTLIST = "duplicatesaaspartlist,";
    
    protected static final String INACTIVESAASPARTLIST = "inactivesaaspartlist,";
    
    protected static final String UNCERTIFIEDSAASPARTLIST = "uncertifiedsaaspartlist,";
    
    protected static final String PARTLIST_SPLITTER = "_";
    
    protected static final String MAXPARTQTY = "maxpartqty";
    
    

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.DraftQuoteBaseAction#getSessionQuoteDetail(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void getDraftQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler) throws QuoteException {
        handleUploadMessage(contract, handler);
        handleCreateFromOrderMessage(contract, handler);
        CustPrtnrTabContract cpContract = (CustPrtnrTabContract) contract;
        if (quote != null) {
            QuoteProcess process = QuoteProcessFactory.singleton().create();
            QuoteUserSession quoteUserSession = cpContract.getQuoteUserSession();
        	String bpSiteNum = "";
        	if(this.isPGSEnv(quoteUserSession)){
        		bpSiteNum = quoteUserSession.getSiteNumber();
        	}
            process.getQuoteDetailForCustTab(quote, bpSiteNum);
        }

        if (quote == null || quote.getQuoteHeader() == null) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        } else if (QuoteConstants.QUOTE_TYPE_RENEWAL.equals(quote.getQuoteHeader().getQuoteTypeCode())) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_RQ_CUST_PRTNR_TAB);
        } else {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SQ_CUST_PRTNR_TAB);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.draftQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return "";
    }

    protected void handleUploadMessage(ProcessContract contract, ResultHandler handler) {
        CustPrtnrTabContract cpContract = (CustPrtnrTabContract) contract;
        String uploadWarning = cpContract.getUploadWarning();
        Locale locale = cpContract.getLocale();

        if (StringUtils.isNotBlank(uploadWarning)) {
            String[] invalids = uploadWarning.split(UPLOAD_WARNING_SPLITTER);
            MessageBean mBean = MessageBeanFactory.create();

            for (int i = 0; i < invalids.length; i++) {
                String invalid = invalids[i];

                if (CUST_NUM.equalsIgnoreCase(invalid)) {
                    String msgInfo = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INVALID_CUST,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (CTRCT_NUM.equalsIgnoreCase(invalid)) {
                    String msgInfo = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INVALID_CTRCT,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (INCLUDINGSAASPARTLIST.equalsIgnoreCase(invalid)) {
                    String msgInfo = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INCLUDING_SAAS_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(DUPLICATEDSAASPARTLIST)) {
                    String partList = invalid.substring(DUPLICATEDSAASPARTLIST.length());
                    StringTokenizer stParts = new StringTokenizer(partList, PARTLIST_SPLITTER);
                    StringBuffer sbParts = new StringBuffer();
                    String msg = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_DUPLICATE_SAAS_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);

                    while (stParts.hasMoreTokens()) {
                        String part = stParts.nextToken();
                        if (sbParts.length() == 0)
                            sbParts.append("[" + part + "]");
                        else
                            sbParts.append(", [" + part + "]");
                    }

                    String[] args = { sbParts.toString() };
                    String msgInfo = MessageFormat.format(msg, args);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(INACTIVESAASPARTLIST)) {
                    String partList = invalid.substring(INACTIVESAASPARTLIST.length());
                    StringTokenizer stParts = new StringTokenizer(partList, PARTLIST_SPLITTER);
                    StringBuffer sbParts = new StringBuffer();
                    String msg = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INACTIVE_SAAS_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);

                    while (stParts.hasMoreTokens()) {
                        String part = stParts.nextToken();
                        if (sbParts.length() == 0)
                            sbParts.append("[" + part + "]");
                        else
                            sbParts.append(", [" + part + "]");
                    }

                    String[] args = { sbParts.toString() };
                    String msgInfo = MessageFormat.format(msg, args);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                
                } else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(UNCERTIFIEDSAASPARTLIST)) {
                    String partList = invalid.substring(UNCERTIFIEDSAASPARTLIST.length());
                    StringTokenizer stParts = new StringTokenizer(partList, PARTLIST_SPLITTER);
                    StringBuffer sbParts = new StringBuffer();
                    String msg = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_UNCERTIFIED_SAAS_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);

                    while (stParts.hasMoreTokens()) {
                        String part = stParts.nextToken();
                        if (sbParts.length() == 0)
                            sbParts.append("[" + part + "]");
                        else
                            sbParts.append(", [" + part + "]");
                    }

                    String[] args = { sbParts.toString() };
                    String msgInfo = MessageFormat.format(msg, args);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                
                } else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(PARTLIST)) {
                    String partList = invalid.substring(PARTLIST.length());
                    StringTokenizer stParts = new StringTokenizer(partList, PARTLIST_SPLITTER);
                    StringBuffer sbParts = new StringBuffer();
                    String msg = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INVALID_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);

                    while (stParts.hasMoreTokens()) {
                        String part = stParts.nextToken();
                        if (sbParts.length() == 0)
                            sbParts.append("[" + part + "]");
                        else
                            sbParts.append(", [" + part + "]");
                    }

                    String[] args = { sbParts.toString() };
                    String msgInfo = MessageFormat.format(msg, args);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(SAASPARTLIST)) {
                    String partList = invalid.substring(SAASPARTLIST.length());
                    StringTokenizer stParts = new StringTokenizer(partList, PARTLIST_SPLITTER);
                    StringBuffer sbParts = new StringBuffer();
                    String msg = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INVALID_SAAS_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);

                    while (stParts.hasMoreTokens()) {
                        String part = stParts.nextToken();
                        if (sbParts.length() == 0)
                            sbParts.append("[" + part + "]");
                        else
                            sbParts.append(", [" + part + "]");
                    }

                    String[] args = { sbParts.toString() };
                    String msgInfo = MessageFormat.format(msg, args);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(SWSAASPARTLIST)) {
                    String partList = invalid.substring(SWSAASPARTLIST.length());
                    StringTokenizer stParts = new StringTokenizer(partList, PARTLIST_SPLITTER);
                    StringBuffer sbParts = new StringBuffer();
                    String msg = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INVALID_SW_INCLUDING_SAAS_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);

                    while (stParts.hasMoreTokens()) {
                        String part = stParts.nextToken();
                        if (sbParts.length() == 0)
                            sbParts.append("[" + part + "]");
                        else
                            sbParts.append(", [" + part + "]");
                    }

                    String[] args = { sbParts.toString() };
                    String msgInfo = MessageFormat.format(msg, args);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(SAASSWPARTLIST)) {
                    String partList = invalid.substring(SAASSWPARTLIST.length());
                    StringTokenizer stParts = new StringTokenizer(partList, PARTLIST_SPLITTER);
                    StringBuffer sbParts = new StringBuffer();
                    String msg = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INVALID_SAAS_INCLUDING_SW_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);

                    while (stParts.hasMoreTokens()) {
                        String part = stParts.nextToken();
                        if (sbParts.length() == 0)
                            sbParts.append("[" + part + "]");
                        else
                            sbParts.append(", [" + part + "]");
                    }

                    String[] args = { sbParts.toString() };
                    String msgInfo = MessageFormat.format(msg, args);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(FREQUENCYPARTLIST)) {
                    String partList = invalid.substring(FREQUENCYPARTLIST.length());
                    StringTokenizer stParts = new StringTokenizer(partList, PARTLIST_SPLITTER);
                    StringBuffer sbParts = new StringBuffer();
                    String msg = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INVALID_FREQUENCY_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);

                    while (stParts.hasMoreTokens()) {
                        String part = stParts.nextToken();
                        if (sbParts.length() == 0)
                            sbParts.append("[" + part + "]");
                        else
                            sbParts.append(", [" + part + "]");
                    }

                    String[] args = { sbParts.toString() };
                    String msgInfo = MessageFormat.format(msg, args);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                }else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(OCSPARTLIST)) {
                    String msgInfo = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INVALID_OCS_PARTS,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (StringUtils.isNotBlank(invalid) && invalid.startsWith(NewQuoteParamKeys.UPLOAD_WARNING_INVALID_FUTURE_DATE)) {
                    String msgInfo = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_INVALID_FUTURE_DATE,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                } else if (MAXPARTQTY.equalsIgnoreCase(invalid)) {
                    String msgInfo = this.getI18NString(DraftQuoteMessageKeys.UPLOAD_WARNING_MAX_PART_QTY,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
                    String[] args = {String.valueOf(PartPriceConfigFactory.singleton().getElaLimits())};
                    msgInfo = MessageFormat.format(msgInfo, args);
                    mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
                }
            }

            handler.setMessage(mBean);
        }
    }
    private void handleCreateFromOrderMessage(ProcessContract contract, ResultHandler handler) {
        CustPrtnrTabContract cpContract = (CustPrtnrTabContract) contract;
        String invalidPartsWarning = cpContract.getInvalidPartsWarning();
        Locale locale = cpContract.getLocale();

        if (StringUtils.isNotBlank(invalidPartsWarning)) {
            MessageBean mBean = MessageBeanFactory.create();
            StringTokenizer stParts = new StringTokenizer(invalidPartsWarning, PARTLIST_SPLITTER);
            StringBuffer sbParts = new StringBuffer();
            String msg = this.getI18NString(DraftQuoteMessageKeys.CREATE_QT_INVALID_PARTS_WARNING,
                    MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);

            while (stParts.hasMoreTokens()) {
                String part = stParts.nextToken();
                if (sbParts.length() == 0)
                    sbParts.append("[" + part + "]");
                else
                    sbParts.append(", [" + part + "]");
            }

            Object[] args = { sbParts.toString() };
            String msgInfo = MessageFormat.format(msg, args);
            mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
            handler.setMessage(mBean);
        }

    }
    
    private boolean isPGSEnv(QuoteUserSession quoteUserSession){
    	if(null != quoteUserSession)
    		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(quoteUserSession.getAudienceCode());
    	else return false;
	}

}
