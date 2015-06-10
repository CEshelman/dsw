package com.ibm.dsw.quote.draftquote.util.sort;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;

public class MonthlySwPartComparator extends QuoteBaseComparator {

	MonthlySwPartComparator() {

    }

    public int compare(Object o1, Object o2) {

        MonthlySwLineItem item1 = (MonthlySwLineItem) o1;
        MonthlySwLineItem item2 = (MonthlySwLineItem) o2;
        
        
        int result = compareReplaceFlag(item1, item2);
        if (result != 0) {
            return result;
        }
        
        result = compareManualSortOrder(item1, item2);
        if (result != 0) {
            return result;
        }
        
        result = compareString(item1.getSwSubId(),item2.getSwSubId());
        if (result != 0) {
        	return result;
        }
        
        result = compareString(getMonthlySwPartTypeStrValue(item1),getMonthlySwPartTypeStrValue(item2));
        if (result != 0) {
            return result;
        }

        result = compareString(item1.getRevnStrmCodeDesc(), item2.getRevnStrmCodeDesc());
        if (result != 0) {
            return result;
        }

        return 0;

    }
    
    protected int compareReplaceFlag(MonthlySwLineItem item1, MonthlySwLineItem item2) {
        if(item1.isReplacedPart() && item2.isReplacedPart()){
        	return 0;
        }
        if(!item1.isReplacedPart() && !item2.isReplacedPart()){
        	return 0;
        }
        if(!item1.isReplacedPart() && item2.isReplacedPart()){
        	return -1;
        }
        if(item1.isReplacedPart() && !item2.isReplacedPart()){
        	return 1;
        }
        return 0;
    }
    
    protected String getMonthlySwPartTypeStrValue(MonthlySwLineItem item) {
        if(item.isMonthlySwSubscrptnPart()){
        	return "1";
        }
        
        if(item.isMonthlySwDailyPart()){
        	return "2";
        }
        
        if(item.isMonthlySwSubscrptnOvragePart()){
        	return "3";
        }
        
        if(item.isMonthlySwOnDemandPart()){
        	return "4";
        }
        
        return "5";
    }

}
