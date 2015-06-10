package com.ibm.dsw.quote.submittedquote.util;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

public class ApplianceServiceUtil {
	public static boolean isQuoteContainsApplianceMtmInfo(List<QuoteLineItem> lineItemList){
		if(lineItemList == null || lineItemList.size() == 0){
			return false;
		}
		for (Iterator iterator = lineItemList.iterator(); iterator.hasNext();) {
			QuoteLineItem quoteLineItem = (QuoteLineItem) iterator.next();
			if(quoteLineItem.isApplncPart()){
				if(StringUtils.isNotBlank(quoteLineItem.getMachineType())
					|| StringUtils.isNotBlank(quoteLineItem.getModel())
					|| StringUtils.isNotBlank(quoteLineItem.getSerialNumber())
					){
					return true;
				}
			}
		}
		return false;
	}
}
