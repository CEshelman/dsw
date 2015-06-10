package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerGroup<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: Jan 28, 2011
 *
 * $Log: CustomerGroup.java,v $
 * Revision 1.3  2011/02/16 09:43:41  yuepingl
 * Restore the file
 *
 * Revision 1.1  2011/01/30 02:33:36  mmzhou
 * RTC task 81294: ebiz: JKEY-8DALPM: FIT AS CAN: Add customer groupings to SQO/Admin - Pushpa team - CP area impact  reviewed by Carter
 *
 */
public class CustomerGroup implements Serializable{
	private String customerGroupName = null;

	public String getCustomerGroupName() {
		return customerGroupName;
	}

	public void setCustomerGroupName(String customerGroupName) {
		this.customerGroupName = customerGroupName;
	}
	
}
