package com.ibm.dsw.quote.draftquote.util.sort;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). <p/>
 * 
 * The <code>PAAndPAEComparator</code> is a implementation for PPSS customer
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 20, 2007
 */

class PPSSComparator extends QuoteBaseComparator {

    PPSSComparator() {

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
        result = compareString(item1.getPartDesc(), item2.getPartDesc());
        if (result != 0) {
            return result;
        }
        return 0;
    }

}
