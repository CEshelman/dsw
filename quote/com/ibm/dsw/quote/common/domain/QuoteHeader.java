package com.ibm.dsw.quote.common.domain;

import java.util.Date;
import java.util.List;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>QuoteHeader<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-3-8
 */

public interface QuoteHeader {

    public String getCreatorId();

    public String getPriorQuoteNum();

    public String getRenwlQuoteNum();

    public Country getCountry();

    public String getCurrencyCode();
    
    public String getCountryCurrencyCode();

    //prog_code in data base
    public CodeDescObj getLob();

    public CodeDescObj getSystemLOB();

    public String getSapIntrmdiatDocNum();

    public String getSoldToCustNum();

    //this is not in WEB_QUOTE table,
    public String getCustName();

    public String getContractNum();

    public String getFulfillmentSrc();

    public String getQuoteTypeCode();

    public Date getQuoteExpDate();

    public String getRselCustNum();

    public String getPayerCustNum();

    public double getQuotePriceTot();

    public String getQuoteTitle();

    public String getQuoteDscr();

    public String getQuoteStageCode();

    public Date getModDate();

    public String getModByFullName();

    public String getExemptnCode();

    public int getGsaPricngFlg();

    public String getOpprtntyNum();

    public String getOpprtntyOwnrEmailAdr();

    public String getBusOrgCode();

    public int getQuoteExpDays();

    public int getSpeclBidFlag();
    
    public String getVolDiscLevelCode();

    public int getWebCustId();

    public String getWebQuoteNum();

    public int getPriceRecalcFlag();

    public int getSpeclBidManualInitFlg();

    public int getSpeclBidSystemInitFlg();

    public String getAcqrtnCode();

    public String getAudCode();

    public Date getLastQuoteDate();

    public Date getPriceStartDate();

    public int getReqstIbmCustNumFlag();

    public int getReqstPreCreditCheckFlag();

    public String getSapDistribtnChnlCode();

    public String getSapSalesDocTypeCode();

    public String getTranPriceLevelCode();

    public String getOvrrdTranLevelCode();

    public int getPartHasPriceFlag();

    public boolean getHasPartsFlag();

    public boolean getHasObsoletePartsFlag();

    public double getTotalPoints();

    public int getRecalcPrcFlag();

    public int getRnwlPrtnrAccessFlag();

    public String getAddtnlCntEmailAdr();

    public int getInclTaxFinalQuoteFlag();
    
    public int getFirmOrdLtrFlag();

    public int getOrdQtyTotal();

    public String getQSubmitCoverText();

    public Date getRenwlEndDate();

    public String getRenwlQuoteSalesOddsOode();

    public String getSalesComments();

    public int getSendQuoteToAddtnlCntFlag();

    public int getSendQuoteToPrmryCntFlag();

    public int getSendQuoteToQuoteCntFlag();

    public List getTacticCodes();

    public String getUpsideTrendTowardsPurch();

    public String getRnwlTermntnReasCode();

    public String getTermntnComments();

    public Date getRqModDate();

    public Date getRqStatModDate();

    public int getBlockRnwlReminder();

    public boolean isSalesQuote();

    public boolean isRenewalQuote();

    public boolean isPAQuote();

    public boolean isPAEQuote();

    public boolean isFCTQuote();

    public boolean isPPSSQuote();

    public boolean isPAUNQuote();

    public boolean isSubmittedQuote();

    public boolean isFCTToPAQuote();

    public boolean isTBDQuote();

    public boolean isOEMQuote();
    
    public boolean isCSRAQuote();
    
    public boolean isCSTAQuote();

    //for Submitted Quote
    public String getSubmitterId();

    public Date getSubmittedDate();

    public List getQuoteOverallStatuses();
    
    public List getQuoteOverallStatuses4Cpexdate();

    public String getSapQuoteNum();

    public int getCalcBLPriceFlag();

    public String getOriginalIdocNum();

    public int getContainLineItemRevnStrm();

    public String getSavedQuoteNum();

    public boolean containsOverallStatus(String overallStatus);

    public boolean containsOverallStatus4Cpexdate(String overallStatus);
    
    // added May 17, 2007
    public double getQuotePriceTotIncldTax();

