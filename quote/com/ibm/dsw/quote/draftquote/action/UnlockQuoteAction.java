package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.BooleanUtils;
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
import com.ibm.dsw.quote.draftquote.contract.UnlockQuoteContract;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>LoadSavedQuoteAction</code> class is to load a saved draft quote
 * into a session quote.
 * 
 * @author: cyxu@cn.ibm.com
 * 
 * Created on May 11, 2010
 */
public class UnlockQuoteAction extends BaseContractActionHandler {

    protected static final String OPEN_MY_CURRENT_QUOTE = "1";

    protected static final String OPEN_SAVED_QUOTE = "2";
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        UnlockQuoteContract unlockQuoteContract = (UnlockQuoteContract) contract;
        String creatorId = unlockQuoteContract.getUserId();
        User user = unlockQuoteContract.getUser();
        QuoteUserSession salesRep = unlockQuoteContract.getQuoteUserSession();
        QuoteLockInfo quoteLockInfo = null;

        if (user != null)
            handler.addObject(ParamKeys.PARAM_USER_OBJECT, user);
        
        if (salesRep != null)
            handler.addObject(ParamKeys.PARAM_QUOTE_USER_SESSION, salesRep);
        
        String redirectURL = generateRedirectURL(unlockQuoteContract);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, BooleanUtils.toBooleanObject(unlockQuoteContract.getForwardFlag()));
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);

        try {
            QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
            qProcess.unlockQuote(unlockQuoteContract.getWebQuoteNum(), creatorId);
            
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            throw e;
        }
        return handler.getResultBean();

    }

    protected boolean validate(ProcessContract contract) {
        UnlockQuoteContract unlockQuoteContract = (UnlockQuoteContract) contract;

        String quoteNum = unlockQuoteContract.getWebQuoteNum();
        String redirectURL = unlockQuoteContract.getRedirectURL();
        if (StringUtils.isBlank(quoteNum) || StringUtils.isBlank(redirectURL)) {
            FieldResult field = new FieldResult();
            HashMap map = new HashMap();

            field.setMsg(MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, NewQuoteMessageKeys.MSG_URL_PARAM_NOT_VALID);
            field.addArg(MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, NewQuoteMessageKeys.URL_PARAM);
            map.put(NewQuoteParamKeys.URL_PARAM, field);
            addToValidationDataMap(unlockQuoteContract, map);
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
    private String generateRedirectURL(UnlockQuoteContract baseContract) {
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
    protected HashMap getRedirerctParams(UnlockQuoteContract contract) {
        HashMap redirerctParams = null;
        String buttonName = contract.getButtonName();
        if(StringUtils.isNotBlank(buttonName)) {
            redirerctParams = new HashMap();
            if(OPEN_SAVED_QUOTE.equals(buttonName)){
                redirerctParams.put(NewQuoteParamKeys.PARAM_QUOTE_ID,contract.getWebQuoteNum());
            }
        }
        return redirerctParams;
    }

}
