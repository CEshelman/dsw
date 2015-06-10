package com.ibm.dsw.quote.customerlist.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;


public class RedirectConfiguratorDataBasePack implements java.io.Serializable {
	
	//parameters required by Configurator
	private String consumingAppID = null;
	private String redirectURL = null;
	private String referenceNum = null;
	private String configurationID = null;
	private String bandLevel = null;
	private String country = null;
	private String region = null;
	private String offeringCode = null;
	private String currencyCode = null;
	private String ctFlag = null;
	private String caEndDate = null;
	private List<ActiveService> activeServices = null;
	private List dynParams = null;
	private Quote quote = null;
	private String lob = null;
	private String addTradeFlag = null;
	private List avaliableBillingOptions = null;
	
	//only for SQO configurator
	private String chrgAgrmtNum = null;
	private String configId = null;
	private String orgConfigId = null; // Saas FCT to PA will generate new configuration id, this original id is used for this scenario.
	private String configuratorType = null;
	private String configrtnActionCode = null;
	private String sourceType = null;
	
	//local vars
	private String configurationURL = null;
	private HashMap redirectParams = new HashMap(); 
	private HashMap<String,String> returnedParams = new HashMap(); 
	private List<ActiveService> existingPartsInCA = null;

	private String overrideFlag = "0"; //0: not override;  1: override
	private String overridePilotFlag = null;
	private String overrideRstrctFlag = null;
	
	private MigrateRequest migrateRequest = null;

	private String fctToPaCalcTerm = null;
	private String fctToPaFinalTerm = null;
	
	private Date provisioningDate = null;
	private Date cotermEndDate = null;
	
	// add for 10.6
	private boolean isTermExtension = false;
	private ServiceDateModType serviceModelType = null;
	private List<ExtensionActiveService> extensionActiveServices = new ArrayList<ExtensionActiveService>();

	public List<ExtensionActiveService> getExtensionActiveServices() {
		return extensionActiveServices;
	}

	public void setExtensionActiveServices(
			List<ExtensionActiveService> extensionActiveServices) {
		this.extensionActiveServices = extensionActiveServices;
	}

	public ServiceDateModType getServiceModelType() {
		return serviceModelType;
	}

	public void setServiceModelType(ServiceDateModType serviceModelType) {
		this.serviceModelType = serviceModelType;
	}

