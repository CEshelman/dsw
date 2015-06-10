
package com.ibm.dsw.quote.common.util.spbid.date;
/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MaintPartGroup</code>  
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 10, 2008
 */
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;


public class MaintPartGroup {
    String partNumber;
    int quantity;
    List lineItems = new ArrayList();
    
    public MaintPartGroup(String partNumber,int quantity){
        this.partNumber = partNumber;
        this.quantity = quantity;
    }
    public int getQuantity(){
        return this.quantity;
    }
    public void addLineItem(QuoteLineItem item){
        this.lineItems.add(item);
    }
    public List getLineItems(){
        return this.lineItems;
    }
    /**
     * @return
     */
    public String getPartNumber() {
        return this.partNumber;
    }
    public QuoteLineItem getFirstRelatedLineItem(QuoteLineItem license){
        
        List candidateRelatedLineItems = new ArrayList();
        
        Iterator iter = (Iterator)lineItems.iterator();
        
        while(iter.hasNext()){                
            QuoteLineItem item = (QuoteLineItem)iter.next();  
            
                Date diseredDate = DateUtil.plusOneDay(license.getMaintEndDate());
                if(DateUtil.isYMDEqual(diseredDate,item.getMaintStartDate())){
                    // ok, the start date is one day after the license end date
                    candidateRelatedLineItems.add(item);
                    
                }
            
        }
        if(candidateRelatedLineItems.size() == 0){
            return null;
        }
        
        // find the item with longest continus coverage by sorting them
        Collections.sort(candidateRelatedLineItems,new Comparator(){
            public int compare(Object o1, Object o2){
                
                QuoteLineItem item1 = (QuoteLineItem)o1;
                QuoteLineItem item2 = (QuoteLineItem)o2;
                
                int result = item2.getMaintEndDate().compareTo(item1.getMaintEndDate());
                if(result !=0){
                    return result;
                }
                
                return item1.getSeqNum()-item2.getSeqNum();
                
            }
        });
    
        return (QuoteLineItem)candidateRelatedLineItems.get(0);
    }   
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Maint Group: PartNumber=[").append(this.getPartNumber())
        	.append("],quantity=[").append(this.getQuantity()).append("]");
        buffer.append(",SeqNum=");
        for(int i=0;i<this.lineItems.size();i++){
            QuoteLineItem item = (QuoteLineItem)this.lineItems.get(i);
            buffer.append(item.getSeqNum()).append(",");
        }
        return buffer.toString();
    }
}
