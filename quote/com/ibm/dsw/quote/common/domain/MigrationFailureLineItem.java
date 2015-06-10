package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

public class MigrationFailureLineItem implements Serializable {

	private static final long serialVersionUID = 2492412312864615029L;
	private String partNum;
	private String partDesc;
	private String reason;
	public String getPartNum() {
		return partNum;
	}
	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}
	public String getPartDesc() {
		return partDesc;
	}
	public void setPartDesc(String partDesc) {
		this.partDesc = partDesc;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	

}
