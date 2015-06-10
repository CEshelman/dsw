package com.ibm.dsw.quote.submittedquote.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.submittedquote.contract.SaveDraftCommentsBaseContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SaveDraftComemntsBaseAction<code> class.
 *    
 * @author: Fred(qinfengc@cn.ibm.com)
 * 
 * Creation date: 2009-2-5
 */
public abstract class SaveDraftComemntsBaseAction extends BaseContractActionHandler {
    protected boolean isBidIterSpbidEditAble(Quote quote)
    {
        if ( quote.getQuoteHeader().isBidIteratnQt() && !quote.getQuoteHeader().isSubmittedQuote() )
	    {
	        return true;
	    }
        return false;
    }
    
    protected void saveUserDraftComments(Quote quote, SaveDraftCommentsBaseContract baseContract)
    {
    	try
		{
    	    if ( this.isBidIterSpbidEditAble(quote) || quote.getQuoteHeader().isUnderEvaluation() )
    	    {
    	        return;
    	    }
    		SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
    		int level = quote.getQuoteUserAccess().getPendingAppLevel();
    		logContext.debug(this, "save user draft comments: " + level);
    		
    		String quoteTxtTypeCode = null;
    		logContext.debug(this, "submitter comments: " + baseContract.getSpecialBidCommentSubmitter());
    		logContext.debug(this, "reviewer comments: " + baseContract.getSpecialBidCommentReviewer());
    		logContext.debug(this, "approver approve comments: " + baseContract.getSpecialBidCommentApprove());
    		logContext.debug(this, "approver add comments: " + baseContract.getSpecialBidCommentAdd());
    		
    		logContext.debug(this, "approver reiverer comments: " + baseContract.getApvrReviewComments());
    		logContext.debug(this, "just comments: " + baseContract.getJustCommentsDraft());
    		logContext.debug(this, "tnc comments: " + baseContract.getTncCommentsDraft());
    		
    		
    		if ( StringUtils.isNotBlank(baseContract.getSpecialBidCommentSubmitter() ))
    		{
    		    quoteTxtTypeCode = "_L" + level + "_SAL";
				process.persistQuoteComment(quote.getQuoteHeader().getWebQuoteNum(), baseContract.getUserId(), quoteTxtTypeCode, 
    					baseContract.getSpecialBidCommentSubmitter(), -1, -1);
    		}
    		
    		if ( StringUtils.isNotBlank(baseContract.getSpecialBidCommentReviewer() ))
    		{
    		    quoteTxtTypeCode = "_L" + level + "_RVW";
				process.persistQuoteComment(quote.getQuoteHeader().getWebQuoteNum(), baseContract.getUserId(), quoteTxtTypeCode, 
    					baseContract.getSpecialBidCommentReviewer(), -1, -1);
    		}
    		
    		if ( StringUtils.isNotBlank(baseContract.getSpecialBidCommentApprove()))
    		{
    		    quoteTxtTypeCode = "_L" + level + "_APR";
				process.persistQuoteComment(quote.getQuoteHeader().getWebQuoteNum(), baseContract.getUserId(), quoteTxtTypeCode, 
    					baseContract.getSpecialBidCommentApprove(), -1, -1);
    		}
    		
    		if ( StringUtils.isNotBlank(baseContract.getSpecialBidCommentAdd()))
    		{
    		    quoteTxtTypeCode = "_L" + level + "_AAD";
				process.persistQuoteComment(quote.getQuoteHeader().getWebQuoteNum(), baseContract.getUserId(), quoteTxtTypeCode, 
    					baseContract.getSpecialBidCommentAdd(), -1, -1);
    		}
    		
    		if ( StringUtils.isNotBlank(baseContract.getApvrReviewComments()))
    		{
    			quoteTxtTypeCode = "_L" + level + "_ARW";
    			process.persistQuoteComment(quote.getQuoteHeader().getWebQuoteNum(), baseContract.getUserId(), quoteTxtTypeCode, 
    					baseContract.getApvrReviewComments(), -1, -1);
    		}
    		
    		if ( StringUtils.isNotBlank(baseContract.getJustCommentsDraft()))
    		{
    		    quoteTxtTypeCode = "_L" + level + "_JUS";
    		    process.persistQuoteComment(quote.getQuoteHeader().getWebQuoteNum(), baseContract.getUserId(), quoteTxtTypeCode, 
    					baseContract.getJustCommentsDraft(), -1, -1);
    		}
    		if ( StringUtils.isNotBlank(baseContract.getTncCommentsDraft()))
    		{
    		    quoteTxtTypeCode = "_L" + level + "_TNC";
    		    process.persistQuoteComment(quote.getQuoteHeader().getWebQuoteNum(), baseContract.getUserId(), quoteTxtTypeCode, 
    					baseContract.getTncCommentsDraft(), -1, -1);
    		}
		}
    	catch ( Throwable e )
		{
    		logContext.error(this, e.toString());
    		logContext.error(this, e);
		}
    }
}
