package com.ibm.dsw.quote.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.YTYGrowthFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.PriorYearSSPrice;
import com.ibm.dsw.quote.common.domain.QuoteOmittedLineItemVO;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract.LineItemParameter;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.util.date.PartTypeChecker.DateComparator;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2011 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>GrowthDelegationUtil<code> class.
 *    
 * @author: junqingz@cn.ibm.com
 * 
 * Creation date: January 09, 2013
 */

public class GrowthDelegationUtil {
	private static final String ELIGIBLE_REVENUE_STREAM_CODE = "RNWMNTSP";
public static final int IMPLIED_GROWTH=1;
public static final int OVERALL_GROWTH=2;


private static final String REVENUE_STREAM_OS_SUPT="OS SUPT";
private static final String REVENUE_STREAM_OSNOSPT="OSNOSPT";
private static final String REVENUE_STREAM_SUP_RNWL="SUP RNWL";

public GrowthDelegationUtil() {
}

protected static final LogContext logger = LogContextFactory.singleton().getLogContext();

//To check if display YTY growth at line item level
public static boolean isDisplayLineItemYTY(Quote quote, QuoteLineItem qli) {
    return  quote.getQuoteHeader().isSalesQuote() 
    		&& isPartedAdedFromRQ(qli)
    		&& isLobEligibleForGrowthDelegation(quote) 
    		&& isPartTypeEligibleForGrowthDelegation(qli); 
}

//To check if display YTY growth at line item level
public static boolean isCreateDefaultYTYGrowth(Quote quote, QuoteLineItem qli) {
    return   isLobEligibleForGrowthDelegation(quote)
			&& isContainEligibleRQLineItems(quote);
    		 
}

//To check if display overall YTY growth total at summary level
public static boolean isDisplayOverallYTYTotal(Quote quote) {
	boolean isDisplayOverallYTYTotal =
			isLobEligibleForGrowthDelegation(quote) 
			   && isContainEligibleRQLineItems(quote);
	return isDisplayOverallYTYTotal;
}

//To check if display implied YTY growth total at summary level
public static boolean isDisplayImpliedYTYTotal(Quote quote) {
    return onlyForPAPAE(quote) 
    		&& isContainEligibleRQLineItems(quote);
}

//Check if line item is eligible for YTY growth calculation
public static boolean isLineItemEligibleForYTYGrowth(Quote quote, QuoteLineItem qli) {
	boolean isLineItemEligibleForYTYGrowth = 
			onlyForPAPAE(quote)
    		&& isPartedAdedFromRQ(qli)
    		&& isPartTypeEligibleForGrowthDelegation(qli);
//        		&& !quote.getQuoteHeader().isBidIteratnQt()        		;
    return isLineItemEligibleForYTYGrowth;
}

public static boolean isDisplayReferenceInformation(Quote quote, QuoteLineItem qli) {
	boolean isLineItemEligibleForYTYGrowth = 
			isLobEligibleForGrowthDelegation(quote)
    		&& isContainEligibleRQLineItems(quote);
    return isLineItemEligibleForYTYGrowth;
}


//Check if line item is eligible for YTY growth calculation
public static boolean isAllowEditLineItemYTYGrowth(Quote quote, QuoteLineItem qli) {
    return quote.getQuoteHeader().isSalesQuote() 
    		&& onlyForPAPAE(quote)
    		&& isLineItemEligibleForYTYGrowth(quote, qli)
    		&& !quote.getQuoteHeader().isSubmittedQuote()
    		&& !quote.getQuoteHeader().isBidIteratnQt();
}

//Check if line item is eligible for YTY growth calculation
public static boolean isDisplayYTYStatusMessage(Quote quote, QuoteLineItem qli) {
	YTYGrowth yty = qli.getYtyGrowth();
	
	if(yty != null){
		boolean isDisplayYTYStatusMessage = isLobEligibleForGrowthDelegation(quote)
			&& isContainEligibleRQLineItems(quote)
        	&& (yty.getIncludedInImpliedYTYGrowthFlag() != 0 
    		|| yty.getIncludedInOverallYTYGrowthFlag() != 0); 
        return isDisplayYTYStatusMessage;
	}
	else{
		return false;
	}
}

public static String getYTYStatusCode(QuoteLineItem qli){
	YTYGrowth yty = qli.getYtyGrowth();
	if(qli.getYtyGrowth() != null){
		return String.valueOf(yty.getIncludedInOverallYTYGrowthFlag())+String.valueOf(yty.getIncludedInImpliedYTYGrowthFlag());
	}
	else{
		return "00";
	}
}

public static int getReasonCodeForEligibleForImpliedGrowth(QuoteLineItem qli){
	if(qli.getYtyGrowth() != null){
		return qli.getYtyGrowth().getIncludedInImpliedYTYGrowthFlag();
	}
	else{
		return 0;
	}
}

//Set the reason code of why an line item is included in implied growth
/*
 * Pricing methods
 * 	CL              Contract level pricing                
	CT              Contracted pricing                      
	EC              Exception contracted pricing            
	LP              Prior year plus                         
	OT              Other                                   
	SV              RSVP/SRP pricing                        
 * */
public static Map<Integer, Integer> getGrowthDelegationEligiblilityReasonCode(Quote quote, QuoteLineItem qli) {
    int reasonCodeForImpliedGrowth= 0;
    int reasonCodeForOveralGrowth= 0;

    if (isPartedAdedFromRQ(qli)) {
        boolean isPriorYearSSPriceAvailable = StringUtils.isNotBlank(getLPPFromLineItem(qli)) || StringUtils.isNotBlank(getOldLPPFromLineItem(qli));
        boolean isBidPriceEqualsRQPrice = isBidPriceEqualsRQPrice(qli);
        boolean isBidPriceGreaterThanRSVPSRP = isBidPriceGreaterThanRSVPSRP(qli);
        boolean isBidPriceEqualsRSVPSRP = isBidPriceEqualsRSVPSRP(qli);
        boolean isLPPEntered = isLPPEntered(qli);
        boolean pricingMethodIsRSVP = PartPriceConstants.PRICING_METHOD_RSVP_SRP.equals(qli.getRenewalPricingMethod());
        boolean pricingMethodIsPYP = PartPriceConstants.PRICING_METHOD_PYP.equals(qli.getRenewalPricingMethod());
        boolean pricingMethodIsEC = PartPriceConstants.PRICING_METHOD_EXCEPTION_CONTRACT.equals(qli.getRenewalPricingMethod());
        // bid unit price is null or equals to 0.0, and exclude overall growth and implied growth
        if(null != qli.getLocalUnitProratedDiscPrc() && qli.getLocalUnitProratedDiscPrc() == 0.0){
            reasonCodeForOveralGrowth = -12;
            reasonCodeForImpliedGrowth = -12;
         // exclude appliance parts
        }else if (qli.isApplncPart()){
            reasonCodeForOveralGrowth = -14;
            reasonCodeForImpliedGrowth = -14;
        // exclude saas parts
        }else if (qli.isSaasPart() || qli.isMonthlySoftwarePart()){
            reasonCodeForOveralGrowth = -12;
            reasonCodeForImpliedGrowth = -12;
        // can involve price increase or decline
        }else if ((isLPPEntered || !isLPPEntered && (pricingMethodIsRSVP || pricingMethodIsPYP || pricingMethodIsEC))
                && isBidPriceLessThenRSVPSRP(qli) && isPriorYearSSPriceAvailable){
            reasonCodeForOveralGrowth = 1;
            reasonCodeForImpliedGrowth = 1;
        // can involve price equals RESVPSRP or decline with pricingMethodIsRSVP
        }else if ((isLPPEntered || !isLPPEntered && pricingMethodIsRSVP)
                && isBidPriceEqualsRSVPSRP && isPriorYearSSPriceAvailable){
            reasonCodeForOveralGrowth = 2;
            reasonCodeForImpliedGrowth = -2;
        // can involve price equals RESVPSRP or decline with pricingMethodIsPYP and pricingMethodIsEC
        }else if ((isLPPEntered || !isLPPEntered && (pricingMethodIsPYP || pricingMethodIsEC))
                && isBidPriceEqualsRSVPSRP && isPriorYearSSPriceAvailable){
            reasonCodeForOveralGrowth = 3;
            reasonCodeForImpliedGrowth = 3;
        // price uplifted beyond RSVP and Cover #2 + bid price > RSVP scenario
        }else if ((isLPPEntered || !isLPPEntered && (pricingMethodIsRSVP || pricingMethodIsPYP || pricingMethodIsEC))
                && isBidPriceGreaterThanRSVPSRP && isPriorYearSSPriceAvailable){
            reasonCodeForOveralGrowth = -3;
            reasonCodeForImpliedGrowth = -3;
        // price predetermined and rep not changed price
        }else if ((isLPPEntered || !isLPPEntered && (!(pricingMethodIsRSVP || pricingMethodIsPYP || pricingMethodIsEC)))
                && isBidPriceEqualsRQPrice && isPriorYearSSPriceAvailable){
            reasonCodeForOveralGrowth = 4;
            reasonCodeForImpliedGrowth = -4;
        // price predetermined and rep changed price
        }else if ((isLPPEntered || !isLPPEntered && (!(pricingMethodIsRSVP || pricingMethodIsPYP || pricingMethodIsEC)))
                && !isBidPriceEqualsRQPrice && isPriorYearSSPriceAvailable){
            reasonCodeForOveralGrowth = 4;
            reasonCodeForImpliedGrowth = 4;
        // validate for growth delegation and lpp require entry
        }else if (!isLPPEntered && !isPriorYearSSPriceAvailable && !qli.isApplncPart()){
            reasonCodeForOveralGrowth = -6;
            reasonCodeForImpliedGrowth =-6;
        }
        if (!isLPPEntered && !(reasonCodeForImpliedGrowth < 0 && reasonCodeForOveralGrowth < 0)){
        	PriorYearSSPrice pyp = qli.getPriorYearSSPrice();
            //Check if LPP currency is different from qoute's currency
            if(null != pyp && StringUtils.isNotBlank(pyp.getPriorYrCurrncyCode()) && !quote.getQuoteHeader().getCurrencyCode().equals(pyp.getPriorYrCurrncyCode())){
                    // LPP currency is different from qoute's currency
                    reasonCodeForOveralGrowth = -7;
                    reasonCodeForImpliedGrowth = -7;
              }
        }
    }else if(isContainEligibleRQLineItems(quote) && isLobEligibleForGrowthDelegation(quote) && !GDPartsUtil.isEligibleRenewalPart(qli) || qli.isSaasPart() || qli.isMonthlySoftwarePart()){
        reasonCodeForOveralGrowth = -13;
        reasonCodeForImpliedGrowth = -13;
    //14.2 GD:renewal parts added to a license part    
    }else if(GDPartsUtil.isEligibleRenewalPart(qli)){
        reasonCodeForOveralGrowth = -15;
        reasonCodeForImpliedGrowth = -15;
    }
    
    //for eibz ticke : Notes://ltsgdb001b/85256B83004B1D94//A656E40AC16C82FD85257C61006F19E9   
    if(isExcludedRevstream(qli)){
	    reasonCodeForOveralGrowth = -16;
	    reasonCodeForImpliedGrowth = -16;
	}

    /*
     * StatusCode: combine reasonCodeForImpliedGrowth and reasonCodeForImpliedGrowth as string
     * StatusCode               Status message
     * -----------------------------------------------------------------------
     * 00               - Initial status, no message to display
     * 11,33,44         - Included in overall growth and implied growth
     * -3-3              - REMINDER: This line item exceeds RSVP/SRP price, please correct the discount or YTY growth
     * 2-2, 4-4         - Included in overall growth and excluded from implied growth
     * -6-6             - Prior S&S price requires calculated entry: Excluded from overall growth and implied growth until entered
     * -7-7             - Currency mismatch: Excluded from overall growth and implied growth
     * -12-12, -13-13    - Prior S&S price could not be calculated, but is not required for Growth delegation: Excluded from overall growth and implied growth
     * -16-16            - Excluded from overall growth and implied growth
     *
     * Notice: adding new status code will also need to add related status message in quote_en_US.properties
     */
    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    map.put(OVERALL_GROWTH,reasonCodeForOveralGrowth);
    map.put(IMPLIED_GROWTH,reasonCodeForImpliedGrowth);
	
	return map;
}

public static boolean isExcludedRevstream(QuoteLineItem qli){
	return GrowthDelegationUtil.REVENUE_STREAM_OS_SUPT.equals(qli.getRevnStrmCode()) || GrowthDelegationUtil.REVENUE_STREAM_OSNOSPT.equals(qli.getRevnStrmCode()) ||
				GrowthDelegationUtil.REVENUE_STREAM_SUP_RNWL.equals(qli.getRevnStrmCode());
}

public static boolean isNotShowRefWhenContainsExcludedRevstream(QuoteLineItem qli){
	return isExcludedRevstream(qli) && qli.getYtyGrowth() == null;
}
public static boolean isBidPriceLessThenRSVPSRP(QuoteLineItem qli) {
	if(qli.getLocalUnitProratedDiscPrc() == null
			|| qli.getRenewalRsvpPrice() == null
			|| getYtyBidUnitPrice(qli) == null){
		return false;
	}
	boolean isBidPriceLessThenRSVPSRP = getYtyBidUnitPrice(qli).doubleValue() < DecimalUtil.roundAsDouble(qli.getRenewalRsvpPrice(),2); 
	return isBidPriceLessThenRSVPSRP;
}
public static boolean isBidPriceEqualsRSVPSRP(QuoteLineItem qli) {
	if(qli.getLocalUnitProratedDiscPrc() == null
			|| qli.getRenewalRsvpPrice() == null
			|| getYtyBidUnitPrice(qli) == null){
		return false;
	}

	return Math.abs(getYtyBidUnitPrice(qli).doubleValue() - DecimalUtil.roundAsDouble(qli.getRenewalRsvpPrice(),2)) < 0.0001 ? true : false;
}

public static boolean isBidPriceGreaterThanRSVPSRP(QuoteLineItem qli) {
	if(qli.getLocalUnitProratedDiscPrc() == null
			|| qli.getRenewalRsvpPrice() == null
			|| getYtyBidUnitPrice(qli) == null){
		return false;
	}
	
	return ((getYtyBidUnitPrice(qli) > DecimalUtil.roundAsDouble(qli.getRenewalRsvpPrice(),2)) && !qli.isSetLineToRsvpSrpFlag()) ;
}

public static boolean isBidPriceEqualsRQPrice(QuoteLineItem qli) {
	Double localExtPrc = qli.getLocalExtProratedDiscPrc();
	Double origExtPrc = qli.getOrigExtPrice();
	if(localExtPrc == null || origExtPrc == null){
		return false;
	}
	if(localExtPrc.floatValue() == origExtPrc.floatValue()){
		return true;
	}
	
	return false;
}

public static boolean isLPPEntered(QuoteLineItem qli) {
	YTYGrowth ytwGrowth = qli.getYtyGrowth();
	return (ytwGrowth != null && ytwGrowth.getManualLPP() != null && StringUtils.isNotBlank(ytwGrowth.getLppMissReas()));
}

public static boolean isPartedAdedFromRQ(QuoteLineItem qli) {
	return StringUtils.isNotBlank(qli.getRenewalQuoteNum());
}

//Add for Growth delegation, to check if line item is eligible for YTY growth calculation
public static boolean isPricingMethodEligibleForGrowthDelegation(Quote quote, QuoteLineItem qli) {
	String renewalPricingMethod = qli
			.getRenewalPricingMethod();
	boolean result = (PartPriceConstants.PRICING_METHOD_RSVP_SRP.equals(renewalPricingMethod)
					|| PartPriceConstants.PRICING_METHOD_PYP.equals(renewalPricingMethod) 
					|| PartPriceConstants.PRICING_METHOD_EXCEPTION_CONTRACT.equals(renewalPricingMethod));
	return result ;
}

//Add for Growth delegation, to check if line item is eligible for YTY growth calculation
public static boolean isDisplayOverallYTYGrowthGoBtn(Quote quote) {
    return onlyForPAPAE(quote) &&
    		isContainEligibleRQLineItems(quote) &&
    		!quote.getQuoteHeader().isSubmittedQuote() &&
    		!quote.getQuoteHeader().isBidIteratnQt();
}

//Wakan, 2013-02-07
//Deprecated on 2013.02.19 because there is no * in mockup page
//	public static boolean isShowRequireMarkForReference(Quote quote, QuoteLineItem qli) {
//		return (isAllowAddingLPP(quote, qli) || isCurrencyConversionRequired(quote, qli) || isShowViewUpdateLinkForDraft(quote, qli) || isPriorPriceOverrideRequired(quote, qli));
//	}

//Wakan, 2013-02-21
public static boolean isShowAddUpdateLinkForDraft(Quote quote, QuoteLineItem qli){
	YTYGrowth yty = qli.getYtyGrowth();
	
	//if already updated price previously
	if(yty != null && yty.getManualLPP() != null){
		if(qli.isAddedToLicPart() && (yty.getYtySourceCode() != null && yty.getYtySourceCode().trim().equals("DEFAULT"))){
			return true;
		}
		return false;
	}
	
	String ytySourceCode = getYtySourceCode(qli);
	if (quote.getQuoteHeader().isSalesQuote() 
			&& ytySourceCode != null 
			&& DraftQuoteConstants.LPP_SOURCE_DEFAULT.equals(StringUtils.trim(ytySourceCode))
			&& !quote.getQuoteHeader().isBidIteratnQt())
	    return isCurrencyConvertForDraft(quote, qli) || isPriceMissingForDraft(quote, qli) || isPriceOverrideForDraft(quote, qli);
	else
		return false;
}

//Wakan: Currency convert
public static boolean isCurrencyConvertForDraft(Quote quote, QuoteLineItem qli){
	if (onlyForPAPAE(quote) && isPartTypeEligibleForGrowthDelegation(qli) && ((qli.getPriorYearSSPrice() != null) && qli.getPriorYearSSPrice().isShowPriorYearPrice())){
		if (!StringUtils.isBlank(qli.getPriorYearSSPrice().getPriorYrLocalUnitPrice12Mnths())) {
			String priorCurrency = qli.getPriorYearSSPrice().getPriorYrCurrncyCode();
			String quoteCurrency = quote.getQuoteHeader().getCurrencyCode();
			if ((!StringUtils.isBlank(priorCurrency)) && (!priorCurrency.equalsIgnoreCase(quoteCurrency)))
				return true;
		}
	}
	return false;
}

//Wakan: Price missing
public static boolean isPriceMissingForDraft(Quote quote, QuoteLineItem qli){
	if (onlyForPAPAE(quote) && isPartTypeEligibleForGrowthDelegation(qli)) {
		PriorYearSSPrice pyp = qli.getPriorYearSSPrice();
		if ((pyp != null) && pyp.isShowPriorYearPrice()){
			if(StringUtils.isBlank(pyp.getPriorYrLocalUnitPrice12Mnths())){
				return true;
			}
		}
		else if (pyp == null) {
			return true;
		}
	}
	return false;
}

//Wakan: Currency is the same, but override price value is allowed
public static boolean isPriceOverrideForDraft(Quote quote, QuoteLineItem qli){
	if (onlyForPAPAE(quote) && isPartTypeEligibleForGrowthDelegation(qli) && ((qli.getPriorYearSSPrice() != null) && qli.getPriorYearSSPrice().isShowPriorYearPrice())){
		if (!StringUtils.isBlank(qli.getPriorYearSSPrice().getPriorYrLocalUnitPrice12Mnths())) {
			String priorCurrency = qli.getPriorYearSSPrice().getPriorYrCurrncyCode();
			String quoteCurrency = quote.getQuoteHeader().getCurrencyCode();
			if ((!StringUtils.isBlank(priorCurrency)) && (priorCurrency.equalsIgnoreCase(quoteCurrency)))
				return true;
		}
	}
	return false;
}

public static String getCurrencyCodeFromLineItem(QuoteLineItem qli) {
	PriorYearSSPrice pyss = qli.getPriorYearSSPrice(); 
	if (pyss != null && (!StringUtils.isBlank(pyss.getPriorYrLocalUnitPrice12Mnths()))) {
		String priorCurrency = pyss.getPriorYrCurrncyCode();
		if (!StringUtils.isBlank(priorCurrency))
			return priorCurrency;
	}
	return "";
}

public static String getLPPFromLineItem(QuoteLineItem qli) {
		PriorYearSSPrice pyss = qli.getPriorYearSSPrice(); 
		if (pyss != null) {
			String lpp = pyss.getPriorYrLocalUnitPrice12Mnths();
			if (!StringUtils.isBlank(lpp)) {
				return lpp;
			}
		}else{
			YTYGrowth yty = qli.getYtyGrowth(); 
			if (yty != null) {
				Double lpp = yty.getSysComputedPriorPrice();
				if (lpp != null) {
					return DecimalUtil.formatTo5Number(lpp.doubleValue());
				}
			}
		}
		
	return "";
}

public static String getOldLPPFromLineItem(QuoteLineItem qli) {
	YTYGrowth ytygrowth = qli.getYtyGrowth();
	if (ytygrowth != null) {
		Double oldLpp = qli.getYtyGrowth().getManualLPP();
		if (oldLpp != null)
		    return oldLpp.toString();
	}
	return "";
}

public static boolean isShowViewUpdateLinkForDraft(Quote quote, QuoteLineItem qli){
	YTYGrowth yty = qli.getYtyGrowth();
	if(qli != null && qli.isAddedToLicPart()){
		if(qli.isAddedToLicPart() && !"DEFAULT".equals(yty.getYtySourceCode())){
			return true;
		}
	}else{
		if(yty != null && yty.getManualLPP() != null){
			
			return true;
		}
	}
	if (onlyForPAPAE(quote) && isPartTypeEligibleForGrowthDelegation(qli) && ((qli.getPriorYearSSPrice() != null) && qli.getPriorYearSSPrice().isShowPriorYearPrice())){
		String ytySourceCode = getYtySourceCode(qli);
		if (ytySourceCode != null)
			ytySourceCode = ytySourceCode.trim();
		if ((DraftQuoteConstants.LPP_POPUP_SOURCE_CURRENCY_CONVERSION.equals(ytySourceCode)
				|| DraftQuoteConstants.LPP_POPUP_SOURCE_OVRRD_LPP.equals(ytySourceCode)
				|| DraftQuoteConstants.LPP_POPUP_SOURCE_NO_LPP.equals(ytySourceCode)))
		return true;
	}
	return false;
}

public static boolean isShowViewOverridenForSubmitted(Quote quote, QuoteLineItem qli){
	String ytySourceCode = getYtySourceCode(qli);
	return ytySourceCode.equalsIgnoreCase(DraftQuoteConstants.LPP_POPUP_SOURCE_OVRRD_LPP);
}

public static boolean isShowViewManuallyEnteredForSubmitted(Quote quote, QuoteLineItem qli){
	String ytySourceCode = getYtySourceCode(qli);
	return ytySourceCode.equalsIgnoreCase(DraftQuoteConstants.LPP_POPUP_SOURCE_NO_LPP);
}

public static boolean isShowViewCurrencyConvertedForSubmitted(Quote quote, QuoteLineItem qli){
	String ytySourceCode = getYtySourceCode(qli);
	return ytySourceCode.equalsIgnoreCase(DraftQuoteConstants.LPP_POPUP_SOURCE_CURRENCY_CONVERSION);
}

//Wakan: 2013.02.21 Get YTY Source code
//Never return null. If value is not set, blank String is returned.
private static String getYtySourceCode(QuoteLineItem qli){
	YTYGrowth ytyGrowth = qli.getYtyGrowth();
	if (ytyGrowth != null) {
		String ytySourceCode = ytyGrowth.getYtySourceCode();  //TODO should change to getYtySourceCode()
		if (ytySourceCode != null)
//				return "0".equals(StringUtils.trim(ytySourceCode))?"DEFAULT":StringUtils.trim(ytySourceCode);
			return StringUtils.trim(ytySourceCode);
	}
	return "";
}

//Add for Growth delegation, to check if "Price is missing" - Add/Update link should be displayed for draft
//Wakan, 2013-01-22
//	public static boolean isAllowAddingLPP(Quote quote, QuoteLineItem qli) {
//		if (onlyForPAPAE(quote) && isPartTypeEligibleForGrowthDelegation(qli)) {
//			PriorYearSSPrice pyp = qli.getPriorYearSSPrice();
//			if ((pyp != null) && pyp.isShowPriorYearPrice()){
//				if(StringUtils.isBlank(pyp.getPriorYrLocalUnitPrice12Mnths())){
//					return true;
//				}
//			}
//			else if (pyp == null) {
//				return true;
//			}
//		}
//		return false;
//	}

//Add for Growth delegation, to check if "currency is updated" - Add/Update link should be displayed for draft, "View currency converted details" for submitted
//Wakan, 2013-01-29
//	public static boolean isCurrencyConversionRequired(Quote quote, QuoteLineItem qli) {
//		if (onlyForPAPAE(quote) && isPartTypeEligibleForGrowthDelegation(qli) && ((qli.getPriorYearSSPrice() != null) && qli.getPriorYearSSPrice().isShowPriorYearPrice())){
//			if (!StringUtils.isBlank(qli.getPriorYearSSPrice().getPriorYrLocalUnitPrice12Mnths())) {
//				String priorCurrency = qli.getPriorYearSSPrice().getPriorYrCurrncyCode();
//				String quoteCurrency = quote.getQuoteHeader().getCurrencyCode();
//				if ((!StringUtils.isBlank(priorCurrency)) && (!priorCurrency.equalsIgnoreCase(quoteCurrency)))
//					return true;
//			}
//		}
//		return false;
//	}

//Add for Growth delegation, to check if currency conversion is required
//Wakan, 2013-01-29
//	public static boolean isPriorPriceOverrideRequired(Quote quote, QuoteLineItem qli) {
//		if (onlyForPAPAE(quote) && isPartTypeEligibleForGrowthDelegation(qli) && ((qli.getPriorYearSSPrice() != null) && qli.getPriorYearSSPrice().isShowPriorYearPrice())){
//			if (!StringUtils.isBlank(qli.getPriorYearSSPrice().getPriorYrLocalUnitPrice12Mnths())) {
//				String priorCurrency = qli.getPriorYearSSPrice().getPriorYrCurrncyCode();
//				String quoteCurrency = quote.getQuoteHeader().getCurrencyCode();
//				if ((!StringUtils.isBlank(priorCurrency)) && (priorCurrency.equalsIgnoreCase(quoteCurrency)))
//					return true;
//			}
//		}
//		return true;
//	}

//Add for Growth delegation, to ensure the change is only for PAPAE
public static boolean onlyForPAPAE(Quote quote) {
    QuoteHeader quoteHeader = quote.getQuoteHeader();
	return (quote.getQuoteHeader().isSalesQuote() &&
			(quoteHeader.isPAQuote() || 
    			quoteHeader.isPAEQuote() ||
    			quoteHeader.isPAUNQuote()) 
    		&& !quoteHeader.isFCTToPAQuote() 
    		&& !quoteHeader.isOEMQuote())
    		;
}

//Add for Growth delegation, to check if eligible for growth delegation for lobs
public static boolean isLobEligibleForGrowthDelegation(Quote quote) {
    String lob = quote.getQuoteHeader().getLob().getCode();
    return QuoteConstants.LOB_PA.equals(lob) ||
    		QuoteConstants.LOB_PAE.equals(lob) ||
    		QuoteConstants.LOB_FCT.equals(lob) ||
    		QuoteConstants.LOB_PAUN.equals(lob);
}

//Add for Growth delegation, to check if display Line item YTY Growth
public static boolean isContainEligibleRQLineItems(Quote quote) {
	boolean isContainRqLineItem = false;
    List lineItems = quote.getLineItemList();
    if(lineItems != null){
        int size = lineItems.size();
		for (int i = 0; i < size; i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if(StringUtils.isNotBlank(item.getRenewalQuoteNum()) && isPartTypeEligibleForGrowthDelegation(item)){
            	isContainRqLineItem = true;
            	break;
            }
        }
   }
    return isContainRqLineItem || quote.getQuoteHeader().isContainRQLineItem(); 
}

//Add for Growth delegation, to check if display Line item YTY Growth
public static boolean isQuoteLineItemCopiedFromRQLineItem(QuoteLineItem qli) {
    return StringUtils.isNotBlank(qli.getRenewalQuoteNum());
}

//Add for Growth delegation, to check if display Line item YTY Growth
public static boolean isPartTypeEligibleForGrowthDelegation(QuoteLineItem qli) {
    return ELIGIBLE_REVENUE_STREAM_CODE.equals(qli.getRevnStrmCode()) && !qli.isApplncPart();
}

//Add for Growth delegation, to check if display related special bid
public static boolean isDisplayRelatedBid(Quote quote) {
    return onlyForPAPAE(quote)
    		&& isContainEligibleRQLineItems(quote)
    		&& !quote.getQuoteHeader().isBidIteratnQt();
}

//Add for Growth delegation, to check if display RSVP/SRP pricing only flag
public static boolean isDisplayRSVPSVPPricingFlag(Quote quote) {
    boolean isDisplayRSVPSVPPricingFlag = 
    		isLobEligibleForGrowthDelegation(quote)
    		&& (isContainEligibleRQLineItems(quote) 
//        				|| StringUtils.isNotBlank(quote.getQuoteHeader().getRenwlQuoteNum()) 
    				|| quote.getQuoteHeader().isContainRQLineItem()); //Already checked revenue stream code in SP

    return isDisplayRSVPSVPPricingFlag;
}	

//this method called only when the current quote is full growth delegation
public static boolean isPartialRenewal(Quote quote){
	return isPartialRenewalForItemOmitted(quote) || isPartialRenewalForQuantityLowered(quote);
}

public static boolean isPartialRenewalForItemOmitted(Quote quote) {
	return quote.getQuoteHeader().isOmittedLine();
}

public static boolean isPartialRenewalForQuantityLowered(Quote quote) {
	List lineItems = quote.getLineItemList();
	
	if(lineItems == null || lineItems.size() == 0){
		return false;
	}
	
	boolean qtyLowered = false;
	int rqItemsCount = 0;
	int rsvpPricedItemsCount = 0;
	
	for(int i = 0; i < lineItems.size(); i++){
		QuoteLineItem qli = (QuoteLineItem)lineItems.get(i);
		if(qli.isReferenceToRenewalQuote()){
			rqItemsCount++;
			
			if(qli.getPartQty() != null && qli.getPartQty() < qli.getOpenQty()){
				qtyLowered = true;
			}
			
			if(PartPriceConstants.PRICING_METHOD_RSVP_SRP.equals(qli.getRenewalPricingMethod())
					|| qli.isSetLineToRsvpSrpFlag()){
				rsvpPricedItemsCount++;
			}
		}
	}
	
	if(qtyLowered){ // has quantity lowered
		if(rqItemsCount == rsvpPricedItemsCount){ 
			//all renewal parts are RSVP priced or reset to RSVP
			return false;
		} else {
			//some of the parts are not RSVP priced
			return true;
		}
	} else {
		//no part has quantity decrease at all, return false
		return false;
	}
}

private static boolean isPartialRenewalSpBid(Quote quote){
	List lineItems = quote.getLineItemList();
	
	QuoteHeader header = quote.getQuoteHeader();
	if(!header.isRSVPSRPOnly()){
		 
		if(lineItems == null || lineItems.size() == 0){
			return false;
		}
		int pypCount=0;
		int RSVPSRPCount=0;
		for(int i = 0; i < lineItems.size(); i++){
			QuoteLineItem qli = (QuoteLineItem)lineItems.get(i);
			
			if(!qli.isReferenceToRenewalQuote()){
				continue;
			}
			if(PartPriceConstants.PRICING_METHOD_PYP.equals(qli.getRenewalPricingMethod())){
				pypCount++;
			}
			if(PartPriceConstants.PRICING_METHOD_RSVP_SRP.equals(qli.getRenewalPricingMethod())){
				RSVPSRPCount++;
			}
		}
		//any pricing method (pricing strategy code) on the bid = prior year plus
		//then the SB  is always flagged as a partial
		if(pypCount > 0){
			return true;
		}
		
		//all pricing method (pricing strategy code) on the bid =  RSVP or RSP
		//then the SB  is not a partial any more
		if(RSVPSRPCount == lineItems.size()){
			return false;
		}
		
	} else {
		//if this is a RSVP priced quote, check to see if user has manually decreased prices on any part
		if(lineItems == null || lineItems.size() == 0){
			return false;
		}
		
		for(int i = 0; i < lineItems.size(); i++){
			QuoteLineItem qli = (QuoteLineItem)lineItems.get(i);
			
			if(!qli.isReferenceToRenewalQuote()){
				continue;
			}
			
			// override percent discount exist but not caused by setting lines to rsvp/srp price
			if(qli.getOvrrdExtPrice() != null
					|| ( qli.getOverrideUnitPrc() != null 
							 || DecimalUtil.isNotEqual(qli.getLineDiscPct(), 0)) && !qli.isSetLineToRsvpSrpFlag() ){
				return true;
			}
		}
	}
	
	return false;
}

public static boolean isPartialRenewalSpBidForItemOmitted(Quote quote) {
	return isPartialRenewalForItemOmitted(quote) && isPartialRenewalSpBid(quote);
}

public static boolean isPartialRenewalSpBidForQuantityLowered(Quote quote) {
	return isPartialRenewalForQuantityLowered(quote) && isPartialRenewalSpBid(quote);
}

public static boolean isGrwthDltgnQuote(String type){
	if(QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_FULL.equals(type)
			|| QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_MIXED.equals(type)){
		return true;
	}
	
	return false;
}

public static boolean isFullGrwthDlgtn(String type){
	return QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_FULL.equals(type);
}

public static String getQuoteGrwthDlgtnType(Quote quote){
	if(!quote.getQuoteHeader().isPAEQuote() && !quote.getQuoteHeader().isPAQuote()){
		return QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_NO;
	}
	
	List lineItems = quote.getLineItemList();
	
	if(lineItems == null || lineItems.size() == 0){
		return QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_NO;
	}
	
	int totalCount = lineItems.size();
	int fullCount = 0;
	int noYtyGrwthCount = 0;
	int ytyGrwthOrRsvpPricingCount = 0;
	int noYtyGrwthOrRsvpPricingCount = 0;
	int refRQCount = 0;
	int notRefRQCount = 0;
	
	// Exclude the additional year.
	List mainLineItems = quote.getMasterSoftwareLineItems();
	if (mainLineItems == null || mainLineItems.size() == 0){
		mainLineItems = CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(quote.getLineItemList());
		quote.setMasterSoftwareLineItems(mainLineItems);
	}
	List<Integer> additionalYearItemsSeqNums = null;
	if(mainLineItems!=null){
		totalCount = mainLineItems.size();
        additionalYearItemsSeqNums = GrowthDelegationUtil.getAdditionalYearItemsSeqNums(quote);
	}
	
	for(int i = 0; i < lineItems.size(); i++){
		QuoteLineItem qli = (QuoteLineItem)lineItems.get(i);
		
		if (additionalYearItemsSeqNums !=null && additionalYearItemsSeqNums.contains(qli.getSeqNum())) {
        	continue;
        }
		
		if(qli.isReferenceToRenewalQuote()){
			refRQCount++;
			
			if(hasImpldYtyGrwthOrRsvpPriced(qli)){
				fullCount++;
			}
		}
		
		if(!qli.isReferenceToRenewalQuote()){
			notRefRQCount++;
		}
		
		if(!hasImpldYtyGrwth(qli)){
			noYtyGrwthCount++;
		}
		
		if(hasImpldYtyGrwthOrRsvpPriced(qli)){
			ytyGrwthOrRsvpPricingCount++;
		}
		
		if(!isPartTypeEligibleForGrowthDelegation(qli)
				|| !hasImpldYtyGrwthOrRsvpPriced(qli)){
			noYtyGrwthOrRsvpPricingCount++;
		}
	}
	
	if(fullCount == totalCount){
		return QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_FULL;
	} 
	
	if((notRefRQCount == totalCount)
			|| (noYtyGrwthCount == totalCount)){
		return QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_NO;
	}
	
	if((refRQCount > 0)
			&& (ytyGrwthOrRsvpPricingCount > 0)
			&& (noYtyGrwthOrRsvpPricingCount > 0)){
		return QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_MIXED;
	}

	return QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_NO;
}

private static boolean hasImpldYtyGrwthOrRsvpPriced(QuoteLineItem qli){
	return hasImpldYtyGrwth(qli)
	         || PartPriceConstants.PRICING_METHOD_RSVP_SRP.equals(qli.getRenewalPricingMethod());
}


private static boolean hasImpldYtyGrwth(QuoteLineItem qli){
	return (qli.getYtyGrowth() != null
			&& qli.getYtyGrowth().getYTYGrowthPct() != null
			&& qli.getYtyGrowth().getIncludedInImpliedYTYGrowthFlag() > 0);
}

public static boolean isPriceDecline(QuoteLineItem qli, QuoteHeader header){
	Double lppPrice = getUnitLppPrice(qli, header);
	Double bidUnitPrice = getAnnualYtyBidUnitPrice(qli) ;
	
	if(lppPrice == null || bidUnitPrice == null){
		return false;
	}
	if((bidUnitPrice < lppPrice)
			&& !PartPriceConstants.PRICING_METHOD_RSVP_SRP.equals(qli.getRenewalPricingMethod())){
		return true;
	}
	
	return false;
}

public static boolean isPriceDecline(List list, QuoteHeader header){
	if(list == null || list.size() == 0){
		return false;
	}
	
	for(int i = 0; i < list.size(); i++){
		QuoteLineItem qli = (QuoteLineItem)list.get(i);
		
		if(isPriceDecline(qli, header)){
			return true;
		}
	}
	
	return false;
}

public static boolean isMissingYty(Quote quote){
	
	List items = quote.getLineItemList();
	if(items == null || items.size() == 0){
		return false;
	}
	
	String lob = quote.getQuoteHeader().getLob().getCode();
	if(!(QuoteConstants.LOB_PA.equals(lob) ||
    		QuoteConstants.LOB_PAE.equals(lob)||
    		 QuoteConstants.LOB_PAUN.equals(lob))){
		return false;
	}
	
	//check to see if any line items is missing yty growth pct
	for(int i = 0; i < items.size(); i++){
		QuoteLineItem qli = (QuoteLineItem)items.get(i);			
		Double lpp = GrowthDelegationUtil.getUnitLppPrice(qli, quote.getQuoteHeader());
		boolean flag = false;
		if (qli != null && qli.getLocalUnitProratedPrc() != null) {
			flag = qli.getLocalUnitProratedPrc() == 0.0;
		}
		if (qli != null && qli.getLocalExtProratedPrc() != null) {
			flag = flag || qli.getLocalExtProratedPrc() == 0.0;
		}
		//if LPP is null,show warn messgae
		if (!flag) {
			if(isDisplayLineItemYTY(quote, qli)			
				&& (lpp == null) ){
				return true;
			}
		}
		if(isDisplayLineItemYTY(quote, qli)
				&& (qli.getYtyGrowth() == null
						|| qli.getYtyGrowth().getYTYGrowthPct() == null)
				&&(qli.getLocalUnitProratedDiscPrc()!=null && qli.getLocalUnitProratedDiscPrc() != 0.0) ){
			return true;
		}
	}
	return false;
}


public static boolean isManualEnterYTY(String ytyRadioValue){
	if(DraftQuoteParamKeys.YTY_RADIO_YTY_GROWTH_VALUE.equals(ytyRadioValue)){
		return true;
	}
	
	return false;
}


public static void reCalculateYTYGrowthStatusAndYtyPct(Quote quote) throws TopazException, QuoteException{
	List lineItems = quote.getLineItemList();
	QuoteLineItem lineItem = null;
	if(lineItems == null){
		return;
	}
	
	PartPriceProcess process = PartPriceProcessFactory.singleton().create();
	
	List<Integer> additionalYearMainItemsSeqNums = getAdditionalYearMainItemsSeqNums(quote);
	List<Integer> needCalculateItemSeqNums = getCalculatedItemSeqNums(quote);
	
	for (Iterator it = lineItems.iterator(); it.hasNext(); ) {
		lineItem = (QuoteLineItem)it.next();    		
		if(isCreateDefaultYTYGrowth(quote, lineItem)){
			if (needCalculateItemSeqNums.contains(lineItem.getSeqNum())) {
				updateLineItemYTYGrowthStatusAndPct(quote, lineItem);
    		}
			
			if (additionalYearMainItemsSeqNums.contains(lineItem.getSeqNum())) {	    	    		    	
				updateAdditionalYearYTYGrowthStatusAndPct(quote, lineItem);
    	    }
			
			calYtyPriceAndDiscOff(lineItem);
			
			//process.doCalculateYtyGrowth(lineItem, quote.getQuoteHeader());
		}
	}
}

public static void reCalculateYTYGrowthReferenceInfo(Quote quote) throws TopazException, QuoteException{	
	List lineItems = quote.getLineItemList();
	QuoteLineItem lineItem = null;
	if(lineItems == null){
		return;
	}
	
	for(Iterator it = lineItems.iterator(); it.hasNext(); ){
		lineItem = (QuoteLineItem)it.next();    
		YTYGrowth yty = lineItem.getYtyGrowth();
		
		if(yty != null){
			calYtyPriceAndDiscOff(lineItem);
		}
	}
}

public static void updateLineItemYTYGrowthStatusAndPct(Quote quote, QuoteLineItem lineItem) throws TopazException{		
	YTYGrowth ytyGrowth = lineItem.getYtyGrowth();
	if(ytyGrowth == null){
		ytyGrowth = getDefaultYTY(quote, lineItem, null);
		lineItem.setYtyGrowth(ytyGrowth);
	}
	//Calculate the reason code for implied growth and overall growth
	Map<Integer, Integer> map = getGrowthDelegationEligiblilityReasonCode(quote, lineItem);
	int reasonCodeForImpliedGrowth = map.get(GrowthDelegationUtil.IMPLIED_GROWTH);
	int reasonCodeForOveralGrowth = map.get(GrowthDelegationUtil.OVERALL_GROWTH);
	
	String radio = ytyGrowth.getYtyGrwothRadio();
	Double ytyGrowthPctTmp = ytyGrowth.getYTYGrowthPct();

	if (!isManualEnterYTY(radio)) {
		ytyGrowthPctTmp = getYtyGrowthPct(lineItem, quote.getQuoteHeader());
	}
	// Update reason code and yty to YTYGrowth object
	ytyGrowth.setIncludedInOverallYTYGrowthFlag(reasonCodeForOveralGrowth);
	ytyGrowth.setIncludedInImpliedYTYGrowthFlag(reasonCodeForImpliedGrowth);
	ytyGrowth.setYTYGrowthPct(ytyGrowthPctTmp);
	map.clear();
	map = null;
}

public static double calculateMonths(QuoteLineItem item){
	if(item == null){
		return 0;
	}

	if(item.getCmprssCvrageMonth() != null && item.getCmprssCvrageMonth().intValue() > 0){
		return item.getCmprssCvrageMonth();
	} else {
		if(item.getMaintStartDate() !=null && item.getMaintEndDate()!=null){
			return DateUtil.calculateFullCalendarMonths(item.getMaintStartDate(), item.getMaintEndDate()); 
		}else{
			return 0;
		}
	}
}


private static String createKey(QuoteLineItem item) {
    return item.getPartNum().trim() + "_" + item.getSeqNum();
}

public static double calculateLineItemMonths(LineItemParameter lineItem){
	if(lineItem != null && lineItem.maintStartDate !=null && lineItem.maintEndDate!=null){
		return DateUtil.calculateFullCalendarMonths(lineItem.maintStartDate,  lineItem.maintEndDate); 
	}else{
		return 0;
	}
}

public static Double getYtyGrowthPct(QuoteLineItem item, QuoteHeader header){
	Double extndLppPrice = GDPartsUtil.getExtndLppPrice(item, header);
	Double extndBidPrice = item.getLocalExtProratedDiscPrc();
	
	if(extndLppPrice != null && extndBidPrice != null  && extndLppPrice.doubleValue() !=0){
		if(item.getLocalUnitProratedDiscPrc()!=null && item.getLocalUnitProratedDiscPrc() ==0.0){
			return null;
		}else{
			Double ytyGrowth = ((extndBidPrice / extndLppPrice) - 1) * 100;
    		
    		return ytyGrowth;
		}
		
	}
	
	return null;
}

public static Double getRoundedExtndLppPrice(QuoteLineItem item, Quote quote){
	Double price = GDPartsUtil.getExtndLppPrice(item, quote.getQuoteHeader());
	if(price == null){
		return null;
	}
	
	return DecimalUtil.roundAsDouble(price, QuoteCommonUtil.getPartPriceRoundingFactor(quote.getQuoteHeader(), item));
}

private static Double getUnitLppPriceConsideringCurrencyMismatch(QuoteLineItem item, 
		                                        QuoteHeader header, boolean acceptCurrenceMismatch){
	//in what ever case, we need to find the manually entered/updated price firstly
    	YTYGrowth yty = item.getYtyGrowth();
    	if(yty != null){
			Double manualLpp = yty.getManualLPP();
			if(manualLpp != null && (StringUtils.isNotBlank(yty.getLppMissReas()) || GDPartsUtil.isEligibleRenewalPart(item))){
				return manualLpp;
			}
		}
    	
    	return getOldUnitLppPriceConsideringCurrencyMismatch(item, header, acceptCurrenceMismatch);
    }
    
