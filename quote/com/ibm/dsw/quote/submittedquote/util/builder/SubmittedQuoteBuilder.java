package com.ibm.dsw.quote.submittedquote.util.builder;

import is.domainx.User;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.builder.QuoteBuilder;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuotePriceTotals;
import com.ibm.dsw.quote.common.domain.QuotePriceTotalsFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.EmptyPersister;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.util.price.PriceCalculator;
import com.ibm.dsw.quote.draftquote.util.price.TotalPriceCalculator;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.quote.submittedquote.util.SubmittedDateComparator;
import com.ibm.dsw.quote.submittedquote.util.SubmittedPriceCalculator;
import com.ibm.dsw.quote.submittedquote.util.sort.SectnSeqNumComparator;
import com.ibm.dsw.quote.submittedquote.util.sort.SubmittedQuoteSortUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: May 9, 2007
 */

public abstract class SubmittedQuoteBuilder extends QuoteBuilder {

    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();



    private boolean isSapCallFailed = false;

    protected SubmittedDateComparator dateComparator = new SubmittedDateComparator();

    public SubmittedQuoteBuilder(Quote q, User user) {
        super(q, user);
    }

    /**
     * 
     * @param q
     * @param user In SQO system, the user object come from WebAuth @{link is.domainx.User} class,
     *             If the SBA system need a Dummy user, you can try the proxy {@link com.ibm.dsw.quote.common.domain.UserWrapper UserWrapper} class.
     * @return
     */
    public static SubmittedQuoteBuilder create(Quote q, User user) {
        if (q.getQuoteHeader().isSalesQuote()) {
            return new SubmittedSalesQuoteBuilder(q, user);
        } else {
            return new SubmittedRenewalQuoteBuilder(q, user);
        }
    }

    protected abstract void checkReCalculateFlag() throws TopazException;

    protected abstract void adjustDate() throws TopazException;

    public void build() throws QuoteException {
        if(quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()){
            buildForPriceIncrease();
        }else{
            standardBuild();
        }


    }
    
    public void standardBuild() throws QuoteException {
        QuoteHeader header = quote.getQuoteHeader();

        try {

        	quote.fillLineItemsForQuoteBuilder();

            fillPriceTotals();
            if (quote.getLineItemList().size() == 0) {
                return;
            }

            checkReCalculateFlag();

			adjustDate();
			// 14.2 GD
			GDPartsUtil.checkLicAndMaitAssociation(quote);
            

            if (header.getRecalcPrcFlag() == 1) {
                calculatePrice();
            }
			setCmprssCvrageOUP(quote);

			calculateProrateDuration();

            updateEOLPartPrices();

            calculateDiscount();

            sortLineItems();
            
            quote.fillAndSortSaasConfigurationForSubmitted();
			
			quote.fillMonthlySwConfiguration(false);
            
            // if there is saas part or monthly software part, need to calculate term
            if (quote.isQuoteHasSaasPart()) {
                calculateCoverageTerm(quote);
            }

            if (quote.getMonthlySwQuoteDomain().isQuoteHasMonthlySwPart()) {
                quote.processMonthlyTerm();
            }
			
            if(needUpdateRelatedItemSeqNum()){
                CommonServiceUtil.updateRelatedItemSeqNum(quote);
            }
            
			processForGrowthDelegation();
			
			//add a isSubmittedQuote flag param for 14.4
			calculateIncreaseUnusedTCV(quote, header.isSubmittedQuote());
            
			quote.getMonthlySwQuoteDomain()
					.calculateMonthlySwIncreaseUnusedTCV(
							quote.getQuoteHeader().isChannelQuote());

            logContext.debug(this, "recalcuate flag = " + header.getRecalcPrcFlag());
            logContext.debug(this, "Sap Call failed=" + this.isSapCallFailed);
            if ((header.getRecalcPrcFlag() == 1) && (!this.isSapCallFailed)) {
                header.setRecalcPrcFlag(0);
                PartPriceHelper.calculateQuoteTotalPrice(quote);
                logContext.debug(this, "Price recalcuated, flag is set to 0");

            }

            calculateTotalPrice();
            PartPriceHelper.calculateTotalPoint(quote);

            updateQuoteHeader();

        } catch (TopazException e) {

            throw new QuoteException(e);

        }
    }

