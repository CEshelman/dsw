package com.ibm.dsw.quote.common.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.BaseI18NBundleNames;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.ead4j.common.util.GlobalContext;
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
 * This <code>ValidationMessageFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 2, 2009
 */

public class ValidationMessageFactory extends PortalXMLConfigReader {
    
    public static final String ACTION_ALL = "ALL";
    
    public static final String ACTION_SBMT_CP_APPRVD_BID = "SBMT_CP_APPRVD_BID";
    
    private static ValidationMessageFactory singleton = null;
    
    private LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    private HashMap validationMessages = null;

    public ValidationMessageFactory() {
        super();
        validationMessages = new HashMap();
        loadConfig(buildConfigFileName());
    }
    
    public static ValidationMessageFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (ValidationMessageFactory.singleton == null) {
            try {
                Class factoryClass = Class.forName(ValidationMessageFactory.class.getName());
                ValidationMessageFactory.singleton = (ValidationMessageFactory) factoryClass.newInstance();
            } catch (Exception e) {
                logCtx.error(ValidationMessageFactory.class, e, e.getMessage());
            }
        }
        return singleton;
    }
    
    protected String getDefaultBaseName() {
        return BaseI18NBundleNames.QUOTE_BASE;
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
    
    public String getValidationMessage(String reason, Locale locale, String param) {
        if (validationMessages == null) {
            logContext.debug(this, "The validation message map is not initialized.");
            return "";
        }
        
        String reasonKey = getKey(ACTION_ALL, reason);
        String messageKey = (String) validationMessages.get(reasonKey);
        
        if (StringUtils.isBlank(messageKey)) {
            logContext.debug(this, "The reason " + reason + " has no matched message.");
            return "";
        }
        
        String message = this.getI18NString(messageKey, getDefaultBaseName(), locale);
        if (param != null)
            message = StringHelper.replaceStr(message, param);
        
        return message;
    }
    
    public String getVldtnMsgForSbmtCpApprvdBid(String reason, Locale locale, String param) {
        if (validationMessages == null) {
            logContext.debug(this, "The validation message map is not initialized.");
            return "";
        }
        
        String reasonKey = getKey(ACTION_SBMT_CP_APPRVD_BID, reason);
        String messageKey = (String) validationMessages.get(reasonKey);
        
        if (StringUtils.isBlank(messageKey)) {
            reasonKey = getKey(ACTION_ALL, reason);
            messageKey = (String) validationMessages.get(reasonKey);
            
            if (StringUtils.isBlank(messageKey)) {
                logContext.debug(this, "The reason " + reason + " has no matched message.");
                return "";
            }
        }
        
        String message = this.getI18NString(messageKey, getDefaultBaseName(), locale);
        if (param != null)
            message = StringHelper.replaceStr(message, param);
        
        return message;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#buildConfigFileName()
     */
    protected String buildConfigFileName() {
        return getAbsoluteFilePath(ApplicationProperties.getInstance().getValidationMessageConfigFileName());
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#loadConfig(java.lang.String)
     */
    protected void loadConfig(String fileName) {
        
        try {
            logContext.debug(this, "Loading validation message mapping from file: " + fileName);
            Iterator actnIter = getRootElement(fileName).getChildren().iterator();
            
            while (actnIter.hasNext()) {
                Element actnElement = (Element) actnIter.next();
                String action = actnElement.getAttributeValue("value");
                Iterator msgIter = actnElement.getChildren().iterator();
                
                while (msgIter.hasNext()) {
                    Element msgElement = (Element) msgIter.next();
	                String reason = msgElement.getAttributeValue("reason");
	                String message = msgElement.getAttributeValue("message");
	                
	                String key = getKey(action, reason);
	                validationMessages.put(key, message);
                }
            }
        } catch (Exception e) {
            logContext.error(this, e, "Exception loading validation message mapping from file: " + fileName);
        }
        logContext.debug(this, "Finished loading validation message mapping from file: " + fileName);
        logContext.debug(this, toString());
    }
    
    private String getKey(String action, String reason) {
        return action + "." + reason;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#reset()
     */
    protected void reset() {
        singleton = null;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        if ((validationMessages != null) && (validationMessages.size() > 0)) {
            Iterator itr = validationMessages.keySet().iterator();
            buffer.append("\n Validation message mappings \n");

            while (itr.hasNext()) {
                String key = (String) itr.next();
                buffer.append(" reason=");
                buffer.append(key);
                buffer.append(" message=");
                buffer.append(validationMessages.get(key).toString());
                buffer.append("\n");
            }
        }

        return buffer.toString();
    }

}
