package com.ibm.dsw.quote.common.domain;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ButtonDisplayRuleFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2008-5-6
 */

public class ButtonDisplayRuleFactory extends PortalXMLConfigReader {
    
    private static ButtonDisplayRuleFactory singleton = null;
    
    protected LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    protected ButtonDisplayRuleTreeNode rootRuleTreeNode = null;
    
    public static final String BUTTON = "display-action-button";
    public static final String LOB = "lob";
    public static final String CNTRY = "country";
    public static final String IS_MIGRATION = "isMigration";
    public static final String ACQSTN = "acqstnCode";
    public static final String IS_ELA = "isELA";
    public static final String QUOTE_TOTAL_PRICE = "quote-total-price";
    public static final String HAS_NEW_CUST = "has-new-cust";
    public static final String FULFILLMENT_SRC = "fulfillment-src";
    public static final String CURRENCY = "currency";
    public static final String SPECL_BID_AREA = "specl-bid-area";
    public static final String IS_SB = "isSB";
    public static final String QUOTE_TYPE = "quote-type";
    public static final String HAS_SAAS_PART = "has-saas-part";
    
    public static final String ORDER = "ORDER";
    public static final String SB = "SB";
    public static final String EXPORT_SPREADSHEET = "EXPORT_SPREADSHEET";
    public static final String PRE_CREDIT_CHECK = "PRE_CREDIT_CHECK";
    public static final String GOV_SITE_TYPE = "GOV_SITE_TYPE";
    public static final String REQUEST_ICN = "REQUEST_ICN";
    public static final String REGION = "region";
    public static final String PO_DRIVEN = "PO_DRIVEN";
    public static final String DISPLAY_PARTNER_ADDRESS_INPUT = "DISPLAY_PARTNER_ADDRESS_INPUT";
    public static final String COPY_FOR_PRICE_INCREASE = "COPY_FOR_PRICE_INCREASE";
    public static final String COPY_FOR_EXPIRY_DATE = "COPY_FOR_EXPIRY_DATE";
    public static final String DISPLAY_NO_PRCORPOINTS_OUTPUT = "DISPLAY_NO_PRCORPOINTS_OUTPUT";
    public static final String DISPLAY_LA_UPLIFT = "DISPLAY_LA_UPLIFT";
    public static final String DISPLAY_OEM_BID_TYPE = "DISPLAY_OEM_BID_TYPE";
    public static final String DISPLAY_GCS_URL = "DISPLAY_GCS_URL";

    public static final String DISPLAY_PRMTN = "DISPLAY_PRMTN";
    public static final String DISPLAY_CHG_OUTPUT = "DISPLAY_CHG_OUTPUT";
    
    public static final String DISPLAY_FCT_NON_STD_TERMS_CONDS = "DISPLAY_FCT_NON_STD_TERMS_CONDS";
    public static final String DISPLAY_PAO_QUESTION_DISABLED = "DISPLAY_PAO_QUESTION_DISABLED";
    public static final String PGS_QUOTE_SUBMISSION_ALLOWED_T1 = "PGS_QUOTE_SUBMISSION_ALLOWED_T1";
    public static final String PGS_QUOTE_SUBMISSION_ALLOWED_T2 = "PGS_QUOTE_SUBMISSION_ALLOWED_T2";
    
    public ButtonDisplayRuleFactory() {
        super();
        loadConfig(buildConfigFileName());
    }
    
    public boolean isSBButtonDisplay(String lob, String cntryCode, String isMigration, List acqstnCodes) {
        
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }
        
        HashMap params = new HashMap();
        params.put(BUTTON, SB);
        params.put(LOB, lob);
        params.put(CNTRY, cntryCode);
        params.put(IS_MIGRATION, isMigration);
        params.put(ACQSTN, acqstnCodes);
        
