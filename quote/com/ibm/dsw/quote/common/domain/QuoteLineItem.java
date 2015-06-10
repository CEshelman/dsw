package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>QuoteLineItem</code> class is Quote Line Item Domain.
 *
 *
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: 2007-3-8
 *
 * $Log: QuoteLineItem.java,v $
 * Revision 1.79  2010/07/23 05:51:18  wxiaoli
 * SQO date logic : Enhance parts and pricing tab to derive 2 new line item attributes additionally, RTC 3334, reviewed by Will
 * fix the  issue of the relatedSeqNum is incorrect and roll back the relatedSapItemNum
 *
 * Revision 1.78  2010/07/15 12:04:36  wxiaoli
 * SQO date logic : Enhance parts and pricing tab to derive 2 new line item attributes additionally, RTC 3334, reviewed by Will
 * fix the  issue of the relatedSeqNum is incorrect
 *
 * Revision 1.77  2010/06/30 07:47:17  wxiaoli
 * SQR : Pushpa's area : Update web_quote sps : Need to put current quote user as mod_by, RTC 3407, reviewed by Will
 *
 * Revision 1.76  2010/06/25 16:55:49  dsmith
 * Refactor getGoupName() to getGroupName()
 *
 * Revision 1.75  2010/06/25 08:58:30  changwei
 * Task 3334 SQO date logic : Enhance parts and pricing tab to derive 2 new line item attributes additionally
 * Reviewed by Vivian.
 *
 * Revision 1.74  2010/06/23 14:18:46  dsmith
 * Modify Special Bid Part Group determination based on country RTC Task 3143, Ebiz Help Request #MCOR-84YHK8
 *
 * Revision 1.73  2010/06/22 11:10:12  wxiaoli
 * BidIteration : DSQ02f: Display draft sales quote parts & pricing tab in bid iteration mode, RTC 3399, reviewed by Will
 *
 * Revision 1.72  2010/06/09 09:42:37  changwei
 * Use item.hasValidCmprssCvrageMonth() and item.hasValidCmprssCvrageDiscPct() instead of item.getCmprssCvrageMonth() != null and item.getCmprssCvrageDiscPct() != null to fix FVT PL : JKEY-867HJV
 * defect 3429
 *
 * Revision 1.71  2010/05/27 07:45:52  wxiaoli
 * contract level prcing flag for Display parts & pricing tab, RTC 3211,3212,3213,3214,3215,3216,3217, reviewed by Will
 *
 */
public interface QuoteLineItem  extends Comparable, Serializable, Cloneable{

    public List getLineItemConfigs();

    //  key attributes to identify a line item
    public String getQuoteNum();

    public String getPartNum();

    public String getPartDesc();

    public int getSeqNum();

    public int getDestSeqNum();

    public int getSapLineItemSeqNum();

    public Integer getPartQty();

    public int getManualSortSeqNum();

    public int getQuoteSectnSeqNum();

    //  renewal quote related attributes
    public String getRenewalQuoteNum();

    public int getRenewalQuoteSeqNum();

    public Date getRenewalQuoteEndDate();

    public Date getMaintEndDate();

    public Date getMaintStartDate();

    public Date getMaintEndDateForExport();

    public Date getMaintStartDateForExport();

    public String getRenwlChgCode();

    //  VU vonfig related attributes
    public boolean isPvuPart();

    /*public String getCUId();

    public String getCUCode();

    public String getCUCodeDscr();

    public int getCUQty();*/

    public ChargeableUnit[] getChargeableUnits();

    public String getPVUOverrideQtyIndCode();

    public int getVuConfigrtnNum();

    //  proration related attributes
    public int getLicPartQty();

    public boolean getProrateFlag();

    public int getAddtnlMaintCvrageQty();// interface still keep same with CMRe
                                         // #48 update

    public int getAddtnlYearCvrageSeqNum();

    public List getAddtnlYearCvrageLineItems();
    
    public void setParentMasterPart(QuoteLineItem parentMasterPart);
    
    public QuoteLineItem getParentMasterPart();

    public boolean getAssocdLicPartFlag();

    //  use-inputted pricing discounts attributes
    public double getLineDiscPct();

    public Double getOverrideUnitPrc();

    //  prices - attributes
    public Double getLocalExtPrc();

    public Double getLocalExtProratedDiscPrc();

    public Double getLocalExtProratedPrc();

    public Double getLocalUnitPrc();

