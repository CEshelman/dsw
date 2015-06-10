package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidInfo</code> class is for all special bid related
 * information in quote.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 20, 2007
 */
public interface SpecialBidInfo {
	public static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    public static final int BEGIN_SUBMITTER = 100;
    public static final int BEGIN_REVIEWER = 0;
    public static final int BEGIN_APPROVER = 1;
    public static final int BEGIN_USER_COMMENTS = 2;
    public static final int BEGIN_TNC = 3;
    
    public boolean isCpqExcep();
    
    public void setCpqExcep(boolean cpqExcep);
    
    public boolean isOverBySQOBasic();
    
    public void setOverBySQOBasic(boolean overBySQOBasic);
    
    public boolean isInitSpeclBidApprFlag();
    
    public void setInitSpeclBidApprFlag(boolean flag);
    
    public String getSalesPlayNum();
    
    public void setSalesPlayNum(String salesPlayNum);
    
    public String getOrgnlSalesOrdNum();
    
    public String getOrgnlQuoteNum();
    
    public void setOrgnlSalesOrdNum(String orgnlSalesOrdNum);
    
    public void setOrgnlQuoteNum(String orgnlQuoteNum);
    
    public int getRateBuyDown();
    
    public int getSWGIncur();
    
    public Double getFinanceRate();
    
    public Double getProgRBD();
    
    public Double getIncrRBD();
    
    public void setRateBuyDown(int rateBuyDown);
    
    //public void setSWGIncur(int SWGIncur);
    
    //public void setFinanceRate(Double financeRate);
    
    public void setProgRBD(Double progRBD);
    
    public void setIncrRBD(Double incrRBD);
    
    public String getUserMail();
    
    public void setUserMail(String userMail);
    
    public int getSecDisplayId();
    
    public int getSecLogicId();
    
    public String getCompetitorName();
    
    public void setCompetitorName(String competitorName);
    
    public String getCompetitorPrice();
    
    public void setCompetitorPrice(String competitorPrice);
    
    public String getCompetitorProduct();
    
    public void setCompetitorProduct(String competitorProduct);
    
    public String getCompetitorTC();
    
    public void setCompetitorTC(String competitorTC);
    
    public boolean isCompetitive();
    
    public void setCompetitive(boolean isCompetitive);
    
    public void  setEvaltnAction(String evaltnAction);
    
    public void setEvaltnComment(String evaltnComment);
    
    public List getJustSections();
    
    public List getReviewerAttachs();
    
    public List getApproverAttachs();
    
    public List getAllTypeInfo();
    
    public boolean isCredAndRebill();

    public boolean isElaTermsAndCondsChg();

    public boolean isFulfllViaLanddMdl();

    public boolean isPreApprvdCtrctLvlPric();

    public boolean isRyltyDiscExcdd();

    public boolean isSetCtrctLvlPricng();

    public boolean isTermsAndCondsChg();

    public String getCreditJustText();

    public List getSpBidCategories();

    public String getSalesDiscTypeCode();

    public String getSpBidCustIndustryCode();

    public String getSpBidDist();

    public String getSpBidJustText();

    public String getSpBidRgn();

    public String getSpBidType();

    public String getWebQuoteNum();
    
    public int getAttachmentCount();
    
    public boolean isOrigSalesOrdNumInvld();

    public List getAllApprovers();

    public List getApproverComments();

    public List getAttachements();

    public List getChosenApprovers();

    public List getReviewerComments();

    // added 06/12/2007 for CMRE #46
    public List getQuestions();

    // additional justification text by submitter
    public List getAddtnlJustTexts();
    
    public List getUserComments();
    
    public String getEvaltnAction();
    
    public String getEvaltnComment();

    public boolean isSplitBid();

    /**
     * @param credAndRebillFlag
     *            The bCredAndRebillFlag to set.
     */
    public void setCreditAndRebillFlag(boolean credAndRebillFlag) throws TopazException;

    /**
     * @param elaTermsAndCondsChgFlag
     *            The bElaTermsAndCondsChgFlag to set.
     */
    public void setElaTermsAndCondsChgFlag(boolean elaTermsAndCondsChgFlag) throws TopazException;

