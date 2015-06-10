package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteExpireDays<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 21, 2007
 */

public class QuoteExpireDays {
    String sLOB;

    String sGeo;

    boolean bSpBidFlag;

    int iQuoteMaxExpireDays;

    public QuoteExpireDays(String lob, String geo, boolean specialBidFlag, int expireDays) {
        sLOB = lob;
        sGeo = geo;
        bSpBidFlag = specialBidFlag;
        iQuoteMaxExpireDays = expireDays;

    }

    public boolean isBSpBidFlag() {
        return bSpBidFlag;
    }

    public void setBSpBidFlag(boolean spBidFlag) {
        bSpBidFlag = spBidFlag;
    }

    public int getIQuoteMaxExpireDays() {
        return iQuoteMaxExpireDays;
    }

    public void setIQuoteMaxExpireDays(int quoteMaxExpireDays) {
        iQuoteMaxExpireDays = quoteMaxExpireDays;
    }

    public String getSGeo() {
        return sGeo;
    }

    public void setSGeo(String geo) {
        sGeo = geo;
    }

    public String getSLOB() {
        return sLOB;
    }

    public void setSLOB(String slob) {
        sLOB = slob;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("sLOB = ").append(sLOB).append("; ");
        buffer.append("sGeo = ").append(sGeo).append("; ");
        buffer.append("bSpBidFlag = ").append(bSpBidFlag).append("; ");
        buffer.append("iQuoteMaxExpireDays = ").append(iQuoteMaxExpireDays).append("; ");
        return buffer.toString();
    }
}
