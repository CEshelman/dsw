package com.ibm.dsw.quote.draftquote.util.price;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteRequest;
import com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteResponse;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author xiaogy@cn.ibm.com
 */
public class IncreasePricingCalculator extends PriceCalculator{
    private static final LogContext logger = LogContextFactory.singleton().getLogContext();
    
	private Quote quote;
	private SubmittedQuoteRequest request;
	private SubmittedQuoteResponse response;
	
    private double nonSpecialBidLineItemPriceTot;
    private double oriSpecialBidLineItemPriceTot;
    
    private Map originalLineItemMap = new HashMap();
    
	public IncreasePricingCalculator(SubmittedQuoteRequest request, SubmittedQuoteResponse response){
        super(request.getQuote());
	    
	    this.request = request;
        this.response = response;
	    this.quote = request.getQuote();
        
        init(request.getOriginalLineItemList());
    }
	
	private boolean applicableForIncrease(QuoteLineItem qli){
		if(!qli.isSaasPart() && !qli.isMonthlySoftwarePart()){
			return true;
		}
		
		if(!PartPriceSaaSPartConfigFactory.singleton().showBidExtndPrice(qli)){
			return false;
		}
		
		return true;
	}
	private void init(List origQuoteLineItemList){
	    
	    double bidPriceTot = 0;
	    
	    for(Iterator it = origQuoteLineItemList.iterator(); it.hasNext(); ){
	        QuoteLineItem qli = (QuoteLineItem)it.next();
	        
	        if(!applicableForIncrease(qli)){
	        	continue;
	        }
	        if(qli.isSaasTcvAcv()){
	        	bidPriceTot += qli.getSaasBidTCV() == null ? 0.0 : qli.getSaasBidTCV().doubleValue();
	        }else{
	        	bidPriceTot += qli.getLocalExtProratedDiscPrc() == null ? 0.0 : qli.getLocalExtProratedDiscPrc().doubleValue();
	        }
	        
	        if(exclude(qli)){
		        if(qli.isSaasTcvAcv()){
		        	nonSpecialBidLineItemPriceTot += qli.getSaasBidTCV() == null ? 0.0 : qli.getSaasBidTCV().doubleValue();
		        }else{
		        	nonSpecialBidLineItemPriceTot += qli.getLocalExtProratedDiscPrc() == null ? 0.0 : qli.getLocalExtProratedDiscPrc().doubleValue();
		        }
	        }
	        
	        originalLineItemMap.put(qli.getPartNum() + qli.getSeqNum(), qli);
	    }
	    
	    oriSpecialBidLineItemPriceTot = bidPriceTot - nonSpecialBidLineItemPriceTot;
	}
	
	private boolean exclude(QuoteLineItem qli){
	    return DecimalUtil.isEqual(qli.getLineDiscPct(),  0);
	}
	
	public void calculate() throws QuoteException{
        int tryLimit = PartPriceConfigFactory.singleton().getIncreasePricingTryLimit();
        int factor = CommonServiceUtil.getEndCustomerRoundingFactor(quote);
        
        calculate(tryLimit, factor);
	}
    
