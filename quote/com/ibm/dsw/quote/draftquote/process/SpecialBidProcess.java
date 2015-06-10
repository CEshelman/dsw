/*
 * Created on 2007-4-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ApproveComment;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidProcess.java</code> class is to 
 * 
 * @author: lijiatao@cn.ibm.com
 * 
 * Creation date: 2007-4-2
 */
public interface SpecialBidProcess {
	
	public void getSpecialBidInfo(Quote quote) throws QuoteException;
	public void postSpecialBidInfo(Quote quote) throws QuoteException;
	public void processSpecialBidApproverSelection(String webQuoteNum, List approvers, String userId, boolean isPGS) throws QuoteException;
	public void notifyUpdatedApprovers(Quote quote, List approvers) throws QuoteException;
	public void processSpecialBidApproverAction(SpecialBidApprvr approver) throws QuoteException;
	public SpecialBidInfo getSpecialBidInfo(String webQuoteNum) throws QuoteException;
	public SpecialBidInfo getSpecialBidInfoHeader(String webQuoteNum) throws QuoteException;
	public void createSpecialBidWorkFlowProcess(Quote quote, List approvers, String justifaction, String submitterId, String submitterName, boolean isSQOEnv) throws QuoteException;
    public void updateCategory(String webQuoteNum, String[] spBidCategories, String userId) throws QuoteException;
    public void updateSpecialBid(String webQuoteNum, int piTNC, String userId) throws QuoteException;
    public void updateSpecialBidGridDelegationFlag(String webQuoteNum, boolean gridDelegationFlag) throws QuoteException;
    public void updateSalesPlayNum(String webQuoteNum, String salesPlayNum, String userId) throws QuoteException;
    public boolean checkCpqExcepCode(String webQuoteNum) throws QuoteException;
    public List<ApproveComment> getAllApproverCommentsWithType(String webQuoteNum, String aprId, int level) throws QuoteException;
    public void removeChannelOverrideDiscountReason(SpecialBidInfo spbidInfo) throws QuoteException;
}
