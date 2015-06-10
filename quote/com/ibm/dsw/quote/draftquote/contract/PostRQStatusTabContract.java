package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.common.domain.Quote;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PostRQStatusTabContract</code> class is to collect input
 * parameters for post draft renewal quote status.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 18, 2007
 */
public class PostRQStatusTabContract extends PostDraftQuoteBaseContract {
    
    private String quoteNum = null;

    private String primaryStatus = null;

    private String secondaryStatus = null;

    private String termComment = null;

    private String termReason = null;
    
    private Quote quote = null;

    /**
     * @return Returns the primaryStatus.
     */
    public String getPrimaryStatus() {
        return primaryStatus;
    }

    /**
     * @param status
     *            The primaryStatus to set.
     */
    public void setPrimaryStatus(String status) {
        primaryStatus = status;
    }

    /**
     * @return Returns the secondaryStatus.
     */
    public String getSecondaryStatus() {
        return secondaryStatus;
    }

    /**
     * @param status
     *            The sStatus to set.
     */
    public void setSecondaryStatus(String status) {
        secondaryStatus = status;
    }

    /**
     * @return Returns the quoteNum.
     */
    public String getQuoteNum() {
        return quoteNum;
    }

    /**
     * @param quoteNum
     *            The quoteNum to set.
     */
    public void setQuoteNum(String quoteNum) {
        this.quoteNum = quoteNum;
    }

    /**
     * @return Returns the termComment.
     */
    public String getTermComment() {
        return termComment;
    }

    /**
     * @param termComment
     *            The termComment to set.
     */
    public void setTermComment(String termComment) {
        this.termComment = termComment;
    }

    /**
     * @return Returns the termReason.
     */
    public String getTermReason() {
        return termReason;
    }

    /**
     * @param termReason
     *            The termReason to set.
     */
    public void setTermReason(String termReason) {
        this.termReason = termReason;
    }

    public Quote getQuote() {
        return quote;
    }
    
    public void setQuote(Quote quote) {
        this.quote = quote;
    }
}
