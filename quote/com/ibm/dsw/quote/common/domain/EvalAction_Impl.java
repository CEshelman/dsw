package com.ibm.dsw.quote.common.domain;

import java.util.Date;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:kunzhwh@cn.ibm.com">Jason Zhang</a><br/>
 */
public class EvalAction_Impl implements EvalAction {
	
	public EvalAction_Impl(){}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fullName;
	private String emailAdr;
	private String userAction;
	private Date   addDate;
	private String  comments;
	
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmailAdr() {
		return emailAdr;
	}
	public void setEmailAdr(String emailAdr) {
		this.emailAdr = emailAdr;
	}
	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
}
