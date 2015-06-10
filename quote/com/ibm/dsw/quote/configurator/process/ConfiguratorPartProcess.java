package com.ibm.dsw.quote.configurator.process;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.configurator.contract.ConfigureHostedServiceContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.configurator.domain.DomainAdapter;
import com.ibm.ead4j.topaz.exception.TopazException;

public interface ConfiguratorPartProcess {

	public int addOrUpdateConfigrtn(String webQuoteNum, String configrtnId
                                      , String configrtrConfigrtnId
                                      , String userId, String refDocNum
                                      , String errorCode, String configrtnAction
                                      , Date endDate, String coTermToConfigrtnId
                                      , String overrideFlag
                                      , String importFlag
                                      ,String termExtensionFlag
                                      ,Date serviceDate
                                      ,String serviceDateModType
                                      , String provisioningId
                                      , int extEntireConfigFlag) throws TopazException;
	public void buildConfiguratorHeader(ConfigureHostedServiceContract chsContract,ConfiguratorHeader header) throws TopazException;
	public void changeRampUpPeriods(ConfigureHostedServiceContract chsContract,ConfiguratorHeader header) throws TopazException;

	public void setSaaSPartAttribute(List<ConfiguratorPart> list, Map<String, ConfiguratorPart> map, String pId)
			throws QuoteException;

	public CotermParameter getCotermToPartInfo(String chrgAgrmtNum, String configrtnId, List<DomainAdapter> replacedParts,String actionCode) throws QuoteException;
	public Map<String, ConfiguratorPart> findMainPartsFromChrgAgrm(String chrgAgrmtNum, String configId)  throws TopazException;
}