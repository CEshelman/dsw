package com.ibm.dsw.quote.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.TimeOffset;
import com.ibm.dsw.quote.appcache.domain.TimeOffsetFactory;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.JustSection;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.TacticCode;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.util.TuscanySWVNRuleHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.util.date.PartTypeChecker;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>QuoteValidationRule</code> class is to execute validation for
 * "submit as final", "submit for approval" or "order".
 *
 *
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: Apr 25, 2007
 */
public abstract class QuoteValidationRule {

    protected LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Quote quote;

    protected List<String> ppConfigurationValidatorKeys = new LinkedList<String>();

    protected List<String> quoteLineItemValidatorKeys = new LinkedList<String>();

    protected Map validateResult = new HashMap();

    protected QuoteUserSession user;

    protected Cookie cookie;

    protected QuoteValidationRule(Quote quote, QuoteUserSession user, Cookie cookie) {
        this.user = user;
        this.quote = quote;
        this.cookie = cookie;
    }

    public static QuoteValidationRule createRule(QuoteUserSession user, Quote quote, Cookie cookie) throws Exception {
        if (quote == null) {
            throw new IllegalStateException();
        }
        if (QuoteConstants.QUOTE_TYPE_SALES.equals(quote.getQuoteHeader().getQuoteTypeCode())) {
            return new SalesQuoteValidationRule(user, quote, cookie);
        } else
            return new RenewalQuoteValidationRule(quote, user, cookie);

    }

    public abstract Map validateSubmissionAsFinal() throws QuoteException;

    public abstract Map validateSubmissionForApproval() throws QuoteException;

    public abstract Map validateOrder() throws QuoteException;
    
    public abstract Map getDraftQuoteActionButtonsRule(QuoteUserSession salesRep);

    public abstract Map getSubmittedQuoteActionButtonsRule();

    protected boolean validateSpecialJustText()
    {
        SpecialBidInfo bidInfo = quote.getSpecialBidInfo();
        List secList = bidInfo.getJustSections();
        if ( !StringUtils.isEmpty(quote.getSpecialBidInfo().getSpBidJustText()) )
        {
            logContext.debug(this, "validate spb return false here");
            return false;
        }
        if ( secList.size() == 0 )
        {
            return true;
        }
        for ( int i = 0; i < secList.size(); i++ )
        {
            JustSection sec = (JustSection)secList.get(i);
            if ( !sec.isEmpty() )
            {
                return false;
            }
        }
        return true;
    }
    
    protected void validateGrwthDlgtn(){
    	if(GrowthDelegationUtil.isMissingYty(quote)){
    		validateResult.put(QuoteCapabilityProcess.GRWTH_DLGTN_MISSING_YTY, Boolean.TRUE);
    	}
    	
    	if(GrowthDelegationUtil.hasPartsExceedRsvpPrice(quote)){
    		validateResult.put(QuoteCapabilityProcess.MSG_EXCEED_RSVP_PRICE, Boolean.TRUE);
    	}
    }

    /**
     * @param result
     * @param bidInfo
     */
    protected void validateSpecialBidInfo() {

        SpecialBidInfo bidInfo = quote.getSpecialBidInfo();
        QuoteHeader header = quote.getQuoteHeader();
        String geo = header.getCountry().getSpecialBidAreaCode().trim();
        if ( (header.isPAQuote() || header.isPAEQuote() || header.isOEMQuote() || header.isFCTQuote() ||header.isSSPQuote())
                && (bidInfo.isTermsAndCondsChg()        || quote.getQuoteHeader().isTriggerTC() )
                && StringUtils.isEmpty(bidInfo.getCreditJustText())) {
            validateResult.put(QuoteCapabilityProcess.SPECIAL_BID_EXP_TEXT_NOT_INPUT, Boolean.TRUE);
        }
        if (StringUtils.isEmpty(bidInfo.getSpBidRgn())) {
            validateResult.put(QuoteCapabilityProcess.SPECIAL_BID_REGION_NOT_INPUT, Boolean.TRUE);
        }
        if (StringUtils.isEmpty(bidInfo.getSpBidDist())) {
            validateResult.put(QuoteCapabilityProcess.SPECIAL_BID_DISTRICT_NOT_INPUT, Boolean.TRUE);
        }
        if ((geo.equals(QuoteConstants.GEO_AP) || geo.equals(QuoteConstants.GEO_EMEA))
                    && StringUtils.isEmpty(bidInfo.getSpBidCustIndustryCode())
                    && !QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(user.getAudienceCode())) {
            validateResult.put(QuoteCapabilityProcess.SPECIAL_BID_CUST_IND_SEGMENT_NOT_INPUT, Boolean.TRUE);
        }
        if (QuoteConstants.LOB_FCT.equals(quote.getQuoteHeader().getLob().getCode())
                && StringUtils.isEmpty(bidInfo.getSpBidType())) {
            validateResult.put(QuoteCapabilityProcess.SPECIAL_BID_TYPE_NOT_INPUT, Boolean.TRUE);
        }
        //check sb just text

        if ( validateSpecialJustText() )
        {
            validateResult.put(QuoteCapabilityProcess.SPECIAL_BIT_JUST_TEXT_NOT_INPUT, Boolean.TRUE);
        }
        
        if ( validateSpecialJustText() )
        {
            validateResult.put(QuoteCapabilityProcess.SPECIAL_BIT_JUST_TEXT_NOT_INPUT, Boolean.TRUE);
        }
        
    	

        String lob = header.getLob().getCode();
        String cntryCode = header.getCountry().getCode3();
        String isMigration = header.isMigration() ? "1" : "0";
        if (!this.isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes())) {
            validateResult.put(QuoteCapabilityProcess.SPEC_BID_NOT_PERMITTED, Boolean.TRUE);
        }

