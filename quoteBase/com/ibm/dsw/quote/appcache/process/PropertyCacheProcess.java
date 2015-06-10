package com.ibm.dsw.quote.appcache.process;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CacheProcess</code> class is to wrap interfaces upon all
 * cacheable domains.
 * 
 */
public interface PropertyCacheProcess {
    public String getPropertyConfigByName(String name) throws QuoteException;
}
