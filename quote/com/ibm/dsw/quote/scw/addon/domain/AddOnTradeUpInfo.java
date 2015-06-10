package com.ibm.dsw.quote.scw.addon.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement(name="QuoteAddOnTradeUpRequest")
@XmlAccessorType (XmlAccessType.FIELD)
public class AddOnTradeUpInfo {
	
	private AddOnTradeUpHeader header;
	private String appIdentifier;
	private String referenceNumber;
	private String chargeAgreementNumber;
	private List<AddOnTradeUpConfiguration> configurations;

	@XmlTransient
	private String webQuoteNum;
	@XmlTransient
	private String userId;
	
	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the appIdentifier
	 */
	public String getAppIdentifier() {
		if (header != null) {
			return header.getIdentifier();
		}
		return appIdentifier;
	}
	/**
	 * @param appIdentifier the appIdentifier to set
	 */
	public void setAppIdentifier(String appIdentifier) {
		this.appIdentifier = appIdentifier;
	}
	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		if (header != null) {
			return header.getReferenceNumber();
		}
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
		if (header != null) {
			return header.getChargeAgreementNumber();
		}
		return chargeAgreementNumber;
	}
	/**
	 * @param chargeAgreementNumber the chargeAgreementNumber to set
	 */
	public void setChargeAgreementNumber(String chargeAgreementNumber) {
		this.chargeAgreementNumber = chargeAgreementNumber;
	}

	/**
	 * @return the configurations
	 */
	public List<AddOnTradeUpConfiguration> getConfigurations() {
		return configurations;
	}
	/**
	 * @param configurations the configurations to set
	 */
	public void setConfigurations(List<AddOnTradeUpConfiguration> configurations) {
		this.configurations = configurations;
	}

	/**
	 * @return the header
	 */
	public AddOnTradeUpHeader getHeader() {
		return header;
	}
	/**
	 * @param header the header to set
	 */
	public void setHeader(AddOnTradeUpHeader header) {
		this.header = header;
	}
	


}