    protected void calculate(int tryLimit, int factor) throws QuoteException{
        double newSpecialBidBottomLinePrice = getNewSpecialBidLineItemPriceTot();
        List currentLineItems = getInclusionLineItemList();        
       
        boolean increasePriceSucceed = true;
        try{
	        int i = 0;
	        
	        while(i < tryLimit){
	            logger.debug(this, " the [" + i + "]th try");
	            
	            double exclusionOfOrigFornextTry = 0;
	            double exclusionOfNewForNextTry = 0;
	
	            boolean needRetry = false;
	            for(Iterator it = currentLineItems.iterator(); it.hasNext(); ){
	                QuoteLineItem qli = (QuoteLineItem)it.next();
	 
	                QuoteLineItem oriQli = findOriLineItem(qli);
	                double oriBidExtPrice =  0.0;
			        if(oriQli.isSaasTcvAcv()){
			        	oriBidExtPrice = oriQli.getSaasBidTCV() == null ? 0.0 : oriQli.getSaasBidTCV().doubleValue();
			        }else{
			        	oriBidExtPrice = oriQli.getLocalExtProratedDiscPrc() == null ? 0.0 : oriQli.getLocalExtProratedDiscPrc().doubleValue();
			        }
	                
	                double newBidExtPrice = (oriBidExtPrice / oriSpecialBidLineItemPriceTot) * newSpecialBidBottomLinePrice;
	                newBidExtPrice = DecimalUtil.roundAsDouble(newBidExtPrice, factor);
	                
	                double oriLocalExtPrc = 0.0;
			        if(oriQli.isSaasTcvAcv()){
			        	Double entitledTCV = QuoteCommonUtil.calculateEntitledTcvForSubmitQuote(oriQli);
			        	oriLocalExtPrc = entitledTCV == null ? 0.0 : entitledTCV.doubleValue();
			        }else{
			        	oriLocalExtPrc = oriQli.getLocalExtProratedPrc() == null ? 0.0 : oriQli.getLocalExtProratedPrc().doubleValue();
			        }
	                if(newBidExtPrice > oriLocalExtPrc){
	                    newBidExtPrice = oriLocalExtPrc;
	                    
	                    //Calculated bid ext price higher than bid entitled price, need to retry
	                    it.remove();
	                    needRetry = true;
	                    
	                    exclusionOfOrigFornextTry += oriBidExtPrice;
	                    exclusionOfNewForNextTry += newBidExtPrice;
	                }
	                
	                setLineItemPrice(qli, newBidExtPrice, factor);
	            }
	            
	            //Price increase succeed, no need to retry
	            if(!needRetry){
	                break;
	            }
	            
	            oriSpecialBidLineItemPriceTot -= exclusionOfOrigFornextTry;
	            newSpecialBidBottomLinePrice -= exclusionOfNewForNextTry;
	
	            i++;
	            
	            //Have reached out to the limit, but still need to retry
	            //We should inform user that the new bottom line price is not valid
	            if(i == tryLimit && needRetry){
	                increasePriceSucceed = false;
	                break;
	            }
	        }
	        
	        if(!callPWS()){
	        	return ;
	        }
	        
	        if(increasePriceSucceed){
		        double difference = DecimalUtil.roundAsDouble((getNewSpecialBidLineItemPriceTot() - getNewLineItemBidExtPriceTot()), factor);
		        double maxDifference = PartPriceConfigFactory.singleton().getIncreasePricingDifference();
		        if(Math.abs(difference) >= PartPriceConstants.MINIUM_UNIT_PRICE
    	           && Math.abs(difference) <= maxDifference){
    	            adjust(difference, factor);
    	            
    		        if(!callPWS()){
    		        	return ;
    		        }
    	        }
    	        
    	        updatePriceInfo();		    	
	        } else {
	            increasePricingFailed();	           
	        }
        } catch(TopazException te){
            logger.error(this, te);
            throw new QuoteException(te);
        }
    }
    
    private boolean callPWS() throws TopazException{
        boolean pricingCallOK = calculatePrice(true);
        if(!pricingCallOK){
        	
            increasePricingFailed();
            
            return false;
        }
        
        return true;
    }
    
    private void updatePriceInfo() throws TopazException, QuoteException{
        PartPriceHelper.calculateDiscount(quote.getLineItemList(), quote.getQuoteHeader());
        
        //calculate quote line item overall discount
        quote.calculateLineItemOverallDiscount();
        
        //Update quote total price field
        PartPriceHelper.calculateQuoteTotalPrice(quote);
        
        //Set new offer price to quote header
        quote.getQuoteHeader().setOfferPrice(Double.valueOf(request.getOfferPrice()));
       
        PartPriceProcess process = PartPriceProcessFactory.singleton().create();
        process.updateQuoteHeader(quote.getQuoteHeader(), quote.getQuoteHeader().getCreatorId());
        
        //Need to recalculate price totals
        TotalPriceCalculator tpc = new TotalPriceCalculator(quote, null, request.getContract().getUser().getEmail());
        tpc.calculate();
    }
    
