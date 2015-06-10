package com.ibm.dsw.quote.customer.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.draftquote.contract.CreateNewQuoteContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PrepareConfiguratorRedirectDataBaseAction<code> class.
 * This action is responsible for fetching configurator selection information
 *    
 * @author: bear@cn.ibm.com
 * 
 * Creation date: 2014-1-21
 */
public class PrepareCreateNewQuoteAjaxAction extends BaseContractActionHandler{
	private transient LogContext logContext = LogContextFactory.singleton().getLogContext();
	

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		
		CreateNewQuoteContract ct = (CreateNewQuoteContract)contract;
		Quote quote = this.fetchCurrentQuote(ct.getUserId());
		
		if(quote == null || isCreateQuote(ct,quote)){
			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
			quoteProcess.createQuoteFromOrderForConfigurator(ct.getChrgAgrmtNum(), ct.getUserId(), ct.getQuoteUserSession().getAudienceCode());
			quote = this.fetchCurrentQuote(ct.getUserId());
		}
        
        handler.addObject(CustomerParamKeys.PARAM_RESULT_CREATE_QUOTE_AJAX, "true");
        handler.setState(CustomerStateKeys.STATE_CREATE_NEW_QUOTE_RESULT);
		return handler.getResultBean();
	}
	
	public boolean isCreateQuote(CreateNewQuoteContract ct,Quote quote)throws QuoteException{
		boolean isCreateQuote = false;
		String createType = ct.getCreateType();
		String quoteCustNum = null;
		if(quote.getCustomer()!= null){
			quoteCustNum = quote.getCustomer().getCustNum();
		}
		if(createType.equals("1")){ // update quantity for monthly licensing
			if(StringUtils.isBlank(quoteCustNum)  // if there is no customer on the draft quote, create a new session quote
					|| !StringUtils.trim(ct.getCustomerNum()).equals(StringUtils.trim(quoteCustNum) )// if customer don't match, replace session quote
					|| ( !StringUtils.isBlank(ct.getIsCreateNewQuote()) && StringUtils.trim(ct.getIsCreateNewQuote()).equals("1")) // if has same CA ,configID
				   ){
				isCreateQuote = true;
			}
		}
		return isCreateQuote;
	}
	
	public Quote fetchCurrentQuote(String creatorID)throws QuoteException{
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();

        Quote quote = null;
        
        try {
            quote = quoteProcess.getDraftQuoteBaseInfo(creatorID);
        } catch (NoDataException e) {
            logContext.error(this, "NoDataExceptoin accor when geting quote base info.");
            logContext.error(this, e.getMessage());
            quote = null;
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            throw e;
        }

		return quote;
	}
    
}
