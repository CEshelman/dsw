package com.ibm.dsw.quote.customer.contract;

public class SelectApplianceAddressContract extends CustomerBaseContract {

	private static final long serialVersionUID = 169499378152128563L;
	
	String addressType;
	private String isSubmittedQuote;

	public String getAddressType() { return addressType; }
	public void setAddressType(String aType) { addressType = aType; }
	
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
