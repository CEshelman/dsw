
package com.ibm.dsw.quote.submittedquote.util.sort;

import com.ibm.dsw.quote.common.config.PartPriceConstants.PartTypeCode;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.util.sort.QuoteBaseComparator;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 9, 2007
 */

public  class NonSaleRepPAandPAEComparator extends QuoteBaseComparator{
    
    public NonSaleRepPAandPAEComparator(){
        
    }
    public static int getOrderForPartType(String partType){
        if(PartTypeCode.PACTRCT.equals(partType)){
            return 1;
        }else if (PartTypeCode.SERVICES.equals(partType)){
            return 2;
        }else if(PartTypeCode.PADOCMED.equals(partType)){
            return 3;
        }else if(PartTypeCode.SHRNKWRP.equals(partType)){
            return 4;
        }else {
            return -1;
        }
        
    }
    private int comparePartType(QuoteLineItem  item1,QuoteLineItem item2){
        int order1 = getOrderForPartType(item1.getPartTypeCode());
        int order2 = getOrderForPartType(item2.getPartTypeCode());
        return order1 - order2;
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        QuoteLineItem item1 = (QuoteLineItem) o1;
        QuoteLineItem item2 = (QuoteLineItem) o2;
        int result = compareApplianceAttribute(item1,item2);
        if( result !=0 ){
            return result;
        }
        result = this.comparePartType(item1,item2);
        if( result !=0 ){
            return result;
        }
        result = compareString(item1.getWwideProdCodeDesc(), item2.getWwideProdCodeDesc());
        if (result != 0) {
            return result;
        }
        
        result = compareString(item1.getSwSubId(),item2.getSwSubId());
        if (result != 0) {
            return result;
        }
        
        result = compareCPC(item1, item2);
        if (result != 0) {
            return result;
        }
        
        result = comparePPTC(item1, item2);
        if (result != 0) {
            return result;
        }
        
        int flag1 = item1.getAssocdLicPartFlag()? 0 : 1;
        int flag2 = item2.getAssocdLicPartFlag()? 0 : 1;
        
        result = (flag1-flag2);
        
        if (result != 0) {
            return result;
        }
        
        result = compareString(item1.getPartDesc(), item2.getPartDesc());
        if (result != 0) {
            return result;
        }
        
        if (isRenewalQuote(item1) && isRenewalQuote(item2)) {
            result = compareString(item1.getRenewalQuoteNum(), item2.getRenewalQuoteNum());
            if (0 != result) {
                return result;
            }
            result = compareDate(item1.getRenewalQuoteEndDate(), item2.getRenewalQuoteEndDate());
            if (0 != result) {
                return result;
            }
        }
        return 0;
    }
    
    

}
