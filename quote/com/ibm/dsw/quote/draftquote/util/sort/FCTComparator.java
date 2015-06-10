package com.ibm.dsw.quote.draftquote.util.sort;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). <p/>
 * 
 * The <code>FCTComparator</code> is a implementation for FCT customer
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 20, 2007
 */

class FCTComparator extends QuoteBaseComparator {

    FCTComparator() {

    }

    public int compare(Object o1, Object o2) {
        QuoteLineItem item1 = (QuoteLineItem) o1;
        QuoteLineItem item2 = (QuoteLineItem) o2;
        
        int result = compareManualSortOrder(item1, item2);
        if (result != 0) {
            return result;
        }
        
        result = compareApplianceAttribute(item1, item2);
        if (result != 0) {
            return result;
        }
        
        result = compareString(item1.getWwideProdCodeDesc(), item2.getWwideProdCodeDesc());
        if (result != 0) {
            return result;
        }
        int order1 = getPPTCOrderForFCT(StringUtils.trim(item1.getProdPackTypeCode()));
        int order2 = getPPTCOrderForFCT(StringUtils.trim(item2.getProdPackTypeCode()));
        result = order1 - order2;        
        if (result != 0) {
            return result;
        }
        result = compareString(item1.getPartDesc(), item2.getPartDesc());
        if (result != 0) {
            return result;
        }

        /*
         * this is for sales quotes with RQ parts, not for RQs
         */
        if (isRenewalQuote(item1) && isRenewalQuote(item2)) {
            result = compareString(item1.getRenewalQuoteNum(), item2.getRenewalQuoteNum());
            if (0 != result) {
                return result;
            }
            result = compareDate(item1.getRenewalQuoteEndDate(), item2.getRenewalQuoteEndDate());
            if (0 != result) {
                return result;
            }
        }
        
        return 0;
    }

}
