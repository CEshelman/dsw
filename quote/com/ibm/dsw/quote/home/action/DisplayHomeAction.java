package com.ibm.dsw.quote.home.action;

import com.ibm.dsw.quote.base.config.StateKeys;
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
 * @author: doris_yuen@us.ibm.com
 * 
 * Creation date: Feb 09, 2007
 */
public class DisplayHomeAction extends QuoteRightColumnBaseAction {

    public ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        return handler.getResultBean();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.QuoteRightColumnBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return StateKeys.STATE_DISPLAY_HOME;
    }
}
