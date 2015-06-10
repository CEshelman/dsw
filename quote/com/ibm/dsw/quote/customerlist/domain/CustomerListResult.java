package com.ibm.dsw.quote.customerlist.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerListResult<code> class.
 *    
 * @author: xiaogy@cn.ibm.com
 * 
 * Creation date: 2008-6-24
 */

public class CustomerListResult 
{
    private int totalCount = 0;
    private int sqlErrorCode = 0;
    private	List customers = new ArrayList();
    
    /**
     * @return Returns the sqlErrorCode.
     */
    public int getSqlErrorCode() {
        return sqlErrorCode;
    }
    /**
     * @param sqlErrorCode The sqlErrorCode to set.
     */
    public void setSqlErrorCode(int sqlErrorCode) {
        this.sqlErrorCode = sqlErrorCode;
    }
    /**
     * @return Returns the totalCount.
     */
    public int getTotalCount() {
        return totalCount;
    }
    /**
     * @param totalCount The totalCount to set.
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    /**
     * @return Returns the customers.
     */
    public List getCustomers() {
        return customers;
    }
    
    public void addCustomer(CustomerList customer){
        customers.add(customer);
    }
}