    public double getLocalExtndTaxAmtTot();
    // Andy : this flag is only for Submitted Quote ,if the the flag is true, it means the approver change the line item date for submitted quote
    // otherwise , no dates are updated, the flag will be set by sp U_SAP_LI
    public boolean getDateOvrrdByApproverFlag();

    public Double getOfferPrice();

    public int getIncldLineItmDtlQuoteFlg();

    public Date getApproverAssignDate();

    public String getApproverName();

    public String getSbApprovalGroupName();

    public Country getPriceCountry();

    public String getProgMigrationCode();

    public boolean isMigration();

    public List getAcquisitionCodes();

    public int getApprovalRouteFlag();

    public String getAddtnlPrtnrEmailAdr();

    public boolean isDistribtrToBeDtrmndFlag();

    public boolean isSingleTierNoDistributorFlag();

    public boolean isResellerToBeDtrmndFlag();

    public boolean isSendQuoteToAddtnlPrtnrFlag();

    //Add on 16/9/2008 for back dating reason codes and comment
    public List getReasonCodes();

    public String getBackDatingComment();

    public boolean getBackDatingFlag();

    public String getQuoteClassfctnCode();

    public int getRenwlQuoteSpeclBidFlag();

    public boolean isELAQuote();

    public String getCustReasCode();

    public String getSalesStageCode();

    public boolean isCreateCtrctFlag();

    public boolean isSubmittedForEval();
    
    public boolean isAcceptedByEval();
    
    public boolean isReturnForChgByEval();
    
    public boolean isEditForRetChgByEval();
    
    public boolean isAssignedEval(String userId);
    
    public boolean isDeleteByEditor();
    
    public boolean isQuoteCntryEvaluator();
    
    public String getEvalFullName();
    
    public String getEvalEmailAdr();
    
    public Date getEvalDate();
    
    public int getWebCtrctId();

    public Date getPriorQuoteSbmtDate();

    public boolean isCopied4ReslChangeFlag();

    public Date getQuoteStartDate();

    public String getOrdgMethodCode();

    public String getEndUserCustNum();
    
    public String setEndUserCustNum(String endUserCustNum);

    public boolean isLockedFlag();

    public String getLockedBy();

    public int getPymTermsDays();

    public boolean hasLotusLiveItem();

    public int getOemBidType();

    public boolean hasSaaSLineItem();

    public boolean hasSoftSLineItem();
    
    public boolean isHasEvaluator();
    
    public boolean isPGSNewPAEnrolled();
    
    public void setHasEvaluator(boolean isHasEvaluator);
    
    public void setQuoteStartDate(Date quoteStartDate)  throws TopazException;

    public void setOfferPrice(Double offerPrice) throws TopazException;

    public void setDateOvrrdByApproverFlag(boolean dateOvrrdByApproverFlag);
    // setters
    //TODO add all setters used in SQO
    public void setWebQuoteNum(String webQuoteNum) throws TopazException;

    public void setLOB(CodeDescObj lob) throws TopazException;

    public void setSystemLOB(CodeDescObj systemLOB) throws TopazException;

    public void setCountry(Country country) throws TopazException;

    public void setCustomerNum(String customerNum);

    public void setRselCustNum(String resellerNum) throws TopazException;

    public void setPayerCustNum(String payerNum) throws TopazException;

    public void setTotalPoints(double totalPoints) throws TopazException;

    public void setQuoteExpDate(Date d) throws TopazException;

    public void setGsaPricngFlg(int flag) throws TopazException;

    public void setRecalcPrcFlag(int flag) throws TopazException;

    public void setVolDiscLevelCode(String code) throws TopazException;

    public void setTranPriceLevelCode(String code) throws TopazException;

    public void setQuotePriceTot(double priceTotal) throws TopazException;

    public void setSpeclBidFlag(int flag);
    
    // used to delete a saved draft quote
    public void delete() throws TopazException;

    public boolean hasNewCustomer();

    public boolean hasExistingCustomer();

    public void setAcquisition(String acquisition) throws TopazException;

    public void setQuoteExpDays(int quoteExpDays);

    public void setRnwlPrtnrAccessFlag(int rnwlPrtnrAccessFlag) throws TopazException;

