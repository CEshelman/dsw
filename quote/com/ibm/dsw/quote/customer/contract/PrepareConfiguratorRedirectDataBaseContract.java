package com.ibm.dsw.quote.customer.contract;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PrepareConfiguratorRedirectDataBaseContract<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2011-6-16
 */
public class PrepareConfiguratorRedirectDataBaseContract extends QuoteBaseContract {
	public static final String CONFIGURATOR_TYPE_SQO = "SQO";
	public static final String CONFIGURATOR_TYPE_CPQ = "CPQ";
	public static final String REDIRECT_ACTION_CANCELL = "0";

	/********* redirect parameters ****************/
	/**
	 * BN - Brand New
	 * AT - Add-on/Trade-up
	 * UD - update - update new config | update add-on/trade-up
	 * 
	 */
	private String callingType = null;
	


	private String chrgAgrmtNum = null;
	private String caEndDate = null;
	private String debuger = null;
	private String configId = null;
	private String orgConfigId = null;
	private String customerNum = null;
	private String tarChrgAgrmtNum = null;// used for copy new configuration to point out which CA the copied configuration will be applied for. 
	private String pid = null;
	private String cTFlag = null;

	private String addOnTradeUpFlag;
	
	private String configrtnActionCode = null;
	
	/********** returned parameters ***************/
	
	
	private String referenceNum = null;
	private String redirectAction = null;
	private String cpqConfigurationID = null;
	private String configuratorType = null;
	
	private String copyFromActiveCAFlag = "1"; //1: active 0: inactive

	private String overrideFlag = "0"; //0: not override;  1: override
	
	private String configFinal = "0"; // 0: basic, 1: GST
	private String configIndicator = "0"; //0: basic, 1: GST, 2: part restriction, 3: pilot
	
	private String overridePilotFlag = null; // this field indicate if it comes from pilot choosen result
	private String overrideRstrctFlag = null; // this fileld indicate if it comes from part restriction
	
	private String mgrtReqNum = null;
	
	private String needNewQuote = null; //1 need create new quote; 0 use existing session quote
	

	private int exceedCode ;

	private String termExtensionFlag = "0";
	private String seviceDate;
	private String serviceDateModType;

    private String updateCAConfigCode;

	public String getMgrtReqNum() {
		return mgrtReqNum;
	}

	public void setMgrtReqNum(String mgrtReqNum) {
		this.mgrtReqNum = mgrtReqNum;
	}

	public String getOverridePilotFlag() {
		return overridePilotFlag;
	}

	public void setOverridePilotFlag(String overridePilotFlag) {
		this.overridePilotFlag = overridePilotFlag;
	}

	public String getOverrideRstrctFlag() {
		return overrideRstrctFlag;
	}

	public void setOverrideRstrctFlag(String overrideRstrctFlag) {
		this.overrideRstrctFlag = overrideRstrctFlag;
	}

	public String getConfigFinal() {
		return configFinal;
	}

	public void setConfigFinal(String configFinal) {
		this.configFinal = configFinal;
	}

	public String getConfigIndicator() {
		return configIndicator;
	}

	public void setConfigIndicator(String configIndicator) {
		this.configIndicator = configIndicator;
	}

	public String getOverrideFlag() {
		return overrideFlag;
	}

	public void setOverrideFlag(String overrideFlag) {
		this.overrideFlag = overrideFlag;
	}

	public String getCopyFromActiveCAFlag() {
		return copyFromActiveCAFlag;
	}

	public void setCopyFromActiveCAFlag(String copyFromActiveCAFlag) {
		this.copyFromActiveCAFlag = copyFromActiveCAFlag;
	}

	public String getConfigrtnActionCode() {
		return configrtnActionCode;
	}

	public void setConfigrtnActionCode(String configrtnActionCode) {
		this.configrtnActionCode = configrtnActionCode;
	}

