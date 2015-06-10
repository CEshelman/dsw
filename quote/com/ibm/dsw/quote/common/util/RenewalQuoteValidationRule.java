package com.ibm.dsw.quote.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.domain.SubmittedQuoteAccess;
import com.ibm.ead4j.common.util.DateHelper;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RenewalQuoteValidationRule</code> class .
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 25, 2007
 */
public class RenewalQuoteValidationRule extends QuoteValidationRule {

    /**
     * @param quote
     * @param user
     * @param cookie
     */
    public RenewalQuoteValidationRule(Quote quote, QuoteUserSession user, Cookie cookie) {
        super(quote, user, cookie);
    }

    /**
     * @throws QuoteException
     *  
     */

    public Map validateSubmissionAsFinal() throws QuoteException {
        validateRQCommon();
        // added 6/7/2007, to validate RQ's fulfillment source
        validateFulfillmentSourceAsFinal();
        
        // Validate appliance part MTM,Serial Number 
        verifyAppliancePart(validateResult);
        
        validateGrwthDlgtn();
        
        validateDivestedPart();

        return validateResult;
    }

    protected  void validateDivestedPart() {
    	List lineItemList = quote.getLineItemList();
    	if( lineItemList == null ||lineItemList.size() == 0) 
    		return;
        Iterator iterator = lineItemList.iterator();
        StringBuffer parts = new StringBuffer();
        while (iterator.hasNext()) {
        	QuoteLineItem item = (QuoteLineItem) iterator.next();
        	if(item.isDivestedPart()){
        		parts.append(item.getPartNum()+", ");
        	}
        }
        if(parts.length()>0){
        	String partStr = parts.toString().substring(0, parts.toString().lastIndexOf(","));
        	validateResult.put(QuoteCapabilityProcess.MSG_RQ_HAS_DIVESTED_PART, partStr);
        }
    }
    
