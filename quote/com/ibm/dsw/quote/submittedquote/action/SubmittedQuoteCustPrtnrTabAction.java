package com.ibm.dsw.quote.submittedquote.action;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>SubmittedQuoteCustPrtnrTabAction<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-4-28
 */

public class SubmittedQuoteCustPrtnrTabAction extends SubmittedQuoteBaseAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.action.SubmittedQuoteBaseAction#getSubmittedQuoteDetail(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected Quote getSubmittedQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler) throws QuoteException {
        if (quote != null) {
        	SubmittedQuoteBaseContract sqc = (SubmittedQuoteBaseContract) contract;
        	QuoteUserSession quoteUserSession = sqc.getQuoteUserSession();
            QuoteProcess process = QuoteProcessFactory.singleton().create();
        	String bpSiteNum = "";
        	if(this.isPGSEnv(quoteUserSession)){
        		bpSiteNum = quoteUserSession.getSiteNumber();
        	}
            process.getQuoteDetailForCustTab(quote, bpSiteNum);
            process.getQuoteDetailForSpecialBidTab(quote,quoteUserSession);

            QuoteHeader header = quote.getQuoteHeader();
            if (header.isRenewalQuote()){
                handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_RQ_CUST_PARTNER_TAB);
            }

            if(header.isBidIteratnQt() && QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(header.getQuoteStageCode())){
                handler.setState(DraftQuoteStateKeys.STATE_CUST_PRTNR_TAB_BID_ITERATION);
            }
        }
        return quote;

    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.action.SubmittedQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_CUST_PARTNER_TAB;
    }
    
    private boolean isPGSEnv(QuoteUserSession quoteUserSession){
    	if(null != quoteUserSession)
    		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(quoteUserSession.getAudienceCode());
    	else return false;
	}

}
