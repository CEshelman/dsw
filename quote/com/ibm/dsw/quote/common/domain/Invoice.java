package com.ibm.dsw.quote.common.domain;

import java.util.Date;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Invoice.java</code>
 * 
 * @author: <a href="wjinfeng@cn.ibm.com">Mars</a>
 * 
 * Created on: Oct 15, 2010
 */

public interface Invoice {
	/**
	 * @return Returns the InvoiceNumber.
	 */
    public String getInvoiceNumber();
    /**
     * @return Returns the InvoiceDate.
     */
    public Date getInvoiceDate();
    /**
     * @return Returns the Billing Order number.
     */
    public String getBillingOrderNumber();
}
