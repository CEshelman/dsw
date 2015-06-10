package com.ibm.dsw.quote.submittedquote.domain;

import com.ibm.dsw.quote.common.domain.Domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpecialBidApprvr_Imp<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public abstract class SpecialBidApprvr_Impl implements SpecialBidApprvr {

    private boolean _isModefied = false;
    
    private int _mode = Domain.DOMAIN_MODE_PO;
    
    public String webQuoteNum;

    public String specialBidApprGrp;

    public int specialBidApprLvl;
    
    public String apprvrEmail;

    public String apprvrAction;
    
    public int rdyToOrder;
    
    public int isPGS;
    
    public String getReturnReason() {
		return returnReason;
	}

	public void setReturnReason(String returnReason) throws Exception {
		this.returnReason = returnReason;
	}
	public String predecessorEmail;
    
    public String applierEmail;
    
    public String firstName;
    
    public String lastName;
    
    public String lastActApprEmail;

    public int supersedeApprvFlag;
    
    public String returnReason;
    
    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }
    
    public String getWebQuoteNum() {
        return webQuoteNum;
    }

    public String getSpecialBidApprGrp() {
        return specialBidApprGrp;
    }

    public String getApprvrEmail() {
        return apprvrEmail;
    }

    public String getApprvrAction() {
        return apprvrAction;
    }
    
    public int getSpecialBidApprLvl() {
        return specialBidApprLvl;
    }
    
    public void setPredecessorEmail(String predecessorEmail){
        this.predecessorEmail = predecessorEmail;
    }
    
    public String getPredecessorEmail(){
        return this.predecessorEmail;
    }

    public void setApplierEmail(String applierEmail) throws Exception {
        this.applierEmail = applierEmail;
    }
    
    public String getApplierEmail() {
        return this.applierEmail;
    }
    
    
    public void setApprvrAction(String apprvrAction) throws Exception {
        this.apprvrAction = apprvrAction;
    }
    
    public void setApprvrEmail(String apprvrEmail) throws Exception {
        this.apprvrEmail = apprvrEmail;
    }
    
    public void setSpecialBidApprGrp(String specialBidApprGrp) throws Exception {
        this.specialBidApprGrp = specialBidApprGrp;
    }
    
    public void setSpecialBidApprLvl(int specialBidApprLvl) throws Exception {
        this.specialBidApprLvl = specialBidApprLvl;
    }
    
    public void setWebQuoteNum(String webQuoteNum) throws Exception{
        this.webQuoteNum = webQuoteNum;
    }
    
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.Domain#setMode(int)
     */
    public void setMode(int mode)  throws Exception{
        if(mode == Domain.DOMAIN_MODE_PO || mode == Domain.DOMAIN_MODE_VO){
            this._mode = mode;
        }
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.Domain#getMode()
     */
    public int getMode() {
        return _mode;
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.Domain#isModified()
     */
    public boolean isModified() {
        return _isModefied;
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.Domain#markAsModified()
     */
    public void markAsModified() throws Exception{
        this._isModefied = true;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("webQuoteNum = ").append(webQuoteNum).append("\n");
        buffer.append("specialBidApprGrp = ").append(specialBidApprGrp).append("\n");
        buffer.append("specialBidApprLvl = ").append(specialBidApprLvl).append("\n");
        buffer.append("apprvrEmail = ").append(apprvrEmail).append("\n");
        buffer.append("apprvrAction = ").append(apprvrAction).append("\n");
        buffer.append("supersedeApprvFlag = ").append(supersedeApprvFlag).append("\n");
        return buffer.toString();
    }

    public String getLastActApprEmail() {
        return lastActApprEmail;
    }
	/**
	 * @return Returns the supersedeApprvFlag.
	 */
	public int getSupersedeApprvFlag() {
		return supersedeApprvFlag;
	}
	/**
	 * @param supersedeApprvFlag The supersedeApprvFlag to set.
	 */
	public void setSupersedeApprvFlag(int supersedeApprvFlag) {
		this.supersedeApprvFlag = supersedeApprvFlag;
	}

	public int getRdyToOrder() {
		return rdyToOrder;
	}
	public void setRdyToOrder(int rdyToOrder) {
		this.rdyToOrder = rdyToOrder;
	}
	
	public void setIsPGS(int isPGS) {
		this.isPGS = isPGS;
	}
	
	public int getIsPGS() {
		return isPGS;
	}
	
}
