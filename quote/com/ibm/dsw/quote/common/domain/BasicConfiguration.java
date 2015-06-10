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

import java.io.Serializable;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * DOC class global comment. Detailled comment
 */
@SuppressWarnings("rawtypes")
public interface BasicConfiguration extends Comparable, Serializable, Cloneable {

    /**
     * Getter for webQuoteNum.
     * 
     * @return the webQuoteNum
     */
    public abstract String getWebQuoteNum();

    /**
     * Sets the webQuoteNum.
     * 
     * @param webQuoteNum the webQuoteNum to set
     */
    public abstract void setWebQuoteNum(String webQuoteNum) throws TopazException;

    /**
     * Getter for configrtnId.
     * 
     * @return the configrtnId
     */
    public abstract String getConfigrtnId();

    /**
     * Sets the configrtnId.
     * 
     * @param configrtnId the configrtnId to set
     */
    public abstract void setConfigrtnId(String configrtnId) throws TopazException;

    /**
     * Getter for configrtnActionCode.
     * 
     * @return the configrtnActionCode
     */
    public abstract String getConfigrtnActionCode();

    /**
     * Sets the configrtnActionCode.
     * 
     * @param configrtnActionCode the configrtnActionCode to set
     */
    public abstract void setConfigrtnActionCode(String configrtnActionCode) throws TopazException;

    /**
     * Getter for cotermConfigrtnId.
     * 
     * @return the cotermConfigrtnId
     */
    public abstract String getCotermConfigrtnId();

    /**
     * Sets the cotermConfigrtnId.
     * 
     * @param cotermConfigrtnId the cotermConfigrtnId to set
     */
    public abstract void setCotermConfigrtnId(String cotermConfigrtnId) throws TopazException;

	boolean isAddOnTradeUp();

	boolean isNewNoCaNoCoterm();

	boolean isNewCaCoterm();

	boolean isNewCaNoCoterm();
	
	public Double getIncreaseBidTCV();
	public void setIncreaseBidTCV(Double increaseBidTCV);
	public Double getUnusedBidTCV();
	public void setUnusedBidTCV(Double unusedBidTCV);


}
