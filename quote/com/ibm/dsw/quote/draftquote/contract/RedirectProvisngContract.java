package com.ibm.dsw.quote.draftquote.contract;

public class RedirectProvisngContract extends PostPartPriceTabContract {
	
	private String webQuoteNum;
	
	private String provisngIdForBrand;
	
	private String returnUrl;
	
	private String saasBrandCode;
	

	public String getProvisngIdForBrand() {
		return provisngIdForBrand;
	}

	public void setProvisngIdForBrand(String provisngIdForBrand) {
		this.provisngIdForBrand = provisngIdForBrand;
	}

	public String getSaasBrandCode() {
		return saasBrandCode;
	}

	public void setSaasBrandCode(String saasBrandCode) {
		this.saasBrandCode = saasBrandCode;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}

	
	
}
