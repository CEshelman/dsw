package com.ibm.dsw.quote.base.action;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.jade.action.AbstractActionHandler;
import com.ibm.ead4j.jade.bean.Message;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.bean.StateBeanImpl;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This
 * <code>BaseParameterActionHandler<code> class is the base action class for paramter based action handlers
 *    
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jul 5, 2006
 */
public abstract class BaseParameterActionHandler extends AbstractActionHandler {
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    public BaseParameterActionHandler() {
        super();
    }

    public BaseParameterActionHandler(String aKey) {
        super(aKey);
    }

    /**
     * Adds common objects that are required to maintain session state
     * 
     * @param params
     *            com.ibm.ead4j.jade.util.Parameters
     * @param jadeSession
     *            com.ibm.ead4j.jade.session.JadeSession
     */
    public static void addCommonObjectsToSession(Parameters params, JadeSession session) {
    }

    /**
     * Add Biz Objects that are required to session
     * 
     * @param params
     *            com.ibm.ead4j.jade.util.Parameters
     * @param session
     *            com.ibm.ead4j.jade.session.JadeSession
     */
    protected void addBizObjectsToSession(Parameters params, JadeSession session) {
    }

    /**
     * Retrieves common objects from session and adds to parameters
     * 
     * @param params
     *            com.ibm.ead4j.jade.util.Parameters
     * @param jadeSession
     *            com.ibm.ead4j.jade.session.JadeSession
     */
    public static void retrieveCommonObjectsFromSession(Parameters params, JadeSession session) {
        if (params == null || session == null) {
            return;
        }
        try {
            Object obj = session.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
            if (obj != null) {
                params.addParameter(ParamKeys.PARAM_LOCAL, obj);
            }
            obj = getTimeZone(session);
            if (obj != null) {
                params.addParameter(ParamKeys.PARAM_TIMEZONE, obj);
            }
        } catch (Exception e) {
            logContext.error(BaseParameterActionHandler.class, e, "Exception caught retrieving common objects from session");
        }
    }

    /**
     * Retrieves Biz objects from session and adds them to parameters
     * 
     * @param params
     *            com.ibm.ead4j.jade.util.Parameters
     * @param session
     *            com.ibm.ead4j.jade.session.JadeSession
     */
    protected void retrieveBizObjectsFromSession(Parameters params, JadeSession session) {
    }

    protected String getErrorState() {
        return StateKeys.STATE_GENERAL_ERROR;
    }

    /**
     * This method must be implemented by all ActionHandlers. This method
     * provides the dynamic capability of the Framework to deal with individual
     * requests.
     * 
     * @return com.ibm.ead4j.jade.bean.ResultBean
     * @param parms
     *            com.ibm.ead4j.jade.util.Parameters
     * @param jadeSession
     *            com.ibm.ead4j.jade.session.JadeSession
     */
    public final ResultBean execute(Parameters params, JadeSession session) {
        ResultBean result = null;

        try {
            //Objects extending this can get more objects from session and add
            //to parameters table
            preExecute(params, session);

            //Retrieve common objects from session and add to parameters table
            retrieveCommonObjectsFromSession(params, session);
            
            params.addParameter(ParamKeys.PARAM_SESSION_QUOTE_USER, session.getAttribute(SessionKeys.SESSION_QUOTE_USER));

            //Retrieve Biz objects from session and add to parameters table
            retrieveBizObjectsFromSession(params, session);

            //validate request parameters
            Map validationResults = validate(params);

            //if validation returned any messages that means one or more
            //request parameters are invalid
            if ((validationResults != null) && (validationResults.size() > 0)) {
                //Create a message bean
                MessageBean validationMessagesBean = MessageBeanFactory.create();
                Iterator it = validationResults.keySet().iterator();
                while (it.hasNext()) {
                    String key = validationResults.get(it.next()).toString();
                    String messageText = getI18NString(key, getBaseName(), getLocale(params));
                    validationMessagesBean.setMessage(messageText);
                }

                //get the undo result bean from session
                ResultHandler validationErrorsResultHandler = new ResultHandler(params);
                result = validationErrorsResultHandler.getUndoResultBean();
                result.setMessageBean(validationMessagesBean);

                //End execution. Return undo result bean.
                return result;
            }

            //validation did not return any messages. Continue with execute.
            //Execute the action handler and get the ResultBean
            result = executeBiz(params);

            //Add Biz objects to session
            addBizObjectsToSession(params, session);

            //Add common objects to session
            addCommonObjectsToSession(params, session);

            //Objects extending this can add objects to session now
            postExecute(params, session);
        } catch (QuoteException e) {
            logContext.error(this, e);
            return handleError(e, params);
        } catch (ResultBeanException e) {
            logContext.error(this, e);
            QuoteException dswe = new QuoteException("error get result bean", e);
            return handleError(dswe, params);
        } catch (Exception e) {
            logContext.error(this, e);
            QuoteException dswe = new QuoteException("unknow exception in BaseParameterActionHandler.execute()", e);
            return handleError(dswe, params);
        }

        return result;
    }

    public abstract ResultBean executeBiz(Parameters params) throws QuoteException, ResultBeanException;

    public MessageBean getMessageBean(Parameters params, String msgKey) {
        MessageBean mBean = MessageBeanFactory.create();

        Message msg = ApplicationContextFactory.singleton().getApplicationContext().getMessage(msgKey);
        mBean.addMessage(msg);

        return mBean;
    }

    public ResultBean handleError(QuoteException e, Parameters params) {
        try {
            MessageBean mBean = MessageBeanFactory.create();

            String messageText = getI18NString(e.getMessageKey(), I18NBundleNames.ERROR_MESSAGE, getLocale(params));
            mBean.setMessage(messageText);

            ResultHandler resultHandler = new ResultHandler(params);
            resultHandler.setState(new StateBeanImpl(getErrorState()));
            resultHandler.setMessage(mBean);
            return resultHandler.getResultBean();
        } catch (ResultBeanException r) {
            logContext.error(this, r);
        }

        return null;
    }

    /**
     * This method is called after executeBiz(Parameters params, JadeSession
     * session) method just before returning the ResultBean. Common objects that
     * required to maintain session state have been added to JadeSession before
     * this method call. If you need to add more objects to JadeSession before
     * returning the ResultBean, do so in this method.
     * 
     * @param parms
     *            com.ibm.ead4j.jade.util.Parameters
     * @param jadeSession
     *            com.ibm.ead4j.jade.session.JadeSession
     */
    protected void postExecute(Parameters params, JadeSession session) {
    }

    /**
     * This method is called just before executeBiz(Parameters params,
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
    protected void preExecute(Parameters params, JadeSession session) {
    }

    /**
     * This method is called after preExecute(Parameters params, JadeSession
     * session) and just before executeBiz(Parameters params, JadeSession
     * session) method. Use this method to validate request parameters. If this
     * method returns a non-null map with size > 0, a message bean is created
     * and request state is set to UNDO.
     * 
     * @param parms
     *            com.ibm.ead4j.jade.util.Parameters
     */
    protected Map validate(Parameters params) {
        return null;
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

    protected String getBaseName() {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        return appCtx.getConfigParameter(FrameworkKeys.JADE_I18N_BASENAME_KEY);
    }

    protected Locale getLocale(Parameters params) {
        Locale locale = (Locale) params.getParameter(ParamKeys.PARAM_LOCAL);

        if (locale == null) {
            locale = new Locale("en", "US");
        }

        return locale;
    }
    
    /**
	 * @param session
	 * @return
	 */
	private static TimeZone getTimeZone(JadeSession session) {
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
}