package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.RenewalRevnStreamCode;
import com.ibm.dsw.quote.appcache.domain.RenewalRevnStreamCodeFactory;
import com.ibm.dsw.quote.appcache.domain.RenewalRevnStreamCode_Impl;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This
 * <code>QuoteLineItem_Impl<code> class is abstract implementation of QuoteLineItem.
 *
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: Mar 4, 2007
 *
 * $Log: QuoteLineItem_Impl.java,v $
 * Revision 1.94  2010/11/10 09:18:32  wxiaoli
 * ebiz : RBIE-8ADS82: Need a new revenue stream code for Competitive Trade-Ups, RTC 50680
 * fix an issue that got exception when submit quote with a part having no revenue stream code category
 *
 * Revision 1.93  2010/07/23 05:51:18  wxiaoli
 * SQO date logic : Enhance parts and pricing tab to derive 2 new line item attributes additionally, RTC 3334, reviewed by Will
 * fix the  issue of the relatedSeqNum is incorrect and roll back the relatedSapItemNum
 *
 * Revision 1.92  2010/07/15 12:04:37  wxiaoli
 * SQO date logic : Enhance parts and pricing tab to derive 2 new line item attributes additionally, RTC 3334, reviewed by Will
 * fix the  issue of the relatedSeqNum is incorrect
 *
 * Revision 1.91  2010/06/30 07:47:18  wxiaoli
 * SQR : Pushpa's area : Update web_quote sps : Need to put current quote user as mod_by, RTC 3407, reviewed by Will
 *
 * Revision 1.90  2010/06/25 08:58:30  changwei
 * Task 3334 SQO date logic : Enhance parts and pricing tab to derive 2 new line item attributes additionally
 * Reviewed by Vivian.
 *
 * Revision 1.89  2010/06/23 14:21:29  dsmith
 * Modify Special Bid Part Group determination based on country RTC Task 3143, Ebiz Help Request #MCOR-84YHK8
 *
 * Revision 1.88  2010/06/22 11:10:12  wxiaoli
 * BidIteration : DSQ02f: Display draft sales quote parts & pricing tab in bid iteration mode, RTC 3399, reviewed by Will
 *
 * Revision 1.87  2010/06/11 09:33:02  wxiaoli
 * FVT PL : JKEY-867HJV : Discount not being displayed on Increased Pricing Special Bids, RTC 3429, reviewed by Will
 *
 * Revision 1.86  2010/06/09 09:42:38  changwei
 * Use item.hasValidCmprssCvrageMonth()  to fix FVT PL : JKEY-867HJV
 * defect 3429
 *
 * Revision 1.85  2010/05/27 07:45:52  wxiaoli
 * contract level prcing flag for Display parts & pricing tab, RTC 3211,3212,3213,3214,3215,3216,3217, reviewed by Will
 *
 */
public abstract class QuoteLineItem_Impl implements QuoteLineItem, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -4491772748259558465L;

		protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    // key attributes to identify a line item
    public String sQuoteNum;

    public int iSeqNum;

    public int iDestSeqNum = -1;

    public int iSapSeqNum = -1;

    public Integer iPartQty;

    public int iManualSortSeqNum;

    public int iQuoteSectnSeqNum;

    // renewal quote related attributes
    public String sRenewalQuoteNum;

    public int iRenewalQuoteSeqNum;

    public Date dtRenewalQuoteEndDate;

    public Date dtMaintStartDate;

    public Date dtMaintEndDate;

    public Date dtPrevsStartDate;

    public Date dtPrevsEndDate;

    public String sRenwlChgCode;

    public ChargeableUnit[] chargeableUnits = {};

    //public int iCUQty;

    public String sPVUOverrideQtyIndCode;

    public int iVuConfigrtnNum;

    public List lineItemConfigs = new ArrayList(); // arraylist of QLIC objects

    // proration related attributes
    public int iLicPartQty;

    public boolean bProrationFlag;

    public boolean bHasAssocdLicPart;

    // use-inputted pricing discounts attributes
    public double dLineDiscPct;

    public Double dOverrideUnitPrc;

    // prices - attributes
    public Double dLocalUnitPrc;

    public Double dLocalUnitProratedPrc;

    public Double dLocalUnitProratedDiscPrc;

    public Double dLocalExtPrc;

    public Double dLocalExtProratedPrc;

    public Double dLocalExtProratedDiscPrc;

    public Double dLocalChnlTaxAmt;

    public Double dLclExtndChnlPriceIncldTax;

    // added Jun 19, 2007 - CMRe #48
    public int iAddtnlYearCvrageSeqNum;

    public int iAddtnlMaintCvrageQty;

    public List addtnlYearCvrageLineItems = new ArrayList();

    //when part is master subscription part, to store the related ramp-up parts
    //when part is master over rage part, to store the related sub over rage parts
    public List rampUpLineItems = new ArrayList();

    // added May 15, 2007
    public Double dChannelUnitPrice;

    public Double dChannelExtndPrice;

    public Double dLocalTaxAmt;

    public Double dLclExtndPriceIncldTax;


    public Date dtEOL;

    public double dContributionUnitPts;

    public double dContributionExtPts;

    public boolean bStartDtOvrrdFlg;

    public boolean bEndDtOvrrdFlg;

    private PartDisplayAttr partDispAttr;

    protected boolean bSeclBidReordFlag;

    public PartGroup partGroup;

    public SpBidPartGroup spBidPartGroup;

    public List partGroups = new ArrayList();

    public List spBidPartGroups = new ArrayList();

    // added Apr 12, 2007
    public String sComment;

    public String sChgType;

    // added for renewal quote
    public int iOpenQty;

    public int iOrderQty;

    public Date dtOrigStDate;

    public Date dtOrigEndDate;

    private int iProrateMonths;

    //public PartPriceLevel partPriceLevel = new PartPriceLevel(0, new double[12]);

    public double dOrigUnitPrice;

    public double dOrigExtPrice;

    public Double ovrrdExtPrice;

    public Boolean offerIncldFlag;

    public boolean bBackDatingFlag ;

    //added on 9/25/2008 for BP discount
    public Double dChnlOvrrdDiscPct;

    public Double dChnlStdDiscPct = new Double(0);

    public Double dTotDiscPct = new Double(0);

    //  added on 02/13/2009
    public String orderStatusCode ;

    public boolean bSalesQuoteRefFlag;

