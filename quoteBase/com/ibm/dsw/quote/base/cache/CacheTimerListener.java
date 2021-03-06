/*
 * Created on 2007-2-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.cache;

import com.ibm.dsw.quote.base.util.PeakHoursUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.quartz.common.CacheContext;
import com.ibm.ead4j.quartz.common.CacheContextFactory;
import com.ibm.ead4j.quartz.exception.CacheException;
import com.ibm.ead4j.topaz.persistence.jdbc.TopazCacheableFactory;
import commonj.timers.Timer;
import commonj.timers.TimerListener;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CacheTimerListener implements TimerListener {
    

    /* (non-Javadoc)
     * @see commonj.timers.TimerListener#timerExpired(commonj.timers.Timer)
     */
    public void timerExpired(Timer timer) {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        logger.debug(this,"["+System.currentTimeMillis()+"]Checking Cached Object ......");
        //check if the Cached Object is valid
        if ( PeakHoursUtil.getInstance().skipCheck() )
        {
            logger.info(this, "Current time is in peak hours, skip cache refresh");
            return;
        }
        else
        {
            logger.info(this, "Current time is not in peak hours, begin cache refresh"); 
        }
//        if ( )
        TopazCacheableFactory[] factories = QuoteCacheBootStrape.getInstance().getCachedKeyFactories(); 
        CacheContext cc = CacheContextFactory.singleton().getCacheContext();
        //CacheAccess ca;
        try {
            logger.debug(this,"Refreshing Cache Group: QUOTE_CACHE_GROUP");
            QuoteCache cache = (QuoteCache) cc.getCache();
            cache.loadGroup("QUOTE_CACHE_GROUP",factories);
            logger.debug(this,"Cache Group: QUOTE_CACHE_GROUP has been refreshed.");
            /*
            ca = cc.getCacheAccess("QUOTE_CACHE_REGION");
            for(int i=0;i<factories.length;i++){
                TopazCacheableFactory factory = factories[i];
                if(factory == null){
                    continue;
                }
                logger.debug(this,"Refreshing Cached Object with key:"+factory.getClass().getName());
                if(ca.isPresent(factory.getClass().getName())){
                    ca.invalidate(factory.getClass().getName());
                    ca.destroy(factory.getClass().getName());
                }
                QuoteTopazCacheableFactoryHelper helper = QuoteTopazCacheableFactoryHelper.singleton();
                Map map = helper.initializeCache(factory);
                ApplicationProperties appProp = ApplicationProperties.getInstance();
                ca.put(factory.getClass().getName(),"QUOTE_CACHE_GROUP",cc.getCacheGroupConfig("QUOTE_CACHE_GROUP").getAttributes(),map);
                logger.debug(this,"Object with Key:"+factory.getClass().getName()+" has been refreshed!");
            }
            */
        } catch (CacheException e) {
            // TODO Auto-generated catch block
            logger.error(this, e);
        } /*catch (TopazException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

}
