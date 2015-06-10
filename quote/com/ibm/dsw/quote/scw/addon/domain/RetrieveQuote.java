package com.ibm.dsw.quote.scw.addon.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="QuoteAddOnTradeUpResponse")
@XmlAccessorType (XmlAccessType.FIELD)
public class RetrieveQuote {

	private List<RetrieveResult> result;
	private String resultCode;
	private String resultDesc;
	private RetrieveQuoteHeader header;
    /**
	 * @return the result
	 */
	public List<RetrieveResult> getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(List<RetrieveResult> result) {
		this.result = result;
	}

	private List<RetrieveLineItem> lineItems;
	
	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		if (StringUtils.isBlank(resultCode)) {
			return;
		}
		this.resultCode = resultCode;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		if (StringUtils.isBlank(resultDesc)) {
			return;
		}
		this.resultDesc = resultDesc;
	}

	public RetrieveQuoteHeader getHeader() {
		return header;
	}

	public void setHeader(RetrieveQuoteHeader header) {
		this.header = header;
	}

	public List<RetrieveLineItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<RetrieveLineItem> lineItems) {
		this.lineItems = lineItems;
	}

	
}
