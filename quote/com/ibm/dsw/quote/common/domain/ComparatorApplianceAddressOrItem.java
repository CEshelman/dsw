package com.ibm.dsw.quote.common.domain;

import java.util.Comparator;

public class ComparatorApplianceAddressOrItem implements Comparator<Object> {

	public ComparatorApplianceAddressOrItem() {
	}

	@Override
	public int compare(Object object1, Object object2) {
		
		if((object1 instanceof ApplianceAddress) && (object2 instanceof ApplianceAddress))
		{
			ApplianceAddress  applianceAddress1 = (ApplianceAddress)object1;
			ApplianceAddress  applianceAddress2 = (ApplianceAddress)object2;
			
			int flag = applianceAddress1.getSecId().compareTo(applianceAddress2.getSecId());
			if(flag==0){
				return applianceAddress1.getQuoteLineItemSeqNum().compareTo(applianceAddress2.getQuoteLineItemSeqNum());
			}else{
				return flag;
			}
		}else{
			return 0;
		}
	}

	
}
