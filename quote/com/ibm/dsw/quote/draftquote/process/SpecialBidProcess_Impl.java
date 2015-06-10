/*
 * Created on 2007-4-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.AuditHistoryFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidApprvrOjbect;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidInfoFactory;
import com.ibm.dsw.quote.mail.config.MailConstants;
import com.ibm.dsw.quote.mail.process.Approvers;
import com.ibm.dsw.quote.mail.process.MailProcess;
import com.ibm.dsw.quote.mail.process.MailProcessFactory;
import com.ibm.dsw.quote.mail.process.SpecialBid;
import com.ibm.dsw.quote.mail.process.SpecialBidEmailHelper;
import com.ibm.dsw.quote.mail.process.SpecialBidStatus;
import com.ibm.dsw.quote.mail.process.SpecialBidWorkflow;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidProcess_Impl.java</code> class is to
 * 
 * @author: lijiatao@cn.ibm.com
 * 
 * Creation date: 2007-4-2
 */
public class SpecialBidProcess_Impl extends TopazTransactionalProcess implements SpecialBidProcess {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidProcess#getSpecialBidInfo(com.ibm.dsw.quote.common.domain.Quote)
     */
    public void getSpecialBidInfo(Quote quote) throws QuoteException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidProcess#postSpecialBidInfo(com.ibm.dsw.quote.common.domain.Quote)
     */
    public void postSpecialBidInfo(Quote quote) throws QuoteException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidProcess#persistSpecialBidApproverSelection()
     */
    public void processSpecialBidApproverSelection(String webQuoteNum, List approvers, String userId, boolean isPGS)
            throws QuoteException {
        if (approvers == null || approvers.size() == 0) {
            return;
        }
        this.beginTransaction();
        try {
            for (int i = 0; i < approvers.size(); i++) {
                Object apprvr = approvers.get(i);
                if (apprvr instanceof SpecialBidApprvrOjbect) {
                    SpecialBidApprvrOjbect approver = (SpecialBidApprvrOjbect) apprvr;
                    specialBidApproverOnSelect(webQuoteNum, approver.getApprvrGrpName(), null, approver
                                .getApprvrEmail(), userId, approver.getApprvrLevel(), isPGS);
                    
                    
                } else if (apprvr instanceof SpecialBidApprvr) {
                    SpecialBidApprvr approver = (SpecialBidApprvr) apprvr;
                    specialBidApproverOnSelect(webQuoteNum, approver.getSpecialBidApprGrp(), approver
                                .getPredecessorEmail(), approver.getApprvrEmail(), userId, approver.getSpecialBidApprLvl(), isPGS);
                    
                    
                }
            }
            this.commitTransaction();
        } catch (QuoteException e) {
            this.rollbackTransaction();
            throw e;
        } catch (Exception te) {
            this.rollbackTransaction();
            throw new QuoteException(te);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidProcess#postSpecialBidFinalization(java.lang.String,
     *      java.util.List, java.lang.String)
     */
    public void createSpecialBidWorkFlowProcess(Quote quote, final List approvers, String justifaction, String submitterId, String submitterName, boolean isSQOEnv) throws QuoteException {
        Approvers[] apprss = null;
        
        List copyApprovers = new ArrayList();
        copyApprovers.addAll(approvers);
        for(Iterator it = copyApprovers.iterator(); it.hasNext(); ){
        	Object apprvr = it.next();
        	
        	if (apprvr instanceof SpecialBidApprvrOjbect) {
        		
                SpecialBidApprvrOjbect approver = (SpecialBidApprvrOjbect) apprvr;
                if(approver.getRdyToOrder() == 1){
                	it.remove();
                }
        	}
        }
        for (int i = 0; i < copyApprovers.size(); i++) {
            Object apprvr = copyApprovers.get(i);
            if (apprvr instanceof SpecialBidApprvrOjbect) {
                SpecialBidApprvrOjbect approver = (SpecialBidApprvrOjbect) apprvr;
                if (apprss == null) {
                    apprss = new Approvers[copyApprovers.size()];
                }
                
                Approvers apprs = new Approvers();
                apprs.setEmail(approver.getApprvrEmail());
                apprs.setGroupName(approver.getApprvrGrpName());
                apprs.setLevel(String.valueOf(approver.getApprvrLevel()));
                apprs.setFirstName(approver.getApprvrFirstName());
                apprs.setLastName(approver.getApprvrlastName());
                apprss[i] = apprs;
            }
        }
        try {
            SpecialBidEmailHelper helper = new SpecialBidEmailHelper();

            SpecialBidWorkflow workFlow = new SpecialBidWorkflow();
            SpecialBid sb = new SpecialBid();

            QuoteHeader quoteHeader = quote.getQuoteHeader();
            CacheProcess cacheProcess = CacheProcessFactory.singleton().create();

            String creatorId = quoteHeader.getCreatorId();
            String countryCode = quoteHeader.getCountry().getCode3();
            List adminstrators = cacheProcess.getSpecialBidAdmins(countryCode);
            StringBuffer adminsSB = new StringBuffer();
            if (adminstrators != null) {
                for (int ai = 0; ai < adminstrators.size(); ai++) {
                    adminsSB.append(adminstrators.get(ai));
                    if (ai != adminstrators.size() - 1) {
                        adminsSB.append(",");
                    }
                }
            }
            String adminstratorEmails = adminsSB.toString();

            List delegatesList = quote.getDelegatesList();
            StringBuffer delegateSB = new StringBuffer();
            if (delegatesList != null) {
                for (int di = 0; di < delegatesList.size(); di++) {
                    SalesRep sr = (SalesRep) delegatesList.get(di);
                    delegateSB.append(sr.getEmailAddress());
                    if (di != delegatesList.size() - 1) {
                        delegateSB.append(",");
                    }
                }
            }
            String delegateEmails = delegateSB.toString();
            if ( quote.getQuoteHeader().isPGSQuote() )
            {
            	sb.setPGSQuote(true);
            	SalesRep partner = SubmittedQuoteProcessFactory.singleton().create().getSalesRepById(submitterId);
            	sb.setSubmitterEmail(partner.getEmailAddress());
            	sb.setQuoteCreatorFirstName(partner.getFullName());
            }
            else
            {
            	sb.setSubmitterEmail(submitterId);
            	sb.setQuoteCreatorFirstName(submitterName);
            }
//            SalesRep creator = quote.getCreator();
            sb.setSQOEnv(isSQOEnv);
            sb.setQuoteNumber(quoteHeader.getWebQuoteNum());
            sb.setQuoteCreatorEmail(creatorId);
            String custName = quoteHeader.getCustName();
            if(custName==null)
                custName = "";
            sb.setCustomerName(custName.trim());
            sb.setAdministratorsEmails(adminstratorEmails);
            sb.setDelagatesEmails(delegateEmails);
            
//            sb.setQuoteCreatorLastName(creator.getLastName());
            sb.setQuoteTitle(quoteHeader.getQuoteTitle());
            sb.setBidExpirationDate(DateHelper.getDateByFormat(quoteHeader.getQuoteExpDate(), "dd MMM yyyy"));
            sb.setExpDateExtensionJustification(quoteHeader.getExpDateExtensionJustification());

            String link = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
            	+ "&quoteNum=" + quoteHeader.getWebQuoteNum();
            sb.setUrl(link);
            
            sb.setPartnerURL(HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB)
            	+ "&quoteNum=" + quoteHeader.getWebQuoteNum());

            workFlow.setApprovers(apprss);
            workFlow.setComments(justifaction);
            workFlow.setSpecialBid(sb);
            workFlow.setSpecialBidStatus(SpecialBidStatus.fromValue(2));

            if(apprss != null){
            	boolean isSuccess = helper.createApprovalProcess(workFlow);
            	if (!isSuccess) {
            		throw new QuoteException("The quote SpecialBids WorkFlow webservice call is failed!!",
            				MessageKeys.MSG_SPECIAL_BID_WORKFLOW_FAILED, new QuoteException()); //TODO
            	}
            }

        } catch (Exception e) {
            throw new QuoteException("The quote SpecialBids WorkFlow webservice is unavailable now.",
                    MessageKeys.MSG_SPECIAL_BID_WORKFLOW_FAILED, e);
        }
    }

