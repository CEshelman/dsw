package com.ibm.dsw.quote.draftquote.action;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQViewKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DraftRQStatusTabAction</code> class is to show renewal quote
 * status
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Apr 16, 2007
 */
public class DraftRQStatusTabAction extends DraftQuoteBaseAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.DraftQuoteBaseAction#getDraftQuoteDetail(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void getDraftQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler) throws QuoteException {
        QuoteAccess rqAccess = quote.getQuoteAccess();
        
        if (!quote.getQuoteHeader().isRenewalQuote()) {
            throw new QuoteException("Current quote is not renewal quote [" + quote.getQuoteHeader().getWebQuoteNum() + "]");
        }
        
        boolean editable = rqAccess.isCanEditRQ();
        boolean updatable = rqAccess.isCanUpdateRQStatus();
        QuoteProcess qp = QuoteProcessFactory.singleton().create();
        List status;
        List reasons;
        if (editable || updatable){
            int accessFlag = editable ? 1 : 2;
            status = qp.getAvailablePrimaryStatus(quote.getQuoteHeader().getWebQuoteNum(), accessFlag);
            reasons = qp.getTerminationReasons();
        }else{
            //pass empty data to page when the quote is not editable
            status = new ArrayList();
            reasons = new ArrayList();
        }
        
        handler.addObject(DraftRQViewKeys.RQ_EDITABLE, Boolean.valueOf(editable));
        handler.addObject(DraftRQViewKeys.RQ_UPDATABLE, Boolean.valueOf(updatable));
        handler.addObject(DraftRQViewKeys.RQ_P_STATUS, status);
        handler.addObject(DraftRQViewKeys.RQ_TERM_REASON, reasons);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.DraftQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return DraftQuoteStateKeys.STATE_DISPLAY_RQ_STATUS_TAB;
    }

}
