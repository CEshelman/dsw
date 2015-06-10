/*
 * Created on 2007-2-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.cache;

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
public class CacheForceRefreshWork implements Work {
    
    private Timer timer;
    
    private TimerManager timeManager;
    
    public CacheForceRefreshWork(TimerManager tm){
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
//      QuoteCacheBootStrape.getInstance().initialize();
        CacheForceRefreshTimerListener listener = new CacheForceRefreshTimerListener();
        listener.initTimeStamp();
        timer = timeManager.schedule(listener, appProp.getCacheForceRefreshInterval(), appProp.getCacheForceRefreshInterval());
    }
    
    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }

}
