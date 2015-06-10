package com.ibm.dsw.quote.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.domain.YTYGrowthFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.date.PartTypeChecker;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2011 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>GDPartsUtil<code> class.
 *    
 * @author: wangxucd@cn.ibm.com
 * 
 * Creation date: November 27, 2013
 */

public class GDPartsUtil extends GrowthDelegationUtil{
	// revenue streams of license parts
	private static final String ELIGIBLE_REVENUE_STREAM_LCMNTSPT = "LCMNTSPT";
	private static final String ELIGIBLE_REVENUE_STREAM_MNTSPT = "MNT&SPT";
	private static final String ELIGIBLE_REVENUE_STREAM_TRDMNTSP = "TRDMNTSP";
	private static final String ELIGIBLE_REVENUE_STREAM_CMPTRDUP = "CMPTRDUP";

	// revenue streams of renewal parts
	private static final String ELIGIBLE_REVENUE_STREAM_RNWMNTSP = "RNWMNTSP";

	// associate license parts to maintenance parts
	public static void checkLicAndMaitAssociation(Quote quote) throws QuoteException, TopazException{
			if(onlyForPAPAE(quote) && isContainMaitLineItems(quote)){
				PartTypeChecker partTypeChecker = new PartTypeChecker(quote);
				partTypeChecker.checkLicAndMaitAssociation();
				deleteUnassociationYty(quote);
			}
	}

	private static void deleteUnassociationYty(Quote quote) throws TopazException, QuoteException {
	   	List mainLineItems = quote.getMasterSoftwareLineItems();
	   	List<QuoteLineItem> needDeleteYTYLineItems = new ArrayList<QuoteLineItem>();
    	Iterator it = mainLineItems.iterator();
    	while(it.hasNext()){
    		QuoteLineItem mainLineItem = (QuoteLineItem) it.next();
    		if(isTypeEligibleRenewalPart(mainLineItem) && !mainLineItem.isAddedToLicPart()){
    			YTYGrowth ytyGrowth = mainLineItem.getYtyGrowth();
    			if(ytyGrowth != null){
    				ytyGrowth.delete();
    				mainLineItem.setYtyGrowth(null);
    			}
    			if(mainLineItem.getAddtnlMaintCvrageQty() > 0){
    				List subAdditionalYearLineItems = mainLineItem.getAddtnlYearCvrageLineItems();
    		    	for (int i = 0; i < subAdditionalYearLineItems.size(); i++) {
    					QuoteLineItem subLineItem = (QuoteLineItem) subAdditionalYearLineItems.get(i);
    					if(subLineItem.getYtyGrowth() != null){
    						subLineItem.getYtyGrowth().delete();
    						subLineItem.setYtyGrowth(null);
    					}
    		    	}
    			}
    		}
    	}
	}

	// check if this is a license part
	public static boolean isLicensePart(QuoteLineItem qli) {
		return ELIGIBLE_REVENUE_STREAM_LCMNTSPT.equals(qli.getRevnStrmCode())
				|| ELIGIBLE_REVENUE_STREAM_MNTSPT.equals(qli.getRevnStrmCode())
				|| ELIGIBLE_REVENUE_STREAM_TRDMNTSP.equals(qli.getRevnStrmCode())
				|| ELIGIBLE_REVENUE_STREAM_CMPTRDUP.equals(qli.getRevnStrmCode());
	}

	// check if this is a renewal/maintenance part
	public static boolean isRenewalPart(QuoteLineItem qli) {
		return ELIGIBLE_REVENUE_STREAM_RNWMNTSP.equals(qli.getRevnStrmCode());
	}
	
	// check if eligible for 14.2 growth delegation for part types
	public static boolean isPartTypeEligibleForRenewalGrowthDelegation(QuoteLineItem qli) {
        return !qli.isApplncPart() 
	        && !qli.isSaasPart() 
	        && !qli.getPartDispAttr().isFtlPart()
	        && !qli.isMonthlySoftwarePart();
	}
	
