package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.Iterator;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.util.spbid.SpecialBidRuleExecuteResult;
import com.ibm.dsw.quote.common.util.spbid.SpecialBidRuleHelper;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidCondition</code> class contain the logic
 * to check the necessary attribute for special bid
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Apr 25, 2007
 */

public class SpecialBidCondition implements Serializable {

    private Quote quote;

    private boolean isGEORequireSpBid = false;
    
    private boolean isEMEADiscountRequireSpBid = false;
    
    
    private SpecialBidRuleExecuteResult specialBidRuleResult = null;

    public SpecialBidCondition(Quote quote, String userId) throws QuoteException {
        this.quote = quote;
        
        specialBidRuleResult = SpecialBidRuleHelper.executeSpecialBidRule(quote, userId);
        
        check();

    }

    private void check() {

        if (checkRegionAndGeo()) {
            isGEORequireSpBid = true;
        }

        Iterator iterator = quote.getLineItemList().iterator();
        while (iterator.hasNext()) {
            QuoteLineItem item = (QuoteLineItem) iterator.next();

            if (checkEMEADiscount(item)) {
                this.isEMEADiscountRequireSpBid = true;
            }
        }
    }


    private boolean isAPRegion() {
        Country country = quote.getQuoteHeader().getCountry();
        return country.getWWRegion().equals(QuoteConstants.REGION_APAC);
    }


    /**
     * @return Returns the hasDiscountOrOverridePrice.
     */
    public boolean hasDiscountOrOverridePrice() {
        return (specialBidRuleResult.isDiscountSpecificed() || specialBidRuleResult.isOverridePriceSpecificed());
    }
    /**
     *  If WW region is APAC, geo is AP
		If WW region is EMEA, geo is EMEA
		If WW region is Americas:
		 If country is Brazil or Mexico, geo is LA
		 All other countries, geo is AG
     * @return
     */
    private boolean checkRegionAndGeo() {
        
        Country country = quote.getQuoteHeader().getCountry();
        String geo = country.getSpecialBidAreaCode();
        String region = country.getWWRegion();
        
        if(QuoteConstants.GEO_AP.equals(geo) && QuoteConstants.REGION_APAC.equals(region)){
            return true;
        }
        if(QuoteConstants.GEO_EMEA.equals(geo) && QuoteConstants.REGION_EMEA.equals(region)){
            return true;
        }
        if(QuoteConstants.REGION_AMERICAS.equals(region)){
            if("BRA".equals(country.getCode3()) || "MEX".equals(country.getCode3())){
                return QuoteConstants.GEO_LA.equals(geo);
            }
            
            else if(QuoteConstants.GEO_AG.equals(geo)){
                return true;
            }
        }       
       
        return false;
    }

    private boolean checkEMEADiscount(QuoteLineItem item) {
        
        Country country = quote.getQuoteHeader().getCountry();
        String geo = country.getSpecialBidAreaCode();
        if (QuoteConstants.GEO_EMEA.equals(geo) && (item.getLineDiscPct() > 85.0)) {
            return true;
        }
        return false;
    }

    /**
     * @return Returns the isOverrideStartOrEndDateSet.
     */
    public boolean isOverrideStartOrEndDateSet() {
        return false;
    }

    /**
     * @return Returns the isPartGroupRequireSpBid.
     */
    public boolean isPartGroupRequireSpBid() {
        return specialBidRuleResult.isPartGroupSpid();
    }

    /**
     * @return Returns the isRestrictPart.
     */
    public boolean hasRestrictPart() {
        return specialBidRuleResult.isPartRestrict();
    }

    /**
     * @return Returns the maintInAPOverDefaultPeriod.
     */
    public boolean isMaintNotInAPOverDefaultPeriod() {
        return specialBidRuleResult.isMaintOverDefaultPeriod();
    }

    /**
     * @return Returns the isEMEADiscountRequireSpBid.
     */
    public boolean isEMEADiscountRequireSpBid() {
        return isEMEADiscountRequireSpBid;
    }

    /**
     * @return Returns the isGEORequireSpBid.
     */
    public boolean isGEORequireSpBid() {
        return isGEORequireSpBid;
    }

    /**
     * @return Returns the maintOverDefaultPeriod.
     */
    public boolean isMaintOverDefaultPeriod() {
        return specialBidRuleResult.isMaintOverDefaultPeriod();
    }
    
    /**
     * @return Returns the isResellerNotAuthorizedToPortfolio.
     */
    public boolean isResellerAuthorizedToPortfolio() {
        return specialBidRuleResult.isResellerAuthorizedToPortfolio();
    }
    
    public boolean isDiscountOverDelegation()
    {
        return specialBidRuleResult.isDiscountOverDelegation();
    }
    
    public boolean isDiscountWithNoDelegation()
    {
    	return specialBidRuleResult.isDiscountWithNoDelegation();
    }
    
    public boolean isApprovalRouteFlag()
    {
    	return specialBidRuleResult.isApprovalRouteFlag();
    }
    
    public boolean isDiscountBelowDelegation()
    {
    	return specialBidRuleResult.isDiscountBelowDelegation();
    }
    
    public boolean isLineItemBackDated()
    {
        return specialBidRuleResult.isQuoteBackDated();
    }
    
    public boolean isChannelDiscountOverriden()
    {
        return specialBidRuleResult.isChannelDiscountOverriden();
    }
    
    public SpecialBidReason getSpecialBidReason()
    {
        return this.specialBidRuleResult.getSpecialBidReason();
    }
    
    public boolean isCmprssCvrageQuote()
    {
        return specialBidRuleResult.isCmprssCvrageQuote();
    }
}
