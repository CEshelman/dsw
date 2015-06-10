package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.CoTermConfiguration;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.MonthlySwQuoteDomain;
import com.ibm.dsw.quote.common.domain.OmitRenewalLine;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemBillingOption;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.UIFormatter;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.DraftPartTableElementUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.ItemCoverageDates;
import com.ibm.dsw.quote.draftquote.viewbean.helper.NoteRow;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PVUDetail;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PVUSection;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartInfo;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPricingTabURLs;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTable;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTableTotal;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartsAndPrice;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PricingNotes;
import com.ibm.dsw.quote.draftquote.viewbean.helper.ReasonRow;
import com.ibm.dsw.quote.draftquote.viewbean.helper.SoftwareMaintenance;
import com.ibm.dsw.quote.draftquote.viewbean.helper.TopSection;
import com.ibm.dsw.quote.pvu.config.VUActionKeys;
import com.ibm.dsw.quote.submittedquote.viewbean.SubmittedQuotePartPricingViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 *
 * <p>
 * Copyright 2006 by IBM Corporation All rights reserved.
 * </p>
 *
 * <p>
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * </p>
 *
 * @author tboulet <br/>
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney </a> <br/>
 *
 */
public class DraftQuotePartPricingViewBean extends DraftSQBaseViewBean {

/**
	 *
	 */
	private static final long serialVersionUID = 3028229415144083808L;
	//
	transient LogContext logContext = LogContextFactory.singleton().getLogContext();
    public PartsAndPrice partAndPrice;

    public TopSection topSection;

    public PricingNotes pricingNotes;

    public PVUSection pvuSection;

    public PartInfo partInfo;

    public PartTable partTable;

    public DraftPartTableElementUtil partTableElementUtil;

    public PVUDetail pvuDetail;

    public SoftwareMaintenance softwareMaintenance;

    public NoteRow noteRow;

    public ReasonRow reasonRow;

    public ItemCoverageDates backDate;

    public PartTableTotal partTableTotal;

    public PartPricingTabURLs tabURLs;

    private boolean priceEngineAvailable;

    protected boolean rqEditable = false;

    private int overrideStartDateYear;

    private int overrideStartDateMonth;

    private int overrideStartDateDay;

    private int overrideEndDateYear;

    private int overrideEndDateMonth;

    private int overrideEndDateDay;

    private int startDateYear;

    private int startDateMonth;

    private int startDateDay;

    private int endDateYear;

    private int endDateMonth;

    private int endDateDay;

    public boolean isFTL = false;

    //from 0 to 20
    private static int yearRange = 20;

    private Boolean engineUnAvailable;

    private  UIFormatter formatter;

    private SubmittedQuotePartPricingViewBean subPPViewBean;

    private boolean displayApplyOfferForBidIteration = false;

    private boolean displayDiscountForBidIteration = false;

    private boolean hasRampUpPartFlag;

    /**
     * Constructor
     */
    public DraftQuotePartPricingViewBean() {
        super();

    }

    /**
     * @param params
     * @throws ViewBeanException
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters params) throws ViewBeanException {
        // this.quote = FakeQuoteFactory.geFakeQuote();
        super.collectResults(params);
        if(isBidIteratnQt()){
	        subPPViewBean = new SubmittedQuotePartPricingViewBean();
	        subPPViewBean.collectResults(params);
	        setSubPPViewBean(subPPViewBean);
        }
        engineUnAvailable = (Boolean)params.getParameter(DraftQuoteParamKeys.Price_Engine_UnAvailable);

        LogContextFactory.singleton().getLogContext().debug(this,"engineUnAvailable value... " + engineUnAvailable);
        formatter = new UIFormatter(quote);
        if (quote.getQuoteHeader().isRenewalQuote()) {
            rqEditable = quote.getQuoteAccess().isCanEditRQ();
        }

        partAndPrice = new PartsAndPrice(quote);

        topSection = new TopSection(quote);

        pricingNotes = new PricingNotes(quote);

        backDate = new ItemCoverageDates(quote);

        pvuSection = new PVUSection(quote);

        partInfo = new PartInfo(quote);

        partTable = new PartTable(quote, null, getLocale());

        partTableElementUtil = new DraftPartTableElementUtil(quote, partTable);

        pvuDetail = new PVUDetail(quote);

        softwareMaintenance = new SoftwareMaintenance(quote);

        reasonRow = new ReasonRow(quote);

        noteRow = new NoteRow(quote,getLocale());

        partTableTotal = new PartTableTotal(quote);

        tabURLs = new PartPricingTabURLs(quote);

        displayApplyOfferForBidIteration = !isBidIteratnQt() || (isBidIteratnQt() && quote.getQuoteHeader().getOfferPrice() != null && quote.getQuoteHeader().getOfferPrice().doubleValue() != 0);

        displayDiscountForBidIteration = !isBidIteratnQt() || (isBidIteratnQt() && !(quote.getQuoteHeader().getOfferPrice() != null && quote.getQuoteHeader().getOfferPrice().doubleValue() != 0));

        hasRampUpPartFlag = quote.getQuoteHeader().isHasRampUpPartFlag();
        
    }
    
    //Appliance#99
    /**
     * @return return valid lineItem CRAD or header CRAD if lineItem CRAD= null 
     */
    public Date getLineItemCRAD(int index){
    	ArrayList quoteLineItemList = (ArrayList) quote.getMasterSoftwareLineItems();
    	Date custReqArrvDate = header.getCustReqstArrivlDate();
    	Date lineItemCRAD = new Date();
    	if(quoteLineItemList==null||quoteLineItemList.size()==0){
    		return lineItemCRAD;
    	}
    	QuoteLineItem qli = (QuoteLineItem) quoteLineItemList.get(index);
    	if (qli!=null){
    		lineItemCRAD = qli.getLineItemCRAD();
    	}
    	if (lineItemCRAD == null){
    		lineItemCRAD = custReqArrvDate;
    	} 
    	return lineItemCRAD;
    }
    
