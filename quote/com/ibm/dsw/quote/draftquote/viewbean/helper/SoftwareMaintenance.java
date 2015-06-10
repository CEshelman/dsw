package com.ibm.dsw.quote.draftquote.viewbean.helper;

import is.domainx.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>SoftwareMaintenanceViewBean</code>
 *
 *
 * @author: xiuliw@cn.ibm.com
 *
 * Creation date: 2007-4-3
 */
public class SoftwareMaintenance extends PartPriceCommon {
    public SoftwareMaintenance(Quote quote) {
        super(quote);
    }

    public SoftwareMaintenance(Quote quote, User user) {
        super(quote, user);
    }

    /**
     * Added by lee,March 30,2007
     *
     * @return
     */
    public boolean showSoftwareMaintenanceCoverage(QuoteLineItem lineItem) {
    	// For Ownership transfered parts
    	if (lineItem.isOwerTransferPart()) {
			return false;
		}
    	return PartPriceConfigFactory.singleton().needDetermineDate(lineItem.getRevnStrmCode());
    }

    /**
     * @param lineItem
     * @return
     */
    public String getEndDateHiddenPro(QuoteLineItem lineItem) {
        return DateUtil.formatDate(lineItem.getPartDispAttr().getProEndDate());
    }

    /**
     * @param lineItem
     * @return
     */
    public String getNonEndDateHiddenPro(QuoteLineItem lineItem) {
        return DateUtil.formatDate(lineItem.getPartDispAttr().getNonProEndDate());
    }

    public String getStdStartDate(QuoteLineItem lineItem) {
        return DateUtil.formatDate(lineItem.getPartDispAttr().getStdStartDate());
    }

    public String getStdEndDate(QuoteLineItem lineItem) {
        return DateUtil.formatDate(lineItem.getPartDispAttr().getStdEndDate());
    }

    /**
     * Display if the editable version of this screen displays
     * Only display if the user's preference is to show line item details
     * @return
     */
    public boolean showSubmittedOverrideMaintenanceCoverage(QuoteLineItem lineItem,boolean userPreference) {
        return PartPriceConfigFactory.singleton().isDateEditAllowed(quote.getQuoteHeader(), lineItem)
        	&& this.isSubmittedEditable()
        	&& userPreference&&!showApplianceId(lineItem,quote.getQuoteHeader().isSalesQuote());
    }

    /**
     *
     * @param lineItem
     * @return
     */
    public boolean prorateFirstYearToAnniversaryCheckbox(QuoteLineItem lineItem) {

        //Line of business is PA
        //Existing customer with existing contract was selected
        //Part is a maintenance or subsequent FTL part, but was not added from a renewal quote
        //Pushpa to confirm : Add two new additional condition: isLicensePart exist and qty match ,
    	if(isPA()){
	        PartDisplayAttr attr = lineItem.getPartDispAttr();
	        // changes for ebiz ticket SDIN-77PLRC Assoc.Mtc date logic error
	        if(prorateToAnniversaryEnabled() && attr.isMaintBehavior() && !attr.isFromRQ()){
	            if(attr.isLicensePartExist()){
	                // if license part exist, the qty must match
	                return attr.isQtyMatch();
	            }
	            else{
	                // if license part don't exist , it can be prorate
	                return true;
	            }
	        }
        }
        return false;
    }

    /**
     * check if proration to anniversary supported
     * @return
     */

    public boolean prorateToAnniversaryEnabled(){

        // not PA and OEM quote
        if (!quote.getQuoteHeader().isPAQuote() && !quote.getQuoteHeader().isOEMQuote()) {
            return false;
        }

        Customer customer = quote.getCustomer();

        // customer dosent's exist
        if (null == customer) {
            return false;
        }

        // no contract exist
        List contractList = customer.getContractList();
        if ((null == contractList) || (0 == contractList.size())) {
            return false;
        }
        // Customer is new
        if (StringUtils.isBlank(quote.getQuoteHeader().getSoldToCustNum())) {
            return false;
        }
        return true;
    }

    /**
     * Added by lee,March 30,2007
     *
     * @param lineItem
     * @return true if additional years maintenance dropdown list to be shown
     */
    public boolean showAddYearsMaintenanceDropDown(QuoteLineItem lineItem) {

        // Part is a maintenance or subsequent FTL part, but was not added from a renewal quote
        PartDisplayAttr attr = lineItem.getPartDispAttr();
        return attr.isMaintBehavior() && !attr.isFromRQ();

    }

    public String getAddYearsMaintenanceSeq(QuoteLineItem lineItem) {
        String allAddiSeqNum = "";

		if (showAddYearsMaintenanceDropDown(lineItem) &&
		        lineItem.getAddtnlYearCvrageLineItems() != null &&
		        	lineItem.getAddtnlYearCvrageLineItems().size() != 0) {
		   	   Iterator licIt = lineItem.getAddtnlYearCvrageLineItems().iterator();

			   while (licIt.hasNext()) {
			       QuoteLineItem addiCovQli = (QuoteLineItem)licIt.next();
			       String addiPrefix = addiCovQli.getPartNum() + "_" + addiCovQli.getSeqNum();
			       String addiSeqNum = addiPrefix + DraftQuoteParamKeys.addiSeqNumSuffix;
		           allAddiSeqNum = allAddiSeqNum + addiPrefix + ",";
			   }
		}

		return allAddiSeqNum;
    }

