package com.ibm.dsw.quote.dsj.contract;


public class DsjDisplayDraftQuoteContract extends DsjBaseContract {
	private String siteNumber = null;
	private String agreementNumber = null;
	private String currency = null;
	private String partnerNum = null;
	private String partnerType = null;
	private String partnerLob = null;
	
	
	
	public String getPartnerLob() {
		return partnerLob;
	}
	public void setPartnerLob(String partnerLob) {
		this.partnerLob = partnerLob;
	}
	public String getSiteNumber() {
		return siteNumber;
	}
	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}
	public String getAgreementNumber() {
		return agreementNumber;
	}
	public void setAgreementNumber(String agreementNumber) {
		this.agreementNumber = agreementNumber;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getPartnerNum() {
		return partnerNum;
	}
	public void setPartnerNum(String partnerNum) {
		this.partnerNum = partnerNum;
	}
	public String getPartnerType() {
		return partnerType;
	}
	public void setPartnerType(String partnerType) {
		this.partnerType = partnerType;
	}
	
	
}
