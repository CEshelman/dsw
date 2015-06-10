package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AuditHistory</code> class is to define the AuditHistory domain
 * object.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 14, 2007
 */
public interface AuditHistory {
    public String getWebQuoteNum();

    public Integer getLineItemNum();

    public String getUserEmail();

    public String getUserAction();

    public String getOldValue();

    public String getNewValue();
    
    public Integer getApprvrLvl();
}
