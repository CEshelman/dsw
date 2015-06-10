
package com.ibm.dsw.quote.common.util.spbid.date;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

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
 * The <code>LineItemTreeBuilder</code>  
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 11, 2008
 */
public class LineItemTreeBuilder {
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    List lineItems = new ArrayList();
    QuoteLineItem startLineItem;
    
    boolean fullCoverageAsChildNode = false;
    //boolean qtyMustMatch = false;
    public LineItemTreeBuilder(QuoteLineItem startLineItem,List lineItems){
        this.lineItems.addAll(lineItems);
        this.startLineItem = startLineItem;
        
        SpecialBidUtil.sortLineItemByStartDate(this.lineItems);
        
    }
    
    public LineItemTreeBuilder(List lineItems){
        this.lineItems.addAll(lineItems);
   
         SpecialBidUtil.sortLineItemByStartDate(this.lineItems);
        
        this.startLineItem = (QuoteLineItem) this.lineItems.get(0);
    }
    public void setFullCoverageAsChildNode(){
        this.fullCoverageAsChildNode = true;
    }
    /*public void setQuantityMustMatch(){
        this.qtyMustMatch = true;
    }*/
    public LineItemTreeNode build(){
        long t1 = System.currentTimeMillis();
        Stack stack = new Stack();
        
        List checkedItems = new ArrayList();
        
        
        LineItemTreeNode root = new LineItemTreeNode();
        root.parent = null;
        root.lineItem = startLineItem;
        
        checkedItems.add(startLineItem);
        
        stack.push(root);           
        
        LineItemTreeNode node = (LineItemTreeNode)stack.pop();
        long total = System.currentTimeMillis();
        while(node != null){
            
            List nextContinuousItems = this.findOverlapOrContinusLineItem(node.lineItem,checkedItems);            
            
            for (int i=0;i<nextContinuousItems.size();i++){
                
                // add to the checked list
                checkedItems.add(nextContinuousItems.get(i));
                
                // create a child node and setup the relationship                    
                LineItemTreeNode childNode = new LineItemTreeNode();                
                childNode.parent = node;
                childNode.lineItem = (QuoteLineItem)nextContinuousItems.get(i);                    
                node.children.add(childNode);
                
                // push the child node to the top of the stack
                stack.push(childNode);
            }                
            if(stack.size()==0){
                break;
            }
            node = (LineItemTreeNode)stack.pop();                
        }
        
        // now the full tree is constructed
        long t2 = System.currentTimeMillis();
        logContext.debug(this,"total time of building the tree:"+(t2-t1));
        return root;
        
    }
    public List findOverlapOrContinusLineItem(QuoteLineItem item,List checkedItems){
        List result = new ArrayList();
        Iterator iter = this.lineItems.iterator();
        
        while(iter.hasNext()){
            
            QuoteLineItem currentItem = (QuoteLineItem)iter.next();
            if(currentItem.equals(item)){
                continue;
            }
            if(checkedItems.contains(currentItem)){
                continue;
            }
            
            Date endOfMonth = DateUtil.moveToLastDayofMonth(item.getMaintEndDate());
            if(currentItem.getMaintStartDate().after(DateUtil.plusOneDay(endOfMonth))){
                break;
            }
            
            if(isContinous(item,currentItem) || this.isOverlap(item,currentItem) ){
                result.add(currentItem);
            }
            if(this.fullCoverageAsChildNode){
                if(this.isContain(item,currentItem)){
                    result.add(currentItem);
                }
            }
        }
        
        return result;
     }
    /*boolean isQuantityMatch(QuoteLineItem item1,QuoteLineItem item2){
        int qty1 = -1;
        int qty2 = -1;
        if(item1.getPartQty()!= null  ){
            qty1 = item1.getPartQty().intValue();
        }
        if(item2.getPartQty() != null){
            qty2 = item2.getPartQty().intValue();
        }
        return qty1 == qty2;
    }*/
    boolean isContinous(QuoteLineItem item1,QuoteLineItem item2){
        
        if(DateUtil.isYMDEqual(item2.getMaintStartDate(),DateUtil.plusOneDay(item1.getMaintEndDate()))){
            return true;
        }
        
        
        Date endOfMonth = DateUtil.moveToLastDayofMonth(item1.getMaintEndDate());
        
        if(DateUtil.isYMDEqual(item2.getMaintStartDate(),DateUtil.plusOneDay(endOfMonth)) ){
            return true;
        }
        
        
        return false;
    }
    boolean isContain(QuoteLineItem item1, QuoteLineItem item2){
        return item2.getMaintStartDate().after(item1.getMaintStartDate()) && 
        	item2.getMaintEndDate().before(item1.getMaintEndDate());
    }
    
    boolean isOverlap(QuoteLineItem item1, QuoteLineItem item2){
        
        /*if(item1.getMaintStartDate().before(item2.getMaintEndDate())
                && item1.getMaintEndDate().after(item2.getMaintEndDate())){
            return true;
        }  */
        // firstly move to the last day of month
        Date endOfMonth = DateUtil.moveToLastDayofMonth(item1.getMaintEndDate());
        
        if( item2.getMaintStartDate().before(endOfMonth)
                && item2.getMaintEndDate().after(endOfMonth)){
            return true;
        }  
        
        return false;
    }
}
