package com.ibm.dsw.quote.draftquote.util.price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author xiaogy@cn.ibm.com
 * 
 * Creation date: Mar 14, 2013
 */
public class OverallGrowthPriceCalculator {
    private static final LogContext logger = LogContextFactory.singleton().getLogContext();
    
	private Quote quote;
	private QuoteHeader header;
	
	private Map<String, QuoteLineItem> reachRSVPItem = new HashMap<String, QuoteLineItem>();

    public OverallGrowthPriceCalculator(Quote quote){
        this.quote = quote;
        this.header = quote.getQuoteHeader();
    }
    
    private List<QuoteLineItem> initLineItemList() throws TopazException{
    	List<QuoteLineItem> lineItems = new ArrayList<QuoteLineItem>();
    	
    	if (quote.getLineItemList() != null){
            for (Iterator iter= quote.getLineItemList().iterator(); iter.hasNext();){
        		QuoteLineItem qli = (QuoteLineItem)iter.next();
        		
        		if(qli.getPartQty() == null || qli.getPartQty().intValue() == 0){
        			continue;
        		}
        		
        		if(!GrowthDelegationUtil.isDisplayLineItemYTY(quote, qli)){
        			continue;
        		}
        		
        		if(qli.getYtyGrowth() == null){
        			continue;
        		}
        		
        		if(GDPartsUtil.getExtndLppPrice(qli, quote.getQuoteHeader()) == null
        				|| GrowthDelegationUtil.getProratedRSVPPrice(qli) == null){
        			continue;
        		}
        		
        		if(qli.getLocalExtProratedPrc() == null || qli.getLocalExtProratedDiscPrc() == null){
        			continue;
        		}
        		
        		clearLineItem(qli);
        		
        		if(QuoteCommonUtil.isPartHasPrice(qli)){
        			lineItems.add(qli);
        		}
            }
        }
    	
    	return lineItems;
    }
    
    private void clearLineItem(QuoteLineItem qli) throws TopazException{
    	qli.setOvrrdExtPrice(null);
    	qli.setOfferIncldFlag(null);
    	qli.setOverrideUnitPrc(null);
    	qli.setLineDiscPct(0);
    	YTYGrowth yty = qli.getYtyGrowth();
    	
    	if(yty != null){
    		yty.setYTYGrowthPct(null);
    		yty.setYtyGrwothRadio(null);
    	}
    }
    
    private String genKey(QuoteLineItem qli){
    	return qli.getPartNum() + qli.getSeqNum();
    }
    
    private double getTotalLPPExtPrice(Collection list){
    	double totalLPPExtPrice = 0;
    	
    	for (Iterator iter= list.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		
    		YTYGrowth yty = qli.getYtyGrowth();
    		Double lppExtPrice = GDPartsUtil.getExtndLppPrice(qli, header);
    		lppExtPrice = getRoundedPrice(lppExtPrice, QuoteCommonUtil.getPartPriceRoundingFactor(header, qli));
    		totalLPPExtPrice += lppExtPrice;
    	}
    	
    	return totalLPPExtPrice;
    }
    
    
    private double getTotalBidExtPrice(Collection list){
    	double totalBidExtPrice = 0;
    	
    	for (Iterator iter= list.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		Double bidExtPrice = qli.getLocalExtProratedDiscPrc();
    		
    		totalBidExtPrice += bidExtPrice;
    	}
    	
    	return totalBidExtPrice;
    }
    
    private double getTotalBidExtPriceFromOUP(Collection list){
    	double totalBidExtPrice = 0;
    	
    	for (Iterator iter= list.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		
    		totalBidExtPrice += (qli.getOverrideUnitPrc() * qli.getPartQty());
    	}
    	
    	return totalBidExtPrice;
    }
    
