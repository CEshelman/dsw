package com.ibm.dsw.quote.newquote.spreadsheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpreadSheetPart.java</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-1
 */
/**
 * @author cnwangshp
 *
 */
public class SpreadSheetPart {
    
    public static final String SOURCE_AP_TAB = "AP";
    
    public static final String SOURCE_EP_TAB = "EP";
    
    public static final String SOURCE_SAAS_AP_TAB = "SAAS_AP";
    
    public static final String SOURCE_SAAS_EP_TAB = "SAAS_EP";
    
    private String epPartNumber = "";

    private String epPartDesc = "";
    
    private String epQuantity = "";

    private String epMaintenanceProrated = "";

    private String epAddYears = "";

    private String epOverrideDatesStartDate = "";

    private String epOverideDatesEndDate = "";

    private double epItemPoints = 0d;

    private String epItemPrice = "";
    
    private double epItemPriceDouble = 0d;

    private String epEnterOnlyOneOverridePrice  ;

    private double epEnterOnlyOneDiscountPercent ;
    
    private double epTotalPoints = 0d;

    private double epTotalPrice = 0d;
    
    private String epSTDStartDate = "";

    private String epSTDEndDate = "";

    private String renewalQuoteNumber = "";
    
    private String renewalLineItemSeqNum = "";
    
    private String brandCode = "";
    
    private String partTypeCode = "";
    
    private String chargeUnit = "";
    
    private double totalPoints = 0d;
    
    private double totalPrice = 0d;
    
    //mapping to 1st line of each item in RTF part deteails section
    private String partDescOnRTF = "";
    
    private static String whiteSpace = " ";
    
    private static int  maxPartDescInOneLine = 48;
    
    private double bidUnitPrice = 0d;
    
    private double entitledExtPrice = 0d;
    
    private double bidExtPrice = 0d;
    
    private String RTFStartDate = ""; 
    
    private String RTFEndDate = "";
    
    private boolean prorateFlag = false;
    
    private String lobCode = "";
    
    private boolean showSoftwareMaintFlag = false;
    
    private boolean showStartDateFlag = false;
    
    private boolean showEndDateFlag = false;
    
    private String controlledCodeDesc = "";
    
    private boolean isPricingCallFailed = false;
    
    public static final String NP_MSG = "- No price -";
    
    private int epAddtnlYearCvrageSeqNum;
    
    private int epSortSeqNum;
    
    private double epBPStdDiscPct = -1;
    
    private double epBPOvrrdDiscPct = -1;
    
    private double channelExtPrice = -1;
    
    private double channelUnitPrice = -1;
    
    private boolean isBPDiscOvrrdFlag = false;
    
    private double localUnitPrc = 0d;
    
    private String proratedMonths = "-";
    
    private double proratedMonthsExcel = 0d;
    
    private String proratedWeeks = "-";
    
    private int scale = 2;
    
    private String programDesc = "";
    
    private boolean showNoteRow = false;
    
    private boolean showEOLNote = false;
    
    private boolean showPartGroups = false;
    
    private boolean showHasEOLPriceNote = false;
    
    private boolean showEntitledPriceOverriden = false;
    
    private boolean showEntitledTCVFlag = false;
    
    private boolean showBidTCVFlag = false;
    
    private boolean showStartDateForPdf = false;
    
    private boolean isFtlPart = false;
    
    private boolean obsoletePart = false;
    
    private boolean partUnPublished = false;
    
    private String submittedGroupName = "";
    
    private boolean showPartGroupRow = false;
    
    private boolean partControlled = false;
    
    private boolean addedFromRenewalQuote = false;
    
    private String epBrandDesc = "";
    
    private double comCoverageMonths = 0;
    
    private double epTotalLineDiscount = 0d;
    
    private String quoteRTFBPDiscount = "";
    
    private String sourceTab = "";
    
    private String billingFrequency = "";
    
    private String contractTerm = "";
    
    private double totalContractCommitValue = 0d;
    
    private boolean showBillingFrequencyFlag = false;
    
    private boolean showTermSelectionFlag = false;
    
    private boolean showItemTotContractVal = false;
    
    private boolean showTotalPointsFlag = false;
    
    private String cvrageTermUnit = "";
    
    private boolean showUpToSelection = false;
    
    private boolean decimal4Flag = false;
    
    private boolean noQty = false;
        
    private boolean isSaasPart = false;
    
    private int saasScale;
    
    private boolean usagePart = false;
    
    private boolean saasPartAndHasQty = false;
    
    private List rampUpLineItems = new ArrayList();
    
    private String configurationId = "";
    
    private String rampUpTitle = "";
    
    private boolean bRampUpPart = false; //is rampup part
    
    private boolean showReplaceTitle = false;
    
    private boolean repeatedReplaceTitle = false;

	private String servicesAgreementNum = "";
    
    private String downloadPricingType = "";
    
    private double bpLineItemPrice = 0d;
    
    private boolean tier2ResellerFlag = false;
    
    private boolean submittedQuoteFlag = false;
    
    private double localUnitProratedPrc = -1;
    
    private boolean saasProdHumanServicesPart = false;
    
    private boolean saasSubscrptnPart = false;
    
    private boolean saasSetUpPart = false;
    
    private double billingPeriods = -1;
    
    private boolean disableOverrideUnitPriceInput = false;
    
    private boolean replacedPartFlag = false;
    
    private boolean showBpTCVFlag = false;
    
    private String configrtrConfigrtnId = "";
    
    private String refDocNum = "";
    
    private String errorCode = "";
    
    private String configrtnAction = "";
    
    private String configrationEndDate = "";
    
    private String coTermToConfigrtnId = "";
    
    private boolean overrideFlag = false;

    private boolean migrationFlag = false;
    
    private double entitledRateVal = -1;
    
    private double bidRateVal = -1;
    
    private String bpRateVal = "";
    
    private boolean exportRestricted = false;
    
    private boolean saasSubscrptnOvragePart = false;
    
    private String destSeqNum = "";
    
    private String iRelatedLineItmNum = "";
    
    private String relatedLinePartNum = "";
    
    private String provisioningId = "";
    
    private boolean showEventBasedDescription = false;//whether show "event based" text and subsumed part.
    
    private boolean showPart = false;//kenexa: indicate this part whether be exported.
    
    // Karl added ----begin
    //---equity curve info
    //-- top performer
    private String preferDiscount = "";
    private String preferBidUnitPrice = "";
    private String preferBidExtendedPrice = "";
    //---market average
    private String maxDiscount = "";
    private String maxBidUnitPrice = "";
    private String maxBidExendedPrice = "";
    private String priorCustomerPurchase = "";
    
    private String partTypeDsc = "";
    //Karl  added ---end
	public String getPreferDiscount() {
		return preferDiscount == null ? "" : preferDiscount;
	}

	public void setPreferDiscount(String preferDiscount) {
		this.preferDiscount = preferDiscount;
	}