    /**
     * @param fulfllViaLanddMdlFlag
     *            The bFulfllViaLanddMdlFlag to set.
     */
    public void setFulfllViaLanddMdlFlag(boolean fulfllViaLanddMdlFlag) throws TopazException;

    /**
     * @param preApprvdCtrctLvlPricFlg
     *            The bPreApprvdCtrctLvlPricFlg to set.
     */
    public void setPreApprvdCtrctLvlPricFlg(boolean preApprvdCtrctLvlPricFlg) throws TopazException;

    /**
     * @param ryltyDiscExcddFlag
     *            The bRyltyDiscExcddFlag to set.
     */
    public void setRyltyDiscExcddFlag(boolean ryltyDiscExcddFlag) throws TopazException;

    /**
     * @param setCtrctLvlPricngFlag
     *            The bSetCtrctLvlPricngFlag to set.
     */
    public void setSetCtrctLvlPricngFlag(boolean setCtrctLvlPricngFlag) throws TopazException;

    /**
     * @param termsAndCondsChgFlag
     *            The bTermsAndCondsChgFlag to set.
     */
    public void setTermsAndCondsChgFlag(boolean termsAndCondsChgFlag) throws TopazException;

    /**
     * @param questions
     */
    public void setQuestions(List questions) throws TopazException;

    /**
     * @param creditJustText
     *            The sCreditJustText to set.
     */
    public void setCreditJustText(String creditJustText) throws TopazException;

    /**
     * @param spBidCategories
     *            The spBidCategories to set.
     */
    public void setSpBidCategories(List spBidCategories) throws TopazException;

    /**
     * @param salesDiscTypeCode
     *            The salesDiscTypeCode to set.
     */
    public void setSalesDiscTypeCode(String salesDiscTypeCode) throws TopazException;

    /**
     * @param spBidCustIndustryCode
     *            The spBidCustIndustryCode to set.
     */
    public void setSpBidCustIndustryCode(String spBidCustIndustryCode) throws TopazException;

    /**
     * @param spBidDist
     *            The spBidDist to set.
     */
    public void setSpBidDist(String spBidDist) throws TopazException;

    /**
     * @param spBidJustText
     *            The spBidJustText to set.
     */
    public void setSpBidJustText(String spBidJustText) throws TopazException;

    /**
     * @param spBidRgn
     *            The spBidRgn to set.
     */
    public void setSpBidRgn(String spBidRgn) throws TopazException;
    
    public void migrateTextAndAttachment();
    
    /**
     * @param spBidType
     *            The spBidType to set.
     */
    public void setSpBidType(String spBidType) throws TopazException;
    
    /**
     * @param splitBidFlag
     *            The bSplitBidFlag to set.
     */
    public void setSplitBidFlag(boolean splitBidFlag) throws TopazException;

    public static class Approver implements Serializable{
		private static final long serialVersionUID = -905589695862690821L;

		public String groupName;

        public String groupType;

        public int level;
        
        public int rdyToOrder;

        public String userEmail;

        public String approverName;
        
        public String superSedeApproveType;

