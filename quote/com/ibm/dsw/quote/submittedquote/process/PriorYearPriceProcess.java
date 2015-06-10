package com.ibm.dsw.quote.submittedquote.process;

import com.ibm.dsw.quote.submittedquote.contract.SubmittedPriorSSPriceContract;
/**
 * 
 * @author J Zhang
 *
 */
public interface PriorYearPriceProcess {
    public SubmittedPriorSSPriceContract getPriorSSPrice(String quoteNum, String lineItemSeq);
    
    public SubmittedPriorSSPriceContract getSubmittedOmittedPriorSSPrice(String quoteNum, String lineItemSeq,String renewalNum);
}