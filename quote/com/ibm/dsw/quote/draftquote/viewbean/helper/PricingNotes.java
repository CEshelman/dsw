package com.ibm.dsw.quote.draftquote.viewbean.helper;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.PartPriceTranPriceLevel;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.process.CommonProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PricingNotesSectionViewBean</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-3
 */
public class PricingNotes extends PartPriceCommon {
    public String nextLevel = "";
    public double ptsToNextLevel = 0;
    
    public PricingNotes(Quote quote)
    {
        super(quote);
    }
    /**
     * 
     * @return
     */
    public boolean showPreApprovedContractPricingMessage() {
        if (isPA() || isOEM()) {
            if (quote.getCustomer() != null) {
                if (quote.getQuoteHeader().getSoldToCustNum() != null) {
                    if (quote.getCustomer().getCtrctHasPreApprPrcLvlFlg() != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * return true if a contract has been selected for the quote
     * @return
     */
    private boolean isSubmittedContractSelected() {
        if (quote.getCustomer() != null) {
            if (quote.getCustomer().getContractList() != null && quote.getCustomer().getContractList().size() != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * A contract has been selected for the quote, and
     * That contract has an active pre-approved contract pricing flag (see validation checks, above)
     * @return
     */
    public boolean showSubmittedPreApprovedContractPricingMessage() {
        if (isPA() || isOEM()) {
            if (isSubmittedContractSelected()) {
                if (quote.getCustomer().getCtrctHasPreApprPrcLvlFlg() != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean showBandLevel() {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        if (quote.getCustomer() != null) {
            if (isPA()) {
                String SVPLevel = getSVPLevelCode();
                double totalPoints = quote.getQuoteHeader().getTotalPoints();

                //get nextLevel & ptsToNextLevel 
                try {
                    PartPriceTranPriceLevel pts = CommonProcessFactory.singleton().create().getNextTranPriceLevel(totalPoints,SVPLevel);
                    if (pts != null) {
                        nextLevel = pts.getTranPriceLevelCode()==null?"":pts.getTranPriceLevelCode();
                        double minTranPts = pts.getMinTranPts();
                        ptsToNextLevel = minTranPts-totalPoints; 
                    }
                } catch (QuoteException e) {
                    logger.error(this,e);
                } catch (Throwable e) {
                    logger.error(this, e);
                }
                
                //Total points on the quote is within 50 points of moving to the next band level
                if (ptsToNextLevel > 0 && 
                        ptsToNextLevel <= 50 && 
                        	!SVPLevel.equalsIgnoreCase(DraftQuoteConstants.ED_SVP) && 
                        		!SVPLevel.equalsIgnoreCase(DraftQuoteConstants.GV_SVP)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * A contract has been selected for the quote, and
     * Total points on the quote is within 50 points of moving to the next band level
     * @return
     */
    public boolean showSubmittedBandLevel() {
        return isSubmittedContractSelected() && showBandLevel();
    }
    
    public boolean showChannelMarginDiscountNote(){
    	if(isPA() || isPAE() || isPAUN() || isOEM()){
    		QuoteLineItem item = null;
    		List lineItems = quote.getLineItemList();
    		
    		if(lineItems == null || lineItems.size() == 0){
    			return false;
    		}
    		
    		for(Iterator it = lineItems.iterator(); it.hasNext(); ){
    			item = (QuoteLineItem)it.next();
    			
    			if(item.getChnlOvrrdDiscPct() != null){
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    public boolean showPointsToPA() {
        double totalPoints = quote.getQuoteHeader().getTotalPoints();
        if (totalPoints >= 450 && totalPoints < 500) {
            return true;
        }
        
        return false;
    }

    private boolean noCustomerOrCustomerIsNew(Quote qutoe){
        
        return (quote.getCustomer() != null && StringUtils.isBlank(quote.getQuoteHeader().getSoldToCustNum())) 
        || (quote.getCustomer() == null);
    }
    
    /**
     * 
     * @return true if any of the messages in this section display
     */
    public boolean showPricingNotesSection() {
        boolean flag = false;
        
        flag = showPreApprovedContractPricingMessage() || 
        	       showBandLevel() || 
                           		isVolTranLevelOvrrd() ||
								showChannelMarginDiscountNote();
        
        return flag;
    }
    
    /**
     * return true if any of the messages in this section display
     * @return
     */
    public boolean showSubmittedPricingNotesSection() {
    	if(showChannelMarginDiscountNote()){
    		return true;
    	}
        if ((isPA() || isOEM()) && 
                (showSubmittedPreApprovedContractPricingMessage() || 
                        showSubmittedBandLevel())) {
            return true;
        }

        return false;
    }
    
    
    /**
     * 
     * @return
     */
    public boolean showRQPricingNotesSection() {
    	return true;
//        if (showPreApprovedContractPricingMessage() || 
//                showBandLevel() || showChannelMarginDiscountNote()) {
//            return true;
//        }
//        return false;
    }
}