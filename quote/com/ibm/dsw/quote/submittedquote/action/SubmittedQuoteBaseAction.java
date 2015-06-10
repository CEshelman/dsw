package com.ibm.dsw.quote.submittedquote.action;

import is.domainx.User;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmittedQuoteBaseAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-28
 */

public abstract class SubmittedQuoteBaseAction extends SaveDraftComemntsBaseAction {
    
    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        handler.setState(getState(contract));
        
        SubmittedQuoteBaseContract baseContract = (SubmittedQuoteBaseContract) contract;
        String webQuoteNum = baseContract.getQuoteNum();
        QuoteUserSession quoteUserSession = baseContract.getQuoteUserSession();
        String up2ReportingUserIds = quoteUserSession == null ? "" : quoteUserSession.getUp2ReportingUserIds();
        String userId = baseContract.getUserId();
        User user = baseContract.getUser();
        String hasDraftQuote = "0";
        Quote quote = null;
        
        try {
            // Get quote header and customer.
            QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
            quote = getSubmittedQuoteBaseInfo(qProcess, webQuoteNum, userId, up2ReportingUserIds, baseContract);
            quote.setPgsAppl(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(quoteUserSession.getAudienceCode()));
            QuoteUserAccess quoteUserAccess = quote.getQuoteUserAccess();
            
            logContext.debug(this, "webQuoteNum: " + webQuoteNum);
            logContext.debug(this, "UserID: " + userId);
            logContext.debug(this, "Up2ReportingUserIds: " + up2ReportingUserIds);
            logContext.debug(this, "User access level: " + user.getAccessLevel(QuoteConstants.APP_CODE_SQO));
            logContext.debug(this, "User can view this quote: " + quoteUserAccess.isCanViewQuote());
            
            if (user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_ECARE
                    || quoteUserAccess.isCanViewQuote()) {
            	this.saveUserDraftComments(quote, baseContract);
            	this.getUserDraftComments(quote, baseContract, handler);
                quote = getSubmittedQuoteDetail(quote, contract, handler);

                // Added for JavaScript popup
                if (userId != null && !userId.equals("")) {
                    QuoteRightColumn sessionQuote = qProcess.getQuoteRightColumnInfo(userId);
                    if (sessionQuote != null) {
                        hasDraftQuote = "1";
                    }
                }
                handler.addObject(SubmittedQuoteParamKeys.HAS_ACTIVE_DRAFT_QUOTE, hasDraftQuote);
                handler.addObject(ParamKeys.PARAM_QUOTE_OBJECT, quote);
                addContractToResult(contract, handler);
            } else {
                logContext.error(this, "User " + userId + " can't view this quote " + webQuoteNum + "!!");
                QuoteException e = new QuoteException();
                e.setMessageKey(MessageKeys.MSG_UNAUTHORIZED_VIEW_QUOTE);
                throw e;
            }
            if(quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()
               && QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode()))
            {
	            CommonServiceUtil.matchOfferPriceFailed(quote);
	            if(quote.getQuoteHeader().isMatchOfferPriceFailed()){
	                MessageBean mBean = MessageBeanFactory.create();
	                String msg = getI18NString(SubmittedQuoteMessageKeys.MATCH_OFFER_PRICE_FAILED,
	                        MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, baseContract.getLocale());
	                mBean.addMessage(msg, MessageBeanKeys.INFO);
	                handler.setMessage(mBean);
	            }
            }
            if ("1".equals(StringUtils.trimToEmpty(baseContract.getSaveSuccess()))) {
                MessageBean mBean = MessageBeanFactory.create();
                String message = this.getI18NString(DraftQuoteMessageKeys.SAVE_DRAFT_QUOTE_SUCCESS_MSG,
                        MessageKeys.BUNDLE_APPL_I18N_QUOTE, baseContract.getLocale());
                mBean.addMessage(message, MessageBeanKeys.SUCCESS);
                handler.setMessage(mBean);
            }
            
