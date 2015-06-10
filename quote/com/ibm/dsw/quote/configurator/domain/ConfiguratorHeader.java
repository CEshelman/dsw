package com.ibm.dsw.quote.configurator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ibm.dsw.quote.configurator.viewbean.RestrictedPartSearchUIParams;

public class ConfiguratorHeader implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7488660887536200027L;
	
	private Date endDate;
	private String cntryCode;
	private String cntryCodeDscr;
	private String currencyCode;
	private String currencyCodeDscr;
	private String pid;
	private String pidDscr;
	private Integer term;
	private String audience;
	private String acqrtnCode;
	private String progMigrationCode;
	private String quoteTypeCode;
	private String customerNumber;
	private String sapContractNum;
	
	private boolean addOnOrTradeUp;
	private boolean coTermed;
	private String configurationAction;
	private String scenario;
	private String webQuoteNum;
	private String bandLevel = null;
	private String lob = null;
	private String cTFlag = null;
	private String configId;
	private String orgConfigId;
	private String chrgAgrmtNum;
	private String configrtnActionCode;
	private String[] avaliableBillingFrequencyOptions;
	private String existingPartNumbersInCA;
	private transient List<ConfiguratorProduct> products;
	
	private String overrideFlag; //0: not override;  1: override
	private String overridePilotFlag = null; // this field indicate if it comes from pilot choosen 	result
	private String overrideRstrctFlag = null; // this fileld indicate if it comes from part restriction
	
	private Integer calcTerm = null;	//first calculation step of FCT TO PA Finalization term.
	private String termForFCTToPAFinalization = null;	//first calculation step of FCT TO PA Finalization term.
	
	private RestrictedPartSearchUIParams rstrctPartSearchUIParams;
	
	public String getSapContractNum(){
		return sapContractNum;
	}
	
	public void setSapContractNum(String sapContractNum){
		this.sapContractNum = sapContractNum;
	}
	
	public String getCustomerNumber(){
		return customerNumber;
	}
	
	public void setCustomerNumber(String customerNumber){
		this.customerNumber = customerNumber;
	}
	
	public String getQuoteTypeCode(){
		return quoteTypeCode;
	}
	
	public void setQuoteTypeCode(String quoteTypeCode){
		this.quoteTypeCode = quoteTypeCode;
	}
	
	public String getProgMigrationCode(){
		return progMigrationCode;
	}
	
	public void setProgMigrationCode(String progMigrationCode){
		this.progMigrationCode = progMigrationCode;
	}
	
	public String getAcqrtnCode(){
		return acqrtnCode;
	}
	
	public void setAcqrtnCode(String acqrtnCode){
		this.acqrtnCode = acqrtnCode;
	}
	
	public String getAudience(){
		return audience;
	}
	
	public void setAudience(String audience){
		this.audience = audience;
	}
	
	public String getConfigrtnActionCode() {
		return configrtnActionCode;
	}
	public void setConfigrtnActionCode(String configrtnActionCode) {
		this.configrtnActionCode = configrtnActionCode;
	}
	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}
	public void setChrgAgrmtNum(String chrgAgrmtNum) {
		this.chrgAgrmtNum = chrgAgrmtNum;
	}
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}
	public List<ConfiguratorProduct> getProducts() {
		return products;
	}
	public void setProducts(List<ConfiguratorProduct> products) {
		this.products = products;
	}
	public String getScenario() {
		return scenario;
	}
	public void setScenario(String scenario) {
		this.scenario = scenario;
	}
	public String getCntryCode() {
		return cntryCode;
	}
	public void setCntryCode(String cntryCode) {
		this.cntryCode = cntryCode;
	}
	public String getCntryCodeDscr() {
		return cntryCodeDscr;
	}
	public void setCntryCodeDscr(String cntryCodeDscr) {
		this.cntryCodeDscr = cntryCodeDscr;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCurrencyCodeDscr() {
		return currencyCodeDscr;
	}
	public void setCurrencyCodeDscr(String currencyCodeDscr) {
		this.currencyCodeDscr = currencyCodeDscr;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getPidDscr() {
		return pidDscr;
	}
	public void setPidDscr(String pidDscr) {
		this.pidDscr = pidDscr;
	}
	public Integer getTerm() {
		return term;
	}
	public void setTerm(Integer term) {
		this.term = term;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public boolean isAddOnOrTradeUp() {
		return addOnOrTradeUp;
	}
	public void setAddOnOrTradeUp(boolean addOnOrTradeUp) {
		this.addOnOrTradeUp = addOnOrTradeUp;
	}
	public boolean isCoTermed() {
		return coTermed;
	}
	public void setCoTermed(boolean coTermed) {
		this.coTermed = coTermed;
	}
	public String getConfigurationAction() {
		return configurationAction;
	}
	public void setConfigurationAction(String configurationAction) {
		this.configurationAction = configurationAction;
	}
	public void addConfiguratorProduct(ConfiguratorProduct cprod) {
		if(products==null)
			products = new ArrayList();
		products.add(cprod);
	}	
	public String getBandLevel() {
		return bandLevel;
	}
	public void setBandLevel(String bandLevel) {
		this.bandLevel = bandLevel;
	}
	public String getLob() {
		return lob;
	}
	public void setLob(String lob) {
		this.lob = lob;
	}
	public String getCTFlag() {
		return cTFlag;
	}
	public void setCTFlag(String flag) {
		cTFlag = flag;
	}
	public String[] getAvaliableBillingFrequencyOptions() {
		return avaliableBillingFrequencyOptions;
	}
	public void setAvaliableBillingFrequencyOptions(
			String[] avaliableBillingFrequencyOptions) {
		this.avaliableBillingFrequencyOptions = avaliableBillingFrequencyOptions;
	}
	public String getExistingPartNumbersInCA() {
		return existingPartNumbersInCA;
	}
	public void setExistingPartNumbersInCA(String existingPartNumbersInCA) {
		this.existingPartNumbersInCA = existingPartNumbersInCA;
	}
	public String getOverrideFlag() {
		return overrideFlag;
	}
	public void setOverrideFlag(String overrideFlag) {
		this.overrideFlag = overrideFlag;
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
	public Integer getCalcTerm() {
		return calcTerm;
	}
	public void setCalcTerm(Integer calcTerm) {
		this.calcTerm = calcTerm;
	}
	public String getTermForFCTToPAFinalization() {
		return termForFCTToPAFinalization;
	}
	public void setTermForFCTToPAFinalization(String termForFCTToPAFinalization) {
		this.termForFCTToPAFinalization = termForFCTToPAFinalization;
	}
	public String getOrgConfigId() {
		return orgConfigId;
	}
	public void setOrgConfigId(String orgConfigId) {
		this.orgConfigId = orgConfigId;
	}

	public RestrictedPartSearchUIParams getRstrctPartSearchUIParams() {
		return rstrctPartSearchUIParams;
	}

	public void setRstrctPartSearchUIParams(
			RestrictedPartSearchUIParams rstrctPartSearchUIParams) {
		this.rstrctPartSearchUIParams = rstrctPartSearchUIParams;
	}
}
