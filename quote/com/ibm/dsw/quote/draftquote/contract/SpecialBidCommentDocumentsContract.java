/*
 * Created on 2007-4-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.contract;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.common.bean.Result;
import com.ibm.ead4j.common.bean.View;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.jade.util.UploadFile;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SpecialBidCommentDocumentsContract extends AttachmentsContract {
    /**
     * @return Returns the salesRepUpdatedData.
     */
    public String getSalesRepUpdatedData() {
        return salesRepUpdatedData;
    }
    private String specialBidComment;
    
    private String userRole;

    private static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    private String tncTxt;
    
    private String summaryTxt;
    
    private String salesRepUpdatedData;
    
    private String redirectURL = "";
    
    private transient SpecialBidInfo spBidInfo = null;
    
    public void load(Parameters parameters, JadeSession session) {
        try {
            super.load(parameters, session);
            String submitterComment = parameters.getParameterAsString(SpecialBidParamKeys.SPECIAL_BID_COMMENT_SUBMITTER);
            if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(submitterComment)) == true){
            	submitterComment = "";
            }
            String reviewerComment = parameters.getParameterAsString(SpecialBidParamKeys.SPECIAL_BID_COMMENT_REVIEWER);
            if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(reviewerComment)) == true){
            	reviewerComment = "";
            }
            String approverComment = parameters.getParameterAsString(SpecialBidParamKeys.SPECIAL_BID_COMMENT_ADD);
            if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(approverComment)) == true){
            	approverComment = "";
            }
            userRole = parameters.getParameterAsString(ParamKeys.PARAM_USER_ROLE);

            if ( StringUtils.equals(userRole, SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_APPROVER) )
            {
                specialBidComment = approverComment;
            }
            else if ( StringUtils.equals(userRole, SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_REVIEWER) )
            {
                specialBidComment = reviewerComment;
            }
            else if ( StringUtils.equals(userRole, SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_SUBMITTER) )
            {
                specialBidComment = submitterComment;
            }
            salesRepUpdatedData = parameters.getParameterAsString(SpecialBidParamKeys.SALES_REP_UPDATE_DATA);
            tncTxt = parameters.getParameterAsString(SpecialBidParamKeys.EXPLANATION_TEXT);
            if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(tncTxt)) == true){
            	tncTxt = "";
            }
            summaryTxt = parameters.getParameterAsString(SpecialBidParamKeys.SECTION_JUST_TEXTS);
            if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(summaryTxt)) == true){
            	summaryTxt = "";
            }
            redirectURL = parameters.getParameterAsString(ParamKeys.PARAM_REDIRECT_URL);
            String key = "jadeUnit.resultBean_previousResult_key";
            logContext.debug(this, "get previousResult key=:" + key);
            
            Result previousResult = (Result)session.getAttribute(key);
            
            logContext.debug(this, "get previousResult:" + previousResult);
            if ( previousResult != null )
            {
            	logContext.debug(this, "previousResult is not null");
            	View view = previousResult.getView();
            	logContext.debug(this, "get view: " + view);
            	if ( view instanceof DisplayQuoteBaseViewBean )
            	{
            		DisplayQuoteBaseViewBean quoteView = (DisplayQuoteBaseViewBean)view;
            		if ( quoteView != null )
            		{
            			logContext.debug(this, "quote view not null");
            			Quote quote = quoteView.getQuote();
            			if ( quote != null )
            			{
            				logContext.debug(this, "quote not null");
            				spBidInfo = quote.getSpecialBidInfo();
            			}
            		}
            	}
            }
        } catch (Throwable e) {
            logContext.error(this, e);
        }
    }
    
    public String getSpecialBidComment() {
        return specialBidComment;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    /**
     * @return Returns the summaryTxt.
     */
    public String getSummaryTxt() {
        return summaryTxt;
    }
    /**
     * @return Returns the tncTxt.
     */
    public String getTncTxt() {
        return tncTxt;
    }
    
    protected QuoteAttachment jadeUploadFileToAttechment(UploadFile file){
        if(file == null){
            return null;
        }
        
        QuoteAttachment attchmt = super.jadeUploadFileToAttechment(file);
        attchmt.setClassfctnCode(QuoteConstants.QT_ATTCHMNT_SPEL_BID);
        
        return attchmt;
    }
    
    /**
     * @return Returns the redirectURL.
     */
    public String getRedirectURL() {
        return redirectURL;
    }
    
    /**
	 * @return Returns the spBidInfo.
	 */
	public SpecialBidInfo getSpBidInfo() {
		return spBidInfo;
	}

}