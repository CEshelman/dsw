package com.ibm.dsw.quote.customer.contract;

public class ApplianceAddressSearchContract extends CustomerSearchContract {

	private static final long serialVersionUID = -2058316439778053853L;
	
	protected String addressType;
	protected String isSubmittedQuote;
    
	public void setAddressType(String aType) { addressType = aType; }
	public String getAddressType() { return addressType; }
	
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
