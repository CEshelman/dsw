package com.ibm.dsw.quote.common.domain;

import java.util.Iterator;
import java.util.List;

public class QuoteBusinessDomain {
	

	
	/**
	 * @param lineItems
	 * @return
	 * if quote has appliance part, return true
	 */
	public boolean isQuoteHasAppliancePart(List<QuoteLineItem> lineItems){
		if(lineItems == null || lineItems.size() == 0){
			return false;
		}
		for (Iterator iterator = lineItems.iterator(); iterator.hasNext();) {
			QuoteLineItem quoteLineItem = (QuoteLineItem) iterator.next();
			if(quoteLineItem.isApplncPart()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param lineItems
	 * @return
	 * if quote has equity curve part, return true
	 */
	public boolean isQuoteHasEcPart(List<QuoteLineItem> lineItems){
		if(lineItems == null || lineItems.size() == 0){
			return false;
		}
		for (Iterator iterator = lineItems.iterator(); iterator.hasNext();) {
			QuoteLineItem quoteLineItem = (QuoteLineItem) iterator.next();
			if(quoteLineItem.getEquityCurve() != null){
				return true;
			}
		}
		return false;
	}
}
