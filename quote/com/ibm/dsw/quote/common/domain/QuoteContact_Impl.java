package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteContact_Impl<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 14, 2007
 */

public abstract class QuoteContact_Impl implements QuoteContact {
    
    public String creatorId;
    
    public String webQuoteNum;
    
    public String cntPrtnrFuncCode;
    
    public String cntFirstName;
    
    public String CntLastName;
    
    public String cntPhoneNumFull;
    
    public String cntFaxNumFull;
    
    public String cntEmailAdr;
    
    

    /**
     * @return Returns the cntEmailAdr.
     */
    public String getCntEmailAdr() {
        return cntEmailAdr;
    }
    
    /**
     * @return Returns the cntPrtnrFuncCode.
     */
    public String getCntPrtnrFuncCode() {
        return cntPrtnrFuncCode;
    }
    /**
     * @return Returns the cntFaxNumFull.
     */
    public String getCntFaxNumFull() {
        return cntFaxNumFull;
    }
    /**
     * @return Returns the cntFirstName.
     */
    public String getCntFirstName() {
        return cntFirstName;
    }
    /**
     * @return Returns the cntLastName.
     */
    public String getCntLastName() {
        return CntLastName;
    }
    /**
     * @return Returns the cntPhoneNumFull.
     */
    public String getCntPhoneNumFull() {
        return cntPhoneNumFull;
    }
    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    /**
     * @return Returns the creatorId.
     */
    public String getCreatorId() {
        return creatorId;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("creatorId = ").append(creatorId).append("\n");
        buffer.append("webQuoteNum = ").append(webQuoteNum).append("\n");
        buffer.append("cntPrtnrFuncCode = ").append(cntPrtnrFuncCode).append("\n");
        buffer.append("cntFirstName = ").append(cntFirstName).append("\n");
        buffer.append("CntLastName = ").append(CntLastName).append("\n");
        buffer.append("cntPhoneNumFull = ").append(cntPhoneNumFull).append("\n");
        buffer.append("cntFaxNumFull = ").append(cntFaxNumFull).append("\n");
        buffer.append("cntEmailAdr = ").append(cntEmailAdr).append("\n");
        return buffer.toString();
    }
}
