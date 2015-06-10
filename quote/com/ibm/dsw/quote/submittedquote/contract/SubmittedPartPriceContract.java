package com.ibm.dsw.quote.submittedquote.contract;

import is.domainx.User;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.common.config.QuoteParamKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 22, 2007
 */

public class SubmittedPartPriceContract extends SubmittedQuoteBaseContract {

    Locale locale;
    private User user;
    private String lineItemDetailFlag;
    private String brandDetailFlag;
    private String updateLineItemDateFlag;
    private int pastYearLimit = 0;
    private static final String UN_SUPPORT_PARAM = "UN_SUPPORT_PARAM";
    private String offerPrice;
    private String applyOfferPriceFlag;
    //Attribute for increase price after special bid approval
    private transient List originalLineItemList;

    HashMap items = new HashMap();

    public static class LineItemParameter {
        public String key;

        public String ovrdStartDay;

        public String ovrdStartMonth;

        public String ovrdStartYear;

        public String ovrdEndDay;

        public String ovrdEndMonth;

        public String ovrdEndYear;
        
        public boolean startDateBackDated = false;
        
        public String revnStrmCode = "";
        
        public String getOvrdStartDate() {        	
            return ovrdStartYear + "-" + ovrdStartMonth + "-" + ovrdStartDay;
        }

        public String getOvrdEndDate() {
            return ovrdEndYear + "-" + ovrdEndMonth + "-" + ovrdEndDay;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append(this.key + ":");
            buffer.append("\tOverride start date[").append(this.getOvrdStartDate());
            buffer.append("]\tOverride end date[").append(this.getOvrdEndDate());
            return buffer.toString();
        }
    }

    public HashMap getItems() {
        return items;
    }

    /**
     * @return Returns the lineItemDetailFlag.
     */
    public String getLineItemDetailFlag() {
        return lineItemDetailFlag;
    }

    /**
     * @param lineItemDetailFlag
     *            The lineItemDetailFlag to set.
     */
    public void setLineItemDetailFlag(String lineItemDetailFlag) {
        this.lineItemDetailFlag = lineItemDetailFlag;
    }
    
    /**
     * @return Returns the brandDetailFlag.
     */
    public String getBrandDetailFlag() {
        return brandDetailFlag;
    }
    
