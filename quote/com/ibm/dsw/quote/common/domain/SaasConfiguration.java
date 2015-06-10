package com.ibm.dsw.quote.common.domain;

import java.sql.Date;
import org.apache.commons.lang.StringUtils;
public class SaasConfiguration {
	// need to add all fields of table ebiz1.web_quote_configrtn table
	private String webQuoteNum;
	private String configrtnId;
	private String configrtrConfigrtnId;
	private Integer estdProvisngDays;
	private Date configrtnModDate;
	private String configrtnErrCode;
	private String configrtnActionCode;
	private Date endDate;
	private String cotermConfigrtnId;
	private Date addDate;
	private String addBy;
	private Date modeDate;
	private String modeBy;
	private Integer  configrtnOvrrdn;
	private String configrtnType;
	private String provisioningId;
	private Integer  provisioningCopied;
	private String serviceDateModType;
	private Date serviceDate;
	private String termExtFlag;
	private Integer configEntireExtended;
	private String tcvNextChange;
	//the columns below aren't existent in  EBIZ.WEB_QUOTE_CONFIGRTN table 
	private String userId;
	private String refDocNum;
	private String importFlag;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRefDocNum() {
		return refDocNum;
	}
	public void setRefDocNum(String refDocNum) {
		this.refDocNum = refDocNum;
	}
	public String getImportFlag() {
		return importFlag;
	}
	public void setImportFlag(String importFlag) {
		this.importFlag = importFlag;
	}
	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}
	public String getConfigrtnId() {
		return configrtnId;
	}
	public void setConfigrtnId(String configrtnId) {
		this.configrtnId = configrtnId;
	}
	public String getConfigrtrConfigrtnId() {
		return configrtrConfigrtnId;
	}
	public void setConfigrtrConfigrtnId(String configrtrConfigrtnId) {
		this.configrtrConfigrtnId = configrtrConfigrtnId;
	}
	public Integer getEstdProvisngDays() {
		return estdProvisngDays;
	}
	public void setEstdProvisngDays(Integer estdProvisngDays) {
		this.estdProvisngDays = estdProvisngDays;
	}
	public Date getConfigrtnModDate() {
		return configrtnModDate;
	}
	public void setConfigrtnModDate(Date configrtnModDate) {
		this.configrtnModDate = configrtnModDate;
	}
	public String getConfigrtnErrCode() {
		return configrtnErrCode;
	}
	public void setConfigrtnErrCode(String configrtnErrCode) {
		this.configrtnErrCode = configrtnErrCode;
	}
	public String getConfigrtnActionCode() {
		return configrtnActionCode;
	}
	public void setConfigrtnActionCode(String configrtnActionCode) {
		this.configrtnActionCode = configrtnActionCode;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getCotermConfigrtnId() {
		return cotermConfigrtnId;
	}
	public void setCotermConfigrtnId(String cotermConfigrtnId) {
		this.cotermConfigrtnId = cotermConfigrtnId;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	public String getAddBy() {
		return addBy;
	}
	public void setAddBy(String addBy) {
		this.addBy = addBy;
	}
	public Date getModeDate() {
		return modeDate;
	}
	public void setModeDate(Date modeDate) {
		this.modeDate = modeDate;
	}
	public String getModeBy() {
		return modeBy;
	}
	public void setModeBy(String modeBy) {
		this.modeBy = modeBy;
	}
	public Integer getConfigrtnOvrrdn() {
		return configrtnOvrrdn;
	}
	public void setConfigrtnOvrrdn(Integer configrtnOvrrdn) {
		this.configrtnOvrrdn = configrtnOvrrdn;
	}
	public String getConfigrtnType() {
		return configrtnType;
	}
	public void setConfigrtnType(String configrtnType) {
		this.configrtnType = configrtnType;
	}
	public String getProvisioningId() {
		return provisioningId;
	}
	public void setProvisioningId(String provisioningId) {
		this.provisioningId = provisioningId;
	}
	public Integer setProvisioningCopied() {
		return provisioningCopied;
	}
	public void setProvisioningCopied(Integer provisioningCopied) {
		this.provisioningCopied = provisioningCopied;
	}
	public String getServiceDateModType() {
		return serviceDateModType;
	}

	public void setServiceDateModType(String serviceDateModType) {
		this.serviceDateModType = serviceDateModType;
	}
	public Date getServiceDate() {
		return serviceDate;
	}
	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}
	public String getTermExtFlag() {
		return termExtFlag;
	}
	public void setTermExtFlag(String termExtFlag) {
		this.termExtFlag = termExtFlag;
	}
	public Integer getConfigEntireExtended() {
		return configEntireExtended;
	}
	public void setConfigEntireExtended(Integer configEntireExtended) {
		this.configEntireExtended = configEntireExtended;
	}
	public String getTcvNextChange() {
		return tcvNextChange;
	}
	public void setTcvNextChange(String tcvNextChange) {
		this.tcvNextChange = tcvNextChange;
	}
	@Override
	public boolean equals (Object object) {
		if (object == this) return true;
		if (object instanceof SaasConfiguration) {
			SaasConfiguration another = (SaasConfiguration)object;
			if (another != null && StringUtils.equals(another.getConfigrtnId(), this.getConfigrtnId())){
				return true;
			}
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.getConfigrtnId().hashCode();
	}
}
