package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.ibm.ead4j.topaz.exception.TopazException;

public interface PartsPricingConfiguration  extends Comparable, Serializable{

	public void delete() throws TopazException;
	public String getWebQuoteNum();
	public void setWebQuoteNum(String webQuoteNum)  throws TopazException;
	public String getConfigrtnId();
	public void setConfigrtnId(String configrtnId)  throws TopazException;
	public String getConfigrtrConfigrtnId();
	public void setConfigrtrConfigrtnId(String configrtrConfigrtnId)  throws TopazException;
	public Integer getProvisngDays();
	public void setProvisngDays(Integer provisngDays)  throws TopazException;
	public String getUserID();
	public void setUserID(String userID)  throws TopazException;
	public String getIbmProdId();
	public String getIbmProdIdDscr();
	public String getConfigrtnActionCode();
	public void setConfigrtnActionCode(String configrtnActionCode)  throws TopazException;
	public List<String> getChargeAgreementList();
	public void setChargeAgreementList(List<String> chargeAgreementList);
	public Date getEndDate();
	public void setEndDate(Date endDate) throws TopazException;
	public String getCotermConfigrtnId();
	public void setCotermConfigrtnId(String cotermConfigrtnId) throws TopazException;
	public Map<String, List> getCotermMap();
	public void setCotermMap(Map<String, List> cotermMap);
	public boolean isAddOnTradeUp();
	public boolean isNewNoCaNoCoterm();
	public boolean isNewCaCoterm();
	public boolean isNewCaNoCoterm();
	public Date getConfigrtnModDate();
	public void setConfigrtnModDate(Date configrtnModDate) throws TopazException;
	public Integer getProvisngDaysDefault();
	public Double getIncreaseBidTCV();
	public void setIncreaseBidTCV(Double increaseBidTCV);
	public Double getUnusedBidTCV();
	public void setUnusedBidTCV(Double unusedBidTCV);
	public boolean isConfigrtnOvrrdn();
	public boolean isAllowOvrrd();
	public String getConfigrtnErrCode();
	public String getProvisioningId();
	public void setProvisioningId(String provisioningId) throws TopazException;
	public boolean isProvisioningCopied();
	public String getProdBrandCode();
	public String getProdBrandCodeDscr();
	public ServiceDateModType getServiceDateModType();

	public void setServiceDateModType(ServiceDateModType serviceDateModType) throws TopazException;
	public Date getServiceDate();

	public void setServiceDate(Date serviceDate) throws TopazException;
	public boolean isTermExtension();

	public void setTermExtension(boolean termExtension) throws TopazException;

	public boolean isConfigEntireExtended();

    /**
     * DOC Comment method "setConfigEntireExtended".
     * 
     * @param configEntireExtended
     * @throws TopazException
     */
    void setConfigEntireExtended(boolean configEntireExtended) throws TopazException;


}

