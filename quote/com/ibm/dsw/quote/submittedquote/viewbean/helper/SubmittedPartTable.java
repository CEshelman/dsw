package com.ibm.dsw.quote.submittedquote.viewbean.helper;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.PartGroup;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>SubmittedPartTable</code>
 *
 *
 * @author: xiuliw@cn.ibm.com
 *
 * Creation date: 2007-5-11
 */
public class SubmittedPartTable extends PartPriceCommon {
    private Locale locale;

    public SubmittedPartTable(Quote quote) {
        super(quote);
    }

    public SubmittedPartTable(Quote quote,Locale locale) {
        super(quote, null, locale);
        this.locale = locale;
    }

    /**
     * @return true if any parts have been selected
     */
    public boolean showSubmittedPartTableTotalsSection() {
        if (isPA() || isPAE() || isOEM()) {
            return hasSubmittedLineItems();
        }
        return false;
    }

    /**
     * return true if the quote contains any parts
     * @return
     */
    public boolean showSubmittedPartTableSection() {
        if (quote != null && quote.getLineItemList() != null && quote.getLineItemList().size() != 0) {
            return true;
        }
        return false;
    }

    /**
     * @param qli
     * @return
     */
    private boolean isSubmittedContractPart(QuoteLineItem qli) {

        if(PartPriceConstants.PartTypeCode.PACTRCT.equals(qli.getPartTypeCode()))
        {
            return true;
        }

        return false;
    }

    /**
     * Display if need to determine part date
     * Show value in red if the rep overrode the default start date
     * @return
     */
    public boolean showSubmittedStartDateText(QuoteLineItem qli) {
    	// For Ownership transfered parts
    	if (qli.isOwerTransferPart()) {
			return false;
		}
    	
    	if(qli.isApplncPart()){
    		return QuoteCommonUtil.isShowDatesForApplnc(qli);
    	}else{
    		return PartPriceConfigFactory.singleton().needDetermineDate(qli.getRevnStrmCode());
    	}
    }

    public String getSubmittedStartDateText(QuoteLineItem qli) {
        if (qli.getMaintStartDate() == null) return "";

        return DateUtil.formatDate(qli.getMaintStartDate(),DateUtil.PATTERN1,locale);
    }

    /**
     * Display if part is a contract part
     * Show value in red if the rep overrode the default end date
     * @param qli
     * @return
     */
    public boolean showSubmittedEndDateText(QuoteLineItem qli) {
    	return showSubmittedStartDateText(qli);
    }

    public String getSubmittedEndDateText(QuoteLineItem qli) {
        if (qli.getMaintEndDate() == null) return "";

        return DateUtil.formatDate(qli.getMaintEndDate(),DateUtil.PATTERN1,locale);
    }

    /**
     * get group name
     * @param qli
     * @return
     */
    public String getSubmittedGroupName(QuoteLineItem qli) {
        List groups = qli.getPartGroups();

        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<groups.size();i++){
            PartGroup group = (PartGroup)groups.get(i);
            if(i >0 ){
                buffer.append(",<br/>");
            }
            buffer.append(group.getGroupName());
        }

