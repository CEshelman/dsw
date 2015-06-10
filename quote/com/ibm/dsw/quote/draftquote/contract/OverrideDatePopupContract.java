package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>OverrideDatePopupContract</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-9
 */
public class OverrideDatePopupContract extends DraftQuoteBaseContract {
    private String partNum;
    private String seqNum;
    private String startDate;
    private String endDate;
    private String revnStrmCode;
    private boolean isSpecialBidRnwlPart;
    private String allowEditStartDate;
    private String allowEditEndDate;
    private boolean cmprssCvrageApplied;
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    public void load(Parameters parameters, JadeSession session) { 
        super.load(parameters, session);
        
        isSpecialBidRnwlPart = parameters.getParameterAsBoolean(DraftQuoteParamKeys.IS_SPECIAL_BID_RNWL_PART);
        cmprssCvrageApplied = parameters.getParameterAsBoolean(DraftQuoteParamKeys.CMPRSS_CVRAGE_APPLIED);
    }
    

    
    /**
     * @param endDate The endDate to set.
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }
    /**
     * @param startDate The startDate to set.
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    /**
     * @return Returns the endDate.
     */
    public String getEndDate() {
        return endDate;
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
    public String getSeqNum() {
        return seqNum;
    }
    /**
     * @return Returns the startDate.
     */
    public String getStartDate() {
        return startDate;
    }
	/**
	 * @return Returns the logContext.
	 */
	public static LogContext getLogContext() {
		return logContext;
	}
	/**
	 * @return Returns the isLicence.
	 */
	public String getRevnStrmCode() {
		return revnStrmCode;
	}
	/**
	 * @param isLicence The isLicence to set.
	 */
	public void setRevnStrmCode(String revnStrmCode) {
		this.revnStrmCode = revnStrmCode;
	}
	
	public boolean isSpecialBidRnwlPart() {
		return isSpecialBidRnwlPart;
	}
	
	/**
	 * @return Returns the allowEditEndDate.
	 */
	public String getAllowEditEndDate() {
		return allowEditEndDate;
	}
	/**
	 * @param allowEditEndDate The allowEditEndDate to set.
	 */
	public void setAllowEditEndDate(String allowEditEndDate) {
		this.allowEditEndDate = allowEditEndDate;
	}
	/**
	 * @return Returns the allowEditStartDate.
	 */
	public String getAllowEditStartDate() {
		return allowEditStartDate;
	}
	/**
	 * @param allowEditStartDate The allowEditStartDate to set.
	 */
	public void setAllowEditStartDate(String allowEditStartDate) {
		this.allowEditStartDate = allowEditStartDate;
	}
    /**
     * @return Returns the cmprssCvrageApplied.
     */
    public boolean isCmprssCvrageApplied() {
        return cmprssCvrageApplied;
    }
}