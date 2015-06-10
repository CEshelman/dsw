/*
 * Created on 2007-2-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.cache;

import com.ibm.ead4j.quartz.common.Cache;
import com.ibm.ead4j.quartz.common.CacheAttributesFactory;
import com.ibm.ead4j.quartz.common.CacheFactory;

/**
 * @author Nathan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuoteCacheFactory extends CacheFactory {

    public Cache create()
    {
        if(super.cache == null)
        {
            super.cache = new QuoteCache();
            com.ibm.ead4j.quartz.common.CacheAttributes cacheAttributes = CacheAttributesFactory.singleton().create();
            cache.init(cacheAttributes);
        }
        return cache;
    }

}
