package com.ibm.dsw.quote.draftquote.util.builder;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RenewalQuoteBuilder</code> class is a Renewal quote Builder
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Apr 17, 2007
 */

class RenewalQuoteBuilder extends DraftQuoteBuilder {

    RenewalQuoteBuilder(Quote q,  String userID) {
    	super(q, userID);
    }

    public boolean needRecalculatePrice() throws TopazException{
        
        if(quote.containsWebSecondaryStatus(SubmittedQuoteConstants.RQ_SPECIAL_BID_APPROVED_STATUS)
                || quote.containsWebSecondaryStatus(SubmittedQuoteConstants.RQ_SPECIAL_BID_REQUESTED_STATUS)){        	
            return false;
        }
        
        QuoteAccess access = quote.getQuoteAccess();
        boolean needRecalculate = access.isCanEditRQ() || access.isCanEditRQSalesInfo() || access.isCanUpdateRQStatus();
        
        return needRecalculate;
    }
    public void clearCalculateFlag() throws TopazException{
        
        QuoteHeader header = quote.getQuoteHeader();
        header.setRecalcPrcFlag(0);

    }
    
    protected  void preBuild() throws TopazException {
        // do nothing
    }
    
    protected void updateEOLPartPrices() throws TopazException{
        // do nothing
    }
    private boolean isOfferPriceApplied(){
        QuoteHeader header = quote.getQuoteHeader();
        return (header.getOfferPrice()!=null) && (header.getPriceRecalcFlag()==1);
    }
   


}
