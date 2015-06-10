/*
 * Created on Apr 16, 2007
 */
package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

/**
 * @author Lavanya
 */

public class QuoteAccess implements Serializable {
    
    private boolean canEditRQ;               
    private boolean canEditRQSalesInfo;    
    private boolean canBeAddedToSQ;      
    private boolean canUpdateRQStatus;   
    private boolean canOrderRQ;
    private boolean canCreateRQSpeclBid;
    private boolean isSalesRep;
    private boolean isQuotingRep;
    private boolean canEditGPExprdRQ;

    /**
     * @return Returns the canBeAddedToSQ.
     */
    public boolean isCanBeAddedToSQ() {
        return canBeAddedToSQ;
    }
    /**
     * @param canBeAddedToSQ The canBeAddedToSQ to set.
     */
    public void setCanBeAddedToSQ(boolean canBeAddedToSQ) {
        this.canBeAddedToSQ = canBeAddedToSQ;
    }
    /**
     * @return Returns the canEditRQ.
     */
    public boolean isCanEditRQ() {
        return canEditRQ;
    }
    /**
     * @param canEditRQ The canEditRQ to set.
     */
    public void setCanEditRQ(boolean canEditRQ) {
        this.canEditRQ = canEditRQ;
    }
    /**
     * @return Returns the canEditRQSalesInfo.
     */
    public boolean isCanEditRQSalesInfo() {
        return canEditRQSalesInfo;
    }
    /**
     * @param canEditRQSalesInfo The canEditRQSalesInfo to set.
     */
    public void setCanEditRQSalesInfo(boolean canEditRQSalesInfo) {
        this.canEditRQSalesInfo = canEditRQSalesInfo;
    }
    /**
     * @return Returns the canUpdateRQStatus.
     */
    public boolean isCanUpdateRQStatus() {
        return canUpdateRQStatus;
    }
    /**
     * @param canUpdateRQStatus The canUpdateRQStatus to set.
     */
    public void setCanUpdateRQStatus(boolean canUpdateRQStatus) {
        this.canUpdateRQStatus = canUpdateRQStatus;
    }
    /**
     * @return Returns the canOrderRQ.
     */
    public boolean isCanOrderRQ() {
        return canOrderRQ;
    }
    /**
     * @param canOrderRQ The canOrderRQ to set.
     */
    public void setCanOrderRQ(boolean canOrderRQ) {
        this.canOrderRQ = canOrderRQ;
    }
    /**
     * @return Returns the isQuotingRep.
     */
    public boolean isQuotingRep() {
        return isQuotingRep;
    }
    /**
     * @param isQuotingRep The isQuotingRep to set.
     */
    public void setQuotingRep(boolean isQuotingRep) {
        this.isQuotingRep = isQuotingRep;
    }
    /**
     * @return Returns the isSalesRep.
     */
    public boolean isSalesRep() {
        return isSalesRep;
    }
    /**
     * @param isSalesRep The isSalesRep to set.
     */
    public void setSalesRep(boolean isSalesRep) {
        this.isSalesRep = isSalesRep;
    }
    
    public boolean isCanCreateRQSpeclBid() {
        return canCreateRQSpeclBid;
    }
    
    public void setCanCreateRQSpeclBid(boolean canCreateRQSpeclBid) {
        this.canCreateRQSpeclBid = canCreateRQSpeclBid;
    }
    /**
     * @return Returns the canEditGPExprdRQ.
     */
    public boolean isCanEditGPExprdRQ() {
        return canEditGPExprdRQ;
    }
    /**
     * @param canEditGPExprdRQ The canEditGPExprdRQ to set.
     */
    public void setCanEditGPExprdRQ(boolean canEditGPExprdRQ) {
        this.canEditGPExprdRQ = canEditGPExprdRQ;
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("canEditRQ = ").append(canEditRQ).append("\n");  
        buffer.append("canEditRQSalesInfo = ").append(canEditRQSalesInfo).append("\n");  
        buffer.append("canBeAddedToSQ = ").append(canBeAddedToSQ).append("\n");  
        buffer.append("canUpdateRQStatus = ").append(canUpdateRQStatus).append("\n");  
        buffer.append("canOrderRQ = ").append(canOrderRQ).append("\n");  
        buffer.append("canCreateRQSpeclBid = ").append(canCreateRQSpeclBid).append("\n");
        buffer.append("isSalesRep = ").append(isSalesRep).append("\n");
        buffer.append("isQuotingRep = ").append(isQuotingRep).append("\n");
        return buffer.toString();
    }
}