    /**
     * If part is a contract part
     *
     * @param lineItem
     * @return
     */
    public boolean showStartDateText(QuoteLineItem lineItem) {
        return this.showSoftwareMaintenanceCoverage(lineItem);
    }

    /**
     * If part is a contract part
     *
     * @param lineItem
     * @return
     */
    public boolean showEndDateText(QuoteLineItem lineItem) {
        return this.showStartDateText(lineItem);
    }

    /**
     * Part is from the original renewal quote (not added manually by the user)
     * @param lineItem
     * @return
     */
    public boolean showRQStartEndDateText(QuoteLineItem lineItem) {
        if (lineItem.getSeqNum() < PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ && this.showSoftwareMaintenanceCoverage(lineItem)) {
            return true;
        }
        return false;
    }

    /**
     * Part is from the original renewal quote (not added manually by the user)
     * Default value:  the line item start date from the quote
     * @param lineItem
     * @return
     */
    public boolean showRQOvrStartEndDateDrop(QuoteLineItem lineItem) {
        return this.showRQStartEndDateText(lineItem);
    }

    /**
     * If part was added manually
     * Default value:  the renewal quote's renewal end date.
     * @param lineItem
     * @return
     */
    public boolean showRQStartEndDateDrop(QuoteLineItem lineItem) {
        if (lineItem.getSeqNum() >= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ && this.showSoftwareMaintenanceCoverage(lineItem)) {
            return true;
        }
        return false;
    }

    /**
     * Added by lee,March 30,2007 If part is a contract part
     *
     * @param lineItem
     * @return
     */
    public boolean showOverrideStandardLink(QuoteLineItem lineItem) {

       return  PartPriceConfigFactory.singleton().isDateEditAllowed(quote.getQuoteHeader(), lineItem);
    }

    /**
     *
     * @param lineItem
     * @return
     */
    public boolean showQtyProrationMessage(QuoteLineItem lineItem) {
        //      changes for ebiz ticket SDIN-77PLRC Assoc.Mtc date logic error
        PartDisplayAttr attr = lineItem.getPartDispAttr();
        return (isPA() || isOEM()) &&
        	//attr.isCustomerContractExist() &&
        	attr.isMaintBehavior() && !attr.isFromRQ() &&
        	attr.isLicensePartExist() &&
        	!attr.isQtyMatch();

    }

    /**
     *
     * @param lineItem
     * @return
     */
    public boolean showQtyMessage(QuoteLineItem lineItem) {

        PartDisplayAttr attr = lineItem.getPartDispAttr();
        return isFCT() && attr.isMaintBehavior() &&	attr.isLicensePartExist() && !attr.isQtyMatch();

    }

    public int getTotalQtyOfLicenseParts(QuoteLineItem lineItem) {
        return lineItem.getLicPartQty();
    }

    public boolean showMissingProrationMessageLink(QuoteLineItem lineItem) {
        if(CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader())){
            return false;
        }
        if (isPA() || isPAE() || isFCT() || isOEM()) {

            PartDisplayAttr attr = lineItem.getPartDispAttr();
            // Andy : the following conditions are checked
            // (1) it's a un-related maint part

            // (3) the associated license part is not in the quote
            return  attr.isUnRelatedMaint()
            		&&  !attr.isLicensePartExist()
            		&& !lineItem.isObsoletePart()
            		&& !lineItem.isApplncPart();

        }
        return false;
    }

    public boolean showCmprssCvrageRow(QuoteLineItem qli){
        //Back-dating and compressed coverage are mutually exclusive.
        if(!quote.getQuoteHeader().getCmprssCvrageFlag() || quote.getQuoteHeader().getBackDatingFlag()){
            return false;
        }

        return qli.isEligibleForCmprssCvrage();
    }

    //When invoking this method, the parameter qli is ensured to be eligible for comprss cvrage
    public Collection generateCmprssCvrageMonthOptions(QuoteLineItem qli) {
        Collection collection = new ArrayList();

        //Start value should be 1 month higher than current cvrage period
        int startMonthNumber = DateUtil.calculateWholeMonths(qli.getMaintStartDate(), qli.getMaintEndDate())
                                         + 1;

        //For parts referencing a renewal quote, the max cmprss cvrage length should be the original cvreage period
        int endMonthNumber = PartPriceConstants.MAX_CMPRSS_CVRAGE_MONTH;
        if(StringUtils.isNotBlank(qli.getRenewalQuoteNum())){
            endMonthNumber = DateUtil.calculateFullCalendarMonths(qli.getOrigStDate(), qli.getOrigEndDate());
        }

        int defaultMonth = PartPriceConstants.MAX_CMPRSS_CVRAGE_MONTH;
        //For cmprss cvreage eligible parts, cmprss cvrage month is sure to have default value
        if(qli.hasValidCmprssCvrageMonth() ){
            defaultMonth = qli.getCmprssCvrageMonth().intValue();
        }

    	for (int i = startMonthNumber; i <= endMonthNumber; i++) {
    		if (i == defaultMonth)
    			collection.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), true));
    		else
    			collection.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), false));
    	}

        return collection;
    }

    //check if the part is reference to renewal quote line item
    public boolean isSQReferenceRQLineItem(QuoteLineItem qli){

        if(quote.getQuoteHeader().isSalesQuote()
                && StringUtils.isNotBlank(qli.getRenewalQuoteNum())){
            return true;
        }

        return false;
    }
}