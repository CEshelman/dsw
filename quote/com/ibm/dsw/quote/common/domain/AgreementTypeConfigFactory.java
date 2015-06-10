package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>AgreementTypeConfigFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Aug 12, 2009
 */

public class AgreementTypeConfigFactory extends PortalXMLConfigReader {
    
    private static AgreementTypeConfigFactory singleton = null;
    
    protected LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    protected AgreementTypeConfigTreeNode rootConfigTreeNode = null;
    
    public static final String CONFIG_TYPE = "config-type";
    public static final String AGREEMENT_TYPE = "agreement-type";
    public static final String REGION = "region";
    public static final String CNTRY = "country";
    public static final String AUTHRZTN_GROUP = "authrztn-group";
    public static final String PROG_MIGRTN = "prog-migrtn";
    
    public static final String SVP_LEVEL = "SVP-level";
    public static final String PROCD_FLAG = "procd-flag";

    public AgreementTypeConfigFactory() {
        super();
        loadConfig(buildConfigFileName());
    }
    
    public List getSVPLevels(String agrmntType, String region, String cntryCode, String authrztnGroup) {
        
        if (rootConfigTreeNode == null) {
            logContext.info(this, "Agreement type config is not correctly initialized.");
            return new ArrayList();
        }
        
        HashMap params = new HashMap();
        params.put(CONFIG_TYPE, SVP_LEVEL);
        params.put(AGREEMENT_TYPE, agrmntType);
        params.put(REGION, region);
        params.put(CNTRY, cntryCode);
        params.put(AUTHRZTN_GROUP, authrztnGroup);
        
        String result = rootConfigTreeNode.getResult(params);
        return parseResultAsList(result);
    }
    
    public Integer getProcdFlagByAgrmntType(String agrmntType) throws QuoteException {
        
        if (rootConfigTreeNode == null) {
            logContext.error(this, "Agreement type config is not correctly initialized.");
            throw new QuoteException("Agreement type config is not correctly initialized.");
        }
        
        Integer procdFlag = null;
        String result = null;
        HashMap params = new HashMap();
        
        params.put(CONFIG_TYPE, PROCD_FLAG);
        params.put(AGREEMENT_TYPE, agrmntType);
        
        try {
            result = rootConfigTreeNode.getResult(params);
            procdFlag = Integer.valueOf(result);
        } catch (NumberFormatException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
        
        return procdFlag;
    }
    
    
    public List getAgrmntTypeList(int agrmntTypeFlag) throws QuoteException {
        List agrmntTypeList = new ArrayList();
        
        try {
            CustomerProcess process = CustomerProcessFactory.singleton().create();
            agrmntTypeList = process.findAllAgreementTypes(agrmntTypeFlag);
            
        } catch (TopazException e) {
            throw new QuoteException(e);
        }
        
        return agrmntTypeList;
    }
    
    public List getAgrmntOptionList() throws QuoteException {
        List agrmntOptionList = new ArrayList();
        
        try {
            CustomerProcess process = CustomerProcessFactory.singleton().create();
            agrmntOptionList = process.findAllAgreementOptions();
            
        } catch (TopazException e) {
            throw new QuoteException(e);
        }
        
        return agrmntOptionList;
    }
    
    public List getPAUNAgrmntOptionList() throws QuoteException {
        List agrmntOptionList = new ArrayList();
        
        try {
            CustomerProcess process = CustomerProcessFactory.singleton().create();
            agrmntOptionList = process.findPAUNAgreementOptions();
            
        } catch (TopazException e) {
            throw new QuoteException(e);
        }
        
        return agrmntOptionList;
    }
    
    protected List parseResultAsList(String result) {
        ArrayList resultList = new ArrayList();
        
        if (StringUtils.isNotBlank(result)) {
            StringTokenizer st = new StringTokenizer(result, ",");
            
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (StringUtils.isNotBlank(token)) {
                    String prcLvlCode = token.trim();
                    String prcLvlDesc = PartPriceConfigFactory.singleton().getPriceLevelDesc(prcLvlCode);
                    CodeDescObj_jdbc cdObj = new CodeDescObj_jdbc(prcLvlCode, prcLvlDesc);
                    resultList.add(cdObj);
                }
            }
        }
        
        return resultList;
    }

    protected String buildConfigFileName() {
        return getAbsoluteFilePath(ApplicationProperties.getInstance().getCustAgreementTypeConfigFileName());
    }

    protected void loadConfig(String fileName) {
        Element root = null;
        
        try {
            logContext.debug(this, "Loading agreement types of new customers from file: " + fileName);
            root = getRootElement(fileName);
            rootConfigTreeNode = new AgreementTypeConfigTreeNode(root);
            
        } catch (Exception e) {
            logContext.error(this, e, "Exception agreement types of new customers from file: " + fileName);
        }
        
        logContext.debug(this, "Finished loading agreement types of new customers from file: " + fileName);
    }

    protected void reset() {
        singleton = null;
    }
    
    public static AgreementTypeConfigFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (AgreementTypeConfigFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = AgreementTypeConfigFactory.class.getName();
                Class factoryClass = Class.forName(factoryClassName);
                AgreementTypeConfigFactory.singleton = (AgreementTypeConfigFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(AgreementTypeConfigFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(AgreementTypeConfigFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(AgreementTypeConfigFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

}
