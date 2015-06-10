package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmitMigrationReqContract<code> class.
 *    
 * @author: xiongxj@cn.ibm.com
 * 
 * Creation date: 2012-5-15
 */
public class SubmitMigrationReqContract extends QuoteBaseCookieContract {

	private static final long serialVersionUID = 1L;
	protected String migrationReqNum;
	protected String sapCustNum;
	protected String sapCtrctNum;
	protected String highlightId;
	

	public String getSapCustNum() {
		return sapCustNum;
	}
	public void setSapCustNum(String sapCustNum) {
		this.sapCustNum = sapCustNum;
	}
	public String getMigrationReqNum() {
		return migrationReqNum;
	}
	public void setMigrationReqNum(String migrationReqNum) {
		this.migrationReqNum = migrationReqNum;
	}
	public String getSapCtrctNum() {
		return sapCtrctNum;
	}
	public void setSapCtrctNum(String sapCtrctNum) {
		this.sapCtrctNum = sapCtrctNum;
	}
	public String getHighlightId() {
		return highlightId;
	}
	public void setHighlightId(String highlightId) {
		this.highlightId = highlightId;
	}
	
}
