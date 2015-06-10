package com.ibm.dsw.quote.common.service.price.rule;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Xiao Guo Yi
 *
 */
public class ChannelPricingRule extends PricingRule {
    public ChannelPricingRule(PricingRequest pr){
        super(pr);
    }
    
    public void execute(ItemOut[] itemOutArray) throws TopazException {
        ItemOut itemOut = null;
        QuoteLineItem lineItem = null;
        
        for(int i = 0; i < itemOutArray.length; i++){
            itemOut = itemOutArray[i];
            lineItem = getLineItem(itemOut.getPartNum(), itemOut.getItmNum().intValue());
            
            //for eol parts, PWS will always return error code
            if(QuoteCommonUtil.acceptPrice(itemOut, lineItem)){
                QuoteCommonUtil.setLineItemPrice(itemOut, lineItem, quote);
            }
            else{
                logContext.info(this,"Sap return error for part " + itemOut.getPartNum()+",Price will be cleared");
                logContext.info(this,"Error Code =" + itemOut.getErrCode());
                logContext.info(this,"Error Message =" + itemOut.getErrMsg());
                lineItem.clearPrices();                
            }
        }
    }
}
