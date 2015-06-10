package com.ibm.dsw.quote.draftquote.util.price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.appcache.domain.CurrencyConversionFactor;
import com.ibm.dsw.quote.appcache.domain.CurrencyConversionFactorFactory;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuotePriceTotals;
import com.ibm.dsw.quote.common.domain.QuotePriceTotalsFactory;
import com.ibm.dsw.quote.common.service.price.PriceTotal;
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
 * Creation date: May 9, 2007
 */

public class TotalPriceCalculator {

    private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    Quote quote;

    QuotePriceTotalsFactory factory = QuotePriceTotalsFactory.singleton();

    List baseLinePrcs;

    private HashMap localCurrencyPrices = new HashMap();

    private String userID;

    public TotalPriceCalculator(Quote q,List baseLinePrcs, String userID) {

        this.quote = q;
        this.baseLinePrcs = baseLinePrcs;
        this.userID = userID;
    }

    private String createKey(String distChannelCode,String prcType,String prcSumLevelCode,String currencyCode, String revnStrmCategory){
        return distChannelCode + "-" + prcType + "-" + prcSumLevelCode + "-" + currencyCode + "-" + revnStrmCategory;
    }

    private void getExistingPriceTotals() throws TopazException {
        String webQuoteNum = quote.getQuoteHeader().getWebQuoteNum();

        List prcTotals = factory.getQuotePriceTotals(webQuoteNum, this.userID);
        quote.setPriceTotals(prcTotals);
    }

    public void calculate() throws TopazException {
        if(quote.getPriceTotals() == null){
            getExistingPriceTotals();
        }

        calculateLocalCurrencyPriceTotals();

        calculateLocalCurrencyNetRevenuePriceTotal();

        List pts = calculateRegionAndReportingPrice(this.localCurrencyPrices.values());

        List results = new ArrayList();
        results.addAll(this.localCurrencyPrices.values());
        results.addAll(pts);
        results.addAll(processBaseLinePrice());

        for(int i=0;i<results.size();i++){
            PriceTotal pt = (PriceTotal)results.get(i);

            updateQuotePriceTotals(pt);
        }
    }


    private void calculateLocalCurrencyNetRevenuePriceTotal(){
    	boolean channelQuote = quote.getQuoteHeader().isChannelQuote();

    	PriceTotal netRevenueTotal = getPriceTotal(PartPriceConstants.PriceType.NET_REVENUE, QuoteConstants.PRICE_SUM_LEVEL_TOTAL,
    			getLocalCurrency(), quote.getQuoteHeader().getSapDistribtnChnlCode(), PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL);

    	List qliList = getValidLineItems();
    	double price = 0;
    	for(Iterator it = qliList.iterator(); it.hasNext(); ){
    		QuoteLineItem qli = (QuoteLineItem)it.next();

    		if(channelQuote){
                if(qli.isSaasTcvAcv()){
            		if(qli.getSaasBidTCV() != null){
            			price += (qli.getSaasBpTCV() == null ? 0.0 : qli.getSaasBpTCV().doubleValue());
            		}
            	}else{
            		if(qli.getLocalExtProratedDiscPrc() != null){
            			price += (qli.getChannelExtndPrice() == null ? 0.0 : qli.getChannelExtndPrice().doubleValue());
            		}
            	}
    		} else {
    			if(qli.isSaasTcvAcv()){
            		if(qli.getSaasBidTCV() != null){
            			price += qli.getSaasBidTCV().doubleValue();
            		}
            	}else{
            		if(qli.getLocalExtProratedDiscPrc() != null){
            			price += qli.getLocalExtProratedDiscPrc().doubleValue();
            		}
            	}
    		}
    	}

    	netRevenueTotal.extAmt = price;
    }

