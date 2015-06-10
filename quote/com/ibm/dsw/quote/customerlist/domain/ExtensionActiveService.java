package com.ibm.dsw.quote.customerlist.domain;

import java.sql.Date;


public class ExtensionActiveService implements java.io.Serializable{
	private Date caLineItemEndDate = null;

	private Date caLineItemStartDate = null;
	private Integer renwlTermMths = null;
	private Date newxtRenwlDate = null;
	private String renwlModelCode = null;
	private Date renewalEndDate = null;

	private int remaningTerm = 0;
	private int finalTerm = 0;
	private int calculateRemaningTerm = 0;
	private String partNumber = null;
	private Integer lineItemSeqNumber = null;


	boolean allEndDateIsNull = false;

	public boolean isAllEndDateIsNull() {
		return allEndDateIsNull;
	}
	public void setAllEndDateIsNull(boolean allEndDateIsNull) {
		this.allEndDateIsNull = allEndDateIsNull;
	}
	public int getRemaningTerm() {
		return remaningTerm;
	}
	public void setRemaningTerm(int remaningTerm) {
		this.remaningTerm = remaningTerm;
	}
	public int getFinalTerm() {
		return finalTerm;
	}
	public void setFinalTerm(int finalTerm) {
		this.finalTerm = finalTerm;
	}
	public Date getCaLineItemEndDate() {
		return caLineItemEndDate;
	}
	public void setCaLineItemEndDate(Date caLineItemEndDate) {
		this.caLineItemEndDate = caLineItemEndDate;
	}
	public Integer getRenwlTermMths() {
		return renwlTermMths;
	}
	public void setRenwlTermMths(Integer renwlTermMths) {
		this.renwlTermMths = renwlTermMths;
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
	public Date getRenewalEndDate() {
		return renewalEndDate;
	}
	public void setRenewalEndDate(Date renewalEndDate) {
		this.renewalEndDate = renewalEndDate;
	}
	
	public Date getCaLineItemStartDate() {
		return caLineItemStartDate;
	}
	public void setCaLineItemStartDate(Date caLineItemStartDate) {
		this.caLineItemStartDate = caLineItemStartDate;
	}
	
	
	public int getCalculateRemaningTerm() {
		return calculateRemaningTerm;
	}
	public void setCalculateRemaningTerm(int calculateRemaningTerm) {
		this.calculateRemaningTerm = calculateRemaningTerm;
	}
	
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public Integer getLineItemSeqNumber() {
		return lineItemSeqNumber;
	}
	public void setLineItemSeqNumber(Integer lineItemSeqNumber) {
		this.lineItemSeqNumber = lineItemSeqNumber;
	}


}