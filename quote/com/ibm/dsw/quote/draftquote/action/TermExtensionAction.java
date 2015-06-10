package com.ibm.dsw.quote.draftquote.action;


import is.domainx.User;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.contract.TermExtensionContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>TermExtensionAction<code> class.
 *    
 * @author: yuepingl@cn.ibm.com
 * 
 * Creation date: May 16, 2013
 */

public class TermExtensionAction extends BaseContractActionHandler {

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
		TermExtensionContract termExtensionContract = (TermExtensionContract) contract;
        String redirectURL = null;
        
        
        if (validate(termExtensionContract.getUser(), termExtensionContract.getQuoteUserSession())) {
        	populateQuote(termExtensionContract);
        	redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB);
        } else {
            logContext.debug(this, "Validation failed");
            redirectURL = genGobackURL(termExtensionContract);
        }

        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        logContext.debug(this, "This action will be redirected to " + redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
	}
	
	// TODO put validation here if needed
	public boolean validate(User user, QuoteUserSession quoteUserSession)throws QuoteException{
		return true;
	} 

    public String generateRedirectURL(ProcessContract baseContract){
		return key;
	}

    private String genGobackURL(TermExtensionContract termExtensionContract) {
        StringBuffer sb = new StringBuffer();
        // TODO return to saas report if quote create failed.
        return sb.toString();
    }
    
    // TODO Remove this function if a new draft quote will be always created once the term extension link is clicked. Still need discussion with BA.
    protected boolean ifCreateNewQuote(Quote quote, TermExtensionContract te){
    	String quoteCustNum = quote.getCustomer().getCustNum();    	
    	
		return (StringUtils.isBlank(quoteCustNum)  // if there is no customer on the draft quote, create a new session quote
				|| !StringUtils.trim(te.getCustomerNum()).equals(StringUtils.trim(quoteCustNum) )// if customer don't match, replace session quote
				|| ( !StringUtils.isBlank(te.getIsCreateNewQuote()) && StringUtils.trim(te.getIsCreateNewQuote()).equals("1")) // if has same CA ,configID, prodID
			   );
	}

	public void populateQuote(TermExtensionContract te)throws QuoteException{
		
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();

        Quote quote = null;
        
        try {
            quote = quoteProcess.getDraftQuoteBaseInfo(te.getUserId());
        } catch (NoDataException e) {
            logContext.error(this, "NoDataExceptoin accor when geting quote base info.");
            logContext.error(this, e.getMessage());
            quote = null;
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            throw e;
        }
        
		if(quote == null || ifCreateNewQuote(quote, te)){
			// create new quote if there is no session quote for this user or we need to create new quote if customers are different.
			quoteProcess.createQuoteFromOrderForConfigurator(te.getChrgAgrmtNum(), te.getUserId(), te.getQuoteUserSession().getAudienceCode());
		}else{
			quoteProcess.copyQuoteInfoFromOrderForConfigurator(te.getChrgAgrmtNum(), quote.getQuoteHeader().getWebQuoteNum(), te.getUserId(),  te.getQuoteUserSession().getAudienceCode());
		}
		
		
		QuoteHeader qtHeader = null;
        try {
            qtHeader = quoteProcess.getQuoteHdrInfo(te.getUserId());
        } catch (NoDataException nde) {
            throw new QuoteException("Quote header is not found for the login user " + te.getUserId());
        }
		PartPriceProcess process = PartPriceProcessFactory.singleton().create();
		process.addEntirParts(qtHeader.getWebQuoteNum(), te.getChrgAgrmtNum(), te.getConfigId(), "1");
		
	}
}