    private void updateQuotePriceTotals(PriceTotal pt) throws TopazException{
        List prcTotals = quote.getPriceTotals();

        for (int i = 0; i < prcTotals.size(); i++) {
            QuotePriceTotals prcTotal = (QuotePriceTotals) prcTotals.get(i);
            if(match(pt, prcTotal)){
                prcTotal.setExtAmount(pt.extAmt);

                return;
            }
        }

        QuotePriceTotals prcTotal = factory.createQuotePriceTotals(quote.getQuoteHeader().getWebQuoteNum(),
                                                                      pt.distChannelCode, pt.prcType, pt.prcSumLevelCode,
                                                                      pt.currencyCode, userID, pt.revnStrmCategory);

        quote.getPriceTotals().add(prcTotal);

        prcTotal.setExtAmount(pt.extAmt);
    }

    private boolean match(PriceTotal priceTotal, QuotePriceTotals quotePriceTotal){
        if(priceTotal.prcType.equals(quotePriceTotal.getPriceType())
                && priceTotal.prcSumLevelCode.equals(quotePriceTotal.getPriceSumLevelCode())
                && priceTotal.revnStrmCategory.equals(quotePriceTotal.getRevnStrmCategoryCode())
                && priceTotal.currencyCode.equals(quotePriceTotal.getCurrencyCode())
                && priceTotal.distChannelCode.equals(quotePriceTotal.getDistChannelCode())){
            return true;
        } else {
            return false;
        }
    }

    private List processBaseLinePrice() throws TopazException{
        List results = new ArrayList();

        if(this.baseLinePrcs != null){
            results.addAll(this.baseLinePrcs);

            List blPts = this.calculateRegionAndReportingPrice(this.baseLinePrcs);
            results.addAll(blPts);
        }

        return results;
    }


    private List calculateRegionAndReportingPrice(Collection c) throws TopazException {

        String[] currencyCodes = QuoteCommonUtil.getRegionAndReportingCurrencyCodes(quote.getQuoteHeader());

        QuoteHeader header = quote.getQuoteHeader();
        String quoteCntryCode = header.getPriceCountry().getCode3();
        String quoteCurrencyCode = header.getCurrencyCode();
        String rgnCurrCode = currencyCodes[0];
        String rptCurrCode = currencyCodes[1];

        CurrencyConversionFactor[] factors = CurrencyConversionFactorFactory.singleton().getCurrencyConversionFactors(quoteCntryCode,quoteCurrencyCode, rgnCurrCode, rptCurrCode);

        List pts = new ArrayList();

        Iterator iter = c.iterator();

        while(iter.hasNext()){

            PriceTotal localPrcTotal = (PriceTotal)iter.next();

            for(int i=0;i<currencyCodes.length;i++){

                if(!localPrcTotal.currencyCode.equals(currencyCodes[i])){

                    CurrencyConversionFactor factor = factors[i];
                    if((null != factor) && (null != factor.getExchangeRate())){

                        Double rate = factor.getExchangeRate();

                        PriceTotal pt = new PriceTotal();
                        pt.distChannelCode = localPrcTotal.distChannelCode;
                        pt.prcType = localPrcTotal.prcType;
                        pt.prcSumLevelCode = localPrcTotal.prcSumLevelCode;
                        pt.revnStrmCategory = localPrcTotal.revnStrmCategory;
                        pt.currencyCode = currencyCodes[i];
                        pt.extAmt = localPrcTotal.extAmt * rate.doubleValue();

                        pts.add(pt);
                    }
                    else{
                        logContext.info(this,"Get Rate factor for "+localPrcTotal.currencyCode +"->"+currencyCodes[i]+" error");
                    }
                }
            }
        }

        return pts;
    }

    private String getLocalCurrency(){
        return quote.getQuoteHeader().getCurrencyCode();
    }