    public String getOrgLineItemCRAD(Date lineItemCRAD){
    	if (lineItemCRAD !=null){
    		logContext.debug(this, "lineItemCRAD for show ="+DateUtil.formatDate(lineItemCRAD, DateUtil.PATTERN5));
    		return DateUtil.formatDate(lineItemCRAD, DateUtil.PATTERN);
    	} else {
    		logContext.debug(this, "lineItemCRAD for show = blank");
    		return "";
    	}
    	
    }
    
    public boolean isDisplayCRAD(int index){    	
    	ArrayList quoteLineItemList = (ArrayList) quote.getMasterSoftwareLineItems();
    	QuoteLineItem qli = (QuoteLineItem) quoteLineItemList.get(index);
    	// For Ownership transfered parts
    	if (qli.isOwerTransferPart()) {
			return false;
		}
    	
    	boolean isApplncSendMFGFlg = qli.getApplncSendMFGFLG();
    	boolean isMainPartFlg = qli.isApplncMain();
    	boolean isUpgradeFlg = qli.isApplncUpgrade();
    	logContext.debug(this, "Part Num:"+ qli.getPartNum() + "  Appliance Id:" + qli.getConfigrtnId()+" isMainPart:" + isMainPartFlg + " isUpgrade:"+isUpgradeFlg + " ApplncSendMFGFlg:"+isApplncSendMFGFlg);
    	return isApplncSendMFGFlg &&(isMainPartFlg||isUpgradeFlg);
    }
    
    public String getHeaderCRAD(){
    	Date headerCRAD = quote.getQuoteHeader().getCustReqstArrivlDate();
    	if (headerCRAD!=null){
    		Calendar CRADDate = Calendar.getInstance();
    		CRADDate.setTime(headerCRAD);
    		String day = StringHelper.fillString(String.valueOf(CRADDate.get(Calendar.DAY_OF_MONTH)),2,'0');
    		String month =StringHelper.fillString(String.valueOf(CRADDate.get(Calendar.MONTH)+1),2,'0');
    		String year = String.valueOf(CRADDate.get(Calendar.YEAR));
    		return year+"-"+month+"-"+day;
    	}
    	return "";
    }

    /**
     * @return Returns the isFTL.
     */
    public boolean isFTL(QuoteLineItem lineItem) {
        if (lineItem.getPartDispAttr() != null) {
            isFTL = lineItem.getPartDispAttr().isFtlPart();
        }

        return isFTL;
    }

    public List getMasterSoftwareLineItems() {
    	return quote.getMasterSoftwareLineItems();
    }


    /**
     *
     * @return
     */
    public String getTotalPoints() {
        return formatter.formatPoint(quote.getQuoteHeader().getTotalPoints());

    }

    /**
     *
     * @return
     */
    public String getTotalPrice() {
        if(this.isPriceUnAvaialbe()){
            return DraftQuoteConstants.BLANK;
        }

        return formatter.formatEndCustomerPrice(quote.getQuoteHeader().getQuotePriceTot());

    }

    /**
     * Added by lee,March 30,2007
     *
     * @return row span number by judging what will be shown
     */
    public int getRowSpan(QuoteLineItem lineItem) {
        int rowNumber = 3;

        if (isPA() || isPAE() || isFCT() || isOEM() || isSSP()) {
        	if(isOEM() || isFCT()){
        		rowNumber = 2;
        	}
            if (pvuDetail.showPVUDetailsRow(lineItem))
                rowNumber++;
            if (softwareMaintenance.showSoftwareMaintenanceCoverage(lineItem))
                rowNumber++;
            if (noteRow.showNoteRow(lineItem))
                rowNumber++;
            if (softwareMaintenance.showAddYearsMaintenanceDropDown(lineItem)) {
                int addiYearCovLen = lineItem.getAddtnlMaintCvrageQty();
                rowNumber = rowNumber + addiYearCovLen;

            }
            //renewal quote row
            if (partTable.isAddedFromRenewalQuote(lineItem))
                rowNumber++;

            //reference Information Row
            if ((partTable.showRenewalPriceRSVP(lineItem, quoteUserSession)|| partTable.showRenewalPricePYP(lineItem, quoteUserSession)
            		|| partTable.showRenewalPriceMethod(lineItem, quoteUserSession)
            		|| GrowthDelegationUtil.isDisplayYTYStatusMessage(this.getQuote(), lineItem)) && !GrowthDelegationUtil.isNotShowRefWhenContainsExcludedRevstream(lineItem))
            	rowNumber++;

            //reseller authorization row
            if((isPA() || isPAE() || isOEM()|| isSSP()) && partTable.isPartControlled(lineItem)){
                rowNumber++;
            }
        }

        else if (isPPSS()) {
            rowNumber = 1;

            if (noteRow.showEndOfLifeDateNote(lineItem) ||
                    noteRow.showPartObsoleteNote(lineItem))
                rowNumber++;
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
        	rowNumber++;
        }
        
        if (PartPriceCommon.isShowApplncMTMMsg(lineItem)) {
        	rowNumber++;
        }

		return rowNumber;
    }



