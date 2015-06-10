package com.ibm.dsw.quote.submittedquote.contract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="zgsun@cn.ibm.com">Owen Sun </a> <br/>
 * 
 * Creation date: Apr 5, 2007
 */

/**
 *$Log: SaveBidIterationContract.java,v $
 *Revision 1.1  2010/06/13 03:00:47  zhigang
 *bid iteration implementation, RTC task 2918
 *
 * @author Administrator
 *
 */
public class SaveBidIterationContract extends SubmittedQuoteBaseContract {

    private String redirectURL = null;
    /**
     * @return Returns the redirectURL.
     */
    public String getRedirectURL() {
        return redirectURL;
    }
    /**
     * @param redirectURL The redirectURL to set.
     */
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
}
