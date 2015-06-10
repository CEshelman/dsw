package com.ibm.dsw.quote.submittedquote.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PriorComments</code> interface is to store prior comments
 * tab of a draft quote
 * 
 * @author: Fred (qinfengc@cn.ibm.com)
 * 
 * Creation date: Jul. 06, 2010
 */
public interface PriorComments {
    public List getAttachments();
    public List getSalesComments();
    public List getReviewerRequests();
    public List getReviewerResponses();
    public List getApproverComments();
    public boolean isNoPriorComments();
    class Comments implements Serializable
    {
        private static final long serialVersionUID = -4943952371447377940L;
		String webQuoteNum;
        String userId;
        String userName;
        String commentTxt;
        Date commentDate;
        String textTypeCode;
        
        public String getTextTypeCode() {
            return textTypeCode;
        }
        public void setTextTypeCode(String textTypeCode) {
            this.textTypeCode = textTypeCode;
        }
        public Date getCommentDate() {
            return commentDate;
        }
        public void setCommentDate(Date commentDate) {
            this.commentDate = commentDate;
        }
        public String getCommentTxt() {
            return commentTxt;
        }
        public void setCommentTxt(String commentTxt) {
            this.commentTxt = commentTxt;
        }
        public String getUserId() {
            return userId;
        }
        public void setUserId(String userId) {
            this.userId = userId;
        }
        public String getUserName() {
            return userName;
        }
        public void setUserName(String userName) {
            this.userName = userName;
        }
        public String getWebQuoteNum() {
            return webQuoteNum;
        }
        public void setWebQuoteNum(String webQuoteNum) {
            this.webQuoteNum = webQuoteNum;
        }
    }
}
