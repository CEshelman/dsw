package com.ibm.dsw.quote.base.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
* Copyright 2007 by IBM Corporation All rights reserved.
*
* This software is the confidential and proprietary information of IBM
* Corporation. ("Confidential Information").
*
* This <code>LogThrowableUtil<code> class ...
*
* @author: wnan@cn.ibm.com
*
* Creation date: Mar 27, 2007
*/

public class LogThrowableUtil {
    
    public static String getStackTraceContent(Throwable e){
        String content = null;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        logCtx.error(LogThrowableUtil.class, e);
        e.printStackTrace(pw);
        content = sw.toString();
        try{
          sw.close();
          pw.close();
        }catch(Exception ex){
        	logCtx.error(LogThrowableUtil.class, ex.getMessage());
        }
        return e.getMessage()+"\n"+content;
    }

}