	public void addActiveService(String partNumber, String billingFrequency, String term, String quantity,String rampupFlag,
															String rampupSeqNum){
		if(activeServices == null){
			activeServices = new LinkedList();
		}
		ActiveService as = new ActiveService();
		as.setPartNumber(partNumber);
		as.setBillingFrequency(billingFrequency);
		as.setTerm(term);
		as.setQuantity(quantity);
		as.setRampupFlag(rampupFlag);
		as.setRampupSeqNum(rampupSeqNum);
		activeServices.add(as);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("consumingAppID is :"+consumingAppID);
		sb.append("\n");
		sb.append("redirectURL is :"+redirectURL);
		sb.append("\n");
		sb.append("referenceNum is :"+referenceNum);
		sb.append("\n");
		sb.append("configurationID is :"+configurationID);
		sb.append("\n");
		sb.append("bandLevel is :"+bandLevel);
		sb.append("\n");
		sb.append("country is :"+country);
		sb.append("\n");
		sb.append("region is :"+region);
		sb.append("\n");
		sb.append("offeringCode is :"+offeringCode);
		sb.append("\n");
		sb.append("currencyCode is :"+currencyCode);
		sb.append("\n");
		sb.append("ctFlag is :"+ctFlag);
		sb.append("\n");
		sb.append("caEndDate is :"+caEndDate);
		sb.append("\n");
		sb.append("addTradeFlag is :"+addTradeFlag);
		sb.append("\n");
		sb.append("activeServices contains:");
		sb.append("\n[");
		sb.append("\n");
		if(this.activeServices!=null && this.activeServices.size()>0){
			for(ActiveService as: this.activeServices){
				sb.append("\t\t");
				sb.append("billingFrequency["+as.getBillingFrequency());
				sb.append("],");
				sb.append("endDate["+as.getEndDate());
				sb.append("],");
				sb.append("partNumber["+as.getPartNumber());
				sb.append("],");
				sb.append("quantity["+as.getQuantity());
				sb.append("],");
				sb.append("rampupFlag["+as.getRampupFlag());
				sb.append("],");
				sb.append("rampupSeqNum["+as.getRampupSeqNum());
				sb.append("],");
				sb.append("replaced["+as.getReplaced());
				sb.append("],");
				sb.append("term["+as.getTerm());
				sb.append("]");
				sb.append("\n");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	


	public Date getProvisioningDate() {
		return provisioningDate;
	}

	public void setProvisioningDate(Date provisioningDate) {
		this.provisioningDate = provisioningDate;
	}

	public String getFctToPaFinalTerm() {
		return fctToPaFinalTerm;
	}

	public void setFctToPaFinalTerm(String fctToPaFinalTerm) {
		this.fctToPaFinalTerm = fctToPaFinalTerm;
	}

	public String getFctToPaCalcTerm() {
		return fctToPaCalcTerm;
	}

	public void setFctToPaCalcTerm(String fctToPaCalcTerm) {
		this.fctToPaCalcTerm = fctToPaCalcTerm;
	}

	public MigrateRequest getMigrateRequest() {
		return migrateRequest;
	}

	public void setMigrateRequest(MigrateRequest migrateRequest) {
		this.migrateRequest = migrateRequest;
	}

	public String getOverrideFlag() {
		return overrideFlag;
	}

	public void setOverrideFlag(String overrideFlag) {
		this.overrideFlag = overrideFlag;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public List<ActiveService> getExistingPartsInCA() {
		return existingPartsInCA;
	}

	public void setExistingPartsInCA(List<ActiveService> existingPartsInCA) {
		this.existingPartsInCA = existingPartsInCA;
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



	public void setConfiguratorType(String configuratorType) {
		this.configuratorType = configuratorType;
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

	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}



	public void setChrgAgrmtNum(String chrgAgrmtNum) {
		this.chrgAgrmtNum = chrgAgrmtNum;
	}



	public List getAvaliableBillingOptions() {
		return avaliableBillingOptions;
	}



	public void setAvaliableBillingOptions(List avaliableBillingOptions) {
		this.avaliableBillingOptions = avaliableBillingOptions;
	}



	public void addActiveService(ActiveService service){
		if(activeServices == null){
			activeServices = new LinkedList();
		}
		activeServices.add(service);
	}
	

	public void addActiveService(Collection services){
		if(activeServices == null){
			activeServices = new LinkedList();
		}
		activeServices.addAll(services);
	}
	
	public void addParam(String paramName, String paramValue){
		if(dynParams == null){
			dynParams = new LinkedList();
		}
		dynParams.add(new Parameter(paramName,paramValue));
	}
	
	public class Parameter{
		private String paramName = null;
		private String paramValue = null;
		
		public Parameter(String paramName, String paramValue ){
			this.paramName = paramName;
			this.paramValue = paramValue;
		}
		
		public String getParamName() {
			return paramName;
		}
		public String getParamValue() {
			return paramValue;
		}		
		
	}
	
	
	
	public String getAddTradeFlag() {
		return addTradeFlag;
	}

	public void setAddTradeFlag(String addTradeFlag) {
		this.addTradeFlag = addTradeFlag;
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public String getConsumingAppID() {
		return consumingAppID;
	}
	public void setConsumingAppID(String consumingAppID) {
		this.consumingAppID = consumingAppID;
	}
	public String getRedirectURL() {
		return redirectURL;
	}
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	public String getReferenceNum() {
		return referenceNum;
	}
	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}
	public String getConfigurationID() {
		return configurationID;
	}
	public void setConfigurationID(String configurationID) {
		this.configurationID = configurationID;
	}
	public String getBandLevel() {
		return bandLevel;
	}
	public void setBandLevel(String bandLevel) {
		this.bandLevel = bandLevel;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getOfferingCode() {
		return offeringCode;
	}
	public void setOfferingCode(String offeringCode) {
		this.offeringCode = offeringCode;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCtFlag() {
		return ctFlag;
	}
	public void setCtFlag(String ctFlag) {
		this.ctFlag = ctFlag;
	}
	public List getActiveServices() {
		return activeServices;
	}
	public void setActiveServices(List activeServices) {
		this.activeServices = activeServices;
	}

	public List getDynParams() {
		return dynParams;
	}

	public void setDynParams(List dynParams) {
		this.dynParams = dynParams;
	}

	public String getCaEndDate() {
		return caEndDate;
	}

	public void setCaEndDate(String caEndDate) {
		this.caEndDate = caEndDate;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public String getConfigurationURL() {
		return configurationURL;
	}

	public void setConfigurationURL(String configurationURL) {
		this.configurationURL = configurationURL;
	}

	public HashMap getRedirectParams() {
		return redirectParams;
	}

	public void setRedirectParams(HashMap redirectParams) {
		this.redirectParams = redirectParams;
	}

	public HashMap<String, String> getReturnedParams() {
		return returnedParams;
	}

	public void setReturnedParams(HashMap<String, String> returnedParams) {
		this.returnedParams = returnedParams;
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

	public Date getCotermEndDate() {
		return cotermEndDate;
	}

	public void setCotermEndDate(Date cotermEndDate) {
		this.cotermEndDate = cotermEndDate;
	}

	public boolean isTermExtension() {
		return isTermExtension;
	}

	public void setTermExtension(boolean isTermExtension) {
		this.isTermExtension = isTermExtension;
	}
	
	public ExtensionActiveService getExtensionActiveServiceByPartNumSeqNum(String partNum, int seqNum) {
		if(this.getExtensionActiveServices() != null && this.getExtensionActiveServices().size() > 0){
			for (Iterator iterator = this.getExtensionActiveServices().iterator(); iterator.hasNext();) {
				ExtensionActiveService eas = (ExtensionActiveService) iterator.next();
				if(StringUtils.equals(partNum, eas.getPartNumber())
					&& eas.getLineItemSeqNumber() != null && seqNum == eas.getLineItemSeqNumber().intValue()){
					return eas;
				}
			}
		}
		return null;
	}

	public static void main(String args)throws Exception{

		String url = "https://w3-117fvt.etl.ibm.com/software/sales/passportadvantage/dswpricebook/PbCfgSales/quote.wss?jadeAction=RETURN_FROM_CONF_TO_SQO&configrtnActionCode=NewNCt&webQuoteNum=0001723881&configuratorType=CPQ&addTradeFlag";
		String encodedVal = java.net.URLEncoder.encode(url, "UTF-8");
	}
	
}