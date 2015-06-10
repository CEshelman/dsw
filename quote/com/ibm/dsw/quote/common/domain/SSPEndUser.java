package com.ibm.dsw.quote.common.domain;

import com.ibm.dsw.quote.common.domain.QuoteContact;


public class SSPEndUser {
	
	private String dswCustomerNumber;
    private String companyName1;
    private String companyName2;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateProvinceCode;
    private String postalCode;
    private String countryCode;
    
    private QuoteContact sspContact;
    
	public SSPEndUser() {	
	}
	
	

	public QuoteContact getSspContact() {
		return sspContact;
	}


	public void setSspContact(QuoteContact sspContact) {
		this.sspContact = sspContact;
	}



	public String getDswCustomerNumber() {
		return dswCustomerNumber;
	}

	public void setDswCustomerNumber(String dswCustomerNumber) {
		this.dswCustomerNumber = dswCustomerNumber;
	}


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

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStateProvinceCode() {
		return stateProvinceCode;
	}

	public void setStateProvinceCode(String stateProvinceCode) {
		this.stateProvinceCode = stateProvinceCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
}
