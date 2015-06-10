package com.ibm.dsw.quote.scw.addon.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="configuration")
@XmlAccessorType (XmlAccessType.FIELD)
public class AddOnTradeUpConfiguration {
	
	private String termLength;
	/** required, value 0 or 1 */
	private int coTermFlag = Integer.MIN_VALUE;
	private String refConfigId;
	private String configId;
	/** required, value A, U, N */
	private String updateCAConfigCode;
    /**public static final String configCode_a;*/
	public static final String CONFIGCODE_U="U";
	/**add a new configuration to an existing charge agreement*/
	public static final String CONFIGCODE_A="A";
	/**does not update an existing charge agreement*/
	public static final String CONFIGCODE_N="N";
	private List<AddOnTradeUpLineItem> lineItems;
	/**
	 * @return the termLength
	 */
	public String getTermLength() {
		return termLength;
	}
	/**
	 * @param termLength the termLength to set
	 */
	public void setTermLength(String termLength) {
		this.termLength = termLength;
	}
	/**
	 * @return the coTermFlag
	 */
	public int getCoTermFlag() {
		return coTermFlag;
	}
	/**
	 * @param coTermFlag the coTermFlag to set
	 */
	public void setCoTermFlag(int coTermFlag) {
		this.coTermFlag = coTermFlag;
	}
	/**
	 * @return the configId
	 */
	public String getConfigId() {
		return configId;
	}
	/**
	 * @param configId the configId to set
	 */
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	/**
	 * @return the updateCAConfigCode
	 */
	public String getUpdateCAConfigCode() {
		return updateCAConfigCode;
	}
	/**
	 * @param updateCAConfigCode the updateCAConfigCode to set
	 */
	public void setUpdateCAConfigCode(String updateCAConfigCode) {
		this.updateCAConfigCode = updateCAConfigCode;
	}
	/**
	 * @return the lineItems
	 */
	public List<AddOnTradeUpLineItem> getLineItems() {
		return lineItems;
	}
	/**
	 * @param lineItems the lineItems to set
	 */
	public void setLineItems(List<AddOnTradeUpLineItem> lineItems) {
		this.lineItems = lineItems;
	}
	
	public String getRefConfigId() {
		return refConfigId;
	}
	public void setRefConfigId(String refConfigId) {
		this.refConfigId = refConfigId;
	}

}