	public String getPreferBidUnitPrice() {
		return preferBidUnitPrice == null ? "" : preferBidUnitPrice;
	}

	public void setPreferBidUnitPrice(String preferBidUnitPrice) {
		this.preferBidUnitPrice = preferBidUnitPrice;
	}

	public String getPreferBidExtendedPrice() {
		return preferBidExtendedPrice == null ? "" : preferBidExtendedPrice;
	}

	public void setPreferBidExtendedPrice(String preferBidExtendedPrice) {
		this.preferBidExtendedPrice = preferBidExtendedPrice;
	}

	public String getMaxDiscount() {
		return maxDiscount == null ? "" : maxDiscount;
	}

	public void setMaxDiscount(String maxDiscount) {
		this.maxDiscount = maxDiscount;
	}

	public String getMaxBidUnitPrice() {
		return maxBidUnitPrice == null ? "" : maxBidUnitPrice;
	}

	public void setMaxBidUnitPrice(String maxBidUnitPrice) {
		this.maxBidUnitPrice = maxBidUnitPrice;
	}

	public String getMaxBidExendedPrice() {
		return maxBidExendedPrice == null ? "" : maxBidExendedPrice;
	}

	public void setMaxBidExendedPrice(String maxBidExendedPrice) {
		this.maxBidExendedPrice = maxBidExendedPrice;
	}

	public String getPriorCustomerPurchase() {
		return priorCustomerPurchase;
	}

	public void setPriorCustomerPurchase(String priorCustomerPurchase) {
		this.priorCustomerPurchase = priorCustomerPurchase;
	}

	public String getProvisioningId() {
		return provisioningId;
	}

	public void setProvisioningId(String provisioningId) {
		this.provisioningId = provisioningId;
	}

	public boolean isbRampUpPart() {
		return bRampUpPart;
	}

	public void setbRampUpPart(boolean bRampUpPart) {
		this.bRampUpPart = bRampUpPart;
	}

	public String getDestSeqNum() {
		return destSeqNum;
	}

	public void setDestSeqNum(String destSeqNum) {
		this.destSeqNum = destSeqNum;
	}

	public String getiRelatedLineItmNum() {
		return iRelatedLineItmNum;
	}

	public void setiRelatedLineItmNum(String iRelatedLineItmNum) {
		this.iRelatedLineItmNum = iRelatedLineItmNum;
	}
	
	public String getRelatedLinePartNum() {
		return relatedLinePartNum;
	}

	public void setRelatedLinePartNum(String relatedLinePartNum) {
		this.relatedLinePartNum = relatedLinePartNum;
	}

	public boolean isSaasSubscrptnOvragePart() {
		return saasSubscrptnOvragePart;
	}

	public void setSaasSubscrptnOvragePart(boolean saasSubscrptnOvragePart) {
		this.saasSubscrptnOvragePart = saasSubscrptnOvragePart;
	}

	public boolean isExportRestricted() {
		return exportRestricted;
	}

	public void setExportRestricted(boolean exportRestricted) {
		this.exportRestricted = exportRestricted;
	}

	public String getBpRateVal() {
		return bpRateVal;
	}

	public void setBpRateVal(String bpRateVal) {
		this.bpRateVal = bpRateVal;
	}

	public double getEntitledRateVal() {
		return entitledRateVal;
	}

	public void setEntitledRateVal(double entitledRateVal) {
		this.entitledRateVal = entitledRateVal;
	}

	public double getBidRateVal() {
		return bidRateVal;
	}

	public void setBidRateVal(double bidRateVal) {
		this.bidRateVal = bidRateVal;
	}

	public boolean isMigrationFlag() {
		return migrationFlag;
	}

	public void setMigrationFlag(boolean migrationFlag) {
		this.migrationFlag = migrationFlag;
	}

	public String getConfigrtrConfigrtnId() {
		return configrtrConfigrtnId;
	}

	public void setConfigrtrConfigrtnId(String configrtrConfigrtnId) {
		this.configrtrConfigrtnId = configrtrConfigrtnId;
	}

	public String getRefDocNum() {
		return refDocNum;
	}

	public void setRefDocNum(String refDocNum) {
		this.refDocNum = refDocNum;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getConfigrtnAction() {
		return configrtnAction;
	}

	public void setConfigrtnAction(String configrtnAction) {
		this.configrtnAction = configrtnAction;
	}

	public String getConfigrationEndDate() {
		return configrationEndDate;
	}

	public void setConfigrationEndDate(String configrationEndDate) {
		this.configrationEndDate = configrationEndDate;
	}

	public String getCoTermToConfigrtnId() {
		return coTermToConfigrtnId;
	}

	public void setCoTermToConfigrtnId(String coTermToConfigrtnId) {
		this.coTermToConfigrtnId = coTermToConfigrtnId;
	}

	public boolean isOverrideFlag() {
		return overrideFlag;
	}

	public void setOverrideFlag(boolean overrideFlag) {
		this.overrideFlag = overrideFlag;
	}

	public boolean isShowBpTCVFlag() {
		return showBpTCVFlag;
	}

	public void setShowBpTCV(boolean showBpTCVFlag) {
		this.showBpTCVFlag = showBpTCVFlag;
	}

	public boolean isTier2ResellerFlag() {
		return tier2ResellerFlag;
	}

	public void setTier2ResellerFlag(boolean tier2ResellerFlag) {
		this.tier2ResellerFlag = tier2ResellerFlag;
	}
    
	public boolean isSubmittedQuoteFlag() {
		return submittedQuoteFlag;
	}

	public void setSubmittedQuoteFlag(boolean submittedQuoteFlag) {
		this.submittedQuoteFlag = submittedQuoteFlag;
	}

	public double getBpLineItemPrice() {
		return bpLineItemPrice;
	}
	public void setBpLineItemPrice(double bpLineItemPrice) {
		this.bpLineItemPrice = bpLineItemPrice;
	}
	public String getDownloadPricingType() {
		return downloadPricingType;
	}
	public void setDownloadPricingType(String downloadPricingType) {
		this.downloadPricingType = downloadPricingType;
	}
    
	public String getServicesAgreementNum() {
		return servicesAgreementNum == null ? "" : servicesAgreementNum;
	}

	public void setServicesAgreementNum(String servicesAgreementNum) {
		this.servicesAgreementNum = servicesAgreementNum;
	}

	public boolean isShowReplaceTitle() {
		return showReplaceTitle;
	}

	public void setShowReplaceTitle(boolean replaceTitle) {
		this.showReplaceTitle = replaceTitle;
	}
	
    public boolean isRepeatedReplaceTitle() {
		return repeatedReplaceTitle;
	}

	public void setRepeatedReplaceTitle(boolean repeatedReplaceTitle) {
		this.repeatedReplaceTitle = repeatedReplaceTitle;
	}

	public String getRampUpTitle() {
		return rampUpTitle;
	}

	public void setRampUpTitle(String rampUpTitle) {
		this.rampUpTitle = rampUpTitle;
	}

	public List getRampUpLineItems() {
		return rampUpLineItems;
	}

	public void setRampUpLineItems(List rampUpLineItems) {
		this.rampUpLineItems = rampUpLineItems;
	}
	
	public String getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(String configurationId) {
		this.configurationId = configurationId;
	}

	public boolean isSaasPart() {
		return isSaasPart;
	}

	public void setSaasPart(boolean isSaasPart) {
		this.isSaasPart = isSaasPart;
	}
    
	public int getSaasScale() {
		return saasScale;
	}

	public void setSaasScale(int saasScale) {
		this.saasScale = saasScale;
	}

		public boolean isShowUpToSelection() {
		return showUpToSelection;
	}

	public void setShowUpToSelection(boolean showUpToSelection) {
		this.showUpToSelection = showUpToSelection;
	}

	public String getCvrageTermUnit() {
		return cvrageTermUnit;
	}

	public void setCvrageTermUnit(String cvrageTermUnit) {
		this.cvrageTermUnit = cvrageTermUnit;
	}

	public double getTotalContractCommitValue() {
		return totalContractCommitValue;
	}

	public void setTotalContractCommitValue(Double totalContractCommitValue) {
		if(totalContractCommitValue != null){
			this.totalContractCommitValue = totalContractCommitValue.doubleValue();
		}
	}

	public String getContractTerm() {
		return contractTerm == null? "":contractTerm;
	}

	public void setContractTerm(String contractTerm) {
		this.contractTerm = contractTerm;
	}

	public String getBillingFrequency() {
		return billingFrequency == null? "":billingFrequency;
	}

	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}
	