        if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc())) {
            if (!header.isDistribtrToBeDtrmndFlag() && !header.isSingleTierNoDistributorFlag()) {
                String custCurrency = quote.getCustomer() == null ? null : quote.getCustomer().getCurrencyCode();
                String ptnrCurrency = quote.getPayer() == null ? null : StringUtils.trimToEmpty(quote.getPayer()
                        .getCurrencyCode());
                if (custCurrency == null || !custCurrency.equalsIgnoreCase(ptnrCurrency))
                    validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_CURRENCY_MISMATCH, Boolean.TRUE);
            }
        }

        //eBiz ticket JKEY-8EMHQB, add payer customer currency check for FCT sb
        if (header.isFCTQuote() && (quote.getPayer() != null)) {
        	String custCurrency = quote.getCustomer() == null ? null : quote.getCustomer().getCurrencyCode();
        	String ptnrCurrency = StringUtils.trimToEmpty(quote.getPayer().getCurrencyCode());
        	if (custCurrency == null || !custCurrency.equalsIgnoreCase(ptnrCurrency)){
        		validateResult.put(QuoteCapabilityProcess.CUST_PAYER_CURRENCY_MISMATCH, Boolean.TRUE);
        	}
        }
    }
    
    protected boolean isQuoteApplyLevel0(QuoteUserSession user, Quote quote, Cookie cookie)
    {
    	QuoteHeader header = quote.getQuoteHeader();
    	if ( header.getSpeclBidFlag() != 1 || !QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(user.getAudienceCode()) 
    			|| quote.getSpecialBidReason() == null || quote.getSpecialBidInfo().isTermsAndCondsChg() )
    	{
    		logContext.debug(this, "check level 0 failed for not special bid, not BP, no speical bid reason, tnc flag is checked");
    		return false;
    	}
    	List reasonList = quote.getSpecialBidReason().getSpecialBidReasonList();
    	if ( reasonList == null || reasonList.size() == 0 )
    	{
    		logContext.debug(this, "check level 0 failed for reason list is empty");
    		return false;
    	}
    	for(int i = 0; i < reasonList.size();i++ ){
        	String reasonCode = (String)reasonList.get(i);

        	if(!QuoteConstants.SpecialBidReason.PART_GROUP_REQUIRE_SPECIAL_BID.equalsIgnoreCase(reasonCode)
        			&& !QuoteConstants.SpecialBidReason.CPQ_EXCEP.equalsIgnoreCase(reasonCode)
        			&& !QuoteConstants.SpecialBidReason.CONFGRTN_OVRRDN.equalsIgnoreCase(reasonCode) ){
        		logContext.debug(this, "check level 0 failed for reason: " + reasonCode);
        		return false;
        		
        	}
        }
    	if ( !reasonList.contains(QuoteConstants.SpecialBidReason.PART_GROUP_REQUIRE_SPECIAL_BID) )
    	{
    		return true;
    	}
    	//begin check level 0 flag for part group
    	return checkPartGroupLevel0(quote);
    }
    
    protected boolean checkPartGroupLevel0(Quote quote)
    {
    	List list = quote.getLineItemList();
    	for ( int i = 0; i < list.size(); i++ )
    	{
    		QuoteLineItem item = (QuoteLineItem)list.get(i);
    		List sbPartGroups = item.getSpBidPartGroups();
    		if ( sbPartGroups == null || sbPartGroups.size() == 0 )
    		{
    			continue;
    		}
    		if ( !item.isSaasPart() && !item.isApplncPart() && !item.isMonthlySoftwarePart() )
    		{
    			logContext.debug(this, "check level 0 failed for part is not saas or appliance or monthly software");
    			return false;
    		}
    		for ( int j = 0; j < sbPartGroups.size(); j++ )
    		{
    			QuoteLineItem.SpBidPartGroup grp = (QuoteLineItem.SpBidPartGroup)sbPartGroups.get(j);
    			if ( !grp.getLvl0ConfFlag() )
    			{
    				logContext.debug(this, "check level 0 failed for part group conf vald flag is false: " + grp.getSpBidGroupName());
    				return false;
    			}
    		}
    	}
    	return true;
    }
    
    public boolean isSubmitPGSLevel0SPBid(QuoteUserSession user, Quote quote, Cookie cookie)throws QuoteException
    {
    	logContext.debug(this, "check level 0 specil bid has saas line item: " + quote.getQuoteHeader().hasSaaSLineItem());
    	boolean retFlag = this.isSubmitPGSQuoteForSaas(user, quote, cookie) || isSubmitPGSQuoteForAppliance(user, quote, cookie);
    	logContext.debug(this, "check level 0 result: " + retFlag + ", quote num:" + quote.getQuoteHeader().getWebQuoteNum());
    	return retFlag;
    }
    
    //for PGS quotes, only three reason codes are allowed to submit special bid; work Item: 252130:Influencer & VAR: Allow PGS special bids to be submitted
    //by IBM sales rep regardless of special bid reasons.
    protected boolean isSubmitPGSQuoteForSaas(QuoteUserSession user, Quote quote, Cookie cookie)throws QuoteException{
//    	if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(user.getAudienceCode()) &&
//        		quote.getSpecialBidReason() != null){
//            for(int i = 0; i < quote.getSpecialBidReason().getSpecialBidReasonList().size();i++ ){
//            	String reasonCode = (String)quote.getSpecialBidReason().getSpecialBidReasonList().get(i);
//
//            	if(!QuoteConstants.SpecialBidReason.PART_GROUP_REQUIRE_SPECIAL_BID.equalsIgnoreCase(reasonCode)
//            			&& !QuoteConstants.SpecialBidReason.CPQ_EXCEP.equalsIgnoreCase(reasonCode)
//            			&& !QuoteConstants.SpecialBidReason.CONFGRTN_OVRRDN.equalsIgnoreCase(reasonCode) ){
//            		return false;
//            		
//            	}
//            }
//        }
//    	return true;
    	if ( !quote.getQuoteHeader().isOnlySaaSParts() )
    	{
    		return false;
    	}
    	return isQuoteApplyLevel0(user, quote, cookie);
    }
    
    protected boolean isSubmitPGSQuoteForAppliance(QuoteUserSession user, Quote quote, Cookie cookie)throws QuoteException{
    	if ( quote.getQuoteHeader().hasSaaSLineItem() )
    	{
    		return false;
    	}
    	return isQuoteApplyLevel0(user, quote, cookie);
    }

    //if the distribution channel is 'J'
    protected boolean isChannelJ(){
        QuoteHeader header = quote.getQuoteHeader();

        String distChnl = header.getSapDistribtnChnlCode();
        return QuoteConstants.DIST_CHNL_DISTRIBUTOR.equals(distChnl);
    }

    // if the distribution channel is 'H'
    protected boolean isChannelH() {
        String distChnl = quote.getQuoteHeader().getSapDistribtnChnlCode();
        return QuoteConstants.DIST_CHNL_HOUSE_ACCOUNT.equals(distChnl);
    }

    //if the fulfillment source is channel
    protected boolean isFulfillmentSrcChannel(){
        QuoteHeader header = quote.getQuoteHeader();

        String fulfillmentSrc = header.getFulfillmentSrc();
        if(StringUtils.isNotEmpty(fulfillmentSrc)
             && fulfillmentSrc.equals(QuoteConstants.FULFILLMENT_CHANNEL)){
            return true;
        }

        return false;
    }
    /**
     * @param header
     * @return
     */
    protected boolean isFulfillmentSrcSet() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;
        QuoteHeader header = quote.getQuoteHeader();

        if(isFulfillmentSrcChannel()
                || isFulfillmentSrcDirect()
                || QuoteConstants.FULFILLMENT_NOT_SPECIFIED.equalsIgnoreCase(header.getFulfillmentSrc())){
            return true;
        }else{
            return false;
        }
    }

    protected boolean checkPAQuoteHasPACust() {
        if (quote == null || quote.getQuoteHeader() == null || quote.getCustomer() == null)
            return false;

        QuoteHeader header = quote.getQuoteHeader();
        Customer cust = quote.getCustomer();

        if (header.isPAQuote() && hasNewCust())
            return cust.isPAWebCustomer();
        else
            return true;
    }

    /**
     * @param contract
     * @return
     */
    protected boolean validateContactInfo() {
        if (quote == null)
            return false;
        if (quote.getContactList() != null && quote.getContactList().size() > 0) {
            QuoteContact contact = (QuoteContact) quote.getContactList().get(0);
            if (StringUtils.isBlank(contact.getCntEmailAdr())) {
                validateResult.put(QuoteCapabilityProcess.CONTACT_EMAIL_ADR_BLANK, Boolean.TRUE);
            }
            if (StringUtils.isBlank(contact.getCntPhoneNumFull())) {
                validateResult.put(QuoteCapabilityProcess.CONTACT_PHONE_NUM_BLANK, Boolean.TRUE);
            }
            if (StringUtils.isBlank(contact.getCntFirstName())) {
                validateResult.put(QuoteCapabilityProcess.CONTACT_FIRST_NAME_BLANK, Boolean.TRUE);
            }
            if (StringUtils.isBlank(contact.getCntLastName())) {
                validateResult.put(QuoteCapabilityProcess.CONTACT_LAST_NAME_BLANK, Boolean.TRUE);
            }
            return true;
        } else
            return false;
    }

    /**
     * @param customer
     * @return If a contract was selected for an existing customer, check if the
     *         contract is active
     */
    protected boolean isActiveIfContractSelected() {
        // if quote or quote header is null, return false
        if (quote == null || quote.getQuoteHeader() == null)
            return false;

        // if no existing contract is selected or no existing customer is
        // selected, return true
        QuoteHeader header = quote.getQuoteHeader();
        if (!hasExistCust() || StringUtils.isBlank(header.getContractNum()))
            return true;

        // if a contract was selected for an existing customer, check the active
        // flag of the contract.
        Customer cust = quote.getCustomer();
        if (cust != null) {
            if (cust.hasContract())
                return cust.hasActiveContract();
            else
                return true;
        } else
            return false;
    }
    
    /**
     * @return If additional email address has a value, check if the length of additional addresses plus creator address
     *         exceeds 80
     */
    protected boolean isExceedEmailMaxLength() {
        // if quote or quote header is null, return false
        if (quote == null || quote.getQuoteHeader() == null)
            return false;

        // if no additional email address is provided return false
        QuoteHeader header = quote.getQuoteHeader();
        if (StringUtils.isBlank(header.getAddtnlCntEmailAdr()))
            return false;

        // check the length
        String additnEmail = CommonServiceUtil.genAddiEmailList(header, null, user);
        if(additnEmail.length() > QuoteConstants.EMAIL_ADDRESS_MAX_LENGTH)
        	return true;
        else
        	return false;

    }

    protected boolean hasActiveContract() {
        if (quote == null || quote.getCustomer() == null)
            return false;
        return quote.getCustomer().hasActiveContract();
    }

    // An existing contract is selected, and the contract is not active
    protected boolean isSelectedContractNotActive() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;

        if (StringUtils.isNotBlank(quote.getQuoteHeader().getContractNum())) {
            Customer cust = quote.getCustomer();
            return (cust != null && !cust.hasActiveContract());
        } else
            return false;
    }

    protected boolean isCtrctPriceLevelSetUp() {
        Contract ctrct = this.getQuoteContract();
        return (ctrct != null && StringUtils.isNotBlank(ctrct.getVolDiscLevelCode()));
    }

    protected boolean hasExistCust() {
        if (quote != null && quote.getQuoteHeader() != null
                && StringUtils.isNotBlank(quote.getQuoteHeader().getSoldToCustNum()))
            return true;
        else
            return false;
    }

    protected boolean hasNewCust() {
        if (quote != null && quote.getQuoteHeader() != null
                && StringUtils.isBlank(quote.getQuoteHeader().getSoldToCustNum())
                && quote.getQuoteHeader().getWebCustId() > 0)
            return true;
        else
            return false;
    }

    /**
     * @param customer
     * @return
     */
    protected boolean isCustomerSelected() {
        if (hasExistCust() || hasNewCust()) {
            Customer cust = quote.getCustomer();
            if (cust == null)
                return false;
            else
                return true;
        } else
            return false;
    }

    protected boolean isQuoteClassfctnCodeSelected() {
        QuoteHeader header = quote.getQuoteHeader();
        if (header.isFCTQuote() && StringUtils.isBlank(header.getQuoteClassfctnCode()))
            return false;
        else
            return true;
    }

    protected boolean isQuoteOemAgreementTypeSelected() {
        QuoteHeader header = quote.getQuoteHeader();
        if (header.isOEMQuote() && StringUtils.isBlank(header.getOrdgMethodCode()))
            return false;
        else
            return true;
    }

    protected boolean isQuoteOemBidTypeSelected() {
        QuoteHeader header = quote.getQuoteHeader();
        if (header.isOEMQuote() && header.isSalesQuote() && ButtonDisplayRuleFactory.singleton().isDisplayOemBidType(header)
                && (StringUtils.isBlank(String.valueOf(header.getOemBidType())) || header.getOemBidType() == 0))
            return false;
        else
            return true;
    }

    // If quote has unrelated prorated maintenance parts
    // Limit the expiration date to within the last day of the month of the earliest start date
    protected Integer getMaxExpDaysForUnrelatedMaintenanceParts() {

        Integer maxExpDays = null;
        Date earliestOverridenStartDate = null;
        QuoteHeader header = quote.getQuoteHeader();

        // Only applies to PA/PAE/FCT special bid
        if ((header.isPAQuote() || header.isPAEQuote() || header.isFCTQuote() || header.isOEMQuote() || header.isSSPQuote())
                && header.getSpeclBidFlag() == 1) {
            PartTypeChecker checker = new PartTypeChecker(quote);
            checker.checkType();

            List masterLineItems = quote.getMasterSoftwareLineItems();
            Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
            if (masterLineItems != null) {
                for (int i = 0; i < masterLineItems.size(); i++) {
                    QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);
                    PartDisplayAttr attr = item.getPartDispAttr();

                    if (attr.isUnRelatedMaint()
                            && (item.isMaintCoverageProrated() || item.getProrateFlag() )) {
                        Date itemStartDate = item.getMaintStartDate();
                        boolean itemOverridenFlag = item.getStartDtOvrrdFlg()|| item.getEndDtOvrrdFlg();

                        if (itemOverridenFlag && itemStartDate != null
                                // of line items not in the past from today
                                && !itemStartDate.before(today)) {
                        	// If item has overriden flag, use earliest 'overridden' start date
                        	if (earliestOverridenStartDate == null || itemStartDate.before(earliestOverridenStartDate)) {
                        		earliestOverridenStartDate = itemStartDate;
                        }
                    }
                }
            }}

            if (earliestOverridenStartDate != null) {
                //Take the last day of the month
                Date maxExpDate = DateHelper.extendToMonthEnd(earliestOverridenStartDate);

                logContext.debug(this, "Max expiration date for unrelated prorated parts is " + maxExpDate);
                int days = DateHelper.dateDifferenceFromNow(maxExpDate);
                // Set to zero if the last day of the month of the earliest start date is before today
                if (days < 0)
                    days = 0;
                maxExpDays = new Integer(days);
            }
        }
        return maxExpDays;
    }

    // If quote has related prorated maintenance parts
    // Limit the expiration date to within the last day of submit month
    protected Integer getMaxExpDaysForRelatedMaintenanceParts() {

        Integer maxExpDays = null;
        QuoteHeader header = quote.getQuoteHeader();

        if ((header.isPAQuote() || header.isPAEQuote() || header.isFCTQuote() || header.isOEMQuote() || header.isSSPQuote())
                && header.getSpeclBidFlag() == 1) {
            boolean hasProratedRelatedMaint = false;
            PartTypeChecker checker = new PartTypeChecker(quote);
            checker.checkType();

            List masterLineItems = quote.getMasterSoftwareLineItems();
            if (masterLineItems != null) {
                for (int i = 0; i < masterLineItems.size(); i++) {
                    QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);
                    PartDisplayAttr attr = item.getPartDispAttr();

                    if (attr.isRelatedMaint()
                            && (item.isMaintCoverageProrated() || item.getProrateFlag())) {
                        hasProratedRelatedMaint = true;
                        break;
                    }
                }
            }

            if (hasProratedRelatedMaint) {
                Date now = Calendar.getInstance().getTime();
                int days = DateHelper.dateDifferenceFromNow(DateHelper.extendToMonthEnd(now));
                maxExpDays = new Integer(days);
            }
        }
        return maxExpDays;
    }

    protected boolean checkQuoteExpirationDate(int maxExpDays) {
        Date expireDate = quote.getQuoteHeader().getQuoteExpDate();

        if (expireDate == null) {
            return false;
        }

        Calendar curr = Calendar.getInstance();
        //Check to make sure the quot expiration date is not before today's date
        Date now = curr.getTime();
        //Remove time from today's date to compare date only, quote expiration date can be today's date for FCT to PA RQs
        Date currDate = DateUtils.truncate(now, Calendar.DATE);

        if (currDate.after(expireDate)) {
            return false;
        } else {
            curr.add(Calendar.DATE, maxExpDays);
            Date maxDate = curr.getTime();

            if (maxDate.before(expireDate)) {
                return false;
            }
        }

        return true;
    }

    /**
     */
    protected void validateQuoteExpiredDate() {
        if (quote == null || quote.getQuoteHeader() == null)
            return;

        QuoteHeader qh = quote.getQuoteHeader();
        int maxExpDays = qh.getQuoteExpDays();

        if (maxExpDays < 0) {
            return;
        }
        else {
            boolean valid = checkQuoteExpirationDate(maxExpDays);
            if (!valid) {
                validateResult.put(QuoteCapabilityProcess.EXPIRED_DATE_NOT_WITHIN_ACTIVIE_DAYS,
                        String.valueOf(maxExpDays));
            }
        }
    }

    /**
     * @param quoteHeader
     * @return
     */
    protected boolean getSpecialBidFlag(QuoteHeader quoteHeader) {
        return quoteHeader.getSpeclBidFlag() == 1;
    }

    /**
     * @param header
     * @return
     */
    protected boolean isBusOrgSelected() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;

        if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(this.user.getAudienceCode())){
        	return true;
        }

        QuoteHeader header = quote.getQuoteHeader();
        if (StringUtils.isNotBlank(header.getBusOrgCode())) {
            return true;
        } else if (StringUtils.isNotBlank(QuoteCookie.getBusinessOrg(this.cookie))) {
            // persist bus org from cookie to DB
            String busOrgCode = QuoteCookie.getBusinessOrg(this.cookie);
            header.setBusOrgCode(busOrgCode);

            QuoteProcess quoteProcess = null;
            try {
                quoteProcess = QuoteProcessFactory.singleton().create();
                StringBuffer tacticCodes = new StringBuffer();
                if (header.getTacticCodes() != null) {
                    List codeList = header.getTacticCodes();
                    if (codeList != null && codeList.size() > 0) {
                        tacticCodes.append(((TacticCode) codeList.get(0)).getTacticCode());
                        for (int i = 1; i < codeList.size(); i++) {
                            tacticCodes.append("," + ((TacticCode) codeList.get(i)).getTacticCode());
                        }
                    }
                }
                logContext.debug(this, "begin to persist bus org info from cookie after submission validate...");
                quoteProcess.updateQuoteHeaderSalesInfoTab(header.getCreatorId(), header.getQuoteExpDate(), header
                        .getQuoteTitle(), header.getQuoteDscr(), busOrgCode, header.getOpprtntyNum(), header
                        .getExemptnCode(), header.getUpsideTrendTowardsPurch(), header.getRenwlQuoteSalesOddsOode(),
                        tacticCodes.toString(), header.getSalesComments(), header.getQuoteClassfctnCode(), header
                                .getSalesStageCode(), header.getCustReasCode(), header.getQuoteStartDate(),
                                header.getOrdgMethodCode(), header.getBlockRnwlReminder(), header.getPymTermsDays(), header.getOemBidType(), header.getEstmtdOrdDate(), header.getCustReqstArrivlDate(),header.getSspType());
            } catch (QuoteException e) {
                logContext.error(this, "error when persist bus org info from cookie to db");
            }
            logContext.debug(this, "complete persist bus org info from cookie after submission validate...");
            return true;
        }
        return false;
    }

    /**
     * @param header
     * @return
     */
    protected boolean isOppNumOrExempCodeSet() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;
        QuoteHeader header = quote.getQuoteHeader();
        String exemptnCode = header.getExemptnCode();
        if (StringUtils.isNotEmpty(exemptnCode)) {
            try {
                validateAndAdjustExmptnCode(exemptnCode);
            } catch (QuoteException e) {
            	 logContext.error(this, "error when executing validateAndAdjustExmptnCode");
            }
        }
        return StringUtils.isNotEmpty(header.getOpprtntyNum()) || StringUtils.isNotEmpty(header.getExemptnCode());
    }

    /**
     * @param exemptnCode
     * @throws QuoteException
     *
     */
    protected void validateAndAdjustExmptnCode(String exemptnCode) throws QuoteException {
        QuoteHeader qh = quote.getQuoteHeader();
        QuoteCapabilityProcess process = QuoteCapabilityProcessFactory.singleton().create();
        boolean isExemptionCodeUpdated = false;
        if (!exemptnCode.equals(DraftQuoteConstants.EXEMPTNCODE_80)) {
    		if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(qh.getFulfillmentSrc()) && (this.isPA() || this.isPAE() || this.isOEM() || this.isSSP())) {
	        	if (!exemptnCode.equals(DraftQuoteConstants.EXEMPTNCODE_30)) {
	        		exemptnCode = DraftQuoteConstants.EXEMPTNCODE_30;
	                isExemptionCodeUpdated = true;
	        	}
	        } else if ((this.isPPSS() || (this.isPA() || this.isPAE() || this.isOEM() || this.isSSP()))
	                && QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(qh.getFulfillmentSrc())
	                && qh.getContainLineItemRevnStrm() == 1) {
	        	if (!exemptnCode.equals(DraftQuoteConstants.EXEMPTNCODE_60)) {
	        		exemptnCode = DraftQuoteConstants.EXEMPTNCODE_60;
	                isExemptionCodeUpdated = true;
	        	}
	        } else if((this.isPPSS() || (this.isPA() || this.isPAE() || this.isOEM() || this.isSSP()))
	        		&& ((qh.getQuotePriceTot() == DraftQuoteConstants.QUOTE_TOTAL_VALUE_ZERO && !qh.hasSaaSLineItem())
	        				|| qh.isAllPartsHasMediaAttr())) {
	        	if (!exemptnCode.equals(DraftQuoteConstants.EXEMPTNCODE_40)) {
	        		exemptnCode = DraftQuoteConstants.EXEMPTNCODE_40;
	                isExemptionCodeUpdated = true;
	        	}
	        }  else if ((this.isPPSS() || (this.isPA() || this.isPAE() || this.isOEM() || this.isSSP()))
	        		&& qh.getTotalPriceInUSD()< DraftQuoteConstants.QUOTE_TOTAL_VALUE_UPPER_LIMIT) {
	        	if (!exemptnCode.equals(DraftQuoteConstants.EXEMPTNCODE_50)) {
	        		exemptnCode = DraftQuoteConstants.EXEMPTNCODE_50;
	                isExemptionCodeUpdated = true;
	        	}
	        } else {
	        	if (!exemptnCode.equals(DraftQuoteConstants.EXEMPTNCODE_70)) {
	        		exemptnCode = DraftQuoteConstants.EXEMPTNCODE_70;
	                isExemptionCodeUpdated = true;
	        	}
	        }
        }

        if (isExemptionCodeUpdated) {
            QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
            QuoteHeader header = quote.getQuoteHeader();
            StringBuffer tacticCodes = new StringBuffer();
            if (header.getTacticCodes() != null) {
                List codeList = header.getTacticCodes();
                if (codeList != null && codeList.size() > 0) {
                    tacticCodes.append(((TacticCode) codeList.get(0)).getTacticCode());
                    for (int i = 1; i < codeList.size(); i++) {
                        tacticCodes.append("," + ((TacticCode) codeList.get(i)).getTacticCode());
                    }
                }
            }
            logContext.debug(this, "begin to update exmeption code after submission validate...");
            quoteProcess.updateQuoteHeaderSalesInfoTab(header.getCreatorId(), header.getQuoteExpDate(), header
                    .getQuoteTitle(), header.getQuoteDscr(), header.getBusOrgCode(), header.getOpprtntyNum(),
                    exemptnCode, header.getUpsideTrendTowardsPurch(), header.getRenwlQuoteSalesOddsOode(), tacticCodes
                            .toString(), header.getSalesComments(), header.getQuoteClassfctnCode(), header
                            .getSalesStageCode(), header.getCustReasCode(), header.getQuoteStartDate(),
                            header.getOrdgMethodCode(), header.getBlockRnwlReminder(), header.getPymTermsDays(), header.getOemBidType(), header.getEstmtdOrdDate(), header.getCustReqstArrivlDate(),header.getSspType());
            logContext.debug(this, "complete update exmeption code after submission validate...");
        }
    }

    public boolean isPA() {
        return quote.getQuoteHeader().isPAQuote();
    }

    public boolean isPAE() {
        return quote.getQuoteHeader().isPAEQuote();
    }

    public boolean isFCT() {
        return quote.getQuoteHeader().isFCTQuote();
    }

    public boolean isPPSS() {
        return quote.getQuoteHeader().isPPSSQuote();
    }
    
    public boolean isOEM() {
        return quote.getQuoteHeader().isOEMQuote();
    }
    
    public boolean isSSP() {
        return quote.getQuoteHeader().isSSPQuote();
    }
    /**
     * @param quoteHeader
     * @return
     */
    protected boolean isBriefTitleFilled() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        if(quoteHeader.getQuoteTypeCode().equals(QuoteConstants.QUOTE_TYPE_SALES)) {
            return StringUtils.isNotEmpty(quoteHeader.getQuoteTitle());
        }
        return true;
    }

    /**
     * @param quoteHeader
     * @return
     */
    protected boolean isFulfillmentSrcDirect() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        return quoteHeader.getFulfillmentSrc() != null
                && QuoteConstants.FULFILLMENT_DIRECT.equals(quoteHeader.getFulfillmentSrc());
    }

    /**
     * @param lineItemList
     * @param newParam
     * @return
     */
    protected void verifyEveryPart(Map result) {

        if (quote != null) {
            // for ELA quote, if there is no line items and attachments, show validation message.
            if (quote.getQuoteHeader().isELAQuote()) {
                if ((quote.getLineItemList() == null || quote.getLineItemList().size() == 0)
                        && quote.getSpecialBidInfo().getAttachmentCount() == 0) {
                    result.put(QuoteCapabilityProcess.ELA_HAS_NO_LINE_ITM_OR_SPRDSHT, Boolean.TRUE);
                }
            }
            else {
                if ( quote.getLineItemList() == null ||quote.getLineItemList().size() == 0) {
                    //fix PL DBOT-85V2LR allow no parts bid submission when "Initiate special bid approval" checkbox checked
                    if (!(quote.getSpecialBidInfo()!=null &&
                            (quote.getSpecialBidInfo().isTermsAndCondsChg() || quote.getSpecialBidInfo().isInitSpeclBidApprFlag()))) {
                        result.put(QuoteCapabilityProcess.PART_QTY_NOT_SET, Boolean.TRUE);
                        return;
                    }
                }
            }
        } else {
            result.put(QuoteCapabilityProcess.PART_QTY_NOT_SET, Boolean.TRUE);
            return;
        }

        List lineItemList = quote.getLineItemList();
        Iterator iterator = lineItemList.iterator();
        while (iterator.hasNext()) {
            QuoteLineItem item = (QuoteLineItem) iterator.next();
            // Every part must have a quantity set
            // Part is a PVU part, and the quantity is 1
            // Every part must be active
            // Every part must have a price
            // if part is SaaS part and is not noqty part and has no qty value, add the error msg
            if(item.isSaasPart() || item.isMonthlySoftwarePart()){
            	if(!PartPriceSaaSPartConfigFactory.singleton().isNoQty(item)
            		&& item.getPartQty() == null){
            		result.put(QuoteCapabilityProcess.PART_QTY_NOT_SET, Boolean.TRUE);
            	}
            }else if (item.getPartQty() == null ) {
                result.put(QuoteCapabilityProcess.PART_QTY_NOT_SET, Boolean.TRUE);
            }
            // ignore the SaaS part PVU value validation
            if (!item.isSaasPart() && !item.isMonthlySoftwarePart() && !item.isApplncRenewal() && item.isPvuPart() && item.getPartQty() != null && item.getPartQty().intValue() == 1) {
                result.put(QuoteCapabilityProcess.PVU_PART_QTY_NOT_RIGHT, Boolean.TRUE);
            }
            // If it is obsolete part, entitled price and overridden price are required
            // ignore the SaaS part validation, need to update for SaaS obsolete parts to input
            //the error msg when the part has no bid unit price (need to judge the SaaS part type and terisd model)
            if ((!item.isObsoletePart() && item.getLocalUnitProratedPrc() == null && !item.isSaasPart() && !item.isMonthlySoftwarePart())
                    || (item.isObsoletePart()
                            && ((!item.isHasEolPrice() && item.getManualProratedLclUnitPriceFlag() == 0)
                                    || item.getOverrideUnitPrc() == null))) {
                result.put(QuoteCapabilityProcess.PART_NOT_HAS_PRICE, Boolean.TRUE);
            }
            // for EOL part, only Z8 status can be added to the quote
            if (item.isObsoletePart() && !item.canPartBeReactivated()) {
                result.put(QuoteCapabilityProcess.HAS_INAVLID_OBSOLETE_PARTS, Boolean.TRUE);
            }

            //for appliance upgrade part, appliance renewal part, appliance related software part
            //if they are not associated with appliance main part then need to check part start date, end date 
            if(item.isApplncPart()){
            	if((PartPriceConstants.APPLNC_NOT_ON_QUOTE.equalsIgnoreCase(item.getApplianceId()) 
            			|| PartPriceConstants.APPLNC_NOT_ASSOCIATED.equalsIgnoreCase(item.getApplianceId())) 
            			&& (item.isApplncRenewal() || item.isApplianceRelatedSoftware() || item.isApplncServicePack() || item.isApplncServicePackRenewal()||(item.isApplncReinstatement()&&isNotInPGS()))
            			&& (item.getMaintStartDate() == null || item.getMaintEndDate() == null)) {
            		result.put(QuoteCapabilityProcess.QT_LINE_ITM_START_END_DATE_INVALID, Boolean.TRUE);
            	}
            }else{
            	// validate line item start/end date for certain revenue stream codess
            	// ignore the SaaS part start date and end date validation
            	if (PartPriceConfigFactory.singleton().needDetermineDate(item.getRevnStrmCode())
            			&& (item.getMaintStartDate() == null || item.getMaintEndDate() == null) && !item.isSaasPart() && !item.isMonthlySoftwarePart()){
            		result.put(QuoteCapabilityProcess.QT_LINE_ITM_START_END_DATE_INVALID, Boolean.TRUE);
            	}
            }
        }
    }

    protected void verifyAppliancePart(Map result){
    	Set set = QuoteCommonUtil.validateApplncParts(quote);
    	for(Object key: set){
    		 result.put((String)key, Boolean.TRUE);
    	}
    }
    
    /**
     * validate appliance part CRAD, for Appliance#99
     */
    protected void validateLineItemCRAD(){
    	if(!QuoteCommonUtil.validationLineItemCRAD(quote)){
    		if(!validateResult.containsKey(QuoteCapabilityProcess.ENTER_CUST_REQSTD_ARRIVL_DATE)){
    			validateResult.put(QuoteCapabilityProcess.ENTER_CUST_REQSTD_ARRIVL_DATE, Boolean.TRUE);
    		}
    	}
    }
    
    /**
     * @return
     */
    protected boolean audienceHasSubmitterAccess() {

    	 if(this.isInPGS()){
         	return true;
         }

        return user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_SUBMITTER;
    }

    /**
     * @param item
     * @return
     */
    protected boolean isOverrideStartOrEndDateSet(QuoteLineItem item) {
        return item.getStartDtOvrrdFlg() || item.getEndDtOvrrdFlg();
    }

    /**
     * @param item
     * @return
     */
    protected boolean isPartGroupRequireSpBid(QuoteLineItem item) {
        return item.isPartGroupRequireSpBid();
    }

    /**
     * @param quote
     * @return
     */
    protected boolean isAPRegion(Quote quote) {
        Country country = quote.getQuoteHeader().getCountry();
        return country.getWWRegion().equals(QuoteConstants.REGION_APAC);
    }

    /**
     * @param item
     * @return
     */
    protected boolean isMaintRequireSpBid(QuoteLineItem item) {
        return item.getAddtnlMaintCvrageQty() > QuoteConstants.ALLOWED_MAINT_YEARS;
    }

    /**
     * @param item
     * @return
     */
    protected boolean discOrOverrideUnitPrcSpecified(QuoteLineItem item) {
        return item.getLineDiscPct() > 0 || item.getOverrideUnitPrc().doubleValue() > 0;
    }

    protected boolean isPartnerAccessSet() {
        return quote.getQuoteHeader().getRnwlPrtnrAccessFlag() != -1;
    }

    protected boolean isSalesOddSelected() {
        return StringUtils.isNotEmpty(quote.getQuoteHeader().getRenwlQuoteSalesOddsOode());
    }

    protected boolean isUpsideTranSelected() {
        return StringUtils.isNotEmpty(quote.getQuoteHeader().getUpsideTrendTowardsPurch());
    }

    protected Contract getQuoteContract() {
        if (quote == null || quote.getCustomer() == null || quote.getCustomer().getContractList() == null
                || quote.getCustomer().getContractList().size() == 0)
            return null;
        return (Contract) quote.getCustomer().getContractList().get(0);
    }

    // Submitted quote related rule
    protected boolean userIsQuoteCreator() {
        String ownerId = quote.getQuoteHeader() == null ? "" : quote.getQuoteHeader().getCreatorId();
        return ownerId.equalsIgnoreCase(user.getEmail());
    }

    protected boolean userIsQuoteEditor() {
        List delegateList = quote.getDelegatesList();

        if (delegateList != null && delegateList.size() > 0) {
            for (int i = 0; i < delegateList.size(); i++) {
                SalesRep editor = (SalesRep) delegateList.get(i);
                String editorId = editor.getEmailAddress() == null ? "" : editor.getEmailAddress();
                if (editorId.equalsIgnoreCase(user.getEmail()))
                    return true;
            }
        }
        return false;
    }

    protected boolean userIsQuoteCreatorEditor(List massDlgtnList) {
        if (massDlgtnList != null && massDlgtnList.size() > 0) {
            for (int i = 0; i < massDlgtnList.size(); i++) {
                SalesRep editor = (SalesRep) massDlgtnList.get(i);
                String editorId = editor.getEmailAddress() == null ? "" : editor.getEmailAddress();
                if (editorId.equalsIgnoreCase(user.getEmail()))
                    return true;
            }
        }
        return true;
    }

    protected boolean userIsAnyEditor() {
        // user is one of quote creator, quote editor, quote creator editor.
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return (qtUserAccess == null ? false : qtUserAccess.isEditor());
    }

    protected boolean userIsPendingAppGrpMember() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return (qtUserAccess == null ? false : qtUserAccess.isPendingAppTypMember());
    }

    protected boolean userIsAnyAppGrpMember() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return (qtUserAccess == null ? false : qtUserAccess.isAnyAppTypMember());
    }

    protected boolean userIsReviewer() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return (qtUserAccess == null ? false : qtUserAccess.isReviewer());
    }

    protected boolean userIsFirstAppGrpMember() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return (qtUserAccess == null ? false : qtUserAccess.isFirstAppTypMember());
    }

    protected boolean quoteHasNoneApproval() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return qtUserAccess == null ? false : qtUserAccess.isNoneApproval();
    }

    protected boolean userCanChangeBidExpDate() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return qtUserAccess == null ? false : qtUserAccess.isCanChangeBidExpDate();
    }

    protected boolean userCanChangeBidLineItemDate() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return qtUserAccess == null ? false : qtUserAccess.isCanChangeBidLineItemDate();
    }

    protected boolean userCanSupersedeAppr() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return qtUserAccess == null ? false : qtUserAccess.isCanSupersedeAppr();
    }

    protected boolean userCanEditExecSummary() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return qtUserAccess == null ? false : qtUserAccess.isCanEditExecSummary(user.getAccessLevel(QuoteConstants.APP_CODE_SQO));
    }

    protected boolean userCanApprove() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return qtUserAccess == null ? false : qtUserAccess.isCanApprove();
    }

    protected boolean userCanViewExecSummary() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return qtUserAccess == null ? false : qtUserAccess.isCanViewExecSummry(user.getAccessLevel(QuoteConstants.APP_CODE_SQO));
    }

    protected boolean isSBButtonDisplay(String lob, String cntryCode, String isMigration, List mtrlGrpCodes) {
        return ButtonDisplayRuleFactory.singleton().isSBButtonDisplay(lob, cntryCode, isMigration, mtrlGrpCodes);
    }

    protected boolean isOrderButtonDisplay(String lob, boolean ELAFlag, String cntryCode) {
        return ButtonDisplayRuleFactory.singleton().isOrderButtonDisplay(lob, ELAFlag, cntryCode);
    }

    protected boolean isExprtSprdshtButtonDisplay(String lob) {
        return ButtonDisplayRuleFactory.singleton().isExprtSprdshtButtonDisplay(lob);
    }

    protected void validateTuscanySWVNRule() {
        boolean valid = true;
        HashMap reasons = new HashMap();
        // Only check channel quotes
        if (!quote.getQuoteHeader().isChannelQuote())
            return;

        try {
            TuscanySWVNRuleHelper helper = new TuscanySWVNRuleHelper(quote);
            valid = helper.validateSubmission(reasons);

        } catch (QuoteException e) {
            valid = false;
            logContext.error(this, e.getMessage());
        }

        if (!valid) {
            logContext.debug(this, "Tuscany & SWVN validation failed, prevent submission.");

            if (reasons.containsKey(QuoteCapabilityProcess.RSEL_NOT_AUTH_TO_PORTFOLIO_TBD)) {
                validateResult.put(QuoteCapabilityProcess.RSEL_NOT_AUTH_TO_PORTFOLIO_TBD, Boolean.TRUE);
            }
            else {
                validateResult.put(QuoteCapabilityProcess.RSEL_NOT_AUTH_TO_PORTFOLIO, Boolean.TRUE);
            }
        }
    }

    public boolean canCancelApprovedBid(int userAccess)
    {
        boolean flag = false;
        QuoteHeader header = quote.getQuoteHeader();
        flag = (this.isAccessLevelApprover(userAccess, true)
        	&& header.getSpeclBidFlag() == 1
        	&& header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)
        	&& quote.getQuoteUserAccess().isCanCancelApprovedBid()
            && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG)
            && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_REJECTED)
            && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)
            && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED)
            && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.BILLED_ORDER)
            && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED)
            && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS) );
        logContext.debug(this, "canCancelApprovedBid=" + flag);
        logContext.debug(this, "QuoteConstants.ACCESS_LEVEL_APPROVER == userAccess =" + (this.isAccessLevelApprover(userAccess, true)) );
        logContext.debug(this, "header.getSpeclBidFlag() == 1" + (header.getSpeclBidFlag() == 1) );
        logContext.debug(this, "header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)=" + (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)) );
        logContext.debug(this, "quote.getQuoteUserAccess().isCanCancelApprovedBid() =" + (quote.getQuoteUserAccess().isCanCancelApprovedBid()) );


        return flag;
    }

    protected Date getTodayInQuoteTimeZone() {

        // time zone defaults to US/Eastern
        String timeZone = "US/Eastern";
        QuoteHeader header = quote.getQuoteHeader();
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        // country defaults to USA
        String priceCntryCode = header.getPriceCountry() == null ? CustomerConstants.COUNTRY_USA : header
                .getPriceCountry().getCode3();

        try {
            String salesOrgCode = CommonServiceUtil.getSalesOrgCode(priceCntryCode, lob);

            logContext.debug(this, "Price country code is " + priceCntryCode);
            logContext.debug(this, "Sales org code is " + salesOrgCode);

            logContext.debug(this, "Quote["+header.getWebQuoteNum()+"] Price country code is " + priceCntryCode);
            logContext.debug(this, "Quote["+header.getWebQuoteNum()+"] Sales org code is " + salesOrgCode);

            TimeOffset to = TimeOffsetFactory.singleton().getTimeOffsetBySalesOrg(salesOrgCode);

            if (to != null) {
                timeZone = to.getTimeZoneID();
                logContext.debug(this, "TimeOffset object is NOT null & timezone id is " + timeZone);

                logContext.debug(this, "Quote["+header.getWebQuoteNum()+"] TimeOffset object is NOT null & timezone id is " + timeZone);
            }
        } catch (TopazException e) {
            logContext.error(this, "Error when getting Timeoffset object " + e);
        }

        logContext.debug(this, "Quote["+header.getWebQuoteNum()+"] final timezone id is " + timeZone);
        Date today = DateHelper.getDateFromTimeZone(timeZone);
        logContext.debug(this, "Today in quote time zone is " + today);
        logContext.debug(this, "Quote["+header.getWebQuoteNum()+"] Today in quote time zone is " + today);
        Calendar cal = Calendar.getInstance();
        logContext.debug(this, "Quote["+header.getWebQuoteNum()+"] current maching time is " + cal.getTime());
        logContext.debug(this, "Quote["+header.getWebQuoteNum()+"] current maching timezone is " + cal.getTimeZone());

        return today;
    }

    
    protected boolean canSubmitQtWithSaaSOrMonthlySWParts() {
		if (quote == null || quote.getQuoteHeader() == null) {
			return false;
		} else {
			if(quote.getQuoteHeader().isChannelQuote()&&quote.getQuoteHeader().hasSoftSLineItem()&&
					(quote.getQuoteHeader().isHasMonthlySoftPart()||quote.getQuoteHeader().hasSaaSLineItem())){
				return false;
			}else{
				return true;
			}
		}
	}

    protected void validateForSaaSQuote() throws QuoteException {
    	validateBillingOptn();
    	validateSaaSConfigrtn();
    	//validateSbscrptnPartsTCV();  -- merge with monthly sw validation
        validateCACustCurrncyMtch();
        validateChannelQtBPsMatchChargeAgreement();
        validateTerms();
    }
    
    protected void validateSaasFCTToPAQuote()throws QuoteException{
    	if(this.quote.getQuoteHeader().isTermDiffInDiffFctConfig()){
    		validateResult.put(QuoteCapabilityProcess.FCT_TO_PA_MGT_TERM_NOT_SAME, Boolean.TRUE);
    	}
    }

    protected void validateBillingOptn() throws QuoteException{
    	// 0 : Valid data
        // 1 : Invalid Contract term or billing frequency not be specified
        // 2 : Invalid Subscription parts committed term is not equally divisible by the setup parts selected billing option
    	// 3 : Invalid Setup parts without associated subscription parts
    	// 4 : Invalid Subscription parts committed term is not equally divisible by billing option
    	int isValid = 0;
    	if(quote == null)
    		return;
    	//Validate subscription parts billing option
    	try {
    		isValid = QuoteCommonUtil.validateSubscrptnPartBillingOption(quote);
			logContext.debug(this, "Validate subscription parts billing option, isValid num :" + isValid);
			if(isValid == 1) {
				validateResult.put(QuoteCapabilityProcess.TERM_OR_BILLG_OPTN_NOT_SPECIFY_MSG, Boolean.TRUE);
			}else if (isValid == 4) {
				validateResult.put(QuoteCapabilityProcess.TERM_NOT_EQL_DVSBL_BY_BILLG_OPTN_MSG, Boolean.TRUE);
			}
		} catch (QuoteException e) {
			logContext.error(this,"Can't successfully validate subscription parts billing option!");
			logContext.error(this, e.getMessage());
			throw e;
		}
    }

    // for DSW10.4 release, channel quote with SaaS parts or monthly parts not supported yet, so not allow to submit
    protected void validateChannelQtWithSaaSParts() {
		if (quote == null || quote.getQuoteHeader() == null)
			return;
		if (quote.getQuoteHeader().isChannelQuote() && quote.getQuoteHeader().hasSoftSLineItem()&&
				(quote.getQuoteHeader().isHasMonthlySoftPart()||quote.getQuoteHeader().hasSaaSLineItem()) ) {
			validateResult.put(QuoteCapabilityProcess.NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG,Boolean.TRUE);
		}
	}

    protected void validateChannelQtBPsMatchChargeAgreement() {
		if (quote == null || quote.getQuoteHeader() == null)
			return;
		if (quote.getQuoteHeader().isChannelQuote() && !quote.getQuoteHeader().isMatchBPsInChargeAgreementFlag()) {
			validateResult.put(QuoteCapabilityProcess.NOT_MATCH_BPS_ON_CHARGE_AGREEMENT_FOR_CHNNL_QT_MSG,Boolean.TRUE);
		}
	}

    protected void validateCACustCurrncyMtch() {
    	if (quote == null || quote.getQuoteHeader() == null)
			return;
		QuoteHeader header = quote.getQuoteHeader();

		if(header.isCACustCurrncyNotMatchFlag()) {
			validateResult.put(QuoteCapabilityProcess.CA_CUST_CURRNCY_NOT_MATCH,Boolean.TRUE);
		}
	}

    protected void validateSaaSConfigrtn() {
    	if (quote == null || quote.getQuoteHeader() == null)
			return;
		QuoteHeader header = quote.getQuoteHeader();
		//just check for quote with SaaS Parts
		if (!header.hasSaaSLineItem())
			return;
		List configrtns = quote.getPartsPricingConfigrtnsList();
		int staleDays = ApplicationProperties.getInstance().getConfigrtnStaleTime();
		Calendar cal = Calendar.getInstance();
		// Get current date
		Date today = new Date();
        Date currDate = DateUtils.truncate(today, Calendar.DATE);

		if (configrtns == null || configrtns.isEmpty()) {
			logContext.debug(this, "SaaS quote without configurations!!");
			return;
		}

		ListIterator li = configrtns.listIterator();
		StringBuffer staleCngIds = new StringBuffer();
		String space = "&nbsp;" ;
		while (li.hasNext()) {
			PartsPricingConfiguration configrtn = (PartsPricingConfiguration) li.next();
			// if quote has configuration but there is null configuration modify date or stale modify date,it's invalid
			if (configrtn.getConfigrtnModDate() == null) {
				staleCngIds.append(configrtn.getIbmProdIdDscr())
				.append(space).append("(")
				.append(configrtn.getConfigrtnId().trim())
				.append(")").append(",").append(space);
			} else {
				Date configrtnModDate = DateUtils.truncate(configrtn.getConfigrtnModDate(), Calendar.DATE);
				cal.setTime(configrtnModDate);
				cal.add(Calendar.DATE, staleDays);
				Date staleDate = cal.getTime();
				if (staleDate.before(currDate)) {
					staleCngIds.append(configrtn.getIbmProdIdDscr())
					.append(space).append("(")
					.append(configrtn.getConfigrtnId().trim())
					.append(")").append(",").append(space);
				}
			}
		}
		if (staleCngIds.toString().endsWith(",&nbsp;")) {
			validateResult.put(QuoteCapabilityProcess.CONFIGRTN_STALE_MSG,staleCngIds.substring(0, staleCngIds.length()-7));
		}
	}

    protected void validateTerms() {
    	if (quote == null || quote.getQuoteHeader() == null)
			return;
		QuoteHeader header = quote.getQuoteHeader();
		//just check for quote with SaaS Parts
		if (!header.hasSaaSLineItem() && !quote.getMonthlySwQuoteDomain().isQuoteHasMonthlySwPart())
			return;

		@SuppressWarnings("rawtypes")
		List lineItems  = quote.getLineItemList();
		if (lineItems == null || lineItems.isEmpty()) {
			logContext.debug(this, "SaaS quote without lineItems!");
			return;
		}

		@SuppressWarnings("rawtypes")
		ListIterator li = lineItems.listIterator();
		while (li.hasNext()) {
			QuoteLineItem qli = (QuoteLineItem) li.next();
            if (!qli.isReplacedPart() && (qli.isSaasSubscrptnPart() || qli.isSaasSetUpPart() || (qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart()))) {
                if (qli.getICvrageTerm() != null && qli.getICvrageTerm().intValue() < 1) {
					validateResult.put(QuoteCapabilityProcess.TERMS_LESS_THAN_1_MONTH_MSG,Boolean.TRUE);
					return;
				}
			}
		}
	}

    protected void validateEstmtdOrdDate() {
		if (quote == null || quote.getQuoteHeader() == null)
			return;

		// estimated order date can be today or future date, can not be later than the default expiration date for that quote type
		QuoteHeader header = quote.getQuoteHeader();
		Date quoteExpDate = header.getQuoteExpDate();
		Date estmtdOrdDate = header.getEstmtdOrdDate();
		int qtMaxExpDays = quote.getQuoteHeader().getQuoteExpDays();
		// just validate estimated order date for add-on/trade-up/co-term
		if (header.isAddTrdOrCotermFlag()) {
			if (estmtdOrdDate == null) {
				validateResult.put(QuoteCapabilityProcess.ENTER_VALID_ESTMTD_ORD_DATE,Boolean.TRUE);
				return;
			}
			//can not be later than the default expiration date for that quote type
			if (isEstmtdOrdDateAfterMaxExpDays()) {
				validateResult.put(QuoteCapabilityProcess.ESTMTD_ORD_DATE_NOT_WITHIN_ACTIVIE_DAYS,String.valueOf(qtMaxExpDays));
				return;
			}
			// estimated order date should be today or future date, but before the expiration date
			if (isEstmtdOrdDateBeforeToday()|| (quoteExpDate != null && estmtdOrdDate.after(quoteExpDate))) {
				validateResult.put(QuoteCapabilityProcess.INVALID_ESTMTD_ORD_DATE,Boolean.TRUE);
				return;
			}
		}

	}
    
    /**
     *  A quote can contain Software, monthly software and SaaS, so long as the SaaS and monthly software  is on a new charge agreement
	 *	A quote can update one and only one charge agreement.  This means,
	 *   that a quote cannot contain software or create a new charge agreement if it also contains any of the following.  
	 *   A quote can contain any combination of the following, so long as they are on the same charge agreement.
	 *		An add-on and/or
	 *		A trade-up and/or
	 *		A co-termed new service or monthly software and/or
	 *	    An invoice aligned new service.or new monthly software
     */
    protected void validateCAByParts(){
    	if (quote == null || quote.getQuoteHeader() == null)
			return;
		QuoteHeader header = quote.getQuoteHeader();
		
		List saasConfigrtnList = quote.getPartsPricingConfigrtnsList();
		List<MonthlySoftwareConfiguration> msConfigList = quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
    	if( ( saasConfigrtnList == null || saasConfigrtnList.size() == 0 ) 
    			&& (msConfigList == null || msConfigList.size() ==0)){
    		return;
    	}
		
		boolean hasNewCAFlag = false;
		
    	ListIterator li = saasConfigrtnList.listIterator();
    	while(li.hasNext()) {
    		PartsPricingConfiguration cli = (PartsPricingConfiguration) li.next();
    		if(PartPriceConstants.ConfigrtnActionCode.NEW_NCT.equals(cli.getConfigrtnActionCode())){
				hasNewCAFlag = true ;
				break;
			}
    	}    	
		if(hasNewCAFlag){
			for (Iterator iterator = msConfigList.iterator(); iterator.hasNext();) {
				MonthlySoftwareConfiguration msConfiguration = (MonthlySoftwareConfiguration) iterator
						.next();
				//new configuration (no reference to existing charge agreement) - co-term radio-button "No" is selected
				if(PartPriceConstants.ConfigrtnActionCode.NEW_NCT.equals(msConfiguration.getConfigrtnActionCode())){
					hasNewCAFlag = true ;
					break;
				}
			}
		}
		if (checkIsNotBlank(header.getRefDocNum()) && (hasNewCAFlag || header.hasSoftSLineItem())) {
			validateResult.put(QuoteCapabilityProcess.EXISTING_CA_AND_NEW_CA_RULE_MSG, Boolean.TRUE);
		}
    }
    
    /**
     * the net increase of all monthly / saas software subscription parts in a configuration after add-on/tradeup must be greater than or equal to zero.
     */
    protected void validateSbscrptnPartsTCV(){
    	if (quote == null || quote.getQuoteHeader() == null)
			return;
		QuoteHeader header = quote.getQuoteHeader();
		if(quote.getQuoteHeader().isSaasFCTToPAQuote()){
    		return;
    	}
		
    	List saasConfigrtnList = quote.getPartsPricingConfigrtnsList();
    	List<MonthlySoftwareConfiguration> msConfigList = quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
    	
    	if((saasConfigrtnList == null || saasConfigrtnList.size() == 0) && (msConfigList == null || msConfigList.size() ==0) ){
    		return;
    	}
    	ListIterator li = saasConfigrtnList.listIterator();
    	while(li.hasNext()) {
    		PartsPricingConfiguration cli = (PartsPricingConfiguration) li.next();
    		if(cli.getIncreaseBidTCV() == null)
    			continue;
    		if (cli.getIncreaseBidTCV() < 0) {
    			validateResult.put(QuoteCapabilityProcess.SBSCRPTB_TCV_LOWER_THAN_ORGNL,Boolean.TRUE);
    			return;
    		}
    	}   
    	
		for (Iterator iterator = msConfigList.iterator(); iterator.hasNext();) {
			MonthlySoftwareConfiguration msConfiguration = (MonthlySoftwareConfiguration) iterator
					.next();
			if(msConfiguration.getIncreaseBidTCV() == null)
    			continue;
			if(msConfiguration.isAddOnTradeUp() && msConfiguration.getIncreaseBidTCV() < 0){
				validateResult.put(QuoteCapabilityProcess.SBSCRPTB_TCV_LOWER_THAN_ORGNL,Boolean.TRUE);
				return;
			}
		}
    }

    protected void validateCRAD(){
		if (quote == null || quote.getQuoteHeader() == null)
			return;
		QuoteHeader header = quote.getQuoteHeader();
		Date custReqestArrivlDate = header.getCustReqstArrivlDate();
		Date quoteStartDate = header.getQuoteStartDate();
		if((header.isPAQuote() || header.isPAEQuote() || header.isFCTQuote() || header.isSSPQuote() ||
	        	header.isOEMQuote() || header.isFCTToPAQuote()) && (header.isHasAppMainPart()||header.isHasAppUpgradePart())){
			if(custReqestArrivlDate == null){
				validateResult.put(QuoteCapabilityProcess.ENTER_CUST_REQSTD_ARRIVL_DATE,Boolean.TRUE);
				return;
			}
			if (isCustReqstdArrivlDateBeforeToday() || custReqestArrivlDate.before(quoteStartDate)) {
				validateResult.put(QuoteCapabilityProcess.INVALID_CUST_REQSTD_ARRIVL_DATE,Boolean.TRUE);
				return;
			}

		}

    }
    
    protected void validateProratePart(){
    	QuoteHeader header = quote.getQuoteHeader();
    	String lob = header.getLob() == null ? "" : header.getLob().getCode();
    	if(!QuoteConstants.LOB_PA.equalsIgnoreCase(lob) && !QuoteConstants.LOB_PAE.equalsIgnoreCase(lob) && !QuoteConstants.LOB_FCT.equalsIgnoreCase(lob)){
            return;
    	}
    	Date quoteExpDate = header.getQuoteExpDate();
    	if(quoteExpDate != null){
			List parts = quote.getLineItemList();
			if(parts != null){
				Calendar ca=Calendar.getInstance();
				int currDay = ca.get(ca.DATE);
				if(currDay == 1){
			    	QuoteLineItem item = null;
        			Date currentDate = DateUtil.getCurrentDate();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String quoteExp = sdf.format(quoteExpDate);
					String currentTime = sdf.format(currentDate);
					for(Iterator i = parts.iterator(); i.hasNext();){
		        		item = (QuoteLineItem)i.next();
		        		if(item.getProrateFlag()){
	        		    	if(!quoteExp.equals(currentTime)){
	        		    		validateResult.put(QuoteCapabilityProcess.QUOTE_PRORATE_PART_EXPIRATION_MESSAGE,Boolean.TRUE);
	        		    	}
	            				break;
		        		}
			    	}
				}
		    }
	    }
    }
    
    protected boolean isEstmtdOrdDateAfterMaxExpDays() {
		 int qtMaxExpDays = quote.getQuoteHeader().getQuoteExpDays();
		 Date estmtdOrdDate = quote.getQuoteHeader().getEstmtdOrdDate();
		 Calendar curr = Calendar.getInstance();
	     curr.add(Calendar.DATE, qtMaxExpDays);
         Date maxDate = curr.getTime();
         if (estmtdOrdDate.after(maxDate)) {
        	 return true;
         }
		return false;
	}

    protected boolean isEstmtdOrdDateBeforeToday() {
		 Date estmtdOrdDate = quote.getQuoteHeader().getEstmtdOrdDate();
	        if (estmtdOrdDate == null)
	            return false;

	        Date today = getTodayInQuoteTimeZone();
	        Date currDate = DateUtils.truncate(today, Calendar.DATE);

	        logContext.debug(this, "Current date is " + currDate);
	        logContext.debug(this, "Estimated order date is " + estmtdOrdDate);

	        return estmtdOrdDate.before(currDate);
	}

    protected boolean isCustReqstdArrivlDateBeforeToday() {

		 Date custReqstArrivlDate = quote.getQuoteHeader().getCustReqstArrivlDate();
	        if (custReqstArrivlDate == null)
	            return false;

	        Date today = getTodayInQuoteTimeZone();
	        Date currDate = DateUtils.truncate(today, Calendar.DATE);

	        logContext.debug(this, "Current date is " + currDate);
	        logContext.debug(this, "Customer requested arrivl date is " + custReqstArrivlDate);

	        return custReqstArrivlDate.before(currDate);
	}

    public abstract boolean canCnvrtToSalesQuote();

    public abstract boolean canCnvrtToSpeclBid();

    public abstract boolean canConvertToPAE();

    protected abstract int isPartnerRegnCntryCrncyValid();

    public abstract Map validateUpdateQuotePartners();

    public abstract Map validateSubmitCopiedApprvdBid();

    public abstract boolean validateNewContract();


	public boolean isInPGS(){
		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(this.user.getAudienceCode());
	}

	public boolean isNotInPGS(){
		return !this.isInPGS();
	}

	public boolean isPGSQT(){
		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(this.quote.getQuoteHeader().getAudCode());
	}

	public boolean isNotPGSQT(){
		return !this.isPGSQT();
	}

	public boolean isAccessLevelSubmitter(int userAccess, boolean ifPGS){
		if(isInPGS()){
			return ifPGS;
		}else{
			return QuoteConstants.ACCESS_LEVEL_SUBMITTER == userAccess;
		}
	}

	public boolean isAccessLevelReader(int userAccess, boolean ifPGS){
		if(isInPGS()){
			return ifPGS;
		}else{
			return QuoteConstants.ACCESS_LEVEL_READER == userAccess;
		}
	}

	public boolean isAccessLevelApprover(int userAccess, boolean ifPGS){
		if(isInPGS()){
			return ifPGS;
		}else{
			return QuoteConstants.ACCESS_LEVEL_APPROVER == userAccess;
		}
	}

	public boolean isAccessLevelAdministrator(int userAccess, boolean ifPGS){
		if(isInPGS()){
			return ifPGS;
		}else{
			return QuoteConstants.ACCESS_LEVEL_ADMINISTRATOR == userAccess;
		}
	}

	public boolean isAccessLevelEcare(int userAccess, boolean ifPGS){
		if(isInPGS()){
			return ifPGS;
		}else{
			return QuoteConstants.ACCESS_LEVEL_ECARE == userAccess;
		}
	}

    public boolean isSubmitterWithoutEditPrivilege(int userAccess, boolean ifPGS){

		if(isInPGS()){
			return ifPGS;
		}

    	if(QuoteConstants.ACCESS_LEVEL_SUBMITTER == userAccess){
        	if(this.userIsAnyEditor()){
        		return false;
        	}else{
        		return true;
        	}
        }else{
        	return false;
        }
    }
    
  //Calculate Last Provisioning Date by Estimated Line Item Start Date
    public static Date getLastProvDateByEsmtLineStDate(Date estmtLineStDate) {
    	Date resultDate = new Date();
    	resultDate.setTime(estmtLineStDate.getTime());
    	if(estmtLineStDate.getDate()!=1){
    		Calendar c = Calendar.getInstance();
            c.setTime(resultDate);
            c.add(Calendar.MONTH, 1);
            c.set(Calendar.DATE, 1);
            resultDate.setTime( c.getTimeInMillis());
    	}
    	return resultDate;
    }
    
    /**
     * 
     * @return
     */
    protected boolean isSoftwarePartWithoutAppIncId() {
    	return quote.getQuoteHeader().isSoftwarePartWithoutApplncId();
    }
    
    //Calculate Earliest Provision Date by Estimated Line Item Start Date
    public static Date getEarliestProvDateByEsmtLineStDate(Date estmtLineStDate) {
    	Date resultDate = new Date();
    	resultDate.setTime(estmtLineStDate.getTime());
    	if(estmtLineStDate.getDate()==1){
    		Calendar c = Calendar.getInstance();
            c.setTime(resultDate);
            c.add(Calendar.MONTH, -1);
            c.set(Calendar.DATE, 21);
            resultDate.setTime( c.getTimeInMillis());
    	}else{
    		resultDate.setDate(2);
    	}
    	return resultDate;
    }

    /**
     * Getter for validateResult.
     * 
     * @return the validateResult
     */
    @SuppressWarnings("rawtypes")
    public Map getValidateResult() {
        return this.validateResult;
    }

	public abstract Map validateTou(boolean orderFlag) throws QuoteException;

    