        public String getApproverName() {
            return approverName;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getGroupType() {
            return groupType;
        }

        public int getLevel() {
            return level;
        }
        
        public int getRdyToOrder() {
            return rdyToOrder;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String toString() {
            return userEmail;
        }

        public String getSuperSedeApproveType() {
			return superSedeApproveType;
		}
    }

    public static class ApproverComment implements Serializable, Comparable{
		private static final long serialVersionUID = 1049970097259554372L;

		public String userEmail;

        public String approverName;

        public int approverLvl;
        
        public String idoc;
        
        public String returnReason;
        
        public String getIdoc() {
            return idoc;
        }

        public String getApproverName() {
            return approverName;
        }

        public CommentInfo comment = null;

        public CommentInfo getComment() {
            return comment;
        }

        public int getApproverLvl() {
            return approverLvl;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String toString() {
            return approverLvl + ":" + userEmail + ":" + comment + ":" + returnReason;
        }

		public String getReturnReason() {
			return returnReason;
		}
		
        public int compareTo(Object o) {
            if (!( o instanceof ApproverComment ) )
            {
                return -1;
            }
            ApproverComment ac = (ApproverComment)o;
            return this.getComment().getCommentDate().after(ac.getComment().getCommentDate()) ? -1 : 1;
        }
    }

    public static class Attachement implements Serializable{
   
		private static final long serialVersionUID = -4558894282025340706L;

		public String webQuoteNum;

        public String seqNum;

        public String fileName;

        public int fileSize;

        public String userEmail;

        public Timestamp addDate;
        
        public String removeUrl;
        
        public int secId;
        
        public String status;
        
        public String getAddDateString()
        {
            if ( addDate == null )
            {
                return "";
            }
            String str = addDate.toString();
            int index = str.indexOf('.');
            return str.substring(0, index);
        }

        /**
         * @return Returns the status.
         */
        public String getStatus() {
            return status;
        }
        public Timestamp getAddDate() {
            return addDate;
        }

        public String getFileName() {
            return fileName;
        }

        public int getFileSize() {
            return fileSize;
        }

        public String getSeqNum() {
            return seqNum;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getWebQuoteNum() {
            return webQuoteNum;
        }
        
        public String getRemoveUrl(){
        	return removeUrl;
        }
        /**
         * @return Returns the secId.
         */
        public int getSecId() {
            return secId;
        }
    }

    public static class ChosenApprover implements Serializable{
     
		private static final long serialVersionUID = 8977984860533722127L;

		public int groupLevel;

        public String userEmail;

        public String groupName;

        public String lastAction;

        public Date modeDate;

        public String approverName;

        public String superSedeApproveType;
        
        public int rdyToOrder;

        public String getApproverName() {
            return approverName;
        }

        public int getGroupLevel() {
            return groupLevel;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getLastAction() {
            return lastAction;
        }

        public Date getModeDate() {
            return modeDate;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String toString() {
            return userEmail + ": lastAction=" + lastAction;
        }

        public String getSuperSedeApproveType() {
			return superSedeApproveType;
		}

		public int getRdyToOrder() {
			return rdyToOrder;
		}

		public void setRdyToOrder(int rdyToOrder) {
			this.rdyToOrder = rdyToOrder;
		}
        
    }

    public static class ReviewerComment implements Serializable, Comparable{
      
		private static final long serialVersionUID = 7503224621037423273L;

		public String reviewerEmail;  //2

        public String reviewerName; //8

        public String requesterEmail;//1

        public String requesterName;//7
        
        public String reviewerTxt;//4
        
        public String requestTxt;//5
        
        public Timestamp requestDate;//6
                
        public transient List reviewerComments = new ArrayList();
        
        public transient List requesterComments = new ArrayList();
        
        public Timestamp revwerCommetTimestamp;
        
        public Timestamp getRequestDate() {
            return requestDate;
        }
        
        public String getRequesterName() {
            return requesterName;
        }

        public String getReviewerName() {
            return reviewerName;
        }

        public List getRequesterComments(){
            return requesterComments;
        }
        
        public List getReviewerComments() {
            return reviewerComments;
        }

        

        public String getRequesterEmail() {
            return requesterEmail;
        }

        public String getReviewerEmail() {
            return reviewerEmail;
        }
       
        public String getRequestTxt() {
            return requestTxt;
        }
       
        public void setRequestTxt(String requestTxt) {
            this.requestTxt = requestTxt;
        }
        
        public Timestamp getRevwerCommetTimestamp() {
            return revwerCommetTimestamp;
        }

        public void setRevwerCommetTimestamp(Timestamp revwerCommetTimestamp) {
            this.revwerCommetTimestamp = revwerCommetTimestamp;
        }
        
        public String getReviewerTxt() {
            return reviewerTxt;
        }

        public int compareTo(Object o) {
            ReviewerComment cmt = (ReviewerComment)o;
            if ( requestDate.after(cmt.getRequestDate()) )
            {
                return 1;
            }
            return -1;
        }
    }

    public static class CommentInfo implements Serializable, Comparable{
      
		private static final long serialVersionUID = -5346919653235428802L;

		public String comment;

        public java.util.Date commentDate;
        
        public String formatCommentDate;

        public String action;
        
        public String textId;
        
        public int secId;
        
        public String typeCode;
        
        public static final String SBADJUST = "SBADJUST";
        public static final String SPBID_J = "SPBID_J";
        public static final String CREDIT_J = "CREDIT_J";
        public static final String AP2RWCMT = "AP2RWCMT";
        /**
         * @return Returns the typeCode.
         */
        public String getTypeCode() {
            return this.typeCode;
        }
        public String commentDateText;
        
        /**
         * @return Returns the commentDateText.
         */
        public String getCommentDateText() {
            return commentDateText;
        }
        /**
         * @return Returns the secId.
         */
        public int getSecId() {
            return secId;
        }
        /**
         * @return Returns the textId.
         */
        public String getTextId() {
            return textId;
        }
        
        public CommentInfo(){}
        
        /**
         * @param date
         * @param string
         */
        public CommentInfo(Date date, String string, String action) {
        	if (string == null){
        		this.comment = "";
        	} else {
        		this.comment = string;
        	}
            this.formatCommentDate = DateHelper.getDateByFormat(date, "d MMM yyyy HH:mm:ss z");
            this.commentDate = date;
            this.action = action;
        }
        
        public CommentInfo(Timestamp stamp,String string,String action){
        	if (string == null){
        		this.comment = "";
        	} else {
        		this.comment = string;
        	}
            this.formatCommentDate = DateHelper.getTimestampByFormat(stamp, "d MMM yyyy HH:mm:ss z");
            this.action = action;
        }

        public String getComment() {
            try {
                return comment;
            } catch (Exception e) {
            	logContext.error(this, e);
            }
            return "";
        }

        public java.util.Date getCommentDate() {
            return commentDate;
        }

        public String getAction() {
            return action;
        }

        public String toString() {
            return commentDate + "-" + action + "-" + comment + "-" + textId + "-" + secId;
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) {
            if ( o != null && o instanceof CommentInfo )
            {
                long temp = Long.parseLong(textId);
                long temp2 = Long.parseLong(((CommentInfo)o).getTextId());
                long val = temp - temp2;
                if ( val > 0 )
                {
                    return 1;
                }
                else if ( val < 0 )
                {
                    return -1;
                }
                return 0;
            }
            return 1;
        }
        /**
         * @return Returns the formateCommentDate.
         */
        public String getFormatCommentDate() {
            return formatCommentDate;
        }
        /**
         * @param formateCommentDate The formateCommentDate to set.
         */
       
    }

    public static class SpecialBidQuestion implements Serializable{
     
		private static final long serialVersionUID = -5085322805047215983L;

		public String questionText;

        public int configNum;

        public int answer;
        
        public String questionInfo;

        /**
         * @param configNum
         * @param answer
         */
        public SpecialBidQuestion(String configNum, String answer) {
            try {
            	if (configNum!=null && answer !=null){
            		this.configNum = Integer.valueOf(configNum).intValue();
                    this.answer = Integer.valueOf(answer).intValue();
            	} else {
            		this.configNum = 0;
            		this.answer = 0;
            	}
                
            } catch (Exception e) {
            	logContext.error(this, e);
            }
        }

        public SpecialBidQuestion() {
        }

        public int getAnswer() {
            return answer;
        }

        public int getConfigNum() {
            return configNum;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String getQuestionInfo() {
			return questionInfo;
		}

		public void setQuestionInfo(String questionInfo) {
			this.questionInfo = questionInfo;
		}

		public String toString() {
            return configNum + "-" + questionText + "-" + answer;
        }
    }

	public boolean isResellerAuthorizedToPortfolio();

	public void setResellerAuthorizedToPortfolio(boolean isResellerAuthorizedToPortfolio);
	
	public void setExtendedAllowedYears(boolean extendedAllowedYears);
	public boolean isExtendedAllowedYears();
	public boolean isPartLessOneYear();
	public void setPartLessOneYear(boolean partLessOneYear);
	public void setCompressedCover(boolean compressedCover);
	public boolean isCompressedCover();
    public void setProvisngDaysChanged(boolean provisngDaysChangedFlag);
    public boolean isProvisngDaysChanged();
    public void setGridFlag(boolean isGridDelegationFlag) throws TopazException;
    public boolean isGridFlag();
    public void setChannelOverrideDiscountReasonCode(String channelOverrideDiscountReasonCode) throws TopazException;
    public String getChannelOverrideDiscountReasonCode();
    public boolean isTrmGrt60Mon();
    public void setTrmGrt60Mon(boolean isTrmGrt60Mon);
}
