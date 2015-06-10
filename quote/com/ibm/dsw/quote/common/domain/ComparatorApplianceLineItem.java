package com.ibm.dsw.quote.common.domain;

import java.util.Comparator;

public class ComparatorApplianceLineItem implements Comparator<Object> {

	public ComparatorApplianceLineItem() {
	}

	@Override
	public int compare(Object object1, Object object2) {
		
		if((object1 instanceof ApplianceLineItem) && (object2 instanceof ApplianceLineItem))
		{
			
			ApplianceLineItem  LineItem1 = (ApplianceLineItem)object1;
			ApplianceLineItem  LineItem2 = (ApplianceLineItem)object2;
			
			return LineItem1.getQuoteSectnSeqNum().compareTo(LineItem2.getQuoteSectnSeqNum());
		}else{
			return 0;
		}
	}

	
}