    public boolean calculate(double ytyGrowth) throws TopazException{
    	int tryLimit = 0;
    	
    	List<QuoteLineItem> origLineItemList = initLineItemList();
    	int maxTryLimit = origLineItemList.size();
    	
    	List<QuoteLineItem> lineItemList = new ArrayList<QuoteLineItem>();
    	lineItemList.addAll(origLineItemList);
    	
    	double initialTotalLPPExtPrice = getTotalLPPExtPrice(origLineItemList);
    	double totalDesiredBidExtPrice = getRoundedPrice(initialTotalLPPExtPrice * (ytyGrowth / 100 + 1), 2); 
    	
    	//try as many times as possible to reach even yty growth
    	while((tryLimit < maxTryLimit) && (lineItemList.size() > 0)){
    		tryLimit++;

        	boolean hasPartReachRSVPLimit = calculateOUPPrice(lineItemList, ytyGrowth);
        	if(!hasPartReachRSVPLimit){
        		//YTY growth is successfully applied to each part, no need to continue
        		break;
        	} else {
        		double totalLPPExtPriceOfAlreadyRSVPParts = getTotalLPPExtPrice(reachRSVPItem.values());
        		double totalLPPExtPriceOfRemainingParts = initialTotalLPPExtPrice - totalLPPExtPriceOfAlreadyRSVPParts;
        		
        		double totalBidExtPriceOfAlreadyRSVPParts = getTotalBidExtPriceFromOUP(reachRSVPItem.values());
        		double totalBixExtPriceOfRemainingParts = totalDesiredBidExtPrice - totalBidExtPriceOfAlreadyRSVPParts;
        		
        		if(DecimalUtil.isEqual(totalLPPExtPriceOfRemainingParts, 0)){
        			//this usually means all items have reached RSVP price, stop processing
        			return true;
        		}
        		ytyGrowth = ((totalBixExtPriceOfRemainingParts / totalLPPExtPriceOfRemainingParts) - 1) * 100;
        		ytyGrowth = getRoundedPrice(ytyGrowth, 2);
        	}
    	}
    	
    	double adjustDesiredBidExtPrice = getTotalBidExtPriceFromOUP(origLineItemList);
        double difference = totalDesiredBidExtPrice - adjustDesiredBidExtPrice;
    	
    	if (!DecimalUtil.isEqual(difference, 0)){
    	    adjustGrowthAsNeeded(origLineItemList, difference);
    	}
    	
        return true;
    }
    
    private Double getRoundedPrice(Double price, int factor){
    	if(price == null){
    		return null;
    	}
	   return DecimalUtil.roundAsDouble(price, factor);
    }
    
    private boolean calculateOUPPrice(List lineItemList, double ytyGrowthPct) throws TopazException{
    	ytyGrowthPct = ytyGrowthPct / 100;
    	
    	boolean hasPartReachRSVPLimit = false;
    	
    	for (Iterator iter= lineItemList.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		
			int factor = QuoteCommonUtil.getPartPriceRoundingFactor(quote.getQuoteHeader(), qli);
	    	
	    	double lppExtPrice = GDPartsUtil.getExtndLppPrice(qli, quote.getQuoteHeader());
	    	double toBeBidExtPrice = lppExtPrice * (1 + ytyGrowthPct);
	    	Double ovrrdUnitPrice = getRoundedPrice(toBeBidExtPrice / qli.getPartQty(), factor);
	    	
	    	Double proratedRSVPPrice = getRoundedPrice(GrowthDelegationUtil.getProratedRSVPPrice(qli), factor);
    		//Make sure override unit price doesn't exceed OUP
    		if((DecimalUtil.isNotEqual(ovrrdUnitPrice, proratedRSVPPrice))
    				&& (ovrrdUnitPrice > proratedRSVPPrice)){
    			ovrrdUnitPrice = proratedRSVPPrice;
    			
    			reachRSVPItem.put(genKey(qli), qli);
    			//remove this part from further calculation
    			iter.remove();
    			
    			hasPartReachRSVPLimit = true;
    		}
	    	
			qli.setOverrideUnitPrc(ovrrdUnitPrice);
			
			qli.setLineDiscPct(PartPriceHelper.calculateDiscount(qli));
			
			YTYGrowth yty = qli.getYtyGrowth();
			if(yty != null){
				yty.setYTYGrowthPct(null);
				yty.setYtyGrwothRadio(DraftQuoteParamKeys.YTY_RADIO_OVERRIDE_PRICE_VALUE);
			}
    	}
    	
    	return hasPartReachRSVPLimit;
    }
    