            if(quote.getQuoteHeader().isSubmittedQuote()
               && QuoteConstants.QUOTE_TYPE_SALES.equalsIgnoreCase(quote.getQuoteHeader().getQuoteTypeCode())
               ){
            	MessageBean mBean = MessageBeanFactory.create();
            	logContext.debug(this, "divstdObsltPartFlag: " + quote.getQuoteHeader().getDivstdObsltPartFlag());
            	switch (quote.getQuoteHeader().getDivstdObsltPartFlag()){ 
            	case 1:
            		String divstdMsg = getI18NString(PartPriceViewKeys.QUOTE_CONTAIN_DIVESTED_PART_MSG,
            				MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, baseContract.getLocale());
            		mBean.addMessage(divstdMsg, MessageBeanKeys.ERROR);
            		break;
            	case 2:
            		String obsoltMsg = getI18NString(SubmittedQuoteMessageKeys.PART_OBSOLETE_AFTER_SUBMIT,
            				MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, baseContract.getLocale());
            		mBean.addMessage(obsoltMsg, MessageBeanKeys.ERROR);
            		break;
            	case 3:
            		String divstdMsg2 = getI18NString(PartPriceViewKeys.QUOTE_CONTAIN_DIVESTED_PART_MSG,
            				MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, baseContract.getLocale());
            		mBean.addMessage(divstdMsg2, MessageBeanKeys.ERROR);
            		String obsoltMsg2 = getI18NString(SubmittedQuoteMessageKeys.PART_OBSOLETE_AFTER_SUBMIT,
            				MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, baseContract.getLocale());
            		mBean.addMessage(obsoltMsg2, MessageBeanKeys.ERROR);
            		break;
            	}
            	
                handler.setMessage(mBean);
            }
            
