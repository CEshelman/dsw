
package com.ibm.dsw.quote.common.util.spbid.date;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ConsecutiveMaintCoverageRule</code> 
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 10, 2008
 */
public class SalesContinuousCoverageRule {
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    MaintPartGroup group;
    QuoteLineItem licenseItem;
    
    public SalesContinuousCoverageRule(MaintPartGroup group){
        
        this.group = group;
    }
    public void setLicenseLineItem(QuoteLineItem item){
        this.licenseItem = item;
    }
    
    public int countMaxTotalMonths(){
        logContext.debug(this, group.toString());
        if(null == licenseItem){
            int maxMonths = 0;
            List itemGroups = this.categorizeLineItemsWithoutDateGaps(group.getLineItems());
            for(int i=0;i<itemGroups.size();i++){
                List items = (List)itemGroups.get(i);
                int months = this.getContinusMonths(items);
                if(months> maxMonths){
                    maxMonths = months;
                }                   
                logContext.debug(this,"URelated Coverage(no license)=["+ this.printLineItemList(items) +"]:" +  months);
            }            
            
            return maxMonths;
        }
        else{            
            
            List continousRelatedLineItems = getLongestContinusLineItems(licenseItem);
            
            int maxMonths = this.getContinusMonths(continousRelatedLineItems);            
            logContext.debug(this,"Related Coverage=["+ this.printLineItemList(continousRelatedLineItems) +"]:" +  maxMonths);
            
            // get all unrelated line items : all line items minus related line items
            List unRelatedLineItems = new ArrayList();
            unRelatedLineItems.addAll(group.getLineItems());
            unRelatedLineItems.removeAll(continousRelatedLineItems);
            
            int unRelatedMonths = 0;
            List itemGroups = this.categorizeLineItemsWithoutDateGaps(unRelatedLineItems);
            for(int i=0;i<itemGroups.size();i++){
                List items = (List)itemGroups.get(i);
                unRelatedMonths= this.getContinusMonths(items);        
                logContext.debug(this,"UnRelated Coverage=["+ this.printLineItemList(items) +"]:" +  unRelatedMonths);
                if(unRelatedMonths> maxMonths){
                    maxMonths = unRelatedMonths;
                }
            }
            
           
            return maxMonths;
        }
        
    }
    
    
    private String printLineItemList(List items){
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<items.size();i++){
            QuoteLineItem item = (QuoteLineItem)items.get(i);
            buffer.append(item.getSeqNum()).append(",");
        }
        return buffer.toString();
    }
    
    public List getLongestContinusLineItems(QuoteLineItem startLineItem){
        
        if(startLineItem == null){
            logContext.debug(this,"the input parameter , startLineItem is null");
            return new ArrayList();
        }
        LineItemTreeBuilder builder = new LineItemTreeBuilder(startLineItem,group.getLineItems());
        return builder.build().getDeepestPathWithLineItems();
        
    }
    
    public int getContinusMonths(List items) {
    	LinkedList continusCoverage = getContinusCoverage(items);
    	if(continusCoverage.isEmpty()) return 0;
    	return DateUtil.calculateFullCalendarMonths((Date) continusCoverage.getFirst(),
    			DateUtil.moveToLastDayofMonth((Date) continusCoverage.getLast()));
    }
    
    protected LinkedList getContinusCoverage(List items) {
    	//hold start/end dates pair in months list
    	LinkedList continusCoverage = new LinkedList();
    	
    	//re-order the given line item list by start asc
    	Collections.sort(items, new Comparator() {
    		public int compare(Object obj1, Object obj2) {
    			if (obj1 instanceof QuoteLineItem && obj2 instanceof QuoteLineItem) {
    				Date date1 = ((QuoteLineItem) obj1).getMaintStartDate();
    				Date date2 = ((QuoteLineItem) obj2).getMaintStartDate();
    				return date1.compareTo(date2);
    			}
    			return 0;
    		}
    	});
    	
    	//rearrange item's start/end date pair;
    	Iterator it = items.iterator();
    	while (it.hasNext()) {
    		QuoteLineItem item = (QuoteLineItem) it.next();
    		
    		Date startDate = item.getMaintStartDate();
    		Date endDate = item.getMaintEndDate();
    		logContext.debug(this, "[ " + startDate + "  ---  " + endDate + " ]");
    		
    		if (startDate.before(endDate)) {
    			if (continusCoverage.isEmpty()) {
    				continusCoverage.addFirst(startDate);
    				continusCoverage.addLast(endDate);
    			} else {
    				Date lastEndDate = (Date) continusCoverage.getLast();
    				if(endDate.before(lastEndDate)) {
    					//skip, since duration of current item is entirely covered by previous items coverage
    				} else if(isContinusOrOverlappDate(lastEndDate, startDate)) {
    					continusCoverage.addLast(endDate);
    					
    				}
    			}
    		}
    	}
    	return continusCoverage;
    }
    
    /* update end date of items coverage if current item is partially overlapped 
	 * by the previous items.
	 * OR providing continus coverage  after another maintenance item.
	*/
    protected boolean isContinusOrOverlappDate(Date endDate, Date startDate) {
    	if(startDate.before(endDate)) return true;
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(endDate);
    	cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
    	cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DAY_OF_MONTH));
    	return cal.getTimeInMillis() >= startDate.getTime();
    }

    protected boolean isContinusOrOverlappedItem(List items, QuoteLineItem item) {
    	LinkedList continusCoverage = getContinusCoverage(items);
    	if(continusCoverage.isEmpty()) return false;
    	
    	Date continusCoverageEndDate = (Date)continusCoverage.getLast();
    	Date startDate = item.getMaintStartDate();
		Date endDate = item.getMaintEndDate();
		
    	return endDate.after(continusCoverageEndDate) && isContinusOrOverlappDate(continusCoverageEndDate, item.getMaintStartDate());
    	
    }
    /**
	 * @param items
	 * @return
	 */
    public List categorizeLineItemsWithoutDateGaps(List items) {
        
        List result = new ArrayList();
        List lineItems = new ArrayList();
        lineItems.addAll(items);
        
        SpecialBidUtil.sortLineItemByStartDate(lineItems);
        
        while(lineItems.size()>0){
            QuoteLineItem item = (QuoteLineItem)lineItems.get(0);
        
            LineItemTreeBuilder builder = new LineItemTreeBuilder(item,lineItems);
            builder.setFullCoverageAsChildNode();
            
            LineItemTreeNode root = builder.build();
            
            List checkedLineItems = root.getAllNodes();            
            result.add(checkedLineItems);
            
            lineItems.removeAll(checkedLineItems);            
            
        }
        return result;
    }
    
    

}