    //Method for building quote for price increase after special bid approved
    public void buildForPriceIncrease() throws QuoteException {
        try {
        	quote.fillLineItemsForQuoteBuilder();

            fillPriceTotals();

            calculateTotalPrice();
            // if there is saas part or monthly software part, need to calculate term
            if (quote.isQuoteHasSaasPart()) {
                calculateCoverageTerm(quote);
            }

            if (quote.getMonthlySwQuoteDomain().isQuoteHasMonthlySwPart()) {
                quote.processMonthlyTerm();
            }
			
			calculateProrateDuration();

            sortLineItems();
            
            quote.fillAndSortSaasConfigurationForSubmitted();

            quote.fillMonthlySwConfiguration(false);
            
            QuoteHeader header = quote.getQuoteHeader();
			calculateIncreaseUnusedTCV(quote, header.isSubmittedQuote());
            
			quote.getMonthlySwQuoteDomain()
					.calculateMonthlySwIncreaseUnusedTCV(
							quote.getQuoteHeader().isChannelQuote());
			
			GrowthDelegationUtil.reCalculateYTYGrowthReferenceInfo(quote);
			PartPriceHelper.calculateTotalPoint(quote);

        } catch (TopazException e) {
            throw new QuoteException(e);
        }
    }

    protected String getPriceLvl(){
    	QuoteHeader header = quote.getQuoteHeader();
    	//always user SRP price for FCT quote
    	if(header.isFCTQuote()){
    		logContext.debug(this, "FCT quote EOL parts, use SRP price level");
    		return PartPriceConstants.PriceLevel.PRC_LVL_SRP;
    	}

    	if(header.isPAEQuote() || header.isPAQuote() || header.isOEMQuote() || header.isSSPQuote()){
	    	if(StringUtils.isNotEmpty(header.getOvrrdTranLevelCode())){
	    		logContext.debug(this, "EOL parts, transaction price level code is used: " + header.getOvrrdTranLevelCode());
	    		return header.getOvrrdTranLevelCode();
	    	}
			//try to use customer price level
			Customer customer = quote.getCustomer();
			List ctList = null;
			if(customer != null){
				ctList = customer.getContractList();
			}

			if(ctList != null && ctList.size() > 0){
				for(Iterator it = ctList.iterator(); it.hasNext(); ){
					Contract ct = (Contract)it.next();
					if(ct != null){
						logContext.debug(this, "EOL parts, price level code from customer svp is used: " + ct.getVolDiscLevelCode());
						return ct.getVolDiscLevelCode();
					}
				}
			}
    	}

		logContext.debug(this, "EOL parts, no price level code found, use null");
		return null;
    }
    protected void updateQuoteHeader() throws QuoteException{
        PartPriceProcess process = PartPriceProcessFactory.singleton().create();
        process.updateQuoteHeader(quote.getQuoteHeader(), quote.getQuoteHeader().getCreatorId());
    }

    /**
     * @throws TopazException
     *
     */
    protected void calculateTotalPrice() throws TopazException {
        //This method can be removed after a faily long time after DSW 10.1 rollout
        processForLegacyPriceTotals();

        if(needCalculatePriceTotal()){
            List blPrices = this.getBaseLinePricing();
            removePriceTotals();
            TotalPriceCalculator tpc = new TotalPriceCalculator(quote, blPrices, this.user.getEmail());

            tpc.calculate();
        }
        
        // Calculate the multiple implied growth for additional year
        quote.calculateMultipleAdditionalYearImpliedGrowth();
    }

    private void processForLegacyPriceTotals() throws TopazException{
        List totals = quote.getPriceTotals();

        //No price totals at all, return
        if(totals == null || totals.size() == 0){
            return ;
        }

        boolean needClearPriceTotals = false;
        boolean hasNetRevenuePriceTotal = false;
        for(Iterator it = totals.iterator(); it.hasNext(); ){
            QuotePriceTotals qpt = (QuotePriceTotals)it.next();

            //check if the price totals are calculated before the addition of column revenue stream code category
            if(PartPriceConstants.PriceTotalRevnStrmCategory.LEGACY_ALL.equals(qpt.getRevnStrmCategoryCode())){
            	needClearPriceTotals = true;
            	break;
            }

            //check if net revenue is calculated
            if(PartPriceConstants.PriceType.NET_REVENUE.equals(qpt.getPriceType())){
            	hasNetRevenuePriceTotal = true;
            }
        }

        if(needClearPriceTotals || !hasNetRevenuePriceTotal){
        	//Need to remove all legacy quotes
        	removePriceTotals();
        }

        return;
    }

    private void removePriceTotals() throws TopazException{
        QuotePriceTotalsFactory.singleton().removePriceTotals(quote.getQuoteHeader().getWebQuoteNum());
        quote.setPriceTotals(null);
    }

