package com.ibm.dsw.quote.common.process;

import is.domainx.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.ReturnReasonFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ApproverRuleValidation;
import com.ibm.dsw.quote.common.domain.AuditHistory;
import com.ibm.dsw.quote.common.domain.AuditHistoryFactory;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.ContractFactory;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerFactory;
import com.ibm.dsw.quote.common.domain.DefaultCustAddress;
import com.ibm.dsw.quote.common.domain.EvalFactory;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfigurationFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteAttachmentFactory;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteContactFactory;
import com.ibm.dsw.quote.common.domain.QuoteExpireDaysFactory;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem_Impl;
import com.ibm.dsw.quote.common.domain.QuoteReasonFactory;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.domain.QuoteRightColumnFactory;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.common.domain.QuoteStatusFactory;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.QuoteTxtFactory;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.domain.SQOHeadLineFactory;
import com.ibm.dsw.quote.common.domain.SSPEndUser;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidInfoFactory;
import com.ibm.dsw.quote.common.domain.UserWrapper;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.service.QuoteCreateServiceHelper;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.spbid.SpecialBidRule;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcessFactory;
import com.ibm.dsw.quote.draftquote.contract.PostCustPrtnrTabContract;
import com.ibm.dsw.quote.draftquote.contract.SubmitQuoteSubmissionContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.dsw.quote.draftquote.util.builder.DraftQuoteBuilder;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidInputException;
import com.ibm.dsw.quote.partner.config.PartnerDBConstants;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcessFactory;
import com.ibm.dsw.quote.submittedquote.domain.ExecSummary;
import com.ibm.dsw.quote.submittedquote.domain.ExecSummaryFactory;
import com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHistFactory;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvrFactory;
import com.ibm.dsw.quote.submittedquote.domain.SubmittedQuoteAccess;
import com.ibm.dsw.quote.submittedquote.util.builder.SubmittedQuoteBuilder;
import com.ibm.dsw.spbid.common.ApprovalGroup;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code><code> class.
 *    
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Mar 4, 2007
 */
