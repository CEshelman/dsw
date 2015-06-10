package com.ibm.dsw.quote.massdlgtn.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.massdlgtn.config.MassDlgtnKeys;
import com.ibm.dsw.quote.massdlgtn.contract.DlgtnSalesRepContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). <br/><br/>
 * 
 * The <code>ShowSalesSelectionAction</code> class is only for Sales Manager
 * who would select another sales rep, this action only show the selection page
 * 
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class ShowSalesSelectionAction extends AbstractDlgAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeMassDlgtn(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        // if current user is not sales manager, just redirect to Quote Home
        // page
        if (!this.isSalesManager) {
            handler.setState(com.ibm.dsw.quote.base.config.StateKeys.STATE_DISPLAY_HOME);
            return handler.getResultBean();
        }
        DlgtnSalesRepContract ct = (DlgtnSalesRepContract) contract;

        handler.setState(MassDlgtnKeys.State.STATE_DISPLAY_SALES_REP_SELECTION);

        return handler.getResultBean();

    }

}