    protected boolean needCalculateBaseLinePrice(){

        QuoteHeader header = quote.getQuoteHeader();

        if(isSpecialBid() && (header.isPAQuote()|| header.isOEMQuote() || header.isSSPQuote())){

            List totals = quote.getPriceTotals();
            if(totals == null || totals.size() == 0){
                return true;
            }

            for(int i=0;i<totals.size();i++){
                QuotePriceTotals qpt = (QuotePriceTotals)totals.get(i);
                if(PartPriceConstants.PriceType.BASELINE_PRICE.equals(qpt.getPriceType())){
                        logContext.debug(this,"BaseLine price already calcuated, don't calculate again");
                        return false;
                }
             }

            logContext.debug(this, "Need calcuate BL price");
            return true;

        } else{
            logContext.debug(this,"Not PA and special bid,don't calcuate BL price");
            return false;
        }
    }

    private boolean needCalculatePriceTotal() throws TopazException {
        if (quote.getQuoteHeader().getRecalcPrcFlag() == 1) {
            return true;
        } else {
            if ((quote.getPriceTotals() == null) || quote.getPriceTotals().size() == 0) {
                //No price totals yet, need to recalculate
                return true;
            }
        }
        return false;
    }

    protected void fillPriceTotals() throws TopazException {

        QuotePriceTotalsFactory factory = QuotePriceTotalsFactory.singleton();
        List prcTotals = factory.getQuotePriceTotals(quote.getQuoteHeader().getWebQuoteNum(), this.user.getEmail());
        quote.setPriceTotals(prcTotals);

    }

    /**
     * @throws TopazException
     *
     */
    protected void sortLineItems() throws TopazException {
        QuoteHeader header = quote.getQuoteHeader();
        String lob = header.getLob().getCode();
        boolean isSubmitter = user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_SUBMITTER
        						|| quote.isPgsAppl();
        logContext.debug(this, user.getEmail() + "is a submiter=" + isSubmitter);
        List lineItems = new ArrayList();
        List softwareLineItems = quote.getSoftwareLineItems();
        //set ApplncMain part group
        setApplncMainGroup(softwareLineItems);
        if(softwareLineItems != null && softwareLineItems.size() > 0){
        	SubmittedQuoteSortUtil.sort(header.isSalesQuote(), softwareLineItems, lob, isSubmitter);
        	lineItems.addAll(softwareLineItems);
        }
        
        List monthlySwLineItems = quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
        if(monthlySwLineItems != null && monthlySwLineItems.size() > 0){
        	Collections.sort(monthlySwLineItems, new SectnSeqNumComparator());
        	lineItems.addAll(monthlySwLineItems);
        }
        
        List SaaSLineItems = quote.getSaaSLineItems();
        if(SaaSLineItems != null && SaaSLineItems.size() > 0){
        	Collections.sort(SaaSLineItems, new SectnSeqNumComparator());
        	lineItems.addAll(SaaSLineItems);
        }
        quote.setLineItemList(lineItems);

    }

    /**
     *
     */
    private List getBaseLinePricing() throws TopazException {

        QuoteHeader header = quote.getQuoteHeader();

        String lob = header.getLob().getCode();

        if (isSpecialBid() && (header.isPAQuote()|| header.isOEMQuote() || header.isSSPQuote())) {
        	PriceCalculator calculator = new PriceCalculator(quote);

        	return calculator.calculateBLPrice();
        }
        return new ArrayList();

    }

    private void calculatePrice() throws TopazException {

        SubmittedPriceCalculator calculator = new SubmittedPriceCalculator(quote, dateComparator);

        this.isSapCallFailed = !calculator.calculateDefaultPrice();

    }

    protected void calculateProrateDuration() {
    	if(!quote.isQuoteHasSoftwarePart()){
    		return;
    	}
        QuoteHeader header = this.quote.getQuoteHeader();
        if (header.isPAQuote() || header.isPAEQuote() || header.isFCTQuote() || header.isOEMQuote()) {
            List lineItems = quote.getLineItemList();

            for (int i = 0; i < lineItems.size(); i++) {
                QuoteLineItem lineItem = (QuoteLineItem) lineItems.get(i);
                boolean needCalcProrateDate = false;

                if (header.isPAQuote() || header.isPAEQuote() || header.isOEMQuote()) {
                    needCalcProrateDate = PartPriceConstants.PartTypeCode.PACTRCT.equals(lineItem.getPartTypeCode());
                }
                if (header.isFCTQuote()) {
                    PartDisplayAttr attr = lineItem.getPartDispAttr();
                    needCalcProrateDate = attr.isLicenseBehavior() || attr.isMaintBehavior() || attr.isSupport();
                }

                if (needCalcProrateDate) {
                    PartDisplayAttr attr = lineItem.getPartDispAttr();

                    if(attr.isFtlPart()){
                        int weeks = DateUtil.calculateWeeks(lineItem.getMaintStartDate(), lineItem.getMaintEndDate());
                        logContext.debug(this, "FTL parts, proration weeks: " + weeks + " (" + lineItem.getPartNum() + ","
                                            + lineItem.getSeqNum() + ")");
                        lineItem.setProrateWeeks(weeks);

                    } else {
                        int months = DateUtil.calculateFullCalendarMonths(lineItem.getMaintStartDate(), lineItem.getMaintEndDate());
                        logContext.debug(this, "non-FTL parts, proration months:" + months + " (" + lineItem.getPartNum() + ","
                                            + lineItem.getSeqNum() + ")");
                        lineItem.setProrateMonths(months);
                    }
                }

            }
        }

    }

