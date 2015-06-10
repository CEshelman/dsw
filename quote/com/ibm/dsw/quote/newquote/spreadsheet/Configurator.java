package com.ibm.dsw.quote.newquote.spreadsheet;

import java.sql.Date;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>Configurator<code> class used by import and export functions.
 *    
 * @author: whlihui@cn.ibm.com
 * 
 * Creation date: 2012-6-20
 */
public class Configurator {

	private String configurationId = "";
	
    private String configrtrConfigrtnId = "";
    
    private String refDocNum = "";
    
    private String errorCode = "";
    
    private String configrtnAction = "";
    
    private Date endDate = null;
    
    private String coTermToConfigrtnId = "";
    
    private String overrideFlag = "";
    
    private String provisioningId = "";

	public String getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(String configurationId) {
		this.configurationId = configurationId;
	}

	public String getConfigrtrConfigrtnId() {
		return configrtrConfigrtnId;
	}

	public void setConfigrtrConfigrtnId(String configrtrConfigrtnId) {
		this.configrtrConfigrtnId = configrtrConfigrtnId;
	}

	public String getRefDocNum() {
		return refDocNum;
	}

	public void setRefDocNum(String refDocNum) {
		this.refDocNum = refDocNum;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getConfigrtnAction() {
		return configrtnAction;
	}

	public void setConfigrtnAction(String configrtnAction) {
		this.configrtnAction = configrtnAction;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCoTermToConfigrtnId() {
		return coTermToConfigrtnId;
	}

	public void setCoTermToConfigrtnId(String coTermToConfigrtnId) {
		this.coTermToConfigrtnId = coTermToConfigrtnId;
	}

	public String isOverrideFlag() {
		return overrideFlag;
	}

	public void setOverrideFlag(String overrideFlag) {
		this.overrideFlag = overrideFlag;
	}

	public String getProvisioningId() {
		return provisioningId;
	}

	public void setProvisioningId(String provisioningId) {
		this.provisioningId = provisioningId;
	}
}
