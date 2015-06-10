package com.ibm.dsw.quote.configurator.domain;

import java.io.Serializable;
import java.sql.Date;

public class CotermParameter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5075138628405740418L;
	private Date endDate;
	private int refDocLineItemSeqNum;
	private String configrtnId;
	
	public CotermParameter(Date endDate, int refDocLineItemSeqNum, String configrtnId){
		this.endDate = endDate;
		this.refDocLineItemSeqNum = refDocLineItemSeqNum;
		this.configrtnId = configrtnId;
	}
	
	public int getRefDocLineItemSeqNum() {
		return refDocLineItemSeqNum;
	}

	public Date getEndDate() {
		return endDate;
	}
	
	public String getConfigrtnId(){
		return configrtnId;
	}
	
	public String toString(){
		return "endDate=" + getEndDate()
		        + ", configrtnId=" + getConfigrtnId()
		        + ", refDocLineItemSeqNum=" + getRefDocLineItemSeqNum();
	}

}
