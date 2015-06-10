package com.ibm.dsw.quote.provisng.process;

import com.ibm.ead4j.topaz.exception.TopazException;

public interface RedirectProvisngProcess {
	
	/**
	 * udpate the provisioning id that belongs to the webQuoteNum and configrtnId
	 * @param webQuoteNum
	 * @param configrtnId
	 * @throws TopazException
	 */
	public String updateProvisngId(String webQuoteNum, String provisngIdForBrand,String saasBrandCode) throws TopazException;
	
}
