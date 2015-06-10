
package com.ibm.dsw.quote.common.util.spbid.date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SwSubscriptionGroup</code>  
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 10, 2008
 */
public class SwSubscriptionGroup {
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    String subscriptionId;
    
    Quote quote;
    
    List licenseLineItems = new ArrayList();
    
    
    HashMap maintGroupMap = new HashMap();
    
    HashMap trmlsssr_OSSupt_GroupMap = new HashMap();
    
    public SwSubscriptionGroup(String subscriptionId,List lineItems,Quote quote){
        this.quote = quote;
        this.subscriptionId =subscriptionId;
        this.fillLicenseAndMaints(lineItems);
    }
    /**
     * @return
     */
    public String getSubscriptionId() {
        
        return this.subscriptionId;
    }
    /**
     * @return
     */
    public List getLicenseLineItems() {
        
        return this.licenseLineItems;
    }
    public boolean hasLicenseLineItem(){
        return this.licenseLineItems.size()!=0;
    }
    public int getLicensePartQuantity(){
        int total = 0;
        for(int i=0;i<licenseLineItems.size(); i++){
            QuoteLineItem item = (QuoteLineItem)licenseLineItems.get(i);
            if(item.getPartQty()!=null){
                total += item.getPartQty().intValue();
            }
        }
        return total;
    }
    public QuoteLineItem getFirstLicenseLineItem(){
        if (licenseLineItems.size()>0){
            return (QuoteLineItem)licenseLineItems.get(0);
        }
        return null;
    }
    /**
     * @return Returns the maintGroups.
     */
    public Map getMaintGroupMap() {
        return this.maintGroupMap;
        
    }
    
    public Map getTrmlsssr_OSSupt_GroupMap() {
        return this.trmlsssr_OSSupt_GroupMap;
        
    }
    
    public void fillLicenseAndMaints(List lineItems){
        for(int i=0;i<lineItems.size();i++){
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            PartDisplayAttr attr = item.getPartDispAttr();
            
            if(attr.isLicenseBehavior()){
                this.licenseLineItems.add(item);
            }
            //logContext.info(this,"Part and Seq = "+ item.getPartNum() + "_" + item.getSeqNum());
            //logContext.info(this,"isMaint = "+attr.isMaint());
            //logContext.info(this,"isFromRenewal =["+item.getRenewalQuoteNum()+"]");
            
            // it's maitenacen( but not added from RQ) and the software subscription id is same with the license
            if(attr.isMaintBehavior() && (!CommonServiceUtil.isSalesQuoteOtherRnwlPart(quote.getQuoteHeader(), item))) {
                //logContext.info(this,"Subscription id = " + this.subscriptionId + "Part Number ="+item.getPartNum());
                String partNum = item.getPartNum();
                int qty = -1;
                if(item.getPartQty()!=null){
                    qty = item.getPartQty().intValue();
                }
                String key = partNum + "_" + qty;
                MaintPartGroup group = (MaintPartGroup) this.maintGroupMap.get(key);
                if(group == null){
                    group = new MaintPartGroup(partNum,qty);
                    this.maintGroupMap.put(key,group);
                }
                group.addLineItem(item);
                
            }
            
            if(attr.isTrmlsssr_OSSupt()) {
                //logContext.info(this,"Subscription id = " + this.subscriptionId + "Part Number ="+item.getPartNum());
                String partNum = item.getPartNum();
                int qty = -1;
                if(item.getPartQty()!=null){
                    qty = item.getPartQty().intValue();
                }
                String key = partNum + "_" + qty;
                MaintPartGroup group = (MaintPartGroup) this.trmlsssr_OSSupt_GroupMap.get(key);
                if(group == null){
                    group = new MaintPartGroup(partNum,qty);
                    this.trmlsssr_OSSupt_GroupMap.put(key,group);
                }
                group.addLineItem(item);
                
            }
            
            
            
        }
    }
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Software Subscription ID=["+ this.subscriptionId+"]\n");
        buffer.append("\tHas License Part="+ this.hasLicenseLineItem()+"\n");
        if(this.hasLicenseLineItem()){
            buffer.append("\tLicense Part Total Qty="+ this.getLicensePartQuantity()+"\n");
            buffer.append("\tLicense Sequence Number:" );
            for(int i=0;i<this.licenseLineItems.size();i++){
                QuoteLineItem item = (QuoteLineItem)this.licenseLineItems.get(i);
                buffer.append(item.getSeqNum()+",");
            }
            buffer.append("\n");
        }
        Iterator iter = this.maintGroupMap.values().iterator();
        while(iter.hasNext()){
            MaintPartGroup g = (MaintPartGroup)iter.next();
            buffer.append("\t" + g.getPartNumber() + "[Quantity=" + g.getQuantity()+ "]:");
            List items = g.getLineItems();
            for(int i=0;i<items.size();i++){
                QuoteLineItem item = (QuoteLineItem)items.get(i);
                buffer.append(item.getSeqNum()).append(",");
            }
            
            buffer.append("\n");
        }
        return buffer.toString();
    }
    
}
