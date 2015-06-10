/*
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * @author Xiao Guo Yi
 * 
 * Created on 2009-2-4
 */
package com.ibm.dsw.quote.submittedquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;


public class ExecSummaryPartPriceAction extends SubmittedQuoteBaseAction{
	
	protected Quote getSubmittedQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler)
	                                  throws QuoteException{
		 SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
		 SubmittedQuoteBaseContract ct = (SubmittedQuoteBaseContract) contract;
		 
		 process.getExecPartPriceInfo(quote, ct.getUser());
		 
		 return quote;
	 }
	 
	 protected String getState(ProcessContract contract){
	 	return SubmittedQuoteStateKeys.STATE_DISPLAY_EXEC_SUMMARY_PP;
	 }
}
