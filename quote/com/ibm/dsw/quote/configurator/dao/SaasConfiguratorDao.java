package com.ibm.dsw.quote.configurator.dao;

import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SaasConfiguration;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.ead4j.topaz.exception.TopazException;

public interface SaasConfiguratorDao {
	List findConfiguratorsByWebQuoteNum(String webQuoteNum)
			throws TopazException;

	List findPartsByWebQuoteNumPID(String webQuoteNum, String pid)
			throws TopazException;

	List findPartsByWebQuoteNumPID4Scw(String webQuoteNum, String pid)
			throws TopazException;

	
	/**
	 * Retrieve all active subscription parts within a charge agreement
	 */
	List<ConfiguratorPart> getSubPartsFromChrgAgrm(String chrgAgrmtNum,
			String configId) throws QuoteException;
	public  void setDefaultProvisngDays(String webQuoteNum,String configrtnIdParam,String orgConfigrtnIdParam)
			throws TopazException;
	public int addOrUpdateConfigrtn(SaasConfiguration configrtn)
			throws TopazException ;

	public List<ConfiguratorPart> getPartsFromChrgAgrm(AddOrUpdateConfigurationContract ct, Map<String, ConfiguratorPart> map)
			throws QuoteException;
}
