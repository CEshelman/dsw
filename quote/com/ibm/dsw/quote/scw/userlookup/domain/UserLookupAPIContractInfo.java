package com.ibm.dsw.quote.scw.userlookup.domain;



public class UserLookupAPIContractInfo{
	/**
	 * 
	 */
	//Web customer(user) info
	private String webIdentityID;
	private String webIdentityUniqueID;
	private String emailAddress;
	//Customer info
	private String siteNumber; //DSW SAP customer number
	//Product info
	private String ifContainSoftware;
	private String ifContainSaaS;
	private String pid;
	//Charge Agreemant Info
	private String chargeAgreementNumber;
	private String configID;
	//Store Info
	private String allowedCountries;
	
	

	public String getWebIdentityID() {
		return webIdentityID;
	}
	
	public void setWebIdentityID(String wiid) {
		this.webIdentityID = wiid;
	}



	public String getWebIdentityUniqueID() {
		return webIdentityUniqueID;
	}

	public void  setWebIdentityUniqueID(String wiuniqid) {
		this.webIdentityUniqueID = wiuniqid;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailaddress) {
		this.emailAddress = emailaddress;
	}

	public String getSiteNumber() {
		return siteNumber;
	}

	public void setSiteNumber(String sitenumber) {
		this.siteNumber = sitenumber;
	}

	public String getIfContainSoftware() {
		return ifContainSoftware;
	}

	public void setIfContainSoftware(String ifcontainsftwr) {
		this.ifContainSoftware = ifcontainsftwr;
	}

	public String getIfContainSaaS() {
		return ifContainSaaS;
	}

	public void setIfContainSaaS(String ifcontainsaas) {
		this.ifContainSaaS = ifcontainsaas;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid =pid;
	}

	public String getChargeAgreementNumber() {
		return chargeAgreementNumber;
	}

	public void setChargeAgreementNumber(String canumber) {
		this.chargeAgreementNumber = canumber;
	}

	public String getConfigID() {
		return configID;
	}

	public void setConfigID(String configid) {
		this.configID = configid;
	}

	public String getAllowedCountries() {
		return allowedCountries;
	}

	public void setAllowedCountries(String countries) {
		this.allowedCountries = countries;
	}

	/**
	 * @return the addOnTradeUp
	 */
	
	/**
	 * Added by Randy to 
	 */
	public String toString(){
		StringBuffer result = new StringBuffer();
		String lineSeparator = System.getProperty("line.separator", "\n"); 
		result.append("	Allowed countries: " + this.allowedCountries + lineSeparator );
		result.append("	Charge Agreement Number: " + this.chargeAgreementNumber + lineSeparator);
		result.append("	ConfigID: " + this.configID + lineSeparator);
		result.append("	Email Address: " + this.emailAddress + lineSeparator);
		result.append("	If Contain SaaS: " + this.ifContainSaaS + lineSeparator) ;
		result.append("	Pid: " + this.pid + lineSeparator);
		result.append("	Web Identity ID: " + this.webIdentityID + lineSeparator);
		result.append("	Web Identity Unique ID: " + this.webIdentityID + lineSeparator);
		return result.toString();
	}

	


}