/**
 * @author Brandon
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class QuoteProcess_Impl extends TopazTransactionalProcess implements QuoteProcess {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public QuoteRightColumn getQuoteRightColumnInfo(String creatorId) throws QuoteException {
        QuoteRightColumn quoteRightColumn = null;
        try {
            //begin the transaction
            this.beginTransaction();
            quoteRightColumn = QuoteRightColumnFactory.singleton().findQuoteRightColumnByID(creatorId);

            //commit the transaction
            this.commitTransaction();
            return quoteRightColumn;
        } catch (NoDataException nde) {
            return null;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public Quote getQuoteForApplianceAddress(String webQuoteNum) throws QuoteException
    {
    	try
    	{
    		return getQuoteCommonInfo(null, webQuoteNum);
    	}
    	catch ( TopazException t )
    	{
    		throw new QuoteException(t);
    	}
    }

    protected Quote getQuoteCommonInfo(String creatorID, String webQuoteNum) throws TopazException, QuoteException {
        TimeTracer tracer = TimeTracer.newInstance();
    	
    	Quote quote = null;

        QuoteHeader quoteHeader = null;
        if (StringUtils.isNotBlank(webQuoteNum)) {
            logContext.debug(this, "To retrieve quote header by quote num: " + webQuoteNum);
            quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(webQuoteNum);
        } else if (StringUtils.isNotBlank(creatorID)) {
            logContext.debug(this, "To retrieve quote header by creatorId: " + creatorID);
            quoteHeader = QuoteHeaderFactory.singleton().findByCreatorID(creatorID);
        } else
            throw new NoDataException();

        //logContext.debug(this, "QuoteHeader:\n" + quoteHeader.toString());
        quote = new Quote(quoteHeader);

        String lob = quoteHeader.getLob() == null ? "" : StringUtils.trimToEmpty(quoteHeader.getLob().getCode());
        String soldToCustNum = quoteHeader.getSoldToCustNum();
        String ctrctNum = quoteHeader.getContractNum();
        if (StringUtils.isBlank(webQuoteNum))
            webQuoteNum = StringUtils.trimToEmpty(quoteHeader.getWebQuoteNum());
        int webCustId = quoteHeader.getWebCustId();
        int newCustFlag = StringUtils.isNotBlank(soldToCustNum) ? 0 : 1;

        if (StringUtils.isNotBlank(soldToCustNum) || (webCustId > 0)) {
            logContext.debug(this, "To retrieve customer by web quote num: " + webQuoteNum);
            Customer customer = CustomerFactory.singleton().findByQuoteNum(lob, newCustFlag, soldToCustNum, ctrctNum,
                    webQuoteNum, webCustId);
            //logContext.debug(this, "Customer:\n" + customer.toString());
            quote.setCustomer(customer);
        }
      
        if (quoteHeader.isRenewalQuote()) {
            List statusList = QuoteStatusFactory.singleton().getStatusByQuoteNum(null, webQuoteNum);
            if (statusList != null) {
                for (Iterator iter = statusList.iterator(); iter.hasNext();) {
                    QuoteStatus status = (QuoteStatus) iter.next();
                    if (QuoteConstants.QUOTE_STATUS_PRIMARY.equalsIgnoreCase(status.getStatusType())) {
                        quote.addWebPrimaryStatus(status);
                    } else if (QuoteConstants.QUOTE_STATUS_SECONDARY.equalsIgnoreCase(status.getStatusType())) {
                        quote.addWebSecondaryStatus(status);
                    }
                }
            }
        }
        
        tracer.dump();
        
        return quote;
    }
    
    public Quote getDraftQuoteBaseInfoByQuoteNum(String webQuoteNum) throws QuoteException, NoDataException {
    	
    	Quote quote = null;
        try {
           
            quote = getQuoteCommonInfo(null, webQuoteNum);
            QuoteHeader quoteHeader = quote.getQuoteHeader();

            // get max expiration days for all quotes:
            // for sales quote, get max quote expiration days
            // for renewal quote, get max speical bid expiration days, if quote is non-special-bid, it won't be used.
            int maxExpDays = QuoteExpireDaysFactory.singleton().findMaxQuoteExpireDays(quoteHeader);
            quoteHeader.setQuoteExpDays(maxExpDays);

        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (QuoteException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } 

        return quote;
    }
    
    public Quote getDraftQuoteBaseInfoWithTransaction(String creatorID) throws QuoteException, NoDataException {
        TimeTracer tracer = TimeTracer.newInstance();
    	
    	Quote quote = null;
        try {
        	// begin transaction
            this.beginTransaction();
            
            quote = getQuoteCommonInfo(creatorID, null);
            QuoteHeader quoteHeader = quote.getQuoteHeader();

            // get max expiration days for all quotes:
            // for sales quote, get max quote expiration days
            // for renewal quote, get max speical bid expiration days, if quote is non-special-bid, it won't be used.
            int maxExpDays = QuoteExpireDaysFactory.singleton().findMaxQuoteExpireDays(quoteHeader);
            quoteHeader.setQuoteExpDays(maxExpDays);
            
            if (quoteHeader.isRenewalQuote()) {
                // To get QuoteAccess
                String rnwlQuoteNum = quoteHeader.getRenwlQuoteNum();
                QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();

                logContext.debug(this, "To retrieve QuoteAccess by renewal quote number: " + rnwlQuoteNum);
                QuoteAccess quoteAccess = qcProcess.getRnwlQuoteAccess(creatorID, rnwlQuoteNum);
                logContext.debug(this, "QuoteAccess:\n" + quoteAccess.toString());
                quote.setQuoteAccess(quoteAccess);
            }
            
            //commit the transaction
            this.commitTransaction();

        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (QuoteException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } finally {
        	rollbackTransaction();
        }
        return quote;
    }
    
    public Quote getDraftQuoteBaseInfo(String creatorID) throws QuoteException, NoDataException {
        TimeTracer tracer = TimeTracer.newInstance();
    	
    	Quote quote = null;
        try {

            quote = getQuoteCommonInfo(creatorID, null);
            QuoteHeader quoteHeader = quote.getQuoteHeader();

            // get max expiration days for all quotes:
            // for sales quote, get max quote expiration days
            // for renewal quote, get max speical bid expiration days, if quote is non-special-bid, it won't be used.
            int maxExpDays = QuoteExpireDaysFactory.singleton().findMaxQuoteExpireDays(quoteHeader);
            quoteHeader.setQuoteExpDays(maxExpDays);
            
            if (quoteHeader.isRenewalQuote()) {
                // To get QuoteAccess
                String rnwlQuoteNum = quoteHeader.getRenwlQuoteNum();
                QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();

                logContext.debug(this, "To retrieve QuoteAccess by renewal quote number: " + rnwlQuoteNum);
                QuoteAccess quoteAccess = qcProcess.getRnwlQuoteAccess(creatorID, rnwlQuoteNum);
                logContext.debug(this, "QuoteAccess:\n" + quoteAccess.toString());
                quote.setQuoteAccess(quoteAccess);
            }
            

        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (QuoteException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } 
        return quote;
    }

    public Quote getSubmittedQuoteBaseInfo(String webQuoteNum, String userId, String up2ReportingUserIds) throws QuoteException, NoDataException {
        Quote quote = null;

        try {
            // begin transaction
            this.beginTransaction();

            quote = getQuoteCommonInfo(userId, webQuoteNum);
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            
            //if copy approved sb flag is true, we won't verify the iDocNum
            if(!(quoteHeader.isCopied4PrcIncrQuoteFlag() &&
                    QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quoteHeader.getQuoteStageCode()))
                && !(quoteHeader.isExpDateExtendedFlag() && 
                	QuoteConstants.QUOTE_STAGE_CODE_CPEXDATE.equalsIgnoreCase(quoteHeader.getQuoteStageCode()))
                && ! (quoteHeader.isBidIteratnQt()
                        && QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quoteHeader.getQuoteStageCode()))
                && ! (quoteHeader.isCopiedForOutputChangeFlag()
                        && QuoteConstants.QUOTE_STAGE_CODE_CPCHGOUT.equalsIgnoreCase(quoteHeader.getQuoteStageCode()))
                && !quoteHeader.isSubmittedForEval() && !quoteHeader.isAcceptedByEval() && !quoteHeader.isReturnForChgByEval()
                && !quoteHeader.isEditForRetChgByEval() && !quoteHeader.isDeleteByEditor()
            ){
                String sapIDocNum = quote.getQuoteHeader().getSapIntrmdiatDocNum();
                if (StringUtils.isBlank(sapIDocNum)) {
                    logContext.error(this, "The sap idoc number is empty, throws no data exeception.");
                    throw new NoDataException();
                }
            }

            logContext.debug(this, "To retrieve QuoteUserAccess by webQuoteNum: " + webQuoteNum);
            QuoteUserAccess quoteUserAccess = getQuoteUserAccess(webQuoteNum, userId, up2ReportingUserIds);
            quote.setQuoteUserAccess(quoteUserAccess);
            logContext.debug(this, "QuoteUserAccess:\n" + quoteUserAccess.toString());
            
            logContext.debug(this, "To retrieve SubmittedQuoteAccess by webQuoteNum: " + webQuoteNum);
            SubmittedQuoteAccess sbmtQuoteAccess = this.getSubmittedQuoteAccess(webQuoteNum);
            quote.setSubmittedQuoteAccess(sbmtQuoteAccess);
            logContext.debug(this, "SubmittedQuoteAccess:\n" + sbmtQuoteAccess.toString());

            if (quote.getQuoteHeader().isRenewalQuote()) {
                logContext.debug(this, "To retireve SAP quote status by quote num: " + webQuoteNum);
                List statusList = QuoteStatusFactory.singleton().getSapStatusByQuoteNum(quote.getQuoteHeader().getRenwlQuoteNum());
                if (statusList != null) {
                    for (Iterator iter = statusList.iterator(); iter.hasNext();) {
                        QuoteStatus status = (QuoteStatus) iter.next();
                        if (QuoteConstants.QUOTE_STATUS_PRIMARY.equalsIgnoreCase(status.getStatusType())) {
                            quote.addSapPrimaryStatus(status);
                        } else if (QuoteConstants.QUOTE_STATUS_SECONDARY.equalsIgnoreCase(status.getStatusType())) {
                            quote.addSapSecondaryStatus(status);
                        }
                    }
                }
            }

            //end transaction
            this.commitTransaction();

        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (QuoteException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } finally {
            rollbackTransaction();
        }

        return quote;
    }

    public void getQuoteDetailForCustTab(Quote quote, String bpSiteNum) throws QuoteException {
        try {
            //begin the transaction
            this.beginTransaction();

            QuoteHeader quoteHeader = quote.getQuoteHeader();
            if (quoteHeader == null)
                return;

            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum())) {
                logContext.debug(this, "To retrieve QuoteContacts by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                QuoteContact quoteContact = QuoteContactFactory.singleton().findQuoteContact(
                        quoteHeader.getWebQuoteNum(), quoteHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
                List contactList = new ArrayList();
                contactList.add(quoteContact);
                quote.setContactList(contactList);
            }

            String quoteTypeCode = quoteHeader.getQuoteTypeCode();
            String fulfillSrc = quoteHeader.getFulfillmentSrc();
            String lob = quoteHeader.getLob().getCode();
            quoteTypeCode = quoteTypeCode == null ? "" : quoteTypeCode.trim();
            fulfillSrc = fulfillSrc == null ? "" : fulfillSrc.trim();
            lob = lob == null ? "" : lob.trim();

            // Updated to retrieve payer for FCT quotes
            if (QuoteConstants.QUOTE_TYPE_RENEWAL.equalsIgnoreCase(quoteTypeCode)
                    || (QuoteConstants.QUOTE_TYPE_SALES.equalsIgnoreCase(quoteTypeCode)
                            && (quoteHeader.isPAQuote() || quoteHeader.isPAEQuote() || quoteHeader.isOEMQuote() || quoteHeader.isSSPQuote()) && !QuoteConstants.FULFILLMENT_DIRECT
                            .equalsIgnoreCase(fulfillSrc))
                    || (QuoteConstants.QUOTE_TYPE_SALES.equalsIgnoreCase(quoteTypeCode) && quoteHeader.isFCTQuote() && QuoteConstants.FULFILLMENT_DIRECT
                            .equalsIgnoreCase(fulfillSrc))) {

                if (StringUtils.isNotBlank(quoteHeader.getPayerCustNum())) {
                    logContext.debug(this, "To retrieve Payer by number: " + quoteHeader.getPayerCustNum());
                    Partner payer = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getPayerCustNum(),
                            quoteHeader.getLob().getCode());
                    quote.setPayer(payer);
                }

                if (StringUtils.isNotBlank(quoteHeader.getRselCustNum())) {
                    logContext.debug(this, "To retrieve Reseller by number: " + quoteHeader.getRselCustNum());
                    Partner reseller = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getRselCustNum(),
                            quoteHeader.getLob().getCode());
                    quote.setReseller(reseller);
                }
            }

            //Get end user info when SSP quote
            this.getEndUser(quote);
            
            String endUserCustNum = quoteHeader.getEndUserCustNum();
            if (StringUtils.isNotBlank(endUserCustNum) && (CustomerConstants.LOB_OEM.equalsIgnoreCase(lob))) {
                logContext.debug(this, "To retrieve OEM customer by web quote num: " + quoteHeader.getWebQuoteNum());
                 Customer endUser = CustomerFactory.singleton().findByQuoteNum(lob, 0, endUserCustNum, null, quoteHeader.getWebQuoteNum(), 0);
                 quote.setEndUser(endUser);  
            }
            
            //Get customer default address for ship to and install at
            if(quote.getQuoteHeader().isDisShipInstAdrFlag()){
            	DefaultCustAddress defaultCustAddr = CustomerFactory.singleton().findCustomerDefaulAddress(quote.getQuoteHeader().getWebQuoteNum(),bpSiteNum);
            	quote.setDefaultCustAddr(defaultCustAddr);
            }
            
            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    
    public void getQuoteDetailForCustTab(Quote quote) throws QuoteException {
    	this.getQuoteDetailForCustTab(quote,"");
    }
    
    
    public Quote getQuoteDetailForComparison(String webQuoteNum, User user, String userId, String up2ReportingUserIds) throws QuoteException {
    	Quote quote = null;
    	try {
            //begin the transaction
            this.beginTransaction();
            quote = getQuoteCommonInfo(null, webQuoteNum);
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
            	
            	logContext.debug(this, "To retrieve promotion info by webQuoteNum: " + quoteHeader.getWebQuoteNum());
    			List promotionsList = QuotePromotionProcessFactory.singleton().create().findPromotionsByQuote(
    					quoteHeader.getWebQuoteNum());
    			quote.setPromotionsList(promotionsList);

    			logContext.debug(this, "To retrieve QuoteContacts by webQuoteNum: " + quoteHeader.getWebQuoteNum());
    			QuoteContact quoteContact = QuoteContactFactory.singleton().findQuoteContact(
    					quoteHeader.getWebQuoteNum(), quoteHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
    			List contactList = new ArrayList();
    			contactList.add(quoteContact);
    			quote.setContactList(contactList);
            	
                if (quoteHeader.isSubmittedQuote()) { 	
                    // the build methods will retrieve quote line item info for submitted quote
                    SubmittedQuoteBuilder.create(quote,user).build();
                    
    				logContext.debug(this, "To retrieve QuoteUserAccess by webQuoteNum: " + webQuoteNum);
                    QuoteUserAccess quoteUserAccess = getQuoteUserAccess(webQuoteNum, userId, up2ReportingUserIds);
                    quote.setQuoteUserAccess(quoteUserAccess);
                    logContext.debug(this, "QuoteUserAccess:\n" + quoteUserAccess.toString());
    				
                    if (quoteUserAccess!=null && quoteUserAccess.isExecSummryCreatd()){
                    	//get exec summary
                        ExecSummary execSummary = ExecSummaryFactory.singleton().findExecSummaryByQuoteNum(quoteHeader.getWebQuoteNum());
                        quote.setExecSummary(execSummary);
                    }
    			} else {
    				// the build methods will retrieve quote line item info for draft quote
    			    DraftQuoteBuilder draftBuilder = DraftQuoteBuilder.create(quote, user.getEmail());
    			    draftBuilder.build();
    			}
    			
    			logContext.debug(this, "To retrieve special bid info by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                SpecialBidInfo specialBidInfo = SpecialBidInfoFactory.singleton().findByQuoteNum(
                        quoteHeader.getWebQuoteNum());
                quote.setSpecialBidInfo(specialBidInfo);
            }
            
            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return quote;
    }

    public void getQuoteDetailForSpecialBidTab(Quote quote) throws QuoteException {
    	getQuoteDetailForSpecialBidTab(quote,null);
    }
    public void getQuoteDetailForSpecialBidTab(Quote quote,QuoteUserSession user) throws QuoteException {
        try {
            //begin the transaction
            this.beginTransaction();
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve lineItemList by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                        quoteHeader.getWebQuoteNum());
                quote.setLineItemList(lineItemList);
            }
            if (quoteHeader.getWebQuoteNum() != null
                    && quoteHeader.getWebQuoteNum().trim().length() > 0
                    ) {
                logContext.debug(this, "To retrieve special bid info by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                quote.setSpecialBidInfo(this.buildSpecialBidInfo(quoteHeader.getWebQuoteNum()));
            }
            
            if (StringUtils.isNotBlank(quoteHeader.getRselCustNum())) {
                logContext.debug(this, "To retrieve Reseller by number: " + quoteHeader.getRselCustNum());
                Partner reseller = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getRselCustNum(), quoteHeader.getLob().getCode());
                quote.setReseller(reseller);
            }
            
         // a section for quote evaluator in the tab sheet of sales information shown, if there have data, else don't display this section.
            initEvalHistory(user, quote);

            //commit the transaction
            this.commitTransaction();
            if(user != null && quoteHeader.isPGSQuote()){
		        SpecialBidInfo spBidInfo = quote.getSpecialBidInfo();
		        
		        List spBidList = QuoteTxtFactory.singleton().getUserDraftComments(quoteHeader.getWebQuoteNum(), SpecialBidInfo.CommentInfo.SPBID_J);
		        List creditList = QuoteTxtFactory.singleton().getUserDraftComments(quoteHeader.getWebQuoteNum(), SpecialBidInfo.CommentInfo.CREDIT_J);
		        
		        if(user.getAudienceCode().equals(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS)){
		        	if(quoteHeader.getPayerCustNum()!=null && user.getWIID()!=null && 
		        			user.getWIID().equals(quoteHeader.getPayerCustNum()) && 
		        			!quoteHeader.getCreatorEmail().equals(user.getEmailAddress())){
	            		spBidInfo.setSpBidJustText("");
	            		spBidInfo.setCreditJustText("");
	                }else{
			        	for(int i=0;i<spBidList.size();i++){
			        		QuoteTxt qtTxt = (QuoteTxt)spBidList.get(i);
			        		if(!qtTxt.getAddByUserName().contains("@")){
			        			spBidInfo.setSpBidJustText(qtTxt.getQuoteText());
			        			break;
			        		}
			        	}
			        	
			        	for(int i=0;i<creditList.size();i++){
			        		QuoteTxt qtTxt = (QuoteTxt)creditList.get(i);
			        		if(!qtTxt.getAddByUserName().contains("@")){
			        			spBidInfo.setCreditJustText(qtTxt.getQuoteText());
			        			break;
			        		}
			        	}
	                }
		        }
            }
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    private void initEvalHistory(QuoteUserSession user, Quote quote) throws TopazException
    {
    	QuoteHeader quoteHeader = quote.getQuoteHeader();
    	if(user != null && quoteHeader.isPGSQuote())
        {
        	if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(user.getAudienceCode()))
        	{
        		// FOR PGS:  the quote is BP quote.
        		quote.setEvalActionHis(EvalFactory.singleton().getEvalActionHis(quoteHeader.getWebQuoteNum()));
        	}else{
        		// FOR SQO: the quote is BP quote and the user is evaluator for this quote: assigned evaluator, same group evaluator. 
        		if(  quoteHeader.isAssignedEval(user.getUserId()) || quoteHeader.isQuoteCntryEvaluator())
        		{
        			quote.setEvalActionHis(EvalFactory.singleton().getEvalActionHis(quoteHeader.getWebQuoteNum()));
        		}
        	}
        }
    }

    public void getQuoteDetailForStatusTab(Quote quote,QuoteUserSession user) throws QuoteException {
		try {
			this.beginTransaction();
			QuoteHeader quoteHeader = quote.getQuoteHeader();
			//quote.setLineItemList(new ArrayList());
			List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                    quoteHeader.getWebQuoteNum());
            quote.setLineItemList(lineItemList);
			if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
				logContext.debug(this, "To retrieve special bid info by webQuoteNum: " + quoteHeader.getWebQuoteNum());
				quote.setSpecialBidInfo(this.buildSpecialBidInfo(quoteHeader.getWebQuoteNum()));
			}
			//Get contact info for sales quote
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum())) {
                logContext.debug(this, "To retrieve QuoteContacts by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                QuoteContact quoteContact = QuoteContactFactory.singleton().findQuoteContact(
                        quoteHeader.getWebQuoteNum(), quoteHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
                List contactList = new ArrayList();
                contactList.add(quoteContact);
                quote.setContactList(contactList);
            }
            //Get payer info for channel quote
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum()) && quoteHeader.isChannelQuote()) {
                if (StringUtils.isNotBlank(quoteHeader.getPayerCustNum())) {
	                logContext.debug(this, "To retrieve Payer by number: " + quoteHeader.getPayerCustNum());
	                Partner payer = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getPayerCustNum(), quoteHeader.getLob().getCode());
	                quote.setPayer(payer);
	            }
                
                if (StringUtils.isNotBlank(quoteHeader.getRselCustNum())) {
                    logContext.debug(this, "To retrieve Reseller by number: " + quoteHeader.getRselCustNum());
                    Partner reseller = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getRselCustNum(), quoteHeader.getLob().getCode());
                    quote.setReseller(reseller);
                }
            }
          //Get fct non std tc attachment list
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum()) && ButtonDisplayRuleFactory.singleton().isDisplayFCTNonStdTermsConds(quoteHeader)) {
            	quote.setFctNonStdTcAttachmentsList(QuoteAttachmentFactory.singleton().getFctNonStdTcAttachments(quoteHeader.getWebQuoteNum()));
            }
            
         // a section for quote evaluator in the tab sheet of sales information shown, if there have data, else don't display this section.
            initEvalHistory(user, quote);
            
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			rollbackTransaction();
		}
	}
    
    @SuppressWarnings("rawtypes")
    public List getQuoteLineItemsForSubmittedCRAD(String webQuoteNum) throws QuoteException {
        try {
            logContext.debug(this, "To retrieve QuoteLineItemList by quote number: " + webQuoteNum);
            List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(webQuoteNum);
            return lineItemList;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        }
    }

    public Quote getQuoteForSubmittedCRAD(String webQuoteNum) throws QuoteException{
    	Quote quote = null;
        try {
            // begin transaction
            this.beginTransaction();

            quote = getQuoteCommonInfo(null, webQuoteNum);
            
            logContext.debug(this, "To retrieve QuoteLineItemList by quote number: " + webQuoteNum);
            List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(webQuoteNum);
            
            quote.setLineItemList(lineItemList);
            return quote;
    	} catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public void updateLineItemCRAD(Quote quote, String userId)throws QuoteException {
        try {
            this.beginTransaction();
            PartPriceProcess process = PartPriceProcessFactory.singleton().create();
            String webQuoteNum = quote.getQuoteHeader().getWebQuoteNum();
            Iterator iter = quote.getLineItemList().iterator();
            while (iter.hasNext()){
            	QuoteLineItem qli = (QuoteLineItem) iter.next();
            	
            	int seqNum = qli.getSeqNum();
            	Date lineItemCRAD = qli.getLineItemCRAD();
            	java.sql.Date qliCrad = null;
            	if (lineItemCRAD!=null){
            		qliCrad = new java.sql.Date(lineItemCRAD.getTime());
            	}
            	
            	QuoteLineItemFactory.singleton().updateLineItemCRAD(webQuoteNum,seqNum,qliCrad, userId);
            	process.addOrUpdateDeploymentId(qli, userId);
            }
            
            
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }

    public void getQuoteDetailForSalesInfoTab(Quote quote) throws QuoteException {
        try {
            //begin the transaction
            this.beginTransaction();
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            if (quoteHeader.getWebQuoteNum() != null
					&& quoteHeader.getWebQuoteNum().trim().length() > 0) {
				logContext.debug(this,"To retrieve DelegatesList by webQuoteNum: "
								+ quoteHeader.getWebQuoteNum());
				List delegatesList = SalesRepFactory.singleton().findDelegatesByQuote(quoteHeader.getWebQuoteNum());
				quote.setDelegatesList(delegatesList);

				List promotionsList = QuotePromotionProcessFactory.singleton()
						.create().findPromotionsByQuote(quoteHeader.getWebQuoteNum());
				quote.setPromotionsList(promotionsList);
			}
            
            if (quoteHeader.getCreatorId() != null && quoteHeader.getCreatorId().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Creator information by creatorId: "
                        + quoteHeader.getCreatorId());
                SalesRep creator = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getCreatorId());
                logContext.debug(this, "The quote creator's information: \n" + creator.toString());
                quote.setCreator(creator);
            }
            if (quoteHeader.getOpprtntyOwnrEmailAdr() != null
                    && quoteHeader.getOpprtntyOwnrEmailAdr().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Opportunity information by owner's email: "
                        + quoteHeader.getOpprtntyOwnrEmailAdr());
                SalesRep oppOwner = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getOpprtntyOwnrEmailAdr());
                logContext.debug(this, "The opportunity owner's information: \n" + oppOwner.toString());
                quote.setOppOwner(oppOwner);
            }
            
            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    
    public void getQuoteDetailForExecSummaryTab(Quote quote) throws QuoteException{
    	try {
            //begin the transaction
            this.beginTransaction();
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            
            if (quoteHeader.getWebQuoteNum() != null
                    && quoteHeader.getWebQuoteNum().trim().length() > 0
                    ) {
                logContext.debug(this, "To retrieve special bid info by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                SpecialBidInfo specialBidInfo = SpecialBidInfoFactory.singleton().findByQuoteNum(
                        quoteHeader.getWebQuoteNum());
                quote.setSpecialBidInfo(specialBidInfo);
                specialBidInfo.getAttachements().clear();
                specialBidInfo.getAttachements().addAll(QuoteAttachmentFactory.singleton().getSpecialBidAttachments(quoteHeader.getWebQuoteNum()));
                specialBidInfo.migrateTextAndAttachment();
            }
            
            if (quoteHeader.getCreatorId() != null && quoteHeader.getCreatorId().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Creator information by creatorId: "
                        + quoteHeader.getCreatorId());
                SalesRep creator = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getCreatorId());
                logContext.debug(this, "The quote creator's information: \n" + creator.toString());
                quote.setCreator(creator);
            }
            if (quoteHeader.getOpprtntyOwnrEmailAdr() != null
                    && quoteHeader.getOpprtntyOwnrEmailAdr().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Opportunity information by owner's email: "
                        + quoteHeader.getOpprtntyOwnrEmailAdr());
                SalesRep oppOwner = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getOpprtntyOwnrEmailAdr());
                logContext.debug(this, "The opportunity owner's information: \n" + oppOwner.toString());
                quote.setOppOwner(oppOwner);
            }
            //get exec summary
            ExecSummary execSummary = ExecSummaryFactory.singleton().findExecSummaryByQuoteNum(quoteHeader.getWebQuoteNum());
            
            quote.setExecSummary(execSummary);
            
            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public QuoteHeader createNewSessionQuote(Country country, CodeDescObj lob, String acqstn, String creatorID,
            String progMigrationCode, String renwlQuoteNum, String audCode) throws QuoteException {
        QuoteHeader quoteHeader = null;
        try {
            //begin the transaction
            this.beginTransaction();

            quoteHeader = QuoteHeaderFactory.singleton().createQuote(creatorID);
            quoteHeader.setCountry(country);
            quoteHeader.setSystemLOB(lob);
            quoteHeader.setAcquisition(acqstn);
            quoteHeader.setProgMigrationCode(progMigrationCode);
            quoteHeader.setRenwlQuoteNum(renwlQuoteNum);
            quoteHeader.setAudCode(audCode);
            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }

        return quoteHeader;
    }

    public void updateQuoteHeaderCustInfo(String creatorId, String webQuoteNum, String customerNum, String contractNum,
            int webCustId, String currencyCode,String endUserFlag) throws QuoteException {
        try {
            //begin the transaction
            this.beginTransaction();

            QuoteHeaderFactory.singleton().updateCustomer(creatorId, webQuoteNum, customerNum, contractNum, webCustId,
                    currencyCode,endUserFlag);

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }

    public void updateQuoteHeaderEndUserInfo(String creatorId, String webQuoteNum, String endCustNum) throws QuoteException {
        try {
            //begin the transaction
            this.beginTransaction();

            QuoteHeaderFactory.singleton().updateEndUser(creatorId, webQuoteNum, endCustNum);

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#getQuoteHeaderInfoByCreatorID(java.lang.String)
     */
    public QuoteHeader getQuoteHdrInfo(String creatorId) throws NoDataException, QuoteException {
        QuoteHeader quoteHeader = null;
        try {
            //begin the transaction
            this.beginTransaction();

            //depends on how SPs are designed
            quoteHeader = QuoteHeaderFactory.singleton().findByCreatorID(creatorId);

            //commit the transaction
            this.commitTransaction();
        } catch (NoDataException nde) {
            throw nde;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }

        return quoteHeader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#getQuoteHdrInfoByWebQuoteNum(java.lang.String)
     */
    public QuoteHeader getQuoteHdrInfoByWebQuoteNum(String webQuoteNum) throws NoDataException, QuoteException {
        QuoteHeader quoteHeader = null;
        try {

            //depends on how SPs are designed
            quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(webQuoteNum);

        } catch (NoDataException nde) {
            throw nde;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } 

        return quoteHeader;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#getQuoteHdrInfoByWebQuoteNum(java.lang.String)
     */
    public QuoteHeader getQuoteHdrInfoByWebQuoteNumAndUserId(String webQuoteNum, String userId) throws NoDataException, QuoteException {
        QuoteHeader quoteHeader = null;
        try {
            //begin the transaction
            this.beginTransaction();

            //depends on how SPs are designed
            quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNumAndUserId(webQuoteNum, userId);

            //commit the transaction
            this.commitTransaction();
        } catch (NoDataException nde) {
            throw nde;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }

        return quoteHeader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.domain.DraftSalesQuoteFactory#deleteQuote(java.lang.String)
     */
    public void deleteQuote(String quoteId, String userId) throws QuoteException {
        QuoteHeader quoteHeader;

        try {
            //begin the transaction
            this.beginTransaction();

            quoteHeader = QuoteHeaderFactory.singleton().createQuote(quoteId);
            quoteHeader.setWebQuoteNum(quoteId);
            quoteHeader.setDeleteBy(userId);
            quoteHeader.delete();

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.DraftQuoteProcess#retrieveDraftQuote()
     */
    public List findDraftQuotes(String userId, String ownerFilter, String timeFilter) throws QuoteException {
        List retrievedDraftQuotes = null;

        try {
            //begin the transaction
            this.beginTransaction();

            if (ownerFilter == null || timeFilter == null || ownerFilter.equals("") || timeFilter.equals(""))
                throw new NewQuoteInvalidInputException();
            
            logContext.debug(this, "To retrieve saved quotes by userId=" + userId + "; ownerFilter=" + ownerFilter
                    + "; timeFilter=" + timeFilter);
            retrievedDraftQuotes = QuoteHeaderFactory.singleton().findSavedQuotes(userId, ownerFilter, timeFilter);

            //commit the transaction
            this.commitTransaction();
        } catch (SPException spe) {
            if (spe.getGenStatus() == CommonDBConstants.DB2_SP_RETURN_SIGN_INPUT_INVALID)
                throw new NewQuoteInvalidInputException();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }

        return retrievedDraftQuotes;
    }

    public void updateQuoteContact(ProcessContract contract) throws QuoteException {
        try {
            PostCustPrtnrTabContract custPrtnrTabContract = (PostCustPrtnrTabContract) contract;
            QuoteHeader qtHeader = this.getQuoteHdrInfo(custPrtnrTabContract.getUserId());
            QuoteContact quoteContact = QuoteContactFactory.singleton().updateQuoteContact(
                    custPrtnrTabContract.getUserId());
            quoteContact.setCntFirstName(custPrtnrTabContract.getCntFirstName());
            quoteContact.setCntLastName(custPrtnrTabContract.getCntLastName());
            quoteContact.setCntPhoneNumFull(custPrtnrTabContract.getCntPhoneNumFull());
            quoteContact.setCntFaxNumFull(custPrtnrTabContract.getCntFaxNumFull());
            quoteContact.setCntEmailAdr(custPrtnrTabContract.getCntEmailAdr());
            quoteContact.setCntPrtnrFuncCode(qtHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        }
    }
    
    public void updateQuoteContactInsubmit(ProcessContract contract,QuoteHeader quoteHeader) throws QuoteException {
    	 try {
    		 SubmitQuoteSubmissionContract submitQuoteSubmissionCrt = (SubmitQuoteSubmissionContract) contract;   		 
    		 QuoteContact quoteContact = QuoteContactFactory.singleton().updateQuoteContact(
    				 submitQuoteSubmissionCrt.getUserId());
             quoteContact.setCntFirstName(submitQuoteSubmissionCrt.getCntFirstName());
             quoteContact.setCntLastName(submitQuoteSubmissionCrt.getCntLastName());
             quoteContact.setCntPhoneNumFull(submitQuoteSubmissionCrt.getCntPhoneNumFull());
             quoteContact.setCntFaxNumFull(submitQuoteSubmissionCrt.getCntFaxNumFull());
             quoteContact.setCntEmailAdr(submitQuoteSubmissionCrt.getCntEmailAdr());
             quoteContact.setCntPrtnrFuncCode(quoteHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
         } catch (TopazException tce) {
             throw new QuoteException(tce);
         }
     }

    public void updateQuoteHeaderCustPrtnrTab(String creatorId,
			Date expireDate, String fullfillmentSrc, String partnerAccess,
			int rselToBeDtrmndFlg, int distribtrToBeDtrmndFlg,
			String quoteClassfctnCode, Date startDate, String oemAgreementType,
			int pymTermsDays, int oemBidType, Date estmtdOrdDate, Date custReqstdArrivlDate,String sspType)
			throws QuoteException {
		try {
			QuoteHeaderFactory.singleton().updateExprDateFullfill(creatorId,
					expireDate, fullfillmentSrc, partnerAccess,
					rselToBeDtrmndFlg, distribtrToBeDtrmndFlg,
					quoteClassfctnCode, startDate, oemAgreementType,
					pymTermsDays, oemBidType, estmtdOrdDate, custReqstdArrivlDate,sspType);
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		}
	}

    public void updateQuoteHeaderSalesInfoTab(String creatorId,
			Date expireDate, String briefTitle, String quoteDesc,
			String busOrgCode, String opprtntyNum, String exemptnCode,
			String upsideTrendTowardsPurch, String salesOdds,
			String tacticCodes, String comments, String quoteClassfctnCode,
			String salesStageCode, String custReasCode, Date startDate,
			String oemAgreementType, int blockRenewalReminder,
			int pymTermsDays, int oemBidType, Date estmtdOrdDate, Date custReqstdArrivlDate,String sspType)
			throws QuoteException {
		try {
			QuoteHeaderFactory.singleton().updateSalesInfo(creatorId,
					expireDate, briefTitle, quoteDesc, busOrgCode, opprtntyNum,
					exemptnCode, upsideTrendTowardsPurch, salesOdds,
					tacticCodes, comments, quoteClassfctnCode, salesStageCode,
					custReasCode, startDate, oemAgreementType,
					blockRenewalReminder, pymTermsDays, oemBidType,
					estmtdOrdDate, custReqstdArrivlDate,sspType);
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		}
	}

    public void saveDraftQuote(String creatorId, boolean createNewCopy) throws QuoteException, SPException {
        try {
            // Begin transaction.
            this.beginTransaction();
            // Save draft quote.
            QuoteHeaderFactory.singleton().saveDraftQuote(creatorId, createNewCopy);
            // End transaction.
            this.commitTransaction();

        } catch (SPException spe) {
            throw spe;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
    }

    /**
     * Get quote partners for display quote submission
     * @param quote
     * @throws QuoteException
     * @throws TopazException
     */
    protected void processPartnersForSubmit(Quote quote, String userID) throws QuoteException, TopazException {
        
        QuoteHeader header = quote.getQuoteHeader();
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        
        // for OEM channel quote
        if (header.isOEMQuote()
                && QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc())
                && StringUtils.isNotBlank(header.getSoldToCustNum())) {
                
            // if reseller is selected but payer is empty, populate customer as payer
            if (StringUtils.isNotBlank(header.getRselCustNum())
                    && StringUtils.isBlank(header.getPayerCustNum())) {
                
                this.updateQuotePartnerInfo(header.getWebQuoteNum(), lob, header.getSoldToCustNum(), String
                        .valueOf(PartnerDBConstants.PARTNER_TYPE_DISTRIBUTOR), userID);
                header.setPayerCustNum(header.getSoldToCustNum());
            }
            // if payer is selected but reseller is empty, populate customer as reseller
            else if (StringUtils.isBlank(header.getRselCustNum())
                    && StringUtils.isNotBlank(header.getPayerCustNum())) {
                
                this.updateQuotePartnerInfo(header.getWebQuoteNum(), lob, header.getSoldToCustNum(), String
                        .valueOf(PartnerDBConstants.PARTNER_TYPE_RESELLER), userID);
                header.setRselCustNum(header.getSoldToCustNum());
            }
        }
        
        boolean isTier1Rsel = false;
        
        if (StringUtils.isNotBlank(header.getRselCustNum())) {
            Partner rsel = PartnerFactory.singleton().findPartnerByNum(header.getRselCustNum(), header.getLob().getCode());
            quote.setReseller(rsel);
            isTier1Rsel = rsel.isTierOne();
        }
        
        // for PA/PAE channel quote, if it is set as single tier and has a tier 1 reseller
        // popuate the reseller as payer
        if ((header.isPAQuote() || header.isPAEQuote() || header.isSSPQuote())
                && QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc())
                && StringUtils.isNotBlank(header.getRselCustNum()) && isTier1Rsel
                && header.isSingleTierNoDistributorFlag()) {
            
            this.updateQuotePartnerInfo(header.getWebQuoteNum(), lob, header.getRselCustNum(), String
                    .valueOf(PartnerDBConstants.PARTNER_TYPE_DISTRIBUTOR), userID);
            header.setPayerCustNum(header.getRselCustNum());
            header.setDistribtrToBeDtrmndFlag(0);
        }
        
        if (StringUtils.isNotBlank(header.getPayerCustNum())) {
            quote.setPayer(PartnerFactory.singleton().findPartnerByNum(header.getPayerCustNum(), header.getLob().getCode()));
        }
        
        // for SSP quote, get end user
        this.getEndUser(quote);
    }
    
    /**
     * Get quote details for display quote submission
     * @param creatorId
     * @throws QuoteException
     * @throws NoDataException
     */
    public Quote getDraftQuoteDetails(String creatorId,boolean isPGSEnv) throws QuoteException, NoDataException {
        Quote quote = null;
        try {
            this.beginTransaction();
            quote = getDraftQuoteBaseInfo(creatorId);
            if (quote == null) {
                return null;
            }
            quote.setPgsAppl(isPGSEnv);
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            
            // populate partners for OEM/PA/PAE/SSP quote if needed, and fetch partners
            processPartnersForSubmit(quote, creatorId);
            
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                        quote.getQuoteHeader().getWebQuoteNum());
                quote.setLineItemList(lineItemList);
                              
                QuoteReasonFactory.singleton().getBackDatingReason(quote.getQuoteHeader());
            }
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve DelegatesList by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                List delegatesList = SalesRepFactory.singleton().findDelegatesByQuote(quoteHeader.getWebQuoteNum());
                quote.setDelegatesList(delegatesList);
            }
            if (quoteHeader.getCreatorId() != null && quoteHeader.getCreatorId().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Creator information by creatorId: "
                        + quoteHeader.getCreatorId());
                SalesRep creator = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getCreatorId());
                quote.setCreator(creator);
            }
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum())) {
                logContext.debug(this, "To retrieve QuoteContacts by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                QuoteContact quoteContact = QuoteContactFactory.singleton().findQuoteContact(
                        quoteHeader.getWebQuoteNum(), quoteHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
                List contactList = new ArrayList();
                contactList.add(quoteContact);
                quote.setContactList(contactList);
            }
            if (quoteHeader.getOpprtntyOwnrEmailAdr() != null
                    && quoteHeader.getOpprtntyOwnrEmailAdr().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Opportunity information by owner's email: "
                        + quoteHeader.getOpprtntyOwnrEmailAdr());
                SalesRep oppOwner = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getOpprtntyOwnrEmailAdr());
                quote.setOppOwner(oppOwner);
            }
            //should not check special bid rules for renewal quote
            if(quote.getQuoteHeader().isSalesQuote()){
            	validateSpecialBidRule(quote, creatorId);
            }


            this.commitTransaction();
        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

        return quote;
    }
    
    public Quote getDraftQuoteDetailsForSubmit(String creatorId) throws QuoteException, NoDataException {
    	return getDraftQuoteDetailsForSubmit(creatorId, false);
    }
    
    
    public Quote getDraftQuoteDetailsForSubmit(String creatorId, boolean isSubmittedByEvaluator) throws QuoteException, NoDataException {
        Quote quote = null;
        try {
            this.beginTransaction();
            quote = getDraftQuoteBaseInfo(creatorId);
            if (quote == null) {
                return null;
            }
            
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            if (quote.getQuoteHeader().isRenewalQuote()) {
                logContext.debug(this, "To retireve SAP quote status by quote num: " + quoteHeader.getWebQuoteNum());
                List statusList = QuoteStatusFactory.singleton().getSapStatusByQuoteNum(quoteHeader.getRenwlQuoteNum());
                if (statusList != null) {
                    for (Iterator iter = statusList.iterator(); iter.hasNext();) {
                        QuoteStatus status = (QuoteStatus) iter.next();
                        if (QuoteConstants.QUOTE_STATUS_PRIMARY.equalsIgnoreCase(status.getStatusType())) {
                            quote.addSapPrimaryStatus(status);
                        } else if (QuoteConstants.QUOTE_STATUS_SECONDARY.equalsIgnoreCase(status.getStatusType())) {
                            quote.addSapSecondaryStatus(status);
                        }
                    }
                }
            }
            if (quoteHeader.getRselCustNum() != null && quoteHeader.getRselCustNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve Reseller by number: " + quoteHeader.getRselCustNum());
                quote.setReseller(PartnerFactory.singleton().findPartnerByNum(quoteHeader.getRselCustNum(), quoteHeader.getLob().getCode()));
            }
            if (quoteHeader.getPayerCustNum() != null && quoteHeader.getPayerCustNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve Payer by number: " + quoteHeader.getPayerCustNum());
                quote.setPayer(PartnerFactory.singleton().findPartnerByNum(quoteHeader.getPayerCustNum(), quoteHeader.getLob().getCode()));
            }
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve QuoteLineItemList by quote number: " + quoteHeader.getWebQuoteNum());
                List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                        quote.getQuoteHeader().getWebQuoteNum());
                quote.setLineItemList(lineItemList);
                quote.setSaaSLineItems(CommonServiceUtil.getSaaSLineItemList(lineItemList));
            }
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve QuoteConfgrtnList by quote number: " + quoteHeader.getWebQuoteNum());
                List confgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(quote.getQuoteHeader().getWebQuoteNum());
            	quote.setPartsPricingConfigrtnsList(confgrtnList);
            }
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve SapLineItemList by quote number: " + quoteHeader.getWebQuoteNum());
                //List sapLineItemList = SapLineItemFactory.singleton().getSapLineItems(quote);
                //quote.setSapLineItemList(sapLineItemList);

            }
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum())) {
                logContext.debug(this, "To retrieve QuoteContacts by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                QuoteContact quoteContact = QuoteContactFactory.singleton().findQuoteContact(
                        quoteHeader.getWebQuoteNum(), quoteHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
                List contactList = new ArrayList();
                contactList.add(quoteContact);
                quote.setContactList(contactList);
            }
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0
                    && quoteHeader.getSpeclBidFlag() == 1) {
                logContext.debug(this, "To retrieve Quote Special Bid information : "
                        + quoteHeader.getOpprtntyOwnrEmailAdr());
                SpecialBidInfo bidInfo = SpecialBidInfoFactory.singleton().findByQuoteNum(quoteHeader.getWebQuoteNum());
                quote.setSpecialBidInfo(bidInfo);

                logContext.debug(this, "To retrieve DelegatesList by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                List delegatesList = SalesRepFactory.singleton().findDelegatesByQuote(quoteHeader.getWebQuoteNum());
                quote.setDelegatesList(delegatesList);

                logContext.debug(this, "To retrieve Quote Creator information by creatorId: "
                        + quoteHeader.getCreatorId());
                SalesRep creator = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getCreatorId());
                quote.setCreator(creator);
            }
            //for submit by evaluator, the evaluation comments are needed
            if ( isSubmittedByEvaluator && quote.getSpecialBidInfo() == null )
            {
            	quote.setSpecialBidInfo(SpecialBidInfoFactory.singleton().findByQuoteNum(quoteHeader.getWebQuoteNum()));
            }
          
            String OpprtntyOwnrEmailAdr = quoteHeader.getOpprtntyOwnrEmailAdr() ;
            if (OpprtntyOwnrEmailAdr != null && OpprtntyOwnrEmailAdr.length()> 0 ){
                // load opportunity owner data 
            logContext.info(this, "To retrieve opportunity owner information by email: "
                    + OpprtntyOwnrEmailAdr);
            SalesRep opprtntyOwnr = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getOpprtntyOwnrEmailAdr());
            quote.setOppOwner(opprtntyOwnr);
            }
            
            //Get end user info when SSP quote
            this.getEndUser(quote);
            
            this.commitTransaction();
        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

        return quote;
    }

    public Quote getDraftQuoteDetailsForQuoteRetrieval(String webQuoteNum) throws QuoteException, NoDataException {
        Quote quote = null;
        try {
            this.beginTransaction();

            quote = getQuoteCommonInfo("", webQuoteNum);
            QuoteHeader quoteHeader = quote.getQuoteHeader();

            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum())) {
                logContext.debug(this, "To retrieve QuoteContacts by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                QuoteContact quoteContact = QuoteContactFactory.singleton().findQuoteContact(
                        quoteHeader.getWebQuoteNum(), quoteHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
                List contactList = new ArrayList();
                contactList.add(quoteContact);
                quote.setContactList(contactList);
            }
            if (quoteHeader.getRselCustNum() != null && quoteHeader.getRselCustNum().trim().length() > 0) {
                quote.setReseller(PartnerFactory.singleton().findPartnerByNum(quoteHeader.getRselCustNum(), quoteHeader.getLob().getCode()));
            }
            if (quoteHeader.getPayerCustNum() != null && quoteHeader.getPayerCustNum().trim().length() > 0) {
                quote.setPayer(PartnerFactory.singleton().findPartnerByNum(quoteHeader.getPayerCustNum(), quoteHeader.getLob().getCode()));
            }
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                
                //For Finalization
                try {
                	quote.fillLineItemsForQuoteBuilder();
                	quote.fillAndSortSaasConfigurationForDraft();
                	quote.fillMonthlySwConfiguration(true);
        			setUpReplacedTerm(quote.getLineItemList());

                    
                    //439814: SaaS 10.18 - Handling line items not part of Add-on/Trade-up for QRWS
                    List findSaaSLineItemsNotPartOfAddOnTradeUpByWebQuoteNumList = QuoteLineItemFactory.singleton().findSaaSLineItemsNotPartOfAddOnTradeUpByWebQuoteNum(
                            quote.getQuoteHeader().getWebQuoteNum());
                    quote.setLineItemListNotPartOfAddOnTradeUp(findSaaSLineItemsNotPartOfAddOnTradeUpByWebQuoteNumList);
                    
        			setUpRenewalCounter(quote);
        		} catch (TopazException e) {
        			logContext.error(this, e.getMessage());
        		}
                
            }
            if(QuoteConstants.SSP_PROVIDER_TYPE_SINGLE.equals(quoteHeader.getSspType())
            		&& quoteHeader.getEndUserCustNum()!=null && quoteHeader.getLob().getCode()!=null){
            	Partner partner = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getEndUserCustNum(), quoteHeader.getLob().getCode());
            	this.getEndUser(quote);
            	
            	SSPEndUser sspEndUser = new SSPEndUser();
            	sspEndUser.setAddressLine1(partner.getAddress1());
            	sspEndUser.setAddressLine2(partner.getAddress2());
            	sspEndUser.setCity(partner.getCity());
            	sspEndUser.setCountryCode(partner.getCountry());
            	sspEndUser.setStateProvinceCode(partner.getState());
            	sspEndUser.setPostalCode(partner.getPostalCode());
            	sspEndUser.setCompanyName1(partner.getCustName());
            	sspEndUser.setCompanyName2(partner.getCustName2());
            	sspEndUser.setDswCustomerNumber(partner.getCustNum());          	
            	
            	quote.setSspEndUser(sspEndUser);
            }

            this.commitTransaction();
        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

        return quote;
    }

    public void postRQStatusTab(String quoteNum, String pStatus, String sStatus, boolean activeSStatus,
            String termReason, String termComment,  String userID) throws QuoteException {
        try {
            QuoteStatusFactory.singleton().validateAndSaveRQStatus(quoteNum, pStatus, 1, true);

            QuoteStatusFactory.singleton().validateAndSaveRQStatus(quoteNum, sStatus, 0, activeSStatus);

            this.updateTerminationTracking(quoteNum, termReason, termComment , userID);
        } catch (TopazException te) {
            throw new QuoteException(te);
        }
    }

    public void updateQuoteSubmission(String creatorId, String webQuoteNum, int reqstICNFlag,
            int reqstPreCreditChkFlag, int inclTaxFlag, int includeFOL, int sendToQTCntFlag, int sendToPrmryCntFlag,
            int sendToAddtnlCntFlag, String addtnlCntEmailAdr, String qtCoverEmail, int incldLineItmDtlQuoteFlg,
            int sendQuoteToAddtnlPrtnrFlg, String addtnlPrtnrEmailAdr, int PAOBlockFlag , int supprsPARegstrnEmailFlag,
            String preCreditCheckedQuoteNum, Integer fctNonStdTermsConds, String quoteOutputType, String softBidIteratnQtInd, String saasBidIteratnQtInd,Integer saaSStrmlndApprvlFlag,String quoteOutputOption,Integer budgetaryQuoteFlag) throws QuoteException {
        try {
            //begin the transaction
            this.beginTransaction();
            QuoteHeaderFactory.singleton().updateQuoteSubmission(creatorId, webQuoteNum, reqstICNFlag,
                    reqstPreCreditChkFlag, inclTaxFlag, includeFOL, sendToQTCntFlag, sendToPrmryCntFlag, sendToAddtnlCntFlag,
                    addtnlCntEmailAdr, qtCoverEmail, incldLineItmDtlQuoteFlg, sendQuoteToAddtnlPrtnrFlg,
                    addtnlPrtnrEmailAdr, PAOBlockFlag, supprsPARegstrnEmailFlag ,
                    preCreditCheckedQuoteNum,fctNonStdTermsConds, quoteOutputType, softBidIteratnQtInd, saasBidIteratnQtInd,saaSStrmlndApprvlFlag,quoteOutputOption,budgetaryQuoteFlag);
            //commit the transaction
            this.commitTransaction();
        } catch (SPException spe) {
            if (spe.getGenStatus() == CommonDBConstants.DB2_SP_RETURN_SIGN_INPUT_INVALID)
                throw new NewQuoteInvalidInputException();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#validateAndSaveRQStatus(java.lang.String,
     *      java.lang.String, int, boolean)
     */
    public void validateAndSaveRQStatus(String quoteNum, String statusCode, int statusType, boolean active)
            throws QuoteException {

        try {
            this.beginTransaction();
            QuoteStatusFactory.singleton().validateAndSaveRQStatus(quoteNum, statusCode, statusType, active);
            this.commitTransaction();
        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }

    public void orderDraftQuote(User user, QuoteUserSession salesRep, Quote quote) throws QuoteException,
            WebServiceException {

        QuoteHeader header = quote.getQuoteHeader();

        try {
            this.beginTransaction();

            // sent quote to SAP
            if (header.isSalesQuote()) {

                logContext.debug(this, "For sales quote, call quote create service.");
                QuoteCreateServiceHelper quoteCreateSevice = new QuoteCreateServiceHelper();
                quoteCreateSevice.callQuoteCreateService(user, salesRep, quote);

            } else if (header.isRenewalQuote()) {

                logContext.debug(this, "For renewal quote, get sap statuses.");
                List statusList = QuoteStatusFactory.singleton().getSapStatusByQuoteNum(header.getRenwlQuoteNum());
                if (statusList != null) {
                    for (Iterator iter = statusList.iterator(); iter.hasNext();) {
                        QuoteStatus status = (QuoteStatus) iter.next();
                        if (QuoteConstants.QUOTE_STATUS_PRIMARY.equalsIgnoreCase(status.getStatusType())) {
                            quote.addSapPrimaryStatus(status);
                        } else if (QuoteConstants.QUOTE_STATUS_SECONDARY.equalsIgnoreCase(status.getStatusType())) {
                            quote.addSapSecondaryStatus(status);
                        }
                    }
                }

                logContext.debug(this, "For renewal quote, call quote modify service.");
                QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();
                quoteModifyService.callQuoteModifyService(user, salesRep, quote);
            }

            this.commitTransaction();
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (WebServiceException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } finally {
            this.rollbackTransaction();
        }
    }

    public void updateQuoteStageForSubmission(String userId, Quote quote) throws QuoteException {
        QuoteHeader header = quote.getQuoteHeader();
        
        // Set quote stage code
        if(header.getSpeclBidFlag() == 1)
            header.setQuoteStageCode(QuoteConstants.QUOTE_STAGE_CODE_SAPSBQT);
        else
            header.setQuoteStageCode(QuoteConstants.QUOTE_STAGE_CODE_SAPQUOTE);
        
        boolean success = true;
        
        // Update quote stage once
        try {
            updateQuoteStage(userId, quote);
        } catch (QuoteException e) {
            success = false;
        }
        
        // If success, check if the quote stage code & sap idoc num are updated correctly
        if (success) {
            success = checkQuoteStage(userId, quote);
        }
        
        // If success & quote stage code updated correctly, return
        if (success) {
            return;
        }
        
        // Update quote stage for the 2nd time
        success = true;
        
        try {
            updateQuoteStage(userId, quote);
        } catch (QuoteException e) {
            success = false;
        }
        
        // If success, check if the quote stage code & sap idoc num are updated correctly
        if (success) {
            success = checkQuoteStage(userId, quote);
        }
        
        if (!success) {
            // If sales quote failed, update current session quote and throw the quote exception
            if (header.isSalesQuote()) {
                substituteSessionQuote(userId, quote.getQuoteHeader().getWebQuoteNum());
            }
            
            throw new QuoteException("The quote stage code is not successfully updated.");
        }
    }
    
    public void updateQuoteStage(String userId, Quote quote) throws QuoteException {
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        
        try {
            this.beginTransaction();
            QuoteHeaderFactory.singleton().updateQuoteStage(userId, quoteHeader.getWebQuoteNum(),
                    quoteHeader.getQuoteStageCode(), quoteHeader.getSapIntrmdiatDocNum());
            this.commitTransaction();
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    protected boolean checkQuoteStage(String userId, Quote quote) {
        boolean success = true;
        String webQuoteNum = quote.getQuoteHeader().getWebQuoteNum();
        String expctdStageCode = quote.getQuoteHeader().getQuoteStageCode();
        String expctdIDocNum = quote.getQuoteHeader().getSapIntrmdiatDocNum();
        
        try {
            this.beginTransaction();
            
            QuoteHeader header = QuoteHeaderFactory.singleton().getQuoteStage(webQuoteNum);
            String quoteStageCode = header.getQuoteStageCode();
            String sapIDocNum = header.getSapIntrmdiatDocNum();
            
            if (!StringUtils.equalsIgnoreCase(expctdStageCode, quoteStageCode)
                    || !StringUtils.equalsIgnoreCase(expctdIDocNum, sapIDocNum))
                success = false;
            
            this.commitTransaction();
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            success = false;
        } finally {
            this.rollbackTransaction();
        }
        
        return success;
    }

    public String copyUpdateSbmtQuoteToSession(String quoteNum, String creatorId, int webRefFlag) throws QuoteException {
        String destQuoteNum = null;
        try {
            this.beginTransaction();
            destQuoteNum = QuoteHeaderFactory.singleton().copyUpdateSbmtQuoteToSession(quoteNum, creatorId, webRefFlag);
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        
        return destQuoteNum;
    }

    public void updateExpICNCRD(String quoteNum, Date expireDate, Integer reqstICNFlag, Integer reqstPreCreditChkFlag,
            String userEmailAdr, Date startDate) throws QuoteException {
        this.updateExpICNCRD(quoteNum, expireDate, reqstICNFlag, reqstPreCreditChkFlag, userEmailAdr, startDate, null);
    }
    
    public void updateSapIDocNum(String webQuoteNum, String sapIDocNum, String userEmailAdr, String userAction) throws QuoteException {
        try {
            this.beginTransaction();
            
            QuoteHeaderFactory.singleton().updateSapIDocNum(webQuoteNum, sapIDocNum, userEmailAdr, userAction);
            
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#addQuoteAuditHist(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void addQuoteAuditHist(String webQuoteNum, Integer lineItemNum, String userEmail, String userAction,
            String oldValue, String newValue) throws QuoteException {
        try {
            this.beginTransaction();

            AuditHistory auditHist = AuditHistoryFactory.singleton().createAuditHistory(webQuoteNum, lineItemNum,
                    userEmail, userAction, oldValue, newValue);

            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#findApprvrActHistsByQuoteNum(java.lang.String)
     */
    public List findApprvrActHistsByQuoteNum(String webQuoteNum) throws QuoteException {
        List actHists = null;
        try {
            this.beginTransaction();

            actHists = SBApprvrActHistFactory.singleton().findActHistsByQuoteNum(webQuoteNum);

            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return actHists;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.process.QuoteProcess#findApprvrsByQuoteNum(java.lang.String)
     */
    public List findApprvrsByQuoteNum(String webQuoteNum) throws QuoteException {
        List apprvrs = null;
        try {
            this.beginTransaction();

            apprvrs = SpecialBidApprvrFactory.singleton().findApprvrsByQuoteNum(webQuoteNum);

            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return apprvrs;
    }
    
    protected void validateSpecialBidRule(Quote quote, String creatorId) throws TopazException, QuoteException {

        logContext.debug(this, "Check special bid rule.");
        SpecialBidRule specialBidRule = SpecialBidRule.create(quote);
        boolean isSpecialBid = specialBidRule.validate(creatorId);
        QuoteHeader header = quote.getQuoteHeader();
        header.setApprovalRouteFlag(specialBidRule.getApprovalRouteFlagAsInt());
 
        QuoteReasonFactory.singleton().updateSpecialBidReason(
                quote.getQuoteHeader().getWebQuoteNum(), specialBidRule.getSpecialBidReason(), creatorId);
        
        if (isSpecialBid && (header.getSpeclBidFlag() != 1 || header.getSpeclBidSystemInitFlg() != 1)) {
            header.setSpeclBidFlag(1);
            header.setSpeclBidSystemInitFlag(1);

            logContext.debug(this, "Update special bid flag and system special bid flag to 1.");
            PartPriceProcess ppProcess = PartPriceProcessFactory.singleton().create();
            ppProcess.updateQuoteHeader(quote.getQuoteHeader(), creatorId);
        }
        else if (!isSpecialBid && header.getSpeclBidSystemInitFlg() != 0) {
            header.setSpeclBidSystemInitFlag(0);
            int manualFlag = header.getSpeclBidManualInitFlg();
            header.setSpeclBidFlag(manualFlag); // special bid flag is determined by manual special bid flag
            
            logContext.debug(this, "Update special bid flag to " + manualFlag + " and system special bid flag to 0.");
            PartPriceProcess ppProcess = PartPriceProcessFactory.singleton().create();
            ppProcess.updateQuoteHeader(quote.getQuoteHeader(), creatorId);
        }

        if (header.getWebQuoteNum() != null && header.getWebQuoteNum().trim().length() > 0
                && header.getSpeclBidFlag() == 1) {
            logContext.debug(this, "To retrieve Quote Special Bid information : "
                    + header.getOpprtntyOwnrEmailAdr());
            SpecialBidInfo bidInfo = SpecialBidInfoFactory.singleton().findByQuoteNum(header.getWebQuoteNum());
            bidInfo.setResellerAuthorizedToPortfolio(specialBidRule.isResellerAuthorizedToPortfolio());
            bidInfo.setExtendedAllowedYears(specialBidRule.isMaintOverDefaultPeriod());
            bidInfo.setPartLessOneYear(specialBidRule.isDateChangeTriggerSB());
            bidInfo.setCompressedCover(specialBidRule.isCmprssCvrageQuote());
            bidInfo.setCpqExcep(specialBidRule.isCpqExcep());
            bidInfo.setOverBySQOBasic(specialBidRule.isConfgrtnOvrrdn());
            bidInfo.setProvisngDaysChanged(specialBidRule.isProvisngDaysChanged());
            bidInfo.setTrmGrt60Mon(specialBidRule.isTrmGrt60Mon());
            quote.setSpecialBidInfo(bidInfo);
            
        }
    }

    public Quote getSpecialBidQuoteInfoForEmail(String webQuoteNum) throws QuoteException, NoDataException{
		Quote quote = null;
		try {
			this.beginTransaction();
			
			logContext.debug(this, "To retrieve Quote Header information by webQuoteNum: "+webQuoteNum);
			QuoteHeader quoteHdr = getQuoteHdrInfoByWebQuoteNum(webQuoteNum);
			quote = new Quote(quoteHdr);
			
			logContext.debug(this, "To retrieve Quote Creator information by creatorId: "
                + quoteHdr.getCreatorId());
			SalesRep creator = SalesRepFactory.singleton().findDelegateByID(quoteHdr.getCreatorId());
			quote.setCreator(creator);
			
//			logContext.info(this, "To retrieve Quote Special Bid information by webQuoteNum: "+webQuoteNum);
//			SpecialBidInfo spbidInfo =  SpecialBidInfoFactory.singleton().findByQuoteNum(webQuoteNum);
//			quote.setSpecialBidInfo(spbidInfo);
			
			logContext.debug(this, "To retrieve DelegatesList by webQuoteNum: " + webQuoteNum);
			List delegatesList = SalesRepFactory.singleton().findDelegatesByQuote(webQuoteNum);
			quote.setDelegatesList(delegatesList);
			
			// get payer info for send mail to vad
			if (quoteHdr.getPayerCustNum() != null && quoteHdr.getPayerCustNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve Payer by number: " + quoteHdr.getPayerCustNum());
                quote.setPayer(PartnerFactory.singleton().findPartnerByNum(quoteHdr.getPayerCustNum(), quoteHdr.getLob().getCode()));
            }
			
			this.commitTransaction();
		} catch (NoDataException e) {
		    logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			throw e;
		} catch (TopazException e) {
		    logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			throw new QuoteException(e);
		} finally {
			rollbackTransaction();
		}
		return quote;
	}
    
    public int updateQuoteComment(String webQuoteNum, int qtTxtId, String qtTxtTypeCode, String qtTxt,
            String userEmail, String sectnId) throws QuoteException {

        QuoteTxt comment = null;

        try {
            //begin topaz transaction
            this.beginTransaction();

            comment = QuoteTxtFactory.singleton().updateQuoteComment(webQuoteNum, qtTxtId, qtTxtTypeCode, qtTxt,
                    userEmail, sectnId);

            //end topaz transaction
            this.commitTransaction();

        } catch (TopazException te) {
            logContext.error(this, te.getMessage());
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }

        return comment == null ? -1 : comment.getQuoteTextId();
    }
    
    public QuoteTxt getRenewalQuoteDetailComment(String webQuoteNum) throws QuoteException {
        QuoteTxt comment = null;
        
        try {
            this.beginTransaction();
            
            comment = QuoteTxtFactory.singleton().getRenewalQuoteDetailComments(webQuoteNum);
            
            this.commitTransaction();

        } catch (TopazException e){
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
        return comment;
    }
    
    public void callServicesToCreateQuote(String userId, User user, QuoteUserSession salesRep, Quote quote)
            throws QuoteException, WebServiceException {
        QuoteHeader header = quote.getQuoteHeader();

        try {
            this.beginTransaction();
            
            SpecialBidRule specialBidRule = SpecialBidRule.create(quote);
            boolean isSpecialBid = specialBidRule.validate(userId);
            
            PartPriceProcess ppProcess = PartPriceProcessFactory.singleton().create();
            ppProcess.updateQuoteHeader(quote.getQuoteHeader(), userId);
            
            QuoteReasonFactory.singleton().updateSpecialBidReason(header.getWebQuoteNum(),
                        specialBidRule.getSpecialBidReason(), userId);
            
            if (header.isRenewalQuote()) {
                logContext.debug(this, "To call SAP RFC to modify the renewal quote in SAP...");

                QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();
                quoteModifyService.callQuoteModifyService(user, salesRep, quote);
            } else {
                logContext.debug(this, "To call SAP RFC to create the sales quote in SAP...");

                QuoteCreateServiceHelper quoteCreateService = new QuoteCreateServiceHelper();
                quoteCreateService.callQuoteCreateService(user, salesRep, quote);
            }

            this.commitTransaction();
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            this.rollbackConnection();
            throw new QuoteException(e);
        } catch (WebServiceException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            this.rollbackConnection();
            throw e;
        } finally {
            this.rollbackTransaction();
        }
    }
    
    public void updateQuoteExecSummary(String webQuoteNum, String userId, Boolean recmdtFlag, 
    		                           String recmdtText, Double periodBookableRevenue, Double serviceRevenue, 
									   String execSupport, String briefOverviewText) throws QuoteException{
    	try{
    		this.beginTransaction();
    		
    		ExecSummary summary = ExecSummaryFactory.singleton().create();
    		summary.setWebQuoteNum(webQuoteNum);
    		summary.setUserId(userId);
    		summary.setPeriodBookableRevenue(periodBookableRevenue);
    		summary.setServiceRevenue(serviceRevenue);
    		summary.setRecmdtFlag(recmdtFlag);
    		summary.setRecmdtText(recmdtText);
    		summary.setExecSupport(execSupport);
    		summary.setBriefOverviewText(briefOverviewText);
    		
    		this.commitTransaction();
    		
    	}catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            this.rollbackConnection();
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    public int createOrUpdateWebCtrct(int webCtrctId, String agrmntType, String authrztnGroup, String transSVPLevel, String userID) throws QuoteException {
        
        Contract contract = new Contract();
        
        try{
    		this.beginTransaction();
    		
    		contract.setWebCtrctId(webCtrctId);
    		contract.setSapContractVariantCode(agrmntType);
    		contract.setAuthrztnGroup(authrztnGroup);
    		contract.setVolDiscLevelCode(transSVPLevel);
    		
    		ContractFactory.singleton().createOrUpdateWebCtrct(contract, userID);
    		
    		this.commitTransaction();
    		
    	}catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
        webCtrctId = contract.getWebCtrctId();
        return webCtrctId;
    }
    
    public Contract getContractByNum(String sapCtrctNum, String lob) throws QuoteException {
        Contract contract = null;
        
        try{
    		this.beginTransaction();
    		
    		contract = ContractFactory.singleton().getContractByNum(sapCtrctNum, lob);
    		
    		this.commitTransaction();
    		
    	}catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
        return contract;
    }
    
    public void unlockQuote(String webQuoteNum, String creatorId) throws QuoteException {
        try {
            //begin the transaction
            this.beginTransaction();

            QuoteHeaderFactory.singleton().unlockQuote(webQuoteNum, creatorId);

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    private void rollbackConnection() throws QuoteException {
        try {
            TransactionContextManager.singleton().getConnection().rollback();
        } catch (TopazException e) {
            throw new QuoteException(e);
        } catch (SQLException e) {
            throw new QuoteException(e);
        }
    }
        
    public Quote getQuoteByNumForTest(String webQuoteNum,boolean isPGSEnv) throws QuoteException
    {
        Quote quote = null;
        try {
            this.beginTransaction();
            quote = this.getQuoteCommonInfo(null, webQuoteNum);
            boolean isSubmittedQuote = quote.getQuoteHeader().isSubmittedQuote();
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            QuoteHeader header = quoteHeader;
            String creatorID = quoteHeader.getCreatorId();
            String userID = creatorID;
            // get max expiration days for all quotes:
            // for sales quote, get max quote expiration days
            // for renewal quote, get max speical bid expiration days, if quote is non-special-bid, it won't be used.
            int maxExpDays = QuoteExpireDaysFactory.singleton().findMaxQuoteExpireDays(quoteHeader);
            quoteHeader.setQuoteExpDays(maxExpDays);
            
            if (quoteHeader.isRenewalQuote()) {
                // To get QuoteAccess
                String rnwlQuoteNum = quoteHeader.getRenwlQuoteNum();
                QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();

                logContext.debug(this, "To retrieve QuoteAccess by renewal quote number: " + rnwlQuoteNum);
                QuoteAccess quoteAccess = qcProcess.getRnwlQuoteAccess(creatorID, rnwlQuoteNum);
                logContext.debug(this, "QuoteAccess:\n" + quoteAccess.toString());
                quote.setQuoteAccess(quoteAccess);
            }
            
            //process parnter for submit
            String lob = header.getLob() == null ? "" : header.getLob().getCode();
            
            // for OEM channel quote
            if (header.isOEMQuote()
                    && QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc())
                    && StringUtils.isNotBlank(header.getSoldToCustNum())) {
                    
                // if reseller is selected but payer is empty, populate customer as payer
                if (StringUtils.isNotBlank(header.getRselCustNum())
                        && StringUtils.isBlank(header.getPayerCustNum())) {
                    
//                    this.updateQuotePartnerInfo(header.getWebQuoteNum(), lob, header.getSoldToCustNum(), String
//                            .valueOf(PartnerDBConstants.PARTNER_TYPE_DISTRIBUTOR), userID);
                    header.setPayerCustNum(header.getSoldToCustNum());
                }
                // if payer is selected but reseller is empty, populate customer as reseller
                else if (StringUtils.isBlank(header.getRselCustNum())
                        && StringUtils.isNotBlank(header.getPayerCustNum())) {
                    
//                    this.updateQuotePartnerInfo(header.getWebQuoteNum(), lob, header.getSoldToCustNum(), String
//                            .valueOf(PartnerDBConstants.PARTNER_TYPE_RESELLER), userID);
                    header.setRselCustNum(header.getSoldToCustNum());
                }
            }
            
            boolean isTier1Rsel = false;
            
            if (StringUtils.isNotBlank(header.getRselCustNum())) {
                Partner rsel = PartnerFactory.singleton().findPartnerByNum(header.getRselCustNum(), header.getLob().getCode());
                quote.setReseller(rsel);
                isTier1Rsel = rsel.isTierOne();
            }
            
            // for PA/PAE channel quote, if it is set as single tier and has a tier 1 reseller
            // popuate the reseller as payer
            if ((header.isPAQuote() || header.isPAEQuote() || header.isSSPQuote())
                    && QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc())
                    && StringUtils.isNotBlank(header.getRselCustNum()) && isTier1Rsel
                    && header.isSingleTierNoDistributorFlag()) {
                
//                this.updateQuotePartnerInfo(header.getWebQuoteNum(), lob, header.getRselCustNum(), String
//                        .valueOf(PartnerDBConstants.PARTNER_TYPE_DISTRIBUTOR), userID);
                header.setPayerCustNum(header.getRselCustNum());
                header.setDistribtrToBeDtrmndFlag(0);
            }
            
            if (StringUtils.isNotBlank(header.getPayerCustNum())) {
                quote.setPayer(PartnerFactory.singleton().findPartnerByNum(header.getPayerCustNum(), header.getLob().getCode()));
            }
            //end process partner for submit
            
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                /*List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                        quote.getQuoteHeader().getWebQuoteNum());
                quote.setLineItemList(lineItemList);
                              
                QuoteReasonFactory.singleton().getBackDatingReason(quote.getQuoteHeader());*/
            	
            	User user = new UserWrapper(null);
            	((UserWrapper)user).setEmail(creatorID);
            	((UserWrapper)user).getAccessLevels().put(QuoteConstants.APP_CODE_SQO, QuoteConstants.ACCESS_LEVEL_SUBMITTER);
            	
            	if (isSubmittedQuote) {
					SubmittedQuoteBuilder.create(quote, user).build();
					if (quote.getLineItemList().size() > 0 || quote.getSaaSLineItems().size() > 0) {
						PartPriceHelper.calculateQuoteTotalPrice(quote);
					}
				}else {
					DraftQuoteBuilder quoteBuilder = DraftQuoteBuilder.create(quote, creatorID);
					quoteBuilder.build();
				}
            }
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve DelegatesList by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                List delegatesList = SalesRepFactory.singleton().findDelegatesByQuote(quoteHeader.getWebQuoteNum());
                quote.setDelegatesList(delegatesList);
            }
            if (quoteHeader.getCreatorId() != null && quoteHeader.getCreatorId().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Creator information by creatorId: "
                        + quoteHeader.getCreatorId());
                SalesRep creator = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getCreatorId());
                quote.setCreator(creator);
            }
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum())) {
                logContext.debug(this, "To retrieve QuoteContacts by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                QuoteContact quoteContact = QuoteContactFactory.singleton().findQuoteContact(
                        quoteHeader.getWebQuoteNum(), quoteHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
                List contactList = new ArrayList();
                contactList.add(quoteContact);
                quote.setContactList(contactList);
            }
            if (quoteHeader.getOpprtntyOwnrEmailAdr() != null
                    && quoteHeader.getOpprtntyOwnrEmailAdr().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Opportunity information by owner's email: "
                        + quoteHeader.getOpprtntyOwnrEmailAdr());
                SalesRep oppOwner = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getOpprtntyOwnrEmailAdr());
                quote.setOppOwner(oppOwner);
            }
            
            //validate special bid rule
            logContext.debug(this, "Check special bid rule.");
            String creatorId = creatorID;
            SpecialBidRule specialBidRule = SpecialBidRule.create(quote);
            boolean isSpecialBid = specialBidRule.validate(creatorId);
            header.setApprovalRouteFlag(specialBidRule.getApprovalRouteFlagAsInt());
            
//            QuoteReasonFactory.singleton().updateSpecialBidReason(
//                    quote.getQuoteHeader().getWebQuoteNum(), specialBidRule.getSpecialBidReason(), creatorId);
            
            if (isSpecialBid && (header.getSpeclBidFlag() != 1 || header.getSpeclBidSystemInitFlg() != 1)) {
                header.setSpeclBidFlag(1);
                header.setSpeclBidSystemInitFlag(1);

                logContext.debug(this, "Update special bid flag and system special bid flag to 1.");
//                PartPriceProcess ppProcess = PartPriceProcessFactory.singleton().create();
//                ppProcess.updateQuoteHeader(quote.getQuoteHeader(), creatorId);
            }
            else if (!isSpecialBid && header.getSpeclBidSystemInitFlg() != 0) {
                header.setSpeclBidSystemInitFlag(0);
                int manualFlag = header.getSpeclBidManualInitFlg();
                header.setSpeclBidFlag(manualFlag); // special bid flag is determined by manual special bid flag
                
                logContext.debug(this, "Update special bid flag to " + manualFlag + " and system special bid flag to 0.");
//                PartPriceProcess ppProcess = PartPriceProcessFactory.singleton().create();
//                ppProcess.updateQuoteHeader(quote.getQuoteHeader(), creatorId);
            }

            if (header.getWebQuoteNum() != null && header.getWebQuoteNum().trim().length() > 0
                    && header.getSpeclBidFlag() == 1) {
                logContext.debug(this, "To retrieve Quote Special Bid information : "
                        + header.getOpprtntyOwnrEmailAdr());
                SpecialBidInfo bidInfo = SpecialBidInfoFactory.singleton().findByQuoteNum(header.getWebQuoteNum());
                bidInfo.setResellerAuthorizedToPortfolio(specialBidRule.isResellerAuthorizedToPortfolio());
                bidInfo.setExtendedAllowedYears(specialBidRule.isMaintOverDefaultPeriod());
                bidInfo.setPartLessOneYear(specialBidRule.isDateChangeTriggerSB());
                bidInfo.setCompressedCover(specialBidRule.isCmprssCvrageQuote());
                bidInfo.setCpqExcep(specialBidRule.isCpqExcep());
                bidInfo.setOverBySQOBasic(specialBidRule.isConfgrtnOvrrdn());
                bidInfo.setProvisngDaysChanged(specialBidRule.isProvisngDaysChanged());
                bidInfo.setTrmGrt60Mon(specialBidRule.isTrmGrt60Mon());
                quote.setSpecialBidInfo(bidInfo);
                
            }
            
            //end validate special bid rule
            
//            this.commitTransaction();
        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return quote;
    }
    public List getSQOHeadLineMsg(String applCode) throws QuoteException {
        List headLineMsg = new ArrayList();
        try {
            this.beginTransaction();

            headLineMsg = SQOHeadLineFactory.singleton().getSQOHeadLineMsg(applCode);

            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return headLineMsg;
    }
    

    /**
     * Get submitted quote info as a draft quote for some special copy usage.
     * @param webQuoteNum
     * @param creatorId
     * @return
     * @throws QuoteException
     * @throws NoDataException
     */
    public Quote getSubmittedQuoteDetailsAsDraft(String webQuoteNum,String creatorId) throws QuoteException, NoDataException {
        Quote quote = null;
        try {
            this.beginTransaction();
            
            quote = this.getSubmittedQuoteDetailsAsDraftWithoutTransaction(webQuoteNum, creatorId);
            
            this.commitTransaction();
        } catch (NoDataException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw e;
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

        return quote;
    }
    

    public Quote getSubmittedQuoteDetailsAsDraftWithoutTransaction(String webQuoteNum,String creatorId) throws QuoteException, TopazException {
        
            Quote quote = getSubmittedQuoteBaseInfo(webQuoteNum, creatorId, "");
            if (quote == null) {
                return null;
            }

            QuoteHeader quoteHeader = quote.getQuoteHeader();
            
            // populate partners for OEM/PA/PAE/SSP quote if needed, and fetch partners
            processPartnersForSubmit(quote, creatorId);
            
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                        quote.getQuoteHeader().getWebQuoteNum());
                quote.setLineItemList(lineItemList);
                              
                QuoteReasonFactory.singleton().getBackDatingReason(quote.getQuoteHeader());
            }
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0) {
                logContext.debug(this, "To retrieve DelegatesList by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                List delegatesList = SalesRepFactory.singleton().findDelegatesByQuote(quoteHeader.getWebQuoteNum());
                quote.setDelegatesList(delegatesList);
            }
            if (quoteHeader.getCreatorId() != null && quoteHeader.getCreatorId().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Creator information by creatorId: "
                        + quoteHeader.getCreatorId());
                SalesRep creator = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getCreatorId());
                quote.setCreator(creator);
            }
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum())) {
                logContext.debug(this, "To retrieve QuoteContacts by webQuoteNum: " + quoteHeader.getWebQuoteNum());
                QuoteContact quoteContact = QuoteContactFactory.singleton().findQuoteContact(
                        quoteHeader.getWebQuoteNum(), quoteHeader.isSSPQuote()? QuoteConstants.PARTNER_FUNC_CODE_ZG:QuoteConstants.PARTNER_FUNC_CODE_ZW);
                List contactList = new ArrayList();
                contactList.add(quoteContact);
                quote.setContactList(contactList);
            }
            if (quoteHeader.getOpprtntyOwnrEmailAdr() != null
                    && quoteHeader.getOpprtntyOwnrEmailAdr().trim().length() > 0) {
                logContext.debug(this, "To retrieve Quote Opportunity information by owner's email: "
                        + quoteHeader.getOpprtntyOwnrEmailAdr());
                SalesRep oppOwner = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getOpprtntyOwnrEmailAdr());
                quote.setOppOwner(oppOwner);
            }
            

            SpecialBidRule specialBidRule = SpecialBidRule.create(quote);
            if (quoteHeader.getWebQuoteNum() != null && quoteHeader.getWebQuoteNum().trim().length() > 0
                    && quoteHeader.getSpeclBidFlag() == 1) {
                logContext.debug(this, "To retrieve Quote Special Bid information : "
                        + quoteHeader.getOpprtntyOwnrEmailAdr());
                SpecialBidInfo bidInfo = SpecialBidInfoFactory.singleton().findByQuoteNum(quoteHeader.getWebQuoteNum());
                bidInfo.setResellerAuthorizedToPortfolio(specialBidRule.isResellerAuthorizedToPortfolio());
                bidInfo.setExtendedAllowedYears(specialBidRule.isMaintOverDefaultPeriod());
                bidInfo.setPartLessOneYear(specialBidRule.isDateChangeTriggerSB());
                bidInfo.setCompressedCover(specialBidRule.isCmprssCvrageQuote());
                bidInfo.setCpqExcep(specialBidRule.isCpqExcep());
                bidInfo.setOverBySQOBasic(specialBidRule.isConfgrtnOvrrdn());
                bidInfo.setProvisngDaysChanged(specialBidRule.isProvisngDaysChanged());
                bidInfo.setTrmGrt60Mon(specialBidRule.isTrmGrt60Mon());
                quote.setSpecialBidInfo(bidInfo);
                
            }


        return quote;
    }
    

    public void deleteQuoteDeeply(String quoteId) throws QuoteException{
        try {
            this.beginTransaction();
            
            this.deleteQuoteDeeplyWithoutTransaction(quoteId);
            
            this.commitTransaction();
        } catch(Throwable t){
        	if(t instanceof QuoteException){
        		throw (QuoteException)t;
        	}else{
        		throw new QuoteException("exception["+t.getMessage()+
        				"] occur when deleteQuoteDeeplyWithoutTransaction",t);
        	}
        } finally {
            this.rollbackTransaction();
        }
    }


    public void deleteQuoteDeeplyWithoutTransaction(String quoteId) throws TopazException{
    	QuoteHeaderFactory.singleton().deleteQuoteDeeply(quoteId);
    }
    
    public Quote getQuoteDetailsForCreateQtFromOrder(String webQuoteNum) throws QuoteException, NoDataException {
        Quote quote = null;
        QuoteHeader quoteHeader = null;
        try {
            //begin the transaction
            this.beginTransaction();

            //get quote head info
            quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(webQuoteNum);
            quote = new Quote(quoteHeader);
            //get quote line items list
            List lineItemList = QuoteLineItemFactory.singleton().findPartsWithoutValidation(webQuoteNum);
            quote.setLineItemList(lineItemList);
            //commit the transaction
            this.commitTransaction();
        } catch (NoDataException nde) {
            throw nde;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return quote;
    }
    public void delInvalidLineItemsByWebQuoteNum(String webQuoteNum, List invalidPartList) throws QuoteException {
        try {
            logContext.debug(this, "To delete parts by webQuoteNum: " + webQuoteNum);
            
            this.beginTransaction();
            
            QuoteLineItemFactory.singleton().delInvalidLineItemsByWebQuoteNum(webQuoteNum, invalidPartList);
            
            this.commitTransaction();
            
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
    }
    public List getFctNonStdTcAttachments(QuoteHeader quoteHeader) throws QuoteException {
    	List fctNonStdTcAttachments=null;
		try {
			this.beginTransaction();
          //Get fct non std tc attachment list
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum())
            		&& ButtonDisplayRuleFactory.singleton().isDisplayFCTNonStdTermsConds(quoteHeader)) {
            	logContext.debug(this, "To retrieve FctNonStdTc attachments by webQuoteNum: "
                        + quoteHeader.getWebQuoteNum());            	            	
            	fctNonStdTcAttachments= QuoteAttachmentFactory.singleton().getFctNonStdTcAttachments(quoteHeader.getWebQuoteNum());
            }
			this.commitTransaction();
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			rollbackTransaction();
		}
		return fctNonStdTcAttachments;
	}    
    /**
     * For FCT to PA Finalization, including Migration and Add On , needs to set up replaced term.
     * Only for Subscription Part.
     * @param lineItems
     * @throws TopazException
     */
    private void setUpReplacedTerm(List lineItems) throws TopazException{
    	if(lineItems == null || lineItems.size() == 0)
    		return;
    	//1. get one subscription part term, which is not replaced part.
    	int term = 0;
    	//int repTerm = 0;
    	String webQuoteNumber = "";
    	String configId = "";
    	boolean isExtension = false;
        for(int i=0;i<lineItems.size();i++){
        	QuoteLineItem_Impl lineItem = (QuoteLineItem_Impl) lineItems.get(i);
        	webQuoteNumber = lineItem.getQuoteNum();
        	configId = lineItem.getConfigrtnId();
            if(!lineItem.isReplacedPart() && (lineItem.isSaasSubscrptnPart() || lineItem.isSaasSubsumedSubscrptnPart())){
            	term = lineItem.getICvrageTerm() == null ? 0 : Integer.valueOf(lineItem.getICvrageTerm());
            	//repTerm = lineItem.getCumCvrageTerm() == null ? 0 : Integer.valueOf(lineItem.getCumCvrageTerm());
            	break;
            }
        }
        if(configId!=null && configId.length() >0 && webQuoteNumber.length() > 0){        	
        	List confgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(webQuoteNumber);
        	for(int i=0;i<confgrtnList.size();i++){
        		PartsPricingConfiguration config = (PartsPricingConfiguration) confgrtnList.get(i);
        		if(configId.equals(config.getConfigrtnId())){
        			isExtension = config.isTermExtension();
        			break;
        		}
        	}
        }
        //2. set up replaced term for replaced subscription parts.
        for(int i=0;i<lineItems.size();i++){
        	QuoteLineItem_Impl lineItem = (QuoteLineItem_Impl) lineItems.get(i);
            if(lineItem.isReplacedPart() && (lineItem.isSaasSubscrptnPart() || lineItem.isSaasSubsumedSubscrptnPart())){
            	if(isExtension){
            		//if term extension, 
					//if part renewal model is C
					//if part is replaced part, set term to 0
						if(PartPriceConstants.RenewalModelCode.C.equals(lineItem.getRenwlMdlCode())){
							lineItem.replacedTerm = new Integer(0);
						}else{
							lineItem.replacedTerm = lineItem.getCumCvrageTerm() == null ? 0 : Integer.valueOf(lineItem.getCumCvrageTerm());
						}
            	}else{            		
            		lineItem.replacedTerm = term;
            	}
            }
        }
    }    
    /**
     * For FCT to PA Finalization, including Migration and Add On , needs to set up replaced term.
     * Only for Subscription Part.
     * @param lineItems
     * @throws TopazException
     */
    private void setUpRenewalCounter(Quote quote) throws TopazException{
        
		List configrtnsList = quote.getPartsPricingConfigrtnsList();
		if (configrtnsList == null || configrtnsList.size() == 0)
			return;
		Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
		Iterator configrtnsIt = configrtnsList.iterator();
		ConfiguratorPartProcess process = null;
		try {
			process = ConfiguratorPartProcessFactory.singleton().create();
		} catch (QuoteException e) {
			logContext.error(this, e.getMessage());
		}
		while(configrtnsIt.hasNext()){
			PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt.next();

			if(PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode())
					|| PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ppc.getConfigrtnActionCode())){
				List subSaaSlineItemList = (List)configrtnsMap.get(ppc);
				if(subSaaSlineItemList == null) continue;
				
				Map<String, ConfiguratorPart> configrtrPartMap = null;
				if(PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode())){
					configrtrPartMap = process.findMainPartsFromChrgAgrm(quote.getQuoteHeader().getRefDocNum(), ppc.getConfigrtnId());
				}else if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ppc.getConfigrtnActionCode())){
					String orignlConfigrtnId = QuoteCommonUtil.getOrignlConfigrtnIdForFnl(subSaaSlineItemList,ppc);
					configrtrPartMap = process.findMainPartsFromChrgAgrm(quote.getQuoteHeader().getRefDocNum(), orignlConfigrtnId);
				}
				Object[] configrtrParts = configrtrPartMap.values().toArray();
				int renwlCounter = 0;
				for(int i = 0; i < configrtrParts.length; i++){
					ConfiguratorPart cp = (ConfiguratorPart) configrtrParts[i];
					if(cp.getRenwlCounter() !=null){
						renwlCounter = cp.getRenwlCounter().intValue();
						break;
					}
				}
					               
				 for(int i=0;i<subSaaSlineItemList.size();i++){
			            QuoteLineItem_Impl lineItem = (QuoteLineItem_Impl) subSaaSlineItemList.get(i);
			            if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ppc.getConfigrtnActionCode())){
			            	lineItem.renewalCounter = renwlCounter;
			            }else if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode())){
				            if(lineItem.isReplacedPart() && lineItem.isSaasSubscrptnPart()){
				            	lineItem.renewalCounter = renwlCounter;
				            }
			            }
			     }
			}
		}
    }
    
    public ApproverRuleValidation filterApprover(QuoteHeader header, List groups) throws QuoteException
    {
    	this.logContext.debug(this, "filter approver: bidIter=" + header.isBidIteratnQt() + ";streamLined=" + header.isSaaSStrmlndApprvlFlag());
    	ApproverRuleValidation ret = null;
    	if ( header.isBidIteratnQt() )
    	{
    		ret = filterBidIterQtApr(header.getPriorQuoteNum(), buildApprovalInfo(groups), header.getSoftBidIteratnQtInd(), header.getSaasBidIteratnQtInd());
    		if ( !ret.isBidIterFlag() )
    		{
    			ret.getSaasErrorCodeList().add(QuoteConstants.BidIterationErrorMsg.APPROVAL_TYPE_AND_LEVEL_HAVE_CHANGE);
    		}
    		return ret;
    	}
    	else if ( header.isSaaSStrmlndApprvlFlag() )
    	{
    		String geo = header.getCountry().getSpecialBidAreaCode().trim();
    		String progCode = header.getLob().getCode().trim();
    		if ( progCode.startsWith("PA") )
    		{
    			progCode = "PAUN";
    		}
    		ret = filterStrmlndQtApr(geo, progCode, buildApprovalInfo(groups));
    		return ret;
    	}
    	ret = new ApproverRuleValidation();
    	ret.setBidIterFlag(false);
    	ret.setStreamLineFlag(false);
    	return ret;
    }
    
    //Get end user info for SSP quote
    private void getEndUser(Quote quote) throws QuoteException{

        String endUserCustNum = quote.getQuoteHeader().getEndUserCustNum();
        Customer endUser;
        try {
	        if( quote.getQuoteHeader().isSSPQuote()){
	        	int endUserWebCustId = quote.getQuoteHeader().getEndUserWebCustId();
	        	int newCustFlag = StringUtils.isBlank(endUserCustNum) ? 1 : 0;
	        	if(endUserWebCustId > 0 || StringUtils.isNotBlank(endUserCustNum)){
					 endUser = CustomerFactory.singleton().findByQuoteNum(quote.getQuoteHeader().getLob().getCode(), newCustFlag, endUserCustNum, null, quote.getQuoteHeader().getWebQuoteNum(), endUserWebCustId);
	        		 quote.setEndUser(endUser);
	        	}
	        }
		} catch (TopazException e) {
			throw new QuoteException(e);
		}
    }
    private String buildApprovalInfo(List groups){
        //'type xxx@@1##type xxx@@1##type yyy@@2##'
        
        if (groups == null || groups.size() == 0){
            return null;
        }
        
        StringBuffer buffer = new StringBuffer();
        
        for (int i = 0; i < groups.size(); i++) {
            ApprovalGroup currGroup = (ApprovalGroup) groups.get(i);
            
            String typeName = currGroup.getType().getTypeName();
            int level = currGroup.getType().getLevel();
            
            buffer.append(typeName).append("@@").append(level).append("##");
        }
        
        return buffer.toString();
    }
    
    private SpecialBidInfo buildSpecialBidInfo(String webQuoteNumber) throws TopazException{
    	//Written by Emily on Feb 20,2012
    	//this function is used to set special bid info for special bid and status tab
        SpecialBidInfo specialBidInfo = SpecialBidInfoFactory.singleton().findByQuoteNum(webQuoteNumber);
        List approverComments = specialBidInfo.getApproverComments();
        for(int i=0;i<approverComments.size();i++){
        	SpecialBidInfo.ApproverComment approverComment = (SpecialBidInfo.ApproverComment)approverComments.get(i);
        	String returnReason = StringUtils.trim(approverComment.getReturnReason());
        	if(StringUtils.isNotBlank(returnReason)){
        		String reasonDesc = ReturnReasonFactory.singleton().getReasonDescByCode(returnReason);
        		approverComment.returnReason = reasonDesc;
        	}
        }
        specialBidInfo.getAttachements().clear();
        specialBidInfo.getAttachements().addAll(QuoteAttachmentFactory.singleton().getSpecialBidAttachments(webQuoteNumber));
        specialBidInfo.migrateTextAndAttachment();  
        
        return specialBidInfo;
    }
    
    protected abstract ApproverRuleValidation filterBidIterQtApr(String orignQuoteNum, String approvalInfo, int softBidIter, int saasBidIter) throws QuoteException;
    
    protected abstract ApproverRuleValidation filterStrmlndQtApr(String geo, String progCode, String approvalInfo) throws QuoteException;
    
    
    public void updateExpICNCRD(String quoteNum, Date expireDate, Integer reqstICNFlag, Integer reqstPreCreditChkFlag,
            String userEmailAdr, Date startDate, Date cradDate) throws QuoteException {
        try {
            this.beginTransaction();
            QuoteHeaderFactory.singleton().updateExpICNCRD(quoteNum, expireDate, reqstICNFlag, reqstPreCreditChkFlag,
                    userEmailAdr, startDate, cradDate);
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public void updateTouFlag(String webQuoteNum, int saasTermCondCatFlag, String touURLs, String termsTypes, String termsSubTypes, String radioFlags, int updateTouFlag) throws QuoteException {
        try {
            this.beginTransaction();
            QuoteHeaderFactory.singleton().updateTouFlag(webQuoteNum, saasTermCondCatFlag, touURLs, termsTypes,
            		termsSubTypes, radioFlags, updateTouFlag, "");
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public void updateTouFlag(String webQuoteNum) throws QuoteException {
        try {
            this.beginTransaction();
            QuoteHeaderFactory.singleton().updateTouFlag(webQuoteNum, 0, "", "", "", "", 1, "");
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public boolean isPlaceHolderTou(String touURL) throws QuoteException{
    	boolean isPlaceHolder = false;
    	try {
            this.beginTransaction();
            isPlaceHolder = QuoteHeaderFactory.singleton().isPlaceHolderTou(touURL);
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return isPlaceHolder;
    }
    
    public Quote getQuoteForToUMailOutByQuoteNum(String webQuoteNum) throws QuoteException, NoDataException{
    	try {
            //begin the transaction
            this.beginTransaction();
            
            Quote quote = getQuoteCommonInfo(null, webQuoteNum);
            
            //commit the transaction
            this.commitTransaction();
            return quote;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    
    public void updateWarningTouFlag(String webQuoteNum, String userId, String yesFlags, String noFileTous) throws QuoteException{
    	try {
            this.beginTransaction();
            QuoteHeaderFactory.singleton().updateWarningTouFlag(webQuoteNum,userId,yesFlags,noFileTous);
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public boolean updateQuoteAgrmtTypeByTou(String webQuoteNum,String agrmtTypeCode,String agrmtNum) throws QuoteException{
    	boolean isUpdateQuoteAgrmt = false;
    	try {
            this.beginTransaction();
            QuoteHeaderFactory.singleton().updateTouFlag(webQuoteNum, 0, "", "", "", "", 3, agrmtTypeCode);
            isUpdateQuoteAgrmt=  QuoteHeaderFactory.singleton().updateQuoteAgrmtTypeByTou(webQuoteNum, agrmtTypeCode, agrmtNum);
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    	return isUpdateQuoteAgrmt;
    }
    public Customer findCustForExistCtrctCust(String webQuoteNum,String custNum) throws QuoteException{
	   Customer customer = null;
    try {
        //begin the transaction
        this.beginTransaction();
        customer  = CustomerFactory.singleton().findCustForExistCtrctCust(webQuoteNum, custNum);
        //commit the transaction
        this.commitTransaction();
    } catch (TopazException tce) {
        throw new QuoteException(tce);
    } finally {
        rollbackTransaction();
    }
    return customer;
	
} 
    public void updateQuoteExpirationDateExtension(String webQuoteNum, String userId,Date expireDate,String justification,String updateSavedQuoteFlag)
			throws QuoteException {
	 
		try {
            this.beginTransaction();
            QuoteHeaderFactory.singleton().updateQuoteExpirationDateExtension(webQuoteNum,userId,
					expireDate, justification, updateSavedQuoteFlag);
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    public QuoteHeader getQuoteHdrInfoByWebQuoteNumWithTransaction(String webQuoteNum) throws NoDataException, QuoteException {
        QuoteHeader quoteHeader = null;
        try {
        	//begin the transaction
            this.beginTransaction();
            //depends on how SPs are designed
            quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(webQuoteNum);

            //commit the transaction
            this.commitTransaction();
        } catch (NoDataException nde) {
            throw nde;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } 

        return quoteHeader;
    }
}
