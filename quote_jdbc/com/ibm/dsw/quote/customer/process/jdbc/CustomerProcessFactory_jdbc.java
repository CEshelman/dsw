package com.ibm.dsw.quote.customer.process.jdbc;

import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerProcessFactory_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public class CustomerProcessFactory_jdbc extends CustomerProcessFactory {

    /**
     *  
     */
    public CustomerProcessFactory_jdbc() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.home.action.process.CustomerProcessFactory#create()
     */
    public CustomerProcess create() throws TopazException {
        return new CustomerProcess_jdbc();
    }

}
