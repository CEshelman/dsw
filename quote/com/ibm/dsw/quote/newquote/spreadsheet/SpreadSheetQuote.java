package com.ibm.dsw.quote.newquote.spreadsheet;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpreadSheetQuote<code> class used by import and export functions.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-9
 */
public class SpreadSheetQuote {
    
    //Mapping to countryCode to XMLSS
    private String cntryCode = "";
    
    //Mapping to quoteType on XMLSS
    private String lobCode = "";
    
    //lobCodeDesc is required in export and optional import
    private String lobCodeDesc = "";
    
    //Mapping to IBMCustomerNumber on XMLSS
    private String customerNum = "";
    
    //Mapping to agreementNumber on XMLSS
    private String sapCtrctNum = "";
    
    private String webQuoteNum = "";
    
    /**
     *  software part list
     */
    private List eqPartList = new ArrayList();
    
	/**
	 * saas part list
	 */
	private List eqSaaSPartList = new ArrayList();
	
	/**
	 * all part list of the quote
	 */
	private List eqAllParsList = new ArrayList();
    
    private boolean custValid = true;
    
    private boolean sapCtrctNumValid = true;
    
    private boolean siteNumValid = true;
    
    private String currency = "";
    
    private String currencyDesc = "";
    
    private boolean currencyValid = true;
    
    private List invalidPartList = new ArrayList();
    
    private List validPartList = new ArrayList();
    
    private List applianceParts=new ArrayList();
    
    private List invalidSaaSPartList = new ArrayList();

	private List validSaaSPartList = new ArrayList();
    
    private List saasPartNumList = new ArrayList();
    
    private List partNumList = new ArrayList();
    
    private List invalidFrequencyPartNumList = new ArrayList();

	private List duplicateSaaSPartNumList = new ArrayList();
	
	private List inactiveSaaSPartNumList = new ArrayList();
	
	private List uncertifiedSaaSPartNumList = new ArrayList();

	private String acquisitionDesc = "";
    
    private String cntryCodeDesc = "";
    
    //Mapping to acquisitionCode on XMLSS
    private String acquisition = "";
    
    //Mapping to siteNumber on XMLSS
    private String siteNum = "";
    
    //Mapping to anniversary on XMLSS
    private String anniversary = "";
    
    private String oemAgrmntType = "";
    
    private String oemAgrmntTypeDesc = "";
    
    private String relationSVPLevel = "";
    
    private String quoteSVPLevel = "";
    
    private String custName = "";
    
    private String streetAddress ="";
    
    private String cityAddress = "";
    
    private String cntryAddress = "";
    
    private boolean gsaPricingFlag = false ;
    
    private boolean preApprovedPricingFlag = false ;
    
    private String postalZipCode = "";
    
    private String city = "";
    
    private String state = "";

    //Map to RTF primaryContactName
    private String primaryContactName = "";
    
    //Map to RTF primaryContactPhone
    private String primaryContactPhone = "";
    
    // Map to RTF primaryContactEmail
    private String primaryContactEmail = "";
    
    //  Map to RTF primaryContactFax
    private String primaryContactFax = "";
    
