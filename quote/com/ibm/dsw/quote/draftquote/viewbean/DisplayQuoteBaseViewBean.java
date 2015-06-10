package com.ibm.dsw.quote.draftquote.viewbean;

import is.domainx.User;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ApplianceAddress;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerGroup;
import com.ibm.dsw.quote.common.domain.JustSection;
import com.ibm.dsw.quote.common.domain.JustSection_Impl;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteClassificationCodeFactory;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidReason;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.UIFormatter;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SpecialBidCommon;
import com.ibm.ead4j.common.util.DateHelper;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>DisplayQuoteBaseViewBean<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-4-30
 */

/**
 * @author Vivian
 *
 */
public abstract class DisplayQuoteBaseViewBean extends BaseViewBean {

    private static final long serialVersionUID = -2837899552625750446L;

	protected static final int DAY_OF_MONTH = 31;
	
	protected static final String[] ADDITIONAL_YEAR_LIST= {"First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth", "Eleventh", "Twelfth", "Thirteenth", "Fourteenth", "Fifteenth", "Sixteenth", "Seventeenth", "Eighteenth", "Nineteenth", "Twentieth", "Twenty-First"};

    protected Quote quote = null;
    protected User user = null;
    protected transient QuoteHeader header = null;
    protected transient Customer cust = null;
    protected Contract contract = null;
    protected QuoteUserAccess qtUserAccess = null;

    protected String custName = "";
    protected String custCity = "";
    protected String custState = "";
    protected String custStateDesc = "";
    protected String custCntry = "";
    protected String custCntryCode = "";
    protected String custCurrencyCode = "";
    protected boolean isCustOnlySSP=false;

    protected String originalWebReference = "";
    protected String siteNumber = "";
    protected String ibmCustNum = "";
    protected String agreementNum = "";
    protected String agreementOption="";
    protected String relationshipSVPLevel = "";
    protected String quoteValue = "";
    protected String currency = "";
    protected boolean isRSVPSRPOnly = false;
    protected boolean isDisplayRSVPSRPOnly = false;
    protected String acqstnCode = "";
    protected String acqstnDesc = "";

    protected Date quoteExpDate = null;
    protected int currYear = 0;
    protected int quoteExpDay = 0;
    protected int quoteExpMonth = 0;
    protected int quoteExpYear = 0;

    protected String country = "";
    protected String lob = "";
    protected String quoteType = "";
    protected String formName = "";
    protected int userAccess = 0;
    protected String webQuoteNum = "";

    protected boolean isEmpty = true;
    protected boolean isDisplayQuoteValue = false;

    protected UIFormatter uiFormatter = null;

    protected String custDesignation = "";

    protected SpecialBidReason spbReason = null;
    protected boolean isEMEADiscountRequireSpBid = false;

    protected Date quoteStartDate = null;
    protected int quoteStartDay = 0;
    protected int quoteStartMonth = 0;
    protected int quoteStartYear = 0;

    protected String lockedQuoteMsg = "";
    protected boolean isLockedFlag = false;
    protected int validityDays = 0;

    protected transient List customerGroupList = null;
    protected boolean isDisplayAgreementInfo = false;
    protected String contractVariant = "";
    protected String anniversary = "";
    protected String cntrctVarMonthLev = "";

    protected Date estmtdOrdDate = null;
    protected int estmtdOrdDay = 0;
    protected int estmtdOrdMonth = 0;
    protected int estmtdOrdYear = 0;
    
    protected int saasTermCondCatFlag;
    
    public int getEstmtdOrdDay() {
		return estmtdOrdDay;
	}

	public int getEstmtdOrdMonth() {
		return estmtdOrdMonth;
	}

	public int getEstmtdOrdYear() {
		return estmtdOrdYear;
	}

	protected Date custReqstdArrivlDate = null;
    protected int custReqstdArrivlDay = 0;
    protected int custReqstdArrivlMonth = 0;
    protected int custReqstdArrivlYear = 0;
    protected boolean isDisplayCRAD = false;
    protected boolean isExtensionDate = false;
    private boolean isSaasFCTToPAQuote = false;
    private String orignlSalesOrdRefNum = "";
    
    protected String sspType ="";
    
    protected String bpSubmitterEmail;
    protected String bpSubmitterName;
    protected Date bpSubmitDate;
    
    protected boolean isSubmittedForEval;
    protected boolean isAcceptedByEval;
    protected boolean isReturnForChgByEval;
    protected boolean isEditForRetChgByEval;
    
    public String getSspType() {
		return sspType;
	}

	public void setSspType(String sspType) {
		this.sspType = sspType;
	}

