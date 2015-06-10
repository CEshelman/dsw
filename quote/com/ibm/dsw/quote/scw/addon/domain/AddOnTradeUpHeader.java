package com.ibm.dsw.quote.scw.addon.domain;


public class AddOnTradeUpHeader {
	
	private String identifier;
	private String referenceNumber;
	private String chargeAgreementNumber;
	/**
	 * @return the appIdentifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @param appIdentifier the appIdentifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}
	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	/**
	 * @return the chargeAgreementNumber
	 */
	public String getChargeAgreementNumber() {
		return chargeAgreementNumber;
	}
	/**
	 * @param chargeAgreementNumber the chargeAgreementNumber to set
	 */
	public void setChargeAgreementNumber(String chargeAgreementNumber) {
		this.chargeAgreementNumber = chargeAgreementNumber;
	}


}