    public void setSapIntrmdiatDocNum(String sapIntrmdiatDocNum);

    public void setQuoteStageCode(String quoteStageCode);

    public void setExemptnCode(String exemptnCode);

    public void setFulfillmentSrc(String fulfillmentSrc);

    public void setOpprtntyNum(String opprtntyNum);

    public void setSpeclBidSystemInitFlag(int flag);

    public void setSpeclBidManualInitFlag(int flag);

    //for Submitted Quote
    public void setSubmitterId(String submitterId) throws TopazException;

    public void setSubmittedDate(Date submittedDate) throws TopazException;

    public void setReqstIbmCustNumFlag(int flag) throws TopazException;

    public void setReqstPreCreditCheckFlag(int flag) throws TopazException;

    public void setSavedQuoteNum(String savedQuoteNum) throws TopazException;

    public void setOvrrdTranLevelCode(String ovrrdTranLevelCode) throws TopazException;

    public void setIncldLineItmDtlQuoteFlg(int incldLineItmDtlQuoteFlg) throws TopazException;

    public void setPriceCountry(Country priceCountry) throws TopazException;

    public void setProgMigrationCode(String progMigrationCode) throws TopazException;

    public void setRenwlQuoteNum(String renwlQuoteNum) throws TopazException;

    public void setAudCode(String audCode) throws TopazException;

    public void setOrdgMethodCode(String ordgMethodCode) throws TopazException;

    public void setApprovalRouteFlag(int approvalRouteFlag);

    public void setAddtnlPrtnrEmailAdr(String addtnlPrtnrEmailAdr) throws TopazException;

    public void setDistribtrToBeDtrmndFlag(int distribtrToBeDtrmndFlag) throws TopazException;

    public void setResellerToBeDtrmndFlag(boolean resellerToBeDtrmndFlag) throws TopazException;

    public void setSendQuoteToAddtnlPrtnrFlag(boolean sendQuoteToAddtnlPrtnrFlag) throws TopazException;

    //Add on 16/9/2008 for back dating reasons and comment
    public void setBackDatingFlag(boolean backDatingFlag) throws TopazException;

    public void setReasonCodes(List reasonCodes) throws TopazException;

    public void setBackDatingComment(String backDatingComment) throws TopazException;

    public void setQuoteClassfctnCode(String quoteClassfctnCode) throws TopazException;

    public void setRenwlQuoteSpeclBidFlag(int renwlQuoteSpeclBidFlag) throws TopazException;

    public void setLockedFlag(boolean lockedFlag) throws TopazException;

    public void setLockedBy(String lockedBy) throws TopazException;

    public void setPymTermsDays(int pymTermsDays) throws TopazException;

    public boolean isSpBiddableRQ();

    public boolean isChannelQuote();

    public void setPriceStartDate(Date priceStartDate) throws TopazException;

    public int getPAOBlockFlag();
    
    public void setPAOBlockFlag(int pAOBlockFlag);
    
    public void setCreateCtrctFlag(boolean newCtrctFlag) throws TopazException;
    
    public void setEvalFullName(String evalFullName);
    
    public void setEvalEmailAdr(String evalEmailAdr) throws TopazException;
    
    public void setEvalDate(Date evalDate) throws TopazException;
    
    public void setWebCtrctId(int webCtrctId) throws TopazException;

    public void addOverallStatus(CodeDescObj overallStatus);

    //Compressed coverage
    public boolean getCmprssCvrageFlag();
    public void setCmprssCvrageFlag(boolean cmprssCvrageFlag) throws TopazException;

    public boolean getHasPARenwlLineItmsFlag();

    public void setBusOrgCode(String busOrgCode);

    public boolean isCopied4PrcIncrQuoteFlag();
    
    public void setCopied4PrcIncrQuoteFlag(boolean copied4PrcIncrQuoteFlag);   

    public Date getQuoteAddDate();

    public void setQuoteAddDate(Date addDate);

    public boolean isMatchOfferPriceFailed();

    public void setMatchOfferPriceFailed(boolean isMatchOfferPriceFailed);

    public double getOriQuotePriceTot();

    public void setOriQuotePriceTot(double oriQuotePriceTot);

    public boolean isQtEligible4BidIteratn();

