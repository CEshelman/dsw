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

import commonj.timers.TimerManager;

public class CacheOnceWork extends CacheWork {
	public CacheOnceWork(TimerManager tm) {
		super(tm);
	}
	
	@Override
	public void run() {
		// For now, delay one min to start loading BootStrap cahce
        timer = timeManager.schedule(new BootStrapCacheListener(), 1000);
	}
}
