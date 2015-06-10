package com.ibm.dsw.quote.draftquote.util.builder;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.builder.QuoteBuilder;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceAppliancePartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateCalculator;
import com.ibm.dsw.quote.draftquote.util.price.PriceCalculator;
import com.ibm.dsw.quote.draftquote.util.price.TotalPriceCalculator;
import com.ibm.dsw.quote.draftquote.util.sort.PartSortUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>QuoteBuilder</code> class is the abstract class to build Quote
 * object based on Quote Header
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 *         Creation date: Apr 17, 2007
 */

public abstract class DraftQuoteBuilder extends QuoteBuilder {



	protected static LogContext logContext = LogContextFactory.singleton()
			.getLogContext();

	public DraftQuoteBuilder(Quote q, String userID) {
		super(q, userID);
	}

	private boolean isSapCallFailed = false;

	private boolean offerPriceOk = true;

	private boolean sapCallFailedBecausePayerDataIssue = false;
	
	private boolean sapCallFailedBeacuseArithmeticIssue = false;

	private String payerDataIssueMsg = "";
	
	private String arithmeticOperationMsg = "";

	private String priceLvlCodeFromPricingCall;



	public void build() throws QuoteException {
		TimeTracer tracer = TimeTracer.newInstance();
		try {

			boolean checkHeaderFlg = true;
			boolean loadLineItems = true;
			innerBuild(checkHeaderFlg, loadLineItems);
			updateQuoteHeader();

		} catch (TopazException e) {
			throw new QuoteException(e);
		}
		tracer.dump();
	}


	void submitGrowthDelegation() throws QuoteException, TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		PartPriceProcess process = PartPriceProcessFactory.singleton().create();

