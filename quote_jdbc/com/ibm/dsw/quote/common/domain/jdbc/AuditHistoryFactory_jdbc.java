package com.ibm.dsw.quote.common.domain.jdbc;

import com.ibm.dsw.quote.common.domain.AuditHistory;
import com.ibm.dsw.quote.common.domain.AuditHistoryFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AuditHistoryFactory_jdbc</code> class is the jdbc implementation
 * for the AuditHistory domain factory.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 14, 2007
 */
public class AuditHistoryFactory_jdbc extends AuditHistoryFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.AuditHistoryFactory#createAuditHistory(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public AuditHistory createAuditHistory(String webQuoteNum, Integer lineItemNum, String userEmail, String userAction,
            String oldValue, String newValue) throws TopazException {
        AuditHistory_jdbc auditHist = new AuditHistory_jdbc();
        
        auditHist.setWebQuoteNum(webQuoteNum);
        auditHist.setLineItemNum(lineItemNum);
        auditHist.setUserEmail(userEmail);
        auditHist.setUserAction(userAction);
        auditHist.setApprvlLvl(null);
        auditHist.setOldValue(oldValue);
        auditHist.setNewValue(newValue);
        
        auditHist.isNew(true);
        
        return auditHist;
    }
    
    public  AuditHistory createAuditHistory(String webQuoteNum, Integer lineItemNum, String userEmail,
            String userAction, int apprvlLvl,String oldValue, String newValue) throws TopazException{
    	AuditHistory_jdbc auditHist = new AuditHistory_jdbc();
        
        auditHist.setWebQuoteNum(webQuoteNum);
        auditHist.setLineItemNum(lineItemNum);
        auditHist.setUserEmail(userEmail);
        auditHist.setUserAction(userAction);
        auditHist.setApprvlLvl(new Integer(apprvlLvl));
        auditHist.setOldValue(oldValue);
        auditHist.setNewValue(newValue);
        
        auditHist.isNew(true);
        
        return auditHist;
    }

}
