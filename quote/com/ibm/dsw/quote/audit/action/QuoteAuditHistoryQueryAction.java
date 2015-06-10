package com.ibm.dsw.quote.audit.action;

import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.audit.domain.QuoteAuditHistInfo;
import com.ibm.dsw.quote.audit.process.QuoteAuditProcess;
import com.ibm.dsw.quote.audit.process.QuoteAuditProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteAuditHistoryQueryAction<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: Dec 27, 2010
 */
public class QuoteAuditHistoryQueryAction extends BaseContractActionHandler {


	
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		
		QuoteAuditHistoryQueryContract ctrct = (QuoteAuditHistoryQueryContract)contract;
		QuoteAuditProcess quoteAuditProcess = QuoteAuditProcessFactory.singleton().create();
		List auditHistories = quoteAuditProcess.getQuoteAuditHistoriesBySpecificActions(
										ctrct.getWebQuoteNum(),SubmittedQuoteConstants.USER_ACTION_QT_AUDIT_HIST);

		handler.addObject(ParamKeys.PARAM_QT_AUDIT_HIST_LIST, auditHistories);
		handler.addObject(ParamKeys.PARAM_QUOTE_NUM, ctrct.getWebQuoteNum());
        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_QT_AUDIT_HIST);
        return handler.getResultBean();
        
	}

}
