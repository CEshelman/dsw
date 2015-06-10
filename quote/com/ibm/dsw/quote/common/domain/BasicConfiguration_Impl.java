/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Dec 12, 2013
 */
package com.ibm.dsw.quote.common.domain;

import com.ibm.dsw.quote.common.config.PartPriceConstants;

/**
 * DOC class global comment. Detailled comment
 */
public abstract class BasicConfiguration_Impl implements BasicConfiguration {

    /**
     * 
     */
    private static final long serialVersionUID = 5871148422738176295L;

    public String webQuoteNum;

    public String configrtnId;

    public String configrtnActionCode;

    public String cotermConfigrtnId;
    
    public Double increaseBidTCV;
    public Double unusedBidTCV;

    public int compareTo(BasicConfiguration basicConfiguration) {
        if (this.getConfigrtnId() == null || basicConfiguration == null || basicConfiguration.getConfigrtnId() == null)
            return -1;
        else
            return this.getConfigrtnId().compareTo(basicConfiguration.getConfigrtnId());
    }

    public int compareTo(Object o) {
        if (o == null) {
            return 0;
        } else {
            return compareTo((MonthlySoftwareConfiguration) o);
        }
    }

    /**
     * Getter for webQuoteNum.
     * 
     * @return the webQuoteNum
     */
    public String getWebQuoteNum() {
        return this.webQuoteNum;
    }


    /**
     * Getter for configrtnId.
     * 
     * @return the configrtnId
     */
    public String getConfigrtnId() {
        return this.configrtnId;
    }


    /**
     * Getter for configrtnActionCode.
     * 
     * @return the configrtnActionCode
     */
    public String getConfigrtnActionCode() {
        return this.configrtnActionCode;
    }


    /**
     * Getter for cotermConfigrtnId.
     * 
     * @return the cotermConfigrtnId
     */
    public String getCotermConfigrtnId() {
        return this.cotermConfigrtnId;
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

}
