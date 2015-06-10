package com.ibm.dsw.quote.scw.userlookup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ScwUserLookupConfigResult {
	
	@XmlElement(name="configID")
	private String configrtnID;
	
	@XmlElement(name="CANum")
	private String chargeAgreement;
	
	//0 or 1
	@XmlElement(name="allowConfigUpdate")
	private int updateFlag;
	
	@XmlElement(name="IsProvisioningHold")
	private int isProvisioningHold;
	
	@XmlElement(name="HasRenwlMdl")
	private int hasRenwlMdl;
	
	@XmlElement(name="configEndDate")
	private String endDate;
	
	@XmlElement(name="partDescription")
	private String partDescriptionList;

	//	public String getSapSalesOrdNum() {
	//	return chargeAgreement;
	//}

	public void setSapSalesOrdNum(String sapSalesOrdNum) {
		this.chargeAgreement = sapSalesOrdNum;
	}

	public String getConfigrtnID() {
		return configrtnID;
	}

	public void setConfigrtnID(String configrtnID) {
		this.configrtnID = configrtnID;
	}

	//public int getUpdateFlag() {
	//	return updateFlag;
	//}

	public void setUpdateFlag(int updateFlag) {
		this.updateFlag = updateFlag;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
    /**
     * Getter for chargeAgreement.
     * 
     * @return the chargeAgreement
     */
    public String getChargeAgreement() {
        return this.chargeAgreement;
    }

	public void setIsProvisioningHold(int isProvisioningHold) {
		this.isProvisioningHold = isProvisioningHold;
	}

	public void setHasRenwlMdl(int hasRenwlMdl) {
		this.hasRenwlMdl = hasRenwlMdl;
	}

	public int getIsProvisioningHold() {
		return isProvisioningHold;
	}

	public int getHasRenwlMdl() {
		return hasRenwlMdl;
	}
	
    
    public void setPartDescriptionList(String dscrList){
    	this.partDescriptionList = dscrList;
    	
    }
    
    public String getPartDescriptionList(){
    	return this.partDescriptionList;
    }
}
