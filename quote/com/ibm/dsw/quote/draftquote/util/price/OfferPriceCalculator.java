package com.ibm.dsw.quote.draftquote.util.price;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.service.price.DefaultPricingHelper;
import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Creation date: Aug 8, 2007
 */
public class OfferPriceCalculator {
    private static final LogContext logger = LogContextFactory.singleton().getLogContext();
    
	private Quote quote;
	private List lineItems = new ArrayList();
    public OfferPriceCalculator(Quote quote){
        this.quote = quote;
        if (quote.getLineItemList() != null){
            for (Iterator iter= quote.getLineItemList().iterator(); iter.hasNext();){
        		QuoteLineItem qli = (QuoteLineItem)iter.next();
        		
        		if(qli.getPartQty() == null || qli.getPartQty().intValue() == 0){
        			continue;
        		}
        		
        		if((qli.isSaasPart() || qli.isMonthlySoftwarePart()) && 
        				(!PartPriceSaaSPartConfigFactory.singleton().showBidExtndPrice(qli)
        				|| qli.isReplacedPart())){
        			continue;
        		}
        		
        		if(QuoteCommonUtil.isPartHasPrice(qli)){
        			lineItems.add(qli);
        		}
            }
        }
    }
    
    public boolean calculate() throws TopazException{
        int factor = CommonServiceUtil.getEndCustomerRoundingFactor(quote);
    	
    	// #1
    	double total = 0d;
    	int totalQuanity = 0;
    	for (Iterator iter= lineItems.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		
    		if (QuoteCommonUtil.shouldIncludeInOfferPrice(quote, qli)){
    			if((qli.isSaasSubscrptnPart() || 
    					(qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart()))
    				&& 
    				qli.getSaasEntitledTCV() != null){
    				total += qli.getSaasEntitledTCV().doubleValue();
    			}else{
    				total += qli.getLocalExtProratedPrc().doubleValue();
    			}
    			
    			if(qli.getPartQty() != null){
                    totalQuanity+=qli.getPartQty().intValue();
                }
    		}else{
    			total += qli.getLocalExtProratedDiscPrc().doubleValue();
    		}
    	}
    	
    	// #2
    	double totalExclusion = 0d;
    	for (Iterator iter= lineItems.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		    		
    		// for all exclusion
    		
    		if (!QuoteCommonUtil.shouldIncludeInOfferPrice(quote, qli)){
    			totalExclusion += qli.getLocalExtProratedDiscPrc().doubleValue();
    		}
    	}
    	
    	// #3 = #1-#2
    	double adjustTotal = total - totalExclusion;
    	
    	// #4
    	double adjustOffer = quote.getQuoteHeader().getOfferPrice().doubleValue() - totalExclusion;
    	
    	double miniumOfferPrice = PartPriceConstants.MINIUM_UNIT_PRICE * totalQuanity;
    	
    	if (adjustOffer < miniumOfferPrice){
//    	  	if offer price is not qualified, clear offer;
            for( Iterator iter= lineItems.iterator(); iter.hasNext();){
            	QuoteLineItem qli = (QuoteLineItem)iter.next();
				qli.setOvrrdExtPrice(null);
            	qli.setOfferIncldFlag(null); 
            }
            quote.getQuoteHeader().setOfferPrice(null);
            logger.debug(this,"Offer price is " + quote.getQuoteHeader().getOfferPrice() );
            logger.debug(this,"Total excludsion is " + quote.getQuoteHeader().getOfferPrice() );
            logger.debug(this,"Offer price is cleared" );
    	    return false;
    	}
    	
    	// #5
    	double discount = (adjustTotal - adjustOffer)/adjustTotal; 
    	
    	for (Iterator iter= lineItems.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		if (qli.getPartQty() == null)
    		    continue;    	
    		// for all inclusion
    		if (QuoteCommonUtil.shouldIncludeInOfferPrice(quote, qli)){
    			calculateDiscPrice(qli, discount, factor);
    		}
    	}
    	
    	if(needCallPricingServiceAgain()){
    		try {
	    		PricingRequest pr = new PricingRequest(quote, QuoteConstants.QUOTE_DOC_CAT);
	
	            if (QuoteConstants.FULFILLMENT_DIRECT.equals(quote.getQuoteHeader().getFulfillmentSrc())) {
	                pr.setChannel(false);
	            } else if (QuoteConstants.FULFILLMENT_CHANNEL.equals(quote.getQuoteHeader().getFulfillmentSrc())) {
	                pr.setChannel(true);
	            }
	            DefaultPricingHelper serviceHelper = null;
	            pr.setSendOvrrdExtndPrice(true); 
	       	 	serviceHelper = new DefaultPricingHelper(pr);
	            serviceHelper.getPrice();   
	            
	            if(!serviceHelper.isPriceOk()){
	            	logger.info(this, "All line item don't have a price");
	                return false;
	            }
    		} catch (Exception e) {
    			logger.error(this, "Failed in Price calculation:" + e.getMessage());
                throw new TopazException(e);
            }
    	}
    	
    	double adjustOfferBySum = 0d;
    	for (Iterator iter= lineItems.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		if (qli.getPartQty() == null)
    		    continue;    		
    		
    		// for all inclusion
    		if (QuoteCommonUtil.shouldIncludeInOfferPrice(quote, qli)){
    			if((qli.isSaasSubscrptnPart() || 
    					(qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart()))
        			&& 
        			qli.getSaasEntitledTCV() != null){
    				adjustOfferBySum += qli.getSaasBidTCV().doubleValue();
    			}else{
    				adjustOfferBySum += qli.getLocalExtProratedDiscPrc().doubleValue();
    			}
    		}
    	}
    	
    	double difference = adjustOffer - adjustOfferBySum;
    	if (!DecimalUtil.isEqual(difference, 0)){
    	    adjustOfferPrice(difference, factor);
    	}
    	
        return true;
    }
    
