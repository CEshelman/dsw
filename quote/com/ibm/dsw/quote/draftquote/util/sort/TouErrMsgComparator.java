package com.ibm.dsw.quote.draftquote.util.sort;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

public class TouErrMsgComparator extends QuoteBaseComparator{

	@Override
	public int compare(Object o1, Object o2) {
        QuoteLineItem item1 = (QuoteLineItem) o1;
        QuoteLineItem item2 = (QuoteLineItem) o2;
        
		if (item1.getSeqNum() > item2.getSeqNum())
			return 1;
		if (item1.getSeqNum() < item2.getSeqNum())
			return -1;
		return 0;
	}

}