            // Updated to retrieve payer for FCT quotes
            // add pull payer logic for order button display logic as we are using payer country code for the order button display
            //PL TMWG-8PXBTZ, RTC WI 176270
            QuoteHeader quoteHeader = quote.getQuoteHeader();
            if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum()) && quoteHeader.isChannelQuote()) {
                if (StringUtils.isNotBlank(quoteHeader.getPayerCustNum())) {
	                logContext.debug(this, "To retrieve Payer by number: " + quoteHeader.getPayerCustNum());
	                Partner payer = PartnerFactory.singleton().findPartnerByNum(quoteHeader.getPayerCustNum(), quoteHeader.getLob().getCode());
	                quote.setPayer(payer);
	            }
            }
            
            SubmittedQuoteProcess sqp = SubmittedQuoteProcessFactory.singleton().create();
            List returnReasonList = new ArrayList();
            try {
            	returnReasonList = sqp.getReturnReasonList();
    		} catch (TopazException e) {
    			logContext.error(this, e);
    		}
            handler.addObject(ParamKeys.PARAM_RETURN_REASONS, returnReasonList);
            
        } catch (NoDataException nde) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(nde));
            throw new QuoteException("The input submitted quote number is invalid.", nde);
        } catch (TopazException te){
            throw new QuoteException("Match offer price error.", te);
        }
        
        //process validation appliance mtm message
        addValidatorMTMMessage(
				QuoteCommonUtil.isAddApplncMTMMsgToMessageBean(quote),
				baseContract.getLocale(), handler);
        
        addRedirectMessage(handler, baseContract.getRedirectMsg(), baseContract.getLocale());
        return handler.getResultBean();
    }
    
    protected Quote getSubmittedQuoteBaseInfo(QuoteProcess qProcess, String webQuoteNum,
                                                          String userId, String up2ReportingUserIds,
                                                          SubmittedQuoteBaseContract baseContract)
                                                             throws NoDataException, QuoteException{
        return qProcess.getSubmittedQuoteBaseInfo(webQuoteNum, userId, up2ReportingUserIds);
    }
    
    protected abstract Quote getSubmittedQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler) throws QuoteException;
    
    protected abstract String getState(ProcessContract contract);
    
    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
        SubmittedQuoteBaseContract baseContract = (SubmittedQuoteBaseContract) contract;
        
        handler.addObject(ParamKeys.PARAM_USER_OBJECT, baseContract.getUser());
        handler.addObject(ParamKeys.PARAM_QUOTE_NUM, baseContract.getQuoteNum());
    }

    protected boolean isDisplayTranMessage(String bundleName, String bundleKey) {
        String[] keys = {  SubmittedQuoteMessageKeys.MSG_RQ_NOT_EDITABLE };
        return ArrayUtils.contains(keys, bundleKey);
    }
    
    protected void getUserDraftComments(Quote quote, SubmittedQuoteBaseContract baseContract, ResultHandler handler)
    {
    	try
		{
    	    if ( this.isBidIterSpbidEditAble(quote) || quote.getQuoteHeader().isUnderEvaluation())
    	    {
    	        return;
    	    }
    		SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
    		int level = quote.getQuoteUserAccess().getPendingAppLevel();
    		String approverType = "_L" + level + "_APR";
    		String approverAddType = "_L" + level + "_AAD";
    		String reviewerType = "_L" + level + "_RVW";
    		String submitterType = "_L" + level + "_SAL";
    		String apprAddReviewerType = "_L" + level + "_ARW";
    		String justCommentsType = "_L" + level + "_JUS";
    		String tncCommentsType = "_L" + level + "_TNC";
    		StringBuffer buff = new StringBuffer();
    		buff.append("_L");
    		buff.append(level);
    		buff.append("_APR");
    		buff.append(",");
    		buff.append("_L");
    		buff.append(level);
    		buff.append("_RVW");
    		buff.append(",");
    		buff.append("_L");
    		buff.append(level);
    		buff.append("_SAL");
    		buff.append(",");
    		buff.append("_L");
    		buff.append(level);
    		buff.append("_ARW");
    		buff.append(",");
    		buff.append("_L");
    		buff.append(level);
    		buff.append("_JUS");
    		buff.append(",");
    		buff.append("_L");
    		buff.append(level);
    		buff.append("_TNC");
    		buff.append(",");
    		buff.append("_L");
    		buff.append(level);
    		buff.append("_AAD");
    		List list = process.getUserDraftComments(quote.getQuoteHeader().getWebQuoteNum(), buff.toString());
    		String userId = baseContract.getUserId();
    		if ( list == null )
    		{
    			return;
    		}
    		boolean approverFlag = false;
    		boolean approverAddFlag = false;
    		boolean reviewerFlag = false;
    		boolean submitterFlag = false;
    		boolean approverAddReviewerFlag = false;
    		boolean justCommentsFlag = false;
    		boolean tncCommentsFlag = false;
    		for ( int i = 0; i < list.size(); i++ )
    		{
    			QuoteTxt txt = (QuoteTxt)list.get(i);
    			if ( !approverFlag & approverType.equals(txt.getQuoteTextTypeCode()) )
    			{
    				handler.addObject(SpecialBidParamKeys.APPROVER_COMMENT, txt.getQuoteText());
    				approverFlag = true;
    			}
    			if ( !userId.equalsIgnoreCase(txt.getUserEmail()) )
    			{
    				continue;
    			}
    			if ( !reviewerFlag & reviewerType.equals(txt.getQuoteTextTypeCode()) )
    			{
    				handler.addObject(SpecialBidParamKeys.REVIEWER_COMMENT, txt.getQuoteText());
    				reviewerFlag = true;
    			}
    			else if ( !submitterFlag & submitterType.equals(txt.getQuoteTextTypeCode()) )
    			{
    				handler.addObject(SpecialBidParamKeys.SUBMITTER_COMMENT, txt.getQuoteText());
    				submitterFlag = true;
    			}
    			else if ( !approverAddReviewerFlag & apprAddReviewerType.equals(txt.getQuoteTextTypeCode()) )
    			{
    				handler.addObject(SpecialBidParamKeys.APVR_REVIEWER_COMMENT, txt.getQuoteText());
    				approverAddReviewerFlag = true;
    			}
    			else if ( !justCommentsFlag & justCommentsType.equals(txt.getQuoteTextTypeCode()) )
    			{
    			    handler.addObject(SpecialBidParamKeys.JUST_COMMENTS, txt.getQuoteText());
    			    justCommentsFlag = true;
    			}
    			else if ( !tncCommentsFlag & tncCommentsType.equals(txt.getQuoteTextTypeCode()) )
    			{
    			    handler.addObject(SpecialBidParamKeys.TNC_COMMENTS, txt.getQuoteText()); 
    			    tncCommentsFlag = true;
    			}
    			else if ( !approverAddFlag & approverAddType.equals(txt.getQuoteTextTypeCode()) )
    			{
    			    handler.addObject(SpecialBidParamKeys.APPROVER_COMMENT_ADD, txt.getQuoteText());
    			    approverAddFlag = true;
    			}
    		}
		}
    	catch ( Exception e )
		{
    		logContext.error(this, e.toString());
    		logContext.error(this, e);
		}
    }
}
