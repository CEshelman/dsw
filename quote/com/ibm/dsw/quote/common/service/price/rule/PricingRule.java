
package com.ibm.dsw.quote.common.service.price.rule;

import java.util.List;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;

import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
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

public abstract class PricingRule {
    
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    protected Quote quote;
    
    protected PricingRequest pr;
    
    public PricingRule(PricingRequest pr){
        this.quote = pr.getQuote();
        this.pr = pr;
    }
    
    protected QuoteLineItem getLineItem(String partNum, int seqNum) {
    	return quote.getLineItemByDestSeqNum(partNum, seqNum);
    }
    
    
    protected boolean hasContractLevelPricing() {

        Customer customer = quote.getCustomer();

        if (null == customer) {
            return false;
        } else {
            return customer.getCtrctHasPreApprPrcLvlFlg() == 1;
        }

    }
    
            
        
    
    protected QuoteLineItem findQuoteLineItemWithSameKey(String key) {
        List items = quote.getLineItemList();
        for (int i = 0; i < items.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) items.get(i);
            String itemKey = item.getPartNum() + item.getSeqNum();
            if (itemKey.equals(key)) {
                return item;
            }
        }
        return null;
    }
    protected void setNewPrice(QuoteLineItem lineItem,ItemOut itemOut) throws TopazException{
    	QuoteCommonUtil.setLineItemPrice(itemOut, lineItem, quote);
    }
    protected boolean showChannelMargin(){
        return PartPriceConfigFactory.singleton().allowChannelMarginDiscount(this.quote);
    }
    
    public abstract void execute(ItemOut[] outItems ) throws TopazException;
    
    
    
}
