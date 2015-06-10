package com.ibm.dsw.quote.dlgtn.action;


import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.exception.RowExistingException;
import com.ibm.dsw.quote.dlgtn.config.DlgtnViewKeys;
import com.ibm.dsw.quote.dlgtn.contract.QuoteDlgtnContract;
import com.ibm.dsw.quote.dlgtn.process.QuoteDlgtnProcess;
import com.ibm.dsw.quote.dlgtn.process.QuoteDlgtnProcessFactory;
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
 * The <code>RemoveDlgAction</code>
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-3-13
 */
public class RemoveDlgAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        QuoteDlgtnContract ct = (QuoteDlgtnContract) contract;

        try {
            QuoteDlgtnProcess process = QuoteDlgtnProcessFactory.singleton().create();
            process.removeQuoteDelegation(ct.getWebQuoteNum(), ct.getUserId(), ct.getDelegateId());
        } catch (RowExistingException re) {
            logContext.error(this, "The editor does not exists :" + ct.getDelegateId());
        } catch (TopazException e) {
            logContext.error(this, "Remove Delegation Error :" + e.getMessage());
            throw new QuoteException(e);
        }

        // redirect to sales info tab action
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil.getURLForAction(ct.getTargetAction()));
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);

        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.common.action.BaseActionHandlerAdapter#validate(com.ibm.ead4j.common.contract.ProcessContract)
     */
    protected boolean validate(ProcessContract contract) {
        if (!super.validate(contract)) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#getValidationForm()
     */
    protected String getValidationForm() {
        return DlgtnViewKeys.QUOTE_DELEGATION_FORM;
    }
}