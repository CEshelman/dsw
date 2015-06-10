package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.LineOfBusinessFactory;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.topaz.exception.TopazException;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>QuoteHeader_Impl<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-3-8
 */

public abstract class QuoteHeader_Impl implements QuoteHeader {

    public String webQuoteNum;

    public String creatorId;

    public String currencyCode;

    public String sapIntrmdiatDocNum;

    public String soldToCustNum;

    public String custName;

    public String contractNum;

    public String fulfillmentSrc;

    public int quoteExpDays = -1;

    public String rselCustNum;

    public String payerCustNum;

    public double quotePriceTot;

    public String quoteTitle;

    public String quoteDscr;

    public String priorQuoteNum;

    public String quoteStageCode;

    public Country country;

    public CodeDescObj lob;

    public CodeDescObj systemLOB;

    public Date modDate;

    public Date quoteExpDate;

    public String quoteTypeCode;

    public String renwlQuoteNum;

    public String ordgMethodCode;

    public String opprtntyNum;

    public String opprtntyOwnrEmailAdr;

    public String opprtntyOwnrName;
    
    public String busOrgCode;

    public String exemptnCode;

    public String volDiscLevelCode;

    public int webCustId = -1;

    public int gsaPricngFlg;

    public int speclBidFlag;
    
    public int speclBidSystemInitFlg;

    public int speclBidManualInitFlg;

    public int priceRecalcFlag;

    public String audCode;

    public String acqrtnCode;

    public Date priceStartDate;

    public String tranPriceLevelCode;

    public String ovrrdTranLevelCode;

    public Date lastQuoteDate;

    public String sapDistribtnChnlCode;

    public String sapSalesDocTypeCode;

    public int reqstIbmCustNumFlag;

    public int reqstPreCreditCheckFlag;

    public int partHasPriceFlag;

    public int hasPartsFlag;

    public double totalPoints;

    public int rnwlPrtnrAccessFlag;

    public Date renwlEndDate;

    public int ordQtyTotal;

    public String upsideTrendTowardsPurch;

    public String renwlQuoteSalesOddsOode;

    public int inclTaxFinalQuoteFlag;

    public int sendQuoteToPrmryCntFlag;

    public int sendQuoteToQuoteCntFlag;

    public int sendQuoteToAddtnlCntFlag;

    public String addtnlCntEmailAdr;

    public String salesComments;

    public String qSubmitCoverText;

    public String rnwlTermntnReasCode;

    public String termntnComments;

    public List tacticCodes;

    public Date rqModDate;

    public Date rqStatModDate;

    public List quoteOverallStatuses = new ArrayList();
    
    public List quoteOverallStatuses4Cpexdate = new ArrayList();

    public Date submittedDate;

    public String submitterId;

    public String sapQuoteNum;

    public String sapOrderNum;
    
    public int calcBLPriceFlag;

    public double dLocalExtndTaxAmtTot;

    public double dQuotePriceTotIncldTax;

    public boolean dateOvrrdByApprvrFlg = false;

    public String originalIdocNum;

    public int containLineItemRevnStrm;

    public String savedQuoteNum;

    public Double offerPrice;

    public String approverName;

    public Date approverAssignDate;

    public String sbApprovalGroupName;

    public int incldLineItmDtlQuoteFlg;

    public Country priceCountry;

    public String progMigrationCode;

    public int approvalRouteFlag;

    public int distribtrToBeDtrmndFlag;

    public boolean resellerToBeDtrmndFlag;

    public boolean sendQuoteToAddtnlPrtnrFlag;

    public String addtnlPrtnrEmailAdr;

    public boolean cmprssCvrageFlag;

    public boolean hasPARenwlLineItmsFlag;

    // material group 3 codes for all line items.
    public List acquisitionCodes = new ArrayList();

    //each element in this list is a string representing the reason code
    public List reasonCodes = new ArrayList();

    public String backDatingComment;

    public boolean backDatingFlag;

    public String quoteClassfctnCode;

    public int renwlQuoteSpeclBidFlag;

    public boolean ELAFlag;

    public int PAOBlockFlag;

    public String salesStageCode;

    public String custReasCode;

    public boolean createCtrctFlag;
    
    private boolean submittedForEval = false;
    
    private boolean acceptedByEval = false;
    
    private boolean returnForChgByEval = false;
    
    private boolean editForRetChgByEval = false;
    
    private boolean isAssignedEval = false;
    
    private String evalEmailAdr;
    
    private Date evalDate;

    public int webCtrctId;

    public Date priorQuoteSbmtDate;

    public boolean copyFromApprvdBidFlag;

    public Date quoteStartDate;

    public String endUserCustNum;

    
    private boolean copied4PrcIncrQuoteFlag;

    private Date quoteAddDate;

    private boolean lockedFlag;

    private String lockedBy;

    private int blockRnwlReminder;

    private String modByFullName;

    private boolean isMatchOfferPriceFailed = false ;

    private double oriQuotePriceTot;

    private boolean isQtEligible4BidIteratn;

    private boolean isBidIteratnQt;

    private int saasBidIteratnQtInd;
    
    private int softBidIteratnQtInd;
    
    private boolean noApprovalRequire = false;

    private boolean hasDiscountableItems;

    private int pymTermsDays;

    double latamUpliftPct;

    private boolean hasLotusLiveItem;

    private int oemBidType;

    private boolean copiedForOutputChangeFlag = false;

    private String submitterName;

    public String priorSapQuoteNum;

    public Integer fctNonStdTermsConds;

    private Double dSaasTotCmmtmtVal;

    public Double saasBpTCV;

    private boolean hasSaaSLineItem;

    private Date estmtdOrdDate;

    private String refDocNum;

    private boolean onlySaaSParts;
    
    private boolean hasRampUpPartFlag;

    private int maxEstdNumProvisngDays;

    private boolean hasConfigrtnFlag;

	private boolean cACustCurrncyNotMatchFlag;

	private boolean addTrdOrCotermFlag;

	private boolean hasNewConfFlag;

	private boolean isBPRelatedCust;

	public String qcCompany;

	public String qcCountry;
	
	public String creatorEmail;
	
	public String creatorName;
	
	public double quoteTotalEntitledExtendedPrice;
	
	private boolean matchBPsInChargeAgreementFlag;
	
	public boolean isSubmittedOnSQO = false;	

	private boolean hasNwLcPart = false;
	
	private boolean hasSubNoReNwPart = false;
    
    private int firmOrdLtrFlag;
    
    private Date custReqstArrivlDate;
    
    private boolean hasAppliancePartFlag = false;
    
    private String quoteOutputType = null;

	private boolean hasAppMainPart = false;
	
	private boolean hasAppUpgradePart = false;
	
	private boolean isSaasFCTToPAQuote = false;
	
	private boolean termDiffInDiffFctConfig = false;
	
	private String orignlSalesOrdRefNum;
	
	private boolean saaSStrmlndApprvlFlag = false;
	
	private Date priorQuoteExpDate;
	
	public String quoteOutputOption;
	
	public String streamlinedWebQuoteNum;
	
	private boolean rebillCreditOrder;
	
	private boolean isHasEvaluator;
	
	private boolean isQuoteCntryEvaluator = false;
	
	private String evalFullName;
	
	private boolean isSubmitByEvaluator = false;
	
	private String sspType;//sspProviderType
	
	private String caEndUserCustNum;//charge agreement  end user
	
	private boolean isDisShipInstAdrFlag;//display ship-to/install at address on CP tab flag
	
	// 15.1 add 4 col for renewal flag and migration flag of saas and monthly
	private Boolean isSaasRenewalFlag;
	private Boolean isSaasMigrationFlag;
	private Boolean isMonthlyRenewalFlag;
	private Boolean isMonthlyMigrationFlag;
	
	/**
     * YTY Growth
     */
	public Double ytyGrwthPct;
	public Double impldGrwthPct;
	public String quoteYty;
	
	private String bpSubmitterEmail;
	
	private String  bpSubmitterName;
	
	private Date bpSubmitDate;
	
	private boolean isPGSNewPAEnrolled;
	
	private boolean isRSVPSRPOnly;
	
	private boolean isAddTrd;
	
	private int rqLineItemCount = 0;
	
	private boolean hasRQLineItem = false;

	protected String deleteBy = null;
	
	private boolean isSoftwarePartWithoutApplncId;
	
	private String evalActionCode = null;
	
	private boolean isAllPartsHasMediaAttr = false;
	
	private double totalPriceInUSD = 0.0D;
	
	private boolean isECEligible;
	
	private boolean isECRecalculateFlag;
	
	private EquityCurveTotal equityCurveTotal;
	
	private boolean isOmittedLine;
	