    public void setQtEligible4BidIteratn(boolean qtEligible4BidIteratn);

    public boolean isBidIteratnQt();

    public void setBidIteratnQt(boolean bidIteratnQt);


	public int getSaasBidIteratnQtInd();

	public void setSaasBidIteratnQtInd(int saasBidIteratnQtInd);
	
	public int getSoftBidIteratnQtInd() ;

	public void setSoftBidIteratnQtInd(int softBidIteratnQtInd) ;


    
    public boolean hasDiscountableItems();

    public double getLatamUpliftPct();

    public void setLatamUpliftPct(double latamUpliftPct);

    public void setHasLotusLiveItem(boolean hasLotusLiveTtem);

    public boolean isTriggerTC();

    public void setOemBidType(int oemBidType) throws TopazException;

    public boolean isCopiedForOutputChangeFlag();

    public void setCopiedForOutputChangeFlag(boolean copiedForOutputChangeFlag);

    public String getSubmitterName();


	public String getPriorSapQuoteNum() ;
	public void setPriorSapQuoteNum(String priorSapQuoteNum) ;

	public Integer getFctNonStdTermsConds();

	public void setHasSaaSLineItem(boolean hasSaaSLineItem);

	public Double getSaasTotCmmtmtVal() ;
	public void setSaasTotCmmtmtVal(Double saasTotCmmtmtVal) ;

	public Date getEstmtdOrdDate();
	public void setEstmtdOrdDate(Date estmtdOrdDate);

	public String getRefDocNum();
	public void setRefDocNum(String refDocNum);

	public boolean isOnlySaaSParts();
	public void setOnlySaaSParts(boolean onlySaaSParts);
	
	public boolean isHasRampUpPartFlag();
	public void setHasRampUpPartFlag(boolean hasRampUpPartFlag); 

	public int getMaxEstdNumProvisngDays();
	public void setMaxEstdNumProvisngDays(int maxEstdNumProvisngDays);

	public boolean hasConfigrtnFlag();
	public void setHasConfigrtnFlag(boolean hasConfigrtnFlag);

	public boolean isCACustCurrncyNotMatchFlag();
	public void setCACustCurrncyNotMatchFlag(boolean custCurrncyNotMatchFlag);

	public boolean isAddTrdOrCotermFlag();
	public void setAddTrdOrCotermFlag(boolean addTrdOrCotermFlag);


	public boolean isHasNewConfFlag();
	public void setHasNewConfFlag(boolean hasNewConfFlag) ;

	public boolean isPGSQuote();

	public boolean isBPRelatedCust();

	public void setBPRelatedCust(boolean isBPRelatedCust);

	public Double getSaasBpTCV();
	public void setSaasBpTCV(Double saasBpTCV);

	public String getQcCompany();
	public void setQcCompany(String qcCompany);

	public String getQcCountry();
	public void setQcCountry(String qcCountry);
	
	public boolean isMatchBPsInChargeAgreementFlag();
	public void setMatchBPsInChargeAgreementFlag(boolean matchBPsInChargeAgreementFlag);
	
	public String getCreatorEmail();
	public void setCreatorEmail(String creatorEmail);
	
	public double getQuoteTotalEntitledExtendedPrice();
	public void setQuoteTotalEntitledExtendedPrice(double quoteTotalEntitledExtendedPrice);
	
	public boolean isSubmittedOnSQO();
	public void setSubmittedOnSQO(boolean isSubmittedONSQO);
	
	/**
	 * if quote contains new licence part
	 */
	public boolean isHasNewLicencePart();
	public void setHasNewLicencePart(boolean hasNwLcPart);
	
	/**
	 * if quote contains saas subscription part without renewl flag
	 */
	public boolean isHasSaasSubNoReNwPart();
	public void setHasSaasSubNoReNwPart(boolean hasSubNoReNwPart);
	
	/**
	 * if quote contains appliance part
	 */
	public boolean isHasAppliancePartFlag();
	public void setHasAppliancePartFlag(boolean hasApplianceFlag);
	

	public String getQuoteOutputType();
	public void setQuoteOutputType(String quoteOutputType);
    
    public Date getCustReqstArrivlDate();
	public void setCustReqstArrivlDate(Date custReqstArrivlDate);
	

