package com.ibm.dsw.quote.draftquote.util.validation;

import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceAppliancePartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.action.validator.PartPriceUIValidator;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract.LineItemParameter;
import com.ibm.dsw.quote.draftquote.util.date.DateCalculator;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>ValidationRule</code> is a abstract class for the common
 * validation it's the only entry to access validation, please refer to use case
 * PSQ02 for details
 *
 * Important: this class should be used in a transcation context
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Mar 26, 2007
 */

public abstract class ValidationRule {

    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Quote quote;

    protected PostPartPriceTabContract ct;

    //private boolean isSpecialBid = false;

    protected HashMap validationResults = new HashMap();

    protected boolean needUpdateGSA = false;

    protected int useGsaPricing;

    protected boolean itemCountChanged;

    protected boolean pymTermsDaysChanged = false;

    protected boolean validityDaysChanged = false;
    protected boolean provsningDaysChanged = false;
    protected boolean coTermChanged = false;
    protected boolean estmtdOrdDateChanged = false;
    protected boolean serviceDateChanged = false;

    ValidationRule(Quote quote, PostPartPriceTabContract ct) {
        this.quote = quote;
        this.ct = ct;

        List lineItems = this.quote.getLineItemList();

        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem lineItem = (QuoteLineItem) lineItems.get(i);
            String key = this.createKey(lineItem);
            validationResults.put(key, new ValidationResult());
        }
    }

    public static ValidationRule createRule(Quote quote, PostPartPriceTabContract ct) throws TopazException {

        if (quote.getQuoteHeader().isSalesQuote()) {
            String lob = quote.getQuoteHeader().getLob().getCode();
            if (QuoteConstants.LOB_PA.equals(lob) || QuoteConstants.LOB_PAE.equals(lob)) {
                PAAndPAERule r = new PAAndPAERule(quote, ct);
                return r;
            }
            if (QuoteConstants.LOB_PPSS.equals(lob)) {
                PPSSRule r = new PPSSRule(quote, ct);
                return r;
            }
            if (QuoteConstants.LOB_FCT.equals(lob) || QuoteConstants.LOB_OEM.equalsIgnoreCase(lob)) {
                FCTRule r = new FCTRule(quote, ct);
                return r;
            }
            if (QuoteConstants.LOB_SSP.equals(lob)) {
            	SSPRule r = new SSPRule(quote, ct);
                return r;
            }
            throw new TopazException("Fatal error, Unsupported LOB=" + lob);

        }

        if (quote.getQuoteHeader().isRenewalQuote()) {
            return new RenewalQuoteRule(quote, ct);
        }

        throw new TopazException("Fatal error, Unsupported Quote Type=" + quote.getQuoteHeader().getQuoteTypeCode());
    }

    public void execute() throws TopazException {

        // do some common vlaidation here
        validate();
        if (quote.getQuoteHeader().getOfferPrice() != null){
	        List masterItems = CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(quote.getLineItemList());
	        for (Iterator iter = masterItems.iterator(); iter.hasNext();) {
	            QuoteLineItem qli = (QuoteLineItem) iter.next();
	            boolean ex = Boolean.FALSE.equals(qli.isOfferIncldFlag());
	            List subItems = qli.getAddtnlYearCvrageLineItems();
	            for (Iterator iterSub = subItems.iterator(); iterSub.hasNext();){
	                QuoteLineItem qliSub = (QuoteLineItem) iterSub.next();
	                if (ex){
	                    qliSub.setOfferIncldFlag(Boolean.FALSE);
	                    qliSub.setOvrrdExtPrice(null);
	                }else{
	                    ex = Boolean.FALSE.equals(qliSub.isOfferIncldFlag());
	                }
	            }
	        }
        }
        //set mod_by_user_name to lineItems
        List lineItems = this.quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem lineItem = (QuoteLineItem) lineItems.get(i);
            lineItem.setSModByUserID(ct.getUserId());
        }
        //set Set line to RSVP/SRP to line item
        List quoteLineItemList = this.quote.getLineItemList();
        if(quoteLineItemList != null && quoteLineItemList.size() > 0){
        	 for (int i = 0; i < quoteLineItemList.size(); i++) {
                 QuoteLineItem lineItem = (QuoteLineItem) lineItems.get(i);
                 if(lineItem.isRenewalPart()){
                 	if(lineItem.getOverrideUnitPrc() != null && GrowthDelegationUtil.getProratedRSVPPrice(lineItem) != null && !lineItem.getOverrideUnitPrc().equals(GrowthDelegationUtil.getProratedRSVPPrice(lineItem))){
                 		 lineItem.setSetLineToRsvpSrpFlag(false);
                 	}
                 }
             }
        }
       

    }

    public abstract void validate() throws TopazException;

    protected String createKey(QuoteLineItem item) {
        return item.getPartNum().trim() + "_" + item.getSeqNum();
    }

    protected ValidationResult getValidationResult(String key) {
        return (ValidationResult) this.validationResults.get(key);
    }

    protected void writeLog(String field, QuoteLineItem item, Throwable e) {
        logContext.debug(this, "Can't find " + field + " value for LineItem(" + item.getPartNum() + ","
                + item.getSeqNum() + "),err=" + e.getMessage());
    }
    
