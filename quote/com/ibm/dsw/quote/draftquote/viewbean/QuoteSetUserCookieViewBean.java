package com.ibm.dsw.quote.draftquote.viewbean;

import is.domainx.User;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteSetUserCookieViewBean<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Jun 7, 2007
 */

public class QuoteSetUserCookieViewBean extends RedirectViewBean {

    private User user;
    
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        user = (User) params.getParameter(ParamKeys.PARAM_USER_OBJECT);
    }
    
    public User getUser() {
        return user;
    }
}
