package com.ibm.dsw.quote.base.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Msg;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.common.validator.ValidatorFactory;
import com.ibm.dsw.common.validator.ValidatorMessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ValidationHelper<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-26
 */

public class ValidationHelper {
    
    private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     * 
     */
    public ValidationHelper() {
    }
    
    public static boolean checkOneResult(ValidatorResults results, String validationForm) {
        if (results == null || StringUtils.isBlank(validationForm))
            return true;

        ValidatorResources resources = ValidatorFactory.singleton().getResources();
        Form form = resources.getForm(Locale.getDefault(), validationForm);
        Iterator propertyNames = results.getPropertyNames().iterator();

        while (propertyNames.hasNext()) {

            String propertyName = (String) propertyNames.next();
            Field field = form.getField(propertyName);
            ValidatorResult result = results.getValidatorResult(propertyName);
            Iterator keys = result.getActions();

            while (keys.hasNext()) {
                String actionName = (String) keys.next();
                if (!result.isValid(actionName))
                    return false;
            }
        }

        return true;
    }
    
    private static String getI18NString(String key, String basename, Locale locale) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();

        Object i18nValue = appCtx.getI18nValue(basename, locale, key);
        if (i18nValue instanceof String) {
            return (String) i18nValue;
        } else {
            return key;
        }
    }
    
    private static void putErrorMsgAndArgs(HashMap msgMap, String key, String msg, String[] args) {
        String vldKey = ParamKeys.PARAM_VALIDATION + key;
        String argKey = ParamKeys.PARAM_ARGUMENT + key;
        String fmtMsg = MessageFormat.format(msg, args);
        logContext.debug(ValidationHelper.class, "\t Error message will be: " + fmtMsg);
        
        msgMap.put(vldKey, fmtMsg);
        if (args != null && args.length > 0)
            msgMap.put(argKey, args[0]);
    }
    
    public static HashMap getOneFormCommValidErrMsg(ValidatorResults results, Locale locale, String validationForm) {
        ValidatorResources resources = ValidatorFactory.singleton().getResources();
        HashMap errMsg = new HashMap();
        
        if (StringUtils.isBlank(validationForm))
            return errMsg;
        
        Form form = resources.getForm(Locale.getDefault(), validationForm);
        Iterator propertyNames = results.getPropertyNames().iterator();
        
        while (form != null && propertyNames.hasNext()) {
            String propertyName = (String) propertyNames.next();
            if (!form.containsField(propertyName))
                continue;
            
            Field field = form.getField(propertyName);
            ArrayList args = new ArrayList();
            int idx = 0;
            
            while (field.getArg(idx) != null) {
                args.add(field.getArg(idx));
                idx++;
            }

            String[] sArgs = new String[args.size()];
            for (int i = 0; i < args.size(); i++) {
                Arg arg = (Arg) args.get(i);
                if (arg.isResource()) {
                    String bundleName = StringUtils.isBlank(arg.getBundle()) ? ValidatorMessageKeys.DEFAULT_RESOURCE_BUNDLE_KEY
                            : arg.getBundle();
                    String bundleKey = arg.getKey();
                    sArgs[i] = getI18NString(bundleKey, bundleName, locale);
                }
                else
                    sArgs[i] = arg.getKey();
            }

            // Get the result of validating the property.
            ValidatorResult result = results.getValidatorResult(propertyName);
            Iterator keys = result.getActions();
            
            while (keys.hasNext()) {
                String actionName = (String) keys.next();
                ValidatorAction action = resources.getValidatorAction(actionName);

                // If the result is valid, print PASSED, otherwise print FAILED
                logContext.debug(ValidationHelper.class, propertyName + "[" + actionName + "] ("
                        + (result.isValid(actionName) ? "PASSED" : "FAILED") + ")");

                if (!result.isValid(actionName)) {
                    String bundleName = ValidatorMessageKeys.DEFAULT_RESOURCE_BUNDLE_KEY;
                    String bundleKey = action.getMsg();
                    String message = getI18NString(bundleKey, bundleName, locale);

                    putErrorMsgAndArgs(errMsg, propertyName, message, sArgs);
                }
            }
        }
        
        return errMsg;
    }
    
    public static HashMap getManualValidationErrMsg(HashMap errMap, Locale locale) {
        HashMap msgMap = new HashMap();
        String bundleName = null;
        String bundleKey = null;

        Set keySet = errMap.keySet();
        Iterator iter = keySet.iterator();

        while (iter.hasNext()) {
            String sMsg = null;
            String[] sArgs = null;

            String fieldName = (String) iter.next();
            FieldResult result = (FieldResult) errMap.get(fieldName);
            Msg msg = result.getMsg();
            if (msg.isResource()) {
                bundleName = StringUtils.isBlank(msg.getBundle()) ? ValidatorMessageKeys.DEFAULT_RESOURCE_BUNDLE_KEY
                        : msg.getBundle();
                bundleKey = msg.getKey();
                sMsg = getI18NString(bundleKey, bundleName, locale);
            } else
                sMsg = msg.getKey();
            ArrayList args = result.getArgs();
            sArgs = new String[args.size()];
            for (int i = 0; i < args.size(); i++) {
                Arg arg = (Arg) args.get(i);
                if (arg.isResource()) {
                    bundleName = StringUtils.isBlank(arg.getBundle()) ? ValidatorMessageKeys.DEFAULT_RESOURCE_BUNDLE_KEY
                            : arg.getBundle();
                    bundleKey = arg.getKey();
                    sArgs[i] = getI18NString(bundleKey, bundleName, locale);
                } else
                    sArgs[i] = arg.getKey();
            }
            putErrorMsgAndArgs(msgMap, fieldName, sMsg, sArgs);
        }
        return msgMap;
    }

}
