package com.ibm.dsw.quote.submittedquote.contract;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>UpdateQuotePartnerContract<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Jul 1, 2008
 */

public class UpdateQuotePartnerContract extends SubmittedQuoteBaseContract {
    
    protected String chkEmailY9PartnerAddrList = null;
    protected String chkEmailAddiPartnerAddr = null;
    protected String custEmailAddiPartnerAddr = null;
    
    public String getChkEmailAddiPartnerAddr() {
        return chkEmailAddiPartnerAddr;
    }
    
    public void setChkEmailAddiPartnerAddr(String chkEmailAddiPartnerAddr) {
        this.chkEmailAddiPartnerAddr = chkEmailAddiPartnerAddr;
    }
    
    public String getChkEmailY9PartnerAddrList() {
        return chkEmailY9PartnerAddrList;
    }
    
    public void setChkEmailY9PartnerAddrList(String chkEmailY9PartnerAddrList) {
        this.chkEmailY9PartnerAddrList = chkEmailY9PartnerAddrList;
    }
    
    public String getCustEmailAddiPartnerAddr() {
        return custEmailAddiPartnerAddr;
    }
    
    public void setCustEmailAddiPartnerAddr(String custEmailAddiPartnerAddr) {
        this.custEmailAddiPartnerAddr = custEmailAddiPartnerAddr;
    }

}
