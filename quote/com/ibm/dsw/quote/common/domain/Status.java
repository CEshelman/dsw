package com.ibm.dsw.quote.common.domain;

import java.util.Date;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Status</code> class is to define common status domain object for
 * quote tool.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 21, 2007
 */
public interface Status {
    public String getWebQuoteNum();

    public String getStatusCode();

    public String getStatusCodeDesc();

    public Date getModifiedDate();

    public void setStatusCode(String statusCode) throws TopazException;

    public void setStatusCodeDesc(String statusCodeDesc) throws TopazException;

    public void setWebQuoteNum(String webQuoteNum) throws TopazException;

    public void setModifiedDate(Date modifiedDate) throws TopazException;
}
