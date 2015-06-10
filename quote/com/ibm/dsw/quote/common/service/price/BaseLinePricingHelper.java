package com.ibm.dsw.quote.common.service.price;

import java.util.List;

import DswSalesLibrary.HeaderIn;
import DswSalesLibrary.HeaderOut;
import DswSalesLibrary.ItemIn;
import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.service.price.rule.BaseLinePricingRule;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 21, 2007
 */

public class BaseLinePricingHelper extends PricingServiceHelper {

    String distChannelCode = null;

    List baseLinePrices = null;

    public BaseLinePricingHelper(PricingRequest pr) throws Exception {

        super(pr);
        pr.setChannel(false);
        this.rule = new BaseLinePricingRule(pr);
    }

    

    protected HeaderIn mapQuoteHeader() throws Exception {
        //this.distChannelCode = CommonServiceUtil.getDistributionChannelCode(this.quote.getQuoteHeader());
        HeaderIn headerIn = this.mapQuoteHeaderBasicInfo();
        //price band
        headerIn.setPrcLvl(BASELINE_PRICE_LEVEL);
        
        // for BL pricing, don't send contract number 
        headerIn.setContract("");
        //headerIn.setPayer("");
        //headerIn.setReseller("");
        return headerIn;
    }

    
    protected void processPrice(ItemOut[] itemOut) throws Exception {
        
      this.rule.execute(itemOut);
      this.baseLinePrices = ((BaseLinePricingRule) rule).getBaseLinePrices();
      
    }

    protected  void postMapQuoteLineItem(QuoteLineItem lineItem, ItemIn itemIn) {
        //don't send discount to SAP
        itemIn.setDiscPct(null);
        
        //reserve the OUP for obsolete parts
        if(!lineItem.isObsoletePart()){
        	itemIn.setUnitPrc(null);
        }
        itemIn.setExtPrc(null);
        itemIn.setSpbdOveragePrc(null);
        
        //ebiz: DEVS-7ZMKJE(Base Line (BL) and Entitled prices are indentical)
        //don't send renewal quote num / renewal quote line item seq num for calculating 
        //base line prices
        itemIn.setRefDocNum(null);
        itemIn.setRefDocItemNum(null);
    }

    public List getBaseLinePrices() {
       
        return this.baseLinePrices;
    }
    
    protected void setLatamUpliftPct(QuoteHeader header, HeaderOut headerOut){
        return;
    }

}
