package com.ibm.dsw.quote.base.contract;

import javax.servlet.http.Cookie;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteBaseCookieContract.java</code> class
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Created on 2007-3-14
 */

public class QuoteBaseCookieContract extends QuoteBaseContract {
    protected transient Cookie sqoCookie = null;

    /**
     * @return Returns the sqoCookie.
     */
    public Cookie getSqoCookie() {
        return sqoCookie;
    }

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        sqoCookie = getCookie(parameters);
    }

    protected Cookie getCookie(Parameters parameters) {
        return (Cookie) parameters.getParameter(ParamKeys.PARAM_QUOTE_COOKIE);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append("Cookie = ").append(sqoCookie.getValue());
        return buffer.toString();
    }
}
