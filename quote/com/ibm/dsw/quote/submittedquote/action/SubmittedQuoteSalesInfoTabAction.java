package com.ibm.dsw.quote.submittedquote.action;

import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
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
 * This <code>SubmittedQuoteSalesInfoTabAction<code> class.
 *
 * @author: zhaoxw@cn.ibm.com
 *
 * Creation date: Apr 28, 2007
 */

public class SubmittedQuoteSalesInfoTabAction extends SubmittedQuoteBaseAction {

    protected Quote getSubmittedQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler) throws QuoteException {
        SubmittedQuoteBaseContract submittedQuoteContract = (SubmittedQuoteBaseContract) contract;
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        process.getQuoteDetailForSalesInfoTab(quote);
        process.getQuoteDetailForSpecialBidTab(quote,submittedQuoteContract.getQuoteUserSession());

        QuoteHeader header = quote.getQuoteHeader();
        if (header.isRenewalQuote()) {
            QuoteTxt qtCmmnt = process.getRenewalQuoteDetailComment(header.getWebQuoteNum());
            handler.addObject(SubmittedQuoteParamKeys.PARAM_RQ_DETAIL_SALES_COMMENTS, qtCmmnt);

            QuoteAttachmentProcess attchProcess = QuoteAttachmentProcessFactory.singleton().create();
            List attachments = attchProcess.getRQSalesCommentAttachments(header.getWebQuoteNum());
            handler.addObject(SubmittedQuoteParamKeys.PARAM_ATTACHMENT_LIST, attachments);
        }

        if(header.isBidIteratnQt() && QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(header.getQuoteStageCode())){
            handler.setState(DraftQuoteStateKeys.STATE_SALES_INFO_TAB_BID_ITERATION);
        }

        return quote;
    }

    protected String getState(ProcessContract contract) {
        return SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_SALES_INFO_TAB;
    }

}
