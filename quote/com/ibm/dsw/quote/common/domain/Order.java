package com.ibm.dsw.quote.common.domain;

import java.util.Date;
import java.util.List;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Order.java</code>
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Created on: Apr 27, 2007
 */

public interface Order {
    public String getOrderNumber();
    public Date getSubmittedDate();
    public double getTotalPrice();
    public List getStatusList();
    public String getCurrencyCode();
    public String getCountryCode();
    public String getOrderType();
    public String getOrderIdocNum();
    public String getPurchaseNum();
    public List getInvoiceList();
    public String getOrderSubmitterName();
    public String getOrderSubmitterEmail();
    
}
