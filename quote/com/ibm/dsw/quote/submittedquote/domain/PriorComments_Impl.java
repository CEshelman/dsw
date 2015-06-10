package com.ibm.dsw.quote.submittedquote.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PriorComments_Impl</code> interface is to store prior comments
 * tab of a draft quote
 * 
 * @author: Fred (qinfengc@cn.ibm.com)
 * 
 * Creation date: Jul. 06, 2010
 */
public class PriorComments_Impl implements PriorComments, Serializable {
    protected transient List allComments;
    protected transient List attachments;
    protected transient List salesComments;
    protected transient List approverComments;
    protected transient List reviewerRequests;
    protected transient List reviewerResponses;
//    'AP2RWCMT', 'APRVRCMT', 'RVWRCMT', 'SBADJUST'
    static final String SBADJUST = "SBADJUST";
    static final String AP2RWCMT = "AP2RWCMT";
    static final String APRVRCMT = "APRVRCMT";
    static final String RVWRCMT = "RVWRCMT";
    
    public PriorComments_Impl(List allComments, List allAttachments)
    {
        this.allComments = allComments;
        this.attachments = allAttachments;
        initComments();
    }
    
    protected void initComments()
    {
        if ( this.allComments == null )
        {
            return;
        }
        salesComments = new ArrayList();
        approverComments = new ArrayList();
        reviewerRequests = new ArrayList();
        reviewerResponses = new ArrayList();
        
        for ( int i = 0; i < allComments.size(); i++ )
        {
            PriorComments.Comments cmt = (PriorComments.Comments)allComments.get(i);
            if ( StringUtils.isBlank(cmt.getCommentTxt()) )
            {
                continue;
            }
            String txtTypeCode = cmt.getTextTypeCode();
            if ( SBADJUST.equalsIgnoreCase(txtTypeCode) )
            {
                this.salesComments.add(cmt);
            }
            else if ( APRVRCMT.equalsIgnoreCase(txtTypeCode) )
            {
                this.approverComments.add(cmt);
            }
            else if ( AP2RWCMT.equalsIgnoreCase(txtTypeCode) )
            {
                this.reviewerRequests.add(cmt);
            }
            else if ( RVWRCMT.equalsIgnoreCase(txtTypeCode) )
            {
                this.reviewerResponses.add(cmt);
            }
        }
    }
    
    private boolean isExistsByNum(String quoteNum, List list)
    {
        for ( int i = 0; i < list.size(); i++ )
        {
            PriorComments.Comments cmt = (PriorComments.Comments)list.get(i);
            if ( StringUtils.equals(quoteNum, cmt.getWebQuoteNum()) )
            {
                return true;
            }
        }
        return false;
    }
    public List getApproverComments() {
        return approverComments;
    }
    public List getReviewerRequests() {
        return reviewerRequests;
    }
    public List getReviewerResponses() {
        return reviewerResponses;
    }
    public List getSalesComments() {
        return salesComments;
    }
    public List getAttachments() {
        return attachments;
    }
    public boolean isNoPriorComments() {
       if ( this.allComments.size() == 0 && this.attachments.size() == 0 )
       {
           return true;
       }
       return false;
    }
}
