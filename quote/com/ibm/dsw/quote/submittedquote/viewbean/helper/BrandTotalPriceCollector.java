
package com.ibm.dsw.quote.submittedquote.viewbean.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuotePriceTotals;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 31, 2007
 */

public class BrandTotalPriceCollector {
    Quote quote;
    
    public BrandTotalPriceCollector(Quote q){
        this.quote = q;
    }
    
    private static class BrandComparator implements Comparator {

        public int compare(Object o1, Object o2) {

            BrandTotalPrice btp1 = (BrandTotalPrice) o1;
            BrandTotalPrice btp2 = (BrandTotalPrice) o2;

            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp1.prcSumLevelCode)) {
                return 1;

            } else if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp2.prcSumLevelCode)){
                return -1;
            }
            else {
                return btp1.prcSumLevelCode.compareTo(btp2.prcSumLevelCode);
            }
        }

    }
    
    public Map getTotalPrice(String distChannel, String prcType, Map specialBidPrices, String revnStrmCategoryCode) {
        String[] prcSumLevelCodes = this.selectPrcSumLevelGroupBy(distChannel, prcType, revnStrmCategoryCode);
        Map results = new HashMap();
        for (int i = 0; i < prcSumLevelCodes.length; i++) {
            BrandTotalPrice btpSBMapping = new BrandTotalPrice(distChannel, PartPriceConstants.PriceType.SPECIAL_BID_PRICE, prcSumLevelCodes[i], this.findBrandDesc(prcSumLevelCodes[i]), revnStrmCategoryCode);
            BrandTotalPrice btp = new BrandTotalPrice(distChannel, prcType, prcSumLevelCodes[i], this.findBrandDesc(prcSumLevelCodes[i]), revnStrmCategoryCode);
            HashMap map = this.selectCurrenPriceWhere(distChannel, prcType, btp.prcSumLevelCode, revnStrmCategoryCode);
            BrandTotalPrice sbPrice = specialBidPrices == null ? null : (BrandTotalPrice) specialBidPrices
                    .get(btpSBMapping.brandTotalPriceKey);
            if (sbPrice != null) {
                this.fillCurrencyPrice(btp, map, Double.valueOf(String.valueOf(sbPrice.localCurrencyPrice)));
            } else {
                this.fillCurrencyPrice(btp, map, null);
            }
            results.put(btp.brandTotalPriceKey, btp);
        }
        return results;
    }
    
    private void fillCurrencyPrice(BrandTotalPrice btp,HashMap map,Double sbPrice){
        
        String localCurrency = quote.getQuoteHeader().getCurrencyCode();
        Country country = quote.getQuoteHeader().getPriceCountry();
        String geo = country.getSpecialBidAreaCode();
        
        Double prcLocal = (Double) map.get(localCurrency);
        Double prcEUR = (Double) map.get(QuoteConstants.CURRENCY_EUR);
        Double prcUSD = (Double) map.get(QuoteConstants.CURRENCY_USD);

        //local currency price
        if (prcLocal != null) {
            btp.localCurrencyPrice = prcLocal.doubleValue();
        }

        // reporting based currency           

        if (QuoteConstants.GEO_EMEA.equals(geo))  {

            if (null != prcEUR) {
                btp.regionBasedCurrencyPrice = prcEUR.doubleValue();
            }
        } else {

            if (null != prcUSD) {
                btp.regionBasedCurrencyPrice = prcUSD.doubleValue();
            }

        }

        // reporting currency price        
        if (prcUSD != null) {
            btp.reportCurrencyPrice = prcUSD.doubleValue();
        }
        
        if(sbPrice != null){
            btp.customerDiscount = DecimalUtil.calculateDiscount(sbPrice.doubleValue(),btp.localCurrencyPrice);
        }else{
            btp.customerDiscount = "0.000";
        }
        
        String distChannel = quote.getQuoteHeader().getSapDistribtnChnlCode();
        String prcType = PartPriceConstants.PriceType.CHANNEL;
        HashMap channelPriceMap = this.selectCurrenPriceWhere(distChannel, prcType, btp.prcSumLevelCode,btp.revnStrmCategoryCode);
        if(channelPriceMap != null && channelPriceMap.size() != 0){
            btp.combinedDiscount = DecimalUtil.calculateDiscount(((Double)channelPriceMap.get(localCurrency)).doubleValue(),btp.localCurrencyPrice);
        }
        
    }
    
    private String [] selectPrcSumLevelGroupBy(String distChannel,String prcType,String revnStrmCategoryCode){
        
        HashMap map = new HashMap();
        List prcTotalList = this.quote.getPriceTotals();
        for (int i = 0; prcTotalList != null && i < prcTotalList.size(); i++) {
            QuotePriceTotals prcTotal = (QuotePriceTotals)prcTotalList.get(i);
            if(prcTotal.getDistChannelCode().equals(distChannel) && prcTotal.getPriceType().equals(prcType) && prcTotal.getRevnStrmCategoryCode().equals(revnStrmCategoryCode)){
                map.put(prcTotal.getPriceSumLevelCode(),"");    
            }
        }
        String [] results = new String[map.size()];
        map.keySet().toArray(results);
        return results;
    }

    private HashMap selectCurrenPriceWhere(String distChannel,String prcType,String prcSumLevelCode,String revnStrmCategoryCode){
        HashMap map = new HashMap();
        List prcTotalList = this.quote.getPriceTotals();
        for (int i=0;i<prcTotalList.size(); i++){
            QuotePriceTotals prcTotal = (QuotePriceTotals)prcTotalList.get(i);
            if(prcTotal.getDistChannelCode().equals(distChannel) 
                    && prcTotal.getPriceType().equals(prcType)
                    && prcTotal.getPriceSumLevelCode().equals(prcSumLevelCode)
                    && prcTotal.getRevnStrmCategoryCode().equals(revnStrmCategoryCode)){
                map.put(prcTotal.getCurrencyCode(),new Double(prcTotal.getExtAmount()));
            }
        }
        return map;
    }
   
    private String findBrandDesc(String code) {
        if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(code)) {
            return code;
        } else {
            List lineItemList = this.quote.getLineItemList();
            for (int i = 0; i < lineItemList.size(); i++) {
                QuoteLineItem item = (QuoteLineItem) lineItemList.get(i);
                if (item.getSwProdBrandCode() == null) {
                    continue;
                }
                if (code.trim().equals(item.getSwProdBrandCode().trim())) {
                    return item.getSwProdBrandCodeDesc();
                }
            }
        }
        return "";

    }
    
    public BrandTotalPrice getChannelTotalPrice(){
        
        String distChannel = quote.getQuoteHeader().getSapDistribtnChnlCode();
        String prcType = PartPriceConstants.PriceType.CHANNEL;
        BrandTotalPrice btp = new BrandTotalPrice(distChannel, prcType, QuoteConstants.PRICE_SUM_LEVEL_TOTAL, QuoteConstants.PRICE_SUM_LEVEL_TOTAL, PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL);
        HashMap map = this.selectCurrenPriceWhere(distChannel, prcType, btp.prcSumLevelCode,PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL);
        if(map != null && map.size() != 0){
            fillCurrencyPrice(btp, map, null);
            return btp;
        }
        return null;
    }
    
    public BrandTotalPrice getNetRevenueTotalPrice(){
    	List priceTotals = quote.getPriceTotals();
    	
    	String localCurrency = quote.getQuoteHeader().getCurrencyCode();
    	String[] regionAndReportingCurrency = QuoteCommonUtil.getRegionAndReportingCurrencyCodes(quote.getQuoteHeader());
    	
    	BrandTotalPrice netRevenue = new BrandTotalPrice();
    	for(Iterator it = priceTotals.iterator(); it.hasNext(); ){
    		QuotePriceTotals qpt = (QuotePriceTotals)it.next();
    		if(PartPriceConstants.PriceType.NET_REVENUE.equals(qpt.getPriceType())){
    			if(localCurrency.equals(qpt.getCurrencyCode())){
    				netRevenue.localCurrencyPrice = qpt.getExtAmount();
    			}
    			
    			if(regionAndReportingCurrency[0].equals(qpt.getCurrencyCode())){
    				netRevenue.regionBasedCurrencyPrice = qpt.getExtAmount();
    			}
    			
    			if(regionAndReportingCurrency[1].equals(qpt.getCurrencyCode())){
    				netRevenue.reportCurrencyPrice = qpt.getExtAmount();
    			}
    		}
    	}
    	
    	return netRevenue;
    }
    
    public Map getSpecialBidPrices(String distChannel, Map specialBidPrices, String revnStrmCategoryCode) {
        return this.getTotalPrice(distChannel, PartPriceConstants.PriceType.SPECIAL_BID_PRICE,
                specialBidPrices, revnStrmCategoryCode);
    }
    
    public Map getBasedLinePrices(String distChannel, Map specialBidPrices, String revnStrmCategoryCode) {
        return this.getTotalPrice(distChannel, PartPriceConstants.PriceType.BASELINE_PRICE,
                specialBidPrices, revnStrmCategoryCode);
    }
    
    public Map getEntitledPrices(String distChannel, Map specialBidPrices, String revnStrmCategoryCode) {
        return this.getTotalPrice(distChannel, PartPriceConstants.PriceType.ENTITLED_PRICE,
                specialBidPrices, revnStrmCategoryCode);
    }
    
    public List convertMap2List(Map map){
        List list = null;
        if(map != null){
            list = new ArrayList(map.values());
            BrandComparator comparator = new BrandComparator();
            Collections.sort(list, comparator);
        }
        return list;
    }
}
