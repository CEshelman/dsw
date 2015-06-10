package com.ibm.dsw.quote.appcache.process.jdbc;

import com.ibm.dsw.quote.appcache.process.PropertyCacheProcess;
import com.ibm.dsw.quote.appcache.process.PropertyCacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PropertyCacheProcessFactory_jdbc</code> class is jdbc impl of
 * PropertyCacheProcessFactory.
 */
public class PropertyCacheProcessFactory_jdbc extends PropertyCacheProcessFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.appcache.process.CacheProcessFactory#create()
     */
    public PropertyCacheProcess create() throws QuoteException {
        return new PropertyCacheProcess_jdbc();
    }

}
