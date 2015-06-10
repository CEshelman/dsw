package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.util.StringFilter;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SaveDraftCommentsBaseContract<code> class.
 *    
 * @author: Fred(qinfengc@cn.ibm.com)
 * 
 * Creation date: 2009-2-5
 */
public class SaveDraftCommentsBaseContract extends QuoteBaseCookieContract{
	
	private static final long serialVersionUID = -7665475683070553525L;

	private String specialBidCommentSubmitter;
    
    private String specialBidCommentReviewer;
    
    private String specialBidCommentApprove;
    
    private String specialBidCommentAdd;
                
    /**
     * @param apvrReviewComments The apvrReviewComments to set.
     */
    public void setApvrReviewComments(String apvrReviewComments) {
        this.apvrReviewComments = apvrReviewComments;
    }
    /**
     * @param justCommentsDraft The justCommentsDraft to set.
     */
    public void setJustCommentsDraft(String justCommentsDraft) {
        this.justCommentsDraft = justCommentsDraft;
    }
    /**
     * @param specialBidCommentAdd The specialBidCommentAdd to set.
     */
    public void setSpecialBidCommentAdd(String specialBidCommentAdd) {
        this.specialBidCommentAdd = specialBidCommentAdd;
    }
    /**
     * @param specialBidCommentApprove The specialBidCommentApprove to set.
     */
    public void setSpecialBidCommentApprove(String specialBidCommentApprove) {
        this.specialBidCommentApprove = specialBidCommentApprove;
    }
    /**
     * @param specialBidCommentReviewer The specialBidCommentReviewer to set.
     */
    public void setSpecialBidCommentReviewer(String specialBidCommentReviewer) {
        this.specialBidCommentReviewer = specialBidCommentReviewer;
    }
    /**
     * @param specialBidCommentSubmitter The specialBidCommentSubmitter to set.
     */
    public void setSpecialBidCommentSubmitter(String specialBidCommentSubmitter) {
        this.specialBidCommentSubmitter = specialBidCommentSubmitter;
    }
    /**
     * @param tncCommentsDraft The tncCommentsDraft to set.
     */
    public void setTncCommentsDraft(String tncCommentsDraft) {
        this.tncCommentsDraft = tncCommentsDraft;
    }
    private String apvrReviewComments = null;
        
    private String justCommentsDraft = null;
    
    private String tncCommentsDraft = null;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        
        specialBidCommentSubmitter = StringFilter.filter(parameters.getParameterAsString(SpecialBidParamKeys.SPECIAL_BID_COMMENT_SUBMITTER));
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(specialBidCommentSubmitter)) == true){
        	specialBidCommentSubmitter = "";
        }
        specialBidCommentReviewer = StringFilter.filter(parameters.getParameterAsString(SpecialBidParamKeys.SPECIAL_BID_COMMENT_REVIEWER));
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(specialBidCommentReviewer)) == true){
        	specialBidCommentReviewer = "";
        }
        specialBidCommentApprove = StringFilter.filter(parameters.getParameterAsString(SpecialBidParamKeys.SPECIAL_BID_COMMENT_APPROVE));
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(specialBidCommentApprove)) == true){
        	specialBidCommentApprove = "";
        }
        specialBidCommentAdd = StringFilter.filter(parameters.getParameterAsString(SpecialBidParamKeys.SPECIAL_BID_COMMENT_ADD));
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(specialBidCommentAdd)) == true){
        	specialBidCommentAdd = "";
        }
        
        apvrReviewComments = StringFilter.filter(parameters.getParameterAsString(SpecialBidParamKeys.APVR_REVIEWER_COMMENT));
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(apvrReviewComments)) == true){
        	apvrReviewComments = "";
        }
        justCommentsDraft = StringFilter.filter(parameters.getParameterAsString(SpecialBidParamKeys.SECTION_JUST_TEXTS));
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(justCommentsDraft)) == true){
        	justCommentsDraft = "";
        }
        tncCommentsDraft = StringFilter.filter(parameters.getParameterAsString(SpecialBidParamKeys.EXPLANATION_TEXT));
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(tncCommentsDraft)) == true){
        	tncCommentsDraft = "";
        }
    }
    /**
     * @return Returns the apvrReviewComments.
     */
    public String getApvrReviewComments() {
        return apvrReviewComments;
    }
    
    /**
     * @return Returns the justComments.
     */
    public String getJustCommentsDraft() {
        return justCommentsDraft;
    }
    
    /**
     * @return Returns the tncComments.
     */
    public String getTncCommentsDraft() {
        return tncCommentsDraft;
    }
    /**
     * @return Returns the specialBidCommentAdd.
     */
    public String getSpecialBidCommentAdd() {
        return specialBidCommentAdd;
    }
    /**
     * @return Returns the specialBidCommentApprove.
     */
    public String getSpecialBidCommentApprove() {
        return specialBidCommentApprove;
    }
    /**
     * @return Returns the specialBidCommentReviewer.
     */
    public String getSpecialBidCommentReviewer() {
        return specialBidCommentReviewer;
    }
    /**
     * @return Returns the specialBidCommentSubmitter.
     */
    public String getSpecialBidCommentSubmitter() {
        return specialBidCommentSubmitter;
    }
}