    public Double getLocalUnitProratedDiscPrc();

    public Double getLocalUnitProratedPrc();

    public Double getLclExtndChnlPriceIncldTax();

    public Double getLocalChnlTaxAmt();

    // added May 15
    public Double getChannelExtndPrice();

    public Double getChannelUnitPrice();

    public Double getLocalTaxAmt();

    public Double getLclExtndPriceIncldTax();

    //added May 30, 2007 - prodcut target marketing code
    public String getProdTrgtMktCode();

    //  part attributes
    public String getSwProdBrandCode();

    public String getSwProdBrandCodeDesc();

    public String getCtrctProgCode();

    public String getCtrctProgCodeDesc();

    public String getProdPackTypeCode();

    public String getPartTypeCode();

    public String getRevnStrmCode();

    public String getRevnStrmCodeDesc();

    public String getWwideProdCode();

    public String getWwideProdCodeDesc();

    public String getWwideProdSetCode();

    public String getWwideProdGrpCode();

    public String getSwSubId();
    
    //add new fields for aspen
    public String getControlledCode();

    //this is a handy method, controlled code desc is retrieved from app cache
    public String getControlledCodeDesc();

    public boolean isControlled();
    //  associated flags attributes
    // for any parts
    public boolean isRoyalty();

    public boolean isPartEOLSet();

    public boolean isPartRestrct();

    public boolean isExprsPart();

    public boolean isObsoletePart();


    public Date getEOLDate();

    public double getContributionUnitPts();

    public double getContributionExtPts();

    public boolean getStartDtOvrrdFlg();

    public boolean getEndDtOvrrdFlg();

    public PartDisplayAttr getPartDispAttr();

    // added comment, change type code - Apr 12, 2007
    public String getComment();

    public String getChgType();

    public boolean getBackDatingFlag();

    // added for renewal quote


    public int getOpenQty();

    public int getOrderQty();

    public Date getOrigEndDate();

    public Date getOrigStDate();

    public double getOrigUnitPrice();

    public double getOrigExtPrice();

    public Date getPrevsStartDate();

    public Date getPrevsEndDate();

    public Double getChnlOvrrdDiscPct();

    public Double getChnlStdDiscPct();

    public Double getTotDiscPct();

    public boolean getSalesQuoteRefFlag();

    public String getIbmProgCode();

    public String getIbmProgCodeDscr();

    public boolean isRenewalPart();

    public boolean isMaintCoverageProrated();

    // set field value

    public void setQuoteNum(String quoteNum) throws TopazException;

    public void setPartNum(String partNum) throws TopazException;

    public void setHasExprsPart(boolean has) throws TopazException;

    public void setPvuPart(boolean is) throws TopazException;

    public void setPartEOLSet(boolean is) throws TopazException;

    public void setPartRestrct(boolean is) throws TopazException;

    public void setProrateFlag(boolean flag) throws TopazException;

    public void setRoyalty(boolean is) throws TopazException;

    public void setLineDiscPct(double discPct) throws TopazException;

    public void setLocalExtPrc(Double localExtPrc) throws TopazException;

    public void setLocalExtProratedDiscPrc(Double localExtProratedDiscPrc) throws TopazException;

    public void setLocalExtProratedPrc(Double localExtProratedPrc) throws TopazException;

    public void setLocalUnitPrc(Double localUnitPrc) throws TopazException;

    public void setLocalUnitProratedDiscPrc(Double localUnitProratedDiscPrc) throws TopazException;

    public void setLocalUnitProratedPrc(Double localUnitproratedPrc) throws TopazException;

    public void setOverrideUnitPrc(Double overrideUnitPrc) throws TopazException;

    public void setContributionUnitPts(double unitPts) throws TopazException;

    public void setContributionExtPts(double unitPts) throws TopazException;

    public void setAddtnlMaintCvrageQty(int qty) throws TopazException;

    //public void setCUQty(int qty) throws TopazException;

    public void setLicPartQty(int qty) throws TopazException;

    public void setManualSortSeqNum(int seqNum) throws TopazException;

    public void setPartQty(Integer partQty) throws TopazException;

    public void setRenewalQuoteSeqNum(int seqNum) throws TopazException;

    public void setSeqNum(int seqNum) throws TopazException;

    public void setQuoteSectnSeqNum(int seqNum) throws TopazException;

    public void setMaintEndDate(Date maintEndDate) throws TopazException;

    public void setMaintStartDate(Date maintStartDate) throws TopazException;

