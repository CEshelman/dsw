/*
 * Created on 2007-2-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.cache.ror;

import com.ibm.dsw.quote.base.cache.ror.CacheTimerRORListener;
import com.ibm.dsw.quote.base.cache.ror.QuoteStatusSearchCacheBootStrape;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import commonj.timers.Timer;
import commonj.timers.TimerManager;
import commonj.work.Work;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RORCacheWork implements Work {
    
    private Timer timer;
    
    private TimerManager timeManager;
    
    public RORCacheWork(TimerManager tm){
        timeManager = tm;
    }

    /* (non-Javadoc)
     * @see commonj.work.Work#release()
     */
    public void release() {
        
    }

    /* (non-Javadoc)
     * @see commonj.work.Work#isDaemon()
     */
    public boolean isDaemon() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        ApplicationProperties appProp = ApplicationProperties.getInstance();
        QuoteStatusSearchCacheBootStrape.getInstance().initialize();
        //cache work for ROR DB switch
        CacheTimerRORListener RORlistener = new CacheTimerRORListener();
        timer = timeManager.schedule(RORlistener, appProp.getRORcacheCheckValidInterval(), appProp.getRORcacheCheckValidInterval());
    }
    
    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }

}
