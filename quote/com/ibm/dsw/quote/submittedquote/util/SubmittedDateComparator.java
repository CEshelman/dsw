
package com.ibm.dsw.quote.submittedquote.util;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.opal.log.LogContextFactory;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Aug 18, 2007
 */
public class SubmittedDateComparator {
    
    HashMap dateChangeMap = new HashMap();
    
    public Iterator getDateRecords(){
        return this.dateChangeMap.values().iterator();
    }
    public static class DateRecord{
        public DateRecord(QuoteLineItem item,Date prevStartDate,Date prevEndDate,Date curStartDate,Date curEndDate){
            this.item = item;
            this.prevStartDate = prevStartDate;
            this.prevEndDate = prevEndDate;
            this.curStartDate = curStartDate;
            this.curEndDate = curEndDate;
        }
        public QuoteLineItem item;
        public Date curStartDate;
        public Date curEndDate;
        public Date prevStartDate;
        public Date prevEndDate;
        public boolean isStartDateChanged(){
            return !DateUtil.isYMDEqual(prevStartDate, curStartDate);
        }
        public boolean isEndDateChanged(){
            return !DateUtil.isYMDEqual(prevEndDate, curEndDate);
        }
        public boolean isDateChanged(){
            return isStartDateChanged() || isEndDateChanged();
        }
        public boolean isDurationChanged(){
            
            
            if(item.getPartDispAttr().isFtlPart()){
                // compare weeks
                if (DateUtil.calculateWeeks(curStartDate, curEndDate) != DateUtil.calculateWeeks(prevStartDate, prevEndDate)) {
                    LogContextFactory.singleton().getLogContext().debug(this,"Weeks duration changed-> Prev("+prevStartDate +"," + prevEndDate+"), Cur("+curStartDate+","+ curEndDate+")" );
                    LogContextFactory.singleton().getLogContext().debug(this,"Prev Weeks Duration = "+ DateUtil.calculateWeeks(prevStartDate, prevEndDate));
                    LogContextFactory.singleton().getLogContext().debug(this,"Cur Weeks Duration = "+ DateUtil.calculateWeeks(curStartDate, curEndDate));
                   return true;
                }
            }
            else{
                // compare months
                if (DateUtil.calculateFullCalendarMonths(curStartDate, curEndDate) != DateUtil.calculateFullCalendarMonths(prevStartDate, prevEndDate)) {
                    LogContextFactory.singleton().getLogContext().debug(this,"Prev Months Duration = "+ DateUtil.calculateFullCalendarMonths(prevStartDate, prevEndDate));
                    LogContextFactory.singleton().getLogContext().debug(this,"Cur Months Duration = "+ DateUtil.calculateFullCalendarMonths(curStartDate, curEndDate));
                    LogContextFactory.singleton().getLogContext().debug(this,"Months duration changed-> Prev("+prevStartDate +"," + prevEndDate+"), Cur("+curStartDate+","+ curEndDate+")" );
                    return true;
                 }
            }
            return false;
        }
        
    }
    public boolean anyDurationChanged(){
        Iterator iter = this.dateChangeMap.values().iterator();
        while(iter.hasNext()){
            DateRecord dc = (DateRecord)iter.next();
            if(dc.isDurationChanged()){
                return true;
            }
                
        }
        return false;
    }
    public DateRecord addDateRecord(QuoteLineItem item,Date prevStartDate,Date prevEndDate,Date curStartDate,Date curEndDate){
        DateRecord dr = new DateRecord(item,prevStartDate,prevEndDate,curStartDate,curEndDate);
        
        addDateRecord(dr);
        
        return dr;
    }
    public void addDateRecord(DateRecord dr){
        
        this.dateChangeMap.put(dr.item,dr);
        
    }
    public boolean isDateChanged(QuoteLineItem item){
        
        DateRecord dr = (DateRecord)this.dateChangeMap.get(item);
        if(dr == null){
            return false;
        }
        return dr.isDateChanged();
        
    }
    public boolean isDurationChanged(QuoteLineItem item){
        
        DateRecord dr = (DateRecord)this.dateChangeMap.get(item);
        if(dr == null){
            return false;
        }
        return dr.isDurationChanged();
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        Iterator iter = this.getDateRecords();
        while(iter.hasNext()){
            DateRecord dr = (DateRecord) iter.next();
            buffer.append("[" + dr.item.getPartNum() + "," + dr.item.getSeqNum() + "]->" );
            buffer.append("Prev:("+dr.prevStartDate + "," + dr.prevEndDate + ") ");
            buffer.append("Current:("+dr.curStartDate + "," + dr.curEndDate + ")");
            buffer.append("\n");
        }
        return buffer.toString();
    }

}