    private void calculateDiscPrice(QuoteLineItem qli, double discount, int factor) throws TopazException{
		if(canSetBidUnitPrice(qli)){
			//update unit price
		    double localUnitProratedDiscPrc = qli.getLocalUnitProratedPrc().doubleValue() * (1 - discount);
		    //0.01 is the minimum price for line item 
		    double temp = DecimalUtil.roundAsDouble(localUnitProratedDiscPrc, factor);
		    if(DecimalUtil.isEqual(temp, 0)){
		        temp = PartPriceConstants.MINIUM_UNIT_PRICE;
		    }
			qli.setLocalUnitProratedDiscPrc(new Double(temp));
			// #7 update extend price
			qli.setLocalExtProratedDiscPrc(new Double(qli.getLocalUnitProratedDiscPrc().doubleValue() * qli.getPartQty().intValue()));
			
			qli.setLocalExtPrc(qli.getLocalExtProratedDiscPrc());
		} else {
			//saas parts with bid extended prices only
		    double localExtProratedDiscPrc = qli.getLocalExtProratedPrc().doubleValue() * (1 - discount);
		    double temp = DecimalUtil.roundAsDouble(localExtProratedDiscPrc, factor);
		    if(DecimalUtil.isEqual(temp, 0)){
		        temp = PartPriceConstants.MINIUM_UNIT_PRICE;
		    }
			qli.setLocalExtProratedDiscPrc(new Double(temp));
			qli.setLocalExtPrc(qli.getLocalExtProratedDiscPrc());
		}
		
		if(canSetOvrrdExtPrice(qli)){
			qli.setOvrrdExtPrice(qli.getLocalExtProratedDiscPrc());
		}
		
		logger.debug(this,qli.getPartNum() + "," + qli.getSeqNum() + " Ovrrd Ext Price : " + qli.getOvrrdExtPrice());		
		
		qli.setOfferIncldFlag(Boolean.TRUE);
		qli.setLineDiscPct(PartPriceHelper.calculateDiscount(qli));
    }
    
    protected void adjustOfferPrice(double difference, int factor) throws TopazException{
        List inclusionList = getInclusionList();
        
	    // find first included part to apply difference
		QuoteLineItem firstQli = findFirstIncludePart(inclusionList, difference, factor);
		if (firstQli != null){
		    applyDiff(firstQli, difference, factor);
		    
			return;
		} else {
		    
		    //up here, the difference must be a negative value
		    for(Iterator it = inclusionList.iterator(); it.hasNext(); ){
		        
		        if(DecimalUtil.isEqual(difference, 0)){
		            break;
		        }
		        QuoteLineItem qli = (QuoteLineItem)it.next();
		        if(qli.isSaasSubscrptnPart() || (qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart())){
		        	continue;
		        }
		        
		        double extProratedDiscPrc = qli.getLocalExtProratedDiscPrc().doubleValue();
		        
		        if(DecimalUtil.roundAsDouble(extProratedDiscPrc + difference, factor) < qli.getPartQty().intValue() * PartPriceConstants.MINIUM_UNIT_PRICE){
		            //apply part of the difference to come up with 100% discount
		        	
		        	if(canSetBidUnitPrice(qli)){
		    			qli.setLocalUnitProratedDiscPrc(new Double(0.01));
		        	}
	    			
	    			qli.setLocalExtProratedDiscPrc(new Double(qli.getLocalUnitProratedDiscPrc().doubleValue() * qli.getPartQty().intValue()));
	    			
	    			qli.setLocalExtPrc(qli.getLocalExtProratedDiscPrc());
	    			
	    			if(canSetOvrrdExtPrice(qli)){
		    			qli.setOvrrdExtPrice(qli.getLocalExtProratedDiscPrc());
	    			}
	    			
	    			qli.setOfferIncldFlag(Boolean.TRUE);
	    			qli.setLineDiscPct(getLineDiscPct(qli));
	    			
			        difference += qli.getLocalExtProratedDiscPrc().doubleValue();
		        } else {
		            applyDiff(qli, difference, factor);		           
		            
		            break;
		        }
		    }
		}
    }
    