	private int omittedLineRecalcFlag;
	
	private static List<String> editableList = new ArrayList<String>();
	
	private static List<String> nonEditableList = new ArrayList<String>();
	
	private int budgetaryQuoteFlag;
	
	private int installAtOpt;
	
	private boolean stdloneSaasGenTermFlag;
	
	private int saasTermCondCatFlag;
	
	private boolean isT2CreatedFlag;
	
	private double nonCOGandFNETAvgDiscount;
	
	private double cogAndFNETAvgDiscount;
	
	private boolean isMaxAvgDiscountExceeded = false;
	
	private boolean hasDivestedPart = false;
	
	private int divstdObsltPartFlag = 0;
	
	private List multipleImpliedGrowthList;
	
	private int endUserWebCustId = 0;
	
	private String agrmtTypeCode;

    /**
     * if the quote contains MTM invalid parts. 0, no; 1, contains.
     */
    private boolean serialNumWarningFlag;
    
    private boolean needReconfigFlag;
    
    private boolean gridFlag = false;

	private boolean isHasMonthlySoftPart;//check quote contain monthly software part

	private boolean ishasSoftSLineItem;// check quote contain SW part

	private String subRgnCode; //add for 14.3 grid criteria
	
	private Boolean isExpDateExtendedFlag;//add for 15.2 ,check if expiration date is changed
	
	private String expDateExtensionJustification;//add for 15.2 ,justification for expiration date extension 
	
//	private boolean isSSPCustHasContract = false;
	
	private boolean isDsjFlag;//for DSJ system
	
	static{
		editableList.add(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR);
		editableList.add(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO);
		editableList.add(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED);
		editableList.add(SubmittedQuoteConstants.OverallStatus.QUOTE_ON_HOLD);
		editableList.add(SubmittedQuoteConstants.OverallStatus.READY_TO_ORDER);
		
		nonEditableList.add(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG);
		nonEditableList.add(SubmittedQuoteConstants.OverallStatus.SPEC_BID_REJECTED);
		nonEditableList.add(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD);
		nonEditableList.add(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED);
		nonEditableList.add(SubmittedQuoteConstants.OverallStatus.BILLED_ORDER);
		nonEditableList.add(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED);
		nonEditableList.add(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS);
		nonEditableList.add(SubmittedQuoteConstants.OverallStatus.ORDER_REJECTED);
	}
	
	
	
	public int getBudgetaryQuoteFlag() {
		return budgetaryQuoteFlag;
	}

	public void setBudgetaryQuoteFlag(int budgetaryQuoteFlag) {
		this.budgetaryQuoteFlag = budgetaryQuoteFlag;
	}

	public double getTotalPriceInUSD() {
		return QuoteCommonUtil.getTotalPriceInUSD(this);		
	}

	public void setTotalPriceInUSD(double totalPriceInUSD) {
		this.totalPriceInUSD = totalPriceInUSD;
	}

	public boolean isAllPartsHasMediaAttr() {
		return isAllPartsHasMediaAttr;
	}

	public void setAllPartsHasMediaAttr(boolean isAllPartsHasMediaAttr) {
		this.isAllPartsHasMediaAttr = isAllPartsHasMediaAttr;
	}
	
	public String getEvalActionCode() {
		return evalActionCode;
	}

	public void setEvalActionCode(String evalActionCode) {
		this.evalActionCode = evalActionCode;
	}

	public boolean isSoftwarePartWithoutApplncId() {
		return isSoftwarePartWithoutApplncId;
	}

	public void setSoftwarePartWithoutApplncId(
			boolean isSoftwarePartWithoutApplncId) {
		this.isSoftwarePartWithoutApplncId = isSoftwarePartWithoutApplncId;
	}

	public void setDeleteBy(String userId)
	{
		this.deleteBy = userId;
	}
	
	public String getDeleteBy()
	{
		return this.deleteBy;
	}
	
	public boolean isQuoteCntryEvaluator() {
		return isQuoteCntryEvaluator;
	}

	public void setQuoteCntryEvaluator(boolean isQuoteCntryEvaluator) {
		this.isQuoteCntryEvaluator = isQuoteCntryEvaluator;
	}

	public boolean isHasEvaluator() {
		return isHasEvaluator;
	}

	public void setHasEvaluator(boolean isHasEvaluator) {
		this.isHasEvaluator = isHasEvaluator;
	}

	public boolean isTermDiffInDiffFctConfig() {
		return termDiffInDiffFctConfig;
	}

	public void setTermDiffInDiffFctConfig(boolean termDiffInDiffFctConfig) {
		this.termDiffInDiffFctConfig = termDiffInDiffFctConfig;
	}

	public boolean isSaasFCTToPAQuote() {
		return isSaasFCTToPAQuote;
	}

	public void setSaasFCTToPAQuote(boolean isSaasFCTToPAQuote) {
		this.isSaasFCTToPAQuote = isSaasFCTToPAQuote;
	}

	public boolean isHasAppMainPart() {
		return hasAppMainPart;
	}

	public void setHasAppMainPart(boolean hasAppMainPart) {
		this.hasAppMainPart = hasAppMainPart;
	}

	public String getQuoteOutputType() {
		return quoteOutputType;
	}

