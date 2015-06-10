package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;

public class LineItemAddressDetailsContract extends QuoteBaseContract {

	private static final long serialVersionUID = 7201491986108859185L;
	
	private String webQuoteNum;

	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}
	

}
