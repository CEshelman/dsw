package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.ead4j.topaz.exception.TopazException;

public abstract class PartsPricingConfiguration_Impl implements PartsPricingConfiguration, Serializable {
	public String webQuoteNum;
	public String configrtnId;
	public String configrtrConfigrtnId;
	public Integer provisngDays;
	public String configrtnActionCode;
	public String userID;
	public String ibmProdId;
    public String ibmProdIdDscr;
    public Date endDate;
    public String cotermConfigrtnId;
    public Date configrtnModDate;
    public Integer provisngDaysDefault;
    public Double increaseBidTCV;
    public Double unusedBidTCV;
    public boolean configrtnOvrrdn;
    public boolean allowOvrrd;
    public String configrtnErrCode;
    public String provisioningId;
    public boolean isProvisioningCopied;
    public List<String> chargeAgreementList = new ArrayList();
    //store the coterm configurations <ca num,coterm config list>
    public Map<String,List> cotermMap = new HashMap();
    public String prodBrandCode;
    public String prodBrandCodeDscr;

	// added for Saas 10.4 and 10.6
	public ServiceDateModType serviceDateModType;
	public Date serviceDate;
	public boolean termExtension;
	public boolean configEntireExtended;

    public int compareTo(PartsPricingConfiguration partsPricingConfiguration){
	    if (this.getIbmProdIdDscr() == null) {
			return -1 ;
		} else {
			return this.getIbmProdIdDscr().compareTo(partsPricingConfiguration.getIbmProdIdDscr());
		}
	}

	  @Override
	public int compareTo(Object o) {
	        if (o == null) {
				return 0;
			} else {
				return compareTo((PartsPricingConfiguration) o);
			}
	    }


	@Override
	public String getProvisioningId() {
		return provisioningId;
	}
	@Override
	public boolean isProvisioningCopied() {
		return isProvisioningCopied;
	}

    @Override
	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	@Override
	public void setWebQuoteNum(String webQuoteNum)  throws TopazException{
		this.webQuoteNum = webQuoteNum;
	}
	@Override
	public String getConfigrtnId() {
		return configrtnId;
	}
	@Override
	public void setConfigrtnId(String configrtnId) throws TopazException{
		this.configrtnId = configrtnId;
	}
	@Override
	public String getConfigrtrConfigrtnId() {
		return configrtrConfigrtnId;
	}
	@Override
	public void setConfigrtrConfigrtnId(String configrtrConfigrtnId)throws TopazException{
		this.configrtrConfigrtnId = configrtrConfigrtnId;
	}
	@Override
	public Integer getProvisngDays() {
		return provisngDays != null ? provisngDays : getProvisngDaysDefault();
	}
	@Override
	public void setProvisngDays(Integer provisngDays) throws TopazException{
		this.provisngDays = provisngDays;
	}
	@Override
	public String getUserID() {
		return userID;
	}
	@Override
	public void setUserID(String userID) throws TopazException{
		this.userID = userID;
	}
	@Override
	public String getIbmProdId() {
		return ibmProdId;
	}
	@Override
	public String getIbmProdIdDscr() {
		return ibmProdIdDscr;
	}

	@Override
	public String getConfigrtnActionCode() {
		return configrtnActionCode;
	}



	@Override
	public List<String> getChargeAgreementList() {
		return chargeAgreementList;
	}

	@Override
	public void setChargeAgreementList(List<String> chargeAgreementList) {
		this.chargeAgreementList = chargeAgreementList;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	@Override
	public String getCotermConfigrtnId() {
		return cotermConfigrtnId;
	}

	@Override
	public Map<String, List> getCotermMap() {
		return cotermMap;
	}

	@Override
	public void setCotermMap(Map<String, List> cotermMap) {
		this.cotermMap = cotermMap;
	}

	//for add-ons/trade-ups and FCT TO PA Finalization, automatically co-termed
	@Override
	public boolean isAddOnTradeUp() {
		return PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(this.configrtnActionCode) || PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(this.configrtnActionCode) ;
	}
	//new configuration (no reference to existing charge agreement) - co-term radio-button "No" is selected
	@Override
	public boolean isNewNoCaNoCoterm() {
		return PartPriceConstants.ConfigrtnActionCode.NEW_NCT.equals(this.configrtnActionCode);
	}
	//new configuration for an existing charge agreement - co-term radio-button "Yes" is selected
	@Override
	public boolean isNewCaCoterm() {
		return PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(this.configrtnActionCode);
	}
	//new configuration for an existing charge agreement - co-term radio-button "No" is selected
	@Override
	public boolean isNewCaNoCoterm() {
		return PartPriceConstants.ConfigrtnActionCode.NEW_CA_NCT.equals(this.configrtnActionCode);
	}

	@Override
	public Date getConfigrtnModDate() {
		return configrtnModDate;
	}

	@Override
	public Integer getProvisngDaysDefault() {
		return provisngDaysDefault;
	}

	@Override
	public Double getIncreaseBidTCV() {
		return increaseBidTCV;
	}

	@Override
	public void setIncreaseBidTCV(Double increaseBidTCV) {
		this.increaseBidTCV = increaseBidTCV;
	}

	@Override
	public Double getUnusedBidTCV() {
		return unusedBidTCV;
	}

	@Override
	public void setUnusedBidTCV(Double unusedBidTCV) {
		this.unusedBidTCV = unusedBidTCV;
	}

	@Override
	public boolean isConfigrtnOvrrdn() {
		return configrtnOvrrdn;
	}

	@Override
	public boolean isAllowOvrrd() {
		return allowOvrrd;
	}

	@Override
	public String getConfigrtnErrCode() {
		return configrtnErrCode;
	}
    @Override
	public String getProdBrandCode() {
        return prodBrandCode;
    }

    @Override
	public String getProdBrandCodeDscr() {
        return prodBrandCodeDscr;
    }

	@Override
	public ServiceDateModType getServiceDateModType() {
		return serviceDateModType;
	}



	@Override
	public Date getServiceDate() {
		return serviceDate;
	}



	@Override
	public boolean isTermExtension() {
		return termExtension;
	}

	public boolean isConfigEntireExtended(){
		return configEntireExtended;
	}



}
