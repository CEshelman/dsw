package com.ibm.dsw.quote.submittedquote.viewbean.helper;

import java.io.Serializable;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 8, 2007
 */

public class BrandTotalPrice implements Serializable{
    
    public String distChannelCode; 
    
    public String prcType;
    
    public String prcSumLevelCode;   

    public String brandDesc;

    public double localCurrencyPrice;       // local price

    public double regionBasedCurrencyPrice; // USD or ERO price

    public double reportCurrencyPrice; // USD price
    
    public String customerDiscount;
    
    public String combinedDiscount;
    
    public String revnStrmCategoryCode;
    
    public String brandTotalPriceKey;
       
    public BrandTotalPrice() {      
    }

    public BrandTotalPrice(String distChannelCode, String prcType, String prcSumLevelCode, String brandDesc,String revnStrmCategoryCode) {      
        this.distChannelCode = distChannelCode;
        this.prcType = prcType;
        this.prcSumLevelCode = prcSumLevelCode;
        this.brandDesc = brandDesc;
        this.revnStrmCategoryCode = revnStrmCategoryCode;
        genBrandTotalPriceKey();
    }
    
    private void genBrandTotalPriceKey(){
        this.brandTotalPriceKey = this.distChannelCode+this.prcType+this.prcSumLevelCode+this.revnStrmCategoryCode;
    }
}