    private boolean needReCalculateOrNoTotalPrice() {

        QuoteHeader header = quote.getQuoteHeader();

        if (header.getRecalcPrcFlag() == 1) {
            return true;
        }

        // check if total price is avaliable
        return header.getRecalcPrcFlag() == 1;

    }

    protected void calculateDiscount() throws TopazException, QuoteException {

        QuoteHeader header = this.quote.getQuoteHeader();
        if (header.getRecalcPrcFlag() == 0) {
            return;
        }

        PartPriceHelper.calculateDiscount(quote.getLineItemList(), quote.getQuoteHeader());
    }


    protected boolean isSpecialBid() {
        QuoteHeader header = quote.getQuoteHeader();

        return (header.getSpeclBidFlag() == 1);

    }


    /**
     * @return Returns the isSapCallFailed.
     */
    public boolean isSapCallFailed() {
        return isSapCallFailed;
    }

    // if current user is approver, but not the first approval , call new mehtod :buildForNotFirstApproval
    /*protected boolean isFirstApprover(Quote quote) {
        return quote.getQuoteUserAccess().isFirstAppTypMember();
    }*/

    protected boolean isApprover(User user) {
        return user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER;
    }

    private void setEmtpyPersister(Quote quote) {
        Persister emptyPersister = new EmptyPersister();
        List lineItems = quote.getLineItemList();
        for(int i=0;i<lineItems.size();i++){
            QuoteLineItem item = (QuoteLineItem)lineItems.get(i);
            item.setPersister(emptyPersister);
        }
    }



    protected void updateProrateEolPrice(QuoteLineItem item) throws TopazException {
        if (!PartPriceConfigFactory.singleton().needDetermineDate(item.getRevnStrmCode())) {
            return;
        }
        if(quote.getQuoteHeader().getCmprssCvrageFlag() && item.isEligibleForCmprssCvrage()){
            try{
                int iComrssCvrageMonth = 0;
                double dCmprssCvrageDisc = 0.0;
                if(item.getCmprssCvrageMonth() != null){
                    iComrssCvrageMonth = item.getCmprssCvrageMonth().intValue();
                }
                if(item.getCmprssCvrageDiscPct() != null){
                    dCmprssCvrageDisc = item.getCmprssCvrageDiscPct().doubleValue();
                }
                NumberFormat format = NumberFormat.getInstance();
                Double dNewEntldUnitPrice = CommonServiceUtil.getCmprssCvrageEntitlUnitPrcEol(quote, item, iComrssCvrageMonth);
                PartPriceCommon partPrice = new PartPriceCommon(quote);
                Double dNewOvrridPrice = new Double(format.parse(partPrice.getFormattedPriceByPartType(item, new Double(dNewEntldUnitPrice.doubleValue() * (1 - dCmprssCvrageDisc / 100)))).doubleValue());
                CommonServiceUtil.setCmprssCvrageItemEol(quote, item, dNewEntldUnitPrice, dNewOvrridPrice, dCmprssCvrageDisc);
            }catch(ParseException e) {
                throw new TopazException(e);
            }
        } else {
	        Double entitled_unit_price = item.getLocalUnitPrc();
	        entitled_unit_price = CommonServiceUtil.getProrateEolPrice(quote,item);
	        CommonServiceUtil.setEntitledPriceForEOLPart(item, item.getLocalUnitPrc(), entitled_unit_price, 0);
        }
    }

    private boolean needUpdateRelatedItemSeqNum(){
        List lineItemList = quote.getLineItemList();
        for (int i = 0; i < lineItemList.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItemList.get(i);
            if(item.getIRelatedLineItmNum() == PartPriceConstants.PartRelNumAndTypeDefault.RELATED_LINE_ITM_NUM_DEFAULT
               || PartPriceConstants.PartRelNumAndTypeDefault.PART_TYPE_DEFAULT.equalsIgnoreCase(item.getSPartType())){
                return true;
            }
        }
        return false;
    }

    
}
