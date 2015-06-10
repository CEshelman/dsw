/*
 * Created on Feb 5, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author Nan CDL Wang (wnan@cn.ibm.com)
 *
 * Created At: 2009-2-5
 */

public class SubmittedQuoteExecSummaryTabAction extends SubmittedQuoteBaseAction {
	
	protected Quote getSubmittedQuoteDetail(Quote quote,
			ProcessContract contract, ResultHandler handler)
			throws QuoteException {
		if (quote != null) {
            QuoteProcess process = QuoteProcessFactory.singleton().create();
            process.getQuoteDetailForExecSummaryTab(quote);
        }
		
		handler.setState(getState(contract));
        return quote;
	}

	protected String getState(ProcessContract contract) {
		return SubmittedQuoteStateKeys.STATE_SUBMITTED_EXEC_SUMMARY_TAB;
	}

}
