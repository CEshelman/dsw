package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PriceTotals<code> class.
 *    
 * @author: liuxinlx@cn.ibm.com
 * 
 * Creation date: Apr 25, 2007
 */

public interface QuotePriceTotals {   
    
    public String getPriceType();
    public String getPriceSumLevelCode();
    public String getCurrencyCode();
    public String getDistChannelCode();
    public double getExtAmount();
    public String getRevnStrmCategoryCode();
    
    public void setPriceType(String priceType)throws TopazException;
    public void setPriceSumLevelCode(String code)throws TopazException;
    public void setCurrencyCode(String code)throws TopazException;
    public void setDistChannelCode(String code)throws TopazException;
    public void setExtAmount(double amt)throws TopazException;
    public void setRevnStrmCategoryCode(String revnStrmCategoryCode) throws TopazException;
       
    
}
