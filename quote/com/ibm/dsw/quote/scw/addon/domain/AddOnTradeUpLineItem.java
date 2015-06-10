package com.ibm.dsw.quote.scw.addon.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="lineItem")
@XmlAccessorType (XmlAccessType.FIELD)
public class AddOnTradeUpLineItem {
	
	private String itemNumber;
	private String partNumber;
	private String billFrequency;
	private String quantity;
	
	/**
	 * @return the itemNumber
	 */
	public String getItemNumber() {
		return itemNumber;
	}
	/**
	 * @param itemNumber the itemNumber to set
	 */
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	/**
	 * @return the partNumber
	 */
	public String getPartNumber() {
		return partNumber;
	}
	/**
	 * @param partNumber the partNumber to set
	 */
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	/**
	 * @return the billFrequency
	 */
	public String getBillFrequency() {
		return billFrequency;
	}
	/**
	 * @param billFrequency the billFrequency to set
	 */
	public void setBillFrequency(String billFrequency) {
		this.billFrequency = billFrequency;
	}

	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}


}
