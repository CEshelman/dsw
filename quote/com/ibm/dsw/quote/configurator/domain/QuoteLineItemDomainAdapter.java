package com.ibm.dsw.quote.configurator.domain;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

class QuoteLineItemDomainAdapter extends DomainAdapter {
	private transient QuoteLineItem qli;
	
	public QuoteLineItemDomainAdapter(QuoteLineItem qli){
		this.qli = qli;
	}
	public String getPartNum() {
		return qli.getPartNum();
	}

	public int getRefDocLineItemNum() {
		return qli.getRefDocLineNum();
	}

}
