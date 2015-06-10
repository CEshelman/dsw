
package com.ibm.dsw.quote.draftquote.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.configurator.preprocessor.FctToPAPreProcessor;
import com.ibm.dsw.quote.customer.action.PrepareConfiguratorRedirectDataBaseAction;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Jun 28, 2007
 */

public class PartPriceHelper {
    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
    public static void calculateQuoteTotalPrice(Quote quote) throws TopazException {
        double quoteTotalPrice = 0;
        double quoteTotEntldExtPrc = 0;
        double quoteSaaSTotCmmtmtVal=0;
        double totalBpTCV = 0;
        boolean hasBpTcvActiveItem = false;
        boolean shouldCalculateTotalBpTCV = quote.getQuoteHeader().isChannelQuote();

        List lineItemList = quote.getLineItemList();
        for (int i = 0; i < lineItemList.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItemList.get(i);
            if(item.isReplacedPart()){
            	continue;
            }
            if(isQulifiedPrice(item,quote)){
            	if(item.isSaasTcvAcv()){
            		if(item.getSaasBidTCV() != null){
            			quoteTotalPrice = quoteTotalPrice + item.getSaasBidTCV().doubleValue();
            		}
            		if(item.getSaasEntitledTCV() != null){
            			quoteTotEntldExtPrc = quoteTotEntldExtPrc + item.getSaasEntitledTCV().doubleValue();
            		}
            		if(shouldCalculateTotalBpTCV && item.getSaasBpTCV() != null){
            			hasBpTcvActiveItem = true;
            			totalBpTCV = totalBpTCV + item.getSaasBpTCV().doubleValue();
            		}
            	}else{
            		if(item.getLocalExtProratedDiscPrc() != null){
            			quoteTotalPrice = quoteTotalPrice + item.getLocalExtProratedDiscPrc().doubleValue();
            		}
            		if(item.getLocalExtProratedPrc() != null){
            			quoteTotEntldExtPrc = quoteTotEntldExtPrc + item.getLocalExtProratedPrc().doubleValue();
            		}
            	}
            }
            quoteSaaSTotCmmtmtVal += (item.getSaasBidTCV()==null ? 0.0 : item.getSaasBidTCV().doubleValue() );
        }
        quote.getQuoteHeader().setQuotePriceTot(quoteTotalPrice);
        quote.getQuoteHeader().setSaasTotCmmtmtVal(quoteSaaSTotCmmtmtVal);
        quote.getQuoteHeader().setQuoteTotalEntitledExtendedPrice(quoteTotEntldExtPrc);
        if(hasBpTcvActiveItem && shouldCalculateTotalBpTCV){
        	quote.getQuoteHeader().setSaasBpTCV(new Double(totalBpTCV));
        }else{
        	quote.getQuoteHeader().setSaasBpTCV(null);
        }
        //Only set implied growth % and overall growth % to header if the quote is eligible for growth delegation
        quote.calculateYtyAndImpldGrwthPctTotal();
        
        // Calculate the multiple implied growth for additional year
        quote.calculateMultipleAdditionalYearImpliedGrowth();
    }
    
    public static void calculateTotalPoint(Quote quote) throws TopazException {

        double quoteTotalPoints = 0;
        List lineItemList = quote.getLineItemList();
        for (int i = 0; i < lineItemList.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItemList.get(i);
            if(item.isReplacedPart()){
            	continue;
            }
            int qty = 1;
            if (item.getPartQty() != null) {
                qty = item.getPartQty().intValue();
            }
            BigDecimal totalPointDecimal = new BigDecimal(qty).multiply(new BigDecimal(item.getContributionUnitPts()));
            int scale = 3;
            if (item.getContributionUnitPts() < 1){
            	scale = 5;
            }
            double lineItemTotalPoint = totalPointDecimal.setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue();
            item.setContributionExtPts(lineItemTotalPoint);
            quoteTotalPoints += lineItemTotalPoint;

        }
        quote.getQuoteHeader().setTotalPoints(quoteTotalPoints);

    }

