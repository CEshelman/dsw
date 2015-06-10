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
public class CacheWork implements Work {
    
    protected Timer timer;
    
    protected TimerManager timeManager;
    
    public CacheWork(TimerManager tm){
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
//        QuoteCacheBootStrape.getInstance().initialize();
        CacheTimerListener listener = new CacheTimerListener();
        timer = timeManager.schedule(listener, appProp.getCacheCheckValidInterval(), appProp.getCacheCheckValidInterval());
    }
    
    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }

}
