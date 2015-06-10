package com.ibm.dsw.quote.draftquote.viewbean.helper;

import is.domainx.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.RenewalPricingMethodCodeFactory;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.ViewKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.DeployModel;
import com.ibm.dsw.quote.common.domain.EquityCurve;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceAppliancePartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.UIFormatter;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
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
 * Creation date: Apr 3, 2007
 */

public class PartPriceCommon implements Serializable{
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
	private static final String rightAlign = "right";
	private static final String bottomAlign = "bottom";

	private Locale locale;

	protected Quote quote;

	/** the line of business for this quote */
	private String lob;

	/** the country price disclaimer message key */
	protected String countryPriceDisclaimerMessage;

	private User user;


	protected UIFormatter formatter ;
	public PartPriceCommon(Quote quote) {
		this(quote, null);
	}

	public PartPriceCommon(Quote quote, User user) {
		this.quote = quote;
		this.lob = quote.getQuoteHeader().getLob().getCode();
		this.user = user;
		this.formatter = new UIFormatter(quote);

	}

	public PartPriceCommon(Quote quote,  User user, Locale locale){
		this(quote, user);
		this.locale = locale;
	}

	public String getSVPLevelCode() {
		String svpLevel = "";
		QuoteHeader header = quote.getQuoteHeader();

		if (StringUtils.isNotBlank(header.getOvrrdTranLevelCode())) {
			svpLevel = header.getOvrrdTranLevelCode();
			return svpLevel;
		}

		if (StringUtils.isNotBlank(header.getTranPriceLevelCode())) {
			svpLevel = header.getTranPriceLevelCode();
		} else if(quote.getCustomer() != null) {
			Customer cust = quote.getCustomer();

			//For existing/additional site new customer, get SVP level from contract
			if(header.hasExistingCustomer() || cust.isAddiSiteCustomer()){
				Contract contract = null;
				if ((cust.getContractList() != null) && (cust.getContractList().size() != 0)) {
					contract = (Contract) cust.getContractList().get(0);
				}
				if ((contract != null) && (contract.getVolDiscLevelCode() != null)) {
					svpLevel = contract.getVolDiscLevelCode().trim();
				}
			}

			//For XSP/GV/ED, use SVP level that user selected
			if(QuoteCommonUtil.isCustWithOverrideSVP(quote)){
				svpLevel = cust.getTransSVPLevel();
			}
		}
		return svpLevel;
	}

	public String getSVPLevel() {
		String SVPLevelCode = this.getSVPLevelCode();
		return PartPriceConfigFactory.singleton().getPriceLevelDesc(SVPLevelCode);
	}

	/**
	 * @param
	 * @return true if user manually selected a price level
	 */
	public boolean isVolTranLevelOvrrd(){
		return StringUtils.isNotBlank(quote.getQuoteHeader().getOvrrdTranLevelCode());
	}

	/**
	 * @param lineItem
	 * @return true if part is a contract part
	 */
	protected boolean isContractPart(QuoteLineItem lineItem) {
		String partTypeCode = lineItem.getPartTypeCode();
		if (null == partTypeCode) {
			return false;
		}

		return PartPriceConstants.PartTypeCode.PACTRCT.equals(partTypeCode.trim());

	}

	protected boolean isMaintPart(QuoteLineItem item) {
		return item.getPartDispAttr().isMaintBehavior();
	}

	public boolean isLicensePart(QuoteLineItem item) {
		return item.getPartDispAttr().isLicenseBehavior();
	}
	protected boolean isSupportPart(QuoteLineItem item) {
		return item.getPartDispAttr().isSupport();
	}

	public boolean isAddedFromRenewalQuote(QuoteLineItem item) {
		return (item.getRenewalQuoteNum() != null) && (!"".equals(item.getRenewalQuoteNum().trim()));
	}

	public Boolean isNewServiceLineItem(QuoteLineItem item) {
		return item.isNewService();
	}

	protected boolean hasCustomerWithContract() {
		if (!quote.getQuoteHeader().hasExistingCustomer()) {
			return false;
		}

		if (quote.getCustomer() == null) {
			return false;
		}

		List contracts = quote.getCustomer().getContractList();
		if (null == contracts) {
			return false;
		}
		return contracts.size() > 0;
	}

	/**
	 * @param lineItem
	 * @return true if quantity has been set
	 */
	protected boolean isQuantitySet(QuoteLineItem lineItem) {
		return (PartPriceSaaSPartConfigFactory.singleton().isNoQty(lineItem)
				|| (lineItem.getPartQty() != null));
	}

	/**
	 * @return true if any parts have been selected
	 */
	protected boolean hasLineItems() {
		return (quote.getLineItemList() != null) && (quote.getLineItemList().size() != 0);
	}

	/**
	 * @return true if any parts have been selected
	 */
	protected boolean hasSubmittedLineItems() {
		return (quote.getLineItemList() != null) && (quote.getLineItemList().size() != 0);
	}

	/**
	 * Added by lee,March 29,2007.
	 *
	 * @return line items of quote
	 */
	public List getLineItems() {
		return quote.getLineItemList();
	}

	public boolean showLineitemPrice(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
		if (isPriceUnAvaialbe
				|| (quote.getQuoteHeader().isRenewalQuote() && (lineItem.getPartQty() != null) && (lineItem.getPartQty()
						.intValue() == 0))
						|| (quote.getQuoteHeader().isPPSSQuote() && lineItem.isObsoletePart())) {
			return false;
		}

		if (isQuantitySet(lineItem)) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * @return
	 */
	public String getTotalPoints() {
		return String.valueOf(quote.getQuoteHeader().getTotalPoints());
	}

	/**
	 *
	 * @return
	 */
	public String getTotalPrice() {
		return String.valueOf(quote.getQuoteHeader().getQuotePriceTot());
	}

	/**
	 * @return
	 */
	protected boolean isSubmittedEditable() {
		if ((quote.getLineItemList() != null) && (quote.getLineItemList().size() != 0)) {
			if (quote.getQuoteHeader().getSpeclBidFlag() == 1) {
				boolean isApprover = user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER;
				if (isApprover) {
					QuoteHeader quoteHeader = quote.getQuoteHeader();
					boolean overStatus = quoteHeader.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR);

					//The current user is a member of an approval group of
					//which the approval type has been enabled to change the line item dates
					boolean isAnyAppMember = false;
					boolean isCanChgBidLiDate = false;

					if (quote.getQuoteUserAccess() != null) {
						isAnyAppMember = quote.getQuoteUserAccess().isAnyAppTypMember();
						isCanChgBidLiDate = quote.getQuoteUserAccess().isCanChangeBidLineItemDate();
						logContext.debug(this,"web_quote_num : "+ quote.getQuoteHeader().getWebQuoteNum());
						logContext.debug(this,"Awaiting spcial bid approval :"+overStatus);
						logContext.debug(this,"Can Change bid line date :"+isCanChgBidLiDate);
						logContext.debug(this,"is any approval member :"+isAnyAppMember);
					}
					if (overStatus && isAnyAppMember && isCanChgBidLiDate) {
						return true;
					}

				}
			}
		}
		return false;
	}
	public String getFormattedPrice(Double price){
		if(price == null){
			return "";
		}
		return this.getFormattedPrice(price.doubleValue());
	}
	public String getFormattedPrice(double ori) {
		return formatter.formatEndCustomerPrice(ori);

	}

	public String getFormattedPriceByPartType(QuoteLineItem qli,Double ori) {
		if(ori == null){
			return DraftQuoteConstants.BLANK;
		}
		return DecimalUtil.format(ori.doubleValue(), QuoteCommonUtil.getPartPriceRoundingFactor(quote.getQuoteHeader(), qli));
	}

	public String getUSFormattedPrice(double ori) {
		return UIFormatter.formatUSDPrice(ori);
	}

	public String getEURFormattedPrice(double ori) {
		return UIFormatter.formatEURPrice(ori);
	}

	public String getFormattedPoint(double ori) {
		return formatter.formatPoint(ori);

	}

	/**
	 * Added by lee,March 30,2007
	 *
	 * @return row span number by judging what will be shown
	 */

	public boolean isPA() {
		return QuoteConstants.LOB_PA.equalsIgnoreCase(lob);
	}

	/**
	 * Passport Advantage Express
	 *
	 * @return true if the line of business is PAE
	 */
	public boolean isPAE() {
		return QuoteConstants.LOB_PAE.equalsIgnoreCase(lob);
	}

	public boolean isPAUN() {
		return QuoteConstants.LOB_PAUN.equals(quote.getQuoteHeader().getSystemLOB().getCode());
	}

	public boolean isFCT() {
		return QuoteConstants.LOB_FCT.equalsIgnoreCase(lob);
	}

	public boolean isPPSS() {
		return QuoteConstants.LOB_PPSS.equalsIgnoreCase(lob);
	}

	public boolean isSSP(){
		return QuoteConstants.LOB_SSP.equalsIgnoreCase(lob);
	}

	public String getQtyAlign() {
		return rightAlign;
	}

	public String getUnitPriceAlign() {
		return rightAlign;
	}

