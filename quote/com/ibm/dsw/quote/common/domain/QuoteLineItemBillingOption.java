package com.ibm.dsw.quote.common.domain;

import com.ibm.dsw.quote.appcache.domain.BillingOption;

public class QuoteLineItemBillingOption {
	private boolean isDefault;
	private BillingOption billingOption;
	
	public boolean isDefault() {
		return isDefault;
	}
	
	public String getCode(){
		return billingOption.getCode();
	}
	
	public String getCodeDesc(){
		return billingOption.getCodeDesc();
	}


	public QuoteLineItemBillingOption(boolean isDefault, BillingOption billingOption){
		this.isDefault = isDefault;
		this.billingOption = billingOption;
	}
	
	public BillingOption getBillingOption(){
		return billingOption;
	}
}
