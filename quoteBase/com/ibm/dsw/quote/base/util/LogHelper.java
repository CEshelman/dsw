package com.ibm.dsw.quote.base.util;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>LogHelper<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Feb 28, 2007
 */

public class LogHelper {
    
    public static String logSPCall(String sqlQuery, HashMap parms){
        StringBuffer sp = new StringBuffer();
        sp.append("Calling SP: ").append("\n");
        sp.append("\t").append(sqlQuery).append("\n");
		Iterator it = parms.keySet().iterator();
		while(it.hasNext()){
		    String key = it.next().toString();
		    sp.append("\t"+ key + ": ").append(parms.get(key)).append("\n");
		}
		return sp.toString();
    }
}
