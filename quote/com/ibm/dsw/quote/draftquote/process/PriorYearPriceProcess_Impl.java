package com.ibm.dsw.quote.draftquote.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.contract.PriorSSPriceContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;
/**
 * 
 * @author jason
 *
 */
public class PriorYearPriceProcess_Impl extends TopazTransactionalProcess implements PriorYearPriceProcess {
	protected LogContext logContext = LogContextFactory.singleton().getLogContext();
	@Override
	public void AddPriorSSPrice(PriorSSPriceContract contract) {
	}

	@Override
	public void updatePriorSSPrice(PriorSSPriceContract contract) {

	}

	@Override
	public PriorSSPriceContract getPriorSSPrice(String quoteNum,
			String lineItemSeq) {
		return null;
	}
	@Override
	public void addOmittedPriorSSPrice(PriorSSPriceContract  contract){
		
	}
	
	@Override
	public PriorSSPriceContract getOmittedPriorSSPrice(String quoteNum,
			String lineItemSeq,String renewalNum) {
		return null;
	}
	
	@Override
	public void updateOmittedPriorSSPrice(PriorSSPriceContract  contract) {

	}
	@Override
	public String getNewOmittedYtYGrowth (String quoteNum,String renewalNum,String renwlQuoteLineSeqNum) throws QuoteException{
		
		return null;
	}
}