    protected void adjustGrowthAsNeeded(List lineItemList, double difference) throws TopazException{
        if(difference > 0){
        	processPositveDiff(lineItemList, difference);
        } else {
        	processNegativeDiff(lineItemList, difference);
        }
	   
    }
    
    private void processPositveDiff(List lineItemList, double difference) throws TopazException{
    	List inclusionList = getInclusionList(lineItemList, true);
    	//try to adjust the differences to achieve balance
	    for(Iterator it = inclusionList.iterator(); it.hasNext(); ){
	        
	        if(DecimalUtil.isEqual(difference, 0)){
	            break;
	        }
	        
	        QuoteLineItem qli = (QuoteLineItem)it.next();
	        int factor = QuoteCommonUtil.getPartPriceRoundingFactor(quote.getQuoteHeader(), qli);
	        
	        double ovrrdPrice = qli.getOverrideUnitPrc();
	        double proratedRSVPPrice = getRoundedPrice(GrowthDelegationUtil.getProratedRSVPPrice(qli), factor);
	        
	        if(ovrrdPrice < proratedRSVPPrice){
	        	double remains = (proratedRSVPPrice - ovrrdPrice) * qli.getPartQty();
	        	
	        	if(difference <= remains){
	        		qli.setOverrideUnitPrc(ovrrdPrice + difference / qli.getPartQty());
	        		difference = 0;
	        	} else {
	        		difference -= remains;
	        		qli.setOverrideUnitPrc(proratedRSVPPrice);
	        	}
	        }
	    }
    }
    
    private void processNegativeDiff(List lineItemList, double difference) throws TopazException{
    	List inclusionList = getInclusionList(lineItemList, false);
    	
	    for(Iterator it = inclusionList.iterator(); it.hasNext(); ){
	        
	        if(DecimalUtil.isEqual(difference, 0)){
	            break;
	        }
	        
	        QuoteLineItem qli = (QuoteLineItem)it.next();
	        
	        double ovrrdPrice = qli.getOverrideUnitPrc();
	        
	        if((ovrrdPrice + difference/qli.getPartQty()) >= PartPriceConstants.MINIUM_UNIT_PRICE){
	        	double unitDifference=difference/qli.getPartQty();
	        	double newOvrrdPrice = ovrrdPrice + unitDifference;
	        	qli.setOverrideUnitPrc(newOvrrdPrice);
	        	
	        	difference = 0;
	        	
	        	return;
	        } else {
	        	qli.setOverrideUnitPrc(PartPriceConstants.MINIUM_UNIT_PRICE);
	        	
	        	difference += (ovrrdPrice - PartPriceConstants.MINIUM_UNIT_PRICE) * qli.getPartQty();
	        }
	    }
    }
    
    private List getInclusionList(List lineItemList, boolean forPositive){
    	List inclusionList = new ArrayList();
    	for (Iterator iter= lineItemList.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		if(qli.getOverrideUnitPrc() == null){
    			continue;
    		}
    		
    		Double proratedRSVPPrice = GrowthDelegationUtil.getProratedRSVPPrice(qli);
    		if(proratedRSVPPrice == null){
    			continue;
    		}
    		
    		if(GrowthDelegationUtil.calculateMonths(qli) <= 0){
    			continue;
    		}
    		
    		if(forPositive){
    			if(qli.getOverrideUnitPrc() < proratedRSVPPrice){
        			inclusionList.add(qli);
        		}
    		} else {
    			inclusionList.add(qli);
    		}
    	}
    	
    	return inclusionList;
    }
}
