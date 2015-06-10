package com.ibm.dsw.quote.draftquote.util.sort;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

public class SaaSPartComparator extends QuoteBaseComparator {

	SaaSPartComparator() {

    }

    public int compare(Object o1, Object o2) {

        QuoteLineItem item1 = (QuoteLineItem) o1;
        QuoteLineItem item2 = (QuoteLineItem) o2;
        
        
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
        
        result = compareString(getSaasPartTypeStrValue(item1),getSaasPartTypeStrValue(item2));
        if (result != 0) {
            return result;
        }

        result = compareString(item1.getRevnStrmCodeDesc(), item2.getRevnStrmCodeDesc());
        if (result != 0) {
            return result;
        }

        return 0;

    }
    
    protected int compareReplaceFlag(QuoteLineItem item1, QuoteLineItem item2) {
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
    
    protected String getSaasPartTypeStrValue(QuoteLineItem item) {
        if(item.isSaasSubscrptnPart()){
        	return "1";
        }
        
        if(item.isSaasDaily()){
        	return "2";
        }
        
        if(item.isSaasSubscrptnOvragePart()){
        	return "3";
        }
        
        if(item.isSaasSetUpPart()){
        	return "4";
        }
        
        if(item.isSaasSetUpOvragePart()){
        	return "5";
        }
        
        if(item.isSaasOnDemand()){
        	return "6";
        }
        
        if(item.isSaasProdHumanServicesPart()){
        	return "7";
        }
        
        return "8";
    }

}