	public String getTotalPriceAlign() {
		return rightAlign;
	}

	public String getUnitPointAlign() {
		return rightAlign;
	}

	public String getTotalPointAlign() {
		return rightAlign;
	}

	public String getDiscountAlign() {
		return rightAlign;
	}

	public String getPriceTypeAlign(){
		return bottomAlign;
	}

	public String getControlledCodeDesc(QuoteLineItem lineItem){
		return lineItem.getControlledCodeDesc();
	}
	public boolean showChannelMarginCol(){
		List lineItems = quote.getLineItemList();

		return PartPriceConfigFactory.singleton().allowChannelMarginDiscount(quote)  && (lineItems.size()>0);
	}

	public boolean isShowBpDisField(){
		if(CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader())){
			return false;
		}
		return true;
	}

	public boolean enableManualBpDiscount(){
		return PartPriceConfigFactory.singleton().enableManualBpDiscount(quote) && !CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader()) ;
	}

	public boolean disableBpDiscountInputBox(QuoteLineItem qli) {
		return (qli.isObsoletePart() && !qli.canPartBeReactivated())
		|| CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader());
	}

	public boolean isPartControlled(QuoteLineItem qli){
		return (isPA() || isPAE() || isOEM()) && qli.isControlled();
	}

	public String[] getPartControlledParams(QuoteLineItem qli){
		return new String[]{qli.getControlledCodeDesc(), qli.getIbmProgCodeDscr()};
	}

	public boolean isOEM(){
		return QuoteConstants.LOB_OEM.equalsIgnoreCase(lob);
	}

	/**
	 * Added by lee,March 30,2007
	 *
	 * @param lineItem
	 * @return
	 * if SaaS part, hide the "+" button
	 */
	public boolean showAddAntLineItemLink(QuoteLineItem lineItem) {

		if(lineItem.isSaasPart() || lineItem.isMonthlySoftwarePart()){
			return false;
		}
		String lob = quote.getQuoteHeader().getLob().getCode();
		String partTypeCode = lineItem.getPartTypeCode();
		return PartPriceConfigFactory.singleton().allowOvrdUnitPriceOrDisc(lob, partTypeCode)
		&& !CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader());

	}

	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether show the SaaS part quantity text field
	 */
	public boolean showSaaSQuantityField(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		showQtyInputBox(lineItem);
	}

	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether show the SaaS EntitledUnitPrice
	 */
	public boolean showSaaSEntitledUnitPrice(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		showEntldUnitPrice(lineItem);
	}

	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether show the SaaS BidUnitPrice
	 */
	public boolean showSaaSBidUnitPrice(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		showBidUnitPrice(lineItem);
	}

	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether show the SaaS Entitled Extended Price
	 */
	public boolean showSaaSEntitledExtPrice(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		showEntldExtndPrice(lineItem);
	}
	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether show the SaaS Bid Extended Price
	 */
	public boolean showSaaSBidExtPrice(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		showBidExtndPrice(lineItem);
	}

	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether Can user input discount percent
	 */
	public boolean canOvrrdDisc(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		canInputDiscount(lineItem);
	}

	/**
	 * @param qli
	 * @return
	 * boolean: if user can input unit override price, return true, else return false
	 */
	public boolean canOvrrdUnitPrice(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		canInputUnitOvrrdPrice(lineItem);
	}

	/**
	 * @param qli
	 * @return
	 * boolean: if user can input extended override price, return true, else return false
	 */
	public boolean canOvrrdExtPrice(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		canInputExtndOvrrdPrice(lineItem);
	}

	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether Does billing frequency apply to these parts
	 */
	public boolean showBillingFrequency(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		showBillingFrqncy(lineItem);
	}

	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether Does committed term selection apply to these parts
	 */
	public boolean showTermSelection(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		showCommittedTerm(lineItem);
	}

	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether show total contract value
	 */
	public boolean showItemTotContractVal(QuoteLineItem lineItem) {
		if(lineItem.isSaasTcvAcv()){
			return true;
		}
		return false;
	}

	/**
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether show UpTo selection
	 */
	public boolean showUpToSelection(QuoteLineItem lineItem) {
		return PartPriceSaaSPartConfigFactory.singleton().
		showUpToDropDown(lineItem);
	}

	/**
	 * @param lineItem
	 * @return
	 * String
	 * get coverage term unit (Years or Months)
	 */
	public String getCvrageTermUnit(QuoteLineItem lineItem){
		if(PartPriceConstants.SAAS_TERM_UNIT_ANNUAL.equals(lineItem.getPricngIndCode())){
			return "Years";
		} else if(PartPriceConstants.SAAS_TERM_UNIT_MONTH.equals(lineItem.getPricngIndCode())){
			if ((lineItem.getICvrageTerm()!=null) && (lineItem.getICvrageTerm().intValue() == 1)) {
				return "Month";
			}
			return "Months";
		}

		return "";
	}

	/**
	 * @param lineItem
	 * @param isPriceUnAvaialbe
	 * @return
	 * String
	 */
	public String getSaasBidTCV(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
		if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe) && (lineItem.getSaasBidTCV()!=null)) {
			return getFormattedPriceByPartType(lineItem, lineItem.getSaasBidTCV());
		} else {
			return DraftQuoteConstants.BLANK;
		}
	}

	/**
	 * @param lineItem
	 * @param isPriceUnAvaialbe
	 * @return
	 * String
	 */
	public String getSaasEntitledTCV(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
		if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe) && (lineItem.getSaasEntitledTCV()!=null)) {
			return getFormattedPriceByPartType(lineItem, lineItem.getSaasEntitledTCV());
		} else {
			return DraftQuoteConstants.BLANK;
		}
	}

	/**
	 * @param qli
	 * @return
	 * String: Unit or Extended
	 */
	public String getSaaSOvrrdTxt(QuoteLineItem qli){
		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
		String unit = context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,
				PartPriceViewKeys.OVERRIDE_TYPE_UNIT);
		String extended = context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,
				PartPriceViewKeys.OVERRIDE_TYPE_EXTENDED);
		if(canOvrrdUnitPrice(qli)){
			return unit;
		}else if(canOvrrdExtPrice(qli)){
			return extended;
		}
		return "";
	}

	/**
	 * @param qli
	 * @return
	 * String: Unit or Extended
	 * get the SaaS part override type
	 */
	public String getSaaSOvrrdType(QuoteLineItem qli){

		if(canOvrrdUnitPrice(qli)){
			return PartPriceConstants.SaaSOverrideType.UNIT;
		}else if(canOvrrdExtPrice(qli)){
			return PartPriceConstants.SaaSOverrideType.EXTENDED;
		}
		return PartPriceConstants.SaaSOverrideType.DEFAULT;
	}


	/**
	 * @param qli
	 * @return
	 * boolean: if user can both override unit price and extended price, show the override dropdown
	 */
	public boolean showOvrrdDropDown(QuoteLineItem qli){
		if(canOvrrdUnitPrice(qli) && canOvrrdExtPrice(qli)){
			return true;
		}
		return false;
	}

	/**
	 * @param qli
	 * @return
	 * boolean
	 * judge whether show Multiple of X text
	 */
	public boolean showSaaSMultipleText(QuoteLineItem qli){
		return QuoteCommonUtil.needValidateSaaSMultiple(qli);
	}

	public String getSaaSMultipleText(QuoteLineItem qli){
		Integer multipleVal = qli.getTierQtyMeasre();
		if(multipleVal != null){
			return multipleVal.toString();
		}
		return "";
	}

	//get the SaaS part terms option values list
	public List getCvrageTermsList(QuoteLineItem qli) {
		List list = new ArrayList();
		int maxTermValue = PartPriceSaaSPartConfigFactory.singleton().getSaaSPartTermMaxValue(qli.getPricngIndCode());
		if(maxTermValue > 0){
			for(int i = 1; i <= maxTermValue; i++ ){
				list.add(i);
			}
		}
		return list;
	}

	/**
	 * @param qli
	 * @return
	 * String
	 * get the override price value,
	 * if override type is unit, return the override unit price
	 * else if override type is extended, return the override extended price
	 * else return blank
	 */
	public String getOverridePrcVal(QuoteLineItem qli) {
		if(qli.isSetLineToRsvpSrpFlag() == true){
			return DecimalUtil.format(GrowthDelegationUtil.getProratedRSVPPrice(qli),2);
		}else{
			String overrideType = getSaaSOvrrdType(qli);
			if(PartPriceConstants.SaaSOverrideType.UNIT.equals(overrideType)){
				return qli.getOverrideUnitPrc() == null ? "" : getFormattedPriceByPartType(qli,qli.getOverrideUnitPrc());
			}else if(PartPriceConstants.SaaSOverrideType.EXTENDED.equals(overrideType)){
				return qli.getOvrrdExtPrice() == null ? "" : getFormattedPriceByPartType(qli,qli.getOvrrdExtPrice());
			}else{
				return "";
			}
		}
		
	}

	/**
	 * @param qli
	 * @param isPriceUnAvaialbe
	 * @return
	 * String
	 * return the SaaS bid unit price, format the bid unit price as the same decimal as the override unit price decimal
	 */
	public String getSaaSBidUnitPrcVal(QuoteLineItem qli, boolean isPriceUnAvaialbe) {
		if (this.showLineitemPrice(qli, isPriceUnAvaialbe) && (qli.getLocalUnitProratedDiscPrc()!=null)) {
			return qli.getLocalUnitProratedDiscPrc() == null ? "" : getFormattedPriceByPartType(qli,qli.getLocalUnitProratedDiscPrc());
		} else {
			return "";
		}
	}

	public boolean showSrvcAgrmntNum() {
		if(!StringUtils.isBlank(quote.getQuoteHeader().getRefDocNum())){
			return true;
		}
		return false;
	}

	public String getSrvcAgrmntNum() {
		return StringUtils.isBlank(quote.getQuoteHeader().getRefDocNum()) ? "" : quote.getQuoteHeader().getRefDocNum();
	}

	/**
	 * @param subSaaSlineItemList
	 * @param currentIndex
	 * @return boolean
	 * judge whether show the replaced the parts label
	 */
	public boolean showRepacePartsLabel(List subSaaSlineItemList, int currentIndex){
		QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemList.get(currentIndex);
		if(currentIndex == 0){
			if(qli.isReplacedPart()){
				return true;
			}
		} else {
			if(qli.isReplacedPart()){
				QuoteLineItem previousQli = (QuoteLineItem) subSaaSlineItemList.get(currentIndex - 1);
				if(!previousQli.isReplacedPart()){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param qli
	 * @return boolean
	 * judge whether show manual sort section
	 */
	public boolean showManualSort(QuoteLineItem qli) {
		if(qli.isReplacedPart()){
			return false;
		}
		return true;
	}

	/**
	 * @param subSaaSlineItemList
	 * @return boolean
	 * judge whether show estimated provisioning days section
	 */
	public boolean showEstmtdPrvsningDays(PartsPricingConfiguration configrtn){
		if(configrtn == null){
			return false;
		}else{
			return configrtn.isAddOnTradeUp() || configrtn.isNewCaCoterm();
		}
	}

	public boolean isAddOnTradeUpConfigrtn(String configurationId){
		PartsPricingConfiguration configrtn = QuoteCommonUtil.getPartsPricingConfigurationById(configurationId, quote);
		if(configrtn == null){
			return false;
		}else{
			return PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configrtn.getConfigrtnActionCode());
		}
	}

	public String getConfigrtnEndDate(PartsPricingConfiguration configrtn){
		if(configrtn.getEndDate() == null)
			return "N/A";
		return DateUtil.formatDate(configrtn.getEndDate(), "dd-MMM-yyyy", this.locale);
	}

	/**
	 *
	 * @param configrtn
	 * @return add for submitted quote UI
	 */
	public String isTermExtension(PartsPricingConfiguration configrtn){
		if(configrtn.isTermExtension())
			return PartPriceViewKeys.YES_LABEL;
		return PartPriceViewKeys.NO_LABEL;
	}

	public String getServiceDateModType(PartsPricingConfiguration configrtn){
		String typeString = "";
		if(configrtn.getServiceDateModType() == null){
			typeString =  "N/A";
		}else{
			ServiceDateModType type = configrtn.getServiceDateModType();
			switch(type){
			case CE:
				typeString =  PartPriceViewKeys.EX_END_DATE;
				break;
			case LS:
				typeString =  PartPriceViewKeys.EX_LASTEST_ACPT;
				break;
			case RS:
				typeString =  PartPriceViewKeys.EX_REQ_START_DATE;
				break;
			}
		}
		return typeString;
	}

	public String getServiceDate(PartsPricingConfiguration configrtn){
		if(configrtn.getServiceDate() == null)
			return "N/A";
		return DateUtil.formatDate(configrtn.getServiceDate(), "dd-MMM-yyyy", this.locale);
	}

	public String getBillgFrqncyDscr(QuoteLineItem lineItem) {
		return lineItem.getBillgFrqncyDscr() == null ? DraftQuoteConstants.BLANK : lineItem.getBillgFrqncyDscr();
	}
	public boolean showNAQty(QuoteLineItem lineItem){
		return PartPriceSaaSPartConfigFactory.singleton().isNoQty(lineItem);
	}
	/**
	 * @param lineItem
	 * @return
	 * if is ramp-up part then show ramp-up period
	 * else if part is usage part and has related ramp-up part then also show ramp-up period
	 */
	public boolean showRampUpPeriodForSubmitted(QuoteLineItem lineItem){
		return (lineItem.isRampupPart()
				|| (lineItem.getRampUpPeriodNum() > 0));
	}

	public boolean showLineItemPriorSSEntitledUnitPrice(QuoteLineItem lineItem){
		return (((lineItem.getPriorYearSSPrice() != null)
				     && lineItem.getPriorYearSSPrice().isShowPriorYearPrice()) 
				|| ((lineItem.getYtyGrowth() != null) && lineItem.getYtyGrowth().getManualLPP() != null && !GDPartsUtil.isEligibleRenewalPart(lineItem)));
	}

	public String getLineItemPriorSSEntitledUnitPrice(QuoteLineItem lineItem){
		ApplicationContext context = ApplicationContextFactory.singleton()
		.getApplicationContext();
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)){
			Double lppPrice = GrowthDelegationUtil.getUnitLppPriceAcceptCurrencyMismatch(lineItem, quote.getQuoteHeader());
			
			if(lppPrice == null){
				return context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale, SubmittedQuoteMessageKeys.PRIOR_PRICE_COULD_NOT_BE_CALCULATED);
			}else{
				return getFormattedPriceByPartType(lineItem, lppPrice);
			}
		}else{
			return DraftQuoteConstants.BLANK;
		}
	}
	
	public String getOldLineItemPriorSSEntitledUnitPrice(QuoteLineItem lineItem){
		ApplicationContext context = ApplicationContextFactory.singleton()
		.getApplicationContext();
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)){
			Double lppPrice = GrowthDelegationUtil.getOldUnitLppPriceAcceptCurrencyMismatch(lineItem, quote.getQuoteHeader());
			
			if(lppPrice == null){
				return context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale, SubmittedQuoteMessageKeys.PRIOR_PRICE_COULD_NOT_BE_CALCULATED);
			}else{
				return getFormattedPriceByPartType(lineItem, lppPrice);
			}
		}else{
			return DraftQuoteConstants.BLANK;
		}
	}

	public String getPriorSSCurrncyCode(QuoteLineItem lineItem){
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)){
			if(lineItem.getPriorYearSSPrice() == null){
				return DraftQuoteConstants.BLANK;
			}
			String lppCurrency = lineItem.getPriorYearSSPrice().getPriorYrCurrncyCode();
			String quoteCurrency = quote.getQuoteHeader().getCurrencyCode();
			
			if(StringUtils.isBlank(lppCurrency)){
				return DraftQuoteConstants.BLANK;
			} else {
				if(StringUtils.equals(lppCurrency, quoteCurrency)){
					return DraftQuoteConstants.BLANK;
				} else {
					//Check if user has already provided manual currency conversion
					YTYGrowth yty = lineItem.getYtyGrowth();
					if(yty == null){
						return lppCurrency;
					} else {
						if(yty.getManualLPP() != null){
							return quoteCurrency;
						} else {
							return lppCurrency;
						}
					}
				}
			}
		} else {
			return DraftQuoteConstants.BLANK;
		}
	}
	public String getOldPriorSSCurrncyCode(QuoteLineItem lineItem){
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)){
			String lppCurrency = lineItem.getPriorYearSSPrice() == null ? null : lineItem.getPriorYearSSPrice().getPriorYrCurrncyCode();
			if(StringUtils.isBlank(lppCurrency)){
				return DraftQuoteConstants.BLANK;
			} else {
				return lppCurrency;
			}
		} else{
			return DraftQuoteConstants.BLANK;
		}
	}
	 

	public String getPriorSSLable(QuoteLineItem lineItem) {
		ApplicationContext context = ApplicationContextFactory.singleton()
		.getApplicationContext();
		if(locale == null){
			locale = Locale.US;
		}
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)) {
			if (lineItem.getPriorYearSSPrice() != null && lineItem.getPriorYearSSPrice().isEvolved()) {
				return context.getI18nValueAsString(
						I18NBundleNames.BASE_MESSAGES, locale,
						ViewKeys.PRIOR_SS_PRICE_EVOLVED);
			} else if (lineItem.getPriorYearSSPrice().isChannel()) {
				return context.getI18nValueAsString(
						I18NBundleNames.BASE_MESSAGES, locale,
						ViewKeys.PRIOR_SS_PRICE_CHANNEL);
			}
		}
		return DraftQuoteConstants.BLANK;
	}

	public String getPriorSSEvolvedLable(QuoteLineItem lineItem) {
		ApplicationContext context = ApplicationContextFactory.singleton()
		.getApplicationContext();
		if(locale == null){
			locale = Locale.US;
		}
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)) {
			if (lineItem.getPriorYearSSPrice() != null && lineItem.getPriorYearSSPrice().isEvolved()) {
				return context.getI18nValueAsString(
						I18NBundleNames.BASE_MESSAGES, locale,
						ViewKeys.PRIOR_SS_PRICE_EVOLVED);
			}
		}
		return DraftQuoteConstants.BLANK;
	}

	public String getPriorSSChannelLable(QuoteLineItem lineItem) {
		ApplicationContext context = ApplicationContextFactory.singleton()
		.getApplicationContext();
		if (locale == null) {
			locale = Locale.US;
		}
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)) {
			if (lineItem.getPriorYearSSPrice() != null && lineItem.getPriorYearSSPrice().isChannel()) {
				return context.getI18nValueAsString(
						I18NBundleNames.BASE_MESSAGES, locale,
						ViewKeys.PRIOR_SS_PRICE_CHANNEL);
			}
		}
		return DraftQuoteConstants.BLANK;
	}



	public boolean showLineItemPriorSSEntitledUnitPriceHeader(){
		List<QuoteLineItem> lineItems = getLineItems();
		if(lineItems != null){
			for(QuoteLineItem item : lineItems){

				if ((item.getPriorYearSSPrice() != null)
						&& item.getPriorYearSSPrice().isShowPriorYearPrice()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return
	 * if quote has SaaS part, show total BP extended price column
	 * else return false
	 */
	public boolean showTotBpExtndPrcColumn(){
		if((quote.getSaaSLineItems() == null) || (quote.getSaaSLineItems().size() == 0)){
			return true;
		}
		return false;
	}

	/**
	 * @return
	 * if quote has SaaS part, show BP TCV column for software part
	 * else return false
	 */
	public boolean showBpTcvColumn(){
		if((quote.getSaaSLineItems() != null) && (quote.getSaaSLineItems().size() > 0)){
			return true;
		}
		if (quote.getMonthlySwQuoteDomain().getMonthlySoftwares() != null
				&& quote.getMonthlySwQuoteDomain().getMonthlySoftwares().size() > 0) {
			return true;
		}
		return false;
	}

	public String getLineItemBidExtendedPrc(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
		if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe) && (lineItem.getLocalExtProratedDiscPrc() != null)) {
			return getFormattedPriceByPartType(lineItem, lineItem.getLocalExtProratedDiscPrc());
		} else {
			return DraftQuoteConstants.BLANK;
		}
	}

	/**
	 * @param lineItem
	 * @param isPriceUnAvaialbe
	 * @return if SaaS part TCV active flag is 1, return BP tcv
	 * else return ChannelExtndPrice
	 */
	public String getLineItemBpTCV(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
		if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe)) {
			String strChannelExtndPrice = "";
			String strSaasBpTCV = "";
			if(lineItem.getChannelExtndPrice() != null){
				strChannelExtndPrice = getFormattedPriceByPartType(lineItem, lineItem.getChannelExtndPrice());
			}else {
				strChannelExtndPrice = DraftQuoteConstants.BLANK;
			}
			if(lineItem.getSaasBpTCV() != null){
				strSaasBpTCV = getFormattedPriceByPartType(lineItem, lineItem.getSaasBpTCV());
			}else {
				strSaasBpTCV = DraftQuoteConstants.BLANK;
			}
			if(lineItem.isSaasTcvAcv()){
				return strSaasBpTCV;
			} else {
				return strChannelExtndPrice;
			}
		} else {
			return DraftQuoteConstants.BLANK;
		}
	}

	public String getLineItemEntitledUnitPrc(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {

		//always display entitled price for EOL parts that have no history price
		if(lineItem.isObsoletePart()
				&& !lineItem.isHasEolPrice()
				&& (lineItem.getLocalUnitProratedPrc() != null)){
			return getFormattedPriceByPartType(lineItem, lineItem.getLocalUnitProratedPrc());
		}

		if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe) && (lineItem.getLocalUnitProratedPrc()!=null)) {
			return getFormattedPriceByPartType(lineItem, lineItem.getLocalUnitProratedPrc());
		} else {
			return "";
		}
	}

	public String getLineItemEntitledExtendedPrc(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
		if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe) && (lineItem.getLocalExtProratedPrc()!=null)) {
			return getFormattedPriceByPartType(lineItem, lineItem.getLocalExtProratedPrc());
		} else {
			return DraftQuoteConstants.BLANK;
		}
	}

	/**
	 *show Renewal PriceMethod
	 * @param lineItem
	 * @return
	 */
	public boolean showRenewalPriceMethod(QuoteLineItem lineItem,QuoteUserSession session) {
		if (StringUtils.isNotBlank(lineItem.getRenewalQuoteNum()) && StringUtils.isNotBlank(lineItem.getRenewalPricingMethod())) {
			return PartPriceConfigFactory.singleton().showRenewalPriceMethod(
					lob, quote.getQuoteHeader().getQuoteTypeCode(),
					quote.getQuoteHeader().getQuoteStageCode(),getAccessLevel(session));
		}
		return false;
	}

	public boolean showRenewalPriceRSVP(QuoteLineItem lineItem,QuoteUserSession session) {
		if (StringUtils.isNotBlank(lineItem.getRenewalQuoteNum()) && (lineItem.getRenewalRsvpPrice() != null)) {
			return PartPriceConfigFactory.singleton().showRenewalPriceRSVP(lob,
					quote.getQuoteHeader().getQuoteTypeCode(),
					quote.getQuoteHeader().getQuoteStageCode(),getAccessLevel(session));
		}
		return false;
	}
	
	public boolean showRenewalPriceRSVP(QuoteLineItem lineItem,QuoteUserSession session,Quote quote) {
		boolean isShow = showRenewalPriceRSVP(lineItem,session) || GrowthDelegationUtil.isShowYTYReferenceInfoForBidIteration(quote);
		return isShow;
	}

	public boolean showRenewalPricePYP(QuoteLineItem lineItem,QuoteUserSession session) {
		if (showLineItemPriorSSEntitledUnitPrice(lineItem)) {
			return PartPriceConfigFactory.singleton().showRenewalPricePYP(lob,
					quote.getQuoteHeader().getQuoteTypeCode(),
					quote.getQuoteHeader().getQuoteStageCode(),getAccessLevel(session));
		}
		return false;
	}

	public String getRenewalRsvpPrice(QuoteLineItem lineItem,QuoteUserSession session){
		if (showRenewalPriceRSVP(lineItem,session)){
			return getFormattedPriceByPartType(lineItem, lineItem.getRenewalRsvpPrice());
		}
		return "N/A";
	}

	public String getBidDiscountOffRSVPPrice(QuoteLineItem lineItem,
			QuoteUserSession session) {
		if (showRenewalPriceRSVP(lineItem, session)) {
			if(lineItem.getYtyGrowth() != null){
				double discOffRsvpBidUnit = lineItem.getYtyGrowth().getDiscOffRsvpBidUnit();
				if (discOffRsvpBidUnit == 0) {
					return "0.00%";
				} else {
					return discOffRsvpBidUnit + "%";
				}
			}
		}
		return "N/A";
	}

	public String getDiscountOffRSVPPrice(QuoteLineItem lineItem,
			QuoteUserSession session) {
		if (showRenewalPriceRSVP(lineItem, session)) {
			if(lineItem.getYtyGrowth() != null){
				double discOffRsvpUnit = lineItem.getYtyGrowth().getDiscOffRsvpUnit();
				if (discOffRsvpUnit == 0) {
					return "0.00%";
				} else {
					return discOffRsvpUnit + "%";
				}
			}
		}
		return "N/A";
	}
	
	public String getYtyUnitPrice(QuoteLineItem lineItem,
			QuoteUserSession session) {
		if (showRenewalPriceRSVP(lineItem, session)) {
			if(lineItem.getYtyGrowth() != null
					&& lineItem.getYtyGrowth().getYtyUnitPrice() != null){
				double ytyUnitPrice = lineItem.getYtyGrowth().getYtyUnitPrice()!=null?lineItem.getYtyGrowth().getYtyUnitPrice():0.0;
				return getFormattedPrice(ytyUnitPrice);
			}
		}
		return "N/A";
	}
	
	public String getYtyBidUnitPrice(QuoteLineItem lineItem, QuoteUserSession session) {
		String priorSSPrice = getLineItemPriorSSEntitledUnitPrice(lineItem);
		boolean isSpecialBid = isSpecialBid();
		if (showRenewalPriceRSVP(lineItem, session)) {
			if(lineItem.getYtyGrowth() != null && lineItem.getYtyGrowth().getYtyBidUnitPrice() != null){
				Double rsvpPrice = lineItem.getRenewalRsvpPrice()!=null?lineItem.getRenewalRsvpPrice():0.0;
				String rsvpPriceStr = getFormattedPriceByPartType(lineItem, lineItem.getRenewalRsvpPrice());
				Double ytyBidUnitPriceDou = lineItem.getYtyGrowth().getYtyBidUnitPrice()!=null?lineItem.getYtyGrowth().getYtyBidUnitPrice():0.0;
				String bidUnitPriceStr =  getFormattedPrice(ytyBidUnitPriceDou);
				if(quote.getQuoteHeader().isSubmittedQuote()){
					if(lineItem.isSetLineToRsvpSrpFlag()){
						
						if(rsvpPrice != null && !"".equals(rsvpPrice)){
							if(ytyBidUnitPriceDou > rsvpPrice){
								return "<span class='ibm-price'>"+rsvpPriceStr+"</span>";
							}
						}
					}
					if (!priorSSPrice.trim().equals(bidUnitPriceStr.trim())
							&& isSpecialBid 
							&& StringUtils.isNotBlank(lineItem.getRenewalQuoteNum())){
						return "<span class='ibm-price'>"+bidUnitPriceStr+"</span>";
					}
				}
				if(lineItem.isSetLineToRsvpSrpFlag()){
				
					if(rsvpPrice != null && !"".equals(rsvpPrice)){
						if(ytyBidUnitPriceDou > rsvpPrice){
							return rsvpPriceStr;
						}
					}
				}
				return bidUnitPriceStr;
				
			}
		}
		return "N/A";
	}
	
	
	 public boolean isSpecialBid() {
	       QuoteHeader header = quote.getQuoteHeader();
	       return header.getSpeclBidManualInitFlg() == 1 || header.getSpeclBidSystemInitFlg() == 1;
	 }


	public String getRenewalPricingMethod(QuoteLineItem lineItem,QuoteUserSession session){

		String prm=  DraftQuoteConstants.BLANK;
		if(quote.getQuoteHeader().isFCTQuote()){
			lineItem.setRenewalPricingMethod(PartPriceConstants.PRICING_METHOD_RSVP_SRP);
		}
		
		if (showRenewalPriceMethod(lineItem,session)){
			CodeDescObj codeObj;
			try {
				codeObj = RenewalPricingMethodCodeFactory.singleton().findRenewalPricingMethodByCode(lineItem.getRenewalPricingMethod());

				if (codeObj != null) {
					prm = codeObj.getCodeDesc();
				}
			} catch (TopazException e) {
				logContext.debug(this, "Can not get RenewalPricingMethodCode description. Renewal quote num = " + lineItem.getRenewalQuoteNum());
			}
		}
		return prm;
	}

	private String getAccessLevel(QuoteUserSession session){
		int accessLevel = session.getAccessLevel(QuoteConstants.APP_CODE_SQO);
		return String.valueOf(accessLevel);
	}

	public String getRSVPLable(){
		ApplicationContext context = ApplicationContextFactory.singleton()
		.getApplicationContext();
		if(locale == null){
			locale = Locale.US;
		}
		if (isPA()){
			return context.getI18nValueAsString(
					I18NBundleNames.BASE_MESSAGES, locale,
					ViewKeys.RSVP_PRICE);
		} else if (isPAE() || isFCT()){
			return context.getI18nValueAsString(
					I18NBundleNames.BASE_MESSAGES, locale,
			ViewKeys.SRP_PRICE);
		}
		return DraftQuoteConstants.BLANK;
	}

	public String getYtyGrwothLable(){
		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
		if(locale == null){
			locale = Locale.US;
		}
		return context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,ViewKeys.YTY_GRWOTH);
	}

	public String getYtyGrwothTips(){
		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
		if(locale == null){
			locale = Locale.US;
		}
		return context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,ViewKeys.YTY_GRWOTH_TIPS);
	}

	public String getDiscountOffRSVPSRPLabel(){
		ApplicationContext context = ApplicationContextFactory.singleton()
		.getApplicationContext();
		if(locale == null){
			locale = Locale.US;
		}
		if (isPA()){
			return context.getI18nValueAsString(
					I18NBundleNames.BASE_MESSAGES, locale,
					ViewKeys.DISCOUNT_RSVP);
		} else if (isPAE() || isFCT()){
			return context.getI18nValueAsString(
					I18NBundleNames.BASE_MESSAGES, locale,
					ViewKeys.DISCOUNT_SRP);
		}
		return DraftQuoteConstants.BLANK;
	}

	/**
	 * Show Main Appliance information row in Line Item
	 * @param lineItem
	 * @return
	 */
	public boolean showApplianceInformation(QuoteLineItem lineItem, boolean isDraftQuote, boolean isSalesQuote) {
		//If the quote is Sales Quote
		if(isSalesQuote){
			PartPriceAppliancePartConfigFactory factory = PartPriceAppliancePartConfigFactory.singleton();
			return factory.displayApplianceInfoDraft(lineItem);
		}
		//If the quote is Renewal Quote
		else if (quote.getQuoteHeader().isRenewalQuote()){
			return false;
		}else{
			return showApplianceMtm(lineItem, isDraftQuote, isSalesQuote);
		}
	}



	/**
	 * Show Appliance association row in Line Item
	 * @param lineItem
	 * @return
	 */
	public boolean showApplianceAssociation(QuoteLineItem lineItem, boolean isDraftQuote, boolean isSalesQuote) {
		//If the quote is sales quote
		if(isSalesQuote){
			PartPriceAppliancePartConfigFactory factory = PartPriceAppliancePartConfigFactory.singleton();
			return factory.displayApplianceAssociationInfoDraft(lineItem)||lineItem.isDisplayModelAndSerialNum();
		}else{
			return showApplianceMtm(lineItem, isDraftQuote, isSalesQuote);
		}
	}

	public boolean showPocInformation(QuoteLineItem lineItem) {
		PartPriceAppliancePartConfigFactory factory = PartPriceAppliancePartConfigFactory.singleton();
		return factory.displayPoc(lineItem);
	}

	public boolean showApplianceMtm(QuoteLineItem lineItem, boolean isDraftQuote, boolean isSalesQuote) {
		boolean isShowApplncMtm = false;
		PartPriceAppliancePartConfigFactory factory = PartPriceAppliancePartConfigFactory.singleton();

		if(isSalesQuote){
			//For Ownership transfered parts
	    	if (lineItem.isOwerTransferPart()) {
	    		return true;
			}
			if(isDraftQuote){
				if(lineItem.isDisplayModelAndSerialNum()) {
					isShowApplncMtm = true;
				} else {
					isShowApplncMtm = factory.allowMtmInput(lineItem);
				}
			}
			else{
				//Appliance main part
				if(lineItem.isApplncMain()){
					if(isMtmNotBlank(lineItem)){
						isShowApplncMtm = true;
					}else{
						isShowApplncMtm = false;
					}
				}

				//Appliance Upgrade part
				else if(lineItem.isApplncUpgrade())
					isShowApplncMtm = true;


				else if(lineItem.isApplncServicePack()
						|| lineItem.isApplncServicePackRenewal()
						|| lineItem.isApplncRenewal()
						|| lineItem.isApplncReinstatement()
						|| lineItem.isApplianceRelatedSoftware()){
					if(lineItem.isHasApplncId()){
						isShowApplncMtm = false;
					}else{
						isShowApplncMtm = true;
					}
				}


				else if(lineItem.isApplncTransceiver()){
					isShowApplncMtm = false;
				}

                /*else if (lineItem.isApplianceRelatedSoftware()) {
                    isShowApplncMtm = true;
                    
                }*/

				isShowApplncMtm = isShowApplncMtm && !QuoteCommonUtil.isApplncQtyGtOne(lineItem);
			}

		}
		else if (quote.getQuoteHeader().isRenewalQuote()){
			isShowApplncMtm = factory.allowMtmInput(lineItem);

		}
		else{
			if(isMtmNotBlank(lineItem)){
				isShowApplncMtm = true;
			}else{
				isShowApplncMtm = false;
			}
		}
		return isShowApplncMtm;
	}

	public boolean showApplianceId(QuoteLineItem lineItem, boolean isSalesQuote){
		boolean isShowApplianceId = false;
		if(isSalesQuote){
			if(!lineItem.isHasApplncId()){
				isShowApplianceId = false;
			} else {
				isShowApplianceId = true;
			}
		}
		return isShowApplianceId;
	}

	public boolean showCustomerCommitedDate(QuoteLineItem lineItem, boolean isSalesQuote){
		boolean isShowCustomerCommitedDate = false;
		if(isSalesQuote && lineItem.getCustCmmttdArrivlDate() != null
				 && !QuoteCommonUtil.isApplncQtyGtOne(lineItem)){
			isShowCustomerCommitedDate = true;
		}
		return isShowCustomerCommitedDate;
	}

	public String getCustomerCommitedDate(QuoteLineItem lineItem){
		return DateUtil.formatDate(lineItem.getCustCmmttdArrivlDate(), DateUtil.PATTERN1, locale);
	}

	public boolean isMtmNotBlank(QuoteLineItem lineItem){
		return (!StringUtils.isBlank(lineItem.getMachineType())
				|| !StringUtils.isBlank(lineItem.getModel())
				|| !StringUtils.isBlank(lineItem.getSerialNumber())
				);
	}
	public boolean showMachineType(QuoteLineItem lineItem){
		return !StringUtils.isBlank(lineItem.getMachineType()) ;
	}

	public boolean showModel(QuoteLineItem lineItem){
		return !StringUtils.isBlank(lineItem.getModel()) ;
	}

	public boolean showSerialNumber(QuoteLineItem lineItem){
		return !StringUtils.isBlank(lineItem.getSerialNumber()) ;
	}

	/**
	 * @param lineItem
	 * @return if login application is not PGS and part is not replaced part, return true
	 */
	public boolean isDisplayMigration(QuoteLineItem lineItem, boolean isPGSFlag){
		if(quote.getQuoteHeader().isSalesQuote()){
			if((quote.getQuoteHeader().isPAQuote()
				|| quote.getQuoteHeader().isPAEQuote()
				|| quote.getQuoteHeader().isPAUNQuote()
				|| quote.getQuoteHeader().isFCTQuote())
				&& !quote.getQuoteHeader().isFCTToPAQuote()
				&& !quote.getQuoteHeader().isOEMQuote()){
				if(!isPGSFlag
					&& !lineItem.isReplacedPart()
					&& (lineItem.isSaasSubscrptnPart() || lineItem.isMonthlySoftwarePart() && ((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart())){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get entitle rate value
	 * @param item
	 * @return
	 */
	public String getEntitleRateValForSaasPart(QuoteLineItem qli, PartPriceCommon partPriceCommon, boolean isPriceUnAvaialbe, boolean isTier2Reseller) {
		String entitledRateVal = DraftQuoteConstants.BLANK;
    	if(!isEntitledRateAndBidRateBlank(qli, isTier2Reseller) && !qli.isReplacedPart()){
    		if(partPriceCommon.showSaaSEntitledUnitPrice(qli)){
    			entitledRateVal = partPriceCommon.getLineItemEntitledUnitPrc(qli,isPriceUnAvaialbe);
	    	} else if(partPriceCommon.showItemTotContractVal(qli) && partPriceCommon.showSaaSEntitledExtPrice(qli) && qli.isSaasSubscrptnPart()){
	    		entitledRateVal = partPriceCommon.getLineItemEntitledExtendedPrc(qli,isPriceUnAvaialbe);
	    	}
    	}
    	return entitledRateVal;
	}

	/**
	 * Get bid rate value
	 * @param qli
	 * @param partPriceCommon
	 * @param isPriceUnAvaialbe
	 * @param isTier2Reseller
	 * @return
	 */
	public String getBidRateValForSaasPart(QuoteLineItem qli, PartPriceCommon partPriceCommon, boolean isPriceUnAvaialbe, boolean isTier2Reseller) {
		String bidRateVal = DraftQuoteConstants.BLANK;
		if(!isEntitledRateAndBidRateBlank(qli, isTier2Reseller)){
    		if(partPriceCommon.showSaaSBidUnitPrice(qli)){
    			bidRateVal = partPriceCommon.getSaaSBidUnitPrcVal(qli,isPriceUnAvaialbe);
	    	} else if(partPriceCommon.showItemTotContractVal(qli) && partPriceCommon.showSaaSBidExtPrice(qli) && qli.isSaasSubscrptnPart()){
	    		bidRateVal = partPriceCommon.getLineItemBidExtendedPrc(qli,isPriceUnAvaialbe);
	    	}
    	}
    	return bidRateVal;
	}

	/**
	 * isEntitledRateAndBidRateBlank
	 * @param qli
	 * @param isTier2Reseller
	 * @return
	 */
	private boolean isEntitledRateAndBidRateBlank(QuoteLineItem qli, boolean isTier2Reseller) {
		if(isTier2Reseller
        	&& !ApplicationProperties.getInstance().getT2PriceAvailable()
        	&& qli.isReplacedPart()){
        	return true;
        }
    	else if(quote.getQuoteHeader().isChannelQuote() && qli.isReplacedPart()){
    		return true;
    	}
		return false;
	}

	public String getYtyGrowthVal(QuoteLineItem qli) {
		if(qli.getYtyGrowth() != null && qli.getYtyGrowth().getYTYGrowthPct() != null)
			return  formatter.formatPoint(qli.getYtyGrowth().getYTYGrowthPct());
		return "";
	}

	public boolean showRenewalYTYGrowth(QuoteLineItem lineItem) {
		return GDPartsUtil.isEligibleRenewalPart(lineItem);
	}
	
	public boolean showYTYGrowth(QuoteLineItem lineItem) {
		return quote.getQuoteHeader() != null ? 
				(!quote.getQuoteHeader().isBidIteratnQt()) && GrowthDelegationUtil.isDisplayLineItemYTY(quote,lineItem) 
				: GrowthDelegationUtil.isDisplayLineItemYTY(quote,lineItem);
	}

	public boolean showYTYGrowth() {
		return quote.getQuoteHeader() != null  ? 
				(!quote.getQuoteHeader().isBidIteratnQt()) && GrowthDelegationUtil.isDisplayOverallYTYGrowthGoBtn(quote)
				: GrowthDelegationUtil.isDisplayOverallYTYGrowthGoBtn(quote);
	}
	
	public boolean showRenewalYTYGrowth() {
		return GDPartsUtil.isQuoteEligibleForRenewalGrowthDelegation(quote);
	}

	public boolean showOverallGrowthImpliedGrowth(QuoteLineItem lineItem) {
		if(showYTYGrowth(lineItem) && lineItem.getYtyGrowth() != null){
			return lineItem.getYtyGrowth().isIncludedInImpliedYTYGrowth();
		}
		return false;
	}

	public boolean showOverallGrowthExcludedImpliedGrowth(QuoteLineItem lineItem) {
		if(showYTYGrowth(lineItem) && lineItem.getYtyGrowth() != null){
			return lineItem.getYtyGrowth().isIncludedInOverallYTYGrowth();
		}
		return false;
	}




	/**
	 * judge whether renewal model is showing in subscription
	 * @param lineItem
	 * @return
	 */
	public boolean showRenwlModeForSubscrptn(QuoteLineItem lineItem){
		boolean isShow = false;
			if (lineItem.isSaasSubscrptnPart()&& !lineItem.isReplacedPart()
					&& lineItem.isSupportRenwlMdl() && !lineItem.isFixedRenwlMdl()){
				isShow = true;
			} else if (lineItem.isMonthlySoftwarePart()){
				if (((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart() && !lineItem.isReplacedPart()
						&& lineItem.isSupportRenwlMdl() && !lineItem.isFixedRenwlMdl())
					isShow = true;
			}
		return isShow;
	}

	public boolean showFixedModelForSubscrptn(QuoteLineItem lineItem) {
		boolean isShow = false;
		if ((lineItem.isSaasSubscrptnPart() && !lineItem.isReplacedPart() || lineItem.isSaasSubsumedSubscrptnPart())
				&& lineItem.isSupportRenwlMdl() && lineItem.isFixedRenwlMdl()) {
			isShow = true;
		} else if (lineItem.isMonthlySoftwarePart()){
			if (((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart() && !lineItem.isReplacedPart()
					&& lineItem.isSupportRenwlMdl() && lineItem.isFixedRenwlMdl())
				isShow = true;
		}
		return isShow;
	}
	
	public boolean showRenewalButton(QuoteLineItem lineItem){
		boolean isShow = true;
		if(lineItem.isSaasSubsumedSubscrptnPart()){
			isShow = false;
		}
		return isShow;
	}

	public String getRewalModCodeDesc(QuoteLineItem lineItem) {
		String rewalModCode = lineItem.getRenwlMdlCode();
		if (PartPriceConstants.RenewalModelCode.C
				.equalsIgnoreCase(rewalModCode)) {
			return PartPriceViewKeys.CONTINUOUSLY_BILL;
		} else if (PartPriceConstants.RenewalModelCode.O
				.equalsIgnoreCase(rewalModCode)) {
				return PartPriceViewKeys.RENEW_SERVICE_TERM_ORIGINAL;
		} else if (PartPriceConstants.RenewalModelCode.R
				.equalsIgnoreCase(rewalModCode)) {
			return PartPriceViewKeys.RENEW_SERVICE_TERM_MONTHS;
		} else if (PartPriceConstants.RenewalModelCode.T
				.equalsIgnoreCase(rewalModCode)) {
			return PartPriceViewKeys.TERMINATE_END_TERM;
		} else {
			return "";
		}
	}
	
	/**
	 * judge whether the equity curve of the part is showing
	 * @param lineItem
	 * @return
	 */
	public boolean showEquityCurveFlag(QuoteLineItem lineItem){
		if (null == lineItem){
			return false;
		}
		EquityCurve equityCurve = lineItem.getEquityCurve();
		boolean showFlag = null != equityCurve && equityCurve.isEquityCurveFlag() && equityCurve.isEquityCurveCtrolFlag();
		if (!showFlag){
			return false;
		}
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		if (null == quoteHeader){
			return false;
		}
		return !quoteHeader.isBidIteratnQt() && !quoteHeader.isCopied4PrcIncrQuoteFlag();
	}
	
	/**
	 * judge whether the equity curve  is calculated
	 * @param lineItem
	 * @return false when equity curve cannot be calculated
	 */
	public boolean showEquityCurveCalculateFlag(QuoteLineItem lineItem){
		if (null == lineItem || null == lineItem.getLocalUnitProratedPrc()){
			return false;
		}
		EquityCurve equityCurve = lineItem.getEquityCurve();
		return null != equityCurve &&  null != equityCurve.getMinDiscount() && null != equityCurve.getMaxDiscount();
	}

	/**
	 * judge whether the purchase history of the equity curve is showing
	 * @param lineItem
	 * @return
	 */
	public boolean showPurchaseHistAppliedFlag(QuoteLineItem lineItem){
		if (null == lineItem){
			return false;
		}
		EquityCurve equityCurve = lineItem.getEquityCurve();
		return null != equityCurve && !QuoteConstants.EC_QT_PURCH_HIST_NO.equalsIgnoreCase(equityCurve.getPurchaseHistAppliedFlag());
	}
	
	/**
	 * judge whether the purchase history of the equity curve is showing
	 * @param lineItem
	 * @return
	 */
	public boolean showPurchaseHistAppliedFlagWithTopPerformer(QuoteLineItem lineItem){
		if (null == lineItem){
			return false;
		}
		EquityCurve equityCurve = lineItem.getEquityCurve();
		return null != equityCurve && (QuoteConstants.EC_QT_PURCH_HIST_TOP_PERFORMER.equalsIgnoreCase(equityCurve.getPurchaseHistAppliedFlag()) 
				|| QuoteConstants.EC_QT_PURCH_HIST_ALL.equalsIgnoreCase(equityCurve.getPurchaseHistAppliedFlag()));
	}
	
	/**
	 * judge whether the purchase history of the equity curve is showing
	 * @param lineItem
	 * @return
	 */
	public boolean showPurchaseHistAppliedFlagWithMarketAverage(QuoteLineItem lineItem){
		if (null == lineItem){
			return false;
		}
		EquityCurve equityCurve = lineItem.getEquityCurve();
		return null != equityCurve && (QuoteConstants.EC_QT_PURCH_HIST_MARKET_AVERAGE.equalsIgnoreCase(equityCurve.getPurchaseHistAppliedFlag()) 
				|| QuoteConstants.EC_QT_PURCH_HIST_ALL.equalsIgnoreCase(equityCurve.getPurchaseHistAppliedFlag()));
	}
	
	/**
	 * format the minimum discount of the equity curve
	 * @param lineItem
	 * @return
	 */
	public String getPreferDiscount(QuoteLineItem lineItem){
		if (null == lineItem){
			return DraftQuoteConstants.BLANK;
		}
		EquityCurve equityCurve = lineItem.getEquityCurve();
		if(null == equityCurve ||null == equityCurve.getMinDiscount()){
			return DraftQuoteConstants.BLANK;
		}
		return DecimalUtil.format(equityCurve.getMinDiscount(),2)+" %";
	}
	
	/**
	 * format the minimum bid unit price of the equity curve
	 * @param lineItem
	 * @return
	 */
	public String getPreferBidUnitPrice(QuoteLineItem lineItem){
		if (null == lineItem || null == lineItem.getMinBidUnitPrice()){
			return DraftQuoteConstants.BLANK;
		}
		return DecimalUtil.format(lineItem.getMinBidUnitPrice(),2);
	}
	
	/**
	 * format the minimum bid extended price of the equity curve
	 * @param lineItem
	 * @return
	 */
	public String getPreferBidExtendedPrice(QuoteLineItem lineItem){
		if (null == lineItem || null == lineItem.getMinBidExtendedPrice()){
			return DraftQuoteConstants.BLANK;
		}

		String  preferBidExtendedPrice = DecimalUtil.format(lineItem.getMinBidExtendedPrice(),2);
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		String  region = (null == quoteHeader.getCountry()|| null == quoteHeader.getCountry().getWWRegion()) ? "":quoteHeader.getCountry().getWWRegion().trim();
		if ("JAPAN".equalsIgnoreCase(region) && preferBidExtendedPrice.endsWith(".00")){
			return preferBidExtendedPrice.substring(0, preferBidExtendedPrice.length()-3);
		}
		return preferBidExtendedPrice;
	}
	
	/**
	 * format the maximum discount of the equity curve
	 * @param lineItem
	 * @return
	 */
	public String getMaxDiscount(QuoteLineItem lineItem){
		if (null == lineItem){
			return DraftQuoteConstants.BLANK;
		}
		EquityCurve equityCurve = lineItem.getEquityCurve();
		if(null == equityCurve ||null == equityCurve.getMaxDiscount()){
			return DraftQuoteConstants.BLANK;
		}
		return DecimalUtil.format(equityCurve.getMaxDiscount(),2)+" %";
	}
	
	/**
	 * format the maximum bid unit price of the equity curve
	 * @param lineItem
	 * @return
	 */
	public String getMaxBidUnitPrice(QuoteLineItem lineItem){
		if (null == lineItem || null == lineItem.getMaxBidUnitPrice()){
			return DraftQuoteConstants.BLANK;
		}
		return DecimalUtil.format(lineItem.getMaxBidUnitPrice(),2);
	}
	
	/**
	 *  format the maximum bid extended price of the equity curve
	 * @param lineItem
	 * @return
	 */
	public String getMaxBidExendedPrice(QuoteLineItem lineItem){
		if (null == lineItem || null == lineItem.getMaxBidExendedPrice()){
			return DraftQuoteConstants.BLANK;
		}

		String maxBidExendedPrice =  DecimalUtil.format(lineItem.getMaxBidExendedPrice(),2);
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		String  region = (null == quoteHeader.getCountry()|| null == quoteHeader.getCountry().getWWRegion()) ? "":quoteHeader.getCountry().getWWRegion().trim();
		if ("JAPAN".equalsIgnoreCase(region) && maxBidExendedPrice.endsWith(".00")){
			return maxBidExendedPrice.substring(0, maxBidExendedPrice.length()-3);
		}
		return maxBidExendedPrice;
	}
	
	/**
	 * Annualized bid unit price / Prior S&S price -1
	 * @return
	 */
	 
	public String getUnitPriceGrowthPrice(QuoteLineItem lineItem,Quote quote){
		if (null == lineItem ){
			return DraftQuoteConstants.BLANK;
		}
		if(lineItem.getRenewalQuoteNum() != null && !"".equals(lineItem.getRenewalQuoteNum().trim())){
			Double annualizedBidUnitPrice = GrowthDelegationUtil.getAnnualYtyBidUnitPrice(lineItem);
			
			Double priorPrice = null;
			if(GrowthDelegationUtil.getUnitLppPriceAcceptCurrencyMismatch(lineItem,quote.getQuoteHeader()) != null){
				priorPrice = GrowthDelegationUtil.getUnitLppPriceAcceptCurrencyMismatch(lineItem,quote.getQuoteHeader());
			}
			if(lineItem.isSetLineToRsvpSrpFlag()){
				annualizedBidUnitPrice = lineItem.getRenewalRsvpPrice();
			}
			if(annualizedBidUnitPrice!= null && priorPrice != null && priorPrice.doubleValue() != 0 ){
				return DecimalUtil.format(( annualizedBidUnitPrice/priorPrice - 1)*100,2)+" %";
			}else{
				return DraftQuoteConstants.BLANK;
			}
			
		}
		
		 return DraftQuoteConstants.BLANK;
	}
	
	public boolean isDisabledOverrideDscYty(QuoteLineItem lineItem,QuoteUserSession session){
		if(quote.getQuoteHeader().isFCTQuote()){
			lineItem.setRenewalPricingMethod(PartPriceConstants.PRICING_METHOD_RSVP_SRP);
		}
		
		if (showRenewalPriceMethod(lineItem,session) && "OT".equalsIgnoreCase(lineItem.getRenewalPricingMethod())){
			return true;
		}
		return false;
	}
	
	/**
	 * Indicate whether the quote is CHANNEL and APAC/JAPAN region
	 * @return
	 * @since release 14.1  copy from PartsAndPrice class
	 */
	public boolean showChannelApJapan(){
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		String fullfillment = (null == quoteHeader.getFulfillmentSrc())?"":quoteHeader.getFulfillmentSrc().trim();
		String  region = (null == quoteHeader.getCountry()|| null == quoteHeader.getCountry().getWWRegion()) ? "":quoteHeader.getCountry().getWWRegion().trim();
		return "CHANNEL".equalsIgnoreCase(fullfillment) && ("APAC".equalsIgnoreCase(region)||"JAPAN".equalsIgnoreCase(region));
	}
	
	public String getApplncMTMValidationMsg(QuoteLineItem lineItem){
		String msg = "";
		
		//no validation and validate success
		// avoid null point exception
		if(!isShowApplncMTMMsg(lineItem)){ 
			return msg;
		} 
        DeployModel deployModel = lineItem.getDeployModel();
        Integer applncSerialNumWarngFlag = deployModel == null ? null : deployModel.getSerialNumWarningFlag();
		
		if (quote.getQuoteHeader().isSubmittedQuote()){
			
			if(PartPriceConstants.ApplncSerialNumWarngFlag.MTM_NO_EXIST.equals(applncSerialNumWarngFlag)){ // no exist
				msg = PartPriceViewKeys.APPLNC_MTM_NO_EXIST_SUBMITTED_MSG;
			} else if (PartPriceConstants.ApplncSerialNumWarngFlag.MTM_OF_DIFFERENT_CUSTOMER.equals(applncSerialNumWarngFlag) ){ // diffent customer
				msg = PartPriceViewKeys.APPLNC_MTM_DIFFERENT_CUSTMOER_SUBMITTED_MSG;
			} else if (PartPriceConstants.ApplncSerialNumWarngFlag.MTM_OF_DIFFERENT_CONTRACT.equals(applncSerialNumWarngFlag)){ //diffent contract
				msg = PartPriceViewKeys.APPLNC_MTM_DIFFERENT_CONTRACT_SUBMITTED_MSG;
			} else if (PartPriceConstants.ApplncSerialNumWarngFlag.MTM_OF_DIFFERENT_BRAND.equals(applncSerialNumWarngFlag) ) { //diffent brand
				msg = PartPriceViewKeys.APPLNC_MTM_DIFFERENT_BRAND_SUBMITTED_MSG;
			}
			
		} else {
			if(PartPriceConstants.ApplncSerialNumWarngFlag.MTM_NO_EXIST.equals(applncSerialNumWarngFlag) ){ // no exist
				msg = PartPriceViewKeys.APPLNC_MTM_NO_EXIST_DRAFT_MSG;
			} else if (PartPriceConstants.ApplncSerialNumWarngFlag.MTM_OF_DIFFERENT_CUSTOMER.equals(applncSerialNumWarngFlag)){ // diffent customer
				msg = PartPriceViewKeys.APPLNC_MTM_DIFFERENT_CUSTOMER_DRAFT_MSG;
			} else if (PartPriceConstants.ApplncSerialNumWarngFlag.MTM_OF_DIFFERENT_CONTRACT.equals(applncSerialNumWarngFlag)){ //diffent contract
				msg = PartPriceViewKeys.APPLNC_MTM_DIFFERENT_CONTRACT_DRAFT_MSG;
			} else if (PartPriceConstants.ApplncSerialNumWarngFlag.MTM_OF_DIFFERENT_BRAND.equals(applncSerialNumWarngFlag)) { //diffent brand
				msg = PartPriceViewKeys.APPLNC_MTM_DIFFERENT_BRAND_DRAFT_MSG;
			}
			
		}
		
		return msg;
		
	}
	
    public static boolean isShowApplncMTMMsg(QuoteLineItem lineItem) {
		boolean isShow = true;
        DeployModel deployModel = lineItem.getDeployModel();
        Integer applncSerialNumWarngFlag = deployModel == null ? null : deployModel.getSerialNumWarningFlag();
		if(applncSerialNumWarngFlag == PartPriceConstants.ApplncSerialNumWarngFlag.NO_VALIDATION ||
				PartPriceConstants.ApplncSerialNumWarngFlag.VALIDATION_SUCCESS.equals(applncSerialNumWarngFlag)){ 
			isShow = false;
        }
		return isShow;
	}

	 /**
	  * Get deployment association list. created by Grover.
	  * @param lineItem
	  * @return Collection
	  */
	public Collection getDeploymentAssociations(QuoteLineItem qli) {
		Collection associations = new ArrayList();
		Integer originalDeployOption = PartPriceConstants.DEPLOYMENT_SELECT_DEFAULT;
		if (null != qli.getDeployModel()&& PartPriceConstants.DEPLOYMENT_SELECT_DEFAULT != qli.getDeployModel().getDeployModelOption()){
			originalDeployOption = qli.getDeployModel().getDeployModelOption();
		}
		
		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
		String defaultDeploymentId = context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,PartPriceViewKeys.DEPLOYMENT_SELECT_DEFAULT);
		String deploymentNotOnQuote = context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,PartPriceViewKeys.DEPLOYMENT_NOT_ON_QUOTE);
		String newDeploymentID=context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,PartPriceViewKeys.DEPLOYMENT_NEW_ID);
 		String associatedDeploymentId=context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,PartPriceViewKeys.DEPLOYMENT_ASSOCIATED);
		// add default selected option
		associations.add(new SelectOptionImpl(defaultDeploymentId, PartPriceConstants.DEPLOYMENT_SELECT_DEFAULT.toString(),PartPriceConstants.DEPLOYMENT_SELECT_DEFAULT.equals(originalDeployOption)));
		associations.add(new SelectOptionImpl(deploymentNotOnQuote, PartPriceConstants.DEPLOYMENT_NOT_ON_QUOTE.toString(),PartPriceConstants.DEPLOYMENT_NOT_ON_QUOTE.equals(originalDeployOption)));
		associations.add(new SelectOptionImpl(newDeploymentID, PartPriceConstants.DEPLOYMENT_NEW_ID.toString(), PartPriceConstants.DEPLOYMENT_NEW_ID.equals(originalDeployOption)));


		List applanceLists = quote.getSoftwareLineItems();
		SortedSet<String> deployIds = new TreeSet<String>();
		Map<String,String> appAssociations = new HashMap<String,String>();
		for (int i = 0; i < applanceLists.size(); i++) {

			QuoteLineItem lineItem = (QuoteLineItem) applanceLists.get(i);
			// not the main appliance part
			if (!lineItem.isDeploymentAssoaciatePart()){
				continue;
			}
			DeployModel deployModel = lineItem.getDeployModel();
			// not assign the deployment id
			if (null == deployModel || StringUtils.isBlank(deployModel.getDeployModelId()) || PartPriceConstants.DEPLOYMENT_SELECT_DEFAULT.equals(deployModel.getDeployModelOption())){
				continue;
			}
			// filter the current deployment model identity for the fourth deployment option
			if (!PartPriceConstants.DEPLOYMENT_ASSOCIATED.equals(originalDeployOption)  && deployModel.getDeployModelId().equals(qli.getDeployModel().getDeployModelId())){
				continue;
			}
			String deployModelId = deployModel.getDeployModelId();
			String deployModelLabel = associatedDeploymentId + " " + deployModelId;
			
			deployIds.add(deployModelId);
			appAssociations.put(deployModelId, deployModelLabel);
		}
		String originalDeployID = "";
		if (PartPriceConstants.DEPLOYMENT_ASSOCIATED.equals(originalDeployOption)){
			originalDeployID =  qli.getDeployModel().getDeployModelId();
		}
		for (String deployId: deployIds)
		{
			String applianceLabel = appAssociations.get(deployId);
			associations.add(new SelectOptionImpl(applianceLabel, PartPriceConstants.DEPLOYMENT_ASSOCIATED.toString(),deployId.equals(originalDeployID)));
		}

		return associations;

	}
	
	public boolean isDisplayModelAndSerialNum(QuoteLineItem lineItem) {
		return lineItem.isDisplayModelAndSerialNum();
	}

	public String isDisabledMTM(QuoteLineItem qli) {
		String isDisabled = "false";
		
		if (1 == quote.getQuoteHeader().getRenwlQuoteSpeclBidFlag()) {
			isDisabled = "true";
		} 
		if (isDisplayModelAndSerialNum(qli)) {
			if (1 == quote.getQuoteHeader().getRenwlQuoteSpeclBidFlag()) {
				isDisabled = "true";
			} else {
				isDisabled = "false";
			}
		}
		
		return isDisabled;
	}
	
	//monthly
	public String getMonthlyConfigrtnEndDate(MonthlySoftwareConfiguration configrtn){
		if(configrtn.getEndDate() == null)
			return "N/A";
		return DateUtil.formatDate(configrtn.getEndDate(), "dd-MMM-yyyy", this.locale);
	}
	
	// to determin if line item should show " is this a new service" question
	public boolean isShowIsNewServiceLine(QuoteLineItem qli){
		QuoteHeader header=quote.getQuoteHeader();
		//1.1: saas main part
		if ((qli.isSaasSubscrptnPart()&& !qli.isReplacedPart())|| qli.isSaasSubsumedSubscrptnPart()){
			Boolean saasRenewalFlag = header.isSaasRenewalFlag();
			Boolean saasMigrationFlag = header.isSaasMigrationFlag();
			//2.1: if header questions is yes
			if ((saasRenewalFlag!=null && saasRenewalFlag)||(saasMigrationFlag!=null && saasMigrationFlag)){
				return true;
			}
		//1.2: monthly main part
		} else if (qli.isMonthlySoftwarePart()&& ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart() && !qli.isReplacedPart()){
			Boolean monthlyRewnewalFlag = header.isMonthlyRenewalFlag();
			Boolean monthlyMigrationFlag = header.isMonthlyMigrationFlag();
			//2.2 if header question is yes
			if ((monthlyRewnewalFlag!=null && monthlyRewnewalFlag)|| (monthlyMigrationFlag!=null &&monthlyMigrationFlag)){
				return true;
			}
		}
		return false;
	}
}