	public void setQuoteOutputType(String quoteOutputType) {
		this.quoteOutputType = quoteOutputType;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public boolean isSubmittedOnSQO() {
		return isSubmittedOnSQO;
	}

	public void setSubmittedOnSQO(boolean isSubmittedOnSQO) {
		this.isSubmittedOnSQO = isSubmittedOnSQO;
	}

	public boolean isMatchBPsInChargeAgreementFlag() {
		return matchBPsInChargeAgreementFlag;
	}

	public void setMatchBPsInChargeAgreementFlag(
			boolean matchBPsInChargeAgreementFlag) {
		this.matchBPsInChargeAgreementFlag = matchBPsInChargeAgreementFlag;
	}

	public String getQcCompany() {
		return qcCompany;
	}

	public void setQcCompany(String qcCompany) {
		this.qcCompany = qcCompany;
	}

	public String getQcCountry() {
		return qcCountry;
	}

	public void setQcCountry(String qcCountry) {
		this.qcCountry = qcCountry;
	}

	public boolean isBPRelatedCust() {
		return isBPRelatedCust;
	}

	public void setBPRelatedCust(boolean isBPRelatedCust) {
		this.isBPRelatedCust = isBPRelatedCust;
	}

	public boolean isHasNewConfFlag() {
		return hasNewConfFlag;
	}
	public void setHasNewConfFlag(boolean hasNewConfFlag) {
		this.hasNewConfFlag = hasNewConfFlag;
	}
	public boolean isAddTrdOrCotermFlag() {
		return addTrdOrCotermFlag;
	}
	public void setAddTrdOrCotermFlag(boolean addTrdOrCotermFlag) {
		this.addTrdOrCotermFlag = addTrdOrCotermFlag;
	}
	public boolean isCACustCurrncyNotMatchFlag() {
		return cACustCurrncyNotMatchFlag;
	}
	public void setCACustCurrncyNotMatchFlag(boolean custCurrncyNotMatchFlag) {
		cACustCurrncyNotMatchFlag = custCurrncyNotMatchFlag;
	}
	public boolean hasConfigrtnFlag() {
		return hasConfigrtnFlag;
	}
	public void setHasConfigrtnFlag(boolean hasConfigrtnFlag) {
		this.hasConfigrtnFlag = hasConfigrtnFlag;
	}

	public int getMaxEstdNumProvisngDays() {
		return maxEstdNumProvisngDays;
	}
	public void setMaxEstdNumProvisngDays(int maxEstdNumProvisngDays) {
		this.maxEstdNumProvisngDays = maxEstdNumProvisngDays;
	}
	public boolean isOnlySaaSParts() {
		return onlySaaSParts;
	}
	public void setOnlySaaSParts(boolean onlySaaSParts) {
		this.onlySaaSParts = onlySaaSParts;
	}
	
	public boolean isHasRampUpPartFlag() {
		return hasRampUpPartFlag;
	}
	public void setHasRampUpPartFlag(boolean hasRampUpPartFlag) {
		this.hasRampUpPartFlag = hasRampUpPartFlag;
	}

	public Double getSaasTotCmmtmtVal() {
		return dSaasTotCmmtmtVal;
	}
	public void setSaasTotCmmtmtVal(Double dSaasTotCmmtmtVal) {
		this.dSaasTotCmmtmtVal = dSaasTotCmmtmtVal;
	}

	public String getSubmitterName() {
        return submitterName;
    }
    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }
    public String getBusOrgCode() {
        return busOrgCode;
    }
    public String getContractNum() {
        return contractNum;
    }
    public Country getCountry() {
        return country;
    }
    public String getCreatorId() {
        return creatorId;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    public String getCountryCurrencyCode(){
    	 CodeDescObj currency = (CodeDescObj) this.getCountry().getCurrencyList().get(0);
    	return currency.getCode();
    }
    public String getExemptnCode() {
        return exemptnCode;
    }
    public String getFulfillmentSrc() {
        return fulfillmentSrc;
    }
    public int getGsaPricngFlg() {
        return gsaPricngFlg;
    }
    public CodeDescObj getLob() {
        return lob;
    }
    public CodeDescObj getSystemLOB() {
        return systemLOB;
    }
    public Date getModDate() {
        return modDate;
    }
    public String getOpprtntyNum() {
        return opprtntyNum;
    }
    public String getOpprtntyOwnrEmailAdr() {
        return opprtntyOwnrEmailAdr;
    }
    public String getPayerCustNum() {
        return payerCustNum;
    }
    public int getPriceRecalcFlag() {
        return priceRecalcFlag;
    }
    public String getPriorQuoteNum() {
        return priorQuoteNum;
    }
    public String getQuoteDscr() {
        return quoteDscr;
    }
    public Date getQuoteExpDate() {
        return quoteExpDate;
    }
    public int getQuoteExpDays() {
        return quoteExpDays;
    }
    public double getQuotePriceTot() {
        return quotePriceTot;
    }
    public String getQuoteStageCode() {
        return quoteStageCode;
    }
    public String getQuoteTitle() {
        return quoteTitle;
    }
    public String getQuoteTypeCode() {
        return quoteTypeCode;
    }
    public String getRenwlQuoteNum() {
        return renwlQuoteNum;
    }
    public String getOrdgMethodCode() {
        return ordgMethodCode;
    }
    public String getRselCustNum() {
        return rselCustNum;
    }
    public String getSapIntrmdiatDocNum() {
        return sapIntrmdiatDocNum;
    }
    public String getSoldToCustNum() {
        return soldToCustNum;
    }
    public int getSpeclBidFlag() {
        return speclBidFlag;
    }
    public int getRecalcPrcFlag(){
        return this.priceRecalcFlag;
    }
    public int getSpeclBidManualInitFlg() {
        return speclBidManualInitFlg;
    }
    public int getSpeclBidSystemInitFlg() {
        return speclBidSystemInitFlg;
    }
    public String getVolDiscLevelCode() {
        return volDiscLevelCode;
    }
    public int getWebCustId() {
        return webCustId;
    }
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    public String getCustName(){
        return custName;
    }

    public String getAcqrtnCode() {
        return acqrtnCode;
    }
    public String getAudCode() {
        return audCode;
    }
    public Date getLastQuoteDate() {
        return lastQuoteDate;
    }
    public Date getPriceStartDate() {
        return priceStartDate;
    }
    public int getReqstIbmCustNumFlag() {
        return reqstIbmCustNumFlag;
    }
    public int getReqstPreCreditCheckFlag() {
        return reqstPreCreditCheckFlag;
    }
    public String getSapDistribtnChnlCode() {
        return sapDistribtnChnlCode;
    }
    public String getSapSalesDocTypeCode() {
        return sapSalesDocTypeCode;
    }
    public String getTranPriceLevelCode() {
        return tranPriceLevelCode;
    }
    public int getPartHasPriceFlag() {
        return partHasPriceFlag;
    }
    public boolean getHasPartsFlag() {
        // if hasPartsFlag == 1, means the quote has parts, and no parts are obsolete;
        // if hasPartsFlag == 2, means the quote has parts, one or some of them are obsolete.
        return (hasPartsFlag == 1 || hasPartsFlag == 2);
    }
    public boolean getHasObsoletePartsFlag() {
        return (hasPartsFlag == 2);
    }
    public double getTotalPoints() {
        return totalPoints;
    }
    public int getRnwlPrtnrAccessFlag() {
        return rnwlPrtnrAccessFlag;
    }
    public boolean isSalesQuote()
    {
        return QuoteConstants.QUOTE_TYPE_SALES.equals(getQuoteTypeCode());
    }

    public boolean isRenewalQuote(){
        return QuoteConstants.QUOTE_TYPE_RENEWAL.equals(getQuoteTypeCode());
    }

    public boolean isPAQuote() {
        String sLob = this.lob == null ? "" : this.lob.getCode();
        return QuoteConstants.LOB_PA.equalsIgnoreCase(sLob);
    }

    public boolean isPAEQuote() {
        String sLob = this.lob == null ? "" : this.lob.getCode();
        return QuoteConstants.LOB_PAE.equalsIgnoreCase(sLob);
    }

    public boolean isFCTQuote() {
        String sLob = this.lob == null ? "" : this.lob.getCode();
        return QuoteConstants.LOB_FCT.equalsIgnoreCase(sLob);
    }

    public boolean isPPSSQuote() {
        String sLob = this.lob == null ? "" : this.lob.getCode();
        return QuoteConstants.LOB_PPSS.equalsIgnoreCase(sLob);
    }

    public boolean isPAUNQuote() {
        String sSystemLob = this.systemLOB == null ? "" : this.systemLOB.getCode();
        return QuoteConstants.LOB_PAUN.equalsIgnoreCase(sSystemLob);
    }

    public boolean isOEMQuote() {
        String sLob = this.lob == null ? "" : this.lob.getCode();
        return QuoteConstants.LOB_OEM.equalsIgnoreCase(sLob);
    }
    
    public boolean isCSRAQuote() {
        
        return QuoteConstants.LOB_CSRA.equalsIgnoreCase(agrmtTypeCode);
    }
    
    public boolean isCSTAQuote() {
    	
    	if(StringUtils.isBlank(agrmtTypeCode) && saasTermCondCatFlag == 2)
    		return true;
    	else 
    		return QuoteConstants.LOB_CSTA.equalsIgnoreCase(agrmtTypeCode); 
    }

    public boolean isSubmittedQuote(){
        return StringUtils.isNotBlank(sapIntrmdiatDocNum);
    }
    public boolean hasNewCustomer(){
        return this.hasNewSoldToCustomer();
    }
    //add and refactor for 14.1 ssp, new ssp end user customer not included it
    protected boolean hasNewSoldToCustomer()
    {
        return getWebCustId() > 0;
    }
    
    public boolean hasExistingCustomer(){
        return StringUtils.isNotBlank(getSoldToCustNum());
    }
    public String getAddtnlCntEmailAdr() {
        return addtnlCntEmailAdr;
    }
    public int getInclTaxFinalQuoteFlag() {
        return inclTaxFinalQuoteFlag;
    }
	public int getFirmOrdLtrFlag() {
		return firmOrdLtrFlag;
	}
	public void setFirmOrdLtrFlag(int firmOrdLtrFlag) {
		this.firmOrdLtrFlag = firmOrdLtrFlag;
	}
    public int getOrdQtyTotal() {
        return ordQtyTotal;
    }
    public String getQSubmitCoverText() {
        return qSubmitCoverText;
    }
    public Date getRenwlEndDate() {
        return renwlEndDate;
    }
    public String getRenwlQuoteSalesOddsOode() {
        return renwlQuoteSalesOddsOode;
    }
    public String getSalesComments() {
        return salesComments;
    }
    public int getSendQuoteToAddtnlCntFlag() {
        return sendQuoteToAddtnlCntFlag;
    }
    public int getSendQuoteToPrmryCntFlag() {
        return sendQuoteToPrmryCntFlag;
    }
    public int getSendQuoteToQuoteCntFlag() {
        return sendQuoteToQuoteCntFlag;
    }
    public List getTacticCodes() {
        return tacticCodes;
    }
    public String getUpsideTrendTowardsPurch() {
        return upsideTrendTowardsPurch;
    }
    public String getRnwlTermntnReasCode() {
        return rnwlTermntnReasCode;
    }
    public String getTermntnComments() {
        return termntnComments;
    }
    public Date getRqModDate() {
        return rqModDate;
    }
    public Date getRqStatModDate() {
        return rqStatModDate;
    }

    public List getQuoteOverallStatuses() {
        if(quoteOverallStatuses!= null){
            return quoteOverallStatuses;
        }else{
            return new ArrayList();
        }
    }

    public List getQuoteOverallStatuses4Cpexdate() {
        if(quoteOverallStatuses4Cpexdate!= null){
            return quoteOverallStatuses4Cpexdate;
        }else{
            return new ArrayList();
        }
    }
    
    public String getSspType() {
		return sspType;
	}

	public void setSspType(String sspType) {
		this.sspType = sspType;
	}

	public Date getSubmittedDate() {
        return submittedDate;
    }
    public String getSubmitterId() {
        return submitterId;
    }
    public String getSapQuoteNum() {
        return sapQuoteNum;
    }

    public int getCalcBLPriceFlag() {
        return this.calcBLPriceFlag;
    }
    public boolean containsOverallStatus(String overallStatus) {
        if (this.quoteOverallStatuses == null || this.quoteOverallStatuses.size() == 0)
            return false;
        for (int i = 0; i < this.quoteOverallStatuses.size(); i++) {
            CodeDescObj qtOS = (CodeDescObj) this.quoteOverallStatuses.get(i);
            if (qtOS != null && qtOS.getCode().substring(0, 5).equalsIgnoreCase(overallStatus))
                return true;
        }
        return false;
    }
    
    public boolean containsOverallStatus4Cpexdate(String overallStatus) {
        if (this.quoteOverallStatuses4Cpexdate == null || this.quoteOverallStatuses4Cpexdate.size() == 0)
            return false;
        for (int i = 0; i < this.quoteOverallStatuses4Cpexdate.size(); i++) {
            CodeDescObj qtOS = (CodeDescObj) this.quoteOverallStatuses4Cpexdate.get(i);
            if (qtOS != null && qtOS.getCode().substring(0, 5).equalsIgnoreCase(overallStatus))
                return true;
        }
        return false;
    }

    public double getLocalExtndTaxAmtTot() {
        return dLocalExtndTaxAmtTot;
    }

    public double getQuotePriceTotIncldTax() {
        return dQuotePriceTotIncldTax;
    }
    public boolean getDateOvrrdByApproverFlag(){
        return this.dateOvrrdByApprvrFlg;
    }
    public void setDateOvrrdByApproverFlag(boolean dateOvrrdByApproverFlag) {
    	this.dateOvrrdByApprvrFlg = dateOvrrdByApproverFlag;
    }

    public String getOriginalIdocNum(){
        return originalIdocNum;
    }

    public int getContainLineItemRevnStrm() {
        return containLineItemRevnStrm;
    }

    public String getSavedQuoteNum() {
        return savedQuoteNum;
    }

    public String getOvrrdTranLevelCode() {
        return ovrrdTranLevelCode;
    }

    public Double getOfferPrice(){
        return offerPrice;
    }

    public int getIncldLineItmDtlQuoteFlg() {
        return incldLineItmDtlQuoteFlg;
    }

    public Country getPriceCountry() {
        return priceCountry;
    }

    public String getProgMigrationCode() {
        return progMigrationCode;
    }

    public boolean isMigration() {
        return QuoteConstants.MIGRTN_CODE_FCT_TO_PA.equalsIgnoreCase(this.progMigrationCode);
    }

    public List getAcquisitionCodes() {
        return acquisitionCodes;
    }

    public int getApprovalRouteFlag() {
        return approvalRouteFlag;
    }

    public String getAddtnlPrtnrEmailAdr() {
        return addtnlPrtnrEmailAdr;
    }

    public boolean isDistribtrToBeDtrmndFlag() {
        return QuoteConstants.DISTRIBUTOR_TO_BE_DTRMND == distribtrToBeDtrmndFlag;
    }

    public boolean isSingleTierNoDistributorFlag() {
        return QuoteConstants.SINGLE_TIER_NO_DISTRIBUTOR == distribtrToBeDtrmndFlag;
    }

    public boolean isResellerToBeDtrmndFlag() {
        return resellerToBeDtrmndFlag;
    }

    public boolean isSendQuoteToAddtnlPrtnrFlag() {
        return sendQuoteToAddtnlPrtnrFlag;
    }

    public boolean isFCTToPAQuote() {
        String sysLobCode = this.systemLOB == null ? "" : this.systemLOB.getCode();
        return ((QuoteConstants.LOB_PA.equalsIgnoreCase(sysLobCode)
                || QuoteConstants.LOB_PAE.equalsIgnoreCase(sysLobCode)
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(sysLobCode))
                && QuoteConstants.MIGRTN_CODE_FCT_TO_PA.equalsIgnoreCase(this.progMigrationCode));
    }

    public boolean isTBDQuote() {
        // if reseller tbd flag == 0, distributor tbd flag == 2, distributor will be auto-filled, it is not tbd quote;
        return ((isPAQuote() || isPAEQuote() || isSSPQuote()) && isSalesQuote() && (isResellerToBeDtrmndFlag() || isDistribtrToBeDtrmndFlag()));
    }

    public void setLobCode(String systemLobCode) throws TopazException {
        String lobCode = systemLobCode;
        if (QuoteConstants.LOB_PAUN.equalsIgnoreCase(lobCode)) {
            if (this.totalPoints >= QuoteConstants.PA_MIN_POINTS)
                lobCode = QuoteConstants.LOB_PA;
            else
                lobCode = QuoteConstants.LOB_PAE;
        }
        if (QuoteConstants.LOB_CSRA.equalsIgnoreCase(lobCode) || QuoteConstants.LOB_CSTA.equalsIgnoreCase(lobCode)){
        	 lobCode = QuoteConstants.LOB_CSA;
        }
        this.systemLOB = LineOfBusinessFactory.singleton().findLOBByCode(systemLobCode);
        this.lob = LineOfBusinessFactory.singleton().findLOBByCode(lobCode);
    }

	public boolean shouldSentTouUrltoSAP() {
		/*if the quote can be assgined the saasTermCondCatFlag and 
		the saasTermCondCatFlag==2(CSA),then did not send tou url(or send empty url).
		otherwise send the ToU urls configured in the line item.
		*/
    	return !(
    			this.isPAEQuote()
    			&& this.isOnlySaaSParts()
    			&& !this.isChannelQuote()
    			//if above 3 conditions are all true, then sales rep should select saasTermCondCatFlag on tou page. 
    			// refer to ToUList.jsp 
    			&& this.getSaasTermCondCatFlag() == 2 //sales rep selected CSA in TOU page
    	);
	}

	public List getReasonCodes(){
    	return reasonCodes;
    }

    public String getBackDatingComment(){
    	return backDatingComment;
    }

    public boolean getBackDatingFlag(){
    	return backDatingFlag;
    }

    public String getQuoteClassfctnCode() {
        return quoteClassfctnCode;
    }

    public int getRenwlQuoteSpeclBidFlag() {
        return renwlQuoteSpeclBidFlag;
    }

    public boolean isELAQuote() {
        return ELAFlag;
    }

    public int getPAOBlockFlag() {
        return PAOBlockFlag;
    }

    public String getCustReasCode() {
        return custReasCode;
    }

    public String getSalesStageCode() {
        return salesStageCode;
    }

    public boolean isCreateCtrctFlag() {
        return createCtrctFlag;
    }
    
    public boolean isSubmittedForEval() {
    	if(QuoteConstants.QUOTE_STAGE_CODE_SUBMITTED_FOR_EVAL.equals(quoteStageCode)){
    		submittedForEval = true;
    	}
        return submittedForEval;
    }
    
    public boolean isAcceptedByEval() {
    	if(QuoteConstants.QUOTE_STAGE_CODE_ACCEPTED_EVAL.equals(quoteStageCode)){
    		acceptedByEval = true;
    	}
        return acceptedByEval;
    }
    
    public boolean isReturnForChgByEval() {
    	if(QuoteConstants.QUOTE_STAGE_CODE_RETURN_FORCHG_EVAL.equals(quoteStageCode)){
    		returnForChgByEval = true;
    	}
        return returnForChgByEval;
    }
    
    public boolean isEditForRetChgByEval() {
    	if(QuoteConstants.QUOTE_STAGE_CODE_EDIT_FORRETCHG_EVAL.equals(quoteStageCode)){
    		editForRetChgByEval = true;
    	}
        return editForRetChgByEval;
    }
    
    public boolean  isAssignedEval(String userId){
    	if(userId != null && userId.equals(evalEmailAdr)) {
    		isAssignedEval = true;
    	}
    	return isAssignedEval;
    }
    
    public boolean isDeleteByEditor()
    {
    	if ( QuoteConstants.QUOTE_STAGE_CODE_DELETE.equals(quoteStageCode)) {
    		return true;
    	}
    	return false;
    }
    
    public String getEvalEmailAdr() {
        return evalEmailAdr;
    }
    
    public void setEvalEmailAdr(String evalEmailAdr) {
        this.evalEmailAdr = evalEmailAdr;
    }
    
    public Date getEvalDate() {
        return evalDate;
    }
    
    public void setEvalDate(Date evalDate) {
        this.evalDate = evalDate;
    }
    
    public int getWebCtrctId() {
        return webCtrctId;
    }

    public Date getPriorQuoteSbmtDate() {
        return priorQuoteSbmtDate;
    }

    public boolean isCopied4ReslChangeFlag() {
        return copyFromApprvdBidFlag;
    }

    public Date getQuoteStartDate() {
        return quoteStartDate;
    }

    public String getEndUserCustNum() {
        return endUserCustNum;
    }

    public String setEndUserCustNum(String endUserCustNum) {
        return this.endUserCustNum=endUserCustNum;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("webQuoteNum = ").append(webQuoteNum).append("\n");
        buffer.append("renwlQuoteNum = ").append(renwlQuoteNum).append("\n");
        buffer.append("quoteStageCode = ").append(quoteStageCode).append("\n");
        buffer.append("quoteTypeCode = ").append(quoteTypeCode).append("\n");
        buffer.append("creatorId = ").append(creatorId).append("\n");
        buffer.append("countryCode = ").append(country == null? null : country.getCode3()).append("\n");
        buffer.append("priceCountryCode = ").append(priceCountry == null? null : priceCountry.getCode3()).append("\n");
        buffer.append("currencyCode = ").append(currencyCode).append("\n");
        buffer.append("sapIntrmdiatDocNum = ").append(sapIntrmdiatDocNum).append("\n");
        buffer.append("originalIdocNum= ").append(originalIdocNum).append("\n");
        buffer.append("soldToCustNum = ").append(soldToCustNum).append("\n");
        buffer.append("custName = ").append(custName).append("\n");
        buffer.append("contractNum = ").append(contractNum).append("\n");
        buffer.append("fulfillmentSrc = ").append(fulfillmentSrc).append("\n");
        buffer.append("quoteExpDays = ").append(quoteExpDays).append("\n");
        buffer.append("quoteExpDate = ").append(quoteExpDate).append("\n");
        buffer.append("rselCustNum = ").append(rselCustNum).append("\n");
        buffer.append("payerCustNum = ").append(payerCustNum).append("\n");
        buffer.append("quotePriceTot = ").append(quotePriceTot).append("\n");
        buffer.append("quoteTitle = ").append(quoteTitle).append("\n");
        buffer.append("quoteDscr = ").append(quoteDscr).append("\n");
        buffer.append("priorQuoteNum = ").append(priorQuoteNum).append("\n");
        buffer.append("opprtntyNum = ").append(opprtntyNum).append("\n");
        buffer.append("opprtntyOwnrEmailAdr = ").append(opprtntyOwnrEmailAdr).append("\n");
        buffer.append("busOrgCode = ").append(busOrgCode).append("\n");
        buffer.append("lob = ").append(lob == null? null : lob.getCode()).append("\n");
        buffer.append("exemptnCode = ").append(exemptnCode).append("\n");
        buffer.append("volDiscLevelCode = ").append(volDiscLevelCode).append("\n");
        buffer.append("webCustId = ").append(webCustId).append("\n");
        buffer.append("gsaPricngFlg = ").append(gsaPricngFlg).append("\n");
        buffer.append("speclBidFlag = ").append(speclBidFlag).append("\n");
        buffer.append("speclBidSystemInitFlg = ").append(speclBidSystemInitFlg).append("\n");
        buffer.append("speclBidManualInitFlg = ").append(speclBidManualInitFlg).append("\n");
        buffer.append("priceRecalcFlag = ").append(priceRecalcFlag).append("\n");
        buffer.append("audCode = ").append(audCode).append("\n");
        buffer.append("acqrtnCode = ").append(acqrtnCode).append("\n");
        buffer.append("priceStartDate = ").append(priceStartDate).append("\n");
        buffer.append("tranPriceLevelCode = ").append(tranPriceLevelCode).append("\n");
        buffer.append("ovrrdTranLevelCode = ").append(ovrrdTranLevelCode).append("\n");
        buffer.append("lastQuoteDate = ").append(lastQuoteDate).append("\n");
        buffer.append("sapDistribtnChnlCode = ").append(sapDistribtnChnlCode).append("\n");
        buffer.append("sapSalesDocTypeCode = ").append(sapSalesDocTypeCode).append("\n");
        buffer.append("reqstIbmCustNumFlag = ").append(reqstIbmCustNumFlag).append("\n");
        buffer.append("reqstPreCreditCheckFlag = ").append(reqstPreCreditCheckFlag).append("\n");
        buffer.append("partHasPriceFlag = ").append(partHasPriceFlag).append("\n");
        buffer.append("hasPartsFlag = ").append(hasPartsFlag).append("\n");
        buffer.append("totalPoints = ").append(totalPoints).append("\n");
        buffer.append("rnwlPrtnrAccessFlag = ").append(rnwlPrtnrAccessFlag).append("\n");
        buffer.append("renwlEndDate = ").append(renwlEndDate).append("\n");
        buffer.append("ordQtyTotal = ").append(ordQtyTotal).append("\n");
        buffer.append("upsideTrendTowardsPurch = ").append(upsideTrendTowardsPurch).append("\n");
        buffer.append("renwlQuoteSalesOddsOode = ").append(renwlQuoteSalesOddsOode).append("\n");
        buffer.append("inclTaxFinalQuoteFlag = ").append(inclTaxFinalQuoteFlag).append("\n");
        buffer.append("firmOrdLtrFlag = ").append(firmOrdLtrFlag).append("\n");
        buffer.append("sendQuoteToPrmryCntFlag = ").append(sendQuoteToPrmryCntFlag).append("\n");
        buffer.append("sendQuoteToQuoteCntFlag = ").append(sendQuoteToQuoteCntFlag).append("\n");
        buffer.append("sendQuoteToAddtnlCntFlag = ").append(sendQuoteToAddtnlCntFlag).append("\n");
        buffer.append("addtnlCntEmailAdr = ").append(addtnlCntEmailAdr).append("\n");
        buffer.append("salesComments = ").append(salesComments).append("\n");
        buffer.append("qSubmitCoverText = ").append(qSubmitCoverText).append("\n");
        buffer.append("rnwlTermntnReasCode = ").append(rnwlTermntnReasCode).append("\n");
        buffer.append("termntnComments = ").append(termntnComments).append("\n");
        for(int i = 0; tacticCodes != null && i < tacticCodes.size(); i ++){
            TacticCode tacticCode = (TacticCode) tacticCodes.get(i);
            buffer.append("tacticCodes=" + i).append(tacticCode.toString()).append("\n");
        }
        buffer.append("rqModDate = ").append(rqModDate).append("\n");
        buffer.append("rqStatModDate = ").append(rqStatModDate).append("\n");
        buffer.append("submittedDate = ").append(submittedDate).append("\n");
        buffer.append("sapQuoteNum = ").append(sapQuoteNum).append("\n");
        buffer.append("dateChangedByApprover = ").append(dateOvrrdByApprvrFlg).append("\n");
        buffer.append("containLineItemRevnStrm = ").append(containLineItemRevnStrm).append("\n");
        buffer.append("savedQuoteNum = ").append(savedQuoteNum).append("\n");
        for(int i = 0; quoteOverallStatuses!=null && i<quoteOverallStatuses.size(); i ++){
            buffer.append("quoteOverallStatuses "+ i+" =" ).append(quoteOverallStatuses.get(i).toString()).append("\n");
        }
        buffer.append("offerPrice = ").append(offerPrice).append("\n");
        buffer.append("approverAssignDate = ").append(approverAssignDate).append("\n");
        buffer.append("approverName = ").append(approverName).append("\n");
        buffer.append("sbApprovalGroupName = ").append(sbApprovalGroupName).append("\n");
        for (int i = 0; acquisitionCodes != null && i < acquisitionCodes.size(); i++) {
            String acquisitionCode = (String) acquisitionCodes.get(i);
            buffer.append("acquisitionCode=" + i).append(acquisitionCode).append("\n");
        }

        buffer.append("backDatingComment = ").append(backDatingComment).append("\n");

        if(reasonCodes != null){
        	buffer.append("reasonCodes = {" );
            for(int i = 0; i < reasonCodes.size(); i++){
            	buffer.append((String)reasonCodes.get(i)).append(" ");
            }
            buffer.append("}");
        }
        buffer.append("quoteClassfctnCode = ").append(quoteClassfctnCode).append("\n");
        buffer.append("renwlQuoteSpeclBidFlag = ").append(renwlQuoteSpeclBidFlag).append("\n");
        buffer.append("ELAFlag = ").append(ELAFlag).append("\n");
        buffer.append("PAOBlockFlag = ").append(PAOBlockFlag).append("\n");
        buffer.append("salesStageCode = ").append(salesStageCode).append("\n");
        buffer.append("custReasCode = ").append(custReasCode).append("\n");
        buffer.append("createCtrctFlag = ").append(createCtrctFlag).append("\n");
        buffer.append("submittedForEval = ").append(submittedForEval).append("\n");
        buffer.append("acceptedByEval = ").append(acceptedByEval).append("\n");
        buffer.append("returnForChgByEval = ").append(returnForChgByEval).append("\n");
        buffer.append("editForRetChgByEval = ").append(editForRetChgByEval).append("\n");
        buffer.append("evalEmailAdr = ").append(evalEmailAdr).append("\n");
        buffer.append("evalDate = ").append(evalDate).append("\n");
        
        buffer.append("webCtrctId = ").append(webCtrctId).append("\n");
        buffer.append("modByFullName = ").append(modByFullName).append("\n");

        buffer.append("quoteStartDate = ").append(quoteStartDate).append("\n");
        buffer.append("qcCompany = ").append(qcCompany).append("\n");
        buffer.append("qcCountry = ").append(qcCountry).append("\n");
        buffer.append("budgetaryQuoteFlag = ").append(budgetaryQuoteFlag).append("\n");
        buffer.append("stdloneSaasGenTermFlag = ").append(stdloneSaasGenTermFlag).append("\n");
        buffer.append("saasTermCondCatFlag = ").append(saasTermCondCatFlag).append("\n");
        buffer.append("isT2CreatedFlag = ").append(isT2CreatedFlag).append("\n");
        
        return buffer.toString();
    }


    /**
     * @return Returns the approverAssignDate.
     */
    public Date getApproverAssignDate() {
        return approverAssignDate;
    }
    /**
     * @return Returns the approverName.
     */
    public String getApproverName() {
        return approverName;
    }
    /**
     * @return Returns the sbApprovalGroupName.
     */
    public String getSbApprovalGroupName() {
        return sbApprovalGroupName;
    }

    public boolean isSpBiddableRQ(){
    	return QuoteConstants.QUOTE_TYPE_SALES.equals(getQuoteTypeCode())
				&& (renwlQuoteSpeclBidFlag == 1);
    }

    public boolean isChannelQuote(){
    	return QuoteConstants.FULFILLMENT_CHANNEL.equals(getFulfillmentSrc());
    }

    public boolean getCmprssCvrageFlag(){
        return cmprssCvrageFlag;
    }

    public boolean getHasPARenwlLineItmsFlag(){
        return hasPARenwlLineItmsFlag;
    }

    public boolean isCopied4PrcIncrQuoteFlag(){
        return this.copied4PrcIncrQuoteFlag;
    }

    public boolean isLockedFlag(){
        return this.lockedFlag;
    }

    public String getLockedBy(){
        return this.lockedBy;
    }

    /**
     * @return Returns the modByFullName.
     */
    public String getModByFullName() {
        return modByFullName;
    }
    /**
     * @param modByFullName The modByFullName to set.
     */
    public void setModByFullName(String modByFullName) {
        this.modByFullName = modByFullName;
    }
    /**
     * @param copied4PrcIncrQuoteFlag The copied4PrcIncrQuoteFlag to set.
     */
    public void setCopied4PrcIncrQuoteFlag(boolean copied4PrcIncrQuoteFlag) {
        this.copied4PrcIncrQuoteFlag = copied4PrcIncrQuoteFlag;
    }
    /**
     * @return Returns the quoteAddDate.
     */
    public Date getQuoteAddDate() {
        return quoteAddDate;
    }
    /**
     * @param quoteAddDate The quoteAddDate to set.
     */
    public void setQuoteAddDate(Date quoteAddDate) {
        this.quoteAddDate = quoteAddDate;
    }
    /**
     * @param lockedFlag The lockedFlag to set.
     */
    public void setLockedFlag(boolean lockedFlag) {
        this.lockedFlag = lockedFlag;
    }
    /**
     * @param lockedBy The lockedBy to set.
     */
    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }
     /**
     * @return Returns the blockRnwlReminder.
     */
    public int getBlockRnwlReminder() {
        return blockRnwlReminder;
    }
    /**
     * @param blockRnwlReminder The blockRnwlReminder to set.
     */
    public void setBlockRnwlReminder(int blockRnwlReminder) {
        this.blockRnwlReminder = blockRnwlReminder;
    }
    public boolean isMatchOfferPriceFailed() {
        return isMatchOfferPriceFailed;
    }
    public void setMatchOfferPriceFailed(boolean isMatchOfferPriceFailed) {
        this.isMatchOfferPriceFailed = isMatchOfferPriceFailed;
    }