    private void applyDiff(QuoteLineItem qli, double difference, int factor) throws TopazException{
        double proratedDiscExtPrc = DecimalUtil.roundAsDouble(
                                         qli.getLocalExtProratedDiscPrc().doubleValue() + difference, 
                                         factor);

        qli.setLocalExtProratedDiscPrc(new Double(proratedDiscExtPrc));
		
        qli.setLocalExtPrc(qli.getLocalExtProratedDiscPrc());
        if(canSetOvrrdExtPrice(qli)){
            qli.setOvrrdExtPrice(qli.getLocalExtProratedDiscPrc());
        }
        
        if(canSetBidUnitPrice(qli)){
            qli.setLocalUnitProratedDiscPrc(new Double(DecimalUtil.roundAsDouble(
                    qli.getLocalExtProratedDiscPrc().doubleValue()
                    / qli.getPartQty().intValue(), factor)));
        }
		
        if(DecimalUtil.isNotEqual(qli.getLocalExtProratedPrc().doubleValue(),0.0)){
		    qli.setLineDiscPct(getLineDiscPct(qli));
		}
    }
    
    private double getLineDiscPct(QuoteLineItem qli){
    	return 100 * roundDiscount(
                (qli.getLocalExtProratedPrc().doubleValue()
                        - qli.getLocalExtProratedDiscPrc().doubleValue())
                 / qli.getLocalExtProratedPrc().doubleValue());
    }
    
    private boolean canSetOvrrdExtPrice(QuoteLineItem qli){
    	if(qli.isSaasPart() || qli.isMonthlySoftwarePart()){
    		return PartPriceSaaSPartConfigFactory.singleton().canInputExtndOvrrdPrice(qli);
    	} else {
    		return true;
    	}
    }
    
    private boolean canSetBidUnitPrice(QuoteLineItem qli){
    	return ((qli.isSaasPart() || qli.isMonthlySoftwarePart()) && PartPriceSaaSPartConfigFactory
    			                        .singleton().showBidUnitPrice(qli)
    	          || (!qli.isSaasPart() && !qli.isMonthlySoftwarePart()));
    }
    
    private List getInclusionList(){
    	List inclusionList = new ArrayList();
    	for (Iterator iter= lineItems.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		
    		if (QuoteCommonUtil.shouldIncludeInOfferPrice(quote, qli)){
    		    inclusionList.add(qli);
    		}
    	}
    	
    	// sort by seq number asc
    	Collections.sort(inclusionList, new Comparator(){
            public int compare(Object o1, Object o2) {
                QuoteLineItem item1 = (QuoteLineItem)o1;
                QuoteLineItem item2 = (QuoteLineItem)o2;
                return item1.getSeqNum() - item2.getSeqNum();                
            }    	    
    	});
    	
    	return inclusionList;
    }
    
    
    private QuoteLineItem findFirstIncludePart(List inclusionList, double difference, int factor){
    	if(difference >= 0){
    		for (Iterator iter= inclusionList.iterator(); iter.hasNext();){
    	        QuoteLineItem qli = (QuoteLineItem)iter.next();
    	        if(!qli.isSaasSubscrptnPart() || (qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart())){
    	        	return qli;
    	        }
    		}
    	    return null;
    	} else {
    	    // for each included items , find the first with price greater than Math.abs(difference)
    	    for (Iterator iter= inclusionList.iterator(); iter.hasNext();){
    	        QuoteLineItem qli = (QuoteLineItem)iter.next();
    	        if(qli.isSaasSubscrptnPart() || (qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart())){
    	        	continue;
    	        }
    	        if (qli.getLocalExtProratedDiscPrc().doubleValue() > Math.abs(difference)
    	                && DecimalUtil.roundAsDouble(qli.getLocalExtProratedDiscPrc().doubleValue() + difference, factor) >= (qli.getPartQty().intValue() * PartPriceConstants.MINIUM_UNIT_PRICE)){
    	            logger.debug(this,"The first line item for applying offer price difference is "+qli.getPartNum()+"," + qli.getSeqNum());
    	            return qli;
    	        }
    	    }
    	    
		    logger.debug(this, "trying to find a part to apply the difference returns null");
    	    return null;
    	}
    }
    
    private double roundDiscount(double src){
    	return ((double)Math.round(100000 * src))/100000; 
    }
    
    private boolean needCallPricingServiceAgain(){
    	for (Iterator iter= lineItems.iterator(); iter.hasNext();){
    		QuoteLineItem qli = (QuoteLineItem)iter.next();
    		if(qli.isSaasSubscrptnPart() || (qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart())){
    			return true;
    		}
    	}
    	return false;
    }
    
}
