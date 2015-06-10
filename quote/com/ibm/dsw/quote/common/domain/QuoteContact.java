package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteContact<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 14, 2007
 */

public interface QuoteContact {

    public String getCntEmailAdr();

    public String getCntFaxNumFull();

    public String getCntFirstName();

    public String getCntLastName();

    public String getCntPhoneNumFull();

    public String getWebQuoteNum();
    
    public String getCntPrtnrFuncCode();

    public void setCntEmailAdr(String cntEmailAdr) throws TopazException;

    public void setCntFaxNumFull(String cntFaxNumFull) throws TopazException;

    public void setCntFirstName(String cntFirstName) throws TopazException;

    public void setCntLastName(String cntLastName) throws TopazException;

    public void setCntPhoneNumFull(String cntPhoneNumFull) throws TopazException;

    public void setWebQuoteNum(String webQuoteNum) throws TopazException;
    
    public void setCntPrtnrFuncCode(String cntPrtnrFuncCode) throws TopazException;
}
