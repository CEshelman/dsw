package com.ibm.dsw.quote.submittedquote.util.builder;

import is.domainx.User;

import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.draftquote.util.date.DateCalculator;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 28, 2007
 */

public class SubmittedSalesQuoteBuilder extends SubmittedQuoteBuilder {

    public SubmittedSalesQuoteBuilder(Quote q,User user) {
        super(q,user);

    }

    protected void checkReCalculateFlag() throws TopazException {

        QuoteHeader header = quote.getQuoteHeader();
/*        if (header.isExpDateExtendedFlag()){
        	header.setRecalcPrcFlag(0);
        	return;
        }*/
        if (header.isPAQuote() || header.isPAEQuote() || header.isFCTQuote() || header.isOEMQuote() || header.isSSPQuote()) {
            boolean waitingForApproval = header
                    .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR);
            boolean returned = header
                    .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO);
            
            if (this.isSpecialBid()) {

                if ((waitingForApproval || returned) && quoteHasNotApproved()) {
                    header.setRecalcPrcFlag(1);
                }

            }            
            logContext.debug(this,"Check Submitted Quote ReCalculate Flag :" + header.getRecalcPrcFlag());
            logContext.debug(this,"Is Special Bid :" + this.isSpecialBid());
            logContext.debug(this,"waitingForApproval :" + waitingForApproval);
            logContext.debug(this,"returned :" + returned);
            logContext.debug(this,"quote has none Approval :"+ quoteHasNotApproved());
            
        }
    }
    protected boolean quoteHasNotApproved() {
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        return qtUserAccess == null ? false : qtUserAccess.isNoneApproval();
    }
    protected void adjustDate() throws TopazException {
    	if(!quote.isQuoteHasSoftwarePart()){
    		return;
    	}
        
        DateCalculator dc = DateCalculator.create(quote);
        if(!dc.needCalculateDate(user)){
            logContext.debug(this,"No need to adjust and split line item");
            return;
        }
        logContext.debug(this,"Need Adjust and split line item ");
        dc.calculateDate();
        
        
        Iterator iter = quote.getLineItemList().iterator();
        while(iter.hasNext()){
            QuoteLineItem item = (QuoteLineItem)iter.next();
            // user override the date in draft quote
            if (item.getStartDtOvrrdFlg() || item.getEndDtOvrrdFlg()) {
                this.dateComparator.addDateRecord(item,
                        item.getMaintStartDate(),
                        item.getMaintEndDate(),
                        item.getMaintStartDate(),
                        item.getMaintEndDate());
            }
            else{
                this.dateComparator.addDateRecord(item,
                        item.getMaintStartDate(),
                        item.getMaintEndDate(),
                        item.getPartDispAttr().getStdStartDate(),
                        item.getPartDispAttr().getStdEndDate());
            }
            
        }
        logContext.debug(this,"Date Comparator Afer system adjusted date: \n" + dateComparator.toString());
        
        if (this.isApprover(user) && quoteHasNotApproved()){
            //check if any duration is changed, then clear the offer price
            if(dateComparator.anyDurationChanged()){
                
                logContext.debug(this,"Duration has been changed, clear offer price");
                List lineItems = quote.getLineItemList();
                
                quote.getQuoteHeader().setOfferPrice(null);
                
                for (int i = 0; i < lineItems.size(); i++) {
                    QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
                    item.setOvrrdExtPrice(null);
                    item.setOfferIncldFlag(null);
                }
            }
	        
	        quote.getQuoteHeader().setRecalcPrcFlag(1);
        }
        //set system calculated date to line item date
        dc.setLineItemDates();
        
    }
    
    
    

}