    private void calculateLocalCurrencyPriceTotals()  {
        List lineItems = this.getValidLineItems();
        if (lineItems.size() == 0) {
            return;
        }
        // key : revn strm category code, value Map
        Map revnStrmCategoryMap = groupByRevnStrmCategoryAndBrandCode(lineItems);

        boolean isSpecialBid = quote.getQuoteHeader().getSpeclBidFlag() == 1;
        boolean isChannel = quote.getQuoteHeader().isChannelQuote();

        for(Iterator revnCatIt = revnStrmCategoryMap.keySet().iterator(); revnCatIt.hasNext(); ){
            String revnStrmCategory = (String)revnCatIt.next();

           //key : product brand code, value List
            Map brandMap = (Map)revnStrmCategoryMap.get(revnStrmCategory);

            double revnSpecificEntitledPrc = 0;
            double revnSpecificSpecialBidPrc = 0;
            double revnSpecificChannelPrc = 0;

            for(Iterator brandIt = brandMap.keySet().iterator(); brandIt.hasNext(); ){
                String brandCode = (String)brandIt.next();

                List parts = (List)brandMap.get(brandCode);

                //Entitled price total
                revnSpecificEntitledPrc += fillEntitledEndCustomerPrice(revnStrmCategory, brandCode, parts);

                //Special bid price total
                if(isSpecialBid){
                    revnSpecificSpecialBidPrc += fillSpecialBidEndCustomerPrice(revnStrmCategory, brandCode, parts);
                }

                //Channel price total
                if(isChannel){
                    revnSpecificChannelPrc += fillChannelPrice(revnStrmCategory, brandCode, parts);
                }
            }

            addEndCustomerEntitledPriceTotal(QuoteConstants.PRICE_SUM_LEVEL_TOTAL, revnStrmCategory, revnSpecificEntitledPrc);

            if(isSpecialBid){
                addEndCustomerSpecialBidPriceTotal(QuoteConstants.PRICE_SUM_LEVEL_TOTAL, revnStrmCategory, revnSpecificSpecialBidPrc);
            }

            if(isChannel){
                addChannelPriceTotal(QuoteConstants.PRICE_SUM_LEVEL_TOTAL, revnStrmCategory, revnSpecificChannelPrc);
            }
        }
    }

    private void addEndCustomerPriceTotal(String prcType, String prcSumLevelCode, String revnStrmCategory, double tot){
        PriceTotal prcTot = getPriceTotal(prcType, prcSumLevelCode, getLocalCurrency(),
                                     QuoteConstants.DIST_CHNL_END_CUSTOMER, revnStrmCategory);
        prcTot.extAmt = tot;
    }

    private void addEndCustomerEntitledPriceTotal(String prcSumLevelCode, String revnStrmCategory, double tot){
        addEndCustomerPriceTotal(PartPriceConstants.PriceType.ENTITLED_PRICE, prcSumLevelCode, revnStrmCategory, tot);
    }

    private void addEndCustomerSpecialBidPriceTotal(String prcSumLevelCode, String revnStrmCategory, double tot){
        addEndCustomerPriceTotal(PartPriceConstants.PriceType.SPECIAL_BID_PRICE, prcSumLevelCode, revnStrmCategory, tot);
    }

    private double fillEntitledEndCustomerPrice(String revnStrmCategory, String brandCode, List parts){
        double tot = 0;

        for(Iterator it = parts.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();
            if(qli.isSaasTcvAcv()){
        		if(qli.getSaasEntitledTCV() != null){
        			tot += qli.getSaasEntitledTCV().doubleValue();
        		}
        	}else{
        		if(qli.getLocalExtProratedPrc() != null){
        			tot += qli.getLocalExtProratedPrc().doubleValue();
        		}
        	}
        }

        addEndCustomerEntitledPriceTotal(brandCode, revnStrmCategory, tot);

        return tot;
    }

