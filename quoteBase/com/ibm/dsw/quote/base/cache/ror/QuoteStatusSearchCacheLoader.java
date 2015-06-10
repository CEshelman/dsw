package com.ibm.dsw.quote.base.cache.ror;

import java.util.Date;
import java.util.Map;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.quartz.common.CacheLoader;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
import com.ibm.ead4j.topaz.persistence.jdbc.TopazCacheableFactory;

/**
 * @author Nathan
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

public class QuoteStatusSearchCacheLoader extends CacheLoader {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.quartz.common.CacheLoader#load(java.lang.Object,
     *      java.lang.Object)
     */
public Object load(Object handle, Object arg) {
        
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        long beginMillions = new Date().getTime();
        logContext.debug(this, "Initilizing Quote Cache.........");
        if (arg != null && arg instanceof TopazCacheableFactory[]) {
            try {
                TopazCacheableFactory[] factories = (TopazCacheableFactory[]) arg;
                StatusSearchCacheableFactoryHelper helper = StatusSearchCacheableFactoryHelper.singleton();
                for (int i = 0; i < factories.length; i++) {
                    TopazCacheableFactory factory = factories[i];
                    if (factory == null) {
                        continue;
                    }
                    try {
                        TransactionContextManager.singleton().begin();
                        Map map = helper.initializeCache(factory);
                        TransactionContextManager.singleton().commit();
                        if (getCacheAccess().isPresent(factory.getClass().getName())) {
                            getCacheAccess().destroy(factory.getClass().getName());
                        }
                        getCacheAccess().put(factory.getClass().getName(), getAttributes(), map);
                    } catch (Exception ex) {
                        logContext.error(this, "Failed to initialize using factory:" + factory.getClass().getName());
                    } finally {
                        try {
                            TransactionContextManager.singleton().rollback();
                        } catch (TopazException e1) {
                            logContext.error(this, e1, "Rollback quote cache loading failed");
                        }
                    }
                }
            } catch (Exception e) {
                logContext.error(this, e, "Initilizing Quote Cache failed");
            }
        }
        logContext.debug(this, "Initilizing Quote Cache Successful");
        logContext.debug(this, "Total Initilizing Cache Seconds: " + (new Date().getTime() - beginMillions) / 1000);
        return null;
    }

}