	// check if the quote has an eligible renewal/maintenance part for growth delegation
	// but here no check for quote info
	public static boolean isContainEligibleRenewalLineItem(Quote quote) {
        List lineItems = quote.getLineItemList();
        if(lineItems != null){
            int size = lineItems.size();
    		for (int i = 0; i < size; i++) {
                QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
                if(isEligibleRenewalPart(item)){
                	return true;
                }
            }
       }
       return false; 
	}
	
	// check if the quote is eligible for growth delegation
	public static boolean isQuoteEligibleForRenewalGrowthDelegation(Quote quote) {
        return  onlyForPAPAE(quote) && isContainEligibleRenewalLineItem(quote); 
	}
	
	// check if the quote has any renewal/maintenance part before association for potential growth delegation
	public static boolean isContainMaitLineItems(Quote quote) {
        List lineItems = quote.getLineItemList();
        if(lineItems != null){
            int size = lineItems.size();
    		for (int i = 0; i < size; i++) {
                QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
                if(isTypeEligibleRenewalPart(item)){
                	return true;
                }               
            }
       }
       return false; 
	}
	
	// check if this is an eligible master renewal part for growth delegation
	public static boolean isEligibleMasterRenewalPart(QuoteLineItem qli) {
		if(isEligibleRenewalPart(qli)){
			QuoteLineItem licItem = (QuoteLineItem) qli.getAddedToLicParts().get(0);
			return isLicensePart(licItem);
		}
		return false;
	}
	
	// check if this is an eligible additional/additional like renewal part for growth delegation
	public static boolean isEligibleAdditionalLikeRenewalPart(QuoteLineItem qli) {
		if(isEligibleRenewalPart(qli)){
			QuoteLineItem renewalItem = (QuoteLineItem) qli.getAddedToLicParts().get(0);
			return isRenewalPart(renewalItem);
		}
		return false;
	}
	
	// check if the master/additional renewal part is eligible for growth delegation
	public static boolean isEligibleRenewalPart(QuoteLineItem qli) {
		return qli.isAddedToLicPart();
	}
	
	// check if the part is a renewal part not from a renewal quote and has eligible types 
	public static boolean isTypeEligibleRenewalPart(QuoteLineItem qli){
		return isPartTypeEligibleForRenewalGrowthDelegation(qli) 
				&& GDPartsUtil.isRenewalPart(qli) 
				&& StringUtils.isBlank(qli.getRenewalQuoteNum());
	}
	
	// check if the part is a license part not from a renewal quote and has eligible types 
	public static boolean isTypeEligibleLicensePart(QuoteLineItem qli){
		return isPartTypeEligibleForRenewalGrowthDelegation(qli) 
				&& GDPartsUtil.isLicensePart(qli) 
				&& StringUtils.isBlank(qli.getRenewalQuoteNum());
	}
	
	// process 14.2 GD
	public static void processForGrowthDelegation(Quote quote) throws TopazException, QuoteException {
			PartPriceProcess process = PartPriceProcessFactory.singleton().create();
			// get all license and master renewal parts
		   	List lineItems = quote.getSoftwareLineItems();
	    	Iterator it = lineItems.iterator();
	    	while(it.hasNext()){
	    		QuoteLineItem qli = (QuoteLineItem) it.next();
	    		// calculate and persist yty for master renewal parts
	    		if(isEligibleRenewalPart(qli)){
	    			calcYTYAtLineItem(quote, qli);	    			
	    			//process.doCalculateYtyGrowth(qli, quote.getQuoteHeader());
	    		}
	    	}
	}
	
	// yty default
	public static void initYtyGrowth(Quote quote, QuoteLineItem lineItem) throws TopazException{
		YTYGrowth ytyGrowth = lineItem.getYtyGrowth();
		// initial yty
		if(ytyGrowth == null){
			ytyGrowth = YTYGrowthFactory.singleton().createYTYGrowth(quote.getQuoteHeader().getWebQuoteNum(), lineItem.getSeqNum());

			// yty % = null
	   		ytyGrowth.setYTYGrowthPct(null);
	   		ytyGrowth.setManualLPP(null);
	   		ytyGrowth.setYtySourceCode(DraftQuoteConstants.LPP_SOURCE_DEFAULT);
		
	   		lineItem.setYtyGrowth(ytyGrowth);
	   		calcYTYStatusCode(quote, lineItem);
		}
	}
	
