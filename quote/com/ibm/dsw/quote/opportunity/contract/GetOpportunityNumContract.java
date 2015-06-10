package com.ibm.dsw.quote.opportunity.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;

public class GetOpportunityNumContract  extends QuoteBaseContract {
	
	private static final long serialVersionUID = 1L;
	
	private String webQuoteNum;
	
	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}

}