    public void setRenewalQuoteEndDate(Date renewalQuoteEndDate) throws TopazException;

    public void setPVUOverrideQtyIndCode(String code) throws TopazException;

    public void setCtrctProgCode(String code) throws TopazException;

    public void setCtrctProgCodeDesc(String codeDesc) throws TopazException;

    //public void setCUCode(String code) throws TopazException;

    //public void setCUCodeDscr(String codeDesc) throws TopazException;

    public void setPartTypeCode(String code) throws TopazException;

    public void setRenewalQuoteNum(String quoteNum) throws TopazException;

    public void setRevnStrmCode(String code) throws TopazException;

    public void setRevnStrmCodeDesc(String codeDesc) throws TopazException;

    public void setSwProdBrandCode(String code) throws TopazException;

    public void setSwProdBrandCodeDesc(String codeDesc) throws TopazException;

    public void setSwSubId(String id) throws TopazException;

    public void setWwideProdCode(String code) throws TopazException;

    public void setWwideProdCodeDesc(String codeDesc) throws TopazException;

    public void setStartDtOvrrdFlg(boolean flag) throws TopazException;

    public void setEndDtOvrrdFlg(boolean flag) throws TopazException;

    public void setPartDispAttr(PartDisplayAttr partDispAttr);

    public void setRenwlChgCode(String code) throws TopazException;

    public void setChgType(String type) throws TopazException;

    public void setComment(String comment) throws TopazException;

    public void setChannelExtndPrice(Double price) throws TopazException;

    public void setChannelUnitPrice(Double price) throws TopazException;

    public void setLclExtndPriceIncldTax(Double tax) throws TopazException;

    public void setLocalTaxAmt(Double tax) throws TopazException;

    public void setLclExtndChnlPriceIncldTax(Double tax) throws TopazException;

    public void setLocalChnlTaxAmt(Double tax) throws TopazException;

    public void setPrevsStartDate(Date startDate) throws TopazException;

    public void setPrevsEndDate(Date endDate) throws TopazException;

    public void setAssocdLicPartFlag(boolean has) throws TopazException;

    public void setAddtnlYearCvrageSeqNum(int seqNum) throws TopazException;

    public void setBackDatingFlag(boolean backDatingFlag) throws TopazException;

    public void setChnlOvrrdDiscPct(Double chnlOvrrdDiscPct) throws TopazException;

    public void setChnlStdDiscPct(Double chnlStdDiscPct) throws TopazException;

    public void setTotDiscPct(Double totDiscPct) throws TopazException;

    public void delete() throws TopazException;

    // used as POJO for part group info
    public interface PartGroup {
        String getGroupName();
        
        boolean getExcluGridDeleFlag();

  //      boolean getSpeclBidReordFlag();
    }

    public interface SpBidPartGroup {
        String getSpBidGroupName();

        boolean getLvl0ConfFlag();
        
  //    boolean getSpeclBidReordFlag();
    }



    public PartGroup getPartGroup();

    public SpBidPartGroup getSpBidPartGroup();

    public List getPartGroups();

    public List getSpBidPartGroups();

    /**
     * @param months
     */
    public void setProrateMonths(int months);
    public int getProrateMonths();

    //public PartPriceLevel getPartPriceLevel();

    public void setPersister(Persister p);

    //new attribute for October enhancement

    public Double getOvrrdExtPrice();

    public void setOvrrdExtPrice(Double ovrrdExtPrice) throws TopazException ;

    public Boolean isOfferIncldFlag();

    public void setOfferIncldFlag(Boolean offerIncldFlag) throws TopazException ;
    // clear all prices
    public void clearPrices() throws TopazException;

    public void setDestSeqNum(int itmNum) throws TopazException;

    public boolean isPartGroupRequireSpBid();

    public void  setChargeableUnits(ChargeableUnit[] chargeableUnits);

    public boolean isItemBackDated();

    /**
     * @return Returns the orderStatusCode.
     */
    public String getOrderStatusCode() ;
    /**
     * @param orderStatusCode The orderStatusCode to set.
     */
    public void setOrderStatusCode(String orderStatusCode) ;

    public void setSalesQuoteRefFlag(boolean salesQuoteRefFlag) throws TopazException;

    //added for EOL

    public boolean isHasEolPrice() ;

	public void setHasEolPrice(boolean hasHisPrice) ;

	public int getManualProratedLclUnitPriceFlag();

