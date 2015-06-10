package com.ibm.dsw.quote.common.domain;

import java.util.List;

public class MigrateRequest_Impl implements MigrateRequest {

	protected String requestNum = "";
	protected String billingFreq = null;
	protected int coverageTerm = -1;
	protected List<MigratePart> parts = null;
	
	private String orginalCANum = null;
	private String soldToCustNum = null;
	private String sapCtrctNum = null;
	private String reslCustNum = null;
	private String payerCustNum = null;
	private String sapIDocNum = null;
	private String sapDistChnl = null;
	private String migrtnStageCode = null;
	private String lob = null;
	private String acqCode = null;
	private List<MigrationFailureLineItem> lineItems = null;
	
    private Customer customer = null;
    private Partner payer = null;
    private Partner reseller = null;
    private String fulfillmentSrc = null;
    private String countryCode = null;
    private String currencyCode = null;
    private String orignalSapDistChnl = null;
    
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getOrginalCANum() {
		return orginalCANum;
	}
	public void setOrginalCANum(String orginalCANum) {
		this.orginalCANum = orginalCANum;
	}
	
	public String getLob() {
		return lob;
	}
	public void setLob(String lob) {
		this.lob = lob;
	}
	
	public String getSoldToCustNum() {
		return soldToCustNum;
	}
	public void setSoldToCustNum(String soldToCustNum) {
		this.soldToCustNum = soldToCustNum;
	}
	public String getSapCtrctNum() {
		return sapCtrctNum;
	}
	public void setSapCtrctNum(String sapCtrctNum) {
		this.sapCtrctNum = sapCtrctNum;
	}
	public String getReslCustNum() {
		return reslCustNum;
	}
	public void setReslCustNum(String reslCustNum) {
		this.reslCustNum = reslCustNum;
	}
	public String getPayerCustNum() {
		return payerCustNum;
	}
	public void setPayerCustNum(String payerCustNum) {
		this.payerCustNum = payerCustNum;
	}
	public String getSapIDocNum() {
		return sapIDocNum;
	}
	public void setSapIDocNum(String sapIDocNum) {
		this.sapIDocNum = sapIDocNum;
	}
	public String getSapDistChnl() {
		return sapDistChnl;
	}
	public void setSapDistChnl(String sapDistChnl) {
		this.sapDistChnl = sapDistChnl;
	}
	public String getMigrtnStageCode() {
		return migrtnStageCode;
	}
	public void setMigrtnStageCode(String migrtnStageCode) {
		this.migrtnStageCode = migrtnStageCode;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Partner getPayer() {
		return payer;
	}
	public void setPayer(Partner payer) {
		this.payer = payer;
	}
	public Partner getReseller() {
		return reseller;
	}
	public void setReseller(Partner reseller) {
		this.reseller = reseller;
	}
	public String getRequestNum() {
		return requestNum;
	}
	public void setRequestNum(String requestNum) {
		this.requestNum = requestNum;
	}
	public String getBillingFreq() {
		return billingFreq;
	}
	public void setBillingFreq(String billingFreq) {
		this.billingFreq = billingFreq;
	}
	public int getCoverageTerm() {
		return coverageTerm;
	}
	public void setCoverageTerm(int coverageTerm) {
		this.coverageTerm = coverageTerm;
	}
	public List<MigratePart> getParts() {
		return parts;
	}
	public void setParts(List<MigratePart> parts) {
		this.parts = parts;
	}

	public String getFulfillmentSrc() {
		return fulfillmentSrc;
	}
	public void setFulfillmentSrc(String fulfillmentSrc) {
		this.fulfillmentSrc = fulfillmentSrc;
	}
	public String getAcqCode() {
		return acqCode;
	}
	public void setAcqCode(String acqCode) {
		this.acqCode = acqCode;
	}

	public List<MigrationFailureLineItem> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<MigrationFailureLineItem> lineItems) {
		this.lineItems = lineItems;
	}
	public String getOrignalSapDistChnl(){
		return orignalSapDistChnl;
	}
	public void setOrignalSapDistChnl(String orignalSapDistChnl){
		this.orignalSapDistChnl = orignalSapDistChnl;
	}
}
