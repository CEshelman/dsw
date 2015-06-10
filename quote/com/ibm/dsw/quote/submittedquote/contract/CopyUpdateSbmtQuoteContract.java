package com.ibm.dsw.quote.submittedquote.contract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CopyUpdateSbmtQuoteContract<code> class.
 *    
 * @author: lthalla@us.ibm.com
 * 
 * Creation date: May 04, 2007
 */
public class CopyUpdateSbmtQuoteContract extends SubmittedQuoteBaseContract {
    
    private String includeRef;
    private String copyUpdateFlag;
    private String redirectURL;
    
    /**
     * @return Returns the copyUpdateFlag.
     */
    public String getCopyUpdateFlag() {
        return copyUpdateFlag;
    }
    /**
     * @param copyUpdateFlag The copyUpdateFlag to set.
     */
    public void setCopyUpdateFlag(String copyUpdateFlag) {
        this.copyUpdateFlag = copyUpdateFlag;
    }
    /**
     * @return Returns the includeRef.
     */
    public String getIncludeRef() {
        return includeRef;
    }
    /**
     * @param includeRef The includeRef to set.
     */
    public void setIncludeRef(String includeRef) {
        this.includeRef = includeRef;
    }
    
    public String getRedirectURL() {
        return redirectURL;
    }
    
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
}