    public void processSpecialBidApproverAction(SpecialBidApprvr approver) throws QuoteException {
        //do something here

        // persist changes.
        persistSpecialPidApproverAction();
    }

    protected void specialBidApproverOnSelect(String quoteNumber, String groupName, String formerApproverEmail,
            String approverEmail, String applierEmail, int approverLevel, boolean isPGS) throws QuoteException {
        persistSpecialPidApproverSelection(quoteNumber, groupName, approverEmail, applierEmail, approverLevel, isPGS);        
    }

    protected void persistSpecialPidApproverSelection(String quoteNumber, String groupName, String approverEmail,
            String applierEmail, int approverLevel, boolean isPGS) throws QuoteException {
        //will be fullfiled by its subclasses.
        //throw new UnsupportedOperationException("This method is not implemented!");
    }

    protected void persistSpecialPidApproverAction() throws QuoteException {
        //will be fullfiled by its subclasses.
        //throw new UnsupportedOperationException("This method is not implemented!");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidProcess#getSpecialBidInfo(java.lang.String)
     */
    public SpecialBidInfo getSpecialBidInfo(String webQuoteNum) throws QuoteException {
        SpecialBidInfo specialBidInfo = null;
        try {
            specialBidInfo = SpecialBidInfoFactory.singleton().findByQuoteNum(webQuoteNum);

        } catch (TopazException tce) {
            throw new QuoteException(tce);
        }
        return specialBidInfo;
    }
    public SpecialBidInfo getSpecialBidInfoHeader(String webQuoteNum) throws QuoteException {
        SpecialBidInfo specialBidInfo = null;
        
        try {
            this.beginTransaction();
            specialBidInfo = SpecialBidInfoFactory.singleton().getSpeclBidInfoHeader(webQuoteNum);
            this.commitTransaction();

        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return specialBidInfo;
    }
    
    public void removeSpBidAttachment(String quoteNum, String attchmtSeqNum) throws QuoteException {
        //will be fullfiled by its subclasses.
        //throw new UnsupportedOperationException("This method is not implemented!");
    }

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.draftquote.process.SpecialBidProcess#notifyUpdatedApprovers(com.ibm.dsw.quote.common.domain.Quote, java.util.List, java.lang.String,java.lang.String)
	 */
	public void notifyUpdatedApprovers(Quote quote, List approvers) throws QuoteException {
		try {
            this.beginTransaction();
            int pendingAppLevel = quote.getQuoteUserAccess().getPendingAppLevel();
            for (int i = 0; i < approvers.size(); i++) {
                SpecialBidApprvr approver = (SpecialBidApprvr) approvers.get(i);
                
                if(pendingAppLevel == approver.getSpecialBidApprLvl() 
                  		&& !approver.getPredecessorEmail().equalsIgnoreCase(approver.getApprvrEmail())){
                    String toAddress = approver.getApprvrEmail();
                    //Pgs quote, no need to cc to submitter; but if submiited on sqo, need to send to submitter
                    String ccAddress = quote.getQuoteHeader().isPGSQuote() && !quote.getQuoteHeader().isSubmittedOnSQO() ? null : quote.getQuoteHeader().getSubmitterId();
                    SalesRep creator = SalesRepFactory.singleton().findDelegateByID(ccAddress);

                    String salesRepName =creator.getFirstName() + " " + creator.getLastName();
                    String salesRepEmail = quote.getQuoteHeader().getSubmitterId();
                    String url = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                		+ "&quoteNum=" + quote.getQuoteHeader().getWebQuoteNum();
                    String extendedDate = DateHelper.getDateByFormat(quote.getQuoteHeader().getQuoteExpDate(), "dd MMM yyyy");
                    String extendedReason = (quote.getQuoteHeader().getExpDateExtensionJustification() == null) ? "":quote.getQuoteHeader().getExpDateExtensionJustification();
                    String custName = quote.getQuoteHeader().getCustName();
                    if(custName==null)
                        custName = "";
                    
                  	MailProcess mailProcess = MailProcessFactory.singleton().create();
                  	//update approver no need to send mail to partner
                  	mailProcess.sendBidRequestApprovalForm(
                  			toAddress,
                  			ccAddress,
   							quote.getQuoteHeader().getWebQuoteNum(),
   							quote.getQuoteHeader().getQuoteTitle(),
   							custName.trim(),
   							salesRepName,
   							salesRepEmail,
   							url, MailConstants.MAIL_TEMPLATE_BID_REQUEST_APPROVAL,
   							extendedDate,
   							extendedReason);
                  	//Update approve, no need to send mail to submitter
//                  	if ( quote.getQuoteHeader().isPGSQuote() )
//                  	{
//                  		url = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
//                		+ "&quoteNum=" + quote.getQuoteHeader().getWebQuoteNum();
//                  		toAddress = creator.getEmailAddress();
//                  		mailProcess.sendBidRequestApprovalForm(
//                      			toAddress,
//                      			null,
//       							quote.getQuoteHeader().getWebQuoteNum(),
//       							quote.getQuoteHeader().getQuoteTitle(),
//       							quote.getQuoteHeader().getCustName(),
//       							salesRepName,
//       							salesRepEmail,
//       							url, MailConstants.MAIL_TEMPLATE_BID_REQUEST_APPROVAL_PGS);
//                  	}
                  	LogContextFactory.singleton().getLogContext().info(this, "sended mail success for update approver: " + quote.getQuoteHeader().getWebQuoteNum() + ";" 
                     		+ toAddress + ";" + ccAddress);
                        }
            }
            //commit the transaction
            this.commitTransaction();
        } catch (QuoteException e) {
            rollbackTransaction();
            throw e;
        } catch (Exception te) {
            rollbackTransaction();
            throw new QuoteException(te);
        }
	}

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidProcess#updateCata(java.lang.String, java.lang.String[], java.lang.String)
     */
    public void updateCategory(String webQuoteNum, String[] spBidCategories, String userId) throws QuoteException {
        // TODO Auto-generated method stub
        
    }
    
    protected String updateAndGetOldSalesPlayNum(String webQuoteNum, String salesPlayNum, String userId) throws QuoteException {return null;}
    
    public void updateSalesPlayNum(String webQuoteNum, String salesPlayNum, String userId) throws QuoteException
    {
    	try {
            this.beginTransaction();
            String oldValue = this.updateAndGetOldSalesPlayNum(webQuoteNum, salesPlayNum, userId);
            AuditHistoryFactory.singleton().createAuditHistory(webQuoteNum, 0, userId, SubmittedQuoteConstants.USER_ACTION_UP_SALES_PLAY_NUM, oldValue, salesPlayNum);
            this.commitTransaction();
        } catch (QuoteException e) {
            rollbackTransaction();
            throw e;
        } catch (Exception te) {
            rollbackTransaction();
            throw new QuoteException(te);
    	}
    	
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidProcess#updateSpecialBid(java.lang.String, int, int, java.lang.String)
     */
    public void updateSpecialBid(String webQuoteNum, int piTNC, String userId) throws QuoteException {
        // TODO Auto-generated method stub
        
    }
    public void updateSpecialBidGridDelegationFlag(String webQuoteNum, boolean gridDelegationFlag)  throws QuoteException {      
    }
    public boolean checkCpqExcepCode(String webQuoteNum) throws QuoteException
    {
    	return false;
    }
    
    public List getAllApproverCommentsWithType(String webQuoteNum, String aprEmal, int level) throws QuoteException
    {
    	return null;	
    }
    
    public void removeChannelOverrideDiscountReason(SpecialBidInfo spbidInfo) throws QuoteException
    {
    	try {
    		this.beginTransaction();
    		spbidInfo.setChannelOverrideDiscountReasonCode(null);
    		this.commitTransaction();
    	} catch (Exception te) {
            rollbackTransaction();
            throw new QuoteException(te);
        }
    }
}