	public void setManualProratedLclUnitPriceFlag(int manualProratedLclUnitPriceFlag) throws TopazException;

	public boolean canPartBeReactivated();

	public boolean isPartPrcEndSoon();
	public int getProrateWeeks();
	public void setProrateWeeks(int weeks);

	public boolean isContractPart();


	//For compressed coverage
	public Integer getCmprssCvrageMonth();
	public void setCmprssCvrageMonth(Integer cmprssCvrageMonth) throws TopazException;

	public Double getCmprssCvrageDiscPct();
	public void setCmprssCvrageDiscPct(Double cmprssCvrageDiscPct) throws TopazException;

	public boolean hasValidCmprssCvrageMonth();
	public boolean hasValidCmprssCvrageDiscPct();

	//Read only attribute
	public boolean isEligibleForCmprssCvrage();
	public void determineEligibleForCmprssCvrage();

	 /**
     * @return Returns the sControlledDistributionType.
     */
    public String getSControlledDistributionType() ;

    public String getRevnStrmCategoryCode();

    public boolean isLegacyBasePriceUsedFlag();
    public void setLegacyBasePriceUsedFlag(boolean legacyBasePriceUsedFlag) throws TopazException;

    public int getIOriAddtnlMaintCvrageQty();
    public void setIOriAddtnlMaintCvrageQty(int oriAddtnlMaintCvrageQty);

    public int getIRelatedLineItmNum();
    public void setIRelatedLineItmNum(int relatedLineItmNum)throws TopazException;

    public String getSPartType();
    public void setSPartType(String partType) throws TopazException;

    public String getSModByUserID();
    public void setSModByUserID(String modByUserID)  throws TopazException;

    public List getLTierdScaleQty();
    public void setLTierdScaleQty(List tierdScaleQty);

    public Double getSaasBidTCV();
	public void setSaasBidTCV(Double saasBidTCV)  throws TopazException;

	public Double getSaasEntitledTCV();

	public void setSaasEntitledTCV(Double saasEntitledTCV);

	public Integer getICvrageTerm();
	public void setICvrageTerm(Integer contractTerm)  throws TopazException;

	public String getBillgFrqncyCode();
	public String getBillgFrqncyDscr();
	public void setBillgFrqncyCode(String billingFrequency)  throws TopazException;


	public int getSaaSMultipleVal();
	public void setSaaSMultipleVal(int SaaSMultipleVal);
    public boolean isSaasPart();
	public boolean isSaasSetUpPart();
	public boolean isSaasSubscrptnPart();
	public boolean isSaasSubsumedSubscrptnPart();
	public boolean isSaasSubscrptnOvragePart();
	public boolean isSaasSetUpOvragePart();
	public boolean isSaasDaily();
	public boolean isSaasOnDemand();
	public boolean isSaasProdHumanServicesPart();
	public boolean isSaasTcvAcv();
	public boolean isProvisngHold();
	public String getPricingTierModel();
	public Integer getTierQtyMeasre();
    public String getPricngIndCode();
	public String getMigrtnCode();
	public boolean isSaasSLApart();

	public String getSapDocUserStat();
	public void addSapDocUserStat(String sapDocUserStat,String sapDocUserStatDesc);
	public boolean isToUHold();
	public boolean isToUDeclined();
	public String getConfigrtnId();
	public void setConfigrtnId(String configrtnId)  throws TopazException;
	public Integer getProvisngDays();
	public boolean isReplacedPart();
	public void setReplacedPart(boolean replacedPart)  throws TopazException;
	public Integer getRefDocLineNum() ;
	public void setRefDocLineNum(Integer refDocLineNum)  throws TopazException;
	public Integer getRelatedCotermLineItmNum();
	public void setRelatedCotermLineItmNum(Integer relatedCotermLineItmNum)  throws TopazException;
	public Integer getRelatedAlignLineItmNum();
	public void setRelatedAlignLineItmNum(Integer relatedAlignLineItmNum)  throws TopazException;
	public boolean isBeRampuped();
	public void setRampUp(boolean rampUp)  throws TopazException;
	public Integer getCumCvrageTerm();
	public void setCumCvrageTerm(Integer cumCvrageTerm)  throws TopazException;
	public boolean isRampupPart();
	public List getRampUpLineItems();
	public void setRampUpLineItems(List rampUpLineItems);
	public double getBillingPeriods();
	public int getRampUpPeriodNum();
	public void setRampUpPeriodNum(int rampUpPeriodNum);
	public boolean isMasterOvrage();
	public void setMasterOvrage(boolean masterOvrage);
	public Double getSaasBpTCV();
	public void setSaasBpTCV(Double saasBpTCV)  throws TopazException;
	public boolean isWebMigrtdDoc();
	public void setWebMigrtdDocFlag(boolean webMigrtdDocFlag)  throws TopazException;

