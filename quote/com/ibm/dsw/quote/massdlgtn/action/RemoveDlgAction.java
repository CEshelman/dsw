package com.ibm.dsw.quote.massdlgtn.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.massdlgtn.config.MassDlgtnKeys;
import com.ibm.dsw.quote.massdlgtn.contract.DlgtnSalesRepContract;
import com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcess;
import com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RemoveDlgAction</code> class is to handle the request for
 * removing a Delegate of a specific SalesRep
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class RemoveDlgAction extends AbstractDlgAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeMassDlgtn(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        DlgtnSalesRepContract ct = (DlgtnSalesRepContract) contract;

        try {

            MassDlgtnProcess process = MassDlgtnProcessFactory.singleton().create();

            process.removeDelegate(ct.getSalesUserId(), ct.getDelegateUserId(), ct.getUserId());

            fillDelegates(handler, ct.getSalesUserId());
            
            if (this.isSalesManager) {
                handler.addObject(MassDlgtnKeys.Params.SALES_USER_FULL_NAME, ct.getSalesFullName());
            }

        } catch (TopazException e) {

            logContext.error(this, "Remove Delegation Error :" + e.getMessage());
            throw new QuoteException(e);
        }

        handler.setState(MassDlgtnKeys.State.STATE_DISPLAY_MASS_DLGTN);

        return handler.getResultBean();
    }

}
