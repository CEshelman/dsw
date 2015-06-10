package com.ibm.dsw.quote.audit.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteAuditProcess<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: Dec 27, 2010
 */
public interface QuoteAuditProcess {
	public List getQuoteAuditHistoriesBySpecificActions(String webQuoteNum,String Actionlist)throws QuoteException;
}
