package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteRightColumn<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 5, 2007
 */

public interface QuoteRightColumn {
    
    public String getCreatorId();
    
    public String getSQuoteTypeCode();
 
    public int getINumOfParts();
      
    public String getSCustName();
     
    public String getSWebQuoteNum();
    
    public int getQtCopyType();
    
}
