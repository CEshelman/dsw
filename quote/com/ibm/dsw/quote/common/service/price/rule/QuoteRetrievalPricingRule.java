
package com.ibm.dsw.quote.common.service.price.rule;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jun 6, 2007
 */

public class QuoteRetrievalPricingRule extends PricingRule {

    private ItemOut[] lineItemPrices;
    public QuoteRetrievalPricingRule(PricingRequest pr){
        super(pr);
        
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.service.price.rule.PricingRule#execute(DswSalesLibrary.ItemOut[])
     */
    public void execute(ItemOut[] outItems) throws TopazException {        
        this.lineItemPrices = outItems;
    }
    
    public ItemOut[] getLineItemPrices()
    {
        return this.lineItemPrices;
    }
}
