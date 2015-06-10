package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.action.validator.PartPriceUIValidator;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 *
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * 
 * @author xiuliw@cn.ibm.com
 *
 * 2007-8-10
 */
public class ClearOfferAction extends PostPartPriceTabAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.PostDraftQuoteBaseAction#postDraftQuoteTab(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void innerPostPartPriceTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        PostPartPriceTabContract ct = (PostPartPriceTabContract)contract;

        if (ct.getItems().values().size() == 0) {
            logContext.debug(this, "no parts in the quote, no need to perform the post");
            return;

        }

        if (ct.isRenwalQuote() && !ct.isRqEditable()) {
            logContext.debug(this, "Renewal Qutoe, but not editable");
            return;
        }

        PartPriceProcess process;
        process = PartPriceProcessFactory.singleton().create();

        process.clearOffer(ct);
    }
    
    
    protected boolean innerValidate(ProcessContract contract){
        PartPriceUIValidator validator = PartPriceUIValidator.create(this, (PostPartPriceTabContract) contract);

        try {
            if (!validator.validate(contract,false)) {
                return false;
            }

        } catch (QuoteException e) {
            logContext.fatal(this, "validate part price data error:" + e.getMessage());
            return false;
        }

        return true;
    }    
}