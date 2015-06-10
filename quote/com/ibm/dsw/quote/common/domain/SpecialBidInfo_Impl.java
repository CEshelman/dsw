package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidInfo_Impl</code> class is abstract implementation of
 * SpecialBidInfo domain.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 20, 2007
 */
public abstract class SpecialBidInfo_Impl implements SpecialBidInfo {
    public static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    public String userMail;
    
    public String sWebQuoteNum;
    
    public String salesPlayNum;
    
    public boolean initSpeclBidApprFlag;

    public boolean bCredAndRebillFlag;

    public boolean bTermsAndCondsChgFlag;

    public boolean bSetCtrctLvlPricngFlag;
    
    public boolean bPPWSSetCtrctLvlPricngFlag;

    public boolean bFulfllViaLanddMdlFlag;

    public boolean bElaTermsAndCondsChgFlag;

    public boolean bPreApprvdCtrctLvlPricFlg;

    public boolean bRyltyDiscExcddFlag;

    public String sSpBidRgn;

    public String sSpBidDist;

    public String sSpBidType; // waiting for approvals to add to db2 table & sp

    public String sSpBidCustIndustryCode;

    public String sSalesDiscTypeCode;

    public String sCreditJustText;

    public String sSpBidJustText;

    public List spBidCategories = new ArrayList();

    public List approverComments = new ArrayList();//array of ApproverComment

    public List chosenApprovers = new ArrayList();//array of String

    public List allApprovers = new ArrayList();//array of Approver

    public List reviewerComments = new ArrayList();//array of ReviewerComment

    public List attachements = new ArrayList();//array of Attachement

    public List questions = new ArrayList(); //array of SpecialBidQuestion
    
    public List addtnlJustTexts = new ArrayList(); //array of CommentInfo
    
    public List userComments = new ArrayList();
    
    public List allTypeInfo = new ArrayList();
    
    public String competitorName;
    
    public String competitorPrice;
    
    public String competitorProduct;
    
    public String competitorTC;
    
    public List justSections = new ArrayList(); //array of Section
    
    public List reviewerAttachs = new ArrayList();
    
    public List approverAttachs = new ArrayList();
    
    public boolean forceUpdate = true;
    
    public boolean isCompetitive = false;
    
    public boolean resellerAuthorizedToPortfolio =false;
    protected List approverReviewRequests = new ArrayList();
    
    protected List reviewerReviews = new ArrayList();
    
    public int rateBuyDown = 0;
    
    public int SWGIncur = 0;
    
    public Double financeRate = null;
    
    public Double progRBD = null;
    
    public Double incrRBD = null;
    
    public int attachmentCount = 0;
    
    public String orgnlSalesOrdNum;
    
    public String orgnlQuoteNum;
    
    public boolean origSalesOrdNumInvld = false;
    
    public boolean isExtendedAllowedYears = false;
    
    public boolean partLessOneYear = false;
    
    public boolean isCompressedCover = false;
    
    public boolean isCpqExcep = false;
    
    public boolean overBySQOBasic = false;
    
    public String evaltnAction;
    
    public String evaltnComment;
    
    public boolean bSplitBidFlag;
    
    public boolean isProvisngDaysChanged = false;
    
    public boolean isGridDelegationFlag;
    
    public String channelOverrideDiscountReasonCode;
    
    public boolean isTrmGrt60Mon = false;
    
    /**
     * @return Returns the orgnlQuoteNum.
     */
    public String getOrgnlQuoteNum() {
        return orgnlQuoteNum;
    }
    /**
     * @param orgnlQuoteNum The orgnlQuoteNum to set.
     */
    public void setOrgnlQuoteNum(String orgnlQuoteNum) {
        this.orgnlQuoteNum = orgnlQuoteNum;
    }
    /**
     * @return Returns the orgnlSalesOrdNum.
     */
    public String getOrgnlSalesOrdNum() {
        return orgnlSalesOrdNum;
    }
    public boolean isCpqExcep() {
		return isCpqExcep;
	}
	public void setCpqExcep(boolean isCpqExcep) {
		this.isCpqExcep = isCpqExcep;
	}
	
