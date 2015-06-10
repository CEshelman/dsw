package com.ibm.dsw.quote.customer.contract;

public class CreateApplianceAddressContract extends CustomerCreateContract {

	private static final long serialVersionUID = -8241608331277746793L;

	private String companyName1;
	private String companyName2;
	private String addressType;
	private String isSubmittedQuote;
	
	public String getCompanyName1() {
		return companyName1;
	}

	public void setCompanyName1(String companyName1) {
		this.companyName1 = companyName1;
	}

	public String getCompanyName2() {
		return companyName2;
	}

	public void setCompanyName2(String companyName2) {
		this.companyName2 = companyName2;
	}
	
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
