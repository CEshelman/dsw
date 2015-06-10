
package com.ibm.dsw.quote.common.service.price.rule;

import java.util.List;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jun 7, 2007
 */

public class DefaultPricingRule extends PricingRule {
    
    
    public DefaultPricingRule(PricingRequest pr){
        super(pr);
        
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.service.price.rule.PricingRule#execute(DswSalesLibrary.ItemOut[])
     */
    public void execute(ItemOut[] itemOut) throws TopazException {
        
        List lineItems = quote.getLineItemList();
        logContext.debug(this,"begin to use Default pricing rule...");
        if (itemOut == null) return ;
        for (int i = 0; i < itemOut.length; i++) {
            
            ItemOut item = itemOut[i];

            QuoteLineItem lineItem = this.getLineItem(item.getPartNum(), item.getItmNum().intValue());
            String errCode = item.getErrCode();
            //for eol parts, PWS will always return error code
            if(QuoteCommonUtil.acceptPrice(item, lineItem)){
                this.setNewPrice(lineItem,item);
            }
            else{
                logContext.info(this,"Sap return error for part " + item.getPartNum()+",Price will be cleared");
                logContext.info(this,"Error Code =" + errCode);
                logContext.info(this,"Error Message =" + item.getErrMsg());
                lineItem.clearPrices();                
            }
            
            
        }

    }

}
