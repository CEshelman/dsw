package com.ibm.dsw.quote.draftquote.viewbean.helper;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>TopSectionViewBean</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-3
 */
public class TopSection extends PartPriceCommon {
    
    public TopSection(Quote quote)
    {
        super(quote);
    }
    /**
     * Shows SVP level block
     * 
     * @return true if it should be shown
     */
    public boolean showSVPLevel() {
        if (isPA() || isOEM())
            return true;
        return false;
    }
    
    public boolean showSVPLevelDropDown() {
        if (isPA() || isOEM())
            return true;
        return false;
    }

    /**
     * get SVP level
     * 
     * @return
     */
    public String getRQSVPLevel() {
        if (quote.getQuoteHeader().getTranPriceLevelCode() != null 
                && quote.getQuoteHeader().getTranPriceLevelCode().length() != 0) {
            return quote.getQuoteHeader().getTranPriceLevelCode();
        }
        else {
            return quote.getQuoteHeader().getVolDiscLevelCode();
        }
    }

    /**
     * Shows GSA Pricing
     * 
     * @return true if it should be shown
     */
    public boolean showGSAPricing() {
    	if (quote.getQuoteHeader().isPGSQuote()){
    		return false;
    	}
        return (isPA() || isPAE() || isOEM()) && (null != quote.getCustomer()) && quote.getCustomer().getGsaStatusFlag();

    }

    /**
     * Sets the countryPriceDisclaimerMessage
     * 
     * @param countryPriceDisclaimerMessage
     *            The countryPriceDisclaimerMessage to set.
     */
    private void setCountryPriceDisclaimerMessage(java.lang.String countryPriceDisclaimerMessage) {
        this.countryPriceDisclaimerMessage = countryPriceDisclaimerMessage;
    }

    /**
     * Gets the countryPriceDisclaimerMessage
     * 
     * @return Returns the countryPriceDisclaimerMessage.
     */
    public String getCountryPriceDisclaimerMessage() {
        return countryPriceDisclaimerMessage;
    }

    /**
     * Determines of the country price disclaimer message should be shown
     * 
     * @return true and sets the message key if applicable
     */
    public boolean showCountryPriceDisclaimerMessage() {
        if (isPA() || isPAE() || isFCT() || isPPSS() || isOEM()) {
            String countryCode = quote.getQuoteHeader().getPriceCountry().getCode3();
            if (PartPriceConstants.AUSTRALIA.equals(countryCode)) {
                setCountryPriceDisclaimerMessage(PartPriceViewKeys.CTRY_PRICE_DISCLAIMER_AUS);
                return true;
            } else if (PartPriceConstants.CHINA.equals(countryCode)) {
                setCountryPriceDisclaimerMessage(PartPriceViewKeys.CTRY_PRICE_DISCLAIMER_CHN);
                return true;
            } else if (PartPriceConstants.INDIA.equals(countryCode)) {
                setCountryPriceDisclaimerMessage(PartPriceViewKeys.CTRY_PRICE_DISCLAIMER_IND);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public boolean showRQOpenQtyMessage() {
        if (quote.getQuoteAccess().isCanEditRQ()
                && (quote.containsWebPrimaryStatus(PartPriceConstants.E0005) || (quote
                        .containsWebPrimaryStatus(PartPriceConstants.E0006)))) {
            return true;
        }
        return false;
    }
}