	//S&S Interim Prior Year Price reference on SQO
	public interface PriorYearSSPrice {
		String getPriorYrLocalUnitPrice12Mnths();
        String getPriorYrCurrncyCode();
        boolean isEvolved();
        boolean isChannel();
        boolean isShowPriorYearPrice();
    }

	public PriorYearSSPrice getPriorYearSSPrice();

	public void setPriorYearSSPrice(PriorYearSSPrice priorYearSSPrice) ;

	public boolean isSaasRenwl();

	public void setSaasRenwl(boolean saasRenwl)  throws TopazException;

	public String getRenewalPricingMethod() ;

	public void setRenewalPricingMethod(String renewalPricingMethod) ;

	public Double getRenewalRsvpPrice() ;
	public void setRenewalRsvpPrice(Double renewalRsvpPrice) ;

	/*
	 * Appliance Item
	 */
	public String getApplianceId();
	public String getMachineType();
	public String getModel();
	public String getSerialNumber() ;

	public String getApplncPocInd() ;

	public String getApplncPriorPoc() ;
	public Date getCustCmmttdArrivlDate();
	public String getCcDscr();

	public boolean isApplncPocInd();
	public boolean isApplncPriorPoc();//isPocSystm: false = New System;
	public boolean isApplncPriorPSC1(); //only for bid compare page. isApplncPriorPSC1 = !isApplncPriorPoc

	public void setMachineType(String machineType) throws TopazException;
	public void setModel(String model) throws TopazException;
	public void setSerialNumber(String serialNumber) throws TopazException;
	public void setApplncPocInd(String appliancePocInd)throws TopazException;
	public void setApplncPriorPoc(String appliancePriorPoc) throws TopazException;

	//Appliance part type
	// contain all appliance type
	public boolean isApplncPart();
	public boolean isApplncServicePack();	// part is Appliance Service Pack behavior
	public boolean isApplncServicePackRenewal();//part is Appliance Service pack renewal part
	public boolean isApplncMain();         // part is appliance part
	public boolean isApplncReinstatement(); // part is Reinstatement part
	public boolean isApplncUpgrade();       //part is Appliance Upgrade part
	public boolean isApplncTransceiver();  // part is Related hardware
	public boolean isApplncRenewal();
	public boolean isApplianceRelatedSoftware();  // contain Related Software
	public boolean isHasApplncId();        // part has valid appliance id
	public boolean isApplncQtyRestrctn();  // part qty is restrict to 1
	public boolean isApplncMainGroup();    // part is ApplncMain Group
	public void setApplncMainGroup(boolean applncMainGroup);
	
	//Appliance#99
	public Date getLineItemCRAD();
	public boolean getApplncSendMFGFLG();
	public void setLineItemCRAD(Date lineItemCRAD) throws TopazException;
	
	//Appliance #145
	public String getNonIBMModel();
	public String getNonIBMSerialNumber();
	
	public void setNonIBMModel(String nonIBMModel) throws TopazException;
	public void setNonIBMSerialNumber(String nonIBMSerialNumber)  throws TopazException;

	//FCT TO PA Finalization
	public String getOrignlSalesOrdRefNum();
	public String getOrignlConfigrtnId();
	public Integer getReplacedTerm();
	public Integer getRenewalCounter();
	public Date getEarlyRenewalCompDate();
	public void setOrignlSalesOrdRefNum(String orignlSalesOrdRefNum) throws TopazException;
	public void setOrignlConfigrtnId(String orignlConfigrtnId) throws TopazException;
	public void setEarlyRenewalCompDate(Date earlyRenewalCompDate) throws TopazException;

	/**
	 *
	 * Check if the quote line item come from a renewal quote.
	 */
	public boolean isReferenceToRenewalQuote();
	public String getPid();

	public boolean isHasProvisngHold();//indicate that quote has at least one line item where the provisioning hold has not been lifted.

	public boolean isExportRestricted(); // indicate the part is export restricted or not

	public String getProductId();

	public void setProductId(String productId);

	public boolean isOrdered();

