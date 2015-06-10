/*
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * @author Xiao Guo Yi
 * 
 * Created on 2009-2-11
 */
package com.ibm.dsw.quote.submittedquote.action;

import java.util.HashMap;
import java.util.Locale;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.dsw.quote.submittedquote.contract.SaveQuoteExecSummaryContract;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;


public class SaveQuoteExecSummaryAction extends BaseContractActionHandler {
	
	protected boolean validate(ProcessContract contract) {
		if(!super.validate(contract)){
			return false;
		}
		
		SaveQuoteExecSummaryContract saveCt = (SaveQuoteExecSummaryContract)contract;
		
		if(saveCt.getRecmdtFlag() == null){
			addInvalidationMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
					           SubmittedQuoteMessageKeys.EXEC_SUMMARY_RECMD_FLAG_REQUIRED,
					           SubmittedQuoteViewKeys.SELECT_RECMD_FLAG,
					           SubmittedQuoteParamKeys.EXEC_RECMD_FLAG, saveCt);
    		
            return false;
		}
		
		if(!saveCt.isBookableRevenueValid()){
			addInvalidationMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, 
					           SubmittedQuoteMessageKeys.VALUE_MUST_BE_NUMERIC, 
							   SubmittedQuoteViewKeys.PERIOD_BOOKABLE_REVENUE, 
							   SubmittedQuoteParamKeys.EXEC_PERIOD_BOOKABLE_REVENUE, saveCt);

            return false;
		}
		
		if(!saveCt.isServicesValid()){
			addInvalidationMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, 
			           SubmittedQuoteMessageKeys.VALUE_MUST_BE_NUMERIC,
					   SubmittedQuoteViewKeys.EXEC_SERVICE_REVENUE, 
					   SubmittedQuoteParamKeys.EXEC_SERVICE_REVENUE, saveCt);
			return false;
		}
		
		return true;
	}

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		SaveQuoteExecSummaryContract saveCt = (SaveQuoteExecSummaryContract)contract;
		
		QuoteProcess quoteProc = QuoteProcessFactory.singleton().create();
		quoteProc.updateQuoteExecSummary(saveCt.getQuoteNum(), saveCt.getUserId(), saveCt.getRecmdtFlag(),
					                         saveCt.getApprRecmd(), saveCt.getPeriodBookableRevenue(), saveCt.getServiceRevenue(),
					                         saveCt.getExecSupport(), saveCt.getBriefOverview());
		
		handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		ResultBean rb = handler.getResultBean();
		setResultMsg(true, rb, saveCt.getLocale());
		
		return rb;
	}
	
	private void setResultMsg(boolean succeed, ResultBean rBean, Locale locale) throws ResultBeanException{
		String msg = getI18NString(MessageKeys.MSG_EXEC_SUMMARY_TAB_SAVE_SUCCEED, I18NBundleNames.BASE_MESSAGES, locale);
		MessageBean mBean = MessageBeanFactory.create();
		
		rBean.getMessageBean().addMessage(msg, MessageBeanKeys.SUCCESS);
	}
	
	private void addInvalidationMsg(String boundle, String msg, String arg, String key, ProcessContract contract){
    	HashMap map = new HashMap();
    	FieldResult fieldResult = new FieldResult();
    	
        fieldResult.setMsg(boundle, msg);
        fieldResult.addArg(boundle, arg);
        
        map.put(key, fieldResult);
        addToValidationDataMap(contract, map);
	}
}
