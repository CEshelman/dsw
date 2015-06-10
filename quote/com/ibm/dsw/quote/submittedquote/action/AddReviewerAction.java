package com.ibm.dsw.quote.submittedquote.action;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.config.ViewKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.dsw.quote.home.process.LoginProcessFactory;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.dsw.quote.mail.process.SpecialBidEmailHelper;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.contract.AddReviewerContract;
import com.ibm.dsw.quote.submittedquote.exception.DuplicatedReviewerException;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
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
 * The <code>AddReviewerAction</code> class is to add reviewers to submitted
 * quote
 * 
 * @author yujjing@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public class AddReviewerAction extends SaveDraftComemntsBaseAction {

    private static final long serialVersionUID = -6415464751932787158L;
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
	static final String MSG_MAIL_SEND_FAIL = MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES + ":" + SubmittedQuoteMessageKeys.MSG_MAIL_SEND_FAIL + ":" + MessageBeanKeys.ERROR;

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        AddReviewerContract ct = (AddReviewerContract) contract;

        SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
        List redirectMsgList = new ArrayList();
        //Validate if User has privilege
        Quote quote = null;
        try {
            quote = QuoteProcessFactory.singleton().create()
                    .getSubmittedQuoteBaseInfo(ct.getQuoteNum(), ct.getUserId(), null);
        } catch (NoDataException e1) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e1));
            throw new QuoteException("The input submitted quote number is invalid.", e1);
        } catch (QuoteException e1) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e1));
            throw new QuoteException("The input submitted quote number is invalid.", e1);
        }
        boolean editableFlag = (ct.getUser().getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER)
                && quote.getQuoteUserAccess().isAnyAppTypMember();
        boolean redirectFlag = quote.getQuoteUserAccess().isReviewer();
        
        if (!editableFlag && !redirectFlag) {
            String msg = getI18NString(ErrorKeys.MSG_REQUEST_FAILED, MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, ct
                    .getLocale());
            handler.addMessage(msg, MessageBeanKeys.ERROR);
            return handler.getUndoResultBean();
        }
        
        //Insert reviewer comments into db2
        int quoteTxtId = -1;
        try{
            quoteTxtId = process.insertQuoteCommentforAP2RV(ct.getQuoteNum(),ct.getUserId(),SubmittedQuoteConstants.APPROVER_TO_REVIEWER_COMMENT,ct.getApvrReviewComments());
            logContext.debug(this,"quote text id is : " + quoteTxtId);
        }catch(Exception e){
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException("Failed to insert reviewer's comments to db2",e);
        }
        //Add reviewer
        try {
            process.addReviewer(ct.getQuoteNum(), ct.getUserId(), ct.getReviewerEmail(),quoteTxtId);
        } catch (DuplicatedReviewerException e) {
            String msg = getI18NString(SubmittedQuoteMessageKeys.MSG_EXISTED_REVIEWER, I18NBundleNames.BASE_MESSAGES,
                    ct.getLocale());
            handler.addMessage(msg, MessageBeanKeys.ERROR);
            return handler.getUndoResultBean();
        }
        
        
        //Insert reviewer's bluepage infomation to db2
        try {
            LoginProcessFactory.singleton().create().logUser(ct.getReviewerEmail(), QuoteConstants.ACCESS_LEVEL_READER);
            logContext.debug(this,"insert review's into db2");
        } catch (TopazException e2) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e2));
            throw new QuoteException("Failed to insert reviewer's bluepage infomation to db2", e2);
        } 

        //Send Email
        try {
//            QuoteHeader quoteHeader = process.getQuoteHeaderByQuoteNum(ct.getQuoteNum());
//            
//            String link = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
//                    + "&quoteNum=" + ct.getQuoteNum();
//            boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
//            
//            String ccAddressList = ct.getUserId() + "," + ( isPGSQuote ? "" : quoteHeader.getSubmitterId());
//            ccAddressList = (new SpecialBidEmailHelper()).getEditorEmailAddress(ct.getQuoteNum(), ccAddressList);
//            logContext.debug(this, "cc ccAddressList: " + ccAddressList);
//            String mailTemplate = isPGSQuote ? MailConstants.MAIL_TEMPLATE_BID_ADD_REVIEWER_PGS : MailConstants.MAIL_TEMPLATE_BID_ADD_REVIEWER;
//            MailProcessFactory.singleton().create().sendAddtionalReview(ct.getReviewerEmail(), ccAddressList, ct.getQuoteNum(),
//                    ct.getUserId(), quoteHeader.getQuoteTitle(), quoteHeader.getCustName(), ct.getApvrReviewComments(),
//                    quoteHeader.getSubmitterId(), link, mailTemplate);
            
            SpecialBidEmailHelper helper = new SpecialBidEmailHelper();
            helper.addReviewer(quote, ct.getReviewerEmail(), ct.getUserId(), ct.getApvrReviewComments());
            
            //mail no need to send submitter when request review for PGS quote 
//            if ( quote.getQuoteHeader().isPGSQuote() )
//            {
//            	link = HtmlUtil.getPGSFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
//                    + "&quoteNum=" + ct.getQuoteNum();
//            	SalesRep partner = SubmittedQuoteProcessFactory.singleton().create().getSalesRepById(quote.getQuoteHeader().getSubmitterId());
//            	MailProcessFactory.singleton().create().sendAddtionalReview(partner.getEmailAddress(), null, ct.getQuoteNum(),
//                        ct.getUserId(), quoteHeader.getQuoteTitle(), quoteHeader.getCustName(), ct.getApvrReviewComments(),
//                        quoteHeader.getSubmitterId(), link, MailConstants.MAIL_TEMPLATE_BID_ADD_REVIEWER_PGS);
//            }
            logContext.debug(this,"send email to :" + ct.getReviewerEmail());

        } catch (EmailException e) {
//            String msg = getI18NString(SubmittedQuoteMessageKeys.MSG_MAIL_SEND_ERROR, I18NBundleNames.BASE_MESSAGES, ct
//                    .getLocale());
//            handler.addMessage(msg, MessageBeanKeys.ERROR);
            logContext.error(this, "Send mail failed:" + e.getMessage());
            redirectMsgList.add(MSG_MAIL_SEND_FAIL);
//            return handler.getUndoResultBean();
        }
        
        logContext.debug(this, "save user draft comments");
        //set reviewer status
    	if(redirectFlag){
    		process.persistApproverActHistWithTransaction(ct.getQuoteNum(),ct.getUserId(), SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_REVIEWER, SubmittedQuoteConstants.REVIEWER_ACTION_ADD_COMMENT, SubmittedQuoteConstants.REVIEWER_TO_REDIRECT_COMMENT);
    	}
    	//set approver add reviewer comment null, cause don't need to save this comments as draft now
        process.deleteUserDraftComments(ct.getQuoteNum(), ct.getUserId(), 0);
        ct.setApvrReviewComments(null);
        this.saveUserDraftComments(quote, ct);
        
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
                + "&"
                + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM
                + "="
                + ct.getQuoteNum());
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        
        redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.MSG_REVIEW_REQUEST_SENT + ":" + MessageBeanKeys.INFO);
        handler.addObject(ParamKeys.PARAM_REDIRECT_MSG, redirectMsgList);
        return handler.getResultBean();
    }

    protected String getValidationForm() {
        return ViewKeys.SPECIAL_BID_FORM;
    }

    protected boolean validate(ProcessContract contract) {
        if (!super.validate(contract)) {
            return false;
        } else {
            AddReviewerContract ct = (AddReviewerContract) contract;

            BluePageUser user = BluePagesLookup.getBluePagesInfo(ct.getReviewerEmail());
            if (null == user) {
            	logContext.info(this, "Add reviewer " + ct.getReviewerEmail() + ":" + ct.getQuoteNum() + " not in BP or BP is not available now");
                HashMap vMap = new HashMap();
                FieldResult fieldResult = new FieldResult();
                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_ERRORMESSAGES, MessageKeys.EMAIL_ADDR_NOT_IN_BLUEPAGES);
                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SpecialBidViewKeys.ADD_REVIEWER_EMAIL);
                vMap.put("reviewerEmail", fieldResult);
                addToValidationDataMap(contract, vMap);
                return false;
            }
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.action.SubmittedQuoteBaseAction#getSubmittedQuoteDetail(com.ibm.dsw.quote.common.domain.Quote,
     *      com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected Quote getSubmittedQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler)
            throws QuoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.action.SubmittedQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        // TODO Auto-generated method stub
        return null;
    }

}
