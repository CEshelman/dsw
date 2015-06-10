package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteLineItemConfig_Impl</code> class is abstract implementation
 * of QuoteLineItemConfig.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-8
 */
public abstract class QuoteLineItemConfig_Impl implements QuoteLineItemConfig {
    public String webQuoteNum;

    public int iQuoteLineItemSecNum;

    public String sProcrCode;

    public int iProcrTypeQty;

    public int iUnitDVU;

    public int iExtndDVU;

    public String sProcrVendCode;

    public String sProcrVendCodeDesc;

    public String sProcrBrandCode;

    public String sProcrBrandCodeDesc;

    public String sProcrTypeCode;

    public String sProcrTypeCodeDesc;

    public double dCoreValueUnit;

    public double getCoreValueUnit() {
        return dCoreValueUnit;
    }

    public String getProcrBrandCode() {
        return sProcrBrandCode;
    }

    public String getProcrBrandCodeDesc() {
        return sProcrBrandCodeDesc;
    }

    public String getProcrTypeCode() {
        return sProcrTypeCode;
    }

    public String getProcrTypeCodeDesc() {
        return sProcrTypeCodeDesc;
    }

    public String getProcrVendCode() {
        return sProcrVendCode;
    }

    public String getProcrVendCodeDesc() {
        return sProcrVendCodeDesc;
    }

    /**
     * @return Returns the iExtndDVU.
     */
    public int getExtndDVU() {
        return iExtndDVU;
    }

    /**
     * @return Returns the iProcrTypeQty.
     */
    public int getProcrTypeQty() {
        return iProcrTypeQty;
    }

    /**
     * @return Returns the iQuoteLineItemSecNum.
     */
    public int getQuoteLineItemSecNum() {
        return iQuoteLineItemSecNum;
    }

    /**
     * @return Returns the iUnitDVU.
     */
    public int getUnitDVU() {
        return iUnitDVU;
    }

    /**
     * @return Returns the procrCode.
     */
    public String getProcrCode() {
        return sProcrCode;
    }

    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("webQuoteNum=" + webQuoteNum + "\n");
        buffer.append("iQuoteLineItemSecNum=" + iQuoteLineItemSecNum + "\n");
        buffer.append("procrCode=" + sProcrCode + "\n");
        buffer.append("iProcrTypeQty=" + iProcrTypeQty + "\n");
        buffer.append("iUnitDVU=" + iUnitDVU + "\n");
        buffer.append("iExtndDVU=" + iExtndDVU + "\n");
        buffer.append("sProcrVendCode=" + sProcrVendCode + "\n");
        buffer.append("sProcrVendCodeDesc=" + sProcrVendCodeDesc + "\n");
        buffer.append("sProcrBrandCode=" + sProcrBrandCode + "\n");
        buffer.append("sProcrBrandCodeDesc=" + sProcrBrandCodeDesc + "\n");
        buffer.append("sProcrTypeCode=" + sProcrTypeCode + "\n");
        buffer.append("sProcrTypeCodeDesc=" + sProcrTypeCodeDesc + "\n");
        buffer.append("dCoreValueUnit=" + dCoreValueUnit + "\n");
        return buffer.toString();
    }
}