	public boolean isOverBySQOBasic() {
		return overBySQOBasic;
	}
	public void setOverBySQOBasic(boolean overBySQOBasic) {
		this.overBySQOBasic = overBySQOBasic;
	}
	/**
     * @param orgnlSalesOrdNum The orgnlSalesOrdNum to set.
     */
    public void setOrgnlSalesOrdNum(String orgnlSalesOrdNum) {
        this.orgnlSalesOrdNum = orgnlSalesOrdNum;
    }
    
    /**
     * @return Returns the financeRate.
     */
    public Double getFinanceRate() {
        return financeRate;
    }
    /**
     * @return Returns the rateBuyDown.
     */
    public int getRateBuyDown() {
        return rateBuyDown;
    }
    /**
     * @return Returns the sWGIncur.
     */
    public int getSWGIncur() {
        return SWGIncur;
    }
    /**
     * @return Returns the approverReviewRequests.
     */
    public List getApproverReviewRequests() {
        return approverReviewRequests;
    }
    /**
     * @param approverReviewRequests The approverReviewRequests to set.
     */
    public void setApproverReviewRequests(List approverReviewRequests) {
        this.approverReviewRequests = approverReviewRequests;
    }
    /**
     * @return Returns the reviewerReview.
     */
    public List getReviewerReviews() {
        return reviewerReviews;
    }
    /**
     * @param reviewerReview The reviewerReview to set.
     */
    public void setReviewerReviews(List reviewerReviews) {
        this.reviewerReviews = reviewerReviews;
    }
    /**
     * @return Returns the approverAttachs.
     */
    public List getApproverAttachs() {
        return approverAttachs;
    }
    
    /**
     * @return Returns the reviewerAttachs.
     */
    public List getReviewerAttachs() {
        return reviewerAttachs;
    }
    
    /**
     * @param forecUpdate The forecUpdate to set.
     */
    public void setForceUpdate(boolean forecUpdate) {
        this.forceUpdate = forecUpdate;
    }
    
    /**
     * @return Returns the forecUpdate.
     */
    public boolean isForceUpdate() {
        return forceUpdate;
    }
    
    /**
     * @return Returns the isCompetitive.
     */
    public boolean isCompetitive() {
        return isCompetitive;
    }
    
    /**
     * @param isCompetitive The isCompetitive to set.
     */
    public void setCompetitive(boolean isCompetitive) {
        this.isCompetitive = isCompetitive;
    }
    
    public List getJustSections()
    {
        return justSections;   
    }
    
    public int getSecLogicId()
    {
        List list = this.getJustSections();
        int size = list.size();
        JustSection sec = (JustSection)list.get(size - 1);
        return sec.getSecId() + 1;
    }
    
    public int getSecDisplayId()
    {
        List list = this.getJustSections();
        int size = list.size();
        return size;
    }
    
    /**
     * @return Returns the competitorName.
     */
    public String getCompetitorName() {
        return competitorName;
    }
    /**
     * @return Returns the competitorPrice.
     */
    public String getCompetitorPrice() {
        return competitorPrice;
    }
    /**
     * @return Returns the competitorProduct.
     */
    public String getCompetitorProduct() {
        return competitorProduct;
    }
    /**
     * @return Returns the competitorTC.
     */
    public String getCompetitorTC() {
        return competitorTC;
    }
    public boolean isCredAndRebill() {
        return bCredAndRebillFlag;
    }

    public boolean isElaTermsAndCondsChg() {
        return bElaTermsAndCondsChgFlag;
    }

    public boolean isFulfllViaLanddMdl() {
        return bFulfllViaLanddMdlFlag;
    }

    public boolean isPreApprvdCtrctLvlPric() {
        return bPreApprvdCtrctLvlPricFlg;
    }

    public boolean isRyltyDiscExcdd() {
        return bRyltyDiscExcddFlag;
    }