    /**
     * @return Returns the oriQuotePriceTot.
     */
    public double getOriQuotePriceTot() {
        return oriQuotePriceTot;
    }
    /**
     * @param oriQuotePriceTot The oriQuotePriceTot to set.
     */
    public void setOriQuotePriceTot(double oriQuotePriceTot) {
        this.oriQuotePriceTot = oriQuotePriceTot;
    }
    /**
     * @return Returns the isBidIteratnQtFlag.
     */
    public boolean isBidIteratnQt() {
        return isBidIteratnQt;
    }
    /**
     * @param isBidIteratnQtFlag The isBidIteratnQtFlag to set.
     */
    public void setBidIteratnQt(boolean isBidIteratnQtFlag) {
        this.isBidIteratnQt = isBidIteratnQtFlag;
    }
    

	public int getSaasBidIteratnQtInd() {
		return saasBidIteratnQtInd;
	}

	public void setSaasBidIteratnQtInd(int saasBidIteratnQtInd) {
		this.saasBidIteratnQtInd = saasBidIteratnQtInd;
	}

	public int getSoftBidIteratnQtInd() {
		return softBidIteratnQtInd;
	}

	public void setSoftBidIteratnQtInd(int softBidIteratnQtInd) {
		this.softBidIteratnQtInd = softBidIteratnQtInd;
	}