    /**
     * validate all common input for both "as final" and "for approval"
     */
    private void validateRQCommon() {
        // If a contract was selected for an existing customer, the contract
        // must be active
        if (!isActiveIfContractSelected()) {
            validateResult.put(QuoteCapabilityProcess.CONTRACT_NOT_ACTIVE, Boolean.TRUE);
        }

        if (!isPartnerAccessSet()) {
            validateResult.put(QuoteCapabilityProcess.PARTNER_ACCESS_NOT_SET, Boolean.TRUE);
        }
        QuoteAccess quoteAccess = quote.getQuoteAccess();
        boolean quoteEditable = false;
        boolean quoteStatusUpdatable = false;
        boolean quoteSalesInfoEditable = false;
        if (null != quoteAccess) {
            quoteEditable = quoteAccess.isCanEditRQ();
            quoteStatusUpdatable = quoteAccess.isCanUpdateRQStatus();
            quoteSalesInfoEditable = quoteAccess.isCanEditRQSalesInfo();
        }
        // Validate follow information only if the quote?s status allows editing
        if (quoteEditable) {
            // Every part must have a quantity set
            // Part is a PVU part, and the quantity is 1
            // Every part must be active
            // Every part must have a price
            // if part changed, specify the reason
            this.verifyRenewalLineItems(validateResult);

            // Brief title must be filled in
            if (!isBriefTitleFilled()) {
                validateResult.put(QuoteCapabilityProcess.BRIEF_TITLE_NOT_FILLED, Boolean.TRUE);
            }

            // sales odd validate
            if (!isSalesOddSelected()) {
                validateResult.put(QuoteCapabilityProcess.SALES_ODDS_REQUIRED, Boolean.TRUE);
            }

            // A value of business organization must be selected
            if (!isBusOrgSelected()) {
                validateResult.put(QuoteCapabilityProcess.BUS_ORG_NOT_SELECTED, Boolean.TRUE);
            }
            // In order to submit quote, user should have submitter access level
            if (!audienceHasSubmitterAccess()) {
                validateResult.put(QuoteCapabilityProcess.AUD_CAN_NOT_SUBMIT_QUOTE, Boolean.TRUE);
            }

            // Upside transaction validate
            if (!isUpsideTranSelected()) {
                validateResult.put(QuoteCapabilityProcess.UPSIDE_TRAN_NOT_SELECT, Boolean.TRUE);
            }

        }
        else if (quoteStatusUpdatable || quoteSalesInfoEditable) {
            isBusOrgSelected();
        }
        // Validate this information only if:
        // the quote's status allows editing OR
        // status updates are allowed AND the quote contains open (not
        // ordered) line items with start dates that are not more than 90 days
        // in the past.

        if (quoteEditable || (quoteStatusUpdatable && isQuoteContainOpenLineItems())) {
            if (quote.getAllWebPrimaryStatuses().size() == 0) {
                validateResult.put(QuoteCapabilityProcess.QUOTE_STATUS_NOT_SET, Boolean.TRUE);
            }
        }
        
        // validate partner region & currency for FCT renewal quote
        int isValid = isPartnerRegnCntryCrncyValid() ;
        if (isValid == 1) {
            validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_REGION_MISMATCH, Boolean.TRUE);
        }
        if (isValid == 2) {
            validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_COUNTRY_MISMATCH, Boolean.TRUE);
        }
        if (isValid == 3) {
            validateResult.put(QuoteCapabilityProcess.CUST_PAYER_CURRENCY_MISMATCH, Boolean.TRUE);
        }
        
        if (!isQuoteClassfctnCodeSelected()) {
            validateResult.put(QuoteCapabilityProcess.QT_CLASSFCTN_NOT_SET, Boolean.TRUE);
        }
    }

    private void validateFulfillmentSourceAsFinal() {
        if (!isSpecialbid()) {
            if (!quote.getQuoteHeader().getFulfillmentSrc().equals(QuoteConstants.FULFILLMENT_NOT_SPECIFIED)) {
                validateResult.put(QuoteCapabilityProcess.QUOTE_FULFILLMENT_INVALID, Boolean.TRUE);
            }
        }
    }

    /**
     * @return any special bid flag
     */
    private boolean isSpecialbid() {
        return quote.getQuoteHeader().getSpeclBidFlag() == 1 || quote.getQuoteHeader().getSpeclBidSystemInitFlg() == 1
                || quote.getQuoteHeader().getSpeclBidManualInitFlg() == 1;
    }

    /**
     * @return
     */
    private boolean isQuoteContainOpenLineItems() {
        List lineItems = quote.getLineItemList();
        if (lineItems == null || lineItems.size() == 0) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 90);

        Iterator iterator = lineItems.iterator();
        while (iterator.hasNext()) {
            QuoteLineItem item = (QuoteLineItem) iterator.next();
            if (item.getOpenQty() > 0 && (item.getMaintStartDate().after(calendar.getTime()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @throws QuoteException
     *  
     */
    public Map validateSubmissionForApproval() throws QuoteException {
        validateResult.put(QuoteCapabilityProcess.RQ_NOT_SPECALL_BIDABLE, Boolean.TRUE);
        return validateResult;
    }

    /**
     *  
     */
    public Map validateOrder() {
        // Special bid indicator must be set to false
        if (getSpecialBidFlag(quote.getQuoteHeader())) {
            validateResult.put(QuoteCapabilityProcess.SPECIAL_BID_INDICATOR_NOT_FALSE, Boolean.TRUE);
        }

        // Exipired Date must be within the number of Quote Active Days
        if (!isRnwlQuoteAllowOrder()) {
            validateResult.put(QuoteCapabilityProcess.QUOTE_STATUS_NOT_ALLOW_ORDER, Boolean.TRUE);
        }

        // If a contract was selected for an existing customer, the contract
        // must be active
        if (!isActiveIfContractSelected()) {
            validateResult.put(QuoteCapabilityProcess.CONTRACT_NOT_ACTIVE, Boolean.TRUE);
        }

        // partner access must be selected
        if (!isRnwlPartnerAccessSelected()) {
            validateResult.put(QuoteCapabilityProcess.PARTNER_ACCESS_NOT_SET, Boolean.TRUE);
        }
        
        if (!isQuoteClassfctnCodeSelected()) {
            validateResult.put(QuoteCapabilityProcess.QT_CLASSFCTN_NOT_SET, Boolean.TRUE);
        }

        // Every part must have a quantity set
        // Part is a PVU part, and the quantity is 1
        // Every part must be active
        // Every part must have a price
        this.verifyRenewalLineItems(validateResult);
        
        // Validate appliance part MTM,Serial Number 
        verifyAppliancePart(validateResult);

        // Brief title must be filled in
        if (!isBriefTitleFilled()) {
            validateResult.put(QuoteCapabilityProcess.BRIEF_TITLE_NOT_FILLED, Boolean.TRUE);
        }

        // A value of business organization must be selected
        if (!isBusOrgSelected()) {
            validateResult.put(QuoteCapabilityProcess.BUS_ORG_NOT_SELECTED, Boolean.TRUE);
        }

        if (!isSalesOddsSelected())
            validateResult.put(QuoteCapabilityProcess.SALES_ODDS_REQUIRED, Boolean.TRUE);

        if (!isUpsideTranSelected())
            validateResult.put(QuoteCapabilityProcess.UPSIDE_TRAN_NOT_SELECT, Boolean.TRUE);
        
        if (!isRnwlStatusSelected()) {
            validateResult.put(QuoteCapabilityProcess.QUOTE_STATUS_NOT_SET, Boolean.TRUE);
        }

        // audience should have telesales update access
        if (!audienceHasSubmitterAccess()) {
            validateResult.put(QuoteCapabilityProcess.AUD_CAN_NOT_SUBMIT_QUOTE, Boolean.TRUE);
        }
        
        validateFulfillmentSourceAsFinal();
        
        validateGrwthDlgtn();
        
        return validateResult;
    }

    protected boolean isRnwlQuoteAllowOrder() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;
        QuoteAccess qtAccess = quote.getQuoteAccess();
        if (qtAccess == null)
            return false;
        return qtAccess.isCanOrderRQ(); 
    }

    protected boolean isRnwlPartnerAccessSelected() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;
        return (quote.getQuoteHeader().getRnwlPrtnrAccessFlag() >= 0);
    }
    
    protected boolean isRnwlStatusSelected() {
        if (quote == null)
            return false;
        List status = quote.getAllWebPrimaryStatuses();
        if (status == null || status.size() == 0)
            return false;
        else
            return true;
    }
    
    protected boolean hasManualChanges() {
        List lineItemList = quote.getLineItemList();
        Iterator iterator = lineItemList.iterator();
        
        while (iterator.hasNext()) {
            QuoteLineItem lineItem = (QuoteLineItem) iterator.next();
            if(StringUtils.contains(lineItem.getChgType(), PartPriceConstants.PartChangeType.PART_ADDED)
                    || StringUtils.contains(lineItem.getChgType(), PartPriceConstants.PartChangeType.PART_DELETED)
                    || StringUtils.contains(lineItem.getChgType(), PartPriceConstants.PartChangeType.PART_QTY_DECREASE)
                    || StringUtils.contains(lineItem.getChgType(), PartPriceConstants.PartChangeType.PART_QTY_INCREASE)
                    || StringUtils.contains(lineItem.getChgType(), PartPriceConstants.PartChangeType.PART_DATE_CHANGED)
                    || StringUtils.contains(lineItem.getChgType(), PartPriceConstants.PartChangeType.PART_PVU_CHANGED)
                    || StringUtils.contains(lineItem.getChgType(),
                            PartPriceConstants.PartChangeType.PART_DISCOUNT_UNIT_PRICE_CHANAGED)) {
                //has line item change, perform line item validation
                return true;
            }
        }
        
        return false;
    }

    protected void verifyRenewalLineItems(Map result) {
        if (quote == null || quote.getLineItemList() == null) {
            result.put(QuoteCapabilityProcess.PART_QTY_NOT_SET, Boolean.TRUE);
            return;
        }

        List lineItemList = quote.getLineItemList();
        Iterator iterator = lineItemList.iterator();
        StringBuffer delParts = new StringBuffer();
        boolean hasManualChanges = this.hasManualChanges();
        
        while (iterator.hasNext()) {
            QuoteLineItem item = (QuoteLineItem) iterator.next();
            // Every part must have a quantity set
            // Part is a PVU part, and the quantity is 1
            // Every part must be active
            // Every part must have a price
            if (item.getPartQty() == null) {
                result.put(QuoteCapabilityProcess.PART_QTY_NOT_SET, Boolean.TRUE);
            }
            if (item.isPvuPart() && item.getPartQty() != null && item.getPartQty().intValue() == 1) {
                result.put(QuoteCapabilityProcess.PVU_PART_QTY_NOT_RIGHT, Boolean.TRUE);
            }
            if (hasManualChanges) {
                // If it is obsolete part, entitled price and overridden price are required
	            if (item.getPartQty() != null && item.getPartQty().intValue() != 0 
	                    && (item.getLocalUnitProratedDiscPrc() == null 
	                            || (item.isObsoletePart() && item.getOverrideUnitPrc() == null))) {
	                result.put(QuoteCapabilityProcess.PART_NOT_HAS_PRICE, Boolean.TRUE);
	            }
            }
            if (!isFCT() && StringUtils.isBlank(item.getComment())) {
                if (PartPriceConstants.PartChangeType.PART_NO_CHANGES.equalsIgnoreCase(item.getChgType()) || StringUtils.isBlank(item.getChgType())) {
                    // do nothing;
                } else if (PartPriceConstants.PartChangeType.PART_ADDED.equalsIgnoreCase(item.getChgType())) {
                    result.put(QuoteCapabilityProcess.REASON_FOR_INSERT_REQUIRED, Boolean.TRUE);
                } else if (PartPriceConstants.PartChangeType.PART_DELETED.equalsIgnoreCase(item.getChgType())) {
                    result.put(QuoteCapabilityProcess.REASON_FOR_DELETE_REQUIRED, Boolean.TRUE);
                } else if (StringUtils.contains(item.getChgType(), PartPriceConstants.PartChangeType.PART_QTY_INCREASE) || 
                        StringUtils.contains(item.getChgType(), PartPriceConstants.PartChangeType.PART_DATE_CHANGED)) {
                    result.put(QuoteCapabilityProcess.REASON_FOR_CHANGE_REQUIRED, Boolean.TRUE);
                }
            }
            if (PartPriceConstants.PartChangeType.PART_DELETED.equalsIgnoreCase(item.getChgType()) 
                    && item.getSalesQuoteRefFlag()) {
                if (delParts.length() == 0)
                    delParts.append(item.getPartNum());
                else
                    delParts.append(", ").append(item.getPartNum());
            }
        }
        if (delParts.length() > 0)
            result.put(QuoteCapabilityProcess.DEL_ITEM_REF_BY_SQ, delParts.toString());
    }

    protected boolean isSalesOddsSelected() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;
        return StringUtils.isNotBlank(quote.getQuoteHeader().getRenwlQuoteSalesOddsOode());
    }

    protected boolean isUpsideTranSelected() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;
        return StringUtils.isNotBlank(quote.getQuoteHeader().getUpsideTrendTowardsPurch());
    }
    
    public boolean canCnvrtToSalesQuote() {
        
        QuoteHeader header = quote.getQuoteHeader();
        QuoteAccess qtAccess = quote.getQuoteAccess();
        
        if (qtAccess.isCanBeAddedToSQ()) {
            Date rnwlEndDate = header.getRenwlEndDate();
            if (rnwlEndDate != null) {
                Date now = DateHelper.singleton().today();
                if (now.before(rnwlEndDate)) {
                    return true;
                } else {
                    return DateHelper.singleton().daysDifference(rnwlEndDate, now) <= 90;
                }
            }
        }
        return false;
    }
    
    public boolean canCnvrtToSpeclBid() {
        
        int userAccess = user.getAccessLevel(QuoteConstants.APP_CODE_SQO);
        QuoteHeader header = quote.getQuoteHeader();
        QuoteAccess qtAccess = quote.getQuoteAccess();
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        String cntryCode = header.getCountry() == null ? "" : header.getCountry().getCode3();
        String isMigration = header.isMigration() ? "1" : "0";
        
        if (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER 
                && qtAccess.isCanCreateRQSpeclBid()
                && header.getPartHasPriceFlag() == 1
                && isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes())) {
            if (header.isPAQuote())
                return this.hasActiveContract();
            else
                return true;
        }
        
        return false;
    }

    public Map getDraftQuoteActionButtonsRule(QuoteUserSession salesRep) {
        HashMap rules = new HashMap();

        if (user == null || quote == null || quote.getQuoteHeader() == null || quote.getQuoteAccess() == null)
            return null;
		
        Customer cust = quote.getCustomer();
        boolean isCustBlocked = false;//if the customer is blocked, for hidden order button message
        if(cust !=null && cust.isCustOrdBlcked()){
        	isCustBlocked = true;
        }
        int userAccess = user.getAccessLevel(QuoteConstants.APP_CODE_SQO);
        QuoteHeader header = quote.getQuoteHeader();
        QuoteAccess qtAccess = quote.getQuoteAccess();
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        String cntryCode = header.getCountry() == null ? "" : header.getCountry().getCode3();
        String isMigration = header.isMigration() ? "1" : "0";
        boolean ELAFlag = header.isELAQuote();

        boolean isDisplayCnvrtToSalesBtn = false;
        boolean isDisplaySubmitAsFinalBtn = false;
        boolean isDisplayOrderBtn = false;
        boolean isDisplayCtrctNotActiveMsg = false;
        boolean isDisplayPartNotPrcMsg = false;
        boolean isDiaplaySBNotPermittedMsg = false;
        boolean isDisplayDldRichTextBtn = false;
        boolean isDisplayBluePageFailedMsg = false;
        boolean isDisplayCreateSpeclBidBtn = false;
        boolean isDisplayObsltPartMsg = false;
        boolean isDisplayNotSpeclBidableMsg = false;
        boolean isDisplayNotAllowOrderForCustBlockMsg = false; //not allow order as a blocked customer - sods2.customer.sales_ord_block = '01'
        boolean isDisplayCntryNotAllowOrderMsg = false;
        boolean isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg = false; //Hide the submit button and display message if the quote is a channel quote and the quote has SaaS parts in it
        

        logContext.debug(this, "Draft renewal quote common button rules begin:");
        logContext.debug(this, "Web quote number is: " + header.getWebQuoteNum());
        logContext.debug(this, "User id is: " + user.getEmail());
        logContext.debug(this, "Can be added to sales quote is: " + qtAccess.isCanBeAddedToSQ());
        logContext.debug(this, "Renewal end date is: " + header.getRenwlEndDate());
        logContext.debug(this, "User access is: " + userAccess);
        logContext.debug(this, "Can edit renewal quote is: " + qtAccess.isCanEditRQ());
        logContext.debug(this, "Can create RQ special bid is: " + qtAccess.isCanCreateRQSpeclBid());
        logContext.debug(this, "Special bid flag is: " + header.getSpeclBidFlag());
        logContext.debug(this, "Part has price flag is: " + header.getPartHasPriceFlag());
        logContext.debug(this, "Has obsolete parts flag is: " + header.getHasObsoletePartsFlag());
        logContext.debug(this, "Can edit renewal quote sales info is: " + qtAccess.isCanEditRQSalesInfo());
        logContext.debug(this, "Can update renewal quote status is: " + qtAccess.isCanUpdateRQStatus());
        logContext.debug(this, "isActiveIfContractSelected: " + isActiveIfContractSelected());
        logContext.debug(this, "LOB is: " + lob);
        logContext.debug(this, "hasActiveContract is: " + hasActiveContract());
        logContext.debug(this, "isSelectedContractNotActive is: " + isSelectedContractNotActive());
        logContext.debug(this, "ELA flag is: " + ELAFlag);
        logContext.debug(this, "Country code is: " + cntryCode);
        logContext.debug(this, "isSBButtonDisplay is: " + isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes()));
        logContext.debug(this, "isOrderButtonDisplay is: " + isOrderButtonDisplay(lob, ELAFlag, cntryCode));

        // Quote's status allows action.
        // Reference the "Quote can be added to a sales quote" column in the
        // Primary Statuses and Secondary Statuses tables in the appendix.
        // Quote's renewal end date is not more than 90 days in the past.
        isDisplayCnvrtToSalesBtn = this.canCnvrtToSalesQuote();
        
        isDisplayCreateSpeclBidBtn = this.canCnvrtToSpeclBid();

        // User's access level is submit
        // Quote has NOT been flagged as special bid
        // For PA quotes, the contract must be active
        // Quotes status allows action per one of the following:
        // Sales rep can edit sales info column in the Primary Statuses and
        // Secondary Statuses tables in the appendix.
        // Can update status when open line items' column in the Primary
        // Statuses and Secondary Statuses tables in the appendix.
        // Note that the quote must have open (not ordered) line items with a
        // start date not more than 90 days in the past.
        // Status permits action per the sales rep can edit quote column in the
        // Primary Statuses and Secondary Statuses tables
        // All parts on the draft renewal quote have pricing
        // All parts on the draft renewal quote are active
        // For channel quote,must no SaaS parts in it
        if (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER && header.getSpeclBidFlag() == 0
                && salesRep != null
                && ((qtAccess.isCanEditRQ() && header.getPartHasPriceFlag() == 1) 
                        || (!qtAccess.isCanEditRQ()
                                && (qtAccess.isCanEditRQSalesInfo() || qtAccess.isCanUpdateRQStatus())))) {
            if (header.isPAQuote())
                isDisplaySubmitAsFinalBtn = this.hasActiveContract();
            else
                isDisplaySubmitAsFinalBtn = true;
            
            if (isDisplaySubmitAsFinalBtn && header.hasSaaSLineItem()) {
				isDisplaySubmitAsFinalBtn = false;
			}  
        }

        // User's access level is submit
        // Quote has NOT been flagged as special bid (see business rules above)
        // For PA quotes, the contract must be active
        // Status permits action per the Sales rep can edit quote column in the
        // Primary Statuses and Secondary Statuses tables in the appendix
        // All parts on the draft renewal quote have pricing
        // All parts on the draft renewal quote are active
        if (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER && header.getSpeclBidFlag() == 0
                && qtAccess.isCanOrderRQ() && header.getPartHasPriceFlag() == 1
                && salesRep != null
                && isOrderButtonDisplay(lob, ELAFlag, cntryCode)
                &&!isCustBlocked 
                &&!header.hasSaaSLineItem()
        	) {
            if (header.isPAQuote())
                isDisplayOrderBtn = this.hasActiveContract();
            else
                isDisplayOrderBtn = true;
        }
        
        isDisplayNotSpeclBidableMsg = (this.canCnvrtToSpeclBid() && header.getSpeclBidFlag() == 1);

        // User's access level is submit
        // Neither the submit as final nor the submit for approval button
        // display
        // The line of business is PA
        // The selected contract is not active
        isDisplayCtrctNotActiveMsg = (!isDisplaySubmitAsFinalBtn && !isDisplayNotSpeclBidableMsg
                && userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER && quote.getQuoteHeader().isPAQuote() && this
                .isSelectedContractNotActive());

        // User's access level is submit
        // Neither the submit as final nor the submit for approval button
        // displays
        // If the line of business is PA, the contract is active
        // One or more parts are not active cannot be priced.
        if (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER && !isDisplayNotSpeclBidableMsg
                && !isDisplaySubmitAsFinalBtn && (quote.getQuoteHeader().getPartHasPriceFlag() == 0)) {
            if (header.isPAQuote())
                isDisplayPartNotPrcMsg = this.hasActiveContract();
            else
                isDisplayPartNotPrcMsg = true;
        }
        
        // User?s access level is submitter
        // Quotes status allows action. Reference the "Sales rep can edit quote" column in the 
        // Primary Renewal Quote Statuses and Secondary Renewal Quote Statuses tables
        // Quote has been flagged as special bid (see business rules above)
        // The quote's line of business (quote type) and country combination do not permit special bids to be submitted
        isDiaplaySBNotPermittedMsg = (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER 
                && qtAccess.isCanEditRQ()
                && header.getSpeclBidFlag() == 1
                && !this.isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes())
                && !isDisplayNotSpeclBidableMsg);
        
        // Always display
        isDisplayDldRichTextBtn = true;
        
        // display if quoteUserSession object is null
        isDisplayBluePageFailedMsg = (salesRep == null && !isDisplayCtrctNotActiveMsg
                && !isDisplayPartNotPrcMsg && !isDiaplaySBNotPermittedMsg
                && !isDisplayNotSpeclBidableMsg);
        
        isDisplayObsltPartMsg = header.getHasObsoletePartsFlag();
        
        if(!isDisplayOrderBtn){
        	if(isCustBlocked){//if customer is blocked, show message
        	   isDisplayNotAllowOrderForCustBlockMsg = true;
        	}
        	if(!isOrderButtonDisplay(lob, false, cntryCode)) {
        	    isDisplayCntryNotAllowOrderMsg = true;
        	}
        }
        
        //Display message if the quote is a channel quote and the quote has SaaS parts in it
        if(!isDisplaySubmitAsFinalBtn && header.hasSaaSLineItem()&&(!header.isOnlySaaSParts())){
        	isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg = true;
        }
        
        rules.put(QuoteCapabilityProcess.DISPLAY_CNVRT_TO_SALES_QT_BTN, Boolean.valueOf(isDisplayCnvrtToSalesBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN, Boolean.valueOf(isDisplaySubmitAsFinalBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_ORDER_BTN, Boolean.valueOf(isDisplayOrderBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_CTRCT_NOT_ACTIVE_MSG, Boolean.valueOf(isDisplayCtrctNotActiveMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_PART_NO_RPC_MSG, Boolean.valueOf(isDisplayPartNotPrcMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_SB_NOT_PERMITTED_MSG, Boolean.valueOf(isDiaplaySBNotPermittedMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN, Boolean.valueOf(isDisplayDldRichTextBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_BLUE_PAGE_UNAVAILABLE_MSG, Boolean.valueOf(isDisplayBluePageFailedMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CREATE_SPECL_BID_BTN, Boolean.valueOf(isDisplayCreateSpeclBidBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG, Boolean.valueOf(isDisplayObsltPartMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_NOT_SPECL_BIDABLE_MSG, Boolean.valueOf(isDisplayNotSpeclBidableMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG, Boolean.valueOf(isDisplayNotAllowOrderForCustBlockMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG, Boolean.valueOf(isDisplayCntryNotAllowOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG, Boolean.valueOf(isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg));
        
        return rules;
    }

    public Map getSubmittedQuoteActionButtonsRule() {
        HashMap rules = new HashMap();

        if (user == null || quote == null || quote.getQuoteHeader() == null)
            return null;

        int userAccess = user.getAccessLevel(QuoteConstants.APP_CODE_SQO);
        QuoteHeader header = quote.getQuoteHeader();
        SubmittedQuoteAccess sbmtQtAccess = quote.getSubmittedQuoteAccess();
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        String cntryCode = header.getCountry() == null ? "" : header.getCountry().getCode3();
        boolean ELAFlag = header.isELAQuote();

        boolean isDisplayOverrideExpDate = false;
        boolean isDisplayEnterAddiSBJust = false;
        boolean isDisplayUpdateSpecialBid = false;
        boolean isDisplayOrder = false;
        boolean isDisplayCancelSpecialBid = false;
        boolean isDisplayApprAction = false;
        boolean isDisplayAddiComments = false;
        boolean isDisplaySaveComments = false;
        boolean isDisplayManualIntrvntnNeededMsg = false;
        boolean isDisplayQtProcessInSapMsg = false;
        boolean isDisplayHasOrderedMsg = false;
        boolean isDisplayCancelledQuoteMsg = false;
        boolean isDisplaySpecBidQuoteMsg = false;
        boolean isDisplaySubmitterAccessRequiredMsg = false;
        boolean isDisplayChannelSQOrderMsg =false;
        boolean isDisplayDldRichTextBtn = false;
        boolean isDisplayExprtSprdshtBtn = false;

        boolean isDisplayExpSBCannotOrderMsg = false;
        boolean isDisplaySBExpDatePassedMsg = false;
        boolean isDisplaySpecBidNotActiveMsg = false;
        boolean isDisplayExecSmmryMsg = false;
        boolean isDisplayObsltPartMsg = false;
        boolean canCancelApprovedBid = false;
        boolean isDisplayNotAllowOrderForCustBlockMsg = false; //not allow order as a blocked customer - sods2.customer.sales_ord_block = '01'
        boolean isDisplayCntryNotAllowOrderMsg = false;
        boolean isDisplaySSPWithOutContract = false;
        
        logContext.debug(this, "Submitted renewal quote common button rules begin:");
        logContext.debug(this, "Web quote number is: " + header.getWebQuoteNum());
        logContext.debug(this, "User id is: " + user.getEmail());
        logContext.debug(this, "User access is: " + userAccess);
        logContext.debug(this, "SpeclBidFlag is: " + header.getSpeclBidFlag());
        logContext.debug(this, "userIsFirstAppGrpMember is: " + userIsFirstAppGrpMember());
        logContext.debug(this, "quoteHasNoneApproval is: " + quoteHasNoneApproval());
        logContext.debug(this, "userIsQuoteCreator is: " + userIsQuoteCreator());
        logContext.debug(this, "userIsQuoteEditor is: " + userIsQuoteEditor());
        logContext.debug(this, "userIsAnyEditor is: " + userIsAnyEditor());
        logContext.debug(this, "userIsPendingAppGrpMember is: " + userIsPendingAppGrpMember());
        logContext.debug(this, "userIsReviewer is: " + userIsReviewer());
        logContext.debug(this, "userCanChangeBidExpDate is: " + userCanChangeBidExpDate());
        logContext.debug(this, "userCanApprove is: " + userCanApprove());
        logContext.debug(this, "userCanViewExecSummary is: " + userCanViewExecSummary());
        logContext.debug(this, "LOB is: " + lob);
        logContext.debug(this, "ELA flag is: " + ELAFlag);
        logContext.debug(this, "Country code is: " + cntryCode);
        logContext.debug(this, "isOrderButtonDisplay is: " + isOrderButtonDisplay(lob, ELAFlag, cntryCode));
        logContext.debug(this, "isExpirationDateBeforeToday is: " + isExpirationDateBeforeToday());
        logContext.debug(this, "isExprtSprdshtButtonDisplay is: " + isExprtSprdshtButtonDisplay(lob));
        logContext.debug(this, "Has obsolete parts flag is: " + header.getHasObsoletePartsFlag());

        List overallStatuses = header.getQuoteOverallStatuses();
        if (overallStatuses != null) {
            for (int i = 0; i < overallStatuses.size(); i++) {
                CodeDescObj qtOS = (CodeDescObj) overallStatuses.get(i);
                if (qtOS != null)
                    logContext.debug(this, "Overall status contains: " + qtOS.getCodeDesc());
            }
        }

        List sapPrimStatuses = quote.getSapPrimaryStatusList();
        if (sapPrimStatuses != null) {
            for (Iterator iter = sapPrimStatuses.iterator(); iter.hasNext();) {
                QuoteStatus status = (QuoteStatus) iter.next();
                logContext.debug(this, "SAP primary status contains: " + status.getStatusCode());
            }
        }

        List sapSecStatuses = quote.getSapSecondaryStatusList();
        if (sapSecStatuses != null) {
            for (Iterator iter = sapSecStatuses.iterator(); iter.hasNext();) {
                QuoteStatus status = (QuoteStatus) iter.next();
                logContext.debug(this, "SAP secondary status contains: " + status.getStatusCode());
            }
        }

        // The special bid flag is true, and
        // The current user's access level is approver, and
        // The quote's overall status is Awaiting special bid approval, and
        // No approvers have approved the quote, and
        // The current user is a member of the first approval group listed on
        // the quote
        isDisplayOverrideExpDate = (header.getSpeclBidFlag() == 1 && userAccess == QuoteConstants.ACCESS_LEVEL_APPROVER
                && header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR)
                && userCanChangeBidExpDate());

        if (QuoteConstants.ACCESS_LEVEL_SUBMITTER == userAccess) {
            if (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO))
                isDisplayEnterAddiSBJust = this.userIsAnyEditor();
            else if (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR))
                isDisplayEnterAddiSBJust = (this.userIsAnyEditor() && this.quoteHasNoneApproval());
        }

        isDisplayUpdateSpecialBid = (QuoteConstants.ACCESS_LEVEL_SUBMITTER == userAccess && this.userIsAnyEditor() && (header
                .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG)
                || header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_REJECTED) || quote
                .containsSapSecondaryStatus(SubmittedQuoteConstants.RQ_SPECIAL_BID_CANCELLED)));

        if (QuoteConstants.ACCESS_LEVEL_SUBMITTER == userAccess && this.userIsAnyEditor() && (header
                .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.READY_TO_ORDER))
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO)
                && !QuoteConstants.QUOTE_STAGE_CODE_CANCEL.equalsIgnoreCase(header.getQuoteStageCode())
                && isOrderButtonDisplay(lob, ELAFlag, cntryCode)
                && !(quote.containsWebSecondaryStatus(SubmittedQuoteConstants.RQ_SPECIAL_BID_APPROVED_STATUS)
                        && this.isExpirationDateBeforeToday())) {
            
            if (header.getSpeclBidFlag() == 0)
                isDisplayOrder = true;
            else
                isDisplayOrder = QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(header.getFulfillmentSrc());
        }
        
        isDisplaySSPWithOutContract = checkSSPContractCust(lob,isDisplayOrder);
        
        isDisplayOrder = (isDisplayOrder && !isDisplaySSPWithOutContract);

        isDisplayCancelSpecialBid = ( QuoteConstants.ACCESS_LEVEL_SUBMITTER == userAccess && this.userIsAnyEditor() 
                && ( header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR) 
                        || header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO)
                        || ( quote.containsWebSecondaryStatus(SubmittedQuoteConstants.RQ_SPECIAL_BID_APPROVED_STATUS)
                                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)
                                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED)
                                && !isExpirationDateBeforeToday()
                        	) 
                	)
                && !QuoteConstants.QUOTE_STAGE_CODE_CANCEL.equalsIgnoreCase(header.getQuoteStageCode()));

        // whether the approver can approve this special bid.
        isDisplayApprAction = (QuoteConstants.ACCESS_LEVEL_APPROVER == userAccess 
                && header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR)
                && this.userCanApprove());

        // whether the approver can add additional comments.
        if (QuoteConstants.ACCESS_LEVEL_APPROVER == userAccess && userIsAnyAppGrpMember()) {
            if (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR))
                isDisplayAddiComments = !userIsPendingAppGrpMember() && !this.userCanApprove();
            else
                isDisplayAddiComments = true;
        }

        isDisplaySaveComments = userIsReviewer();
        
        // Always display
        isDisplayDldRichTextBtn = true;
        
        isDisplayExprtSprdshtBtn = (QuoteConstants.ACCESS_LEVEL_SUBMITTER == userAccess
                && this.isExprtSprdshtButtonDisplay(lob));

        Customer cust = quote.getCustomer();
        if (!isDisplayOrder) {
            isDisplaySubmitterAccessRequiredMsg = (QuoteConstants.ACCESS_LEVEL_SUBMITTER != userAccess);
            
            isDisplayChannelSQOrderMsg = (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc())
                    && !isDisplaySubmitterAccessRequiredMsg);
            
            isDisplayCancelledQuoteMsg = ((header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED)
                    || header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS)
                    || QuoteConstants.QUOTE_STAGE_CODE_CANCEL.equalsIgnoreCase(header.getQuoteStageCode()))
	                && !isDisplaySubmitterAccessRequiredMsg
	                && !isDisplayChannelSQOrderMsg);
            
            isDisplayHasOrderedMsg = ((sbmtQtAccess == null ? false : sbmtQtAccess.isHasOrdered())
	                && !isDisplaySubmitterAccessRequiredMsg
	                && !isDisplayChannelSQOrderMsg
	                && !isDisplayCancelledQuoteMsg);
            
            isDisplaySpecBidQuoteMsg = ((header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR)
	                || header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO)
	                || header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG))
	                && !isDisplaySubmitterAccessRequiredMsg
	                && !isDisplayChannelSQOrderMsg
	                && !isDisplayCancelledQuoteMsg
	                && !isDisplayHasOrderedMsg);
            
            isDisplayQtProcessInSapMsg = ((sbmtQtAccess == null ? false : sbmtQtAccess.isNoStatusInOneHour())
                    && header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.QUOTE_ON_HOLD)
	                && !isDisplaySubmitterAccessRequiredMsg
	                && !isDisplayChannelSQOrderMsg
	                && !isDisplayCancelledQuoteMsg
	                && !isDisplayHasOrderedMsg
	                && !isDisplaySpecBidQuoteMsg);
	        
	        isDisplayManualIntrvntnNeededMsg = ((sbmtQtAccess == null ? false : sbmtQtAccess.isNoStatusOverOneHour())
	                && header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.QUOTE_ON_HOLD)
	                && !isDisplaySubmitterAccessRequiredMsg
	                && !isDisplayChannelSQOrderMsg
	                && !isDisplayCancelledQuoteMsg
	                && !isDisplayHasOrderedMsg
	                && !isDisplaySpecBidQuoteMsg
	                && !isDisplayQtProcessInSapMsg);
	        
	        // Display if overall status is ready to order and 
	        // special bid approved with expiration in the past
	        isDisplayExpSBCannotOrderMsg = ( header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.READY_TO_ORDER)
	                && quote.containsWebSecondaryStatus(SubmittedQuoteConstants.RQ_SPECIAL_BID_APPROVED_STATUS)
	                && isExpirationDateBeforeToday()
	                && !isDisplaySubmitterAccessRequiredMsg
	                && !isDisplayChannelSQOrderMsg 
	                && !isDisplayCancelledQuoteMsg
	                && !isDisplayHasOrderedMsg
	                && !isDisplaySpecBidQuoteMsg
	                && !isDisplayQtProcessInSapMsg 
	                && !isDisplayManualIntrvntnNeededMsg );
			
			//if customer is blocked, show message
	        isDisplayNotAllowOrderForCustBlockMsg = (
	        		   (cust!=null)
	        		&& cust.isCustOrdBlcked()
	                && !isDisplaySubmitterAccessRequiredMsg
	                && !isDisplayChannelSQOrderMsg 
	                && !isDisplayCancelledQuoteMsg 
	                && !isDisplayHasOrderedMsg 
	                && !isDisplaySpecBidQuoteMsg 
	                && !isDisplayQtProcessInSapMsg 
	                && !isDisplayManualIntrvntnNeededMsg 
	                && !isDisplayExpSBCannotOrderMsg
	        		);
	        
	        isDisplayCntryNotAllowOrderMsg = !isOrderButtonDisplay(lob, false, cntryCode);
	        
	        isDisplaySpecBidNotActiveMsg = (!isDisplayCancelSpecialBid
	                && !isDisplaySubmitterAccessRequiredMsg
	                && !isDisplayChannelSQOrderMsg 
	                && !isDisplayCancelledQuoteMsg 
	                && !isDisplayHasOrderedMsg 
	                && !isDisplaySpecBidQuoteMsg 
	                && !isDisplayQtProcessInSapMsg 
	                && !isDisplayManualIntrvntnNeededMsg 
	                && !isDisplayExpSBCannotOrderMsg 
	                && !isDisplayNotAllowOrderForCustBlockMsg 
	                && !isDisplayCntryNotAllowOrderMsg);
	        
        }

        // Display if special bid approved and not Order on hold 
        // and not Ordered but not billed and the expiration date is in the past 
        isDisplaySBExpDatePassedMsg = (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER 
                && this.userIsAnyEditor()
                && quote.containsWebSecondaryStatus(SubmittedQuoteConstants.RQ_SPECIAL_BID_APPROVED_STATUS)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED)
                && isExpirationDateBeforeToday());
        
        isDisplayExecSmmryMsg = (header.getSpeclBidFlag() == 1 && !userCanViewExecSummary());
        
        isDisplayObsltPartMsg = header.getHasObsoletePartsFlag();
        
        canCancelApprovedBid = canCancelApprovedBid(userAccess);
        
        rules.put(QuoteCapabilityProcess.DISPLAY_OVERRIDE_EXP_DATE, Boolean.valueOf(isDisplayOverrideExpDate));
        rules.put(QuoteCapabilityProcess.DISPLAY_SUBMITTER_ENTER_ADDI_SB_JUST, Boolean.valueOf(isDisplayEnterAddiSBJust));
        rules.put(QuoteCapabilityProcess.DISPLAY_UPDATE_SPEC_BID_BTN, Boolean.valueOf(isDisplayUpdateSpecialBid));
        rules.put(QuoteCapabilityProcess.DISPLAY_ORDER_BTN, Boolean.valueOf(isDisplayOrder));
        rules.put(QuoteCapabilityProcess.DISPLAY_CANCEL_SPEC_BID_BTN, Boolean.valueOf(isDisplayCancelSpecialBid));
        rules.put(QuoteCapabilityProcess.DISPLAY_APPROVER_ACTION, Boolean.valueOf(isDisplayApprAction));
        rules.put(QuoteCapabilityProcess.DISPLAY_APPROVER_ADDI_COMMENTS, Boolean.valueOf(isDisplayAddiComments));
        rules.put(QuoteCapabilityProcess.DISPLAY_REVIEWER_SAVE_COMMENTS, Boolean.valueOf(isDisplaySaveComments));
        rules.put(QuoteCapabilityProcess.DISPLAY_MANUAL_INTRVNTN_NEEDED_MSG, Boolean.valueOf(isDisplayManualIntrvntnNeededMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_QT_PROCESS_IN_SAP_MSG, Boolean.valueOf(isDisplayQtProcessInSapMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_HAS_ORDERED_MSG, Boolean.valueOf(isDisplayHasOrderedMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CANCELLED_QT_MSG, Boolean.valueOf(isDisplayCancelledQuoteMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_SB_QT_MSG, Boolean.valueOf(isDisplaySpecBidQuoteMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_SBMT_ACL_REQ_MSG, Boolean.valueOf(isDisplaySubmitterAccessRequiredMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CHANNEL_QT_ORDER_MSG, Boolean.valueOf(isDisplayChannelSQOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN, Boolean.valueOf(isDisplayDldRichTextBtn));        
        rules.put(QuoteCapabilityProcess.DISPLAY_EXPIRED_SB_QT_CANNOT_ORDERED_MSG, Boolean.valueOf(isDisplayExpSBCannotOrderMsg));  
        rules.put(QuoteCapabilityProcess.DISPLAY_SB_NOT_ACTIVE_MSG, Boolean.valueOf(isDisplaySpecBidNotActiveMsg));  
        rules.put(QuoteCapabilityProcess.DISPLAY_EXPIRATION_DATE_PASSED_MSG, Boolean.valueOf(isDisplaySBExpDatePassedMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_EXEC_SMMRY_MSG, Boolean.valueOf(isDisplayExecSmmryMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG, Boolean.valueOf(isDisplayObsltPartMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_EXPRT_SPRDSHT_BTN, Boolean.valueOf(isDisplayExprtSprdshtBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_CANCEL_APPROVED_BID, Boolean.valueOf(canCancelApprovedBid));
        rules.put(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG, Boolean.valueOf(isDisplayNotAllowOrderForCustBlockMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG, Boolean.valueOf(isDisplayCntryNotAllowOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_SSP_NO_CONTACT, Boolean.valueOf(isDisplaySSPWithOutContract));
        return rules;
    }
    
    public boolean canConvertToPAE() {
        return false;
    }
    
    protected int isPartnerRegnCntryCrncyValid() {
        // 0 :  Valid data
        // 1 :  Invalid region
        // 2 :  Invalid country
        // 3 :  Invalid currency

        QuoteHeader header = quote.getQuoteHeader();
        Customer cust = quote.getCustomer();
        Partner payer = quote.getPayer();
        Partner reseller = quote.getReseller();
        
        String geo = header.getCountry().getSpecialBidAreaCode();
        String custCntry = cust.getCountryCode();
        String custSalesOrg = cust.getSalesOrg();
        String custCurrency = cust.getCurrencyCode();
        
        if (header.isFCTQuote()) {

            if (payer != null) {
                String payerCntry = StringUtils.trimToEmpty(payer.getCountry());
                String payerSalesOrg = StringUtils.trimToEmpty(payer.getSapSalesOrgCode());
                String payerCurrency = StringUtils.trimToEmpty(payer.getCurrencyCode());
                
                if (QuoteConstants.GEO_AP.equalsIgnoreCase(geo)) {
                    if (!custCntry.equalsIgnoreCase(payerCntry))
                        return 2;
                    
                    // For AP FCT quote, customer currency should match the payer's currency
                    if (custCurrency == null || payerCurrency == null || !payerCurrency.equalsIgnoreCase(custCurrency))
                        return 3;
                }
                else {
                    if (!custSalesOrg.equalsIgnoreCase(payerSalesOrg))
                        return 1;
                }
            }
            
            if (reseller != null) {
                String resellerCntry = StringUtils.trimToEmpty(reseller.getCountry());
                String resellerSalesOrg = StringUtils.trimToEmpty(reseller.getSapSalesOrgCode());
                String resellerCurrency = StringUtils.trimToEmpty(reseller.getCurrencyCode());
                
                if (QuoteConstants.GEO_AP.equalsIgnoreCase(geo)) {
                    if (!custCntry.equalsIgnoreCase(resellerCntry))
                        return 2;
                    
                    // For AP FCT quote, customer currency should match the payer's currency
                    if (custCurrency == null || resellerCurrency == null || !resellerCurrency.equalsIgnoreCase(custCurrency))
                        return 3;
                }
                else {
                    if (!custSalesOrg.equalsIgnoreCase(resellerSalesOrg))
                        return 1;
                }
            }
        }
        
        return 0;
    }
    
    public Map validateUpdateQuotePartners() {
        return validateResult;
    }
    
    public Map validateSubmitCopiedApprvdBid() {
        return validateResult;
    }
    
    public boolean isExpirationDateBeforeToday() {

        Date expDate = quote.getQuoteHeader().getQuoteExpDate();
        if (expDate == null)
            return false;
        
        Date today = getTodayInQuoteTimeZone();
        Date dueTime = com.ibm.dsw.quote.base.util.DateHelper.getTimeOfMidnight(expDate);

        return dueTime.before(today);
    }
    
    public boolean validateNewContract() {
        return true;
    }

	@Override
	public Map validateTou(boolean orderFlag) throws QuoteException {
		QuoteHeader header = quote.getQuoteHeader();
    	//IBM Cloud Service Agreement will never validate for all products.
    	if(header.getSaasTermCondCatFlag() == 2){
    		return validateResult;
    	}
    	/*If the quote qualifies for standalone SaaS general terms,
    	 * need specify the terms and conditions covering this order.
    	 * */
    	boolean touRadioChecked = true;
        String type = header.getLob().getCode();
        //remove the !header.isChannelQuote() conditions for 15.2 CSA requirement should support Channel CSA quote
        if (QuoteConstants.LOB_PAE.equals(type) && header.isOnlySaaSParts() && header.getSaasTermCondCatFlag() <1){
        	validateResult.put(QuoteCapabilityProcess.SPECIFY_TERMS_CONDITIONS_COVERING, Boolean.TRUE);
        }
        
        try {
			QuoteHeaderFactory.singleton().updateTouFlag(header.getWebQuoteNum(), 0, "", "", "", "", 1, "");
		} catch (TopazException e1) {
			logContext.debug(this, e1.getMessage());
		}
		
        /*TOU validation:check that all SaaS items are covered by a ToU. 
         *  If any line item is not associated with a ToU
         *  If a quote is PGS special bid quote, will be passed*/
        //if(header.isPGSQuote() && header.getSpeclBidFlag() == 1 && isInPGS()){
        if(!orderFlag && header.isPGSQuote() && header.getSpeclBidFlag() == 1 && isInPGS()){
        }else{
        	List<QuoteLineItem> itemList;
			try {
				itemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(quote.getQuoteHeader().getWebQuoteNum());
				StringBuilder missParts = new StringBuilder();
	            Iterator it = itemList.iterator();
	            while(it.hasNext()){
	            	QuoteLineItem qi = (QuoteLineItem)it.next();
	            	if(null != qi && qi.isSaasPart()){
	            		if(null == qi.getTouName()){
            				missParts.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;(").append(qi.getPartNum()).append(")[").append(qi.getPartDesc()).append("]</p>");
            			}
	            		if(orderFlag &&( null == qi.getAmendedTouFlag() && null == qi.getAmendedTouFlagB())){
	            			touRadioChecked = false;
	            		}
	            	}
	            }
	            if(missParts.length()>0){
	            	validateResult.put(QuoteCapabilityProcess.SUBMIT_PART_LOST_TOU_MSG, missParts.toString());
	            }
			} catch (TopazException e) {
				logContext.debug(this, e.getMessage());
			}
        }
        
        if(!isInPGS() && !touRadioChecked){
        	validateResult.put(QuoteCapabilityProcess.TOU_AMENDMENT_BLANK_MSG, Boolean.TRUE);
        }
        return validateResult;
	}
}
