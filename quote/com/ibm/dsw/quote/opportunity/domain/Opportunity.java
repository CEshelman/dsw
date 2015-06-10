package com.ibm.dsw.quote.opportunity.domain;

import java.io.Serializable;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>Opportunity</code> class. This class is used as value object
 * which is in the return Opportunity list from EIW
 * 
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2012-02-15
 */
public class Opportunity implements Serializable {
	
	private static final long serialVersionUID = -2958056052115842319L;
	
	private String opptNum = null;
	private String opptName = null;
	
	public Opportunity(String opptNum, String opptName){
		this.opptNum = opptNum;
		this.opptName = opptName;
	}
	
	public String getOpptNum() {
		return opptNum;
	}
	public void setOpptNum(String opptNum) {
		this.opptNum = opptNum;
	}
	public String getOpptName() {
		return opptName;
	}
	public void setOpptName(String opptName) {
		this.opptName = opptName;
	}
	
	
}
