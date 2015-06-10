package com.ibm.dsw.quote.customer.contract;

public class DisplayCreateApplianceAddressContract extends CustomerBaseContract {
	
	private static final long serialVersionUID = -3077428116342726931L;
	
	private String addressType;
	private String isSubmittedQuote;

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	/**
	 * @return the isSubmittedQuote
	 */
	public String getIsSubmittedQuote() {
		return isSubmittedQuote;
	}

	/**
	 * @param isSubmittedQuote the isSubmittedQuote to set
	 */
	public void setIsSubmittedQuote(String isSubmittedQuote) {
		this.isSubmittedQuote = isSubmittedQuote;
	}
	
	
}
