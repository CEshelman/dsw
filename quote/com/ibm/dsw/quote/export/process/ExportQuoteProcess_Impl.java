package com.ibm.dsw.quote.export.process;

import is.domainx.User;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.CountryFactory;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ChargeableUnit;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.PartGroup;
import com.ibm.dsw.quote.common.domain.QuotePriceTotals;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.draftquote.util.builder.DraftQuoteBuilder;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTable;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTableTotal;
import com.ibm.dsw.quote.draftquote.viewbean.helper.SoftwareMaintenance;
import com.ibm.dsw.quote.export.ExportExecSummaryUtil;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;
import com.ibm.dsw.quote.export.exception.InvildPartQtyException;
import com.ibm.dsw.quote.findquote.config.FindQuoteActionKeys;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetMonthlyItemToSaasProxy;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetPart;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuote;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuoteConfigurationHelper;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetUtil;
import com.ibm.dsw.quote.submittedquote.domain.SubmittedQuoteAccess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.dsw.quote.submittedquote.util.builder.SubmittedQuoteBuilder;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SubmittedNoteRow;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SubmittedPartTable;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ExportQuoteProcess_Impl.java</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-11
 */
public abstract class ExportQuoteProcess_Impl extends TopazTransactionalProcess implements ExportQuoteProcess {
    
    //Date format
	public static final String dateFromat = "yyyy-MM-dd";

	//Date format on RTF
	public static final String RTFDateFromat = "dd-MMM-yyyy";

	//Anniversary format
	public static final String mouthFromat = "MMMM";

	private Quote quote = null;

	private QuoteProcess qProcess = null;
	
	private User user = null;
	
	private boolean isPricingCallFailed = false;
	
	private boolean isPGSFlag = false;
	
	private String FCT_TO_PA_MGRTN_QT_KEY = "fct_to_pa_mgrtn_qt";
	
	private boolean tier2ResellerFlag = false;
	
	public String exportUnderEvalQuoteAsSpreadSheet(OutputStream os, User user, QuoteUserSession quoteUserSession, String webQuoteNum) throws ExportQuoteException,QuoteException
	{
		this.user = user;
	    this.tier2ResellerFlag = QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO.equalsIgnoreCase(quoteUserSession.getBpTierModel());
		Quote quote = null;
		isPGSFlag = QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode());
		String up2ReportingUserIds = quoteUserSession == null ? null : quoteUserSession.getUp2ReportingUserIds();
		String userId = quoteUserSession.getUserId();
		
		quote = getUnderEvalQuoteInfo(webQuoteNum, userId, up2ReportingUserIds);
	
		SpreadSheetQuote epQuote = this.exportQuote(quote, user, true);
		
		if(quote.getQuoteHeader().isPAQuote())
			epQuote = populateSupportingData(epQuote, userId, webQuoteNum);
		
		SpreadSheetUtil.singleton().exportToExcel(epQuote, os);
				
