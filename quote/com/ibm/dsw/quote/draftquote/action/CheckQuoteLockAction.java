package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteLockInfo;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.CheckQuoteLockContract;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
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
 * The <code>CheckQuoteLockAction</code> class is to check if others is locking the quote.
 * 
 * @author: cyxu@cn.ibm.com
 * 
 * Created on May 11, 2010
 */
public class CheckQuoteLockAction extends BaseContractActionHandler {
	transient LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected static final String OPEN_MY_CURRENT_QUOTE = "1";

    protected static final String OPEN_SAVED_QUOTE = "2";
    
    protected static final String DELETE_SAVED_QUOTE = "3";
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        CheckQuoteLockContract checkQuoteLockContract = (CheckQuoteLockContract) contract;
        String creatorId = checkQuoteLockContract.getUserId();
        User user = checkQuoteLockContract.getUser();
        QuoteUserSession salesRep = checkQuoteLockContract.getQuoteUserSession();
        QuoteLockInfo quoteLockInfo = null;

        if (user != null)
            handler.addObject(ParamKeys.PARAM_USER_OBJECT, user);
        
        if (salesRep != null)
            handler.addObject(ParamKeys.PARAM_QUOTE_USER_SESSION, salesRep);
        
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        
        quoteLockInfo = qProcess.getQuoteLockInfo(checkQuoteLockContract.getWebQuoteNum(), creatorId);
        
        if (quoteLockInfo == null) {
            logContext.info(this, "failed to get quote lock info, redirect to emptyDraftQuote.jsp page.");
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        } else if (quoteLockInfo.isLockedFlag()) {
            logContext.debug(this, "quote is currently locked by others.");
            handler.addObject(ParamKeys.PARAM_QUOTE_LOCK_INFO, quoteLockInfo);
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, this.generateRedirectURL(checkQuoteLockContract));
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_LOCKED_QUOTE);
        } else {
            logContext.debug(this, "quote is not locked by others.");
            handler.setState(StateKeys.STATE_REDIRECT_ACTION);
            handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, this.generateRedirectURL(checkQuoteLockContract));
        }
        
        return handler.getResultBean();

    }

    protected boolean validate(ProcessContract contract) {
        CheckQuoteLockContract checkQuoteLockContract = (CheckQuoteLockContract) contract;

        String quoteNum = checkQuoteLockContract.getWebQuoteNum();
        String redirectURL = checkQuoteLockContract.getRedirectURL();
        if (StringUtils.isBlank(quoteNum) || StringUtils.isBlank(redirectURL)) {
            FieldResult field = new FieldResult();
            HashMap map = new HashMap();

            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, NewQuoteMessageKeys.MSG_URL_PARAM_NOT_VALID);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, NewQuoteMessageKeys.URL_PARAM);
            map.put(NewQuoteParamKeys.URL_PARAM, field);
            addToValidationDataMap(checkQuoteLockContract, map);
            return false;
        }

        return true;
    }
    
    /**
     * To generate the redirectURL. First get the redirectURL from contract,
     * then append the additional params.
     * 
     * @param baseContract
     * @return
     */
    private String generateRedirectURL(CheckQuoteLockContract baseContract) {
        String redirectURL = baseContract.getRedirectURL();
        HashMap redirectParams = getRedirerctParams(baseContract);

        if (redirectURL != null && redirectParams != null) {
            String connector = "&";

            if (redirectURL != null && redirectURL.indexOf("?") < 0) {
                connector = "?";
            }
            StringBuffer sb = new StringBuffer(redirectURL);
            Iterator it = redirectParams.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                sb.append(connector).append(key).append("=").append((String) redirectParams.get(key));
                connector = "&";
            }
            redirectURL = sb.toString();
        }
        return redirectURL;
    }

    /**
     * If additional params are needed, override this method to return them
     * 
     * @param contract
     * @return
     */
    protected HashMap getRedirerctParams(CheckQuoteLockContract contract) {
        HashMap redirerctParams = new HashMap();;
        String buttonName = StringUtils.trimToEmpty(contract.getButtonName());
        String webQuoteNum = StringUtils.trimToEmpty(contract.getWebQuoteNum());
        if(OPEN_SAVED_QUOTE.equals(buttonName)) {
            redirerctParams.put(NewQuoteParamKeys.PARAM_QUOTE_ID,contract.getWebQuoteNum());
        } else if(DELETE_SAVED_QUOTE.equals(buttonName)) {
            redirerctParams.put(NewQuoteParamKeys.PARAM_QUOTE_ID,contract.getWebQuoteNum());
            redirerctParams.put(NewQuoteParamKeys.PARAM_OWNER_FILTER,contract.getOwnerFilter());
            redirerctParams.put(NewQuoteParamKeys.PARAM_TIME_FILTER,contract.getTimeFilter());
            redirerctParams.put(NewQuoteParamKeys.PARAM_FORM_FLAG,contract.getFormFlag());
            redirerctParams.put(NewQuoteParamKeys.PARAM_QUOTE_SORT,contract.getQuoteSort());
        }
        return redirerctParams;
    }

}
