package com.ibm.dsw.quote.submittedquote.viewbean;

import java.util.List;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.config.BidCompareConstants;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
/**
 * Copyright 2011 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>BidCompareViewBean<code> class.
 *    
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: January 22, 2011
 */
public class BidCompareViewBean extends BaseViewBean {

	private transient List quoteHeader;

	private transient List lineItems;
	
	private transient List webQuoteSpeclBid;
	
	private String copiedQuoteNum;
	
	private String originalQuoteNum;
	
	private transient List quoteSpecBidQuestion;
	
	private transient List promotions;
	
	public void collectResults(Parameters params) throws ViewBeanException {
		copiedQuoteNum = (String)params.getParameter(BidCompareConstants.COPIED_QUOTE_NUM);
		originalQuoteNum = (String)params.getParameter(BidCompareConstants.ORIGINAL_QUOTE_NUM);
		quoteHeader = (List)params.getParameter(BidCompareConstants.COMPARE_QUOTE_HEADER);
		lineItems = (List)params.getParameter(BidCompareConstants.COMPARE_LINE_ITEMS);
		webQuoteSpeclBid = (List)params.getParameter(BidCompareConstants.COMPARE_QUOTE_SPECL_BID);
		quoteSpecBidQuestion = (List)params.getParameter(BidCompareConstants.QUOTE_SPEC_BID_QUESTION);
		promotions = (List)params.getParameter(BidCompareConstants.COMPARE_QUOTE_PROMOTION);
		super.collectResults(params);
	}

	public List getQuoteHeader() {
		return quoteHeader;
	}

	public void setQuoteHeader(List quoteHeader) {
		this.quoteHeader = quoteHeader;
	}

	public List getLineItems() {
		return lineItems;
	}

	public List getWebQuoteSpeclBid() {
		return webQuoteSpeclBid;
	}

	
	public String getCopiedQuoteNum() {
		return copiedQuoteNum;
	}

	public String getOriginalQuoteNum() {
		return originalQuoteNum;
	}

	public List getQuoteSpecBidQuestion() {
		return quoteSpecBidQuestion;
	}

	public List getPromotions() {
		return promotions;
	}
}
