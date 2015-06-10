package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteStatus_Impl<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-6
 */

public abstract class QuoteStatus_Impl extends Status_Impl implements QuoteStatus {
    
    public String statusType = "";
    
    public String quoteNum = "";

    public String getQuoteNum() {
		return quoteNum;
	}

	/**
     *  
     */
    public QuoteStatus_Impl() {
        super();
    }

    /**
     * @return Returns the statusType.
     */
    public String getStatusType() {
        return statusType;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("statusType = ").append(statusType).append("\n");
        buffer.append("statusCode = ").append(statusCode).append("\n");
        buffer.append("statusCodeDesc = ").append(statusCodeDesc).append("\n");
        return buffer.toString();
    }
}
