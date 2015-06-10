
package com.ibm.dsw.quote.common.util.spbid.date;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.common.domain.Quote;
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
 * The <code>ConsecutiveCoverageRenewalChecker</code>  
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 12, 2008
 */
public class RenewalContinuousCoverageChecker {
    
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    Quote quote;

    public RenewalContinuousCoverageChecker(Quote quote) {
        this.quote = quote;

    }
    
    
    public boolean checkSpeicalBid() {
        
        HashMap map = this.groupByPartNum();
        
        Iterator iter = map.keySet().iterator();
        
        while(iter.hasNext()){
        
            String partNum = (String)iter.next();
            
            List items = (List)map.get(partNum);
            
            if(items.size()<3){
                continue;
            }
            
            // sort by start date
            Collections.sort(items,new Comparator(){
                public int compare(Object o1, Object o2){
                   
                    QuoteLineItem item1 = (QuoteLineItem)o1;
                    QuoteLineItem item2 = (QuoteLineItem)o2;
                    
                    return item1.getMaintStartDate().compareTo(item2.getMaintStartDate());
                }                
                });
            
            
            for(int i=0;i<items.size();i++){
                int continusYears = 1;
                List checkedLineItms = new ArrayList();
                QuoteLineItem currentItem = (QuoteLineItem)items.get(i);
                checkedLineItms.add(currentItem);
                QuoteLineItem continusItem = currentItem;
                do{
	                continusItem = this.findLineItemWithSpecificStartDate(continusItem,checkedLineItms,items);
	                if(continusItem != null){
	                    continusYears ++;
	                    checkedLineItms.add(continusItem);
	                }
                }while(continusItem !=null);
                
                // ok, we find a part has more than 3 years continus maintainence
                if(continusYears >3){
                    return true;
                }
            }
            
        }
        return false;
        
    }
    private QuoteLineItem findLineItemWithSpecificStartDate(QuoteLineItem item,List checkedItems,List items){
        Date diseredStartDate =  DateUtil.plusOneDay(item.getMaintEndDate());
        for(int i=0;i<items.size();i++){
            QuoteLineItem tmp = (QuoteLineItem)items.get(i);
            
            if(checkedItems.contains(tmp)){
                continue;
            }         
            
            
            if(diseredStartDate.equals(tmp.getMaintStartDate())){
                return tmp;
            }
        }
        return null;
    }
    private HashMap groupByPartNum(){
        
        HashMap map = new HashMap();
        
        List items = quote.getLineItemList();
        
        for (int i = 0; i < items.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) items.get(i);
            if((item.getPartQty()!=null) && (item.getPartQty().intValue()==0)){
                continue;
            }
            if((item.getMaintStartDate() == null) || (item.getMaintEndDate() == null)){
                continue;
            }
            String partNum = item.getPartNum();
            List l = (List)map.get(partNum);
            if(null == l){
                l = new ArrayList();
                map.put(partNum,l);
            }
            l.add(item);
            
        }
        
        
        return map;
    }
    
}
