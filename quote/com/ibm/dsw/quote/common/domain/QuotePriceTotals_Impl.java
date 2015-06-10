
package com.ibm.dsw.quote.common.domain;
/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuotePriceTotals_Impl<code> class is abstract implementation
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 11, 2007
 */

public abstract class QuotePriceTotals_Impl implements QuotePriceTotals {
    
    public String webQuoteNum;
    
    public String priceType;
    
    public String priceSumLevelCode;
    
    public String currencyCode;
    
    public String distChannelCode;
    
    public double extAmt;
    
    public String revnStrmCategoryCode;
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#getPriceType()
     */
    public String getPriceType() {
        
        return this.priceType;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#getPriceSumLevelCode()
     */
    public String getPriceSumLevelCode() {
        
        return this.priceSumLevelCode;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#getCurrencyCode()
     */
    public String getCurrencyCode() {
        
        return this.currencyCode;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#getDistChannelCode()
     */
    public String getDistChannelCode() {
        
        return this.distChannelCode;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuotePriceTotals#getExtAmount()
     */
    public double getExtAmount() {       
        return this.extAmt;
    }   
    
    public String getRevnStrmCategoryCode(){
        return this.revnStrmCategoryCode;
    }
   
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Dist Channel Code:"+this.distChannelCode).append("\n");
        buffer.append("Price Type       :"+this.priceType).append("\n");
        buffer.append("Price Sum Level  :"+this.priceSumLevelCode).append("\n");
        buffer.append("CurrencyCode     :"+this.currencyCode).append("\n");
        buffer.append("Revn strm category code :" + this.revnStrmCategoryCode).append("\n");
        buffer.append("Amount     :"+this.extAmt).append("\n");
        return buffer.toString();
    }

}