    private String pricingDate = (new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    
    private List pricingList = new ArrayList();
    
    //  Map to RTF totalPoints for all items sum
    private double totalPoints = 0d;
    //  Map to RTF totalPoints for all items sum
    private double totalPrice = 0d;
    
    private double entitledExtTotalPrice = 0d;

    private String bidExtTotalPrice = "";
    
    private double bpExtTotalPrice = 0d;
    
    private double userLineTotalDiscount = 0d;
    
    private double totalLineTotalDiscount = 0d;
    
    private double totalPointsForExcel = 0;
    
    private double totalPriceForExcel = 0;
    
    private String contractOption = "";
    
    private String renewalQuoteNumber = "";
    
    private String renewalQuoteDueDate ="";
    
    private boolean renwalQuoteFlag = false;
    
    private boolean submittedQuoteFlag = false;
    
    private String quoteExpDate = "";
    
    private String modDate = "";
    
    private String quoteStartDate = "";
    
    private String submittedDate = "";
    
    private String progMigrtnCode = "";
    
    private boolean isPricingCallFailed = false;
    
    private String custDesignation = "";
    
    private String govEntityIndDesc = ""; //Government for business partner incentives
    
    public static final String NP_MSG = "- No price -";
    
    public static final List excludeFromXML = new ArrayList();
    
    private boolean isChannelMarginQuote;
    
    private String opprtntyInfo = "";
    
    private String fulfillmentSrc = "";
    
    private String sapQuoteNum = "";
    
    private String sapIDocNum = "";
    
    private String resellerCustNum = "";
    
    private String resellerIBMCustNum = "";
    
    private String resellerCustName = "";
    
    private String resellerStreetAddr = "";
    
    private String resellerCityAddr = "";
    
    private String resellerCntryAddr = "";
    
    private String distributorCustNum = "";
    
    private String distributorIBMCustNum = "";
    
    private String distributorCustName = "";
    
    private String distributorStreetAddr = "";
    
    private String distributorCityAddr = "";
    
    private String distributorCntryAddr = "";
    
    private String exemptnCode;
    
    private List invalidOCSPartList = new ArrayList();
    
    private boolean isExceedMaxPartsQty = false;
    
    private String overallStatus = "";
    
    private String quoteOppOwnerEmail = "";
    
    private String sbApprovedDate = "";
    
    private String viewSubmittedQuoteDetailUrl = "";
    
    private int scale = 2;
    
    private String initSVPLevel = "";
    
    private boolean hasInvalidFutureDates = false;
    
    private String nimOffergCode = "";
    
    private boolean isProtected = true; 
    
    private boolean isToImportNewQuote = false;
    
    private boolean isToUnlockQuote = false;
    
    private boolean isLocked = false;
    
    private String lockedBy = "";
    
    private String totalCommitValue = "";
    
    private String totalSaasPoint = "";
    
	private boolean hasSaaSLineItem = false;
	
	private boolean hasSoftwareLineItem = false;
    
	public Map partsPricingConfigrtnsMap;
	
	private Map<String, PartsPricingConfiguration> partsPricingConfigurationMap = new HashMap<String, PartsPricingConfiguration>();
    
    public List partsPricingConfigrtnsList;
    
    private String  servicesAgreementNum ;

    private String  totalSoftwarePoint ;
    
    private String  totalSoftwarePrice ;
    
    //Downloaded pricing type for PGS
    private String downloadPricingType = "";
    
    private boolean tier2ResellerFlag = false;
    
    private boolean bpRelatedCust = false;
    
	private String bpExtTotalValue = "";
	
	private String bpTCVPrice = "";
    
	private String progMigrationDscr = ""; 
	
	private String totalEntitledTCV = ""; 
	
	private boolean showSaasSheet = false;
	
	private boolean isPGSFlag = false;
	
	private Configurator configurator = new Configurator();
	
	private String notesAccessUrl = "";
    
	//release 14.1: Equity Curve data
    //-- top performer
    private String preferDiscountTotal = "";
    private String preferBidUnitPriceTotal = "";
    private String preferBidExtendedPriceTotal = "";
    //---market average
    private String maxDiscountTotal = "";
    private String maxBidUnitPriceTotal = "";
    private String maxBidExendedPriceTotal = "";
	
	public String getBidExtTotalPrice() {
		return bidExtTotalPrice;
	}

	public void setBidExtTotalPrice(String bidExtTotalPrice) {
		this.bidExtTotalPrice = bidExtTotalPrice;
	}

	public List getApplianceParts() {
		return applianceParts;
	}

	public void setApplianceParts(List applianceParts) {
		this.applianceParts = applianceParts;
	}
	
	public void addAppliancePart(SpreadSheetPart ssPart){
		this.applianceParts.add(ssPart);
	}

	public List getInactiveSaaSPartNumList() {
		return inactiveSaaSPartNumList;
	}
	
	public void setInactiveSaaSPartNumList(List inactiveSaaSPartNumList) {
		this.inactiveSaaSPartNumList = inactiveSaaSPartNumList;
	}
	
	public List getUncertifiedSaaSPartNumList() {
		return uncertifiedSaaSPartNumList;
	}

	public void setUncertifiedSaaSPartNumList(List uncertifiedSaaSPartNumList) {
		this.uncertifiedSaaSPartNumList = uncertifiedSaaSPartNumList;
	}
	
    public String getNotesAccessUrl() {
		return notesAccessUrl;
	}

	public void setNotesAccessUrl(String notesAccessUrl) {
		this.notesAccessUrl = notesAccessUrl;
	}
	
	public Configurator getConfigurator() {
		return configurator;
	}
	public void setConfigurator(Configurator configurator) {
		this.configurator = configurator;
	}
	
	public Map<String, PartsPricingConfiguration> getPartsPricingConfigurationMap() {
		return partsPricingConfigurationMap;
	}
	public void setPartsPricingConfigurationMap(
			Map<String, PartsPricingConfiguration> partsPricingConfigurationMap) {
		this.partsPricingConfigurationMap = partsPricingConfigurationMap;
	}
	public boolean isPGSFlag() {
		return isPGSFlag;
	}
	public void setPGSFlag(boolean isPGSFlag) {
		this.isPGSFlag = isPGSFlag;
	}
	public boolean isShowSaasSheet() {
		return showSaasSheet;
	}
	public void setShowSaasSheet(boolean showSaasSheet) {
		this.showSaasSheet = showSaasSheet;
	}
	public String getTotalEntitledTCV() {
		return totalEntitledTCV;
	}
	public void setTotalEntitledTCV(String totalEntitledTCV) {
		this.totalEntitledTCV = totalEntitledTCV;
	}
	public String getBpTCVPrice() {
		return bpTCVPrice;
	}
	public void setBpTCVPrice(String bpTCVPrice) {
		this.bpTCVPrice = bpTCVPrice;
	}
	
    public boolean isBpRelatedCust() {
		return bpRelatedCust;
	}
	public void setBpRelatedCust(boolean bpRelatedCust) {
		this.bpRelatedCust = bpRelatedCust;
	}
	public String getBpExtTotalValue() {
		return bpExtTotalValue;
	}
	public void setBpExtTotalValue(String bpExtTotalValue) {
		this.bpExtTotalValue = bpExtTotalValue;
	}
	public boolean isTier2ResellerFlag() {
		return tier2ResellerFlag;
	}
	public void setTier2ResellerFlag(boolean tier2ResellerFlag) {
		this.tier2ResellerFlag = tier2ResellerFlag;
	}
	public String getDownloadPricingType() {
		return filterAlink(downloadPricingType);
	}
	public void setDownloadPricingType(String downloadPricingType) {
		this.downloadPricingType = downloadPricingType;
	}
	public String getTotalSoftwarePoint() {
		return StringUtils.rightPad(totalSoftwarePoint ,8);
	}
	public void setTotalSoftwarePoint(String totalSoftwarePoint) {
		this.totalSoftwarePoint = totalSoftwarePoint;
	}
	public String getTotalSoftwarePrice() {
		if (!isPricingCallFailed) {
	          if (isPA()) {
	                return "  " + StringUtils.leftPad(totalSoftwarePrice, 12);
	            } else {
	                return StringUtils.leftPad(totalSoftwarePrice, 65);
	            }
	        } else {
	            if(isPA()) {
	                return  "  " + StringUtils.leftPad(NP_MSG,12);
	            } else {
	                return  StringUtils.leftPad(NP_MSG,65);
	            }
	        }
	}
	public void setTotalSoftwarePrice(String totalSoftwarePrice) {
		this.totalSoftwarePrice = totalSoftwarePrice;
	}
	public String getBpExtTotalPriceForRTF() {
		String bpExtTotalPriceStr = new DecimalFormat("###,###.00").format(bpExtTotalPrice);
		if (!isPricingCallFailed) {
	          if (isPA()) {
	                return "  " + StringUtils.leftPad(bpExtTotalPriceStr + "", 12);
	            } else {
	                return StringUtils.leftPad(bpExtTotalPriceStr + "", 65);
	            }
	        } else {
	            if(isPA()) {
	                return  "  " + StringUtils.leftPad(NP_MSG,12);
	            } else {
	                return  StringUtils.leftPad(NP_MSG,65);
	            }
	        }
	}
    public String getTotalSaasPoint() {
    	return StringUtils.rightPad(totalSaasPoint ,8);
    }
    public void setTotalSaasPoint(String totalSaasPoint) {
    	this.totalSaasPoint = totalSaasPoint;
    }
    public boolean isHasSoftwareLineItem() {
    	return hasSoftwareLineItem;
    }
    public void setHasSoftwareLineItem(boolean hasSoftwareLineItem) {
    	this.hasSoftwareLineItem = hasSoftwareLineItem;
    }
    public String getServicesAgreementNum() {
		return servicesAgreementNum;
	}
	public void setServicesAgreementNum(String servicesAgreementNum) {
		this.servicesAgreementNum = servicesAgreementNum;
	}
	public Map getPartsPricingConfigrtnsMap() {
		return partsPricingConfigrtnsMap;
	}
	public void setPartsPricingConfigrtnsMap(Map partsPricingConfigrtnsMap) {
		this.partsPricingConfigrtnsMap = partsPricingConfigrtnsMap;
	}
	public List getPartsPricingConfigrtnsList() {
		return partsPricingConfigrtnsList;
	}
	public void setPartsPricingConfigrtnsList(List partsPricingConfigrtnsList) {
		this.partsPricingConfigrtnsList = partsPricingConfigrtnsList;
	}
    
	public boolean isHasSaaSLineItem() {
		return hasSaaSLineItem;
	}
	public void setHasSaaSLineItem(boolean hasSaaSLineItem) {
		this.hasSaaSLineItem = hasSaaSLineItem;
	}
	static{
    	excludeFromXML.add("validPartList");
    	excludeFromXML.add("class");
    	excludeFromXML.add("pricingList");
    	excludeFromXML.add("invalidPartList");
    	excludeFromXML.add("contentHandler");
    	excludeFromXML.add("eqPartList");
    	excludeFromXML.add("eqSaaSPartList");
    	excludeFromXML.add("invalidOCSPartList");
    	excludeFromXML.add("saasPartNumList");
    	excludeFromXML.add("partNumList");
    	excludeFromXML.add("invalidSaaSPartList");
    	excludeFromXML.add("validSaaSPartList");
    	excludeFromXML.add("invalidFrequencyPartNumList");
    	excludeFromXML.add("duplicateSaaSPartNumList");
    	excludeFromXML.add("invalidApplianceQtyPartlist");
    	excludeFromXML.add("validApplianceQtyPartlist");
    }

    public String getTotalCommitValue() {
    	if (!isPricingCallFailed) {
            if (isPA()) {
                  return "  " + StringUtils.leftPad(totalCommitValue, 12);
              } else {
                  return StringUtils.leftPad(totalCommitValue, 52);
              }
          } else {
              if(isPA()) {
                  return  "  " + StringUtils.leftPad(NP_MSG,12);
              } else {
                  return  StringUtils.leftPad(NP_MSG,52);
              }
          }
	}
	public void setTotalCommitValue(String totalCommitValue) {
		this.totalCommitValue = totalCommitValue;
	}
	
	/**
	 * @return Returns the bpExtTotalPrice.
	 */
	public double getBpExtTotalPrice() {
		return bpExtTotalPrice;
	}
	/**
	 * @param bpExtTotalPrice The bpExtTotalPrice to set.
	 */
	public void setBpExtTotalPrice(double bpExtTotalPrice) {
		this.bpExtTotalPrice = bpExtTotalPrice;
	}
	/**
	 * @return Returns the entitledExtTotalPrice.
	 */
	public double getEntitledExtTotalPrice() {
		return entitledExtTotalPrice;
	}
	/**
	 * @param entitledExtTotalPrice The entitledExtTotalPrice to set.
	 */
	public void setEntitledExtTotalPrice(double entitledExtTotalPrice) {
		this.entitledExtTotalPrice = entitledExtTotalPrice;
	}
	/**
	 * @return Returns the totalLineTotalDiscount.
	 */
	public double getTotalLineTotalDiscount() {
		return totalLineTotalDiscount;
	}
	
	public List getEqPartList() {
		return eqPartList;
	}
	public void setEqPartList(List eqPartList) {
		this.eqPartList = eqPartList;
	}
	/**
	 * @param totalLineTotalDiscount The totalLineTotalDiscount to set.
	 */
	public void setTotalLineTotalDiscount(double totalLineTotalDiscount) {
		this.totalLineTotalDiscount = totalLineTotalDiscount;
	}
	/**
	 * @return Returns the userLineTotalDiscount.
	 */
	public double getUserLineTotalDiscount() {
		return userLineTotalDiscount;
	}
	/**
	 * @param userLineTotalDiscount The userLineTotalDiscount to set.
	 */
	public void setUserLineTotalDiscount(double userLineTotalDiscount) {
		this.userLineTotalDiscount = userLineTotalDiscount;
	}
    /**
     * @return Returns the isProtected.
     */
    public boolean isProtected() {
        return isProtected;
    }
    /**
     * @param isProtected The isProtected to set.
     */
    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }
	public String getInitSVPLevel() {
		return initSVPLevel;
	}
	public void setInitSVPLevel(String initSVPLevel) {
		this.initSVPLevel = initSVPLevel;
	}
    /**
     * @return Returns the progMigrtnCode.
     */
    public String getProgMigrtnCode() {
        return progMigrtnCode;
    }
    /**
     * @param progMigrtnCode The progMigrtnCode to set.
     */
    public void setProgMigrtnCode(String progMigrtnCode) {
        this.progMigrtnCode = progMigrtnCode;
    }
    public DefaultHandler getContentHandler() {
        return new XMLSSQuoteHandler();
    }
    
