package com.ibm.dsw.quote.customerlist.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customerlist.domain.CustomerListResult;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerListProcess<code> class.
 *    
 * @author: xiaogy@cn.ibm.com
 * 
 * Creation date: 2008-6-24
 */

public interface CustomerListProcess {
    public CustomerListResult retrieveCustomerByCustNum(String custNum, String programType, String countryCode,
            String currencyCode, int startingIndex, int endingIndex) throws QuoteException;

    public CustomerListResult retrieveCustomerByCustAttr(String custAttr, String stateOrProvinceCode,
            String programType, String countryCode, String currencyCode, int startingIndex, int endingIndex)
            throws QuoteException;
}