    public String getAppPvuConfigUrl() {
        return HtmlUtil.getQuoteAppUrl() + HtmlUtil.getURLForAction(VUActionKeys.APPLY_PVU_CONFIG);
    }

    public String getDisplayTabAction() {
        return DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB;
    }

    public String getPostTabAction() {
        return DraftQuoteActionKeys.POST_PART_PRICE_TAB;
    }

    public boolean isPriceUnAvaialbe(){
        return this.engineUnAvailable.booleanValue();
    }

    public PartPricingTabURLs getTabURLs() {
        return tabURLs;
    }

    // get price level dropdownlist options
    public Collection generateVolTranLevelOptions() throws Exception {
        Collection collection = new ArrayList();
        String ovrrdLevel = quote.getQuoteHeader().getOvrrdTranLevelCode();
        String code = "";
        String desc = "";
        Iterator itr = PartPriceConfigFactory.singleton().getOverridePriceLevels().iterator();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String boDefaultOption = context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,
                PartPriceViewKeys.TEXT_DEFAULT_SVP_LEVEL);
        if (StringUtils.isBlank(ovrrdLevel)) {
            collection.add(new SelectOptionImpl(boDefaultOption, "", true));
        } else {
            collection.add(new SelectOptionImpl(boDefaultOption, "", false));
        }
        Customer customer = quote.getCustomer();
        while (itr.hasNext()) {
            CodeDescObj priceLevel = (CodeDescObj) itr.next();
            code = priceLevel.getCode();
            desc = priceLevel.getCodeDesc();            
            if (customer != null && customer.isXSPCustomer() && QuoteConstants.PRICE_LEVEL_B.equals(code)) {
            	continue;
            }
            if (ovrrdLevel != null && code != null && ovrrdLevel.trim().equalsIgnoreCase(code.trim())) {
                collection.add(new SelectOptionImpl(desc, code, true));
            } else {
                collection.add(new SelectOptionImpl(desc, code, false));
            }
        }
        
