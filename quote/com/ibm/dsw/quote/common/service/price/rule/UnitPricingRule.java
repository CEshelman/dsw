
package com.ibm.dsw.quote.common.service.price.rule;

import java.util.HashMap;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jul 3, 2007
 */

public class UnitPricingRule extends PricingRule {
    
    private HashMap unitPrcMap = new HashMap(); // key= part number(String), value = unit price(Double)
    
    public UnitPricingRule(PricingRequest pr){
       super(pr);
        
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.service.price.rule.PricingRule#execute(DswSalesLibrary.ItemOut[])
     */
    public void execute(ItemOut[] itemOut) throws TopazException {        
        
        logContext.debug(this,"begin to use UnitPricingRule...");
        // only read the unit price
        for (int i = 0; i < itemOut.length; i++) {
            
            ItemOut item = itemOut[i];
            unitPrcMap.put(item.getPartNum(),item.getLclUnitPrc());
        }
    }
    
    public HashMap getUnitPriceMap(){
        
        return this.unitPrcMap;
    }

}
