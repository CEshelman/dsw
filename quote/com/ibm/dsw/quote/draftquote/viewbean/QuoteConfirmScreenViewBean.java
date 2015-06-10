package com.ibm.dsw.quote.draftquote.viewbean;

import org.apache.commons.lang.BooleanUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteConfirmScreenViewBean<code> class.
 *    
 * @author: doris_yuen@us.ibm.com
 * 
 * Creation date: April 4, 2007
 */

public class QuoteConfirmScreenViewBean extends BaseViewBean {
    
    protected Quote quote = null;
    protected transient QuoteHeader qh = null; 
    
    protected String webQuoteNum = "";
    protected String sapConfirmNum = "";
    protected String passcode = "";
    protected boolean isRenewalQuote = false;
    protected boolean isSalesQuote = false;
    protected boolean isNewCustomer = false;
    protected boolean supprsPARegstrnEmailFlag = false;
    private String PARegstrnEmailSendFlag;
    private boolean isDisplayCustCofirmation = false;
    
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        
        quote = (Quote) params.getParameter(ParamKeys.PARAM_QUOTE_OBJECT);
        
        if (quote == null || quote.getQuoteHeader() == null)
            return;
        
        if ( quote.getQuoteHeader().getQuoteTypeCode().equals(QuoteConstants.QUOTE_TYPE_RENEWAL) ) {
            isRenewalQuote = true;
        } else {
            isSalesQuote = true;
        }
        
        qh = quote.getQuoteHeader();
        
        webQuoteNum = qh.getWebQuoteNum();
        sapConfirmNum = qh.getSapIntrmdiatDocNum();
        
        Customer cust = quote.getCustomer();

        //if (cust != null && qh.hasNewCustomer() && !qh.hasExistingCustomer()) {
        if (cust != null && qh.hasNewCustomer()) {
            isNewCustomer = true;
            passcode = cust.getTempAccessNum();
            supprsPARegstrnEmailFlag = cust.getSupprsPARegstrnEmailFlag() == 1;
        }
        
        PARegstrnEmailSendFlag = params.getParameterAsString(ParamKeys.PARAM_PA_REG_EMAIL_SEND);
        //if OEM quote don't display new cust confirmation
        //fix PL:The sections for new PA/PAE customer should be hidden for CSA quote because CSA customer will not have access on SWSO.
        isDisplayCustCofirmation = !(qh.isCSRAQuote()||qh.isCSTAQuote()) && BooleanUtils.toBoolean(this.PARegstrnEmailSendFlag) && !qh.isOEMQuote() && !qh.isSSPQuote();
       
    }

    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum == null ? "" : webQuoteNum;
    }

    /**
     * @return Returns the sapConfirmNum.
     */
    public String getSapConfirmNum() {
        return sapConfirmNum == null ? "" : sapConfirmNum;
    }

    /**
     * @return Returns the passcode.
     */
    public String getPasscode() {
        return passcode == null ? "" : passcode;
    }

    /**
     * @return Returns the isRenewalQuote.
     */
    public boolean isRenewalQuote() {
        return isRenewalQuote;
    }
    
    /**
     * @return Returns the isSalesQuote.
     */
    public boolean isSalesQuote() {
        return isSalesQuote;
    }
    
    public boolean isNewCustomer() {
        return isNewCustomer;
    }
    
    public boolean isDisplayCustCofirmation() {
        return isDisplayCustCofirmation;
    }
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("webQuoteNum = ").append(webQuoteNum).append("\n");
        buffer.append("sapConfirmNum = ").append(sapConfirmNum).append("\n");
        buffer.append("passcode = ").append(passcode).append("\n");
        buffer.append("isRenewalQuote = ").append(isRenewalQuote).append("\n");
        buffer.append("isSalesQuote = ").append(isSalesQuote).append("\n");
        buffer.append("isNewCustomer = ").append(isNewCustomer).append("\n");
        buffer.append("supprsPARegstrnEmailFlag = ").append(supprsPARegstrnEmailFlag).append("\n");
        return buffer.toString();
    }
}