		for(Object obj : quote.getLineItemList()){
			final QuoteLineItem qli = (QuoteLineItem)obj;
			YTYGrowth ytyGrowth = qli.getYtyGrowth();
			if(ytyGrowth != null){
				ytyGrowth.setRenwlQliQty(qli.getOpenQty());
				//process.createOrUpdateLineItemYty(qli.getQuoteNum(), Integer.valueOf(qli.getSeqNum()),qli.getPartQty(),ytyGrowth);
		}
	}
		tracer.dump();
	}


	/**
	 * @throws QuoteException
	 */
	void updateQuoteHeader() throws QuoteException {
		TimeTracer tracer = TimeTracer.newInstance();
		PartPriceProcess process = PartPriceProcessFactory.singleton().create();
		process.updateQuoteHeader(quote.getQuoteHeader(), userID);

		tracer.dump();
	}

	void calculateLineItemPrice() throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		PriceCalculator calculator = new PriceCalculator(quote);

		boolean sapCallOK = calculator.calculatePrice();
		this.offerPriceOk = calculator.isOfferPriceOk();
		this.sapCallFailedBecausePayerDataIssue = calculator.isPayerDataIssue();
		this.payerDataIssueMsg = calculator.getPayerDataIssueMsg();
		this.sapCallFailedBeacuseArithmeticIssue = calculator.isArithmeticOperationIssue();
		this.arithmeticOperationMsg = calculator.getArighmeticOperationIssueMsg();

		this.priceLvlCodeFromPricingCall = calculator.getTranPriceLevelCode();
		this.isSapCallFailed = !sapCallOK;

		if (sapCallOK) {
			this.clearCalculateFlag();
		}
		tracer.dump();
	}

	protected void updateQuoteBackDateFlag() throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		if(quote.isQuoteHasSoftwarePart()){
			List lineItems = quote.getSoftwareLineItems();
			boolean quoteBackDated = false;
			for (int i = 0; i < lineItems.size(); i++) {
				QuoteLineItem lineItem = (QuoteLineItem) lineItems.get(i);
	
				// For cmprss cvreage line item, don't trigger back dating
				// hasValidCmprssCvrageMonth() will always return non-null values if
				// line item is cmprss cvrage
				if (!lineItem.getPartDispAttr().isFromRQ()
						&& lineItem.isItemBackDated()
						&& !lineItem.hasValidCmprssCvrageMonth()) {
					quoteBackDated = true;
					break;
				}
			}
			quote.getQuoteHeader().setBackDatingFlag(quoteBackDated);
		}
		tracer.dump();
	}

	protected abstract void preBuild() throws TopazException;

	public void buildForDisplay() throws QuoteException {
		TimeTracer tracer = TimeTracer.newInstance();
		try {
			// need check the recalcul_prc_flag in quote header level, and nened
			// load
			// line items from db2
			boolean checkHeaderFlg = true;
			boolean loadLineItems = true;
			innerBuild(checkHeaderFlg, loadLineItems);

			updateQuoteHeader();
		} catch (TopazException e) {
			throw new QuoteException(e);
		}
		tracer.dump();
	}
	
    /*
     * Reload the QuoteHeader and set the partHasPriceFlag for the quote.
     */
    public void updatePartPriceFlagsByBuilder() throws TopazException {
    	TimeTracer tracer = TimeTracer.newInstance();
        try {
            QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
            QuoteHeader reloadedQuoteHeader = quoteProcess.getQuoteHdrInfoByWebQuoteNum(quote.getQuoteHeader().getWebQuoteNum());
            quote.getQuoteHeader().setPartHasPriceFlag(reloadedQuoteHeader.getPartHasPriceFlag());
        } catch (QuoteException e) {
            throw new TopazException(e);
        }
        tracer.dump();
    }

    protected void innerBuild(boolean checkHeaderFlag, boolean loadLineItems) throws TopazException, QuoteException {

    	TimeTracer tracer = TimeTracer.newInstance();
		if (!quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()) {
			logContext.debug(this, "need check quote header flag =" + checkHeaderFlag);
			logContext.debug(this, "need load line items from db2=" + loadLineItems);
			if (loadLineItems) {
				quote.fillLineItemsForQuoteBuilder();
			}
			PartPriceHelper.calculateTotalPoint(quote);

			if (0 == quote.getLineItemList().size()) {
				PartPriceHelper.calculateQuoteTotalPrice(quote);
				return;
			}

			// important: call this method before preBuild
			// Sort function need renewal quote number which may be removed in
			// preBuild method
			
			quote.fillAndSortSaasConfigurationForDraft();
			
			quote.fillMonthlySwConfiguration(true);
			
			//if there is saas part or monthly software part, need to calculate term
			if(quote.isQuoteHasSaasPart())
			{
				calculateCoverageTerm(quote);
			}
			
			if (quote.getMonthlySwQuoteDomain().isQuoteHasMonthlySwPart()){
				quote.processMonthlyTerm();
			}
			
			sortLineItems();

			preBuild();
			
			calculateDates();

			// auto set channel discount
			setELAAutoChnlDisc(quote.getLineItemList());

			// need call this method because when the quote is imported the
			// quote level back-date flag is not set
			updateQuoteBackDateFlag();

			// do possible cleaning for cmprss cvreage
			// Eg, some manual overriden dates becomes back dated as time went
			// by
			processForCmprssCvrage();
			// associate maintenance parts to license parts
			GDPartsUtil.checkLicAndMaitAssociation(quote);

			processForSaaSParts();
			
			quote.getMonthlySwQuoteDomain().clearMonthlyDailyParts();
			
			
			boolean needRecalcPrc = needRecalculatePrice();

			if (checkHeaderFlag) {
				needRecalcPrc = needRecalcPrc
						&& (quote.getQuoteHeader().getPriceRecalcFlag() == 1);
			}

			// Do not call SAP pricing engine for multiple reseller quote &
			// output options change
			needRecalcPrc = needRecalcPrc
					&& !(quote.getQuoteHeader().isCopied4ReslChangeFlag() && QuoteConstants.QUOTE_STAGE_CODE_CPMLTRSL
							.equals(quote.getQuoteHeader().getQuoteStageCode()))
					&& !(quote.getQuoteHeader().isCopiedForOutputChangeFlag() && QuoteConstants.QUOTE_STAGE_CODE_CPCHGOUT
							.equals(quote.getQuoteHeader().getQuoteStageCode()));
			if (needRecalcPrc) {
				this.calculateLineItemPrice();
				if(needCalculatePriceAgain()){
					this.calculateLineItemPrice();
				}
			}

			// Only generagted growth delegation info if the quote is eligible
			// for growth delegation
			// Store the yty info after the price is accurate
			processForGrowthDelegation();
			
			QuoteHeader header = quote.getQuoteHeader();
			calculateIncreaseUnusedTCV(quote, header.isSubmittedQuote());
			
			quote.getMonthlySwQuoteDomain()
					.calculateMonthlySwIncreaseUnusedTCV(
							quote.getQuoteHeader().isChannelQuote());
			
			setCmprssCvrageOUP(quote);

			// retrieve EOL part price
			updateEOLPartPrices();

			CommonServiceUtil.updateRelatedItemSeqNum(quote);

			PartPriceHelper.calculateDiscount(quote.getLineItemList(), quote.getQuoteHeader());

			PartPriceHelper.calculateQuoteTotalPrice(quote);

			// need recluate the line item overall discount because after the bp
			// panter is changed
			// the chnnel price will be changed as well
			quote.calculateLineItemOverallDiscount();
			
			// fill applianceItem for quote
			fillQuoteApplianceParts(quote);
			// clear MTM information
			handlerApplianceLogic();
			// handle equity curve total discount
			quote.calculateEquityCurveTotal();
            }
		tracer.dump();
	}

	protected void setELAAutoChnlDisc(List<QuoteLineItem> lineItems) throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		if (lineItems == null || lineItems.size() == 0) {
			return;
		}

		QuoteHeader header = quote.getQuoteHeader();
		if (QuoteCommonUtil.shouldSetELAAutoChnlDisc(quote)) {
			Double chnlDisc = PartPriceConfigFactory.singleton()
					.getAutoChnlDiscOvrd(
							header.getCountry().getSpecialBidAreaCode());

			for (Iterator it = lineItems.iterator(); it.hasNext();) {
				QuoteLineItem qli = (QuoteLineItem) it.next();
				Double oriDisc = qli.getChnlOvrrdDiscPct();

				if (oriDisc == null
						|| DecimalUtil.isNotEqual(chnlDisc.doubleValue(),
								oriDisc.doubleValue())) {
					header.setRecalcPrcFlag(1);

					qli.setChnlOvrrdDiscPct(chnlDisc);
				}
			}
		} else if (!PartPriceConfigFactory.singleton()
				.allowChannelMarginDiscount(quote)) {
			// clear the auto set partner discount
			for (Iterator it = lineItems.iterator(); it.hasNext();) {
				QuoteLineItem qli = (QuoteLineItem) it.next();
				if (qli.getChnlOvrrdDiscPct() != null) {
					qli.setChnlOvrrdDiscPct(null);
					header.setRecalcPrcFlag(1);
				}
			}
		}
		tracer.dump();
	}

	public void buildForSubmit() throws QuoteException {
		TimeTracer tracer = TimeTracer.newInstance();
		try {
			boolean checkHeaderFlg = true;
			boolean loadLineItems = true;
			innerBuild(checkHeaderFlg, loadLineItems);
			updateQuoteHeader();
			updateMdlCodeForSubmitted();
			submitGrowthDelegation();
			// we need remove all price totals before total price calculation,
			// for example
			// 1st user submisison, the dist_channel_code is J , but failed, 2nd
			// time user submission, the
			// quote's dist_channel_code may become other values like "H"/"00",
			// so the records with "J" need removed

			List blPrices = getBLPrice();
			TotalPriceCalculator tpc = new TotalPriceCalculator(quote,blPrices, userID);

			tpc.cleanAndCalcuate();
		} catch (TopazException e) {
			throw new QuoteException(e);
		}
		tracer.dump();
	}

	private void handlerApplianceLogic() throws TopazException{
		TimeTracer tracer = TimeTracer.newInstance();
		if(!quote.getQuoteBusinessDomain().isQuoteHasAppliancePart(quote.getLineItemList())){
    		return;
    	}
		
		//if applianceId APPLNC_NOT_ON_QUOTE, set MTM = null
		for(Object obj: quote.getLineItemList()){
			QuoteLineItem quoteLineItem = (QuoteLineItem)obj;

			if (isClearMTM(quoteLineItem)){
				quoteLineItem.setMachineType(null);
				quoteLineItem.setModel(null);
				quoteLineItem.setSerialNumber(null);
			}

			//If quote don't have main appliance part or upgrade part , clear configId
			if (isClearApplncId(quoteLineItem)){
				quoteLineItem.setConfigrtnId(null);
			}

			if( quoteLineItem.getAddtnlYearCvrageLineItems() != null &&  quoteLineItem.getAddtnlYearCvrageLineItems().size()>0){
				for(Object obj2 : quoteLineItem.getAddtnlYearCvrageLineItems()){
					QuoteLineItem item = (QuoteLineItem)obj2;
					item.setConfigrtnId(quoteLineItem.getConfigrtnId());//configId = applianceId
					item.setApplncPocInd(quoteLineItem.getApplncPocInd());
					item.setApplncPriorPoc(quoteLineItem.getApplncPriorPoc());
					item.setMachineType(quoteLineItem.getMachineType());
					item.setModel(quoteLineItem.getModel());
					item.setSerialNumber(quoteLineItem.getSerialNumber());
					
			 }
			}
		}
		tracer.dump();
	}

	private List getBLPrice() throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		QuoteHeader header = quote.getQuoteHeader();

		if (header.getSpeclBidFlag() == 1
				&& (header.isPAQuote() || header.isOEMQuote() || header.isSSPQuote())) {
			PriceCalculator calculator = new PriceCalculator(quote);
			return calculator.calculateBLPrice();
		}
		tracer.dump();
		return null;
	}

	public void buildForOrder() throws QuoteException {
		TimeTracer tracer = TimeTracer.newInstance();
		try {
			boolean checkHeaderFlg = true;
			boolean loadLineItems = true;
			innerBuild(checkHeaderFlg, loadLineItems);
			updateMdlCodeForSubmitted();
			// Calculate price totals per ebiz ticket: RBAJ-7QNTMJ(If sales
			// quote is immediate ordered, the Web totals are not populated)
			// we need remove all price totals before total price calculation,
			// for example
			// 1st user submisison, the dist_channel_code is J , but failed, 2nd
			// time user submission, the
			// quote's dist_channel_code may become other values like "H"/"00",
			// so the records with "J" need removed

			List blPrices = getBLPrice();
			TotalPriceCalculator tpc = new TotalPriceCalculator(quote,
					blPrices, this.userID);
			tpc.cleanAndCalcuate();

			updateQuoteHeader();

		} catch (TopazException e) {
			throw new QuoteException(e);
		}
		tracer.dump();
	}

	protected abstract void clearCalculateFlag() throws TopazException;

	public void sortLineItems() throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		if(quote.getQuoteBusinessDomain().isQuoteHasAppliancePart(quote.getLineItemList())){
			setApplncMainGroup(quote.getLineItemList());
		}
		PartSortUtil.sort(quote);
		tracer.dump();
	}

	protected abstract boolean needRecalculatePrice() throws TopazException;

	private void calculateDates() throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		if(quote.isQuoteHasSoftwarePart()){
			DateCalculator calculator = DateCalculator.create(quote);
			calculator.calculateDate();
			calculator.setLineItemDates();
		}
		tracer.dump();
	}

	public static DraftQuoteBuilder create(Quote q, String userID) {
		TimeTracer tracer = TimeTracer.newInstance();
		QuoteHeader header = q.getQuoteHeader();
		if (header.isSalesQuote()) {
			return new SalesQuoteBuilder(q, userID);
		}
		if (header.isRenewalQuote()) {
			return new RenewalQuoteBuilder(q, userID);
		}

		logContext.info(DraftQuoteBuilder.class, "Unsupported Quote Type :type"
				+ header.getQuoteTypeCode());
		tracer.dump();
		return null;
	}

	protected String getPriceLvl() {
		TimeTracer tracer = TimeTracer.newInstance();
		QuoteHeader header = quote.getQuoteHeader();
		// always user SRP price for FCT quote
		if (header.isFCTQuote()) {
			logContext.debug(this, "FCT quote EOL parts, use SRP price level");
			return PartPriceConstants.PriceLevel.PRC_LVL_SRP;
		}

		if (header.isPAEQuote() || header.isPAQuote() || header.isOEMQuote() || header.isSSPQuote()) {
			if (StringUtils.isNotEmpty(header.getOvrrdTranLevelCode())) {
				logContext.debug(this,
						"EOL parts, transaction price level code is used: "
								+ header.getOvrrdTranLevelCode());
				return header.getOvrrdTranLevelCode();
			}

			if (!isSapCallFailed
					&& StringUtils.isNotEmpty(priceLvlCodeFromPricingCall)) {
				logContext.debug(this,
						"EOL parts, price level code from sap pricing call is used: "
								+ priceLvlCodeFromPricingCall);
				return priceLvlCodeFromPricingCall;
			}

			//If we do not call pricing to get SVP, need to use quote's SVP level
			if (StringUtils.isNotEmpty(header.getTranPriceLevelCode())) {
				logContext.debug(this,
						"EOL parts, transaction price level code is used: "
								+ header.getTranPriceLevelCode());
				return header.getTranPriceLevelCode();
			}

			// try to use customer price level
			Customer customer = quote.getCustomer();
			List ctList = null;
			if (customer != null) {
				ctList = customer.getContractList();
			}

			if (ctList != null && ctList.size() > 0) {
				for (Iterator it = ctList.iterator(); it.hasNext();) {
					Contract ct = (Contract) it.next();
					if (ct != null) {
						logContext.debug(this,
								"EOL parts, price level code from customer svp is used: "
										+ ct.getVolDiscLevelCode());
						return ct.getVolDiscLevelCode();
					}
				}
			}
		}

		logContext
				.debug(this, "EOL parts, no price level code found, use null");
		tracer.dump();
		return null;
	}

	private void processForCmprssCvrage() throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		if(quote.isQuoteHasSoftwarePart()){
			List masterLineItems = quote.getMasterSoftwareLineItems();
			if (masterLineItems == null || masterLineItems.size()==0) {
				quote.setMasterSoftwareLineItems(CommonServiceUtil
						.buildMasterLineItemsWithAddtnlMaint(quote
								.getLineItemList()));
			}
	
			boolean hasEligiableLineItems = false;
			boolean cmprssCvrageEnabled = quote.getQuoteHeader()
					.getCmprssCvrageFlag()
					&& !quote.getQuoteHeader().getBackDatingFlag();
	
	
	    	if(logContext instanceof QuoteLogContextLog4JImpl){
				if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){
					logContext.debug(this, "getQuoteHeader().getCmprssCvrageFlag() = "
							+ quote.getQuoteHeader().getCmprssCvrageFlag());
					logContext.debug(this, "getQuoteHeader().getBackDatingFlag() = "
							+ quote.getQuoteHeader().getBackDatingFlag());
				}
			}
	
			for (Iterator it = quote.getMasterSoftwareLineItems().iterator(); it.hasNext();) {
				QuoteLineItem qli = (QuoteLineItem) it.next();
	
				if (!cmprssCvrageEnabled) {
					clearCmprssCvrageFields(qli);
					continue;
				}
	
				// Part is not eligiable for cmprss cvrage
				if (!qli.isEligibleForCmprssCvrage()) {
					clearCmprssCvrageFields(qli);
					continue;
				}
				hasEligiableLineItems = true;
			}
	
			// No line item is eligiable for cmprss cvrage, clear the flag from
			// quote header
			if (!hasEligiableLineItems) {
				quote.getQuoteHeader().setCmprssCvrageFlag(false);
			}
		}
		tracer.dump();
	}

	/**
	 * @throws TopazException void before display PP tab, clear the override
	 *         unit price and discount for SaaS daily part
	 */
	protected void processForSaaSParts() throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		List SaaSlist = quote.getSaaSLineItems();
		if (SaaSlist == null || SaaSlist.size() == 0) {
			return;
		}
		for (int i = 0; i < SaaSlist.size(); i++) {
			QuoteLineItem qli = (QuoteLineItem) SaaSlist.get(i);
			if (qli.isSaasDaily()) { // For saas daily part
				if (qli.getOverrideUnitPrc() != null) {
					qli.setOverrideUnitPrc(null);
				}
				if (qli.getLineDiscPct() != 0) {
					qli.setLineDiscPct(0);
				}
			}
		}
		tracer.dump();
	}

	private void clearCmprssCvrageFields(QuoteLineItem qli)
			throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		QuoteHeader header = quote.getQuoteHeader();

		// This item is not included in cmprss cvrage, exclude
		if (!qli.hasValidCmprssCvrageMonth()) {
			return;
		}

		qli.setCmprssCvrageMonth(new Integer(0));
		qli.setCmprssCvrageDiscPct(new Double(0.0));

		qli.setOverrideUnitPrc(null);
		qli.setLineDiscPct(0);

		if (StringUtils.isNotBlank(qli.getRenewalQuoteNum())) {
			// Backgrounk info
			// 1. For special biddable renewal parts, start date edit is not
			// allowed
			// 2. For parts added from renewal quote, start/end date edit is not
			// allowed

			// Aleays need to reset maint start date to original value since rep
			// will never be able
			// to manual reset maint start date
			qli.setStartDtOvrrdFlg(false);
			qli.setMaintStartDate(qli.getOrigStDate());

			// Reset maint end date for parts added from renewal quote
			// No action for special biddable renewal parts
			if (CommonServiceUtil.isSalesQuoteOtherRnwlPart(quote
					.getQuoteHeader(), qli)) {
				qli.setEndDtOvrrdFlg(false);
				qli.setMaintEndDate(qli.getOrigEndDate());
			}
		}

		header.setRecalcPrcFlag(1);
		tracer.dump();
	}

	/**
	 * @return Returns the isSapCallFailed.
	 */
	public boolean isSapCallFailed() {
		return isSapCallFailed;
	}

	public boolean isOfferPriceOk() {
		return offerPriceOk;
	}

	public boolean isSapCallFailedBecausePayerDataIssue() {
		return sapCallFailedBecausePayerDataIssue;
	}

	public String getPayerDataIssueMsg() {
		return payerDataIssueMsg;
	}
	
	public boolean isSapCallFailedBeacuseArithmeticIssue() {
		return sapCallFailedBeacuseArithmeticIssue;
	}


	public String getArithmeticOperationMsg() {
		return arithmeticOperationMsg;
	}

	protected void updateProrateEolPrice(QuoteLineItem item)
			throws TopazException {
		TimeTracer tracer = TimeTracer.newInstance();
		if (!PartPriceConfigFactory.singleton().needDetermineDate(
				item.getRevnStrmCode())) {
			return;
		}
		if (item.isHasEolPrice()
				&& item.getManualProratedLclUnitPriceFlag() == 0) {
			Double entitled_unit_price = item.getLocalUnitProratedPrc();
			entitled_unit_price = CommonServiceUtil.getProrateEolPrice(quote,
					item);
			CommonServiceUtil.setEntitledPriceForEOLPart(item, item
					.getLocalUnitPrc(), entitled_unit_price, 0);
		}
		if (quote.getQuoteHeader().getCmprssCvrageFlag()
				&& item.isEligibleForCmprssCvrage()) {
			try {
				int iComrssCvrageMonth = 0;
				double dCmprssCvrageDisc = 0.0;
				if (item.getCmprssCvrageMonth() != null) {
					iComrssCvrageMonth = item.getCmprssCvrageMonth().intValue();
				}
				if (item.getCmprssCvrageDiscPct() != null) {
					dCmprssCvrageDisc = item.getCmprssCvrageDiscPct()
							.doubleValue();
				}
				NumberFormat format = NumberFormat.getInstance();
				Double dNewEntldUnitPrice = CommonServiceUtil
						.getCmprssCvrageEntitlUnitPrcEol(quote, item,
								iComrssCvrageMonth);
				PartPriceCommon partPrice = new PartPriceCommon(quote);
				Double dNewOvrridPrice = new Double(format.parse(
						partPrice.getFormattedPrice(dNewEntldUnitPrice
								.doubleValue()
								* (1 - dCmprssCvrageDisc / 100))).doubleValue());
				CommonServiceUtil.setCmprssCvrageItemEol(quote, item,
						dNewEntldUnitPrice, dNewOvrridPrice, dCmprssCvrageDisc);
			} catch (ParseException e) {
				throw new TopazException(e);
			}
		}
		tracer.dump();
	}


    
    
   /**
    * 1. fill applncParts list for quote (contain type:main appliance , Restatement ,
    *  Upgrade  ,Additional, Renewal and Transceiver)
    * 2. fill applncMian list for quote (contain type: main appliance)
    * 3. fill applncMTMParts list for quote ( when submit the quote ,need validate these parts MTM are not null)
    * @param quote
    */
    private void fillQuoteApplianceParts(Quote quote){
    	TimeTracer tracer = TimeTracer.newInstance();
    	if(!quote.getQuoteBusinessDomain().isQuoteHasAppliancePart(quote.getLineItemList())){
    		return;
    	}
    	
    	List lineItems = quote.getLineItemList();
    	// all appliance type parts
    	List applncParts = new ArrayList();
    	//  main appliance type parts
    	List applncMains = new ArrayList();

    	List applncUpgradeParts = new ArrayList(); //Upgrade Appliance part
    	//  ownership transfer appliance type parts
    	List applncOwnerships = new ArrayList();

    	//must validate MTM pars
    	List applncMTMParts = new ArrayList();

    	for (Object obj : lineItems){
    		QuoteLineItem lineItem = (QuoteLineItem)obj;
    		if (lineItem != null && lineItem.isApplncPart()){
    			applncParts.add(lineItem);   // add all appliance part

    			if (lineItem.isApplncMain()){
    				applncMains.add(lineItem); //add main appliance part
    			}

    			if(lineItem.isApplncUpgrade())
    				applncUpgradeParts.add(lineItem); // add upgrade appliance part
    			
    			if (lineItem.isOwerTransferPart()){
    				applncOwnerships.add(lineItem); //add ownership transfer appliance part
    			}
    		}
    	}


    	// fill MTM parts
    	for (Object obj: applncParts){
    		QuoteLineItem lineItem = (QuoteLineItem)obj;
			if (isValidationMTM(lineItem,applncMains,applncUpgradeParts,applncOwnerships) || lineItem.isOwerTransferPart()){
				applncMTMParts.add(lineItem);  // add validation MTM parts
			}
    	}

    	quote.setApplncMains(applncMains);
    	quote.setApplncUpgradeParts(applncUpgradeParts);
    	quote.setApplncOwnerShipParts(applncOwnerships);
    	quote.setApplncParts(applncParts);
    	quote.setApplncMTMParts(applncMTMParts);
    	
    	tracer.dump();
    }

    /**
     * validation lineItem is MTM input
     * @param qli
     * @return
     */
	private boolean isValidationMTM(QuoteLineItem qli, List applncMains,List upgradeParts,List ownershipParts) {
		TimeTracer tracer = TimeTracer.newInstance();
		PartPriceAppliancePartConfigFactory applncPartFactory = PartPriceAppliancePartConfigFactory.singleton();
		if (!qli.isReferenceToRenewalQuote() && applncPartFactory.allowMtmInput(qli)) {

			//If the part is appliance upgrade part, need mtm.
			if(qli.isApplncUpgrade())
				return true;

			//If the part is appliance reinstatement part, need mtm
			else if(qli.isApplncReinstatement())
				return true;

			//1.appliance is main part 2.applance poc is selected
			else if (qli.isApplncMain() && "Y".equalsIgnoreCase(qli.getApplncPocInd())) {
				return true;
			}

			/*
			 * Associated appliance part
			 */
			else if (qli.isApplianceRelatedSoftware()&&(PartPriceConstants.APPLNC_NOT_ON_QUOTE.equalsIgnoreCase(qli.getApplianceId())||StringUtils.isBlank(qli.getApplianceId()))) {
				return true;
			}

			//If quote have applaince Main part or Upgrade part and part appliance id !=  APPLNC_NOT_ON_QUOTE need validate mtm
			else if ((applncMains != null && applncMains.size() > 0) ||( upgradeParts != null && upgradeParts.size() > 0)||( ownershipParts != null && ownershipParts.size() > 0)) {

				/**
				 * appliance not on quote condition 1. must display dropdown 2.
				 * must be Appliance Upgrade, Transceiver & Renewal Parts 3.
				 * must seleted "appliance not on quote" option, appliance Id is "NOT_ON_QUOTE"
				 */
				if (applncPartFactory.displayApplianceIdDropdown(qli)
						&& !qli.isApplianceRelatedSoftware()
						&& PartPriceConstants.APPLNC_NOT_ON_QUOTE.equalsIgnoreCase(qli.getApplianceId()))
					return true;

			}
			else {
				/*
				 * if not main part , upgrade and renewal are show MTM
				 */
				if (qli.isApplncServicePack() || qli.isApplncServicePackRenewal() || qli.isApplncRenewal() || qli.isApplncReinstatement())
					return true;

			}

		}
		tracer.dump();
		return false;
	}



	/**
	 * 1.when quote has appliance main parts ,if main part is not poc ,clear main part MTM
	 * 2.when quote has appliance main parts ,if show applianceId drop down and value is not applnc_not_on_quote clear MTM
	 * 3.when quote has not appliance main parts,if part are not servicePack , ServicePackRenewal and Renewal clear MTM
	 * @param qli
	 * @return
	 */
	private boolean isClearMTM(QuoteLineItem qli){
		TimeTracer tracer = TimeTracer.newInstance();
		PartPriceAppliancePartConfigFactory applncPartFactory = PartPriceAppliancePartConfigFactory.singleton();
    	if (qli.isApplncPart()&&!qli.isReferenceToRenewalQuote()){

    		//Appliance upgrade part or appliance ownership transfer part
    		if(qli.isApplncUpgrade() || qli.isOwerTransferPart()){
    			return false;
    		}


    		if (quote.applncMains != null && quote.applncMains.size() >0){

    			if(qli.isApplncMain()&&"N".equalsIgnoreCase(qli.getApplncPocInd())){
        			return true;
        		}

                if (applncPartFactory.displayApplianceIdDropdown(qli)
                        &&!PartPriceConstants.APPLNC_NOT_ON_QUOTE.equalsIgnoreCase(qli.getApplianceId())
                        && StringUtils.isNotBlank(qli.getApplianceId())){
                    return true;
                }
              
    		}
    		else {

    			if(qli.isApplncReinstatement()||qli.isApplianceRelatedSoftware())
    				return false;

    			if (!(qli.isApplncServicePack()||qli.isApplncServicePackRenewal() || qli.isApplncRenewal())){
    				return true;
    			}

    		}
    	}
    	
    	tracer.dump();

    	return false;

    }


    /**
     * appliance parts: if no main part or upgrade part ,clear all appliance Id
     * @param qli
     * @return
     */
    private boolean isClearApplncId(QuoteLineItem qli){
    	TimeTracer tracer = TimeTracer.newInstance();
		if (qli.isApplncPart()&& !qli.isReferenceToRenewalQuote()){
			//If quote has Appliance main part or upgrade part, don't clear appliance id
			if(quote.getApplncMains()!=null && quote.getApplncMains().size()>0){
				return false;
			}
			else if ((quote.getApplncUpgradeParts()!=null && quote.getApplncUpgradeParts().size()>0)){
				return false;
			}
			else if ((quote.getApplncOwnerShipParts()!=null && quote.getApplncOwnerShipParts().size()>0)){
				return false;
			}

			return true;
		}
		tracer.dump();
    	return false;
    }

    private boolean needCalculatePriceAgain(){
    	TimeTracer tracer = TimeTracer.newInstance();
    	List<PartsPricingConfiguration> confiList = quote.getPartsPricingConfigrtnsList();
    	if(confiList != null && confiList.size() > 0){
    		for (PartsPricingConfiguration partsPricingConfiguration : confiList) {
    			//configuration modify date is null means it's imported
	    		if(partsPricingConfiguration.getConfigrtnModDate() == null){
	    			List<QuoteLineItem> saasList = (List) quote.getPartsPricingConfigrtnsMap().get(partsPricingConfiguration);
	    			for (QuoteLineItem lineItem : saasList) {
	    				//if it's an overage part and has discount and has no bid rate price,
	    				//have to call pricing service again to get bid rate
	    				if(lineItem.isSaasOnDemand() || lineItem.isSaasSetUpOvragePart()
	    						|| lineItem.isSaasSubscrptnOvragePart()){
	    					if(lineItem.getLineDiscPct() != 0
	    						&&(lineItem.getLocalUnitProratedDiscPrc() == null || lineItem.getLocalUnitProratedDiscPrc().doubleValue() == 0)){
	    						return true;
	    					}
	    				}
	    			}
	    		}
			}
    	}
    	tracer.dump();
    	return false;
    }


    private void updateMdlCodeForSubmitted()throws QuoteException{
    	TimeTracer tracer = TimeTracer.newInstance();
    	List saasList = quote.getSaaSLineItems();
    	StringBuffer sb = new StringBuffer("");
    	PartPriceProcess process=null; 
    	if (saasList != null && saasList.size()>0){
    		for (int i =0 ;i<saasList.size();i++){
    			QuoteLineItem qli =  (QuoteLineItem)saasList.get(i);
    			if (qli.isSaasSubscrptnPart()&&!qli.isReplacedPart()
    					&&!qli.isFixedRenwlMdl() && StringUtils.isBlank(qli.getRenwlMdlCode())){
    				combineLineItemData(sb,qli);
    			}
    		}
    	}
    	
    	if (StringUtils.isNotBlank(sb.toString())){
    	    process = PartPriceProcessFactory.singleton().create();
    		process.addRenwlModel(userID, sb.toString(),quote.getQuoteHeader().getWebQuoteNum() , "", QuoteConstants.SourceOfRenewalModel.FROM_WEB);
    	}
    	
    	//monthly part
    	sb.delete(0, sb.length());
    	List monthlyList = quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
    	if (monthlyList != null && monthlyList.size()>0){
    		for(int i =0 ;i<monthlyList.size();i++){
        		MonthlySwLineItem qli=(MonthlySwLineItem)monthlyList.get(i);
        		if (qli.isMonthlySwSubscrptnPart()&&!qli.isReplacedPart()
    					&&!qli.isFixedRenwlMdl() && StringUtils.isBlank(qli.getRenwlMdlCode())){
        			combineLineItemData(sb,qli);
    			}
        	}
    	}
    	
    	if (StringUtils.isNotBlank(sb.toString())){
    		if(process==null)
    			process = PartPriceProcessFactory.singleton().create();
    		process.addRenwlModel(userID, sb.toString(),quote.getQuoteHeader().getWebQuoteNum() , "", QuoteConstants.SourceOfRenewalModel.FROM_WEB);
    	}

    	tracer.dump();
    }

    /**
     * DOC Comment method "updateDeployModelByBuilder".
     */
    public void updateDeployModelByBuilder() throws TopazException {
    	TimeTracer tracer = TimeTracer.newInstance();
        try {
            PartPriceProcess process = PartPriceProcessFactory.singleton().create();
            process.doCalculateDeployModel(quote);
        } catch (QuoteException e) {
            throw new TopazException(e);
        }
        tracer.dump();
    }

    private void combineLineItemData(StringBuffer sb,QuoteLineItem qli){
    	sb.append(qli.getSeqNum());
		sb.append(":");
		sb.append(qli.getDefultRenwlMdlCode());
		sb.append(":");
		sb.append(PartPriceConstants.RenewalModelCode.LINE_RENWL_MDL_CODE_LEVEL);
		sb.append(";");
    }
}
