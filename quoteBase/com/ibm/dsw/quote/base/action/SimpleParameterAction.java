package com.ibm.dsw.quote.base.action;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SimpleParameterAction</code> class is a base implementation for simple 
 * action handlers which only displays a Jade state. Override the getState method to specify
 * the Jade state to be displayed.
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public abstract class SimpleParameterAction extends BaseParameterActionHandler {

    public ResultBean executeBiz(Parameters params) throws QuoteException, ResultBeanException {
    	
    	Object usrSession = params.getParameter(ParamKeys.PARAM_SESSION_QUOTE_USER);
    	
        ResultHandler handler = new ResultHandler(params);
        handler.setState(getState(params));

        Object eObject = getEObject(params);
        
        if (eObject != null) {
            handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, eObject);
        }
        
        handler.addObject(ParamKeys.PARAM_SESSION_QUOTE_USER, usrSession);

        return handler.getResultBean();
    }

    protected abstract Object getEObject(Parameters params) throws QuoteException;

    protected abstract String getState(Parameters params);
}
