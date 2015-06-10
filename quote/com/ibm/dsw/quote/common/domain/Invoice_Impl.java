package com.ibm.dsw.quote.common.domain;

import java.util.Date;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Invoice_Impl.java</code>
 * 
 * @author: <a href="wjinfeng@cn.ibm.com">Mars</a>
 * 
 * Created on: Apr 27, 2007
 */

public class Invoice_Impl implements Invoice {

    public String invoiceNumber="";
    public Date invoiceDate = new Date();
    public String billingOrderNumber = "";
    
    /**
     * @return Returns the InvoiceDate.
     */
	public Date getInvoiceDate() {
		return this.invoiceDate;
	}
	/**
	 * @return Returns the InvoiceNumber.
	 */
	public String getInvoiceNumber() {
		return this.invoiceNumber;
	}
	 /**
     * @return Returns the Billing Order number.
     */
	public String getBillingOrderNumber() {
		return this.billingOrderNumber;
	}

}
