package com.ibm.dsw.quote.submittedquote.action;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.BaseI18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerFactory;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.ValidationMessageFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>UpdateQuotePrtnrBaseAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Nov 17, 2009
 */

public class UpdateQuotePrtnrBaseAction extends BaseContractActionHandler {
    
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        // TODO Auto-generated method stub
        return null;
    }
    
    protected Quote getQuoteInfo(String webQuoteNum) throws QuoteException {
        
        QuoteHeader quoteHeader = null;
        Quote quote = null;
        
        try {
            TransactionContextManager.singleton().begin();
            
            quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(webQuoteNum);
            quote = new Quote(quoteHeader);
            
            int newCustFlag = quoteHeader.getWebCustId() > 0 ? 1 : 0;
            if (StringUtils.isNotBlank(quoteHeader.getSoldToCustNum()) || quoteHeader.getWebCustId() > 0) {
                logContext.debug(this, "To retrieve QuoteContacts by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                Customer customer = CustomerFactory.singleton().findByQuoteNum(quoteHeader.getLob().getCode(),
                        newCustFlag, quoteHeader.getSoldToCustNum(), quoteHeader.getContractNum(), webQuoteNum,
                        quoteHeader.getWebCustId());
                quote.setCustomer(customer);
            }
            
            if (StringUtils.isNotBlank(quoteHeader.getRselCustNum())) {
                logContext.debug(this, "To retrieve Reseller by number: " + quoteHeader.getRselCustNum());
                Partner reseller = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getRselCustNum(), quoteHeader.getLob().getCode());
                quote.setReseller(reseller);
            }
            
            if (StringUtils.isNotBlank(quoteHeader.getPayerCustNum())) {
                logContext.debug(this, "To retrieve Payer by number: " + quoteHeader.getPayerCustNum());
                Partner payer = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getPayerCustNum(), quoteHeader.getLob().getCode());
                quote.setPayer(payer);
            }
            
            logContext.debug(this, "To retrieve QuoteLineItemList by quote number: " + webQuoteNum);
            List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(webQuoteNum);
            quote.setLineItemList(lineItemList);
            
            TransactionContextManager.singleton().commit();
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            try {
    			TransactionContextManager.singleton().rollback();
    		} catch (TopazException pe) {
    		    logContext.error(this, LogThrowableUtil.getStackTraceContent(pe));
    		}
        }
        
        return quote;
    }
    
    protected ResultBean handleValidateResult(Map validResult, ResultHandler handler, Locale locale) {
        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        
        String msgHead = this.getI18NString(DraftQuoteMessageKeys.SUBMIT_FAILED_MSG, BaseI18NBundleNames.QUOTE_BASE, locale);
        messageBean.addMessage(msgHead, MessageKeys.LAYER_MSG_HEAD);

        Set keySet = validResult.keySet();
        Iterator iter = keySet.iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = validResult.get(key);
            String param = null;
            
            // if the value is a string, it will be considered as the message parameter.
            if (value != null && value instanceof String) {
                param = (String) value;
            }
            
            String message = ValidationMessageFactory.singleton().getVldtnMsgForSbmtCpApprvdBid(key, locale, param);
            messageBean.addMessage(message, MessageKeys.LAYER_MSG_ITEM);
        }

        return resultBean;
    }
    
    protected String getValidationForm() {
        return SubmittedQuoteViewKeys.UPDATE_QUOTE_PARTNER_FORM;
    }

}
