package com.ibm.dsw.quote.common.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.CommonProcess;
import com.ibm.dsw.quote.common.process.CommonProcessFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CommonProcessFactory_jdbc</code> class is jdbc impl of
 * CommonProcessFactory.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-9
 */
public class CommonProcessFactory_jdbc extends CommonProcessFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.appcache.process.CommonProcessFactory#create()
     */
    public CommonProcess create() throws QuoteException {
        return new CommonProcess_jdbc();
    }

}
