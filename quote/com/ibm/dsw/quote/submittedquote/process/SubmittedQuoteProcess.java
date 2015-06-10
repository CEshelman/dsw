package com.ibm.dsw.quote.submittedquote.process;

import is.domainx.User;

import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SpecialBidApprvrOjbect;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.dsw.quote.submittedquote.domain.OrderDetail;
import com.ibm.dsw.quote.submittedquote.domain.PriorComments;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr;
import com.ibm.dsw.quote.submittedquote.domain.StatusExplanation;
import com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteRequest;
import com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteResponse;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SubmittedQuoteProcess</code> class is the process interface for
 * Reviewer search.
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public interface SubmittedQuoteProcess {

    public void addReviewer(String webQuoteNum, String userEmailAdr, String rvwrEmailAdr,int quoteTxtId) throws QuoteException;

    public List findReviewersByWebQuoteNum(String webQuoteNum) throws QuoteException;
  
    public Quote getSubmittedQuoteForCancel(String webQuoteNum) throws NoDataException, QuoteException;
    
    public QuoteHeader getQuoteHeaderByQuoteNum(String quoteNum) throws QuoteException;
    
    public List getApproverActHistsByQuoteNum(String quoteNum) throws QuoteException;
    
    public SpecialBidApprvr getCurrentApprover(String quoteNum) throws QuoteException;
    
    public void persistApproverActHist(String webQuoteNum, String userId, String userRole, String userAction, String quoteTxt,String returnReason) throws Exception;
    
    public void persistApproverActHistWithTransaction(String webQuoteNum, String userId, String userRole, String userAction, String quoteTxt) throws QuoteException;
    
    public SpecialBidApprvr persistApproverAction(String webQuoteNum, String apprvrEmail, String apprvrAction, int apprvrLevel) throws QuoteException; 
    
    public boolean submitApprvrAction(String webQuoteNum, String apprvrEmail, String apprvrAction, String userRole, String apprvrComments, QuoteUserSession salesRep, boolean isCanSupersedeAppr,String returnReason,int apprvrLevel) throws QuoteException;
   
    public boolean notifyWorkflowOfApprvrAction(String webQuoteNum, String currentApprEmail, SpecialBidApprvrOjbect specialBidApprvr, String apprvrAction, String apprvrComments, String bidExpireDate,String returnReasonDesc) throws QuoteException;
    
    public SubmittedQuoteResponse getSubmittedPartPriceInfo(SubmittedQuoteRequest request) throws QuoteException;
    
    public void updateQuoteStageToCancel(String userId, String webQuoteNum) throws QuoteException;
    
    public int updateQuoteStatusByApprvrAction(Quote quote, User user, String apprvrAction, QuoteUserSession salesRep, int apprvrLevel) throws QuoteException;
    
     public void persistQuoteComment(String webQuoteNum, String userId, String quoteTxtType, String quoteTxt, int secId, int quoteTxtId) throws QuoteException;
    
    public List getQuoteTxtHistory(String webQuoteNum, String quoteTxtType, int txtFlag) throws QuoteException;
    
    public List getUserDraftComments(String webQuoteNum, String quoteTxtType) throws QuoteException;
    
    public int insertQuoteCommentforAP2RV(String webQuoteNum, String userId, String quoteTxtType, String quoteTxt) throws QuoteException;
    
    public boolean callQuoteStatusChangeServiceToCancel(Quote quote);

	public void deleteUserDraftComments(String webQuoteNum, String userId, int deleteType);
	
	public void getExecPartPriceInfo(Quote quote, User user) throws QuoteException;
		
    public boolean notifyPendingApproverForSupersedeApprove(String webQuoteNum, String currentApprEmail, String apprvrAction, String apprvrComments, String bidExpireDate, int apprvrLevel, String bidTitle, String customerName, String approverName, Locale locale, boolean isPGSQuote)throws EmailException;
	
    public StatusExplanation getStatusDetailExplanation(String sapDocNum, String statusCode)throws QuoteException;
    
    public List getDerivedApprvdBids(String webQuoteNum) throws QuoteException;
    
    public boolean quoteHasCtrldParts(String webQuoteNum) throws QuoteException;
    
    public void updatePARegstrnEmailFlag(String userId, String webQuoteNum) throws QuoteException;
    
    public PriorComments getPriorComments(String webQuoteNum) throws QuoteException;
    
    public void updateSalesInfoTitle(String userId, String title) throws QuoteException;
    
    public SalesRep getSalesRepById(String userId) throws QuoteException;
    
    public void getResellerAndDistributor(Quote quote) throws QuoteException;
    
    public List getReturnReasonList() throws TopazException;
    
    public String getReasonDescByCode(String code);
    
    public String getReturnReasonCode(String webQuoteNum, int apprvrLevel) throws QuoteException;
    
    public List<OrderDetail> getOrderDetails(String webQuoteNum, int destSeqNum) throws QuoteException;
}
