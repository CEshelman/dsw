package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class BidCompareContract extends QuoteBaseContract {
	private String copiedQuoteNum;
	private String originalQuoteNum;
	
	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }
	
	public String getCopiedQuoteNum() {
		return copiedQuoteNum;
	}
	public void setCopiedQuoteNum(String copiedQuoteNum) {
		this.copiedQuoteNum = copiedQuoteNum;
	}
	public String getOriginalQuoteNum() {
		return originalQuoteNum;
	}
	public void setOriginalQuoteNum(String originalQuoteNum) {
		this.originalQuoteNum = originalQuoteNum;
	}
}
