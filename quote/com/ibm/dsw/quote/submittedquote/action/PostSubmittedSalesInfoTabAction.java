package com.ibm.dsw.quote.submittedquote.action; 

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteSalesInfoContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>PostSubmittedSalesInfoTabAction<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: 2010-7-16
 */
public class PostSubmittedSalesInfoTabAction extends BaseContractActionHandler{
    private static final int MAX_QT_TITLE_LENGTH = 75;
    static LogContext logContext = LogContextFactory.singleton().getLogContext();
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        try {
            SubmittedQuoteSalesInfoContract sqSalesInfoTabContract = (SubmittedQuoteSalesInfoContract) contract;
            
            String userId = sqSalesInfoTabContract.getUserId();
            String quoteTitle = sqSalesInfoTabContract.getQuoteTitle();

            SubmittedQuoteProcess sqProcess = SubmittedQuoteProcessFactory.singleton().create();
            sqProcess.updateSalesInfoTitle(userId, quoteTitle);
            handler.setState(StateKeys.STATE_REDIRECT_ACTION);
            return handler.getResultBean();
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        }
    }
    
    protected boolean validate(ProcessContract contract) {
        boolean valid = super.validate(contract);
        
        if (valid) {
            SubmittedQuoteSalesInfoContract postContract = (SubmittedQuoteSalesInfoContract) contract;
            String title = postContract.getQuoteTitle();
            HashMap map = new HashMap();
            if(StringUtils.isBlank(title)){
                valid = false;
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_QT_TITLE);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.BRIEF_TITLE);
                map.put(DraftQuoteParamKeys.PARAM_SI_BRIEF_TITLE, field);
            }
            if (title != null) {
                int length = title.length();
                if (length > MAX_QT_TITLE_LENGTH) {
                    valid = false;
                    FieldResult field = new FieldResult();
                    field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_BRIEF_TITLE_TOO_LONG);
                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.BRIEF_TITLE);
                    map.put(DraftQuoteParamKeys.PARAM_SI_BRIEF_TITLE, field);
                }
            }  
            addToValidationDataMap(postContract, map);
        }
        
        return valid;
    }

}
 