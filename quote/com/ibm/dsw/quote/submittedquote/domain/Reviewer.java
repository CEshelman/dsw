package com.ibm.dsw.quote.submittedquote.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Reviewer</code> class is to define the Reviewer domain object.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public interface Reviewer {

    public String getReviewEmail();

    public String getWebQuoteNum();
    
    public String getUserEmail();

}
