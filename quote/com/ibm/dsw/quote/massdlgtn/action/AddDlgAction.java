package com.ibm.dsw.quote.massdlgtn.action;

import java.util.HashMap;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.bluepages.BluePageUser;
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
 * Copyright 2006 by IBM Corporation All rights reserved. <br/>
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). <br/><br/>
 * 
 * The <code>AddDlgAction</code> class is to handle the request for adding a
 * new Delegate of a specific SalesRep
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public class AddDlgAction extends AbstractDlgAction {

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

            // note that if a delegate already exist, the store procedure will
            // handle it.
            process.addDelegate(ct.getSalesUserId(), ct.getDelegateUserId(),ct.getUserId());
            
            fillDelegates(handler, ct.getSalesUserId());

            if (this.isSalesManager) {
                handler.addObject(MassDlgtnKeys.Params.SALES_USER_FULL_NAME, ct.getSalesFullName());
            }

        } catch (TopazException e) {

            logContext.error(this, "Add Delegation Error:" + e.getMessage());
            throw new QuoteException(e);

        }

        handler.setState(MassDlgtnKeys.State.STATE_DISPLAY_MASS_DLGTN);

        return handler.getResultBean();
    }

    protected String getValidationForm() {
        return MassDlgtnKeys.Forms.MASS_DLGTN_MAIL;
    }

    protected boolean validate(ProcessContract contract) {
        if (!super.validate(contract)) {
            return false;
        }
        DlgtnSalesRepContract ct = (DlgtnSalesRepContract) contract;

        BluePageUser user = getBluePageInfo(ct.getDelegateUserId());

        if (null == user) {
            logContext.debug(this,"the delegate user id you entered is not in bluepage:"+ct.getDelegateUserId());
            HashMap vMap = new HashMap();
            FieldResult fr = new FieldResult();
            fr.setMsg(MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, MessageKeys.EMAIL_ADDR_NOT_IN_BLUEPAGES);           
            fr.addArg(MessageKeys.BUNDLE_APPL_I18N_MASS_DLGTN, MessageKeys.DELEGATE_ID);
            vMap.put(MassDlgtnKeys.Params.DELEGATE_USER_ID, fr);
            addToValidationDataMap(contract,vMap);
            return false;

        }
        return true;

    }

}
