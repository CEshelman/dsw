package com.ibm.dsw.quote.draftquote.action;

import java.util.HashMap;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteViewKeys;
import com.ibm.dsw.quote.draftquote.contract.UpdateOppOwnerContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>UpdateOppOwnerAction</code> class is action for 'update
 * opportunity owner'.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-14
 */
public class UpdateOppOwnerAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        UpdateOppOwnerContract oppOwnerContract = (UpdateOppOwnerContract) contract;
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        TransactionContextManager.singleton().begin();
        SalesRep oppOwner = null;
        try {
            oppOwner = SalesRepFactory.singleton().createSalesRep(oppOwnerContract.getOppOwnerEmailAddr());
            BluePageUser bUser = BluePagesLookup.getBluePagesInfo(oppOwner.getEmailAddress());
            oppOwner.setBluepageInformation(bUser.getCountryCode(), bUser.getFullName(), bUser.getLastName(), bUser
                    .getFirstName(), bUser.getPhoneNumber(), bUser.getFaxNumber(), null, null, oppOwner.getEmailAddress(),
                    bUser.getNotesId(), bUser.getBluePagesId());
            TransactionContextManager.singleton().commit();
        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            try {
                TransactionContextManager.singleton().rollback();
            } catch (TopazException te) {
                logContext.error(this, te, "problems raised when doing rollback ");
            }
        }

        process.updateOppOwner(oppOwnerContract.getUserId(), oppOwner);
        // redirect to sales info tab action
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB));
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#getValidationForm()
     */
    protected String getValidationForm() {
        return DraftQuoteViewKeys.UPDATE_OPP_OWNER_FORM;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.common.action.BaseActionHandlerAdapter#validate(com.ibm.ead4j.common.contract.ProcessContract)
     */
    protected boolean validate(ProcessContract contract) {
        if (!super.validate(contract)) {
            return false;
        }
        UpdateOppOwnerContract oppOwnerContract = (UpdateOppOwnerContract) contract;
        //validate whether input email is in bluepages.
        BluePageUser user = BluePagesLookup.getBluePagesInfo(oppOwnerContract.getOppOwnerEmailAddr());
        if (null == user) {
            HashMap vMap = new HashMap();
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, MessageKeys.EMAIL_ADDR_NOT_IN_BLUEPAGES);
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_DRAFT_QUOTE, MessageKeys.OPP_OWNER_EMAIL_ADDR);
            vMap.put(DraftQuoteParamKeys.PARAM_OPP_OWNER_EMAIL, fieldResult);
            addToValidationDataMap(contract, vMap);
            return false;
        } else {
            return true;
        }
    }
}
