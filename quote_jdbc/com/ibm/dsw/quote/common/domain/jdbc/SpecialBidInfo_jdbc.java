package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import com.ibm.dsw.quote.common.domain.SpecialBidInfo_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidInfo_jdbc</code> class jdbc implementation of
 * SpecialBidInfo domain.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 20, 2007
 */
public class SpecialBidInfo_jdbc extends SpecialBidInfo_Impl implements PersistentObject, Serializable {

   transient SpecialBidInfoPersister persister = new SpecialBidInfoPersister(this);

    /**
     * @param webQuoteNum
     */
    public SpecialBidInfo_jdbc(String webQuoteNum) {
        this.sWebQuoteNum = webQuoteNum;
    }

    /**
     *  
     */

    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);
    }
    
    public void hydrateHeader(Connection connection) throws TopazException {
        persister.hydrateHeader(connection);
    }

    /**
     *  
     */

    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    /**
     *  
     */

    public void isDeleted(boolean deleteState) throws TopazException {
        persister.isDeleted(deleteState);
    }

    /**
     *  
     */

    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
    }

    /**
     * @param credAndRebillFlag
     *            The bCredAndRebillFlag to set.
     */
    public void setCreditAndRebillFlag(boolean credAndRebillFlag) throws TopazException {
        bCredAndRebillFlag = credAndRebillFlag;
        persister.setDirty();
    }

    /**
     * @param elaTermsAndCondsChgFlag
     *            The bElaTermsAndCondsChgFlag to set.
     */
    public void setElaTermsAndCondsChgFlag(boolean elaTermsAndCondsChgFlag) throws TopazException {
        bElaTermsAndCondsChgFlag = elaTermsAndCondsChgFlag;
        persister.setDirty();
    }

    /**
     * @param fulfllViaLanddMdlFlag
     *            The bFulfllViaLanddMdlFlag to set.
     */
    public void setFulfllViaLanddMdlFlag(boolean fulfllViaLanddMdlFlag) throws TopazException {
        bFulfllViaLanddMdlFlag = fulfllViaLanddMdlFlag;
        persister.setDirty();
    }

    /**
     * @param preApprvdCtrctLvlPricFlg
     *            The bPreApprvdCtrctLvlPricFlg to set.
     */
    public void setPreApprvdCtrctLvlPricFlg(boolean preApprvdCtrctLvlPricFlg) throws TopazException {
        bPreApprvdCtrctLvlPricFlg = preApprvdCtrctLvlPricFlg;
        persister.setDirty();
    }

    /**
     * @param ryltyDiscExcddFlag
     *            The bRyltyDiscExcddFlag to set.
     */
    public void setRyltyDiscExcddFlag(boolean ryltyDiscExcddFlag) throws TopazException {
        bRyltyDiscExcddFlag = ryltyDiscExcddFlag;
        persister.setDirty();
    }

    /**
     * @param setCtrctLvlPricngFlag
     *            The bSetCtrctLvlPricngFlag to set.
     */
    public void setSetCtrctLvlPricngFlag(boolean setCtrctLvlPricngFlag) throws TopazException {
        bSetCtrctLvlPricngFlag = setCtrctLvlPricngFlag;
        persister.setDirty();
    }

    /**
     * @param termsAndCondsChgFlag
     *            The bTermsAndCondsChgFlag to set.
     */
    public void setTermsAndCondsChgFlag(boolean termsAndCondsChgFlag) throws TopazException {
        bTermsAndCondsChgFlag = termsAndCondsChgFlag;
        persister.setDirty();
    }

   
    /**
     * @param creditJustText
     *            The sCreditJustText to set.
     */
    public void setCreditJustText(String creditJustText) throws TopazException {
        sCreditJustText = creditJustText;
        persister.setDirty();
    }

    /**
     * @param spBidCategories
     *            The spBidCategories to set.
     */
    public void setSpBidCategories(List spBidCategories) throws TopazException {
        this.spBidCategories = spBidCategories;
        persister.setDirty();
    }

    /**
     * @param salesDiscTypeCode
     *            The salesDiscTypeCode to set.
     */
    public void setSalesDiscTypeCode(String salesDiscTypeCode) throws TopazException {
        sSalesDiscTypeCode = salesDiscTypeCode;
        persister.setDirty();
    }

    /**
     * @param spBidCustIndustryCode
     *            The spBidCustIndustryCode to set.
     */
    public void setSpBidCustIndustryCode(String spBidCustIndustryCode) throws TopazException {
        sSpBidCustIndustryCode = spBidCustIndustryCode;
        persister.setDirty();
    }

    /**
     * @param spBidDist
     *            The spBidDist to set.
     */
    public void setSpBidDist(String spBidDist) throws TopazException {
        sSpBidDist = spBidDist;
        persister.setDirty();
    }

    /**
     * @param spBidJustText
     *            The spBidJustText to set.
     */
    public void setSpBidJustText(String spBidJustText) throws TopazException {
        sSpBidJustText = spBidJustText;
        persister.setDirty();
    }

    /**
     * @param spBidRgn
     *            The spBidRgn to set.
     */
    public void setSpBidRgn(String spBidRgn) throws TopazException {
        sSpBidRgn = spBidRgn;
        persister.setDirty();
    }

    /**
     * @param spBidType
     *            The spBidType to set.
     */
    public void setSpBidType(String spBidType) throws TopazException {
        sSpBidType = spBidType;
        persister.setDirty();
    }

    public void setQuestions(List questions) throws TopazException {
        this.questions = questions;
        persister.setDirty();
    }

    /**
     * @param splitBidFlag
     *            The bSplitBidFlag to set.
     */
    public void setSplitBidFlag(boolean splitBidFlag) throws TopazException {
    	bSplitBidFlag = splitBidFlag;
        persister.setDirty();
    }
    
    public void setGridFlag(boolean isGridDelegationFlag) throws TopazException{
    	this.isGridDelegationFlag = isGridDelegationFlag;
    	persister.setDirty();
    }
    
    public void setChannelOverrideDiscountReasonCode(String channelOverrideDiscountReasonCode) throws TopazException {
    	this.channelOverrideDiscountReasonCode=channelOverrideDiscountReasonCode;
    	persister.setDirty();
    }
}
