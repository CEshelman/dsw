package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.config.PartPriceConstants;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * The <code>DeployModel</code> class is Quote Line Item Domain.
 * 
 * 
 * @author <a href="liaoqc@cn.ibm.com">Jack </a> <br/>
 * 
 * Creation date: 2013-11-29
 * 
 */
public class DeployModel implements Serializable {

    private static final long serialVersionUID = 1L;
    // -1 : means the invalidated value
    private int deployModelOption=-1; // DEPLOYMT_OPT

    private String deployModelId; // DEPLOYMT_ID

    private boolean deployModelInvalid; // DEPLOYMT_ID_INVD

    private Integer serialNumWarningFlag; // APPLNC_SERIAL_NUM_WARNG_FLAG

    public DeployModel() {

    }

    /**
     * DOC DeployModel constructor comment.
     * @param deployModelOption
     * @param deployModelId
     * @param deployModelInvalid
     * @param serialNumWarningFlag
     */
    public DeployModel(int deployModelOption, String deployModelId, boolean deployModelInvalid, Integer serialNumWarningFlag) {
        super();
        this.deployModelOption = deployModelOption;
        this.deployModelId = deployModelId;
        this.deployModelInvalid = deployModelInvalid;
        this.serialNumWarningFlag = serialNumWarningFlag;
    }


    /**
     * Getter for deployModelOption.
     * @return the deployModelOption
     */
    public int getDeployModelOption() {
        return this.deployModelOption;
    }

    
    /**
     * Sets the deployModelOption.
     * @param deployModelOption the deployModelOption to set
     */
    public void setDeployModelOption(int deployModelOption) {
        this.deployModelOption = deployModelOption;
    }

    /**
     * Getter for deployModelId.
     * 
     * @return the deployModelId
     */
    public String getDeployModelId() {
        return StringUtils.trimToNull(this.deployModelId);
    }

    /**
     * Sets the deployModelId.
     * 
     * @param deployModelId the deployModelId to set
     */
    public void setDeployModelId(String deployModelId) {
        this.deployModelId = deployModelId;
    }


    
    /**
     * Getter for deployModelInvalid.
     * 
     * @return the deployModelInvalid
     */
    public boolean isDeployModelInvalid() {
    	if (!PartPriceConstants.DEPLOYMENT_NOT_ON_QUOTE.equals(getDeployModelOption())){
    		this.deployModelInvalid = true;
    	}
        return this.deployModelInvalid;
    }


    
    /**
     * Sets the deployModelInvalid.
     * 
     * @param deployModelInvalid the deployModelInvalid to set
     */
    public void setDeployModelInvalid(boolean deployModelInvalid) {
        this.deployModelInvalid = deployModelInvalid;
    }


    
    /**
     * Getter for serialNumWarningFlag.
     * 
     * @return the serialNumWarningFlag
     */
    public Integer getSerialNumWarningFlag() {
        return this.serialNumWarningFlag;
    }


    
    /**
     * Sets the serialNumWarningFlag.
     * 
     * @param serialNumWarningFlag the serialNumWarningFlag to set
     */
    public void setSerialNumWarningFlag(Integer serialNumWarningFlag) {
        this.serialNumWarningFlag = serialNumWarningFlag;
    }


    @Override
	public String toString() {
		return "DeployModel [deployModelOption=" + deployModelOption
				+ ", deployModelId=" + deployModelId + ", deployModelInvalid="
				+ deployModelInvalid + ", serialNumWarningFlag="
				+ serialNumWarningFlag + "]";
	}

}
