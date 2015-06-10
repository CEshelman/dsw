package com.ibm.dsw.quote.draftquote.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.contract.RetrievePartFromCognosContract;
import com.ibm.ead4j.topaz.exception.TopazException;

public interface RetrievePartFromCognosProcess {
	public void addPartsToQuote(RetrievePartFromCognosContract ct) throws TopazException, QuoteException, Exception;
	 public List<String> getValidPartsByPartSearch(String partNumList, RetrievePartFromCognosContract ct) throws QuoteException;
	 public boolean isValidCognosCallback(RetrievePartFromCognosContract ct);
}
