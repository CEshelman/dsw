package com.ibm.dsw.quote.common.domain;

import java.util.Date;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteTxt_Impl<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 16, 2008
 */

public abstract class QuoteTxt_Impl implements QuoteTxt {
    
    public String webQuoteNum;
    
    public int quoteTextId = -1;
    
    public String quoteTextTypeCode;
    
    public String quoteText;
    
    public String userEmail;
    
    public Date addDate;
    
    public Date modDate;
    
    public String justificationSectionId;
    
    public String firstName;
    
    public String lastName;
    
    public String addByUserName;
    
    public String modByUserName;

    /**
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }
    public QuoteTxt_Impl() {
        
    }

	public Date getAddDate() {
        return addDate;
    }
    
    public String getJustificationSectionId() {
        return justificationSectionId;
    }
    
    public Date getModDate() {
        return modDate;
    }
    
    public String getQuoteText() {
        return quoteText;
    }
    
    public int getQuoteTextId() {
        return quoteTextId;
    }
    
    public String getQuoteTextTypeCode() {
        return quoteTextTypeCode;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public String getWebQuoteNum() {
        return webQuoteNum;
    }

    public String getAddByUserName() {
		return addByUserName;
	}
    
	public String getModByUserName() {
		return modByUserName;
	}
}