	public boolean isHasAppMainPart();
	public void setHasAppMainPart(boolean hasAppMainPart);
	
	public boolean isHasAppUpgradePart();
	public void setHasAppUpgradePart(boolean hasAppUpgradePart);
	

	public boolean isSaasFCTToPAQuote();
	public void setSaasFCTToPAQuote(boolean isSaasFCTToPAQuote) ;
	

	public boolean isTermDiffInDiffFctConfig();
	public void setTermDiffInDiffFctConfig(boolean termDiffInDiffFctConfig);

	public String getOrignlSalesOrdRefNum();
	public void setOrignlSalesOrdRefNum(String orignlSalesOrdRefNum);
	
	public boolean isSaaSStrmlndApprvlFlag();
	public void setSaaSStrmlndApprvlFlag(boolean strmlndApprvlFlag);

	public boolean isNoApprovalRequire();
	public void setNoApprovalRequire(boolean noApprovalRequire) ;

	
	public Date getPriorQuoteExpDate();
	public void setPriorQuoteExpDate(Date priorQuoteExpDate);
	
	public String getQuoteOutputOption();
	public void setQuoteOutputOption(String quoteOutputOption);
	
	public String getStreamlinedWebQuoteNum();
	
	public boolean isRebillCreditOrder();
    public void setRebillCreditOrder(boolean rebillCreditOrder);
    
    // if quote is SSP quote
    public boolean isSSPQuote();
    
    public boolean isSubmitByEvaluator();
    
    public void setSubmitByEvaluator(boolean isSubmitByEvaluator);
    
    public String getSspType();
    public void setSspType(String sspType);
    
    public String getBpSubmitterEmail() ;

	public void setBpSubmitterEmail(String bpSubmitterEmail);

	public String getBpSubmitterName();

	public void setBpSubmitterName(String bpSubmitterName);

	public Date getBpSubmitDate() ;

	public void setBpSubmitDate(Date bpSubmitDate) ;
	
	/**
	 * YTY Growth
	*/
	public Double getYtyGrwthPct();
	public void setYtyGrwthPct(Double ytyGrwthPct)  throws TopazException;
	    
	public Double getImpldGrwthPct();
	public void setImpldGrwthPct(Double impldGrwthPct)  throws TopazException;
	 
	public String getCaEndUserCustNum();
	public void setCaEndUserCustNum(String caEndUserCustNum);
	
	public boolean isRSVPSRPOnly();
    public void setRSVPSRPOnly(boolean flag);
	
    public boolean isAddTrd();
    public void setAddTrd(boolean flag);
	
	public int getRQLineItemCount();
	
	public boolean isUnderEvaluation();
	
	/**
	 * Add a flag in QuoteHeader, if YTY growth% and implied YTY growth% is not
	 * null, it'll be a growth delegation quote.
	 */
	public boolean isGrowthDelegation(Quote quote);
	
	public boolean isContainRQLineItem();

	public void setContainRQLineItem(boolean hasRQLineItem);

	public void setDeleteBy(String userId);
	
	public String getDeleteBy();
	
	boolean isSoftwarePartWithoutApplncId();
	
	public void setEvalActionCode(String evalActionCode);
	
	public String getEvalActionCode();
	
	public boolean isAllPartsHasMediaAttr();
	
	public double getTotalPriceInUSD();
	
	//change for equity curve
	public boolean isECRecalculateFlag() ;

    public void setECRecalculateFlag(boolean isECRecalculateFlag);
	
	public boolean isECEligible();
	
	public void setECEligible(boolean isECEligible);
	
	public EquityCurveTotal getEquityCurveTotal();
	
	public void setEquityCurveTotal(EquityCurveTotal equityCurveTotal);
	
	public boolean isDisShipInstAdrFlag();
	
	public void setDisShipInstAdrFlag(boolean isDisShipInstAdrFlag);
	
	public boolean isOmittedLine();
	
	public void setOmittedLine(boolean isOmittedLine);
	
	public int getOmittedLineRecalcFlag();
	
	public void setOmittedLineRecalcFlag(int omittedLineRecalcFlag);
	
	public boolean isShipInstallEditable();
	
