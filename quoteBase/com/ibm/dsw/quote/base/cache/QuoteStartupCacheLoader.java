package com.ibm.dsw.quote.base.cache;

import com.ibm.ead4j.quartz.common.CacheLoader;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteStartupCacheLoader</code> class is
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-23
 */
public class QuoteStartupCacheLoader extends CacheLoader {

    /**
     * do nothing at start up time.
     */

    public Object load(Object handle, Object args) {
        return null;
    }

}