        return rootRuleTreeNode.isDisplay(params);
    }
    
    public boolean isOrderButtonDisplay(String lob, boolean ELAFlag, String cntryCode) {
        
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }
        
        HashMap params = new HashMap();
        params.put(BUTTON, ORDER);
        params.put(LOB, lob);
        params.put(IS_ELA, ELAFlag ? "1" : "0");
        params.put(CNTRY, cntryCode);
        
        return rootRuleTreeNode.isDisplay(params);
    }
    
    public boolean isExprtSprdshtButtonDisplay(String lob) {
        
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }
        
        HashMap params = new HashMap();
        params.put(BUTTON, EXPORT_SPREADSHEET);
        params.put(LOB, lob);
        
        return rootRuleTreeNode.isDisplay(params);
    }
    
    public boolean isPreCreditCheckOptionDisplay(QuoteHeader header) {
        
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }
        
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        String cntryCode = header.getCountry() == null ? "" : header.getCountry().getCode3();
        String currencyCode = header.getCurrencyCode();
        String fulfillSrc = StringUtils.isBlank(header.getFulfillmentSrc()) ? QuoteConstants.FULFILLMENT_DIRECT
                : header.getFulfillmentSrc();
        String quoteTotalPrice = String.valueOf(header.getQuotePriceTot());
        String hasNewCust = header.hasNewCustomer() ? "1" : "0";
        
        HashMap params = new HashMap();
        params.put(BUTTON, PRE_CREDIT_CHECK);
        params.put(LOB, lob);
        params.put(CNTRY, cntryCode);
        params.put(FULFILLMENT_SRC, fulfillSrc);
        params.put(CURRENCY, currencyCode);
        params.put(HAS_NEW_CUST, hasNewCust);
        params.put(QUOTE_TOTAL_PRICE, quoteTotalPrice);
        
        return rootRuleTreeNode.isDisplay(params);
    }
    
    public boolean isRquestICNOptionDisplay(QuoteHeader header) {
        
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        String region = header.getCountry().getSpecialBidAreaCode();
        String isMigration = header.isMigration() ? "1" : "0";

        HashMap params = new HashMap();
        params.put(BUTTON, REQUEST_ICN);
        params.put(LOB, lob);
        params.put(IS_MIGRATION, isMigration);
        params.put(REGION, region);

        return rootRuleTreeNode.isDisplay(params);       
    }
    
    public boolean isGovSiteTypeOptionDisplay(String cntryCode) {
        
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }
        
        HashMap params = new HashMap();
        params.put(BUTTON, GOV_SITE_TYPE);
        params.put(CNTRY, cntryCode);
        
        return rootRuleTreeNode.isDisplay(params);
    }
    
    public boolean isPODrivenDisplay(QuoteHeader header) {

        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String speclBidArea = header.getCountry().getSpecialBidAreaCode() == null ? "" : header.getCountry()
                .getSpecialBidAreaCode();
        HashMap params = new HashMap();
        params.put(BUTTON, PO_DRIVEN);
        params.put(SPECL_BID_AREA, speclBidArea);

        return rootRuleTreeNode.isDisplay(params);
    }
    
    /**
     * check "E-mail the approved special bid business partner notification to
     * the following address" input box display flag
     * 
     * @param header
     * @return
     */
    public boolean isDisplayParterAddressInput(QuoteHeader header) {

        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String cntryCode = header.getCountry() == null ? "" : header.getCountry().getCode3();
        String fulfillSrc = StringUtils.isBlank(header.getFulfillmentSrc()) ? QuoteConstants.FULFILLMENT_DIRECT
                : header.getFulfillmentSrc();
        HashMap params = new HashMap();
        params.put(BUTTON, DISPLAY_PARTNER_ADDRESS_INPUT);
        params.put(CNTRY, cntryCode);
        params.put(FULFILLMENT_SRC, fulfillSrc);
        return rootRuleTreeNode.isDisplay(params);
    } 


    // build config file absolute path
    protected String buildConfigFileName() {
        return getAbsoluteFilePath(ApplicationProperties.getInstance().getDisplayActionButtonConfigFileName());
    }

    protected void loadConfig(String fileName) {
        Element root = null;
        
        try {
            logContext.debug(this, "Loading action button display rules from file: " + fileName);
            root = getRootElement(fileName);
            rootRuleTreeNode = new ButtonDisplayRuleTreeNode(root);
            
        } catch (Exception e) {
            logContext.error(this, e, "Exception loading Quote Max Expire Days from file: " + fileName);
        }
        
        logContext.debug(this, "Finished loading Quote Max Expire Days from file: " + fileName);
        logContext.debug(this, toString());
    }

    protected void reset() {
        singleton = null;
    }
    
    public static ButtonDisplayRuleFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (ButtonDisplayRuleFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = ButtonDisplayRuleFactory.class.getName();
                Class factoryClass = Class.forName(factoryClassName);
                ButtonDisplayRuleFactory.singleton = (ButtonDisplayRuleFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteExpireDaysFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteExpireDaysFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteExpireDaysFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
    
    public boolean isDisplayCp4PrcIncr(QuoteHeader header) {

        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String region = header.getCountry() == null ? "" : header.getCountry().getSpecialBidAreaCode();
        HashMap params = new HashMap();
        params.put(BUTTON, COPY_FOR_PRICE_INCREASE);
        params.put(SPECL_BID_AREA, region);
        return rootRuleTreeNode.isDisplay(params);
    }   
    
    public boolean isDisplayNoPrcOrPointsOutput(QuoteHeader header) {

        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String region = header.getCountry() == null ? "" : header.getCountry().getSpecialBidAreaCode();
        String fulfillSrc = StringUtils.isBlank(header.getFulfillmentSrc()) ? QuoteConstants.FULFILLMENT_DIRECT
                : header.getFulfillmentSrc();
        HashMap params = new HashMap();
        params.put(BUTTON, DISPLAY_NO_PRCORPOINTS_OUTPUT);
        params.put(SPECL_BID_AREA, region);
        params.put(FULFILLMENT_SRC, fulfillSrc);
        return rootRuleTreeNode.isDisplay(params);
    }   
    
    public boolean isDisplayLAUplift(QuoteHeader header) {

        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String region = header.getCountry() == null ? "" : header.getCountry().getSpecialBidAreaCode();
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        String cntryCode = header.getCountry() == null ? "" : header.getCountry().getCode3();
        
        HashMap params = new HashMap();
        params.put(BUTTON, DISPLAY_LA_UPLIFT);
        params.put(LOB, lob);
        params.put(REGION, region);
        params.put(CNTRY, cntryCode);
        return rootRuleTreeNode.isDisplay(params);
    }   
    
    public boolean isDisplayOemBidType(QuoteHeader header) {
        
        if(header == null){
            return false;
        }

        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String speclBidArea = header.getCountry().getSpecialBidAreaCode() == null ? "" : header.getCountry()
                .getSpecialBidAreaCode();
        HashMap params = new HashMap();
        params.put(BUTTON, DISPLAY_OEM_BID_TYPE);
        params.put(SPECL_BID_AREA, speclBidArea);

        return rootRuleTreeNode.isDisplay(params);
    }
    
    public boolean isDisplayGCSURL(QuoteHeader header) {

        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String region = header.getCountry() == null ? "" : header.getCountry().getSpecialBidAreaCode();
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        
        HashMap params = new HashMap();
        params.put(BUTTON, DISPLAY_GCS_URL);
        params.put(LOB, lob);
        params.put(REGION, region);
        return rootRuleTreeNode.isDisplay(params);
    }   
    
    public boolean isDisplayPromotions(QuoteHeader header) {
        
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        String region = header.getCountry() == null ? "" : header.getCountry().getSpecialBidAreaCode();
        
        HashMap params = new HashMap();
        params.put(BUTTON, DISPLAY_PRMTN);
        params.put(LOB, lob);
        params.put(REGION, region);
        return rootRuleTreeNode.isDisplay(params);
    }
    

    public boolean isDispChgOutputOption(QuoteHeader header) {

        if(header == null){
            return false;
        }
        
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String region = header.getCountry() == null ? "" : header.getCountry().getSpecialBidAreaCode();
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        
        HashMap params = new HashMap();
        params.put(BUTTON, DISPLAY_CHG_OUTPUT);
        params.put(LOB, lob);
        params.put(REGION, region);
        return rootRuleTreeNode.isDisplay(params);
    }   
    
    public boolean isDisplayFCTNonStdTermsConds(QuoteHeader header){
        if(header == null){
            return false;
        }
        
        if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }
        
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        String acqrtnCode = header.getAcqrtnCode();
        String speclAreaCode = header.getCountry() == null ? "" : header.getCountry().getSpecialBidAreaCode();
        String quoteType = header.getQuoteTypeCode();
        
        HashMap params = new HashMap();
        params.put(BUTTON, DISPLAY_FCT_NON_STD_TERMS_CONDS);
        params.put(LOB, lob);
        params.put(ACQSTN, acqrtnCode);
        params.put(SPECL_BID_AREA, speclAreaCode);
        params.put(QUOTE_TYPE, quoteType);
        return rootRuleTreeNode.isDisplay(params);
    }
    
    public boolean isDisablePAOQuestion(QuoteHeader header){
    	if(header == null){
            return false;
        }
    	if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }
    	String lob = header.getLob() == null ? "" : header.getLob().getCode();
    	String countryCode = header.getCountry()== null ? "" :header.getCountry().getCode3();
    	HashMap params = new HashMap();
    	params.put(BUTTON, DISPLAY_PAO_QUESTION_DISABLED);
    	params.put(LOB, lob);
    	params.put(CNTRY, countryCode);
    	return rootRuleTreeNode.isDisplay(params); 
    	 
    }
    
    public boolean isPGSQuoteSubmisAllowed(QuoteHeader header,String tierFlag){
    	if(header == null){
            return false;
        }
    	if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }
    	String lob = header.getLob() == null ? "" : header.getLob().getCode();
    	String countryCode = header.getCountry()== null ? "" :header.getCountry().getCode3();
    	HashMap params = new HashMap();
    	if(tierFlag.equals(QuoteConstants.BPTierModel.BP_TIER_MODEL_ONE)){
    		params.put(BUTTON, PGS_QUOTE_SUBMISSION_ALLOWED_T1);
    	}else{
    		params.put(BUTTON, PGS_QUOTE_SUBMISSION_ALLOWED_T2);
    	}
    	params.put(LOB, lob);
    	params.put(CNTRY, countryCode);
    	return rootRuleTreeNode.isDisplay(params); 
    	
    }
    
    public boolean isDisplayCp4ExpiryDate(QuoteHeader header){
    	if (rootRuleTreeNode == null) {
            logContext.info(this, "Action button display config is not correctly initialized.");
            return false;
        }

        String region = header.getCountry() == null ? "" : header.getCountry().getSpecialBidAreaCode();
        HashMap params = new HashMap();
        params.put(BUTTON, COPY_FOR_EXPIRY_DATE);
        params.put(SPECL_BID_AREA, region);
        return rootRuleTreeNode.isDisplay(params);
    }
    
    
}
