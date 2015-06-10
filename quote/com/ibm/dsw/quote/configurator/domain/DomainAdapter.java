package com.ibm.dsw.quote.configurator.domain;

import java.io.Serializable;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

public abstract class DomainAdapter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8453128389957683010L;

	public abstract int getRefDocLineItemNum();
	public abstract String getPartNum();
	
	public static DomainAdapter create(QuoteLineItem qli){
		return new QuoteLineItemDomainAdapter(qli);
	}
	
	public static DomainAdapter create(ConfiguratorPart part){
		return new ConfiguratorDomainAdapter(part);
	}
}
