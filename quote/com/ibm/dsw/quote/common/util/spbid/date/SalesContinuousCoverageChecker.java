
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
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>ConsecutiveMaintCoverageChecker</code>
 *
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Mar 12, 2008
 */
public class SalesContinuousCoverageChecker {

    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    Quote quote;

    List swSubGroups;

    List qualifiedLineItems = new ArrayList();

    public SalesContinuousCoverageChecker(Quote quote) {
        this.quote = quote;
        this.fillQualifiedLineItems();
        swSubGroups = this.categorizeLineItems();

    }

    private void fillQualifiedLineItems() {

        for (int i = 0; i < quote.getLineItemList().size(); i++) {
            QuoteLineItem item = (QuoteLineItem) quote.getLineItemList().get(i);

            if( (item.getPartQty()!=null) && (item.getPartQty().intValue()==0) ){
                continue;
            }
            PartDisplayAttr attr = item.getPartDispAttr();
            if (attr.isLicense() || (attr.isMaint() && !CommonServiceUtil.isSalesQuoteOtherRnwlPart(quote.getQuoteHeader(), item))) {
                if ((item.getMaintStartDate() != null) && (item.getMaintEndDate() != null)) {
                    qualifiedLineItems.add(item);
                }
            }
        }
    }
    public boolean checkSpecialBid(){
        return this.getMaxContinousCoverage()>36;
    }
    public int getMaxContinousCoverage() {
        int maxMonths = 0;

        for (int i = 0; i < this.swSubGroups.size(); i++) {

            SwSubscriptionGroup group = (SwSubscriptionGroup) this.swSubGroups.get(i);
            logContext.debug(this,group.toString());
            /*logContext.info(this, "Group " + (i + 1) + ": Subscription id=[" + group.getSubscriptionId()
                    + "],total qty=[" + group.getLicensePartQuantity() + "]");*/
            Iterator iter = group.getMaintGroupMap().values().iterator();

            while (iter.hasNext()) {

                MaintPartGroup g = (MaintPartGroup) iter.next();


                SalesContinuousCoverageRule rule = new SalesContinuousCoverageRule(g);

                if (group.hasLicenseLineItem()) {

                    QuoteLineItem licenseLineItem = group.getFirstLicenseLineItem();
                    int licensePartQty = group.getLicensePartQuantity();
                    if (g.getQuantity() == licensePartQty) {
                        //now the subscription id match, quantity match, calcuate the possible related maintenance
                        rule.setLicenseLineItem(licenseLineItem);
                    }
                }


                int months = rule.countMaxTotalMonths();

                if (months > maxMonths) {
                    maxMonths = months;
                }

            }

        }
        return maxMonths;
    }


    List categorizeLineItems() {
        List result = new ArrayList();

        Map map = this.groupBySwSubID();

        Iterator iter = map.keySet().iterator();

        while (iter.hasNext()) {
            String id = (String) iter.next();
            List lineItems = (List) map.get(id);
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
            logContext.debug(this,lineItem.getPartNum() + "_" + lineItem.getSeqNum() + ": subscription id is :"+swSubID);
            if(swSubID == null){
                logContext.info(this,lineItem.getPartNum() + "_" + lineItem.getSeqNum() + ": subscription id is null");
                continue;
            }
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
