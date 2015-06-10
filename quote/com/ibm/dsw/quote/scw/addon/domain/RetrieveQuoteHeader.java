package com.ibm.dsw.quote.scw.addon.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="header")
@XmlAccessorType (XmlAccessType.FIELD)
public class RetrieveQuoteHeader {

	private String identifier;
	private String itemCount;
	private String currencyCode;
	private String orderDate;
	private String salesOrganization;
	private String referenceDocNum;
	private String agreementType;
	private String webIDocNum;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		if (StringUtils.isBlank(identifier)) {
			return;
		}
		this.identifier = identifier;
	}

	public String getWebIDocNum() {
		return webIDocNum;
	}

	public void setWebIDocNum(String webIDocNum) {
		this.webIDocNum = webIDocNum;
	}

	public String getItemCount() {
		return itemCount;
	}

	public void setItemCount(String itemCount) {
		if (StringUtils.isBlank(itemCount)) {
			return;
		}
		this.itemCount = itemCount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		if (StringUtils.isBlank(currencyCode)) {
			return;
		}
		this.currencyCode = currencyCode;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		if (StringUtils.isBlank(orderDate)) {
			return;
		}
		this.orderDate = orderDate;
	}

	public String getSalesOrganization() {
		return salesOrganization;
	}

	public void setSalesOrganization(String salesOrganization) {
		if (StringUtils.isBlank(salesOrganization)) {
			return;
		}
		this.salesOrganization = salesOrganization;
	}

	public String getReferenceDocNum() {
		return referenceDocNum;
	}

	public void setReferenceDocNum(String referenceDocNum) {
		if (StringUtils.isBlank(referenceDocNum)) {
			return;
		}
		this.referenceDocNum = referenceDocNum;
	}

	public String getAgreementType() {
		return agreementType;
	}

	public void setAgreementType(String agreementType) {
		if (StringUtils.isBlank(agreementType)) {
			return;
		}
		this.agreementType = agreementType;
	}
	
}
