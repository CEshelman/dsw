
package com.ibm.dsw.quote.common.util.spbid.date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * 
 * 
 * @author <a href="changwei@cn.ibm.com">Will Chang </a> <br/>
 * 
 * Creation date: Mar 12, 2008
 */
public class TrmlsssrContinuousCoverageChecker {
    
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    Quote quote;

    List swSubGroups;

    List qualifiedLineItems = new ArrayList();

    public TrmlsssrContinuousCoverageChecker(Quote quote) {
        this.quote = quote;
        this.fillQualifiedLineItems();
        swSubGroups = this.categorizeLineItems();

    }

    private void fillQualifiedLineItems() {
       
        for (int i = 0; i < quote.getLineItemList().size(); i++) {
            QuoteLineItem item = (QuoteLineItem) quote.getLineItemList().get(i);
            
          
            PartDisplayAttr attr = item.getPartDispAttr();
            //for the new requirement of JKEY-7WKJLK we also need to check OS SUPT and OSNOSPT
            if (attr.isTrmlsssr_OSSupt()) {
                if ((item.getMaintStartDate() != null) && (item.getMaintEndDate() != null)) {
                    qualifiedLineItems.add(item);
                }
            }
        }
        logContext.debug(this,"qualifiedLineItems.size="+qualifiedLineItems.size());
        
    }
    public boolean checkSpecialBid(){
        return this.getMaxContinousCoverage()>36;
    }
    public int getMaxContinousCoverage() {
        int maxMonths = 0;

        for (int i = 0; i < this.swSubGroups.size(); i++) {

            SwSubscriptionGroup group = (SwSubscriptionGroup) this.swSubGroups.get(i);

            logContext.debug(this, group.toString());
            Iterator iter = group.getTrmlsssr_OSSupt_GroupMap().values().iterator();

            while (iter.hasNext()) {

                logContext.debug(this, "****Enter check longest path.****");

                MaintPartGroup g = (MaintPartGroup) iter.next();

                //get the longest path of the tree.
                logContext.debug(this, "****Line Itmes Size is:****" + g.getLineItems().size());
                if (hasSameQty(g.getLineItems())) {
                    LineItemTreeBuilder builder = new LineItemTreeBuilder(g.getLineItems());
                    //builder.to
                    List longestList = builder.build().getDeepestPathWithLineItems();

                    logContext.debug(this, "longest list is:" + longestList.size());

                    //get the max month
                    SalesContinuousCoverageRule rule = new SalesContinuousCoverageRule(g);
                    int months = rule.getContinusMonths(longestList);

                    if (months > maxMonths) {
                        maxMonths = months;
                    }
                }

            }

        }
        return maxMonths;
    }


    boolean hasSameQty(List lineItems) {
        boolean isSame = true;
        QuoteLineItem firstItem = (QuoteLineItem) lineItems.get(0);

        if (firstItem.getPartQty() == null) {

            for (int i = 1; i < lineItems.size(); i++) {
                if ((QuoteLineItem) lineItems.get(i) != null) {
                    isSame = false;
                    break;
                }
            }
        } else {
            for (int i = 1; i < lineItems.size(); i++) {
                if ((!firstItem.getPartQty().equals(((QuoteLineItem) lineItems.get(i)).getPartQty()))) {
                    isSame = false;
                    break;
                }
            }
        }

        return isSame;

    }
    
    List categorizeLineItems() {
        List result = new ArrayList();

        Map map = this.groupBySwSubID();

        Iterator iter = map.keySet().iterator();

        while (iter.hasNext()) {
            String id = (String) iter.next();
            List lineItems = (List) map.get(id);
            logContext.debug(this,"SwSubscriptionGroup.lineItems.size is:"+lineItems.size());
            SwSubscriptionGroup group = new SwSubscriptionGroup(id, lineItems,quote);
            result.add(group);
        }

        return result;
    }

    Map groupBySwSubID() {
        Map swSubIDMap = new HashMap();
        for (int i = 0; i < this.qualifiedLineItems.size(); i++) {
            QuoteLineItem lineItem = (QuoteLineItem) this.qualifiedLineItems.get(i);
            String swSubID = StringUtils.trimToNull(lineItem.getSwSubId());
            if(swSubID == null){
                logContext.debug(this,lineItem.getPartNum() + "_" + lineItem.getSeqNum() + ": subscription id is null");
                continue;
            }
            
            logContext.debug(this,lineItem.getPartNum() + "_" + lineItem.getSeqNum() + ": subscription id is"+lineItem.getSwSubId());
            List items = (List) swSubIDMap.get(swSubID);
            if (items == null) {
                items = new ArrayList();
                swSubIDMap.put(swSubID, items);
            }
            items.add(lineItem);
        }
        return swSubIDMap;
    }
}
