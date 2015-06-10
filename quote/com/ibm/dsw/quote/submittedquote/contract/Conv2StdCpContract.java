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
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Conv2StdCpContract extends SubmittedQuoteBaseContract {

    private String forwardFlag = null;
    
    /**
     * @return Returns the forwardFlag.
     */
    public String getForwardFlag() {
        return forwardFlag;
    }
    /**
     * @param forwardFlag The forwardFlag to set.
     */
    public void setForwardFlag(String forwardFlag) {
        this.forwardFlag = forwardFlag;
    }

}
