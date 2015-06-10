package com.ibm.dsw.quote.partner.contract;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SearchPartnerByAttrContract</code> class is for partner search by
 * customer attributes.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public class SearchPartnerContract extends PartnerBaseContract {
    /* search criteria - customer name */
    private String name;

    /* search criteria - country */
    private String country;

    /* search criteria - state */
    private String state;

    private String tier1;

    private String tier2;
    
    private String authorizedPort;
    
    private String webQuoteNum;
    
    private String searchTierType;
    
    private String isSubmittedQuote;
    
    private String hasCtrldPart;
    
    private String chkMultipleProd;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        
        String[] portfolios = parameters.getParameterWithMultiValues(PartnerParamKeys.PARAM_AUTHORIZED_PORT);
        if (portfolios == null)
            authorizedPort = "";
        else {
            for (int i = 0; i < portfolios.length; i++) {
                if ("%".equals(portfolios[i])) {
                    authorizedPort = "%";
                    break;
                }
                else if (i == 0)
                    authorizedPort = portfolios[i];
                else
                    authorizedPort = authorizedPort + "," + portfolios[i];
            }
        }
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return Returns the custName.
     */
    public String getName() {
        return name;
    }

    /**
     * @param custName
     *            The custName to set.
     */
    public void setName(String custName) {
        this.name = custName;
    }

    /**
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     *            The state to set.
     */
    public void setState(String state) {
        this.state = state;
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

    public int getTierType() {
        if (StringUtils.isBlank(tier1)) {
            return 2;
        } else if (StringUtils.isBlank(tier2)) {
            return 1;
        } else {
            return 0;
        }
    }

    private String num;

    /**
     * @return Returns the custNum.
     */
    public String getNum() {
        return num;
    }

    /**
     * @param custNum
     *            The custNum to set.
     */
    public void setNum(String custNum) {
        this.num = custNum;
    }
    
    public String getAuthorizedPort() {
        return authorizedPort;
    }
    
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
    
    public String getSearchTierType() {
        return searchTierType;
    }
    
    public void setSearchTierType(String searchTierType) {
        this.searchTierType = searchTierType;
    }
    
    public String getIsSubmittedQuote() {
        return isSubmittedQuote;
    }
    
    public void setIsSubmittedQuote(String isSubmittedQuote) {
        this.isSubmittedQuote = isSubmittedQuote;
    }
    
    public String getHasCtrldPart() {
        return hasCtrldPart;
    }
    
    public void setHasCtrldPart(String hasCtrldPart) {
        this.hasCtrldPart = hasCtrldPart;
    }
    
    public boolean getHasCtrldPartAsBoolean() {
        return "true".equalsIgnoreCase(getHasCtrldPart());
    }
    /**
     * @return Returns the chkMultipleProd.
     */
    public String getChkMultipleProd() {
        return chkMultipleProd;
    }
    /**
     * @param chkMultipleProd The chkMultipleProd to set.
     */
    public void setChkMultipleProd(String chkMultipleProd) {
        this.chkMultipleProd = chkMultipleProd;
    }
}
