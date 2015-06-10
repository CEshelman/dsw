package com.ibm.dsw.quote.customer.contract;

import java.util.List;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.AgreementTypeConfigFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>AgrmntBaseContract<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Oct 30, 2009
 */

public class AgrmntBaseContract extends CustomerBaseContract {
    
    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    protected String agreementType;
    protected String transSVPLevel;
    protected String govSiteType;
    protected String authrztnGroup;
    protected String isAddiSiteGovTypeDisplay;
    
    protected void verifyAgreementType() {
        if (!QuoteConstants.LOB_PA.equalsIgnoreCase(getLob()) && !QuoteConstants.LOB_CSA.equalsIgnoreCase(getLob()) )
            agreementType = "";
    }
    
    protected void verifyAgreementNumber() {
    	if(QuoteConstants.LOB_CSA.equalsIgnoreCase(getLob()) && 
    			CustomerConstants.AGRMNT_TYPE_ADDI_SITE.equalsIgnoreCase(getAgreementType())){
    		this.setLob(QuoteConstants.LOB_PA);
    	}
    	
        if (!QuoteConstants.LOB_PA.equalsIgnoreCase(getLob()) 
                || !CustomerConstants.AGRMNT_TYPE_ADDI_SITE.equalsIgnoreCase(getAgreementType())) {
            this.setAgreementNumber("");
        }
    }
    
    protected void verifyGovSiteType() {
        if (!QuoteConstants.LOB_PA.equalsIgnoreCase(getLob())) {
            govSiteType = "";
        }
        else {
            if (CustomerConstants.AGRMNT_TYPE_GOVERNMENT.equalsIgnoreCase(getAgreementType())
                    || (CustomerConstants.AGRMNT_TYPE_ADDI_SITE.equalsIgnoreCase(getAgreementType())
                            && isAddiSiteGovTypeDisplay())) {
                // keep the govSiteType value;
            }
            else {
                govSiteType = "";
            }
        }

        // Set authrztnGroup = ZFED for USA Gov. Fed. site ONLY.
        if ( "1".equals(getGovSiteType())) {
            if (CustomerConstants.COUNTRY_USA.equalsIgnoreCase(getCountry()))
                authrztnGroup = CustomerConstants.AUTHRZTN_GRP_USA_FEDERAL;
            else
                authrztnGroup = CustomerConstants.AUTHRZTN_GRP_CRB_FEDERAL;
        }
        else if ("0".equals(getGovSiteType()))
            authrztnGroup = CustomerConstants.AUTHRZTN_GRP_STATE_LOCAL;
        else
            authrztnGroup = "";
    }
    
    protected void verifyTransSVPLevel() {
        if (QuoteConstants.LOB_PA.equalsIgnoreCase(getLob())) {
            
            if (CustomerConstants.AGRMNT_TYPE_XSP.equalsIgnoreCase(getAgreementType())) {
                // keep the input form value
            }
            else if (CustomerConstants.AGRMNT_TYPE_ACADEMIC.equalsIgnoreCase(getAgreementType())
                    || CustomerConstants.AGRMNT_TYPE_GOVERNMENT.equalsIgnoreCase(getAgreementType())) {
                transSVPLevel = getSVPLevelFromConfig(getAgreementType(), getCountry(), getAuthrztnGroup());
            }
            else {
                transSVPLevel = ""; // For Addition site & Standard PA new customer, set trans svp level to empty;
            }
        }
        else
            transSVPLevel = "";
    }
    
    protected String getSVPLevelFromConfig(String agrmntType, String cntryCode, String authGroup) {
        String svpLevel = "";
        
        try {
            AgreementTypeConfigFactory factory = AgreementTypeConfigFactory.singleton();
            CacheProcess process = CacheProcessFactory.singleton().create();
            
            Country cntry = process.getCountryByCode3(cntryCode);
            String region = cntry == null ? "" : cntry.getWWRegion();
            
            logContext.debug(this, "Getting svp levels by agrmntType=" + agrmntType + " cntryCode=" + cntryCode
                    + " region=" + region + " authrztnGroup=" + authGroup);
            List svpLevels = factory.getSVPLevels(agrmntType, region, cntryCode, authGroup);
            
            if (svpLevels == null || svpLevels.size() == 0)
                svpLevel = "";
            else
                svpLevel = ((CodeDescObj) svpLevels.get(0)).getCode();
            
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
        }
        
        return svpLevel;
    }
    
    public String getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }

    public String getGovSiteType() {
        return govSiteType;
    }

    public void setGovSiteType(String govSiteType) {
        this.govSiteType = govSiteType;
    }

    public String getTransSVPLevel() {
        return transSVPLevel;
    }

    public void setTransSVPLevel(String transSVPLevel) {
        this.transSVPLevel = transSVPLevel;
    }

    public String getAuthrztnGroup() {
        return authrztnGroup;
    }

    public String getIsAddiSiteGovTypeDisplay() {
        return isAddiSiteGovTypeDisplay;
    }
    
    public void setIsAddiSiteGovTypeDisplay(String isAddiSiteGovTypeDisplay) {
        this.isAddiSiteGovTypeDisplay = isAddiSiteGovTypeDisplay;
    }
    
    public boolean isAddiSiteGovTypeDisplay() {
        return "true".equalsIgnoreCase(getIsAddiSiteGovTypeDisplay());
    }
}