	/**
     * @return Returns the isQtEligible4BidIteratn.
     */
    public boolean isQtEligible4BidIteratn() {
        return isQtEligible4BidIteratn;
    }
    /**
     * @param isQtEligible4BidIteratn The isQtEligible4BidIteratn to set.
     */
    public void setQtEligible4BidIteratn(boolean isQtEligible4BidIteratn) {
        this.isQtEligible4BidIteratn = isQtEligible4BidIteratn;
    }
    /**
     * @return Returns the hasDiscountableItems.
     */
    public boolean hasDiscountableItems() {
        return hasDiscountableItems;
    }
    /**
     * @param hasDiscountableItems The hasDiscountableItems to set.
     */
    public void setHasDiscountableItems(boolean hasDiscountableItems) {
        this.hasDiscountableItems = hasDiscountableItems;
    }
    /**
     * @return Returns the pymTermsDays.
     */
    public int getPymTermsDays() {
        return pymTermsDays;
    }
    /**
     * @param pymTermsDays The pymTermsDays to set.
     */
    public void setPymTermsDays(int pymTermsDays) {
        this.pymTermsDays = pymTermsDays;
    }
    public double getLatamUpliftPct() {
        return latamUpliftPct;
    }
    public void setLatamUpliftPct(double latamUpliftPct) {
        this.latamUpliftPct = latamUpliftPct;
    }

