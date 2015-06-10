package com.ibm.dsw.quote.draftquote.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteValidationRule;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.contract.QuoteConvertContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteConvertAction</code> class is to do quote conversion between
 * PA and PAE
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Apr 3, 2007
 */
public class QuoteConvertAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        QuoteConvertContract c = (QuoteConvertContract) contract;

        Quote quote;
        try {
            quote = QuoteProcessFactory.singleton().create().getDraftQuoteBaseInfo(c.getUserId());
        } catch (Exception e) {
            throw new QuoteException("can't get current quote", e);
        }

        if ( !canConvert(quote) ) {
            throw new QuoteException("quote can't be converted");
        }

        if (quote.getQuoteHeader().isPAQuote()||quote.getQuoteHeader().isCSTAQuote()) {
            QuoteProcessFactory.singleton().create().convertPaToPae(c.getUserId());
        } else if (quote.getQuoteHeader().isPAEQuote()) {
            QuoteProcessFactory.singleton().create().convertPaeToPa(c.getUserId());
        }
        
        String srcAction = c.getSrcAction();
        if (StringUtils.isBlank(srcAction))
            srcAction = DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB;
            
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(srcAction));
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }
    
    private boolean canConvert(Quote quote) {
        boolean valid = false;
        try {
            QuoteValidationRule rule = QuoteValidationRule.createRule(null, quote, null);
            QuoteHeader header = quote.getQuoteHeader();

            valid = (header.isSalesQuote() && !header.isPAUNQuote()
                    && (header.isPAQuote() && rule.canConvertToPAE()))
                    || header.isCSRAQuote() || header.isCSTAQuote();
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            valid = false;
        }
        return valid;
    }

}
