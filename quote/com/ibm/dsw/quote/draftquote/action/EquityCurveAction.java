package com.ibm.dsw.quote.draftquote.action;

import java.text.MessageFormat;
import java.util.Locale;

import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.PriceEngineUnAvailableException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * <p>
 * Copyright 2006 by IBM Corporation All rights reserved.
 * </p>
 * 
 * <p>
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * </p>
 * 
 * The <code>EquityCurveAction</code> class is to recalcute equity curve
 * for a draft quote
 * 
 * @author <a href="mailto:luoyafei@cn.ibm.com">Grover </a> <br/>
 *  
 */
public class EquityCurveAction extends PostPartPriceTabAction {

	private static final long serialVersionUID = -2082276296684072769L;

	protected void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
		super.postDraftQuoteTab(contract, handler);
        
        PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;
        Quote quote = ct.getQuote();
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        if(!validateEquityCurve(quote, ct.getLocale(), handler)){
        	return;
        }
		if (null != quoteHeader && null != quoteHeader.getWebQuoteNum() && quoteHeader.isECRecalculateFlag()){
			TimeTracer tracer = TimeTracer.newInstance();
			PartPriceProcess process = PartPriceProcessFactory.singleton().create();
			try {
				process.getPartPriceInfoNoTransation(quote, ct.getQuoteUserSession());
			} catch (PriceEngineUnAvailableException pe) {
				throw new QuoteException(pe);
			}
			process.updateEquityCurvePart(quoteHeader.getWebQuoteNum());
			tracer.dump();
		}
    }
	
    protected boolean validateEquityCurve(Quote quote, Locale locale, ResultHandler handler){
    	
    	//if quote is EC eligible quote 
    	//AND no customer added
    	//return error msg "Equity curve discount guidance cannot be calculated until a customer or parts have been added to the quote."
    	if(quote.getQuoteHeader().isECEligible() && quote.getCustomer() == null){
    		String errorMessage = getI18NString(DraftQuoteMessageKeys.EC_CANNOT_BE_CALCULATED, 
                    MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, locale);
			String formattedMsg = MessageFormat.format(errorMessage, 
			                      new String[]{PartPriceConfigFactory.singleton().getElaLimits() + ""});
			MessageBean mBean = MessageBeanFactory.create();
			
			mBean.addMessage(formattedMsg, MessageBeanKeys.ERROR);
			handler.setMessage(mBean);
			
			return false;
    	}
        return true;
    }

 }