    /**
     * @return Returns the hasLotusLiveItem.
     */
    public boolean hasLotusLiveItem() {
        return hasLotusLiveItem;
    }
    /**
     * @param hasLotusLiveItem The hasLotusLiveItem to set.
     */
    public void setHasLotusLiveItem(boolean hasLotusLiveItem) {
        this.hasLotusLiveItem = hasLotusLiveItem;
    }

    public boolean isTriggerTC(){
        return ButtonDisplayRuleFactory.singleton().isDisplayLAUplift(this) &&
               ((pymTermsDays > DraftQuoteConstants.PYMNT_TERMS_STAND_DAYS)
               || (CommonServiceUtil.getValidityDays(this) > DraftQuoteConstants.PYMNT_TERMS_STAND_DAYS));
    }

    /**
     * @return Returns the oemBidType.
     */
    public int getOemBidType() {
        return oemBidType;
    }
    /**
     * @param oemBidType The oemBidType to set.
     */
    public void setOemBidType(int oemBidType) {
        this.oemBidType = oemBidType;
    }


    public boolean isCopiedForOutputChangeFlag(){
    	return this.copiedForOutputChangeFlag;
    }

    public void setCopiedForOutputChangeFlag(boolean copiedForOutputChangeFlag){
    	this.copiedForOutputChangeFlag = copiedForOutputChangeFlag;
    }

	public String getPriorSapQuoteNum() {
		return priorSapQuoteNum;
	}
	public void setPriorSapQuoteNum(String priorSapQuoteNum) {
		this.priorSapQuoteNum = priorSapQuoteNum;
	}

	public Integer getFctNonStdTermsConds(){
		return this.fctNonStdTermsConds;
	}