    private void increasePricingFailed() throws TopazException{
        //If price increasing failed, need to restore line items with prices from DB
        List qliList = QuoteLineItemFactory.singleton().
                        findLineItemsByWebQuoteNum(quote.getQuoteHeader().getWebQuoteNum());
        quote.setLineItemList(qliList);
        
        response.setPriceIncreaseFailed(true);
    }
    
    
    
    private double getNewLineItemBidExtPriceTot(){
        List list = getInclusionLineItemList();
        double tot = 0;
        
        for(Iterator it = list.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();
            tot += qli.getLocalExtProratedDiscPrc().doubleValue();
        }
        
        return tot;
    }
    
    private void setLineItemPrice(QuoteLineItem qli, double bidExtPrice, int factor) throws TopazException{
    	
    	if(qli.isSaasPart()){
    		if(qli.isSaasSetUpPart() || qli.isSaasProdHumanServicesPart()){
	    		qli.setOvrrdExtPrice(new Double(bidExtPrice));
	            qli.setLocalExtPrc(new Double(bidExtPrice));
	            qli.setLocalExtProratedDiscPrc(new Double(bidExtPrice));
    		}
    		if(qli.isSaasSubscrptnPart()){
    			double billingPeriods = QuoteCommonUtil.calculateBillingPeriods(qli.getICvrageTerm() == null ? 0 : qli.getICvrageTerm().intValue(), qli);
    			double bidRate = bidExtPrice;
    			if(billingPeriods != 0){
    				bidRate = bidExtPrice / billingPeriods;
    			}
    			qli.setOvrrdExtPrice(new Double(bidRate));
	            qli.setLocalExtPrc(new Double(bidExtPrice));
	            qli.setLocalExtProratedDiscPrc(new Double(bidRate));
    		}
            
            qli.setOverrideUnitPrc(null);
    	}else if(qli.isMonthlySoftwarePart()){
    		if(((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart()){
    			double billingPeriods = qli.getBillingPeriods();
    			double bidRate = bidExtPrice;
    			if(billingPeriods != 0){
    				bidRate = bidExtPrice / billingPeriods;
    			}
    			qli.setOvrrdExtPrice(new Double(bidRate));
	            qli.setLocalExtPrc(new Double(bidExtPrice));
	            qli.setLocalExtProratedDiscPrc(new Double(bidRate));
    		}
            
            qli.setOverrideUnitPrc(null);
    	} else {
            double bidUnitPrice = DecimalUtil.roundAsDouble(bidExtPrice / qli.getPartQty().intValue(), factor);
            
            qli.setOverrideUnitPrc(new Double(bidUnitPrice));
            
            qli.setLocalUnitPrc(new Double(bidUnitPrice));
            qli.setLocalExtPrc(new Double(bidExtPrice));
            
            qli.setLocalUnitProratedDiscPrc(new Double(bidUnitPrice));
            qli.setLocalExtProratedDiscPrc(new Double(bidExtPrice));
            
            //Clear potential override extended price
            qli.setOvrrdExtPrice(null);
    	}
    }
    
    private List getInclusionLineItemList(){
        List list = quote.getLineItemList();
        
        List resultList = new ArrayList();
        for(int i = 0; i < list.size(); i++){
            QuoteLineItem qli = (QuoteLineItem)list.get(i);
            
            QuoteLineItem oriQli = findOriLineItem(qli);
            if(!applicableForIncrease(qli)){
            	continue;
            }
            
            //Pricing increase applies to discounted parts only 
            //During calculation, some parts may get 0 discount
            //need to check the original line item discount
            if(exclude(oriQli)){
                continue;
            }

            resultList.add(qli);
        }
        
        return resultList;
    }
    
    private double getNewSpecialBidLineItemPriceTot(){
        return Double.parseDouble(request.getOfferPrice()) - nonSpecialBidLineItemPriceTot;
    }
    
    private QuoteLineItem findOriLineItem(QuoteLineItem qli){
        return (QuoteLineItem)originalLineItemMap.get(qli.getPartNum() + qli.getSeqNum());
    }
    
    protected void adjust(double difference, int factor) throws TopazException{
        
        List list = getInclusionLineItemList();
        
		if (adjustToOnePart(list, difference, factor)){
			return;
			
		} else {
		    //Need to apply the differences to more than one part
		    for(Iterator it = list.iterator(); it.hasNext(); ){
		        if(DecimalUtil.roundAsDouble(Math.abs(difference), factor) < PartPriceConstants.MINIUM_UNIT_PRICE){
		            break;
		        }
		        
		        QuoteLineItem qli = (QuoteLineItem)it.next();
		        double newBidExtPrice = 0.0;
		        if(qli.isSaasTcvAcv()){
		        	newBidExtPrice = qli.getSaasBidTCV() == null ? 0.0 : qli.getSaasBidTCV().doubleValue();
		        }else{
		        	newBidExtPrice = qli.getLocalExtProratedDiscPrc() == null ? 0.0 : qli.getLocalExtProratedDiscPrc().doubleValue();
		        }

		        QuoteLineItem oriQli = findOriLineItem(qli);
		        
		        //difference is negative
		        if(difference < 0){
		            double oriBidExtPrice = 0.0;
			        if(oriQli.isSaasTcvAcv()){
			        	oriBidExtPrice = oriQli.getSaasBidTCV() == null ? 0.0 : oriQli.getSaasBidTCV().doubleValue();
			        }else{
			        	oriBidExtPrice = oriQli.getLocalExtProratedDiscPrc() == null ? 0.0 : oriQli.getLocalExtProratedDiscPrc().doubleValue();
			        }
		            
		            //new bid ext price must be higher or equal to original bid ext price
		            double maxEating = newBidExtPrice - oriBidExtPrice;
		            
		            if(maxEating >= Math.abs(difference)){
		                newBidExtPrice += difference;
		                setLineItemPrice(qli, newBidExtPrice, factor);
		                
		                break;
		            } else {
		                difference += maxEating;
		                setLineItemPrice(qli, oriBidExtPrice, factor);
		            }
		            
		        } else {
		            double oriBidEntitledPrice = 0.0;
			        if(oriQli.isSaasTcvAcv()){
			        	Double entitledTCV = QuoteCommonUtil.calculateEntitledTcvForSubmitQuote(oriQli);
			        	oriBidEntitledPrice = entitledTCV == null ? 0.0 : entitledTCV.doubleValue();
			        }else{
			        	oriBidEntitledPrice = oriQli.getLocalExtProratedPrc() == null ? 0.0 : oriQli.getLocalExtProratedPrc().doubleValue();
			        }
		            double maxEating = oriBidEntitledPrice - newBidExtPrice;
		            
		            if(maxEating >= difference){
		                newBidExtPrice += difference;
		                setLineItemPrice(qli, newBidExtPrice, factor);
		                
		                break;
		            } else {
		                difference -= maxEating;
		                setLineItemPrice(qli, oriBidEntitledPrice, factor);
		            }
		        }
		    }
		}
    }
    
    //Try to find one part feasible to "eat" the difference
    private boolean adjustToOnePart(List list, double difference, int factor) throws TopazException{
	    for (Iterator iter= list.iterator(); iter.hasNext();){
	        QuoteLineItem qli = (QuoteLineItem)iter.next();
	        double newBidExtPrice = DecimalUtil.roundAsDouble(qli.getLocalExtProratedDiscPrc().doubleValue() + difference, factor);
	        
	        QuoteLineItem oriQli = findOriLineItem(qli);
	        double oriEntitledExtPrice = oriQli.getLocalExtProratedPrc().doubleValue();
	        double oriBidExtPrice = oriQli.getLocalExtProratedDiscPrc().doubleValue();
	        
	        // part must have either the same or higher price than the originating bid
	        // part must not have price higher than entitled price
	        if((newBidExtPrice >= oriBidExtPrice) && (newBidExtPrice <= oriEntitledExtPrice)){
	            setLineItemPrice(qli, newBidExtPrice, factor);
	            
	            return true;
	        }
	    }
	    
	    logger.debug(this, "trying to find a part to apply the difference failed");
	    
	    return false;
    }
    
    protected PricingRequest getPricingRequest() {
        PricingRequest pr = super.getPricingRequest();
        pr.setUpdatePriceLevel(false);
        
        return pr;
    }
    
    protected void processOfferPrice(){
        //do nothing
    }
}
