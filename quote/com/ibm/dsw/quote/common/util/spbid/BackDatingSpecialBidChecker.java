package com.ibm.dsw.quote.common.util.spbid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.QuoteReasonFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.spbid.date.MaintPartGroup;
import com.ibm.dsw.quote.common.util.spbid.date.SalesContinuousCoverageRule;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>BackDatingSpecialBidChecker<code> class.
 *    
 * @author: liuxinlx@cn.ibm.com
 * 
 * Creation date: Oct 23, 2008
 */
public class BackDatingSpecialBidChecker {
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    Quote quote;
    
    public BackDatingSpecialBidChecker(Quote quote) {
        this.quote = quote;
        
    }

    public boolean execute() {
    	
    	boolean sbFlag = false;
        QuoteHeader header = quote.getQuoteHeader();
        
        if(header.getBackDatingFlag()){
        	sbFlag = true;
        	
        	//check quote is direct and only reason is 'Compliance'
        	if(QuoteConstants.FULFILLMENT_DIRECT.equals(header.getFulfillmentSrc())
        			&& isOnlyComplianceChecked(header)){
        		if(isComplianceBackDating(header)){
        			sbFlag = false;
        		}
        	}
        }
        
        return sbFlag;
    }
    
    protected boolean isComplianceBackDating(QuoteHeader header){
    	
        List licenseParts = new ArrayList();
        if(filterLicenseParts(licenseParts)){
            return false;
        }
        
        List lineItems = quote.getLineItemList();

        if(licenseParts.size() > 0){

        	for(int i = 0; i < licenseParts.size(); i++){
                List relatedMaintItems = new ArrayList();
        		
        		QuoteLineItem licItem = (QuoteLineItem)licenseParts.get(i);
            	logContext.debug(this, "checking maintenance coverage for licence part["
            			                + licItem.getPartNum() + "_" + licItem.getSeqNum() + "]");
        		
        		for(Iterator it = lineItems.iterator(); it.hasNext(); ){
        			QuoteLineItem checkItem = (QuoteLineItem)it.next();
        			
        			if(isRelatedMaintPart(licItem, checkItem)){
        				relatedMaintItems.add(checkItem);
        			}
        		}
        		
    			if(relatedMaintItems.size() != 2){
    				logContext.debug(this, "maintenance parts quantity for licence part " 
    						                 + licItem.getPartNum() + "_" + licItem.getSeqNum()
											 + "is " + relatedMaintItems.size());
    				return false;
    			}
    			
    			if(!coverageIsThreeYear(licItem, relatedMaintItems)){
    				return false;
    			}
        	}
        	
        	//all compliance s.b contidion checks are ok 
            return true;
        } else {
        	return false;
        }
    }
    
    protected boolean coverageIsThreeYear(QuoteLineItem licItem, List relatedMaintLitems){
    	MaintPartGroup group = new MaintPartGroup(licItem.getSwSubId(), licItem.getPartQty().intValue());
    	group.addLineItem((QuoteLineItem)relatedMaintLitems.get(0));
    	group.addLineItem((QuoteLineItem)relatedMaintLitems.get(1));
    	
    	SalesContinuousCoverageRule rule = new SalesContinuousCoverageRule(group);
    	rule.setLicenseLineItem(licItem);
        List continousRelatedLineItems = rule.getLongestContinusLineItems(licItem);
        
        int months = rule.getContinusMonths(continousRelatedLineItems);  
    	
    	logContext.debug(this, "total continuoue coverage for part[" 
    			             + licItem.getPartNum() + "_" + licItem.getSeqNum() + "] is "
							 + months + " months");
    	if(months == QuoteConstants.BACK_DATING_SB_CONTINUOUS_COVERAGE_YEARS){
    		return true;
    	}
    	
    	return false;
    }
    
    protected boolean isRelatedMaintPart(QuoteLineItem licItem, QuoteLineItem checkItem){
    	if(!checkItem.getPartDispAttr().isMaintBehavior()){
    		return false;
    	}
    	
    	if(!licItem.getSwSubId().equals(checkItem.getSwSubId())){
    		return false;
    	}
    	
    	if(licItem.getPartQty() == null || checkItem.getPartQty() == null){
    		return false;
    	}
    	
    	if(licItem.getPartQty().intValue() != checkItem.getPartQty().intValue()){
    		return false;
    	}
    	
    	return true;
    }
    /**
     * @param licenseParts
     * @return
     */
    private boolean filterLicenseParts(List licenseParts) {
        HashMap map = new HashMap();
        boolean existingDuplicatedLicenseSubID = false;
        for(Iterator iter = this.quote.getLineItemList().iterator(); iter.hasNext();){
            QuoteLineItem item = (QuoteLineItem)iter.next();
            //only check back dated line items
            if(item.getPartDispAttr().isLicenseBehavior() && item.getBackDatingFlag()){
                if(map.containsKey(item.getSwSubId())){
                	existingDuplicatedLicenseSubID = true;
                	logContext.debug(this, "find duplicated licence part with same sub id:" + item.getSwSubId() + ", return true");
                }
                map.put(item.getSwSubId(),item);
            }
        }
        licenseParts.addAll(map.values());
        return existingDuplicatedLicenseSubID;
    }
    
    private boolean isOnlyComplianceChecked(QuoteHeader header){
    	List reasonCodes = header.getReasonCodes();
    	if(reasonCodes == null || reasonCodes.size() == 0){
    		try{
    			QuoteReasonFactory.singleton().getBackDatingReason(header);
    		} catch(TopazException e){
    			logContext.error(this, e);
    		}
    		
    		reasonCodes = header.getReasonCodes();
    		if(reasonCodes == null || reasonCodes.size() == 0){
    			return false;
    		}
    	}
    	
    	return (reasonCodes.size() == 1
    			   && reasonCodes.contains(QuoteConstants.BACK_DATING_REASON_COMPLIANCE));
    }

}
