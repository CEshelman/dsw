package com.ibm.dsw.quote.promotion.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.QuoteTxtFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;
/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuotePromotionProcess_Impl</code>
 * 
 * 
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: 2010-10-20
 */
public abstract class QuotePromotionProcess_Impl extends
		TopazTransactionalProcess implements QuotePromotionProcess {
	protected static final LogContext logContext = LogContextFactory
			.singleton().getLogContext();
	
	protected static String QUOTE_TXT_TYPE_CODE = "PRMTN";

	public int insertQuotePromotion(String webQuoteNum, String userId, String quoteTxt) throws QuoteException {
		int quoteTxtId = -1;
		try {
			this.beginTransaction();
			QuoteTxt quoteComment = QuoteTxtFactory.singleton()
					.createQuoteComment();

			quoteComment.setWebQuoteNum(webQuoteNum);
			quoteComment.setUserEmail(userId);
			quoteComment.setQuoteTextTypeCode(QUOTE_TXT_TYPE_CODE);
			quoteComment.setQuoteText(quoteTxt);
			//quoteComment.setJustificationSectionId("10");

			this.commitTransaction();

			quoteTxtId = quoteComment.getQuoteTextId();
		} catch (TopazException e) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			throw new QuoteException(e);
		}
		return quoteTxtId;
	}
	
	
	public int updateQuotePromotion(String webQuoteNum, String userId, String quoteTxt, int quoteTxtId) throws QuoteException {
		try {
			this.beginTransaction();
			
			QuoteTxt quoteComment = QuoteTxtFactory.singleton()
					.updateQuoteComment(webQuoteNum, quoteTxtId, QUOTE_TXT_TYPE_CODE, quoteTxt, userId, null);

			this.commitTransaction();

			quoteTxtId = quoteComment.getQuoteTextId();
		} catch (TopazException e) {
			logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			throw new QuoteException(e);
		}
		return quoteTxtId;
	}

}