
package com.ibm.dsw.quote.common.service.price.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.service.price.PriceTotal;
import com.ibm.dsw.quote.common.service.price.PricingRequest;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
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

public class BaseLinePricingRule extends PricingRule {
    private List baseLinePrices = new ArrayList();
    
    public BaseLinePricingRule(PricingRequest pr){
        super(pr);
    }
    
    public class ItemOutWrapper {

        ItemOut itemOut;

        String brandCode;
        
        String revnStrmCategoryCode;

        public ItemOutWrapper(ItemOut itemOut, String brandCode, String revnStrmCategoryCode) {
            this.itemOut = itemOut;
            this.brandCode = brandCode;
            this.revnStrmCategoryCode = revnStrmCategoryCode;
        }
    }

    private List createItemOutWrappers(ItemOut[] itemOut) {
        List results = new ArrayList();

        for (int i = 0; i < itemOut.length; i++) {
            ItemOut item = itemOut[i];
            
            QuoteLineItem sapLineItem = this.getLineItem(item.getPartNum(), item.getItmNum().intValue());
            if (QuoteCommonUtil.shouldIncludeForTotalPrice(quote.getQuoteHeader(), sapLineItem)) {
                String brandCode = sapLineItem.getSwProdBrandCode();
                String revnStrmCategoryCode = sapLineItem.getRevnStrmCategoryCode();

                ItemOutWrapper itemOutWrapper = new ItemOutWrapper(item, brandCode, revnStrmCategoryCode);
                results.add(itemOutWrapper);
            }
        }

        return results;
    }
    
    private void process(Map revnStrmCategoryMap, String revnStrmCategoryCode, ItemOutWrapper item){
        Map brandMap = (Map) revnStrmCategoryMap.get(revnStrmCategoryCode);
        
        if (null == brandMap) {
            brandMap = new HashMap();
            revnStrmCategoryMap.put(revnStrmCategoryCode, brandMap);
        }
        
        List brandItems = (List)brandMap.get(item.brandCode);
        if(brandItems == null){
            brandItems = new ArrayList();
            brandMap.put(item.brandCode, brandItems);
        }
        brandItems.add(item);
    }
    
    private Map groupByRevnStrmCategoryAndBrandCode(List itemOutWrappers) {
        Map revnStrmCategoryMap = new HashMap();

        for (int i = 0; i < itemOutWrappers.size(); i++) {

            ItemOutWrapper item = (ItemOutWrapper) itemOutWrappers.get(i);
            
            process(revnStrmCategoryMap, item.revnStrmCategoryCode, item);
            
            //Take mocked revenue stream category "ALL" to ease coding
            process(revnStrmCategoryMap, PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL, item);
        }

        return revnStrmCategoryMap;
    }

    public void execute(ItemOut[] itemOut) throws TopazException {
        logContext.debug(this,"begin to use base line pricing rule...");
        List itemOuts = this.createItemOutWrappers(itemOut);

        calculateTotalsByRevnStrmCategory(itemOuts);
    }
    
    private void calculateTotalsByRevnStrmCategory(List itemOuts){
        Map revnStrmCategoryMap = groupByRevnStrmCategoryAndBrandCode(itemOuts);
        
        for(Iterator revnStrmCatIt = revnStrmCategoryMap.keySet().iterator(); revnStrmCatIt.hasNext(); ){
            String revnStrmCategory = (String)revnStrmCatIt.next();
            
            Map brandMap = (Map)revnStrmCategoryMap.get(revnStrmCategory);
            
            double revnCatSubTotal = 0;
            for(Iterator brandIt = brandMap.keySet().iterator(); brandIt.hasNext(); ){
                String brandCode = (String)brandIt.next();
                
                List items = (List)brandMap.get(brandCode);
                
                double brandTotal = 0;
                for (int i = 0; i < items.size(); i++) {
                    ItemOutWrapper wrapper = (ItemOutWrapper) items.get(i);
                    QuoteLineItem qli = quote.getLineItemByDestSeqNum(
                    		wrapper.itemOut.getPartNum()
                    		, wrapper.itemOut.getItmNum() == null ? 0 : wrapper.itemOut.getItmNum().intValue());
                    if(qli != null 
                    	&& (qli.isSaasPart() || qli.isMonthlySoftwarePart())
                    	&& qli.isSaasTcvAcv()){
                    	brandTotal += wrapper.itemOut.getTotCtrctValueAmt() == null ? 0.00 : wrapper.itemOut.getTotCtrctValueAmt().doubleValue();
                    }else{
                    	brandTotal += wrapper.itemOut.getUsrExtPrc().doubleValue();
                    }
                }
                
                addByRevnCatPriceTotal(brandCode, revnStrmCategory, brandTotal);
                
                revnCatSubTotal += brandTotal;
            }
            
            addByRevnCatPriceTotal(QuoteConstants.PRICE_SUM_LEVEL_TOTAL, revnStrmCategory, revnCatSubTotal);
        }
    }
    
    private void addByRevnCatPriceTotal(String prcSumLevelCode, String revnStrmCatCode, double extAmt){
        PriceTotal blPrice = new PriceTotal();
        blPrice.distChannelCode = QuoteConstants.DIST_CHNL_END_CUSTOMER;
        blPrice.prcType = PartPriceConstants.PriceType.BASELINE_PRICE;
        blPrice.prcSumLevelCode = prcSumLevelCode;
        blPrice.revnStrmCategory = revnStrmCatCode;
        blPrice.currencyCode = getCurrencyCode();
        blPrice.extAmt = extAmt;
        
        baseLinePrices.add(blPrice);
    }
    
    private String getCurrencyCode(){
        return this.quote.getQuoteHeader().getCurrencyCode();
    }

    /**
     * @return Returns the baseLinePrices.
     */
    public List getBaseLinePrices() {
        return baseLinePrices;
    }
}
