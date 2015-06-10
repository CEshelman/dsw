package com.ibm.dsw.quote.submittedquote.contract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ICNPreCreditContract<code> class.
 *    
 * @author: lthalla@us.ibm.com
 * 
 * Creation date: May 08, 2007
 */
public class ICNPreCreditContract extends SubmittedQuoteBaseContract {
    
    private String chkReqCustNo;
    private String chkReqCredChk;
    
    /**
     * @return Returns the chkReqCredChk.
     */
    public String getChkReqCredChk() {
        return chkReqCredChk;
    }
    /**
     * @param chkReqCredChk The chkReqCredChk to set.
     */
    public void setChkReqCredChk(String chkReqCredChk) {
        this.chkReqCredChk = chkReqCredChk;
    }
    /**
     * @return Returns the chkReqCustNo.
     */
    public String getChkReqCustNo() {
        return chkReqCustNo;
    }
    /**
     * @param chkReqCustNo The chkReqCustNo to set.
     */
    public void setChkReqCustNo(String chkReqCustNo) {
        this.chkReqCustNo = chkReqCustNo;
    }
}
