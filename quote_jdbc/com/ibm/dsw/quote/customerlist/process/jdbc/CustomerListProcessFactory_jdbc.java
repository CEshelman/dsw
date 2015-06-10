package com.ibm.dsw.quote.customerlist.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customerlist.process.CustomerListProcess;
import com.ibm.dsw.quote.customerlist.process.CustomerListProcessFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerListProcessFactory_jdbc<code> class.
 *    
 * @author: xiaogy@cn.ibm.com
 * 
 * Creation date: 2008-6-24
 */

public class CustomerListProcessFactory_jdbc extends CustomerListProcessFactory{

    public CustomerListProcess create() throws QuoteException {
        return new CustomerListProcess_jdbc();
    }
}
