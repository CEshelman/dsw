package com.ibm.dsw.quote.common.domain;

import java.util.Date;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteTxt<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 16, 2008
 */

public interface QuoteTxt {
    
    public Date getAddDate();
    
    public String getJustificationSectionId();
    
    public Date getModDate();
    
    public String getQuoteText();
    
    public int getQuoteTextId();
    
    public String getQuoteTextTypeCode();
    
    public String getUserEmail();
    
    public String getWebQuoteNum();
    
    public String getFirstName();
    
    public String getLastName();
    
    public String getAddByUserName();
    
    public String getModByUserName();
    
    public void setAddDate(Date addDate) throws TopazException;
    
    public void setJustificationSectionId(String justificationSectionId) throws TopazException;
    
    public void setModDate(Date modDate) throws TopazException;
    
    public void setQuoteText(String quoteText) throws TopazException;
    
    public void setQuoteTextId(int quoteTextId) throws TopazException;
    
    public void setQuoteTextTypeCode(String quoteTextTypeCode) throws TopazException;
    
    public void setUserEmail(String userEmail) throws TopazException;
    
    public void setWebQuoteNum(String webQuoteNum) throws TopazException;
    
    public void setAddByUserName(String addByUserName) throws TopazException;
    
    public void setModByUserName(String modByUserName) throws TopazException;

}
