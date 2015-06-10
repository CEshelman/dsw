package com.ibm.dsw.quote.draftquote.action;

import java.text.MessageFormat;
import java.util.Locale;

import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.OmitRenewalLine;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
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
import com.ibm.ead4j.topaz.exception.TopazException;

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
 * The <code>OmittedRenewalLinesRecalculateAction</code> class is to recalculate the omitted renewal line item growth delegation
 * for a draft quote
 * 
 * @author <a href="mailto:luoyafei@cn.ibm.com">Grover </a> <br/>
 *  
 */
public class OmittedRenewalLinesRecalculateAction extends PostPartPriceTabAction {

	private static final long serialVersionUID = -2415453285434840274L;

	protected void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
		super.postDraftQuoteTab(contract, handler);
		
        PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;
        if(!ct.getQuote().getQuoteHeader().isOmittedLine() || !validateOmitLine(ct.getWebQuoteNum(), ct.getLocale(), handler)){
        	return;
        }
        QuoteHeader quoteHeader = ct.getQuote().getQuoteHeader();
        if (null != quoteHeader){
			TimeTracer tracer = TimeTracer.newInstance();
			PartPriceProcess process = PartPriceProcessFactory.singleton().create();
			quoteHeader.setOmittedLineRecalcFlag(QuoteConstants.OMIT_RECALCULATE_N);
			process.updateQuoteHeader(quoteHeader, ct.getUserId());
			tracer.dump();
		}
    }
	
    protected boolean validateOmitLine(String webQuoteNum, Locale locale, ResultHandler handler) throws QuoteException{
    	
    	if (null != webQuoteNum){
            OmitRenewalLine omitRenewalLine = null;
            try {
                omitRenewalLine = QuoteLineItemFactory.singleton().getOmittedRenewalLine(webQuoteNum);
            } catch (TopazException te) {
                throw new QuoteException(te);
            }
            if(null != omitRenewalLine && QuoteConstants.OMIT_RECALCULATE_N == omitRenewalLine.getOmittedLineRecalcFlag()){
            	String errorMessage = getI18NString(DraftQuoteMessageKeys.OL_CANNOT_BE_RECALCULATED, 
                        MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale);
    			String formattedMsg = MessageFormat.format(errorMessage, 
    			                      new String[]{PartPriceConfigFactory.singleton().getElaLimits() + ""});
    			MessageBean mBean = MessageBeanFactory.create();
    			
    			mBean.addMessage(formattedMsg, MessageBeanKeys.ERROR);
    			handler.setMessage(mBean);
    			
            	return false;
            }
		}
        return true;
    }
 }
