
package com.ibm.dsw.quote.submittedquote.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedPartPriceContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jun 7, 2007
 */

public class SubmittedQuoteRequest {
    
    private Quote quote;
    private SubmittedPartPriceContract ct;
    
    public SubmittedQuoteRequest(Quote quote,SubmittedPartPriceContract ct){
        this.quote = quote;
        this.ct = ct;
    }
    public boolean needUpdateLineItemDate(){
        
        return null != ct.getUpdateLineItemDateFlag();
        
    }
    
    public boolean needCalculatePriceIncrease(){
        return StringUtils.isNotBlank(ct.getApplyOfferPriceFlag());
    }
    public Quote getQuote(){
        return this.quote;
        
    }
    public SubmittedPartPriceContract getContract(){
        return this.ct;
    }
    
    public String getOfferPrice(){
        return ct.getOfferPrice();
    }
    
    public List getOriginalLineItemList(){
        return ct.getOriginalLineItemList();
    }

}
