package com.ibm.dsw.quote.scw.addon.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.scw.addon.ScwAddOnTradeUpResult;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpInfo;
import com.ibm.dsw.quote.scw.addon.domain.RetrieveQuote;
import com.ibm.ead4j.topaz.exception.TopazException;

public interface ScwAddonTradeupProcess {

	/**
	 * Vivian team to implement this method, be called by rest service action
	 * @param addOnTradeUpInfo
	 * @return
	 * @throws TopazException
	 * @throws QuoteException
	 */
	public ScwAddOnTradeUpResult doScwAddOnTradeUp(AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException,
	QuoteException;
	
	/**
	 * Jovo to implement this to create a quote and return the webQuoteNum
	 * @param addOnTradeUpInfo
	 * @return
	 * @throws TopazException
	 * @throws QuoteException
	 */
	public String initAddOnTradeUpQuote(AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException,
	QuoteException;
	
	/**
	 * Grover to implement this to insert line item
	 * @param addOnTradeUpInfo
	 * @return
	 * @throws TopazException
	 * @throws QuoteException
	 */
//	public void initAddOnTradeUpLineItem(AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException,
//	QuoteException;
	
	/**
	 * Gaven to implement this to return the xml domain
	 * @param addOnTradeUpInfo
	 * @return
	 * @throws TopazException
	 * @throws QuoteException
	 */
	public RetrieveQuote retrieveScwAddOnTradeUpResult(AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException,
	QuoteException;

}