    public boolean isSetCtrctLvlPricng() {
        return bSetCtrctLvlPricngFlag;
    }
    public boolean isPPWSSetCtrctLvlPricngFlag() {
        return bPPWSSetCtrctLvlPricngFlag;
    }
    public void setPPWSSetCtrctLvlPricngFlag(boolean setCtrctLvlPricngFlag) {
        bPPWSSetCtrctLvlPricngFlag = setCtrctLvlPricngFlag;
    }
    public boolean isTermsAndCondsChg() {
        return bTermsAndCondsChgFlag;
    }

    public String getCreditJustText() {
        return sCreditJustText;
    }

    public List getSpBidCategories() {
        return spBidCategories;
    }

    public String getSalesDiscTypeCode() {
        return sSalesDiscTypeCode;
    }

    public String getSpBidCustIndustryCode() {
        return sSpBidCustIndustryCode;
    }

    public String getSpBidDist() {
        if (sSpBidDist == null || "null".equals(sSpBidDist)) {
            sSpBidDist = "";
        }
        return sSpBidDist;
    }

    public String getSpBidJustText() {
        return sSpBidJustText;
    }

    public String getSpBidRgn() {
        if (sSpBidRgn == null || "null".equals(sSpBidRgn)) {
            sSpBidRgn = "";
        }
        return sSpBidRgn;
    }

    public String getSpBidType() {
        return sSpBidType;
    }

    public String getWebQuoteNum() {
        return sWebQuoteNum;
    }

    public List getAllApprovers() {
        return allApprovers;
    }

    public List getApproverComments() {
        return approverComments;
    }

    public List getAttachements() {
        return attachements;
    }

    public List getChosenApprovers() {
        return chosenApprovers;
    }

    public List getReviewerComments() {
        return reviewerComments;
    }

    public List getQuestions() {
        return this.questions;
    }
    
    public List getAddtnlJustTexts() {
        return this.addtnlJustTexts;
    }
    
	public boolean isSplitBid() {
		return bSplitBidFlag;
	}
	
    /**
     * @param competitorName The competitorName to set.
     */
    public void setCompetitorName(String competitorName) {
        this.competitorName = competitorName;
    }
    /**
     * @param competitorPrice The competitorPrice to set.
     */
    public void setCompetitorPrice(String competitorPrice) {
        this.competitorPrice = competitorPrice;
    }
    /**
     * @param competitorProduct The competitorProduct to set.
     */
    public void setCompetitorProduct(String competitorProduct) {
        this.competitorProduct = competitorProduct;
    }
    /**
     * @param competitorTC The competitorTC to set.
     */
    public void setCompetitorTC(String competitorTC) {
        this.competitorTC = competitorTC;
    }
    
    /**
     * @return Returns the userMail.
     */
    public String getUserMail() {
        return userMail;
    }
    /**
     * @param userMail The userMail to set.
     */
    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    /**
     *  
     */

    public String toString() {
        StringBuffer spBidInfo = new StringBuffer();
        spBidInfo.append("\n outline of special bid info in quote - " + this.sWebQuoteNum);
        spBidInfo.append("\n special bid region = " + sSpBidRgn);
        spBidInfo.append("\n special bid district = " + sSpBidDist);
        spBidInfo.append("\n assigned approvers = " + this.chosenApprovers);
        spBidInfo.append("\n all approvers = " + this.allApprovers);
        spBidInfo.append("\n approver comments = " + this.approverComments);
        spBidInfo.append("\n reviewer comments = " + this.reviewerComments);
        spBidInfo.append("\n submitter comments = " + this.addtnlJustTexts);
        return spBidInfo.toString();
    }
    