        return collection;
    }

    public int getAdditionalYear(){
        String lob = quote.getQuoteHeader().getLob().getCode();
        return PartPriceConfigFactory.singleton().getAddtionalYear(lob);
    }


    public String getSQOverrideDateParams(QuoteLineItem qli){
        StringBuffer sb = new StringBuffer();
        sb.append("partNum=").append(qli.getPartNum().trim());
        sb.append("&seqNum=").append(qli.getSeqNum());
        sb.append("&startDate=").append(partTable.getStartDateText(qli));
        sb.append("&endDate=").append(partTable.getEndDateText(qli));
        //double encode this for the javascript pop-up window
        sb.append("&revnStrmCode=").append(HtmlUtil.urlEncode(HtmlUtil.urlEncode(qli.getRevnStrmCode())));
        sb.append("&isSpecialBidRnwlPart=").append(isSpecialBidRnwlPart(qli));
        sb.append("&allowEditStartDate=").append(isAllowEditStartDate(qli));
        sb.append("&allowEditEndDate=").append(isAllowEditEndDate(qli));
        sb.append("&cmprssCvrageApplied=").append(qli.hasValidCmprssCvrageMonth() );

        return sb.toString();
    }

    public boolean isOUPRequired(QuoteLineItem qli){
        return (qli.isObsoletePart() && qli.canPartBeReactivated() && !qli.isHasEolPrice());
    }

    public boolean isCmprssCvrageEnabled(){
        return quote.getQuoteHeader().getCmprssCvrageFlag();
    }

    public double getLatamUpliftPct(){
        String strPercet = DecimalUtil.formatTo5Number((quote.getQuoteHeader().getLatamUpliftPct() / 1000 + 0.0000000001));
        return Double.parseDouble(strPercet);
    }

    //display for bid iteration
    public SubmittedQuotePartPricingViewBean getSubPPViewBean() {
        return subPPViewBean;
    }
    public void setSubPPViewBean(SubmittedQuotePartPricingViewBean subPPViewBean) {
        this.subPPViewBean = subPPViewBean;
    }
    public boolean isDisplayApplyOfferForBidIteration() {
        return displayApplyOfferForBidIteration;
    }
    public boolean isDisplayDiscountForBidIteration() {
        return displayDiscountForBidIteration;
    }
    public boolean isHasRampUpPartFlag() {
        return hasRampUpPartFlag;
    }
    public boolean isShowGSAPricing() {
        return topSection.showGSAPricing() && !isBidIteratnQt();
    }
    public boolean isShowFindPart() {
        return !isBidIteratnQt();
    }
    public boolean isShowQHDisPctOrChnlMarginCol() {
        return (partInfo.showQuoteHeaderDiscountPct()||partTable.showChannelMarginCol()) && isDisplayDiscountForBidIteration();
    }
    public boolean isShowApplyOfferOrPrcBandOvrrd() {
        return (partInfo.showApplyOffer() || partInfo.showPrcBandOvrrd()) && isDisplayApplyOfferForBidIteration();
    }
    public boolean isShowClearOfferLink() {
        return !isBidIteratnQt();
    }
    public boolean isDisabled4PartQty(QuoteLineItem qli) {
    	// For Ownership transfered parts
    	if (qli.isOwerTransferPart()) {
			return true;
		}
    	
    	return  (isBidIteratnQt() && (qli.isSaasPart() || qli.isMonthlySoftwarePart()));
    }
    public boolean isDisabled4EntitledPrice() {
        return isBidIteratnQt();
    }
    public boolean isDisabled4DiscPct(QuoteLineItem qli) {
        return disableDiscPctInput(qli) || !isDisplayDiscountForBidIteration() || qli.isSaasSubsumedSubscrptnPart();
    }
    public boolean isDisabled4AddiDisPer(QuoteLineItem qli) {
        return qli.isObsoletePart() || !isDisplayDiscountForBidIteration();
    }
    public boolean isDisabled4AddiBpOverrideDis(QuoteLineItem qli) {
        return partTable.disableBpDiscountInputBox(qli) || !isDisplayDiscountForBidIteration();
    }

    /**
     * @return
     * List
     *  get SaaS line items
     */
    public List getSaaSLineItems() {
    	return quote.getSaaSLineItems();
    }

    /**
     * @param lineItem
     * @return
     * int
     *  get SaaS part duplicate row span
     */
    public int getSaaSRowSpan(QuoteLineItem lineItem) {
        int rowNumber = 3;

        if (isPA() || isPAE() || isFCT() || isOEM() || isSSP()) {
        	if(isOEM() || isFCT()){
        		rowNumber = 2;
        	}

			if (pvuDetail.showPVUDetailsRow(lineItem) && !lineItem.isMonthlySoftwarePart())
                rowNumber++;
            if (noteRow.showNoteRow(lineItem))
                rowNumber++;

            //renewal quote row
            if (partTable.isAddedFromRenewalQuote(lineItem))
                rowNumber++;
            //reseller authorization row
            if((isPA() || isPAE() || isOEM()|| isSSP()) && partTable.isPartControlled(lineItem)){
                rowNumber++;
            }
            //committed terms row
            if(partTable.showTermSelection(lineItem)){
            	rowNumber++;
            }
            //billing frequency row
            if(partTable.showBillingFrequency(lineItem)){
            	rowNumber++;
            }
            //saas renewal row, refer to rtc#207982
			if (!quote.getQuoteHeader().isFCTToPAQuote()
					&& !hasFctToPAFinalization()) {
              if (showSaasRenwl(lineItem)
    				|| partTable.showRenwlModeForSubscrptn(lineItem)
    				|| partTable.showFixedModelForSubscrptn(lineItem)) {
    			rowNumber++;
    		  }
        }
            
			
			// if(isDisplayMigration(lineItem)){
			// rowNumber++;
			// }
        }
        else if (isPPSS()) {
            rowNumber = 1;

            if (noteRow.showEndOfLifeDateNote(lineItem) ||
                    noteRow.showPartObsoleteNote(lineItem))
                rowNumber++;
        }

        return rowNumber;
    }






    /**
     * @param qli
     * @return
     * @throws Exception
     * Collection
     *  get caverage term dropdownlist options
     */
    public Collection getCvrageTermOptions(QuoteLineItem qli) throws Exception {
        Collection collection = new ArrayList();
        int cvrageTerm = -1;

        if(qli.getICvrageTerm() != null){
        	cvrageTerm = qli.getICvrageTerm().intValue();
        }

        List cvrageTermsList = partTable.getCvrageTermsList(qli);
        if(cvrageTermsList != null && cvrageTermsList.size() > 0){
	        String code = "" + cvrageTerm;
	        for(int i = 0; i < cvrageTermsList.size(); i ++){
	        	String tempTerm =  ((Integer)cvrageTermsList.get(i)).toString();
	        	if(code.equals(tempTerm)){
	        		collection.add(new SelectOptionImpl(tempTerm, tempTerm, true));
	        	}else{
	        		collection.add(new SelectOptionImpl(tempTerm, tempTerm, false));
	        	}
	        }
        }
        logContext.debug(this, qli.getPartNum()+"_"+qli.getSeqNum()+" getCvrageTermOptions return "+getOptionsString(collection));
        return collection;
    }

    /**
     * @param qli
     * @return
     * @throws Exception
     * Collection
     *  get override dropdownlist options
     */
    public Collection getOvrridDropdownOptions(QuoteLineItem qli) throws Exception {
        Collection collection = new ArrayList();
        //TODO need to add real logic to identify the override type
        //need to add a column to store the override type in table ebiz1.web_quote_line_item
        //say the method is getOvrrdType()
        //int overrideTypeInd = qli.getOvrrdType()
        String code = PartPriceConstants.SaaSOverrideType.DEFAULT;
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String unit = context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,
                PartPriceViewKeys.OVERRIDE_TYPE_UNIT);
        String extended = context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,
                PartPriceViewKeys.OVERRIDE_TYPE_EXTENDED);
        if(code.equals(PartPriceConstants.SaaSOverrideType.UNIT)){
        	collection.add(new SelectOptionImpl(unit, code, true));
        	collection.add(new SelectOptionImpl(extended, code, false));
        }else if(code.equals(PartPriceConstants.SaaSOverrideType.EXTENDED)){
        	collection.add(new SelectOptionImpl(unit, code, false));
        	collection.add(new SelectOptionImpl(extended, code, true));
        }else{
        	collection.add(new SelectOptionImpl(unit, PartPriceConstants.SaaSOverrideType.UNIT, true));
        	collection.add(new SelectOptionImpl(extended, PartPriceConstants.SaaSOverrideType.EXTENDED, false));
        }

        logContext.debug(this, qli.getPartNum()+"_"+qli.getSeqNum()+" getOvrridDropdownOptions return "+getOptionsString(collection));

        return collection;
    }

    public String getOptionsString(Collection collection){
    	if(collection == null){
    		return "null";
    	}
    	Iterator iter = collection.iterator();
    	StringBuffer optionsString = new StringBuffer("{");
    	while(iter.hasNext()){
    		SelectOptionImpl optionImpl = (SelectOptionImpl) iter.next();
    		optionsString.append("[")
    		.append(optionImpl.getLabel()).append(",").append(optionImpl.getValue()).append(",").append(optionImpl.isSelected())
    		.append("]");
    	}
    	optionsString.append("}");
    	return optionsString.toString();
    }

    public boolean isDisabled4Term(QuoteLineItem qli) {
        return isBidIteratnQt() && (qli.isSaasPart() || qli.isMonthlySoftwarePart());
    }

    public boolean isDisabled4BillingFrqncy(QuoteLineItem qli) {
        return isBidIteratnQt() && (qli.isSaasPart() || qli.isMonthlySoftwarePart());
    }

    public String getFormName(){
    	if (isPA() || isPAE()){
    		return "draftSQPartPricePAPAE";
    	}
    	if (isFCT()){
    		return "draftSQPartPriceFCT";
    	}
    	if(isSSP()){
    		return "draftSQPartPriceSSP";
    	}
    	return "";
    }

    public String getEditIbmProdId() {
		return quote.getConfigurationEditParams() == null ? "" : quote.getConfigurationEditParams().getEditIbmProdId();
	}
	public String getEditConfigrtnId() {
		return quote.getConfigurationEditParams() == null ? "" : quote.getConfigurationEditParams().getEditConfigrtnId();
	}
	public String getEditOrgConfigrtnId() {
		return quote.getConfigurationEditParams() == null ? "" : quote.getConfigurationEditParams().getEditOrgConfigrtnId();
	}
	public String getEditConfigrtrConfigrtnId() {
		return quote.getConfigurationEditParams() == null ? "" : quote.getConfigurationEditParams().getEditConfigrtrConfigrtnId();
	}
	public String getEditTradeFlag() {
		return quote.getConfigurationEditParams() == null ? "" : quote.getConfigurationEditParams().getEditTradeFlag();
	}
	public String getEditConfigurationFlag() {
		return quote.getConfigurationEditParams() == null ? "" : quote.getConfigurationEditParams().getEditConfigurationFlag();
	}
	public String getEditOverrideFlag() {
		return quote.getConfigurationEditParams() == null ? "" : quote.getConfigurationEditParams().getOverrideFlag();
	}


	public String getDateString(QuoteLineItem qli){
		if(qli.getMaintEndDate() == null){
			return "";
		}else{
			return DateUtil.formatDate(qli.getMaintEndDate(), "yyyy-MM-dd");
		}
	}

    /**
     * @param configrtn
     * @return
     * If the draft quote contains an add-on/trade-up or co-termed configuration, the drop down will contain only one entry:  the charge agreement number of the add-on/trade-up/co-termed configuration.  No changes are allowed.
	 *	Otherwise, the drop down will contain the following values.  The default value will be the previously selected charge agreement number, if any, or "New service agreement" if that value was selected previously.
	 *  "New service agreement"
	 *  The charge agreement number of all existing charge agreements associated with the customer (the customer is the sold-to customer on the charge agreement).
	 *
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Collection getChargeAgreementOptions() throws Exception {
        Collection collection = new ArrayList();
        try{
	        String quoteHearderCA = StringUtils.isBlank(quote.getQuoteHeader().getRefDocNum()) ? "" : quote.getQuoteHeader().getRefDocNum();
	        boolean quoteContainsAddTradOrCoTerm = false;
	        for (Iterator iterator = quote.getPartsPricingConfigrtnsList().iterator(); iterator.hasNext();) {
				PartsPricingConfiguration configrtn = (PartsPricingConfiguration) iterator.next();
				//If the draft quote contains an add-on/trade-up or co-termed configuration, the drop down will contain only one entry:  the charge agreement number of the add-on/trade-up/co-termed configuration.  No changes are allowed.
				if(PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configrtn.getConfigrtnActionCode())
					|| PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(configrtn.getConfigrtnActionCode())
					|| PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(configrtn.getConfigrtnActionCode())){
					quoteContainsAddTradOrCoTerm = true;
				    break;
				}
			}
            // if the monthlySwQuoteDomain contains 'ADD_TRD' configuration, set quoteContainsAddTradOrCoTerm to be true
            MonthlySwQuoteDomain monthlySwQuoteDomain = quote.getMonthlySwQuoteDomain();
            if (monthlySwQuoteDomain != null) {
                List<MonthlySoftwareConfiguration> monthlySwConfgrtns = monthlySwQuoteDomain.getMonthlySwConfgrtns();
                for (Iterator iterator = monthlySwConfgrtns.iterator(); iterator.hasNext();) {
                    MonthlySoftwareConfiguration configuration = (MonthlySoftwareConfiguration) iterator.next();
                    if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configuration.getConfigrtnActionCode())) {
                        quoteContainsAddTradOrCoTerm = true;
                        break;
                    }
                }
            }
	        if(quoteContainsAddTradOrCoTerm){
	        	collection.add(new SelectOptionImpl(quoteHearderCA, quoteHearderCA, true));
	        }else{
	        	if(quote.getSoftwareLineItems() == null || quote.getSoftwareLineItems().size() == 0){
		        	PartsPricingConfiguration configrtn = (PartsPricingConfiguration)quote.getPartsPricingConfigrtnsList().get(0);
		        	for (int i = 0; i < configrtn.getChargeAgreementList().size(); i++) {
						String customerCA = configrtn.getChargeAgreementList().get(i);
						boolean selected = quoteHearderCA.equals(customerCA);
						collection.add(new SelectOptionImpl(customerCA, customerCA, selected));
					}
	        	}
	        	boolean selectedNewCA = "".equals(quoteHearderCA);
	        	ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
	        	collection.add(new SelectOptionImpl(context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,
	                    PartPriceViewKeys.NEW_BILLING_ARRANGEMENT), "", selectedNewCA));
	        }
	        logContext.debug(this, " getChargeAgreementOptions return "+getOptionsString(collection));
        }catch(Exception e){
        	logContext.error(this, " getChargeAgreementOptions return error "+e.getMessage());
        }
        return collection;
    }

    /**
     * @param chargeAgreementNum, configrtn
     * @return
     * The drop down will list:
     * all of the existing, active services on the service agreement
     * "Do not co-term"
     * The default value will be whatever was previously selected for this configuration.* The entries for existing active services are constructed as follows:* The offering's PID description
     * The latest end date of any active subscription line item under that PID.  In general, the end date will be the same on all active subscriptions in a PID
     * @throws Exception
     */
    public Map getCoTermOptions() throws Exception {

    	//<key,value>--key is service agreement number, value is configurations map
    	Map caMaps = new HashMap();
        try{
        	List configList = quote.getPartsPricingConfigrtnsList();
        	if(configList == null || configList.size() == 0){
        		return caMaps;
        	}
        	PartsPricingConfiguration config = (PartsPricingConfiguration)configList.get(0);
        	//get all the service agreement numbers
        	List caList = new ArrayList();
        	caList.addAll(config.getChargeAgreementList());
        	//add the "New service agreement" in the drop down as blank value
        	caList.add("");
        	for (Iterator iterator = caList.iterator(); iterator.hasNext();) {
        		//<key,value>--key is configuration id, value is co-termed collecntion
        		Map configrtnMaps = new HashMap();
        		Collection collection = new ArrayList();
				String chargeAgreementNum = (String) iterator.next();
				for (Iterator iterator2 = configList.iterator(); iterator2.hasNext();) {
					PartsPricingConfiguration configrtn = (PartsPricingConfiguration) iterator2.next();
        	if(!partTable.showCoTerm(configrtn)){
        		continue;
        	}
	        String cotermConfigrtnId = configrtn.getCotermConfigrtnId() == null ? "" : configrtn.getCotermConfigrtnId();
	        List ctCongifList = configrtn.getCotermMap().get(chargeAgreementNum);
	        boolean selectedDoNotCoTerm = true;
	        if(ctCongifList != null){
		        for (Iterator iterator3 = ctCongifList.iterator(); iterator3.hasNext();) {
		        	CoTermConfiguration cotermConfiguration = (CoTermConfiguration) iterator3.next();
		        	if(cotermConfiguration.getCotermConfigrtnId() == null || "".equals(cotermConfiguration.getCotermConfigrtnId())
		        		|| cotermConfiguration.getEndDate() == null){
		        		continue;
		        	}
		        	String lable = cotermConfiguration.getIbmProdIdDscr()
								+ " - "
								+ DateUtil.formatDate(cotermConfiguration.getEndDate(), "dd-MMM-yyyy", locale);
		        	if(cotermConfigrtnId.equals(cotermConfiguration.getCotermConfigrtnId())){
		        		collection.add(new SelectOptionImpl(lable, cotermConfiguration.getCotermConfigrtnId(), true));
		        		selectedDoNotCoTerm = false;
		        	}else{
		        		collection.add(new SelectOptionImpl(lable, cotermConfiguration.getCotermConfigrtnId(), false));
		        	}
				}
	        }

	        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
	        collection.add(new SelectOptionImpl(context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,
	                PartPriceViewKeys.DO_NOT_CO_TERM), "", selectedDoNotCoTerm));
	        logContext.debug(this, " getCoTermOptions return "+getOptionsString(collection));
	        configrtnMaps.put(configrtn.getConfigrtnId(), collection);
				}
				caMaps.put(chargeAgreementNum, configrtnMaps);
	        }
        }
        catch(Exception e){
        	logContext.error(this, " getCoTermOptions return error "+e.getMessage());
        }
        return caMaps;
    }


    /**
     * @return
     * If a quote contains SaaS on an existing charge agreement:
     * Only allow configuration updates on the Software as a Service page if the configuration is on the same charge agreement
     * Only allow new configurations from the browse services page if they are put on the same charge agreement.  Co-terming is optional.
     * Do not allow browsing or searching for software.
     */
	public boolean canAddSoftwareLineItems() {
		// saas
		String actionCode = "";
		List configList = quote.getPartsPricingConfigrtnsList();
		if (configList != null && configList.size() != 0) {
			for (Iterator iterator = configList.iterator(); iterator.hasNext();) {
				PartsPricingConfiguration config = (PartsPricingConfiguration) iterator.next();
				actionCode = config.getConfigrtnActionCode();
				// if action code in below scenarios, return !isSSP()
				if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(actionCode)
						|| PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(actionCode)
						|| PartPriceConstants.ConfigrtnActionCode.NEW_CA_NCT.equals(actionCode)
						|| PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(actionCode))
					return false;
			}
		}
		// monthly and other scenarios
		return (!isSSP());
	}

    public boolean canAddSoftwareLineItemsForPGS(){
    	return (StringUtils.isBlank(quote.getQuoteHeader().getRefDocNum()))
    		&&!isHideSoftwareLinkForPGS();
    }

    public PartsPricingConfiguration getConfigurationById(String configrtnId){
    	return QuoteCommonUtil.getPartsPricingConfigurationById(configrtnId, quote);
    }


    public boolean showRemoveConfgrtn(){
    	if(isBidIteratnQt()){
    		return false;
    	}
    	return true;
    }

    /**
     * @param confgrtn
     * @return
     *- hide the "Edit configuration" link (the one that goes to GST) if all the following are met.
      - offering exists in WEB_MDLR_IBM_PROD_ID (ie. defined to go to GST)
      - new column in WEB_MDLR_IBM_PROD_ID for the offering allows GST configuration to be overridden by SQO Basic.
      - configuration has been overridden by SQO Basic (new column in WEB_QUOTE_CONFIGRTN = 1)
     */
    public boolean hideEditConfgrtn(PartsPricingConfiguration confgrtn){
    	return (confgrtn.isAllowOvrrd() && confgrtn.isConfigrtnOvrrdn());
    }

    /**
     * @param confgrtn
     * @return
     */
    public boolean showOvrrdConfgrtn(PartsPricingConfiguration confgrtn){
    	if(confgrtn.isConfigrtnOvrrdn()){
    		return true;
    	}
		//- add a new "Override GST configuration" link at the configuration level if all the following are met.
		//- offering exists in WEB_MDLR_IBM_PROD_ID (ie. defined to go to GST)
		//- new column in WEB_MDLR_IBM_PROD_ID for the offering allows GST configuration to be overridden by SQO Basic.
    	if(confgrtn.isAllowOvrrd()){
    		return true;
    	}
    	String configrtnErrCode = confgrtn.getConfigrtnErrCode();
    	if(StringUtils.isNotBlank(configrtnErrCode)){
			//If a configuration was created by CPQ and the configuration retrieval web service return code is 1003 or 1004 or 1005,
			//enable the "override configuration" link in the draft parts and pricing tab (similar to how 1003 controls the link)
			//Notes://D01DBM20/852578710077C3BA/9B03730C11B24C94852565E20060BED0/50DCF753F3069C68852579350055112D
    		if(configrtnErrCode.equals(ConfiguratorConstants.RETRIEVAL_SERVICE_RET_CODE.CODE_1003) || configrtnErrCode.equals(ConfiguratorConstants.RETRIEVAL_SERVICE_RET_CODE.CODE_1004)
    				|| configrtnErrCode.equals(ConfiguratorConstants.RETRIEVAL_SERVICE_RET_CODE.CODE_1005)){
    			return true;
    		}

		}
    	return false;
    }

    public boolean isDisabledSaasLink(){
    	if (isFCTToPA()){
    		return true;
    	}

    	return false;
    }

    public boolean isHideSaasLinkForPGS(){
    	boolean isHide = false;
    	if (isPA() || isPAE() || isFCT()){
    		//
    	} else {
    		return isHide;
    	}

    	List lineItems = getMasterSoftwareLineItems();
    	if (lineItems != null && lineItems.size() >0){
    		isHide = true;
    	}
    	return isHide;
    }

    public boolean isHideSoftwareLinkForPGS(){
    	boolean isHide = false;
    	List saasLineItems = getSaaSLineItems();
    	if (saasLineItems != null && saasLineItems.size() > 0){
    		isHide = true;
    	}
    	return isHide;
    }

	/**
	 * Check if show 'Edit Provisioning Form' link for draft quote
	 * always show the link for draft quote
	 * @return
	 */
	public boolean isShowEditProvisioningForm(PartsPricingConfiguration ppc){
		return true;
	}

	/**
	 * get renewal model options
	 * @param displayLevel  configuration or lineItem  level display
	 * @param subSaasLineItems  All lineItems in configuration.
	 * @param qli
	 * @return
	 */
	public Collection generateAutoRenewOptions(String displayLevel,
			List subSaasLineItems, QuoteLineItem qli) {
		Collection collection = new ArrayList();
		ApplicationContext context = ApplicationContextFactory.singleton()
				.getApplicationContext();
		String selectOne = PartPriceViewKeys.APPLIANCE_SELECTE_ONE;
		String useConfigRewlMod = PartPriceViewKeys.USE_CONFIGURATION_RENEWAL_MODEL;

		String renewModelVal = "";

		String[] labels = { selectOne,
				PartPriceViewKeys.RENEW_SERVICE_TERM_MONTHS,
				PartPriceViewKeys.CONTINUOUSLY_BILL,
				PartPriceViewKeys.RENEW_SERVICE_TERM_ORIGINAL,
				PartPriceViewKeys.TERMINATE_END_TERM };

		String[] values = { "", PartPriceConstants.RenewalModelCode.R,
				PartPriceConstants.RenewalModelCode.C,
				PartPriceConstants.RenewalModelCode.O,
				PartPriceConstants.RenewalModelCode.T };
		// display level
		if (DraftQuoteParamKeys.DISPLAY_LEVEL_CONFIGURTN.equals(displayLevel)) {
			if (subSaasLineItems != null) {
				for (Object obj : subSaasLineItems) {
					QuoteLineItem lineItem = (QuoteLineItem) obj;
					// find configuration level from all parts in configuration
					if (PartPriceConstants.RenewalModelCode.CONFIG_RENWL_MDL_CODE_LEVEL
							.equalsIgnoreCase(lineItem.getRenwlMdlCodeLevel())) {
						renewModelVal = lineItem.getRenwlMdlCode();
						break;
					}
				}
			}

		} else if (DraftQuoteParamKeys.DISPLAY_LEVEL_SUBSCRPTN
				.equals(displayLevel)) {
			renewModelVal = StringUtils.trimToEmpty(qli.getRenwlMdlCode());
			labels[0] = useConfigRewlMod;
			// renewal model is configuration level , show
			// "use configuration renewal model"
			if (PartPriceConstants.RenewalModelCode.CONFIG_RENWL_MDL_CODE_LEVEL
					.equalsIgnoreCase(qli.getRenwlMdlCodeLevel())) {
				renewModelVal = "";
			}
		}

		int length = labels.length;

		for (int i = 0; i < length; i++) {
			String label = context.getI18nValueAsString(
					I18NBundleNames.PART_PRICE_MESSAGES, locale, labels[i]);
			String value = values[i];

			collection.add(new SelectOptionImpl(label, value, value
					.equalsIgnoreCase(renewModelVal)));
		}
		return collection;
	}


	/**
	 * join all scrptionId in configruation
	 * @param subSaasLineItems
	 * @return
	 */
	public String getJoinRenwlModelSubscrptonIds(List subSaasLineItems){
		String joinRenwlSubscptnIds = "";
		if (partTable.showRenwlModeForConfigrtn(subSaasLineItems)){
			for (Object obj : subSaasLineItems){
				QuoteLineItem qli = (QuoteLineItem)obj;
				if ((qli.isSaasSubscrptnPart() ||(qli.isMonthlySoftwarePart()&& ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart()))
						&& !qli.isReplacedPart()&& !qli.isFixedRenwlMdl()){
					String prefix = qli.getPartNum() + "_" + new Integer(qli.getSeqNum()).toString();
					String renwlModelSubscrptonId = getIdByName(prefix + DraftQuoteParamKeys.PARAM_RENEWAL_MODEL_SUBSCRPTN);
					joinRenwlSubscptnIds +=  renwlModelSubscrptonId + "|";
				}
			}
			joinRenwlSubscptnIds =joinRenwlSubscptnIds.substring(0, joinRenwlSubscptnIds.lastIndexOf("|"));
		}

		return joinRenwlSubscptnIds;

	}

	public boolean showBillingFrequencyGlobalChange() throws QuoteException {
		QuoteHeader header = quote.getQuoteHeader();
		String lob = header.getLob().getCode();
		boolean flag = false;
		if (header.isSalesQuote()) {
			if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)
					|| QuoteConstants.LOB_FCT.equalsIgnoreCase(lob) || QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)) {
				List<QuoteLineItem> lineItems = QuoteCommonUtil.findChangeAbleBillingFrequencyIineItems(quote);
				if (lineItems != null && lineItems.size() != 0) {
					List<QuoteLineItemBillingOption> lineItemOptions = lineItems.get(0).getBillingOptions();
					if (lineItemOptions.size() > 1) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	public List getBillingFrequencyOptions() {
		List collection = new ArrayList();
		List<QuoteLineItem> lineItems = QuoteCommonUtil.findChangeAbleBillingFrequencyIineItems(quote);
		if (lineItems != null && lineItems.size() != 0) {
			List<QuoteLineItemBillingOption> lineItemOptions = lineItems.get(0).getBillingOptions();
	        Collections.sort(lineItemOptions, new Comparator() {
	            public int compare(Object o1, Object o2) {
	            	QuoteLineItemBillingOption op1 = (QuoteLineItemBillingOption) o1;
	            	QuoteLineItemBillingOption op2 = (QuoteLineItemBillingOption) o2;
	            	return op1.getBillingOption().getMonths() - op2.getBillingOption().getMonths();
	            }
	        });

	        for(QuoteLineItemBillingOption bo : lineItemOptions) {
	    		if(StringUtils.equals(ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY, bo.getCode())){
	    			collection.add(new SelectOptionImpl(bo.getCodeDesc(), bo.getCode(), true));
	    		} else {
	    			collection.add(new SelectOptionImpl(bo.getCodeDesc(), bo.getCode(), false));
	    		}
	        }
		}
		return collection;
	}

	/**
	 * to confirm show the extension service type according to the Action Code
	 * @param ppc
	 * @return
	 */
	 public boolean showServiceModTypeDropdownList(PartsPricingConfiguration ppc){
		return ppc.getConfigrtnActionCode().equals(PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT)
			|| ppc.getConfigrtnActionCode().equals(PartPriceConstants.ConfigrtnActionCode.NEW_CA_NCT)
			|| ppc.getConfigrtnActionCode().equals(PartPriceConstants.ConfigrtnActionCode.NEW_NCT);
	}

	 /**
	  * to confirm show the extension service type
	  * @param ppc
	  * @return
	  */
	 public boolean showServiceModTypeOption(PartsPricingConfiguration ppc) {
		 if (isBidIteratnQt()) {
			 return false;
		 } else {
			 return true;
		 }
	 }

	 public String getQuoteCurrencyCode(){
		 return getQuote().getQuoteHeader().getCurrencyCode();
	 }


	/**
	 * 13.4 release 10.6B show Extend entire configurations End date
	 * @param config
	 * @param isTermExtension
	 * @return
	 */
	public boolean isShowExEntireConfiguratnEndDateLink(PartsPricingConfiguration config,
			boolean isTermExtension) {
		// only Addon/tradeup or FCT to PA
		return config.isAddOnTradeUp() && isTermExtension;

	}

	public boolean isDisableExEntireConfiguratnEndDateLink(PartsPricingConfiguration config) {
		// contain All CA parts
		return config.isConfigEntireExtended();

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
	   ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
	  
	   String type = GrowthDelegationUtil.getQuoteGrwthDlgtnType(quote);
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
   
   public boolean isShowAdditionalYearForPartFromRenewalQuote(QuoteLineItem qli) {
	   boolean addFlag = false;
	   return qli.getPartDispAttr().isMaint() && !qli.isApplncPart() && partTable.isAddedFromRenewalQuote(qli) && !addFlag;
   }
   
   //monthly
	public List getMonthlyLineItems(){
		return quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
	}
}