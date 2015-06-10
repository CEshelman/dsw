package com.ibm.dsw.quote.submittedquote.viewbean.helper;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;

import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SubmittedReasonRow</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-5-14
 */
public class SubmittedReasonRow extends PartPriceCommon {
    public SubmittedReasonRow(Quote quote) {
        super(quote);
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