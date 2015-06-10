package com.ibm.dsw.quote.draftquote.action;

import java.util.List;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.JustSection;
import com.ibm.dsw.quote.common.domain.JustSection_Impl;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteReasonFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidReason;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SpecialBidCommon;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidTabAction</code> class is to display the special bid
 * tab of a draft quote
 * 
 * @author: lijiatao@cn.ibm.com
 * 
 * Creation date: Apr. 02, 2007
 */
public class SpecialBidTabAction extends DraftQuoteBaseAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.DraftQuoteBaseAction#getDraftQuoteDetail(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void getDraftQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler) throws QuoteException {
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        quoteProcess.getQuoteDetailForSpecialBidTab(quote,((QuoteBaseContract)contract).getQuoteUserSession());
        if (null == quote || null == quote.getQuoteHeader()) 
        {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        }
        QuoteBaseContract ct = (QuoteBaseContract)contract;
        handler.addObject(DraftQuoteParamKeys.SESSION_USER_ID, ct.getUserId());
            
        QuoteHeader header = quote.getQuoteHeader();
        quote.setPgsAppl(ct.isPGSEnv());
        SpecialBidCommon comm = new SpecialBidCommon(quote, ct.getUser().getWIID());
        handler.addObject(SpecialBidParamKeys.PARAM_SPECIAL_BID_COMMON, comm);
        try
        {
	        SpecialBidReason spbReason = comm.spBidCondition.getSpecialBidReason();
	        handler.addObject(SpecialBidParamKeys.PARAM_SPECIAL_BID_REASON, spbReason);
	        QuoteReasonFactory.singleton().updateSpecialBidReason(header.getWebQuoteNum(), spbReason, ct.getUserId());
	        if ( spbReason != null && quote.getSpecialBidInfo().isInitSpeclBidApprFlag() )
            {
                spbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.INIT_SPECL_BID_APPR);
            }
	        PartPriceProcessFactory.singleton().create().updateQuoteHeader(header, ct.getUserId());
        }
        catch ( Exception e )
        {
            logContext.error(this, e.toString());
            logContext.error(this, e);
        }
        
        processForGrowthDelegation(quote, ct, handler);
        
        initJustSections(quote);
        CodeDescObj lob = header.getLob();
        if (QuoteConstants.LOB_PA.equals(lob.getCode()) || QuoteConstants.LOB_PAE.equals(lob.getCode())) {
            if (QuoteConstants.QUOTE_TYPE_RENEWAL.equals(header.getQuoteTypeCode())) {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_RQ_SPECIAL_BID_TAB_PA_PAE);
            } else {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SQ_SPECIAL_BID_TAB_PA_PAE);
            }
        }
        if (QuoteConstants.LOB_FCT.equals(lob.getCode())) {
            if ( QuoteConstants.QUOTE_TYPE_RENEWAL.equals(header.getQuoteTypeCode()) )
            {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_RQ_SPECIAL_BID_TAB_FCT);
            }
            else
            {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SQ_SPECIAL_BID_TAB_FCT);
            }
        }
        if ( QuoteConstants.LOB_OEM.equals(lob.getCode())) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SQ_SPECIAL_BID_TAB_OEM);
        }
        if (QuoteConstants.LOB_PPSS.equals(lob.getCode())) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SQ_SPECIAL_BID_TAB_PPSS);
        }
        if ( QuoteConstants.LOB_SSP.equals(lob.getCode())) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SQ_SPECIAL_BID_TAB_SSP);
        }
    }
    
    private void processForGrowthDelegation(Quote quote, QuoteBaseContract ct, ResultHandler handler){
   	 if(GrowthDelegationUtil.showGrowthDelegationNotCalculatedMessage(quote)){
   		 String message = getI18NString(DraftQuoteMessageKeys.GD_SB_NEED_RECAUCULATE, 
                                                I18NBundleNames.BASE_MESSAGES, ct.getLocale());
   		 
            MessageBean mBean = MessageBeanFactory.create();
            mBean.addMessage(message, MessageBeanKeys.INFO);
            handler.setMessage(mBean);
   	 }
   }
    
    protected void initJustSections(Quote quote)
    {
        List justSections = quote.getSpecialBidInfo().getJustSections();

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
            JustSection sec = new JustSection_Impl();
            sec.setSecId(SpecialBidInfo.BEGIN_SUBMITTER + 1);
            SpecialBidInfo.CommentInfo cmtInfo = new SpecialBidInfo.CommentInfo();
            cmtInfo.comment = "";
            cmtInfo.secId = SpecialBidInfo.BEGIN_SUBMITTER + 1;
            justSections.add(sec);
            sec.getJustTexts().add(cmtInfo);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.DraftQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return null;
    }

}
