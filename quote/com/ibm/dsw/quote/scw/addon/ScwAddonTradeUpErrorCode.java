package com.ibm.dsw.quote.scw.addon;


public class ScwAddonTradeUpErrorCode {

	private String errorCode;
	private String errorCodeDesc;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCodeDesc() {
		return errorCodeDesc;
	}

	public void setErrorCodeDesc(String errorCodeDesc) {
		this.errorCodeDesc = errorCodeDesc;
	}

	@Override
	public String toString() {
		return new StringBuilder("").append(this.errorCode).append(": ").append(this.errorCodeDesc).toString();
	}

}
