package com.ibm.dsw.quote.draftquote.viewbean.helper;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ReasonRow</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-18
 */
public class ReasonRow extends PartPriceCommon {
    public ReasonRow(Quote quote) {
        super(quote);
    }
    
    /**
     * display if part is from the original renewal quote (not added manually by the user)
     * Use the same data field for all of the following three reasons.  
     * If the user enters a reason, and then the label changes from ¡°Reason for change¡± to ¡°Reason for deletion¡±, 
     * the user entered text should still display.
     * @param lineItem
     * @return
     */
    public boolean showReasonRow(QuoteLineItem lineItem) {
        if(isFCT()){
            return false;
        }
        return (showReasonForChange(lineItem) ||
                showReasonForDeletion(lineItem) ||
                	showReasonForAdd(lineItem));
    }
    
    /**
     * Part is from the original renewal quote (not added manually by the user)
     *	Quantity is greater than 0
     * @param lineItem
     * @return
     */
    public boolean showReasonForChange(QuoteLineItem lineItem) {
        
        boolean isOriginalParts = lineItem.getSeqNum() < PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ;
        boolean isQuantityBlank = (lineItem.getPartQty() ==null);
        boolean isQuantityAboveZero = (lineItem.getPartQty() !=null) && (lineItem.getPartQty().intValue() > 0);
        
        
        return isOriginalParts && (isQuantityBlank || isQuantityAboveZero);
        
    }
    
    /**
	 *	Part is from the original renewal quote (not added manually by the user)
	 *	Quantity is 0
	 *	IMPORTANT:  do not hide renewal quote line items with an open quantity greater than zero, 
	 *  even if the user has changed the quantity in the quantity fields to zero.  
	 *  We continue to show the row, the total points and total price are set to 0.00, 
	 *  and the user is prompted to enter a reason for deletion.
     * @param lineItem
     * @return
     */
    public boolean showReasonForDeletion(QuoteLineItem lineItem) {
        if ((lineItem.getSeqNum() < PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ) 
                && (lineItem.getPartQty() !=null) && (lineItem.getPartQty().intValue() == 0)){
            return true;
        }
        return false;
    }
    
    /**
     * Part was added manually by the user
     * @param lineItem
     * @return
     */
    public boolean showReasonForAdd(QuoteLineItem lineItem) {
        if (lineItem.getSeqNum() >= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ) {
            return true;
        }
        return false;
    }
}