		return epQuote.getWebQuoteNum();
	}
	
	public String exportQuoteAsSpreadSheet(OutputStream os, User user, QuoteUserSession quoteUserSession) throws ExportQuoteException, QuoteException {
	    this.user = user;
	    this.tier2ResellerFlag = QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO.equalsIgnoreCase(quoteUserSession.getBpTierModel());
		String creatorID = StringUtils.lowerCase(user.getEmail());
		
		Quote quote = null;
		isPGSFlag = QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode());
		String up2ReportingUserIds = quoteUserSession == null ? null : quoteUserSession.getUp2ReportingUserIds();
		String userId = quoteUserSession.getUserId();
		
		if(isPGSFlag) {
			creatorID = userId;
			quote = getQuoteInfo("", up2ReportingUserIds, userId, isPGSFlag);
		} else {
			quote = getQuoteInfo(null, null, creatorID);
		}
	
		SpreadSheetQuote epQuote = this.exportQuote(quote, user, true);
		
		if(quote.getQuoteHeader().isPAQuote())
			epQuote = populateSupportingData(epQuote,creatorID);
		
		SpreadSheetUtil.singleton().exportToExcel(epQuote, os);
				
		return epQuote.getWebQuoteNum();
	}

	public String exportSubmittedQuoteAsSpreadSheet(OutputStream os, String webQuoteNum, String quoteUserSession, User user) throws ExportQuoteException, QuoteException {
		 LogContext logger = LogContextFactory.singleton().getLogContext();
		    
		 this.user = user;
		    
		 String creatorID =  StringUtils.lowerCase(user.getEmail());
		
		 Quote quote = getQuoteInfo(webQuoteNum, quoteUserSession, creatorID);
		
		 SpreadSheetQuote epQuote = this.exportQuote(quote, user, false);
		
		 epQuote = populateQuoteInfoForSubmittedQuoteExcel(quote, epQuote);
		 
		 SpreadSheetUtil.singleton().exportToExcel(epQuote, os);
			
		 return epQuote.getWebQuoteNum();
	}
	
	public String exportUnderEvalQuoteAsNativeExcel(OutputStream os, User user, QuoteUserSession quoteUserSession, String webQuoteNum) throws ExportQuoteException,QuoteException
	{
		this.user = user;
		String up2ReportingUserIds = quoteUserSession == null ? null : quoteUserSession.getUp2ReportingUserIds();
		String creatorID = StringUtils.lowerCase(user.getEmail());
		
		Quote quote = this.getUnderEvalQuoteInfo(webQuoteNum, creatorID, up2ReportingUserIds);
	
		SpreadSheetQuote epQuote = this.exportQuote(quote, user, true);
		
		if(quote.getQuoteHeader().isPAQuote())
			epQuote = populateSupportingData(epQuote,creatorID);
		
		SpreadSheetUtil.singleton().exportToNativeExcel(epQuote, os);
				
		return epQuote.getWebQuoteNum();
	}
	
	public String exportQuoteAsNativeExcel(OutputStream os, User user) throws ExportQuoteException, QuoteException {
	    this.user = user;
	    
		String creatorID = StringUtils.lowerCase(user.getEmail());
		
		Quote quote = getQuoteInfo(null, null, creatorID);
	
		SpreadSheetQuote epQuote = this.exportQuote(quote, user, true);
		
		if(quote.getQuoteHeader().isPAQuote())
			epQuote = populateSupportingData(epQuote,creatorID);
		
		SpreadSheetUtil.singleton().exportToNativeExcel(epQuote, os);
				
		return epQuote.getWebQuoteNum();
	}
	
	public String exportSubmittedQuoteAsNativeExcel(OutputStream os, String webQuoteNum, QuoteUserSession quoteUserSession, User user) throws ExportQuoteException, QuoteException {
		 LogContext logger = LogContextFactory.singleton().getLogContext();
		 this.tier2ResellerFlag = QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO.equalsIgnoreCase(quoteUserSession.getBpTierModel());   
		 
		 this.user = user;
		 
		 Quote quote = null;
		 String creatorID =  StringUtils.lowerCase(user.getEmail());
		 String up2ReportingUserIds = quoteUserSession == null ? null	: quoteUserSession.getUp2ReportingUserIds();
		 isPGSFlag = QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession == null ? "":quoteUserSession.getAudienceCode());
		 String userId = quoteUserSession.getUserId();
		 
		 if(isPGSFlag) {
			creatorID = userId;
			quote = getQuoteInfo(webQuoteNum, up2ReportingUserIds, userId, isPGSFlag);
		 } else {
			quote = getQuoteInfo(webQuoteNum, up2ReportingUserIds, creatorID);
		 }
		 
		 SpreadSheetQuote epQuote = this.exportQuote(quote, user, true);
		
		 epQuote = populateQuoteInfoForSubmittedQuoteExcel(quote, epQuote);
		 
		 SpreadSheetUtil.singleton().exportToNativeExcel(epQuote, os);
		 	
		 return epQuote.getWebQuoteNum();
	}
	
	public boolean isHAActingAsTier2Only(QuoteUserSession quoteUserSession, Quote quote){

    	if(quoteUserSession.getSiteNumber() != null // in PGS application
    			&& quote.getPayer() != null 
    			&& quoteUserSession.isHouseAccountFlag() // is house account 
    			&& !quoteUserSession.getSiteNumber().equalsIgnoreCase(quote.getPayer().getCustNum())){// current house account is not quote distributor
    		return true;
    	}

    	return false;
	}

	public String exportQuoteAsRTF(OutputStream os, String webQuoteNum, QuoteUserSession quoteUserSession, Boolean isPGSFlag, Boolean isTier2Reseller, String downloadPricingType, User user, String userId) throws ExportQuoteException, QuoteException {
		
	    LogContext logger = LogContextFactory.singleton().getLogContext();
	    this.isPGSFlag = QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession == null ? "":quoteUserSession.getAudienceCode());
	    this.user = user;
	    
	  //Export pricing type for PGS
		if (downloadPricingType == null){
			downloadPricingType = "";
		}
		
		String up2ReportingUserIds = quoteUserSession == null ? null : quoteUserSession.getUp2ReportingUserIds();
		
		Quote quote = getQuoteInfo(webQuoteNum, up2ReportingUserIds, userId, isPGSFlag);
		
		this.tier2ResellerFlag = isTier2Reseller || isHAActingAsTier2Only(quoteUserSession, quote);

		SpreadSheetQuote epQuote = null;
		
		epQuote = this.exportQuote(quote, user, isQuoteHasSaas(quote), downloadPricingType);
		
		epQuote.setDownloadPricingType(downloadPricingType);
		epQuote.setTier2ResellerFlag(isTier2Reseller);
		epQuote.setBpRelatedCust(quote.getQuoteHeader().isBPRelatedCust());
		
		SpreadSheetUtil.singleton().exportToRTF(epQuote, os);
		
		return epQuote.getWebQuoteNum();
	}

	public String exportQuoteExecSummaryAsRTF(OutputStream os, String webQuoteNum, String up2ReportingUserIds, String userId, User user, Locale locale , TimeZone timezone) throws ExportQuoteException, QuoteException {
		
	    LogContext logger = LogContextFactory.singleton().getLogContext();
	    
	    QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
	    Quote quote = null;
	    try{
	    	quote = qProcess.getSubmittedQuoteBaseInfo(webQuoteNum, userId, up2ReportingUserIds);
	    }catch(NoDataException nde){
	    	throw new QuoteException("Cannot find quote with webQuoteNum: "+webQuoteNum);
	    }
	    qProcess.getQuoteDetailForExecSummaryTab(quote);
	    
	    SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
		 
		process.getExecPartPriceInfo(quote, user);
		process.getResellerAndDistributor(quote);

		SpreadSheetQuote epQuote = this.exportQuote(quote, user, isQuoteHasSaas(quote));
		populateResellerAndDistributorInfo(quote,epQuote);
		
	    ExportExecSummaryUtil.exportToRTF(quote,epQuote, os, locale, timezone);
	    
		return webQuoteNum;
	}
	
	public String exportQuoteExecSummaryAsPDF(OutputStream os, String webQuoteNum, String up2ReportingUserIds, String userId, User user, Locale locale , TimeZone timezone) throws ExportQuoteException, QuoteException {
		
	    LogContext logger = LogContextFactory.singleton().getLogContext();
	    
	    QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
	    Quote quote = null;
	    try{
	    	quote = qProcess.getSubmittedQuoteBaseInfo(webQuoteNum, userId, up2ReportingUserIds);
	    }catch(NoDataException nde){
	    	throw new QuoteException("Cannot find quote with webQuoteNum: "+webQuoteNum);
	    }
	    qProcess.getQuoteDetailForExecSummaryTab(quote);
	    
	    SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
		 
		process.getExecPartPriceInfo(quote, user);
		process.getResellerAndDistributor(quote);

		SpreadSheetQuote epQuote = this.exportQuote(quote, user, isQuoteHasSaas(quote));
		populateResellerAndDistributorInfo(quote,epQuote);
		
	    ExportExecSummaryUtil.exportToPDF(quote,epQuote, os, locale, timezone);
	    
		return webQuoteNum;
	}
	
	private Quote getQuoteInfo(String webQuoteNum, String quoteUserSession, String creatorID) throws ExportQuoteException {
		//For SQO use
		return getQuoteInfo(webQuoteNum, quoteUserSession, creatorID, false);
	}
	
	private Quote getUnderEvalQuoteInfo(String webQuoteNum, String userId, String up2ReportingUserIds)throws ExportQuoteException
	{
		LogContext logger = LogContextFactory.singleton().getLogContext();
		logger.debug(this, "get under eval quote for export: " + webQuoteNum + ":" + userId + ":" + up2ReportingUserIds);
		Quote quote = null;
		try {
		 	this.beginTransaction();
		 	QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
		 	QuoteUserAccess quoteUserAccess = qProcess.getQuoteUserAccess(webQuoteNum, userId, up2ReportingUserIds);
		 	if ( !quoteUserAccess.isCanViewQuote() )
		 	{
		 		throw new ExportQuoteException("quote " + webQuoteNum + ":" + userId + ", not exists or can't view");
		 	}
		 	quote = qProcess.getDraftQuoteBaseInfoByQuoteNum(webQuoteNum);
		 	
		 	if ( !quote.getQuoteHeader().isUnderEvaluation() )
		 	{
		 		throw new ExportQuoteException("quote is not in status of under evaluation:" + webQuoteNum);
		 	}
		 	qProcess.getQuoteDetailForCustTab(quote);
			
			// the build methods will retrieve quote line item info 
			
		 	 DraftQuoteBuilder draftBuilder = DraftQuoteBuilder.create(quote, user.getEmail());
			 draftBuilder.build();
			 isPricingCallFailed = draftBuilder.isSapCallFailed();
		 	this.commitTransaction();
		} 
		catch (ExportQuoteException e )
		{
			throw e;
		}
		catch (Exception e) {
			LogThrowableUtil.getStackTraceContent(e);
			throw new ExportQuoteException(e);
		} finally {
	        this.rollbackTransaction();
	    }
		return quote;
	}
	
	private Quote getQuoteInfo(String webQuoteNum, String quoteUserSession, String creatorID, Boolean isPGSFlag) throws ExportQuoteException {
		LogContext logger = LogContextFactory.singleton().getLogContext();
		//No need to lower case for creatorID in PGS
		if (!isPGSFlag){
			creatorID = StringUtils.lowerCase(creatorID);
		}
		Quote quote = null;

		if (StringUtils.isBlank(creatorID) && StringUtils.isBlank(webQuoteNum)) {
			throw new ExportQuoteException("Can not export quote, creatorId and webQuoteNum are NULL");
		} else {
			logger.debug(this, " webQuoteNum " + webQuoteNum);
			logger.debug(this, " quoteUserSession " + quoteUserSession);
			logger.debug(this, " creatorID " + creatorID);
		}
		
		//Submitted Quote is identified by web quote num other than creatorId.
		boolean isSubmittedQuote = StringUtils.isNotBlank(webQuoteNum);
		 
		 try {
		 	this.beginTransaction();
		 	QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
		 	
			if (isSubmittedQuote) {
				quote = qProcess.getSubmittedQuoteBaseInfo(webQuoteNum, creatorID, quoteUserSession);
			} else {
				quote = qProcess.getDraftQuoteBaseInfo(creatorID);
			}
			
			qProcess.getQuoteDetailForCustTab(quote);
			
			// the build methods will retrieve quote line item info 
			if (isSubmittedQuote) {
			    SubmittedQuoteBuilder.create(quote,user).build();
			} else {
			    DraftQuoteBuilder draftBuilder = DraftQuoteBuilder.create(quote, user.getEmail());
			    draftBuilder.build();
			    isPricingCallFailed = draftBuilder.isSapCallFailed();
			}
			
			this.commitTransaction();
		} catch (Exception e) {
			LogThrowableUtil.getStackTraceContent(e);
			throw new ExportQuoteException(e);
		} finally {
	        this.rollbackTransaction();
	    }
		return quote;
	}
	
	public SpreadSheetQuote exportQuote(Quote quote, User user, boolean inlucdeSaaSParts) throws ExportQuoteException, QuoteException {
		return exportQuote(quote, user, inlucdeSaaSParts, "");
	}
	
	public SpreadSheetQuote exportQuote(Quote quote, User user, boolean inlucdeSaaSParts, String downloadPricingType) throws ExportQuoteException, QuoteException {
	    this.user = user;
		SpreadSheetQuote epQuote = new SpreadSheetQuote();
		epQuote = this.populateQuoteInfo(quote, epQuote);
		epQuote = this.populateLineItemInfo(quote, epQuote, downloadPricingType);
		if (inlucdeSaaSParts){
			epQuote = this.populateConfigrationsInfo(quote, epQuote, downloadPricingType);
		}
		return epQuote;
	}
	
	
	/**
	 * @param isSubmitter 
	 * @param webQuoteNum
	 * @throws QuoteException
	 * @throws NoDataException
	 */
	protected SpreadSheetQuote populateConfigrationsInfo(Quote quote, SpreadSheetQuote epQuote, String downloadPricingType) throws ExportQuoteException, QuoteException {
		QuoteHeader header = quote.getQuoteHeader();
		PartPriceCommon partTable = null;
		if(header.isSubmittedQuote()){
  	      	partTable = new SubmittedPartTable(quote);
  	      }else{
  	    	partTable = new PartTable(quote);
  	      }

		//14.2 MonthlyLicensing: Andy updated to compatibility of both SaaS and Monthly
		SpreadSheetQuoteConfigurationHelper.preparePartsPricingConfigrtns(quote, epQuote);
		
		List configrtnsList = epQuote.getPartsPricingConfigrtnsList();
		
		Map configrtnsMap = epQuote.getPartsPricingConfigrtnsMap();
		double bpExtTotalPrice = epQuote.getBpExtTotalPrice();
		boolean repeatedFlag = false;
		
		if (configrtnsList != null && configrtnsList.size() > 0) {
	    	Iterator configrtnsIt = configrtnsList.iterator();
			while(configrtnsIt.hasNext()){
				PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt.next();
				
				epQuote.getPartsPricingConfigurationMap().put(ppc.getConfigrtnId(), ppc);
				if(configrtnsMap.get(ppc) == null){
					//System.out.println("ExportQuoteProcess_Impl: Cannot find entry with quoteconfiguration.");
					continue;
				}
				List subSaaSlineItemList = (List)configrtnsMap.get(ppc);
				List subPartList = new ArrayList();
	            for (int i = 0; i < subSaaSlineItemList.size(); i++) {
	            	QuoteLineItem  item = (QuoteLineItem) subSaaSlineItemList.get(i);
	            	SpreadSheetPart epPart = new SpreadSheetPart();
	            	epPart.setTier2ResellerFlag(tier2ResellerFlag);
	            	epPart.setSubmittedQuoteFlag(epQuote.isSubmittedQuoteFlag());
	            	epPart.setDownloadPricingType(downloadPricingType);
	            	String bpLineItemPrice = partTable.getLineItemBpTCV(item, isPricingCallFailed);
	            	if (bpLineItemPrice == null || bpLineItemPrice.equals("")){
	            		epPart.setBpLineItemPrice(0d);
	            	} else {
	            		epPart.setBpLineItemPrice(Double.parseDouble(bpLineItemPrice.replace(",", "")));
	            	}
	            	if(partTable.showRepacePartsLabel(subSaaSlineItemList, i)){
	            		epPart.setServicesAgreementNum(header.getRefDocNum());
	    			}
	            	epPart.setShowReplaceTitle(item.isReplacedPart());
	            	//has repeated replaced section
	            	if(item.isReplacedPart() && repeatedFlag){
	            		epPart.setRepeatedReplaceTitle(true);
	            	}else{
	            		epPart.setRepeatedReplaceTitle(false);
	            	}
	            	repeatedFlag = item.isReplacedPart() ? true : false;
	    			epPart = populateLineItem(quote, epQuote, item, epPart, partTable, downloadPricingType);
	    			if(epPart!=null){
	    				subPartList.add(epPart);
	    			}
	    			bpExtTotalPrice = bpExtTotalPrice + epPart.getChannelExtPrice();
	            }
	            epQuote.getPartsPricingConfigrtnsMap().put(ppc, subPartList);
			}
	    }
		
		for(int i=0; i<epQuote.getSaaSEqPartList().size(); i++) {
			SpreadSheetPart epPart = (SpreadSheetPart) epQuote.getSaaSEqPartList().get(i);
			PartsPricingConfiguration ppc = (PartsPricingConfiguration)epQuote.getPartsPricingConfigurationMap()
															.get(epPart.getConfigurationId());
			if(ppc != null) {
				if(header.getRefDocNum() != null) {
					epPart.setRefDocNum(header.getRefDocNum());
				}
				if(ppc.getConfigrtnErrCode() != null) {
					epPart.setErrorCode(ppc.getConfigrtnErrCode());
				}
				if(ppc.getConfigrtrConfigrtnId() != null) {
					epPart.setConfigrtnAction(ppc.getConfigrtrConfigrtnId());
				}
				if(ppc.getEndDate() != null) {
					epPart.setConfigrationEndDate(ppc.getEndDate().toString());
				}
				if(ppc.getCotermConfigrtnId() != null) {
					epPart.setCoTermToConfigrtnId(ppc.getCotermConfigrtnId());
				}
				if(ppc.isConfigrtnOvrrdn()) {
					epPart.setOverrideFlag(ppc.isConfigrtnOvrrdn());
				}
				if(ppc.getConfigrtnActionCode() != null) {
					epPart.setConfigrtnAction(ppc.getConfigrtnActionCode());
				}
				if(ppc.getProvisioningId() != null) {
					epPart.setProvisioningId(ppc.getProvisioningId());
				}
			}
			//epQuote.getSaaSEqPartList().add(epPart);
		}
		
	    epQuote.setBpExtTotalPrice(bpExtTotalPrice);
	    return epQuote;
	}
	
	protected SpreadSheetPart populateLineItem(Quote quote, SpreadSheetQuote epQuote, QuoteLineItem item, SpreadSheetPart epPart, PartPriceCommon partTable) throws ExportQuoteException, QuoteException {
		return populateLineItem(quote, epQuote, item, epPart, partTable, "");
	}
	protected SpreadSheetPart populateLineItem(Quote quote, SpreadSheetQuote epQuote, QuoteLineItem item, SpreadSheetPart epPart, PartPriceCommon partTable, String downloadPricingType) throws ExportQuoteException, QuoteException {
		LogContext logger = LogContextFactory.singleton().getLogContext();
		
		//Monthly Software line item align to SaaS
		//14.2 Andy
		if(item instanceof MonthlySwLineItem){
			item = SpreadSheetMonthlyItemToSaasProxy.createProx((MonthlySwLineItem)item);
		}
		
		QuoteHeader header = quote.getQuoteHeader();
		epPart.setScale(epQuote.getScale());
		epPart.setLobCode(epQuote.getLobCode());
		epPart.setEpPartNumber(item.getPartNum());
		epPart.setEpPartDesc(item.getPartDesc());
		// set Brand
		epPart.setEpBrandDesc(item.getSwProdBrandCodeDesc());
		epPart.setExportRestricted(item.isExportRestricted());
		epPart.setObsoletePart(item.isObsoletePart());
		epPart.setProgramDesc(StringUtils.trimToEmpty(item.getIbmProgCodeDscr()));
		epPart.setSubmittedQuoteFlag(epQuote.isSubmittedQuoteFlag());
		epPart.setTier2ResellerFlag(tier2ResellerFlag);
		
		//release 14.1
		//add by  Karl 28/8/2013
		if(partTable.showEquityCurveFlag(item)){
				//indicate whether the EC was calculated
				boolean ecCalculated = partTable.showEquityCurveCalculateFlag(item);
				boolean isApJapan = partTable.showChannelApJapan();
				String NA = "N/A";
				if(ecCalculated){
					// top performer
					epPart.setPreferDiscount(partTable.getPreferDiscount(item));
					epPart.setPreferBidUnitPrice(isApJapan ? "" : partTable.getPreferBidUnitPrice(item));
					epPart.setPreferBidExtendedPrice(partTable.getPreferBidExtendedPrice(item));
					//market average
					epPart.setMaxDiscount(partTable.getMaxDiscount(item));
					epPart.setMaxBidUnitPrice(isApJapan ? "" : partTable.getMaxBidUnitPrice(item));
					epPart.setMaxBidExendedPrice(partTable.getMaxBidExendedPrice(item));
					if(partTable.showPurchaseHistAppliedFlagWithTopPerformer(item) 
							||partTable.showPurchaseHistAppliedFlagWithMarketAverage(item))
						epPart.setPriorCustomerPurchase("Y");
					else
						epPart.setPriorCustomerPurchase("N");
				}
				else{
					// top performer
					epPart.setPreferDiscount(NA);
					epPart.setPreferBidUnitPrice(NA);
					epPart.setPreferBidExtendedPrice(NA);
					//market average
					epPart.setMaxDiscount(NA);
					epPart.setMaxBidUnitPrice(NA);
					epPart.setMaxBidExendedPrice(NA);
					epPart.setPriorCustomerPurchase(NA);
				}
			//add by  Karl 28/8/2013 end
		}
		
		
		Integer partQty = item.getPartQty();
		if (partQty == null) {
			if((item.isSaasPart() || item.isMonthlySoftwarePart()) && (!partTable.showSaaSQuantityField(item))){
				epPart.setNoQty(true);
			}else{
				throw new InvildPartQtyException("can not export: " + item.getPartNum() + " invalid part num " + partQty);
			}
		} else if (partQty.intValue() == 0) {
			return null; //Not export any zero qty part
		} else {
			if(partTable.showNAQty(item)){
				epPart.setEpQuantity("N/A");
			}else{
				epPart.setEpQuantity(String.valueOf(item.getPartQty()));
			}
		}	            
		
		//ChargeUnit is for RTF download only
		StringBuffer chgUnitsBuf = new StringBuffer();
		ChargeableUnit[] chgUnits = item.getChargeableUnits();
		for(int i = 0 ; i< chgUnits.length; i++) {
			ChargeableUnit chgUnit = (ChargeableUnit)chgUnits[i];
			if(chgUnit.getDesc() == null){
				continue;
			}
			chgUnitsBuf.append(chgUnit.getDesc());
			if(i<(chgUnits.length - 1))
				chgUnitsBuf.append(", ");
		}
		epPart.setChargeUnit(StringUtils.trimToEmpty(chgUnitsBuf.toString()));
		
		//bid unit/ext price is showing on RTF, but not on excel
		if(item.isSaasPart()){
			if(partTable.showItemTotContractVal(item)){
				epPart.setBidExtPrice(item.getSaasBidTCV() == null? 0d : item.getSaasBidTCV().doubleValue());
				epPart.setShowBidTCVFlag(true);
			}else if(partTable.showSaaSBidExtPrice(item)){
				epPart.setBidExtPrice(item.getLocalExtProratedDiscPrc() == null? 0d : item.getLocalExtProratedDiscPrc().doubleValue());
				epPart.setShowBidTCVFlag(true);
			}
		}else{
				epPart.setBidExtPrice(item.getLocalExtProratedDiscPrc() == null? 0d : item.getLocalExtProratedDiscPrc().doubleValue());
			}
		
		epPart.setTotalPoints(item.getContributionExtPts());
		
		if(item.isSaasPart()){
			if(partTable.showItemTotContractVal(item)){
				epPart.setTotalPrice(item.getSaasBidTCV() == null? 0d : item.getSaasBidTCV().doubleValue());
			}else if(partTable.showSaaSBidExtPrice(item)){
				epPart.setTotalPrice(item.getLocalExtPrc()==null? 0d : item.getLocalExtPrc().doubleValue());
			}
		}else{
			epPart.setTotalPrice(item.getLocalExtPrc()==null? 0d : item.getLocalExtPrc().doubleValue());
		}
		epPart.setPricingCallFailed(isPricingCallFailed);
		
		//added in dsw9.4 fit as can
		epPart.setEpAddtnlYearCvrageSeqNum(item.getAddtnlYearCvrageSeqNum());
		epPart.setEpSortSeqNum(item.getManualSortSeqNum());
		epPart.setEpTotalPrice(item.getLocalExtPrc()==null? 0d : item.getLocalExtPrc().doubleValue());
		if(item.isSaasPart()){
			if(partTable.showSaaSBidUnitPrice(item)){
				epPart.setBidUnitPrice(item.getLocalUnitProratedDiscPrc() == null? 0d : item.getLocalUnitProratedDiscPrc().doubleValue());
			}else if(partTable.showItemTotContractVal(item) && partTable.showSaaSBidExtPrice(item) && item.isSaasSubscrptnPart()){
				epPart.setBidUnitPrice(item.getLocalExtProratedDiscPrc() == null? 0d : item.getLocalExtProratedDiscPrc().doubleValue());
			}
		}else{
			epPart.setBidUnitPrice(item.getLocalUnitProratedDiscPrc() == null? 0d : item.getLocalUnitProratedDiscPrc().doubleValue());
		}
		if(item.isSaasPart()){
			if(partTable.showItemTotContractVal(item)){
				epPart.setEntitledExtPrice(item.getSaasEntitledTCV()==null?0d:item.getSaasEntitledTCV().doubleValue());
				epPart.setShowEntitledTCVFlag(true);
			}else if(partTable.showSaaSEntitledExtPrice(item)){
				epPart.setEntitledExtPrice(item.getLocalExtProratedPrc()==null?0d:item.getLocalExtProratedPrc().doubleValue());
				epPart.setShowEntitledTCVFlag(true);
			}
		}else{
			epPart.setEntitledExtPrice(item.getLocalExtProratedPrc()==null?0d:item.getLocalExtProratedPrc().doubleValue());
		}
		epPart.setEpBPStdDiscPct(item.getChnlStdDiscPct()==null?0d:Double.parseDouble(DecimalUtil.formatTo5Number(item.getChnlStdDiscPct().doubleValue()/100)));
		epPart.setEpBPOvrrdDiscPct(item.getChnlOvrrdDiscPct()==null?0d:Double.parseDouble(DecimalUtil.formatTo5Number(item.getChnlOvrrdDiscPct().doubleValue()/100)));
		epPart.setEpTotalLineDiscount(item.getTotDiscPct()==null?0d:Double.parseDouble(DecimalUtil.formatTo5Number(item.getTotDiscPct().doubleValue()/100)));
		
		if(item.getChnlOvrrdDiscPct()!=null){
			epPart.setBPDiscOvrrdFlag(true);
		}
		epPart.setQuoteRTFBPDiscount();
		
		epPart.setChannelExtPrice(item.getChannelExtndPrice()==null?0d:item.getChannelExtndPrice().doubleValue());
		epPart.setChannelUnitPrice(item.getChannelUnitPrice()==null?0d:item.getChannelUnitPrice().doubleValue());
		
		//pro-ration option is available on RTF but does not be exported to excel
		if(item.getPartDispAttr() !=null) {
			int prorationMonths = DateUtil.calculateFullCalendarMonths(item.getMaintStartDateForExport(),item.getMaintEndDateForExport());
			boolean isRenewalPart = StringUtils.isNotBlank(item.getRenewalQuoteNum());
			boolean isManitPart = item.getPartDispAttr().isMaintBehavior();
			epPart.setpProrateFlag((prorationMonths!=-1) 
					&& (prorationMonths<12) 
					&& (isManitPart || isRenewalPart));
		}
		
		//Zero additional year for CMRE48 Fix
		epPart.setEpAddYears(String.valueOf(item.getAddtnlMaintCvrageQty()));
		
		if(header.isPAQuote()) {
			String prorated = item.getProrateFlag()? "Yes" : "No";
			epPart.setEpMaintenanceProrated(prorated);
		}
		
		if(item.getPartDispAttr() != null && item.getPartDispAttr().isFtlPart()){
			epPart.setIsFtlPart(true);
			
			int proratedWeeks = item.getProrateWeeks();
			if(proratedWeeks > 0 && proratedWeeks < 52){
				epPart.setProratedWeeks(String.valueOf(proratedWeeks));
			}
			
		} else {
			epPart.setIsFtlPart(false);
			
			int prorateMonths = item.getProrateMonths();
			if(prorateMonths > 0 && prorateMonths < 12){
				epPart.setProratedMonths(prorateMonths); 
			}
		}
		
		epPart.setRenewalLineItemSeqNum(String.valueOf(item.getRenewalQuoteSeqNum()));
		epPart.setRenewalQuoteNumber(item.getRenewalQuoteNum());
		
		
		if(header.isPAQuote() || header.isPAEQuote()) {
			epPart.setEpItemPoints(item.getContributionUnitPts());
		}
		
		epPart.setLocalUnitPrc(item.getLocalUnitPrc() == null? 0d: item.getLocalUnitPrc().doubleValue());
		if(item.isSaasPart()){
			if(partTable.showSaaSEntitledUnitPrice(item)){
				epPart.setEpItemPrice(item.getLocalUnitProratedPrc());
			}else if(partTable.showItemTotContractVal(item) && partTable.showSaaSEntitledExtPrice(item) && item.isSaasSubscrptnPart()){ 
				epPart.setEpItemPrice(item.getLocalExtProratedPrc()==null?0d:item.getLocalExtProratedPrc().doubleValue());
			}
		}else{
			epPart.setEpItemPrice(item.getLocalUnitProratedPrc());
		}
		
		if(item.isSaasPart()) {
			epPart.setEpEnterOnlyOneOverridePrice(partTable.getOverridePrcVal(item)
					.replaceAll(",", ""));
		} else {
			epPart.setEpEnterOnlyOneOverridePrice(item.getOverrideUnitPrc() == null? "" : item.getOverrideUnitPrc().toString());
		}
		epPart.setEpEnterOnlyOneDiscountPercent(Double.parseDouble(DecimalUtil.formatTo5Number(item.getLineDiscPct()/100)));
		
		if(item.isSaasPart()) {
			if(disableOverrideUnitPriceInput(item, quote) || 
					!(partTable.canOvrrdUnitPrice(item) || partTable.canOvrrdExtPrice(item))) {
				epPart.setDisableOverrideUnitPriceInput(true);
			}
			
			//check replaced part
			epPart.setReplacedPartFlag(item.isReplacedPart());
		}
		
		int scale = QuoteCommonUtil.getPartPriceRoundingFactor(quote.getQuoteHeader(), item);
		if(scale == 4){
			epPart.setDecimal4Flag(true);
		}
		epPart.setUsagePart(PartPriceSaaSPartConfigFactory.singleton().isUsagePart(item)); 
		
		if(epQuote.isPA()) {
			epPart.setEpTotalPoints(item.getContributionExtPts());
		}
		epPart.setPartType(item.getPartTypeCode());
		
		//Process date 
		SoftwareMaintenance sm = new SoftwareMaintenance(quote);
		epPart.setShowSoftwareMaintFlag(sm.showSoftwareMaintenanceCoverage(item));
		
		if(item.getPartDispAttr().isLicenseBehavior()){
			epPart.setShowStartDateFlag(item.getStartDtOvrrdFlg());
			epPart.setShowEndDateFlag(true);
		} else {
			epPart.setShowStartDateFlag(true);
			epPart.setShowEndDateFlag(true);
		}
		
		logger.debug(this, "StdStartDate " + item.getPartDispAttr().getStdStartDateForExport());
		logger.debug(this, "StdEndDate " + item.getPartDispAttr().getStdEndDateForExport());
		logger.debug(this, "StartDtOvrrdFlg " + item.getStartDtOvrrdFlg());
		logger.debug(this, "EndDtOvrrdFlg " + item.getEndDtOvrrdFlg());
		logger.debug(this, "ovrStartDate " + item.getMaintStartDateForExport());
		logger.debug(this, "ovrEndDate " + item.getMaintEndDateForExport());
		
		//process STD/override start/end date
		String startDate = DateHelper.getDateByFormat(item.getPartDispAttr().getStdStartDateForExport(), dateFromat);
		String endDate = DateHelper.getDateByFormat(item.getPartDispAttr().getStdEndDateForExport(), dateFromat);
		epPart.setEpSTDStartDate(startDate);
		epPart.setEpSTDEndDate(endDate);
		
		if (item.getStartDtOvrrdFlg()) {
			String epovrStartDate = DateHelper.getDateByFormat(item.getMaintStartDateForExport(), dateFromat);
			epPart.setEpOverrideDatesStartDate(epovrStartDate);
		}
		
		if (item.getEndDtOvrrdFlg()) {
			String epovrEndDate = DateHelper.getDateByFormat(item.getMaintEndDateForExport(), dateFromat);
			epPart.setEpOverideDatesEndDate(epovrEndDate);
		}
		
		//always export the maint start dates for RTF
		epPart.setRTFStartDate(DateHelper.getDateByFormat(item.getMaintStartDateForExport(), RTFDateFromat));
		
		//always export the maint end dates for RTF
		epPart.setRTFEndDate(DateHelper.getDateByFormat(item.getMaintEndDateForExport(), RTFDateFromat));
		
		epPart.setControlledCodeDesc(StringUtils.trimToEmpty(item.getControlledCodeDesc()));
		
		//10.4 SaaS requirement
		epPart.setShowBillingFrequencyFlag(partTable.showBillingFrequency(item));
		epPart.setShowTermSelectionFlag(partTable.showTermSelection(item));
		epPart.setShowItemTotContractVal(partTable.showItemTotContractVal(item));
		epPart.setShowUpToSelection(partTable.showUpToSelection(item));
		if (item.isSaasPart()) {
			epPart.setBrandCode(item.getSwProdBrandCode());
		}
		
		//set part type
		epPart.setPartTypeDsc(item.getPartTypeDsc());
		/*
		List groups = item.getPartGroups();
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<groups.size();i++){
            PartGroup group = (PartGroup)groups.get(i);
            if(i >0 && i != groups.size()-1){
                buffer.append("; ");                
            }
            buffer.append(group.getGroupName());
        }
        epPart.setSubmittedGroupName(buffer.toString());
        */
		
		if(header.isSubmittedQuote()) {
			epPart.setShowTotalPointsFlag((header.isPAQuote() || header.isPAEQuote()) 
					&& PartPriceConstants.PartTypeCode.PACTRCT.equals(item.getPartTypeCode()));
		} else {
			epPart.setShowTotalPointsFlag(((PartTable)partTable).showTotalPoints(item));
		}
		
		if(epPart.isShowBillingFrequencyFlag()){
			epPart.setBillingFrequency(item.getBillgFrqncyDscr());
			epPart.setShowEventBasedDescription(
					StringUtils.equals(ConfiguratorConstants.BILLING_FREQUENCY_EVENT,
							item.getBillgFrqncyCode()));
		}
		epPart.setBillingPeriods(item.getBillingPeriods());
		if(epPart.isShowTermSelectionFlag()){
			if(item.getICvrageTerm() != null){
				epPart.setContractTerm(item.getICvrageTerm().toString());
			}
			epPart.setCvrageTermUnit(partTable.getCvrageTermUnit(item));
		}
		if(epPart.isShowItemTotContractVal()){
			epPart.setTotalContractCommitValue(item.getSaasBidTCV());	
		}
		String bpLineItemPrice = partTable.getLineItemBpTCV(item, isPricingCallFailed);
    	if (bpLineItemPrice == null || bpLineItemPrice.equals("")){
    		epPart.setBpLineItemPrice(0d);
    	} else {
    		epPart.setBpLineItemPrice(Double.parseDouble(bpLineItemPrice.replace(",", "")));
    		epPart.setShowBpTCV(true);
    	}
		if(partTable.showRampUpPeriodForSubmitted(item)){
			epPart.setRampUpTitle("Ramp Up Period " + item.getRampUpPeriodNum());
			//set BpTCV for ramp up BP price in PGS 
			epPart.setBpLineItemPrice(item.getSaasBpTCV() == null? 0d : item.getSaasBpTCV().doubleValue());
		}
		
		if(partTable.isDisplayMigration(item, isPGSFlag)) {
			epPart.setMigrationFlag(item.isWebMigrtdDoc());
		}
		
		if(item.isSaasPart()) {
			String entiteldRateVal = partTable.getEntitleRateValForSaasPart(item, partTable, isPricingCallFailed, tier2ResellerFlag);
			String bidRateValu = partTable.getBidRateValForSaasPart(item, partTable, isPricingCallFailed, tier2ResellerFlag);
			
			if(StringUtils.isNotBlank(entiteldRateVal)) {
				epPart.setEntitledRateVal(Double.parseDouble(entiteldRateVal.replaceAll(",", "")));
				epPart.setBidRateVal(Double.parseDouble(bidRateValu.replaceAll(",", "")));
			}
		}
		
		if(!header.isSubmittedQuote()) {
			epPart.setBpRateVal(((PartTable)partTable).getLineItemBpRate(item, isPricingCallFailed));
		}
		
		if(item.isSaasPart()) {
			epPart.setLocalUnitProratedPrc(item.getLocalUnitProratedPrc()==null?0d:item.getLocalUnitProratedPrc());
		}
		if(item.isSaasPart()){
			epPart.setSaasPart(true);
			epPart.setSaasScale(QuoteCommonUtil.getPartPriceRoundingFactor(quote.getQuoteHeader(), item));
			epPart.setConfigurationId(item.getConfigrtnId());
			if(partTable.showSaaSQuantityField(item)){
				epPart.setSaasPartAndHasQty(true);
			}
			//set downloadPricingType for ramp up BP price in PGS 
			epPart.setDownloadPricingType(downloadPricingType);
			List rampUpList = item.getRampUpLineItems();
			if(rampUpList != null && rampUpList.size()>0){
				List rampUpPartList = new ArrayList();
				for (Iterator it = rampUpList.iterator(); it.hasNext();) {
					QuoteLineItem rampUpitem = (QuoteLineItem) it.next();
					SpreadSheetPart rampUpEpPart = new SpreadSheetPart();
					if(partTable.showRampUpPeriodForSubmitted(item)){
						rampUpEpPart.setRampUpTitle("Ramp Up Period " + item.getRampUpPeriodNum());
					}
					rampUpEpPart = this.populateLineItem(quote, epQuote, rampUpitem, rampUpEpPart, partTable, downloadPricingType);
					if(rampUpEpPart!=null){
						rampUpPartList.add(rampUpEpPart);
					}
				}
				epPart.setRampUpLineItems(rampUpPartList);
			}
			
			// check saas part type
			epPart.setSaasProdHumanServicesPart(item.isSaasProdHumanServicesPart());
			epPart.setSaasSubscrptnPart(item.isSaasSubscrptnPart());
			epPart.setSaasSubscrptnOvragePart(item.isSaasSubscrptnOvragePart());
			epPart.setDestSeqNum(item.getDestSeqNum()+"");
			epPart.setiRelatedLineItmNum(item.getIRelatedLineItmNum()+"");
			epPart.setbRampUpPart(item.isRampupPart());
			epPart.setSaasSetUpPart(item.isSaasSetUpPart());
			
			PartsPricingConfiguration ppc = (PartsPricingConfiguration)epQuote.getPartsPricingConfigurationMap()
				.get(epPart.getConfigurationId());
			
			if(ppc != null && ppc.getProvisioningId() != null) {
				epPart.setProvisioningId(ppc.getProvisioningId());
			}
			
			QuoteLineItem  relatedSubsumedPart = item.getSetUpRelatedSubsumedPart();
			if(relatedSubsumedPart != null){
				epPart.setRelatedLinePartNum(relatedSubsumedPart.getPartNum());
			}
			
			boolean isChannelMargin = epQuote.isChannelMarginQuote();
			//indicate whether export this line item. 
			//if the quote is channel quote, or the part is not subsumed part, then export this line item.
			epPart.setShowPart(isChannelMargin || !item.isSaasSubsumedSubscrptnPart());
			/*
			 * Per Moe's update on 7/17/2013 and 8/21/2013, only Channel quote should hide the subsumed format.
			 */
			if(isChannelMargin ){ 
				epPart.setShowEventBasedDescription(false);				
				epPart.setRelatedLinePartNum("");
			}
		}
		
		return epPart;
	}
    /**
     * @param isSubmitter 
     * @param webQuoteNum
     * @throws QuoteException
     * @throws NoDataException
     */
    protected SpreadSheetQuote populateLineItemInfo(Quote quote, SpreadSheetQuote epQuote, String downloadPricingType) throws ExportQuoteException, QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        
			QuoteHeader header = quote.getQuoteHeader();
			String webQuoteNum = header.getWebQuoteNum();
			String lob = header.getLob().getCode();
			List lineItemList = null;
		    // lineItemList = quote.getSoftwareLineItems();
		    lineItemList = quote.getLineItemList();
	  	      
	  	      SubmittedNoteRow noteRow = null;
	  	      PartPriceCommon partTable = null;
	  	      if(header.isSubmittedQuote()){
	  	      	noteRow = new SubmittedNoteRow(quote);
	  	      	partTable = new SubmittedPartTable(quote);
	  	      }else{
	  	    	partTable = new PartTable(quote);
	  	      }
	  	      double entitledExtTotalPrice = 0d;
	  	      double bpExtTotalPrice = 0d;
	  	       
	  	      for (Iterator it = lineItemList.iterator(); it.hasNext();) {
	  	          QuoteLineItem item = (QuoteLineItem) it.next();
	  	          	  	          	           
	  	          //copy necessary fields from line item domain object to SpreadSheetPart which is a POJO for export/import
	            SpreadSheetPart epPart = new SpreadSheetPart();
	            epPart = populateLineItem(quote, epQuote, item, epPart, partTable);
	            
	            if(epPart!=null){
	            	epPart.setDownloadPricingType(downloadPricingType);
	            	
					epQuote.addEQAllPart(epPart);
					if(epPart.isSaasPart()) {
		            	epQuote.addSaaSEQPart(epPart);
		            } else {
		            	epQuote.addEQPart(epPart);
		            }
				}else{
					continue;
				}
	            
				/*if(epPart!=null){
					epQuote.addEQPart(epPart);
				}else{
					continue;
				}*/
	            
	            // add part type to draft quote
	            List groups = item.getPartGroups();
	            StringBuffer buffer = new StringBuffer();
	            for(int i=0;i<groups.size();i++){
	                PartGroup group = (PartGroup)groups.get(i);
	                if(i >0 && i != groups.size()-1){
	                    buffer.append("; ");                
	                }
	                buffer.append(group.getGroupName());
	            }
	            epPart.setSubmittedGroupName(buffer.toString());
	            
	            
		    		//for submitted quote.
		    		if(header.isSubmittedQuote()){
			    		epPart.setShowNoteRow(noteRow.showNoteRow(item));
			    		epPart.setShowPartGroups(noteRow.showPartGroups(item));
			    		epPart.setShowEOLNote(noteRow.showEOLNote(item));
			    		epPart.setShowHasEOLPriceNote(noteRow.showHasEOLPriceNote(item));
			    		epPart.setShowEntitledPriceOverriden(noteRow.showEntitledPriceOverriden(item));
			    		epPart.setShowStartDateForPdf(((SubmittedPartTable)partTable).showSubmittedStartDateText(item));
			    		epPart.setPartUnPublished((header.isPAQuote() || header.isPAEQuote()) && item.isPartRestrct());
			    		epPart.setPartControlled((header.isPAQuote() || header.isPAEQuote()) && item.isControlled());
			    		
			            epPart.setShowPartGroupRow(item.isObsoletePart()||epPart.isPartUnPublished()||epPart.isShowPartGroups()||epPart.isPartControlled()||epPart.isExportRestricted());
			            epPart.setAddedFromRenewalQuote(header.isSalesQuote() && StringUtils.isNotBlank(item.getRenewalQuoteNum()));
			            if (item.getCmprssCvrageMonth() != null){
			                epPart.setComCoverageMonths(item.getCmprssCvrageMonth());
			            }
		    		}
		    		if(!epPart.isSaasPart()){
		    			entitledExtTotalPrice = entitledExtTotalPrice + epPart.getEntitledExtPrice();
		    			bpExtTotalPrice = bpExtTotalPrice + epPart.getChannelExtPrice();
		    		}
					
		    		logger.debug(this, "exported line item :" + epPart);
	  	      }
	  	    epQuote.setEntitledExtTotalPrice(entitledExtTotalPrice);
	  	    epQuote.setBpExtTotalPrice(bpExtTotalPrice);
		return epQuote;
    }

    /**
	 * @param creatorId
	 * @param epQuote
	 * @throws QuoteException
	 * @throws QuoteException
	 * @throws TopazException
	 * @throws TopazException
	 */
    protected SpreadSheetQuote populateQuoteInfo(Quote quote, SpreadSheetQuote epQuote) throws QuoteException  {
        LogContext logger = LogContextFactory.singleton().getLogContext();
       
        QuoteHeader header = quote.getQuoteHeader();
        logger.debug(this, "exported quote header: \n" + header);
        
        Country country = header.getPriceCountry();

        int roundingFactor = DecimalUtil.DEFAULT_SCALE;
        
        if (country != null) {
            roundingFactor = country.endCustPrckRoundingFactor();
        }

        epQuote.setScale(roundingFactor);
        
        epQuote.setChannelMarginQuote(PartPriceConfigFactory.singleton().allowChannelMarginDiscount(quote));
        
        CodeDescObj lob = header.getLob();
        epQuote.setLobCode(StringUtils.trimToEmpty(lob.getCode()));
        epQuote.setLobCodeDesc(StringUtils.trimToEmpty(lob.getCodeDesc()));
        
        epQuote.setWebQuoteNum(header.getWebQuoteNum());
        epQuote.setModDate(header.getModDate().toString());
        epQuote.setTotalPoints(header.getTotalPoints());
        epQuote.setTotalPrice(header.getQuotePriceTot());
        epQuote.setPricingCallFailed(isPricingCallFailed);
        epQuote.setProgMigrtnCode(StringUtils.trimToEmpty(header.getProgMigrationCode()));
        if(header.isFCTToPAQuote()){
			epQuote.setProgMigrationDscr(ApplicationContextFactory
					.singleton().getApplicationContext()
					.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, Locale.US, FCT_TO_PA_MGRTN_QT_KEY));
			epQuote.setAcquisition(header.getAcqrtnCode());
		}
        
        epQuote.setNotesAccessUrl(HtmlUtil.getAppFullUrl(isPGSFlag));
        boolean isApprover = user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER;
        epQuote.setProtected(!isApprover);

        if(header.isSubmittedQuote()) {
        	epQuote.setSubmittedQuoteFlag(true);
        	epQuote.setSubmittedDate(DateFormatUtils.format(header.getSubmittedDate(), RTFDateFromat, Locale.US));
        } else {
        	//Nothing, keep submit flag as false and Date is blank
        }
		
		//release 14.1 Equity Curve discount guidance - Total:	
		PartTableTotal partTableTotal = new PartTableTotal(quote);
		PartPriceCommon partTable = null;
	      if(header.isSubmittedQuote()){
	      	partTable = new SubmittedPartTable(quote);
	      }else{
	    	partTable = new PartTable(quote);
	      }
		boolean isApJapan = partTable.showChannelApJapan();
		if(partTableTotal.showTotalRecommendDiscount() && partTableTotal.isShowECInfo()){
			//the Judge conditions refer to submittedSQPartPricePAPAE.jsp line 929 
			epQuote.setPreferDiscountTotal(partTableTotal.getTotalMinDiscount());
			epQuote.setPreferBidUnitPriceTotal(isApJapan ? "" : partTableTotal.getTotalMinUnitPrice());
			epQuote.setPreferBidExtendedPriceTotal(partTableTotal.getTotalMinExtendedPrice());
			epQuote.setMaxDiscountTotal(partTableTotal.getTotalMaxDiscount());
			epQuote.setMaxBidUnitPriceTotal(isApJapan ? "" : partTableTotal.getTotalMaxUnitPrice());
			epQuote.setMaxBidExendedPriceTotal(partTableTotal.getTotalMaxExtendedPrice());
		}
		//~14.1 EC

        //accqusition only for FCT
        if (header.isFCTQuote()) {
            CacheProcess cProcess = CacheProcessFactory.singleton().create();
            CodeDescObj acquisition = cProcess.getAcquisitionByCode(header.getAcqrtnCode());
            epQuote.setAcquisition(acquisition.getCode());
            epQuote.setAcquisitionDesc(acquisition.getCodeDesc());
        }
        
        if(header.isRenewalQuote()) {
        	epQuote.setRenwalQuoteFlag(true);
        	epQuote.setRenewalQuoteNumber(StringUtils.trimToEmpty(header.getRenwlQuoteNum()));
        	epQuote.setRenewalQuoteDueDate(DateFormatUtils.format(header.getRenwlEndDate(), RTFDateFromat, Locale.US));
        }
        
        epQuote.setCntryCode(header.getCountry().getCode3());
        epQuote.setCntryCodeDesc(header.getCountry().getDesc());
        
        if(header.getQuoteExpDate() != null) {
        	epQuote.setQuoteExpDate(DateFormatUtils.format(header.getQuoteExpDate(), RTFDateFromat, Locale.US));
        } else {
        	epQuote.setQuoteExpDate(StringUtils.EMPTY);
        }
        
        if(header.getQuoteStartDate() != null) {
        	epQuote.setQuoteStartDate(DateFormatUtils.format(header.getQuoteStartDate(), RTFDateFromat, Locale.US));
        } else {
        	epQuote.setQuoteStartDate(StringUtils.EMPTY);
        }
        
        if(!header.isFCTToPAQuote()) {
        	epQuote.setShowSaasSheet(true);
        }
        
        epQuote.setPGSFlag(isPGSFlag);
        epQuote.setTier2ResellerFlag(tier2ResellerFlag);
        
        //process customer info
        Customer cust = quote.getCustomer();
        if (cust != null) {
        	
	        epQuote.setSiteNum(cust.getCustNum());
	        epQuote.setCustomerNum(cust.getIbmCustNum());
	        epQuote.setCustName(cust.getCustName());
	        epQuote.setSapCtrctNum(cust.getSapContractNum());
	        
	        //wiring RTF download requires PostalZipCode, city, state
	        epQuote.setPostalZipCode(StringUtils.trimToEmpty(cust.getPostalCode()));
            epQuote.setCity(StringUtils.trimToEmpty(cust.getCity()));
            epQuote.setState(StringUtils.trimToEmpty(cust.getSapRegionCode()));
            // set custDesignation
   	        epQuote.setCustDesignation(StringUtils.trimToEmpty(cust.getCustDesignation()));
   	        
   	        // set Government for Business Partner Incentives
   	        epQuote.setGovEntityIndDesc(StringUtils.trimToEmpty(cust.getGovEntityIndCodeDesc()));
   	        
	        //set address on excel
	        epQuote.setStreetAddress(StringUtils.trimToEmpty(cust.getAddress1()) +" " + StringUtils.trimToEmpty(cust.getInternalAddress()));
	        
	        epQuote.setCityAddress(StringUtils.trimToEmpty(cust.getCity()) + (cust.getCity()!=null && cust.getCity().trim().endsWith(",")? " ":", ")
	                			  + getStateDesc(cust.getCountryCode(), cust.getSapRegionCode()) + (StringUtils.isNotBlank(cust.getSapRegionCode())? " " :"")
								  + StringUtils.trimToEmpty(cust.getPostalCode()));
	        
	        epQuote.setCntryAddress(getContryDesc(cust.getCountryCode()));
	        
	        //GSA pricing on notes tab
	        boolean gsaPricingFlag = (header.isPAQuote() || header.isPAEQuote()) && cust.getGsaStatusFlag();
	        epQuote.setGsaPricingFlag (gsaPricingFlag);
	        logger.debug(this, "gsaPricingFlag: " + gsaPricingFlag);
	        
	        //Process contract on PA excel
	        String ctrctNum = header.getContractNum();
	        List contractList = cust.getContractList();
	        if(contractList != null && contractList.size()>0) {
	            for(Iterator it = contractList.iterator(); it.hasNext();) {
	 	           
	 	            Contract ctrct = (Contract) it.next();
	 	            String currentCtrctNum = StringUtils.trimToEmpty(ctrct.getSapContractNum());
	 	            logger.debug(this," contract is " + ctrct);
	 	            if(!StringUtils.equals(ctrctNum,currentCtrctNum)) {
	 	                continue;
	 	            }
	 	            
	 	            if (header.isPAQuote() || header.isOEMQuote()) {
	 	                epQuote.setRelationSVPLevel(PartPriceConfigFactory.singleton().getPriceLevelDesc(ctrct.getVolDiscLevelCode()));
	 	                epQuote.setQuoteSVPLevel(PartPriceConfigFactory.singleton().getPriceLevelDesc(header.getOvrrdTranLevelCode()));
	 	                epQuote.setInitSVPLevel(StringUtils.isNotBlank(
	 	                        header.getOvrrdTranLevelCode())?PartPriceConfigFactory.singleton().getPriceLevelDesc(header.getOvrrdTranLevelCode()):PartPriceConfigFactory.singleton().getPriceLevelDesc(header.getTranPriceLevelCode()));
	 	                epQuote.setPreApprovedPricingFlag(ctrct.getIsContractActiveFlag()== 1);
	 	                epQuote.setContractOption(getContractOptionDesc(ctrct.getSapContractVariantCode()));
	 	                epQuote.setNimOffergCode(ctrct.getNimOffergCode());
	 	                
	 	                if(null != ctrct.getAnniversaryDate())
	 	                epQuote.setAnniversary(DateFormatUtils.format(ctrct.getAnniversaryDate(), mouthFromat, Locale.US));
	 	            }
	 	        }
	        } else if (header.isPAQuote() && header.hasNewCustomer()){
	        	epQuote.setQuoteSVPLevel(PartPriceConfigFactory.singleton().getPriceLevelDesc(header.getOvrrdTranLevelCode()));
	        	epQuote.setInitSVPLevel(StringUtils.isNotBlank(
	                        header.getOvrrdTranLevelCode())?PartPriceConfigFactory.singleton().getPriceLevelDesc(header.getOvrrdTranLevelCode()):PartPriceConfigFactory.singleton().getPriceLevelDesc(header.getTranPriceLevelCode()));
	        	epQuote.setRelationSVPLevel(epQuote.getInitSVPLevel());
	        }
        }
      
        epQuote.setCurrency(header.getCurrencyCode());
        epQuote.setPricingDate(DateUtil.formatDate(header.getPriceStartDate(), dateFromat));
        
        // wiring RFT download currency description
        epQuote.setCurrencyDesc(getCurrencyDesc(header.getPriceCountry(), header.getCurrencyCode()));
        
        List quoteContacts = quote.getContactList();
        QuoteContact qtcact = null;
        if(quoteContacts != null)
        for(Iterator qcit = quoteContacts.iterator(); qcit.hasNext();) {
        	qtcact = (QuoteContact) qcit.next();
        	break;
        }
        
        if(qtcact!=null) {
        	epQuote.setPrimaryContactName(
        			StringUtils.trimToEmpty(qtcact.getCntFirstName()) + " " + 
        			StringUtils.trimToEmpty(qtcact.getCntLastName()));
            epQuote.setPrimaryContactPhone(qtcact.getCntPhoneNumFull());
            epQuote.setPrimaryContactEmail(qtcact.getCntEmailAdr());
            epQuote.setPrimaryContactFax(qtcact.getCntFaxNumFull());
        }
        PartTableTotal ptt = new PartTableTotal(quote);
        if(header.hasSaaSLineItem() || quote.getMonthlySwQuoteDomain().isQuoteHasMonthlySwPart()){
        	epQuote.setHasSaaSLineItem(true);
        	epQuote.setServicesAgreementNum(header.getRefDocNum());
        	//epQuote.setSaaSEqPartList(quote.getSaaSLineItems());
        	List saasLineItem = extractSaasLineItem(quote);
        	epQuote.setTotalSaasPoint(ptt.calculateTotalPoint(saasLineItem));
        	epQuote.setTotalEntitledTCV(ptt.getTotalEntitledExtendedPrice(saasLineItem, isPricingCallFailed));
        	if(this.tier2ResellerFlag && epQuote.isSubmittedQuoteFlag()) {
        		epQuote.setTotalCommitValue(ptt.getTotalEntitledExtendedPrice(saasLineItem, isPricingCallFailed));
        		
        		String totalCommitValue = StringUtils.trimToEmpty(epQuote.getTotalCommitValue().replaceAll(",", ""));
        		epQuote.setTotalPrice(Double.valueOf(totalCommitValue.equals("")?0d+"":totalCommitValue));
        	} else {
        		epQuote.setTotalCommitValue(ptt.getTotalBidExtendedPrice(saasLineItem, isPricingCallFailed));
        	}
        	if(epQuote.isChannelMarginQuote()&&!epQuote.isFCT()){
	        	epQuote.setBpExtTotalValue(ptt.getTotalBpExtendedPrice(saasLineItem, isPricingCallFailed));
	        	epQuote.setBpTCVPrice(ptt.getTotalBpTCV(saasLineItem, isPricingCallFailed));
        	}
        }
        
        if(header.hasSoftSLineItem()) {
        	if(epQuote.isChannelMarginQuote()&&!epQuote.isFCT()){
        		epQuote.setBpTCVPrice(ptt.getTotalBpTCV(quote.getSoftwareLineItems(), isPricingCallFailed));
        	}
        }
        
        if(quote.getSoftwareLineItems()!=null && quote.getSoftwareLineItems().size()>0){
        	epQuote.setHasSoftwareLineItem(true);
        }
        epQuote.setTotalSoftwarePoint(ptt.calculateTotalPoint(quote.getSoftwareLineItems()));
        
        if(this.tier2ResellerFlag) {
        	epQuote.setTotalSoftwarePrice(ptt.getTotalEntitledExtendedPrice(quote.getSoftwareLineItems(), isPricingCallFailed));
        } else {
        	epQuote.setTotalSoftwarePrice(ptt.getTotalBidExtendedPrice(quote.getSoftwareLineItems(), isPricingCallFailed));
        }
        epQuote.setBidExtTotalPrice(ptt.getTotalBidExtendedPrice(quote.getSoftwareLineItems(), isPricingCallFailed));
        
        //added for RTF download end
        return epQuote;
    }
    
    /**
     * obtain SaaS Line items from quote.
     * @param quote
     * @return
     */
    private List extractSaasLineItem(Quote quote) {
    	List saasLineItem = new ArrayList();
    	if(quote.getSaaSLineItems() != null) 
    		saasLineItem.addAll(quote.getSaaSLineItems());
    	//for 14.2 treat monthly as saas
    	if(quote.getMonthlySwQuoteDomain() != null
    			&& quote.getMonthlySwQuoteDomain().getMonthlySoftwares() != null)
    		saasLineItem.addAll(quote.getMonthlySwQuoteDomain().getMonthlySoftwares());
		return saasLineItem;
	}

	protected SpreadSheetQuote populateQuoteInfoForSubmittedQuoteExcel(Quote quote, SpreadSheetQuote epQuote) throws QuoteException  {
    	QuoteHeader header = quote.getQuoteHeader();
    	
    	String url = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.APPLICATION_QUOTE_APPURL);
        url = url + HtmlUtil.getURLForAction(FindQuoteActionKeys.DISPATCH_SUBMITTED_QUOTE_TABS)
                + "&quoteNum=" + header.getWebQuoteNum();
    	epQuote.setViewSubmittedQuoteDetailUrl(url);
    	
    	epQuote.setProgMigrtnCode(StringUtils.trimToEmpty(header.getProgMigrationCode()));
    	
        String overallStatus = null;
        List overallStatuses = header.getQuoteOverallStatuses();
        if (overallStatuses != null) {
            StringBuffer sbOS = new StringBuffer();
            for (int i = 0; i < overallStatuses.size(); i++) {
                CodeDescObj os = (CodeDescObj) overallStatuses.get(i);
                String osDesc = os.getCodeDesc();
                sbOS.append(osDesc);
                if(i!=overallStatuses.size()-1){
                	sbOS.append(",");
                }
            }
            overallStatus = sbOS.toString();
        }
        epQuote.setOverallStatus(StringUtils.trimToEmpty(overallStatus));
        
        String oppNumInfo = "";
        oppNumInfo = header.getOpprtntyNum() == null ? "" : header.getOpprtntyNum();
        if(StringUtils.isBlank(oppNumInfo) && StringUtils.isNotBlank(header.getExemptnCode())){
            oppNumInfo = "Exemption code " + header.getExemptnCode();
        }
        epQuote.setOpprtntyInfo(oppNumInfo);
        epQuote.setExemptnCode(header.getExemptnCode());
        epQuote.setFulfillmentSrc(header.getFulfillmentSrc());
    	epQuote.setSapQuoteNum(StringUtils.trimToEmpty(header.getSapQuoteNum()));
        epQuote.setSapIDocNum(header.getOriginalIdocNum());
        
        if (header.getOpprtntyOwnrEmailAdr() != null) {
        	epQuote.setQuoteOppOwnerEmail(StringUtils.trimToEmpty(header.getOpprtntyOwnrEmailAdr()));
        }
    	
		SubmittedQuoteAccess sqa = quote.getSubmittedQuoteAccess();
		if(sqa != null && sqa.getSbApprovedDate() != null){
			epQuote.setSbApprovedDate(DateFormatUtils.format(sqa.getSbApprovedDate(), RTFDateFromat, Locale.US));
		}
    	// reseller info
        Partner reseller = quote.getReseller();
        if(reseller != null){
        	epQuote.setResellerCustName(reseller.getCustName());
        	epQuote.setResellerCustNum(reseller.getCustNum());
        	epQuote.setResellerIBMCustNum(reseller.getIbmCustNum());
        	
            String cntryCode = StringUtils.trimToEmpty(reseller.getCountry());
            String stateCode = StringUtils.trimToEmpty(reseller.getState());
            
	        String resellerState = getStateDesc(cntryCode, stateCode);
	        String resellerCntry = getContryDesc(cntryCode);        	
        	
        	epQuote.setResellerStreetAddr(StringUtils.trimToEmpty(reseller.getAddress1()) +" " + StringUtils.trimToEmpty(reseller.getAddress2()));
        	epQuote.setResellerCityAddr(StringUtils.trimToEmpty(reseller.getCity()) + (reseller.getCity()!=null && reseller.getCity().trim().endsWith(",")? " ":", ")
      			  + StringUtils.trimToEmpty(resellerState) + (StringUtils.isNotBlank(stateCode)? " " :"")
					  + StringUtils.trimToEmpty(reseller.getPostalCode()));
        	epQuote.setResellerCntryAddr(resellerCntry);
        }
        // distributor info
        Partner payer = quote.getPayer();
        if(payer != null){
        	epQuote.setDistributorCustName(payer.getCustName());
        	epQuote.setDistributorCustNum(payer.getCustNum());
        	epQuote.setDistributorIBMCustNum(payer.getIbmCustNum());
        	
            String cntryCode = StringUtils.trimToEmpty(payer.getCountry());
            String stateCode = StringUtils.trimToEmpty(payer.getState());
            
	        String distributorState = getStateDesc(cntryCode, stateCode);
	        String distributorCntry = getContryDesc(cntryCode);        	
        	
        	epQuote.setDistributorStreetAddr(StringUtils.trimToEmpty(payer.getAddress1()) +" " + StringUtils.trimToEmpty(payer.getAddress2()));
        	epQuote.setDistributorCityAddr(StringUtils.trimToEmpty(payer.getCity()) + (payer.getCity()!=null && payer.getCity().trim().endsWith(",")? " ":", ")
      			  + StringUtils.trimToEmpty(distributorState) + (StringUtils.isNotBlank(stateCode)? " " :"")
					  + StringUtils.trimToEmpty(payer.getPostalCode()));
        	epQuote.setDistributorCntryAddr(StringUtils.trimToEmpty(distributorCntry));
        }
        
        // total price row info
        List prcTotals = quote.getPriceTotals();
        double tempEntitledExtTotalPrice = 0;
        double tempBPExtTotalPrice = 0;
        double tempBidExtendedTotalPrice = -1;
        
		for (int i = 0; i < prcTotals.size(); i++) {
            QuotePriceTotals prcTotal = (QuotePriceTotals) prcTotals.get(i);
            if (prcTotal.getPriceType().equals(PartPriceConstants.PriceType.ENTITLED_PRICE) && prcTotal.getPriceSumLevelCode().equals(QuoteConstants.PRICE_SUM_LEVEL_TOTAL)
                    && prcTotal.getCurrencyCode().equals(quote.getQuoteHeader().getCurrencyCode())
                    && prcTotal.getDistChannelCode().equals(QuoteConstants.DIST_CHNL_END_CUSTOMER)
                    && prcTotal.getRevnStrmCategoryCode().equals(PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL)) {
                tempEntitledExtTotalPrice = prcTotal.getExtAmount();
            }
            if (prcTotal.getPriceType().equals(PartPriceConstants.PriceType.SPECIAL_BID_PRICE) && prcTotal.getPriceSumLevelCode().equals(QuoteConstants.PRICE_SUM_LEVEL_TOTAL)
                    && prcTotal.getCurrencyCode().equals(quote.getQuoteHeader().getCurrencyCode())
                    && prcTotal.getDistChannelCode().equals(QuoteConstants.DIST_CHNL_END_CUSTOMER)
                    && prcTotal.getRevnStrmCategoryCode().equals(PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL)) {
                tempBidExtendedTotalPrice  = prcTotal.getExtAmount();
            }
            if (PartPriceConfigFactory.singleton().allowChannelMarginDiscount(quote) && prcTotal.getPriceType().equals(PartPriceConstants.PriceType.CHANNEL) && prcTotal.getPriceSumLevelCode().equals(QuoteConstants.PRICE_SUM_LEVEL_TOTAL)
                    && prcTotal.getCurrencyCode().equals(quote.getQuoteHeader().getCurrencyCode())
                    && prcTotal.getDistChannelCode().equals(quote.getQuoteHeader().getSapDistribtnChnlCode())
                    && prcTotal.getRevnStrmCategoryCode().equals(PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL)) {
                tempBPExtTotalPrice = prcTotal.getExtAmount();
            }                                    
        }
		
		if (tempBidExtendedTotalPrice == -1){
		    tempBidExtendedTotalPrice = tempEntitledExtTotalPrice;
		}
		epQuote.setUserLineTotalDiscount(DecimalUtil.convertStringToDouble(DecimalUtil.calculateDiscount(tempBidExtendedTotalPrice, tempEntitledExtTotalPrice))/100);
		epQuote.setTotalLineTotalDiscount(DecimalUtil.convertStringToDouble(DecimalUtil.calculateDiscount(tempBPExtTotalPrice, tempEntitledExtTotalPrice))/100);        
        
    	return epQuote;
    }
    
    //get currency desc based on currency code, add for RFT download fuct
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
    
    protected String getStateDesc (String cntryCode, String sapRegionCode) {
        String stateDesc = null;
        
        try {
            Country cntry = CountryFactory.singleton().findByCode3(cntryCode);
            
            if(cntry != null)
                stateDesc = cntry.getStateDescription(StringUtils.trimToEmpty(sapRegionCode));
        } catch (TopazException e) {
            LogContext logger = LogContextFactory.singleton().getLogContext();
        	logger.error(this, "Error get stateDesc des by cntryCode :"+ cntryCode + "  sapRegionCode " + sapRegionCode);
            stateDesc = null;
        }
        return (StringUtils.isBlank(stateDesc)? StringUtils.EMPTY:stateDesc);
    }
    
    protected String getContryDesc(String cntryCode) {
    	String cntryDesc = null;
    	try {
			if (cntryCode == null) {
				return StringUtils.EMPTY;
			}
			Country country = CountryFactory.singleton().findByCode3(cntryCode);
			if(country != null){
				cntryDesc = country.getDesc();
			}
			} catch (TopazException e) {
			LogContext logger = LogContextFactory.singleton().getLogContext();
        	logger.error(this, "Error get contry des by code :"+ cntryCode);
		}
		return (StringUtils.isBlank(cntryDesc)? cntryCode:cntryDesc);
    }
    
    protected String getContractOptionDesc(String contractOption) {
        if (StringUtils.isBlank(contractOption))
            return "";
        List  ctrctOptList = null;
        try {
            CacheProcess process = CacheProcessFactory.singleton().create();
            ctrctOptList = process.getCtrctVariantList();
        } catch (QuoteException qe) {
        	LogContext logger = LogContextFactory.singleton().getLogContext();
        	logger.error(this, "Error executing getContractOptionDesc contractOption:"+ contractOption);
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
    
    protected abstract SpreadSheetQuote populateSupportingData(SpreadSheetQuote epQuote, String creatorId) throws ExportQuoteException ;
    //this one for get data for under evaluator quote by quote num
    protected abstract SpreadSheetQuote populateSupportingData(SpreadSheetQuote epQuote, String creatorId, String webQuoteNum) throws ExportQuoteException ;
   
    protected SpreadSheetQuote populateResellerAndDistributorInfo(Quote quote, SpreadSheetQuote epQuote) throws QuoteException  {
    	QuoteHeader header = quote.getQuoteHeader(); 
    	if(header.isChannelQuote() || header.isFCTQuote()){
         	// reseller info
             Partner reseller = quote.getReseller();
             
             if(reseller != null){
             	epQuote.setResellerCustName(StringUtils.trimToEmpty(reseller.getCustName()));
             	epQuote.setResellerCustNum(reseller.getCustNum());
             }
             // distributor info
             Partner payer = quote.getPayer();
             if(payer != null){
             	epQuote.setDistributorCustName(StringUtils.trimToEmpty(payer.getCustName()));
             	epQuote.setDistributorCustNum(payer.getCustNum());
             }
         }
    	return epQuote;
    }
    
    private boolean disableOverrideUnitPriceInput(QuoteLineItem qli, Quote quote){
    	return ((qli.isObsoletePart() && !qli.canPartBeReactivated())
    	        || (qli.hasValidCmprssCvrageMonth() )
    	        || isBidIteratnQt(quote)
    	        || qli.isReplacedPart());
    }
    
    private boolean isBidIteratnQt(Quote quote){
        return (QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode()) && quote.getQuoteHeader().isBidIteratnQt());
    }
    
    private boolean isQuoteHasSaas(Quote quote){
		boolean includeSaas = quote.getQuoteHeader().hasSaaSLineItem() 
		|| quote.getMonthlySwQuoteDomain().isQuoteHasMonthlySwPart(); 
		return includeSaas;
    }
}