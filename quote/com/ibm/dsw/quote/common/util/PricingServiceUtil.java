package com.ibm.dsw.quote.common.util;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;

public class PricingServiceUtil {
	
	/**
	 * @param itemOut
	 * @param lineItem
	 * @param quote
	 * @return
	 * If software part and not EOL part, return itemOut.getLclUnitPrc()
	 */
	public static Double getLocalUnitPrc(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getLocalUnitPrc();
    	if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		if(!lineItem.isObsoletePart()){
			returnPrice = itemOut.getLclUnitPrc();
		}
		return returnPrice;
	}
	
	public static Double getLocalUnitProratedPrc(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getLocalUnitProratedPrc();
		if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		//If eol part, return original price
		if(lineItem.isObsoletePart()){
			return returnPrice;
		}
		//If software part
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
			if(!lineItem.isObsoletePart()){
				returnPrice = itemOut.getPrUnitPrc();
			}
		}else if(lineItem.isSaasPart()){//If SaaS part
	        if(lineItem.isSaasOnDemand() 
	        	|| lineItem.isSaasSubscrptnOvragePart()
	        	|| lineItem.isSaasSetUpOvragePart()){
	        	returnPrice = itemOut.getUserOverage();
	        }else if(lineItem.isSaasDaily()){
	        	returnPrice = itemOut.getLclUnitPrc();
	        }else if(lineItem.isSaasSetUpPart()
		        || lineItem.isSaasSubscrptnPart()
		        || lineItem.isSaasSubsumedSubscrptnPart()
		        || lineItem.isSaasProdHumanServicesPart()){
		        returnPrice = itemOut.getPrUnitPrc();
		    }
		}else if(lineItem.isMonthlySoftwarePart()){//If monthly software part
			if(((MonthlySwLineItem)lineItem).isMonthlySwOnDemandPart()
		        	|| ((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnOvragePart()
		        	){
		        returnPrice = itemOut.getUserOverage();
		    }else if(((MonthlySwLineItem)lineItem).isMonthlySwDailyPart()){
		    	returnPrice = itemOut.getLclUnitPrc();
		    }else if(((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart()){
			    returnPrice = itemOut.getPrUnitPrc();
			}
		}
		return returnPrice;
	}
	
	public static Double getLocalUnitProratedDiscPrc(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getLocalUnitProratedDiscPrc();
		if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		//If software part
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
			if (quote.getQuoteHeader().isChannelQuote()) {
				returnPrice = itemOut.getUsrUnitPrc();
			} else {
				returnPrice = itemOut.getUnitPrc();
			}
		}else if(lineItem.isSaasPart()){//If SaaS part
	        if(lineItem.isSaasOnDemand() 
	        		|| lineItem.isSaasSubscrptnOvragePart()
	        		|| lineItem.isSaasSetUpOvragePart()){
	        	if(lineItem.getOverrideUnitPrc() != null || lineItem.getLineDiscPct() != 0){
	        		returnPrice = itemOut.getUserSpbdOverage();
	        	}else{
	        		returnPrice = itemOut.getUserOverage();
	        	}
	        }else if(lineItem.isSaasDaily()){
	        	returnPrice = itemOut.getUsrUnitPrc();	
	        }else if(lineItem.isSaasSetUpPart()
	        		|| lineItem.isSaasSubscrptnPart()
	        		|| lineItem.isSaasSubsumedSubscrptnPart()
			        || lineItem.isSaasProdHumanServicesPart()){
	        	if(lineItem.getLocalUnitProratedPrc() != null){
	        		returnPrice = lineItem.getLocalUnitProratedPrc().doubleValue() * (1 - lineItem.getLineDiscPct()/100);
	        	}
			}
		}else if(lineItem.isMonthlySoftwarePart()){//If monthly software part
	        if(((MonthlySwLineItem)lineItem).isMonthlySwOnDemandPart()
		        	|| ((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnOvragePart()
		        	){
	        	if(lineItem.getOverrideUnitPrc() != null || lineItem.getLineDiscPct() != 0){
	        		returnPrice = itemOut.getUserSpbdOverage();
	        	}else{
	        		returnPrice = itemOut.getUserOverage();
	        	}
	        }else if(((MonthlySwLineItem)lineItem).isMonthlySwDailyPart()){
	        	returnPrice = itemOut.getUsrUnitPrc();	
	        }else if(((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart()){
	        	if(lineItem.getLocalUnitProratedPrc() != null){
	        		returnPrice = lineItem.getLocalUnitProratedPrc().doubleValue() * (1 - lineItem.getLineDiscPct()/100);
	        	}
			}
		}
		return returnPrice;
	}
	
	public static Double getLocalExtPrc(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getLocalExtPrc();
		if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		//If software part
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
			if (quote.getQuoteHeader().isChannelQuote()) {
				returnPrice = itemOut.getUsrExtPrc();
			} else {
				returnPrice = itemOut.getExtPrc();
			}
		}else if(lineItem.isSaasPart()){//If SaaS part
	        if(lineItem.isSaasSetUpPart()
	        	|| lineItem.isSaasSubscrptnPart()
	        	|| lineItem.isSaasSubsumedSubscrptnPart()
	        	|| lineItem.isSaasProdHumanServicesPart()){
	        	returnPrice = itemOut.getUsrExtPrc();
	        }
		}else if(lineItem.isMonthlySoftwarePart()){//If monthly software part
	        if(((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart()){
		        	returnPrice = itemOut.getUsrExtPrc();
		        }
			}
		return returnPrice;
	}
	
	public static Double getLocalExtProratedPrc(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getLocalExtProratedPrc();
		if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		//If eol part, return original price
		if(lineItem.isObsoletePart()){
			return lineItem.getLocalExtProratedPrc();
		}
		//If software part
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
	        if(!lineItem.isObsoletePart()){
	        	returnPrice = itemOut.getPrExtPrc();
	        }
		}else if(lineItem.isSaasPart()){//If SaaS part
	        if(lineItem.isSaasSetUpPart()
	        	|| lineItem.isSaasSubscrptnPart()
	        	|| lineItem.isSaasSubsumedSubscrptnPart()
	        	|| lineItem.isSaasProdHumanServicesPart()){
	        	returnPrice = itemOut.getPrExtPrc();
	        }
		}else if(lineItem.isMonthlySoftwarePart()){//If monthly software part
	        if(((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart()){
		        	returnPrice = itemOut.getPrExtPrc();
		        }
			}
		return returnPrice;
	}
	
	public static Double getLocalExtProratedDiscPrc(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getLocalExtProratedDiscPrc();
		if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		//If software part
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
	        if (quote.getQuoteHeader().isChannelQuote()) {
	        	returnPrice = itemOut.getUsrExtPrc();
	        } else {
	        	returnPrice = itemOut.getExtPrc();
	        }
		}else if(lineItem.isSaasPart()){//If SaaS part
	        if(lineItem.isSaasSetUpPart()
	        	|| lineItem.isSaasSubscrptnPart()
	        	|| lineItem.isSaasSubsumedSubscrptnPart()
	        	|| lineItem.isSaasProdHumanServicesPart()){
	        	returnPrice = itemOut.getUsrExtPrc();
	        }
		}else if(lineItem.isMonthlySoftwarePart()){//If monthly software part
	        if(((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart()){
		        	returnPrice = itemOut.getUsrExtPrc();
		        }
			}
		return returnPrice;
	}
	
	public static Double getChannelUnitPrice(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getChannelUnitPrice();
		if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		//If software part
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
	        if (quote.getQuoteHeader().isChannelQuote()) {
	        	returnPrice = itemOut.getUnitPrc();
	        } else {
	        	returnPrice = null;
	        }
		}else if(lineItem.isSaasPart()){//If SaaS part
			if (quote.getQuoteHeader().isChannelQuote()) {
				if (lineItem.isSaasSetUpPart()
						|| lineItem.isSaasSubscrptnPart()
						|| lineItem.isSaasSubsumedSubscrptnPart()
						|| lineItem.isSaasProdHumanServicesPart()) {
					returnPrice = itemOut.getUnitPrc();
				}
				else if (lineItem.isSaasOnDemand()
						|| lineItem.isSaasSubscrptnOvragePart()
						|| lineItem.isSaasSetUpOvragePart()) {
					if (lineItem.getOverrideUnitPrc() != null) {
						returnPrice = itemOut.getSpbdOveragePrc();
					} else {
						returnPrice = itemOut.getOveragePrc();
					}
				}
				else if (lineItem.isSaasDaily()) {
					returnPrice = itemOut.getUnitPrc();
				}
				else if (lineItem.isSaasSetUpPart()
						|| lineItem.isSaasSubscrptnPart()
						|| lineItem.isSaasSubsumedSubscrptnPart()
						|| lineItem.isSaasProdHumanServicesPart()) {
					returnPrice = itemOut.getUnitPrc();
				}
			//If direct quote, set null
			} else {
				returnPrice = null;
			}
		}else if(lineItem.isMonthlySoftwarePart()){//If monthly software part
			if (quote.getQuoteHeader().isChannelQuote()) {
				if (((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart()) {
					returnPrice = itemOut.getUnitPrc();
				}
				else if (((MonthlySwLineItem)lineItem).isMonthlySwOnDemandPart()
			        	|| ((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnOvragePart()) {
					if (lineItem.getOverrideUnitPrc() != null) {
						returnPrice = itemOut.getSpbdOveragePrc();
					} else {
						returnPrice = itemOut.getOveragePrc();
					}
				}
				else if (((MonthlySwLineItem)lineItem).isMonthlySwDailyPart()) {
					returnPrice = itemOut.getUnitPrc();
				}
				else if (((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart()) {
					returnPrice = itemOut.getUnitPrc();
				}
			//If direct quote, set null
			} else {
				returnPrice = null;
			}
		}
		return returnPrice;
	}
	
	public static Double getChannelExtndPrice(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getChannelExtndPrice();
		if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		//If software part
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
	        if (quote.getQuoteHeader().isChannelQuote()) {
	        	returnPrice = itemOut.getExtPrc();
	        } else {
	        	returnPrice = null;
	        }
		}else if(lineItem.isSaasPart()){//If SaaS part
			if (quote.getQuoteHeader().isChannelQuote()) {
				if (lineItem.isSaasSetUpPart()
						|| lineItem.isSaasSubscrptnPart()
						|| lineItem.isSaasSubsumedSubscrptnPart()
						|| lineItem.isSaasProdHumanServicesPart()) {
					returnPrice = itemOut.getExtPrc();
				}
			//If direct quote, set null
			} else {
				returnPrice = null;
			}
		}else if(lineItem.isMonthlySoftwarePart()){//If monthly software part
			if (quote.getQuoteHeader().isChannelQuote()) {
				if (((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart()) {
					returnPrice = itemOut.getExtPrc();
				}
			//If direct quote, set null
			} else {
				returnPrice = null;
			}
		}
		return returnPrice;
	}
	
	public static Double getLocalTaxAmt(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getLocalTaxAmt();
		if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		//If software part
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
	        if (quote.getQuoteHeader().isChannelQuote()) {
	        	//do nothing
	        } else {
	        	returnPrice = itemOut.getVatAmt();
	        }
		} else {// If SaaS part
			//do nothing
		}
		return returnPrice;
	}
	
	public static Double getLclExtndPriceIncldTax(ItemOut itemOut, QuoteLineItem lineItem, Quote quote){
		Double returnPrice = lineItem.getLclExtndPriceIncldTax();
		if(lineItem.isReplacedPart()){
        	return returnPrice;
    	}
		//If software part
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
	        if (quote.getQuoteHeader().isChannelQuote()) {
	        	//do nothing
	        } else {
	        	returnPrice = itemOut.getTotal();
	        }
		} else {// If SaaS part
			//do nothing
		}
		return returnPrice;
	}
	
	public static Double getLocalChnlTaxAmt(ItemOut itemOut,QuoteLineItem lineItem, Quote quote) {
		Double returnPrice = lineItem.getLocalChnlTaxAmt();
		if (lineItem.isReplacedPart()) {
			return returnPrice;
		}
		if (quote.getQuoteHeader().isChannelQuote()) {
			returnPrice = itemOut.getVatAmt();
		} else {
			returnPrice = null;
		}
		return returnPrice;
	}
	
	public static Double getLclExtndChnlPriceIncldTax(ItemOut itemOut,QuoteLineItem lineItem, Quote quote) {
		Double returnPrice = lineItem.getLclExtndChnlPriceIncldTax();
		if (lineItem.isReplacedPart()) {
			return returnPrice;
		}
		if (quote.getQuoteHeader().isChannelQuote()) {
			returnPrice = itemOut.getTotal();
		} else {
			returnPrice = null;
		}
		return returnPrice;
	}
	
	public static Double getSaasBidTCV(ItemOut itemOut,QuoteLineItem lineItem, Quote quote) {
		Double returnPrice = lineItem.getSaasBidTCV();
		if (lineItem.isReplacedPart()) {
			return returnPrice;
		}
		if(lineItem.isSaasPart() || lineItem.isMonthlySoftwarePart()){
			if (lineItem.isSaasTcvAcv()) {
				returnPrice = itemOut.getUserTcvAmount() == null ? new Double(0.00) : itemOut.getUserTcvAmount();
			} else {
				returnPrice = null;
			}
		}
		return returnPrice;
	}
	
	public static boolean getLegacyBasePriceUsedFlag(ItemOut itemOut,QuoteLineItem lineItem, Quote quote) {
		if (lineItem.isReplacedPart()) {
			return false;
		}
		return itemOut.getLegacyBasePriceUsedFlag().booleanValue();
	}
	
	public static String getRenewalPricingMethod(ItemOut itemOut,QuoteLineItem lineItem, Quote quote) {
		String renewalPricingMethod = null;
		if (lineItem.isReplacedPart()) {
			return renewalPricingMethod;
		}
		if(!lineItem.isSaasPart() && !lineItem.isMonthlySoftwarePart()){
			String priceStrategy = itemOut.getPriceStrategy();

			if (StringUtils.isNotBlank(priceStrategy)) {
				renewalPricingMethod = priceStrategy;
			}
		}
		return renewalPricingMethod;
	}
	
	public static Double getRenewalRsvpPrice(ItemOut itemOut, QuoteLineItem lineItem) {
		return GDPartsUtil.getRenewalRsvpPrice(itemOut.getLclUnitPrc(), lineItem);
	}
	
	public static Double getChnlStdDiscPct(ItemOut itemOut,QuoteLineItem lineItem, Quote quote) {
		Double chnlStdDiscPct = lineItem.getChnlStdDiscPct();
		if (lineItem.isReplacedPart()) {
			return chnlStdDiscPct;
		}
		if(quote.getQuoteHeader().isChannelQuote()){
			chnlStdDiscPct = itemOut.getStdPrtnrDiscPct();
		}else{
			chnlStdDiscPct = new Double(0.0);
		}
		return chnlStdDiscPct;
	}
	
	public static Double getChnlOvrrdDiscPct(ItemOut itemOut,QuoteLineItem lineItem, Quote quote) {
		Double chnlOvrrdDiscPct = lineItem.getChnlOvrrdDiscPct();
		if (lineItem.isReplacedPart()) {
			return chnlOvrrdDiscPct;
		}
		if(quote.getQuoteHeader().isChannelQuote()){
			if(!PartPriceConfigFactory.singleton().allowChannelMarginDiscount(quote)
                    && !QuoteCommonUtil.shouldSetELAAutoChnlDisc(quote)){
				chnlOvrrdDiscPct = null;          
            }
		}else{
			chnlOvrrdDiscPct = null;
		}
		return chnlOvrrdDiscPct;
	}
	
	public static Double getSaasBpTCV(ItemOut itemOut,QuoteLineItem lineItem, Quote quote) {
		Double returnPrice = lineItem.getSaasBpTCV();
		if (lineItem.isReplacedPart()) {
			return returnPrice;
		}
		if(quote.getQuoteHeader().isChannelQuote()){
			if(lineItem.isSaasPart() || lineItem.isMonthlySoftwarePart()){
				if (lineItem.isSaasTcvAcv()) {
					returnPrice = itemOut.getTotCtrctValueAmt() == null ? new Double(0.00) : itemOut.getTotCtrctValueAmt();
				} else {
					returnPrice = null;
				}
			}
		}else{
			returnPrice = null;
		}
		return returnPrice;
	}
	
}
