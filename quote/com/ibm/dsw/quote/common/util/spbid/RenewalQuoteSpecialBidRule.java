package com.ibm.dsw.quote.common.util.spbid;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.spbid.date.RenewalContinuousCoverageChecker;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RenewalQuoteSpecialBidRule<code> class.
 *    
 * @author: liuxinlx@cn.ibm.com
 * 
 * Creation date: Feb 25, 2008
 */

class RenewalQuoteSpecialBidRule extends SpecialBidRule{

    /**
     * @param quote
     * @param ct
     */
    public RenewalQuoteSpecialBidRule(Quote quote) {
        super(quote);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.util.validation.spbid.SpecialBidRule#checkSpecialBidForMaintConverage()
     */
    public boolean checkSpecialBidForMaintConverage() {
        RenewalContinuousCoverageChecker checker = new RenewalContinuousCoverageChecker(quote);
        return checker.checkSpeicalBid();
    }
    
    protected boolean rnwlQuoteStatusRequiresSpecialBid(String userId){
    	return false;
    }

    protected boolean skip(){
        QuoteAccess access = quote.getQuoteAccess();
        
        if(access == null){
            return true;
        }
        
        //The renewal quote status allows for status change or sales tracking change only
        //skip special bid validation
        if((access.isCanEditRQSalesInfo() || access.isCanUpdateRQStatus())
                && !access.isCanEditRQ()){
            return true;
        }
        
        //The renewal quote status allows for editing of the quote but
        //no line items have been changed, added or deleted
        if(access.isCanEditRQ()){
            List list = quote.getLineItemList();
            
            if(list != null && list.size() > 0){
                for(Iterator it = list.iterator(); it.hasNext(); ){
                    QuoteLineItem qli = (QuoteLineItem)it.next();
                    if(StringUtils.contains(qli.getChgType(),PartPriceConstants.PartChangeType.PART_ADDED)
                            || StringUtils.contains(qli.getChgType(),PartPriceConstants.PartChangeType.PART_DELETED)
                            || StringUtils.contains(qli.getChgType(),PartPriceConstants.PartChangeType.PART_QTY_DECREASE)
                            || StringUtils.contains(qli.getChgType(),PartPriceConstants.PartChangeType.PART_QTY_INCREASE)
                            || StringUtils.contains(qli.getChgType(),PartPriceConstants.PartChangeType.PART_DATE_CHANGED)
                            || StringUtils.contains(qli.getChgType(),PartPriceConstants.PartChangeType.PART_PVU_CHANGED)
                            || StringUtils.contains(qli.getChgType(),PartPriceConstants.PartChangeType.PART_DISCOUNT_UNIT_PRICE_CHANAGED)){
                        //has line item change, perform special bid determination
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
}