	/**
     * @return Returns the sourceTab.
     */
    public String getSourceTab() {
        return sourceTab;
    }
    /**
     * @param sourceTab The sourceTab to set.
     */
    public void setSourceTab(String sourceTab) {
        this.sourceTab = sourceTab;
    }
	/**
	 * @return Returns the epTotalLineDiscount.
	 */
	public double getEpTotalLineDiscount() {
		return epTotalLineDiscount;
	}
	/**
	 * @param epTotalLineDiscount The epTotalLineDiscount to set.
	 */
	public void setEpTotalLineDiscount(double epTotalLineDiscount) {
		this.epTotalLineDiscount = epTotalLineDiscount;
	}
    /**
     * @return Returns the comCoverageMonths.
     */
    public double getComCoverageMonths() {
        return comCoverageMonths;
    }
    /**
     * @param comCoverageMonths The comCoverageMonths to set.
     */
    public void setComCoverageMonths(Integer comCoverageMonths) {
    	if(comCoverageMonths != null){
    		this.comCoverageMonths = comCoverageMonths.doubleValue();
    	}
    }
	public boolean isAddedFromRenewalQuote() {
		return addedFromRenewalQuote;
	}
	public void setAddedFromRenewalQuote(boolean addedFromRenewalQuote) {
		this.addedFromRenewalQuote = addedFromRenewalQuote;
	}
	public boolean isPartControlled() {
		return partControlled;
	}
	public void setPartControlled(boolean partControlled) {
		this.partControlled = partControlled;
	}
	public boolean isShowPartGroupRow() {
		return showPartGroupRow;
	}
	public void setShowPartGroupRow(boolean showPartGroupRow) {
		this.showPartGroupRow = showPartGroupRow;
	}
	public String getSubmittedGroupName() {
		return submittedGroupName;
	}
	public void setSubmittedGroupName(String submittedGroupName) {
		this.submittedGroupName = submittedGroupName;
	}
	public boolean isPartUnPublished() {
		return partUnPublished;
	}
	public void setPartUnPublished(boolean partUnPublished) {
		this.partUnPublished = partUnPublished;
	}
	public boolean isObsoletePart() {
		return obsoletePart;
	}
	public void setObsoletePart(boolean obsoletePart) {
		this.obsoletePart = obsoletePart;
	}
	public boolean isShowStartDateForPdf() {
		return showStartDateForPdf;
	}
	public void setShowStartDateForPdf(boolean showStartDateForPdf) {
		this.showStartDateForPdf = showStartDateForPdf;
	}
	public boolean isShowEntitledPriceOverriden() {
		return showEntitledPriceOverriden;
	}
	public void setShowEntitledPriceOverriden(boolean showEntitledPriceOverriden) {
		this.showEntitledPriceOverriden = showEntitledPriceOverriden;
	}
	public boolean isShowEntitledTCVFlag() {
		return showEntitledTCVFlag;
	}
	public void setShowEntitledTCVFlag(boolean showEntitledTCVFlag) {
		this.showEntitledTCVFlag = showEntitledTCVFlag;
	}
	public boolean isShowBidTCVFlag() {
		return showBidTCVFlag;
	}
	public void setShowBidTCVFlag(boolean showBidTCVFlag) {
		this.showBidTCVFlag = showBidTCVFlag;
	}
	public boolean isShowTotalPointsFlag() {
		return showTotalPointsFlag;
	}
	public void setShowTotalPointsFlag(boolean showTotalPointsFlag) {
		this.showTotalPointsFlag = showTotalPointsFlag;
	}
	public boolean isShowEOLNote() {
		return showEOLNote;
	}
	public void setShowEOLNote(boolean showEOLNote) {
		this.showEOLNote = showEOLNote;
	}
	public boolean isShowHasEOLPriceNote() {
		return showHasEOLPriceNote;
	}
	public void setShowHasEOLPriceNote(boolean showHasEOLPriceNote) {
		this.showHasEOLPriceNote = showHasEOLPriceNote;
	}
	public boolean isShowNoteRow() {
		return showNoteRow;
	}
	public void setShowNoteRow(boolean showNoteRow) {
		this.showNoteRow = showNoteRow;
	}
	public boolean isShowPartGroups() {
		return showPartGroups;
	}
	public void setShowPartGroups(boolean showPartGroups) {
		this.showPartGroups = showPartGroups;
	}
	public String getProgramDesc() {
		return programDesc;
	}
	
	public void setProgramDesc(String programDesc) {
		this.programDesc = programDesc;
	}
	public boolean isBPDiscOvrrdFlag() {
		return isBPDiscOvrrdFlag;
	}
	
	public void setBPDiscOvrrdFlag(boolean isBPDiscOvrrdFlag) {
		this.isBPDiscOvrrdFlag = isBPDiscOvrrdFlag;
	}
    
    public String getProratedMonths(){
    	return proratedMonths;
    }
    
    public double getProratedMonthsExcel(){
    	return proratedMonthsExcel;
    }
    
    public void setProratedMonths(int proratedMonths){
    	this.proratedMonths = String.valueOf(proratedMonths);
    	this.proratedMonthsExcel = (double)proratedMonths;
    }
    
