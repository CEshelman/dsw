package com.ibm.dsw.quote.audit.domain;

import java.util.Date;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteAuditHistInfo<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: Dec 27, 2010
 */
public class QuoteAuditHistInfo implements java.io.Serializable {
	private String userAction = null;
	private String userActionShowName = null;
	private String lineItemSeqNum = null;
	private String oldVal = null;
	private String newVal = null;
	private Date modDate = null;
	private String modDateStr = null;
	private String modBy = null;
	private String speclBidApprvrLvl = null;

	public QuoteAuditHistInfo(){
		
	}
	public QuoteAuditHistInfo(String userAction,String lineItemSeqNum,
				String oldVal,String newVal,Date modDate,String modBy){
		this.userAction = userAction;
		this.lineItemSeqNum = lineItemSeqNum;
		this.oldVal = oldVal;
		this.newVal = newVal;
		this.modDate = modDate;
		this.modBy = modBy;
	}
	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	public String getLineItemSeqNum() {
		return lineItemSeqNum;
	}
	public void setLineItemSeqNum(String lineItemSeqNum) {
		this.lineItemSeqNum = lineItemSeqNum;
	}
	public String getOldVal() {
		return oldVal;
	}
	public void setOldVal(String oldVal) {
		this.oldVal = oldVal;
	}
	public String getNewVal() {
		return newVal;
	}
	public void setNewVal(String newVal) {
		this.newVal = newVal;
	}
	public Date getModDate() {
		return modDate;
	}
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}
	public String getModBy() {
		return modBy;
	}
	public void setModBy(String modBy) {
		this.modBy = modBy;
	}
	public String getUserActionShowName() {
		return userActionShowName;
	}
	public void setUserActionShowName(String userActionShowName) {
		this.userActionShowName = userActionShowName;
	}
	public String getModDateStr() {
		return modDateStr;
	}
	public void setModDateStr(String modDateStr) {
		this.modDateStr = modDateStr;
	}
	public String getSpeclBidApprvrLvl() {
		return speclBidApprvrLvl;
	}
	public void setSpeclBidApprvrLvl(String speclBidApprvrLvl) {
		this.speclBidApprvrLvl = speclBidApprvrLvl;
	}
	
	
	
}
