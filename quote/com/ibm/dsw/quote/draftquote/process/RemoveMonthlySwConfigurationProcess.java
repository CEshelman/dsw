package com.ibm.dsw.quote.draftquote.process;


import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2014 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>RemoveMonthlySwConfigurationProcess</code> class is business interface for remove
 * monthly software configuration for draft quote.
 *
 *
 * @author <a href="jiamengz@cn.ibm.com">Linda Jia </a> <br/>
 *
 * Creation date: 2014-1-03
 */
public interface RemoveMonthlySwConfigurationProcess {
	
	public void removeMonthlySwConfiguration(String webQuoteNum, String configurationId) throws QuoteException;

	public void removeMonthlySwConfigurations(String webQuoteNum,
			String configurationId)throws QuoteException;

}
