package com.ibm.dsw.quote.draftquote.util.sort;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). <p/>
 * 
 * The <code>RQFCTComparator.java</code>
 * 
 * @author zhangdy <p/>
 * 
 * 
 * Creation date: Apr 15, 2008
 */
class RQFCTComparator extends QuoteBaseComparator {

    /**
     * 
     */
    public RQFCTComparator() {
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {

        QuoteLineItem item1 = (QuoteLineItem) o1;
        QuoteLineItem item2 = (QuoteLineItem) o2;
        
        /*
         *  note , for renewal quote, we don't allow user to manually adjust oder 
         *  so the comparion of manual order is unnecessary
         * int result = compareManualSortOrder(item1, item2);
        if (result != 0) {
            return result;
        }*/
        int result = compareString(item1.getWwideProdCodeDesc(), item2.getWwideProdCodeDesc());
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
        return 0;

    }

}
