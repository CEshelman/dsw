package com.ibm.dsw.quote.customerlist.domain;

import java.sql.Date;

import com.ibm.dsw.quote.base.config.CustomerConstants;


public class ActiveService implements java.io.Serializable {
	private String partNumber = null;
	private String billingFrequency = null;
	private String term = null;
	private String quantity = null;
	private String rampupFlag = null;
	private String rampupSeqNum = null;
	private String endDate = null;
	private String startDate = null;
	
	private String replaced = null;
	private String activeOnAgreementFlag = null;
	
	private boolean setupFlag = false;
	private boolean sbscrptnFlag = false;
	private boolean activeFlag = false;
	
	private Date cotermEndDate = null;
	private Integer renwlTermMths = null;
	private Date ordAddDate = null;
	private Date newxtRenwlDate = null;
	private String renwlModelCode = null;
	private Integer lineItemSeqNumber = null;
	private Date renewalEndDate = null;
	
	
	
	public Integer getLineItemSeqNumber() {
		return lineItemSeqNumber;
	}
	public void setLineItemSeqNumber(Integer lineItemSeqNumber) {
		this.lineItemSeqNumber = lineItemSeqNumber;
	}
	public Date getRenewalEndDate() {
		return renewalEndDate;
	}
	public void setRenewalEndDate(Date renewalEndDate) {
		this.renewalEndDate = renewalEndDate;
	}
	public Date getNewxtRenwlDate() {
		return newxtRenwlDate;
	}
	public void setNewxtRenwlDate(Date newxtRenwlDate) {
		this.newxtRenwlDate = newxtRenwlDate;
	}
	public String getRenwlModelCode() {
		return renwlModelCode;
	}
	public void setRenwlModelCode(String renwlModelCode) {
		this.renwlModelCode = renwlModelCode;
	}
	public Date getOrdAddDate() {
		return ordAddDate;
	}
	public void setOrdAddDate(Date ordAddDate) {
		this.ordAddDate = ordAddDate;
	}
	public Date getCotermEndDate() {
		return cotermEndDate;
	}
	public void setCotermEndDate(Date cotermEndDate) {
		this.cotermEndDate = cotermEndDate;
	}
	public Integer getRenwlTermMths() {
		return renwlTermMths;
	}
	public void setRenwlTermMths(Integer renwlTermMths) {
		this.renwlTermMths = renwlTermMths;
	}
	public boolean isActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	public boolean isSetupFlag() {
		return setupFlag;
	}
	public void setSetupFlag(boolean setupFlag) {
		this.setupFlag = setupFlag;
	}
	public boolean isSbscrptnFlag() {
		return sbscrptnFlag;
	}
	public void setSbscrptnFlag(boolean sbscrptnFlag) {
		this.sbscrptnFlag = sbscrptnFlag;
	}
	public String getActiveOnAgreementFlag() {
		return activeOnAgreementFlag;
	}
	public void setActiveOnAgreementFlag(String activeOnAgreementFlag) {
		this.activeOnAgreementFlag = activeOnAgreementFlag;
	}
	public String getReplaced() {
		return replaced;
	}
	public void setReplaced(String replaced) {
		this.replaced = replaced;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getRampupSeqNum() {
		return rampupSeqNum;
	}
	public void setRampupSeqNum(String rampupSeqNum) {
		this.rampupSeqNum = rampupSeqNum;
	}
	public String getRampupFlag() {
		return rampupFlag;
	}
	public void setRampupFlag(String rampupFlag) {
		this.rampupFlag = rampupFlag;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getBillingFrequency() {
		return billingFrequency;
	}
	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public boolean isRampupPart(){
		if(this.rampupFlag!=null && this.rampupFlag.equals(CustomerConstants.CONFIGURATOR_RAMPUP_FLAG_1)){
			return true;
		}else{
			return false;
		}
	}
	
}