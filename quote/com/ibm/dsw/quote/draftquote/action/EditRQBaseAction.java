package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.contract.EditRQContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>EditRQBaseAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 6, 2007
 */

public abstract class EditRQBaseAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        EditRQContract editRQContract = (EditRQContract) contract;
        String redirectURL = null;
        
        int validationResult = validate(editRQContract.getUser(), editRQContract.getQuoteUserSession(), editRQContract.getRenewalQuoteNum());
        
        if (validationResult == QuoteConstants.RNWLQT_VALIDATION_RESULT.SUCCESS) {
            populateQuote(editRQContract);
            //redirect to draft renewal quote cust&partner tab
            redirectURL = generateRedirectURL(editRQContract);
            redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), ParamKeys.PARAM_CUST_ACT_URL_PARAM, editRQContract.getSearchCriteriaUrlParam()).toString();
            redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), ParamKeys.PARAM_CUST_ACT_REQUESTOR, editRQContract.getRequestorEMail()).toString();
        } else {
            logContext.debug(this, "Validation failed");
            redirectURL = genGobackURL(editRQContract, validationResult);
        }

        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        logContext.debug(this, "This action will be redirected to " + redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }

    public abstract int validate(User user, QuoteUserSession quoteUserSession, String renewalQuoteNum) throws QuoteException;

    public abstract String generateRedirectURL(ProcessContract baseContract);

    protected void populateQuote(EditRQContract editRQContract) throws QuoteException {
        //Call process to populates current draft quote with renewal quote data
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        quoteProcess.populateRenewalQuote(editRQContract.getUserId(), editRQContract.getRenewalQuoteNum(), false, editRQContract.getOrignFromCustChgReqFlag());
    }

    private String genGobackURL(EditRQContract editRQContract, int errorCategory) {
        StringBuffer sb = new StringBuffer();
        String rnwlQuoteDtlURL = HtmlUtil.getURLForReporting(editRQContract.getRequestedAction());
        sb.append(rnwlQuoteDtlURL).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_QUOTE_NUM).append("=").append(editRQContract.getRenewalQuoteNum()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_P1).append("=").append(editRQContract.getP1()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_LIST_ACTION_NAME).append("=").append(editRQContract.getListActionName()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_SEARCH_ACTION_NAME).append("=").append(editRQContract.getSearchActionName()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_SEARCH_CRITERIA_URL_PARAM).append("=").append(editRQContract.getSearchCriteriaUrlParam()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_SORT_BY).append("=").append(editRQContract.getSortBy()).append("&");
        sb.append(DraftRQParamKeys.PARAM_RPT_ERROR_FLAG).append("=").append(errorCategory);
        
        return sb.toString();
    }
}
