package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteSetUserCookieContract<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Jun 14, 2007
 */

public class QuoteSetUserCookieContract extends QuoteBaseCookieContract {
    
    private String siteNumber; //customer number

    private String agreementNumber; //sapContractNumber
    
    private String webQuoteNum;
    
    private String dest;
    
    private String renewalQuoteNum;
    
    private String quoteNum;//SAP quote nubmer

    public String getQuoteNum() {
		return quoteNum;
	}
	public void setQuoteNum(String quoteNum) {
		this.quoteNum = quoteNum;
	}
	public String getAgreementNumber() {
        return agreementNumber;
    }
    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }
    public String getSiteNumber() {
        return siteNumber;
    }
    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }
    public String getDest() {
        return dest;
    }
    public void setDest(String dest) {
        this.dest = dest;
    }
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
    public String getRenewalQuoteNum() {
        return renewalQuoteNum;
    }
    public void setRenewalQuoteNum(String renewalQuoteNum) {
        this.renewalQuoteNum = renewalQuoteNum;
    }
}