    // get the sequence numbers for the additional years
	public static List<Integer> getAdditionalYearItemsSeqNums(Quote quote) {
		List<Integer> additionalYearItemsSeqNums = new ArrayList<Integer>();
		
		List mainLineItems = quote.getMasterSoftwareLineItems();
		if(mainLineItems != null && mainLineItems.size() > 0){
			for(Iterator mainIterator = mainLineItems.iterator(); mainIterator.hasNext();) {
				QuoteLineItem mainLineItem = (QuoteLineItem) mainIterator.next();				
				if (isEligibleRenewalPart(mainLineItem)) {
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
	
	private static Double getAnnualizedSplitBidExtPrice(QuoteLineItem qli){
		Double bidExtPrice = qli.getSplitBidExtPriceIfApplicable();
		
		if(bidExtPrice == null){
			return null;
		} else {
			return getAnnualizedPrice(qli, bidExtPrice);
		}
	}
	
	private static Double calcExtndLppPrice(QuoteLineItem qli){
		List priorQliList = qli.getAddedToLicParts();
		
		if(priorQliList == null && priorQliList.size() == 0){
			//this is a part at the beginning of the addedToLicPartsList
			//for 14.2 GD requirements, this must be a license part
			return getAnnualizedSplitBidExtPrice(qli);
		} else {
			//line item depends on 'added to parts', need to calculate the weighted average price
			Double totalPriorAnnualizedBidExtPrice = null;
			QuoteLineItem priorQli = null;
			Double priorAnnualizedBidExtPrice = null;
			int totalQty = 0;
			
			YTYGrowth qliYty = qli.getYtyGrowth();
			
			if(qliYty != null && qliYty.isUnitPriceManuallyEntered()){
				for(Iterator priorItr = priorQliList.iterator(); priorItr.hasNext(); ){
					priorQli = (QuoteLineItem)priorItr.next();
					
					if(priorQli.getPartQty() != null && priorQli.getPartQty() > 0){
						totalQty += priorQli.getPartQty();
					}
				}
				
				if(totalQty > 0){
					totalPriorAnnualizedBidExtPrice = qliYty.getManualLPP() * totalQty;
				}
				
			} else {
				for(Iterator priorItr = priorQliList.iterator(); priorItr.hasNext(); ){
					priorQli = (QuoteLineItem)priorItr.next();
					
					if(priorQli.getPartQty() != null && priorQli.getPartQty() > 0){
						YTYGrowth yty = priorQli.getYtyGrowth();
						
						if(yty != null && yty.isPctManuallyEntered()){
							//manually entered YTY, need to calculate the LPP price from "added to" parts
							//this is a dependency chain, end is: a license part, a part whose YTY is not manually entered
							priorAnnualizedBidExtPrice = calcExtndLppPrice(priorQli);
							if(priorAnnualizedBidExtPrice != null && yty.getYTYGrowthPct() != null){
								priorAnnualizedBidExtPrice = applyYTYGrowth(priorAnnualizedBidExtPrice, yty.getYTYGrowthPct());
							}
						} else {
							//price is fixed up here, take the annualized price is ok
							priorAnnualizedBidExtPrice = getAnnualizedSplitBidExtPrice(priorQli);
						}
						
						if(priorAnnualizedBidExtPrice != null){
							if(totalPriorAnnualizedBidExtPrice == null){
								totalPriorAnnualizedBidExtPrice = 0.0;
							}
							
							totalPriorAnnualizedBidExtPrice += priorAnnualizedBidExtPrice;
						}
					}
				}
			}
			
			return totalPriorAnnualizedBidExtPrice;
		}
	}
	
	private static Double getAnnualizedPrice(QuoteLineItem qli, Double price){
    	double months = calculateMonths(qli);
    	
    	//months <= 0 should never happen, add check here just in case of anything is wrong
		if(price == null || months <= 0){
			return null;
		}
		
		return DecimalUtil.roundAsDouble(price * 12 / months, 2);
	}

	public static Double getExtndLppPrice(QuoteLineItem item, QuoteHeader header){
    	double months = calculateMonths(item);
    	
		if(months <= 0){
			return null ;
		}
		
		double extendedPrice = 0.0;
    	
    	if(item.isAddedToLicPart()){
    		Double lppPrice = calcExtndLppPrice(item);
    		if(lppPrice == null){
    			return null;
    		} else {
    			extendedPrice = lppPrice;
    		}
    	} else {
    		Double unitPrice = getUnitLppPrice(item, header);
        	
        	if(unitPrice == null){
        		return null;
        	}
        	
    		double qty = 0;
    		YTYGrowth yty = item.getYtyGrowth();
    		if(header.isSubmittedQuote()){
    			qty = yty == null? 0.0 : yty.getRenwlQliQty();
    		} else {
    			qty = item.getOpenQty();
    		}
    		
    		if(qty == 0){
    			return null;
    		}
    		
    		extendedPrice = unitPrice * qty;
    	}
    	
    	return extendedPrice * (months / 12);
    }
	
	private static boolean isPriceChangedDueToManualYTY(QuoteLineItem qli, QuoteHeader header, int subIdx) throws TopazException{
		YTYGrowth yty = qli.getYtyGrowth();
		
		boolean needCallAgain = false;
		
		//YTY growth value is manually entered
		if (yty != null && yty.isPctManuallyEntered() && yty.getYTYGrowthPct() != null) {
			Double desiredBidExtPrice = getDesiredBidExtPriceFromYTYGrowth(yty.getYTYGrowthPct(), qli, header, subIdx);
			
			Double entitledExtndPrice = qli.getLocalExtProratedPrc();
			
			if(desiredBidExtPrice != null && entitledExtndPrice != null 
					&& qli.getPartQty() != null && qli.getPartQty() != 0) {
				desiredBidExtPrice = DecimalUtil.roundAsDouble(desiredBidExtPrice, 2);
				entitledExtndPrice = DecimalUtil.roundAsDouble(entitledExtndPrice, 2);
				
				if(desiredBidExtPrice > entitledExtndPrice) {
					//requested price is greater than entitled price, this will cause negative discount
					//so transform it to override unit price
					double desiredBidUnitPrice = desiredBidExtPrice / qli.getPartQty();
					Double ovrrdUnitPrice = qli.getOverrideUnitPrc();
					if(ovrrdUnitPrice == null || DecimalUtil.isNotEqual(ovrrdUnitPrice, desiredBidUnitPrice)){
						qli.setOverrideUnitPrc(desiredBidUnitPrice);
						
						needCallAgain = true;
					}
				} else {
					if(calculateDiscFromYtyGrwthPctAtLineItem(qli, header, subIdx)){
						needCallAgain = true;
					}
				}
			}
		}
		
		List addiList = qli.getAddtnlYearCvrageLineItems();
		
		if(addiList != null && addiList.size() > 0){
			for(int i = 0; i < addiList.size(); i++){
				QuoteLineItem subQli = (QuoteLineItem)addiList.get(i);
				
				if(qli.isReferenceToRenewalQuote()){
					if(isPriceChangedDueToManualYTY(subQli, header, i)){
						needCallAgain = true;
					}
				} else {
					if(isPriceChangedDueToManualYTY(subQli, header)){
						needCallAgain = true;
					}
				}
			}
		}
		
		return needCallAgain;
	}
	
	//for 14.2 GD requirements, calculate prior S&S unit price
	public static void calcYTYAtLineItem(Quote quote, QuoteLineItem qli) throws TopazException{
		initYtyGrowth(quote, qli);
		YTYGrowth yty = qli.getYtyGrowth();
		calcYTYStatusCode(quote, qli);
				
		Double unitPrice = null;
		int totalQty = 0;
		Double totalPriorBidExtPrice = null;
		List<QuoteLineItem> addedToList = qli.getAddedToLicParts();
		
		for(QuoteLineItem addedToQli : addedToList){
			if(addedToQli.getPartQty() != null && addedToQli.getPartQty() > 0){
				Double annualizedBidExtPrice = getAnnualizedSplitBidExtPrice(addedToQli);
				
				if(annualizedBidExtPrice != null){
					totalQty += addedToQli.getPartQty();
					if(totalPriorBidExtPrice == null){
						totalPriorBidExtPrice = 0.0;
					}
					totalPriorBidExtPrice += annualizedBidExtPrice;
				}
			}
		}
		// use manually entered prior ss price to get bid extended price of prior year and calculate yty growth
		if(totalQty != 0 && totalPriorBidExtPrice != null){
			unitPrice = DecimalUtil.roundAsDouble(totalPriorBidExtPrice / totalQty, 2);
			yty.setSysComputedPriorPrice(unitPrice);
		}
		if(yty.isUnitPriceManuallyEntered()){
			totalPriorBidExtPrice = yty.getManualLPP() * totalQty;
			
		// set calculated prior ss price		
		} else {	
				yty.setManualLPP(unitPrice);
		}
		// calculate yty if yty growth is not manually entered
		if(!yty.isPctManuallyEntered()){
			Double currentBidExtPrice = getAnnualizedPrice(qli, qli.getLocalExtProratedDiscPrc());
		
			if(totalPriorBidExtPrice != null && totalPriorBidExtPrice.doubleValue() != 0d && currentBidExtPrice != null){
				Double ytyGrowthPct = DecimalUtil.roundAsDouble((currentBidExtPrice / totalPriorBidExtPrice - 1) * 100, 2);
				yty.setYTYGrowthPct(ytyGrowthPct);
			}
		}
	}
	
	private static void calcYTYStatusCode(Quote quote, QuoteLineItem qli) throws TopazException{
		YTYGrowth yty = qli.getYtyGrowth();
		
		// calculate the reason code for implied growth and overall growth
		Map<Integer, Integer> map = getGrowthDelegationEligiblilityReasonCode(quote, qli);
		int reasonCodeForImpliedGrowth = map.get(IMPLIED_GROWTH);
		int reasonCodeForOveralGrowth = map.get(OVERALL_GROWTH);
		yty.setIncludedInOverallYTYGrowthFlag(reasonCodeForOveralGrowth);
		yty.setIncludedInImpliedYTYGrowthFlag(reasonCodeForImpliedGrowth);		
		map.clear();
		map = null;
	}

	// Check whether need to recalculate growth delegation for line item.
	private static boolean isPriceChangedDueToManualYTY(QuoteLineItem qli, QuoteHeader header) throws TopazException{
		return isPriceChangedDueToManualYTY(qli, header, -1);
	}

	public static boolean needRecalculateForGrowthDelegation(Quote quote) throws TopazException{
    	boolean needCallAgain = false;
    	
    	List list = quote.getMasterSoftwareLineItems();
    	if(list == null || list.size() == 0){
    		return false;
    	}
    	
    	QuoteHeader header = quote.getQuoteHeader();
    	for(Iterator it = list.iterator(); it.hasNext(); ){
    		QuoteLineItem qli = (QuoteLineItem)it.next();
    		
    		if (isPriceChangedDueToManualYTY(qli, header)) {
				needCallAgain = true;
			}
    	}
    	
    	return needCallAgain;
    }
	
	private static Double applyYTYGrowth(Double lppExtPrice, Double ytyDiscPct){
		return (ytyDiscPct / 100 + 1) * lppExtPrice;
	}
	
	//this method should only be used before GD pricing service is called
	private static Double getExtndLppForAddiYear(QuoteLineItem subQli, QuoteHeader header, int subIdx){
		Double lppExtPrice = null;
		QuoteLineItem parent = subQli.getParentMasterPart();
		
		if(subIdx == 0){
			//this is the first additional year, need to take price from the parent part
			YTYGrowth yty = parent.getYtyGrowth();
			
			//if parent part has manual YTY growth, need to get the parent part desired bid ext price
			//as LPP
			if(yty != null && yty.isPctManuallyEntered()){
				lppExtPrice = getDesiredBidExtPriceFromYTYGrowth(yty.getYTYGrowthPct(), parent, header);
			} else {
				lppExtPrice = parent.getLocalExtProratedDiscPrc();
			}
			
			if(lppExtPrice != null){
				lppExtPrice = getAnnualizedPrice(parent, lppExtPrice);
			}
		} else {
			QuoteLineItem prevYearQli = (QuoteLineItem)parent.getAddtnlYearCvrageLineItems().get(subIdx - 1);
			
			YTYGrowth yty = prevYearQli.getYtyGrowth();
			if(yty != null && yty.isPctManuallyEntered()){
				lppExtPrice = getExtndLppForAddiYear(prevYearQli, header, subIdx - 1);
				lppExtPrice = applyYTYGrowth(lppExtPrice, yty.getYTYGrowthPct());
				
			} else {
				lppExtPrice = prevYearQli.getLocalExtProratedDiscPrc();
			}
		}
		
		return lppExtPrice;
	}
	
	public static Double getDesiredBidExtPriceFromYTYGrowth(Double ytyDiscPct, QuoteLineItem qli, QuoteHeader header, int subIdx){
		//subIdx will be a value > 0  if : the current part is additional year of a master part
		//And master part is added from renewal quote
		
		Double lppExtPrice = null;
		if(subIdx == -1){
			lppExtPrice = getExtndLppPrice(qli, header);
		} else {
			lppExtPrice = getExtndLppForAddiYear(qli, header, subIdx);
		}
		
		if(lppExtPrice == null || ytyDiscPct == null){
			return null;
		}
		
		return applyYTYGrowth(lppExtPrice, ytyDiscPct);
	}

	private static Double getDesiredBidExtPriceFromYTYGrowth(Double ytyDiscPct, QuoteLineItem qli, QuoteHeader header){
		return getDesiredBidExtPriceFromYTYGrowth(ytyDiscPct, qli, header, -1);
	}
	
	public static double calculateDisc(Double ytyDiscPct, QuoteLineItem qli, QuoteHeader header){
		return calculateDisc(ytyDiscPct, qli, header);
	}

	public static double calculateDisc(Double ytyDiscPct, QuoteLineItem qli, QuoteHeader header, int subIdx){
		Double entitledExtndPrice = qli.getLocalExtProratedPrc();
		Double desiredBidExtPrice = getDesiredBidExtPriceFromYTYGrowth(ytyDiscPct, qli, header, subIdx);
		
		if(desiredBidExtPrice == null || entitledExtndPrice == null){
			return 0.0;
		}
		
		return (1 - desiredBidExtPrice / entitledExtndPrice) * 100;
	}
	
	public static boolean calculateDiscFromYtyGrwthPctAtLineItem(QuoteLineItem item, QuoteHeader header, int subIdx) throws TopazException{
		
		YTYGrowth yty = item.getYtyGrowth();
		if(yty == null){
			return false;
		}
		
		if(yty.isPctManuallyEntered() && yty.getYTYGrowthPct() != null){
			//recalculate discount from yty growth pct
			
			double discountPercent = calculateDisc(yty.getYTYGrowthPct(), item, header, subIdx);
			
			if(DecimalUtil.isNotEqual(discountPercent, item.getLineDiscPct())){
				item.setLineDiscPct(discountPercent);
				item.setOverrideUnitPrc(null);
				
				return true;
			}
		}
		
		return false;
	}
	
	public static Double getRenewalRsvpPrice(Double priceToSet, QuoteLineItem lineItem) {
		Double returnPrice = lineItem.getRenewalRsvpPrice();
		if (lineItem.isReplacedPart()) {
			return returnPrice;
		}
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
			returnPrice = priceToSet;
		}
		return returnPrice;
	}
}
