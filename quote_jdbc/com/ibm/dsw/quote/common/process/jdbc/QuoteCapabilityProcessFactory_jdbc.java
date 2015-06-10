package com.ibm.dsw.quote.common.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteCapabilityProcessFactory_jdbc</code> class is jdbc
 * implementaton of QuoteCapabilityProcessFacotry.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 3, 2007
 */
public class QuoteCapabilityProcessFactory_jdbc extends QuoteCapabilityProcessFactory {

    /**
     *  
     */

    public QuoteCapabilityProcess create() throws QuoteException {
        return new QuoteCapabilityProcess_jdbc();
    }

}
