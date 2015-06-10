package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.contract.EditRQContract;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CreateRQSpecialBidAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Feb 11, 2009
 */

public class CreateRQSpecialBidAction extends EditRQBaseAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.EditRQBaseAction#validate(is.domainx.User, com.ibm.dsw.quote.base.domain.QuoteUserSession, java.lang.String)
     */
    public int validate(User user, QuoteUserSession quoteUserSession, String renewalQuoteNum) throws QuoteException {
        QuoteCapabilityProcess capabilityProcess = QuoteCapabilityProcessFactory.singleton().create();
        return capabilityProcess.validateCreateRQSpeclBid(user, quoteUserSession, renewalQuoteNum);
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.EditRQBaseAction#generateRedirectURL(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    public String generateRedirectURL(ProcessContract baseContract) {
        return HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB);
    }
    
    protected void populateQuote(EditRQContract editRQContract) throws QuoteException {
        // Call process to populates current draft quote with renewal quote data
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        quoteProcess.populateRenewalQuote(editRQContract.getUserId(), editRQContract.getRenewalQuoteNum(), false, editRQContract.getOrignFromCustChgReqFlag());
        
        //Call process to convert current draft renewal quote to sales quote
        quoteProcess.convertToSalsQuote(editRQContract.getUserId(), "", true);
    }

}
