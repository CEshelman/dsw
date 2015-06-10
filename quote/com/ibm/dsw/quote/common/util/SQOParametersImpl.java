package com.ibm.dsw.quote.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.ParametersImpl;
import com.ibm.ead4j.jade.util.ParameterNotFoundException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>SQOParametersImpl.java</code> class is the sqo implementation of
 * EAD4J's parameter class This implementation added the function to enable
 * image button be submitted to a specific action.
 *
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Jan 26, 2007
 */
public class SQOParametersImpl extends ParametersImpl {

    private void addQuoteCookieParam(HttpServletRequest arg1) {
        Cookie cookie = QuoteCookie.findQuoteCookie(arg1);
        this.addParameter(ParamKeys.PARAM_QUOTE_COOKIE, cookie);
        this.addParameter(ParamKeys.PARAM_STATUS_TRACKER_COOKIE, QuoteCookie.findStatusTrackerCookie(arg1));
    }

    public void initialize(javax.servlet.http.HttpServletRequest req) throws ParameterNotFoundException {
        super.initialize(req);
        addQuoteCookieParam(req);
        String httpRequestMethod = req.getMethod();
        this.addParameter(ParamKeys.PARAM_HTTP_REQUEST_METHOD, httpRequestMethod);
    }

}