	public boolean hasSaaSLineItem() {
		return hasSaaSLineItem;
	}
	
    public boolean hasSoftSLineItem(){
		return ishasSoftSLineItem;
    }
    
	public void setHasSaaSLineItem(boolean hasSaaSLineItem) {
		this.hasSaaSLineItem = hasSaaSLineItem;
	}

	public Date getEstmtdOrdDate() {
		return estmtdOrdDate;
	}
	public void setEstmtdOrdDate(Date estmtdOrdDate) {
		this.estmtdOrdDate = estmtdOrdDate;
	}

	public String getRefDocNum() {
		return refDocNum;
	}

	public void setRefDocNum(String refDocNum) {
		this.refDocNum = refDocNum;
	}

	public boolean isPGSQuote(){
		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(this.getAudCode());
	}

	public Double getSaasBpTCV() {
		return saasBpTCV;
	}

	public void setSaasBpTCV(Double saasBpTCV) {
		this.saasBpTCV = saasBpTCV;
	}

	public double getQuoteTotalEntitledExtendedPrice() {
		return quoteTotalEntitledExtendedPrice;
	}

	public void setQuoteTotalEntitledExtendedPrice(
			double quoteTotalEntitledExtendedPrice) {
		this.quoteTotalEntitledExtendedPrice = quoteTotalEntitledExtendedPrice;
	}
	
	public void setHasNewLicencePart(boolean hasNwLcPart){
		this.hasNwLcPart = hasNwLcPart;
	}
	public boolean isHasNewLicencePart(){
		return this.hasNwLcPart;
	}
	
	public void setHasSaasSubNoReNwPart(boolean hasSubNoReNwPart){
		this.hasSubNoReNwPart = hasSubNoReNwPart;
	}
	
	public boolean isHasSaasSubNoReNwPart(){
		return this.hasSubNoReNwPart;
	}

	public Date getCustReqstArrivlDate() {
		return custReqstArrivlDate;
	}

	public void setCustReqstArrivlDate(Date custReqstArrivlDate) {
		this.custReqstArrivlDate = custReqstArrivlDate;
	}

	public boolean isHasAppliancePartFlag() {
		return hasAppliancePartFlag;
	}

	public void setHasAppliancePartFlag(boolean hasAppliancePartFlag) {
		this.hasAppliancePartFlag = hasAppliancePartFlag;
	}

	public String getOrignlSalesOrdRefNum() {
		return orignlSalesOrdRefNum;
	}

	public void setOrignlSalesOrdRefNum(String orignlSalesOrdRefNum) {
		this.orignlSalesOrdRefNum = orignlSalesOrdRefNum;
	}

	public boolean isSaaSStrmlndApprvlFlag() {
		return saaSStrmlndApprvlFlag;
	}

	public void setSaaSStrmlndApprvlFlag(boolean strmlndApprvlFlag) {
		this.saaSStrmlndApprvlFlag = strmlndApprvlFlag;
	}

	public boolean isNoApprovalRequire() {
		return noApprovalRequire;
	}

	public void setNoApprovalRequire(boolean noApprovalRequire) {
		this.noApprovalRequire = noApprovalRequire;
	}

	public Date getPriorQuoteExpDate() {
		return priorQuoteExpDate;
	}

	public void setPriorQuoteExpDate(Date priorQuoteExpDate) {
		this.priorQuoteExpDate = priorQuoteExpDate;
	}
	
	public String getQuoteOutputOption() {
		return quoteOutputOption;
	}

	public void setQuoteOutputOption(String quoteOutputOption) {
		this.quoteOutputOption = quoteOutputOption;
	}

	public boolean isHasAppUpgradePart() {
		return hasAppUpgradePart;
	}

	public void setHasAppUpgradePart(boolean hasAppUpgradePart) {
		this.hasAppUpgradePart = hasAppUpgradePart;
	} 
	
	public String getStreamlinedWebQuoteNum() {
		return streamlinedWebQuoteNum;
	}
	public boolean isRebillCreditOrder() {
		return rebillCreditOrder;
	}

	public void setRebillCreditOrder(boolean rebillCreditOrder) {
		this.rebillCreditOrder = rebillCreditOrder;
	}
	
	public boolean isSSPQuote() {
        String sLob = this.lob == null ? "" : this.lob.getCode();
        return QuoteConstants.LOB_SSP.equalsIgnoreCase(sLob);
    }

	public String getEvalFullName() {
		return evalFullName;
	}

	public void setEvalFullName(String evalFullName) {
		this.evalFullName = evalFullName;
	}
	
	/**
	 * This value only will be set in DraftQuoteSubmitBaseAction and SubmitQuoteSubmissionAction
	 */
	public boolean isSubmitByEvaluator()
	{
		return this.isSubmitByEvaluator;
	}
    
    public void setSubmitByEvaluator(boolean isSubmitByEvaluator)
    {
    	this.isSubmitByEvaluator = isSubmitByEvaluator;
    }
    

	public String getBpSubmitterEmail() {
		return bpSubmitterEmail;
	}

	public void setBpSubmitterEmail(String bpSubmitterEmail) {
		this.bpSubmitterEmail = bpSubmitterEmail;
	}

	public String getBpSubmitterName() {
		return bpSubmitterName;
	}

	public void setBpSubmitterName(String bpSubmitterName) {
		this.bpSubmitterName = bpSubmitterName;
	}

	public Date getBpSubmitDate() {
		return bpSubmitDate;
	}

	public void setBpSubmitDate(Date bpSubmitDate) {
		this.bpSubmitDate = bpSubmitDate;
	}


    
    public Double getYtyGrwthPct(){
    	return this.ytyGrwthPct;
    }
    
    public Double getImpldGrwthPct(){
    	return this.impldGrwthPct;
    }

	public boolean isPGSNewPAEnrolled() {
		return isPGSNewPAEnrolled;
	}

	public void setPGSNewPAEnrolled(boolean isPGSNewPAEnrolled) {
		this.isPGSNewPAEnrolled = isPGSNewPAEnrolled;
	}

	public String getCaEndUserCustNum() {
		return caEndUserCustNum;
	}

	public void setCaEndUserCustNum(String caEndUserCustNum) {
		this.caEndUserCustNum = caEndUserCustNum;
	}
	
	public boolean isUnderEvaluation()
	{
		return this.isSubmittedForEval() || this.isAcceptedByEval() || this.isReturnForChgByEval() || this.isEditForRetChgByEval() || this.isDeleteByEditor();
	}

	public boolean isAddTrd() {
		return this.isAddTrd;
	}
	
	public void setAddTrd(boolean flag) {
		this.isAddTrd = flag;
	}	
	
	public boolean isRSVPSRPOnly() {
		return this.isRSVPSRPOnly;
	}
	
	public void setRSVPSRPOnly(boolean flag) {
		this.isRSVPSRPOnly = flag;
	}	
	
