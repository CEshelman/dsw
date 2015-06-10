package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.common.domain.QuoteHeader_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteHeader_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-8
 */

public class QuoteHeader_jdbc extends QuoteHeader_Impl implements PersistentObject, Serializable {
    transient QuoteHeaderPersister quoteHeaderPersister = null;

    public QuoteHeader_jdbc(String creatorId) {
        this.creatorId = creatorId;
        quoteHeaderPersister = new QuoteHeaderPersister(this);
    }

    public void setCountry(Country country) throws TopazException {
        this.country = country;
        quoteHeaderPersister.setDirty();
    }

    public void setCustomerNum(String customerNum) {
        this.soldToCustNum = customerNum;
    }

    public void setLOB(CodeDescObj lob) throws TopazException {
        this.lob = lob;
        quoteHeaderPersister.setDirty();
    }
    public void setSystemLOB(CodeDescObj systemLOB) throws TopazException {
        this.systemLOB = systemLOB;
        quoteHeaderPersister.setDirty();
    }
    public void setAcquisition(String acquisition) throws TopazException {
        this.acqrtnCode = acquisition == null ? "" : acquisition;
        quoteHeaderPersister.setDirty();
    }
    public void setPayerCustNum(String payerNum) throws TopazException {
        this.payerCustNum = payerNum;
        quoteHeaderPersister.setDirty();
    }

    public void setRselCustNum(String resellerNum) throws TopazException {
        this.rselCustNum = resellerNum;
        quoteHeaderPersister.setDirty();
    }

    public void setWebQuoteNum(String webQuoteNum) throws TopazException {
        this.webQuoteNum = webQuoteNum;
        quoteHeaderPersister.setDirty();
    }
    public void setTotalPoints(double totalPoints) throws TopazException {
        this.totalPoints = totalPoints;
        quoteHeaderPersister.setDirty();
    }
    public void setQuoteExpDate(Date d)  throws TopazException {
        this.quoteExpDate = d;
        quoteHeaderPersister.setDirty();
    }
    public void setGsaPricngFlg(int flag) throws TopazException {
        this.gsaPricngFlg = flag;
        quoteHeaderPersister.setDirty();       
    }
    public void setRecalcPrcFlag(int flag) throws TopazException {        
        this.priceRecalcFlag = flag;
        quoteHeaderPersister.setDirty();        
    }
    public void setVolDiscLevelCode(String code) throws TopazException {
        this.volDiscLevelCode = code;
        quoteHeaderPersister.setDirty();        
    }
    public void setTranPriceLevelCode(String code) throws TopazException {
        this.tranPriceLevelCode = code;
        quoteHeaderPersister.setDirty();
    }
    public void setQuotePriceTot(double priceTotal) throws TopazException {
        this.quotePriceTot = priceTotal;
        quoteHeaderPersister.setDirty();
    }
  
    public void setSpeclBidFlag(int flag){
        this.speclBidFlag = flag;
    }
    public void setSpeclBidSystemInitFlag(int flag){
        this.speclBidSystemInitFlg = flag;
    }
    public void setSpeclBidManualInitFlag(int flag){
        this.speclBidManualInitFlg = flag;
    }
    public void setQuoteExpDays(int quoteExpDays) {
        this.quoteExpDays = quoteExpDays;
    }
    public void setRnwlPrtnrAccessFlag(int rnwlPrtnrAccessFlag) throws TopazException {
        this.rnwlPrtnrAccessFlag = rnwlPrtnrAccessFlag;
        quoteHeaderPersister.setDirty();
    }
    public void setSapIntrmdiatDocNum(String sapIntrmdiatDocNum) {
        this.sapIntrmdiatDocNum = sapIntrmdiatDocNum;
    }
    public void setQuoteStageCode(String quoteStageCode) {
        this.quoteStageCode = quoteStageCode;
    }
    
    public void setExemptnCode(String exemptnCode) {
        this.exemptnCode = exemptnCode;
    }
    
    public void setOpprtntyNum(String opprtntyNum) {
        this.opprtntyNum = opprtntyNum;
    }
    
    public void setSubmittedDate(Date submittedDate) throws TopazException {
        this.submittedDate = submittedDate;
        quoteHeaderPersister.setDirty();
    }
    public void setSubmitterId(String submitterId) throws TopazException{
        this.submitterId = submitterId;
        quoteHeaderPersister.setDirty();
    }
    
    public void setReqstIbmCustNumFlag(int flag) throws TopazException {
        this.reqstIbmCustNumFlag = flag;
        quoteHeaderPersister.setDirty();
    }
    
    public void setReqstPreCreditCheckFlag(int flag) throws TopazException {
        this.reqstPreCreditCheckFlag = flag;
        quoteHeaderPersister.setDirty();
    }
    /**
     * @param fulfillmentSrc The fulfillmentSrc to set.
     */
    public void setFulfillmentSrc(String fulfillmentSrc) {
        this.fulfillmentSrc = fulfillmentSrc;
    }
    
