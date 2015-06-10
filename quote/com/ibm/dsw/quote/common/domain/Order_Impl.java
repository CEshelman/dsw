package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Order_Impl.java</code>
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Created on: Apr 27, 2007
 */

public class Order_Impl implements Order {

    public String orderNumber="";
    public Date submittedDate = new Date();
    public double totalPrice = 0;
    public String currencyCode;
    public String countryCode;
    public List statusList = new ArrayList();
    public String orderIdocNum;
    public String purchaseNum;
    public List invoiceList;
    public String orderType;
    public String orderSubmitterName;
    public String orderSubmitterEmail;

    public String getOrderSubmitterName() {
		return orderSubmitterName;
	}

	public String getOrderSubmitterEmail() {
		return orderSubmitterEmail;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getPurchaseNum() {
		return purchaseNum;
	}

	public List getInvoiceList() {
		return invoiceList;
	}

	public String getOrderNumber() {
        return this.orderNumber;
    }

    public Date getSubmittedDate() {
        return this.submittedDate;
    }

    public double getTotalPrice() {
        return this.totalPrice;
    }

    public List getStatusList() {
        return this.statusList;
    }

    /**
     * @return Returns the currencyCode.
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    /**
     * @return Returns the countryCode.
     */
    public String getCountryCode() {
        return countryCode;
    }
    /**
     * @return Returns the orderIdocNum.
     */
    public String getOrderIdocNum() {
        if(orderIdocNum.length() > 8)
            return org.apache.commons.lang.StringUtils.substring(orderIdocNum,8);
        else
            return orderIdocNum;
    }
    /**
     * @param orderIdocNum The orderIdocNum to set.
     */
    public void setOrderIdocNum(String orderIdocNum) {
        this.orderIdocNum = orderIdocNum;
    }
}