	/**
	 * Add a flag in QuoteHeader, if YTY growth% and implied YTY growth% is not
	 * null, it'll be a growth delegation quote.
	 */
	public boolean isGrowthDelegation(Quote quote) {
		String type = GrowthDelegationUtil.getQuoteGrwthDlgtnType(quote);
		
		if(GrowthDelegationUtil.isGrwthDltgnQuote(type)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get the flag for PA and PAE quote, if any a part has been referenced to renewal quote
	 * If exist, return true, else return false.
	 * @param quote
	 * @return 
	 */
	public boolean hasLineItemsFromRQ(Quote quote) {
		boolean result = false;
		if(!this.isPAEQuote() && !this.isPAQuote()) return result;
		List lineItems = quote.getLineItemList();
		for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();) {
			QuoteLineItem currItem = (QuoteLineItem) itemIt.next();
			if(currItem.isReferenceToRenewalQuote()){
				result = true;
				break;
			}
		 }
		return result;
	}
	
	public int getRQLineItemCount(){
		return rqLineItemCount;
	}

	public void setRQLineItemCount(int rqLineItemCount){
		this.rqLineItemCount = rqLineItemCount;
	}

	public boolean isContainRQLineItem() {
		return hasRQLineItem;
	}

	public void setContainRQLineItem(boolean hasRQLineItem) {
		this.hasRQLineItem = hasRQLineItem;
	}
	
	public boolean isECEligible() {
		return isECEligible;
	}

	public void setECEligible(boolean isECEligible) {
		this.isECEligible = isECEligible;
	}
	
	@Override
	public boolean isECRecalculateFlag() {
		return isECRecalculateFlag;
	}

	@Override
	public void setECRecalculateFlag(boolean isECRecalculateFlag) {
		this.isECRecalculateFlag=isECRecalculateFlag;
	}
	
	public EquityCurveTotal getEquityCurveTotal() {
        return equityCurveTotal;
    }

    public void setEquityCurveTotal(EquityCurveTotal equityCurveTotal) {
        this.equityCurveTotal = equityCurveTotal;
    }

	public boolean isDisShipInstAdrFlag() {
		return isDisShipInstAdrFlag;
	}

	public void setDisShipInstAdrFlag(boolean isDisShipInstAdrFlag) {
		this.isDisShipInstAdrFlag = isDisShipInstAdrFlag;
	}

	public boolean isOmittedLine() {
		return isOmittedLine;
	}

	public void setOmittedLine(boolean isOmittedLine) {
		this.isOmittedLine = isOmittedLine;
	}

	public int getOmittedLineRecalcFlag() {
		return omittedLineRecalcFlag;
	}

	public void setOmittedLineRecalcFlag(int omittedLineRecalcFlag) {
		this.omittedLineRecalcFlag = omittedLineRecalcFlag;
	}

	public int getInstallAtOpt() {
		return installAtOpt;
	}

	public void setInstallAtOpt(int installAtOpt) {
		this.installAtOpt = installAtOpt;
	}

	/**
	 * get the status of quote if it is ordered.
	 */
	public boolean isShipInstallEditable(){
		for(String overallStatu: nonEditableList){
			if(this.containsOverallStatus(overallStatu)){
				return false;
			}
		}
		for(String overallStatu: editableList){
			if(this.containsOverallStatus(overallStatu)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isStdloneSaasGenTermFlag() {
		return stdloneSaasGenTermFlag;
	}

	public void setStdloneSaasGenTermFlag(boolean stdloneSaasGenTermFlag) {
		this.stdloneSaasGenTermFlag = stdloneSaasGenTermFlag;
	}

	public int getSaasTermCondCatFlag() {
		return saasTermCondCatFlag;
	}
	
	public void setSaasTermCondCatFlag(int saasTermCondCatFlag) {
		this.saasTermCondCatFlag = saasTermCondCatFlag;
	}

	public boolean isT2CreatedFlag() {
		return isT2CreatedFlag;
	}

	public void setT2CreatedFlag(boolean isT2CreatedFlag) {
		this.isT2CreatedFlag = isT2CreatedFlag;
	}

	public double getNonCOGandFNETAvgDiscount() {
		return nonCOGandFNETAvgDiscount;
	}

	public void setNonCOGandFNETAvgDiscount(double nonCOGandFNETAvgDiscount) {
		this.nonCOGandFNETAvgDiscount = nonCOGandFNETAvgDiscount;
	}

	public double getCogAndFNETAvgDiscount() {
		return cogAndFNETAvgDiscount;
	}

	public void setCogAndFNETAvgDiscount(double cogAndFNETAvgDiscount) {
		this.cogAndFNETAvgDiscount = cogAndFNETAvgDiscount;
	}

	public boolean isMaxAvgDiscountExceeded() {
		return isMaxAvgDiscountExceeded;
	}

	public void setMaxAvgDiscountExceeded(boolean isMaxAvgDiscountExceeded) {
		this.isMaxAvgDiscountExceeded = isMaxAvgDiscountExceeded;
	}
	
	public void setMultipleAdditionalYearImpliedGrowth(List multipleImpliedGrowthList) {
		this.multipleImpliedGrowthList = multipleImpliedGrowthList;
	}

	public List getMultipleAdditionalYearImpliedGrowth() {
		return multipleImpliedGrowthList;
	}

	public String getOpprtntyOwnrName() {
		return opprtntyOwnrName;
	}

	public String getSapOrderNum() {
		return sapOrderNum;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setOpprtntyOwnrName(String opprtntyOwnrName) {
		this.opprtntyOwnrName = opprtntyOwnrName;
	}

	public void setSapOrderNum(String sapOrderNum) {
		this.sapOrderNum = sapOrderNum;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public void setHasDivestedPart(boolean hasDivestedPart) {
		this.hasDivestedPart = hasDivestedPart;
	}	
	
	public boolean isHasDivestedPart(){
		return hasDivestedPart;
	}

	public int getEndUserWebCustId() {
		return endUserWebCustId;
	}

	public void setEndUserWebCustId(int endUserWebCustId) {
		this.endUserWebCustId = endUserWebCustId;
	}

    /**
     * Sets the partHasPriceFlag.
     * @param partHasPriceFlag the partHasPriceFlag to set
     */
    public void setPartHasPriceFlag(int partHasPriceFlag) {
        this.partHasPriceFlag = partHasPriceFlag;
    }

	public int getDivstdObsltPartFlag() {
		return divstdObsltPartFlag;
	}

	public void setDivstdObsltPartFlag(int divstdObsltPartFlag) {
		this.divstdObsltPartFlag = divstdObsltPartFlag;
	}

	public void setPAOBlockFlag(int pAOBlockFlag){
		this.PAOBlockFlag = pAOBlockFlag; 
	}
	public String getAgrmtTypeCode() {
		return agrmtTypeCode;
	}

	public void setAgrmtTypeCode(String agrmtTypeCode) {
		this.agrmtTypeCode = agrmtTypeCode;
	}

    /**
     * Getter for serialNumWarningFlag. <br>
     * Do not display invalid MTM/Serial messages when edit a renewal quote<br>
     * Remove MTM/Serial # messages when quote is no longer in orderable status<br>
     * 
     * @return the serialNumWarningFlag
     */
    public boolean isSerialNumWarningFlag() {
        if (this.isRenewalQuote()) {
            return false;
        }
        if (!QuoteCommonUtil.isQuoteBeforeOrder(this)) {
            return false;
        }
        return this.serialNumWarningFlag;
    }

    /**
     * Sets the serialNumWarningFlag.
     * @param serialNumWarningFlag the serialNumWarningFlag to set
     */
    public void setSerialNumWarningFlag(boolean serialNumWarningFlag) {
        this.serialNumWarningFlag = serialNumWarningFlag;
    }

	public void setNeedReconfigFlag(boolean needReconfigFlag) {
		this.needReconfigFlag = needReconfigFlag;
	}

	public boolean isNeedReconfigFlag() {
		return needReconfigFlag;
	}

	public boolean isHasMonthlySoftPart() {
		return isHasMonthlySoftPart;
	}

	public void setHasMonthlySoftPart(boolean isHasMonthlySoftPart) {
		this.isHasMonthlySoftPart = isHasMonthlySoftPart;
	}

	public void setHasSoftSLineItem(boolean ishasSoftSLineItem) {
		this.ishasSoftSLineItem = ishasSoftSLineItem;
	}
	
     public String getSubRgnCode(){
    	 return subRgnCode;
     }
    
	public void setSubRgnCode(String subRgnCode){
		 this.subRgnCode=subRgnCode;
	}

	public void setSaasRenewalFlag(Boolean isSaasRenewalFlag) {
		this.isSaasRenewalFlag = isSaasRenewalFlag;
	}

	public Boolean isSaasRenewalFlag() {
		return isSaasRenewalFlag;
	}

	public void setSaasMigrationFlag(Boolean isSaasMigrationFlag) {
		this.isSaasMigrationFlag = isSaasMigrationFlag;
	}

	public Boolean isSaasMigrationFlag() {
		return isSaasMigrationFlag;
	}

	public void setMonthlyRenewalFlag(Boolean isMonthlyRenewalFlag) {
		this.isMonthlyRenewalFlag = isMonthlyRenewalFlag;
	}

	public Boolean isMonthlyRenewalFlag() {
		return isMonthlyRenewalFlag;
	}

	public void setMonthlyMigrationFlag(Boolean isMonthlyMigrationFlag) {
		this.isMonthlyMigrationFlag = isMonthlyMigrationFlag;
	}

	public Boolean isMonthlyMigrationFlag() {
		return isMonthlyMigrationFlag;
	}  
	
	//added by Bourne
	public void setExpDateExtendedFlag(Boolean isExpDateExtendedFlag){
		this.isExpDateExtendedFlag=isExpDateExtendedFlag;
	}	
	
	public Boolean isExpDateExtendedFlag(){
		return this.isExpDateExtendedFlag;
	}
	
    public void setGridFlag(boolean gridFlag){
    	this.gridFlag = gridFlag;
    }

    public boolean isGridFlag(){
    	return gridFlag;
    }

	public String getExpDateExtensionJustification() {
		return expDateExtensionJustification;
	}

	public void setExpDateExtensionJustification(String expDateExtensionJustification) {
		this.expDateExtensionJustification = expDateExtensionJustification;
	}

	public boolean isDsjFlag() {
		return isDsjFlag;
	}

	public void setDsjFlag(boolean isDsjFlag) {
		this.isDsjFlag = isDsjFlag;
	}
}