    /**
     * @param brandDetailFlag
     *            The brandDetailFlag to set.
     */
    public void setBrandDetailFlag(String brandDetailFlag) {
        this.brandDetailFlag = brandDetailFlag;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void load(Parameters parameters, JadeSession session) {
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.load(parameters, session);

        locale = (Locale) session.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
        if (locale == null) {
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        }
        user = (User)session.getAttribute(SessionKeys.SESSION_USER);
        this.lineItemDetailFlag = parameters.getParameterAsString(SubmittedQuoteParamKeys.LINE_ITEM_DETAIL_FALG);
        this.brandDetailFlag = parameters.getParameterAsString(SubmittedQuoteParamKeys.BR_DETAIL_FLAG);
        this.updateLineItemDateFlag = parameters.getParameterAsString(SubmittedQuoteParamKeys.UPDATE_LINE_ITEM_DATE_FLAG);
        this.offerPrice = parameters.getParameterAsString(QuoteParamKeys.APPLY_OFFER_INPUT);
        this.applyOfferPriceFlag = parameters.getParameterAsString(SubmittedQuoteParamKeys.APPLY_OFFER_PRICE_FLAG);
        
        
        logContext.debug(this, "locale = " + locale);
        logContext.debug(this, "lineItemDetailFlag = " + lineItemDetailFlag);
        logContext.debug(this, "brandDetailFlag = " + brandDetailFlag);
        logContext.debug(this, "updateLineItemFlag = " + updateLineItemDateFlag);
        logContext.debug(this, "pastYearLimit = " + pastYearLimit);
        
        if(null == this.updateLineItemDateFlag){
            return;
        }
        // load override date for editable quote
        Iterator it = parameters.getParameterNames();

        while (it.hasNext()) {
            String name = (String) it.next();

            try {
                String value = parameters.getParameterAsString(name);

                String key = getKey(name);

                if (UN_SUPPORT_PARAM.equals(key)) {
                    continue;
                }

                LineItemParameter item = getLineItem(key);

                if (name.endsWith(SubmittedQuoteParamKeys.partNumberSuffix)) {
                    item.key = value.trim();
                } else if (name.endsWith(SubmittedQuoteParamKeys.ovrdDtStartDaySuffix)) {

                    item.ovrdStartDay = value.trim();

                } else if (name.endsWith(SubmittedQuoteParamKeys.ovrdDtStartMonthSuffix)) {

                    item.ovrdStartMonth = value.trim();

                } else if (name.endsWith(SubmittedQuoteParamKeys.ovrdDtStartYearSuffix)) {
                    item.ovrdStartYear = value.trim();

                } else if (name.endsWith(SubmittedQuoteParamKeys.ovrdDtEndDaySuffix)) {

                    item.ovrdEndDay = value.trim();

                } else if (name.endsWith(SubmittedQuoteParamKeys.ovrdDtEndMonthSuffix)) {

                    item.ovrdEndMonth = value.trim();

                } else if (name.endsWith(SubmittedQuoteParamKeys.ovrdDtEndYearSuffix)) {

                    item.ovrdEndYear = value.trim();
                    
                } else if(name.endsWith(SubmittedQuoteParamKeys.REVN_STRM_CODE_SUFFIX)){
                    
                    item.revnStrmCode = value.trim();
                    
                }                
                //enforce the end day of month.
                if(StringUtils.isBlank(item.ovrdEndDay)&&StringUtils.isNotBlank(item.ovrdEndMonth)&&StringUtils.isNotBlank(item.ovrdEndYear)){
                    Date ovrdEndDate = DateUtil.parseDate(item.ovrdEndYear+"-"+item.ovrdEndMonth+"-01");
                    Date endDay = DateUtil.moveToLastDayofMonth(ovrdEndDate);
                    item.ovrdEndDay =DateUtil.formatDate(endDay,"dd");
                }
                
            } catch (Throwable e) {
                logContext.error(this, "Load Parameter Error " + e.getMessage());
            }
        }
        logContext.debug(this, "Load Parameter finished:");
        logContext.debug(this, items.toString());
    }

    private LineItemParameter getLineItem(String key) {
        if (key != null && key.trim().length() != 0) {
            LineItemParameter item = (LineItemParameter) items.get(key);
            if (null == item) {
                item = new LineItemParameter();
                item.key = key;
            }
            items.put(key, item);
            return item;
        }
        return null;
    }

    private String getKey(String value) {

        if (value != null && value.trim().length() != 0) {
            if (value.endsWith(SubmittedQuoteParamKeys.partNumberSuffix)) {
                int index = value.lastIndexOf(SubmittedQuoteParamKeys.partNumberSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(SubmittedQuoteParamKeys.ovrdDtStartDaySuffix)) {
                int index = value.lastIndexOf(SubmittedQuoteParamKeys.ovrdDtStartDaySuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(SubmittedQuoteParamKeys.ovrdDtStartMonthSuffix)) {
                int index = value.lastIndexOf(SubmittedQuoteParamKeys.ovrdDtStartMonthSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(SubmittedQuoteParamKeys.ovrdDtStartYearSuffix)) {
                int index = value.lastIndexOf(SubmittedQuoteParamKeys.ovrdDtStartYearSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(SubmittedQuoteParamKeys.ovrdDtEndDaySuffix)) {
                int index = value.lastIndexOf(SubmittedQuoteParamKeys.ovrdDtEndDaySuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(SubmittedQuoteParamKeys.ovrdDtEndMonthSuffix)) {
                int index = value.lastIndexOf(SubmittedQuoteParamKeys.ovrdDtEndMonthSuffix);
                value = value.substring(0, index);
            } else if (value.endsWith(SubmittedQuoteParamKeys.ovrdDtEndYearSuffix)) {
                int index = value.lastIndexOf(SubmittedQuoteParamKeys.ovrdDtEndYearSuffix);
                value = value.substring(0, index);
            } else if(value.endsWith(SubmittedQuoteParamKeys.REVN_STRM_CODE_SUFFIX)){
            	int index = value.lastIndexOf(SubmittedQuoteParamKeys.REVN_STRM_CODE_SUFFIX);
            	value = value.substring(0, index);
            } else {
                value = UN_SUPPORT_PARAM;
            }

        }
        return value;
    }
    /**
     * @return Returns the updateLineItemDateFlag.
     */
    public String getUpdateLineItemDateFlag() {
        return updateLineItemDateFlag;
    }
    /**
     * @param updateLineItemDateFlag The updateLineItemDateFlag to set.
     */
    public void setUpdateLineItemDateFlag(String updateLineItemDateFlag) {
        this.updateLineItemDateFlag = updateLineItemDateFlag;
    }
    public User getUser() {
        return user;
    }

    public int getPastYearLimit(){
    	return pastYearLimit;
    }
    
    public String getOfferPrice(){
        return offerPrice;
    }
    
    public List getOriginalLineItemList(){
        return originalLineItemList;
    }
    
    public void setOriginalLineItemList(List originalLineItemList){
        this.originalLineItemList = originalLineItemList;
    }
    /**
     * @return Returns the applyOfferPriceFlag.
     */
    public String getApplyOfferPriceFlag() {
        return applyOfferPriceFlag;
    }
    /**
     * @param applyOfferPriceFlag The applyOfferPriceFlag to set.
     */
    public void setApplyOfferPriceFlag(String applyOfferPriceFlag) {
        this.applyOfferPriceFlag = applyOfferPriceFlag;
    }
}