	/**
	 * @return Returns the lobCode.
	 */
	public String getLobCode() {
		return lobCode;
	}
	/**
	 * @param lobCode The lobCode to set.
	 */
	public void setLobCode(String lobCode) {
		this.lobCode = lobCode;
	}
	/**
	 * @return Returns the rTFEndDate.
	 */
	public String getRTFEndDate() {
		return RTFEndDate;
	}
	/**
	 * @param endDate The rTFEndDate to set.
	 */
	public void setRTFEndDate(String endDate) {
		RTFEndDate = endDate;
	}
	/**
	 * @return Returns the rTFStartDate.
	 */
	public String getRTFStartDate() {
		return RTFStartDate;
	}
	/**
	 * @param startDate The rTFStartDate to set.
	 */
	public void setRTFStartDate(String startDate) {
		RTFStartDate = startDate;
	}
    
    
	public String getRTFItemPrice(){
		if(this.isSaasPart && this.epItemPriceDouble == 0d){
	    	return StringUtils.rightPad("",11);
	    }else{
			return StringUtils.rightPad(getEpItemPrice(),11);
		}
	}
	
    public String getRTFQty() {
    	return StringUtils.rightPad("  "+this.getEpQuantity(),9);
    }
    
    public String getRTFPartNum() {
    	return StringUtils.rightPad(this.getEpPartNumber(),8);
    }
    
    public String getRTFPoint() {
    	return StringUtils.rightPad(String.valueOf(this.getEpItemPoints()),8);
    }
    
    public String getRTFTotalPoints() {
    	return StringUtils.rightPad(DecimalUtil.format(this.getTotalPoints(),2),8);
    }
    
    public String getRTFOverridePrice() {
    	return StringUtils.rightPad(this.getEpEnterOnlyOneOverridePrice(),7);
    }
    
    public String getRTFOverrideDisc() {
    	String retVal = null;
    	double overrideDisc = this.getEpEnterOnlyOneDiscountPercent();
    	if(overrideDisc != 0d)  {
    		retVal = StringUtils.rightPad(String.valueOf(DecimalUtil.roundAsDouble(overrideDisc*100,3))+"%",9);
    	} else {
    		retVal = StringUtils.rightPad(StringUtils.EMPTY,9);
    	}
    	return retVal;
    }
    
    public String getRTFOverrideDiscPct() {
    	String retVal = null;
    	double overrideDisc = this.getEpEnterOnlyOneDiscountPercent();
    	if(overrideDisc != 0d)  {
    		retVal = StringUtils.rightPad(String.valueOf(DecimalUtil.roundAsDouble(overrideDisc*100,2)),9);
    	} else {
    		retVal = StringUtils.rightPad(StringUtils.EMPTY,9);
    	}
    	return retVal;
    }
	/**
	 * @return Returns the binUnitPrice.
	 */
	public String getRTFBidUnitPrice() {
		//return "getRTFBidUnitPrice";
		if (isPricingCallFailed) {
	        return StringUtils.leftPad(NP_MSG,12);
	    } else {
	    	if(this.isSaasPart && this.bidUnitPrice == 0d){
	    		return "";
	    	}else{
	    		if(this.isTier2ResellerFlag() && this.isSubmittedQuoteFlag()) {
	    			return DecimalUtil.format(this.epItemPriceDouble,decimal4Flag? 4 : scale);
	    		}
	    		return DecimalUtil.format(this.bidUnitPrice,decimal4Flag? 4 : scale);
	    	}
	    }
	}
	
	/**
	 * @return Returns the binUnitPrice.
	 */
	public String getRTFBidUnitPriceRightPad() {
		if (isPricingCallFailed) {
	        return StringUtils.rightPad(NP_MSG,12);
	    } else {
	    	if(this.isSaasPart){
	    		if(this.bidUnitPrice == 0d){
	    			return StringUtils.rightPad("",12);
	    		}else{
	    			return StringUtils.rightPad(DecimalUtil.format(this.bidUnitPrice,this.saasScale),12);
	    		}
	    	}else{
	    		return StringUtils.rightPad(DecimalUtil.format(this.bidUnitPrice,scale),12);
	    	}
	    }
	}
	/**
	 * @return Returns the binUnitPrice.
	 */
	public double getBidUnitPrice() {
		return bidUnitPrice;
	}
	public String getSBidUnitPrice() {
		if (isPricingCallFailed) {
	        return NP_MSG;
	    } else {
	        return DecimalUtil.format(this.bidUnitPrice,scale);
	    }
	}
	/**
	 * @param binUnitPrice The binUnitPrice to set.
	 */
	public void setBidUnitPrice(double binUnitPrice) {
		this.bidUnitPrice = binUnitPrice;
	}
	/**
	 * @return Returns the entitledExtPrice.
	 */

	/**
	 * @return Returns the firstLineOnRTFPartDetails.
	 */
	public String getPartDescOnRTF() {
		String[] wrappedSentence = wrapSentence(this.getEpPartDesc(), whiteSpace, maxPartDescInOneLine);
		StringBuffer buffer = new StringBuffer();
		for(int index = 0 ; index < wrappedSentence.length; index++) {
			if(index == 1 && this.showUpToSelection == true){
				buffer.append(this.getRTFQty());
				buffer.append("         ");
			}
			
			buffer.append(StringUtils.rightPad(wrappedSentence[index],maxPartDescInOneLine));
			if(index == 0){
				if(this.isSaasPart() && this.isNoQty()){
					buffer.append("");
				}else{
					if("PA".equals(getLobCode())){
						buffer.append("  ");
						if(this.isSaasPart() && this.isNoQty()){
							buffer.append(StringUtils.rightPad("", 10));
						}else{
							buffer.append(StringUtils.rightPad(getRTFTotalPoints() + "  ",10));
						}
						if (getDownloadPricingType().equalsIgnoreCase("business partner pricing")) {
							buffer.append(StringUtils.rightPad(getRTFbpLineItemPrice(),10));
						} else {
							if(!this.isSubmittedQuoteFlag()){
							//draft quote	
								if(isTier2ResellerFlag()){
									if(!this.showReplaceTitle){
										buffer.append(StringUtils.rightPad(getRTFBidExtPrice(),10));
									}
								}else{
									buffer.append(StringUtils.rightPad(getRTFBidExtPrice(),10));
								}
							}else{
							//submitted quote	
								if(isTier2ResellerFlag()){
									if(!this.showReplaceTitle){
										buffer.append(StringUtils.rightPad(getRTFEntitledExtPrice(),10));
									}
								}else{
									buffer.append(StringUtils.rightPad(getRTFBidExtPrice(),10));
								}
							}
						}					
					}else{
						buffer.append("  ");
						if (getDownloadPricingType().equalsIgnoreCase("business partner pricing")) {
							buffer.append(StringUtils.rightPad(getRTFbpLineItemPrice(),10));
						} else {
							if(!this.isSubmittedQuoteFlag()){
							//draft quote	
								if(isTier2ResellerFlag()){
									if(!this.showReplaceTitle){
										buffer.append(StringUtils.rightPad(getRTFBidExtPrice(),10));
									}
								}else{
									buffer.append(StringUtils.rightPad(getRTFBidExtPrice(),10));
								}
							}else{
							//submitted quote	
								if(isTier2ResellerFlag()){
									if(!this.showReplaceTitle){
										buffer.append(StringUtils.rightPad(getRTFEntitledExtPrice(),10));
									}
								}else{
									buffer.append(StringUtils.rightPad(getRTFBidExtPrice(),10));
								}
							}
						}
					}
				}
			}			

			if(index != wrappedSentence.length -1){
				if(index == 0 && this.showUpToSelection == true){
					buffer.append("\\par");
				}else{
					buffer.append("\\par                  ");
				}
			}
		}
		
		if(wrappedSentence.length == 1 && this.showUpToSelection == true){
			buffer.append("\\par");
			buffer.append(this.getRTFQty());			
		}
		return buffer.toString();
	}
	
