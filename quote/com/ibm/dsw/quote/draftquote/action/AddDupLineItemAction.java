package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteViewKeys;
import com.ibm.dsw.quote.draftquote.contract.AddLineItemContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AddDupLineItemAction</code> class is for "add another line item
 * for a part".
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 6, 2007
 */
public class AddDupLineItemAction extends PostPartPriceTabAction {

    protected void innerPostPartPriceTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        AddLineItemContract dupLineItemContract = (AddLineItemContract) contract;
        String creatorId = dupLineItemContract.getUserId();
        String partNum = dupLineItemContract.getPartNum();

        PartPriceProcess process;
        try {
            process = PartPriceProcessFactory.singleton().create();
        } catch (QuoteException e) {
            logContext.fatal(this, e.getMessage());
            throw new QuoteException(e);
        }
        logContext.debug(this, "begin posting part & pricing tab");
        process.postPartPriceInfo(dupLineItemContract);
        logContext.debug(this, "complete posting part & pricing tab");
        logContext.debug(this, "begin adding duplicate part, part num=" + partNum);
        process.addDupLineItem(partNum, creatorId);
        logContext.debug(this, "complete adding duplicate part");
    }

    protected String getValidationForm() {
        return DraftQuoteViewKeys.ADD_LINE_ITEM_FORM;
    }

}
