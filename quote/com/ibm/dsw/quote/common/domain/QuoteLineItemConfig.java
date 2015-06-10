package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteLineItemConfig</code> class is Config domain for Quote line
 * item.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public interface QuoteLineItemConfig {

    public void delete() throws TopazException;

    /**
     * @return Returns the iExtndDVU.
     */
    public int getExtndDVU();

    /**
     * @return Returns the iProcrTypeQty.
     */
    public int getProcrTypeQty();

    /**
     * @return Returns the iQuoteLineItemSecNum.
     */
    public int getQuoteLineItemSecNum();

    /**
     * @return Returns the iUnitDVU.
     */
    public int getUnitDVU();

    /**
     * @return Returns the procrCode.
     */
    public String getProcrCode();

    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum();

    public double getCoreValueUnit();

    public String getProcrBrandCode();

    public String getProcrBrandCodeDesc();

    public String getProcrTypeCode();

    public String getProcrTypeCodeDesc();

    public String getProcrVendCode();

    public String getProcrVendCodeDesc();

    public void setWebQuoteNum(String quoteNum) throws TopazException;

    public void setProcrCode(String code) throws TopazException;

    public void setQuoteLineItemSecNum(int seqNum) throws TopazException;

    public void setProcrTypeQty(int qty) throws TopazException;

    public void setExtndDVU(int dvu) throws TopazException;
    
    public void setUnitDVU(int dvu) throws TopazException;
    

}