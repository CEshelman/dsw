
package com.ibm.dsw.quote.common.service.price;

import java.sql.Date;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.Quote;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jul 2, 2007
 */

public class PricingRequest {    
    
    
    
    private Quote quote;
    private String docCat = QuoteConstants.QUOTE_DOC_CAT;
    //private boolean sendOvrdUnitPrice = true;    
    private  boolean isChannel = false;
    private boolean updatePriceLevel = true;  // default value is true, only baseline pricing will set it to false
    private boolean saveToDb = true; // for further usage
    private boolean sendOvrrdExtndPrice = false; 
    private boolean external = false;
    
    // only for QRWS
    private Date prcDate;
    private String customerBandLevel = null; // if not null, should use this level to call pricing
    public PricingRequest(){
        
    }
    public PricingRequest(Quote quote,String docCat){
        this.quote = quote;
        this.docCat = docCat;
    }
    /**
     * @return Returns the docCat.
     */
    public String getDocCat() {
        return docCat;
    }
    /**
     * @param docCat The docCat to set.
     */
    public void setDocCat(String docCat) {
        this.docCat = docCat;
    }
    
    
    /**
     * @param external The external to set.
     */
    public void setExternal(boolean external) {
        this.external = external;
    }
    /**
     * @return Returns the external.
     */
    public boolean isExternal() {
        return external;
    }
    /**
     * @return Returns the isChannel.
     */
    public boolean isChannel() {
        return isChannel;
    }
    /**
     * @param isChannel The isChannel to set.
     */
    public void setChannel(boolean isChannel) {
        this.isChannel = isChannel;
    }
    /**
     * @return Returns the quote.
     */
    public Quote getQuote() {
        return quote;
    }
    /**
     * @param quote The quote to set.
     */
    public void setQuote(Quote quote) {
        this.quote = quote;
    }
    /**
     * @return Returns the saveToDb.
     */
    public boolean isSaveToDb() {
        return saveToDb;
    }
    /**
     * @param saveToDb The saveToDb to set.
     */
    public void setSaveToDb(boolean saveToDb) {
        this.saveToDb = saveToDb;
    }
    /**
     * @return Returns the sendOvrdUnitPrice.
     *//*
    public boolean isSendOvrdUnitPrice() {
        return sendOvrdUnitPrice;
    }
    *//**
     * @param sendOvrdUnitPrice The sendOvrdUnitPrice to set.
     *//*
    public void setSendOvrdUnitPrice(boolean sendOvrdUnitPrice) {
        this.sendOvrdUnitPrice = sendOvrdUnitPrice;
    }*/
    /**
     * @return Returns the prcDate.
     */
    public Date getPrcDate() {
        return prcDate;
    }
    /**
     * @param prcDate The prcDate to set.
     */
    public void setPrcDate(Date prcDate) {
        this.prcDate = prcDate;
    }
    /**
     * @return Returns the updatePriceLevel.
     */
    public boolean isUpdatePriceLevel() {
        return updatePriceLevel;
    }
    /**
     * @param updatePriceLevel The updatePriceLevel to set.
     */
    public void setUpdatePriceLevel(boolean updatePriceLevel) {
        this.updatePriceLevel = updatePriceLevel;
    }
    /**
     * @return Returns the customerBandLevel.
     */
    public String getCustomerBandLevel() {
        return customerBandLevel;
    }
    /**
     * @param customerBandLevel The customerBandLevel to set.
     */
    public void setCustomerBandLevel(String customerBandLevel) {
        this.customerBandLevel = customerBandLevel;
    }
    
    /**
     * @return Returns the sendOvrrdExtndPrice.
     */
    public boolean isSendOvrrdExtndPrice() {
        return sendOvrrdExtndPrice;
    }
    /**
     * @param sendOvrrdExtndPrice The sendOvrrdExtndPrice to set.
     */
    public void setSendOvrrdExtndPrice(boolean sendOvrrdExtndPrice) {
        this.sendOvrrdExtndPrice = sendOvrrdExtndPrice;
    }
}
