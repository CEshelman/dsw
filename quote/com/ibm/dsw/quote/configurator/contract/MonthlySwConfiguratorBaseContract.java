/**
 * 
 */
package com.ibm.dsw.quote.configurator.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @ClassName: MonthlySwConfiguratorBaseContract
 * @author Frank
 * @Description: TODO
 * @date Dec 18, 2013 4:34:58 PM
 *
 */
public class MonthlySwConfiguratorBaseContract extends QuoteBaseContract {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final LogContext logger = LogContextFactory.singleton().getLogContext();
	
	protected String webQuoteNum;
	
	protected String partTerm;
	
	protected String chrgAgrmtNum;
	
	private String globalTerm;
	
	private String configurtnId ;
	
	//is newCA,addon/tradeup , fct to pa
	protected String configrtnActionCode;
	
	private String orgConfigId;
	
	private String notFoundPartListStr="";
	private String alreadyExistedPartListStr="";
	private String processedPartListStr="";
    private String partListStrForSkipTermValidation="";
	
	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	
	public String getPartTerm() {
		return partTerm;
	}
	
	public void setPartTerm(String term) {
		this.partTerm = term;
	}
	
	
	
	public String getConfigrtnActionCode() {
		return configrtnActionCode;
	}

	public void load(Parameters parameters, JadeSession session) {
		
		super.load(parameters, session);
		
		webQuoteNum = (String)parameters.getParameter(ConfiguratorParamKeys.webQuoteNum);
		
		configrtnActionCode = (String)parameters.getParameter(ConfiguratorParamKeys.configrtnActionCode);
		
		configurtnId = (String)parameters.getParameter(ConfiguratorParamKeys.configId);
		
		chrgAgrmtNum = (String)parameters.getParameter(ConfiguratorParamKeys.chrgAgrmtNum);

		orgConfigId = (String) parameters.getParameter(ConfiguratorParamKeys.orgConfigId);
		
        notFoundPartListStr = parameters.getParameterAsString(ConfiguratorParamKeys.notFoundRestrictedPartList);
        alreadyExistedPartListStr = parameters.getParameterAsString(ConfiguratorParamKeys.existedRestriectedPartList);
        processedPartListStr = parameters.getParameterAsString(ConfiguratorParamKeys.neededProcessRestrictedPartList);
        partListStrForSkipTermValidation=parameters.getParameterAsString(ConfiguratorParamKeys.partList4SkipTermValidation);
	}
	


	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}

	public void setChrgAgrmtNum(String chrgAgrmtNum) {
		this.chrgAgrmtNum = chrgAgrmtNum;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}



	public String getConfigurtnId() {
		return configurtnId;
	}

	public void setConfigurtnId(String configurtnId) {
		this.configurtnId = configurtnId;
	}

	public String getGlobalTerm() {
		return globalTerm;
	}

	public void setGlobalTerm(String globalTerm) {
		this.globalTerm = globalTerm;
	}

	public String getOrgConfigId() {
		return orgConfigId;
	}

	public void setOrgConfigId(String orgConfigId) {
		this.orgConfigId = orgConfigId;
	}

	public boolean isAddOnConfigrtn() {
		return (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configrtnActionCode));
	}
	
	public boolean isNewCaConfigrtn() {
		return (PartPriceConstants.ConfigrtnActionCode.NEW_NCT.equals(configrtnActionCode));
	}

	
	public String getNotFoundPartListStr() {
		return notFoundPartListStr;
	}

	public void setNotFoundPartListStr(String notFoundPartListStr) {
		this.notFoundPartListStr = notFoundPartListStr;
	}

	public String getAlreadyExistedPartListStr() {
		return alreadyExistedPartListStr;
	}

	public void setAlreadyExistedPartListStr(String alreadyExistedPartListStr) {
		this.alreadyExistedPartListStr = alreadyExistedPartListStr;
	}

	public String getProcessedPartListStr() {
		return processedPartListStr;
	}

	public void setProcessedPartListStr(String processedPartListStr) {
		this.processedPartListStr = processedPartListStr;
	}

	public String getPartListStrForSkipTermValidation() {
		return partListStrForSkipTermValidation;
	}

	public void setPartListStrForSkipTermValidation(
			String partListStrForSkipTermValidation) {
		this.partListStrForSkipTermValidation = partListStrForSkipTermValidation;
	}


}