	public int getBudgetaryQuoteFlag();
	
	public int getInstallAtOpt();
	
	public void setInstallAtOpt(int installAtOpt);
	
	public boolean isStdloneSaasGenTermFlag();

	public void setStdloneSaasGenTermFlag(boolean stdloneSaasGenTermFlag);

	public void setSaasTermCondCatFlag(int saasTermCondCatFlag);
	
	public int getSaasTermCondCatFlag();
	
	public boolean isT2CreatedFlag();

	public void setT2CreatedFlag(boolean isT2CreatedFlag);
	
	public double getNonCOGandFNETAvgDiscount();

	public void setNonCOGandFNETAvgDiscount(double nonCOGandFNETAvgDiscount);

	public double getCogAndFNETAvgDiscount() ;

	public void setCogAndFNETAvgDiscount(double cogAndFNETAvgDiscount);
	
	public boolean isMaxAvgDiscountExceeded();
	 
	public void setMaxAvgDiscountExceeded(boolean isMaxAvgDiscountExceeded);
	
	public void setMultipleAdditionalYearImpliedGrowth(List multipleImpliedGrowthList);

	public List getMultipleAdditionalYearImpliedGrowth();
	
	public String getOpprtntyOwnrName() ;

	public String getSapOrderNum() ;

	public String getCreatorName() ;
	
	public boolean isHasDivestedPart();
	
	public void setHasDivestedPart(boolean hasDivestedPart);
	
	public void setEndUserWebCustId(int endUserWebCustId);
	
	public int getEndUserWebCustId();
	
	public void setPartHasPriceFlag(int partHasPriceFlag);
	
	/**
	 * Determine whether send TOU URL information to SAP when submit or order quote.<p>
	 * If the quote can be assigned the saasTermCondCatFlag on ToU page and 
	 * the saasTermCondCatFlag==2(CSA),then did NOT send TOU URL(send empty URL).<br>
	 * Otherwise send the ToU URLs configured in the line item.</p>
	 * @return true - should send TOU URL to SAP; false - should NOT send TOU URL.
	 * @deprecated since release 14.2 saasTermCondCatFlag was divested. 
	 * For new data, this method will always return true;
	 */
	public boolean shouldSentTouUrltoSAP();
	
	public int getDivstdObsltPartFlag();
	
	public void setDivstdObsltPartFlag(int divstdObsltPartFlag);
	
	public boolean hasLineItemsFromRQ(Quote quote);
	
	public void setAgrmtTypeCode(String agrmtTypeCode);
	
	public String getAgrmtTypeCode();

    public abstract boolean isSerialNumWarningFlag();

    public abstract void setSerialNumWarningFlag(boolean serialNumWarningFlag);
    
	public void setNeedReconfigFlag(boolean needReconfigFlag);

	public boolean isNeedReconfigFlag();
    
    public boolean isHasMonthlySoftPart();
    
    public void setHasMonthlySoftPart(boolean isHasMonthlySoftPart);
    
    public String getSubRgnCode();
    
    public void setSubRgnCode(String subRgnCode);
    
    public void setContractNum(String contractNum);
    
    //added in 15.1
    public void setSaasRenewalFlag(Boolean saasRenewalFlag);

	public Boolean isSaasRenewalFlag();

	public void setSaasMigrationFlag(Boolean saasMigrationFlag);

	public Boolean isSaasMigrationFlag();

	public void setMonthlyRenewalFlag(Boolean monthlyRenewalFlag);

	public Boolean isMonthlyRenewalFlag();

	public void setMonthlyMigrationFlag(Boolean monthlyMigrationFlag);

	public Boolean isMonthlyMigrationFlag();
	
	//added in 15.2 by Bourne,indicate if expiration date is changed
	public void setExpDateExtendedFlag(Boolean isExpDateExtendedFlag);
	
    public void setGridFlag(boolean isGridDelegationFlag);

    public boolean isGridFlag();
	
	public Boolean isExpDateExtendedFlag();
	
	public String getExpDateExtensionJustification();

	public void setExpDateExtensionJustification(String expDateExtensionJustification);
	
	public boolean isDsjFlag();
	
	public void setDsjFlag(boolean isDsjFlag);
}
