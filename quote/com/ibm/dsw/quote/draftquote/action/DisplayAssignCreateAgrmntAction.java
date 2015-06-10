package com.ibm.dsw.quote.draftquote.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.AgreementTypeConfigFactory;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
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
 * This <code>DisplayAssignCreateAgrmntAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Oct 28, 2009
 */

public class DisplayAssignCreateAgrmntAction extends BaseContractActionHandler {
    
    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        QuoteBaseContract baseContract = (QuoteBaseContract) contract;
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        Quote quote = null;
        boolean hasSessionQuote = true;
        
        try {
            quote = process.getDraftQuoteBaseInfo(baseContract.getUserId());
        } catch (NoDataException e) {
            logContext.debug(this, e.getMessage());
            hasSessionQuote = false;
        }
        
        if (!hasSessionQuote) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }
        
        if (!isQuoteValid(quote)) {
            throw new QuoteException(
                "Assigning or creating a new Passport Advantage agreement is not a valid option for this quote type or customer.");
        }
        // 1 mean get PA/CSA agreement type
        List agrmntTypeList = AgreementTypeConfigFactory.singleton().getAgrmntTypeList(1);
        
        handler.addObject(ParamKeys.PARAM_QUOTE_OBJECT, quote);
        handler.addObject(CustomerParamKeys.PARAM_AGRMNT_TYPES, agrmntTypeList);
        
        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_ASSGN_CRT_AGRMNT);
        
        return handler.getResultBean();
    }
    
    protected boolean isQuoteValid(Quote quote) {
        
        QuoteHeader header = quote.getQuoteHeader();
        Customer cust = quote.getCustomer();
        boolean hasExistPACtrct = (cust == null) ? false : (cust.isActivePACust() || cust.isInactivePACust());

        return ((header.isPAQuote() || header.isPAEQuote() || header.isPAUNQuote() || header.isSSPQuote())
                && StringUtils.isNotBlank(header.getSoldToCustNum())&& !hasExistPACtrct)
                || header.isCSRAQuote() || header.isCSTAQuote();
    }

}
