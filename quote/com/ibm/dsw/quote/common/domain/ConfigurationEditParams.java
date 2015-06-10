package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

public class ConfigurationEditParams implements Serializable {
	String editIbmProdId;
	String editConfigrtnId;
	String editOrgConfigrtnId;
	String editConfigrtrConfigrtnId;
	String editTradeFlag;
	
	String editConfigurationFlag;
	String overrideFlag;
	
	public String getEditIbmProdId() {
		return editIbmProdId;
	}
	public void setEditIbmProdId(String editIbmProdId) {
		this.editIbmProdId = editIbmProdId;
	}
	public String getEditConfigrtnId() {
		return editConfigrtnId;
	}
	public void setEditConfigrtnId(String editConfigrtnId) {
		this.editConfigrtnId = editConfigrtnId;
	}
	public String getEditConfigrtrConfigrtnId() {
		return editConfigrtrConfigrtnId;
	}
	public void setEditConfigrtrConfigrtnId(String editConfigrtrConfigrtnId) {
		this.editConfigrtrConfigrtnId = editConfigrtrConfigrtnId;
	}
	public String getEditTradeFlag() {
		return editTradeFlag;
	}
	public void setEditTradeFlag(String editTradeFlag) {
		this.editTradeFlag = editTradeFlag;
	}
	public String getEditConfigurationFlag() {
		return editConfigurationFlag;
	}
	public void setEditConfigurationFlag(String editConfigurationFlag) {
		this.editConfigurationFlag = editConfigurationFlag;
	}
	public String getOverrideFlag() {
		return overrideFlag;
	}
	public void setOverrideFlag(String overrideFlag) {
		this.overrideFlag = overrideFlag;
	}
	public String getEditOrgConfigrtnId() {
		return editOrgConfigrtnId;
	}
	public void setEditOrgConfigrtnId(String editOrgConfigrtnId) {
		this.editOrgConfigrtnId = editOrgConfigrtnId;
	}
	
}
