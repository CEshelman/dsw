package com.ibm.dsw.quote.submittedquote.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmittedQuoteAccess<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-6-26
 */

public class SubmittedQuoteAccess implements Serializable {
    
    private boolean noStatusInOneHour;
    private boolean noStatusOverOneHour;
    private boolean noCustEnroll;
    private boolean hasOrdered;
    private Date sbApprovedDate;
    private boolean accessBlockStatus;
    private String cancelledBy;

    /**
     * 
     */
    public SubmittedQuoteAccess() {
    }

    /**
     * @return Returns the hasOrdered.
     */
    public boolean isHasOrdered() {
        return hasOrdered;
    }
    /**
     * @param hasOrdered The hasOrdered to set.
     */
    public void setHasOrdered(boolean hasOrdered) {
        this.hasOrdered = hasOrdered;
    }
    /**
     * @return Returns the noCustEnroll.
     */
    public boolean isNoCustEnroll() {
        return noCustEnroll;
    }
    /**
     * @param noCustEnroll The noCustEnroll to set.
     */
    public void setNoCustEnroll(boolean noCustEnroll) {
        this.noCustEnroll = noCustEnroll;
    }
    /**
     * @return Returns the noStatusInOneHour.
     */
    public boolean isNoStatusInOneHour() {
        return noStatusInOneHour;
    }
    /**
     * @param noStatusInOneHour The noStatusInOneHour to set.
     */
    public void setNoStatusInOneHour(boolean noStatusInOneHour) {
        this.noStatusInOneHour = noStatusInOneHour;
    }
    /**
     * @return Returns the noStatusOverOneHour.
     */
    public boolean isNoStatusOverOneHour() {
        return noStatusOverOneHour;
    }
    /**
     * @param noStatusOverOneHour The noStatusOverOneHour to set.
     */
    public void setNoStatusOverOneHour(boolean noStatusOverOneHour) {
        this.noStatusOverOneHour = noStatusOverOneHour;
    }
    
	/**
	 * @return Returns the sbApprovedDate.
	 */
	public Date getSbApprovedDate() {
		return sbApprovedDate;
	}
	/**
	 * @param sbApprovedDate The sbApprovedDate to set.
	 */
	public void setSbApprovedDate(Date sbApprovedDate) {
		this.sbApprovedDate = sbApprovedDate;
	}
    /**
     * @return Returns the accessBlockStatus.
     */
    public boolean isAccessBlockStatus() {
        return accessBlockStatus;
    }
    /**
     * @param accessBlockStatus The accessBlockStatus to set.
     */
    public void setAccessBlockStatus(boolean accessBlockStatus) {
        this.accessBlockStatus = accessBlockStatus;
    }
    /**
     * @return Returns the cancelledBy.
     */
    public String getCancelledBy() {
        return cancelledBy;
    }
    /**
     * @param cancelledBy The cancelledBy to set.
     */
    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("noStatusInOneHour = ").append(noStatusInOneHour).append("\n");  
        buffer.append("noStatusOverOneHour = ").append(noStatusOverOneHour).append("\n");  
        buffer.append("noCustEnroll = ").append(noCustEnroll).append("\n");  
        buffer.append("hasOrdered = ").append(hasOrdered).append("\n");  
        buffer.append("sbApprovedDate = ").append(sbApprovedDate).append("\n"); 
        buffer.append("accessBlockStatus = ").append(accessBlockStatus).append("\n"); 
        buffer.append("cancelledBy = ").append(cancelledBy).append("\n"); 
        return buffer.toString();
    }
}
