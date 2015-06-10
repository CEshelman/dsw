package com.ibm.dsw.quote.common.domain;

import java.util.Date;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Status_Impl</code> class is the abstract implementation for the
 * common status domain object.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 21, 2007
 */
public abstract class Status_Impl implements Status {

    public String webQuoteNum = "";
    
    public String orderNum = "";

	public String statusCode = "";

    public String statusCodeDesc = "";

    public Date modifiedDate = new Date();

    public String getOrderNum() {
    	return orderNum;
    }
    
    public void setOrderNum(String orderNum) {
    	this.orderNum = orderNum;
    }
    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }

    /**
     * @return Returns the statusCode.
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * @return Returns the statusCodeDesc.
     */
    public String getStatusCodeDesc() {
        return statusCodeDesc;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }
}