    private static boolean isQulifiedPrice(QuoteLineItem item,Quote quote){
        if(quote.getQuoteHeader().isRenewalQuote() &&
                item.getPartQty()!=null &&
                item.getPartQty().intValue()==0 ){
            // part is from renewal quote but quantity is set to 0, don't calcuate the price
            return false;
        }

        return (item.getPartQty() != null || item.isSaasPart() || item.isMonthlySoftwarePart());
    }
   /* public static List findAllSapLineItmsWithOvrdUnitPrice(Quote quote){

        List result = new ArrayList();

        List lineItems = quote.getSapLineItemList();
        for(int i=0;i<lineItems.size();i++){
            SapLineItem sapItem= (SapLineItem)lineItems.get(i);
            if(sapItem.getOverrideUnitPrc() != null){
                result.add(sapItem);
            }
        }

        return result;

    }*/
    
    private static double calculateDiscountWithoutRounding(QuoteLineItem item){
    	boolean isCalculateUnitDisCount = false;
    	boolean isCalculateExtDisCount = false;
    	if (item.isSaasPart() || item.isMonthlySoftwarePart()){
    		if (PartPriceSaaSPartConfigFactory.singleton().canInputUnitOvrrdPrice(item)&&
    				null != item.getOverrideUnitPrc()){
    			isCalculateUnitDisCount = true;
    		} else if (PartPriceSaaSPartConfigFactory.singleton().canInputExtndOvrrdPrice(item)&&
    				null != item.getOvrrdExtPrice()){
    			isCalculateExtDisCount = true;
    		}
    	} else {
    		if (null != item.getOverrideUnitPrc()){
    			isCalculateUnitDisCount = true;
    		} else if (null != item.getOvrrdExtPrice()){
    			isCalculateExtDisCount = true;
    		}
    	}

        if (isCalculateUnitDisCount) {

            double ovrrideUnitPrc = item.getOverrideUnitPrc().doubleValue();
            Double proratedPrc = item.getLocalUnitProratedPrc();
            logContext.debug(PartPriceHelper.class,"ovrrideUnitPrc = "+ ovrrideUnitPrc);
            logContext.debug(PartPriceHelper.class,"Proration Price =" + proratedPrc);
            if ((proratedPrc!=null) && DecimalUtil.isNotEqual(proratedPrc.doubleValue(),0.0)) {
                double discount = 1 - ovrrideUnitPrc / proratedPrc.doubleValue();
                return discount*100;
            }
            else{
                return 0.0;
            }
        }
        //calculate the discount by override extended price
        else if (isCalculateExtDisCount) {

            double ovrrideExtPrc = item.getOvrrdExtPrice().doubleValue();
            Double proratedPrc = item.getLocalExtProratedPrc();
            logContext.debug(PartPriceHelper.class,"ovrrideExtPrc = "+ ovrrideExtPrc);
            logContext.debug(PartPriceHelper.class,"Proration Price =" + proratedPrc);
            if ((proratedPrc!=null) && DecimalUtil.isNotEqual(proratedPrc.doubleValue(),0.0)) {
                double discount = 1 - ovrrideExtPrc / proratedPrc.doubleValue();
                return discount*100;
            }
            else{
                return 0.0;
            }
        }
        else{
            return 0.0;
        }
    }

    public static double calculateDiscount(QuoteLineItem item) throws TopazException {
    	double disc = calculateDiscountWithoutRounding(item);
    	
    	return Double.valueOf(DecimalUtil.formatTo5Number(disc));
    }