	public String getPartDescOnExecSummaryRTF(){
		String[] wrappedSentence = wrapSentence(this.getEpPartDesc(), whiteSpace, 55);
		StringBuffer buffer = new StringBuffer();
		for(int index = 0 ; index < wrappedSentence.length; index++) {
				if(index == 0)
					buffer.append(StringUtils.rightPad(wrappedSentence[index].trim(),53));
				else{
					String line = StringUtils.rightPad(wrappedSentence[index].trim(),53);
					buffer.append(StringUtils.leftPad(line, 53+28));
				}
				if(index != wrappedSentence.length -1){
					buffer.append("\\par ");
				}
		}
		return buffer.toString();
	}
	
	public void setTotalPoints(double totalPoints){
	    this.totalPoints = totalPoints; 
	}
	
	public void setTotalPrice(double totalPrice){
	    this.totalPrice = totalPrice; 
	}
	
	/**
	 * @return Returns the totalPoints.
	 */
	public double getTotalPoints() {
		return totalPoints;
	}
	/**
	 * @return Returns the totalPrice.
	 */
	public double getTotalPrice() {
		return totalPrice;
	}
	/**
	 * @return Returns the chargeUnit.
	 */
	public String getChargeUnit() {
		return chargeUnit;
	}
	/**
	 * @param chargeUnit The chargeUnit to set.
	 */
	public void setChargeUnit(String chargeUnit) {
		this.chargeUnit = chargeUnit;
	}
    /**
     * @return Returns the brandCode.
     */
    public String getBrandCode() {
        return brandCode;
    }
    /**
     * @param brandCode The brandCode to set.
     */
    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }
    /**
     * @return Returns the renewalLineItemSeqNum.
     */
    public String getRenewalLineItemSeqNum() {
        return StringUtils.trimToEmpty(renewalLineItemSeqNum);
    }
    /**
     * @param renewalLineItemSeqNum The renewalLineItemSeqNum to set.
     */
    public void setRenewalLineItemSeqNum(String renewalLineItemSeqNum) {
        this.renewalLineItemSeqNum = renewalLineItemSeqNum;
    }
    /**
     * @return Returns the renewalQuoteNum.
     */
    public String getRenewalQuoteNumber() {
        return StringUtils.trimToEmpty(renewalQuoteNumber);
    }
    /**
     * @param renewalQuoteNum The renewalQuoteNum to set.
     */
    public void setRenewalQuoteNumber(String renewalQuoteNum) {
        this.renewalQuoteNumber = renewalQuoteNum;
    }
    /**
     * @return Returns the epTotalPoints.
     */
    public double getEpTotalPoints() {
        return epTotalPoints;
    }
    /**
     * @param epTotalPoints The epTotalPoints to set.
     */
    public void setEpTotalPoints(double epTotalPoints) {
        this.epTotalPoints = epTotalPoints;
    }
    /**
     * @return Returns the epTotalPrice.
     */
    public double getEpTotalPrice() {
        return epTotalPrice;
    }
    /**
     * @param epTotalPrice The epTotalPrice to set.
     */
    public void setEpTotalPrice(double epTotalPrice) {
        this.epTotalPrice = epTotalPrice;
    }
    /**
     * @param epEenterOnlyOneOveridePrice The epEenterOnlyOneOveridePrice to set.
     */
    public void setEpEnterOnlyOneOverridePrice(
             String epEenterOnlyOneOveridePrice) {
        this.epEnterOnlyOneOverridePrice = epEenterOnlyOneOveridePrice;
    }
    /**
     * @return Returns the epSTDEndDate.
     */
    public String getEpSTDEndDate() {
        return normalizeDateString(epSTDEndDate);
    }
    /**
     * @param epSTDEndDate The epSTDEndDate to set.
     */
    public void setEpSTDEndDate(String epSTDEndDate) {
        this.epSTDEndDate = epSTDEndDate;
    }
    /**
     * @return Returns the epSTDStartDate.
     */
    public String getEpSTDStartDate() {
        return normalizeDateString(epSTDStartDate);
    }
    /**
     * @param epSTDStartDate The epSTDStartDate to set.
     */
    public void setEpSTDStartDate(String epSTDStartDate) {
        this.epSTDStartDate = epSTDStartDate;
    }
    /**
     * @return Returns the epPartDesc.
     */
    public String getEpPartDesc() {
        return StringUtils.trimToEmpty(epPartDesc);
    }
    /**
     * @param epPartDesc The epPartDesc to set.
     */
    public void setEpPartDesc(String epPartDesc) {
        this.epPartDesc = epPartDesc;
    }
    /**
     * @return Returns the exportedPartORDEndDate.
     */
    public String getEpOverideDatesEndDate() {
        return normalizeDateString(epOverideDatesEndDate);
    }

    /**
     * @param exportedPartORDEndDate
     *            The exportedPartORDEndDate to set.
     */
    public void setEpOverideDatesEndDate(String exportedPartORDEndDate) {
        this.epOverideDatesEndDate = exportedPartORDEndDate;
    }

    /**
     * @return Returns the exportedPartORDStartDate.
     */
    public String getEpOverrideDatesStartDate() {
        return normalizeDateString(epOverrideDatesStartDate);
    }

    /**
     * @param exportedPartORDStartDate
     *            The exportedPartORDStartDate to set.
     */
    public void setEpOverrideDatesStartDate(String exportedPartORDStartDate) {
        this.epOverrideDatesStartDate = exportedPartORDStartDate;
    }

    /**
     * @return Returns the exportedDiscPerc.
     */
    public double getEpEnterOnlyOneDiscountPercent() {
        return epEnterOnlyOneDiscountPercent;
    }

    /**
     * @param exportedDiscPerc
     *            The exportedDiscPerc to set.
     */
    public void setEpEnterOnlyOneDiscountPercent(double exportedDiscPerc) {
        this.epEnterOnlyOneDiscountPercent = exportedDiscPerc;
    }

    /**
     * @return Returns the exportedItemPrice.
     */
    public String getEpItemPrice() {
        return epItemPrice;
    }
    
    public double getEpItemPriceDouble() {
        return epItemPriceDouble;
    }

    /**
     * @param exportedItemPrice
     *            The exportedItemPrice to set.
     */
    public void setEpItemPrice(Double exportedItemPrice) {
    	double value = exportedItemPrice==null ? 0d : exportedItemPrice.doubleValue();
        this.epItemPrice = DecimalUtil.format(value,scale);
        this.epItemPriceDouble = value;
    }

    /**
     * @return Returns the exportedPartAddYear.
     */
    public String getEpAddYears() {
        return StringUtils.trimToEmpty(epAddYears);
    }

    /**
     * @param exportedPartAddYear
     *            The exportedPartAddYear to set.
     */
    public void setEpAddYears(String exportedPartAddYear) {
        this.epAddYears = exportedPartAddYear;
    }


    /**
     * @return Returns the exportedPartItemPoint.
     */
    public double getEpItemPoints() {
        return epItemPoints;
    }
    
    public String getSEpItemPoints() {
		if (isPricingCallFailed) {
	        return NP_MSG;
	    } else {
	        return DecimalUtil.format(this.epItemPoints,scale);
	    }
	}

    /**
     * @param exportedPartItemPoint
     *            The exportedPartItemPoint to set.
     */
    public void setEpItemPoints(double exportedPartItemPoint) {
        this.epItemPoints = exportedPartItemPoint;
    }

    /**
     * @return Returns the exportedPartMaintenanceType.
     */
    public String getEpMaintenanceProrated() {
        return StringUtils.trimToEmpty(epMaintenanceProrated);
    }

    /**
     * @param exportedPartMaintenanceType
     *            The exportedPartMaintenanceType to set.
     */
    public void setEpMaintenanceProrated(String exportedPartMaintenanceType) {
        this.epMaintenanceProrated = exportedPartMaintenanceType;
    }

    /**
     * @return Returns the exportedPartNum.
     */
    public String getEpPartNumber() {
        return StringUtils.trimToEmpty(epPartNumber);
    }

    /**
     * @param exportedPartNum
     *            The exportedPartNum to set.
     */
    public void setEpPartNumber(String exportedPartNum) {
        this.epPartNumber = exportedPartNum;
    }

    /**
     * @return Returns the exportedPartORDPrice.
     */
    public String getEpEnterOnlyOneOverridePrice() {
        return epEnterOnlyOneOverridePrice == null? "" : epEnterOnlyOneOverridePrice.toString();
    }

   

    /**
     * @return Returns the exportedPartQTY.
     */
    public String getEpQuantity() {
        return StringUtils.trimToEmpty(epQuantity).equals("")?"N/A":StringUtils.trimToEmpty(epQuantity);
    }

    /**
     * @param exportedPartQTY
     *            The exportedPartQTY to set.
     */
    public void setEpQuantity(String exportedPartQTY) {
        this.epQuantity = exportedPartQTY;
    }
    
	/**
	 * @return Returns the partType.
	 */
	public String getPartType() {
		return partTypeCode;
	}
	/**
	 * @param partType The partType to set.
	 */
	public void setPartType(String partType) {
		this.partTypeCode = partType;
	}
	
	/**
	 * @return Returns the bpLineItemPrice.
	 */
	public String getRTFbpLineItemPrice() {
	    if (isPricingCallFailed) {
	        return StringUtils.leftPad(NP_MSG,12);
	    } else {
	    	if(this.isSaasPart && this.getBpLineItemPrice() == 0d){
	    		return "";
	    	}else{
	    		return StringUtils.leftPad(DecimalUtil.format(this.getBpLineItemPrice(),scale),12);
	    	}
	    }
	}
	
	/**
	 * @return Returns the bidExtPrice.
	 */
	public String getRTFBidExtPrice() {
	    if (isPricingCallFailed) {
	        return StringUtils.leftPad(NP_MSG,12);
	    } else {
	    	if(this.isSaasPart && this.getTotalPrice() == 0d){
	    		return "";
	    	}else{
	    		return StringUtils.leftPad(DecimalUtil.format(this.getTotalPrice(),scale),12);
	    	}
	    }
	}
	
	public String getRTFBidExtPriceRightPad() {
	    if (isPricingCallFailed) {
	        return StringUtils.rightPad(NP_MSG,12);
	    } else {
	    	if(this.isSaasPart && this.getTotalPrice() == 0d){
	    		return StringUtils.rightPad("",12);
	    	}else{
	    		return StringUtils.rightPad(DecimalUtil.format(this.getTotalPrice(),scale),12);
	    	}
	    }
	}
	
	public double getBidExtPriceAsDouble() {
		return bidExtPrice;
	}
	
	/**
	 * @param bidExtPrice The bidExtPrice to set.
	 */
	public void setBidExtPrice(double bidExtPrice) {
		this.bidExtPrice = bidExtPrice;
	}
	
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if (obj instanceof SpreadSheetPart) {
            return false;
        }
        SpreadSheetPart another = (SpreadSheetPart) obj;
        return another.getEpPartNumber().equals(this.getEpPartNumber());
    }
    
    

    /**
     * @return Returns the controlledCodeDesc.
     */
    public String getControlledCodeDesc() {
        return controlledCodeDesc;
    }
    /**
     * @param controlledCodeDesc The controlledCodeDesc to set.
     */
    public void setControlledCodeDesc(String controlledCodeDesc) {
        this.controlledCodeDesc = controlledCodeDesc;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
      return new HashCodeBuilder().append(this.getEpPartNumber()).hashCode();
    }

    public String toXMLString() throws Exception {
        Map propsMap = BeanUtils.describe(this);
        StringBuffer bf = new StringBuffer("<eqPart ");

        Set keys = propsMap.entrySet();
        for (Iterator it = keys.iterator(); it.hasNext();) {
            java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
            bf.append(entry.getKey() + " =\"" + StringEscapeUtils.escapeXml(entry.getValue()== null ? "" : entry.getValue().toString()) + "\"  ");
        }
        bf.append(">");
        if(this.isSaasPart()){
        	List rampUpLineitems = this.getRampUpLineItems();
        	if(rampUpLineitems != null && rampUpLineitems.size()>0){
        		bf.append("<rampUpItems>");
            	for(int j = 0; j < rampUpLineitems.size(); j++){
            		SpreadSheetPart rampUpPart = (SpreadSheetPart)rampUpLineitems.get(j);
            		bf.append(rampUpPart.toXMLString());
            	}
            	bf.append("</rampUpItems>");
        	}
        }
        bf.append("</eqPart>");
        return bf.toString();
    }
    
    public boolean isNonContractPart() {
    	boolean isNonContractPart = false;
    	
    	if (this.getPartType() == null) {
    		isNonContractPart = false;
    	} else {
    		isNonContractPart = !PartPriceConstants.PartTypeCode.PACTRCT.equals(this.getPartType());
    	}
    	return isNonContractPart;
    }

	/**
	 * @param prorateFlag
	 */
	public void setpProrateFlag(boolean prorateFlag) {
		this.prorateFlag = prorateFlag;
	}
	
	/**
	 * @return Returns the prorateFlag.
	 */
	public boolean isProrateFlag() {
		return prorateFlag;
	}
	
	
	/**
	 * @return Returns the showEndDateFlag.
	 */
	public boolean isShowEndDateFlag() {
		return showEndDateFlag;
	}
	/**
	 * @param showEndDateFlag The showEndDateFlag to set.
	 */
	public void setShowEndDateFlag(boolean showEndDateFlag) {
		this.showEndDateFlag = showEndDateFlag;
	}
	/**
	 * @return Returns the showSoftwareMaintFlag.
	 */
	public boolean isShowSoftwareMaintFlag() {
		return showSoftwareMaintFlag;
	}
	/**
	 * @param showSoftwareMaintFlag The showSoftwareMaintFlag to set.
	 */
	public void setShowSoftwareMaintFlag(boolean showSoftwareMaintFlag) {
		this.showSoftwareMaintFlag = showSoftwareMaintFlag;
	}
	/**
	 * @return Returns the showStartDateFlag.
	 */
	public boolean isShowStartDateFlag() {
		return showStartDateFlag;
	}
	/**
	 * @param showStartDateFlag The showStartDateFlag to set.
	 */
	public void setShowStartDateFlag(boolean showStartDateFlag) {
		this.showStartDateFlag = showStartDateFlag;
	}
	
	
	 private String normalizeDateString(String dateStr) {
        return StringUtils.stripEnd(StringUtils.trimToEmpty(dateStr), "T");
    }
	 
    /**
     * @return Returns the isPricingCallFailed.
     */
    public boolean isPricingCallFailed() {
        return isPricingCallFailed;
    }
    /**
     * @param isPricingCallFailed The isPricingCallFailed to set.
     */
    public void setPricingCallFailed(boolean isPricingCallFailed) {
        this.isPricingCallFailed = isPricingCallFailed;
    }
    
    /**
     * @return Returns the epAddtnlYearCvrageSeqNum.
     */
    public int getEpAddtnlYearCvrageSeqNum() {
        return epAddtnlYearCvrageSeqNum;
    }
    /**
     * @param epAddtnlYearCvrageSeqNum The epAddtnlYearCvrageSeqNum to set.
     */
    public void setEpAddtnlYearCvrageSeqNum(int epAddtnlYearCvrageSeqNum) {
        this.epAddtnlYearCvrageSeqNum = epAddtnlYearCvrageSeqNum;
    }
    /**
     * @return Returns the epSortSeqNum.
     */
    public int getEpSortSeqNum() {
        return epSortSeqNum;
    }
    /**
     * @param epSortSeqNum The epSortSeqNum to set.
     */
    public void setEpSortSeqNum(int epSortSeqNum) {
        this.epSortSeqNum = epSortSeqNum;
    }
	private String[] wrapSentence(String orginalStr, String separator, int maxLen) {
       String localStr = StringUtils.trimToEmpty(orginalStr);
       String[] strArray = StringUtils.split(orginalStr,separator);
       StringBuffer stack = new StringBuffer();
       List splitSentence = new ArrayList();
       
       int index=0;
       while(index<strArray.length) {
           //push to stack
           stack.append(strArray[index].trim() + " ");
           if(stack.length()> maxLen) {
               //pop from stack
               int lastWord = stack.toString().trim().lastIndexOf(" ");
               stack.delete(lastWord, stack.length());
               
               //move to list and clear stack
               splitSentence.add(stack.toString());
               stack.delete(0,stack.length());
           } else {
               index++;
           }
       }
       splitSentence.add(stack.toString());
       return (String[]) splitSentence.toArray(new String[0]);
   }
   
	/**
	 * @return Returns the channelExtPrice.
	 */
	public double getChannelExtPrice() {
		return channelExtPrice;
	}
	public String getSChannelExtPrice() {
		if (isPricingCallFailed) {
	        return NP_MSG;
	    } else {
	        return DecimalUtil.format(this.channelExtPrice,scale);
	    }
	}
	/**
	 * @param channelExtPrice The channelExtPrice to set.
	 */
	public void setChannelExtPrice(double channelExtPrice) {
		this.channelExtPrice = channelExtPrice;
	}
	
	/**
	 * @return Returns the channelExtPrice.
	 */
	public double getChannelUnitPrice() {
		return channelUnitPrice;
	}
	public String getSChannelUnitPrice() {
		if (isPricingCallFailed) {
	        return NP_MSG;
	    } else {
	        return DecimalUtil.format(this.channelUnitPrice,scale);
	    }
	}
	/**
	 * @param channelUnitPrice The channelUnitPrice to set.
	 */
	public void setChannelUnitPrice(double channelUnitPrice) {
		this.channelUnitPrice = channelUnitPrice;
	}
	
	/**
	 * @return Returns the entitledExtPrice.
	 */
	public double getEntitledExtPrice() {
		return this.entitledExtPrice;
	}
	public String getSEntitledExtPrice(){
		if (isPricingCallFailed) {
	        return NP_MSG;
	    } else {
	        return DecimalUtil.format(this.entitledExtPrice,scale);
	    }
	}
	public String getRTFEntitledExtPrice(){
		if (isPricingCallFailed) {
	        return StringUtils.leftPad(NP_MSG,12);
	    } else {
	        return StringUtils.leftPad(DecimalUtil.format(this.entitledExtPrice,scale),12);
	    }
	}
	public String getRTFEntitledExtPriceRightPad(){
		if (isPricingCallFailed) {
	        return StringUtils.rightPad(NP_MSG,12);
	    } else {
	    	if(this.isSaasPart && this.entitledExtPrice == 0d){
	    		return StringUtils.rightPad("",12);
	    	}else{
	        	return StringUtils.rightPad(DecimalUtil.format(this.entitledExtPrice,scale),12);
	        }
	    }
	}
	/**
	 * @param entitledExtPrice The entitledExtPrice to set.
	 */
	public void setEntitledExtPrice(double entitledExtPrice) {
		this.entitledExtPrice = entitledExtPrice;
	}
	/**
	 * @return Returns the epBPOvrrdDiscPct.
	 */
	public double getEpBPOvrrdDiscPct() {
		return epBPOvrrdDiscPct;
	}
	/**
	 * @param epBPOvrrdDiscPct The epBPOvrrdDiscPct to set.
	 */
	public void setEpBPOvrrdDiscPct(double epBPOvrrdDiscPct) {
		this.epBPOvrrdDiscPct = epBPOvrrdDiscPct;
	}
	/**
	 * @return Returns the epBPStdDiscPct.
	 */
	public double getEpBPStdDiscPct() {
		return epBPStdDiscPct;
	}
	public String getRTFBPStdDiscPct(){
		String retVal = null;
    	double disc = getEpBPStdDiscPct();
    	if(disc > 0d)  {
    		retVal = StringUtils.rightPad(String.valueOf(DecimalUtil.roundAsDouble(disc*100,3))+"%",9);
    	} else {
    		retVal = StringUtils.rightPad(StringUtils.EMPTY,9);
    	}
    	return retVal;
	}
	public String getRTFBPDiscPct(){
		String retVal = null;
		double discPct = 0;
		if(isBPDiscOvrrdFlag()){
			discPct = getEpBPOvrrdDiscPct();
    	}else{
    		discPct = getEpBPStdDiscPct();
    	}
		if(discPct > 0d)  {
    		retVal = StringUtils.rightPad(String.valueOf(DecimalUtil.roundAsDouble(discPct*100,3))+"%",9);
    	} else {
    		retVal = StringUtils.rightPad(StringUtils.EMPTY,9);
    	}
		return retVal;
	}
	
	public String getQuoteRTFBPDiscount(){
	    return this.quoteRTFBPDiscount;
	}
	
	public void setQuoteRTFBPDiscount(){
	    double discPct = 0;
		if(isBPDiscOvrrdFlag()){
			discPct = getEpBPOvrrdDiscPct();
    	}else{
    		discPct = getEpBPStdDiscPct();
    	}
		quoteRTFBPDiscount = String.valueOf(DecimalUtil.roundAsDouble(discPct*100,3))+"%";
	}
	/**
	 * @param epBPStdDiscPct The epBPStdDiscPct to set.
	 */
	public void setEpBPStdDiscPct(double epBPStdDiscPct) {
		this.epBPStdDiscPct = epBPStdDiscPct;
	}
	/**
	 * @return Returns the bidExtPrice.
	 */
	public double getBidExtPrice() {
		return bidExtPrice;
	}
	public String getSBidExtPrice() {
		if (isPricingCallFailed) {
	        return NP_MSG;
	    } else {
	        return DecimalUtil.format(this.bidExtPrice,scale);
	    }
	}
	/**
	 * @return Returns the localUnitPrc.
	 */
	public double getLocalUnitPrc() {
		return localUnitPrc;
	}
	/**
	 * @param localUnitPrc The localUnitPrc to set.
	 */
	public void setLocalUnitPrc(double localUnitPrc) {
		this.localUnitPrc = localUnitPrc;
	}
	
	/**
	 * @return Returns the scale.
	 */
	public int getScale() {
		return scale;
	}
	/**
	 * @param scale The scale to set.
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}
	
    public String getProratedWeeks(){
    	return proratedWeeks;
    }
    
    public void setProratedWeeks(String proratedWeeks){
    	this.proratedWeeks = proratedWeeks;
    }
    
    public void setIsFtlPart(boolean isFtlPart){
        this.isFtlPart = isFtlPart; 
    }
    
    public boolean isFtlPart(){
        return isFtlPart;
    }
    /**
     * @return Returns the epBrandDesc.
     */
    public String getEpBrandDesc() {
        return epBrandDesc;
    }
    /**
     * @param epBrandDesc The epBrandDesc to set.
     */
    public void setEpBrandDesc(String epBrandDesc) {
        this.epBrandDesc = epBrandDesc;
    }

	public void setShowBillingFrequencyFlag(boolean showBillingFrequencyFlag) {
		this.showBillingFrequencyFlag = showBillingFrequencyFlag;
	}

	public boolean isShowBillingFrequencyFlag() {
		return showBillingFrequencyFlag;
	}

	public void setShowTermSelectionFlag(boolean showTermSelectionFlag) {
		this.showTermSelectionFlag = showTermSelectionFlag;
	}

	public boolean isShowTermSelectionFlag() {
		return showTermSelectionFlag;
	}

	public void setShowItemTotContractVal(boolean showItemTotContractVal) {
		this.showItemTotContractVal = showItemTotContractVal;
	}

	public boolean isShowItemTotContractVal() {
		return showItemTotContractVal;
	}
	
	public boolean isDecimal4Flag() {
		return decimal4Flag;
	}
	
	public void setDecimal4Flag(boolean decimal4Flag) {
		this.decimal4Flag = decimal4Flag;
	}

	public boolean isNoQty() {
		return noQty;
	}

	public void setNoQty(boolean noQty) {
		this.noQty = noQty;
	}
	
	public String getRTFTotalContractCommitValue() {
		return DecimalUtil.format(totalContractCommitValue,scale);
	}

	public boolean isSaasPartAndHasQty() {
		return saasPartAndHasQty;
	}

	public void setSaasPartAndHasQty(boolean saasPartAndHasQty) {
		this.saasPartAndHasQty = saasPartAndHasQty;
	}

	public boolean isUsagePart() {
		return usagePart;
	}

	public void setUsagePart(boolean usagePart) {
		this.usagePart = usagePart;
	}

	public Double getLocalUnitProratedPrc() {
		return localUnitProratedPrc;
	}
	public void setLocalUnitProratedPrc(Double localUnitProratedPrc) {
		this.localUnitProratedPrc = localUnitProratedPrc;
	}
	public boolean isSaasProdHumanServicesPart() {
		return saasProdHumanServicesPart;
	}
	public void setSaasProdHumanServicesPart(boolean saasProdHumanServicesPart) {
		this.saasProdHumanServicesPart = saasProdHumanServicesPart;
	}
	public boolean isSaasSubscrptnPart() {
		return saasSubscrptnPart;
	}

	public void setSaasSubscrptnPart(boolean saasSubscrptnPart) {
		this.saasSubscrptnPart = saasSubscrptnPart;
	}

	public boolean isSaasSetUpPart() {
		return saasSetUpPart;
	}

	public void setSaasSetUpPart(boolean saasSetUpPart) {
		this.saasSetUpPart = saasSetUpPart;
	}

	public double getBillingPeriods() {
		return billingPeriods;
	}

	public void setBillingPeriods(double billingPeriods) {
		this.billingPeriods = billingPeriods;
	}

	public boolean isDisableOverrideUnitPriceInput() {
		return disableOverrideUnitPriceInput;
	}

	public void setDisableOverrideUnitPriceInput(
			boolean disableOverrideUnitPriceInput) {
		this.disableOverrideUnitPriceInput = disableOverrideUnitPriceInput;
	}

	public boolean isReplacedPartFlag() {
		return replacedPartFlag;
	}

	public void setReplacedPartFlag(boolean replacedPartFlag) {
		this.replacedPartFlag = replacedPartFlag;
	}

	public boolean isShowEventBasedDescription() {
		return showEventBasedDescription;
	}

	public void setShowEventBasedDescription(boolean showEventBasedDescription) {
		this.showEventBasedDescription = showEventBasedDescription;
	}

	public boolean isShowPart() {
		return showPart;
	}

	public void setShowPart(boolean showPart) {
		this.showPart = showPart;
	}

	public String getPartTypeDsc() {
		return partTypeDsc;
	}

	public void setPartTypeDsc(String partTypeDsc) {
		this.partTypeDsc = partTypeDsc;
	}
}