    private double fillSpecialBidEndCustomerPrice(String revnStrmCategory, String brandCode, List parts){
        double tot = 0;

        for(Iterator it = parts.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();
            if(qli.isSaasTcvAcv()){
        		if(qli.getSaasBidTCV() != null){
        			tot += qli.getSaasBidTCV().doubleValue();
        		}
        	}else{
        		if(qli.getLocalExtProratedDiscPrc() != null){
        			tot += qli.getLocalExtProratedDiscPrc().doubleValue();
        		}
        	}
        }

        addEndCustomerSpecialBidPriceTotal(brandCode, revnStrmCategory, tot);

        return tot;
    }

    private String getDistChannelCode(){
        return quote.getQuoteHeader().getSapDistribtnChnlCode();
    }

    private void addChannelPriceTotal(String prcSumLevelCode, String revnStrmCategory, double tot){
        PriceTotal prcTot = getPriceTotal(PartPriceConstants.PriceType.CHANNEL, prcSumLevelCode,
                                 getLocalCurrency(), getDistChannelCode(), revnStrmCategory);
        prcTot.extAmt = tot;
    }

    private double fillChannelPrice(String revnStrmCategory, String brandCode, List parts){
        double tot = 0;

        for(Iterator it = parts.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();

            if(qli.isSaasTcvAcv()){
        		if(qli.getSaasBidTCV() != null){
        			tot += (qli.getSaasBpTCV() == null ? 0.0 : qli.getSaasBpTCV().doubleValue());
        		}
        	}else{
        		if(qli.getLocalExtProratedDiscPrc() != null){
        			tot += (qli.getChannelExtndPrice() == null ? 0.0 : qli.getChannelExtndPrice().doubleValue());
        		}
        	}
        }

        addChannelPriceTotal(brandCode, revnStrmCategory, tot);

        return tot;
    }

    private PriceTotal getPriceTotal(String prcType, String prcSumLevelCode, String currencyCode,
            String distChannelCode, String revnStrmCategory)  {

        String key = this.createKey(distChannelCode, prcType, prcSumLevelCode, currencyCode, revnStrmCategory);
        PriceTotal pt = (PriceTotal)this.localCurrencyPrices.get(key);

        if(null == this.localCurrencyPrices.get(key)){

            pt = new PriceTotal();
            pt.distChannelCode = distChannelCode;
            pt.prcSumLevelCode = prcSumLevelCode;
            pt.revnStrmCategory = revnStrmCategory;
            pt.prcType = prcType;
            pt.currencyCode = currencyCode;

            this.localCurrencyPrices.put(key,pt);
        }

        return pt;
    }

    private Map groupByRevnStrmCategoryAndBrandCode(List lineItems){
        Map revnStrmCategoryMap = new HashMap();

        for(Iterator it = lineItems.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();

            process(revnStrmCategoryMap, qli.getRevnStrmCategoryCode(), qli);
            process(revnStrmCategoryMap, PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL, qli);
        }

        return revnStrmCategoryMap;
    }

    private void process(Map revnStrmCategoryMap, String revnStrmCategoryCode, QuoteLineItem qli){
        Map brandMap = (Map)revnStrmCategoryMap.get(revnStrmCategoryCode);
        if(brandMap == null){
            brandMap = new HashMap();
            revnStrmCategoryMap.put(revnStrmCategoryCode, brandMap);
        }

        List parts = (List)brandMap.get(qli.getSwProdBrandCode());
        if(parts == null){
            parts = new ArrayList();
            brandMap.put(qli.getSwProdBrandCode(), parts);
        }

        parts.add(qli);
    }

    private List getValidLineItems() {
        List lineItems = quote.getLineItemList();
        List results = new ArrayList();

        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if (QuoteCommonUtil.shouldIncludeForTotalPrice(quote.getQuoteHeader(), item)) {
                results.add(item);
            }
        }

        return results;
    }

    public void cleanAndCalcuate() throws TopazException {
        QuotePriceTotalsFactory.singleton().removePriceTotals(
                   this.quote.getQuoteHeader().getWebQuoteNum());
        calculate();
    }

}