	public String getRenwlMdlCode();
	public String getRenwlMdlCodeLevel();
	public String getSaasRewlModeCode();
	public boolean isFixedRenwlMdl();
	public boolean isSupportRenwlMdl();
	public String getDefultRenwlMdlCode() ;

	// Saas 10.4 and 10.6
	public void setExtensionEligibilityDate(Date extensionEligibilityDate) throws TopazException;
	public Date getExtensionEligibilityDate() ;

	public YTYGrowth getYtyGrowth();
	public void setYtyGrowth(YTYGrowth ytyGrowth) throws TopazException;


	public boolean isOverridePriceValue();
	public boolean isDiscountValue();
	public boolean isYtyGrowthValue();

    public Object clone() throws CloneNotSupportedException;

	public Double getTierdPrice();

	public List getBillingOptions();
	public void setBillingOptions(List<QuoteLineItemBillingOption> billingOptions);
	
    public Integer getCalculateRemaningTerm();
	public void setCalculateRemaningTerm(Integer calculateRemaningTerm);
	 // Equity Curve 13.2
	public EquityCurve getEquityCurve();
	
	public Double calculateOverallDiscount();
	
	boolean isDisplayModelAndSerialNum();
	
	void setDisplayModelAndSerialNum(boolean isDisplayModelAndSerialNum);
	
	boolean isMandatorySerialNum();
	
	void setMandatorySerialNum(boolean isMandatorySerialNum);
		
	public Double getMinBidUnitPrice();

	public Double getMinBidExtendedPrice();

	public Double getMaxBidUnitPrice();

	public Double getMaxBidExendedPrice();
	
	public String getTouName();
	
	public String getTouURL();
	
	public Boolean getAmendedTouFlag();
	
	public Boolean getAmendedTouFlagB();
	/**
	 * indicate whether the CSA header agreement was amended. 
	 * @since release 14.2
	 */
	public Boolean getHdrAgrmentAmdFlag();
	
	public QuoteLineItem getSetUpRelatedSubsumedPart();
	
	public boolean isSetLineToRsvpSrpFlag();

	public void setSetLineToRsvpSrpFlag(boolean isSetLineToRsvpSrpFlag);
	
	//14.1 SDD 
	public boolean isDivestedPart();
	public String  getPartTypeDsc();
	
	public boolean isPartHasPrice();
	
	public boolean isReinstatementPart();
	// 14.2 ownership transfer
	public boolean isOwerTransferPart();
	
	//14.2 GD
	public void setAddedToLicParts(List addedToLicParts);
	public List getAddedToLicParts();
	public boolean isAddedToLicPart();
	public void setSplitFactor(Double splitFactor);
	public Double getSplitBidExtPriceIfApplicable();

    // 14.2 Appliance serial# validation and deployment
    public DeployModel getDeployModel();
    public void setDeployModelOption(int deployModelOption) throws TopazException ;
    public void setDeployModelId(String deployModelId) throws TopazException;
    public void setDeployModelInvalid(boolean deployModelInvalid)throws TopazException;
    public boolean isDeploymentAssoaciatePart();
    
    public void setModifiedProperty(ModifiedProperty mp);
    public ModifiedProperty getModifiedProperty();
    
    // 14.2 monthly software start
    public boolean isMonthlySoftwarePart();
    //:~ 14.2 monthly software end
    
    // added for dsw 14.2 task# 627593 deploymentID display renewal part
    public String getRenewalDeploymtID();
    public void setRenewalDeploymtID(String deploymtID);

    // 14.4 SCW add on /trade up
	public String getAddReasonCode();
	public String getReplacedReasonCode();
	public String getNewConfigFlag();
    public Integer getOriginatingItemNum();

    public void setAddReasonCode(String addReasonCode) throws TopazException;
    public void setReplacedReasonCode(String replacedReasonCode) throws TopazException;
    public void setNewConfigFlag(String newConfigFlag) throws TopazException;
    public void setOriginatingItemNum(Integer originatingItemNum) throws TopazException;
    
    // 15.1
	public void setIsNewService(Boolean isNewService) throws TopazException;
    public Boolean isNewService();

    // ca.getEndDate()-ProvisioningDate
    public Integer getRemainingTermTillCAEndDate();

    public void setRemainingTermTillCAEndDate(Integer remainingTermTillCAEndDate);
    
    // 15.3 judge if this part is a hybird part
    public void setPartSubType(String partSubType) throws TopazException;
    public String getPartSubType();
}