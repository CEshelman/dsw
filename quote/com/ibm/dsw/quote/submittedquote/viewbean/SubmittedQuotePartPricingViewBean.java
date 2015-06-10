package com.ibm.dsw.quote.submittedquote.viewbean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.RevnStrmCategoryDetail;
import com.ibm.dsw.quote.appcache.domain.jdbc.RevnStrmCategoryDetail_jdbc;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.CoTermConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.OmitRenewalLine;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.QuotePriceTotals;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.ItemCoverageDates;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PVUDetail;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PVUSection;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartInfo;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPricingTabURLs;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTable;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTableTotal;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartsAndPrice;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PricingNotes;
import com.ibm.dsw.quote.draftquote.viewbean.helper.SoftwareMaintenance;
import com.ibm.dsw.quote.draftquote.viewbean.helper.SubmitPartTableElementUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.TopSection;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.BrandTotalPrice;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.BrandTotalPriceCollector;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SubmittedNoteRow;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SubmittedReasonRow;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: May 8, 2007
 */

public class SubmittedQuotePartPricingViewBean extends SubmittedQuoteBaseViewBean {

    public PartsAndPrice partAndPrice;

    public TopSection topSection;

    public PartInfo partInfo;

    public PricingNotes pricingNotes;

    public PVUSection pvuSection;

   // public SubmittedPartTable partTable;

    public SubmitPartTableElementUtil partTableElementUtil;

    public PVUDetail pvuDetail;

    public SoftwareMaintenance softwareMaintenance;

    public SubmittedReasonRow reasonRow;

    public PartTableTotal partTableTotal;

    public PartPricingTabURLs tabURLs;

    public ItemCoverageDates backDate;

    private int overrideStartDateYear;

    private int overrideStartDateMonth;

    private int overrideStartDateDay;

    private int overrideEndDateYear;

    private int overrideEndDateMonth;

    private int overrideEndDateDay;

    //from 0 to 20
    private int yearRange = 20;

    private int currYear = 0;

    private int formerYear = 0;

    private static String REPORTING_CURRENCY_VALUE = "USD";

    transient List entitledPrices = null;

    transient List blPrices = null;

    transient List specialBidPrices = null;

    transient List categoryList = null;
    transient List totalPrices = new ArrayList();

//    private ArrayList shmrrs;

    public BrandTotalPrice channelPrice;

    BrandTotalPrice customerTotalPrice;

    public BrandTotalPrice netRevenuePrice;

    //IBM global financing charges
    private BrandTotalPrice rbdPrice;

    private SubmittedNoteRow noteRow;

    PartTable partTableDraft;

    //Approver details

    //private boolean showApproverDetails = false;

    private boolean editableFlag = false;

    private boolean pendingAppGrpFlag = false;

    private transient Map actionText = new HashMap();

    private boolean adminFlag = false;

    public static final double double_min_value = -1.7e+308;

