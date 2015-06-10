package com.ibm.dsw.quote.base.action;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SimpleContractAction.java</code> class is a base implementation for simple 
 * action handlers which only displays a Jade state. Override the getState method to specify
 * the Jade state to be displayed.
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public abstract class SimpleContractAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.common.session.Session,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        handler.setState(getState(contract));

        Object eObject = getEObject(contract);
        if (eObject != null) {
            handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, eObject);
        }

        return handler.getResultBean();
    }

    protected abstract Object getEObject(ProcessContract contract) throws QuoteException;

    protected abstract String getState(ProcessContract contract);

}
