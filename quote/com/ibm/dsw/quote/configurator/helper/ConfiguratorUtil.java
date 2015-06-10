package com.ibm.dsw.quote.configurator.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;

public class ConfiguratorUtil {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sortConfiguratorParts(List<ConfiguratorPart> ConfiguratorPartList, final List<QuoteLineItem> saaSLineItemList){
		Collections.sort(ConfiguratorPartList, new Comparator(){

			public int compare(Object object1, Object object2) {
				ConfiguratorPart part1 = (ConfiguratorPart) object1;
				ConfiguratorPart part2 = (ConfiguratorPart) object2;
				if(saaSLineItemList == null || saaSLineItemList.size() == 0){
					return 0;
				}
				boolean hasSamePart1 = false;
				boolean hasSamePart2 = false;
				for (Iterator iterator1 = saaSLineItemList.iterator(); iterator1.hasNext();) {
					QuoteLineItem lineItem = (QuoteLineItem) iterator1.next();
					if (lineItem.isReplacedPart()) continue;
					if (lineItem.getPartNum().equals(part1.getPartNum())) {
						hasSamePart1 = true;
						break;
					}
				}
				
				for (Iterator iterator2 = saaSLineItemList.iterator(); iterator2.hasNext();) {
					QuoteLineItem lineItem = (QuoteLineItem) iterator2.next();
					if (lineItem.isReplacedPart()) continue;
					if (lineItem.getPartNum().equals(part2.getPartNum())) {
						hasSamePart2 = true;
						break;
					}
				}
				
				if(hasSamePart1 && hasSamePart2){
					return 0;
				}else if(hasSamePart1 && !hasSamePart2){
					return -1;
				}else if(!hasSamePart1 && hasSamePart2){
					return 1;
				}else if(!hasSamePart1 && !hasSamePart2){
					return 0;
				}
				
				return 0;
			}
			
		});
	}
}
