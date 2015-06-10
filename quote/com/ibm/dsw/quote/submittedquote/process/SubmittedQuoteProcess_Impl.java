package com.ibm.dsw.quote.submittedquote.process;

import is.domainx.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.DocumentStatus;

import com.ibm.dsw.quote.appcache.domain.ReturnReasonFactory;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.InvalidWSInputException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.AuditHistoryFactory;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteReasonFactory;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.common.domain.QuoteStatusFactory;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.QuoteTxtFactory;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidApprvrOjbect;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo.ChosenApprover;
import com.ibm.dsw.quote.common.domain.SpecialBidInfoFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.RowExistingException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.common.service.QuoteStatusChangeService;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.price.IncreasePricingCalculator;
import com.ibm.dsw.quote.mail.config.MailConstants;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.dsw.quote.mail.process.MailProcessFactory;
import com.ibm.dsw.quote.mail.process.SpecialBidEmailHelper;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.domain.ReviewerFactory;
import com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHist;
import com.ibm.dsw.quote.submittedquote.domain.SBApprvrActHistFactory;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvrFactory;
import com.ibm.dsw.quote.submittedquote.exception.DuplicatedReviewerException;
import com.ibm.dsw.quote.submittedquote.util.SubmittedDateUpdater;
import com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteRequest;
import com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteResponse;
import com.ibm.dsw.quote.submittedquote.util.builder.SubmittedQuoteBuilder;
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
 * The <code>SubmittedQuoteProcess_Impl</code> class.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public abstract class SubmittedQuoteProcess_Impl extends TopazTransactionalProcess implements SubmittedQuoteProcess {

    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see ibm.com.dsw.quote.submittedquote.process.SubmittedQuoteProcess#addReviewer(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public void addReviewer(String webQuoteNum, String userEmailAdr, String rvwrEmailAdr,int quoteTxtId) throws QuoteException {
        try {
            this.beginTransaction();
//            Reviewer reviewer = 
            ReviewerFactory.singleton().addReviewer(webQuoteNum, userEmailAdr, rvwrEmailAdr,quoteTxtId);
            this.commitTransaction();
        } catch (RowExistingException tce) {
           
            throw new DuplicatedReviewerException(tce);
        } catch (TopazException tce) {
           
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } catch(Exception e){
            logContext.error(this, e);
        }finally {
            rollbackTransaction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#findByWebQuoteNum(java.lang.String)
     */
    public List findReviewersByWebQuoteNum(String webQuoteNum) throws QuoteException {
        List resultList = null;
        try {
            this.beginTransaction();
            SubmittedQuoteProcess partProcess = SubmittedQuoteProcessFactory.singleton().create();
            resultList = partProcess.findReviewersByWebQuoteNum(webQuoteNum);
            this.commitTransaction();
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }

    public QuoteHeader getQuoteHeaderByQuoteNum(String quoteNum) throws QuoteException {
        QuoteHeader quoteHeader = null;
        try {
            this.beginTransaction();
            quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(quoteNum);
            this.commitTransaction();
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
        return quoteHeader;
    }

    public Quote getSubmittedQuoteForCancel(String webQuoteNum) throws NoDataException, QuoteException {
        Quote quote = null;

        try {
            //begin the transaction
            this.beginTransaction();

            logContext.debug(this, "To retireve quote header by quote num: " + webQuoteNum);
            QuoteHeader quoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(webQuoteNum);

            quote = new Quote(quoteHeader);
            List statusList = null;
            logContext.debug(this, "To retireve primary and secondary quote status by quote num: " + webQuoteNum);
            if(StringUtils.isNotEmpty(quoteHeader.getSapQuoteNum())){
                statusList= QuoteStatusFactory.singleton().getSapStatusByQuoteNum(quoteHeader.getSapQuoteNum());
            }
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
            //For PGS quote, need get creator, payer for mail
            if ( quoteHeader.isPGSQuote() )
            {
            	logContext.debug(this, "To retrieve Quote Creator information by creatorId: "
                        + quoteHeader.getCreatorId());
        			SalesRep creator = SalesRepFactory.singleton().findDelegateByID(quoteHeader.getCreatorId());
        			quote.setCreator(creator);
        			
        			
        			// get payer info for send mail to vad
        			if (quoteHeader.getPayerCustNum() != null && quoteHeader.getPayerCustNum().trim().length() > 0) {
                        logContext.info(this, "To retrieve Payer by number: " + quoteHeader.getPayerCustNum());
                        quote.setPayer(PartnerFactory.singleton().findPartnerByNum(quoteHeader.getPayerCustNum(), quoteHeader.getLob().getCode()));
                    }
            }

            //commit the transaction
            this.commitTransaction();
        } catch (NoDataException nde) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(nde));
            throw nde;
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }

        return quote; 
    }

   /*
    *  (non-Javadoc)
    * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#submitApprvrAction(java.lang.StringBuffer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
    public boolean submitApprvrAction(String webQuoteNum, String userId, String apprvrAction,
            String userRole, String apprvrComments, QuoteUserSession salesRep, boolean isCanSupersedeAppr,
            	String returnReason,int apprvrLevel) throws QuoteException {
        
        try {
            this.beginTransaction();
            //userRole=approver-level-1, approver-level-2...
            int apprvlLevel  = Integer.parseInt(StringUtils.substringAfterLast(userRole, "-")); 
            int supersedeApprFlag = isCanSupersedeAppr ? 1:0;
            //submit approver action to db2
            SpecialBidApprvr specialBidApprvr = SpecialBidApprvrFactory.singleton().createSpecialBidApprvrForActionUpdate();
            specialBidApprvr.setWebQuoteNum(webQuoteNum);
            specialBidApprvr.setApprvrEmail(userId);
            specialBidApprvr.setApprvrAction(apprvrAction);
            specialBidApprvr.setSpecialBidApprLvl(apprvlLevel);
            specialBidApprvr.setSupersedeApprvFlag(supersedeApprFlag);
            specialBidApprvr.setReturnReason(returnReason);
            //submit approver comments to db2
            persistApproverActHist(webQuoteNum, userId, userRole, apprvrAction, apprvrComments,returnReason);
            
            if(StringUtils.equals(SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO, apprvrAction)){         	
            	String oldReturnCode = getReturnReasonCode(webQuoteNum,apprvrLevel);
            	AuditHistoryFactory.singleton().createAuditHistory(webQuoteNum, 0, userId, 
            			apprvrAction,apprvrLevel, oldReturnCode, returnReason);            	
            }
            this.commitTransaction();

            return true;
        } catch (Exception e) {
            logContext.error(this, "Fatal error -- Can't add approval action to quote:" + webQuoteNum);
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

/**
    *  Call quote status change service to notify SAP of the approver action
    * @param webQuoteNum
    * @param apprvrAction
    * @return
    * @throws TopazException
 * @throws QuoteException
    */

    public int updateQuoteStatusByApprvrAction(Quote quote, User user, String apprvrAction,
            QuoteUserSession salesRep, int apprvrLevel) throws QuoteException {
        
        int result = SubmittedQuoteConstants.SAP_SUCCESS;
        
        if (StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO)) {
            logContext.debug(this, "No SAP status updates are required for this action RETURN_FOR_ADD_INFO");
            return result;
        }
        
        ArrayList docStatList = new ArrayList();
        DocumentStatus docStatus = null;
//        boolean isSuccessful = false;
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        String webQuoteNum = quoteHeader.getWebQuoteNum();
        String apprvrEmail = user.getEmail().toLowerCase();
        
        //retrieve SpecialBidInfo from db2
        SpecialBidInfo sbInfo = null;
        try {
             sbInfo = SpecialBidInfoFactory.singleton().findByQuoteNum(webQuoteNum);
        } catch (TopazException tze) {
            logContext.error(this, "can't find sbInfo for quote number:" + webQuoteNum);
            logContext.error(this, tze);
            throw new QuoteException(tze);
        }        
        
        //cancel the special bid in SAP
        if (StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_CHANGES)
                || StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_REJECT)) {
            
            if (quoteHeader.isRenewalQuote()) { // renewal quote
                docStatus = new DocumentStatus();
                docStatus.setStatusCode(SubmittedQuoteConstants.RQ_SPECIAL_BID_REJECTED_STATUS);
                docStatus.setActiveFlag(true);
                docStatList.add(docStatus);
                
                docStatus = null;
                docStatus = new DocumentStatus();
                docStatus.setStatusCode(SubmittedQuoteConstants.RQ_SPECIAL_BID_REQUESTED_STATUS);
                docStatus.setActiveFlag(false);
                docStatList.add(docStatus);
            } else {// sales quote
                docStatus = new DocumentStatus();
                docStatus.setStatusCode(SubmittedQuoteConstants.SQ_SPECIAL_BID_REJECTED_STATUS);
                docStatus.setActiveFlag(true);
                docStatList.add(docStatus);

                docStatus = null;
                docStatus = new DocumentStatus();
                docStatus.setStatusCode(SubmittedQuoteConstants.SQ_SPECIAL_BID_REQUESTED_STATUS);
                docStatus.setActiveFlag(false);
                docStatList.add(docStatus);
            }

        } 
        // Submit to approve the special bid in SAP
        else if (apprvrAction.equalsIgnoreCase(SubmittedQuoteConstants.APPRVR_ACTION_APPROVE)) {
            List allChoosenApprovers = sbInfo.getChosenApprovers();
            if ( allChoosenApprovers != null
            		&& (allChoosenApprovers.size() == 1 
            		     || isOnlyOneManualApprover(allChoosenApprovers, apprvrLevel)) && quoteHeader.isSalesQuote() )
            {
                logContext.debug(this, "The first approver(also final approver) is approving the quote, call SAP and send all line items... ");
                result = updateQtInSapAfterFirstApprvl(quote, user, this.getFinalApproveStatus(quoteHeader, sbInfo));
                return result;
            }
            else if (isFirstApprover(quote, allChoosenApprovers, apprvrLevel) && quoteHeader.isSalesQuote()){
            	if(quoteHeader.hasSaaSLineItem() && quoteHeader.isAddTrd()){
            		// Update increaseBidTCV to database.
            		try {
            			quote.fillLineItemsForQuoteBuilder();
						quote.fillAndSortSaasConfigurationForSubmitted();
						QuoteCommonUtil.calculateRemainingTermTillCAEndDate(quote);
						SubmittedQuoteBuilder.calculateIncreaseUnusedTCV(quote, true);
						List saasConfigrtnList = quote.getPartsPricingConfigrtnsList();
	                    ListIterator li = saasConfigrtnList.listIterator();
	                    StringBuilder configrtnIds = new StringBuilder();
	                    StringBuilder increaseBidTCVs =new StringBuilder();
	                    StringBuilder unUsedTCVs = new StringBuilder();
	                    while(li.hasNext()) {
	                		PartsPricingConfiguration cli = (PartsPricingConfiguration) li.next();
	                		if(cli.getConfigrtnActionCode() != null && cli.getConfigrtnActionCode().equals("AddTrd") && cli.getIncreaseBidTCV() !=null){
	                			configrtnIds.append(cli.getConfigrtnId() +",");
	                			increaseBidTCVs.append(cli.getIncreaseBidTCV()+",");
	                			unUsedTCVs.append(cli.getUnusedBidTCV()+",");
	                			
	                		}
	                    }
	                    PartPriceProcess partPriceProcess = PartPriceProcessFactory.singleton().create();
	            		partPriceProcess.updateIncreaseBidTCV(quoteHeader.getWebQuoteNum(), configrtnIds.toString(), increaseBidTCVs.toString(),unUsedTCVs.toString());
	            		this.logContext.debug(this, "Bid is always re-priced when 1st level approved (TCV net increase gets calculated)");
					} catch (TopazException e) {
						// TODO Auto-generated catch block
						logContext.error(this, "Fill And Sort Saas Configuration For Submitted, return"+e);
					}
                    
            	}
                logContext.debug(this, "The first approver is approving the quote, call SAP and send all line items... ");
                result = updateQtInSapAfterFirstApprvl(quote, user, null);
                if (result != SubmittedQuoteConstants.SAP_SUCCESS)
                    return result;                
            }
            if (!isSBApprovedByAllApprovers(webQuoteNum, apprvrLevel, allChoosenApprovers)) {
                logContext.debug(this, "No SAP status updates are required if not all approvers have approved the bid.");
                return SubmittedQuoteConstants.SAP_SUCCESS;
            }
            //If all approvals were received
            //the quote tool sets the status in SAP to special bid approved.
            logContext.debug(this, "is final approve, send status to SAP");
            docStatList.addAll(this.getFinalApproveStatus(quoteHeader, sbInfo));
            
        }
        
        logContext.debug(this, "Calling SAP webservice quoteModifyService.modifyQuoteStatus");
        
        result = SubmittedQuoteConstants.SAP_SUCCESS;
        try {
            this.beginTransaction();
            
            QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();
            quoteModifyService.modifyQuoteStatus(quoteHeader, docStatList, salesRep);
            
            this.commitTransaction();
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            result = SubmittedQuoteConstants.SAP_FAILED;
        } catch (WebServiceException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            result = SubmittedQuoteConstants.SAP_FAILED;
        } finally {
            this.rollbackTransaction();
        }
        
        if (result == SubmittedQuoteConstants.SAP_SUCCESS) {
        	QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        	quoteProcess.updateSapIDocNum(webQuoteNum, quoteHeader.getSapIntrmdiatDocNum(), apprvrEmail, converseUserAction(apprvrAction));
        }

        return result;
    }
    
    protected ArrayList getFinalApproveStatus(QuoteHeader quoteHeader, SpecialBidInfo sbInfo)
    {
        ArrayList docStatList = new ArrayList();
        DocumentStatus docStatus = null;
        if (quoteHeader.isRenewalQuote()) { // renewal quote

            docStatus = new DocumentStatus();
            docStatus.setStatusCode(SubmittedQuoteConstants.RQ_SPECIAL_BID_APPROVED_STATUS);
            docStatus.setActiveFlag(true);
            docStatList.add(docStatus);

            docStatus = null;
            docStatus = new DocumentStatus();
            docStatus.setStatusCode(SubmittedQuoteConstants.RQ_SPECIAL_BID_REQUESTED_STATUS);
            docStatus.setActiveFlag(false);
            docStatList.add(docStatus);
            
            // Fix the ebiz ticket BJJK-7JRJQ3: Fix price discrepancy issue on FCT renewal quotes
            // When the final approver approves an FCT renewal special bid quote, set the status to "Open"
        	if (quoteHeader.isFCTQuote()) {
        		docStatus = new DocumentStatus();
                docStatus.setStatusCode(QuoteConstants.RENEWALSTATUS_CODE_OPEN);
                docStatus.setActiveFlag(true);
                docStatList.add(docStatus);
            }
           
            //If the quote requires a Ts & Cs change or contract-level pricing set up,
            //the quote tool will set the appropriate status in SAP at the same time
            if (sbInfo.isTermsAndCondsChg()) {
                docStatus = new DocumentStatus();
                docStatus.setStatusCode(SubmittedQuoteConstants.RQ_TS_CS_SPECIAL_BID_HOLD_STATUS);
                docStatus.setActiveFlag(true);
                docStatList.add(docStatus);
            }
            if (sbInfo.isSetCtrctLvlPricng()) {
                docStatus = new DocumentStatus();
                docStatus.setStatusCode(SubmittedQuoteConstants.RQ_PREAPPROVED_CONTRACT_PRICING_STATUS);
                docStatus.setActiveFlag(true);
                docStatList.add(docStatus);
            }

        } else {// sales quote
            docStatus = new DocumentStatus();
            docStatus.setStatusCode(SubmittedQuoteConstants.SQ_SPECIAL_BID_APPROVED_STATUS);
            docStatus.setActiveFlag(true);
            docStatList.add(docStatus);

            docStatus = null;
            docStatus = new DocumentStatus();
            docStatus.setStatusCode(SubmittedQuoteConstants.SQ_SPECIAL_BID_REQUESTED_STATUS);
            docStatus.setActiveFlag(false);
            docStatList.add(docStatus);

            //If the quote requires a Ts & Cs change or contract-level pricing set up,
            //the quote tool will set the appropriate status in SAP at the same time
            if (sbInfo.isTermsAndCondsChg()) {
                docStatus = new DocumentStatus();
                docStatus.setStatusCode(SubmittedQuoteConstants.SQ_TS_CS_SPECIAL_BID_HOLD_STATUS);
                docStatus.setActiveFlag(true);
                docStatList.add(docStatus);
            }
            if (sbInfo.isSetCtrctLvlPricng()) {
                docStatus = new DocumentStatus();
                docStatus.setStatusCode(SubmittedQuoteConstants.SQ_PREAPPROVED_CONTRACT_PRICING_STATUS);
                docStatus.setActiveFlag(true);
                docStatList.add(docStatus);
            }
        }
        return docStatList;
    }

    /**
     * 
     * @param webQuoteNum
     * @return
     * @throws QuoteException
     */
    public boolean isFirstApprover(Quote quote, List chosenApprovers, int currentApprvrLevel) throws QuoteException {
    	//all ready to order approver before the current approver will be ignored
    	List sortList = sortChosenApprovers(chosenApprovers);
    	
		boolean allPreviousAreReadyToOrder = true;
		boolean noPreviousReadyToOrder = true;
    	if(sortList != null && sortList.size() > 0){
    		for(Iterator it = sortList.iterator(); it.hasNext(); ){
    			ChosenApprover approver = (ChosenApprover)it.next();
    			
    			if(approver.getGroupLevel() >= currentApprvrLevel){
    				break;
    			}
    			
    			if(approver.getRdyToOrder() == 0){
    				allPreviousAreReadyToOrder = false;
    			} else {
    				noPreviousReadyToOrder = false;
    			}
    		}
    	}
    	
    	if(noPreviousReadyToOrder){
    		//follow BAU logic
            if(quote.getQuoteUserAccess().isNoneApproval()){
                return true;
            } else {
            	return false;
            }
    	} else {
    		if(allPreviousAreReadyToOrder){
        		return true;
        	} else {
        		return false;
        	}
    	}
    }
    
    //check if the final manual approver is the only man approvers: all previous approvers are marked as ready to order
    private boolean isOnlyOneManualApprover(List chosenApprovers, int apprvrLevel) throws QuoteException {
    	
    	if(chosenApprovers != null && chosenApprovers.size() > 0){
    		int rdyToOrderCount = 0;
    		
    		for(Iterator it = chosenApprovers.iterator(); it.hasNext(); ){
    			ChosenApprover approver = (ChosenApprover)it.next();
    			
    			//before the last approver, all approvers are ready to order
				if(approver.getRdyToOrder() == 0){
					rdyToOrderCount++;
    			}
    		}
    		
    		return rdyToOrderCount == 1;
    		
    	} else {
    		return false;
    	}
    }

    /**
     * 
     * @param webQuoteNum
     * @return
     * @throws TopazException
     */
    
    private List sortChosenApprovers(List chosenApprovers){
    	//all ready to order approver before the current approver will be ignored
    	List sortList = new ArrayList();
    	sortList.addAll(chosenApprovers);
    	Collections.sort(sortList, new Comparator(){
    		 public int compare(Object o1, Object o2) {
    			 ChosenApprover approver1 = (ChosenApprover) o1;
    			 ChosenApprover approver2 = (ChosenApprover) o2;
                 return new Integer(approver1.getGroupLevel()).compareTo(new Integer(approver2.getGroupLevel()));
             }
    	});
    	
    	return sortList;
    }
    
    private boolean isSBApprovedByAllApprovers(String webQuoteNum, int apprvrLevel, List chosenApprovers) throws QuoteException {

    	List sortList = sortChosenApprovers(chosenApprovers);
		boolean hasAfter = false;
		boolean allAfterAreRdyToAfter = true;
    	if(sortList != null && sortList.size() > 0){
    		for(Iterator it = sortList.iterator(); it.hasNext(); ){
    			ChosenApprover approver = (ChosenApprover)it.next();
    			
    			if(approver.getGroupLevel() <= apprvrLevel){
    				continue;
    			}
    			
    			hasAfter = true;
    			
    			if(approver.getRdyToOrder() == 0){
    				allAfterAreRdyToAfter = false;
    			}
    		}
    	}
    	
    	if(hasAfter && allAfterAreRdyToAfter){
    		return true;
    	}
    	
    	List approvers;
        try {
            approvers = SpecialBidApprvrFactory.singleton().findApprvrsByQuoteNum(webQuoteNum);
        } catch (TopazException e) {
            logContext.error(this, "can'f find approvers for quote number:" + webQuoteNum);
            logContext.error(this, e);
            throw new QuoteException(e);
        }
        
        if(approvers == null){
        	return false;
        }
        Collections.sort(approvers, new Comparator() {
            public int compare(Object o1, Object o2) {
                SpecialBidApprvr approver1 = (SpecialBidApprvr) o1;
                SpecialBidApprvr approver2 = (SpecialBidApprvr) o2;
                return new Integer(approver1.getSpecialBidApprLvl()).compareTo(new Integer(approver2.getSpecialBidApprLvl()));
            }
         });
        int apprvrsize = approvers.size();
        SpecialBidApprvr finalApprvr = (SpecialBidApprvr)approvers.get(apprvrsize - 1);
        if ( finalApprvr.getSpecialBidApprLvl() <= apprvrLevel )
        {
            return true;
        }    	
        return false;
    }

  /*
   *  (non-Javadoc)
   * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#persistApproverAction(java.lang.String, java.lang.String, java.lang.String)
   */
    public SpecialBidApprvr persistApproverAction(String webQuoteNum, String apprvrEmail, String apprvrAction, int apprvrLevel)
            throws QuoteException {
        SpecialBidApprvr specialBidApprvr = null;
         try {
            this.beginTransaction();
            specialBidApprvr = SpecialBidApprvrFactory.singleton().createSpecialBidApprvrForActionUpdate();
            specialBidApprvr.setWebQuoteNum(webQuoteNum);
            specialBidApprvr.setApprvrEmail(apprvrEmail);
            specialBidApprvr.setApprvrAction(apprvrAction);
            specialBidApprvr.setSpecialBidApprLvl(apprvrLevel);
            this.commitTransaction();
        } catch (Exception e) {
              throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

        return specialBidApprvr;
    }
    
    public SalesRep getSalesRepById(String userId) throws QuoteException {
    	SalesRep rep = null;
    	try {
    		this.beginTransaction();
    		rep = SalesRepFactory.singleton().findDelegateByID(userId);
	
    		this.commitTransaction();
    	} catch (Exception e) {
	      throw new QuoteException(e);
    	} finally {
	    this.rollbackTransaction();
    	}

    	return rep;
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#persistApproverActHist(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void persistApproverActHist(String webQuoteNum, String userEmail, String userRole, String userAction,
            String quoteTxt,String returnReason) throws Exception {
        SBApprvrActHist sbApprvrActHist;
        sbApprvrActHist = SBApprvrActHistFactory.singleton().createSBApprvrActHist();
        sbApprvrActHist.setWebQuoteNum(webQuoteNum);
        sbApprvrActHist.setUserEmail(userEmail);
        sbApprvrActHist.setUserRole(userRole);
        sbApprvrActHist.setUserAction(userAction);
        sbApprvrActHist.setQuoteTxt(quoteTxt);
        sbApprvrActHist.setReturnReason(returnReason);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#getApproverActHistsByQuoteNum(java.lang.String)
     */
    public List getApproverActHistsByQuoteNum(String quoteNum) throws QuoteException {
        List result = null;
        try {
            this.beginTransaction();
            result = SBApprvrActHistFactory.singleton().findActHistsByQuoteNum(quoteNum);
            this.commitTransaction();
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        return result == null ? new ArrayList() : result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#getActiveApproverByWebQuoteNumber(java.lang.String)
     */
    public SpecialBidApprvr getCurrentApprover(String quoteNum) throws QuoteException {
    	SpecialBidApprvr result = null;
        try {
            this.beginTransaction();
            List approvers = SpecialBidApprvrFactory.singleton().findApprvrsByQuoteNum(quoteNum);
            if(approvers == null){
                return result;
            }
            Collections.sort(approvers, new Comparator() {
                public int compare(Object o1, Object o2) {
                    SpecialBidApprvr approver1 = (SpecialBidApprvr) o1;
                    SpecialBidApprvr approver2 = (SpecialBidApprvr) o2;
                    return new Integer(approver1.getSpecialBidApprLvl()).compareTo(new Integer(approver2.getSpecialBidApprLvl()));
                }
            });
            
            for (int i = 0; i < approvers.size(); i++) {
                SpecialBidApprvr approver = (SpecialBidApprvr) approvers.get(i);                
                
                String action = approver.getApprvrAction();
                if (StringUtils.equals(action, SubmittedQuoteConstants.APPRVR_ACTION_APPROVE)) {            
                    continue;
                }else if (approver.getSupersedeApprvFlag() != 1 ){                    
                    result = approver;
                    break;
                }
            }
            this.commitTransaction();
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            this.rollbackTransaction();
        }
        return result;
    }

    /*
     *  (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#notifyWorkflowOfApprvrAction(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean notifyWorkflowOfApprvrAction(String webQuoteNum, String currentApprEmail, SpecialBidApprvrOjbect specialBidApprvr,
            String apprvrAction, String apprvrComments, String bidExpireDate,String returnReasonDesc) throws QuoteException {
        boolean isSuccessful = false;
        SpecialBidEmailHelper helper;
        try {
            helper = new SpecialBidEmailHelper();

            if (StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO)) {
                logContext.debug(this, "Calling special bid work flow serviceHelper.additionalInfoNeeded");
                isSuccessful = helper.additionalInfoNeeded(currentApprEmail, specialBidApprvr, webQuoteNum, apprvrComments,returnReasonDesc);
            } else if (StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_CHANGES)) {
                logContext.debug(this, "Calling special bid work flow serviceHelper.returnForChanges");
                isSuccessful = helper.returnForChanges(currentApprEmail, specialBidApprvr, webQuoteNum, apprvrComments,returnReasonDesc);
            } else if (StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_REJECT)) {
                logContext.debug(this, "Calling special bid work flow serviceHelper.rejectSpecialBid");
                isSuccessful = helper.rejectSpecialBid(currentApprEmail, specialBidApprvr, webQuoteNum, apprvrComments,returnReasonDesc);
            } else if (apprvrAction.equalsIgnoreCase(SubmittedQuoteConstants.APPRVR_ACTION_APPROVE)) {
                logContext.debug(this, "Calling special bid work flow serviceHelper.approveSpecialBid");
                isSuccessful = helper.approveSpecialBid(currentApprEmail, specialBidApprvr, webQuoteNum, apprvrComments, bidExpireDate);
            }
        } catch (QuoteException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            isSuccessful = false;
            throw e;
        }
        return isSuccessful;
    }
    
    public void getExecPartPriceInfo(Quote quote, User user) throws QuoteException{
    	this.beginTransaction();
    	try{
    		 SubmittedQuoteBuilder builder = SubmittedQuoteBuilder.create(quote, user);
             builder.build();
             
             commitTransaction();
    	}catch (TopazException e) {

            throw new QuoteException(e);

        } finally {
            this.rollbackTransaction();
        }
    }

    public SubmittedQuoteResponse getSubmittedPartPriceInfo(SubmittedQuoteRequest request)
            throws QuoteException{

        SubmittedQuoteResponse response = new SubmittedQuoteResponse();
        
        Quote quote = request.getQuote();
      
        try {
        	  this.beginTransaction();
              
              if (request.needUpdateLineItemDate()) {
                  // update the date in UI
                  // in this method , lineItem / sap lineItem already be read into quote object, and data
                  // already be committed to db2 if pricing service and quote modify is ok
                  
                  SubmittedDateUpdater updater = new SubmittedDateUpdater(request,response);
                  updater.execute();
              }
              
              if(request.needCalculatePriceIncrease()){
                  double offerPrice = Double.parseDouble(request.getOfferPrice());
                  
                  if(offerPriceChanged(quote.getQuoteHeader().getOfferPrice(), offerPrice)){
                      
                      IncreasePricingCalculator calculator = new IncreasePricingCalculator(request, response);
                      calculator.calculate();
                  }
              }        

            QuoteReasonFactory.singleton().getBackDatingReason(quote.getQuoteHeader());
            
            SubmittedQuoteBuilder builder = SubmittedQuoteBuilder.create(quote,request.getContract().getUser());
            if(quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()
                    && QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
                builder.buildForPriceIncrease();
                
            } else {
                builder.build(); 
                
                if (builder.isSapCallFailed()) {
                    response.setPriceEngineFailed(true);
                }
            }
            
            commitTransaction();
        } catch (TopazException e) {

            throw new QuoteException(e);

        } finally {
            this.rollbackTransaction();
        }

        return response;
    }

    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#persistApproverActHistWithTransaction(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void persistApproverActHistWithTransaction(String webQuoteNum, String userId, String userRole, String userAction, String quoteTxt) throws QuoteException {
//        SpecialBidApprvr apprvr = null;
        try {
            this.beginTransaction();
            persistApproverActHist(webQuoteNum, userId, userRole, userAction, quoteTxt,"");
            this.commitTransaction();
        } catch(Exception e){
            logContext.error(this, e);
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }
    
    public void updateQuoteStageToCancel(String userId, String webQuoteNum) throws QuoteException {

        try {
            this.beginTransaction();
            QuoteHeaderFactory.singleton().updateQuoteStageToCancel(userId, webQuoteNum);
            this.commitTransaction();
        } catch (TopazException e) {
            logContext.debug(this, "Exception catched when update quote stage to cancel.");
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

    /**
     * Method updateQtInSapAfterFirstApprvl is to:
     * 1) Update part price for 1st approve action
     * 2) if docStatList is not null, then that means this is also the final approve 
     * @param quote
     * @param user
     * @param salesRep
     * @param docStatList
     * @return
     * @throws QuoteException
     */
    private int updateQtInSapAfterFirstApprvl(Quote quote, User user, ArrayList docStatList) throws QuoteException {
        int result = SubmittedQuoteConstants.SAP_SUCCESS;
        String userId = user.getEmail().toLowerCase();
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        String webQuoteNum = quoteHeader.getWebQuoteNum();
            
        try {
            this.beginTransaction();

            SubmittedQuoteBuilder builder = SubmittedQuoteBuilder.create(quote, user);
            builder.build();
            if(builder.isSapCallFailed()){
                logContext.info(this, "Failed to update part price.");
                return SubmittedQuoteConstants.SAP_FAILED;
            }
            
            //PartPriceProcess process = PartPriceProcessFactory.singleton().create();
            //process.updateQuoteHeader(quote.getQuoteHeader());
            
            this.commitTransaction();
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        
        
        try
        {
            QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();
            quoteModifyService.updateQuoteStatusByApprvrAction(quote, docStatList);
            
            //To update the sap idoc num
            QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
            quoteProcess.updateSapIDocNum(webQuoteNum, quoteHeader.getSapIntrmdiatDocNum(), userId,  SubmittedQuoteConstants.USER_ACTION_FIRST_APPRV);
            	
        } catch (InvalidWSInputException e) {
            logContext.error(this, "Failed to modify quote, the input quote data is invalid.");
            result = SubmittedQuoteConstants.SAP_INVALID_DATA;
        }
        catch (WebServiceException e) {
            logContext.error(this, "Failed to modify quote. " + LogThrowableUtil.getStackTraceContent(e));
            result = SubmittedQuoteConstants.SAP_FAILED;
        }
        return result;
    }
   
    private String converseUserAction(String apprvrAction) {
        String newUserAction = "";
        if (StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_CHANGES)) {
            newUserAction = SubmittedQuoteConstants.USER_ACTION_RET_FOR_CHG;
        } else if (StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_REJECT)) {
            newUserAction = SubmittedQuoteConstants.USER_ACTION_REJECT;
        } else if (apprvrAction.equalsIgnoreCase(SubmittedQuoteConstants.APPRVR_ACTION_APPROVE)) {
            newUserAction = SubmittedQuoteConstants.USER_ACTION_FINL_APPRV;
        }
        return newUserAction;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#persistQuoteComment(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    public void persistQuoteComment(String webQuoteNum, String userId, String quoteTxtType, String quoteTxt, int secId, int quoteTxtId) throws QuoteException {
        
        try {
            this.beginTransaction();
            QuoteTxt cmt = QuoteTxtFactory.singleton().createQuoteComment();
            cmt.setWebQuoteNum(webQuoteNum);
            cmt.setUserEmail(userId);
            cmt.setQuoteTextTypeCode(quoteTxtType);
            cmt.setQuoteText(quoteTxt);
            if ( secId != -1 )
            {
                cmt.setJustificationSectionId(Integer.toString(secId));
            }
            if ( quoteTxtId != -1 )
            {
                cmt.setQuoteTextId(quoteTxtId);
            }
            this.commitTransaction();
        } catch(Exception e){
            logContext.error(this, e);
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#getQuoteTxtHistory(java.lang.String, java.lang.String)
     */
    public List getQuoteTxtHistory(String webQuoteNum, String quoteTxtType, int txtFlag) throws QuoteException {
        List resultList = null;
        try {
            this.beginTransaction();
            resultList = QuoteTxtFactory.singleton().getQuoteTxtHistory(webQuoteNum, quoteTxtType, txtFlag);
            this.commitTransaction();
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }
    
    public List getUserDraftComments(String webQuoteNum, String quoteTxtType) throws QuoteException {
        List resultList = null;
        try {
            this.beginTransaction();
            resultList = QuoteTxtFactory.singleton().getUserDraftComments(webQuoteNum, quoteTxtType);
            this.commitTransaction();
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }

    public int insertQuoteCommentforAP2RV(String webQuoteNum, String userId, String quoteTxtType, String quoteTxt)
            throws QuoteException {
        int quoteTxtId = -1;
        try {
            this.beginTransaction();
            QuoteTxt cmtAP2RV = QuoteTxtFactory.singleton().createQuoteComment();

            cmtAP2RV.setWebQuoteNum(webQuoteNum);
            cmtAP2RV.setUserEmail(userId);
            cmtAP2RV.setQuoteTextTypeCode(quoteTxtType);
            cmtAP2RV.setQuoteText(quoteTxt);
            cmtAP2RV.setJustificationSectionId("10");

            this.commitTransaction();

            quoteTxtId = cmtAP2RV.getQuoteTextId();
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        }
        finally{
        	this.rollbackTransaction();
        }
        return quoteTxtId;
    }
    
    public boolean callQuoteStatusChangeServiceToCancel(Quote quote) {
        boolean isSuccessful = true;
        
        try {
            this.beginTransaction();
            
            QuoteStatusChangeService service = new QuoteStatusChangeService();
            service.execute4Cancel(quote);
            
            this.commitTransaction();
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            isSuccessful = false;
        } catch (WebServiceException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            isSuccessful = false;
        } finally {
            this.rollbackTransaction();
        }
        
        return isSuccessful;
    }

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#deleteUserDraftComments(java.lang.String, java.lang.String, int)
	 */
	public void deleteUserDraftComments(String webQuoteNum, String userId, int deleteType) {
		try {
            this.beginTransaction();
            QuoteTxtFactory.singleton().deleteDraftComments(webQuoteNum, userId, deleteType);
            this.commitTransaction();
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            //throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
	}
	
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess#notifyPendingApproverForSupersedeApprove(java.lang.String, java.lang.String, com.ibm.dsw.quote.common.domain.SpecialBidApprvrOjbect, java.lang.String, java.lang.String, java.lang.String, int)
     */
    public boolean notifyPendingApproverForSupersedeApprove(String webQuoteNum, String currentApprEmail, String apprvrAction, String apprvrComments, String bidExpireDate, int apprvrLevel, String bidTitle, String customerName, String approverName, Locale locale, boolean isPGSQuote) throws EmailException{
        boolean isAdditionalInfoProvided = false;
        if ( StringUtils.equals(apprvrAction, SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO) )
        {
            logContext.debug(this, "approver action is return for add info, so don't send any mail notify");
            return true;
        }
        String url = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.APPLICATION_QUOTE_APPURL);
        url = url + HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&quoteNum=" + webQuoteNum;
        
        String toAddress = currentApprEmail;
        logContext.debug(this, "Begin to call mail process: notifyPendingApproverForSupersedeApprove:");
        logContext.debug(this, "------toAddressList: " + toAddress);
        logContext.debug(this, "------bidNum:        " + webQuoteNum);
        logContext.debug(this, "------bidTitle:      " + bidTitle);
        logContext.debug(this, "------customerName:  " + customerName);
        logContext.debug(this, "------url:           " + url);
        logContext.debug(this, "------approverName: " + approverName);
        logContext.debug(this, "------approve action: " + apprvrAction);
        
        try {
        	String mailTemplate = isPGSQuote ? MailConstants.MAIL_TEMPLATE_SUPERSEDE_APPROVE_PGS : MailConstants.MAIL_TEMPLATE_SUPERSEDE_APPROVE;
            MailProcessFactory.singleton().create().notifyPendingApproverForSupersedeApprove(toAddress, webQuoteNum, apprvrAction, apprvrLevel, bidTitle, customerName, url, approverName, locale, mailTemplate);
            isAdditionalInfoProvided = true;
        } catch (EmailException e) {
            logContext.error(this, e);
            throw e;
        }
        logContext.debug(this, "End of calling mail process: notifyPendingApproverForSupersedeApprove");
        return isAdditionalInfoProvided;
    }
       
    private boolean offerPriceChanged(Double headerOfferPrice, double newOfferPrice){
        if(headerOfferPrice == null){
            return true;
        }
        
        double oldOfferPrice = headerOfferPrice.doubleValue();
        
        return DecimalUtil.isNotEqual(oldOfferPrice, newOfferPrice);
    }
    
    public List getDerivedApprvdBids(String webQuoteNum) throws QuoteException {
        List dervdBids = null;
        
		try {
            this.beginTransaction();
            
            dervdBids = QuoteHeaderFactory.singleton().getDerivedApprvdBids(webQuoteNum);
            
            this.commitTransaction();
            
        } catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            rollbackTransaction();
        }
        
        return dervdBids;
	}
    
    public void getResellerAndDistributor(Quote quote) throws QuoteException {
        try {
            //begin the transaction
            this.beginTransaction();
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            if (quoteHeader == null){
                return;
            }
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
            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(tce));
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public List getReturnReasonList() throws TopazException{
    	return ReturnReasonFactory.singleton().getReturnReasonList();
    }
    
    public String getReasonDescByCode(String code){
    	return ReturnReasonFactory.singleton().getReasonDescByCode(code);
    }
}
