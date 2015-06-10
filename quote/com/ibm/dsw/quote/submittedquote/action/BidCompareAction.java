package com.ibm.dsw.quote.submittedquote.action;

import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.BidCompareConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.BidCompareContract;
import com.ibm.dsw.quote.submittedquote.util.BidCompareUtil;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2011 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>BidCompareAction<code> class.
 *    
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: January 22, 2011
 */

public class BidCompareAction extends BaseContractActionHandler {

	private static final long serialVersionUID = 1L;

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		logContext.debug(this, "Begin to execute BidCompareAction.executeBiz");
		BidCompareContract bidContract = (BidCompareContract)contract;
        QuoteUserSession quoteUserSession = bidContract.getQuoteUserSession();
        String up2ReportingUserIds = quoteUserSession == null ? "" : quoteUserSession.getUp2ReportingUserIds();
		try {
			String copiedQuoteNum = bidContract.getCopiedQuoteNum();
//			String originalQuoteNum = bidContract.getOriginalQuoteNum();
			Locale locale = bidContract.getLocale();
			
			Quote copiedQuote = BidCompareUtil.initQuoteInfo(copiedQuoteNum,bidContract.getUser(), bidContract.getUserId(), up2ReportingUserIds);
			String originalQuoteNum = copiedQuote.getQuoteHeader().getPriorQuoteNum();
			Quote originalQuote = BidCompareUtil.initQuoteInfo(originalQuoteNum,bidContract.getUser(), bidContract.getUserId(), up2ReportingUserIds);
			
			List quoteHeader = BidCompareUtil.compareQuoteHead(copiedQuote,originalQuote,locale);
			List webQuoteSpeclBid = BidCompareUtil.compareSpecialBid(copiedQuote,originalQuote,locale);
			List lineItems =BidCompareUtil.compareLineItems(copiedQuote,originalQuote,locale);
			List quoteSpecBidQuestion = BidCompareUtil.compareSpecialBidQuestion(copiedQuote, originalQuote, locale);
			List promotions = BidCompareUtil.comparePromotionList(copiedQuote, originalQuote, locale);
			
			handler.addObject(BidCompareConstants.COPIED_QUOTE_NUM, copiedQuoteNum);
			handler.addObject(BidCompareConstants.ORIGINAL_QUOTE_NUM, originalQuoteNum);
			handler.addObject(BidCompareConstants.COMPARE_QUOTE_HEADER, quoteHeader);
			handler.addObject(BidCompareConstants.COMPARE_LINE_ITEMS, lineItems);
			handler.addObject(BidCompareConstants.COMPARE_QUOTE_SPECL_BID, webQuoteSpeclBid);
			handler.addObject(BidCompareConstants.QUOTE_SPEC_BID_QUESTION, quoteSpecBidQuestion);
			handler.addObject(BidCompareConstants.COMPARE_QUOTE_PROMOTION, promotions);
			
			handler.setState(SubmittedQuoteStateKeys.STATE_DISPLAY_BID_COMPARE);
			
		} catch (QuoteException e) {
			throw e;
		} catch (Exception e) {
			logContext.error(this, e);
			throw new QuoteException(e);
		}
		return handler.getResultBean();
	}

}
