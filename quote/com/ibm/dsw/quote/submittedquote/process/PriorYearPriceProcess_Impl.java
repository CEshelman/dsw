package com.ibm.dsw.quote.submittedquote.process;

import com.ibm.dsw.quote.submittedquote.contract.SubmittedPriorSSPriceContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;
/**
 * 
 * @author J Zhang
 *
 */
public class PriorYearPriceProcess_Impl extends TopazTransactionalProcess implements PriorYearPriceProcess {
	protected LogContext logContext = LogContextFactory.singleton().getLogContext();

	@Override
	public SubmittedPriorSSPriceContract getPriorSSPrice(String quoteNum,
			String lineItemSeq) {
		return null;
	}
	public SubmittedPriorSSPriceContract getSubmittedOmittedPriorSSPrice(String quoteNum, String lineItemSeq,String renewalNum){
		return null;
	}
}
