package com.ibm.dsw.quote.base.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResults;

import com.ibm.dsw.common.validator.ValidatorFactory;
import com.ibm.dsw.common.validator.ValidatorMessageKeys;
import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.ValidationHelper;
import com.ibm.ead4j.jade.action.AbstractContractActionHandler;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.bean.StateBeanImpl;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>BaseContractActionHandler.java</code> class is the base clas for
 * contract based action handlers
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public abstract class BaseContractActionHandler extends AbstractContractActionHandler {
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected ResultBean handleInvalidValidation(ProcessContract contract, JadeSession jadeSession,
            ResultHandler handler) {
        //Set the error message to UNDO ResultBean and call UNDO
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        ResultBean resultBean;
        try {
            resultBean = getPreResultBean(contract, handler);
        } catch (ResultBeanException e) {
            throw new RuntimeException(e);
        }
        MessageBean messageBean = resultBean.getMessageBean();
        Locale locale = getLocale(jadeSession);

        Map vMap = contract.getValidationDataMap();
        HashMap errMsg = new HashMap();
        if (vMap != null) {
            ValidatorResults results = (ValidatorResults) vMap.get(ValidatorMessageKeys.VALIDATION_INVALID_KEY);
            if (results != null) {
                errMsg = getCommonValidationErrMsg(results, locale, contract);
            }
            HashMap manualResults = (HashMap) vMap.get(ValidatorMessageKeys.VALIDATION_MANUALLY_KEY);
            if (manualResults != null) {
                HashMap manualMsg = ValidationHelper.getManualValidationErrMsg(manualResults, locale);
                errMsg.putAll(manualMsg);
            }
            if (!errMsg.isEmpty()) {
                MessageBean validationMessagesBean = MessageBeanFactory.create(errMsg);
                resultBean.setMessageBean(validationMessagesBean);
            }
        }
        return resultBean;
    }

    protected ResultBean getPreResultBean(ProcessContract contract, ResultHandler handler) throws ResultBeanException {
        return handler.getUndoResultBean();
    }

    public ResultBean handleError(QuoteException e, ResultHandler handler, Locale locale) {
        try {
            
            logContext.error(this, e);
            logContext.error(this,LogThrowableUtil.getStackTraceContent(e));

            String errorText;
            handler.setState(new StateBeanImpl(getErrorState()));
            errorText = getI18NString(e.getMessageKey(), I18NBundleNames.ERROR_MESSAGE, locale);
            handler.setMessage(MessageBeanFactory.create(errorText));

            return handler.getResultBean();
        } catch (ResultBeanException r) {
            logContext.error(this, r);
        }

        return null;
    }

    protected String getErrorState() {
        return StateKeys.STATE_GENERAL_ERROR;
    }

    public ResultBean execute(ProcessContract contract, JadeSession jadeSession, ResultHandler handler) {
        ResultBean result = null;
        try {
            //Set locale to resultbean
            handler.addObject(ParamKeys.PARAM_LOCAL, getLocale(jadeSession));
            
            //set time zone to resultBean
            
            handler.addObject(ParamKeys.PARAM_TIMEZONE, getTimeZone(jadeSession));
            
            //add quoteUserSession into JadeSession
            handler.addObject(ParamKeys.PARAM_SESSION_QUOTE_USER, jadeSession.getAttribute(SessionKeys.SESSION_QUOTE_USER));
            
            //Objects extending this can get more objects from session and add
            // to contract
            preExecute(contract, jadeSession);
            preExecute(contract, jadeSession, handler);

            //Retrieve common objects from session and add to contract
            retrieveCommonObjectsFromSession(contract, jadeSession);

            //Retrieve Biz objects from session and add to contract
            retrieveBizObjectsFromSession(contract, jadeSession);

            //Execute the action handler and get the ResultBean
            result = executeBiz(contract, handler);
            
            handleForwardedMessages(contract, jadeSession, handler, result);
            saveForwardMessages(contract, jadeSession, handler, result);

            //Add Biz objects to session
            addBizObjectsToSession(contract, jadeSession);

            //Add common objects to session
            addCommonObjectsToSession(contract, jadeSession);

            //Objects extending this can add objects to session now
            postExecute(contract, jadeSession);

            //Handle messages
            handleTransferedMessages(contract, jadeSession, result);

            
        } catch (QuoteException dswe) {
            return handleError(dswe, handler, getLocale(jadeSession));
        } catch (ResultBeanException e) {
            QuoteException dswe = new QuoteException("error get result bean", e);
            return handleError(dswe, handler, getLocale(jadeSession));
        } catch (Throwable t) {
            QuoteException dswe = new QuoteException("unknow exception in Action Handler", t);
            return handleError(dswe, handler, getLocale(jadeSession));
        }

        return result;
    }
    
    private void handleForwardedMessages(ProcessContract contract, JadeSession jadeSession, ResultHandler handler, ResultBean result){
    	if(result!=null && contract instanceof QuoteBaseContract){
    		QuoteBaseContract ct = (QuoteBaseContract)contract;
    		HttpServletRequest req = ct.getHttpServletRequest();
    		MessageBean forwardedMessage = req == null ? null : (MessageBean)req.getAttribute(ParamKeys.PARAM_FWD_MSG);
    		
    		if(forwardedMessage == null || forwardedMessage.numberOfMessages() == 0){
    			logContext.debug(this, "Forward message bean has no messages.");
    			return;
    		}
    		
    		MessageBean messageBean = result.getMessageBean();
    		
    		if ( messageBean == null || messageBean.numberOfMessages() == 0 ) {
    			logContext.debug(this, "Original message bean has no messages.");
    			result.setMessageBean(forwardedMessage);
    		}
    		
    		Iterator itList = messageBean.getListIterator();
    		if (itList.hasNext()) {
    			List msgList = new ArrayList();
    			while (itList.hasNext()) {
    				msgList.add(itList.next());
    			}
    			forwardedMessage.addMessages(msgList);
    		}
    		
    		Iterator itMap = messageBean.getMapIterator();
    		while (itMap.hasNext()) {
                Object msgObject = itMap.next();
                if (msgObject instanceof com.ibm.ead4j.jade.bean.Message) {
                    com.ibm.ead4j.jade.bean.Message msg = (com.ibm.ead4j.jade.bean.Message) msgObject;
                    forwardedMessage.addKeyedMessage(msg);
                }
            }
    		
    		result.setMessageBean(forwardedMessage);
    	}
    }
    private void saveForwardMessages(ProcessContract contract, JadeSession jadeSession, ResultHandler handler, ResultBean result){
		if (result == null || result.getState() == null) {
			logContext.debug(this, "ResultBean is NULL.");
			return;
		}
    	if(result.getState().getStateAsString().equals(
    			com.ibm.dsw.quote.base.config.StateKeys.STATE_REDIRECT_ACTION)
    			&& contract instanceof QuoteBaseContract
    			&& handler.getParameters() == null ? false : 
    				(handler.getParameters().getParameter(ParamKeys.PARAM_SAVE_MSG_2_HTTP_REQ_FLAG) == null ? false : 
    					(Boolean)(handler.getParameters().getParameter(ParamKeys.PARAM_SAVE_MSG_2_HTTP_REQ_FLAG)))){
    		QuoteBaseContract ct = (QuoteBaseContract)contract;
    		HttpServletRequest req = ct.getHttpServletRequest();
    		
    		if (req != null) {
    			req.setAttribute(ParamKeys.PARAM_FWD_MSG, result.getMessageBean());
    		}
    	}
    }

    public Locale getLocale(JadeSession jadeSession) {

        Locale locale = (Locale) jadeSession.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
        if (locale == null) {
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        }
        return locale;
    }

    /**
	 * @param session
	 * @return
	 */
	private TimeZone getTimeZone(JadeSession session) {
		String timeZoneID = ParamKeys.PARAM_GMT_TIMEZONE;
		String offsetStr = (String) session.getAttribute(ParamKeys.PARAM_TIMEZONEOFFSET);
		
		if(StringUtils.isNotBlank(offsetStr)) {
			int hours = 0;
			int mins = 0;
			int offset = Integer.parseInt(offsetStr);
			String symbol = offset < 0 ? "-" : "+";
			hours = Math.abs(offset) / 60;
			mins = Math.abs(offset) % 60;
			String hoursInStr = (hours < 10) ? symbol + "0" + hours : symbol + hours;
			String minsInStr = mins < 10 ? "0" + mins : mins + "";
			timeZoneID = timeZoneID + hoursInStr +":" + minsInStr;
		}
		
		return TimeZone.getTimeZone(timeZoneID);
	}
	
    protected boolean validate(ProcessContract contract) {
        if (contract == null)
            return false;

        String[] validationForms = this.getValidationForms(contract);
        if (validationForms == null || validationForms.length == 0)
            return true;

        ValidatorResults results = null;
        boolean valid = true;

        try {
            for (int i = 0; i < validationForms.length; i++) {
                String validationForm = validationForms[i];
                if (StringUtils.isBlank(validationForm))
                    continue;

                // validate one form
                Validator validator = ValidatorFactory.singleton().getValidator(validationForm, contract);
                ValidatorResults oneResult = validator.validate();
                if (oneResult == null)
                    continue;

                // merge validate results
                boolean oneValid = ValidationHelper.checkOneResult(oneResult, validationForm);
                valid = (valid && oneValid);

                if (results == null)
                    results = oneResult;
                else
                    results.merge(oneResult);
            }

            // if invalid, put validate results into contract
            if (!valid) {
                HashMap map = new HashMap();
                map.put(ValidatorMessageKeys.VALIDATION_INVALID_KEY, results);
                contract.setValidationDataMap(map);
            }
        } catch (ValidatorException e) {
            logContext.error(this, e.getMessage());
            return false;
        }

        return valid;
    }

    public abstract ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException;

    public static void addCommonObjectsToSession(ProcessContract contract, JadeSession session) {
    }

    protected void addBizObjectsToSession(ProcessContract contract, JadeSession session) {
    }

    public static void retrieveCommonObjectsFromSession(ProcessContract contract, JadeSession session) {
    }

    protected void retrieveBizObjectsFromSession(ProcessContract contract, JadeSession session) {
    }

    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {

    }

    /**
     * This method is called after executeBiz(ProcessContract contract,
     * JadeSession session) method just before returning the ResultBean. Common
     * objects that required to maintain session state have been added to
     * JadeSession before this method call. If you need to add more objects to
     * JadeSession before returning the ResultBean, do so in this method.
     * 
     * @param parms
     *            com.ibm.ead4j.jade.util.Parameters
     * @param jadeSession
     *            com.ibm.ead4j.jade.session.JadeSession
     */
    protected void postExecute(ProcessContract contract, JadeSession session) {
    }

    /**
     * This method is called just before executeBiz(ProcessContract contract,
     * JadeSession session) Common objects that required to maintain session
     * state have been added to Paramters table before this method call. If you
     * need to get more objects from session or manipulate parameters before
     * executing the action, do so in this method.
     * 
     * @param parms
     *            com.ibm.ead4j.jade.util.Parameters
     * @param jadeSession
     *            com.ibm.ead4j.jade.session.JadeSession
     */
    protected void preExecute(ProcessContract contract, JadeSession session) {
    }
    protected void preExecute(ProcessContract contract, JadeSession session, ResultHandler handler) {
    }


    protected String getI18NString(String key, String basename, Locale locale) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();

        Object i18nValue = appCtx.getI18nValue(basename, locale, key);
        if (i18nValue instanceof String) {
            return (String) i18nValue;
        } else {
            return key;
        }
    }
    
    protected String getI18NString(String key, String basename, Locale locale,String[] params) {
        String i18nText = getI18NString(key, basename,locale);
        if(params!=null){
            i18nText = MessageFormat.format(i18nText,(String[])params);
        }
        
        return i18nText;
				
    }

    protected String getBaseName() {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        return appCtx.getConfigParameter(FrameworkKeys.JADE_I18N_BASENAME_KEY);
    }

    public HashMap getCommonValidationErrMsg(ValidatorResults results, Locale locale, ProcessContract contract) {
        ValidatorResources resources = ValidatorFactory.singleton().getResources();
        String[] validationForms = this.getValidationForms(contract);
        HashMap errMsg = new HashMap();

        if (validationForms == null || validationForms.length == 0)
            return errMsg;

        for (int i = 0; i < validationForms.length; i++) {
            String validationForm = validationForms[i];
            if (StringUtils.isBlank(validationForm))
                continue;

            HashMap oneErrMsg = ValidationHelper.getOneFormCommValidErrMsg(results, locale, validationForm);
            errMsg.putAll(oneErrMsg);
        }

        return errMsg;
    }

    public void addToValidationDataMap(ProcessContract contract, HashMap vMap) {
        HashMap dataMap = contract.getValidationDataMap();
        if (dataMap == null)
            dataMap = new HashMap();
        dataMap.put(ValidatorMessageKeys.VALIDATION_MANUALLY_KEY, vMap);
        contract.setValidationDataMap(dataMap);
    }

    //If we don't have to validate the input, use this default method.
    protected String getValidationForm() {
        return "";
    }

    protected String[] getValidationForms(ProcessContract contract) {
        return new String[] { getValidationForm() };
    }

    protected void handleTransferedMessages(ProcessContract contract, JadeSession session, ResultBean resultBean) {
        String HAS_UNDO_KEY = "TRUE";
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();

        if (context.getValueAsString(FrameworkKeys.JADE_HAS_UNDO_KEY).equalsIgnoreCase(HAS_UNDO_KEY)) {
            if (resultBean.getState().getStateAsString().equals(
                    context.getConfigParameter(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY))) {
                return;
            }
        }

        if (contract instanceof QuoteBaseContract) {
            QuoteBaseContract baseContract = (QuoteBaseContract) contract;
            String tranMsg = baseContract.getTranMessage();
            Locale locale = getLocale(session);

            parseTransferedMessages(tranMsg, locale, resultBean);
        }
    }

    protected boolean isDisplayTranMessage(String bundleName, String bundleKey) {
        return false;
    }

    private void parseTransferedMessages(String message, Locale locale, ResultBean resultBean) {
        String TRAN_MSG_SPLITTER = "##";
        String TRAN_MSG_ARG_SPLITTER = "#";

        if (StringUtils.isBlank(message))
            return;

        String[] tokens = HtmlUtil.parseParameter(message, TRAN_MSG_SPLITTER);
        int idx = 0;
        int status = 0;
        String bundleName = null;
        String bundleKey = null;
        String msgType = null;
        ArrayList fields = new ArrayList();
        MessageBean mBean = resultBean.getMessageBean();

        while (idx < tokens.length) {
            switch (status) {
            case 0:
                bundleName = tokens[idx];
                break;
            case 1:
                bundleKey = tokens[idx];
                break;
            case 2:
                msgType = tokens[idx];
                break;
            case 3:
                bundleName = StringUtils.isBlank(bundleName) ? MessageKeys.BUNDLE_APPL_I18N_QUOTE : bundleName;
                String msg = this.getI18NString(bundleKey, bundleName, locale);
                String sArgs = tokens[idx];

                if (isDisplayTranMessage(bundleName, bundleKey)) {
                    if (StringUtils.isNotBlank(sArgs)) {
                        String[] args = HtmlUtil.parseParameter(sArgs, TRAN_MSG_ARG_SPLITTER);
                        msg = MessageFormat.format(msg, args);
                    }

                    if ("0".equals(msgType))
                        mBean.addMessage(msg, MessageBeanKeys.INFO);
                    else if ("1".equals(msgType))
                        mBean.addMessage(msg, MessageBeanKeys.ERROR);
                }
                break;
            default:
                break;
            }
            idx++;
            status = idx % 4;
        }
    }
    
    public ResultBean handleInvalidHttpRequest(ProcessContract contract, ResultHandler handler) throws ResultBeanException {
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil.getURLForAction(ActionKeys.DISPLAY_QUOTE_HOME));
        return handler.getResultBean();
    }
    
    protected void addRedirectMessage(ResultHandler handler, String redirectMsg, Locale locale)
    {
        try
        {
	        if ( redirectMsg == null || redirectMsg.equals("") )
	        {
	            return;
	        }
	        logContext.debug(this, "redirectMsg: " + redirectMsg);
	        redirectMsg = java.net.URLDecoder.decode(redirectMsg, "UTF-8");
	        logContext.debug(this, "redirectMsg decode: " + redirectMsg);
	        String[] arr = redirectMsg.split(":");
	        for ( int i = 0; i < arr.length; i++ )
	        {
	            String bundle = arr[i];
	            i++;
	            String key = arr[i];
	            i++;
	            String msgType = arr[i];
	            String msg = getI18NString(key, bundle, locale);
	            logContext.debug(this, "redirect msg:bundle" + bundle + ",key=" + key + ",type=" + msgType);
	            handler.addMessage(msg, msgType);
	        }
        }
        catch ( Throwable e )
        {
           logContext.error(this, e.toString());
        }
    }
    
    
    protected void addValidatorMTMMessage(boolean isAdd,Locale locale,ResultHandler handler){
    	if(isAdd){
    		String message = getI18NString(MessageKeys.APPLNC_MTM_VALIDATION_MSG, 
                    I18NBundleNames.BASE_MESSAGES, locale);
            handler.addMessage(message, MessageBeanKeys.INFO);
    	}
    }
    
    protected void addNeedConfigMonthlySWMessage(boolean isAdd,Locale locale,ResultHandler handler){
    	if(isAdd){
    		String message = getI18NString(MessageKeys.NEED_CONFIG_MONTHLY_SW_MSG, 
                    I18NBundleNames.BASE_MESSAGES, locale);
            handler.addMessage(message, MessageBeanKeys.INFO);
    	}
    }
    
 
}
