
package com.ibm.dsw.quote.draftquote.util.date;

import java.sql.Date;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 25, 2007
 */

public class DateResult {
    
    String partNum;
    int seqNum;
    Date stdStartDate;
    Date stdEndDate;
    Date ovrdStartDate;
    Date ovrdEndDate;
    boolean ovrdStartDateFlag;
    boolean ovrdEndDateFlag;
    
    
	/**
	 * @param ovrdEndDate The ovrdEndDate to set.
	 */
	public void setOvrdEndDate(Date ovrdEndDate) {
		this.ovrdEndDate = ovrdEndDate;
	}
	/**
	 * @param ovrdEndDateFlag The ovrdEndDateFlag to set.
	 */
	public void setOvrdEndDateFlag(boolean ovrdEndDateFlag) {
		this.ovrdEndDateFlag = ovrdEndDateFlag;
	}
	/**
	 * @param ovrdStartDate The ovrdStartDate to set.
	 */
	public void setOvrdStartDate(Date ovrdStartDate) {
		this.ovrdStartDate = ovrdStartDate;
	}
	/**
	 * @param ovrdStartDateFlag The ovrdStartDateFlag to set.
	 */
	public void setOvrdStartDateFlag(boolean ovrdStartDateFlag) {
		this.ovrdStartDateFlag = ovrdStartDateFlag;
	}
	/**
	 * @param partNum The partNum to set.
	 */
	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}
	/**
	 * @param seqNum The seqNum to set.
	 */
	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}
	/**
	 * @param stdEndDate The stdEndDate to set.
	 */
	public void setStdEndDate(Date stdEndDate) {
		this.stdEndDate = stdEndDate;
	}
	/**
	 * @param stdStartDate The stdStartDate to set.
	 */
	public void setStdStartDate(Date stdStartDate) {
		this.stdStartDate = stdStartDate;
	}
    public DateResult(String partNum,int seqNum){
        this.partNum = partNum;
        this.seqNum = seqNum;
    }
    /**
     * @return Returns the endDate.
     */
    public Date getStdEndDate() {
        return stdEndDate;
    }
    /**
     * @return Returns the partNum.
     */
    public String getPartNum() {
        return partNum;
    }
    /**
     * @return Returns the seqNum.
     */
    public int getSeqNum() {
        return seqNum;
    }
    /**
     * @return Returns the startDate.
     */
    public Date getStdStartDate() {
        return stdStartDate;
    }
    /**
     * @return Returns the ovrdEndDateFlag.
     */
    public boolean isOvrdEndDateFlag() {
        return ovrdEndDateFlag;
    }
    /**
     * @return Returns the ovrdStartDate.
     */
    public Date getOvrdStartDate() {
        return ovrdStartDate;
    }
    /**
     * @return Returns the ovrdStartDateFlag.
     */
    public boolean isOvrdStartDateFlag() {
        return ovrdStartDateFlag;
    }
	/**
	 * @return Returns the ovrdEndDate.
	 */
	public Date getOvrdEndDate() {
		return ovrdEndDate;
	}
}
