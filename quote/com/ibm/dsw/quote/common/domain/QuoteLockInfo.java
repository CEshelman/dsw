package com.ibm.dsw.quote.common.domain;


/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteLockInfo<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: 2010-5-11
 */

public interface QuoteLockInfo extends java.io.Serializable {
    
    public boolean isLockedFlag();
    
    public String getLockedBy();
    
    public String getWebQuoteNum();
   
    public void setLockedFlag(boolean lockedFlag);
    
    public void setLockedBy(String lockedBy);
    
    public void setWebQuoteNum(String webQuoteNum);
    
   
}