    /**
     * @return Returns the submittedDate.
     */
    public String getSubmittedDate() {
        return submittedDate;
    }
    /**
     * @param submittedDate The submittedDate to set.
     */
    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }
    
    /**
     * @return Returns the submittedQuote.
     */
    public boolean isSubmittedQuoteFlag() {
        return submittedQuoteFlag;
    }
    /**
     * @param submittedQuote The submittedQuote to set.
     */
    public void setSubmittedQuoteFlag(boolean submittedQuote) {
        this.submittedQuoteFlag = submittedQuote;
    }
    
    /**
     * @return Returns the primaryContactFax.
     */
    public String getPrimaryContactFax() {
        return StringUtils.trimToEmpty(primaryContactFax);
    }
    /**
     * @param primaryContactFax The primaryContactFax to set.
     */
    public void setPrimaryContactFax(String primaryContactFax) {
        this.primaryContactFax = primaryContactFax;
    }
    /**
     * @return Returns the renewalQuoteDueDate.
     */
    public String getRenewalQuoteDueDate() {
        return renewalQuoteDueDate;
    }
    /**
     * @param renewalQuoteDueDate The renewalQuoteDueDate to set.
     */
    public void setRenewalQuoteDueDate(String renewalQuoteDueDate) {
        this.renewalQuoteDueDate = renewalQuoteDueDate;
    }
    /**
     * @return Returns the renewalQuoteNumber.
     */
    public String getRenewalQuoteNumber() {
        return renewalQuoteNumber;
    }
    /**
     * @param renewalQuoteNumber The renewalQuoteNumber to set.
     */
    public void setRenewalQuoteNumber(String renewalQuoteNumber) {
        this.renewalQuoteNumber = renewalQuoteNumber;
    }
    /**
     * @return Returns the renwalQuoteFlag.
     */
    public boolean isRenwalQuoteFlag() {
        return renwalQuoteFlag;
    }
    /**
     * @param renwalQuoteFlag The renwalQuoteFlag to set.
     */
    public void setRenwalQuoteFlag(boolean renwalQuoteFlag) {
        this.renwalQuoteFlag = renwalQuoteFlag;
    }
    /**
     * @return Returns the contractOption.
     */
    public String getContractOption() {
        return contractOption;
    }
    /**
     * @param contractOption The contractOption to set.
     */
    public void setContractOption(String contractOption) {
        this.contractOption = contractOption;
    }
    /**
     * @return Returns the province.
     */
    public String getState() {
        return state;
    }
    /**
     * @param province The province to set.
     */
    public void setState(String province) {
        this.state = province;
    }
    /**
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }
    /**
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
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
    public String getTotalPoints() {
        return StringUtils.rightPad(DecimalUtil.format(totalPoints,2) ,8);
    }
    /**
     * @return Returns the totalPrice.
     */
    public String getTotalPrice() {
        if (!isPricingCallFailed) {
          if (isPA()) {
                return "  " + StringUtils.leftPad(DecimalUtil.format(totalPrice, scale), 12);
            } else {
                return StringUtils.leftPad(DecimalUtil.format(totalPrice, scale), 68);
            }
        } else {
            if(isPA()) {
                return  "  " + StringUtils.leftPad(NP_MSG,12);
            } else {
                return  StringUtils.leftPad(NP_MSG,68);
            }
        }
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
    
    
    /**
     * @return Returns the primaryContactEmail.
     */
    public String getPrimaryContactEmail() {
        return StringUtils.trimToEmpty(primaryContactEmail);
    }
    /**
     * @param primaryContactEmail The primaryContactEmail to set.
     */
    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }
    /**
     * @return Returns the primaryContactName.
     */
    public String getPrimaryContactName() {
        return filterAlink(StringUtils.trimToEmpty(primaryContactName));
    }
    /**
     * @param primaryContactName The primaryContactName to set.
     */
    public void setPrimaryContactName(String primaryContactName) {
        this.primaryContactName = primaryContactName;
    }
    /**
     * @return Returns the primaryContactPhone.
     */
    public String getPrimaryContactPhone() {
        return StringUtils.trimToEmpty(primaryContactPhone);
    }
    /**
     * @param primaryContactPhone The primaryContactPhone to set.
     */
    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }
    /**
     * @return Returns the currencyDesc.
     */
    public String getCurrencyDesc() {
        return currencyDesc;
    }
    /**
     * @param currencyDesc The currencyDesc to set.
     */
    public void setCurrencyDesc(String currencyDesc) {
        this.currencyDesc = currencyDesc;
    }
    /**
     * @return Returns the postalZipCode.
     */
    public String getPostalZipCode() {
        return postalZipCode;
    }
    /**
     * @param postalZipCode The postalZipCode to set.
     */
    public void setPostalZipCode(String postalZipCode) {
        this.postalZipCode = postalZipCode;
    }
    /**
     * @return Returns the isFCT.
     */
    public boolean isFCT() {
        return this.getLobCode().equals("FCT");
    }
    /**
     * @return Returns the isPA.
     */
    public boolean isPA() {
        return this.getLobCode().equals("PA");
    }
    /**
     * @return Returns the isPAE.
     */
    public boolean isPAE() {
        return this.getLobCode().equals("PAE");
    }
    /**
     * @return Returns the isOEM.
     */
    public boolean isOEM() {
        return this.getLobCode().equals("OEM");
    }
    
    public boolean isFCTTOPA() {
        return QuoteConstants.MIGRTN_CODE_FCT_TO_PA.equals(this.getProgMigrtnCode());
    }
    
    public void addToValidPartList(SpreadSheetPart part) {
        validPartList.add(part);
    }
    
    public void addToValidSaaSPartList(SpreadSheetPart part) {
        validSaaSPartList.add(part);
    }
    
    public void addToDuplicateSaaSPartList(String partNum) {
    	duplicateSaaSPartNumList.add(partNum);
    }

	public List getDuplicateSaaSPartNumList() {
		return duplicateSaaSPartNumList;
	}

	public void setDuplicateSaaSPartNumList(List duplicateSaaSPartNumList) {
		this.duplicateSaaSPartNumList = duplicateSaaSPartNumList;
	}
    
    /**
     * @return Returns the validPartList.
     */
    public List getValidPartList() {
        return validPartList;
    }
    /**
     * @return Returns the gsaPricingFlag.
     */
    public boolean isGsaPricingFlag() {
        return gsaPricingFlag;
    }
    /**
     * @param gsaPricingFlag The gsaPricingFlag to set.
     */
    public void setGsaPricingFlag(boolean gsaPricingFlag) {
        this.gsaPricingFlag = gsaPricingFlag;
    }
    /**
     * @return Returns the preApprovedPricingFlag.
     */
    public boolean isPreApprovedPricingFlag() {
        return preApprovedPricingFlag;
    }
    /**
     * @param preApprovedPricingFlag The preApprovedPricingFlag to set.
     */
    public void setPreApprovedPricingFlag(boolean preApprovedPricingFlag) {
        this.preApprovedPricingFlag = preApprovedPricingFlag;
    }
    /**
     * @return Returns the cityAddress.
     */
    public String getCityAddress() {
        return StringUtils.trimToEmpty(cityAddress);
    }
    /**
     * @param cityAddress The cityAddress to set.
     */
    public void setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
    }
    /**
     * @return Returns the cntryAddress.
     */
    public String getCntryAddress() {
        return StringUtils.trimToEmpty(cntryAddress);
    }
    /**
     * @param cntryAddress The cntryAddress to set.
     */
    public void setCntryAddress(String cntryAddress) {
        this.cntryAddress = cntryAddress;
    }
    /**
     * @return Returns the streetAddress.
     */
    public String getStreetAddress() {
        return StringUtils.trimToEmpty(streetAddress);
    }
    /**
     * @param streetAddress The streetAddress to set.
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
    /**
     * @return Returns the custName.
     */
    public String getCustName() {
        return StringUtils.trimToEmpty(custName);
    }
    /**
     * @param custName The custName to set.
     */
    public void setCustName(String custName) {
        this.custName = custName;
    }
    /**
     * @return Returns the currencyValid.
     */
    public boolean isCurrencyValid() {
        return currencyValid;
    }
    /**
     * @param currencyValid The currencyValid to set.
     */
    public void setCurrencyValid(boolean currencyValid) {
        this.currencyValid = currencyValid;
    }
    /**
     * @return Returns the siteNumValid.
     */
    public boolean isSiteNumValid() {
        return siteNumValid;
    }
    /**
     * @param siteNumValid The siteNumValid to set.
     */
    public void setSiteNumValid(boolean siteNumValid) {
        this.siteNumValid = siteNumValid;
    }
    /**
     * @return Returns the acquisition.
     */
    public String getAcquisition() {
        return StringUtils.trimToEmpty(acquisition);
    }
    
    /**
     * @param acquisition The acquisition to set.
     */
    public void setAcquisition(String acquisition) {
        this.acquisition = acquisition;
    }
    /**
     * @return Returns the oemAgrmntType.
     */
    public String getOemAgrmntType() {
        return StringUtils.trimToEmpty(oemAgrmntType);
    }
    
    /**
     * @param oemAgrmntType The oemAgrmntType to set.
     */
    public void setOemAgrmntType(String oemAgrmntType) {
        this.oemAgrmntType = oemAgrmntType;
    }
    
  
    public String getOemAgrmntTypeDesc() {
        return StringUtils.trimToEmpty(oemAgrmntTypeDesc);
    }
    
  
    public void setOemAgrmntTypeDesc(String oemAgrmntTypeDesc) {
        this.oemAgrmntTypeDesc = oemAgrmntTypeDesc;
    }
    
    /**
     * @return Returns the acquisitionDesc.
     */
    public String getAcquisitionDesc() {
        return StringUtils.trimToEmpty(acquisitionDesc);
    }
    /**
     * @param acquisitionDesc The acquisitionDesc to set.
     */
    public void setAcquisitionDesc(String acquisitionDesc) {
        this.acquisitionDesc = acquisitionDesc;
    }
    /**
     * @return Returns the anniversary.
     */
    public String getAnniversary() {
        return StringUtils.trimToEmpty(anniversary);
    }
    /**
     * @param anniversary The anniversary to set.
     */
    public void setAnniversary(String anniversary) {
        this.anniversary = anniversary;
    }
    /**
     * @return Returns the cntryCode.
     */
    public String getCntryCode() {
        return StringUtils.trimToEmpty(cntryCode);
    }
    /**
     * @param cntryCode The cntryCode to set.
     */
    public void setCntryCode(String cntryCode) {
        this.cntryCode = cntryCode;
    }
    /**
     * @return Returns the cntryCodeDesc.
     */
    public String getCntryCodeDesc() {
        return StringUtils.trimToEmpty(cntryCodeDesc);
    }
    /**
     * @param cntryCodeDesc The cntryCodeDesc to set.
     */
    public void setCntryCodeDesc(String cntryCodeDesc) {
        this.cntryCodeDesc = cntryCodeDesc;
    }
    /**
     * @return Returns the currency.
     */
    public String getCurrency() {
        return StringUtils.trimToEmpty(currency);
    }
    /**
     * @param currency The currency to set.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    /**
     * @return Returns the customerNum.
     */
    public String getCustomerNum() {
        return StringUtils.trimToEmpty(customerNum);
    }
    /**
     * @param customerNum The customerNum to set.
     */
    public void setCustomerNum(String customerNum) {
        this.customerNum = customerNum;
    }
    /**
     * @return Returns the custValid.
     */
    public boolean isCustValid() {
        return custValid;
    }
    /**
     * @param custValid The custValid to set.
     */
    public void setCustValid(boolean custValid) {
        this.custValid = custValid;
    }
    /**
     * @return Returns the eqPartList.
     */
    public List getSaaSEqPartList() {
        return eqSaaSPartList;
    }
    
    public void setSaaSEqPartList(List eqSaaSPartList) {
        this.eqSaaSPartList = eqSaaSPartList;
    }
    
    public List getEqAllParsList() {
		return eqAllParsList;
	}
	public void setEqAllParsList(List eqAllParsList) {
		this.eqAllParsList = eqAllParsList;
	}
	
	/**
     * @return Returns the invalidPartList.
     */
    public List getInvalidPartList() {
        return invalidPartList;
    }
    /**
     * @param invalidPartList The invalidPartList to set.
     */
    public void setInvalidPartList(List invalidPartList) {
        this.invalidPartList = invalidPartList;
    }
    public List getInvalidSaaSPartList() {
		return invalidSaaSPartList;
	}
	public void setInvalidSaaSPartList(List invalidSaaSPartList) {
		this.invalidSaaSPartList = invalidSaaSPartList;
	}
	
	
	public List getValidSaaSPartList() {
		return validSaaSPartList;
	}
	public void setValidSaaSPartList(List validSaaSPartList) {
		this.validSaaSPartList = validSaaSPartList;
	}
    
    /**
     * @return Returns the saasPartNumList.
     */
    public List getSaaSPartNumList() {
        return saasPartNumList;
    }
    /**
     * @param saasPartNumList The saasPartNumList to set.
     */
    public void setSaaSPartNumList(List saasPartNumList) {
        this.saasPartNumList = saasPartNumList;
    }
    
    public List getPartNumList() {
		return partNumList;
	}
	public void setPartNumList(List partNumList) {
		this.partNumList = partNumList;
	}
	
	public List getInvalidFrequencyPartNumList() {
		return invalidFrequencyPartNumList;
	}
	public void setInvalidFrequencyPartNumList(List invalidFrequencyPartNumList) {
		this.invalidFrequencyPartNumList = invalidFrequencyPartNumList;
	}
    /**
     * @return Returns the lobCode.
     */
    public String getLobCode() {
        return StringUtils.trimToEmpty(lobCode);
    }
    /**
     * @param lobCode The lobCode to set.
     */
    public void setLobCode(String lobCode) {
        this.lobCode = lobCode;
    }
    /**
     * @return Returns the lobCodeDesc.
     */
    public String getLobCodeDesc() {
        return StringUtils.trimToEmpty(lobCodeDesc);
    }
    /**
     * @param lobCodeDesc The lobCodeDesc to set.
     */
    public void setLobCodeDesc(String lobCodeDesc) {
        this.lobCodeDesc = lobCodeDesc;
    }
    /**
     * @return Returns the pricingDate.
     */
    public String getPricingDate() {
        return StringUtils.stripEnd(StringUtils.trimToEmpty(pricingDate), "T");
    }
    /**
     * @param pricingDate
     *            The pricingDate to set.
     */
    public void setPricingDate(String pricingDate) {
    	if(StringUtils.isNotBlank(pricingDate))
        this.pricingDate = pricingDate;
    }
    /**
     * @return Returns the quoteSVPLevel.
     */
    public String getQuoteSVPLevel() {
        return StringUtils.trimToEmpty(quoteSVPLevel);
    }
    /**
     * @param quoteSVPLevel The quoteSVPLevel to set.
     */
    public void setQuoteSVPLevel(String quoteSVPLevel) {
        this.quoteSVPLevel = quoteSVPLevel;
    }
    /**
     * @return Returns the relationSVPLevel.
     */
    public String getRelationSVPLevel() {
        return StringUtils.trimToEmpty(relationSVPLevel);
    }
    /**
     * @param relationSVPLevel The relationSVPLevel to set.
     */
    public void setRelationSVPLevel(String relationSVPLevel) {
        this.relationSVPLevel = relationSVPLevel;
    }
    
    /**
     * @return Returns the sapCtrctNum.
     */
    public String getSapCtrctNum() {
        return StringUtils.trimToEmpty(sapCtrctNum);
    }
    /**
     * @param sapCtrctNum The sapCtrctNum to set.
     */
    public void setSapCtrctNum(String sapCtrctNum) {
        this.sapCtrctNum = sapCtrctNum;
    }
    /**
     * @return Returns the sapCtrctNumValid.
     */
    public boolean isSapCtrctNumValid() {
        return sapCtrctNumValid;
    }
    /**
     * @param sapCtrctNumValid The sapCtrctNumValid to set.
     */
    public void setSapCtrctNumValid(boolean sapCtrctNumValid) {
        this.sapCtrctNumValid = sapCtrctNumValid;
    }
    /**
     * @return Returns the siteNum.
     */
    public String getSiteNum() {
        String str = StringUtils.trimToEmpty(siteNum);
        while(str.startsWith("0")) 
            str = str.substring(1);
        return str;
    }
    /**
     * @param siteNum The siteNum to set.
     */
    public void setSiteNum(String siteNum) {
        this.siteNum = siteNum;
    }
    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return StringUtils.trimToEmpty(webQuoteNum);
    }
    /**
     * @param webQuoteNum The webQuoteNum to set.
     */
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
    
    /**
     * @param ssPart
     */
    public void addSaaSEQPart(SpreadSheetPart ssPart) {
        this.getSaaSEqPartList().add(ssPart);
    }
    
    /**
     * @param ssPart
     */
    public void addEQPart(SpreadSheetPart ssPart) {
    	this.getEqPartList().add(ssPart);
    }
    
    /**
     * @param ssPart
     */
    public void addEQAllPart(SpreadSheetPart ssPart) {
    	this.getEqAllParsList().add(ssPart);
    }
    
    /**
     * @return Returns the pricingList.
     */
    public List getPricingList() {
        return pricingList;
    }
    /**
     * @param pricingList The pricingList to set.
     */
    public void setPricingList(List pricingList) {
        this.pricingList = pricingList;
    }
    
    public String getPartNums() {
        String partNums = "";
        List eqPartList = null;
        eqPartList = this.getEqPartList();
        
        if(null != eqPartList && !eqPartList.isEmpty()){
            StringBuffer buffer = new StringBuffer();
            for(Iterator it = eqPartList.iterator(); it.hasNext();){
                SpreadSheetPart ssPart = (SpreadSheetPart)it.next();
                String partNum = ssPart.getEpPartNumber();
                buffer.append(partNum + NewQuoteDBConstants.DELIMIT);
            }
            
            partNums = buffer.toString();
            partNums = partNums.substring(0, partNums.length()-1);
        }
        return partNums;
    }
    
    public String getSaaSPartNums() {
        String partNums = "";
        List eqSaaSPartList = null;
        eqSaaSPartList = this.getSaaSEqPartList();
        
        if(null != eqSaaSPartList && !eqSaaSPartList.isEmpty()){
            StringBuffer buffer = new StringBuffer();
            for(Iterator it = eqSaaSPartList.iterator(); it.hasNext();){
                SpreadSheetPart ssPart = (SpreadSheetPart)it.next();
                String partNum = ssPart.getEpPartNumber();
                buffer.append(partNum + NewQuoteDBConstants.DELIMIT);
            }
            
            partNums = buffer.toString();
            partNums = partNums.substring(0, partNums.length()-1);
        }
        return partNums;
    }
    
    public String getInvalidPartNums() {
    	String partNums = "";
    	if(null != invalidPartList && !invalidPartList.isEmpty()){
    		StringBuffer buffer = new StringBuffer();
            for(Iterator it = invalidPartList.iterator(); it.hasNext();){
                String invalidPartNum = (String)it.next();
                buffer.append(invalidPartNum + NewQuoteDBConstants.DELIMIT);
            }
            
            partNums = buffer.toString();
            partNums = partNums.substring(0, partNums.length()-1);
    	}
    	return partNums;
    }
    
    public String getInvalidSaaSPartNums() {
    	String partNums = "";
    	if(null != invalidSaaSPartList && !invalidSaaSPartList.isEmpty()){
    		StringBuffer buffer = new StringBuffer();
            for(Iterator it = invalidSaaSPartList.iterator(); it.hasNext();){
                String invalidSaaSPartNum = (String)it.next();
                buffer.append(invalidSaaSPartNum + NewQuoteDBConstants.DELIMIT);
            }
            
            partNums = buffer.toString();
            partNums = partNums.substring(0, partNums.length()-1);
    	}
    	return partNums;
    }
    
    public String toXMLString() throws Exception {
        Map propsMap = BeanUtils.describe(this);
        StringBuffer bf = new StringBuffer("<xmlssQuote><quoteSummary>");
        Set keys = propsMap.entrySet();
        for(Iterator it = keys.iterator(); it.hasNext();){
            java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
            Object key = entry.getKey();
            if(excludeFromXML.contains(key.toString())){
            	continue;
            }
            bf.append("<" + key + ">" + StringEscapeUtils.escapeXml((String)entry.getValue()) + "</" + entry.getKey() +">");
        }
        
        bf.append("</quoteSummary>");
        
        
        if(this.getEqPartList()!= null && this.getEqPartList().size()>0){
        	bf.append("<eqParts>");
	        List list= this.getEqPartList();
	        for(Iterator it = list.iterator(); it.hasNext();){
	            SpreadSheetPart part = (SpreadSheetPart) it.next();
	            bf.append(part.toXMLString());
	        }
	        bf.append("</eqParts>");
        }
        
        //populate SaaS configurations to xml here
	    List configrtnsList = this.getPartsPricingConfigrtnsList();
		Map configrtnsMap = this.getPartsPricingConfigrtnsMap();
	    if (configrtnsList != null && configrtnsList.size() > 0) {
	    	bf.append("<configurations>");
	    	Iterator configrtnsIt = configrtnsList.iterator();
			while(configrtnsIt.hasNext()){
				PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt.next();
				List subSaaSlineItemList = (List)configrtnsMap.get(ppc);
				if(subSaaSlineItemList != null && subSaaSlineItemList.size()>0){
					bf.append("<configuration id=\""+ StringEscapeUtils.escapeXml(ppc.getIbmProdId())+"\" "+ "desc=\""+ StringEscapeUtils.escapeXml(ppc.getIbmProdIdDscr())+"\"><eqParts>");
		            for (int i = 0; i < subSaaSlineItemList.size(); i++) {
		            	SpreadSheetPart  part = (SpreadSheetPart) subSaaSlineItemList.get(i);
		            	bf.append(part.toXMLString());
		            }
		            bf.append("</eqParts></configuration>");
				}
			}
			bf.append("</configurations>");
	    }
        bf.append("<pricingList>");
        for(Iterator it = this.getPricingList().iterator(); it.hasNext();){
            SpreadsheetPricing prcingData = (SpreadsheetPricing) it.next();
            bf.append(prcingData.toXMLString());
        }
        bf.append("</pricingList></xmlssQuote>");
        String value = bf.toString();
        return value;
    }
    /**
	 * @param subSaaSlineItemList
	 * @param currentIndex
	 * @return boolean
	 * judge whether show the replaced the parts label 
	 */
	public boolean showRepacePartsLabel(List subSaaSlineItemList, int currentIndex){
		QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemList.get(currentIndex);
		if(currentIndex == 0){
			if(qli.isReplacedPart()){
				return true;
			}
		} else {
			if(qli.isReplacedPart()){
				QuoteLineItem previousQli = (QuoteLineItem) subSaaSlineItemList.get(currentIndex - 1);
				if(!previousQli.isReplacedPart()){
					return true;
				}
			}
		}
		return false;
	}
    public boolean isImportable() {
        return isFCT() || isPA () || isPAE();
    }

    public String getQuoteExpDate() {
        return quoteExpDate;
    }

    public void setQuoteExpDate(String expDate) {
        this.quoteExpDate = expDate;
    }
    
    
    public String getQuoteStartDate() {
        return quoteStartDate;
    }

    public void setQuoteStartDate(String startDate) {
        this.quoteStartDate = startDate;
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
    private class XMLSSQuoteHandler extends DefaultHandler {
        private String currentElLocaleName = null;
        private String currentElText = null;
        private SpreadSheetQuote ssQuote = SpreadSheetQuote.this;
        
        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#characters(char[], int, int)
         */
        public void characters(char[] ch, int start, int length)
        throws SAXException {
            currentElText = String.valueOf(ch,start,length);
        }
        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if(currentElLocaleName != null && currentElText != null)
                populate(ssQuote, currentElLocaleName, currentElText.trim());
            currentElLocaleName = null;
            currentElText = null;
        }
        /**
         * @param propName
         * @param propValue
         * @throws SAXException
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         */
        private void populate(Object obj, String propName, String propValue) throws SAXException {
            try {
            	if(!StringUtils.isEmpty(propValue))
                BeanUtils.setProperty(obj, propName , propValue);
            } catch (Exception e) {
                throw new SAXException("failed to populate " + propName +" with value " + propValue, e);
            }
        }
        /* (non-Javadoc)
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            
            currentElLocaleName = localName;
            
            if(attributes.getLength()>0){
                SpreadSheetPart ssPart = new SpreadSheetPart();
                if (SpreadSheetPart.SOURCE_EP_TAB.equalsIgnoreCase(attributes.getValue("sourceTab")) || SpreadSheetPart.SOURCE_AP_TAB.equalsIgnoreCase(attributes.getValue("sourceTab"))) {
                    for(int i = 0 ; i< attributes.getLength(); i++){
                		populate(ssPart, attributes.getLocalName(i).trim(), attributes.getValue(i).trim());
                	}
                	ssQuote.addEQPart(ssPart);
                }
                if (SpreadSheetPart.SOURCE_SAAS_EP_TAB.equalsIgnoreCase(attributes.getValue("sourceTab")) || SpreadSheetPart.SOURCE_SAAS_AP_TAB.equalsIgnoreCase(attributes.getValue("sourceTab"))) {
                    for(int i = 0 ; i< attributes.getLength(); i++){
                		populate(ssPart, attributes.getLocalName(i).trim(), attributes.getValue(i).trim());
                	}
                	ssQuote.addSaaSEQPart(ssPart);
                }
                ssQuote.addEQAllPart(ssPart);	
            }
        }
    }
    /**
     * @return Returns the custDesignation.
     */
    public String getCustDesignation() {
        return custDesignation;
    }
    /**
     * @param custDesignation The custDesignation to set.
     */
    public void setCustDesignation(String custDesignation) {
        this.custDesignation = custDesignation;
    }
     
    /**
     * @return Returns the govEntityIndDesc.
     */
    public String getGovEntityIndDesc() {
        return govEntityIndDesc;
    }
    /**
     * @param govEntityIndDesc The govEntityIndDesc to set.
     */
    public void setGovEntityIndDesc(String govEntityIndDesc) {
        this.govEntityIndDesc = govEntityIndDesc;
    }
    
	/**
	 * @return Returns the isChannelMarginQuote.
	 */
	public boolean isChannelMarginQuote() {
		return isChannelMarginQuote;
	}
	/**
	 * @param isChannelMarginQuote The isChannelMarginQuote to set.
	 */
	public void setChannelMarginQuote(boolean isChannelMarginQuote) {
		this.isChannelMarginQuote = isChannelMarginQuote;
	}
    /**
     * @return Returns the invalidOCSPartList.
     */
    public List getInvalidOCSPartList() {
        return invalidOCSPartList;
    }
    /**
     * @param invalidOCSPartList The invalidOCSPartList to set.
     */
    public void setInvalidOCSPartList(List invalidOCSPartList) {
        this.invalidOCSPartList = invalidOCSPartList;
    }
    /**
     * @return Returns the isExceedMaxPartsQty.
     */
    public boolean isExceedMaxPartsQty() {
        return isExceedMaxPartsQty;
    }
    /**
     * @param isExceedMaxPartsQty The isExceedMaxPartsQty to set.
     */
    public void setExceedMaxPartsQty(boolean isExceedMaxPartsQty) {
        this.isExceedMaxPartsQty = isExceedMaxPartsQty;
    }
	/**
	 * @param fulfillmentSrc
	 */
	public void setFulfillmentSrc(String fulfillmentSrc) {
		this.fulfillmentSrc = fulfillmentSrc;
	}
	
	public String getFulfillmentSrc(){
		if("NOT_SPEC".equalsIgnoreCase(StringUtils.trimToEmpty(fulfillmentSrc))){
			return "Not specified";
		}
		return StringUtils.capitalise(StringUtils.trimToEmpty(fulfillmentSrc).toLowerCase());
	}
	/**
	 * @return Returns the opprtntyInfo.
	 */
	public String getOpprtntyInfo() {
		return opprtntyInfo;
	}
	/**
	 * @param opprtntyInfo The opprtntyInfo to set.
	 */
	public void setOpprtntyInfo(String opprtntyInfo) {
		this.opprtntyInfo = opprtntyInfo;
	}
	/**
	 * @param sapQuoteNum
	 */
	public void setSapQuoteNum(String sapQuoteNum) {
		this.sapQuoteNum = sapQuoteNum;
	}
	
	public String getSapQuoteNum(){
		return sapQuoteNum;
	}
	/**
	 * @param sapIntrmdiatDocNum
	 */
	public void setSapIDocNum(String sapIDocNum) {
		this.sapIDocNum = sapIDocNum;		
	}
	
	public String getSapIDocNum(){
		return sapIDocNum;
	}
	
	/**
	 * @return Returns the distributorCityAddr.
	 */
	public String getDistributorCityAddr() {
		return distributorCityAddr;
	}
	/**
	 * @param distributorCityAddr The distributorCityAddr to set.
	 */
	public void setDistributorCityAddr(String distributorCityAddr) {
		this.distributorCityAddr = distributorCityAddr;
	}
	/**
	 * @return Returns the distributorCntryAddr.
	 */
	public String getDistributorCntryAddr() {
		return distributorCntryAddr;
	}
	/**
	 * @param distributorCntryAddr The distributorCntryAddr to set.
	 */
	public void setDistributorCntryAddr(String distributorCntryAddr) {
		this.distributorCntryAddr = distributorCntryAddr;
	}
	/**
	 * @return Returns the distributorCustName.
	 */
	public String getDistributorCustName() {
		return distributorCustName;
	}
	/**
	 * @param distributorCustName The distributorCustName to set.
	 */
	public void setDistributorCustName(String distributorCustName) {
		this.distributorCustName = distributorCustName;
	}
	/**
	 * @return Returns the distributorCustNum.
	 */
	public String getDistributorCustNum() {
		return distributorCustNum;
	}
	/**
	 * @param distributorCustNum The distributorCustNum to set.
	 */
	public void setDistributorCustNum(String distributorCustNum) {
		this.distributorCustNum = distributorCustNum;
	}
	/**
	 * @return Returns the distributorIBMCustNum.
	 */
	public String getDistributorIBMCustNum() {
		return distributorIBMCustNum;
	}
	/**
	 * @param distributorIBMCustNum The distributorIBMCustNum to set.
	 */
	public void setDistributorIBMCustNum(String distributorIBMCustNum) {
		this.distributorIBMCustNum = distributorIBMCustNum;
	}
	/**
	 * @return Returns the distributorStreetAddr.
	 */
	public String getDistributorStreetAddr() {
		return distributorStreetAddr;
	}
	/**
	 * @param distributorStreetAddr The distributorStreetAddr to set.
	 */
	public void setDistributorStreetAddr(String distributorStreetAddr) {
		this.distributorStreetAddr = distributorStreetAddr;
	}
	/**
	 * @return Returns the resellerCityAddr.
	 */
	public String getResellerCityAddr() {
		return resellerCityAddr;
	}
	/**
	 * @param resellerCityAddr The resellerCityAddr to set.
	 */
	public void setResellerCityAddr(String resellerCityAddr) {
		this.resellerCityAddr = resellerCityAddr;
	}
	/**
	 * @return Returns the resellerCntryAddr.
	 */
	public String getResellerCntryAddr() {
		return resellerCntryAddr;
	}
	/**
	 * @param resellerCntryAddr The resellerCntryAddr to set.
	 */
	public void setResellerCntryAddr(String resellerCntryAddr) {
		this.resellerCntryAddr = resellerCntryAddr;
	}
	/**
	 * @return Returns the resellerCustName.
	 */
	public String getResellerCustName() {
		return resellerCustName;
	}
	/**
	 * @param resellerCustName The resellerCustName to set.
	 */
	public void setResellerCustName(String resellerCustName) {
		this.resellerCustName = resellerCustName;
	}
	/**
	 * @return Returns the resellerCustNum.
	 */
	public String getResellerCustNum() {
		return resellerCustNum;
	}
	/**
	 * @param resellerCustNum The resellerCustNum to set.
	 */
	public void setResellerCustNum(String resellerCustNum) {
		this.resellerCustNum = resellerCustNum;
	}
	/**
	 * @return Returns the resellerIBMCustNum.
	 */
	public String getResellerIBMCustNum() {
		return resellerIBMCustNum;
	}
	/**
	 * @param resellerIBMCustNum The resellerIBMCustNum to set.
	 */
	public void setResellerIBMCustNum(String resellerIBMCustNum) {
		this.resellerIBMCustNum = resellerIBMCustNum;
	}
	/**
	 * @return Returns the resellerStreetAddr.
	 */
	public String getResellerStreetAddr() {
		return resellerStreetAddr;
	}
	/**
	 * @param resellerStreetAddr The resellerStreetAddr to set.
	 */
	public void setResellerStreetAddr(String resellerStreetAddr) {
		this.resellerStreetAddr = resellerStreetAddr;
	}
	/**
	 * @return Returns the exemptnCode.
	 */
	public String getExemptnCode() {
		return exemptnCode;
	}
	/**
	 * @param exemptnCode The exemptnCode to set.
	 */
	public void setExemptnCode(String exemptnCode) {
		this.exemptnCode = exemptnCode;
	}
	
	/**
	 * @return Returns the totalPriceForExcel.
	 */
	public String getTotalPriceForExcel() {
	     return DecimalUtil.format(totalPrice,scale);
	}
	
	public double getDoubleTotalPriceForExcel() {
	     return totalPrice;
	}
	
	/**
	 * @return Returns the totalPointForExcel.
	 */
	public double getTotalPointsForExcel() {
		return totalPoints;
	}
	/**
	 * @param overallStatus
	 */
	public void setOverallStatus(String overallStatus) {
		this.overallStatus = overallStatus;
	}
	public String getOverallStatus() {
		return overallStatus;
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
	/**
	 * @return Returns the quoteOppOwnerEmail.
	 */
	public String getQuoteOppOwnerEmail() {
		return quoteOppOwnerEmail;
	}
	/**
	 * @param quoteOppOwnerEmail The quoteOppOwnerEmail to set.
	 */
	public void setQuoteOppOwnerEmail(String quoteOppOwnerEmail) {
		this.quoteOppOwnerEmail = quoteOppOwnerEmail;
	}
	/**
	 * @return Returns the sbApprovedDate.
	 */
	public String getSbApprovedDate() {
		return sbApprovedDate;
	}
	/**
	 * @param sbApprovedDate The sbApprovedDate to set.
	 */
	public void setSbApprovedDate(String sbApprovedDate) {
		this.sbApprovedDate = sbApprovedDate;
	}
	
	/**
	 * @return Returns the viewSubmittedQuoteDetailUrl.
	 */
	public String getViewSubmittedQuoteDetailUrl() {
		return viewSubmittedQuoteDetailUrl;
	}
	/**
	 * @param viewSubmittedQuoteDetailUrl The viewSubmittedQuoteDetailUrl to set.
	 */
	public void setViewSubmittedQuoteDetailUrl(
			String viewSubmittedQuoteDetailUrl) {
		this.viewSubmittedQuoteDetailUrl = viewSubmittedQuoteDetailUrl;
	}
	
	/**
     * @return Returns the hasInvalidFutureDates.
     */
    public boolean hasInvalidFutureDates() {
        return hasInvalidFutureDates;
    }
    /**
     * @param hasInvalidFutureDates The hasInvalidFutureDates to set.
     */
    public void setHasInvalidFutureDates(boolean hasInvalidFutureDates) {
        this.hasInvalidFutureDates = hasInvalidFutureDates;
    }
    /**
     * @return Returns the nimOffergCode.
     */
    public String getNimOffergCode() {
        return nimOffergCode;
    }
    /**
     * @param nimOffergCode The nimOffergCode to set.
     */
    public void setNimOffergCode(String nimOffergCode) {
        this.nimOffergCode = nimOffergCode;
    }
    
    
    /**
     * @return Returns the modDate.
     */
    public String getModDate() {
        return modDate;
    }
    /**
     * @param modDate The modDate to set.
     */
    public void setModDate(String modDate) {
        this.modDate = modDate;
    }
    
    
    /**
     * @return Returns the isToImportNewQuote.
     */
    public boolean isToImportNewQuote() {
        return isToImportNewQuote;
    }
    /**
     * @param isToImportNewQuote The isToImportNewQuote to set.
     */
    public void setToImportNewQuote(boolean isToImportNewQuote) {
        this.isToImportNewQuote = isToImportNewQuote;
    }
    /**
     * @return Returns the isToUnlockQuote.
     */
    public boolean isToUnlockQuote() {
        return isToUnlockQuote;
    }
    /**
     * @param isToUnlockQuote The isToUnlockQuote to set.
     */
    public void setToUnlockQuote(boolean isToUnlockQuote) {
        this.isToUnlockQuote = isToUnlockQuote;
    }
    
    
    /**
     * @return Returns the isLocked.
     */
    public boolean isLocked() {
        return isLocked;
    }
    /**
     * @param isLocked The isLocked to set.
     */
    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }
    /**
     * @return Returns the lockedBy.
     */
    public String getLockedBy() {
        return lockedBy;
    }
    /**
     * @param lockedBy The lockedBy to set.
     */
    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }
    
	public String getProgMigrationDscr() {
		return progMigrationDscr;
	}
	public void setProgMigrationDscr(String progMigrationDscr) {
		this.progMigrationDscr = progMigrationDscr;
	}
	

	public String getPreferDiscountTotal() {
		return preferDiscountTotal;
	}

	public void setPreferDiscountTotal(String preferDiscountTotal) {
		this.preferDiscountTotal = preferDiscountTotal;
	}

	public String getPreferBidUnitPriceTotal() {
		return preferBidUnitPriceTotal;
	}

	public void setPreferBidUnitPriceTotal(String preferBidUnitPriceTotal) {
		this.preferBidUnitPriceTotal = preferBidUnitPriceTotal;
	}

	public String getPreferBidExtendedPriceTotal() {
		return preferBidExtendedPriceTotal;
	}

	public void setPreferBidExtendedPriceTotal(String preferBidExtendedPriceTotal) {
		this.preferBidExtendedPriceTotal = preferBidExtendedPriceTotal;
	}

	public String getMaxDiscountTotal() {
		return maxDiscountTotal;
	}

	public void setMaxDiscountTotal(String maxDiscountTotal) {
		this.maxDiscountTotal = maxDiscountTotal;
	}

	public String getMaxBidUnitPriceTotal() {
		return maxBidUnitPriceTotal;
	}

	public void setMaxBidUnitPriceTotal(String maxBidUnitPriceTotal) {
		this.maxBidUnitPriceTotal = maxBidUnitPriceTotal;
	}

	public String getMaxBidExendedPriceTotal() {
		return maxBidExendedPriceTotal;
	}

	public void setMaxBidExendedPriceTotal(String maxBidExendedPriceTotal) {
		this.maxBidExendedPriceTotal = maxBidExendedPriceTotal;
	}

	private static String filterAlink(String str) {
		
		if(str.contains("<a ") || str.contains("<A ")){
			return str.replaceAll("<a ", "a ")
					.replaceAll("/a>", "/a")
					.replaceAll("<A ", "A ")
					.replaceAll("/A>", "/A");
		}
		
		return str;
	}
}
