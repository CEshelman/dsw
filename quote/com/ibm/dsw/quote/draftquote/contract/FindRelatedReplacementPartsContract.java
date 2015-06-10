package com.ibm.dsw.quote.draftquote.contract;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AddLineItemContract</code> class.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 6, 2007
 */
public class FindRelatedReplacementPartsContract extends PostPartPriceTabContract {
    private String partNumber;
    private String lob;
    private String retrievalType;
    private String audience;
    private String country;
    private String currency;
    private boolean addSuccess;

    public String getPartNumber() {
        return partNumber;
    }
    /**
     * @param partNum The partNum to set.
     */
    public void setPartNumber(String partNum) {
        this.partNumber = partNum;
    }
    
    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }
    /**
     * @param lob The lob to set.
     */
    public void setLob(String lob) {
        this.lob = lob;
    }
    
    /**
     * @return Returns the audience.
     */
    public String getAudience() {
        return audience;
    }
    /**
     * @param audience The audience to set.
     */
    public void setAudience(String audience) {
        this.audience = audience;
    }
    
    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }
    /**
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }
    /**
     * @return Returns the currency.
     */
    public String getCurrency() {
        return currency;
    }
    /**
     * @param currency The currency to set.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public boolean isAddSuccess() {
        return addSuccess;
    }
    
    /**
     * @return Returns the retrievalType.
     */
    public String getRetrievalType() {
        return retrievalType;
    }
    /**
     * @param retrievalType The retrievalType to set.
     */
    public void setRetrievalType(String retrievalType) {
        this.retrievalType = retrievalType;
    }
}