	public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);

        spbReason = (SpecialBidReason)params.getParameter(SpecialBidParamKeys.PARAM_SPECIAL_BID_REASON);
        quote = (Quote) params.getParameter(ParamKeys.PARAM_QUOTE_OBJECT);
        user = (User) params.getParameter(ParamKeys.PARAM_USER_OBJECT);

        if (user == null || quote == null || quote.getQuoteHeader() == null)
            return;

        header = quote.getQuoteHeader();
        cust = quote.getCustomer();
        contract = this.getFirstContract(cust);
        qtUserAccess = quote.getQuoteUserAccess();
        uiFormatter = new UIFormatter(quote);
        orignlSalesOrdRefNum = header.getOrignlSalesOrdRefNum();
        
        if (StringUtils.isNotBlank(header.getSoldToCustNum())
                || StringUtils.isNotBlank(header.getPayerCustNum())
                || StringUtils.isNotBlank(header.getRselCustNum())
                || header.getWebCustId() > 0)
            isEmpty = false;

        isDisplayQuoteValue = header.getHasPartsFlag();

        currency = this.getCurrencyDesc(header.getPriceCountry(), header.getCurrencyCode());
        isRSVPSRPOnly = header.isRSVPSRPOnly();
        
        
        isDisplayRSVPSRPOnly = GrowthDelegationUtil.isDisplayRSVPSVPPricingFlag(quote);

        if (header.isLockedFlag()) {
            isLockedFlag = true;
            lockedQuoteMsg = handleLockedQuoteMessage(header.getLockedBy(), header.getWebQuoteNum(), DraftQuoteMessageKeys.MSG_LOCKED_QUOTE_HDR);
        }

        if (cust != null) {
            custName = cust.getCustName();
	        custCity = cust.getCity();
	        custState = cust.getSapRegionCode();
	        custCntryCode = cust.getCountryCode();
	        custCurrencyCode = cust.getCurrencyCode();
	        Country cntryObj = this.getCountry(custCntryCode);
	        if (cntryObj != null) {
	            custStateDesc = cntryObj.getStateDescription(custState);
	            custCntry = cntryObj.getDesc();
	        }
	        else {
		        custStateDesc = custState ;
		        custCntry = custCntryCode;
	        }
	        siteNumber = cust.getCustNum();
	        ibmCustNum = cust.getIbmCustNum();

	        custDesignation = cust.getCustDesignation();
	        isCustOnlySSP =cust.isCustOnlySSP();

	        this.customerGroupList = cust.getCustomerGroupList();

        }
        if(this.customerGroupList == null){
        	this.customerGroupList = new LinkedList();
        }
        
        // if quote is PA/PAE, channel, special bid and GOE review is true, then set GOE review message into Special Customer Groupings list
        if(QuoteCommonUtil.isNeedGOEReview(quote)){
        	ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
            String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.GOE_REVIEW);
            CustomerGroup group = new CustomerGroup();
            group.setCustomerGroupName(labelString);
            this.customerGroupList.add(group);
        }

        quoteValue = this.formatCurrency(header.getQuotePriceTot());
        quoteExpDate = header.getQuoteExpDate();
        country = header.getCountry() == null ? "" : header.getCountry().getCode3();
        lob = header.getLob() == null ? "" : header.getLob().getCode();
        quoteType = header.getQuoteTypeCode();
        webQuoteNum = header.getWebQuoteNum();
        originalWebReference = header.getPriorQuoteNum();
        acqstnCode = header.getAcqrtnCode();
        acqstnDesc = this.getAcqstnDescByCode(acqstnCode);

        if ((CustomerConstants.LOB_PA.equalsIgnoreCase(lob) ||  CustomerConstants.LOB_OEM.equalsIgnoreCase(lob))
                && contract != null && contract.getIsContractActiveFlag() == 1) {
            isDisplayAgreementInfo = true;
            agreementNum = contract.getSapContractNum();
            agreementOption = this.getContractOptionDesc(contract.getSapContractVariantCode());
            contractVariant = this.getContractOptionDesc(contract.getSapContractVariantCode());
            anniversary = com.ibm.dsw.quote.base.util.DateHelper.getDateByFormat(contract.getAnniversaryDate(), "MMM", locale);
            relationshipSVPLevel = this.getPriceLevelDesc(contract.getVolDiscLevelCode());
        }
        //  set agreementOption when create new PA customer
		if (header.isPAQuote() && (header.hasNewCustomer() || StringUtils.isBlank(agreementOption)) && cust != null)
			agreementOption = this.getContractOptionDesc(cust.getAgreementType());
        
        // For CSTA,CSRA TO agreementOption which agreementOption is null
        if(isCSA()&&(header.hasExistingCustomer() || header.hasNewCustomer()) && cust != null){
      	  agreementOption=header.isCSRAQuote() == true ? QuoteConstants.QUOTE_AGREEMENT_OPTION_RELATIONAL :QuoteConstants.QUOTE_AGREEMENT_OPTION_TRANSACTIONAL;
      	}
        	

        // For PA,PAE,FCT,OEM,FCT to PA quote which contains appliance part
        if ((header.isPAQuote() || header.isPAEQuote() || header.isFCTQuote() || header.isSSPQuote() ||
        	header.isOEMQuote() || header.isFCTToPAQuote()) && (header.isHasAppMainPart()||header.isHasAppUpgradePart())){
        	isDisplayCRAD = true;
        }

        // Generate calendar information
        Calendar curr = Calendar.getInstance();
        currYear = curr.get(Calendar.YEAR);

        if (this.quoteExpDate != null) {
            Calendar expireDate = Calendar.getInstance();
            expireDate.setTime(this.quoteExpDate);
            quoteExpYear = expireDate.get(Calendar.YEAR);

            if (quoteExpYear >= currYear) {
                quoteExpMonth = expireDate.get(Calendar.MONTH)+1;
                quoteExpDay = expireDate.get(Calendar.DAY_OF_MONTH);
            }
            else
                quoteExpYear = 0;
        }

        // Quote start date
        quoteStartDate = header.getQuoteStartDate();
        Calendar startDate = Calendar.getInstance();

        if (this.quoteStartDate != null) {
            // Get current date
            Date now = DateHelper.singleton().today();
            Date currDate = DateUtils.truncate(now, Calendar.DATE);

            // If quote start date before current date, set start date to current date
            if ( quoteStartDate.before(currDate) ) {

                startDate.setTime(Calendar.getInstance().getTime());
                quoteStartYear = startDate.get(Calendar.YEAR);
                quoteStartMonth = startDate.get(Calendar.MONTH) + 1;
                quoteStartDay = startDate.get(Calendar.DAY_OF_MONTH);

            } else {
                startDate.setTime(this.quoteStartDate);
                quoteStartYear = startDate.get(Calendar.YEAR);

                if (quoteStartYear >= currYear) {
                    quoteStartMonth = startDate.get(Calendar.MONTH)+1;
                    quoteStartDay = startDate.get(Calendar.DAY_OF_MONTH);
                }
                else
                    quoteStartYear = 0;
            }

        } else {

            startDate.setTime(Calendar.getInstance().getTime());

            quoteStartYear = startDate.get(Calendar.YEAR);
            quoteStartMonth = startDate.get(Calendar.MONTH) + 1;
            quoteStartDay = startDate.get(Calendar.DAY_OF_MONTH);
        }

        // Estimated order date
        estmtdOrdDate = header.getEstmtdOrdDate();
        Calendar currClndr = Calendar.getInstance();
        if (this.estmtdOrdDate != null) {
            // Get current date
            Date now = DateHelper.singleton().today();
            Date currDate = DateUtils.truncate(now, Calendar.DATE);

            // If estimated order date before current date, set estimated order date to current date
            if ( estmtdOrdDate.before(currDate) ) {

            	currClndr.setTime(Calendar.getInstance().getTime());
            	estmtdOrdYear = currClndr.get(Calendar.YEAR);
            	estmtdOrdMonth = currClndr.get(Calendar.MONTH) + 1;
            	estmtdOrdDay = currClndr.get(Calendar.DAY_OF_MONTH);

            } else {
            	currClndr.setTime(this.estmtdOrdDate);
            	estmtdOrdYear = currClndr.get(Calendar.YEAR);

                if (estmtdOrdYear >= currYear) {
                	estmtdOrdMonth = currClndr.get(Calendar.MONTH)+1;
                	estmtdOrdDay = currClndr.get(Calendar.DAY_OF_MONTH);
                }
                else
                	estmtdOrdYear = 0;
            }

        }

        // Customer Requested Arrival Date
        custReqstdArrivlDate = header.getCustReqstArrivlDate();
        if (this.custReqstdArrivlDate != null) {
            Calendar CRADDate = Calendar.getInstance();
            CRADDate.setTime(this.custReqstdArrivlDate);
            custReqstdArrivlYear = CRADDate.get(Calendar.YEAR);

            if (custReqstdArrivlYear >= currYear) {
            	custReqstdArrivlMonth = CRADDate.get(Calendar.MONTH)+1;
            	custReqstdArrivlDay = CRADDate.get(Calendar.DAY_OF_MONTH);
            }
            else
            	custReqstdArrivlYear = 0;
        }

        userAccess = user.getAccessLevel(QuoteConstants.APP_CODE_SQO);
        
        isSaasFCTToPAQuote = header.isSaasFCTToPAQuote();
        
        sspType =header.getSspType();
        
        bpSubmitterEmail = header.getBpSubmitterEmail();
        
        bpSubmitterName = header.getBpSubmitterName();
        
        bpSubmitDate = header.getBpSubmitDate();
        
        isSubmittedForEval = header.isSubmittedForEval();
      
        isAcceptedByEval = header.isAcceptedByEval();
     
        isReturnForChgByEval = header.isReturnForChgByEval();
     
        isEditForRetChgByEval = header.isEditForRetChgByEval();
        
        saasTermCondCatFlag = header.getSaasTermCondCatFlag();
        
    }

    



	public boolean isSaasFCTToPAQuote() {
		return isSaasFCTToPAQuote;
	}



	public void setSaasFCTToPAQuote(boolean isSaasFCTToPAQuote) {
		this.isSaasFCTToPAQuote = isSaasFCTToPAQuote;
	}



	public abstract String getDisplayTabAction();;

    public abstract String getPostTabAction();

    public boolean isSalesQuote() {
        return this.header.isSalesQuote();
    }

    public boolean isPGSQuote(){
    	return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(header.getAudCode());
    }

    public boolean isRenewalQuote() {
        return this.header.isRenewalQuote();
    }

    protected Contract getFirstContract(Customer customer) {
//        if (customer == null || customer.getContractList() == null || customer.getContractList().size() == 0)
//            return null;
//        return (Contract) customer.getContractList().get(0);
        Contract ctrct = null ;
        if (customer != null && customer.getContractList() != null && customer.getContractList().size()>0 ){
            ctrct =  (Contract) customer.getContractList().get(0);
            return ctrct ;
        }
        if (ctrct == null && customer != null && customer.getEnrolledCtrctList()!= null && customer.getEnrolledCtrctList().size()>0){
            ctrct = (Contract) customer.getEnrolledCtrctList().get(0);
            return ctrct ;
        }
        return null ;
    }

    protected String getAcqstnDescByCode(String acqstnCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        CodeDescObj acqstn = null;
        try {
            CacheProcess CacheProcess = CacheProcessFactory.singleton().create();
            acqstn = CacheProcess.getAcquisitionByCode(acqstnCode);
        } catch (QuoteException e) {
            logContext.error(this, "Failed to get acquisition by "+acqstnCode);
        }
        if (acqstn != null)
            return acqstn.getCodeDesc();
        else
            return acqstnCode;
    }

    protected String getCountryDesc(String cntryCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        Country cntry = null;
        try {
            CacheProcess cProcess = CacheProcessFactory.singleton().create();
            cntry = cProcess.getCountryByCode3(cntryCode);
        } catch (QuoteException e) {
            logContext.error(this, "Failed to get country by "+cntryCode);
        }
        return cntry == null ? cntryCode : cntry.getDesc();
    }

    protected String getCurrencyDesc(String cntryCode, String currencyCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        Country cntry = null;
        try {
            CacheProcess cProcess = CacheProcessFactory.singleton().create();
            cntry = cProcess.getCountryByCode3(cntryCode);
        } catch (QuoteException e) {
            logContext.error(this, "Failed to get currency by "+currencyCode);
        }
        return this.getCurrencyDesc(cntry, currencyCode);
    }

    protected String getCurrencyDesc(Country cntry, String currencyCode) {
        String currencyDesc = null;
        if (cntry != null) {
            List currencyList = cntry.getCurrencyList();
            if (currencyList != null && currencyList.size() > 0) {
                for (int i = 0; i < currencyList.size(); i++) {
                    CodeDescObj obj = (CodeDescObj) currencyList.get(i);
                    if (obj != null) {
                        String objKey = obj.getCode();
                        if (objKey != null && objKey.equalsIgnoreCase(currencyCode))
                            currencyDesc = obj.getCodeDesc();
                    }
                }
            }
        }
        return StringUtils.isNotBlank(currencyDesc) ? currencyDesc : currencyCode;
    }

    protected Country getCountry(String cntryCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        Country cntry = null;
        try {
            CacheProcess cProcess = CacheProcessFactory.singleton().create();
            cntry = cProcess.getCountryByCode3(cntryCode);
        } catch (QuoteException e) {
            logContext.error(this, "Failed to get country by "+cntryCode);
        }
        return cntry;
    }

    protected String getContractOptionDesc(String contractOption) {
        if (StringUtils.isBlank(contractOption))
            return "";
        List  ctrctOptList = null;
        try {
            CacheProcess process = CacheProcessFactory.singleton().create();
            ctrctOptList = process.getCtrctVariantList();
        } catch (QuoteException qe) {
            LogContextFactory.singleton().getLogContext().error(this, qe);
        }
        if (ctrctOptList != null && ctrctOptList.size() > 0) {
            for (int i = 0; i < ctrctOptList.size(); i++) {
                CodeDescObj codeDescObj = (CodeDescObj) ctrctOptList.get(i);
                if (contractOption.equalsIgnoreCase(codeDescObj.getCode()))
                    return codeDescObj.getCodeDesc();
            }
        }
        return contractOption;
    }

    public Collection generateAnniversaryOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};

        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        if (quoteExpDay == 0)
            collection.add(new SelectOptionImpl(labelString, "", true));
        else
            collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (quoteExpMonth == i)
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }

    	return collection;

    }

    public Collection generateDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        if (quoteExpDay == 0)
            collection.add(new SelectOptionImpl(sDay, "", true));
        else
            collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (quoteExpDay == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), false));

        }
        return collection;
    }

    public Collection generateYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        boolean yearSelected = (quoteExpYear != currYear && quoteExpYear != (currYear + 1));
        boolean currSelected = (quoteExpYear == currYear);
        boolean nextSelected = (quoteExpYear == (currYear + 1));

        collection.add(new SelectOptionImpl(sYear, "", yearSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear), String.valueOf(currYear), currSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear + 1), String.valueOf(currYear + 1), nextSelected));

        return collection;
    }

    public Collection generateCRADDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        if (this.custReqstdArrivlDay == 0)
            collection.add(new SelectOptionImpl(sDay, "", true));
        else
            collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (custReqstdArrivlDay == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), false));

        }
        return collection;
    }

    public Collection generateCRADAnniversaryOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};

        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        if (this.custReqstdArrivlDay == 0)
            collection.add(new SelectOptionImpl(labelString, "", true));
        else
            collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (this.custReqstdArrivlMonth == i)
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }

    	return collection;

    }

    public Collection generateCRADYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        boolean yearSelected = (this.custReqstdArrivlYear!= currYear && custReqstdArrivlYear != (currYear + 1));
        boolean currSelected = (custReqstdArrivlYear == currYear);
        boolean nextSelected = (custReqstdArrivlYear == (currYear + 1));

        collection.add(new SelectOptionImpl(sYear, "", yearSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear), String.valueOf(currYear), currSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear + 1), String.valueOf(currYear + 1), nextSelected));

        return collection;
    }

    public Collection genQuoteClassfctnOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String defaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, DraftQuoteMessageKeys.DEAFULT_OPTION);
        List classCodes = null;

        String qtClassCode = header.getQuoteClassfctnCode();
        boolean isDefault = StringUtils.isBlank(qtClassCode);
        collection.add(new SelectOptionImpl(defaultOption, "", isDefault));

        classCodes = QuoteClassificationCodeFactory.singleton().getAllQuoteClassfctnCodes();
        if (classCodes != null) {
            for (int i = 0; i < classCodes.size(); i++) {
                CodeDescObj classCode = (CodeDescObj) classCodes.get(i);
                boolean isSelected = false;
                if (StringUtils.isNotBlank(qtClassCode)) {
                    isSelected = qtClassCode.equalsIgnoreCase(classCode.getCode());
                }
                collection.add(new SelectOptionImpl(classCode.getCodeDesc(), classCode.getCode(), isSelected));
            }
        }

        return collection;
    }

    public Collection getAgrmtTypList() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String defaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, DraftQuoteMessageKeys.DEAFULT_OPTION);
        List agrmtTypList = null;

        String qtOemAgreementType = header.getOrdgMethodCode();
        boolean isDefault = StringUtils.isBlank(qtOemAgreementType);
        collection.add(new SelectOptionImpl(defaultOption, "", isDefault));

        try{
            agrmtTypList = CacheProcessFactory.singleton().create().getAllOemAgrmntTypes();
        }catch (QuoteException e) {
            LogContextFactory.singleton().getLogContext().error(this, "Failed to get OEM agreement type list.");
        }

        if (agrmtTypList != null) {
            for (int i = 0; i < agrmtTypList.size(); i++) {
                CodeDescObj classCode = (CodeDescObj) agrmtTypList.get(i);
                boolean isSelected = false;
                if (StringUtils.isNotBlank(qtOemAgreementType)) {
                    isSelected = qtOemAgreementType.equalsIgnoreCase(classCode.getCode());
                }
                collection.add(new SelectOptionImpl(classCode.getCodeDesc(), classCode.getCode(), isSelected));
            }
        }

        return collection;
    }

    public Collection getOemBidTypeList() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String defaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, DraftQuoteMessageKeys.DEAFULT_OPTION);
        List oemBidList = null;

        String qtOEMbidType = String.valueOf(header.getOemBidType());
        boolean isDefault = StringUtils.isBlank(qtOEMbidType);
        collection.add(new SelectOptionImpl(defaultOption, "", isDefault));

        try{
            oemBidList = CacheProcessFactory.singleton().create().getAllOemBidTypes();
        }catch (QuoteException e) {
            LogContextFactory.singleton().getLogContext().error(this, "Failed to get OEM/ASL type list.");
        }

        if (oemBidList != null) {
            for (int i = 0; i < oemBidList.size(); i++) {
                CodeDescObj classCode = (CodeDescObj) oemBidList.get(i);
                boolean isSelected = false;
                if (StringUtils.isNotBlank(qtOEMbidType)) {
                    isSelected = qtOEMbidType.equalsIgnoreCase(classCode.getCode());
                }
                collection.add(new SelectOptionImpl(classCode.getCodeDesc(), classCode.getCode(), isSelected));
            }
        }

        return collection;
    }

    public boolean isPA() {
        return header.isPAQuote();
    }

    public boolean isPAE() {
        return header.isPAEQuote();
    }

    public boolean isFCT() {
        return header.isFCTQuote();
    }

    public boolean isPPSS() {
        return header.isPPSSQuote();
    }

    public boolean isPAUN() {
        return header.isPAUNQuote();
    }

    public boolean isFCTToPA() {
        return ((header.isPAEQuote() || header.isPAQuote()) && header.isMigration());
    }

    public boolean isOEM() {
        return header.isOEMQuote();
    }
    
    public boolean isSSP() {
        return header.isSSPQuote();
    }
    
    public boolean isCSA() {
        return header.isCSRAQuote()||header.isCSTAQuote();
    }
    
    public String getPriceLevelDesc(String prcLvlCode) {
        return PartPriceConfigFactory.singleton().getPriceLevelDesc(prcLvlCode);
    }

    protected String genBtnParamsForAction(String jadeAction, String redirectURL, String params) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        String secondActionKey = appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);
        StringBuffer sb = new StringBuffer();

        sb.append(actionKey).append("=").append(jadeAction);
        if (StringUtils.isNotBlank(redirectURL))
            sb.append(",").append(secondActionKey).append("=").append(redirectURL);
        if (StringUtils.isNotBlank(params))
            sb.append(",").append(params);
        return sb.toString();
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

    public static StringBuffer addURLParam(StringBuffer url, String key, String value) {
        if (url == null) {
			url = new StringBuffer("");
		}
        if (key != null) {
            url.append("&");
            url.append(HtmlUtil.urlEncode(key));
            url.append("=");
            url.append(HtmlUtil.urlEncode(value != null ? value : ""));
        }
        return url;
    }

    public String getCurrency() {
        return currency == null ? "" : currency;
    }

    public String getCustCity() {
        return custCity == null ? "" : custCity;
    }

    public String getCustCntry() {
        return custCntry == null ? "" : custCntry;
    }

    public String getCustName() {
        return custName == null ? "" : custName;
    }

    public String getCustState() {
        return custState == null ? "" : custState;
    }

    public boolean isCustOnlySSP() {
		return this.isCustOnlySSP;
	}
    
    public String getCustStateDesc() {
        return custStateDesc == null ? "" : custStateDesc;
    }

    public Quote getQuote() {
        return quote;
    }

    public String getQuoteValue() {
        return quoteValue == null ? "" : quoteValue;
    }

    public User getUser() {
        return user;
    }

    public String getOriginalWebReference() {
        return StringUtils.trimToEmpty(originalWebReference);
    }

    public String getOrigWebRefUrl() {
        StringBuffer url = new StringBuffer();

        if (StringUtils.isNotBlank(originalWebReference)) {
            String actionURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB);
            url.append(actionURL);
            HtmlUtil.addURLParam(url, ParamKeys.PARAM_QUOTE_NUM, originalWebReference); //TODO find and add param key
        }
        return url.toString();
    }

    public String getAgreementNum() {
        return agreementNum == null ? "" : agreementNum;
    }
    
    public String getAgreementOption() {
        return agreementOption == null ? "" : agreementOption;
    }

    public String getIbmCustNum() {
        return ibmCustNum == null ? "" : ibmCustNum;
    }

    public String getRelationshipSVPLevel() {
        return relationshipSVPLevel == null ? "" : relationshipSVPLevel;
    }

    public String getSiteNumber() {
        return siteNumber == null ? "" : siteNumber;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public int getCurrYear() {
        return currYear;
    }

    public String getFormName() {
        return formName == null ? "" : formName;
    }

    public int getQuoteExpDay() {
        return quoteExpDay;
    }

    public int getQuoteExpMonth() {
        return quoteExpMonth;
    }

    public int getQuoteExpYear() {
        return quoteExpYear;
    }

    public String getLob() {
        return lob;
    }

    public String getQuoteType()
    {
        return quoteType;
    }

    public String getWebQuoteNum() {
        return webQuoteNum;
    }

    public String getCountry() {
        return country;
    }

    public boolean isDisplayQuoteValue() {
        return isDisplayQuoteValue;
    }

    public String formatCurrency(double currency) {
        return uiFormatter.formatEndCustomerPrice(currency);
    }

    public String getCurrGmtTime() {
        Locale locale = this.getLocale();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        SimpleDateFormat sdformat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz",locale);
        sdformat.setTimeZone(tz);
        String sDate = sdformat.format(new Date()).toString();
        return sDate;
    }

    protected String getCurrTime()
    {
    	Locale locale = this.getLocale();
        TimeZone tz = this.getDisplayTimeZone();
        SimpleDateFormat sdformat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss zzz",locale);
        sdformat.setTimeZone(tz);
        String sDate = sdformat.format(new Date()).toString();
        return sDate;
    }

    public String getCurrTimeMark() {
        return " (" + this.getCurrTime() + ")";
    }

    public String getAcqstnCode() {
        return acqstnCode;
    }

    public String getAcqstnDesc() {
        return acqstnDesc;
    }

    public void initJustSection(HttpServletRequest request)
    {
        Parameters parameters = (Parameters) request.getAttribute(FrameworkKeys.JADE_UNDO_PARAMETER_KEY);
        if ( parameters != null )
        {
            try
            {
                String[] justTexts = parameters.getParameterWithMultiValues(SpecialBidParamKeys.SECTION_JUST_TEXTS);
                String[] secIDs = parameters.getParameterWithMultiValues(SpecialBidParamKeys.SECTION_INDEXS);
                String[] lastModifyTimes = parameters.getParameterWithMultiValues(SpecialBidParamKeys.LAST_MODIFY_TIME);
                String[] textIDs = parameters.getParameterWithMultiValues(SpecialBidParamKeys.TEXT_IDS);
                if ( justTexts != null && justTexts.length > 0 )
                {
	                SpecialBidInfo bidInfo = quote.getSpecialBidInfo();
	                List secList = bidInfo.getJustSections();
	                for ( int i = 0; i < textIDs.length; i++ )
	                {
	                	JustSection sec = null;
	                	int secId = -1;
	                	try
						{
			    			secId = Integer.parseInt(secIDs[i]);
						}
			    		catch ( Throwable e )
						{
			    			secId = SpecialBidInfo.BEGIN_SUBMITTER + 1;
						}
	                	for ( int j = 0; j < secList.size(); j++ )
	                	{
	                		JustSection temp = (JustSection)secList.get(j);
	                		if ( temp.getSecId() == secId )
	                		{
	                			sec = temp;
	                			break;
	                		}
	                	}
	                	SpecialBidInfo.CommentInfo cmtInfo = null;
	                	if ( sec == null )
	                	{
	                		sec = new JustSection_Impl();
	                		sec.setSecId(secId);
	                		cmtInfo = new SpecialBidInfo.CommentInfo();
	                		sec.getJustTexts().add(cmtInfo);
	                		if ( i != 0 )
		                    {
		                        cmtInfo.typeCode = SpecialBidInfo.CommentInfo.SBADJUST;
		                    }
		                    else
		                    {
		                        cmtInfo.typeCode = SpecialBidInfo.CommentInfo.SPBID_J;
		                    }
	                	}
	                	else if ( sec.getJustTexts().size() == 0 )
	                	{
	                		cmtInfo = new SpecialBidInfo.CommentInfo();
	                		sec.getJustTexts().add(cmtInfo);
	                		if ( i != 0 )
		                    {
		                        cmtInfo.typeCode = SpecialBidInfo.CommentInfo.SBADJUST;
		                    }
		                    else
		                    {
		                        cmtInfo.typeCode = SpecialBidInfo.CommentInfo.SPBID_J;
		                    }
	                	}
	                	else
	                	{
	                		cmtInfo = (SpecialBidInfo.CommentInfo)sec.getJustTexts().get(0);
	                	}
	                	cmtInfo.comment = justTexts[i];
	                    cmtInfo.secId = Integer.parseInt(secIDs[i]);
	                    cmtInfo.textId = textIDs[i];
	                    cmtInfo.commentDateText = lastModifyTimes[i];
	                }
                }
            }
            catch ( Throwable e )
            {
            	LogContext logContext = LogContextFactory.singleton().getLogContext();
            	logContext.error(this, e);
            }
        }
    }

    protected SpecialBidCommon getSpecialBidCommon()
    {
        //real value will get from sub class
    	return null;
    }

    public List getSPTabMessList()
    {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
    	List list = new ArrayList();
    	QuoteHeader header = quote.getQuoteHeader();
    	SpecialBidCommon comm = getSpecialBidCommon();
    	logContext.debug(this, "spbReason: " + spbReason);
    	if ( header.getSpeclBidFlag() == 1 )
	    {
    	    if ( spbReason == null )
    	    {
    	        spbReason = comm.spBidCondition.getSpecialBidReason();
    	        logContext.debug(this, "spbReason is null and get from rule " + spbReason);
    	    }

    	    String code = spbReason.getEMEADiscountReason();
            if ( code == null || code.trim().equals("") )
            {
                isEMEADiscountRequireSpBid = false;
            }
            else
            {
                isEMEADiscountRequireSpBid = true;
            }

    	    if ( header.getApprovalRouteFlag() == 1 )
	        {
	            list.add(SpecialBidViewKeys.PART_PRICING_REQUIRE_APPROVAL);
	            List temp = spbReason.getSpecialBidReasonList();
                logContext.debug(this, "spbReason getSpecialBidReasonList: " + temp);
                if ( temp != null && temp.size() > 0 )
                {
                    list.addAll(QuoteCommonUtil.translateReasonCode2Key(temp));
                }
	        }
	        else
	        {
	            list.add(SpecialBidViewKeys.DONOT_REQUIRE_APPROVAL);
	            List temp = spbReason.getNoApprovalReasonList();
	            logContext.debug(this, "spbReason getNoApprovalReasonList: " + temp);
                if ( temp != null && temp.size() > 0 )
                {
                    list.addAll(QuoteCommonUtil.translateReasonCode2Key(temp));
                }
	        }
	    }
	    else
	    {
	        list.add(SpecialBidViewKeys.SPECIAL_BID_DONOT_REQUIRE_APPROVAL);
	    }
    	return list;
    }

    public String getCustDesignation() {
        return custDesignation == null ? "" : custDesignation;
    }
    /**
     * @return Returns the isEMEADiscountRequireSpBid.
     */
    public boolean isEMEADiscountRequireSpBid() {
        return isEMEADiscountRequireSpBid;
    }

    public String getI18NString(String baseName, String key){
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();

        String value = appCtx.getI18nValueAsString(baseName, locale, key);

        if (value == null || value.equals("")) {
            value = key;
        }

        return value;
    }

    public String getI18NString(String baseName, String key, String[] params){
        String value = getI18NString(baseName, key);

        if(params != null && params.length > 0){
            return MessageFormat.format(value, params);
        }
        return value;
    }

    public boolean isAllowEditStartDate(QuoteLineItem qli){
        //Comrss cvrage applied parts, start date edit is not allowed
    	return PartPriceConfigFactory.singleton().isAllowEditStartDate(quote.getQuoteHeader(), qli)
    	                  && (!qli.hasValidCmprssCvrageMonth());

    }

    public boolean isAllowEditEndDate(QuoteLineItem qli){
    	return PartPriceConfigFactory.singleton().isAllowEditEndDate(
    			quote.getQuoteHeader(), qli);

    }

    public boolean isSpecialBidRnwlPart(QuoteLineItem qli){
    	return CommonServiceUtil.isSpecialBidRnwlPart(quote.getQuoteHeader(), qli);
    }

    public boolean disableOverrideUnitPriceInput(QuoteLineItem qli){
    	return ((qli.isObsoletePart() && !qli.canPartBeReactivated())
    	        || (qli.hasValidCmprssCvrageMonth() )
    	        || isBidIteratnQt()
    	        || qli.isReplacedPart()
    	        || qli.isSaasSubsumedSubscrptnPart());
    }

    public boolean disableDiscPctInput(QuoteLineItem qli){
        return ((qli.hasValidCmprssCvrageMonth() )
        		|| qli.isObsoletePart()
        		|| qli.isReplacedPart());
    }

    public String getPartControlledMsg(String bundle, QuoteLineItem qli){
        return getI18NString(bundle, PartPriceViewKeys.PART_CONTROLLED_MSG,
                             new String[]{qli.getControlledCodeDesc(), qli.getIbmProgCodeDscr()});
    }
    public String getControlledDistributionMsg(String bundle, QuoteLineItem qli){
        String controlledDistributionMsg = "";
        header = quote.getQuoteHeader();
        String controllDisType= qli.getSControlledDistributionType();
        if(qli.isControlled()
                && StringUtils.isNotBlank(header.getRselCustNum())
                && header.isChannelQuote()
                && (header.isPAQuote() || header.isPAEQuote() || header.isSSPQuote())
                && header.isSalesQuote()
                && StringUtils.isNotEmpty(controllDisType)
                && !QuoteConstants.CONTROLLED_DISTRIBUTION_NONE.equals(controllDisType)){
            controlledDistributionMsg = getI18NString(bundle, controllDisType);
        }
        return controlledDistributionMsg;
    }

    public String getCntrlldDstrbtnMsg4PGS(String bundle, QuoteLineItem qli){
        String controlledDistributionMsg = "";
        header = quote.getQuoteHeader();
        String controllDisType= qli.getSControlledDistributionType();
        if(qli.isControlled()
                && StringUtils.isNotBlank(header.getRselCustNum())
                && header.isChannelQuote()
                && (header.isPAQuote() || header.isPAEQuote())
                && header.isSalesQuote()
                && StringUtils.isNotEmpty(controllDisType)
                && !QuoteConstants.CONTROLLED_DISTRIBUTION_NONE.equals(controllDisType)){
        	String msgKey = "";
        	if(isDistributor()){
        		if(QuoteConstants.CONTROLLED_DISTRIBUTION_RSEL_NOT_AUTHORIZED.equals(controllDisType)){
        			msgKey = "dist_is_not_authorized";
        		}else if(QuoteConstants.CONTROLLED_DISTRIBUTION_RSEL_DIST_NOT_ASSOCIATED.equals(controllDisType)){
        			msgKey = "dist_not_associated";
        		}
        	}else if(isTier1Reseller() || isTier2Reseller()){
        		msgKey = "rsel_is_not_authorized";
        	}
            controlledDistributionMsg = getI18NString(bundle, msgKey);
        }
        return controlledDistributionMsg;
    }


    public boolean isPartUnPublished(QuoteLineItem qli){
        return (isPA() || isPAE()) && qli.isPartRestrct();
    }

    //part price tab, quote last price date
    public String getLastPrcDate() {
    	return DateUtil.formatDate(quote.getQuoteHeader().getPriceStartDate(),
    	        DateUtil.PATTERN1, getLocale());
    }

    public boolean showLastPrcDateNote(){
        return (quote.getQuoteHeader().getPriceStartDate() != null
                && quote.getLineItemList() != null
                && quote.getLineItemList().size() > 0);
    }

    public boolean isEnforceEndDate(QuoteLineItem qli){
        return PartPriceConfigFactory.singleton().isEnforceEndDate(qli.getRevnStrmCode());
    }

    /**
     * @return Returns the displayCustCreateNote.
     */
    public boolean isDisplayCustCreateNote() {
        if ( quote.getQuoteHeader().getSpeclBidFlag() != 1 )
        {
            return false;
        }
        Customer cust = quote.getCustomer();
        if ( cust != null && cust.isAddiSiteCustomer() )
        {
            return true;
        }
        return false;
    }

    public Collection generateExtensionDayOptions(int day) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        if (day == 0)
            collection.add(new SelectOptionImpl(sDay, "", true));
        else
            collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (day == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }
        return collection;
    }
    
    public Collection generateExtensionMonthOptions(int month) {
    	month++;
    	Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};

        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        if (month == 0)
            collection.add(new SelectOptionImpl(labelString, "", true));
        else
            collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (month == i)
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }
        Boolean webTerm = false;
    	return collection;

    }
    
    public Collection generateExtensionYearOptions(int year) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        boolean yearSelected = (year >= currYear && year <= (currYear + 5));
        int selectedYear = -1;
        for(int i = 0;i<6;i++){
        	if(year==currYear+i){
        		selectedYear = currYear+i;
        		break;	
        	}
        }
        collection.add(new SelectOptionImpl(sYear, "", !yearSelected));
        for(int i = 0;i<6;i++){
        	if(selectedYear == currYear+i){
        		collection.add(new SelectOptionImpl(String.valueOf(currYear+i), String.valueOf(currYear+i), true));
        	}else{
        		collection.add(new SelectOptionImpl(String.valueOf(currYear+i), String.valueOf(currYear+i), false));
        	}
        }
        return collection;
    }

    public Collection generateStartMonthOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};

        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        if (quoteStartDay == 0)
            collection.add(new SelectOptionImpl(labelString, "", true));
        else
            collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (quoteStartMonth == i)
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }

    	return collection;

    }

    public Collection generateStartDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        if (quoteStartDay == 0)
            collection.add(new SelectOptionImpl(sDay, "", true));
        else
            collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (quoteStartDay == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), false));

        }
        return collection;
    }

    public Collection generateStartYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        boolean yearSelected = (quoteStartYear != currYear && quoteStartYear != (currYear + 1));
        boolean currSelected = (quoteStartYear == currYear);
        boolean nextSelected = (quoteStartYear == (currYear + 1));

        collection.add(new SelectOptionImpl(sYear, "", yearSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear), String.valueOf(currYear), currSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear + 1), String.valueOf(currYear + 1), nextSelected));

        return collection;
    }

    public String getActionURL(String action1, String action2, String[] paramKeys, String[] paramValues) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String Action2Key = appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);

        String actionURL = HtmlUtil.getURLForAction(action1);
        StringBuffer sb = new StringBuffer(actionURL);
        if(StringUtils.isNotBlank(action2)){
            HtmlUtil.addURLParam(sb, Action2Key, action2);
        }
        if (paramKeys != null && paramValues != null) {
            for (int i = 0; i < paramKeys.length; i++)
                HtmlUtil.addURLParam(sb, paramKeys[i], paramValues[i]);
        }

        return sb.toString();
    }



    protected String handleLockedQuoteMessage(String lockedBy,String webQuoteNum, String msgKey) {
        String msgInfo = "";
        if (StringUtils.isNotBlank(lockedBy)) {
            ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
            msgInfo = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, msgKey);
            String[] args = { lockedBy, webQuoteNum };
            msgInfo = MessageFormat.format(msgInfo, args);
        }
        return msgInfo;
    }
    
    public String getLockedQuoteMsg(){
        return lockedQuoteMsg;
    }
    public boolean isLockedFlag(){
        return isLockedFlag;
    }
    /**
     * @return Returns the validityDays.
     */
    public int getValidityDays() {
        return CommonServiceUtil.getValidityDays(quote.getQuoteHeader());
    }

    public boolean isBidIteratnQt(){
        return (QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode()) && quote.getQuoteHeader().isBidIteratnQt());
    }

    protected String getOemBidTypeDescByCode(String oemBidTypeCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        CodeDescObj oemBidType = null;
        String oemBidTypeDescEmp = "";
        if(StringUtils.isBlank(oemBidTypeCode)){
            return oemBidTypeDescEmp;
        }
        try {
            CacheProcess CacheProcess = CacheProcessFactory.singleton().create();
            oemBidType = CacheProcess.getOemBidTypeByCode(oemBidTypeCode);
        } catch (QuoteException e) {
            logContext.error(this, "Failed to get OEM Bid type by "+oemBidTypeCode);
        }
        if (oemBidType != null)
            return oemBidType.getCodeDesc();
        else
            return oemBidTypeDescEmp;
    }
    /**
     * @return Returns the isDisplayOemBidType.
     */
    public boolean isDisplayOemBidType() {
        return isOEM() && ButtonDisplayRuleFactory.singleton().isDisplayOemBidType(header);
    }

    public String getIdByName(String name){
        return PartPriceViewKeys.PREFIX + name;
    }

    public String getPopupQuoteComparURL(String sqoReference){
    	StringBuffer url = new StringBuffer();

        if (StringUtils.isNotBlank(originalWebReference)) {
            String actionURL = HtmlUtil.getURLForAction(ActionKeys.DISPLAY_QUOTE_BIDCOMPARISON);
            url.append(actionURL);
            HtmlUtil.addURLParam(url, ParamKeys.ORIGINAL_QUOTE_NUM, originalWebReference);
            HtmlUtil.addURLParam(url, ParamKeys.COPIED_QUOTE_NUM, sqoReference);
        }
        return url.toString();

    }



	public List getCustomerGroupList() {
		return customerGroupList;
	}

	public List getLineItems(){
		return quote.getLineItemList();
	}

	public List getSaaSLineItemList(){
		return quote.getSaaSLineItems();
	}

	public List getSoftwareLineItems(){
		return quote.getSoftwareLineItems();
	}

	public Map getPartsPricingConfigrtnsMap(){
		return quote.getPartsPricingConfigrtnsMap();
	}

	public List getPartsPricingConfigrtnsList(){
		return quote.getPartsPricingConfigrtnsList();
	}

	/**
	 * @return
	 * String
	 */
	public String getSaaSTotCmmtVal(){
		Double totCmmtVal = quote.getQuoteHeader().getSaasTotCmmtmtVal();
		if(totCmmtVal == null){
			return "0.00";
		}
		double dTotCmmtVal = totCmmtVal.doubleValue();
		return uiFormatter.formatEndCustomerPrice(dTotCmmtVal);
	}

	public String getCntrctVarMonthLev() {
		cntrctVarMonthLev = anniversary
				+ "&nbsp;/&nbsp;" + relationshipSVPLevel;
		return cntrctVarMonthLev;
	}

	public boolean isDisplayAgreementInfo() {
		return isDisplayAgreementInfo;
	}

	public Collection generateEstmtdOrdMonthOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};

        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        if (estmtdOrdMonth == 0)
            collection.add(new SelectOptionImpl(labelString, "", true));
        else
            collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (estmtdOrdMonth == i)
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }

    	return collection;

    }

    public Collection generateEstmtdOrdDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        if (estmtdOrdDay == 0)
            collection.add(new SelectOptionImpl(sDay, "", true));
        else
            collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (estmtdOrdDay == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), false));

        }
        return collection;
    }


    public Collection generateEstmtdOrdYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        boolean yearSelected = (estmtdOrdYear != currYear && estmtdOrdYear != (currYear + 1));
        boolean currSelected = (estmtdOrdYear == currYear);
        boolean nextSelected = (estmtdOrdYear == (currYear + 1));

        collection.add(new SelectOptionImpl(sYear, "", yearSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear), String.valueOf(currYear), currSelected));
        collection.add(new SelectOptionImpl(String.valueOf(currYear + 1), String.valueOf(currYear + 1), nextSelected));

        return collection;
    }

    public String getChargeAgreementNum(){
    	return StringUtils.isBlank(quote.getQuoteHeader().getRefDocNum()) ? "" : quote.getQuoteHeader().getRefDocNum();
    }

    public QuoteUserSession getSalesRep(){
    	return quoteUserSession;
    }

    /**
     * check if show BP columns for PGS
     * @return
     * if login user is tier 1 reseller or distributor
     * and not acting as tier 2 reseller
     * return true
     */
    public boolean showBPColForPGS(){
		return ((isTier1Reseller() || isDistributor())
				&& !isActingAsTier2Reseller());
	}

	public boolean showIncreaseBidTCV(PartsPricingConfiguration configrtn) {
		if(configrtn.getIncreaseBidTCV() == null){
			return false;
		}else if(isActingAsTier2Reseller()){
			return ApplicationProperties.getInstance().getT2PriceAvailable();
		}else{
			return true;
		}
    }

	public boolean showUnusedBidTCV(PartsPricingConfiguration configrtn) {
		if(configrtn.getUnusedBidTCV() == null){
			return false;
		}else if(isActingAsTier2Reseller()){
			return ApplicationProperties.getInstance().getT2PriceAvailable();
		}else{
			return true;
		}
    }
	
	public boolean showUnusedBidTCV(MonthlySoftwareConfiguration configrtn) {
		if(configrtn.getUnusedBidTCV() == null){
			return false;
		}else if(isActingAsTier2Reseller()){
			return ApplicationProperties.getInstance().getT2PriceAvailable();
		}else{
			return true;
		}
    }

	public String getIncreaseBidTCV(PartsPricingConfiguration configrtn) {
        if (showIncreaseBidTCV(configrtn)) {
          //if draft quote and no 'Estimated order date' or 'Estimated provisioning days', show N/A for UnusedBidTCV and IncreaseBidTCV.
            if (!quote.getQuoteHeader().isSubmittedQuote()) {
                // if EstmtdOrdDate is null, Estimated provisioning days is absolutely null as well.
                if (quote.getQuoteHeader().getEstmtdOrdDate() == null) {
                    return DraftQuoteConstants.N_A;
                }
            }
            return uiFormatter.formatEndCustomerPrice(configrtn.getIncreaseBidTCV().doubleValue());
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }
	public String getUnusedBidTCV(PartsPricingConfiguration configrtn) {
        if (showUnusedBidTCV(configrtn)) {
            if (!quote.getQuoteHeader().isSubmittedQuote()) {
                // if EstmtdOrdDate is null, Estimated provisioning days is absolutely null as well.
                if (quote.getQuoteHeader().getEstmtdOrdDate() == null) {
                    return DraftQuoteConstants.N_A;
                }
            }
            return uiFormatter.formatEndCustomerPrice(configrtn.getUnusedBidTCV().doubleValue());
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }
	
	public String getUnusedBidTCV(MonthlySoftwareConfiguration configrtn) {
        if (showUnusedBidTCV(configrtn)) {
            if (!quote.getQuoteHeader().isSubmittedQuote()) {
                // if EstmtdOrdDate is null
                if (quote.getQuoteHeader().getEstmtdOrdDate() == null) {
                    return DraftQuoteConstants.N_A;
                }
            }
            return uiFormatter.formatEndCustomerPrice(configrtn.getUnusedBidTCV().doubleValue());
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }

	/**
	 * check if one  house account is acting as tier 2 reseller only
	 * @return
	 */
	public boolean isHAActingAsTier2Only(){

    	if(quoteUserSession.getSiteNumber() != null // in PGS application
    			&& quote.getPayer() != null
    			&& quoteUserSession.isHouseAccountFlag() // is house account
    			&& !quoteUserSession.getSiteNumber().equalsIgnoreCase(quote.getPayer().getCustNum())){// current house account is not quote distributor
    		return true;
    	}

    	return false;
	}

	/**
	 * check if behavior acting as a tier 2 reseller
	 * @return
	 * login user is tier 2 reseller or
	 * log in user is a house account and this house account acting as tier 2 reseller
	 * return true
	 */
	public boolean isActingAsTier2Reseller(){
		return isTier2Reseller() || isHAActingAsTier2Only();
	}

	/**
	 * check if it's a PA quote, excluding Fct To PA quote
	 * @return
	 */
	public boolean isPAQuote(){
		if(this.getQuote() == null||this.getQuote().getQuoteHeader() == null){
			return false;
		}
		QuoteHeader qh = this.getQuote().getQuoteHeader();

		if(qh.isPAQuote() && !qh.isFCTToPAQuote()){
			return true;
		}else{
			return false;
		}
	}


	/**
	 * check if it's a PAE quote, excluding Fct To PA quote
	 * @return
	 */
	public boolean isPAEQuote(){
		if(this.getQuote() == null||this.getQuote().getQuoteHeader() == null){
			return false;
		}
		QuoteHeader qh = this.getQuote().getQuoteHeader();

		if(qh.isPAEQuote() && !qh.isFCTToPAQuote()){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * check if it's a FCT quote
	 * @return
	 */
	public boolean isFCTQuote(){
		if(this.getQuote() == null||this.getQuote().getQuoteHeader() == null){
			return false;
		}
		QuoteHeader qh = this.getQuote().getQuoteHeader();

		if(qh.isFCTQuote()){
			return true;
		}else{
			return false;
		}
	}


	/**
	 * check if it's a FCT to PA quote
	 * @return
	 */
	public boolean isFCTToPAQuote(){
		if(this.getQuote() == null||this.getQuote().getQuoteHeader() == null){
			return false;
		}
		QuoteHeader qh = this.getQuote().getQuoteHeader();

		if(qh.isFCTToPAQuote()){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * check if it's a OEM quote
	 * @return
	 */
	public boolean isOEMQuote(){
		if(this.getQuote() == null||this.getQuote().getQuoteHeader() == null){
			return false;
		}
		QuoteHeader qh = this.getQuote().getQuoteHeader();

		if(qh.isOEMQuote()){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * check if it's a PPSS quote
	 * @return
	 */
	public boolean isPPSSQuote(){
		if(this.getQuote() == null||this.getQuote().getQuoteHeader() == null){
			return false;
		}
		QuoteHeader qh = this.getQuote().getQuoteHeader();

		if(qh.isPPSSQuote()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isDisplayCRAD() {
		return isDisplayCRAD;
	}
	
	public String getOrignlSalesOrdRefNum() {
		return orignlSalesOrdRefNum;
	}

	/**
	 * //refer to rtc#207982. For 10.6, only SQO needs this functionality.
	 * @param lineItem
	 * @return
	 * boolean
	 * judge whether Does billing frequency apply to these parts
	 */
	public boolean showSaasRenwl(QuoteLineItem lineItem) {
		boolean ret = false;
		if(!isPGSFlag()){
			if(!quote.getQuoteHeader().isFCTToPAQuote()
				&& !hasFctToPAFinalization()){
				if ((lineItem.isSaasSubscrptnPart() && !lineItem.isReplacedPart()) || lineItem.isSaasSubsumedSubscrptnPart()) {
					ret = true;
				} else if (lineItem.isMonthlySoftwarePart()){
					if (((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart() && !lineItem.isReplacedPart())
					ret =true;
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param lineItem
	 * @return if login application is not PGS and part is not replaced part, return true
	 */
	public boolean isDisplayMigration(QuoteLineItem lineItem){
		if(quote.getQuoteHeader().isSalesQuote()){
			if((quote.getQuoteHeader().isPAQuote()
				|| quote.getQuoteHeader().isPAEQuote()
				|| quote.getQuoteHeader().isPAUNQuote()
				|| quote.getQuoteHeader().isFCTQuote()
				|| quote.getQuoteHeader().isSSPQuote())
				&& !quote.getQuoteHeader().isFCTToPAQuote()
				&& !quote.getQuoteHeader().isOEMQuote()
				&& !hasFctToPAFinalization()){
				if(!isPGSFlag()
					&& !lineItem.isReplacedPart()
					&& (lineItem.isSaasSubscrptnPart() || lineItem.isMonthlySoftwarePart() && ((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart())){
					return true;
				}
			}
		}
		return false;
	}

	public boolean isDisplayMigFlagLink(){
		List saasLineItems = quote.getSaaSLineItems();
		for (int i=0;i<saasLineItems.size();i++){
			QuoteLineItem lineItem = (QuoteLineItem)saasLineItems.get(i);
			if(isDisplayMigration(lineItem)){
				return true;
			}
		}
		return false;
	}

	public boolean isDisPlaySaasRenwlFlagLink(){
		List saasLineItems = quote.getSaaSLineItems();
		for (int i=0;i<saasLineItems.size();i++){
			QuoteLineItem lineItem = (QuoteLineItem)saasLineItems.get(i);
			if(showSaasRenwl(lineItem)){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSoftSLineItem(){
		return quote.getQuoteHeader().hasSoftSLineItem();
	}
	
	public boolean hasFctToPAFinalization(){
		List<PartsPricingConfiguration> configList = quote.getPartsPricingConfigrtnsList();
		if(configList == null || configList.size() ==0){
			return false;
		}
		for (Iterator iterator = configList.iterator(); iterator.hasNext();) {
			PartsPricingConfiguration partsPricingConfiguration = (PartsPricingConfiguration) iterator
					.next();
			if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(partsPricingConfiguration.getConfigrtnActionCode())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if show provisioning id
	 * loop all the configurations in the Saas brand, 
	 * if there is at least provisioning is not blank, return true
	 * else return false
	 * @return
	 */
	public boolean isShowProvisioningId(String saasBrandCode){
		List<PartsPricingConfiguration> configrtnList =  getSaasBrandMap().get(saasBrandCode);
		if(configrtnList != null ){
			for (Iterator iterator = configrtnList.iterator(); iterator.hasNext();) {
				PartsPricingConfiguration configrtn = (PartsPricingConfiguration) iterator.next();
				if(StringUtils.isNotBlank(configrtn.getProvisioningId())){
					return true;
				}
			}
		}
		return false;
	}
	
	public Boolean isSaasMigration() {
		return quote.getQuoteHeader().isSaasMigrationFlag();
	}

	public Boolean isSaasRenewal() {
		return quote.getQuoteHeader().isSaasRenewalFlag();
	}
	

	public Boolean isMonthlyMigration() {
		return quote.getQuoteHeader().isMonthlyMigrationFlag();
	}

	public Boolean isMonthlyRenewal() {
		return quote.getQuoteHeader().isMonthlyRenewalFlag();
	}
	/**
	 * as there may be many configurations in one SaaS brand,
	 * and may some have provisioning id, but some don't
	 * and all the configurations will be sorted in page
	 * so loop all configurations to get the provisioning id not blank to be the SaaS brand provisioning id
	 * @param saasBrandCode
	 * @return
	 */
	public String getProvisioningId(String saasBrandCode){
		String provisgnId = "";
		List<PartsPricingConfiguration> configrtnList =  getSaasBrandMap().get(saasBrandCode);
		if(configrtnList != null ){
			for (Iterator iterator = configrtnList.iterator(); iterator.hasNext();) {
				PartsPricingConfiguration configrtn = (PartsPricingConfiguration) iterator.next();
				if(StringUtils.isNotBlank(configrtn.getProvisioningId())){
					provisgnId = configrtn.getProvisioningId();
					break;
				}
			}
		}
		return provisgnId;
	}

    public Map<String,String> getSaasBrandCodeMap(){
        return quote.getSaasBrandCodeMap();
    }
    
    public Map<String,List<PartsPricingConfiguration>> getSaasBrandMap(){
        return quote.getSaasBrandMap();
    }
    
    public String getBPSubmitter(){
        if(StringUtils.isNotBlank(bpSubmitterName) && StringUtils.isNotBlank(bpSubmitterEmail)) {
            return bpSubmitterName + " (" + bpSubmitterEmail + ")";
        } else {
            return "";
        }
    }

	public String getBpSubmitFormatDate() {
		 if (bpSubmitDate == null) {
			 return "";
		 } else {
	            return com.ibm.dsw.quote.base.util.DateHelper.formatToLocalTime(bpSubmitDate, "dd MMM yyyy HH:mm:ss zzz", this.getDisplayTimeZone(), locale);
		 }
	}

	public boolean isSubmittedForEval() {
		return isSubmittedForEval;
	}

	public boolean isAcceptedByEval() {
		return isAcceptedByEval;
	}

	public boolean isReturnForChgByEval() {
		return isReturnForChgByEval;
	}

	public boolean isEditForRetChgByEval() {
		return isEditForRetChgByEval;
	}
	
	public boolean isRSVPSRPOnly() {
		return this.isRSVPSRPOnly;
	}
	
	public String getRSVPSRPOnly() {
		if (isRSVPSRPOnly()) 
			return "Yes";
		else
			return "No";
	}
	
	public boolean isDisplayRSVPSRPOnly() {
		return this.isDisplayRSVPSRPOnly;
	}
	
	/**
	 * Add a flag in QuoteHeader, if YTY growth% and implied YTY growth% is not
	 * null, it'll be a growth delegation quote.
	 */
	public boolean isGrowthDelegation() {
		if (header.hasLineItemsFromRQ(quote)) return true;
		return header.isGrowthDelegation(quote);
	}
	
	public String getRenewalQuoteNum(){
		return header.getRenwlQuoteNum();
	}
	
	public boolean isShowRelatedBidTable(){
		if((header.isPAEQuote()
				 || header.isPAQuote() || header.isPAUNQuote())
		      && !header.isFCTToPAQuote()){
			List list = quote.getLineItemList();
			
			if(list != null && list.size() > 0){
				for(int i = 0; i < list.size(); i++){
					QuoteLineItem qli = (QuoteLineItem)list.get(i);
					if(qli.isReferenceToRenewalQuote()){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	

	public int getQuoteStartDay() {
		return quoteStartDay;
	}

	public int getQuoteStartMonth() {
		return quoteStartMonth;
	}

	public int getQuoteStartYear() {
		return quoteStartYear;
	}
	
	protected String[] initExemptionInfo(Quote quote) throws QuoteException{
        String[] exemptionInfoArray = new String[3];
		QuoteHeader qh = quote.getQuoteHeader();
        if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(qh.getFulfillmentSrc()) && (this.isPA() || this.isPAE() || this.isOEM() || this.isSSP())) {
        	exemptionInfoArray[0] = DraftQuoteConstants.EXEMPTNCODE_30;
        	exemptionInfoArray[1] = "exemption_code30";
        } else if ((this.isPPSS() || (this.isPA() || this.isPAE() || this.isOEM() || this.isSSP()))
                && !QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(qh.getFulfillmentSrc())
                && qh.getContainLineItemRevnStrm() == 1) {
        	exemptionInfoArray[0] = DraftQuoteConstants.EXEMPTNCODE_60;
        	exemptionInfoArray[1] = "exemption_code60";
        } else if((this.isPPSS() || (this.isPA() || this.isPAE() || this.isOEM() || this.isSSP()))
        		&& ((qh.getQuotePriceTot() == DraftQuoteConstants.QUOTE_TOTAL_VALUE_ZERO && !qh.hasSaaSLineItem())
        				|| qh.isAllPartsHasMediaAttr())) {
        	exemptionInfoArray[0] = DraftQuoteConstants.EXEMPTNCODE_40;
        	exemptionInfoArray[1] = "exemption_code40";
        }  else if ((this.isPPSS() || (this.isPA() || this.isPAE() || this.isOEM() || this.isSSP()))
        		&& qh.getTotalPriceInUSD()< DraftQuoteConstants.QUOTE_TOTAL_VALUE_UPPER_LIMIT) {
        	exemptionInfoArray[0] = DraftQuoteConstants.EXEMPTNCODE_50;
        	exemptionInfoArray[1] = "exemption_code50";
        } else {
        	exemptionInfoArray[0] = DraftQuoteConstants.EXEMPTNCODE_70;
        	exemptionInfoArray[1] = "exemption_code70";
        	if(StringUtils.isNotBlank(qh.getExemptnCode()) && qh.getExemptnCode().equalsIgnoreCase(DraftQuoteConstants.EXEMPTNCODE_80)) {
        		exemptionInfoArray[2] = DraftQuoteConstants.EXEMPTNCODE_80;
        	}   
        }
        if(exemptionInfoArray[2]==null){
        	if(StringUtils.isNotBlank(qh.getExemptnCode())){
        		exemptionInfoArray[2] = qh.getExemptnCode();
        	}else{
        		exemptionInfoArray[2] = exemptionInfoArray[0];
        	}
        }
        return exemptionInfoArray;
    }
	
	/**
	 * set display flag for ship-to/install address section.
	 * @return boolean
	 */
	public boolean isDisShipInstAdr() {
		return header.isDisShipInstAdrFlag();
	}	
	
	
	public Date getCRAD(){
		return header.getCustReqstArrivlDate();
	}
	
	public List getMultipleAddtionalYearImpliedGrowth(){
		return quote.getQuoteHeader().getMultipleAdditionalYearImpliedGrowth();
	}
	
	public List getMultipleAddtionalYearImpliedLabel() {
		List<String> result = new ArrayList<String>();
		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();

		if(getMultipleAddtionalYearImpliedGrowth() != null && getMultipleAddtionalYearImpliedGrowth().size() != 0) {
			for (int i = 0; i < getMultipleAddtionalYearImpliedGrowth().size(); i++) {
				result.add(ADDITIONAL_YEAR_LIST[i] + " " + context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.ADDITIONAL_YEAR_GROWTH));
			}
		}
		return result;
	}
	
	public List getMultipleAddtionalYearImpliedValue(){
		List<String> result = new ArrayList<String>();
		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();

		if(getMultipleAddtionalYearImpliedGrowth() != null && getMultipleAddtionalYearImpliedGrowth().size() != 0) {
			for (int i = 0; i < getMultipleAddtionalYearImpliedGrowth().size(); i++) {
				result.add(context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.OVERALL_GROWTH) + ":");
			}
		}
		return result;
	}
	
	public ApplianceAddress getShipToAddr() {
		ApplianceAddress shipToAdd = null;
		List<ApplianceAddress> addrList = this.getQuote().getDefaultCustAddr().getCustDefaulAddreList();
		if (null != addrList && addrList.size() > 0) {
			shipToAdd = addrList.get(0);
		}
		return shipToAdd;
	}
	
	protected String getSoldToCustomerAddress(CustPrtnrTabViewAdapter adapter){
		StringBuffer sb = new StringBuffer();
		if(StringUtils.isNotBlank(adapter.getQtCntctFirstName()) && StringUtils.isNotBlank(adapter.getQtCntctLastName())) {
			sb.append(adapter.getQtCntctFirstName()).append(" ").append(adapter.getQtCntctLastName());
		}
		if(StringUtils.isNotBlank(this.getCustName())){ 
			sb.append("<br/>").append(this.getCustName());
		}
		if(StringUtils.isNotBlank(adapter.getCustName2())) { 
			sb.append("<br/>").append(adapter.getCustName2());
		}
		if(StringUtils.isNotBlank(adapter.getCustName3())) {
			sb.append("<br/>").append(adapter.getCustName3());
		}
		if(StringUtils.isNotBlank(adapter.getAddress1())) {
			sb.append("<br/>").append(adapter.getAddress1());
		}
		if(StringUtils.isNotBlank(adapter.getAddress2())) {
			sb.append("<br/>").append(adapter.getAddress2());
		}
		if(StringUtils.isNotBlank(adapter.getCustCityStateZip())) { 
			sb.append("<br/>").append(adapter.getCustCityStateZip());
		}
		if(StringUtils.isNotBlank(this.getCustCntry())) {
			sb.append("<br/>").append(this.getCustCntry());
		}
		if(StringUtils.isNotBlank(adapter.getQtCntctPhone())) { 
			sb.append("<br/>").append(adapter.getQtCntctPhone());
		} 
		return sb.toString();
	}
	
	protected String getSoldToCompanyAddress(CustPrtnrTabViewAdapter adapter){
		StringBuffer sb = new StringBuffer();
		if(StringUtils.isNotBlank(adapter.getQtCntctFirstName()) && StringUtils.isNotBlank(adapter.getQtCntctLastName())) {
			sb.append(adapter.getQtCntctFirstName()).append(" ").append(adapter.getQtCntctLastName());
		}
		if(StringUtils.isNotBlank(this.getCustName())){
			if(sb.length() > 0)
				sb.append("<br/>");
			sb.append(this.getCustName());
		}
		if(StringUtils.isNotBlank(adapter.getCustName2())) { 
			sb.append("<br/>").append(adapter.getCustName2());
		}
		if(StringUtils.isNotBlank(adapter.getCustName3())) {
			sb.append("<br/>").append(adapter.getCustName3());
		}
		if(StringUtils.isNotBlank(adapter.getCustCityStateZip())) { 
			sb.append("<br/>").append(adapter.getCustCityStateZip());
		}
		if(StringUtils.isNotBlank(this.getCustCntry())) {
			sb.append("<br/>").append(this.getCustCntry());
		}
		if(StringUtils.isNotBlank(adapter.getQtCntctPhone())) { 
			sb.append("<br/>").append(adapter.getQtCntctPhone());
		} 
		return sb.toString();	
	}
	
    protected String getNewShipToCustomerAddress(CustPrtnrTabViewAdapter adapter){
    	ApplianceAddress shipToAdd = this.getShipToAddr();
    	if(null != shipToAdd){
    		StringBuffer sb = new StringBuffer();
    		if (StringUtils.isNotBlank(shipToAdd.getCntFirstName()) && StringUtils.isNotBlank(shipToAdd.getCntLastName())) {
    			sb.append(shipToAdd.getCntFirstName()).append("  ").append(shipToAdd.getCntLastName());
    		}
    		if (StringUtils.isNotBlank(shipToAdd.getCustName())) {   			
    			sb.append("<br/>").append(shipToAdd.getCustName());
    		}
    		if (StringUtils.isNotBlank(shipToAdd.getCustAddress()) && shipToAdd.isShowPGSInfor()) { 
    			sb.append("<br/>").append(shipToAdd.getCustAddress());
    		}
    		String custCityStateZip = this.getCustCityStateZipForShipToAddr(shipToAdd);
    		if (StringUtils.isNotBlank(custCityStateZip)) {
    			sb.append("<br/>").append(custCityStateZip);
    		}
    		if (StringUtils.isNotBlank(shipToAdd.getCntryCode())) {
    			sb.append("<br/>").append(this.getCountry(shipToAdd.getCntryCode()).getDesc());
    		}
    		if (StringUtils.isNotBlank(shipToAdd.getSapIntlPhoneNumFull())) {
    			sb.append("<br/>").append(shipToAdd.getSapIntlPhoneNumFull());
    		}
    		return sb.toString();
    	}
    	return this.getSoldToCustomerAddress(adapter);
    }
    
    private String getCustCityStateZipForShipToAddr(ApplianceAddress shipToAdd) {
        StringBuffer sb = new StringBuffer();
        Country cntryObj = this.getCountry(shipToAdd.getCntryCode());
		String cityCode = shipToAdd.getCustCity();
		String state = null;
		if(StringUtils.isNotBlank(shipToAdd.getSapRegionCode())){
			state = cntryObj.getStateDescription(StringUtils.trimToEmpty(shipToAdd.getSapRegionCode()));
		}
		String zipCode = shipToAdd.getPostalCode();
        if (StringUtils.isNotBlank(cityCode))
            sb.append(cityCode.trim());
        if (StringUtils.isNotBlank(state)) {
            if (sb.length() == 0)
                sb.append(state.trim());
            else
                sb.append(", "+state.trim());
        }
        if (StringUtils.isNotBlank(zipCode)) {
            if (sb.length() == 0)
                sb.append(zipCode.trim());
            else if (StringUtils.isNotBlank(state))
                sb.append( " " + zipCode.trim());
            else
                sb.append(", "+zipCode.trim());
        }
        return sb.toString();
    }
    
    protected String getNewShipToCompanyAddress(CustPrtnrTabViewAdapter adapter){
    	ApplianceAddress shipToAdd = this.getShipToAddr();
    	if(null != shipToAdd){
    		StringBuffer sb = new StringBuffer();
    		if (StringUtils.isNotBlank(shipToAdd.getCntFirstName()) && StringUtils.isNotBlank(shipToAdd.getCntLastName())) {
    			sb.append(shipToAdd.getCntFirstName()).append("  ").append(shipToAdd.getCntLastName());
    		}
    		if (StringUtils.isNotBlank(shipToAdd.getCustName())) {
    			if(sb.length() > 0)
    				sb.append("<br/>");
    			sb.append(shipToAdd.getCustName());
    		}
    		if (StringUtils.isNotBlank(adapter.getCustCityStateZip())) {
    			sb.append("<br/>").append(adapter.getCustCityStateZip());
    		}
    		if (StringUtils.isNotBlank(this.getCustCntry())) {
    			sb.append("<br/>").append(this.getCustCntry());
    		}
    		if (StringUtils.isNotBlank(shipToAdd.getSapIntlPhoneNumFull())) {
    			sb.append("<br/>").append(shipToAdd.getSapIntlPhoneNumFull());
    		}
    		return sb.toString();
    	}
    	return this.getSoldToCompanyAddress(adapter);
    }

	public int getSaasTermCondCatFlag() {
		return saasTermCondCatFlag;
	}

	public void setSaasTermCondCatFlag(int saasTermCondCatFlag) {
		this.saasTermCondCatFlag = saasTermCondCatFlag;
	}
	
	//14.1 SDD
	public boolean isShowDivestedMessageForPart(QuoteLineItem qli){
		boolean isShow = false;
		
		if (qli.isDivestedPart()){
			isShow = true;
		}
		
		return isShow;
	}
	
	public boolean isShowObsoleteMessageForPart(QuoteLineItem qli){
		boolean isShow = false;
		if(qli.isObsoletePart()&&!qli.isDivestedPart()){
			isShow = true;
		}
		return isShow;
	}
	
	/**
	 * set display flag for divested part.
	 * @return boolean. 0: has no message. 1: quote approved message. 2: quote undergoing message.
	 */
	public int showDivestedMessageForPpTab() {
		int flag = 0;
		if(header.isHasDivestedPart()) {
			if (header.getSpeclBidFlag() == 1) {
				if (isQuoteWithUndergoingApprovalStatus()) {
					flag = 2;
				} else {
					flag = 1;
				}
			} else {
				flag = 1;
			}
		}
		return flag;
	}

	private boolean isQuoteWithUndergoingApprovalStatus() {
		return header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR);
	}
	
	//monthly
	public List getMasterMonthlySwLineItemsList(){
		return quote.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems();
	}
	
	public boolean showMonthlyIncreaseBidTCV(MonthlySoftwareConfiguration configrtn) {
		if(configrtn.getIncreaseBidTCV() == null){
			return false;
		}else if(isActingAsTier2Reseller()){
			return ApplicationProperties.getInstance().getT2PriceAvailable();
		}else{
			return true;
		}
    }
	
	
	public String getMonthlyIncreaseBidTCV(MonthlySoftwareConfiguration configrtn) {
        if (showMonthlyIncreaseBidTCV(configrtn)) {
            if (!quote.getQuoteHeader().isSubmittedQuote()) {
                // if EstmtdOrdDate is null
                if (quote.getQuoteHeader().getEstmtdOrdDate() == null) {
                    return DraftQuoteConstants.N_A;
                }
            }
            return uiFormatter.formatEndCustomerPrice(configrtn.getIncreaseBidTCV().doubleValue());
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }
	
	public List getMonthlyLineItems(){
		return quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
	}
	
	public List<MonthlySoftwareConfiguration> getMonthlySoftwareConfigurations(){
		return quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
	}
	
	public Map getMonthlySwConfgrtnsMap(){
		return quote.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap();
	}

	public boolean isShowContainAddNotSignedMTHLYMsg() {
		if (cust.getPAIndCode() == 1 && header.isHasMonthlySoftPart()) {
			if (!cust.isMthlySwTermsAccptd()) {
				return true;
			}
		}
		return false;
	}
}