        return buffer.toString();
    }

    /**
     * If the part is a contract part and
     * The prorate months (duration) is less than 12 months (see validation checks, above)
     * @return
     */
    public boolean showSubmittedProrateMonth(QuoteLineItem qli) {
        if(qli.getPartDispAttr().isFtlPart()){
            return false;
        }

        if(qli.getProrateMonths() <= -1){
            return false;
        }

        if (isPA() || isPAE() || isOEM()) {
            return isSubmittedContractPart(qli) && qli.getProrateMonths() < 12;
        }
        if (isFCT()){
            if ((isLicensePart(qli)||isMaintPart(qli)||isSupportPart(qli)) && (qli.getProrateMonths() < 12)){
                return true;
            }
        }
        return false;
    }

    public boolean showSubmittedProrateWeek(QuoteLineItem qli){
        return (qli.getProrateWeeks() > 0
                && qli.getProrateWeeks() < 52
                && qli.getPartDispAttr().isFtlPart());
    }

    /**
     * if part is a contract part
     * @return
     */
    public boolean showSubmittedItemPoint(QuoteLineItem qli) {
        return (isPA()|| isPAE()) && isSubmittedContractPart(qli);
    }

    /**
     * if part is a contract part
     * @param qli
     * @return
     */
    public boolean showSubmittedTotalPoints(QuoteLineItem qli) {
        return (isPA() || isPAE()) && isSubmittedContractPart(qli);
    }

    /**
     * display value if part is a contract part
     * @param qli
     * @return
     */
    public boolean showSubmittedDiscountPercent(QuoteLineItem qli) {
        // Added by Andy , to fix PL MTIO-75389N: for FCT quote, always display discount
        if(this.isFCT()){
            return true;
        }
        return isSubmittedContractPart(qli);
    }

    /**
     * @return
     */
    public String getSubmittedOriginalLabel(QuoteLineItem qli) {
        /*
        Label varies by LOB and whether the part was on the original renewal quote or added manually:
        	If part was on the original renewal quote:
            	PA:  Original quote dates/quantity and unit points/prices:
            	PAE:  Original quote dates/quantity and unit prices:
            If part was added manually (not on the original renewal quote):
            	PA:  Unit points/prices:
            	PAE:  Unit prices:
        */
        if (qli.getSeqNum() < PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ) {
            if (isPA() || isPAE()) {
                return SubmittedQuoteViewKeys.ORIGINAL_QUOTE_PA;
            }
            else if (isFCT()) {
                return SubmittedQuoteViewKeys.ORIGINAL_QUOTE_FCT;
            }else if (isOEM()){
                return SubmittedQuoteViewKeys.ORIGINAL_QUOTE_OEM;
            }
        }
        else if (qli.getSeqNum() >= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ) {
            if (isPA() || isPAE()) {
                return SubmittedQuoteViewKeys.UNIT_POINTS_PA;
            }
            else if (isFCT()) {
                return SubmittedQuoteViewKeys.UNIT_PRICES_FCT;
            }else if(isOEM()){
                return SubmittedQuoteViewKeys.UNIT_PRICES_OEM;
            }
        }

        return "";
    }

    public String getLineItemEntitledExtendedPrc(QuoteLineItem lineItem) {
        if (lineItem.getLocalExtProratedPrc()!=null) {
            return getFormattedPriceByPartType(lineItem, lineItem.getLocalExtProratedPrc());
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }

    public String getLineItemBidExtendedPrc(QuoteLineItem lineItem) {
        if (lineItem.getLocalExtProratedDiscPrc() != null) {
            return getFormattedPriceByPartType(lineItem, lineItem.getLocalExtProratedDiscPrc());
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }

    public boolean showItemNumber(){
        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++){
            QuoteLineItem lineItem = (QuoteLineItem)lineItems.get(i);
            if (lineItem.getQuoteSectnSeqNum() <= 0){
                return false;
            }
        }
        return true;
    }

    public String getLineItemBpExtendedPrc(QuoteLineItem lineItem) {
        if (lineItem.getChannelExtndPrice() != null) {
            return getFormattedPriceByPartType(lineItem, lineItem.getChannelExtndPrice());
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }

    /**
     * Display if need to display order status.
     * @return
     */
    public boolean showOrderStatus(QuoteLineItem qli) {
    	return StringUtils.isNotBlank(qli.getOrderStatusCode());
    }

    public boolean showSapDocUserStat(QuoteLineItem qli){
    	return StringUtils.isNotBlank(qli.getSapDocUserStat());
    }

    public String getSapDocUserStat(QuoteLineItem qli){
    	return qli.getSapDocUserStat();
    }

    public boolean showOrderStatusRow(QuoteLineItem qli) {
    	return (showOrderStatus(qli) || showSapDocUserStat(qli)) && !hideStatusRowForApplnc(qli);
    }

    /**
     * If appliance qty > 1 don't show order status row
     * @param qli
     * @return
     */
    private boolean hideStatusRowForApplnc(QuoteLineItem qli){
    	if(QuoteCommonUtil.isApplncQtyGtOne(qli) && qli.isOrdered()){
    		return true;
    	}

    	return false;
    }

    /**
     * if appliance qty > 1 show appliance order detail link
     * @param qli
     * @return
     */
    public boolean showApplianceOrderDetail(QuoteLineItem qli){
    	return hideStatusRowForApplnc(qli);
    }

    public boolean showPartGroups(QuoteLineItem qli){
        return (qli.getPartGroups() != null && qli.getPartGroups().size() > 0);
    }
    public boolean hasFTLParts(){
        List lineItems = quote.getLineItemList();

        if(lineItems == null || lineItems.size() == 0){
            return false;
        }

        for(Iterator it = lineItems.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();

            if(qli.getPartDispAttr().isFtlPart()){
                return true;
            }
        }

        return false;
    }

    public boolean showCmprssCvrageRow(QuoteLineItem qli){
        return (quote.getQuoteHeader().getCmprssCvrageFlag() && qli.hasValidCmprssCvrageMonth() );
    }

    public boolean showCmprssCvrageDiscPctNote(QuoteLineItem qli){
        return qli.hasValidCmprssCvrageDiscPct() ;
    }

    private boolean isNotApproved(){
    	return this.quote.getQuoteHeader().containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR) ||
    	this.quote.getQuoteHeader().containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO) ||
    	this.quote.getQuoteHeader().containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG) ;

    }

    public boolean showLineItemPriorSSEntitledUnitPriceHeader(){
    	return this.isNotApproved() && super.showLineItemPriorSSEntitledUnitPriceHeader();
    }

