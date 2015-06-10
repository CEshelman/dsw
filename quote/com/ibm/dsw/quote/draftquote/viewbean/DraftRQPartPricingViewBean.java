package com.ibm.dsw.quote.draftquote.viewbean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.UIFormatter;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.NoteRow;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PVUDetail;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PVUSection;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartInfo;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPricingTabURLs;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTable;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTableTotal;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartsAndPrice;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PricingNotes;
import com.ibm.dsw.quote.draftquote.viewbean.helper.ReasonRow;
import com.ibm.dsw.quote.draftquote.viewbean.helper.SoftwareMaintenance;
import com.ibm.dsw.quote.draftquote.viewbean.helper.TopSection;
import com.ibm.dsw.quote.pvu.config.VUActionKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>DraftRQPartPricingViewBean</code>
 *
 *
 * @author: xiuliw@cn.ibm.com
 *
 * Creation date: 2007-4-25
 */
public class DraftRQPartPricingViewBean extends DraftRQBaseViewBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -763068101778970485L;

	transient LogContext logContext = LogContextFactory.singleton().getLogContext();

    private static final int DAY_OF_MONTH = 31;

    private int maxExpirationDays= 0;

    private int currYear = 0;

    private int formerYear = 0;

    public PartsAndPrice partAndPrice;

    public TopSection topSection;

    public PricingNotes pricingNotes;

    public PVUSection pvuSection;

    public PartInfo partInfo;

    public PartTable partTable;

    public PVUDetail pvuDetail;

    public SoftwareMaintenance softwareMaintenance;

    public NoteRow noteRow;

    public ReasonRow reasonRow;

    public PartTableTotal partTableTotal;

    public PartPricingTabURLs tabURLs;

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
    private int yearRange = 20;

    private Boolean engineUnAvailable;

    private UIFormatter formatter;

    /**
     * Constructor
     */
    public DraftRQPartPricingViewBean() {
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

        engineUnAvailable = (Boolean)params.getParameter(DraftQuoteParamKeys.Price_Engine_UnAvailable);

        maxExpirationDays = getRenewalQuoteHeader().getQuoteExpDays();

        String lob = quote.getQuoteHeader().getLob().getCode();
        yearRange = PartPriceConfigFactory.singleton().getAddtionalYear(lob);

        // Generate calendar information
        Calendar curr = Calendar.getInstance();
        currYear = curr.get(Calendar.YEAR);
        formerYear = currYear - 1;

        if (renewalQuote.getQuoteHeader().isRenewalQuote()) {
            rqEditable = renewalQuote.getQuoteAccess().isCanEditRQ();
        }
        formatter = new UIFormatter(renewalQuote);
        partAndPrice = new PartsAndPrice(renewalQuote);

        topSection = new TopSection(renewalQuote);

        pricingNotes = new PricingNotes(renewalQuote);

        pvuSection = new PVUSection(renewalQuote);

        partInfo = new PartInfo(renewalQuote);

        partTable = new PartTable(renewalQuote,getLocale());

        pvuDetail = new PVUDetail(renewalQuote);

        softwareMaintenance = new SoftwareMaintenance(renewalQuote);

        reasonRow = new ReasonRow(renewalQuote);

        noteRow = new NoteRow(renewalQuote, locale);

        partTableTotal = new PartTableTotal(renewalQuote);

        tabURLs = new PartPricingTabURLs(renewalQuote);

    }

    protected String genBtnParams(String jadeAction, String redirectURL, String params) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        StringBuffer sb = new StringBuffer();

        sb.append(actionKey).append("=").append(jadeAction);
        if (StringUtils.isNotBlank(redirectURL))
            sb.append(",redirectURL=").append(redirectURL);
        if (StringUtils.isNotBlank(params))
            sb.append(",").append(params);
        return sb.toString();
    }

    /**
     * Added by lee,March 29,2007.
     *
     * @return line items of quote
     */
    public List getLineItems() {
        return renewalQuote.getLineItemList();
    }

    /**
     *
     * @return
     */
    public String getTotalPoints() {
        return formatter.formatPoint(getRenewalQuoteHeader().getTotalPoints());

    }

    /**
     *
     * @return
     */
    public String getTotalPrice() {
        if (isRqEditable()) {
            if (isPriceUnAvaialbe()) {
            	return "";
            }
        }
        return formatter.formatEndCustomerPrice(getRenewalQuoteHeader().getQuotePriceTot());

    }

    /**
     * Added by lee,March 30,2007
     *
     * @return row span number by judging what will be shown
     */
    public int getRowSpan(QuoteLineItem lineItem) {
        int rowNumber = 2;

        if (pvuDetail.showPVUDetailsRow(lineItem))
            rowNumber++;
        if (softwareMaintenance.showSoftwareMaintenanceCoverage(lineItem))
            rowNumber++;
        if (noteRow.showNoteRow(lineItem))
            rowNumber++;
        if (reasonRow.showReasonRow(lineItem) && isRqEditable())
        	rowNumber++;
        if (partTable.isPartControlled(lineItem))
            rowNumber++;

        //Renewal quote show RSVP and PYP
        if (partTable.showRenewalPriceRSVP(lineItem, quoteUserSession) || partTable.showRenewalPricePYP(lineItem, quoteUserSession))
        	rowNumber++;
        //Appliance information
        if(partTable.showApplianceMtm(lineItem,!getQuote().getQuoteHeader().isSubmittedQuote(),getQuote().getQuoteHeader().isSalesQuote())){
        	rowNumber++;
        }

        return rowNumber;
    }

    public String getAppPvuConfigUrl() {
        return HtmlUtil.getQuoteAppUrl() + HtmlUtil.getURLForAction(VUActionKeys.APPLY_PVU_CONFIG);
    }

    /**
     * @return Returns the rqEditable.
     */
    public boolean isRqEditable() {
        return rqEditable;
    }

    /**
     * @return Returns the yearRange.
     */
    public int getYearRange() {
        return yearRange;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftSQBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        return DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB;
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftSQBaseViewBean#getPostTabAction()
     */
    public String getPostTabAction() {
        return DraftQuoteActionKeys.POST_PART_PRICE_TAB;
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

    /**
     * get default values for start date year/month/day and end date year/month/day
     * @param lineItem
     */
    public void getDatesDefaultValue(QuoteLineItem lineItem) {
        Date startDate = lineItem.getMaintStartDate();
        Date endDate = lineItem.getMaintEndDate();

        startDateDay = startDate.getDate();
        startDateMonth = startDate.getMonth();
        startDateYear = startDate.getYear() + 1900;

        endDateDay = endDate.getDate();
        endDateMonth = endDate.getMonth();
        endDateYear = endDate.getYear() + 1900;
    }

    private Date getAnniversary() {
        try {
            Contract contract = (Contract)renewalQuote.getCustomer().getContractList().get(0);

            if (contract.getAnniversaryDate() == null) {
                return null;
            }
            return new Date(contract.getAnniversaryDate().getTime());
        } catch (Throwable e) {
           LogContextFactory.singleton().getLogContext().log(this, "error in getAnniversary: " + e);
        }
        return null;
    }

    private String formatCode2(String s) {
        if (s == null)
            return null;
        if (s.length() == 1)
            return "0"+s;
        else
            return s;
    }

    /**
     * generate start day options
     * @return
     */
    public Collection generateStartDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (startDateDay == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), false));

        }
        return collection;
    }

    /**
     * generate end day options
     * @return
     */
    public Collection generateEndDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (endDateDay == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), false));

        }
        return collection;
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
    public Collection generateOvrEndDayOptions() {
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
     * generate start month options
     * @return
     */
    public Collection generateStartAnniversaryOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);

        collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (startDateMonth + 1 == i)
	            collection.add(new SelectOptionImpl(labelString, formatCode2(String.valueOf(i)), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, formatCode2(String.valueOf(i)), false));
        }

    	return collection;
    }

    /**
     * generate end month options
     * @return
     */
    public Collection generateEndAnniversaryOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);

        collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (endDateMonth + 1 == i)
	            collection.add(new SelectOptionImpl(labelString, formatCode2(String.valueOf(i)), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, formatCode2(String.valueOf(i)), false));
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
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};
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
    public Collection generateOvrEndAnniversaryOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};
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
     * generate start year options
     * @return
     */
    public Collection generateStartYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        collection.add(new SelectOptionImpl(sYear, "", false));

        for (int i=0;i<yearRange;i++) {
            if (formerYear + i == startDateYear) {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), true));
            }
            else {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), false));
            }
        }

        return collection;
    }

    /**
     * generate end year options
     * @return
     */
    public Collection generateEndYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        collection.add(new SelectOptionImpl(sYear, "", false));

        for (int i=0;i<yearRange;i++) {
            if (formerYear + i == endDateYear) {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), true));
            }
            else {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), false));
            }
        }

        return collection;
    }

    /**
     * generate override start year options
     * @return
     */
    public Collection generateOvrStartYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        collection.add(new SelectOptionImpl(sYear, "", false));

        for (int i=0;i<yearRange;i++) {
            if (formerYear + i == overrideStartDateYear) {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), true));
            }
            else {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), false));
            }
        }

        return collection;
    }

    /**
     * generate override end year options
     * @return
     */
    public Collection generateOvrEndYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        collection.add(new SelectOptionImpl(sYear, "", false));

        for (int i=0;i<yearRange;i++) {
            if (formerYear + i == overrideEndDateYear) {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), true));
            }
            else {
                collection.add(new SelectOptionImpl(String.valueOf(formerYear + i), String.valueOf(formerYear + i), false));
            }
        }

        return collection;
    }

    /**
     * @return Returns the maxExpirationDays.
     */
    public int getMaxExpirationDays() {
        return maxExpirationDays;
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
    /**
     * @return Returns the currYear.
     */
    public int getCurrYear() {
        return currYear;
    }

    /**
     * @return Returns the endDateYear.
     */
    public int getEndDateYear() {
        return endDateYear;
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
     * @return Returns the startDateYear.
     */
    public int getStartDateYear() {
        return startDateYear;
    }
	/**
	 * @return Returns the formerYear.
	 */
	public int getFormerYear() {
		return formerYear;
	}
    /**
     * @return Returns the engineUnAvailable.
     */
    public boolean isPriceUnAvaialbe() {
    	return engineUnAvailable.booleanValue();
    }

    /**
     * @return Returns the tabURLs.
     */
    public PartPricingTabURLs getTabURLs() {
        return tabURLs;
    }

    public boolean isEnforceEndDate(QuoteLineItem qli){
        return PartPriceConfigFactory.singleton().isEnforceEndDate(qli.getRevnStrmCode());
    }


    public boolean isDisabled4PartQty(QuoteLineItem qli) {
    	return  (isBidIteratnQt() && (qli.isSaasPart() || qli.isMonthlySoftwarePart()))
    				//add appliance part qty logic
    				|| (qli.isApplncQtyRestrctn());
    }

    public String getQuoteCurrencyCode() {
        return getQuote().getQuoteHeader().getCurrencyCode();
    }
    
    //Appliance#99
    /**
     * @return return valid lineItem CRAD or header CRAD if lineItem CRAD= null 
     */
    public java.util.Date getLineItemCRAD(int index){
    	java.util.Date custReqArrvDate = header.getCustReqstArrivlDate();
    	java.util.Date lineItemCRAD = new java.util.Date();
    	QuoteLineItem qli = (QuoteLineItem) renewalQuote.getLineItemList().get(index);
    	if (qli!=null){
    		lineItemCRAD = qli.getLineItemCRAD();
    	}
    	if (lineItemCRAD == null){
    		lineItemCRAD = custReqArrvDate;
    	} 
    	return lineItemCRAD;
    }
    
    public String getOrgLineItemCRAD(java.util.Date lineItemCRAD){
    	if (lineItemCRAD !=null){
    		logContext.debug(this, "lineItemCRAD for show ="+DateUtil.formatDate(lineItemCRAD, DateUtil.PATTERN5));
    		return DateUtil.formatDate(lineItemCRAD, DateUtil.PATTERN);
    	} else {
    		logContext.debug(this, "lineItemCRAD for show = blank");
    		return "";
    	}
    	
    }
    
    public boolean isDisplayCRAD(int index){
    	QuoteLineItem qli = (QuoteLineItem) renewalQuote.getLineItemList().get(index);
    	boolean isApplncSendMFGFlg = qli.getApplncSendMFGFLG();
    	boolean isMainPartFlg = qli.isApplncMain();
    	boolean isTransceiverWithAppIdFlg = qli.isApplncTransceiver()&&(qli.getConfigrtnId()==null||qli.getConfigrtnId().equals("NOT_ON_QUOTE"));
    	return isApplncSendMFGFlg &&(isMainPartFlg||isTransceiverWithAppIdFlg);
    }
    
    public String getHeaderCRAD(){
    	java.util.Date headerCRAD = quote.getQuoteHeader().getCustReqstArrivlDate();
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
    
    public List getMasterSoftwareLineItems() {
    	return quote.getMasterSoftwareLineItems();
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
    
}