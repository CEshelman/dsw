package com.ibm.dsw.quote.common.domain;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>CountrySignatureRuleFactory<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: 2010-6-11
 */
public class CountrySignatureRuleFactory extends PortalXMLConfigReader {

    private static CountrySignatureRuleFactory singleton = null;

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    private CountrySignatureRuleTreeNode rootRuleTreeNode = null;

    private CountrySignatureRuleFactory() {
        super();
        loadConfig(buildConfigFileName());
    }

    /**
     * public call this method to check if the country require signature
     * 
     * @param code2 
     *            country code2 to be checked
     * @return true/false
     */
    public boolean isRequireSignature(String code2) {

        if (StringUtils.isBlank(code2)) {
            logContext.info(this, "invalid country code for getting country signature rules config:" + code2);
            return false;
        }
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Country signature rules config is not correctly initialized.");
            return false;
        }
        return rootRuleTreeNode.isRequireSignature(code2);
    }

    /**
     * Construct the configuration file absolute path 
     * Notice:the configuration file must to be reached by server classloader
     */
    protected String buildConfigFileName() {
        String fileName = ApplicationProperties.getInstance().getSignatrueCountryConfigFileName();
        String absoluteFilePath = "";

        if (StringUtils.isBlank(fileName)) {
            logContext.info(this, "error to get the country signature configuration file name:  " + fileName);
            return absoluteFilePath;
        }
        try {
            logContext.debug(this, "Get country signature configuration file name: " + fileName);
            URL url = this.getClass().getClassLoader().getResource(fileName);
            absoluteFilePath = url.getPath();
        } catch (Exception e) {
            logContext.error(this, e, "Exception loading country signature info  from file: " + fileName);
        }
        return absoluteFilePath;
    }

    /**
     * To load the country signature configuration file
     * 
     * @param fileName
     *            the configuration file absolute path
     */
    protected void loadConfig(String fileName) {

        if (StringUtils.isBlank(fileName)) {
            logContext.info(this, "error to load the country signature configuration from file name:  " + fileName);
            return;
        }
        Element root = null;

        try {
            logContext.debug(this, "Loading country signature rules from file: " + fileName);
            root = getRootElement(fileName);
            rootRuleTreeNode = new CountrySignatureRuleTreeNode(root);

        } catch (Exception e) {
            logContext.error(this, e, "Exception loading country signature info from file: " + fileName);
        }

        logContext.debug(this, "Finished loading country signature info from file: " + fileName);

    }

    protected void reset() {
        singleton = null;
    }

    public static CountrySignatureRuleFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (CountrySignatureRuleFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = CountrySignatureRuleFactory.class.getName();
                Class factoryClass = Class.forName(factoryClassName);
                CountrySignatureRuleFactory.singleton = (CountrySignatureRuleFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(CountrySignatureRuleFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(CountrySignatureRuleFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(CountrySignatureRuleFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

    /**
     * 
     * TODO Inner class to parse xml configuration file
     *  
     */
    private static class CountrySignatureRuleTreeNode {

        private static final String NO = "no";

        private Element element = null;

        private List subNodes = new ArrayList();

        private Map cntryDetails = new HashMap();

        public CountrySignatureRuleTreeNode(Element element) {
            this.element = element;
            List children = element.getChildren();
            for (int i = 0; children != null && i < children.size(); i++) {
                Element countryEl = (Element) children.get(i);
                CountrySignatureAttr cntryAttr = new CountrySignatureAttr();
                String sapCode = StringUtils.deleteWhitespace(countryEl.getAttributeValue("sapCode"));
                cntryAttr.setSapCode(sapCode);
                cntryAttr.setSupportRegion(StringUtils.deleteWhitespace(countryEl.getAttributeValue("supportRegion")));
                cntryAttr.setSupportSubRegion(StringUtils.deleteWhitespace(countryEl
                        .getAttributeValue("supportSubRegion")));
                cntryAttr.setEUMember(Boolean.valueOf(StringUtils.deleteWhitespace(countryEl.getAttributeValue("eu")))
                        .booleanValue());

                Element signatureEl = countryEl.getChild("signature");
                cntryAttr.setRequired(StringUtils.deleteWhitespace(signatureEl.getAttributeValue("required")));

                Element nameEl = countryEl.getChild("name");
                cntryAttr.setCountryName(StringUtils.trimToEmpty(nameEl.getTextTrim()));

                subNodes.add(cntryAttr);
                cntryDetails.put(sapCode, cntryAttr);

            }
        }

        /**
         * check if the country require signature
         * 
         * @param code2
         *            country code2 to be checked
         * @return true/false
         */
        private boolean isRequireSignature(String code2) {

            if (!cntryDetails.containsKey(code2)) {
                return false;
            }
            CountrySignatureAttr cntryAttr = (CountrySignatureAttr) cntryDetails.get(code2);

            if (!NO.equalsIgnoreCase(cntryAttr.getRequired())) {
                return true;
            } else {
                return false;
            }
        }

    }
}