    protected void migrateReviewComments()
    {
        if ( this.approverReviewRequests.size() == 0 )
        {
            return;
        }
        Map map = new HashMap();
        for ( int i = 0; i < this.approverReviewRequests.size(); i++ )
        {
            SpecialBidInfo.ReviewerComment requester = (SpecialBidInfo.ReviewerComment)this.approverReviewRequests.get(i);
            String key = requester.requesterEmail + "@" + requester.getReviewerEmail();
            SpecialBidInfo.ReviewerComment comment = (SpecialBidInfo.ReviewerComment)map.get(key);
            if ( comment == null )
            {
                comment = new SpecialBidInfo.ReviewerComment();
                comment.requesterName = requester.getRequesterName();
                comment.requesterEmail = requester.getRequesterEmail();
                comment.requesterComments.add(requester);
                comment.reviewerEmail = requester.getReviewerEmail();
                comment.reviewerName = requester.getReviewerName();
                comment.requestDate = requester.requestDate;
                map.put(key, comment);
                this.reviewerComments.add(comment);
            }
            else
            {
                comment.requestDate = requester.requestDate;
                comment.requesterComments.add(requester);
            }
            for ( int j = 0; j < this.reviewerReviews.size(); j++ )
            {
                SpecialBidInfo.ReviewerComment reviewerComment = (SpecialBidInfo.ReviewerComment)this.reviewerReviews.get(j);
                if ( !StringUtils.equals(reviewerComment.getReviewerEmail(), requester.getReviewerEmail()) )
                {
                    continue;
                }
                if ( reviewerComment.revwerCommetTimestamp.after(requester.requestDate) )
                {
                    comment.reviewerComments.add(reviewerComment);
                    this.reviewerReviews.remove(j);
                    j--;
                }
            }
        }
        for ( int i = 0; i < this.reviewerComments.size(); i++ )
        {
            SpecialBidInfo.ReviewerComment comment = (SpecialBidInfo.ReviewerComment)this.reviewerComments.get(i);
            java.util.Collections.reverse(comment.requesterComments);
            java.util.Collections.reverse(comment.reviewerComments);
        }
        java.util.Collections.sort(this.reviewerComments);
    }
    
    public void migrateTextAndAttachment()
    {
        //first migrate approver review request and reviewer review
        migrateReviewComments();
        
        logContext.debug(this, "begin migrateTextAndAttachment");
        justSections.clear();
        this.userComments.clear();
        List textList = this.getAddtnlJustTexts();
        List attachList = this.getAttachements();
        if ( textList == null && attachList == null )
        {
            return;
        }
        int size1 = textList.size();
        int size2 = attachList.size();
        logContext.debug(this, "begin migration: " + size1 + ":" + size2);
        if ( size1 == 0 && size2 == 0 )
        {
            return;
        }
        
            //combose text if sec id equals
        Map map = new HashMap();
        for ( int i = 0; i < size1; i++ )
        {
            SpecialBidInfo.CommentInfo cmtInfo = (SpecialBidInfo.CommentInfo)textList.get(i);
            int secId = cmtInfo.getSecId();
            if ( secId >= SpecialBidInfo.BEGIN_SUBMITTER )
            {
	            JustSection secTemp = (JustSection)map.get(Integer.toString(secId));
	            if ( secTemp == null )
	            {
	                secTemp = new JustSection_Impl();
	                secTemp.setSecId(secId);
	                justSections.add(secTemp);
	                map.put(Integer.toString(secId), secTemp);
	            }
	            secTemp.getJustTexts().add(cmtInfo);
            }
            else if ( secId == SpecialBidInfo.BEGIN_USER_COMMENTS )
            {
                this.userComments.add(cmtInfo);
            }
            else
            {
                SpecialBidInfo_Impl.logContext.info(this, "unknow txt secId=" + secId);
            }
        }
        approverAttachs.clear();
        reviewerAttachs.clear();
        Date d = getLastQuoteStatusChangeDate();
        for ( int i = 0; i < size2; i++ )
        {
            QuoteAttachment attach = (QuoteAttachment)attachList.get(i);
            attach.setRemoveURL(this.genLinkURLsAndParams(attach.getQuoteNumber(), attach.getId()));
            String secIdStr = attach.getSecId();
            int secId = SpecialBidInfo.BEGIN_SUBMITTER + 1;
            try
            {
                secId = Integer.parseInt(secIdStr);
            }
            catch ( Exception e )
            {
            	logContext.error(this, e);
            }
            attach.setSecId(Integer.toString(secId));
            if ( secId == SpecialBidInfo.BEGIN_APPROVER )
            {
                approverAttachs.add(attach);
                if ( d == null )
                {
                    attach.setOverLastApproveAction(true);
                }
                else
                {
                    if ( d.after(attach.getAddDate()) )
                    {
                        attach.setOverLastApproveAction(false);
                    }
                }
            }
            else if ( secId == SpecialBidInfo.BEGIN_REVIEWER )
            {
                reviewerAttachs.add(attach);
            }
            else
            {
	            JustSection secTemp = (JustSection)map.get(Integer.toString(secId));
	            if ( secTemp == null )
	            {
	                //sec text not exists, create a new one
	                secTemp = new JustSection_Impl();
	                secTemp.setSecId(secId);
	                justSections.add(secTemp);
	                map.put(Integer.toString(secId), secTemp);
	                
	                SpecialBidInfo.CommentInfo cmtInfo = new SpecialBidInfo.CommentInfo();
	                cmtInfo.secId = secId;
	                cmtInfo.comment = "";
	                secTemp.getJustTexts().add(cmtInfo);
	            }
	            secTemp.getAttachs().add(attach);
            }
        }
        for ( int i = 0; i < justSections.size(); i++ )
        {
            JustSection secTemp = (JustSection)justSections.get(i);
            if ( secTemp.isEmpty() )
            {
                justSections.remove(i);
                i--;
                logContext.debug(this, "remove a empty section: " + secTemp.getSecId());
            }
        }
        Collections.sort(justSections);
    }
    
