/*
 * Created on 2007-5-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ThreadUtil {
    
    public static final String ACTION_RESULT_HANDLER = "ACTION_RESULT_HANDLER";
    
    public static final ThreadLocal store = new ThreadLocal();
    
    public static Object getThreadObject(String key){
        if(key == null){
            return null;
        }
        Map map = (Map)store.get();
        if(map == null){
            return null;
        }
        return map.get(key);
    }
    
    public static void setThreadObject(String key, Object object){
        if(key == null || object == null){
            return;
        }
        Map map = (Map)store.get();
        if(map == null){
            map = new HashMap();
            store.set(map);
        }
        map.put(key, object);
    }
}