//    public boolean showLineItemPriorSSEntitledUnitPrice(QuoteLineItem lineItem){
//    	return this.isNotApproved() &&super.showLineItemPriorSSEntitledUnitPrice(lineItem);
//    }

    public String getPriorSSLable(QuoteLineItem lineItem) {
		ApplicationContext context = ApplicationContextFactory.singleton()
				.getApplicationContext();
		if(locale == null){
			locale = Locale.US;
		}
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)) {
			if (lineItem.getPriorYearSSPrice() != null && lineItem.getPriorYearSSPrice().isEvolved()) {
				return context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale, SubmittedQuoteMessageKeys.AVAILABLE_PRIOR_PRICE_FROM_EVOLVED_PART);
			} else if (lineItem.getPriorYearSSPrice().isChannel()) {
				return context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale, SubmittedQuoteMessageKeys.PRICE_FROM_A_CHANNEL_ORDER);
			}
		}
		return DraftQuoteConstants.BLANK;
	}

    public String getPriorSSEntitledLable (QuoteLineItem lineItem){
    	ApplicationContext context = ApplicationContextFactory.singleton()
		.getApplicationContext();
		if(locale == null){
			locale = Locale.US;
		}
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)) {
			if (lineItem.getPriorYearSSPrice() != null && lineItem.getPriorYearSSPrice().isEvolved()) {
				return context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale, SubmittedQuoteMessageKeys.AVAILABLE_PRIOR_PRICE_FROM_EVOLVED_PART);
			}
		}
		return DraftQuoteConstants.BLANK;

    }


    public String getPriorSSChannelLable (QuoteLineItem lineItem){
    	ApplicationContext context = ApplicationContextFactory.singleton()
		.getApplicationContext();
		if(locale == null){
			locale = Locale.US;
		}
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)) {
			if (lineItem.getPriorYearSSPrice() != null && lineItem.getPriorYearSSPrice().isChannel()) {
				return context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale, SubmittedQuoteMessageKeys.PRICE_FROM_A_CHANNEL_ORDER);
			}
		}
		return DraftQuoteConstants.BLANK;

    }

