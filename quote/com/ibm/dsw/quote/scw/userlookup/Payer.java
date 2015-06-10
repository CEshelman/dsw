package com.ibm.dsw.quote.scw.userlookup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Payer {	
	
	@XmlElement(name="custName")
	private String payerCustNum;
	
	@XmlElement(name="Name")
	private CustomerName customerName;
	
	@XmlElement(name="custAddr")
	private CustomerFullAddress customerAddress;
	
	public void setPayerCustNum(String payerCustNum) {
		this.payerCustNum = payerCustNum;
	}
	
	public void setCustomerName(CustomerName customerName) {
		this.customerName = customerName;
	}
	
	public void setCustomerAddress(CustomerFullAddress customerAddress) {
		this.customerAddress = customerAddress;
	}
	
}
