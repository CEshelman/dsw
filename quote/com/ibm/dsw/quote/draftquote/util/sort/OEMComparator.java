package com.ibm.dsw.quote.draftquote.util.sort;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). <p/>
 * 
 * The <code>OEMComparator</code> is a implementation for OEM part sort
 * 
 */

public class OEMComparator extends QuoteBaseComparator {

    OEMComparator() {

    }

    public int compare(Object o1, Object o2) {

        QuoteLineItem item1 = (QuoteLineItem) o1;
        QuoteLineItem item2 = (QuoteLineItem) o2;

        //with manual specific sort order
        int result = compareManualSortOrder(item1, item2);
        if (result != 0) {
            return result;
        }
        
        result = compareApplianceAttribute(item1, item2);
        if (result != 0) {
            return result;
        }
        
        //Worldwide product code description sort
        result = compareString(item1.getWwideProdCodeDesc(), item2.getWwideProdCodeDesc());
        if (result != 0) {
            return result;
        }
        result = compareCPC(item1, item2);
        if (result != 0) {
            return result;
        }

        //Product pack type code sort
        result = comparePPTC(item1, item2);
        if (result != 0) {
            return result;
        }
        //Part description sort
        result = compareString(item1.getPartDesc(), item2.getPartDesc());
        if (result != 0) {
            return result;
        }
        if (isRenewalQuote(item1) && isRenewalQuote(item2)) {
            //renewal quote number sort 
            result = compareString(item1.getRenewalQuoteNum(), item2.getRenewalQuoteNum());
            if (0 != result) {
                return result;
            }
            //end date sort if renewal quote
            result = compareDate(item1.getRenewalQuoteEndDate(), item2.getRenewalQuoteEndDate());
            if (0 != result) {
                return result;
            }
        }

        return 0;

    }

}
