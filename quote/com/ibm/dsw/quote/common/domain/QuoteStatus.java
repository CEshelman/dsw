package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteStatus<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-6
 */

public interface QuoteStatus extends Status {
    public static final String P_TERMINATED = "E0007";

    public static final String S_BLOCKED_4_AUTORENEWAL = "E0034";

    public String getStatusType();

    public void setStatusType(String statusType) throws TopazException;
    
    public String getQuoteNum();
    
    public void setQuoteNum(String quoteNum) throws TopazException;

}
