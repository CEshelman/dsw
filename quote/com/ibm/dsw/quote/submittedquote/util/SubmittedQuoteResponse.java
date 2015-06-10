
package com.ibm.dsw.quote.submittedquote.util;
/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jun 7, 2007
 */

public class SubmittedQuoteResponse {
    
    private boolean isUpdateLineItemFailed = false;
    private boolean isWebServiceInputInvalid = false;
    private boolean isPriceEngineFailed = false;
    private boolean isBluePageUnavailable = false;

    private boolean isPriceCalculatedWhenUpdateDate = false;
    
    private boolean isPriceIncreaseFailed = false;
    /**
     * @return Returns the isPriceEngineFailed.
     */
    public boolean isPriceEngineFailed() {
        return isPriceEngineFailed;
    }
    /**
     * @param isPriceEngineFailed The isPriceEngineFailed to set.
     */
    public void setPriceEngineFailed(boolean isPriceEngineFailed) {
        this.isPriceEngineFailed = isPriceEngineFailed;
    }
    /**
     * @return Returns the isUpdateLineItemFailed.
     */
    public boolean isUpdateLineItemFailed() {
        return isUpdateLineItemFailed;
    }
    /**
     * @param isUpdateLineItemFailed The isUpdateLineItemFailed to set.
     */
    public void setUpdateLineItemFailed(boolean isUpdateLineItemFailed) {
        this.isUpdateLineItemFailed = isUpdateLineItemFailed;
    }
    /**
     * @return Returns the isBluePageUnavailable.
     */
    public boolean isBluePageUnavailable() {
        return isBluePageUnavailable;
    }
    /**
     * @param isBluePageUnavailable The isBluePageUnavailable to set.
     */
    public void setBluePageUnavailable(boolean isBluePageUnavailable) {
        this.isBluePageUnavailable = isBluePageUnavailable;
    }
    
    public boolean isWebServiceInputInvalid() {
        return isWebServiceInputInvalid;
    }
    
    public void setWebServiceInputInvalid(boolean isWebServiceInputInvalid) {
        this.isWebServiceInputInvalid = isWebServiceInputInvalid;
    }
    /**
     * @return Returns the isPriceIncreaseSucceed.
     */
    public boolean isPriceIncreaseFailed() {
        return isPriceIncreaseFailed;
    }
    /**
     * @param isPriceIncreaseSucceed The isPriceIncreaseSucceed to set.
     */
    public void setPriceIncreaseFailed(boolean isPriceIncreaseFailed) {
        this.isPriceIncreaseFailed = isPriceIncreaseFailed;
    }
}
