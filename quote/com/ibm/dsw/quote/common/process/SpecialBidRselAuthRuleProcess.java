package com.ibm.dsw.quote.common.process;

import java.sql.Timestamp;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpecialBidRselAuthRuleProcess<code> class.
 *    
 *    
 * @author: changwei@cn.ibm.com
 * 
 * Creation date: Apr 21, 2009
 */

public interface SpecialBidRselAuthRuleProcess {
    
    public boolean needUpdateRule(String cnstntName, Timestamp ts) throws QuoteException;
    
    public Timestamp getLastUpdateTime();
    public String getXrule();
}
