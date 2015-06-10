package com.ibm.dsw.quote.common.process;

import java.sql.Timestamp;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * @author Vivian
 * Sep 20, 2012
 */
public interface BusinessRuleProcess {
    public boolean needUpdateRule(String cnstntName, Timestamp ts) throws QuoteException;
    
    public Timestamp getLastUpdateTime();
    public String getXrule();
}
