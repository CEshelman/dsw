
package com.ibm.dsw.quote.draftquote.util.validation;

import java.util.List;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PPSSRule</code> is the implementation for PPSS 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 26, 2007
 */

class PPSSRule extends SalesQuoteRule {

    public PPSSRule(Quote quote, PostPartPriceTabContract ct) {
        super(quote, ct);
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.util.validation.BaseRule#validate()
     */
    public void validate() throws TopazException{
        
        List items = quote.getLineItemList();

        for (int i = 0; i < items.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) items.get(i);

            validateSortOrder(item);

            boolean isDeleted = validateQuantity(item);
            
            // if the quantity is 0, the line item is deleted, ignore subsequent validation
            if (isDeleted) {
                continue;
            }
            validatePVUPartQtyStatus(item);
        }
        
        validatePaymentTermDays();
        
        validateValidityDays();

    }

}
