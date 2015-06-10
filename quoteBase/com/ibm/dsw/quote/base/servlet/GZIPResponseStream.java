package com.ibm.dsw.quote.base.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>GZIPResponseStream<code> class.
 *    
 * @author: qinfengc@cn.ibm.com
 * 
 * Creation date: 2008-6-30
 */

public class GZIPResponseStream extends ServletOutputStream{
     protected ByteArrayOutputStream baos = null;  
       protected GZIPOutputStream gzipstream = null;  
       protected boolean closed = false;  
       protected HttpServletResponse response = null;  
       protected ServletOutputStream output = null;  
       
       public GZIPResponseStream(HttpServletResponse response) throws IOException {  
         closed = false;  
         this.response = response;  
         this.output = response.getOutputStream();  
         baos = new ByteArrayOutputStream();  
         gzipstream = new GZIPOutputStream(baos);  
       }  
       
       public void close() throws IOException {  
         if (closed) {  
           throw new IOException("This output stream has already been closed");  
         }  
         gzipstream.finish();  
         gzipstream.flush();  
         gzipstream.close();  
       
         byte[] bytes = baos.toByteArray();  
         response.setContentLength(bytes.length);  
         response.addHeader("Content-Encoding", "gzip");  
         output.write(bytes);  
         output.flush();  
         output.close();  
         closed = true;  
       }  
       
       public void flush() throws IOException {  
         if (closed) {  
           throw new IOException("Cannot flush a closed output stream");  
         }  
         gzipstream.flush();  
       }  
       
       public void write(int b) throws IOException {  
         if (closed) {  
           throw new IOException("Cannot write to a closed output stream");  
         }  
         gzipstream.write((byte) b);  
       }  
       
       public void write(byte b[]) throws IOException {  
         write(b, 0, b.length);  
       }  
       
       public void write(byte b[], int off, int len) throws IOException {  
         if (closed) {  
           throw new IOException("Cannot write to a closed output stream");  
         }  
         gzipstream.write(b, off, len);  
       }  
       
       public boolean closed() {  
         return (this.closed);  
       }  
       
       public void reset() {  
       }  
}
