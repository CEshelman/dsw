package com.ibm.dsw.quote.common.service.price;

import DswSalesLibrary.HeaderIn;
import DswSalesLibrary.HeaderOut;
import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.service.price.rule.DefaultPricingRule;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jul 3, 2007
 */

public class DefaultPricingHelper extends PricingServiceHelper {

    public DefaultPricingHelper(PricingRequest pr) {
        super(pr);
        this.rule = new DefaultPricingRule(pr);
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.service.price.PricingServiceHelper#mapQuoteHeader()
     */
    protected HeaderIn mapQuoteHeader() throws Exception {

        HeaderIn headerIn = this.mapQuoteHeaderBasicInfo();
        
        String distChannelCode = this.quote.getQuoteHeader().getSapDistribtnChnlCode();
        headerIn.setDistChnl(distChannelCode);
        
        /*if (pr.isChannel()) {

            String distChannelCode = this.quote.getQuoteHeader().getSapDistribtnChnlCode();

            if (QuoteConstants.DIST_CHNL_END_CUSTOMER.equals(distChannelCode)) {
                throw new Exception("Can't call Channel Pricing as Qutoe is direct");
            }

            headerIn.setDistChnl(distChannelCode);

        }*/
        
        return headerIn;

    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.service.price.PricingServiceHelper#processPrice(DswSalesLibrary.ItemOut[])
     */
    protected void processPrice(ItemOut[] itemOut) throws Exception {
        this.rule.execute(itemOut);

    }
    protected void setLatamUpliftPct(QuoteHeader header, HeaderOut headerOut){
        header.setLatamUpliftPct(headerOut.getLatamUpliftPct() == null ? 0.00 : headerOut.getLatamUpliftPct().doubleValue()*1000);
    }

}
