package com.ibm.dsw.quote.base.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>GZIPEncoding<code> class.
 *    
 * @author: qinfengc@cn.ibm.com
 * 
 * Creation date: 2008-6-30
 */

public class GZIPEncoding implements Filter {
    static final LogContext logger = LogContextFactory.singleton().getLogContext();
    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String ae = request.getHeader("Accept-Encoding");
        //logger.debug(this, "url: " + request.getRequestURL().toString());
        //logger.debug(this, "Accept Encoding: " + ae);
        //ae = "gzip,zip";
        if (ae != null && ae.indexOf("gzip") != -1) {
            GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response);
            chain.doFilter(req, wrappedResponse);
            wrappedResponse.finishResponse();
        } else {
            chain.doFilter(req, res);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub

    }

}
