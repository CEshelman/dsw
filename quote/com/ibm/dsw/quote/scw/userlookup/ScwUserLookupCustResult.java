package com.ibm.dsw.quote.scw.userlookup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ScwUserLookupCustResult {
	
	@XmlElement(name="custNum")
	private String custNum;

	/*
	 * Restrict payment method to credit card or allow PO purchase?
	 * 
	 */
	@XmlElement(name="ifCreditCardAndPoAllow")
	private boolean ifCreditCardAndPoAllow;
	
	@XmlElement(name="PIDStatus")
	private int configStatus = 0;
	
	@XmlElement(name="custName")
	private CustomerName customerName;
	
	@XmlElement(name="custAddr")
	private CustomerFullAddress customerAddress;
	
	@XmlElement(name="currencyCode")
	private String currencyCode;


//	public CustomerName getCustomerName() {
//		return customerName;
//	}

	public void setCustomerName(CustomerName customerName) {
		this.customerName = customerName;
	}

//	public CustomerFullAddress getCustomerAddress() {
//		return customerAddress;
//	}

	public void setCustomerAddress(CustomerFullAddress customerAddress) {
		this.customerAddress = customerAddress;
	}

//	public String getCurrencyCode() {
//		return currencyCode;
//	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}


//	public boolean getIfCreditCardAndPoAllow() {
//		return ifCreditCardAndPoAllow;
//	}

	public void setIfCreditCardAndPoAllow(boolean ifCreditCardAndPoAllow) {
		this.ifCreditCardAndPoAllow = ifCreditCardAndPoAllow;
	}
	
//	public String getCustNum() {
//		return custNum;
//	}

	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}
	
	public void setConfigStatus(int flag){
		this.configStatus = flag;
	}
}
