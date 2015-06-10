/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author: zhangji@us.ibm.com
 * 
 * Creation date: Feb 5, 2013
 */
package com.ibm.dsw.quote.base.cache;

import java.util.Map;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.quartz.common.CacheAccess;
import com.ibm.ead4j.quartz.common.CacheContext;
import com.ibm.ead4j.quartz.common.CacheContextFactory;
import com.ibm.ead4j.quartz.exception.CacheException;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.TopazCacheableFactory;

import commonj.timers.Timer;
import commonj.timers.TimerListener;

public class BootStrapCacheListener implements TimerListener {
	public void timerExpired(Timer arg0) {
		LogContext logger = LogContextFactory.singleton().getLogContext();

		TopazCacheableFactory[] factories = QuoteCacheBootStrape.getInstance() .getCachedKeyFactories();
		CacheContext cc = CacheContextFactory.singleton().getCacheContext();
		CacheAccess ca;

		try {
			QuoteCache cache = (QuoteCache) cc.getCache();
			cache.loadGroup("QUOTE_CACHE_GROUP", factories);
			ca = cc.getCacheAccess("QUOTE_CACHE_REGION");

			for (TopazCacheableFactory factory: factories) {
				if (null == factory) {
					continue;
				}
				
				String factoryName = factory.getClass().getName();
				logger.debug(this, "Fetching Cached Object with key: " + factoryName);

				if (ca.isPresent(factoryName)) {
					continue;
				}

				QuoteTopazCacheableFactoryHelper helper = QuoteTopazCacheableFactoryHelper.singleton();
				Map map = helper.initializeCache(factory);

				ca.put(factoryName, "QUOTE_CACHE_GROUP", cc.getCacheGroupConfig("QUOTE_CACHE_GROUP").getAttributes(), map);
				logger.debug(this, "Object with Key: " + factoryName + " has been refreshed!");
			}
		} catch (CacheException e) {
			logger.error(this, e);
		} catch (TopazException e) {
			e.printStackTrace();
		}
	}
}