    private java.util.Date getLastQuoteStatusChangeDate()
    {
        List list = this.approverComments;
        if ( list == null )
        {
            return null;
        }
        java.util.Date d = null;
        for ( int i = 0; i < list.size(); i++ )
        {
            SpecialBidInfo.ApproverComment cmt = (SpecialBidInfo.ApproverComment)list.get(i);
            String action = cmt.getComment().getAction();
            logContext.debug(this, "user action in comments: " + action);
            if ( SubmittedQuoteConstants.APPRVR_ACTION_APPROVE.equalsIgnoreCase(action) 
            		|| SubmittedQuoteConstants.APPRVR_ACTION_REJECT.equalsIgnoreCase(action) 
					|| SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO.equalsIgnoreCase(action)
					|| SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_CHANGES.equalsIgnoreCase(action) )
            {
                d = cmt.getComment().getCommentDate();
                return d;
            }
        }
        return d;
    }
    
    private  String genLinkURLsAndParams(String quoteNum,String fileNum) {
    	
    	String targetParams = ","+DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM+"=" + quoteNum + ",attchmtSeqNum=" + fileNum;
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        StringBuffer sb = new StringBuffer();
        String targetAction = DraftQuoteActionKeys.REMOVE_SB_ATTACHMENT_ACTION;
        String secondAction = DraftQuoteActionKeys.DISPLAY_SPECIAL_BID_TAB;
        
        sb.append(actionKey).append("=").append(targetAction);
        if (StringUtils.isNotBlank(secondAction))
            sb.append("," + appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY) + "=").append(
                    secondAction);
        if (StringUtils.isNotBlank(targetParams))
            sb.append(",").append(targetParams);
        return sb.toString();
    }
    
    /**
     * @return Returns the userComments.
     */
    public List getUserComments() {
        return userComments;
    }
    
    /**
     * @param financeRate The financeRate to set.
     */
/*    public void setFinanceRate(Double financeRate) {
        this.financeRate = financeRate;
    }*/
    /**
     * @param rateBuyDown The rateBuyDown to set.
     */
    public void setRateBuyDown(int rateBuyDown) {
        this.rateBuyDown = rateBuyDown;
    }
    /**
     * @param incur The sWGIncur to set.
     */