//    public String getLineItemPriorSSEntitledUnitPrice(QuoteLineItem lineItem){
//    	ApplicationContext context = ApplicationContextFactory.singleton()
//		.getApplicationContext();
//
//    	if (isNotApproved()){
//    		String unitPrice = super.getLineItemPriorSSEntitledUnitPrice(lineItem);
//    		if("N/A".equals(unitPrice)){
//    			return context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale, SubmittedQuoteMessageKeys.PRIOR_PRICE_COULD_NOT_BE_CALCULATED);
//    		}else {
//    			return unitPrice;
//    		}
//    	}else{
//    		return DraftQuoteConstants.BLANK;
//    	}
//    }

    public String getLineItemBpRate(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
        if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe)) {
        	if(CommonServiceUtil.checkIsUsagePart(lineItem)){
        		if(lineItem.getChannelUnitPrice() != null){
        			return getFormattedPriceByPartType(lineItem, lineItem.getChannelUnitPrice());
        		}else {
                    return DraftQuoteConstants.BLANK;
                }
        	}else{
        		if(lineItem.isSaasSubscrptnPart() && lineItem.getChannelExtndPrice() != null){
        			return getFormattedPriceByPartType(lineItem, lineItem.getChannelExtndPrice());
        		}else {
                    return DraftQuoteConstants.BLANK;
                }
        	}
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }

    public String getLineItemChnnlUnitPrice(QuoteLineItem lineItem) {
        if(lineItem.getChannelUnitPrice() != null){
        	return getFormattedPriceByPartType(lineItem, lineItem.getChannelUnitPrice());
        }else {
            return DraftQuoteConstants.BLANK;
        }
    }

	public boolean showCoTerm(PartsPricingConfiguration configrtn){
		//Don't show coterm if is new service agreement
		if(StringUtils.isBlank(quote.getQuoteHeader().getRefDocNum())){
			return false;
		}
		if(!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configrtn.getConfigrtnActionCode())){
        	List lineItems = (List)quote.getPartsPricingConfigrtnsMap().get(configrtn);
        	if(lineItems != null && lineItems.size() > 0){
        		//Don't show coterm options if configuration cotains ramp up parts
        		for(Iterator it = lineItems.iterator(); it.hasNext(); ){
        			QuoteLineItem qli = (QuoteLineItem)it.next();
        			if(qli.getRampUpLineItems() != null && qli.getRampUpLineItems().size() > 0){
        				return false;
        			}
        		}
        	}

			return true;
        }
		return false;
	}

	public String getPYPPriceBySpecialBid (QuoteLineItem lineItem , boolean isSpecialBid){
		String priorSSPrice = getLineItemPriorSSEntitledUnitPrice(lineItem);
		String bidUnitPrice = getFormattedPriceByPartType(lineItem, GrowthDelegationUtil.getAnnualYtyBidUnitPrice(lineItem));
		
		if (!priorSSPrice.trim().equals(bidUnitPrice.trim())&& isSpecialBid && StringUtils.isNotBlank(lineItem.getRenewalQuoteNum())){
			return "<span class='ibm-price'>"+priorSSPrice+"</span>";
		}
		return priorSSPrice;
	}

	public String getBidUnitPriceBySpecialBid(QuoteLineItem lineItem , boolean isSpecialBid){
		String priorSSPrice = getLineItemPriorSSEntitledUnitPrice(lineItem);
		String bidUnitPrice = getFormattedPriceByPartType(lineItem, lineItem.getLocalUnitProratedDiscPrc());
		if (!priorSSPrice.trim().equals(bidUnitPrice.trim())&& isSpecialBid && StringUtils.isNotBlank(lineItem.getRenewalQuoteNum())){
			return "<span class='ibm-price'>"+bidUnitPrice+"</span>";
		}
		return "<span class='gray-font'>"+bidUnitPrice+"</span>";
	}

	public boolean isPGSQuote(){
		return quote.getQuoteHeader().isPGSQuote();
	}

    public boolean applianceAssoRowIsNotBlank(QuoteLineItem lineItem, boolean isDraftQuote, boolean isSalesQuote) {
        if (showApplianceId(lineItem, isSalesQuote)) {
            return true;
        } else if (showApplianceInformation(lineItem, isDraftQuote, isSalesQuote)) {
            return true;
        } else if (showApplianceMtm(lineItem, isDraftQuote, isSalesQuote)) {
            return true;
        } else if (showApplianceOrderDetail(lineItem)) {
            return true;
        }
        return false;
    }
    
    public boolean isDisplayModelAndSerialNum(QuoteLineItem lineItem){
     	return lineItem.isDisplayModelAndSerialNum();
    }
    
    public boolean isMandatorySerialNum(QuoteLineItem lineItem) {
     	return lineItem.isMandatorySerialNum();
    }

}
