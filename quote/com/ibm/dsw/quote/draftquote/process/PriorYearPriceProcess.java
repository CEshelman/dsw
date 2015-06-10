package com.ibm.dsw.quote.draftquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.contract.PriorSSPriceContract;

/**
 * 
 * @author jason
 *
 */
public interface PriorYearPriceProcess {
	public void AddPriorSSPrice(PriorSSPriceContract  contract);

    public void updatePriorSSPrice(PriorSSPriceContract  contract);

    public PriorSSPriceContract getPriorSSPrice(String  quoteNum,String lineItemSeq);
    
    public void addOmittedPriorSSPrice(PriorSSPriceContract  contract);
    
    public PriorSSPriceContract getOmittedPriorSSPrice(String  quoteNum,String lineItemSeq,String renewalNum);
    
    public void updateOmittedPriorSSPrice(PriorSSPriceContract  contract);
    
    public String getNewOmittedYtYGrowth (String quoteNum,String renewalNum,String renwlQuoteLineSeqNum)throws QuoteException;
}
