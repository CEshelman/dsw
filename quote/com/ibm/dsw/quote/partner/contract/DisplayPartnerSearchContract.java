package com.ibm.dsw.quote.partner.contract;

import com.ibm.dsw.quote.partner.config.PartnerDBConstants;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayPartnerSearchContract</code> class is for displaying
 * partner search page.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public class DisplayPartnerSearchContract extends PartnerBaseContract {
    /* saved search criteria - customer number */
    private String savedCustNum;

    /* saved search criteria - customer name */
    private String savedCustName;

    /* saved search criteria - country */
    private String savedCountry;

    /* saved search criteria - state */
    private String savedState;

    private String tier1;

    private String tier2;

    private String savedAuthorizedPort;
    
    private String searchTierType;
    
    private String webQuoteNum;
    
    private String isSubmittedQuote;
    
    private String chkMultipleProd;

    /**
     * @return Returns the savedCountry.
     */
    public String getSavedCountry() {
        return savedCountry;
    }

    /**
     * @param savedCountry
     *            The savedCountry to set.
     */
    public void setSavedCountry(String savedCountry) {
        this.savedCountry = savedCountry;
    }

    /**
     * @return Returns the savedCustName.
     */
    public String getSavedCustName() {
        return savedCustName;
    }

    /**
     * @param savedCustName
     *            The savedCustName to set.
     */
    public void setSavedCustName(String savedCustName) {
        this.savedCustName = savedCustName;
    }

    /**
     * @return Returns the savedCustNum.
     */
    public String getSavedCustNum() {
        return savedCustNum;
    }

    /**
     * @param savedCustNum
     *            The savedCustNum to set.
     */
    public void setSavedCustNum(String savedCustNum) {
        this.savedCustNum = savedCustNum;
    }

    /**
     * @return Returns the savedState.
     */
    public String getSavedState() {
        return savedState;
    }

    /**
     * @param savedState
     *            The savedState to set.
     */
    public void setSavedState(String savedState) {
        this.savedState = savedState;
    }

    public boolean isFromSearchByNum() {
        return PartnerDBConstants.SEARCH_METHOD_BY_NUM.equals(getSearchMethod());
    }

    public boolean isFromSearchByPort() {
        return PartnerDBConstants.SEARCH_METHOD_BY_PORT.equals(getSearchMethod());
    }
    
    public boolean isNew() {
        return getSearchMethod() == null;
    }

    /**
     * @return Returns the tier1.
     */
    public String getTier1() {
        return tier1;
    }

    /**
     * @param tier1
     *            The tier1 to set.
     */
    public void setTier1(String tier1) {
        this.tier1 = tier1;
    }

    /**
     * @return Returns the tier2.
     */
    public String getTier2() {
        return tier2;
    }

    /**
     * @param tier2
     *            The tier2 to set.
     */
    public void setTier2(String tier2) {
        this.tier2 = tier2;
    }

    public String getSavedAuthorizedPort() {
        return savedAuthorizedPort;
    }

    public void setSavedAuthorizedPort(String savedAuthorizedPort) {
        this.savedAuthorizedPort = savedAuthorizedPort;
    }

    public String getSearchTierType() {
        return searchTierType;
    }
    
    public void setSearchTierType(String searchTierType) {
        this.searchTierType = searchTierType;
    }
    
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
    
    public String getIsSubmittedQuote() {
        return isSubmittedQuote;
    }
    
    public void setIsSubmittedQuote(String isSubmittedQuote) {
        this.isSubmittedQuote = isSubmittedQuote;
    }
    
    public String getChkMultipleProd() {
        return chkMultipleProd;
    }

    public void setChkMultipleProd(String chkMultipleProd) {
        this.chkMultipleProd = chkMultipleProd;
    }

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);

        if (isNew()){
            //if new, lobCode and custCnt should be loaded from parameter lob and country
            //else, auto load is enough
            this.setCustCnt(parameters.getParameterAsString("country"));
            this.setLobCode(parameters.getParameterAsString("lob"));
        }
    }    
}
