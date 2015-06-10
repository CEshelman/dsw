package com.ibm.dsw.quote.submittedquote.action;

import java.util.List;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.ViewQuoteTxtHistoryContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ViewQuoteTxtHistoryAction</code> class is to view quote txt edit history
 * 
 * @author qinfengc@cn.ibm.com
 * 
 * Created on 2010-12-21
 */
public class ViewQuoteTxtHistoryAction extends BaseContractActionHandler
{
	private static final long serialVersionUID = -3281054200202522000L;
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
	
	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException
	{
		ViewQuoteTxtHistoryContract viewTxtContract = (ViewQuoteTxtHistoryContract)contract;
		SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
		List list = process.getQuoteTxtHistory(viewTxtContract.getQuoteNum(), viewTxtContract.getTxtTypeCode(), 1);
		handler.addObject(SpecialBidParamKeys.PARAM_QUOTE_TXT_HISTORY, list);
		handler.addObject(SpecialBidParamKeys.PARAM_QUOTE_TXT_TYPE, viewTxtContract.getTxtTypeCode());
		handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_VIEW_TXT_HISTORY);
		return handler.getResultBean();
	}

}