    public BrandTotalPrice getRbdPrice() {
		return rbdPrice;
	}
    public boolean isPendingAppGrpFlag() {
        return pendingAppGrpFlag;
    }
    public Map getActionText() {
        return actionText;
    }
    public boolean isAdminFlag() {
        return adminFlag;
    }
	public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);

        String lob = quote.getQuoteHeader().getLob().getCode();
        yearRange = PartPriceConfigFactory.singleton().getAddtionalYear(lob);

        // Generate calendar information
        Calendar curr = Calendar.getInstance();
        currYear = curr.get(Calendar.YEAR);
        formerYear = currYear - 1;

        LogContextFactory.singleton().getLogContext().debug(this,"Begin to create page UI object");
        partAndPrice = new PartsAndPrice(quote, user);

        collectApproverDetails();

        topSection = new TopSection(quote);

        partInfo = new PartInfo(quote);

        pricingNotes = new PricingNotes(quote);

        pvuSection = new PVUSection(quote);

       // partTable = new SubmittedPartTable(quote,getLocale());

        partTableDraft = new PartTable(quote);

        partTableElementUtil = new SubmitPartTableElementUtil(quote, partTable);

        pvuDetail = new PVUDetail(quote);

        softwareMaintenance = new SoftwareMaintenance(quote, user);

        backDate = new ItemCoverageDates(quote);

        partTableTotal = new PartTableTotal(quote);

        reasonRow = new SubmittedReasonRow(quote);

        noteRow = new SubmittedNoteRow(quote);

        tabURLs = new PartPricingTabURLs(quote);
        if(!isBidIteratnQt()){
	        LogContextFactory.singleton().getLogContext().debug(this,"Begin to create total prices");
	        categoryList = getDistinctRevnStrmCategoryCode();
	        getTotalData();
	        caculateRBDDiscount();
        }
    }

    /**
     *
     */
    private void collectApproverDetails() {

    /*    if( quote.getQuoteHeader().getSpeclBidFlag() == 1 && this.isApprover ){
            this.showApproverDetails = true;
        }else{
            return ;
        }*/

        editableFlag = (user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER)
        && this.quote.getQuoteUserAccess().isAnyAppTypMember();

        pendingAppGrpFlag = editableFlag && this.quote.getQuoteUserAccess().isPendingAppTypMember();

        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_ADD_APRVR_COMMENT,
                SpecialBidViewKeys.MSG_APPRVR_ACTION_ADD_APRVR_COMMENT);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_APPROVE, SpecialBidViewKeys.MSG_APPRVR_ACTION_APPROVE);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_APPRVL_PENDG,
                SpecialBidViewKeys.MSG_APPRVR_ACTION_APPRVL_PENDG);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_REJECT, SpecialBidViewKeys.MSG_APPRVR_ACTION_REJECT);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO,
                SpecialBidViewKeys.MSG_APPRVR_ACTION_RETURN_FOR_ADD_INFO);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_CHANGES,
                SpecialBidViewKeys.MSG_APPRVR_ACTION_RETURN_FOR_CHANGES);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_CANCEL_APPROVED_BID, SpecialBidViewKeys.MSG_APPRVR_ACTION_CANCEL_APPROVED_BID);

    }
    private void getTotalData() {
        BrandTotalPriceCollector collector = new BrandTotalPriceCollector(quote);
        if(categoryList != null){
            LogContextFactory.singleton().getLogContext().debug(this,"Begin to create total and subtotal prices");
            for (int i = 0; i < categoryList.size(); i++) {
                Map pricesMap = new HashMap();
                RevnStrmCategoryDetail revnStrmCategoryDetail = (RevnStrmCategoryDetail) categoryList.get(i);
                List sbPriceList = null;
                List blPriceList = null;
                List entitledPriceList = null;
                Map sbPriceMap = null;
                Map blPriceMap = null;
                Map entitledPriceMap = null;
                if (isSpecialBid()) {
                    sbPriceMap = collector.getSpecialBidPrices(QuoteConstants.DIST_CHNL_END_CUSTOMER,null,revnStrmCategoryDetail.getRevnStrmCategoryCode());
                    blPriceMap = collector.getBasedLinePrices(QuoteConstants.DIST_CHNL_END_CUSTOMER,sbPriceMap,revnStrmCategoryDetail.getRevnStrmCategoryCode());
                }
                entitledPriceMap = collector.getEntitledPrices(QuoteConstants.DIST_CHNL_END_CUSTOMER,sbPriceMap,revnStrmCategoryDetail.getRevnStrmCategoryCode());
                sbPriceList = collector.convertMap2List(sbPriceMap);
                blPriceList = collector.convertMap2List(blPriceMap);
                entitledPriceList = collector.convertMap2List(entitledPriceMap);
                pricesMap.put(PartPriceConstants.PriceType.SPECIAL_BID_PRICE,sbPriceList);
	            pricesMap.put(PartPriceConstants.PriceType.BASELINE_PRICE,blPriceList);
	            pricesMap.put(PartPriceConstants.PriceType.ENTITLED_PRICE,entitledPriceList);
	            totalPrices.add(pricesMap);
	            LogContextFactory.singleton().getLogContext().debug(this,"Get total and subtotal prices successfully");
            }
            entitledPrices = (List)((Map)totalPrices.get(0)).get(PartPriceConstants.PriceType.ENTITLED_PRICE);
            specialBidPrices = (List)((Map)totalPrices.get(0)).get(PartPriceConstants.PriceType.SPECIAL_BID_PRICE);
            blPrices = (List)((Map)totalPrices.get(0)).get(PartPriceConstants.PriceType.BASELINE_PRICE);
        }

        getCustomerTotalPrice();

        //Calculate channel discount
        if (isChannel()) {
            channelPrice = collector.getChannelTotalPrice();
            if(channelPrice == null) channelPrice = new BrandTotalPrice();
            calculateChannelDiscount();
        }


        calculateNetRevenue(collector);
    }

    private void calculateNetRevenue(BrandTotalPriceCollector collector){
    	netRevenuePrice = collector.getNetRevenueTotalPrice();

    	String strDisc = "";
        double entitledTotal = 0.0;
        for (int i = 0; i < entitledPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) entitledPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)) {
                entitledTotal = btp.localCurrencyPrice;
                break;
            }
        }
        if (entitledTotal == 0.0) {
        	strDisc = "";
        } else {
        	strDisc = DecimalUtil.calculateDiscount(netRevenuePrice.localCurrencyPrice, entitledTotal);
        }

        netRevenuePrice.customerDiscount = strDisc;
    }

    private void caculateRBDDiscount(){
    	LogContextFactory.singleton().getLogContext().debug(this,"Start to coculate RBD discount");
    	if (isRBD()){
	    	SpecialBidInfo sbInfo = this.quote.getSpecialBidInfo();

        	Double financeRate = sbInfo.getFinanceRate();
        	Double progRBD = sbInfo.getProgRBD();
        	Double incrRBD = sbInfo.getIncrRBD();
        	Double rate = null;
        	if (progRBD == null && incrRBD==null && financeRate !=null){
        		rate = financeRate;
        	}else if (progRBD != null && incrRBD != null){
        		rate = progRBD + incrRBD;
        	}

        	if ( rate == null )
            {
                rate = new Double(0.0);
            }

	    	rbdPrice = new BrandTotalPrice();
	    	rbdPrice.customerDiscount = String.valueOf(rate);
	    	//Amount in local currency (total bid price x rate)

	    	for (int i = 0; i < specialBidPrices.size(); i++) {
	            BrandTotalPrice btp = (BrandTotalPrice) specialBidPrices.get(i);
	            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)) {
	            	rbdPrice.localCurrencyPrice = btp.localCurrencyPrice  * rate/100;
	    	    	rbdPrice.regionBasedCurrencyPrice = btp.regionBasedCurrencyPrice * rate/100;
	    			rbdPrice.reportCurrencyPrice = btp.reportCurrencyPrice  * rate/100;
	                break;
	            }
	        }

    	}
    	LogContextFactory.singleton().getLogContext().debug(this,"Finish coculating RBD discount");

    }

    public boolean isRBD(){
    	SpecialBidInfo sbInfo = this.quote.getSpecialBidInfo();
    	if(sbInfo==null){
    		LogContextFactory.singleton().getLogContext().debug(this,"SBInfo is null!");
    	}
    	return ((sbInfo!=null)&&(sbInfo.getRateBuyDown()==1)) ;
    }

    /**
     * @param channelPrice2
     * @param entitledPrices2
     */
    private void calculateChannelDiscount() {

        if (null == channelPrice) {
            return;
        }
        if ((null != customerTotalPrice) && (customerTotalPrice.localCurrencyPrice != 0.0)) {

            channelPrice.customerDiscount = DecimalUtil.calculateDiscount(channelPrice.localCurrencyPrice,customerTotalPrice.localCurrencyPrice);

        } else {
            channelPrice.customerDiscount = DecimalUtil.formatTo5Number(0);
        }

    }

    private void getCustomerTotalPrice() {
        List prices = null;
        if (isSpecialBid()) {
            prices = this.specialBidPrices;
        } else {
            prices = this.entitledPrices;
        }
        if ((prices == null) || (prices.size() == 0)) {
            return;
        }
        for (int i = 0; i < prices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) prices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)) {
                customerTotalPrice = btp;
            }
        }
    }

    public boolean isSpecialBid() {

        QuoteHeader header = quote.getQuoteHeader();
        return header.getSpeclBidManualInitFlg() == 1 || header.getSpeclBidSystemInitFlg() == 1;
    }

    public boolean isAddedFromRenewalQuote(QuoteLineItem item) {
        return (isSalesQuote())
                 && (StringUtils.isNotBlank(item.getRenewalQuoteNum()));
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        return SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_PART_PRICE_TAB;
    }

    /**
     *
     * @return
     */
    public List getLineItems() {
        return quote.getLineItemList();
    }

    /**
     * @return row span number by judging what will be shown
     */
    public int getRowSpan(QuoteLineItem lineItem, boolean userPreference) {
        int rowNumber = 1;

        if (quote.getQuoteHeader().isRenewalQuote()) {
            if (pvuDetail.showSubmittedPVUSection(lineItem, userPreference)) {
                rowNumber++;
            }
            if (softwareMaintenance.showSubmittedOverrideMaintenanceCoverage(lineItem,userPreference)) {
                rowNumber++;
            }
            if (userPreference)
                if(isFCT()){
                    rowNumber++;
                }else{
                    rowNumber = rowNumber + 2;
                }
//            if(showEOLNote(lineItem) || showCtLvPrcAppPartNote(lineItem)){
//            	rowNumber++;
//            }

            if (partTable.showRenewalPricePYP(lineItem,quoteUserSession) || 
            		partTable.showRenewalPriceRSVP(lineItem,quoteUserSession) || 
            			partTable.showRenewalPriceMethod(lineItem,quoteUserSession)) {
            	rowNumber++;
            }
        } else {
            if (isPA() || isPAE() || isFCT() || isOEM() || isSSP()) {
				if (pvuDetail.showSubmittedPVUSection(lineItem, userPreference) && !lineItem.isMonthlySoftwarePart()) {
                    rowNumber++;
                }
                if (softwareMaintenance.showSubmittedOverrideMaintenanceCoverage(lineItem,userPreference)) {
                    rowNumber++;
                }
                if (userPreference)
                    rowNumber++;
                
                //Cmprss cvrage row
                if(partTable.showCmprssCvrageRow(lineItem)){
                    rowNumber++;
                }
                //renewal quote row
              //pgs not show the  yty related info 14.2 ebiz ID: MNAI-9MB4PD
                if(partTable.showRenewalPricePYP(lineItem, quoteUserSession) || partTable.showRenewalPriceRSVP(lineItem, quoteUserSession)
                		|| partTable.showRenewalPriceMethod(lineItem, quoteUserSession) || (!quote.isPgsAppl() &&(GrowthDelegationUtil.isDisplayYTYStatusMessage(quote, lineItem) || partTable.showRenewalYTYGrowth(lineItem)))){
                	rowNumber++;
                }
                
                if (!isShowIsNewServiceLine(lineItem)){
                	 //saas renewal row, refer to rtc#207982
                    if(showSaasRenwl(lineItem) && lineItem.isSaasRenwl()){
                    	rowNumber++;
                    }
                    //saas migration flag row
                    if (isDisplayMigration(lineItem) && lineItem.isWebMigrtdDoc()) {
                    	rowNumber++;
                    }
                }
               
            }
        }

        if(!isPPSS()){
            if( partTable.showOrderStatusRow(lineItem)){
                rowNumber++;
            }
        }


        //add Appliance row count
        if (partTable.showApplianceInformation(lineItem, !getQuote().getQuoteHeader().isSubmittedQuote(), getQuote()
                .getQuoteHeader().isSalesQuote())
                || (partTable.showApplianceAssociation(lineItem, !getQuote().getQuoteHeader().isSubmittedQuote(), getQuote()
                        .getQuoteHeader().isSalesQuote()) && (partTable.applianceAssoRowIsNotBlank(lineItem, !getQuote()
                        .getQuoteHeader().isSubmittedQuote(), getQuote().getQuoteHeader().isSalesQuote())))) {
        	rowNumber++;
        }
        
        //add Appliance Deployment row count        
        if (lineItem.isDeploymentAssoaciatePart()) {
        	if (isEditable()) {
        		rowNumber++;
        	} else if (lineItem.getDeployModel().getDeployModelId() != null) {        		
        		rowNumber++;        		
        	} else {
        		// Empty
        	}
        }
        
        //add note count
        if(showNoteRow(lineItem)){
        	rowNumber++;
        }
        
        if (PartPriceCommon.isShowApplncMTMMsg(lineItem) && isQuoteBeforeOrder() && isEditable()) {
        	rowNumber++;
        }

        return rowNumber;
    }




    public int getSubTotalRowSpan(int rowSize, boolean hideShowFlag, boolean isTotalSecsionFlag){
        int rowNumber = 1;
        if(isTotalSecsionFlag || hideShowFlag){
            rowNumber = rowSize;
        }
        return rowNumber;
    }

    /**
     * @return
     */
    public int getRQColSpan() {
        if (isPA() || isPAE()) {
            if (this.partTable.showChannelMarginCol()){
                return 9;
            }else{
                return 7;
        	}
        }else if (isFCT()) {
            return 6;
        }else if (isOEM()){
            if(this.partTable.showChannelMarginCol()){
                return 8;
            }else{
                return 6;
            }
        }
        return -1;
    }

    public int getSQColSpan(){
        if (isPA() || isPAE() || isSSP()) {
            if (this.partTable.showChannelMarginCol()){
            	if(partTable.showBpTcvColumn()){
            		return 12;
            	}else{
            		return 11;
            	}
            }else{
                return 8;
        	}
        }else if (isFCT()) {
            return 8;
        }else if (isOEM()){
            if(this.partTable.showChannelMarginCol()){
                return 10;
            }else{
                return 7;
            }
        }
        return -1;
    }

    public int getSQColSpanForPGS(){
    	int span = -1;
        if (isPA() || isPAE()) {
        	span = 6;
        	if(this.isShowDiscountCol4PGS()){
        		span ++;
        	}
        	if(this.isShowBidTcvCol4PGS()){
        		span ++;
        	}
            if (this.partTable.showChannelMarginCol()){
            	if(this.isShowBpDiscountCol4PGS()){
            		span ++;
            	}
            	if(this.isShowBpTcvCol4PGS()){
            		span ++;
            	}
        	}
        }
        return span;
    }

    /**
     * get the net increase & unused price column span
     * @return
     */
    public int getNetIncrsUnusedColSpan4PGS(){
    	int span = 1;
    	if(this.isShowDiscountCol4PGS()){
    		span ++;
    	}
    	if(this.isShowBidTcvCol4PGS()){
    		span ++;
    	}
    	return span;
    }


    /**
     * get default values for override start date year/month/day and override end date year/month/day
     * @param lineItem
     */
    public void getOvrDatesDefaultValue(QuoteLineItem lineItem) {
        //Default value:  the line item start date from the quote
        Date overrideStartDate = lineItem.getMaintStartDate();
        if (overrideStartDate != null) {
            overrideStartDateDay = overrideStartDate.getDate();
            overrideStartDateMonth = overrideStartDate.getMonth();
            overrideStartDateYear = overrideStartDate.getYear() + 1900;
        }

        //Default value:  the line item end date from the quote
        Date overrideEndDate = lineItem.getMaintEndDate();
        if (overrideEndDate != null) {
            overrideEndDateDay = overrideEndDate.getDate();
            overrideEndDateMonth = overrideEndDate.getMonth();
            overrideEndDateYear = overrideEndDate.getYear() + 1900;
        }
    }

    private String formatCode2(String s) {
        if (s == null)
            return null;
        if (s.length() == 1)
            return "0" + s;
        else
            return s;
    }

    /**
     * generate override start day options
     * @return
     */
    public Collection generateOvrStartDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (overrideStartDateDay == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), false));

        }
        return collection;
    }

    /**
     * generate override end day options
     * @return
     */
    public Collection generateOvrEndDayOptions(QuoteLineItem item) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        	collection.add(new SelectOptionImpl(sDay, "", false));

            for (int i = 1; i <= DAY_OF_MONTH; i++) {
                if (overrideEndDateDay == i)
                    collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), true));
                else
                    collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), false));

            }

        return collection;
    }

    /**
     * generate override start month options
     * @return
     */
    public Collection generateOvrStartAnniversaryOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = { MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL,
                MessageKeys.MONTH_AUG, MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV,
                MessageKeys.MONTH_DEC };
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);

        collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
            labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
            if (overrideStartDateMonth + 1 == i)
                collection.add(new SelectOptionImpl(labelString, formatCode2(String.valueOf(i)), true));
            else
                collection.add(new SelectOptionImpl(labelString, formatCode2(String.valueOf(i)), false));
        }

        return collection;
    }

    /**
     * generate override end month options
     * @return
     */
    public Collection generateOvrEndAnniversaryOptions(QuoteLineItem item) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = { MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL,
                MessageKeys.MONTH_AUG, MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV,
                MessageKeys.MONTH_DEC };
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);

        	collection.add(new SelectOptionImpl(labelString, "", false));

            for (int i = 1; i < labels.length; i++) {
                labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
                if (overrideEndDateMonth + 1 == i)
                    collection.add(new SelectOptionImpl(labelString, formatCode2(String.valueOf(i)), true));
                else
                    collection.add(new SelectOptionImpl(labelString, formatCode2(String.valueOf(i)), false));
            }

        return collection;
    }

    /**
     * generate override start year options
     * @return
     */
    public Collection generateOvrStartYearOptions(QuoteLineItem item) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        int totalYears = backDate.getTotalMaintYears();
        int maintStartYear = getMaintStartYear(item.getRevnStrmCode());

        collection.add(new SelectOptionImpl(sYear, "", false));

        for (int i = 0; i < totalYears; i++) {
            if ((i + maintStartYear) == overrideStartDateYear) {
                collection.add(new SelectOptionImpl(String.valueOf(overrideStartDateYear), String.valueOf(overrideStartDateYear), true));
            } else {
                collection.add(new SelectOptionImpl(String.valueOf(maintStartYear + i), String.valueOf(maintStartYear + i), false));
            }
        }

        return collection;
    }

    /**
     * generate override end year options
     * @return
     */
    public Collection generateOvrEndYearOptions(QuoteLineItem item) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        int totalYears = backDate.getTotalMaintYears();
        int maintStartYear = getMaintStartYear(item.getRevnStrmCode());

        	collection.add(new SelectOptionImpl(sYear, "", false));

            for (int i = 0; i < totalYears; i++) {
                if ((i + maintStartYear) == overrideEndDateYear) {
                    collection.add(new SelectOptionImpl(String.valueOf(overrideEndDateYear), String.valueOf(overrideEndDateYear), true));
                } else {
                    collection.add(new SelectOptionImpl(String.valueOf(maintStartYear + i), String.valueOf(maintStartYear + i), false));
                }
            }

        return collection;
    }

    /**
     * generate override start year options
     * @return
     */
    public Collection generateRQOvrStartYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        collection.add(new SelectOptionImpl(sYear, "", false));

        for (int i = 0; i < yearRange; i++) {
            if (formerYear + i == overrideStartDateYear) {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), true));
            } else {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), false));
            }
        }

        return collection;
    }

    /**
     * generate override end year options
     * @return
     */
    public Collection generateRQOvrEndYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        collection.add(new SelectOptionImpl(sYear, "", false));

        for (int i = 0; i < yearRange; i++) {
            if (formerYear + i == overrideEndDateYear) {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), true));
            } else {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), false));
            }
        }

        return collection;
    }

    public String getNetRevenueDiscountForSpbid() {

        if (!isSpecialBid()) {
            return "";
        }
        double entitledTotal = 0.0;
        double specialTotal = 0.0;
        for (int i = 0; i < entitledPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) entitledPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)) {
                entitledTotal = btp.localCurrencyPrice;
                break;
            }
        }
        if (entitledTotal == 0.0) {
            return "";
        }
        for (int i = 0; i < specialBidPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) specialBidPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)) {
                specialTotal = btp.localCurrencyPrice;
                break;
            }
        }
        return DecimalUtil.calculateDiscount(specialTotal,entitledTotal);


    }

    public String getNetRevenueDiscountForChannelSpbid() {
        if (!isSpecialBid() || !isChannel()) {
            return "";
        }
        double entitledTotal = 0.0;
        double channelTotal = 0.0;
        for (int i = 0; i < entitledPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) entitledPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)){
                entitledTotal = btp.localCurrencyPrice;
                break;
            }
        }

        channelTotal = channelPrice.localCurrencyPrice;
        return DecimalUtil.calculateDiscount(channelTotal,entitledTotal);

    }

    public String getDiscountFromBLPrice() {
        if (!isSpecialBid()) {
            return "";
        }
        double spTotal = 0.0;
        double blTotal = 0.0;
        for (int i = 0; i < specialBidPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) specialBidPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode)) {
                spTotal = btp.localCurrencyPrice;
                break;
            }
        }

        for (int i = 0; i < blPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) blPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode)) {
                blTotal = btp.localCurrencyPrice;
                break;
            }
        }

        return DecimalUtil.calculateDiscount(spTotal,blTotal);


    }

    public String getCombinedDiscFromBLPrice(){
        if (!isSpecialBid() || !isChannel()) {
            return "";
        }
        double blTotal = 0.0;
        double channelTotal = 0.0;
        for (int i = 0; i < blPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) blPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode)) {
                blTotal = btp.localCurrencyPrice;
                break;
            }
        }

        channelTotal = channelPrice.localCurrencyPrice;
        return DecimalUtil.calculateDiscount(channelTotal,blTotal);
    }

    public String getMaxDiscount() {

        double maxDiscount = double_min_value;
        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if ((partTableDraft.showOverrideUnitPrice(item)) && item.getLineDiscPct() != 0.0d && item.getLineDiscPct() > maxDiscount && validateProratedDiscPrc(item)){
//            	if(!CommonServiceUtil.checkIsUsagePart(item)) // For Jackie's response, remove the checking code.Rtc #119739
            		maxDiscount = item.getLineDiscPct();
            }
        }

        return maxDiscount == double_min_value ? "0.000" : (DecimalUtil.formatTo5Number(maxDiscount));
    }

    public String getCombinedMaxDiscount() {

        double maxDiscount = double_min_value;
        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if (partTableDraft.showOverrideUnitPrice(item) && validateProratedDiscPrc(item)) {
                double chnlDisc = 0.0;
                if(item.getTotDiscPct() != null){
                    chnlDisc = item.getTotDiscPct().doubleValue();
                }
                if(chnlDisc != 0.0d && chnlDisc > maxDiscount){
                    maxDiscount = chnlDisc;
                }
            }
        }

        return maxDiscount == double_min_value ? "0.000" : (DecimalUtil.formatTo5Number(maxDiscount));
    }

    public String getMinDiscount() {
        double minDiscount = Double.MAX_VALUE;
        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if ((partTableDraft.showOverrideUnitPrice(item)) && item.getLineDiscPct() != 0.0d && item.getLineDiscPct() < minDiscount && validateProratedDiscPrc(item)){
//            	if(!CommonServiceUtil.checkIsUsagePart(item))    // For Jackie's response, remove the checking code.Rtc #119739
            		minDiscount = item.getLineDiscPct();
            }
        }

        return minDiscount == Double.MAX_VALUE ? "0.000" : (DecimalUtil.formatTo5Number(minDiscount));
    }

    public String getCombinedMinDiscount() {
        double minDiscount = Double.MAX_VALUE;
        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if (partTableDraft.showOverrideUnitPrice(item) && validateProratedDiscPrc(item)) {
				double chnlDisc = 0.0;
				if (item.getTotDiscPct() != null) {
					chnlDisc = item.getTotDiscPct().doubleValue();
				}
				if (chnlDisc != 0.0d && chnlDisc < minDiscount) {
					minDiscount = chnlDisc;
				}
            }
        }

        return minDiscount == Double.MAX_VALUE ? "0.000" : (DecimalUtil.formatTo5Number(minDiscount));
    }
    /**
     * Extract a public method for validating prorated discount price.
     * @param item
     * @return
     */
    private boolean validateProratedDiscPrc(QuoteLineItem item){
    	boolean result = false;
    	if(item.getLocalExtProratedDiscPrc() != null && item.getLocalExtProratedDiscPrc().doubleValue() > 0
		|| item.getLocalUnitProratedDiscPrc() != null && item.getLocalUnitProratedDiscPrc().doubleValue() > 0)
    		result = true;
    	return result;

    }

    public boolean isChannel() {
        QuoteHeader header = quote.getQuoteHeader();
        return QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc());
    }

    /**
     * @return Returns the overrideEndDateYear.
     */
    public int getOverrideEndDateYear() {
        return overrideEndDateYear;
    }
    /**
     * @return Returns the overrideStartDateYear.
     */
    public int getOverrideStartDateYear() {
        return overrideStartDateYear;
    }
    /**
     * @return Returns the yearRange.
     */
    public int getYearRange() {
        return yearRange;
    }

	/**
	 * @return Returns the formerYear.
	 */
	public int getFormerYear() {
		return formerYear;
	}

    public String getReportingCurrencyValue() {
        return REPORTING_CURRENCY_VALUE;
    }

    public List getCategoryList() {
        return categoryList;
    }

    public List getTotalPrices() {
        return totalPrices;
    }

    /**
     * @return Returns the tabURLs.
     */
    public PartPricingTabURLs getTabURLs() {
        return tabURLs;
    }

    public int getMaintStartYear(String revnStrmCode){
    	return backDate.getMaintStartYear(revnStrmCode);
    }

    public int getMaintEndYear(String revnStrmCode){
    	return backDate.getMaintEndYear(revnStrmCode);
    }

    public boolean isFutureStartDateAllowed(String revnStrmCode){
    	return PartPriceConfigFactory.singleton().isFutureStartDateAllowed(revnStrmCode);
    }

    public boolean showNoteRow(QuoteLineItem qli){
        return noteRow.showNoteRow(qli);
    }

    public boolean showEOLNote(QuoteLineItem qli){
    	return noteRow.showEOLNote(qli);
    }

    public boolean showPartGroups(QuoteLineItem qli){
        return noteRow.showPartGroups(qli) && !isPGSFlag();
    }

    public boolean showHasEOLPriceNote(QuoteLineItem qli){
    	return noteRow.showHasEOLPriceNote(qli);
    }

    public boolean showEntitledPriceOverriden(QuoteLineItem qli){
        return noteRow.showEntitledPriceOverriden(qli);
    }

    public boolean showCtLvPrcAppPartNote(QuoteLineItem qli) {
        return noteRow.showCtLvPrcAppPartNote(qli);
    }

    public boolean isCmprssCvrageEnabled(){
        return quote.getQuoteHeader().getCmprssCvrageFlag();
    }

    public boolean showExportRestrictedNote(QuoteLineItem qli) {
        return noteRow.showExportRestrictedNote(qli);
    }
    protected List getDistinctRevnStrmCategoryCode() throws ViewBeanException{
        List resultList = new ArrayList();
        Set resultSet = new HashSet();
        List priceTotalsList = quote.getPriceTotals();

        if (priceTotalsList != null) {
            RevnStrmCategoryDetail totalAll = new RevnStrmCategoryDetail_jdbc(QuoteConstants.PRICE_SUM_LEVEL_TOTAL,this.getI18NString("appl.i18n.partprice", SubmittedQuoteViewKeys.TOTALS_BY_BRAND), 0);
            resultSet.add(totalAll);
            for (int i = 0; i < priceTotalsList.size(); i++) {
                try {
                    QuotePriceTotals quotePriceTotals = (QuotePriceTotals) priceTotalsList.get(i);
                    if(quotePriceTotals.getExtAmount() <= 0){
                        continue;
                    }
                    if(QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equalsIgnoreCase(quotePriceTotals.getRevnStrmCategoryCode())){
                        continue;
                    }
                    RevnStrmCategoryDetail revnStrmCategoryDetail = CacheProcessFactory.singleton().create()
                            .getRevnStrmCategoryDetail(quotePriceTotals.getRevnStrmCategoryCode());
                    if(revnStrmCategoryDetail != null){
                    	resultSet.add(revnStrmCategoryDetail);
                    }
                } catch (QuoteException e) {
                    throw new ViewBeanException(e);
                }
            }
            if (resultSet != null) {
                resultList = new ArrayList(resultSet);
                CategoryComparator comparator = new CategoryComparator();
                Collections.sort(resultList, comparator);
            }
        }
        return resultList;

    }

    private static class CategoryComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            RevnStrmCategoryDetail rscd1 = (RevnStrmCategoryDetail) o1;
            RevnStrmCategoryDetail rscd2 = (RevnStrmCategoryDetail) o2;
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(rscd1.getRevnStrmCategoryCode())) {
                return -1;
            } else if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(rscd2.getRevnStrmCategoryCode())){
                return 1;
            }else{
	            if(Integer.toString(rscd1.getSortOrder()).compareTo(Integer.toString(rscd2.getSortOrder())) == 0){
	                return rscd1.getRevnStrmCategoryCodeDscr().compareTo(rscd2.getRevnStrmCategoryCodeDscr());
	            }else{
	                return Integer.toString(rscd1.getSortOrder()).compareTo(Integer.toString(rscd2.getSortOrder()));
	            }
            }
        }
    }

    public String getUIStringData(String brandDescription, Object boldData, Object noneBoldData) {
        String returnStr = new String("");
        if (brandDescription != null && brandDescription.equalsIgnoreCase(SubmittedQuoteViewKeys.TOTAL)) {
            returnStr = "<strong>" + (boldData == null ? "" : boldData.toString()) + "</strong>";
        } else {
            returnStr =  noneBoldData == null ? "" : noneBoldData.toString();
        }
        return returnStr;
    }

    public boolean validateMapValuesAllEmpty(Map validateMap){
        if(validateMap == null || validateMap.size() == 0){
            return false;
        }else{
            List list = new ArrayList(validateMap.values());
            if(list == null || list.size() == 0){
                return true;
            }else{
                boolean flag = true;
                for (int i = 0; i < list.size(); i++) {
                    if(list.get(i) != null && ((List)list.get(i)).size() > 0){
                        flag = false;
                        break;
                    }
                }
                return flag;
            }
        }
    }

    public boolean showBrandDetailInTotalsLink() {
        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if ((partTableDraft.showOverrideUnitPrice(item))) {
                return true;
            }
        }
        return false;
    }

    public double getLatamUpliftPct(){
        String strPercet = DecimalUtil.formatTo5Number((quote.getQuoteHeader().getLatamUpliftPct() / 1000 + 0.0000000001));
        return Double.parseDouble(strPercet);
    }
    public boolean isDoSpecialBidCommonInit()    {
        return true;
    }

    public boolean getDisplayUpdateApprFlag()    {
        return true;
    }

    public boolean getEditableFlag() {
        return editableFlag;
    }

    public String getUpdateApprDisplayAction()    {
        return SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_PART_PRICE_TAB;
    }

    public int getPricesSize(List pricesList){
        int pricesSize = 0;
        Iterator pricesIt = pricesList.iterator();
        while (pricesIt.hasNext()) {
            BrandTotalPrice btp = (BrandTotalPrice)pricesIt.next();
            if(btp.localCurrencyPrice > 0 ){
                pricesSize += 1;
            }
        }
        return pricesSize;
    }

    public String getUnitPirceFondColorStyle(QuoteLineItem qli){
    	if(qli.getLocalExtProratedPrc() == null){
    		return "color:#000000;";
    	}
    	else{
    		return "color:#AAAAAA;";
    	}
    }

    public String getBidUnitPirceFondColorStyle(QuoteLineItem qli){
    	if(qli.getLocalExtProratedDiscPrc() == null){
    		return "color:#000000;";
    	}
    	else{
    		return "color:#AAAAAA;";
    	}
    }

    public String getBpRateFondColorStyle(QuoteLineItem qli){
    	if((qli.isSaasPart() || qli.isMonthlySoftwarePart()) && CommonServiceUtil.checkIsUsagePart(qli)){
    		return "color:#000000;";
    	}
    	else{
    		return "color:#AAAAAA;";
    	}
    }

    public boolean shouldBoldFondForUnitPrice(QuoteLineItem qli){
    	if(qli.getLocalExtProratedPrc() == null){
    		return true;
    	}
    	else{
    		return false;
    	}
    }

    public boolean shouldBoldFondForBidUnitPrice(QuoteLineItem qli){
    	if(qli.getLocalExtProratedDiscPrc() == null){
    		return true;
    	}
    	else{
    		return false;
    	}
    }

    public boolean shouldBoldFondForUnitBpRate(QuoteLineItem qli){
    	if((qli.isSaasPart() || qli.isMonthlySoftwarePart()) && CommonServiceUtil.checkIsUsagePart(qli)){
    		return true;
    	}
    	else{
    		return false;
    	}
    }

    /**
     * @param configrtn
     * @return get the co-termed configuration text, e.g. Sterling Test PA - 31-May-2012
     */
    public String getCotermConfigrtnStr(PartsPricingConfiguration configrtn){
    	String cotermConfigrtnStr = getI18NString("appl.i18n.partprice", PartPriceViewKeys.DO_NOT_CO_TERM);
    	String cotermConfigrtnId = configrtn.getCotermConfigrtnId();
    	if(cotermConfigrtnId == null || "".equals(cotermConfigrtnId)){
    		return cotermConfigrtnStr;
    	}
    	Map ctConfigrtnMap = configrtn.getCotermMap();
    	String CANum = StringUtils.isBlank(quote.getQuoteHeader().getRefDocNum()) ? "" : quote.getQuoteHeader().getRefDocNum();
    	if(CANum == null || "".equals(CANum)){
    		return cotermConfigrtnStr;
    	}
    	List cotermList = (List) ctConfigrtnMap.get(CANum);
    	if(cotermList == null){
    		return cotermConfigrtnStr;
    	}
    	for (Iterator iterator = cotermList.iterator(); iterator.hasNext();) {
			CoTermConfiguration ctConfigrtn = (CoTermConfiguration) iterator.next();
			if(cotermConfigrtnId.equals(ctConfigrtn.getCotermConfigrtnId())){
				cotermConfigrtnStr = ctConfigrtn.getIbmProdIdDscr() + " - " + DateUtil.formatDate(ctConfigrtn.getEndDate(), "dd-MMM-yyyy", locale);
				break;
			}
		}
    	return cotermConfigrtnStr;
    }

    public boolean showSubmittedPriorSSEntitledUnitPrice(){
    	return this.isSpecialBid ;
    }


    /**
     * check if show standard discount column for PGS quote
     * @return
     */
    public boolean isShowDiscountCol4PGS(){
    	return isCustomerMatchDistributor();
    }

    /**
     * check if show Bid TCV column for PGS quote
     * @return
     */
    public boolean isShowBidTcvCol4PGS(){
    	return isCustomerMatchDistributor();
    }

    /**
     * check if show BP discount column for PGS quote
     * @return
     */
    public boolean isShowBpDiscountCol4PGS(){
    	return isCustomerMatchDistributor();
    }

    /**
     * check if show BP TCV column for PGS quote
     * @return
     */
    public boolean isShowBpTcvCol4PGS(){
    	return isCustomerMatchDistributor();
    }

	/**
	 * Check if show 'Edit Provisioning Form' link for submitted quote
	 * if at least one line item has provisioning hold within the SaaS brand, show the link
	 * else hide the link
	 * @return
	 */
	public boolean isShowEditProvisioningForm(String saasBrandCode){
		List<PartsPricingConfiguration> configrtnList =  getSaasBrandMap().get(saasBrandCode);
		if(configrtnList != null ){
			for (Iterator iterator = configrtnList.iterator(); iterator.hasNext();) {
				PartsPricingConfiguration configrtn = (PartsPricingConfiguration) iterator.next();
				List<QuoteLineItem> qliList = (List<QuoteLineItem>) quote.getPartsPricingConfigrtnsMap().get(configrtn);
				if(qliList != null){
					for (Iterator iterator2 = qliList.iterator(); iterator2.hasNext();) {
						QuoteLineItem quoteLineItem = (QuoteLineItem) iterator2.next();
						if(quoteLineItem.isHasProvisngHold()){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public String getConfigLevelForAddOnAndTradeUp(List subSaasLineItems,String resource){
		String configLevel = "";
		if(subSaasLineItems != null){
		for (Object obj : subSaasLineItems){
			QuoteLineItem qli = (QuoteLineItem)obj;
			if (PartPriceConstants.RenewalModelCode.CONFIG_RENWL_MDL_CODE_LEVEL.equalsIgnoreCase(qli.getRenwlMdlCodeLevel())){
				configLevel = partTable.getRewalModCodeDesc(qli);
				break;
			}
		}
		}
		if (StringUtils.isNotBlank(configLevel)){
			return getI18NString(resource,configLevel);
		}
		return configLevel;
	}
	
	public boolean isConfigLevel(List subSaasLineItems){
		boolean isConfigLvl = false;
		String configLevel = "";
		if(subSaasLineItems != null){
			for (Object obj : subSaasLineItems){
				QuoteLineItem qli = (QuoteLineItem)obj;
				configLevel = partTable.getRewalModCodeDesc(qli);
				if (PartPriceConstants.RenewalModelCode.CONFIG_RENWL_MDL_CODE_LEVEL.equalsIgnoreCase(qli.getRenwlMdlCodeLevel()) 
						&& configLevel != null
						&& !"".equals(configLevel)){
					isConfigLvl = true;
					break;
				}
			}
		}
		return isConfigLvl;
	}
	
	//Appliance#99
	
	public boolean isDisplayCRAD(QuoteLineItem qli){
		if (qli==null){
			return false;
		}
		// For Ownership transfered parts
    	if (qli.isOwerTransferPart()) {
			return false;
		}
    	boolean isApplncSendMFGFlg = qli.getApplncSendMFGFLG();
    	boolean isMainPartFlg = qli.isApplncMain();
    	boolean isTransceiverWithAppIdFlg = qli.isApplncTransceiver()&&(qli.getConfigrtnId()==null||qli.getConfigrtnId().equals("NOT_ON_QUOTE"));
    	return isApplncSendMFGFlg &&(isMainPartFlg||isTransceiverWithAppIdFlg);
    }
	
	public java.util.Date getLineItemCRAD(QuoteLineItem qli){
    	return qli!=null?qli.getLineItemCRAD():null;
    }
	
    public String getOrgLineItemCRAD(java.util.Date lineItemCRAD){
    	if (lineItemCRAD !=null){
    		return DateUtil.formatDate(lineItemCRAD, DateUtil.PATTERN);
    	} else {
    		return "";
    	}
    	
    }
	
	public String getLineItemCRADForShow(QuoteLineItem qli){
		if (qli==null){
			return "";
		} 
		Date lineItemCRAD=qli.getLineItemCRAD();
		return DateUtil.formatDate(lineItemCRAD,DateUtil.PATTERN5);
	}
	
    public Collection generateCRADDayOptions(int lineItemCRADday) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);
        
        if (lineItemCRADday == 0)
            collection.add(new SelectOptionImpl(sDay, "", true));
        else
            collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (lineItemCRADday == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), false));

        }
        return collection;
    }

    public Collection generateCRADAnniversaryOptions(int lineItemCRADMonth) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};
        
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        if (lineItemCRADMonth == 0)
            collection.add(new SelectOptionImpl(labelString, "", true));
        else
            collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (lineItemCRADMonth == i)
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }

    	return collection;

    }

    public Collection generateCRADYearOptions(int lineItemCRADYear) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        boolean yearSelected = (lineItemCRADYear!= currYear && lineItemCRADYear != (currYear + 1));
        boolean currSelected = (lineItemCRADYear == currYear);
        boolean nextSelected = (lineItemCRADYear == (currYear + 1));

        collection.add(new SelectOptionImpl(sYear, "", yearSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear), String.valueOf(currYear), currSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear + 1), String.valueOf(currYear + 1), nextSelected));

        return collection;
    }
    
    public String getToTalExtendedPriorPrice() {
 	   List lineItemList = quote.getMasterSoftwareLineItems();
 	   double  totalExtendedPriorPrice = 0;
 	   Double extendedPriorPrice = null;
 	   if(lineItemList != null && lineItemList.size() > 0){
 		   for(int i = 0; i < lineItemList.size(); i++){
 			   QuoteLineItem lineItem =(QuoteLineItem) lineItemList.get(i);
 			   
 			  if(lineItem.getRenewalQuoteNum() != null && !"".equals(lineItem.getRenewalQuoteNum().trim())){
 				 if(!lineItem.isApplncPart()){
	 				   extendedPriorPrice = GDPartsUtil.getExtndLppPrice(lineItem, quote.getQuoteHeader());
	 				   if(extendedPriorPrice != null ){
	 	 				   totalExtendedPriorPrice = totalExtendedPriorPrice + extendedPriorPrice;
	 	 			   }
 				 	}   
 			   }
 			   
 		   }
 	   }
 	  double omittedTotalExtendPriorPrice = 0;
	   OmitRenewalLine omitRenewalLine = null;
	   Double omitRenewalLinePrice = null;
	   if(quote.getQuoteHeader() != null && quote.getQuoteHeader().isOmittedLine()){
		   try {
			omitRenewalLine = QuoteLineItemFactory.singleton().getOmittedRenewalLine(quote.getQuoteHeader().getWebQuoteNum());
		} catch (TopazException e) {
		
			e.printStackTrace();
		}
	   }
	   if(omitRenewalLine != null){
		   omitRenewalLinePrice = omitRenewalLine.getOmittedLinePrice();
	   }
	   if(omitRenewalLinePrice != null){
		   omittedTotalExtendPriorPrice = omitRenewalLinePrice.doubleValue();
	   }
	   if(quote.getQuoteHeader() != null && quote.getQuoteHeader().isOmittedLine()){
		   if(totalExtendedPriorPrice > 0 || omittedTotalExtendPriorPrice > 0 ){
			   return DecimalUtil.format(totalExtendedPriorPrice + omittedTotalExtendPriorPrice,2);
		   }
		   
	   }else if(quote.getQuoteHeader() != null && quote.getQuoteHeader().isOmittedLine() == false){
		   if(totalExtendedPriorPrice > 0 ){
			   return DecimalUtil.format(totalExtendedPriorPrice ,2);  
		   }
		  
	   }else{
		   return "";
	   }
		return "";
 			 
 		
    }
    
    public boolean isShowSubmitCRADLink(){
    	if (!isEditable()) return false;
    	ArrayList quoteLineItemList = (ArrayList) quote.getMasterSoftwareLineItems();
    	if (quoteLineItemList.size()>0) {
    		Iterator iter = quoteLineItemList.iterator();
    		while (iter.hasNext()) {
        		QuoteLineItem qli = (QuoteLineItem)iter.next();
        		boolean isCRADDisplayd = qli.getApplncSendMFGFLG()&&(qli.isApplncMain()||qli.isApplncUpgrade());
        		if (isCRADDisplayd) return true;
    		}
    	}	
    	return false;
    }

    
    public boolean isQuoteBeforeOrder(){
    	return QuoteCommonUtil.isQuoteBeforeOrder(header);
    }
    

    
    public boolean isShowDeploymentID(){
    	if (!isEditable()) return false;
    	ArrayList quoteLineItemList = (ArrayList) quote.getMasterSoftwareLineItems();
    	if (quoteLineItemList.size()>0) {
    		Iterator iter = quoteLineItemList.iterator();
    		while (iter.hasNext()) {
        		QuoteLineItem qli = (QuoteLineItem)iter.next();
        		boolean isDeploymetIDDisplayd = qli.isDeploymentAssoaciatePart();
        		if (isDeploymetIDDisplayd) return true;
    		}
    	}	
    	return false;
    }
    public Boolean isSaasRenewalFlag() {
    	return quote.getQuoteHeader().isSaasRenewalFlag();
    }
    
    public Boolean isSaasMigrationFlag() {
    	return quote.getQuoteHeader().isSaasMigrationFlag();
    }
	public Boolean isMonthlyRenewalFlag(){
		return quote.getQuoteHeader().isMonthlyRenewalFlag();
	}
	
	public Boolean isMonthlyMigrationFlag() {
		return quote.getQuoteHeader().isMonthlyMigrationFlag();
	}
	
	public Boolean isNewServiceFlag(QuoteLineItem qli) {
		return qli.isNewService();
	}
	
	public boolean isRenewalFlag(QuoteLineItem qli){
		return qli.isSaasRenwl();
	}
	
	public boolean isMigrationFlag(QuoteLineItem qli){
		return qli.isWebMigrtdDoc();
	}
	
	public List<MonthlySwLineItem> getMonthlySwLineItemsList() {
		return quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
	}

	public boolean isShowIsNewServiceLine(QuoteLineItem qli){
		QuoteHeader header=quote.getQuoteHeader();
		// prevent ramp up part show line item questions
		if (qli.isRampupPart()){
			return false;
		}
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
	
	// if header level question should be shown
	public boolean isShowRenewalMigrationQForSaas(){
		if (quote.getQuoteHeader().isSaasMigrationFlag()!=null||quote.getQuoteHeader().isSaasRenewalFlag()!=null){
			return true;
		} else return false;
	}
	
	// if header level question should be shown
	public boolean isShowRenewalMigrationQForMonthly() {
		if (quote.getQuoteHeader().isMonthlyRenewalFlag()!=null||quote.getQuoteHeader().isMonthlyMigrationFlag()!=null){
			return true;
		} else return false;
	}
}