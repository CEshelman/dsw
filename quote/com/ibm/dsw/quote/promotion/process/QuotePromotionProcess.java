package com.ibm.dsw.quote.promotion.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuotePromotionProcess</code>
 * 
 * 
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: 2010-10-20
 */
public interface QuotePromotionProcess {
	public int insertQuotePromotion(String webQuoteNum, String userId, String quoteTxt) throws QuoteException;
	public List findPromotionsByQuote(String quoteNum) throws QuoteException;
	public void removeQuotePromotion(String webQuoteNum, String quoteTxtId, int deleteAllFlg) throws QuoteException;
	public int updateQuotePromotion(String webQuoteNum, String userId, String quoteTxt, int quoteTxtId) throws QuoteException;
}