    private static Double getOldUnitLppPriceConsideringCurrencyMismatch(QuoteLineItem item, 
            QuoteHeader header, boolean acceptCurrenceMismatch){
    	String quoteCurrency = header.getCurrencyCode();
    	PriorYearSSPrice pyp = item.getPriorYearSSPrice();
    	if(pyp != null){
    		String lppCurrency = StringUtils.trimToEmpty(pyp.getPriorYrCurrncyCode());
    		String lpp = pyp.getPriorYrLocalUnitPrice12Mnths();

    		if(!StringUtils.equals(quoteCurrency, lppCurrency)){
    			if(acceptCurrenceMismatch){
    				return getDoubleValue(lpp);
    			} else {
    				return null;
    			}

    		} else {
    			return getDoubleValue(lpp);
    		}
    	}

    	return null;
}
    
    private static Double getDoubleValue(String value){
    	try{
    		return Double.valueOf(value);
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public static Double getUnitLppPrice(QuoteLineItem item, QuoteHeader header){
    	return getUnitLppPriceConsideringCurrencyMismatch(item, header, false);
    }
    
    public static Double getUnitLppPriceAcceptCurrencyMismatch(QuoteLineItem item, QuoteHeader header){
    	return getUnitLppPriceConsideringCurrencyMismatch(item, header, true);
    }
    
    public static Double getOldUnitLppPriceAcceptCurrencyMismatch(QuoteLineItem item, QuoteHeader header){
    	return getOldUnitLppPriceConsideringCurrencyMismatch(item, header, true);
    }
    
    public static YTYGrowth getDefaultYTY(Quote quote, QuoteLineItem lineItem, Double ytyGrowthPct) throws TopazException{
	YTYGrowth ytyGrowth = YTYGrowthFactory.singleton().createYTYGrowth(quote.getQuoteHeader().getWebQuoteNum(), lineItem.getSeqNum());

	Map<Integer, Integer> map = GrowthDelegationUtil.getGrowthDelegationEligiblilityReasonCode(quote, lineItem);
	int reasonCodeForImpliedGrowth = map.get(GrowthDelegationUtil.IMPLIED_GROWTH);
	int reasonCodeForOveralGrowth = map.get(GrowthDelegationUtil.OVERALL_GROWTH);

	ytyGrowth.setIncludedInImpliedYTYGrowthFlag(reasonCodeForImpliedGrowth);
	ytyGrowth.setIncludedInOverallYTYGrowthFlag(reasonCodeForOveralGrowth);
	ytyGrowth.setRenwlQliQty(lineItem.getOpenQty());
	ytyGrowth.setYTYGrowthPct(ytyGrowthPct);
	ytyGrowth.setYtySourceCode(DraftQuoteConstants.LPP_SOURCE_DEFAULT); //
	return ytyGrowth;
}


/**
 * @param lineItem
 * calculate the yty unit price to be the annually unit price
 * @throws TopazException 
 */
private static void calculateYtyUnitPrice(QuoteLineItem lineItem) throws TopazException{
	double months = calculateMonths(lineItem);
	if(lineItem.getLocalUnitProratedPrc() != null && months > 0){
		if(lineItem.getRenewalPricingMethod()!=null && lineItem.getRenewalPricingMethod().equals(PartPriceConstants.PRICING_METHOD_RSVP_SRP)){
			if(lineItem.getYtyGrowth() != null && lineItem.getRenewalRsvpPrice() !=null){
				lineItem.getYtyGrowth().setYtyUnitPrice(lineItem.getRenewalRsvpPrice());
			}
		}else{
			double currentUnitPrice = lineItem.getLocalUnitProratedPrc().doubleValue();
			double ytyUnitPrice = currentUnitPrice * 12 / months;
			if(lineItem.getYtyGrowth() != null){
				lineItem.getYtyGrowth().setYtyUnitPrice(new Double(DecimalUtil.roundAsDouble(ytyUnitPrice, 2)));
			}
		}
		
	}
}

/**
 * @param lineItem
 * calculate the yty bid unit price to be the annually unit price
 * @throws TopazException 
 */
private static void calculateYtyBidUnitPrice(QuoteLineItem lineItem) throws TopazException{
	if(lineItem.getYtyGrowth() != null){
		lineItem.getYtyGrowth().setYtyBidUnitPrice(getAnnualYtyBidUnitPrice(lineItem));
	}
}

/**
 * @param Double
 * calculate the yty bid unit price to be the annually unit price
 */
public static Double getAnnualYtyBidUnitPrice(QuoteLineItem lineItem){
	double months = calculateMonths(lineItem);
	Double ytyBidUnitPrice = null;
	if(lineItem.getLocalUnitProratedDiscPrc() != null && months > 0){
		if(lineItem.getRenewalPricingMethod()!=null && lineItem.getRenewalPricingMethod().equals(PartPriceConstants.PRICING_METHOD_RSVP_SRP) 
				&& months < 12 && (lineItem.getOverrideUnitPrc() == null && lineItem.getOvrrdExtPrice()== null && DecimalUtil.isEqual(lineItem.getLineDiscPct(), 0.0)) || lineItem.isSetLineToRsvpSrpFlag()){
			ytyBidUnitPrice= new Double(DecimalUtil.roundAsDouble(lineItem.getRenewalRsvpPrice(),2));
		}else{
			double currentBidUnitPrice = lineItem.getLocalUnitProratedDiscPrc().doubleValue();
			ytyBidUnitPrice = new Double(DecimalUtil.roundAsDouble(currentBidUnitPrice * 12 / months,2));
		}
	} 
	return ytyBidUnitPrice;
}

public static Double getProratedRSVPPrice(QuoteLineItem lineItem){
	double months = calculateMonths(lineItem);
	Double rsvpPrice = lineItem.getRenewalRsvpPrice();
	
	if(rsvpPrice != null && months > 0){
		return new Double(DecimalUtil.roundAsDouble(rsvpPrice * months / 12, 2));
	}
	
	return null;
}

/**
 * @param lineItem
 * calculate the Discount Off RSVP/SRP based on YTY unit price
 * @throws TopazException 
 */
private static void calculateDiscOffRsvpUnit(QuoteLineItem lineItem) throws TopazException{
	if (lineItem.getRenewalRsvpPrice() != null
			&& lineItem.getRenewalRsvpPrice().doubleValue() != 0
			&& lineItem.getYtyGrowth() != null
			&& lineItem.getYtyGrowth().getYtyUnitPrice() != null
			&& lineItem.getYtyGrowth().getYtyUnitPrice().doubleValue() != 0) {
		double discOffRsvpUnit = DecimalUtil.roundAsDouble(
				100
						* (lineItem.getRenewalRsvpPrice() - lineItem.getYtyGrowth().getYtyUnitPrice().doubleValue())
						/ lineItem.getRenewalRsvpPrice(), 2);
		lineItem.getYtyGrowth().setDiscOffRsvpUnit(discOffRsvpUnit);
	}
}

/**
 * @param lineItem
 * calculate the Discount Off RSVP/SRP based on YTY bid unit price
 * @throws TopazException 
 */
private static void calculateDiscOffRsvpBidUnit(QuoteLineItem lineItem) throws TopazException{
	if (lineItem.getRenewalRsvpPrice() != null
			&& lineItem.getRenewalRsvpPrice().doubleValue() != 0
			&& lineItem.getYtyGrowth() != null
			&& lineItem.getYtyGrowth().getYtyBidUnitPrice() != null
			&& lineItem.getYtyGrowth().getYtyBidUnitPrice().doubleValue() != 0) {
		double discOffRsvpBidUnit = DecimalUtil.roundAsDouble(
				100
						* (lineItem.getRenewalRsvpPrice() - lineItem.getYtyGrowth().getYtyBidUnitPrice().doubleValue())
						/ lineItem.getRenewalRsvpPrice(), 2);
		lineItem.getYtyGrowth().setDiscOffRsvpBidUnit(discOffRsvpBidUnit);
	}
}


/**
 * @param lineItem
 * recalculate the YTY bid/unit price and related Discount Off RSVP/SRP
 * @throws TopazException 
 */
private static void calYtyPriceAndDiscOff(QuoteLineItem lineItem) throws TopazException{
	calculateYtyUnitPrice(lineItem);
	calculateYtyBidUnitPrice(lineItem);
	calculateDiscOffRsvpUnit(lineItem);
	calculateDiscOffRsvpBidUnit(lineItem);
}

public static boolean isShowYTYReferenceInfoForBidIteration(Quote quote){
	boolean isShowYTYReferenceInfoForBidIteration = false;
	isShowYTYReferenceInfoForBidIteration = quote.getQuoteHeader().isBidIteratnQt()
			&& isContainEligibleRQLineItems(quote)
			&& isLobEligibleForGrowthDelegation(quote);
	return isShowYTYReferenceInfoForBidIteration;
}


public static boolean hasPartsExceedRsvpPrice(Quote quote){
	List list = quote.getMasterSoftwareLineItems();
	if(list == null || list.size() == 0){
		return false;
	}
	
	String lob = quote.getQuoteHeader().getLob().getCode();
	if(!(QuoteConstants.LOB_PA.equals(lob) ||
    		QuoteConstants.LOB_PAE.equals(lob)||
    		 QuoteConstants.LOB_PAUN.equals(lob))){
		return false;
	}
	
	int i = 0;
	boolean hasExceed = false;
	for(Iterator it = list.iterator(); it.hasNext(); ){
		QuoteLineItem qli = (QuoteLineItem)it.next();
		YTYGrowth yty = qli.getYtyGrowth();
		
		if(yty != null && isDisplayLineItemYTY(quote, qli)){
			if(isBidPriceGreaterThanRSVPSRP(qli)){
				hasExceed = true;
				break;
			}
		}
	}
	
//		if(hasExceed && !isPartialRenewal(quote)){
	if(hasExceed){
		return true;
	}
	
	return false;
}

 public static boolean showGrowthDelegationNotCalculatedMessage(Quote quote){
	 	if(quote.getQuoteHeader().getRecalcPrcFlag() == 0){
	 		return false;
	 	}
	 
    	List list = quote.getLineItemList();
    	
    	if(list == null || list.size() == 0){
    		return false;
    	}
    	
    	for(int i = 0; i < list.size(); i++){
    		QuoteLineItem qli = (QuoteLineItem)list.get(i);
    		
    		YTYGrowth yty = qli.getYtyGrowth();
    		if(yty != null){
    			if(yty.isPctManuallyEntered()){
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
 
 
 public static boolean showYtyExcludeApplncPartOrSaasPart(QuoteLineItem lineItem){
	 boolean isTrue=false;
	 isTrue=!lineItem.isApplncPart() && !lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart() && !isExcludedRevstream(lineItem);
	 return isTrue ;
 }
 
//GuoTao: Currency convert
	public static boolean isOmittedCurrencyConvertForDraft(Quote quote,  String  priorPrice,String priorcurrcyCode){
		if (onlyForPAPAE(quote) ){
			if (!StringUtils.isBlank(priorPrice)) {
				String quoteCurrency = quote.getQuoteHeader().getCurrencyCode();
				if ((!StringUtils.isBlank(priorcurrcyCode)) && (!priorcurrcyCode.equalsIgnoreCase(quoteCurrency)))
					return true;
			}
		}
		return false;
	}
	
	//GuoTao: Price missing
	public static boolean isOmittedPriceMissingForDraft(Quote quote, String  priorPrice){
		if(StringUtils.isBlank(priorPrice)){
			return true;
		}
		return false;
	}
	
	//GuoTao: Currency is the same, but override price value is allowed
	public static boolean isOmittedPriceOverrideForDraft(Quote quote, String priorPrice,String PriorYrCurrncyCode){
		if (onlyForPAPAE(quote) ){
			if (!StringUtils.isBlank(priorPrice)) {
				String quoteCurrency = quote.getQuoteHeader().getCurrencyCode();
				if ((!StringUtils.isBlank(PriorYrCurrncyCode)) && (PriorYrCurrncyCode.equalsIgnoreCase(quoteCurrency)))
					return true;
			}
		}
		return false;
	}
	
	public static boolean isShowViewOverridenForSubmittedOmitted(Quote quote, String ytySourceCode){
		String ytySceCode = getOmittedYtySourceCode(ytySourceCode);
		return ytySceCode.equalsIgnoreCase(DraftQuoteConstants.LPP_POPUP_SOURCE_OVRRD_LPP);
	}
	
	public static boolean isShowViewManuallyEnteredForSubmittedOmitted(Quote quote, String ytySourceCode){
		String ytySceCode = getOmittedYtySourceCode(ytySourceCode);
		return ytySceCode.equalsIgnoreCase(DraftQuoteConstants.LPP_POPUP_SOURCE_NO_LPP);
	}
	
	public static boolean isShowViewCurrencyConvertedForSubmittedOmitted(Quote quote, String ytySourceCode){
		String ytySceCode = getOmittedYtySourceCode(ytySourceCode);
		return ytySceCode.equalsIgnoreCase(DraftQuoteConstants.LPP_POPUP_SOURCE_CURRENCY_CONVERSION);
	}
	
	public static List calculateMultipleAdditionalYearImpliedGrowthList(Quote quote) {
		
		Map<Integer, List<QuoteLineItem>> additionalYearItemMap = initAdditionalYearItemMap(quote);
		
		List additionalYearImpliedGrowthList = new ArrayList();
		Iterator<Entry<Integer, List<QuoteLineItem>>> it = additionalYearItemMap.entrySet().iterator();
		while (it.hasNext()) {
		   Entry<Integer, List<QuoteLineItem>> entry = it.next();
		   int year = entry.getKey();
		   if (year > 0) {
			   List currentAdditionalYearItems = entry.getValue();
			   List lastAdditionalYearItems = additionalYearItemMap.get(year - 1);
			   if (currentAdditionalYearItems.size() != lastAdditionalYearItems.size()) {
				   lastAdditionalYearItems = extractLastAdditionalYearItems(lastAdditionalYearItems, currentAdditionalYearItems);
			   }
			   Double additionalYearImpliedGrowth = calculateAdditionalYearImpliedGrowth(lastAdditionalYearItems, currentAdditionalYearItems, year == 1);
			   additionalYearImpliedGrowthList.add(additionalYearImpliedGrowth);
		   }
		}
		return additionalYearImpliedGrowthList;
	}
	
	// Get the sequence numbers for the additional years.
	public static List<Integer> getAdditionalYearItemsSeqNums(Quote quote) {
		List<Integer> additionalYearItemsSeqNums = new ArrayList<Integer>();
		List mainLineItems = quote.getMasterSoftwareLineItems();
		if(mainLineItems != null && mainLineItems.size() > 0){
			for(Iterator mainIterator = mainLineItems.iterator(); mainIterator.hasNext();) {
				QuoteLineItem mainLineItem = (QuoteLineItem) mainIterator.next();				
				List subLineItems = mainLineItem.getAddtnlYearCvrageLineItems();
				if (subLineItems != null && subLineItems.size() > 0) {
					for (int i = 0; i < subLineItems.size(); i++) {
						QuoteLineItem subLineItem = (QuoteLineItem) subLineItems.get(i);
						additionalYearItemsSeqNums.add(subLineItem.getSeqNum());
					}
				}
			}
		}
		return additionalYearItemsSeqNums;
    }

	// Get the sequence numbers for the additional years.
	public static List<Integer> getRQAdditionalYearItemsSeqNums(Quote quote) {
		List<Integer> additionalYearItemsSeqNums = new ArrayList<Integer>();
		List mainLineItems = quote.getMasterSoftwareLineItems();
		if(mainLineItems != null && mainLineItems.size() > 0){
			for(Iterator mainIterator = mainLineItems.iterator(); mainIterator.hasNext();) {
				QuoteLineItem mainLineItem = (QuoteLineItem) mainIterator.next();				
				if (StringUtils.isNotBlank(mainLineItem.getRenewalQuoteNum())) {
					List subLineItems = mainLineItem.getAddtnlYearCvrageLineItems();
					if (subLineItems != null && subLineItems.size() > 0) {
						for (int i = 0; i < subLineItems.size(); i++) {
							QuoteLineItem subLineItem = (QuoteLineItem) subLineItems.get(i);
							additionalYearItemsSeqNums.add(subLineItem.getSeqNum());
						}
					}
				}
			}
		}
		
		return additionalYearItemsSeqNums;
    }

	private static String getOmittedYtySourceCode(String  ytySourceCode){
			if (ytySourceCode != null){
				return StringUtils.trim(ytySourceCode);
		}
		return "";
	}
	
	// Calculate YTY growth for the additional year.
	private static void updateAdditionalYearYTYGrowthStatusAndPct(Quote quote, QuoteLineItem lineItem) throws TopazException{
		List subAdditionalYearLineItems = lineItem.getAddtnlYearCvrageLineItems();
    	for (int i = 0; i < subAdditionalYearLineItems.size(); i++) {
			QuoteLineItem subLineItem = (QuoteLineItem) subAdditionalYearLineItems.get(i);
			YTYGrowth ytyGrowth = subLineItem.getYtyGrowth();
			if (ytyGrowth == null) {
				ytyGrowth = getDefaultYTY(quote, subLineItem, null);
				subLineItem.setYtyGrowth(ytyGrowth);
			}
			
			//Calculate the reason code for implied growth and overall growth
			Map<Integer, Integer> map = getGrowthDelegationEligiblilityReasonCode(quote, subLineItem);
			int reasonCodeForImpliedGrowth = map.get(GrowthDelegationUtil.IMPLIED_GROWTH);
			int reasonCodeForOveralGrowth = map.get(GrowthDelegationUtil.OVERALL_GROWTH);
			String radio = ytyGrowth.getYtyGrwothRadio();
			Double ytyGrowthPctTmp = ytyGrowth.getYTYGrowthPct();
			if (!isManualEnterYTY(radio)) {
				Double lastBidExtPrice = 0d;
				if (i == 0) {
	    			double months = calculateMonths(lineItem);
	    			if (lineItem.getLocalExtProratedDiscPrc() != null && months > 0){
	    				lastBidExtPrice = lineItem.getLocalExtProratedDiscPrc().doubleValue() * 12 / months;
	    			}
	    		} else {
	    			QuoteLineItem lastLineItem = (QuoteLineItem) subAdditionalYearLineItems.get(i-1);
	    			if (null != lastLineItem.getLocalExtProratedDiscPrc()){
	    			    lastBidExtPrice = lastLineItem.getLocalExtProratedDiscPrc();
	    			}
	    		}
				lastBidExtPrice = DecimalUtil.roundAsDouble(lastBidExtPrice, 2);
				if(subLineItem.getLocalExtProratedDiscPrc() != null){
					if(!DecimalUtil.isEqual(lastBidExtPrice, 0d)){
						Double currentBidExtPrice = subLineItem.getLocalExtProratedDiscPrc().doubleValue();
						ytyGrowthPctTmp = ((currentBidExtPrice - lastBidExtPrice) / lastBidExtPrice) * 100;
					}else{
						ytyGrowthPctTmp=0.0d;
					}
					ytyGrowth.setYTYGrowthPct(ytyGrowthPctTmp);
				}
			}
			// Update reason code and yty to YTYGrowth object
			ytyGrowth.setIncludedInOverallYTYGrowthFlag(reasonCodeForOveralGrowth);
			ytyGrowth.setIncludedInImpliedYTYGrowthFlag(reasonCodeForImpliedGrowth);
		}
    }
	
	// Get the sequence number for the renewal main line item which has additional years.
	protected static List<Integer> getAdditionalYearMainItemsSeqNums(Quote quote) {
		List<Integer> additionalYearMainItemsSeqNums = new ArrayList<Integer>();
    	List mainLineItems = quote.getMasterSoftwareLineItems();
    	if(mainLineItems != null && mainLineItems.size() >0 ){
    		for(Iterator mainIterator = mainLineItems.iterator(); mainIterator.hasNext();) {
				QuoteLineItem mainLineItem = (QuoteLineItem) mainIterator.next();
				if (StringUtils.isNotBlank(mainLineItem.getRenewalQuoteNum()) && mainLineItem.getAddtnlMaintCvrageQty() > 0) {
					additionalYearMainItemsSeqNums.add(mainLineItem.getSeqNum());
				}
			}
    	}
		
		return additionalYearMainItemsSeqNums;
    }
	
	// Get the sequence number for the line item which need to calculate.
	protected static List<Integer> getCalculatedItemSeqNums(Quote quote) {
    	List<Integer> needCalculateItemSeqNums = new ArrayList<Integer>();
    	List mainLineItems = quote.getMasterSoftwareLineItems();
    	if(mainLineItems != null && mainLineItems.size() > 0){
    		for(Iterator mainIterator = mainLineItems.iterator(); mainIterator.hasNext();) {
				QuoteLineItem mainLineItem = (QuoteLineItem) mainIterator.next();
				needCalculateItemSeqNums.add(mainLineItem.getSeqNum());
				if (StringUtils.isBlank(mainLineItem.getRenewalQuoteNum()) && !mainLineItem.isAddedToLicPart()) {
					List subLineItems = mainLineItem.getAddtnlYearCvrageLineItems();
					if (subLineItems != null && subLineItems.size() > 0) {
						for (int i = 0; i < subLineItems.size(); i++) {
							QuoteLineItem subLineItem = (QuoteLineItem) subLineItems.get(i);
							needCalculateItemSeqNums.add(subLineItem.getSeqNum());
						}
					}
				}
			}
    	}
		
		return needCalculateItemSeqNums;
    }
	
	// Initialize the calculated additional year items map.
	private static Map<Integer, List<QuoteLineItem>> initAdditionalYearItemMap(Quote quote) {
		Map<Integer, List<QuoteLineItem>> additionalYearItemMap = new HashMap<Integer, List<QuoteLineItem>>();
    	List mainLineItems = new ArrayList<QuoteLineItem>();
    	mainLineItems.addAll(quote.getMasterSoftwareLineItems());
    	Collections.sort(mainLineItems, new DateComparator());
    	List<QuoteLineItem> additionalYearMainLineItems = new ArrayList<QuoteLineItem>();
    	additionalYearItemMap.put(0, additionalYearMainLineItems);
    	if(mainLineItems != null && mainLineItems.size() > 0){
    		for(Iterator mainIterator = mainLineItems.iterator(); mainIterator.hasNext();) {
				QuoteLineItem mainLineItem = (QuoteLineItem) mainIterator.next();			
				if (StringUtils.isNotBlank(mainLineItem.getRenewalQuoteNum())) {
					List subLineItems = mainLineItem.getAddtnlYearCvrageLineItems();
					if (subLineItems != null && subLineItems.size() > 0) {
						additionalYearMainLineItems.add(mainLineItem);
						for (int i = 1; i <= subLineItems.size(); i++) {
							QuoteLineItem subLineItem = (QuoteLineItem) subLineItems.get(i-1);
							List<QuoteLineItem> additionalYearLineItems = additionalYearItemMap.get(i);
							if (additionalYearLineItems == null) {
								additionalYearLineItems = new ArrayList<QuoteLineItem>();
								additionalYearItemMap.put(i, additionalYearLineItems);
							}
							additionalYearLineItems.add(subLineItem);
						}
					}
				}
				// 14.2 GD, master renewal parts
				if (GDPartsUtil.isEligibleMasterRenewalPart(mainLineItem)) {
					// master renewal part is the first additional year
					if(additionalYearItemMap.get(1) == null){
						List<QuoteLineItem> additionalFirstYearLineItem = new ArrayList<QuoteLineItem>();
						additionalFirstYearLineItem.add(mainLineItem);
						additionalYearItemMap.put(1, additionalFirstYearLineItem);
					}else{
						additionalYearItemMap.get(1).add(mainLineItem);
					}
					additionalYearMainLineItems.addAll(mainLineItem.getAddedToLicParts());
					List subLineItems = mainLineItem.getAddtnlYearCvrageLineItems();
					if (subLineItems != null && subLineItems.size() > 0) {
						// increment up
						for (int i = 2; i < (subLineItems.size() + 2); i++) {
							QuoteLineItem subLineItem = (QuoteLineItem) subLineItems.get(i-2);
							List<QuoteLineItem> additionalYearLineItems = additionalYearItemMap.get(i);
							if (additionalYearLineItems == null) {
								additionalYearLineItems = new ArrayList<QuoteLineItem>();
								additionalYearItemMap.put(i, additionalYearLineItems);
							}
							additionalYearLineItems.add(subLineItem);
						}
					}							
				}
				// 14.2 GD, additional like renewal parts
				if(GDPartsUtil.isEligibleAdditionalLikeRenewalPart(mainLineItem)){
					QuoteLineItem associatedLineItem = (QuoteLineItem) mainLineItem.getAddedToLicParts().get(0);
					Iterator<Entry<Integer, List<QuoteLineItem>>> it = additionalYearItemMap.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Integer, List<QuoteLineItem>> entry = it.next();
						int year = entry.getKey();
						// year 0 is license part group
						// additional like part must associate with a master renewal part/last additional year
						if (year > 0 && entry.getValue().contains(associatedLineItem)) {
							if (additionalYearItemMap.get(year + 1) == null) {
								List<QuoteLineItem> additionalYearLineItems = new ArrayList<QuoteLineItem>();
								additionalYearLineItems.add(mainLineItem);
								additionalYearItemMap.put(year + 1, additionalYearLineItems);
							} else {
								additionalYearItemMap.get(year + 1).add(mainLineItem);
							}
							// additional years of the additional like part
							List subLineItems = mainLineItem.getAddtnlYearCvrageLineItems();
							if (subLineItems != null && subLineItems.size() > 0) {
								// increment up
								for (int i = 0; i < subLineItems.size(); i++) {
									QuoteLineItem subLineItem = (QuoteLineItem) subLineItems.get(i);
									List<QuoteLineItem> additionalYearLineItems = additionalYearItemMap.get(i + year + 2);
									if (additionalYearLineItems == null) {
										additionalYearLineItems = new ArrayList<QuoteLineItem>();
										additionalYearItemMap.put(i + year + 2, additionalYearLineItems);
									}
									additionalYearLineItems.add(subLineItem);
								}
							}
							break;
						}
					}
				}
			}
    	}	    	
		return additionalYearItemMap;
	}
	
	// Extract the new last additional year Items
	private static List extractLastAdditionalYearItems(List<QuoteLineItem> lastAdditionalYearItems, List<QuoteLineItem> currentAdditionalYearItems) {
		List newLastAdditionalYearItems = new ArrayList();		
		for (QuoteLineItem currentLineItem : currentAdditionalYearItems) {
			if(GDPartsUtil.isEligibleRenewalPart(currentLineItem))
			{
				newLastAdditionalYearItems.addAll(currentLineItem.getAddedToLicParts());
			}else{
				for (QuoteLineItem lastLineItem : lastAdditionalYearItems) {
					if (currentLineItem.getSeqNum() - lastLineItem.getSeqNum() == 1) {
						newLastAdditionalYearItems.add(lastLineItem);
						break;
					}
				}
			}
		}
		return newLastAdditionalYearItems;
	}
	
	// Calculate the additional year implied growth
	private static Double calculateAdditionalYearImpliedGrowth(List lastAdditionalYearItems, List currentAdditionalYearItems, boolean isFirstYear) {
		List prevAddiItems = new ArrayList();
		prevAddiItems.addAll(lastAdditionalYearItems);
		List curAddiItems = new ArrayList();
		curAddiItems.addAll(currentAdditionalYearItems);
		Double growth = 0.0d;
		Double lastBidExtPrice = 0.0d;
    	Double currentBidExtPrice = 0.0d;
    	
    	// master renewal part has the different calculation rule for its last bid ext price in 14.2 GD
    	for (Iterator curIt = currentAdditionalYearItems.iterator(); curIt.hasNext();) {
			QuoteLineItem curQli = (QuoteLineItem) curIt.next();
			if(GDPartsUtil.isEligibleRenewalPart(curQli)){
				Integer totalLicQty = new Integer(0);
				List addedToLicParts = curQli.getAddedToLicParts();
				for (Iterator licIt = addedToLicParts.iterator(); licIt.hasNext();) {
					QuoteLineItem licQli = (QuoteLineItem) licIt.next();
					if(licQli.getPartQty() != null)
						totalLicQty += licQli.getPartQty();						
				}
				double months = calculateMonths(curQli);
				if(curQli.getYtyGrowth().getManualLPP() != null && totalLicQty.intValue() > 0 && months > 0){
					lastBidExtPrice += curQli.getYtyGrowth().getManualLPP() * totalLicQty;
					if (null != curQli.getLocalExtProratedDiscPrc()){
					    currentBidExtPrice += curQli.getLocalExtProratedDiscPrc() * 12 / months;
					}
				}
				prevAddiItems.removeAll(addedToLicParts);
				curAddiItems.remove(curQli);
			}
    	}
    	
    	// non master renewal parts
    	lastBidExtPrice += getAdditionalYearTotalBidExtPrice(prevAddiItems, isFirstYear);
    	currentBidExtPrice += getAdditionalYearTotalBidExtPrice(curAddiItems, false);	    	
    	if(lastBidExtPrice != 0.0d && currentBidExtPrice != 0.0d) {
    		growth = ((currentBidExtPrice - lastBidExtPrice) / lastBidExtPrice) * 100;
    	}
    	return DecimalUtil.roundAsDouble(growth, 2);
    }
    
	// get additional year total bid extended price
	private static Double getAdditionalYearTotalBidExtPrice(List<QuoteLineItem> additionalYearItems, boolean isMainPart) {
    	Double bidExtPrice = 0.0d;
    	for (QuoteLineItem lineItem : additionalYearItems) {
    		Double lineItemExtPrice = null;
    		if (lineItem.getLocalExtProratedDiscPrc() != null) {
    			if (isMainPart) {
    				double months = calculateMonths(lineItem);
    				if (months > 0) {
    					lineItemExtPrice = lineItem.getLocalExtProratedDiscPrc().doubleValue() * 12 / months;
    				}
    			} else {
    				lineItemExtPrice = lineItem.getLocalExtProratedDiscPrc().doubleValue();
    			}
    			bidExtPrice = bidExtPrice + lineItemExtPrice;
    		}
    	}
    	return bidExtPrice;
    }
	
	public static boolean isShowViewUpdateLinkForDraftOmitted(Quote quote,String priorPrice, String webQuoteNum,int renewalSecNum,String renewalNum,String ytysrcCode){
		
		if(priorPrice == null || priorPrice.equals("")){
			return false;
		}
		
		if (onlyForPAPAE(quote)){
		
			if (ytysrcCode != null)
				ytysrcCode = ytysrcCode.trim();
			if ((DraftQuoteConstants.LPP_POPUP_SOURCE_CURRENCY_CONVERSION.equals(ytysrcCode)
					|| DraftQuoteConstants.LPP_POPUP_SOURCE_OVRRD_LPP.equals(ytysrcCode)
					|| DraftQuoteConstants.LPP_POPUP_SOURCE_NO_LPP.equals(ytysrcCode)))
			return true;
		}
		return false;
	}
	
	public static String getPriorSSCurrncyCode(Quote quote,String lppCurrency,String priorPrice,double localPriorPrice){
		String quoteCurrency = quote.getQuoteHeader().getCurrencyCode();
		if(StringUtils.isBlank(lppCurrency)){
			return DraftQuoteConstants.BLANK;
		}else{
			if(StringUtils.equals(lppCurrency, quoteCurrency)){
				return DraftQuoteConstants.BLANK;
			}else{
				if(localPriorPrice != 0 ){
					return quoteCurrency;
				}else{
					return lppCurrency;
				}
			}
		} 
	}
	
	public static String getRenewalQuoteCurrency(String renewalNum,List renewalList,Quote quote){
		if(quote == null){
			return DraftQuoteConstants.BLANK; 
		}
		if(renewalList != null && renewalList.size() > 0){
			for(int i = 0; i < renewalList.size(); i++ ){
				QuoteOmittedLineItemVO qliVO = (QuoteOmittedLineItemVO) renewalList.get(i);
				if(renewalNum != null && !"".equals(renewalNum)){
					if(renewalNum.equals(qliVO.getRenewalNum())){
						return getCurrencyDesc(quote.getQuoteHeader().getPriceCountry(),qliVO.getCurrency());
					}
				}
			}
		}else{
			return DraftQuoteConstants.BLANK;
		}
		return DraftQuoteConstants.BLANK;
	}
	
	public static String getCurrencyDesc(Country cntry, String currencyCode) {
        String currencyDesc = null;
        if (cntry != null) {
            List currencyList = cntry.getCurrencyList();
            if (currencyList != null && currencyList.size() > 0) {
                for (int i = 0; i < currencyList.size(); i++) {
                    CodeDescObj obj = (CodeDescObj) currencyList.get(i);
                    if (obj != null) {
                        String objKey = obj.getCode();
                        if (objKey != null && objKey.equalsIgnoreCase(currencyCode))
                            currencyDesc = obj.getCodeDesc();
                    }
                }
            }
        }
        return StringUtils.isNotBlank(currencyDesc) ? currencyDesc : currencyCode;
    }
	
	/**
	 * @param Double
	 * calculate the  bid unit price to be the annually unit price
	 */
	public static Double getYtyBidUnitPrice(QuoteLineItem qli){
		Double ytyBidUnitPrice = getAnnualYtyBidUnitPrice(qli);
		if(ytyBidUnitPrice == null){
			return null;
		}
		if (qli.isSetLineToRsvpSrpFlag() && ytyBidUnitPrice > qli.getRenewalRsvpPrice()){
			ytyBidUnitPrice = DecimalUtil.roundAsDouble(qli.getRenewalRsvpPrice(),2);
		}
		return ytyBidUnitPrice;
	}
	
	//This method used in subbmitedSQPartPricePAPAE.JSP, to get all the get sequence num(renewal quote or sales quote) 
		 public static  List<Integer> getAllAdditionlYearsSeqNum(Quote quote) {
			List<Integer> allAdditionalYearItemsSeqNums = new ArrayList<Integer>();
			List mainLineItems = quote.getMasterSoftwareLineItems();
			if(mainLineItems != null && mainLineItems.size() > 0){
				for(Iterator mainIterator = mainLineItems.iterator(); mainIterator.hasNext();) {
					QuoteLineItem mainLineItem = (QuoteLineItem) mainIterator.next();				
						List subLineItems = mainLineItem.getAddtnlYearCvrageLineItems();
						if (subLineItems != null && subLineItems.size() > 0) {
							for (int i = 0; i < subLineItems.size(); i++) {
								QuoteLineItem subLineItem = (QuoteLineItem) subLineItems.get(i);
								allAdditionalYearItemsSeqNums.add(subLineItem.getSeqNum());
							}
						}
				}
			}
			
			return allAdditionalYearItemsSeqNums;
		 }
}