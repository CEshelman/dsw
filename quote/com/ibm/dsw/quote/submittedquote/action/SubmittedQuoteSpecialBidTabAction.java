package com.ibm.dsw.quote.submittedquote.action;

import java.util.ArrayList;
import java.util.List;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.JustSection;
import com.ibm.dsw.quote.common.domain.JustSection_Impl;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteReasonFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidCondition;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidReason;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SubmittedQuoteSpecialBidTabAction</code> class is to display
 * submitted quote's special bid tab.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: May 8, 2007
 */
public class SubmittedQuoteSpecialBidTabAction extends SubmittedQuoteBaseAction {

    private static final long serialVersionUID = -7863397660542773098L;

	/**
     *  
     */

    protected Quote getSubmittedQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler)
            throws QuoteException {
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        if (null == quote || null == quote.getQuoteHeader()) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        }
        quoteProcess.getQuoteDetailForSpecialBidTab(quote,((SubmittedQuoteBaseContract)contract).getQuoteUserSession());
        QuoteHeader header = quote.getQuoteHeader();
        CodeDescObj lob = header.getLob();
        SubmittedQuoteProcess sqp = SubmittedQuoteProcessFactory.singleton().create();
        boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
        List quoteTxtHistory = sqp.getQuoteTxtHistory(quote.getQuoteHeader().getWebQuoteNum(), SpecialBidInfo.CommentInfo.SPBID_J, 0);
        if (QuoteConstants.LOB_PA.equals(lob.getCode()) || QuoteConstants.LOB_PAE.equals(lob.getCode())) {
            handler.setState(getBidIterState(quote, SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_SPECIAL_BID_TAB_PA_PAE));
            
            quoteTxtHistory.addAll(SubmittedQuoteProcessFactory.singleton().create().getQuoteTxtHistory(quote.getQuoteHeader().getWebQuoteNum(), SpecialBidInfo.CommentInfo.CREDIT_J, 0));
            
        }
        if (QuoteConstants.LOB_FCT.equals(lob.getCode())) {
            handler.setState(getBidIterState(quote, SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_SPECIAL_BID_TAB_FCT));
            if ( !isPGSQuote )
            {
            quoteTxtHistory.addAll(SubmittedQuoteProcessFactory.singleton().create().getQuoteTxtHistory(quote.getQuoteHeader().getWebQuoteNum(), SpecialBidInfo.CommentInfo.CREDIT_J, 0));
            }
        }
        if (QuoteConstants.LOB_OEM.equals(lob.getCode())) {
            handler.setState(getBidIterState(quote, SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_SPECIAL_BID_TAB_OEM));
            if ( !isPGSQuote )
            {
            quoteTxtHistory.addAll(SubmittedQuoteProcessFactory.singleton().create().getQuoteTxtHistory(quote.getQuoteHeader().getWebQuoteNum(), SpecialBidInfo.CommentInfo.CREDIT_J, 0));
            }
        }
        if (QuoteConstants.LOB_SSP.equals(lob.getCode())) {
            handler.setState(getBidIterState(quote, SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_SPECIAL_BID_TAB_SSP));
            if ( !isPGSQuote )
            {
            quoteTxtHistory.addAll(SubmittedQuoteProcessFactory.singleton().create().getQuoteTxtHistory(quote.getQuoteHeader().getWebQuoteNum(), SpecialBidInfo.CommentInfo.CREDIT_J, 0));
            }
        }
        if (QuoteConstants.LOB_PPSS.equals(lob.getCode())) {
            handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_SPECIAL_BID_TAB_PPSS);
        }
        // Add administrators of the bid��s geo object
        String geo = quote.getQuoteHeader().getCountry().getSpecialBidAreaCode().trim();
        List adminList = CacheProcessFactory.singleton().create().getSpecialBidAdmins(geo);
        handler.addObject(ParamKeys.PARAM_ADMIN_LIST_OBJECT, adminList);
        handler.addObject(SpecialBidParamKeys.PARAM_QUOTE_TXT_HISTORY, quoteTxtHistory);
        if ( !isPGSQuote )
        {
        	handler.addObject(SpecialBidParamKeys.PARAM_PRIOR_COMMENTS, sqp.getPriorComments(quote.getQuoteHeader().getWebQuoteNum()));
        }
        try
        {
            SpecialBidReason spbReason = QuoteReasonFactory.singleton().loadSpecialBidReason(header.getWebQuoteNum());
            //if the spbReason is null or this quote is extension then update spbReason from database
            if ( spbReason == null || header.isExpDateExtendedFlag())
            {
            	SubmittedQuoteBaseContract ct = (SubmittedQuoteBaseContract)contract;
                logContext.debug(this, "get spbReason is null, get it from speical bid rule");
                SpecialBidCondition cond = new SpecialBidCondition(quote, ct.getUserId());
                spbReason = cond.getSpecialBidReason();
                QuoteReasonFactory.singleton().updateSpecialBidReason(header.getWebQuoteNum(), spbReason, ct.getUserId());
            }
            if ( spbReason != null && header.isCopied4ReslChangeFlag() )
            {
                spbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.CREATE_FROM_MUL_RESELLER);
            }
            if ( spbReason != null && quote.getSpecialBidInfo().isInitSpeclBidApprFlag() )
            {
                spbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.INIT_SPECL_BID_APPR);
            }
            // For displaying the message for special bid reason: The special bid is a copy of an approved quote.
    		// Added by Yue Ping Li on 2010-4-26
            if ( spbReason != null && header.isCopied4PrcIncrQuoteFlag() )
            {
            	// if the price has been increased,then display the message
            	if(header.getQuotePriceTot() > header.getOriQuotePriceTot()){
            		spbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.CREATE_FROM_APPROVED_QUOTE);
            	}
            }
            //for bid iteration
            if ( spbReason != null && header.isBidIteratnQt() )
            {
                spbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.BID_ITERATION);
            }
            
            if ( spbReason != null && header.isCopiedForOutputChangeFlag() )
            {
                spbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.COPY_FOR_OUT_CHANGE);
            }
            
            if ( spbReason != null && header.isSaaSStrmlndApprvlFlag() )
            {
                spbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.STREAMLINED_APPROVAL_FLAG);
            }
            
            handler.addObject(SpecialBidParamKeys.PARAM_SPECIAL_BID_REASON, spbReason);
            String[] data = getReasonTextFromCode(quote, (SubmittedQuoteBaseContract) contract, spbReason);
            handler.addObject(ParamKeys.PARAM_SPECIAL_BID_REASON_TEXT, data);
        }
        catch ( Throwable t )
        {
          logContext.error(this, t);
        }
        initJustSection(quote);
        return quote;

    }
    
    protected String getBidIterState(Quote quote, String state)
    {
        if ( quote.getQuoteHeader().isBidIteratnQt() && !quote.getQuoteHeader().isSubmittedQuote() )
        {
            return state + "_DRAFT";
        }
        return state;
    }
    
    protected void initJustSection(Quote quote)
    {
        List justSections = quote.getSpecialBidInfo().getJustSections();
        int size = justSections.size();
        logContext.debug(this, "begin init just section, just sections init size:" + size);
        
        JustSection secSummary = new JustSection_Impl();
        secSummary.setSecId(SpecialBidInfo.BEGIN_SUBMITTER);
        SpecialBidInfo.CommentInfo cmt = new SpecialBidInfo.CommentInfo();
        cmt.comment = quote.getSpecialBidInfo().getSpBidJustText();
        if ( cmt.comment == null )
        {
            cmt.comment = "";
        }
        cmt.secId = SpecialBidInfo.BEGIN_SUBMITTER;
        justSections.add(0, secSummary);
        secSummary.getJustTexts().add(cmt);
        
        if ( justSections.size() == 1 )
        {
            logContext.debug(this, "size is 1, only has summary section, add a section");
            JustSection sec = new JustSection_Impl();
            sec.setSecId(SpecialBidInfo.BEGIN_SUBMITTER + 1);
            SpecialBidInfo.CommentInfo cmtInfo = new SpecialBidInfo.CommentInfo();
            cmtInfo.comment = "";
            cmtInfo.secId = SpecialBidInfo.BEGIN_SUBMITTER + 1;
            justSections.add(sec);
            sec.getJustTexts().add(cmtInfo);
        }
    }

    /**
     *  
     */

    protected String getState(ProcessContract contract) {
        return SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_SPECIAL_BID_TAB_PA_PAE;
    }

    /**
     *  
     */

    protected boolean isDisplayTranMessage(String bundleName, String bundleKey) {
        boolean isDisplay = super.isDisplayTranMessage(bundleName, bundleKey);
        if (isDisplay)
            return true;

        String[] keys = { SubmittedQuoteMessageKeys.MSG_REVIEW_REQUEST_SENT };
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(bundleKey))
                return true;
        }
        return false;
    }
    
    protected String[] getReasonTextFromCode(Quote quote, SubmittedQuoteBaseContract ct, SpecialBidReason spbReason) {
    	QuoteHeader header = quote.getQuoteHeader();
    	List<String> list = new ArrayList<String>();
    	if ( header.getSpeclBidFlag() == 1 )
    	{            
    	    if ( header.getApprovalRouteFlag() == 1 )
	        {
	            List temp = spbReason.getSpecialBidReasonList();
                logContext.debug(this, "spbReason getSpecialBidReasonList: " + temp);
                if ( temp != null && temp.size() > 0 )
                {
                    list.addAll(QuoteCommonUtil.translateReasonCode2Key(temp));
                }
	        }
	        else
	        {
	            List temp = spbReason.getNoApprovalReasonList();
	            logContext.debug(this, "spbReason getNoApprovalReasonList: " + temp);
                if ( temp != null && temp.size() > 0 )
                {
                    list.addAll(QuoteCommonUtil.translateReasonCode2Key(temp));
                }
	        }
    	    
            if(quote.getSpecialBidInfo().isTermsAndCondsChg()){
            	list.add(SpecialBidViewKeys.TERMS_CONDITION_OPTION);                	
            }
            		
            if(quote.getSpecialBidInfo().isInitSpeclBidApprFlag()){
            	list.add(SpecialBidViewKeys.INIT_SPECL_BID_APPR);                 	
            }
	    }
    	
    	String[] reasons = new String[list.size()];
    	for(int i = 0; i < list.size(); i++){
    		reasons[i] = QuoteCommonUtil.getI18NString("appl.i18n.quote", ct.getLocale(), list.get(i));
    	}
    	return reasons;    
    }
}