    //Calculate discount for all line items
    public static void calculateDiscount(List lineItems, QuoteHeader header) throws TopazException, QuoteException{
    	PartPriceProcess ppProcess = PartPriceProcessFactory.singleton().create();
    	
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if (isCalulateOvrrdDiscout(item)) {
                //For cmprss cvreage applied parts, discount should be copied from cmprss cvrage additional discount
                if(item.hasValidCmprssCvrageMonth()){
                    if(item.hasValidCmprssCvrageDiscPct()){
                        item.setLineDiscPct(item.getCmprssCvrageDiscPct().doubleValue());
                    } else {
                        item.setLineDiscPct(0);
                    }
                } else {
                    double discount = calculateDiscount(item);
                    item.setLineDiscPct(discount);
                }
            }
            
            if(!header.isSubmittedQuote()){
            	continue;
            }
            YTYGrowth yty = item.getYtyGrowth();
            if(yty != null){
            	 if(!yty.isPctManuallyEntered()){
            		 Double ytyGrowth = GrowthDelegationUtil.getYtyGrowthPct(item, header);
            		 yty.setYTYGrowthPct(ytyGrowth);
        			 //ppProcess.doCalculateYtyGrowth(item, header);
            	}
            }
        }
    }
    

    private static boolean isCalulateOvrrdDiscout(QuoteLineItem item){
    	if (item.isSaasPart() || item.isMonthlySoftwarePart()){
    		if(PartPriceSaaSPartConfigFactory.singleton().canInputUnitOvrrdPrice(item)
            		&&null != item.getOverrideUnitPrc()){
        		return true;
        	}else if(PartPriceSaaSPartConfigFactory.singleton().canInputExtndOvrrdPrice(item)
    		&&null != item.getOvrrdExtPrice()){
        		return true;
        	}
    	} else {
    		if (null != item.getOverrideUnitPrc() || null != item.getOvrrdExtPrice()){
    			return true;
    		}
    	}

    	return false;
    }

    public static RedirectConfiguratorDataBasePack calculateFctToPAMigrationTerm(String chrgAgrmtNum, String configurationId, Date provisioningDate)throws QuoteException{
    	if(chrgAgrmtNum == null || configurationId == null){
    		return null;
    	}
    	RedirectConfiguratorDataBasePack dataPack = new RedirectConfiguratorDataBasePack();
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		List<ActiveService> activeServices = quoteProcess.retrieveLineItemsFromOrder(chrgAgrmtNum, configurationId);
		if(activeServices == null || activeServices.size() == 0){
			return null;
		}
		activeServices = PrepareConfiguratorRedirectDataBaseAction.serviceFilter(activeServices, true);

		if(activeServices!=null){
			ActiveService srv = null;
			for(Iterator i = activeServices.iterator();i.hasNext();){
				srv = (ActiveService) i.next();
				if(!CustomerConstants.CONFIGURATOR_RAMPUP_FLAG_1.equals(srv.getRampupFlag())){
					dataPack.addActiveService(srv);
					srv.setActiveOnAgreementFlag(CustomerConstants.CONFIGURATOR_ACTIVEONAGREEMENTFLAG_FLAG_1);
				}
			}
		}

		if(activeServices == null || activeServices.size() == 0 || dataPack.getActiveServices() == null || dataPack.getActiveServices().size() == 0){
			return null;
		}

		dataPack.setProvisioningDate(provisioningDate);
		FctToPAPreProcessor ftpProc = new FctToPAPreProcessor();
		ftpProc.doPreProcess(dataPack);
    	return dataPack;
    }
    public static RedirectConfiguratorDataBasePack calculateAddOnFinalizationTerm(String chrgAgrmtNum, String configurationId, Date provisioningDate, Date cotermEndDate, String configrtnActionCode,
    		boolean isTermExtension,ServiceDateModType serviceDateModType)throws QuoteException{
    	if(chrgAgrmtNum == null || configurationId == null){
    		return null;
    	}
    	RedirectConfiguratorDataBasePack dataPack = new RedirectConfiguratorDataBasePack();
    	dataPack.setConfigrtnActionCode(configrtnActionCode);
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		List<ActiveService> activeServices = quoteProcess.retrieveLineItemsFromOrderNoTx(chrgAgrmtNum, configurationId);
		if(activeServices == null || activeServices.size() == 0){
			return null;
		}
		activeServices = PrepareConfiguratorRedirectDataBaseAction.serviceFilter(activeServices, true);

		if(activeServices!=null){
			ActiveService srv = null;
			for(Iterator i = activeServices.iterator();i.hasNext();){
				srv = (ActiveService) i.next();
				if(!CustomerConstants.CONFIGURATOR_RAMPUP_FLAG_1.equals(srv.getRampupFlag())){
					dataPack.addActiveService(srv);
					srv.setActiveOnAgreementFlag(CustomerConstants.CONFIGURATOR_ACTIVEONAGREEMENTFLAG_FLAG_1);
				}
			}
		}

		if(activeServices == null || activeServices.size() == 0 || dataPack.getActiveServices() == null || dataPack.getActiveServices().size() == 0){
			return null;
		}

		dataPack.setProvisioningDate(provisioningDate);
		dataPack.setCotermEndDate(cotermEndDate);
		dataPack.setTermExtension(isTermExtension);
		dataPack.setServiceModelType(serviceDateModType);
		FctToPAPreProcessor ftpProc = new FctToPAPreProcessor();
		ftpProc.doPreProcess(dataPack);
    	return dataPack;
    }
    
    public static List<ActiveService> getActiveService(String chrgAgrmtNum, String configurationId)throws QuoteException{
    	if(chrgAgrmtNum == null || configurationId == null){
    		return null;
    	}
    	RedirectConfiguratorDataBasePack dataPack = new RedirectConfiguratorDataBasePack();
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		List<ActiveService> activeServices = quoteProcess.retrieveLineItemsFromOrderNoTx(chrgAgrmtNum, configurationId);
		if(activeServices == null || activeServices.size() == 0){
			return null;
		}
		activeServices = PrepareConfiguratorRedirectDataBaseAction.serviceFilter(activeServices, true);
		List<ActiveService> activeList = new ArrayList<ActiveService> ();
		if(activeServices!=null){
			ActiveService srv = null;
			for(Iterator i = activeServices.iterator();i.hasNext();){
				srv = (ActiveService) i.next();
				activeList.add(srv);
			}
		}
    	return activeList;
    }
    
    public static boolean canShowYtyGrowthPct(QuoteLineItem qli,Quote quote){
    	String priorUnitPrc="";
    	String quoteCurrencyCode=quote.getQuoteHeader().getCurrencyCode()!=null?quote.getQuoteHeader().getCurrencyCode():""; 
    	String priorYearSSPriceCurrency="";
    	//when YtyGrowth is null,use PriorYearSSPrice's localUnitPrice to calculate ytyGrowthPct
    	if(qli.getPriorYearSSPrice()!=null){
    		priorUnitPrc = qli.getPriorYearSSPrice().getPriorYrLocalUnitPrice12Mnths() !=null?qli.getPriorYearSSPrice().getPriorYrLocalUnitPrice12Mnths():"";
    		priorYearSSPriceCurrency=qli.getPriorYearSSPrice().getPriorYrCurrncyCode() != null ?qli.getPriorYearSSPrice().getPriorYrCurrncyCode():"";
    	}
    	
    	//currency mismatch but no manual LPP yet
    	if(!org.apache.commons.lang.StringUtils.equals(quoteCurrencyCode, priorYearSSPriceCurrency)){
			if(qli.getYtyGrowth() == null 
					|| qli.getYtyGrowth().getManualLPP() == null){
				return false;
			} else {
				return true;
			}
    	} else {
    		if(org.apache.commons.lang.StringUtils.isEmpty(priorUnitPrc)){
    			if(qli.getYtyGrowth() == null 
    					|| qli.getYtyGrowth().getManualLPP() == null){
    				return false;
    			} else {
    				return true;
    			}
    		} else {
    			return true;
    		}
    	}
    }

}