/*    public void setSWGIncur(int incur) {
        SWGIncur = incur;
    }*/
    
    public int getAttachmentCount() {
        return attachmentCount;
    }
    
    public boolean isOrigSalesOrdNumInvld() {
        return origSalesOrdNumInvld;
    }
	/**
	 * @return Returns the resellerAuthorizedToPortfolio.
	 */
	public boolean isResellerAuthorizedToPortfolio() {
		return resellerAuthorizedToPortfolio;
	}
	/**
	 * @param resellerAuthorizedToPortfolio The resellerAuthorizedToPortfolio to set.
	 */
	public void setResellerAuthorizedToPortfolio(
			boolean resellerAuthorizedToPortfolio) {
		this.resellerAuthorizedToPortfolio = resellerAuthorizedToPortfolio;
	}
    /**
     * @return Returns the allTypeInfo.
     */
    public List getAllTypeInfo() {
        return allTypeInfo;
    }
    
	public void setExtendedAllowedYears(boolean isExtendedAllowedYears){
	    this.isExtendedAllowedYears = isExtendedAllowedYears;
	}
	public boolean isExtendedAllowedYears(){
	    return isExtendedAllowedYears;
	}
    /**
     * @return Returns the partLessOneYear.
     */
    public boolean isPartLessOneYear() {
        return partLessOneYear;
    }
    /**
     * @param partLessOneYear The partLessOneYear to set.
     */
    public void setPartLessOneYear(boolean partLessOneYear) {
        this.partLessOneYear = partLessOneYear;
    }
    /**
     * @return Returns the isCompressedCover.
     */
    public boolean isCompressedCover() {
        return isCompressedCover;
    }
    /**
     * @param isCompressedCover The isCompressedCover to set.
     */
    public void setCompressedCover(boolean isCompressedCover) {
        this.isCompressedCover = isCompressedCover;
    }
    
    public String getSalesPlayNum()
    {
        return this.salesPlayNum;
    }
    
    public void setSalesPlayNum(String salesPlayNum)
    {
        this.salesPlayNum = salesPlayNum;
    }
  
    public boolean isInitSpeclBidApprFlag() {
        return initSpeclBidApprFlag;
    }
    
    public void setInitSpeclBidApprFlag(boolean initSpeclBidApprFlag) {
        this.initSpeclBidApprFlag = initSpeclBidApprFlag;
    }
    
    /**
     * @return Returns the progRBD.
     */
	public Double getProgRBD() {
		return this.progRBD;
	}

    /**
     * @return Returns the incrRBD.
     */
	public Double getIncrRBD() {
		return this.incrRBD;
	}

    /**
     * @param progRBD The progRBD to set.
     */
	public void setProgRBD(Double progRBD) {
		this.progRBD = progRBD;
	}
	
    /**
     * @param incrRBD The incrRBD to set.
     */
	public void setIncrRBD(Double incrRBD) {
		this.incrRBD = incrRBD;
	}
	
	public void setEvaltnAction(String evaltnAction) {
		this.evaltnAction = evaltnAction;
	}
	
	public String getEvaltnAction() {
		if(StringUtils.isNotBlank(evaltnAction)) {
			return evaltnAction;
		} else {
			return QuoteConstants.EVAL_SELECT_OPTION_SUBMIT;
		}
	}
	
	public void setEvaltnComment(String evaltnComment) {
		this.evaltnComment = evaltnComment;
	}
	
	public String getEvaltnComment() {
		return evaltnComment;
	}

    public boolean isProvisngDaysChanged() {
        return isProvisngDaysChanged;
    }

    public void setProvisngDaysChanged(boolean isProvisngDaysChanged) {
        this.isProvisngDaysChanged = isProvisngDaysChanged;
    }
    
    public boolean isGridFlag(){
    	return isGridDelegationFlag;
    }
    
    public String getChannelOverrideDiscountReasonCode(){
    	return this.channelOverrideDiscountReasonCode;
    }
	public boolean isTrmGrt60Mon() {
		return isTrmGrt60Mon;
	}
	public void setTrmGrt60Mon(boolean isTrmGrt60Mon) {
		this.isTrmGrt60Mon = isTrmGrt60Mon;
	}
    
    
}
