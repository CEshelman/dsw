package com.ibm.dsw.quote.base.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>GZIPResponseWrapper<code> class.
 *    
 * @author: qinfengc@cn.ibm.com
 * 
 * Creation date: 2008-6-30
 */

public class GZIPResponseWrapper extends HttpServletResponseWrapper {

      protected HttpServletResponse wrappedResponse = null;  
       protected ServletOutputStream stream = null;  
       protected PrintWriter writer = null;  
       
       public GZIPResponseWrapper(HttpServletResponse response) {  
         super(response);  
         wrappedResponse = response;  
       }  
       
       public ServletOutputStream createOutputStream() throws IOException {  
         return (new GZIPResponseStream(wrappedResponse));  
       }  
       
       public void finishResponse() {  
    	 final LogContext logger = LogContextFactory.singleton().getLogContext();
         try {  
           if (writer != null) {  
             writer.close();  
           } else {  
             if (stream != null) {  
               stream.close();  
             }  
           }  
         } catch (IOException e) { 
        	 logger.error(this,"finishResponse \n" + LogThrowableUtil.getStackTraceContent(e));
         }  
       }  
       
       public void flushBuffer() throws IOException {  
         stream.flush();  
       }  
       
       public ServletOutputStream getOutputStream() throws IOException {  
         if (writer != null) {  
           throw new IllegalStateException("getWriter() has already been called!");  
         }  
       
         if (stream == null)  
           stream = createOutputStream();  
         return (stream);  
       }  
       
       public PrintWriter getWriter() throws IOException {  
         if (writer != null) {  
           return (writer);  
         }  
       
         if (stream != null) {  
           throw new IllegalStateException("getOutputStream() has already been called!");  
         }  
       
         stream = createOutputStream();  
         writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));  
         return (writer);  
       }  
}
