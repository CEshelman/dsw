package com.ibm.dsw.quote.draftquote.process;


import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2014 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>RemoveMonthlySwConfigurationProcess_Impl</code> class is abstract implementation of
 * RemoveMonthlySwConfigurationProcess.
 *
 *
 * @author <a href="jiamengz@cn.ibm.com">Linda Jia </a> <br/>
 *
 * Creation date: 2014-1-03
 */

public abstract class RemoveMonthlySwConfigurationProcess_Impl extends TopazTransactionalProcess implements RemoveMonthlySwConfigurationProcess {
	
	protected LogContext logContext = LogContextFactory.singleton().getLogContext();
	@Override
	public void removeMonthlySwConfigurations(String webQuoteNum,
			String configurationId) throws QuoteException{
		this.beginTransaction();

        try {

        	removeMonthlySwConfiguration(webQuoteNum, configurationId);

            commitTransaction();

        } catch (TopazException e) {
            throw new QuoteException(e);

        } finally {
            this.rollbackTransaction();
        }

		
	}

	
}