//    protected String calculateDiscountByYtyGrowth(QuoteLineItem item) throws TopazException {
//		String key = createKey(item);
//		String quoteCurrencyCode=ct.getQuote().getQuoteHeader().getCurrencyCode()!=null?ct.getQuote().getQuoteHeader().getCurrencyCode():""; 
//		if(ct.getYtyRadio(key)!=null && DraftQuoteParamKeys.YTY_RADIO_YTY_GROWTH_VALUE.equals(ct.getYtyRadio(key))){
//			String priorYearSSPriceLpp="";
//			String priorYearSSPriceCurrency="";
//			Double ytyLpp=0.0;
//			String sLpp= "";
//			if(item.getYtyGrowth()!=null){
//				ytyLpp = item.getYtyGrowth().getManualLPP() !=null?item.getYtyGrowth().getManualLPP():0.0;
//	    	}
//			if(item.getPriorYearSSPrice()!=null){
//				priorYearSSPriceLpp = item.getPriorYearSSPrice().getPriorYrLocalUnitPrice12Mnths() !=null?item.getPriorYearSSPrice().getPriorYrLocalUnitPrice12Mnths():"";
//				priorYearSSPriceCurrency=item.getPriorYearSSPrice().getPriorYrCurrncyCode() != null ?item.getPriorYearSSPrice().getPriorYrCurrncyCode():"";
//	    	}
//			//prior to use YTY_Growth's lpp
//			if(ytyLpp > 0 && quoteCurrencyCode.equals(priorYearSSPriceCurrency)){
//				sLpp=String.valueOf(ytyLpp);
//			}else if(priorYearSSPriceLpp!=null && !"".equals(priorYearSSPriceLpp) && quoteCurrencyCode.equals(priorYearSSPriceCurrency) ){
//				sLpp=priorYearSSPriceLpp;
//			} 
//			if("".equals(sLpp)){
//				return ct.getPartDiscount(key);
//			}else{
//				double lpp=Double.valueOf(sLpp);
//				String sYtyGrowth = ct.getYty(key);
//				double ytyGrowth = 0.0;
//				if(StringUtils.isNotBlank(sYtyGrowth)){
//					ytyGrowth=Double.parseDouble(sYtyGrowth);
//				}
//				double months = GrowthDelegationUtil.calculateMonths(item);
//				if(months <= 0){
//					return ct.getPartDiscount(key);
//				}
//				double bidUnitPrice = (ytyGrowth / 100 + 1) * lpp * months / 12;
//				double entitledPrice = item.getOrigUnitPrice();
//				double discountPercent = (1-bidUnitPrice/entitledPrice)*100;
//				return String.valueOf(discountPercent);
//			}
//			
//		}else{
//			return ct.getPartDiscount(key);
//		}
//    }
    
    protected String calculateOverridePriceByYtyGrowth(QuoteLineItem item) throws TopazException {
    	String key = createKey(item);
		if(ct.getYtyRadio(key)!=null && ct.getYtyRadio(key).equals(DraftQuoteParamKeys.YTY_RADIO_YTY_GROWTH_VALUE)){
			return "";
		}else{
			return ct.getPartPrice(key);
		}
		 
    }
    protected void validateOverrideUnitPriceAndDiscount(QuoteLineItem item) throws TopazException {

        // Andy: in PP tab, if user input override unit price, discount percent
        // will be recalculated by js, if user input discount percent directly
        // the override unit price will be cleared
        // considering the browsers without js enabled, following logic firstly check
        // the override unit price, if "", set db column NULL, otherwise, recalculate
        // discount percent again

        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);
        String sOverrideUnitPrice = ct.getPartPrice(key);
        String sDiscountPercent = ct.getPartDiscount(key);
        String sOverrideType = ct.getOverrideType(key) == null ? PartPriceConstants.SaaSOverrideType.DEFAULT : ct.getOverrideType(key);
        logContext.debug(this,"begin to validate override unit price");
        //logContext.debug(this, "Override Unit price is input = " + sOverrideUnitPrice);
        //logContext.debug(this, "Discount Percentage is input = " + sDiscountPercent);

        //for EOL parts, discount percent input box is always disabled
        String ytyRadio = ct.getYtyRadio(key);
        if(StringUtils.equals(DraftQuoteParamKeys.YTY_RADIO_OVERRIDE_PRICE_VALUE, ytyRadio)){
        	sDiscountPercent = "";
        } else if(StringUtils.equals(DraftQuoteParamKeys.YTY_RADIO_DISCOUNT_VALUE, ytyRadio)){
        	sOverrideUnitPrice = "";
        }
        
        if (null == sOverrideUnitPrice) {
            return;
        }
        // Note: if the two values are not null, UI validation can make sure they are numbers
        // if override type is unit, follow the old logic
        if(PartPriceConstants.SaaSOverrideType.UNIT.equals(sOverrideType)){
	        if ("".equals(sOverrideUnitPrice)) {
	            // user didn't input override unit price manually
	            // or there is no OUP or user cleared the OUP, if the previous value in db2 is not null
	            // it means there is a change
	            if (item.getOverrideUnitPrc() != null){
	                vr.unitPriceOrDiscountChanged = true;
	                //user clear the override unit price , so discount should also be cleared
	                //item.setLineDiscPct(0.0);
	            }
	            item.setOverrideUnitPrc(null);
	            double discountPercent = 0.0;
	            if (StringUtils.isNotBlank(sDiscountPercent)) {
	                //user input the discount percent
	                discountPercent = Double.parseDouble(sDiscountPercent);
	                //if SaaS part and can override unit price, SQO must calculate override unit price and round per currency's decimal places
	                if(discountPercent != 0.0 && item.isSaasPart()
	                	&& PartPriceSaaSPartConfigFactory.singleton().shldClcltOvrrdUnitPricPerDisc(item)){
	                	item.setOverrideUnitPrc(
	                			QuoteCommonUtil.getFormatDecimalPrice(quote, item, QuoteCommonUtil.calculatePriceByDiscount(discountPercent, item.getLocalUnitProratedPrc()))
	                			);
	                	vr.unitPriceOrDiscountChanged = true;
	                }

	            }

	            if (PartPriceUIValidator.checkLineItemDiscountChange((LineItemParameter)ct.getItems().get(key),String.valueOf(discountPercent))){
	                item.setLineDiscPct(discountPercent);
	                vr.unitPriceOrDiscountChanged = true;

	                //if the  quote has an offer and the line item has an override discount set,
	                //clear the override extended price and set offerIncldFlag to false
	                if (quote.getQuoteHeader().getOfferPrice() != null){
	                	item.setOvrrdExtPrice(null);
	                	item.setOfferIncldFlag(Boolean.FALSE);
	                } else {
	                	item.setOvrrdExtPrice(null);
	                	item.setOfferIncldFlag(null);
	                }
	            }


	        } else {
	            // added June 22, 2007 - parse Number
	            NumberFormat format = NumberFormat.getInstance();
	            double overrideUnitPriceValue = 0;
	            try {
	                overrideUnitPriceValue = format.parse(sOverrideUnitPrice).doubleValue();
	            } catch (ParseException e) {
	            	logContext.debug(this, e.getMessage());
	            }
	            Double dOverrideUnitPrice = QuoteCommonUtil.getFormatDecimalPrice(quote, item, new Double(overrideUnitPriceValue));

	            if (!dOverrideUnitPrice.equals(item.getOverrideUnitPrc())) {
	                vr.unitPriceOrDiscountChanged = true;
	                item.setOverrideUnitPrc(dOverrideUnitPrice);
	            }

	            // recalc the discount percent & persist to db2.
	            // This will cover users without js enabled browsers

	            if ((item.getLocalUnitProratedPrc()!=null) && DecimalUtil.isNotEqual(item.getLocalUnitProratedPrc().doubleValue(),0.0)) {

	                double discPercent = 1 - dOverrideUnitPrice.doubleValue() / item.getLocalUnitProratedPrc().doubleValue();

	                item.setLineDiscPct(discPercent*100);

	                logContext.debug(this, "discount calculated from override price = " + item.getLineDiscPct());

	            }


	            //if the  quote has an offer and the line item has an override extended price set,
	            //clear the override extended price and set offerIncldFlag to false
	            if (quote.getQuoteHeader().getOfferPrice() != null){
	            	item.setOvrrdExtPrice(null);
	            	item.setOfferIncldFlag(Boolean.FALSE);
	            } else {
                	item.setOvrrdExtPrice(null);
                	item.setOfferIncldFlag(null);
                }

	        }
        }
        //if override type is extended, follow the new logic
        else if(PartPriceConstants.SaaSOverrideType.EXTENDED.equals(sOverrideType)){
        	if ("".equals(sOverrideUnitPrice)) {
	            // user didn't input override extended price manually
	            // or there is no OEP or user cleared the OEP, if the previous value in db2 is not null
	            // it means there is a change
	            if (item.getOvrrdExtPrice() != null){
	                vr.unitPriceOrDiscountChanged = true;
	                //user clear the override unit price , so discount should also be cleared
	                //item.setLineDiscPct(0.0);
	            }
	            item.setOvrrdExtPrice(null);
	            double discountPercent = 0.0;
	            if (!"".equals(sDiscountPercent)) {
	                //user input the discount percent
	                discountPercent = Double.parseDouble(sDiscountPercent);

	            }

	            if (PartPriceUIValidator.checkLineItemDiscountChange((LineItemParameter)ct.getItems().get(key))){
	                item.setLineDiscPct(discountPercent);
	                vr.unitPriceOrDiscountChanged = true;

	                //if the  quote has an offer and the line item has an override discount set,
	                //clear the override extended price and set offerIncldFlag to false
	                if (quote.getQuoteHeader().getOfferPrice() != null){
	                	item.setOvrrdExtPrice(null);
	                	item.setOfferIncldFlag(Boolean.FALSE);
	                }else {
	                	item.setOvrrdExtPrice(null);
	                	item.setOfferIncldFlag(null);
	                }
	            }
	        } else {
	            NumberFormat format = NumberFormat.getInstance();
	            double overrideExtPriceValue = 0;
	            try {
	            	overrideExtPriceValue = format.parse(sOverrideUnitPrice).doubleValue();
	            } catch (ParseException e) {
	            	logContext.error(this, e.getMessage());
	            }
	            Double dOverrideExtPrice = QuoteCommonUtil.getFormatDecimalPrice(quote, item, new Double(overrideExtPriceValue));

	            if (!dOverrideExtPrice.equals(item.getLocalExtProratedDiscPrc())) {
	                vr.unitPriceOrDiscountChanged = true;
	                item.setLocalExtProratedDiscPrc(dOverrideExtPrice);
	                item.setOvrrdExtPrice(dOverrideExtPrice);

		            if (quote.getQuoteHeader().getOfferPrice() != null){
		            	item.setOfferIncldFlag(Boolean.FALSE);
		            }
	            }

	            // recalc the discount percent & persist to db2.
	            // This will cover users without js enabled browsers

	            if ((item.getLocalExtProratedPrc()!=null) && DecimalUtil.isNotEqual(item.getLocalExtProratedPrc().doubleValue(),0.0)) {

	                double discPercent = 1 - dOverrideExtPrice.doubleValue() / item.getLocalExtProratedPrc().doubleValue();

	                item.setLineDiscPct(discPercent*100);

	                logContext.debug(this, "discount calculated from override price = " + item.getLineDiscPct());

	            }
	        }
        }


    }


    public boolean needRecalculateQuote() {
        if (this.pymTermsDaysChanged) {
            return true;
        }
        if(this.validityDaysChanged) {
            return true;
        }
        // if GSA pricing flag updated,
        if (this.needUpdateGSA) {
            return true;
        }

        if (this.itemCountChanged){
            return true;
        }
        if (this.provsningDaysChanged){
        	return true;
        }
        if (this.coTermChanged){
        	return true;
        }
        if (this.estmtdOrdDateChanged){
        	return true;
        }
        if (this.serviceDateChanged){
        	return true;
        }
        Iterator iter = this.validationResults.values().iterator();
        while (iter.hasNext()) {
            ValidationResult vr = (ValidationResult) iter.next();
            if (vr.shouldRecalcuate()) {
                return true;
            }
        }
        return false;

    }

    public boolean needUpdateGSA() {
        return this.needUpdateGSA;
    }
    protected void setPrevDates (QuoteLineItem item) throws TopazException{
        Date prevStartDate = item.getMaintStartDate();
        Date prevEndDate = item.getMaintEndDate();

        item.setPrevsStartDate(prevStartDate);
        item.setPrevsEndDate(prevEndDate);
    }
    protected void validatePVUPartQtyStatus(QuoteLineItem item) throws TopazException{

        if(item.isPvuPart()){

            String key = createKey(item);
            boolean result = ct.getPVUQtyManuallyEntered(key);
            if(result){

                String ovrrdQtyCode = item.getPVUOverrideQtyIndCode() == null ? "" : item.getPVUOverrideQtyIndCode().trim();
                // the quanity has been set from pvu calculator
                if (PartPriceConstants.QTY_FROM_CACLTR.equals(ovrrdQtyCode)){
                    item.setPVUOverrideQtyIndCode(PartPriceConstants.QTY_CACLTR_OVERRIDDEN);
                }
                // the quanity is not set
                if (ovrrdQtyCode.equals("")){
                    item.setPVUOverrideQtyIndCode(PartPriceConstants.QTY_MANUAL_ENTERED);
                }

            }
        }

    }
    protected void deleteLineItemAndConfig(QuoteLineItem item)throws TopazException{
        item.setPartQty(new Integer(0));
        item.delete();
    }

    public int getUseGsaPricing() {
        return useGsaPricing;
    }
    protected void calcuateDate() throws TopazException{
        // before determine the special bid , we need calculate date , because some items
        // added by sp (U_QT_PART_ADD_YRS) have NULL date
        DateCalculator dc = DateCalculator.create(this.quote);
        dc.calculateDate();
        dc.setLineItemDates();

        if(logContext instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){
		        List lineItems = quote.getLineItemList();
		        for(int i=0;i<lineItems.size();i++){
		            QuoteLineItem item = (QuoteLineItem)lineItems.get(i);
		            logContext.debug(this,item.getPartNum() + "_" + item.getSeqNum() + ": " + DateUtil.formatDate(item.getMaintStartDate()) + "->" + DateUtil.formatDate(item.getMaintEndDate()));
		        }
			}
		}
    }

    public void validateBpOverrideDiscount(QuoteLineItem item) throws TopazException{
        String key = createKey(item);

        ValidationResult vr = getValidationResult(key);
        String bpOverDisc = ct.getBpOverrideDiscount(key);

        try {

            if((item.getChnlOvrrdDiscPct() == null) && StringUtils.isBlank(bpOverDisc)){
                vr.bpOvrrdDiscountChanged = false;
            }
            if((item.getChnlOvrrdDiscPct()==null) && StringUtils.isNotBlank(bpOverDisc)){
                vr.bpOvrrdDiscountChanged = true;
            } else if (item.getChnlOvrrdDiscPct()!=null && StringUtils.isBlank(bpOverDisc)){
                vr.bpOvrrdDiscountChanged = true;
            } else if (item.getChnlOvrrdDiscPct()!=null && StringUtils.isNotBlank(bpOverDisc)){
                if(DecimalUtil.isEqual(item.getChnlOvrrdDiscPct().doubleValue(),new Double(bpOverDisc.trim()).doubleValue())){
                    vr.bpOvrrdDiscountChanged = false;
                } else{
                    vr.bpOvrrdDiscountChanged = true;
                }
            }

            //user should never be able to change the BP discount manually if
            //should set BP discount automatically
            if(QuoteCommonUtil.shouldSetELAAutoChnlDisc(quote)){
                vr.bpOvrrdDiscountChanged = false;
            }

            if(StringUtils.isBlank( bpOverDisc)){
                item.setChnlOvrrdDiscPct(null);
            }else{
	            bpOverDisc = bpOverDisc.trim();
	            item.setChnlOvrrdDiscPct(new Double(bpOverDisc));

            }

        } catch (Exception e) {
            logContext.error(this,"Bp Override Discount Error! value = " + bpOverDisc);
        }
    }

    public void validateEOLPart() throws TopazException{

        List masterItems = quote.getMasterSoftwareLineItems();
        List subItems = null;

        if (masterItems == null || masterItems.size() == 0) {
            return;
        }

        for (Iterator masterIt = masterItems.iterator(); masterIt.hasNext();) {

            QuoteLineItem masterItem = (QuoteLineItem) masterIt.next();

            //if part is to be deleted then skip validation
            if(masterItem.getPartQty() != null && masterItem.getPartQty().intValue() == 0){
            	continue;
            }

            if(!masterItem.isObsoletePart()){
            	continue;
            }

            //user should delete ELO parts whose status is not Z8
            if(!masterItem.canPartBeReactivated()){
            	continue;
            }

            String key = createKey(masterItem);
            NumberFormat format = NumberFormat.getInstance();
            boolean isChannel = quote.getQuoteHeader().isChannelQuote();

            //if part has eol price, user can't input entitled price at all
            if (!masterItem.isHasEolPrice()) {
                String entitledUnitPrice = ct.getEntitledUnitPrice(key);
                Double entitledPrice = null;
                Double previousUnitPrice = null;
                try {
                	if (StringUtils.isNotBlank(entitledUnitPrice)){
                		entitledPrice = new Double(format.parse(entitledUnitPrice).doubleValue());
                	}
                    if(masterItem.getLocalUnitProratedPrc() != null){

                        previousUnitPrice =  new Double(format.parse(masterItem.getLocalUnitProratedPrc().toString()).doubleValue());
                    }
                } catch (ParseException e) {
                    throw new TopazException(e);
                }

                logContext.debug(this, "entitled price for part " + key + " is " + entitledUnitPrice);

                //compare manually unit price with db2 unit price
                Double finalEntitledUnitPrice = entitledPrice;
                ValidationResult vr = getValidationResult(key);
                if (entitledPrice != null && (!entitledPrice.equals(previousUnitPrice) || vr.dateChanged)) {
                    if (!entitledPrice.equals(previousUnitPrice)) {
                        masterItem.setLocalUnitPrc(entitledPrice);
                    }
                    finalEntitledUnitPrice = CommonServiceUtil.getProrateEolPrice(quote,masterItem);
                }
                CommonServiceUtil.setEntitledPriceForEOLPart(masterItem,masterItem.getLocalUnitPrc(), finalEntitledUnitPrice, 1);
            }
            if(quote.getQuoteHeader().getCmprssCvrageFlag() && masterItem.isEligibleForCmprssCvrage()){
                try{
                    int iComrssCvrageMonth = 0;
                    double dCmprssCvrageDisc = 0.0;
                    if(StringUtils.isNotBlank(ct.getCmprssCvrageMonth(key))){
                        iComrssCvrageMonth = Integer.parseInt(ct.getCmprssCvrageMonth(key));
                    }
                    if(StringUtils.isNotBlank(ct.getCmprssCvrageDisc(key))){
                        dCmprssCvrageDisc = Double.parseDouble(ct.getCmprssCvrageDisc(key));
                    }
                    Double dNewEntldUnitPrice = CommonServiceUtil.getCmprssCvrageEntitlUnitPrcEol(quote, masterItem, iComrssCvrageMonth);
                    PartPriceCommon partPrice = new PartPriceCommon(quote);
                    Double dNewOvrridPrice = new Double(format.parse(partPrice.getFormattedPrice(dNewEntldUnitPrice.doubleValue() * (1 - dCmprssCvrageDisc / 100))).doubleValue());
                    CommonServiceUtil.setCmprssCvrageItemEol(quote, masterItem, dNewEntldUnitPrice, dNewOvrridPrice, dCmprssCvrageDisc);
                }catch(ParseException e) {
                    throw new TopazException(e);
                }
            }
        }
    }

    /*
     * public void validateLineItemOverallDiscount(QuoteLineItem item) throws
     * TopazException{ Double entitledExtPriceValue =
     * item.getLocalExtProratedPrc(); Double bpChannelExtPriceValue =
     * item.getChannelExtndPrice();
     *
     * if (entitledExtPriceValue == null || bpChannelExtPriceValue == null){
     * return; } if (entitledExtPriceValue.doubleValue() == 0){ return; } double
     * overallDiscPercent = 1 - bpChannelExtPriceValue.doubleValue()
     * /entitledExtPriceValue.doubleValue() ;
     *
     * item.setTotDiscPct(new Double(overallDiscPercent*100)); }
     */

    protected boolean isPartParamExist(String key){
    	return ct.getItems().containsKey(key);
    }
    /**
     * refer to rtc #207982
     * @param item
     * @throws TopazException
     */
    public void validateSaasRenwl(QuoteLineItem item) throws TopazException{
    	
    	if(item.isSaasSubscrptnPart()||(item.isMonthlySoftwarePart()&&((MonthlySwLineItem) item).isMonthlySwSubscrptnPart())){
	        String key = createKey(item);
	        boolean saasRenwl = ct.getSaasRenwl(key);
	        item.setSaasRenwl(saasRenwl);
    	}
    }

    @SuppressWarnings("rawtypes")
    public void validateSaasMgrtn(QuoteLineItem item) throws TopazException{
	     if(item.isSaasPart()||item.isMonthlySoftwarePart()){
    		String key = createKey(item);
	        boolean saasMgrtn = ct.getSaasMgrtn(key);
	        // if PGS set webMigrtdDocFlag from contract directly, if SQO, keep webMigrtdDocFalg value from validateRenwlMgrtn, set ramp up item to same value.
	        if(ct.isSQOEnv()){
	        	item.setWebMigrtdDocFlag(saasMgrtn);
	        }
            if (!item.isRampupPart() && item.getRampUpLineItems() != null && item.getRampUpLineItems().size() != 0) {
                List rampUpItems = item.getRampUpLineItems();
                for (Iterator iterator = rampUpItems.iterator(); iterator.hasNext();) {
                    QuoteLineItem rampUpItem = (QuoteLineItem) iterator.next();
                    rampUpItem.setWebMigrtdDocFlag(item.isWebMigrtdDoc());
                }
            }
	     }
    }

    public void validateRenwlMgrtn(QuoteLineItem item, Boolean isRenewalFlag,Boolean isMigrationFlag) throws TopazException{
    	
    	//1. get isNewService from form and set to bean
    	String key = createKey(item);
    	Boolean isNewService=ct.isLineItemNewService(key);
    	item.setIsNewService(isNewService);
    	//2. compute saasRenewl and migration flag according isNewSerivie
    	boolean saasRenwl=false,saasMigration=false;
    	if(isNewService!=null && !isNewService) {
    		if (isRenewalFlag!=null && isRenewalFlag) {
    			saasRenwl=true;    			
    		} 
    		if (isMigrationFlag!=null && isMigrationFlag) {
    			saasMigration=true;
    		} 
    	}
    			
        //3.set saasRenewl and migration flag for item
        item.setSaasRenwl(saasRenwl);
        item.setWebMigrtdDocFlag(saasMigration);

        if (!item.isRampupPart() && item.getRampUpLineItems() != null && item.getRampUpLineItems().size() != 0) {
            List rampUpItems = item.getRampUpLineItems();
            for (Iterator iterator = rampUpItems.iterator(); iterator.hasNext();) {
                QuoteLineItem rampUpItem = (QuoteLineItem) iterator.next();
                rampUpItem.setSaasRenwl(saasRenwl);
            }
        }
    }
    
    

    
	/**
	 * 1.if machineType is not null ,machineType should be Alphanumeric, length
	 * should be not more than 4 character 2.if machineModel is not null,
	 * machineModel should be Alphanumeric,length should be not more than 3
	 * character 3.if serialNumber is not null,serialNumber should be
	 * Alphanumeric, length should be not more than 7 character
	 * 4. if applncPocInd value is "N" , machineType , machineModel , serialNumber should be null
	 *
	 * @param item
	 * @throws TopazException
	 */
	protected void validateAppliancePart(QuoteLineItem item) throws TopazException {

		
		String key = createKey(item);
		String applianceId = ct.getApplianceId(key);
		
		//Appliance Main part or Appliance Upgrade part or Appliance ownership transfer part skip the validation for configID
		if(!(item.isApplncMain() || item.isApplncUpgrade() || item.isOwerTransferPart())){
			if (StringUtils.isEmpty(applianceId) ||PartPriceConstants.APPLNC_SELECT_DEFAULT.equals(applianceId)) 
					applianceId = null;
			
			item.setConfigrtnId(applianceId);
		}
		
		String applncPocInd = StringUtils.isEmpty(ct.getApplncPocInd(key))?"N":ct.getApplncPocInd(key);
		item.setApplncPocInd(((LineItemParameter)ct.getItems().get(key)).applncPocInd);
		item.setApplncPriorPoc(StringUtils.isEmpty(ct.getApplncPriorPoc(key))?"N":ct.getApplncPriorPoc(key));

		item.setMachineType(ct.getMachineType(key));
		item.setModel(ct.getMachineModel(key));
		item.setSerialNumber(ct.getMachineSerialNumber(key));
		//appliance deployment model
		item.setDeployModelOption(ct.getDeployModelOption(key));
		item.setDeployModelId(ct.getDeployModelId(key));
		item.setDeployModelInvalid(ct.isDeployModelValid(key));
		//Appliance#145
		item.setNonIBMModel(ct.getNonIBMModel(key));
		item.setNonIBMSerialNumber(ct.getNonIBMSerialNum(key));
		
		//Appliance#99
		Date oldHeaderCRAD = ct.getHeaderCRAD(key); 
		java.util.Date updatedHeaderCRAD = ct.getCustReqstdArrivlDate();
		Date newHeaderCRAD; 
		if (updatedHeaderCRAD==null){
			newHeaderCRAD=null;
		} else {
			newHeaderCRAD = new Date(updatedHeaderCRAD.getTime()); 
		}
		Date oldLineItemCRAD = ct.getOrgLineItemCRAD(key);  
		Date newLineItemCRAD = ct.getLineItemCRAD(key);   
		boolean appSendMFGFlg = ct.getAppSendMFGFlg(key);
		
		if (appSendMFGFlg) {
		
		// if header CRAD had been updated, the lineItem CRAD which has same value also need to be updated with new header CRAD value.
		// if line item CRAD itself has been updated, then need to update it to itself value.
		
		// header CRAD has been updated
		if (newHeaderCRAD!=null && !newHeaderCRAD.equals(oldHeaderCRAD)) {
			// old header CRAD = null means line item CRAD need to be updated to current header CRAD, 
			if (oldHeaderCRAD==null){
				
				// the line item crad has been changed
				if ((newLineItemCRAD!=null && !newLineItemCRAD.equals(oldLineItemCRAD) )||
						(newLineItemCRAD==null && oldLineItemCRAD!=null)){
					item.setLineItemCRAD(newLineItemCRAD);
				} else {
					item.setLineItemCRAD(newHeaderCRAD);
				}
				
			} else {
				// old header CRAD = old line item CRAD, 
				if (oldHeaderCRAD.equals(oldLineItemCRAD)){
					// line item crad not changed, so this line item CRAD need to be updated to new header CRAD
					if (newLineItemCRAD!=null && newLineItemCRAD.equals(oldHeaderCRAD)){
						item.setLineItemCRAD(newHeaderCRAD);
					// update line item CRAD to new value
					} else {
						item.setLineItemCRAD(newLineItemCRAD);
					}
				// old header CRAD != old line item CRAD 	
				} else {
					item.setLineItemCRAD(newLineItemCRAD);
				}
			}
		} else {
			item.setLineItemCRAD(newLineItemCRAD);
		}
		
		}
		
		
		
		if(item.isApplncMain())
			validateApplianceMainPart(applncPocInd,item);
		
		else if(!item.isApplncUpgrade() && !item.isOwerTransferPart()){
			valdateApplianceAssociatedPart(applianceId,item);
		}
		
		validateApplianceMtm(item);
	}


	protected void validateApplianceMtm(QuoteLineItem item) throws TopazException{

		if (item.getMachineType() != null
				&&
				(StringUtils.isEmpty(item.getMachineType())
						|| !StringHelper.isAlphanumeric(item.getMachineType())
						|| item.getMachineType().length() > 4)) {
			item.setMachineType(null);
		}
		
		int validateModelLength = 3;
		int validateSerialNumberLength = 7;
		
		if (item.isDisplayModelAndSerialNum()) {
			validateModelLength = 10;
			validateSerialNumberLength = 30;
			
			if (item.getNonIBMModel() != null
					&&
					(StringUtils.isEmpty(item.getNonIBMModel())
							|| item.getNonIBMModel().length() > validateModelLength)) {
				item.setNonIBMModel(null);
			}

			if (item.getNonIBMSerialNumber() != null
					&&
					(StringUtils.isEmpty(item.getNonIBMSerialNumber())
					|| item.getSerialNumber().length() > validateSerialNumberLength)) {
				item.setNonIBMSerialNumber(null);
			}
		}
		
		if (item.getModel() != null
				&&
				(StringUtils.isEmpty(item.getModel())
						|| !StringHelper.isAlphanumeric(item.getModel())
						|| item.getModel().length() > validateModelLength)) {
			item.setModel(null);
		}

		if (item.getSerialNumber() != null
				&&
				(StringUtils.isEmpty(item.getSerialNumber())
				|| item.getSerialNumber().length() > validateSerialNumberLength)) {
			item.setSerialNumber(null);
		}
	}



	private void validateApplianceMainPart(String applncPocInd,QuoteLineItem item)throws TopazException {
		/**
		 * condition
		 * 1. parts is main part and applncPocInd is "N"
		 */
		if ("N".equalsIgnoreCase(applncPocInd)){
			clearMtmValue(item);
		}

	}
	
	/**
	 * If quote have appliance main part and appliance not equals PartPriceConstants.APPLNC_NOT_ON_QUOTE, clear mtm 
	 * @param applianceId
	 * @param item
	 * @throws TopazException
	 */
	private void valdateApplianceAssociatedPart(String applianceId,QuoteLineItem item)throws TopazException {
		if (item.isReferenceToRenewalQuote()) {
			return;
		}
		PartPriceAppliancePartConfigFactory applncPartFactory = PartPriceAppliancePartConfigFactory.singleton();
		if(applncPartFactory.displayApplianceIdDropdown(item)
				&& (quote.applncMains != null && quote.applncMains.size()>0 || quote.applncUpgradeParts != null && quote.applncUpgradeParts.size()>0
				|| quote.getApplncOwnerShipParts() != null && quote.getApplncOwnerShipParts().size()>0 )){
			//Have appliance main part
			if(quote.applncMains != null && quote.applncMains.size() > 0){
				if(PartPriceConstants.APPLNC_NOT_ON_QUOTE.equals(applianceId)){

				}
				else{
					clearMtmValue(item);
				}
			}
		}
	}

	private void clearMtmValue(QuoteLineItem item) throws TopazException{
		item.setMachineType(null);
		item.setModel(null);
		item.setSerialNumber(null);
	}


}
