package com.ibm.dsw.quote.draftquote.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * 
 * @author guotao
 * 
 */
public interface QuoteOmittedLineItemProcess {
	public void getOmittedLineItemList(List quoteList,List renewalNumList, String webQuoteNum)
			throws QuoteException;
}
