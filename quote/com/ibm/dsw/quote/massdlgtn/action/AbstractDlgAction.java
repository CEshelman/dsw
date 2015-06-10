package com.ibm.dsw.quote.massdlgtn.action;

import is.domainx.User;

import java.util.List;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
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
 * Corporation. ("Confidential Information"). <br/><br/>
 * 
 * The <code>AbstractDlgAction</code> class overides the executeBiz method to
 * do some common work, then call abstract method executeMassDlgtn which
 * children class must implement.
 * 
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: 2007-3-12
 */
public abstract class AbstractDlgAction extends BaseContractActionHandler {

    protected boolean isSalesManager;

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        DlgtnSalesRepContract ct = (DlgtnSalesRepContract) contract;
        this.isSalesManager = ct.isSalesManager();
        // User's access level must be Update or greater
        if (!userCanDoDelegation(contract)) {
            logContext.error(this, "User can't delegate!!");
            QuoteException e = new QuoteException();
            e.setMessageKey(MessageKeys.MSG_UNAUTHORIZED_DELEGATION);
            throw e;
        }
        handler.addObject(MassDlgtnKeys.Params.IS_SALES_MANAGER, Boolean.toString(this.isSalesManager));
        return executeMassDlgtn(contract, handler);
    }

    /**
     * @param contract
     * @return
     */
    private boolean userCanDoDelegation(ProcessContract contract) {
        User user = ((QuoteBaseContract) contract).getUser();
        int accessLevel = user.getAccessLevel(QuoteConstants.APP_CODE_SQO);
        return accessLevel == QuoteConstants.ACCESS_LEVEL_SUBMITTER
                || (accessLevel == QuoteConstants.ACCESS_LEVEL_APPROVER) || (isSalesManager);
    }

    protected BluePageUser getBluePageInfo(String userId) {
        BluePageUser user = BluePagesLookup.getBluePagesInfo(userId);
        if (null == user) {
            logContext.info(this, "the sales rep is not in bluepage:" + userId);
        }
        return user;
    }

    protected void fillDelegates(ResultHandler handler, String salesUserId) throws QuoteException {

        try {

            MassDlgtnProcess process = MassDlgtnProcessFactory.singleton().create();
            List dlgs = process.getDelegates(salesUserId);

            handler.addObject(MassDlgtnKeys.Params.SALES_USER_ID, salesUserId);
            handler.addObject(MassDlgtnKeys.Params.DELEGATES_LIST, dlgs);

        } catch (TopazException e) {
            logContext.error(this, "Can't get delegates for sales rep=" + salesUserId);
            throw new QuoteException(e);
        }

    }

    public abstract ResultBean executeMassDlgtn(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException;

}
