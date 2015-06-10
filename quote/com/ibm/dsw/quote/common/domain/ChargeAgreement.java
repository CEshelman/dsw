package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.Date;

public class ChargeAgreement implements Serializable {
	private Date endDate = null;

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
}
