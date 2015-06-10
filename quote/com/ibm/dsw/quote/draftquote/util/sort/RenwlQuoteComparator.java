package com.ibm.dsw.quote.draftquote.util.sort;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RenwlQuoteComparator</code> is a implementation for RenwalQuote
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Apr 16, 2007
 */

class RenwlQuoteComparator extends QuoteBaseComparator {
    
    public int compare(Object o1, Object o2) {
        QuoteLineItem item1 = (QuoteLineItem) o1;
        QuoteLineItem item2 = (QuoteLineItem) o2;
        return item1.getSeqNum() - item2.getSeqNum();

    }

}