//    added for EOL
    public boolean bHasEolPrice;

    public int iManualProratedLclUnitPriceFlag;

    public String sPartStatus;

    //added for price end soon note
    public Date dtPartPrcEndDate;
    public int iProrateWeeks;

    //Compressed coverage
    public Integer iCmprssCvrageMonth;
    public Double dCmprssCvrageDiscPct;

    public boolean bEligibleForCmprssCvrage = false;

    //added for controlled-distribution-details message
    public String sControlledDistributionType;

    public boolean legacyBasePriceUsedFlag;

    public int iOriAddtnlMaintCvrageQty;

    //date logic

	public int iRelatedLineItmNum = -1;
    public int iRelatedSapItemNum; //final realted sequence number sent to SAP

    public String sModByUserID;

    // add SaaS part attributes
    public List lTierdScaleQty;
    public Double saasBidTCV;
    public Double saasBpTCV;
    public Double saasEntitledTCV;
    public Integer iCvrageTerm;
    public String billgFrqncyCode;
    public String billgFrqncyCodeDesc;
    public int SaaSMultipleVal;
    public boolean saasTcvAcv;
    public String migrtnCode;
    public boolean webMigrtdDocFlag;
    public String pricingTierModel;
    public Integer tierQtyMeasre;

    public String pricngIndCode;
    public boolean provisngHold;

    public String configrtnId;
    public Integer refDocLineNum;
    public Integer relatedCotermLineItmNum;
    public Integer relatedAlignLineItmNum;
    public boolean rampUp;
    public boolean replacedPart;
    public Integer cumCvrageTerm;
    public Integer provisngDays;
    public int rampUpPeriodNum;
    public boolean masterOvrage = true;//to judge if the line item is a a master over rage
    public boolean hasProvisngHold; //indicate that quote has at least one line item where the provisioning hold has not been lifted.

    private Map <String, String> sapDocUserStatMap = new HashMap<String, String>();
    private boolean triggerToUHold = false;
    private boolean quoteReplicated = false;

    private PriorYearSSPrice priorYearSSPrice;

    public boolean saasRenwl;

	public String renewalPricingMethod;
    public Double renewalRsvpPrice;


    //Appliance part
    public String applianceId;
    public String machineType;
    public String model;
    public String serialNumber;
    public String applncPocInd;
    public String applncPriorPoc = "N";
    public Date   custCmmttdArrivlDate;
    public String ccDscr;

    public Part part = new Part();

	//Appliance#99
    public Date custReqArrvDate;
    public boolean applncSendMFGFlg;
    
    //Appliance #145
    public String nonIBMModel;
    public String nonIBMSerialNumber;

    //FCT TO PA Finalization
    public String orignlSalesOrdRefNum;
    public String orignlConfigrtnId;
    public Date earlyRenewalCompDate;
    public Integer replacedTerm;
    public Integer renewalCounter;

    public String regulationCode;
    public boolean isOrdered;

    //saas renwl model
    public String renwlMdlCode;
    public String renwlMdlCodeLevel;
    public String saasRewlModelCode;
    public boolean fixedRewlModFalg;
    public boolean supportRenwlMdl;
    public String defultRenwlMdlCode;
     // true : CA; false: other
    public boolean isRewlModelCodeFromCa;


    // Saas 10.4 and 10.6
    public Date extensionEligibilityDate;
    public Integer calculateRemaningTerm; 

    // Equity Curve 13.2
    public EquityCurve equityCurve;
    
    private boolean isDisplayModelAndSerialNum;
    private boolean isMandatorySerialNum;
    
    public String touName;
    public String touURL;
    public Boolean amendedTouFlag;
    public Boolean amendedTouFlagB;
    public Boolean hdrAgrmentAmdFlag;
    
    //13.4 kenexa 
    public QuoteLineItem setUpRelatedSubsumedPart;
    
    //13.4 Proration Rounding on Bid Unit Price RSVP Price reset
    
    public boolean isSetLineToRsvpSrpFlag;
    
    //14.1 SDD
    public boolean divestedPart;
    
    public String partTypeDsc;
    
    public boolean partHasPrice;
    
    //14.2 GD
    public List addedToLicParts = new ArrayList(); 
    private Double splitFactor;
    private QuoteLineItem parentMasterPart;
    
   // 14.2 Appliance serial# validation and deployment
    private DeployModel deployModel;
    
    protected ModifiedProperty modifiedProperty;
    // 14.4 SCW add on /trade up
    public String addReasonCode;
    public String replacedReasonCode;
    public String newConfigFlag;
    public Integer originatingItemNum;
    
    // 15.1 SaaS Compensation Adjustment 
    public Boolean isNewService;

    public Integer remainingTermTillCAEndDate;

    public Boolean isNewService() {
		return isNewService;
	}
    //15.3 judge if this is a hybird part
    public String partSubType;
    
    public String getPartSubType() {
    	return partSubType;
    }

	public Integer getCalculateRemaningTerm() {
		return calculateRemaningTerm;
	}

	public void setCalculateRemaningTerm(Integer calculateRemaningTerm) {
		this.calculateRemaningTerm = calculateRemaningTerm;
	}

	public Double tierdPrice;

    public List<QuoteLineItemBillingOption> billingOptions;

    public Date getExtensionEligibilityDate() {
		return extensionEligibilityDate;
	}

	@Override
	public PriorYearSSPrice getPriorYearSSPrice() {
		return priorYearSSPrice;
	}

	@Override
	public void setPriorYearSSPrice(PriorYearSSPrice priorYearSSPrice) {
		this.priorYearSSPrice = priorYearSSPrice;
	}

	public void setQuoteReplicated(boolean quoteReplicated) {
		this.quoteReplicated = quoteReplicated;
	}

	public void setTriggerToUHold(boolean triggerToUHold) {
		this.triggerToUHold = triggerToUHold;
	}

	@Override
	public boolean isHasEolPrice() {
		return bHasEolPrice;
	}

	@Override
	public void setHasEolPrice(boolean hasEolPrice) {
		this.bHasEolPrice = hasEolPrice;
	}

	@Override
	public List getAddtnlYearCvrageLineItems() {
        return this.addtnlYearCvrageLineItems;
    }

    @Override
	public int getAddtnlYearCvrageSeqNum() {
        return this.iAddtnlYearCvrageSeqNum;
    }

    @Override
	public String getProdTrgtMktCode() {
        return part.getsProdTrgtMktCode();
    }
    /**
     *
     */

    @Override
	public Double getLclExtndChnlPriceIncldTax() {
        return this.dLclExtndChnlPriceIncldTax;
    }

    /**
     *
     */

    @Override
	public Double getLocalChnlTaxAmt() {
        return this.dLocalChnlTaxAmt;
    }

    @Override
	public String getPartDesc() {
        return part.getsPartDesc();
    }

    @Override
	public boolean isExprsPart() {
        return part.isbExprsPart();
    }

    @Override
	public boolean getAssocdLicPartFlag() {
        return bHasAssocdLicPart;
    }

    @Override
	public boolean isPvuPart() {
        return part.isbIsPvuPart();
    }

    @Override
	public boolean isPartEOLSet() {
        return part.isbPartEOLSet();
    }

    @Override
	public boolean isPartRestrct() {
        return part.isbPartRestrct();
    }

    @Override
	public boolean getProrateFlag() {
        return bProrationFlag;
    }

    @Override
	public String getPVUOverrideQtyIndCode() {
        return sPVUOverrideQtyIndCode;
    }

    @Override
	public boolean isRoyalty() {
        return part.isbRoyalty();
    }

    @Override
	public boolean isControlled() {
        return StringUtils.isNotBlank(this.part.getsIbmProgCode());
    }

    @Override
	public double getLineDiscPct() {
        return dLineDiscPct;
    }

    @Override
	public Double getLocalExtPrc() {
        return dLocalExtPrc;
    }

    @Override
	public Double getLocalExtProratedDiscPrc() {
        return dLocalExtProratedDiscPrc;
    }

    @Override
	public Double getLocalExtProratedPrc() {
        return dLocalExtProratedPrc;
    }

    @Override
	public Double getLocalUnitPrc() {
        return dLocalUnitPrc;
    }

    @Override
	public Double getLocalUnitProratedDiscPrc() {
        return dLocalUnitProratedDiscPrc;
    }

    @Override
	public Double getChannelExtndPrice() {
        return dChannelExtndPrice;
    }

    @Override
	public Double getChannelUnitPrice() {
        return dChannelUnitPrice;
    }

    @Override
	public Double getLocalUnitProratedPrc() {
        return dLocalUnitProratedPrc;
    }

    @Override
	public Double getOverrideUnitPrc() {
        return dOverrideUnitPrc;
    }

    @Override
	public Date getMaintEndDate() {
    	return dtMaintEndDate;
    }

    @Override
	public Date getMaintStartDate() {
    	return dtMaintStartDate;
    }

    @Override
	public Date getMaintEndDateForExport() {
    	if(this.isApplncPart() && !QuoteCommonUtil.isShowDatesForApplnc(this)){
    		return null;
    	}else{
    		return dtMaintEndDate;
    	}
    }

    @Override
	public Date getMaintStartDateForExport() {
    	if(this.isApplncPart() && !QuoteCommonUtil.isShowDatesForApplnc(this)){
    		return null;
    	}else{
    		return dtMaintStartDate;
    	}
    }

    @Override
	public Date getRenewalQuoteEndDate() {
        return dtRenewalQuoteEndDate;
    }

    @Override
	public int getAddtnlMaintCvrageQty() {
        return this.iAddtnlMaintCvrageQty;
    }

    /*public int getCUQty() {
        return iCUQty;
    }
*/
    @Override
	public int getLicPartQty() {
        return iLicPartQty;
    }

    @Override
	public int getManualSortSeqNum() {
        return iManualSortSeqNum;
    }

    @Override
	public Integer getPartQty() {
        return iPartQty;
    }

    @Override
	public int getRenewalQuoteSeqNum() {
        return iRenewalQuoteSeqNum;
    }

    @Override
	public int getSeqNum() {
        return iSeqNum;
    }

    @Override
	public int getQuoteSectnSeqNum() {
        return iQuoteSectnSeqNum;
    }

    @Override
	public int getDestSeqNum(){
        return iDestSeqNum;
    }

    @Override
	public int getSapLineItemSeqNum(){
        return this.iSapSeqNum;
    }
    @Override
	public List getLineItemConfigs() {
        return lineItemConfigs;
    }

    @Override
	public String getCtrctProgCode() {
        return part.getsCtrctProgCode();
    }

    @Override
	public String getCtrctProgCodeDesc() {
        return part.getsCtrctProgCodeDesc();
    }


    @Override
	public String getPartNum() {
        return part.getsPartNum();
    }

    @Override
	public String getPartTypeCode() {
        return part.getsPartTypeCode();
    }

    @Override
	public String getQuoteNum() {
        return sQuoteNum;
    }

    @Override
	public String getRenewalQuoteNum() {
        return sRenewalQuoteNum;
    }

    @Override
	public String getRevnStrmCode() {
        return part.getsRevnStrmCode();
    }

    @Override
	public String getRevnStrmCodeDesc() {
        return part.getsRevnStrmCodeDesc();
    }

    @Override
	public String getSwProdBrandCode() {
        return part.getsSwProdBrandCode();
    }

    @Override
	public String getSwProdBrandCodeDesc() {
        return part.getsSwProdBrandCodeDesc();
    }
    
    @Override
	public String getSwSubId() {
        return part.getSwSubId();
    }

    @Override
	public String getControlledCode() {
        return this.part.getControlledCode();
    }

    @Override
	public String getControlledCodeDesc() {
        return this.part.getControlledCodeDesc();
    }

    @Override
	public String getWwideProdCode() {
        return part.getsWwideProdCode() == null ? part.getsWwideProdCode() : part.getsWwideProdCode().trim();
    }

    @Override
	public String getWwideProdCodeDesc() {
        return part.getsWwideProdCodeDesc();
    }

    @Override
	public String getWwideProdSetCode() {
    	return part.getsWwideProdSetCode() == null ? part.getsWwideProdSetCode() : part.getsWwideProdSetCode().trim();
    }

    @Override
	public String getWwideProdGrpCode() {
        return part.getsWwideProdGrpCode();
    }

    @Override
	public double getContributionUnitPts() {
        return this.dContributionUnitPts;
    }

    @Override
	public double getContributionExtPts() {
        return this.dContributionExtPts;
    }

    @Override
	public String getProdPackTypeCode() {
        return part.getsProdPackTypeCode();
    }

    @Override
	public int getVuConfigrtnNum() {
        return this.iVuConfigrtnNum;
    }

    @Override
	public boolean getStartDtOvrrdFlg() {
        return this.bStartDtOvrrdFlg;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#getEndDtOvrrdFlg()
     */
    @Override
	public boolean getEndDtOvrrdFlg() {
        return this.bEndDtOvrrdFlg;
    }

    /**
     *
     */

    @Override
	public String getRenwlChgCode() {
        return this.sRenwlChgCode;
    }

    @Override
	public Double getLocalTaxAmt() {
        return this.dLocalTaxAmt;
    }

    @Override
	public Double getLclExtndPriceIncldTax() {
        return this.dLclExtndPriceIncldTax;
    }
    @Override
	public Date getPrevsStartDate(){
        return this.dtPrevsStartDate;

    }

    @Override
	public Date getPrevsEndDate(){
        return this.dtPrevsEndDate;

    }

    /**
     * @return Returns the partDispAttr.
     */
    @Override
	public PartDisplayAttr getPartDispAttr() {
        return partDispAttr;
    }

    /**
     * @param partDispAttr
     *            The partDispAttr to set.
     */
    @Override
	public void setPartDispAttr(PartDisplayAttr partDispAttr) {
        this.partDispAttr = partDispAttr;
    }

    /**
     *
     */

    @Override
	public Date getEOLDate() {
        return this.dtEOL;
    }

    /**
     *
     */

    @Override
	public boolean isObsoletePart() {
        return this.part.isbIsObsolutePart();
    }

    /**
     *
     */

    @Override
	public PartGroup getPartGroup() {
        return this.partGroup;
    }

    /**
     *
     */
    // New for DSW 10.1

    @Override
	public SpBidPartGroup getSpBidPartGroup() {
        return this.spBidPartGroup;
    }

    //
    @Override
	public String getChgType() {
        return sChgType;
    }

    /**
     *
     */

    @Override
	public String getComment() {
        return sComment;
    }



    /**
     *
     */

    @Override
	public int getOpenQty() {
        return this.iOpenQty;

    }

    @Override
	public int getOrderQty() {
        return this.iOrderQty;

    }
    /**
     *
     */

    @Override
	public Date getOrigEndDate() {
        return dtOrigEndDate;
    }
    @Override
	public double getOrigUnitPrice(){
        return this.dOrigUnitPrice;
    }

    @Override
	public double getOrigExtPrice(){
        return this.dOrigExtPrice;
    }
    /**
     *
     */

    @Override
	public Date getOrigStDate() {
        return dtOrigStDate;
    }

    @Override
	public void setProrateMonths(int months){
        this.iProrateMonths = months;
    }
    @Override
	public int getProrateMonths(){
        return this.iProrateMonths;
    }

    public void setControlledCodeDesc (String codeDesc) {
        this.part.setControlledCodeDesc(codeDesc);
    }

    @Override
	public boolean getSalesQuoteRefFlag() {
        return bSalesQuoteRefFlag;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("---------------Key identification Attribute--------\n");
        buffer.append("sQuoteNum=" + sQuoteNum + "\n");
        buffer.append("sPartNum=" + part.getsPartNum() + "\n");
        buffer.append("iSeqNum=" + iSeqNum + "\n");
        buffer.append("iPartQty=" + iPartQty + "\n");
        buffer.append("iManualSortSeqNum=" + iManualSortSeqNum + "\n");
        buffer.append("iQuoteSectnSeqNum=" + iQuoteSectnSeqNum + "\n");
        buffer.append("---------------Renewal related Attribute--------\n");
        buffer.append("sRenewalQuoteNum=" + sRenewalQuoteNum + "\n");
        buffer.append("iRenewalQuoteSeqNum=" + iRenewalQuoteSeqNum + "\n");
        buffer.append("dtRenewalQuoteEndDate=" + dtRenewalQuoteEndDate + "\n");
        buffer.append("dtMaintStartDate=" + dtMaintStartDate + "\n");
        buffer.append("dtMaintEndDate=" + dtMaintEndDate + "\n");
        buffer.append("sRenwlChgCode=" + sRenwlChgCode + "\n");
        buffer.append("---------------PVU related Attribute--------\n");
        buffer.append("bIsPvuPart=" + part.isbIsPvuPart() + "\n");
        //buffer.append("sCUId=" + sCUId + "\n");
        //buffer.append("sCUCode=" + sCUCode + "\n");
        //buffer.append("sCUCodeDscr=" + sCUCodeDscr + "\n");
        //buffer.append("iCUQty=" + iCUQty + "\n");
        buffer.append("iVuConfigrtnNum=" + iVuConfigrtnNum + "\n");
        buffer.append("sPVUOverrideQtyIndCode=" + sPVUOverrideQtyIndCode + "\n");
        buffer.append("lineItemConfigs=" + lineItemConfigs + "\n");
        buffer.append("---------------Proration and discount Attribute--------\n");
        buffer.append("iLicPartQty=" + iLicPartQty + "\n");
        buffer.append("bProrationFlag=" + bProrationFlag + "\n");
        buffer.append("iAddtnlYearCvrageSeqNum=" + iAddtnlYearCvrageSeqNum + "\n");
        buffer.append("bHasAssocdLicPart=" + bHasAssocdLicPart + "\n");
        buffer.append("dLineDiscPct=" + dLineDiscPct + "\n");
        buffer.append("dOverrideUnitPrc=" + dOverrideUnitPrc + "\n");
        buffer.append("---------------Pricing Attribute--------\n");
        buffer.append("dLocalUnitPrc=" + dLocalUnitPrc + "\n");
        buffer.append("dLocalUnitProratedPrc=" + dLocalUnitProratedPrc + "\n");
        buffer.append("dLocalUnitProratedDiscPrc=" + dLocalUnitProratedDiscPrc + "\n");
        buffer.append("dLocalExtPrc=" + dLocalExtPrc + "\n");
        buffer.append("dLocalExtProratedPrc=" + dLocalExtProratedPrc + "\n");
        buffer.append("dLocalExtProratedDiscPrc=" + dLocalExtProratedDiscPrc + "\n");
        buffer.append("---------------Part Attribute--------\n");
        buffer.append("sSwProdBrandCode=" + part.getsSwProdBrandCode() + "\n");
        buffer.append("sSwProdBrandCodeDesc=" + part.getsSwProdBrandCodeDesc() + "\n");
        buffer.append("sPartTypeCode=" + part.getsPartTypeCode() + "\n");
        buffer.append("sRevnStrmCode=" + part.getsRevnStrmCode() + "\n");
        buffer.append("sRevnStrmCodeDesc=" + part.getsRevnStrmCodeDesc() + "\n");
        buffer.append("sCtrctProgCode=" + part.getsCtrctProgCode() + "\n");
        buffer.append("sCtrctProgCodeDesc=" + part.getsCtrctProgCodeDesc() + "\n");
        buffer.append("sProdPackTypeCode=" + part.getsProdPackTypeCode() + "\n");
        buffer.append("sWwideProdCode=" + part.getsWwideProdCode() + "\n");
        buffer.append("sWwideProdCodeDesc=" + part.getsWwideProdCodeDesc() + "\n");
        buffer.append("swSubId=" + part.getSwSubId() + "\n");
        buffer.append("---------------Misc Attribute--------\n");
        buffer.append("dtEOL=" + dtEOL + "\n");
        buffer.append("bPartEOLSet=" + part.isbPartEOLSet() + "\n");
        buffer.append("bRoyalty=" + part.isbRoyalty() + "\n");
        buffer.append("bIsObsolutePart=" + part.isbIsObsolutePart() + "\n");
        buffer.append("bPartRestrct=" + part.isbPartRestrct() + "\n");
        buffer.append("bExprsPart=" + part.isbExprsPart() + "\n");
        buffer.append("dContributionUnitPts=" + dContributionUnitPts + "\n");
        buffer.append("dContributionExtUnitPts=" + dContributionExtPts + "\n");
        buffer.append("bStartDtOvrrdFlg=" + bStartDtOvrrdFlg + "\n");
        buffer.append("bEndDtOvrrdFlg=" + bEndDtOvrrdFlg + "\n");
        buffer.append("sChgType=" + sChgType + "\n");
        buffer.append("sComment=" + sComment + "\n");
        buffer.append("partGroup=" + partGroup + "\n");
        buffer.append("spBidPartGroup=" + spBidPartGroup + "\n");
        buffer.append("iOpenQty=" + iOpenQty + "\n");
        buffer.append("iOrderedQty=" + iOrderQty + "\n");
        buffer.append("dtOrigEndDate=" + dtOrigEndDate + "\n");
        buffer.append("dtOrigStDate=" + dtOrigStDate + "\n");
        buffer.append("bHasEolPrice=" + bHasEolPrice + "\n");
        buffer.append("iManualProratedLclUnitPriceFlag=" + iManualProratedLclUnitPriceFlag  + "\n");
        buffer.append("sPartStatus=" + sPartStatus + "\n");
        if (null != this.partDispAttr) {
            buffer.append("---------------Part Display Attribute--------\n");
            buffer.append(this.partDispAttr.toString()).append("\n");
        }
        buffer.append("ovrrdExtPrice = " + ovrrdExtPrice + "\n");
        buffer.append("offerIncldFlag = " + offerIncldFlag + "\n");
        buffer.append("sIbmProgCode = " + this.part.getsIbmProgCode() + "\n");
        buffer.append("controlledCode = " + this.part.getControlledCode() + "\n");
        buffer.append("controlledCodeDesc = " + this.part.getControlledCodeDesc() + "\n");
        buffer.append("sIbmProgCodeDscr"+this.part.getsIbmProgCodeDscr()+"\n");
        buffer.append("dtPartPrcEndDate"+this.dtPartPrcEndDate+"\n");
        buffer.append("nonIBMModel" + this.nonIBMModel+"\n");
        buffer.append("nonIBMSerialNumber" + this.nonIBMSerialNumber + "\n");
        return buffer.toString();
    }


    @Override
	public Double getOvrrdExtPrice() {
        return ovrrdExtPrice;
    }

    @Override
	public Boolean isOfferIncldFlag() {
        return offerIncldFlag;
    }
    @Override
	public void clearPrices() throws TopazException{
    	//reserve entitled price for eol part
    	if(!isObsoletePart()){
    		this.dLocalUnitPrc = null;
    		this.dLocalUnitProratedPrc = null;
            this.dLocalExtProratedPrc = null;
    	}
        this.dLocalUnitProratedDiscPrc = null;

        this.dLocalExtPrc = null;
        this.dLocalExtProratedDiscPrc = null;

        this.dChannelUnitPrice=null;
        this.dChannelExtndPrice = null;

        this.dLocalChnlTaxAmt = null;
        this.dLclExtndChnlPriceIncldTax=null;

        this.dLocalTaxAmt = null;
        this.dLclExtndPriceIncldTax = null;

        this.dChnlStdDiscPct = new Double(0);
    }
    /**
     * @return Returns the partGroups.
     */
    @Override
	public List getPartGroups() {
        return partGroups;
    }

    /**
     * @return Returns the Special Bid partGroups.
     */
    @Override
	public List getSpBidPartGroups() {
        return spBidPartGroups;
    }

    /* (non-Javadoc)
	 * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#isPartGroupRequireSpBid()
	 */


	@Override
	public boolean isPartGroupRequireSpBid() {
	List spBidPartGroups = getSpBidPartGroups();
	if(spBidPartGroups == null) {
		return false;
	} else {
		Iterator it = spBidPartGroups.iterator();
		while(it.hasNext()) {
//			SpBidPartGroup spbidpg = (SpBidPartGroup)it.next();
			return true;
		}
	}
	return false;
}

    @Override
	public void  setChargeableUnits(ChargeableUnit[] chargeableUnits){
        this.chargeableUnits = chargeableUnits;
    }

    @Override
	public ChargeableUnit[]  getChargeableUnits(){
        return chargeableUnits;
    }

    @Override
	public boolean getBackDatingFlag(){
    	return bBackDatingFlag;
    }

    @Override
	public Double getChnlOvrrdDiscPct(){
    	return dChnlOvrrdDiscPct;
    }

    @Override
	public Double getChnlStdDiscPct(){
    	return dChnlStdDiscPct;
    }

    @Override
	public Double getTotDiscPct(){
    	return dTotDiscPct;
    }

    @Override
	public boolean isItemBackDated(){
    	return (DateUtil.isDateBeforeToday(dtMaintStartDate)
                && !QuoteCommonUtil.isSkipLineItemDateValidate(this));
    }

    /**
     * @return Returns the orderStatusCode.
     */
    @Override
	public String getOrderStatusCode() {
        return orderStatusCode;
    }
    /**
     * @param orderStatusCode The orderStatusCode to set.
     */
    public void setOrderStatusCode(String orderStatusCode) {
        this.orderStatusCode = orderStatusCode;
    }

    public int getManualProratedLclUnitPriceFlag(){
    	return iManualProratedLclUnitPriceFlag;
    }

	public boolean canPartBeReactivated(){
	    return PartPriceConfigFactory.singleton().canPartBeReactivated(sPartStatus);
	}

	public String getIbmProgCode() {
		return part.getsIbmProgCode();
	}

	public void setIbmProgCode(String sIbmProgCode) {
		this.part.setsIbmProgCode(sIbmProgCode);
	}

	public String getIbmProgCodeDscr() {
		return part.getsIbmProgCodeDscr();
	}

	public void setIbmProgCodeDscr(String sIbmProgCodeDscr) {
		this.part.setsIbmProgCodeDscr(sIbmProgCodeDscr);
	}

	public boolean isRenewalPart() {
	    boolean isRenewal = false;

	    try {
            CacheProcess cp = CacheProcessFactory.singleton().create();
            RenewalRevnStreamCode revnCode = new RenewalRevnStreamCode_Impl(this.getRevnStrmCode());

            isRenewal = cp.getAllRenewalRevnStreamCodeList().contains(revnCode);

        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
        }

	    return isRenewal;
	}

	public boolean isPartPrcEndSoon(){
	    if(this.dtPartPrcEndDate == null){
	        return false;
	    }

	    Date after30Days = DateUtil.addDays(DateUtil.getCurrentYYYYMMDDDate(), 30);
	    if(dtPartPrcEndDate.after(new Date(System.currentTimeMillis()))
	            && dtPartPrcEndDate.before(after30Days)){
		    return true;
	    }

	    return false;
	}
	public int getProrateWeeks(){
	    return iProrateWeeks;
	}

	public void setProrateWeeks(int weeks){
	    this.iProrateWeeks = weeks;
	}

	public boolean isMaintCoverageProrated() {
	    if (getPartDispAttr().isFtlPart()) {
	        return (getProrateWeeks() > 0 && getProrateWeeks() < 52);
	    }
	    else if (getMaintStartDate() != null && getMaintEndDate() != null) {
	        int months = DateUtil.calculateFullCalendarMonths(getMaintStartDate(), getMaintEndDate());
	        if (months < 12) {
				return true;
			}
	    }
	    return false;
	}
	public int compareTo(QuoteLineItem lineItem){
	    if (this.getMaintStartDate() == null || lineItem.getMaintStartDate()== null) {
			return -1 ;
		} else {
			return this.getMaintStartDate().compareTo(lineItem.getMaintStartDate());
		}
	}

	  public int compareTo(Object o) {
	        if (o == null) {
				return 0;
			} else {
				return compareTo((QuoteLineItem) o);
			}
	    }

	public boolean isContractPart(){
        if (getPartTypeCode() == null) {
            return false;
        }

        return PartPriceConstants.PartTypeCode.PACTRCT.equals(getPartTypeCode().trim());
	}

	public Integer getCmprssCvrageMonth(){
	    return iCmprssCvrageMonth;
	}

	public Double getCmprssCvrageDiscPct(){
	    return dCmprssCvrageDiscPct;
	}

	public boolean hasValidCmprssCvrageMonth(){
	    return (getCmprssCvrageMonth() != null && getCmprssCvrageMonth().intValue() > 0);
	}

	public boolean hasValidCmprssCvrageDiscPct(){
	    return (getCmprssCvrageDiscPct() != null && getCmprssCvrageDiscPct().doubleValue() > 0);
	}

	public boolean isEligibleForCmprssCvrage(){
	    return bEligibleForCmprssCvrage;
	}

	public void determineEligibleForCmprssCvrage(){
	    this.bEligibleForCmprssCvrage = QuoteCommonUtil.isEligibleForCmprssCvrage(this);
	}


    /**
     * @return Returns the sControlledDistributionType.
     */
    public String getSControlledDistributionType() {
        return sControlledDistributionType;
    }
    /**
     * @param controlledDistributionType The sControlledDistributionType to set.
     */
    public void setSControlledDistributionType(String controlledDistributionType) {
        sControlledDistributionType = controlledDistributionType;
    }

    /**
	 * @deprecated Use {@link com.ibm.dsw.quote.common.domain.Part#getRevnStrmCategoryCode(com.ibm.dsw.quote.common.domain.QuoteLineItem_Impl)} instead
	 */
	public String getRevnStrmCategoryCode(){
		return part.getRevnStrmCategoryCode(this);
	}

    public boolean isLegacyBasePriceUsedFlag(){
        return legacyBasePriceUsedFlag;
    }

    public int getIOriAddtnlMaintCvrageQty() {
        return iOriAddtnlMaintCvrageQty;
    }
    /**
     * @return Returns the iRelatedLineItmNum.
     */
    public int getIRelatedLineItmNum() {
        return iRelatedLineItmNum;
    }

    /**
     * @return Returns the sPartType.
     */
    public String getSPartType() {
        return part.getsPartType();
    }



    public String getSModByUserID() {
        return sModByUserID;
    }

	public List getLTierdScaleQty() {
		return lTierdScaleQty;
	}

	public void setLTierdScaleQty(List tierdScaleQty){
		lTierdScaleQty = tierdScaleQty;
	}

	public Double getSaasBidTCV() {
		return saasBidTCV;
	}

	public Double getSaasEntitledTCV() {
		Double entitledTcv = null;
		if(isSaasPart()){
			if(isSaasSetUpPart() || isSaasProdHumanServicesPart() || isSaasSubsumedSubscrptnPart()){
				entitledTcv = getLocalExtProratedPrc();
				}
			if(isSaasSubscrptnPart()){
				entitledTcv = getLocalExtProratedPrc() == null ? null : new Double(getLocalExtProratedPrc().doubleValue() * getBillingPeriods());
			}
		}else if(isMonthlySoftwarePart()){
			if(((MonthlySwLineItem)this).isMonthlySwSubscrptnPart()){
				entitledTcv = getLocalExtProratedPrc() == null ? null : new Double(getLocalExtProratedPrc().doubleValue() * getBillingPeriods());
			}
		}
		return entitledTcv;
	}

	public void setSaasEntitledTCV(Double saasEntitledTCV) {
		this.saasEntitledTCV = saasEntitledTCV;
	}

	/* means the term of the part
	 */
	public Integer getICvrageTerm() {
		return iCvrageTerm;
	}

	public String getBillgFrqncyCode() {
		return billgFrqncyCode;
	}


	public int getSaaSMultipleVal(){
		return SaaSMultipleVal;
	}
	public void setSaaSMultipleVal(int SaaSMultipleVal){
		this.SaaSMultipleVal = SaaSMultipleVal;
	}

    public boolean isSaasPart() {
		return part.isSaasPart();
	}

	public boolean isSaasSetUpPart() {
		return part.isSaasSetUpPart();
	}

	public boolean isSaasSubscrptnPart() {
		return part.isSaasSubscrptnPart() && !isSaasSubsumedSubscrptnPart();
	}

    public boolean isSaasSubsumedSubscrptnPart() {
		return part.isSaasSubsumedSubscrptnPart();
	}


	public boolean isSaasSLApart(){
		if(this.getRevnStrmCode()!=null && PartPriceConstants.REVENUE_STREAM_CODE_SLA.equals(this.getRevnStrmCode())){
			return true;
		}else{
			return false;
		}
	}

	public boolean isSaasSubscrptnOvragePart() {
		return part.isSaasSubscrptnOvragePart();
	}

	public boolean isSaasSetUpOvragePart() {
		return part.isSaasSetUpOvragePart();
	}

	public boolean isSaasDaily() {
		return part.isSaasDaily();
	}

	public boolean isSaasOnDemand() {
		return part.isSaasOnDemand();
	}

	public String getPricingTierModel() {
		return pricingTierModel;
	}

	public Integer getTierQtyMeasre() {
		return tierQtyMeasre;
	}

    public String getPricngIndCode() {
		return pricngIndCode;
	}

	public boolean isProvisngHold() {
		return provisngHold;
	}

	public boolean isSaasProdHumanServicesPart() {
		return part.isSaasProdHumanServicesPart();
	}

	public boolean isSaasTcvAcv() {
		return saasTcvAcv;
	}


    // User selected billing frequency description
    public String getBillgFrqncyDscr(){
        return billgFrqncyCodeDesc;
    }

    public String getMigrtnCode(){
    	return this.migrtnCode;
    }

	public String getSapDocUserStat(){
 	StringBuffer sb = new StringBuffer();

        for(String status : sapDocUserStatMap.values()){
           	sb.append(status);
           	sb.append(" ,");
        }

        int len = sb.length();
        if(len > 0){
            sb.deleteCharAt(len - 1);
        }

        return sb.toString();
	}

	public void addSapDocUserStat(String sapDocUserStat, String sapDocUserStatDesc){
		sapDocUserStatMap.put(sapDocUserStat,sapDocUserStatDesc);
	}

	public boolean isToUHold(){
		boolean touHold = sapDocUserStatMap.keySet().contains(PartPriceConstants.ToUStatus.HOLD);
		if(touHold){
			//ToU hold status is replicated back, use replication status
			return true;
		} else {
			//This could happen, when the quote is order immediately
			if(quoteReplicated == false){
				if(triggerToUHold){
					return true;
				} else {
					//Part is not configured to trigger ToU hold
					return false;
				}
			} else {
				return false;
			}
		}
	}
	public boolean isToUDeclined(){
		return sapDocUserStatMap.keySet().contains(PartPriceConstants.ToUStatus.DECLINED);
	}

	public String getConfigrtnId() {
		return configrtnId;
	}

	public Integer getProvisngDays() {
		return provisngDays;
	}

	public boolean isReplacedPart() {
		return replacedPart;
	}

	public Integer getRefDocLineNum() {
		return refDocLineNum;
	}

	public Integer getRelatedCotermLineItmNum() {
		return relatedCotermLineItmNum;
	}

	public Integer getRelatedAlignLineItmNum() {
		return relatedAlignLineItmNum;
	}

	/* to judge if this part has rampup parts
	 */
	public boolean isBeRampuped() {
		return (isSaasSubscrptnPart() && getRampUpLineItems() != null && getRampUpLineItems().size() > 0);
	}



	public Integer getCumCvrageTerm() {
		return cumCvrageTerm;
	}

	/* to judge if this part is a rampup part
	 */
	public boolean isRampupPart() {
		return rampUp;
	}

	/* when part is master subscription part, to store the related ramp-up parts
     * when part is master over rage part, to store the related sub over rage parts
	 */
	public List getRampUpLineItems() {
		return rampUpLineItems;
	}

	public void setRampUpLineItems(List rampUpLineItems) {
		this.rampUpLineItems = rampUpLineItems;
	}

	public double getBillingPeriods() {
		return QuoteCommonUtil.calculateBillingPeriods(this.getICvrageTerm() == null ? 0 : this.getICvrageTerm().intValue(), this);
	}

	/* means the sequence number of the rampup
	 */
	public int getRampUpPeriodNum() {
		return rampUpPeriodNum;
	}

	public void setRampUpPeriodNum(int rampUpPeriodNum) {
		this.rampUpPeriodNum = rampUpPeriodNum;
	}

	public boolean isMasterOvrage() {
		return masterOvrage;
	}

	public void setMasterOvrage(boolean masterOvrage) {
		this.masterOvrage = masterOvrage;
	}

	public Double getSaasBpTCV() {
		return saasBpTCV;
	}

	public boolean isSaasRenwl() {
		return saasRenwl;
	}

	public String getRenewalPricingMethod() {
		return renewalPricingMethod;
	}

	public void setRenewalPricingMethod(String renewalPricingMethod) {
		this.renewalPricingMethod = renewalPricingMethod;
	}

	public Double getRenewalRsvpPrice() {
		return renewalRsvpPrice;
	}

	public void setRenewalRsvpPrice(Double renewalRsvpPrice) {
		this.renewalRsvpPrice = renewalRsvpPrice;
	}

	public String getApplianceId() {
		return getConfigrtnId();//configId = applianceId
	}

	public String getMachineType() {
		return machineType == null ? "" : machineType;
	}


	public String getModel() {
		return model == null ? "" : model;
	}

	public String getSerialNumber() {
		return serialNumber == null ? "" : serialNumber;
	}

	public String getApplncPocInd() {
		return applncPocInd;
	}

	public String getApplncPriorPoc() {
		return applncPriorPoc;
	}

	public boolean isApplncPocInd(){
		return "Y".equalsIgnoreCase(applncPocInd);
	}
	public boolean isApplncPriorPoc(){
		return "Y".equals(applncPriorPoc);
	}
	public boolean isApplncPriorPSC1(){
		return !isApplncPriorPoc();
	}

	public boolean isApplncPart() {
		return part.isApplncPart();
	}
	public boolean isApplncMain() {
		return part.isApplncMain();
	}
	public boolean isApplncReinstatement() {
		return part.isApplncReinstatement();
	}

	public boolean isApplncUpgrade() {
		return part.isApplncUpgrade();
	}

	public boolean isApplncTransceiver() {
		return part.isApplncTransceiver();
	}

	public boolean isApplncRenewal() {
		return part.isApplncRenewal();
	}

	public boolean isApplianceRelatedSoftware() {
		return part.isApplncAdditional();
	}

	public boolean isWebMigrtdDoc() {
		return webMigrtdDocFlag;
	}
	public String getOrignlSalesOrdRefNum() {
		return orignlSalesOrdRefNum == null ? "" : orignlSalesOrdRefNum;
	}
	public String getOrignlConfigrtnId() {
		return orignlConfigrtnId == null ? "" : orignlConfigrtnId;
	}
	public Date getEarlyRenewalCompDate() {
		return earlyRenewalCompDate;
	}

	public Date getCustCmmttdArrivlDate() {
		return custCmmttdArrivlDate;
	}

	public String getCcDscr() {
        return ccDscr;
    }

	public boolean isApplncServicePack() {
		return part.isApplncServicePack();
	}

	public Integer getReplacedTerm() {
		return replacedTerm;
	}

	public Integer setRenewalCounter(Integer renewalCounter) {
		return this.renewalCounter = renewalCounter;
	}

	public Integer getRenewalCounter() {
		return renewalCounter;
	}

	public boolean isApplncServicePackRenewal() {
		return part.isApplncServicePackRenewal();
	}
	
	/**
	 *
	 * check if the quote line item come from a renewal quote.
	 */

	public boolean isReferenceToRenewalQuote() {
		return StringUtils.isNotBlank(this.getRenewalQuoteNum());
	}

	/**
	 * If the part has valid appliance id (e.g. 'D0NGALL-45262001') return true
	 * else (e.g. '' or 'NOT_ON_QUOTE' or 'NOT_ASSOCIATED') return false
	 */
	public boolean isHasApplncId(){
		if(StringUtils.isNotBlank(getApplianceId())
			&& !PartPriceConstants.APPLNC_NOT_ON_QUOTE.equalsIgnoreCase(getApplianceId())
			&& !PartPriceConstants.APPLNC_NOT_ASSOCIATED.equalsIgnoreCase(getApplianceId())){
			return true;
		}
		return false;
	}

	public boolean isApplncMainGroup(){
		return part.isApplncMainGroup();
	}

	public void setApplncMainGroup(boolean applncMainGroup){
		this.part.setApplncMainGroup(applncMainGroup);
	}

	public boolean isApplncQtyRestrctn() {
		return part.isApplncQtyRestrctn();
	}

	public String getPid(){
		if(StringUtils.isBlank(this.getConfigrtnId())){
			return StringUtils.EMPTY;
		}
		return this.getConfigrtnId().substring(0, 6);
	}

	public boolean isHasProvisngHold() {
		return hasProvisngHold;
	}

	public String getRegulationCode() {
		return regulationCode;
	}

	public void setRegulationCode(String regulationCode) {
		this.regulationCode = regulationCode;
	}

	@Override
	public boolean isExportRestricted() {
		boolean flag = false;
		if(this.regulationCode !=null && this.regulationCode.trim().equals("1")){
			return true;
		}
		return flag;
	}

	public String getProductId() {
		return part.getProductId();
	}

	public void setProductId(String productId) {
		this.part.setProductId(productId);
	}

	public boolean isOrdered(){
		return isOrdered;
	}

	public String getRenwlMdlCode(){
		if (!isRewlModelCodeFromCa && "ARNC".equalsIgnoreCase(saasRewlModelCode)){
			if ((iCvrageTerm == null ? 0 : iCvrageTerm.intValue()) >= 12){
				renwlMdlCode = "R";
			} else {
				renwlMdlCode = "O";
			}
		}
		return renwlMdlCode;
	}

	public String getRenwlMdlCodeLevel(){
		return renwlMdlCodeLevel;
	}

	public String getSaasRewlModeCode(){
		return saasRewlModelCode;
	}

	public boolean isFixedRenwlMdl(){
		return fixedRewlModFalg;
	}

	public boolean isSupportRenwlMdl(){
		return supportRenwlMdl;
	}

	public String getDefultRenwlMdlCode() {
		return defultRenwlMdlCode;
	}



	/**
     * YTY Growth Delegation
     */
	public YTYGrowth ytyGrowth;




	public YTYGrowth getYtyGrowth() {
		return ytyGrowth;
	}



	public boolean isOverridePriceValue(){
		return ytyGrowth != null && ytyGrowth.getYtyGrwothRadio() != null && "1".equals(ytyGrowth.getYtyGrwothRadio()) ? true : false;
	}

	public boolean isDiscountValue(){
		return ytyGrowth != null && ytyGrowth.getYtyGrwothRadio() != null && "2".equals(ytyGrowth.getYtyGrwothRadio()) ? true : false;
	}

	public boolean isYtyGrowthValue(){
		return ytyGrowth != null && ytyGrowth.getYtyGrwothRadio() != null && "3".equals(ytyGrowth.getYtyGrwothRadio()) ? true : false;
	}

	public Double getTierdPrice() {
		return tierdPrice;
	}

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
	}

	public List<QuoteLineItemBillingOption> getBillingOptions() {
        return billingOptions;
    }

	public void setBillingOptions(List<QuoteLineItemBillingOption> billingOptions) {
        this.billingOptions = billingOptions;
    }

	 // Equity Curve 13.2
	public EquityCurve getEquityCurve(){
		return equityCurve;
	}
	
	
	public Double calculateOverallDiscount() {

		if (hasPositiveEntitledExtPrice() && getChannelExtndPrice() != null) {
			return new Double((1.0D - getChannelExtndPrice().doubleValue() / getLocalExtProratedPrc().doubleValue()) * 100D);
		} else {
			return null;
		}

	}

	private boolean hasPositiveEntitledExtPrice() {
		return getLocalExtProratedPrc() != null && getLocalExtProratedPrc().doubleValue() != 0.0D;
	}
    
	public boolean isDisplayModelAndSerialNum(){
		return this.isDisplayModelAndSerialNum;
	}
	
	public void setDisplayModelAndSerialNum(boolean isDisplayModelAndSerialNum){
		this.isDisplayModelAndSerialNum=isDisplayModelAndSerialNum;
	}
	
	public boolean isMandatorySerialNum(){
		return this.isMandatorySerialNum;
	}
	
	public void setMandatorySerialNum(boolean isMandatorySerialNum){
		this.isMandatorySerialNum=isMandatorySerialNum;
	}

	public Double getMinBidUnitPrice() {
		return null != getEquityCurve()? getEquityCurve().getMinBidUnitPrice(getLocalUnitProratedPrc()):null;
	}

	public Double getMinBidExtendedPrice() {
		return null != getEquityCurve()? getEquityCurve().getMinBidExtendedPrice(getPartQty(),getLocalUnitProratedPrc()):null;
	}

	public Double getMaxBidUnitPrice() {
		return null != getEquityCurve()? getEquityCurve().getMaxBidUnitPrice(getLocalUnitProratedPrc()):null;
	}

	public Double getMaxBidExendedPrice() {
		return null != getEquityCurve()? getEquityCurve().getMaxBidExendedPrice(getPartQty(),getLocalUnitProratedPrc()):null;
	}    
	//Appliance#99
	public Date getLineItemCRAD(){
		return this.custReqArrvDate;
	}
	
	public boolean getApplncSendMFGFLG(){
		return this.applncSendMFGFlg;
	}

	public String getTouName() {
		return touName;
	}

	public void setTouName(String touName) {
		this.touName = touName;
	}

	public String getTouURL() {
		return touURL;
	}

	public void setTouURL(String touURL) {
		this.touURL = touURL;
	}

	public Boolean getAmendedTouFlag() {
		return amendedTouFlag;
	}

	public void setAmendedTouFlag(Boolean amendedTouFlag) {
		this.amendedTouFlag = amendedTouFlag;
	}

	public Boolean getAmendedTouFlagB() {
		return amendedTouFlagB;
	}

	public void setAmendedTouFlagB(Boolean amendedTouFlagB) {
		this.amendedTouFlagB = amendedTouFlagB;
	}
	
	public QuoteLineItem getSetUpRelatedSubsumedPart(){
		return setUpRelatedSubsumedPart;
	}
	
	public String getNonIBMSerialNumber() {
		return nonIBMSerialNumber == null ? "" : nonIBMSerialNumber;
	}

	public String getNonIBMModel() {
		return nonIBMModel == null ? "" : nonIBMModel;
	}

	public boolean isSetLineToRsvpSrpFlag() {
		return isSetLineToRsvpSrpFlag;
	}

	public void setSetLineToRsvpSrpFlag(boolean isSetLineToRsvpSrpFlag) {
		this.isSetLineToRsvpSrpFlag = isSetLineToRsvpSrpFlag;
	}
	
	public boolean isDivestedPart(){
		return divestedPart;
	}

	public String getPartTypeDsc() {
		return partTypeDsc;
	}

	public void setPartTypeDsc(String partTypeDsc) {
		this.partTypeDsc = partTypeDsc;
	}

    
    /**
     * Getter for partHasPrice.
     * @return the partHasPrice
     */
    public boolean isPartHasPrice() {
        return this.partHasPrice;
    }

	@Override
	public boolean isOwerTransferPart() {
		return part.isApplncOwnerShipTransfer();
	}

	public boolean isReinstatementPart() {
	    boolean isReinstatementPart = false;

	    try {
	    	RenewalRevnStreamCode renewalRevnStreamCode = (RenewalRevnStreamCode)RenewalRevnStreamCodeFactory.singleton().getRenewalRevnStreamCodeFromCache(this.getRevnStrmCode());

            if (null != renewalRevnStreamCode && 1 != renewalRevnStreamCode.getRevnSteamCodeFlag()){
            	isReinstatementPart = true;
            }

        } catch (TopazException e) {
            logContext.error(this, e.getMessage());
        }

	    return isReinstatementPart;
	}
	
	public void setSplitFactor(Double splitFactor){
		this.splitFactor = splitFactor;
	}
	
	public Double getSplitBidExtPriceIfApplicable(){
		if(getLocalExtProratedDiscPrc() != null){
			if(splitFactor != null && splitFactor.doubleValue() != 0){
				return getLocalExtProratedDiscPrc() * splitFactor;
			} else {
				return getLocalExtProratedDiscPrc();
			}
		}
		
		return null;
	}
	
	public boolean isAddedToLicPart(){
		if(addedToLicParts != null && addedToLicParts.size() > 0)
			return true;
		else
			return false;
	}
	
    public void setParentMasterPart(QuoteLineItem parentMasterPart){
    	this.parentMasterPart = parentMasterPart;
    }
    
    public QuoteLineItem getParentMasterPart(){
    	return parentMasterPart;
    }

	/**
	 * @return the addedToLicParts
	 */
	public List getAddedToLicParts() {
		return addedToLicParts;
	}

	/**
	 * @param addedToLicParts the addedToLicParts to set
	 */
	public void setAddedToLicParts(List addedToLicParts) {
		this.addedToLicParts = addedToLicParts;
	}

    /**
     * Getter for deployModel.
     * 
     * @return the deployModel
     */
    public DeployModel getDeployModel() {
        if (this.deployModel == null) {
            this.deployModel = new DeployModel();
        }
        return this.deployModel;
    }

    /**
     * Sets the deployModel.
     * 
     * @param deployModel the deployModel to set
     */
    public void setDeployModel(DeployModel deployModel) {
        this.deployModel = deployModel;
    }

	@Override
	public boolean isDeploymentAssoaciatePart() {
		return this.isApplncPart() && (this.isApplncMain()|| this.isApplncUpgrade()|| this.isOwerTransferPart());
	}
	
	public void setModifiedProperty(ModifiedProperty mp)
	{
		modifiedProperty = mp;
	}
    public ModifiedProperty getModifiedProperty()
    {
    	if ( modifiedProperty == null )
    	{
    		modifiedProperty = new ModifiedProperty();
    	}
    	return modifiedProperty;
    }

	public Boolean getHdrAgrmentAmdFlag() {
		return hdrAgrmentAmdFlag;
	}

	public void setHdrAgrmentAmdFlag(Boolean hdrAgrmentAmdFlag) {
		this.hdrAgrmentAmdFlag = hdrAgrmentAmdFlag;
	}
	
    // 14.2 monthly software start
    public boolean monthlySoftwarePart;
    public boolean isMonthlySoftwarePart(){
    	return monthlySoftwarePart;
    }
    //:~ 14.2 monthly software end
    
    // task #627593 display deploymentID in renewal/create quote page
    private String renewalDeploymtID;
    public String getRenewalDeploymtID() {
    	return renewalDeploymtID;
    }
    public void setRenewalDeploymtID(String rqDeploymtID) {
    	this.renewalDeploymtID = rqDeploymtID;
    }

    public String getAddReasonCode() {
        return addReasonCode;
    }

    public String getReplacedReasonCode() {
        return replacedReasonCode;
    }

    public String getNewConfigFlag() {
        return newConfigFlag;
    }

    public Integer getOriginatingItemNum() {
        return originatingItemNum;
    }

    /**
     * Getter for remaningTermForExtension.
     * 
     * @return the remaningTermForExtension
     */
    public Integer getRemainingTermTillCAEndDate() {
        return this.remainingTermTillCAEndDate;
    }

    /**
     * Sets the remaningTermForExtension.
     * 
     * @param remainingTermTillCAEndDate the remaningTermForExtension to set
     */
    public void setRemainingTermTillCAEndDate(Integer remainingTermTillCAEndDate) {
        this.remainingTermTillCAEndDate = remainingTermTillCAEndDate;
    }

}
