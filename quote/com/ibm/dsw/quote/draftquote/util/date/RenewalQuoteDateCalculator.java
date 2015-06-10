
package com.ibm.dsw.quote.draftquote.util.date;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 18, 2007
 */

public class RenewalQuoteDateCalculator extends DateCalculator{
    
    List validLineItems = new ArrayList();
    
    RenewalQuoteDateCalculator(Quote quote){
        
        super(quote);
        
        List lineItems = this.quote.getLineItemList();
        for(int i=0;i<lineItems.size();i++){
            
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            // the part is manually added by part search
            if(item.getSeqNum() >= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ){
                
                validLineItems.add(item);
                this.isQualifiedForDateCalculation = true;
                
            }
        }
    }
    
    
    public void calculateDate(){
        
        if(!isQualifiedForDateCalculation){
            return;
        }
        
        //When parts are added manually to renewal quotes (through the part selection screens), set the default dates as follows:
        //Start date:  the renewal quote’s renewal end date.
        //End date:
        // For PA:  Date of the next anniversary (from the contract record) minus 1 day (3/31/08 for a 04/01/07 anniversary)
        // For PAE:  1 year from start date, minus one day.  Follow program rules:  for non FTL parts, the end date must be the end of the month

        for(int i=0;i<this.validLineItems.size();i++){
            
            QuoteLineItem item = (QuoteLineItem)validLineItems.get(i);
            QuoteHeader header = this.quote.getQuoteHeader();
            
            Date startDate = null;
            if(header.getRenwlEndDate() == null){
                startDate = DateUtil.getCurrentDate();
                logContext.info(this,"Renewal date is null for quote: "+ header.getWebQuoteNum());
            }
            else{
                startDate = new Date(header.getRenwlEndDate().getTime());
            }
            
            
            Date endDate = null;
            if(quote.getQuoteHeader().isPAEQuote()
                    || quote.getQuoteHeader().isFCTQuote() 
                    || quote.getQuoteHeader().isOEMQuote()){
                endDate = DateUtil.plusOneYearMinusOneDay(startDate);
                if(!item.getPartDispAttr().isFtlPart()){
                    endDate = DateUtil.moveToLastDayofMonth(endDate);
                }
            }
            else if(quote.getQuoteHeader().isPAQuote()){
                
                Contract contract = (Contract) quote.getCustomer().getContractList().get(0);
                
                Date anniversary = contract.getAnniversaryDate() == null ? null : new Date(contract.getAnniversaryDate()
                        .getTime());
                
                logContext.debug(this,"Calculate RQ date , The anniversary is "+anniversary);
                if(null == anniversary){
                    logContext.info(this,"Anniversary is null for quote : " + this.quote.getQuoteHeader().getWebQuoteNum());
                    
                }
                endDate = DateUtil.getNextAnniversary(startDate,anniversary);
                endDate = DateUtil.minusOneDay(endDate);
            }
            logContext.debug(this,"Calculate RQ date for line item ("+item.getPartNum()+","+item.getSeqNum()+"), startDate="+startDate+",endDate="+endDate);
            
            item.getPartDispAttr().fillMaintDate(startDate,endDate);
            item.getPartDispAttr().fillMaintDate(startDate,endDate,endDate);
            
        }
        
    }
    
    public  void setLineItemDates() throws TopazException{
        
        if(!isQualifiedForDateCalculation){
            return;
        }
        
        for(int i=0;i<this.validLineItems.size(); i++){
            
            QuoteLineItem item = (QuoteLineItem)validLineItems.get(i);
            PartDisplayAttr attr = item.getPartDispAttr();
            if (!item.getStartDtOvrrdFlg()) {
                item.setMaintStartDate(attr.getMaintStartDate());
            }
            if (!item.getEndDtOvrrdFlg()) {
                item.setMaintEndDate(attr.getMaintEndDate());
            }
            
        }
        
    }
    
    /*public List getDateResult() {
    	List dateResults = new ArrayList();
    	List itemList = this.quote.getLineItemList();
    	for(int i=0; i<itemList.size();i++){
    		QuoteLineItem item = (QuoteLineItem)itemList.get(i);
    		if(item.getSeqNum()>= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ){
    			PartDisplayAttr attr = item.getPartDispAttr();
    			DateResult dr = this.createDateResult(item,attr.getMaintStartDate(),attr.getMaintEndDate());
                dateResults.add(dr);
    		} else {
    			DateResult dr = this.createDateResult(item,item.getOrigStDate(),item.getOrigEndDate());
                dateResults.add(dr);
    		}
    	}
        return dateResults;
    }*/
    

}
