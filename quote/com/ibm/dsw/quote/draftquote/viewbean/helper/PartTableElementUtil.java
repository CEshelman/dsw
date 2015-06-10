package com.ibm.dsw.quote.draftquote.viewbean.helper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class PartTableElementUtil implements Serializable {
	private static final long serialVersionUID = -17648032300412801L;
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

	private PartPriceCommon partPriceCommon;
	
    protected Quote quote;

    public PartTableElementUtil(Quote quote, PartPriceCommon partPriceCommon) {
    	this.quote = quote;
        this.partPriceCommon = partPriceCommon;
    }

    /**
     * @param qli
     * @param isPriceUnAvaialbe
     * @return the SaaS part Entitled rate string value for both draft and submitted quote
     * 1)if draft quote param shouldBoldFont is false
     * 2)if submitted quote param shouldBoldFont is true
     */
    public String getSaasEntitledRateStrVal(QuoteLineItem qli, boolean isPriceUnAvaialbe, boolean shouldBoldFont, boolean isTier2Reseller){
    	StringBuffer resultStr = new StringBuffer("");
    	if(isTier2Reseller 
        	&& !ApplicationProperties.getInstance().getT2PriceAvailable() 
        	&& qli.isReplacedPart()){
        	return DraftQuoteConstants.BLANK;
        }
    	else if(quote.getQuoteHeader().isChannelQuote() && qli.isReplacedPart()){
    		return DraftQuoteConstants.BLANK;
    	}else{
    		if(!qli.isReplacedPart()){
	    		if(shouldBoldFont){
	    			resultStr.append("<strong>");
	    		}
	    		if(partPriceCommon.showSaaSEntitledUnitPrice(qli)){
		    		resultStr.append(partPriceCommon.getLineItemEntitledUnitPrc(qli,isPriceUnAvaialbe));
		    	} else if(partPriceCommon.showItemTotContractVal(qli) && partPriceCommon.showSaaSEntitledExtPrice(qli) && (qli.isSaasSubscrptnPart() || qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart()) ){
		    		resultStr.append(partPriceCommon.getLineItemEntitledExtendedPrc(qli,isPriceUnAvaialbe));
		    		if(partPriceCommon.showBillingFrequency(qli)){
		    			resultStr.append("<br />");
		    			resultStr.append(partPriceCommon.getBillgFrqncyDscr(qli));
		    		}
		    	}
	    		if(shouldBoldFont){
	    			resultStr.append("</strong>");
	    		}
    		}
    	}
    	return resultStr.toString();
    }

    /**
     * @param qli
     * @param isPriceUnAvaialbe
     * @param isTier2Reseller
     * @return the SaaS part bid rate string value for both draft and submitted quote
     * 1)if draft quote param shouldBoldFont is false
     * 2)if submitted quote param shouldBoldFont is true
     */
    public String getSaasBidRateStrVal(QuoteLineItem qli, boolean isPriceUnAvaialbe, boolean shouldBoldFont, boolean isTier2Reseller){
    	StringBuffer resultStr = new StringBuffer("");
    	if(isTier2Reseller 
    		&& !ApplicationProperties.getInstance().getT2PriceAvailable() 
    		&& qli.isReplacedPart()){
    		return DraftQuoteConstants.BLANK;
    	}
    	else if(quote.getQuoteHeader().isChannelQuote() && qli.isReplacedPart()){
    		return DraftQuoteConstants.BLANK;
    	}else{
    		if(shouldBoldFont){
    			resultStr.append("<strong>");
    		}
    		if(partPriceCommon.showSaaSBidUnitPrice(qli)){
	    		resultStr.append(partPriceCommon.getSaaSBidUnitPrcVal(qli,isPriceUnAvaialbe));
	    	} else if(partPriceCommon.showItemTotContractVal(qli) && partPriceCommon.showSaaSBidExtPrice(qli) && (qli.isSaasSubscrptnPart() || qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart())){
	    		resultStr.append(partPriceCommon.getLineItemBidExtendedPrc(qli,isPriceUnAvaialbe));
	    		if(partPriceCommon.showBillingFrequency(qli)){
	    			resultStr.append("<br />");
	    			resultStr.append(partPriceCommon.getBillgFrqncyDscr(qli));
	    		}
	    	}
    		if(shouldBoldFont){
    			resultStr.append("</strong>");
    		}
    	}
    	return resultStr.toString();
    }
    
    /**
     * @param qli
     * @param isPriceUnAvaialbe
     * @param isTier2Reseller
     * @return the SaaS part Entitled Total Commit Value string value for both draft and submitted quote
     * 1)if draft quote param shouldBoldFont is false
     * 2)if submitted quote param shouldBoldFont is replacePartFlag
     */
    public String getSaasEntitledTcvStrVal(QuoteLineItem qli, boolean isPriceUnAvaialbe, boolean shouldBoldFont, boolean isTier2Reseller){
    	StringBuffer resultStr = new StringBuffer("");
    	if(isTier2Reseller 
        	&& !ApplicationProperties.getInstance().getT2PriceAvailable() 
        	&& qli.isReplacedPart()){
        	return DraftQuoteConstants.BLANK;
        }
        else if(quote.getQuoteHeader().isChannelQuote() && qli.isReplacedPart()){
    		return DraftQuoteConstants.BLANK;
    	}else{
	    	if(partPriceCommon.showItemTotContractVal(qli)){
	    		if(!StringUtils.isBlank(partPriceCommon.getSaasEntitledTCV(qli,isPriceUnAvaialbe))){
	    			if(shouldBoldFont){
		    			resultStr.append("<strong>");
		    		}
		    		resultStr.append(partPriceCommon.getSaasEntitledTCV(qli,isPriceUnAvaialbe));
		    		if(shouldBoldFont){
		        		resultStr.append("</strong>");
		        	}
	    		}    		
	    	} else if(partPriceCommon.showSaaSEntitledExtPrice(qli)){
	    		if(!StringUtils.isBlank(partPriceCommon.getLineItemEntitledExtendedPrc(qli,isPriceUnAvaialbe))){
	    			if(shouldBoldFont){
		    			resultStr.append("<strong>");
		    		}
		    		resultStr.append(partPriceCommon.getLineItemEntitledExtendedPrc(qli,isPriceUnAvaialbe));
		    		if(shouldBoldFont){
		        		resultStr.append("</strong>");
		        	}
	    		}	    		
	    		if(partPriceCommon.showBillingFrequency(qli)){
	    			resultStr.append("<br />");
	    			resultStr.append(partPriceCommon.getBillgFrqncyDscr(qli));
	    		}
	    	}
    	}
    	return resultStr.toString();
    }
	
    /**
     * @param qli
     * @param isPriceUnAvaialbe
     * @param isTier2Reseller
     * @return the SaaS part bid Total Commit Value string value for both draft and submitted quote
     * 1)if draft quote param shouldBoldFont is false
     * 2)if submitted quote param shouldBoldFont is replacePartFlag
     */
    public String getSaasBidTcvStrVal(QuoteLineItem qli, boolean isPriceUnAvaialbe, boolean shouldBoldFont, boolean isTier2Reseller){
    	StringBuffer resultStr = new StringBuffer("");
    	if(isTier2Reseller 
            && !ApplicationProperties.getInstance().getT2PriceAvailable() 
            && qli.isReplacedPart()){
            return DraftQuoteConstants.BLANK;
        }
        else if(quote.getQuoteHeader().isChannelQuote() && qli.isReplacedPart()){
    		return DraftQuoteConstants.BLANK;
    	}else{
	    	if(partPriceCommon.showItemTotContractVal(qli)){
	    		if(shouldBoldFont){
	    			resultStr.append("<strong>");
	    		}
	    		resultStr.append(partPriceCommon.getSaasBidTCV(qli,isPriceUnAvaialbe));
	    		if(shouldBoldFont){
	        		resultStr.append("</strong>");
	        	}
	    	} else if(partPriceCommon.showSaaSBidExtPrice(qli)){
	    		if(shouldBoldFont){
	    			resultStr.append("<strong>");
	    		}
	    		resultStr.append(partPriceCommon.getLineItemBidExtendedPrc(qli,isPriceUnAvaialbe));
	    		if(shouldBoldFont){
	        		resultStr.append("</strong>");
	        	}
	    		if(partPriceCommon.showBillingFrequency(qli)){
	    			resultStr.append("<br />");
	    			resultStr.append(partPriceCommon.getBillgFrqncyDscr(qli));
	    		}
	    	}
    	}
    	return resultStr.toString();
    }
    
    public String getRewalModCodeDesc(QuoteLineItem qli){
    	return partPriceCommon.getRewalModCodeDesc(qli);
    }
}
