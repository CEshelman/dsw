package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.draftquote.contract.EditRQContract;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>OrderRQAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 9, 2007
 */

public class OrderRQAction extends EditRQBaseAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.EditRQBaseAction#validate(is.domainx.User, java.lang.String)
     */
    public int validate(User user, QuoteUserSession quoteUserSession, String renewalQuoteNum) throws QuoteException {
        QuoteCapabilityProcess capabilityProcess = QuoteCapabilityProcessFactory.singleton().create();
        return capabilityProcess.validateOrderRQ(user, quoteUserSession, renewalQuoteNum);
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.EditRQBaseAction#generateRedirectURL(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    public String generateRedirectURL(ProcessContract baseContract) {
        EditRQContract editRQContract = (EditRQContract) baseContract;
        String salesEorderURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.RPT_RENEWAL_QUOTE_ORDER_URL);
        salesEorderURL = salesEorderURL + editRQContract.getRenewalQuoteNum();
        return salesEorderURL;
    }
    
    protected void populateQuote(EditRQContract editRQContract) throws QuoteException{
        //Override the super method.
    }
}