    public void setOvrrdTranLevelCode(String ovrrdTranLevelCode) throws TopazException {
        this.ovrrdTranLevelCode = ovrrdTranLevelCode;
        quoteHeaderPersister.setDirty();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteHeader#setSavedQuoteNum(java.lang.String)
     */
    public void setSavedQuoteNum(String savedQuoteNum) throws TopazException {
        this.savedQuoteNum = savedQuoteNum;
    }
    
    public void setOfferPrice(Double offerPrice) throws TopazException {
        this.offerPrice = offerPrice;
    }
    
    public void setIncldLineItmDtlQuoteFlg(int incldLineItmDtlQuoteFlg) throws TopazException {
        this.incldLineItmDtlQuoteFlg = incldLineItmDtlQuoteFlg;
    }
    
    public void setPriceCountry(Country priceCountry) throws TopazException {
        this.priceCountry = priceCountry;
    }
    
    public void setProgMigrationCode(String progMigrationCode) throws TopazException {
        this.progMigrationCode = progMigrationCode;
        quoteHeaderPersister.setDirty();
    }
    
    public void setRenwlQuoteNum(String renwlQuoteNum) throws TopazException {
        this.renwlQuoteNum = renwlQuoteNum;
        quoteHeaderPersister.setDirty();
    }

    public void setAudCode(String audCode) throws TopazException {
        this.audCode = audCode;
        quoteHeaderPersister.setDirty();
    }
    
    public void setOrdgMethodCode(String ordgMethodCode) throws TopazException {
        this.ordgMethodCode = ordgMethodCode;
        quoteHeaderPersister.setDirty();
    }
    
    public void setReasonCodes(List reasonCodes) throws TopazException{
    	this.reasonCodes = reasonCodes;
    }
    
    public void setBackDatingComment(String backDatingComment) throws TopazException{
    	this.backDatingComment = backDatingComment;
    }
    
    public void setBackDatingFlag(boolean backDatingFlag) throws TopazException{
    	this.backDatingFlag = backDatingFlag;
    }
    
    public void setApprovalRouteFlag(int approvalRouteFlag){
        this.approvalRouteFlag = approvalRouteFlag;
    }
    
    public void setAddtnlPrtnrEmailAdr(String addtnlPrtnrEmailAdr) throws TopazException {
        this.addtnlPrtnrEmailAdr = addtnlPrtnrEmailAdr;
    }
    
    public void setDistribtrToBeDtrmndFlag(int distribtrToBeDtrmndFlag) throws TopazException {
        this.distribtrToBeDtrmndFlag = distribtrToBeDtrmndFlag;
    }
    
    public void setResellerToBeDtrmndFlag(boolean resellerToBeDtrmndFlag) throws TopazException {
        this.resellerToBeDtrmndFlag = resellerToBeDtrmndFlag;
    }
    
    public void setSendQuoteToAddtnlPrtnrFlag(boolean sendQuoteToAddtnlPrtnrFlag) throws TopazException {
        this.sendQuoteToAddtnlPrtnrFlag = sendQuoteToAddtnlPrtnrFlag;
    }
    
    public void setQuoteClassfctnCode(String quoteClassfctnCode) throws TopazException {
        this.quoteClassfctnCode = quoteClassfctnCode;
    }
    
    public void setQuoteStartDate(Date quoteStartDate) throws TopazException{
        this.quoteStartDate = quoteStartDate;
    }
    
    public void setRenwlQuoteSpeclBidFlag(int renwlQuoteSpeclBidFlag) throws TopazException {
        this.renwlQuoteSpeclBidFlag = renwlQuoteSpeclBidFlag;
    }
    
    public void setPriceStartDate(Date priceStartDate) throws TopazException{
        this.priceStartDate = priceStartDate;
    }
    
    public void setCreateCtrctFlag(boolean createCtrctFlag) throws TopazException {
        this.createCtrctFlag = createCtrctFlag;
        quoteHeaderPersister.setDirty();
    }
    
    public void setWebCtrctId(int webCtrctId) throws TopazException {
        this.webCtrctId = webCtrctId;
        quoteHeaderPersister.setDirty();
    }
    
    public void addOverallStatus(CodeDescObj overallStatus) {
        if (quoteOverallStatuses != null) {
            quoteOverallStatuses.add(overallStatus);
        }
    }
    
    public void setCmprssCvrageFlag(boolean cmprssCvrageFlag) throws TopazException{
        this.cmprssCvrageFlag= cmprssCvrageFlag;
        quoteHeaderPersister.setDirty();
    }
    
    public void setBusOrgCode(String busOrgCode) {
        this.busOrgCode = busOrgCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        quoteHeaderPersister.hydrate(connection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        quoteHeaderPersister.persist(connection);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        quoteHeaderPersister.isDeleted(deleteState);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        quoteHeaderPersister.isNew(newState);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.domain.DraftSalesQuote#delete()
     */
    public void delete() throws TopazException {
        this.isDeleted(true);
        this.isNew(false);
    }

	@Override
	public void setYtyGrwthPct(Double ytyGrwthPct) throws TopazException {
		  this.ytyGrwthPct = ytyGrwthPct;
	      quoteHeaderPersister.setDirty();
	}

	@Override
	public void setImpldGrwthPct(Double impldGrwthPct) throws TopazException {
		  this.impldGrwthPct = impldGrwthPct;
	       quoteHeaderPersister.setDirty();
	}

	@Override
	public void setContractNum(String contractNum) {
		this.contractNum = contractNum;
	}
	
}