//    protected boolean isSpecialBidAllowed(QuoteHeader header,QuoteUserSession userSession){
//    	boolean result = false;
//    	if(QuoteConstants.BPTierModel.BP_TIER_MODEL_ONE.equalsIgnoreCase(userSession.getBpTierModel())){
//    		result =  ButtonDisplayRuleFactory.singleton().isPGSQuoteSubmisAllowed(header,QuoteConstants.BPTierModel.BP_TIER_MODEL_ONE);
//    	}
//		if(QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO.equalsIgnoreCase(userSession.getBpTierModel())){
//			result =  ButtonDisplayRuleFactory.singleton().isPGSQuoteSubmisAllowed(header,QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO);
//		}
//		return result;
//    	
//    }
	
	/**
	 * check if it is a SSP contract customer
	 * @param lob
	 * @return
	 */
    protected boolean checkSSPContractCust(String lob, Boolean isDisplayOrder) {
    	boolean retFlag = isDisplayOrder && QuoteConstants.LOB_SSP.equalsIgnoreCase(lob);
    	if ( !retFlag )
    	{
    		return false;
    	}
    	if ( StringUtils.isBlank(CommonServiceUtil.getValidCtrctNum(quote)) )
    	{
    		return true;
    	}
    	return false;
	}

	/**
	 * <p>
	 * Checks if a String is not empty (""), not null and not whitespace only.
	 * </p>
	 * 
	 * <pre>
	 * checkIsNotBlank(null)      = false
	 * checkIsNotBlank("null")    = false
	 * checkIsNotBlank("")        = false
	 * checkIsNotBlank(" ")       = false
	 * checkIsNotBlank("bob")     = true
	 * checkIsNotBlank("  bob  ") = true
	 * </pre>
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null and not
	 *         whitespace
	 * @since 2.0
	 */
	protected boolean checkIsNotBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return false;
		}
		if ("null".equals(str.trim())) {
			return false;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return true;
			}
		}
		return false;
	}
}
