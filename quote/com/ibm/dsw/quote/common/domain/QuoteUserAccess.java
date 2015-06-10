package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

import com.ibm.dsw.quote.base.config.QuoteConstants;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteUserAccess<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: 2007-5-15
 */

public class QuoteUserAccess implements Serializable {
    
    private boolean isEditor;
    private boolean isFirstAppTypMember;
    private boolean isAnyAppTypMember;
    private boolean isPendingAppTypMember;
    private boolean isReviewer;
    private boolean noneApproval;
    private int pendingAppLevel;
	private int appLevel;
	private boolean canViewQuote;
    private boolean canUpdtPrtnr;
    private boolean canChangeBidExpDate;
    private boolean canChangeBidLineItemDate;
    private boolean canSupersedeAppr;
    private boolean canEditExecSummary;
    private boolean canApprove;
    private boolean isExecSummryCreatd;
    private boolean canViewExecSummry;
    private boolean canCancelApprovedBid;
   
    
     public boolean isCanApprove() {
        return isPendingAppTypMember || canSupersedeAppr;
    }
     
    public boolean isAnyAppTypMember() {
        return isAnyAppTypMember;
    }
    public void setIsAnyAppTypMember(boolean isAnyAppTypMember) {
        this.isAnyAppTypMember = isAnyAppTypMember;
    }
    public boolean isEditor() {
        return isEditor;
    }
    public void setIsEditor(boolean isEditor) {
        this.isEditor = isEditor;
    }
    public boolean isFirstAppTypMember() {
        return isFirstAppTypMember;
    }
    public void setIsFirstAppTypMember(boolean isFirstAppTypMember) {
        this.isFirstAppTypMember = isFirstAppTypMember;
    }
    public boolean isPendingAppTypMember() {
        return isPendingAppTypMember;
    }
    public void setIsPendingAppTypMember(boolean isPendingAppTypMember) {
        this.isPendingAppTypMember = isPendingAppTypMember;
    }
    public boolean isReviewer() {
        return isReviewer;
    }
    public void setIsReviewer(boolean isReviewer) {
        this.isReviewer = isReviewer;
    }
    public boolean isNoneApproval() {
        return noneApproval;
    }
    public void setNoneApproval(boolean noneApproval) {
        this.noneApproval = noneApproval;
    }
    public int getPendingAppLevel() {
        return pendingAppLevel;
    }
    public void setPendingAppLevel(int pendingAppLevel) {
        this.pendingAppLevel = pendingAppLevel;
    }
    public boolean isCanViewQuote() {
        return canViewQuote;
    }
    public void setCanViewQuote(boolean canViewQuote) {
        this.canViewQuote = canViewQuote;
    }
    public void setCanUpdtPrtnr(boolean canUpdtPrtnr) {
        this.canUpdtPrtnr = canUpdtPrtnr;
    }
    public boolean isCanUpdtPrtnr() {
        return canUpdtPrtnr;
    }
    public boolean isCanChangeBidExpDate() {
        return canChangeBidExpDate;
    }
    public void setCanChangeBidExpDate(boolean canChangeBidExpDate) {
        this.canChangeBidExpDate = canChangeBidExpDate;
    }
    public boolean isCanChangeBidLineItemDate() {
        return canChangeBidLineItemDate;
    }
    public void setCanChangeBidLineItemDate(boolean canChangeBidLineItemDate) {
        this.canChangeBidLineItemDate = canChangeBidLineItemDate;
    }
    public boolean isCanEditExecSummary(int accessLevel) {
        return canEditExecSummary && accessLevel == QuoteConstants.ACCESS_LEVEL_APPROVER;
    }
    public void setCanEditExecSummary(boolean canEditExecSummary) {
        this.canEditExecSummary = canEditExecSummary;
    }
    public boolean isCanSupersedeAppr() {
        return canSupersedeAppr;
    }
    public void setCanSupersedeAppr(boolean canSupersedeAppr) {
        this.canSupersedeAppr = canSupersedeAppr;
    }
    public boolean isExecSummryCreatd() {
        return isExecSummryCreatd;
    }
    public void setExecSummryCreatd(boolean isExecSummryCreatd) {
        this.isExecSummryCreatd = isExecSummryCreatd;
    }
    public boolean isCanViewExecSummry(int accessLevel) {
        return isExecSummryCreatd || (canEditExecSummary && accessLevel == QuoteConstants.ACCESS_LEVEL_APPROVER);
    }
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("canUpdtPrtnr = ").append(canUpdtPrtnr).append("\n"); 
        buffer.append("canViewQuote = ").append(canViewQuote).append("\n"); 
        buffer.append("isEditor = ").append(isEditor).append("\n");  
        buffer.append("isFirstAppTypMember = ").append(isFirstAppTypMember).append("\n");  
        buffer.append("isAnyAppTypMember = ").append(isAnyAppTypMember).append("\n");  
        buffer.append("isPendingAppTypMember = ").append(isPendingAppTypMember).append("\n"); 
        buffer.append("isReviewer = ").append(isReviewer).append("\n");  
        buffer.append("noneApproval = ").append(noneApproval).append("\n");
        buffer.append("pendingAppLevel = ").append(pendingAppLevel).append("\n");
        buffer.append("AppLevel = ").append(appLevel).append("\n");
        buffer.append("canChangeBidExpDate = ").append(canChangeBidExpDate).append("\n");
        buffer.append("canChangeBidLineItemDate = ").append(canChangeBidLineItemDate).append("\n");
        buffer.append("canEditExecSummary = ").append(canEditExecSummary).append("\n");
        buffer.append("canSupersedeAppr = ").append(canSupersedeAppr).append("\n");
        buffer.append("isExecSummryCreatd = ").append(isExecSummryCreatd).append("\n");
        buffer.append("canViewExecSummry = ").append(canViewExecSummry).append("\n");
        buffer.append("canCancelApprovedBid = ").append(canCancelApprovedBid).append("\n");
        return buffer.toString();
    }
	/**
	 * @return Returns the appLevel.
	 */
	public int getAppLevel() {
		return appLevel;
	}
	/**
	 * @param appLevel The appLevel to set.
	 */
	public void setAppLevel(int appLevel) {
		this.appLevel = appLevel;
	}
    /**
     * @return Returns the canCancelApprovedBid.
     */
    public boolean isCanCancelApprovedBid() {
        return canCancelApprovedBid;
    }
    /**
     * @param canCancelApprovedBid The canCancelApprovedBid to set.
     */
    public void setCanCancelApprovedBid(boolean canCancelApprovedBid) {
        this.canCancelApprovedBid = canCancelApprovedBid;
    }
}