	public String getConfiguratorType() {
		return configuratorType;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAddOnTradeUpFlag() {
		return addOnTradeUpFlag;
	}
	
	public void setAddOnTradeUpFlag(String addOnTradeUpFlag) {
		this.addOnTradeUpFlag = addOnTradeUpFlag;
	}

	public boolean isCancellConfigrtn(){
		return REDIRECT_ACTION_CANCELL.equals(this.redirectAction);
	}
	
	public boolean isFromCPQConfigurator(){
		return CONFIGURATOR_TYPE_CPQ.equals(this.configuratorType);
	}

	public void setConfiguratorType(String configuratorType) {
		this.configuratorType = configuratorType;
	}

	public String getTarChrgAgrmtNum() {
		return tarChrgAgrmtNum;
	}

	public void setTarChrgAgrmtNum(String tarChrgAgrmtNum) {
		this.tarChrgAgrmtNum = tarChrgAgrmtNum;
	}

	public String getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getOrgConfigId() {
		return orgConfigId;
	}

	public void setOrgConfigId(String orgConfigId) {
		this.orgConfigId = orgConfigId;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getRedirectAction() {
		return redirectAction;
	}

	public void setRedirectAction(String redirectAction) {
		this.redirectAction = redirectAction;
	}

	public String getCpqConfigurationID() {
		return cpqConfigurationID;
	}

	public void setCpqConfigurationID(String cpqConfigurationID) {
		this.cpqConfigurationID = cpqConfigurationID;
	}

	public String getDebuger() {
		return debuger;
	}

	public void setDebuger(String debuger) {
		this.debuger = debuger;
	}


	public String getCallingType() {
		return callingType;
	}

	public void setCallingType(String callingType) {
		this.callingType = callingType;
	}


	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}

	public void setChrgAgrmtNum(String chrgAgrmtNum) {
		this.chrgAgrmtNum = chrgAgrmtNum;
	}


	public String getCaEndDate() {
		return caEndDate;
	}

	public void setCaEndDate(String caEndDate) {
		this.caEndDate = caEndDate;
	}

	


    public int getExceedCode() {
		return exceedCode;
	}

	public void setExceedCode(int exceedCode) {
		this.exceedCode = exceedCode;
	}

	public void load(Parameters parameters, JadeSession session) {
    	super.load(parameters, session);
    	if(parameters.getParameter(CustomerParamKeys.PARAM_CONFIGURATOR_ReferenceNum)!=null){
    		this.setReferenceNum(parameters.getParameterAsString(CustomerParamKeys.PARAM_CONFIGURATOR_ReferenceNum));
    	}
    	if(parameters.getParameter(CustomerParamKeys.PARAM_CONFIGURATOR_RedirectAction)!=null){
    		this.setRedirectAction(parameters.getParameterAsString(CustomerParamKeys.PARAM_CONFIGURATOR_RedirectAction));
    	}
    	if(parameters.getParameter(CustomerParamKeys.PARAM_CONFIGURATOR_ConfigurationID)!=null){
    		this.setCpqConfigurationID(parameters.getParameterAsString(CustomerParamKeys.PARAM_CONFIGURATOR_ConfigurationID));
    	}
    	if(parameters.getParameter(ConfiguratorParamKeys.AddOnTradeUpFlag)!=null){
            addOnTradeUpFlag = (String)parameters.getParameter(ConfiguratorParamKeys.AddOnTradeUpFlag);
    	}

        pid = (String)parameters.getParameter(ConfiguratorParamKeys.OfferingCode);
        cTFlag = (String)parameters.getParameter(ConfiguratorParamKeys.CTFlag);

		if(StringUtils.isBlank(this.overrideFlag)){
			this.overrideFlag = "0";
		}
		
		if(this.pid != null){
			this.pid = this.pid.trim();
		}
    }
	


	public String getPid() {
		return pid;
	}
	
	public String getCTFlag() {
		return cTFlag;
	}
	public void setCTFlag(String cTFlag) {
		this.cTFlag = cTFlag;
	}

	public String getNeedNewQuote() {
		return needNewQuote;
	}

	public void setNeedNewQuote(String needNewQuote) {
		this.needNewQuote = needNewQuote;
	}

	public String getTermExtensionFlag() {
		return termExtensionFlag;
	}

	public void setTermExtensionFlag(String termExtensionFlag) {
		this.termExtensionFlag = termExtensionFlag;
	}

	public String getSeviceDate() {
		return seviceDate;
	}

	public void setSeviceDate(String seviceDate) {
		this.seviceDate = seviceDate;
	}

	public String getServiceDateModType() {
		return serviceDateModType;
	}

	public void setServiceDateModType(String serviceDateModType) {
		this.serviceDateModType = serviceDateModType;
	}

    /**
     * Getter for updateCAConfigCode.
     * 
     * @return the updateCAConfigCode
     */
    public String getUpdateCAConfigCode() {
        return this.updateCAConfigCode;
    }

    /**
     * Sets the updateCAConfigCode.
     * 
     * @param updateCAConfigCode the updateCAConfigCode to set
     */
    public void setUpdateCAConfigCode(String updateCAConfigCode) {
        this.updateCAConfigCode = updateCAConfigCode;
    }

}
