package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteClassificationCodeFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Feb 4, 2009
 */

public class QuoteClassificationCodeFactory extends PortalXMLConfigReader {
    
    private static QuoteClassificationCodeFactory singleton = null;
    
    protected HashMap quoteClassfctnCodeMap = null;
    
    protected LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    public QuoteClassificationCodeFactory() {
        super();
        quoteClassfctnCodeMap = new HashMap();
        loadConfig(buildConfigFileName());
    }

    public CodeDescObj findByCode(String code) {
        if (StringUtils.isBlank(code))
            return null;
        
        if (quoteClassfctnCodeMap != null)
            return (CodeDescObj) quoteClassfctnCodeMap.get(code);
        
        return null;
    }
    
    public List getAllQuoteClassfctnCodes() {
        ArrayList classfctnCodes = new ArrayList();
        
        if ((quoteClassfctnCodeMap != null) && (quoteClassfctnCodeMap.size() > 0)) {
            Iterator itr = quoteClassfctnCodeMap.keySet().iterator();

            while (itr.hasNext()) {
                String key = (String) itr.next();
                classfctnCodes.add(quoteClassfctnCodeMap.get(key));
            }
        }
        
        Collections.sort(classfctnCodes);
        return classfctnCodes;
    }
    
    public static synchronized QuoteClassificationCodeFactory singleton() {
        if (singleton == null)
            singleton = new QuoteClassificationCodeFactory();
        return singleton;
    }
    
    protected String buildConfigFileName() {
        return getAbsoluteFilePath(ApplicationProperties.getInstance().getQuoteClassfctnConfigFileName());
    }
    
    protected void loadConfig(String fileName) {

        try {
            logContext.debug(this, "Loading Quote Classification Codes from file: " + fileName);
            Iterator iterator = getRootElement(fileName).getChildren().iterator();
            
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();
                String code = element.getChildTextTrim("code");
                String desc = element.getChildTextTrim("description");
                CodeDescObj_jdbc classfctnCode = new CodeDescObj_jdbc(code, desc);
                quoteClassfctnCodeMap.put(code, classfctnCode);
            }
        } catch (Exception e) {
            logContext.error(this, e, "Exception loading Quote Max Expire Days from file: " + fileName);
        }
        logContext.debug(this, "Finished loading Quote Classification Codes from file: " + fileName);
        logContext.debug(this, toString());
    }
    
    protected void reset() {
        singleton = null;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        if ((quoteClassfctnCodeMap != null) && (quoteClassfctnCodeMap.size() > 0)) {
            Iterator itr = quoteClassfctnCodeMap.keySet().iterator();
            buffer.append("\n Quote Classification Codes \n");

            while (itr.hasNext()) {
                String key = (String) itr.next();
                buffer.append(quoteClassfctnCodeMap.get(key));
                buffer.append("\n");
            }
        }

        return buffer.toString();
    }

}
