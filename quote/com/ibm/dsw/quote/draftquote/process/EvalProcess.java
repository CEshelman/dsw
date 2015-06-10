package com.ibm.dsw.quote.draftquote.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.EvalQuote;

public interface EvalProcess {

	public List<EvalQuote> getUnderEvalQtList(String creator_id, String isTeleSales) throws QuoteException;
	
	public List<String> getEvalsByCntry(String cntry_code) throws QuoteException;

}
