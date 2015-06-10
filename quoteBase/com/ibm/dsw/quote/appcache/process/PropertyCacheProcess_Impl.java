package com.ibm.dsw.quote.appcache.process;

import com.ibm.dsw.quote.appcache.domain.PropertyConfigFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CacheProcess_Impl</code> class is abstract implementation of
 * Common Process.
 */

public abstract class PropertyCacheProcess_Impl extends TopazTransactionalProcess implements PropertyCacheProcess {

    
    public String getPropertyConfigByName(String name) throws QuoteException{
    	String propertyValue = null;
    	try {
    		propertyValue = PropertyConfigFactory.singleton().getPropertyValueByName(name);
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } 
    	return propertyValue;
    }
}
