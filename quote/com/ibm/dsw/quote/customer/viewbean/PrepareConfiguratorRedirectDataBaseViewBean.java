package com.ibm.dsw.quote.customer.viewbean;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PrepareConfiguratorRedirectDataBaseViewBean<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2011-6-16
 */
public class PrepareConfiguratorRedirectDataBaseViewBean   extends BaseViewBean{
	private final String cancelURL = "cpqDialog";
	private final String  displayHeader = "0";
	private final String  displayFooter = "0";
	private String returnUrl ="";
	
	private RedirectConfiguratorDataBasePack redirectConfDataPack = null;
	
	private String configurationURL = null;
	private String debuger = null;
	private String webQuoteNum = null;

	private int configIndicator = 0; //0: basic, 1: GST, 2: part restriction, 3: pilot
	
	
	public int getConfigIndicator() {
		return configIndicator;
	}

	public void setConfigIndicator(int configIndicator) {
		this.configIndicator = configIndicator;
	}

	public String getExistingPartNumbersInCA(){
		if(this.redirectConfDataPack.getExistingPartsInCA()!=null && this.redirectConfDataPack.getExistingPartsInCA().size()>0){
			String existingPartNumbersInCA = "";
			for(ActiveService as: this.redirectConfDataPack.getExistingPartsInCA()){
				if("".equals(existingPartNumbersInCA)){
					existingPartNumbersInCA = existingPartNumbersInCA.concat(as.getPartNumber());
				}else{
					existingPartNumbersInCA = existingPartNumbersInCA.concat(",").concat(as.getPartNumber());
				}
			}
			return existingPartNumbersInCA;
		}else{
			return null;
		}
	}

    public void collectResults(Parameters params) throws ViewBeanException {
    	redirectConfDataPack = (RedirectConfiguratorDataBasePack)params.getParameter(CustomerParamKeys.PARAM_CPA_DATA_PACK);
    	configurationURL = (String)params.getParameter(CustomerParamKeys.PARAM_SERVICE_CONFIGURATION_URL);
    	if(params.getParameter(CustomerParamKeys.PARAM_DEBUGER)!=null){
    		this.setDebuger((String)params.getParameter(CustomerParamKeys.PARAM_DEBUGER));
    	}
    	if(params.getParameterAsString(CustomerParamKeys.PARAM_CONFIG_INDICATOR)!=null){
        	this.setConfigIndicator(Integer.parseInt(params.getParameterAsString(CustomerParamKeys.PARAM_CONFIG_INDICATOR)));
    	}
    }
    	
    

	public String getDebuger() {
		return debuger;
	}



	public void setDebuger(String debuger) {
		this.debuger = debuger;
	}



	public String getConsumingAppID() {
		return convertNull(redirectConfDataPack.getConsumingAppID());
	}
	public String getRedirectURL() {
		return convertNull(redirectConfDataPack.getRedirectURL());
	}
	public String getReferenceNum() {
		return convertNull(redirectConfDataPack.getReferenceNum());
	}
	public String getConfigurationID() {
		return convertNull(redirectConfDataPack.getConfigurationID());
	}
	public String getBandLevel() {
		return convertNull(redirectConfDataPack.getBandLevel());
	}
	public String getCountry(){
		return convertNull(redirectConfDataPack.getCountry());
	}
	public String getRegion() {
		return convertNull(redirectConfDataPack.getRegion());
	}
	public String getOfferingCode() {
		return convertNull(redirectConfDataPack.getOfferingCode());
	}
	public String getCurrencyCode() {
		return convertNull(redirectConfDataPack.getCurrencyCode());
	}
	public String getCtFlag() {
		return convertNull(redirectConfDataPack.getCtFlag());
	}
	public String getCaEndDate(){
		return convertNull(redirectConfDataPack.getCaEndDate());
	}
	
	public String getChrgAgrmtNum(){
		return convertNull(redirectConfDataPack.getChrgAgrmtNum());
	}
	
	public String getOverridePilotFlag(){
		return convertNull(redirectConfDataPack.getOverridePilotFlag());
	}
	
	public String getOverrideRstrctFlag(){
		return convertNull(redirectConfDataPack.getOverrideRstrctFlag());
	}
	

	public String getConfigId(){
		return convertNull(redirectConfDataPack.getConfigId());
	}

	public String getOrgConfigId(){
		return convertNull(redirectConfDataPack.getOrgConfigId());
	}
	
	public String getConfigrtnActionCode(){
		return convertNull(redirectConfDataPack.getConfigrtnActionCode());
	}
	

	public String getConfiguratorType(){
		return convertNull(redirectConfDataPack.getConfiguratorType());
	}
	

	public String getSourceType(){
		return convertNull(redirectConfDataPack.getSourceType());
	}
	
	public String getOverrideFlag(){
		return convertNull(redirectConfDataPack.getOverrideFlag());
	}
	
	public RedirectConfiguratorDataBasePack getRedirectConfDataPack() {
		return redirectConfDataPack;
	}
	public void setRedirectConfDataPack(RedirectConfiguratorDataBasePack redirectConfDataPack) {
		this.redirectConfDataPack = redirectConfDataPack;
	}
		
	public String getConfigurationURL() {
		return configurationURL;
	}
	public void setConfigurationURL(String configurationURL) {
		this.configurationURL = configurationURL;
	}

	public String getCancelURL() {
		return cancelURL;
	}
	public String getDisplayHeader() {
		return displayHeader;
	}
	public String getDisplayFooter() {
		return displayFooter;
	}
	
	public String getLob(){
		return convertNull(redirectConfDataPack.getLob());
	}
	
	public String getAddTradeFlag(){
		return convertNull(redirectConfDataPack.getAddTradeFlag());
	}

	public String getCalcTerm(){
		return convertNull(redirectConfDataPack.getFctToPaCalcTerm());
	}
	

	public String convertNull(String string){
		if(string!=null){
			string = string.trim();
		}
		return org.apache.commons.lang.StringUtils.trimToEmpty(string);
	}

	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}
	
	
}
