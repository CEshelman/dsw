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
 * Corporation. ("Confidential Information").
 * 
 * The <code>DlgtnEntryAction</code> class is the only entry to enter Mass
 * delegation tool, it firstly check if current user is sales manager if true
 * ,redirect to slect sales rep page, otherwise disply current user's delegates
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class DlgtnEntryAction extends AbstractDlgAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeMassDlgtn(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        DlgtnSalesRepContract ct = (DlgtnSalesRepContract) contract;

        if (this.isSalesManager) {

            handler.setState(MassDlgtnKeys.State.STATE_DISPLAY_SALES_REP_SELECTION);

        } else {
            
            //important: current user is the salesRep
            String salesUserId = ct.getCurrentUserId(); 
            
            fillDelegates(handler, salesUserId);

            handler.setState(MassDlgtnKeys.State.STATE_DISPLAY_MASS_DLGTN);

        }
        return handler.getResultBean();

    }

}
