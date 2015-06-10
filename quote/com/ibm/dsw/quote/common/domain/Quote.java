package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.util.sort.PartSortUtil;
import com.ibm.dsw.quote.draftquote.util.sort.QuoteBaseComparator;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.dsw.quote.submittedquote.domain.ExecSummary;
import com.ibm.dsw.quote.submittedquote.domain.SubmittedQuoteAccess;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code><code> class.
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Mar 12, 2007
 */
public class Quote implements Serializable {
	
	private transient QuoteHeader quoteHeader = null;
	private transient QuoteBusinessDomain quoteBusinessDomain = null;
    private transient MonthlySwQuoteDomain monthlySwQuoteDomain = null;
	
    public Quote(QuoteHeader quoteHeader) {
        this.quoteHeader = quoteHeader;
        this.quoteBusinessDomain = new QuoteBusinessDomain();
        this.monthlySwQuoteDomain = new MonthlySwQuoteDomain();
    }
    

    private transient Customer customer = null;

    private transient Customer endUser = null;

    private transient Partner payer = null;

    private transient Partner reseller = null;

    private transient SalesRep creator = null;

    private transient SalesRep oppOwner = null;

    private transient List contactList = null;

    private transient List delegatesList = null;

    private transient List lineItemList = null;

    private transient List orders = null;

    /** the list of sap line items */
    private transient List sapLineItemList = null;

    private transient List priceTotals = null;

    private QuoteAccess quoteAccess;

    private QuoteUserAccess quoteUserAccess;

    private SubmittedQuoteAccess submittedQuoteAccess;

    private transient List webPrimaryStatusList = new ArrayList();

    private transient List webSecondaryStatusList = new ArrayList();

    private transient List sapPrimaryStatusList = new ArrayList();

    private transient List sapSecondaryStatusList = new ArrayList();

    private transient SpecialBidInfo spBidInfo;

    private transient List masterSoftwareLineItems = new ArrayList();

    // SaaS line item list
    private transient List SaaSLineItems = new ArrayList();
    private transient List masterSaaSLineItems = new ArrayList();

    // non SaaS line item list
    private transient List softwareLineItems = new ArrayList();

	private transient List fctNonStdTcAttachmentsList;

	public String creatorName;

    public String submitterName;

    public transient ExecSummary execSummary;

    private transient List promotionsList;

    public transient Map partsPricingConfigrtnsMap;

    public transient List partsPricingConfigrtnsList;

    public ConfigurationEditParams configurationEditParams;

    private SpecialBidReason specialBidReason;

    //set appliance main parts,contain appliance part type
    public transient List applncMains  ;
    
    public transient List applncUpgradeParts  ;

    private transient List applncOwnerShipParts  ;
    /**
     *  set appliance parts contain appliance , Reinstatement ,
     *  appliance Upgrade, appliance Transceiver, Renewal,additional
     */
    public transient List applncParts ;
    
    

    /**
     * validate appliance parts  MTM are not null
     */
    public transient List applncMTMParts;
    
    private transient java.lang.Integer eligFirmOrdExcptn;
    
    //439814: SaaS 10.18 - Handling line items not part of Add-on/Trade-up for QRWS
    private transient List lineItemListNotPartOfAddOnTradeUp = new ArrayList();
    
    public transient List evalActionHis = new ArrayList();
    private transient SSPEndUser sspEndUser = null;
    private transient boolean pgsAppl = false;
    
    //Handling default customer address for ship to and install at
    private transient DefaultCustAddress defaultCustAddr = null;
    
    //used for QRWS service
    public transient ApplianceLineItemAddrDetail applianceLineItemAddrDetail = null;
    
    public SSPEndUser getSspEndUser() {
		return sspEndUser;
	}
	public List getLineItemListNotPartOfAddOnTradeUp() {
		return lineItemListNotPartOfAddOnTradeUp;
	}
	public void setLineItemListNotPartOfAddOnTradeUp(
			List lineItemListNotPartOfAddOnTradeUp) {
		this.lineItemListNotPartOfAddOnTradeUp = lineItemListNotPartOfAddOnTradeUp;
	}
	public void setSspEndUser(SSPEndUser sspEndUser) {
		this.sspEndUser = sspEndUser;
	}
	public List getContactList() {
        return contactList;
    }
    public void setContactList(List contactList) {
        this.contactList = contactList;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public List getDelegatesList() {
        return delegatesList;
    }
    public void setDelegatesList(List delegatesList) {
        this.delegatesList = delegatesList;
    }
    public List getLineItemList() {
        return lineItemList;
    }
    public List getMasterSoftwareLineItems(){
        return this.masterSoftwareLineItems;
    }
    public void setLineItemList(List lineItemList) {
        this.lineItemList = lineItemList;
    }
    public void setMasterSoftwareLineItems(List masterSoftwareLineItems){
        this.masterSoftwareLineItems = masterSoftwareLineItems;

    }
    public Partner getPayer() {
        return payer;
    }
    public void setPayer(Partner payer) {
        this.payer = payer;
    }
    public Partner getReseller() {
        return reseller;
    }
    public void setReseller(Partner reseller) {
        this.reseller = reseller;
    }
    public QuoteHeader getQuoteHeader() {
        return quoteHeader;
    }

    public SalesRep getCreator() {
        return creator;
    }
    public void setCreator(SalesRep creator) {
        this.creator = creator;
    }
    public SalesRep getOppOwner() {
        return oppOwner;
    }
    public void setOppOwner(SalesRep oppOwner) {
        this.oppOwner = oppOwner;
    }

    /**
     *  Gets the sapLineItemList
     * @return Returns the sapLineItemList.
     */
    /*public List getSapLineItemList() {
        return sapLineItemList;
    }*/
    /**
     * Sets the sapLineItemList
     * @param sapLineItemList The sapLineItemList to set.
     */
    /*public void setSapLineItemList(List sapLineItemList) {
        this.sapLineItemList = sapLineItemList;
    }*/

    public List getAllWebPrimaryStatuses() {
        List pStatusList = new ArrayList();
        for (Iterator iter = webPrimaryStatusList.iterator(); iter.hasNext();) {
            QuoteStatus status = (QuoteStatus) iter.next();
            pStatusList.add(status);
        }
        return pStatusList;
    }

    public void addWebPrimaryStatus(QuoteStatus primaryStatus) {
        if (!QuoteConstants.QUOTE_STATUS_PRIMARY.equalsIgnoreCase(primaryStatus.getStatusType())
                || containsWebPrimaryStatus(primaryStatus.getStatusCode())) {
            throw new IllegalArgumentException("Invalid primary status [" + primaryStatus.getStatusCode() + "]");
        }
        webPrimaryStatusList.add(primaryStatus);
    }

    public QuoteStatus getWebPrimaryStatus(String code) {
        QuoteStatus primaryStatus = null;

        for (Iterator iter = webPrimaryStatusList.iterator(); iter.hasNext();) {
            QuoteStatus status = (QuoteStatus) iter.next();
            if (status.getStatusCode().equals(code)) {
                primaryStatus = status;
                break;
            }
        }

        return primaryStatus;
    }

    public boolean containsWebPrimaryStatus(String code) {
        return getWebPrimaryStatus(code) != null;
    }

    public List getAllWebSecondayStatuses() {
        List sStatusList = new ArrayList();
        for (Iterator iter = webSecondaryStatusList.iterator(); iter.hasNext();) {
            QuoteStatus status = (QuoteStatus) iter.next();
            sStatusList.add(status);
        }
        return sStatusList;
    }

    public void addWebSecondaryStatus(QuoteStatus secondaryStatus) {
        if (!QuoteConstants.QUOTE_STATUS_SECONDARY.equalsIgnoreCase(secondaryStatus.getStatusType())
                || containsWebSecondaryStatus(secondaryStatus.getStatusCode())) {
            throw new IllegalArgumentException("Invalid secondary status [" + secondaryStatus.getStatusCode() + "]");
        }
        webSecondaryStatusList.add(secondaryStatus);
    }

    public QuoteStatus getWebSecondaryStatus(String code) {
        QuoteStatus secondaryStatus = null;

        for (Iterator iter = webSecondaryStatusList.iterator(); iter.hasNext();) {
            QuoteStatus status = (QuoteStatus) iter.next();
            if (status.getStatusCode().equals(code)) {
                secondaryStatus = status;
                break;
            }
        }

        return secondaryStatus;
    }

    public boolean containsWebSecondaryStatus(String code) {
        return getWebSecondaryStatus(code) != null;
    }
    /**
     * @param specialBidInfo
     */
    public void setSpecialBidInfo(SpecialBidInfo specialBidInfo) {
        this.spBidInfo = specialBidInfo;
    }

    public SpecialBidInfo getSpecialBidInfo() {
        return this.spBidInfo;
    }

	public ExecSummary getExecSummary() {
		return execSummary;
	}

	public void setExecSummary(ExecSummary execSummary) {
		this.execSummary = execSummary;
	}
    public List getPriceTotals() {
        return priceTotals;
    }
    public void setPriceTotals(List priceTotals) {
        this.priceTotals = priceTotals;
    }

    public void setOrders(List orders){
        this.orders = orders;
    }

    public List getOrders(){
       	return this.orders;
    }
    public QuoteLineItem getLineItem(String partNum,int seqNum){

        if(this.lineItemList == null){
            return null;
        }
        for (int i=0;i<this.lineItemList.size(); i++){
            QuoteLineItem item = (QuoteLineItem)this.lineItemList.get(i);
            if((item.getSeqNum() == seqNum) && (item.getPartNum().equals(partNum))){
                return item;
            }
        }
        return null;
    }

    public QuoteLineItem getLineItemByDestSeqNum(String partNum, int destSeqNum){
        if(this.lineItemList == null){
            return null;
        }

        for (int i = 0; i < lineItemList.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItemList.get(i);
            if (item.getPartNum().endsWith(partNum) && (item.getDestSeqNum() == destSeqNum)) {
                return item;
            }
        }

        return null;
    }
    
    public QuoteLineItem getLineItemBySeqNum(int seqNum){
        if(this.lineItemList == null){
            return null;
        }

        for (int i = 0; i < lineItemList.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItemList.get(i);
            if (item.getSeqNum() == seqNum) {
                return item;
            }
        }

        return null;
    }

    public QuoteAccess getQuoteAccess() {
        return quoteAccess;
    }
    public void setQuoteAccess(QuoteAccess quoteAccess) {
        this.quoteAccess = quoteAccess;
    }
    public QuoteUserAccess getQuoteUserAccess() {
        return quoteUserAccess;
    }
    public void setQuoteUserAccess(QuoteUserAccess quoteUserAccess) {
        this.quoteUserAccess = quoteUserAccess;
    }
    public SubmittedQuoteAccess getSubmittedQuoteAccess() {
        return submittedQuoteAccess;
    }
    public void setSubmittedQuoteAccess(SubmittedQuoteAccess submittedQuoteAccess) {
        this.submittedQuoteAccess = submittedQuoteAccess;
    }
    public List getSapPrimaryStatusList() {
        return sapPrimaryStatusList;
    }
    public void setSapPrimaryStatusList(List sapPrimaryStatusList) {
        this.sapPrimaryStatusList = sapPrimaryStatusList;
    }
    public List getSapSecondaryStatusList() {
        return sapSecondaryStatusList;
    }
    public void setSapSecondaryStatusList(List sapSecondaryStatusList) {
        this.sapSecondaryStatusList = sapSecondaryStatusList;
    }
    public void addSapPrimaryStatus(QuoteStatus primaryStatus) {
        sapPrimaryStatusList.add(primaryStatus);
    }
    public void addSapSecondaryStatus(QuoteStatus primaryStatus) {
        sapSecondaryStatusList.add(primaryStatus);
    }

    public QuoteStatus getSapPrimaryStatus(String code) {
        QuoteStatus sapPrimaryStatus = null;

        for (Iterator iter = sapPrimaryStatusList.iterator(); iter.hasNext();) {
            QuoteStatus status = (QuoteStatus) iter.next();
            if (status.getStatusCode().equals(code)) {
                sapPrimaryStatus = status;
                break;
            }
        }
        return sapPrimaryStatus;
    }

    public boolean containsSapPrimaryStatus(String code) {
        return getSapPrimaryStatus(code) != null;
    }

    public QuoteStatus getSapSecondaryStatus(String code) {
        QuoteStatus sapSecondaryStatus = null;

        for (Iterator iter = sapSecondaryStatusList.iterator(); iter.hasNext();) {
            QuoteStatus status = (QuoteStatus) iter.next();
            if (status.getStatusCode().equals(code)) {
                sapSecondaryStatus = status;
                break;
            }
        }
        return sapSecondaryStatus;
    }

    public boolean containsSapSecondaryStatus(String code) {
        return getSapSecondaryStatus(code) != null;
    }
    
    /**
     * Get SaaS brand code and brand desc
     * @return
     */
    public Map<String,String> getSaasBrandCodeMap(){
        Map<String,String> brandMap = new HashMap();
        Set<String> brandSet = new HashSet<String>();
        List<PartsPricingConfiguration> configrtns = getPartsPricingConfigrtnsList();
        for (Iterator iterator = configrtns.iterator(); iterator.hasNext();) {
            PartsPricingConfiguration config = (PartsPricingConfiguration) iterator.next();
            brandSet.add(config.getProdBrandCode());
        }
        for (Iterator iterator1 = brandSet.iterator(); iterator1.hasNext();) {
            String brandCode = (String) iterator1.next();
            String brandCodeDesc = "";
            for (Iterator iterator2 = configrtns.iterator(); iterator2.hasNext();) {
                PartsPricingConfiguration config = (PartsPricingConfiguration) iterator2.next();
                if(StringUtils.equals(brandCode, config.getProdBrandCode())){
                    brandCodeDesc = config.getProdBrandCodeDscr();
                    break;
                }
            }
            brandMap.put(brandCode, brandCodeDesc);
        }
        return brandMap;
    }

    /**
     * Get SaaS brand and related part configuration
     * @return
     */
    public Map<String,List<PartsPricingConfiguration>> getSaasBrandMap(){
        Map <String,List<PartsPricingConfiguration>> brandMap = new HashMap();
        List<PartsPricingConfiguration> configrtns = getPartsPricingConfigrtnsList();
        Map<String,String> brandCodeMap = getSaasBrandCodeMap();
        for (Iterator iterator1 = brandCodeMap.keySet().iterator(); iterator1.hasNext();) {
            String saasBrand = (String) iterator1.next();
            for (Iterator iterator2 = configrtns.iterator(); iterator2.hasNext();) {
                PartsPricingConfiguration config = (PartsPricingConfiguration) iterator2.next();
                if(saasBrand.equals(config.getProdBrandCode())){
                    if(brandMap.get(saasBrand) ==  null){
                        List<PartsPricingConfiguration> brandConfigrtns = new ArrayList<PartsPricingConfiguration>();
                        brandConfigrtns.add(config);
                        brandMap.put(saasBrand, brandConfigrtns);
                    }else{
                        brandMap.get(saasBrand).add(config);
                    }                	
                }
            }
        }
        return brandMap;
    }
    
    /**
     * @return Returns the creatorName.
     */
    public String getCreatorName() {
        return creatorName;
    }
    /**
     * @return Returns the submitterName.
     */
    public String getSubmitterName() {
        return submitterName;
    }
    /**
     * clear webPrimaryStatusList
     */
    public void clearWebPrimaryStatusList() {
        webPrimaryStatusList.clear();
    }
    /**
     * clear webSecondaryStatusList
     */
    public void clearWebSecondaryStatusList() {
        webSecondaryStatusList.clear();
    }
    public void setEndUser(Customer endUser) {
        this.endUser = endUser;
    }
    public Customer getEndUser() {
        return endUser;
    }

	public List getPromotionsList() {
		return promotionsList;
	}
	public void setPromotionsList(List promotionsList) {
		this.promotionsList = promotionsList;
	}
    public List getFctNonStdTcAttachmentsList() {
		return fctNonStdTcAttachmentsList;
	}
	public void setFctNonStdTcAttachmentsList(List fctNonStdTcAttachmentsList) {
		this.fctNonStdTcAttachmentsList = fctNonStdTcAttachmentsList;
	}

	public List getSaaSLineItems() {
		return SaaSLineItems;
	}
	public void setSaaSLineItems(List SaaSLineItems) {
		this.SaaSLineItems = SaaSLineItems;
	}

    public List getSoftwareLineItems() {
		return softwareLineItems;
	}
	public void setSoftwareLineItems(List softwareLineItems) {
		this.softwareLineItems = softwareLineItems;
	}
	public Map getPartsPricingConfigrtnsMap() {
		return partsPricingConfigrtnsMap;
	}
	public void setPartsPricingConfigrtnsMap(Map partsPricingConfigrtnsMap) {
		this.partsPricingConfigrtnsMap = partsPricingConfigrtnsMap;
	}
	public List getPartsPricingConfigrtnsList() {
        if (partsPricingConfigrtnsList == null) {
            partsPricingConfigrtnsList = new ArrayList();
        }
        return partsPricingConfigrtnsList;
	}
	public void setPartsPricingConfigrtnsList(List partsPricingConfigrtnsList) {
		this.partsPricingConfigrtnsList = partsPricingConfigrtnsList;
	}
	public ConfigurationEditParams getConfigurationEditParams() {
		return configurationEditParams;
	}
	public void setConfigurationEditParams(
			ConfigurationEditParams configurationEditParams) {
		this.configurationEditParams = configurationEditParams;
	}
	public QuoteLineItem getSaaSLineItem(int seqNum) {
		if (this.SaaSLineItems == null) {
			return null;
		}
		for (int i = 0; i < this.SaaSLineItems.size(); i++) {
			QuoteLineItem item = (QuoteLineItem) this.SaaSLineItems.get(i);
			if (item.getDestSeqNum() == seqNum){
				return item;
			}
		}
		return null;
	}
	public List getMasterSaaSLineItems() {
		return masterSaaSLineItems;
	}
	public void setMasterSaaSLineItems(List masterSaaSLineItems) {
		this.masterSaaSLineItems = masterSaaSLineItems;
	}
	public SpecialBidReason getSpecialBidReason() {
		return specialBidReason;
	}
	public void setSpecialBidReason(SpecialBidReason specialBidReason) {
		this.specialBidReason = specialBidReason;
	}
	public List getApplncMains() {
		return applncMains;
	}
	public void setApplncMains(List applncMains) {
		this.applncMains = applncMains;
	}
	public List getApplncParts() {
		return applncParts;
	}
	public void setApplncParts(List applncParts) {
		this.applncParts = applncParts;
	}
	public List getApplncMTMParts() {
		return applncMTMParts;
	}
	public void setApplncMTMParts(List applncMTMParts) {
		this.applncMTMParts = applncMTMParts;
	}
	
	public java.lang.Integer getEligFirmOrdExcptn() {
	        return eligFirmOrdExcptn == -1 ? null : eligFirmOrdExcptn;
	}

    public void setEligFirmOrdExcptn(java.lang.Integer eligFirmOrdExcptn) {
        this.eligFirmOrdExcptn = eligFirmOrdExcptn;
    }
    
	public List getApplncUpgradeParts() {
		return applncUpgradeParts;
	}
	public void setApplncUpgradeParts(List applncUpgradeParts) {
		this.applncUpgradeParts = applncUpgradeParts;
	}

	public List getApplncOwnerShipParts() {
		return applncOwnerShipParts;
	}
	public void setApplncOwnerShipParts(List applncOwnerShipParts) {
		this.applncOwnerShipParts = applncOwnerShipParts;
	}
	public List getEvalActionHis() {
		return evalActionHis;
	}
	public void setEvalActionHis(List evalActionHis) {
		this.evalActionHis = evalActionHis;
	}
	
	Double overallYtyGrowth = null;

	public Double getOverallYtyGrowth() {
		return overallYtyGrowth;
	}
	public void setOverallYtyGrowth(Double overallYtyGrowth) {
		this.overallYtyGrowth = overallYtyGrowth;
	}
	public boolean isPgsAppl() {
		return pgsAppl;
	}
	public void setPgsAppl(boolean pgsAppl) {
		this.pgsAppl = pgsAppl;
	}
	
	public ApplianceLineItemAddrDetail getApplianceLineItemAddrDetail() {
		return applianceLineItemAddrDetail;
	}
	public void setApplianceLineItemAddrDetail(ApplianceLineItemAddrDetail applianceLineItemAddrDetail) {
		this.applianceLineItemAddrDetail = applianceLineItemAddrDetail;
	}

	public void calculateLineItemOverallDiscount() throws TopazException {
		List lineItemList = getLineItemList();
		for (int i = 0; i < lineItemList.size(); i++) {
			QuoteLineItem item = (QuoteLineItem) lineItemList.get(i);
			if (item.calculateOverallDiscount() != null) {
				item.setTotDiscPct(item.calculateOverallDiscount());
			}
		}
	}
	
	public DefaultCustAddress getDefaultCustAddr() {
		return defaultCustAddr;
	}
	public void setDefaultCustAddr(DefaultCustAddress defaultCustAddr) {
		this.defaultCustAddr = defaultCustAddr;
	}
	
    public void calculateEquityCurveTotal() {
    	if(!quoteBusinessDomain.isQuoteHasEcPart(this.lineItemList)){
    		return;
    	}
        BigDecimal totalPrice = new BigDecimal(0.00);
        BigDecimal totalMinPrice = new BigDecimal(0.00);
        BigDecimal totalMaxPrice = new BigDecimal(0.00);

        for (Iterator itemIt = getLineItemList().iterator(); itemIt.hasNext();) {
            QuoteLineItem item = (QuoteLineItem) itemIt.next();
            EquityCurve equityCurve = item.getEquityCurve();
            if (item.isSaasPart() || item.isMonthlySoftwarePart() || null == equityCurve || !equityCurve.isEquityCurveFlag()){
                continue ;
            }
            if (null != item.getLocalExtProratedPrc()){
                totalPrice = totalPrice.add(new BigDecimal(item.getLocalExtProratedPrc()));
            }
            if (null != item.getMinBidExtendedPrice()){
                totalMinPrice = totalMinPrice.add(new BigDecimal(item.getMinBidExtendedPrice()));
            }
            if (null != item.getMaxBidExendedPrice()){
                totalMaxPrice = totalMaxPrice.add(new BigDecimal(item.getMaxBidExendedPrice()));
            }
        }
        double totalMinDiscount = totalMinPrice.doubleValue();
        double totalMaxDiscount = totalMaxPrice.doubleValue();

           EquityCurveTotal equityCurveTotal = this.getQuoteHeader().getEquityCurveTotal();
        // euqity curve weight average minimum and maximum discount is null, do not update EC_WTD_AVG_MIN_PCT and EC_WTD_AVG_MAX_PCT
        boolean updateFlag = Math.abs(totalMinDiscount - 0.00) < 0.0001 && Math.abs(totalMaxDiscount - 0.00) < 0.0001 || Math.abs(totalPrice.doubleValue() - 0.00) < 0.001;
        if (!updateFlag){

            totalMinPrice = (totalPrice.subtract(totalMinPrice)).multiply(new BigDecimal(100));
            totalMaxPrice = (totalPrice.subtract(totalMaxPrice)).multiply(new BigDecimal(100));
            totalMinPrice = totalMinPrice.divide(totalPrice,3, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
            totalMaxPrice = totalMaxPrice.divide(totalPrice,3, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP) ;
            if (null == equityCurveTotal){
                equityCurveTotal = new EquityCurveTotal();
            }
            equityCurveTotal.setWeightAverageMin(totalMinPrice.doubleValue());
            equityCurveTotal.setWeightAverageMax(totalMaxPrice.doubleValue());
        }else{
            equityCurveTotal.setWeightAverageMin(null);
            equityCurveTotal.setWeightAverageMax(null);
        }
        this.getQuoteHeader().setEquityCurveTotal(equityCurveTotal);
    }

     public void calculateYtyAndImpldGrwthPctTotal()  throws TopazException{
        //Only set implied growth % and overall growth % to header if the quote is eligible for growth delegation
        if(!GrowthDelegationUtil.isDisplayOverallYTYTotal(this)){
            return ;
        }
        double ytyGrwthTotPrice = 0;
        double ytyGrwthTotBidPrice = 0;

        double impldGrwthTotPrice = 0;
        double impldGrwthTotBidPrice = 0;
        
        boolean overallGrowthFlag = false;
        boolean impldGrowthFlag = false; 

        // Exclude the additional year.        
        List<Integer> additionalYearItemsSeqNums = GrowthDelegationUtil.getRQAdditionalYearItemsSeqNums(this);
        
        for (Iterator itemIt = getLineItemList().iterator(); itemIt.hasNext();) {
            QuoteLineItem item = (QuoteLineItem) itemIt.next();
            
            if (additionalYearItemsSeqNums.contains(item.getSeqNum())) {
            	continue;
            }
            
            if(isQulifiedPrice(item,this)){
                YTYGrowth yty = item.getYtyGrowth();
                if(yty == null){
                    GrowthDelegationUtil.updateLineItemYTYGrowthStatusAndPct(this,item);
                    yty = item.getYtyGrowth();
                }

                if(yty.isIncludedInOverallYTYGrowth()){
                    if(item.getLocalExtProratedDiscPrc() != null){
                        ytyGrwthTotBidPrice += item.getLocalExtProratedDiscPrc().doubleValue();
                        Double price = GrowthDelegationUtil.getRoundedExtndLppPrice(item, this);
                        ytyGrwthTotPrice += (price == null? 0.0 : price.doubleValue());
                        overallGrowthFlag = true;
                    }                   
                    
                }

                if(yty.isIncludedInImpliedYTYGrowth()){
                    if(item.getLocalExtProratedDiscPrc() != null){
                        impldGrwthTotBidPrice += item.getLocalExtProratedDiscPrc().doubleValue();
                        Double price = GrowthDelegationUtil.getRoundedExtndLppPrice(item, this);
                        impldGrwthTotPrice += (price == null? 0.0 : price.doubleValue());
                        impldGrowthFlag = true; 
                    }                    
                }
            }
        }
        // Calculate overall/implied growth to include omitted line items
        if (this.getQuoteHeader().isOmittedLine() && QuoteConstants.OMIT_RECALCULATE_N == this.getQuoteHeader().getOmittedLineRecalcFlag() ){
            OmitRenewalLine omitRenewalLine = QuoteLineItemFactory.singleton().getOmittedRenewalLine(this.getQuoteHeader().getWebQuoteNum());
            if (null != omitRenewalLine){
            	Double price = omitRenewalLine.getOmittedLinePrice();
            	if(overallGrowthFlag){
            	    ytyGrwthTotPrice += (price == null? 0.0 : price.doubleValue());
            	}
            	if(impldGrowthFlag){
            	    impldGrwthTotPrice += (price == null? 0.0 : price.doubleValue());
            	}
            }
        }

        if(ytyGrwthTotPrice != 0){
            double ytyGrwthPct = (ytyGrwthTotBidPrice / ytyGrwthTotPrice - 1) * 100;
            this.getQuoteHeader().setYtyGrwthPct(DecimalUtil.roundAsDouble(ytyGrwthPct, 2));
        } else {
            this.getQuoteHeader().setYtyGrwthPct(null);
        }

        if(impldGrwthTotPrice != 0){
            double impldGrwthPct = (impldGrwthTotBidPrice / impldGrwthTotPrice - 1) * 100;
            this.getQuoteHeader().setImpldGrwthPct(DecimalUtil.roundAsDouble(impldGrwthPct, 2));
        } else {
            this.getQuoteHeader().setImpldGrwthPct(null);
        }
     }

    public void calculateMultipleAdditionalYearImpliedGrowth() {
    	if (GrowthDelegationUtil.isDisplayOverallYTYTotal(this)) {
    		List additionalYearImpliedGrowthList = GrowthDelegationUtil.calculateMultipleAdditionalYearImpliedGrowthList(this);
    		this.getQuoteHeader().setMultipleAdditionalYearImpliedGrowth(additionalYearImpliedGrowthList);
    	}
 	}
	
    public QuoteBusinessDomain getQuoteBusinessDomain() {
		return quoteBusinessDomain;
	}
	private  boolean isQulifiedPrice(QuoteLineItem item,Quote quote){
        if(quote.getQuoteHeader().isRenewalQuote() && null != item.getPartQty() && 0 == item.getPartQty().intValue()){
            return false;
        }

        return (item.getPartQty() != null || item.isSaasPart() || item.isMonthlySoftwarePart());
    }
	public MonthlySwQuoteDomain getMonthlySwQuoteDomain() {
		return monthlySwQuoteDomain;
	}

	/**
	 * @param lineItems
	 * @return
	 * if quote has saas part, return true
	 */
	public boolean isQuoteHasSaasPart(){
		if(this.SaaSLineItems == null || this.SaaSLineItems.size() == 0){
			return false;
		}
		return true;
	}
	
	/**
	 * @param lineItems
	 * @return
	 * if quote has software part, return true
	 */
	public boolean isQuoteHasSoftwarePart(){
		if(this.softwareLineItems == null || this.softwareLineItems.size() == 0){
			return false;
		}
		return true;
	}
	
	public void sortMonthlySwParts() throws TopazException {
    	if (this.getQuoteHeader().isSalesQuote()) {
    		String lob = this.getQuoteHeader().getLob().getCode();
            if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob) 
            		|| QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)
                    || QuoteConstants.LOB_PAUN.equals(lob)
                    || QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)
                    || QuoteConstants.LOB_FCT.equalsIgnoreCase(lob)) {
            	List configrtnsList = this.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
            	Map configrtnsMap = this.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap();
            	if(configrtnsList != null && configrtnsList.size() > 0){
            		List sortedMasterMonthlySwPars = new ArrayList();
            		for (Iterator iterator = configrtnsList.iterator(); iterator
							.hasNext();) {
						MonthlySoftwareConfiguration configrtn = (MonthlySoftwareConfiguration) iterator.next();
						List monthlySwParts = (List)configrtnsMap.get(configrtn);
						Collections.sort(monthlySwParts, QuoteBaseComparator.createMonthlySwPartComparator());
						sortedMasterMonthlySwPars.addAll(monthlySwParts);
					}
            		this.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems().clear();
            		this.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems().addAll(sortedMasterMonthlySwPars);
            	}
            } else {
            	return;
            }
        } else {
        	return;
        }

        // user's specified order always has the top priority, for example:
        // 4 QLIs on the quote
        // QLI_1 : standard order
        // QLI_2 : standard order
        // QLI_3 : user specified order 2
        // QLI_4 : standard order
        // in the sorting result, QLI_3 will be the first one, others will be
        // sortd using standard order
    	List softwareLineItems = this.getSoftwareLineItems();
    	List masterMonthlySwLineItems = this.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems();
        int manualSortOrder = 0;
        int order = 0;
        if(softwareLineItems != null){
        	manualSortOrder = softwareLineItems.size();
        	order = softwareLineItems.size();
        }
        List sorttedMonthlySwLineItems = new ArrayList();
        for (int i = 0; i < masterMonthlySwLineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) masterMonthlySwLineItems.get(i);
            manualSortOrder = manualSortOrder+1;
            if (item.getManualSortSeqNum() != 0) {
                item.setManualSortSeqNum(manualSortOrder);
            }
            order = order + 1;
            item.setQuoteSectnSeqNum(order);
            sorttedMonthlySwLineItems.add(item);
            List rampUpLineItems = item.getRampUpLineItems();
            for (int j = 0; j < rampUpLineItems.size(); j++) {
                QuoteLineItem rampUpItem = (QuoteLineItem) rampUpLineItems.get(j);
                order = order + 1;
                rampUpItem.setQuoteSectnSeqNum(order);
                // make sure the manual sort order of sub line item is same as the main line item
                rampUpItem.setManualSortSeqNum(item.getManualSortSeqNum());
                sorttedMonthlySwLineItems.add(rampUpItem);
            }
        }
        this.getMonthlySwQuoteDomain().getMonthlySoftwares().clear();
        this.getMonthlySwQuoteDomain().getMonthlySoftwares().addAll(sorttedMonthlySwLineItems);
        
        sortMonthlySwDestSeqNums();
        
    }
	
    private void sortMonthlySwDestSeqNums() throws TopazException{
    	List monthlySwItems = this.getMonthlySwQuoteDomain().getMonthlySoftwares();
    	if(monthlySwItems == null || monthlySwItems.size() == 0){
    		return;
    	}
    	List<MonthlySwPartSort> newMonthlySwItems = new ArrayList();
    	for (Iterator iterator = monthlySwItems.iterator(); iterator.hasNext();) {
            MonthlySwLineItem qli = (MonthlySwLineItem) iterator.next();
            MonthlySwPartSort monthlySwPartSort = new MonthlySwPartSort(qli.getPartNum(), qli.getSeqNum(), qli.getDestSeqNum(),
                    qli.isReplacedPart(), qli);
			newMonthlySwItems.add(monthlySwPartSort);
		}
    	Collections.sort(newMonthlySwItems, new  Comparator(){
            public int compare(Object o1, Object o2) {
            	MonthlySwPartSort item1 = (MonthlySwPartSort) o1;
            	MonthlySwPartSort item2 = (MonthlySwPartSort) o2;
            	if(item1.isReplacedPart() && item2.isReplacedPart()){
            		return item1.getSeqNum() - item2.getSeqNum();
            	}else if(item1.isReplacedPart() && !item2.isReplacedPart()){
            		return -1;
            	}else if(!item1.isReplacedPart() && item2.isReplacedPart()){
            		return 1;
            	}
            	return item1.getSeqNum() - item2.getSeqNum();
            }
        });

        Collections.sort(newMonthlySwItems, new Comparator() {

            public int compare(Object o1, Object o2) {
                MonthlySwPartSort item1 = (MonthlySwPartSort) o1;
                MonthlySwPartSort item2 = (MonthlySwPartSort) o2;
                if (item1.quoteLineItem.getPartNum().equals(item2.quoteLineItem.getPartNum())) {
                    if ((item1.quoteLineItem.isMonthlySwSubscrptnPart() && !item1.quoteLineItem.isRampupPart())
                            && (item2.quoteLineItem.isMonthlySwSubscrptnPart() && item2.quoteLineItem.isRampupPart())) {
                        return 1;
                    } else if ((item1.quoteLineItem.isMonthlySwSubscrptnPart() && item1.quoteLineItem.isRampupPart())
                            && (item2.quoteLineItem.isMonthlySwSubscrptnPart() && !item2.quoteLineItem.isRampupPart())) {
                        return -1;
                    }
                }
                return 0;
            }
        });

        Collections.sort(newMonthlySwItems, new Comparator() {

            public int compare(Object o1, Object o2) {
                MonthlySwPartSort item1 = (MonthlySwPartSort) o1;
                MonthlySwPartSort item2 = (MonthlySwPartSort) o2;
                if ((!item1.quoteLineItem.isRampupPart()) && (item2.quoteLineItem.isRampupPart())) {
                    return 1;
                } else if ((item1.quoteLineItem.isRampupPart()) && (!item2.quoteLineItem.isRampupPart())) {
                    return -1;
                }
                return 0;
            }
        });

        // changed by Jack for RTC # 696310, the destSeqNum of monthly parts should not begins with 0, it should begins
        // with the minimal destSeqNum of all monthly parts.
        int destSeqNum = this.getSoftwareLineItems().size();
    	Map relatedItemNumMap = new HashMap();
    	Map desSeqNumMap = new HashMap();
    	for (Iterator iterator = newMonthlySwItems.iterator(); iterator.hasNext();) {
    		destSeqNum ++;
    		MonthlySwPartSort qli = (MonthlySwPartSort) iterator.next();
    		setRelatedSeqNums(qli, monthlySwItems, destSeqNum, relatedItemNumMap, desSeqNumMap);
		}
    	for (Iterator iterator = monthlySwItems.iterator(); iterator.hasNext();) {
			QuoteLineItem subQli = (QuoteLineItem) iterator.next();
			Integer desSeqNumInt = (Integer) desSeqNumMap.get(subQli.getPartNum() + subQli.getSeqNum());
			if(desSeqNumInt != null){
				subQli.setDestSeqNum(desSeqNumInt.intValue());
			}
			Integer relatedSeqNumInt = (Integer) relatedItemNumMap.get(subQli.getPartNum() + subQli.getSeqNum());
			if(relatedSeqNumInt != null){
				subQli.setIRelatedLineItmNum(relatedSeqNumInt.intValue());
			}
		}
    }

    private class MonthlySwPartSort{
    	String partNum;
    	int seqNum;
    	int destSeqNum;
        boolean replacedPart;

        MonthlySwLineItem quoteLineItem;

        /**
         * DOC MonthlySwPartSort constructor comment.
         * 
         * @param partNum
         * @param seqNum
         * @param destSeqNum
         * @param replacedPart
         * @param quoteLineItem
         */
        public MonthlySwPartSort(String partNum, int seqNum, int destSeqNum, boolean replacedPart, MonthlySwLineItem quoteLineItem) {
            super();
            this.partNum = partNum;
            this.seqNum = seqNum;
            this.destSeqNum = destSeqNum;
            this.replacedPart = replacedPart;
            this.quoteLineItem = quoteLineItem;
        }

        public String getPartNum() {
			return partNum;
		}
		public int getSeqNum() {
			return seqNum;
		}
		public int getDestSeqNum() {
			return destSeqNum;
		}
		public boolean isReplacedPart() {
			return replacedPart;
		}

        /**
         * Getter for quoteLineItem.
         * 
         * @return the quoteLineItem
         */
        public QuoteLineItem getQuoteLineItem() {
            return this.quoteLineItem;
        }

    }
    
    private void setRelatedSeqNums(MonthlySwPartSort qli, List monthlySwLineItems, int relatedSeqNum, Map relatedItemNumMap, Map desSeqNumMap) throws TopazException{
    	
    	for (Iterator iterator = monthlySwLineItems.iterator(); iterator.hasNext();) {
			QuoteLineItem subQli = (QuoteLineItem) iterator.next();
			if(qli.getDestSeqNum() != -1 && qli.getDestSeqNum() == subQli.getIRelatedLineItmNum()){
				relatedItemNumMap.put(subQli.getPartNum() + subQli.getSeqNum(), new Integer(relatedSeqNum));
			}
			if(qli.getPartNum().equals(subQli.getPartNum()) && qli.getSeqNum() == subQli.getSeqNum()){
				desSeqNumMap.put(subQli.getPartNum() + subQli.getSeqNum(), new Integer(relatedSeqNum));
			}
		}
    }
    
	public void fillLineItemsForQuoteBuilder() throws TopazException {

		TimeTracer tracer = TimeTracer.newInstance();
		List lineItemsList = this.getLineItemList();
		if(null == lineItemsList)
			lineItemsList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(this.getQuoteHeader().getWebQuoteNum());
		List saasLineItems = new ArrayList();
		List softwareLineItems = new ArrayList();
		
		if (lineItemsList == null || lineItemsList.size() == 0){
			this.setLineItemList(new ArrayList());
			return;
		}
		
		Iterator it = lineItemsList.iterator();
		while (it.hasNext()){
			QuoteLineItem  item = (QuoteLineItem)it.next();
			
			if (item == null) {
				return ;
			}
			
			// saas 
			if (item.isSaasPart()){
				saasLineItems.add(item);
			} else //monthly Software
				if (item.isMonthlySoftwarePart()){
					//do nothing, will fill in monthly software parts in other method
			}else {
				// software
				softwareLineItems.add(item);
			}
			
		}
		
		// set software , saas ,monthlySoftWare
		this.setSoftwareLineItems(softwareLineItems);
		this.setSaaSLineItems(saasLineItems);
		
		
		//List softwareLineItems = CommonServiceUtil.getSoftwareLineItemList(lineItemsList);
		List masterSoftwareLineItems = QuoteLineItemFactory.singleton().findMasterLineItems(lineItemsList);
		this.setMasterSoftwareLineItems(masterSoftwareLineItems);
		
		
		List flatLineItems = new ArrayList();
		for (int i = 0; i < masterSoftwareLineItems.size(); i++) {
			QuoteLineItem item = (QuoteLineItem) masterSoftwareLineItems.get(i);
			flatLineItems.add(item);
			flatLineItems.addAll(item.getAddtnlYearCvrageLineItems());
		}
		//List SaaSLineItems = QuoteLineItemFactory.singleton().findSaaSLineItems(lineItemsList);
		
		// set Msster Saas LineItems
		QuoteCommonUtil.buildSaaSLineItemsWithRampUp(this.getSaaSLineItems());
		this.setMasterSaaSLineItems(CommonServiceUtil.getMasterSaaSLineItemList(this.getSaaSLineItems()));
		
		
		for (Iterator iterator = this.getMasterSaaSLineItems().iterator(); iterator.hasNext();) {
			QuoteLineItem item = (QuoteLineItem) iterator.next();
			flatLineItems.add(item);
			flatLineItems.addAll(item.getRampUpLineItems());
			if(!item.getRampUpLineItems().isEmpty()) {
				this.getQuoteHeader().setHasRampUpPartFlag(true);
			}
		}
		
		this.getMonthlySwQuoteDomain().fillMonthlySwLineItems(lineItemsList);
		if(this.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems() != null){
			for (Iterator iterator = this.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems().iterator(); iterator.hasNext();) {
				QuoteLineItem item = (QuoteLineItem) iterator.next();
				flatLineItems.add(item);
				flatLineItems.addAll(item.getRampUpLineItems());
				if(!item.getRampUpLineItems().isEmpty()) {
					this.getQuoteHeader().setHasRampUpPartFlag(true);
				}
			}
		}
		
		this.setLineItemList(flatLineItems);

		tracer.dump();
	}
    
    public void fillMonthlySwConfiguration(boolean isDraftQuote) throws TopazException{
		TimeTracer tracer = TimeTracer.newInstance();
		if(this.getMonthlySwQuoteDomain().isQuoteHasMonthlySwPart()){	
			List<MonthlySoftwareConfiguration> confgrtnList = MonthlySoftwareConfigurationFactory.singleton().findMonthlySwConfiguration(this.getQuoteHeader().getWebQuoteNum());
	        if(confgrtnList != null && confgrtnList.size() > 0){
	        	if(isDraftQuote){
	        		this.getMonthlySwQuoteDomain().fillMonthlySwConfigurationForDraft(confgrtnList);
	        	}else{
	        		this.getMonthlySwQuoteDomain().fillMonthlySwConfigurationForSubmit(confgrtnList);
	        	}
		        
	        }
		}
		tracer.dump();
	}
    
    /**
     * @param quote
     * @throws TopazException
     * fill And Sort Configuration For Draft quote
     * fill the configurations to the quote.partsPricingConfigrtnsList
     * fill the master saas list to quote.partsPricingConfigrtnsMap
     * then sort them
     */
    public void fillAndSortSaasConfigurationForDraft() throws TopazException{
    	TimeTracer tracer = TimeTracer.newInstance();
    	if(this.isQuoteHasSaasPart()){	
			List confgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(this.getQuoteHeader().getWebQuoteNum());
	        if(confgrtnList != null && confgrtnList.size() > 0){
	        	PartSortUtil.sortConfiguration(confgrtnList);
	        }
	        this.setPartsPricingConfigrtnsList(confgrtnList);
	        this.setPartsPricingConfigrtnsMap(QuoteCommonUtil.getPartsPricingConfigurations(this.getMasterSaaSLineItems(), this.getPartsPricingConfigrtnsList()));
		}
    	tracer.dump();
	}
    
    /**
     * @param quote
     * @throws TopazException
     * fill And Sort Configuration For submitted quote
     * fill the configurations to the quote.partsPricingConfigrtnsList
     * fill the all saas list to quote.partsPricingConfigrtnsMap
     * then sort them
     */
    public void fillAndSortSaasConfigurationForSubmitted() throws TopazException{
    	if(!this.isQuoteHasSaasPart()){
    		return;
    	}
		List confgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(this.getQuoteHeader().getWebQuoteNum());
		this.setPartsPricingConfigrtnsList(confgrtnList);
		this.setPartsPricingConfigrtnsMap(QuoteCommonUtil.getPartsPricingConfigurations(this.getSaaSLineItems(), this.getPartsPricingConfigrtnsList()));
        if(confgrtnList != null && confgrtnList.size() > 0){
        	PartSortUtil.sortConfiguration(confgrtnList);
        }

    }
    
    
    /**
	 * 
	 * @param quote
	 * @throws QuoteException 
	 * @throws TopazException 
	 */
	public void processMonthlyTerm() throws QuoteException, TopazException{
		
        List<MonthlySoftwareConfiguration> monthlyConfigutns = this.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
        if (monthlyConfigutns == null || monthlyConfigutns.size() < 1) {
            return;
        }
        List<MonthlySwLineItem> monthlyItems = this.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems();
        if (monthlyItems == null || monthlyItems.size() < 1) {
            return;
        }
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();

        // submitted quote
		if (this.getQuoteHeader().isSubmittedQuote()){
            // setRemainingTermTillCAEndDate for replacedParts
            for (MonthlySoftwareConfiguration monthlyConfigutn : monthlyConfigutns) {
                if (!monthlyConfigutn.isAddOnTradeUp()) {
                    continue;
                }
                List<MonthlySwLineItem> subMonthlyLineItems = this.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap()
                        .get(monthlyConfigutn);
                if (subMonthlyLineItems == null || subMonthlyLineItems.size() < 1
                        || StringUtils.isBlank(monthlyConfigutn.getConfigrtnIdFromCa())) {
                    continue;
                }
                Map<String, ActiveService> activeServiceMap = this.getActiveServiceMap(quoteProcess, this.getQuoteHeader()
                        .getRefDocNum(), monthlyConfigutn.getConfigrtnIdFromCa());
                for (MonthlySwLineItem lineItem : subMonthlyLineItems) {
                    if (!lineItem.isMonthlySwSubscrptnPart()) {
                        continue;
                    }
                    ActiveService as = activeServiceMap.get(lineItem.getPartNum());
                    if (as == null) {
                        continue;
                    }

                    calculateRemainingTermTillCAEndDate(monthlyConfigutn, lineItem, as);
                }

            }
			return;
		}
        // draft quote
		for (MonthlySoftwareConfiguration monthlyConfigutn : monthlyConfigutns){
			
			if (!monthlyConfigutn.isAddOnTradeUp()){
				continue;
			}
			
			List<MonthlySwLineItem> subMonthlyLineItems = this.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap().get(monthlyConfigutn);
			
			if (subMonthlyLineItems == null || subMonthlyLineItems.size() < 1
					|| StringUtils.isBlank(monthlyConfigutn.getConfigrtnIdFromCa())) {
				continue;
			}
			
			Map<String, ActiveService> activeServiceMap = this.getActiveServiceMap(quoteProcess, this.getQuoteHeader().getRefDocNum(), monthlyConfigutn.getConfigrtnIdFromCa());
			
			for (MonthlySwLineItem lineItem : subMonthlyLineItems){
				if (!lineItem.isMonthlySwSubscrptnPart()){
					continue;
				}
				
				ActiveService as = activeServiceMap.get(lineItem.getPartNum());

                if (as == null) {
                    continue;
                }

                Date endDate = this.getEndDate(monthlyConfigutn, as);
                calculateRemainingTermTillCAEndDate(monthlyConfigutn, lineItem, as);
				
                //Should only set ICvrageTerm of for !replaced part's value to be: this.calculateLineItemTerm((Date)this.getQuoteHeader().getEstmtdOrdDate(), endDate, as), but not for replaced part. 
                if (!lineItem.isReplacedPart()) {
                    lineItem.setICvrageTerm(this.calculateLineItemTerm((Date) this.getQuoteHeader().getEstmtdOrdDate(), endDate,
                            as));
                }
			}
			
		}
		
		
	}

    /**
     * DOC IF Existing End Date - Estimated Start Date (Estimated Order Date + Estimated Provisioning Days) <=0 THEN set
     * term to:<br>
     * Renewal Model R = 12 months<br>
     * Renewal Model O = Original term in months<br>
     * Please refer to<br>
     * <b>Notes://ltsgdb001b/85256B83004B1D94/CA30E8393BC22D28482573A7000E50E0/888189B07D497F0685257DE10045DC75 </b><br>
     * for more details.
     * 
     * @param monthlyConfigutn
     * @param lineItem
     * @param as
     * @return
     * @throws QuoteException
     */
    private void calculateRemainingTermTillCAEndDate(MonthlySoftwareConfiguration monthlyConfigutn, MonthlySwLineItem lineItem,
            ActiveService as) throws QuoteException {
        Date endDateConsiderRenewal = this.getEndDateConsiderRenewal(monthlyConfigutn, as);

        if (lineItem.isReplacedPart()) {
            int calculateLineItemTerm = this.calculateLineItemRemainTermForReplacedParts((Date) this.getQuoteHeader()
                    .getEstmtdOrdDate(), endDateConsiderRenewal, as);
            String rewalModCode = lineItem.getRenwlMdlCode();
            if (calculateLineItemTerm == 0) {
                if (PartPriceConstants.RenewalModelCode.R.equalsIgnoreCase(rewalModCode)
                        || PartPriceConstants.RenewalModelCode.O.equalsIgnoreCase(rewalModCode)) {
                    calculateLineItemTerm = as.getRenwlTermMths();
                }
            }
            lineItem.setRemainingTermTillCAEndDate(calculateLineItemTerm);
        }
    }
	
	private Date getEndDateFromCAForMonthly(
			MonthlySoftwareConfiguration monthlyConfigurtn)
			throws QuoteException {
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		ChargeAgreement ca = quoteProcess
				.getChargeAgreementInfoWithoutTransaction(
						getQuoteHeader().getRefDocNum(), monthlyConfigurtn
						.getConfigrtnId());
		
		Date endDate = ca.getEndDate() != null ? DateUtil.parseDate(DateUtil
				.formatDate(ca.getEndDate(), DateUtil.PATTERN)) : null;

		return endDate;

	}
	
    /**
     * DOC Refer to F_WEB_MONTHLY_CONFIGRTN_END_DATE.SQL to get End Date of line item
     * 
     * @param monthlyConfigutn
     * @param as
     * @return
     * @throws QuoteException
     */
    private Date getEndDateConsiderRenewal(MonthlySoftwareConfiguration monthlyConfigutn, ActiveService as) throws QuoteException {

        Date endDate = monthlyConfigutn.getEndDate();

        /**
         * get lineItem End date
         */
        if (as != null) {
            Date currDate = DateUtil.getCurrentDate();

            endDate = DateUtil.parseDate(as.getEndDate());
            Date renewalEndDate = as.getRenewalEndDate();
            if (endDate == null || endDate.before(currDate)) {
                if (renewalEndDate != null) {
                    endDate = renewalEndDate;
                }
            }
        }

        /**
         * set ca endDate
         */
        if (endDate == null) {
            if (this.getQuoteHeader().getRefDocNum() == null) {
                return null;
            }

            endDate = getEndDateFromCAForMonthly(monthlyConfigutn);
        }
        return endDate;
    }

	private Date getEndDate(MonthlySoftwareConfiguration monthlyConfigutn,ActiveService as) throws QuoteException{
		
		
		Date endDate = monthlyConfigutn.getEndDate();
		
		/**
		 * get lineItem End date
		 */
		if (as != null){
			endDate = DateUtil.parseDate(as.getEndDate());
		}
		
		/**
		 * set ca endDate
		 */
		if (endDate == null){
			if (this.getQuoteHeader().getRefDocNum() == null){
				return null;
			}
			
			endDate = getEndDateFromCAForMonthly(monthlyConfigutn);
		}
		return endDate;
	}
	
    private int calculateLineItemRemainTermForReplacedParts(Date startDate, Date endDate, ActiveService as) {
        int finalTerm = 0;

        int calcTerm = DateUtil.calculateFullCalendarMonths(startDate, endDate);

        if (calcTerm > 0 || (calcTerm == 0 && "C".equals(as.getRenwlModelCode()))) {
            finalTerm = calcTerm;
        } else {
            return finalTerm = 0;
        }
        return finalTerm;
    }
	
	private int calculateLineItemTerm(Date startDate,Date endDate, ActiveService as){
		
		int finalTerm = 0 ;
		
		
		int calcTerm = DateUtil.calculateFullCalendarMonths(startDate,endDate);
		
		if (calcTerm > 0 || 
				(calcTerm == 0 && "C".equals(as.getRenwlModelCode()))) {
			finalTerm = calcTerm;
		} else {
			int renewalTerm = getRenewalTerm(as);
			finalTerm = renewalTerm + calcTerm;
			
			if (finalTerm < 0){
				finalTerm = renewalTerm;
			}
			
		}
		
		return finalTerm;
	}
	
	private int getRenewalTerm(ActiveService as){
		int renewalTerm = as.getRenwlTermMths() == null ? 0 : as.getRenwlTermMths().intValue();
		
		if (renewalTerm <= 0 ){
			int orgiTerm = StringUtils.isEmpty(as.getTerm()) ? 0 : Integer.parseInt(as.getTerm()); 
			
			if (orgiTerm <= 12){
				renewalTerm = orgiTerm;
			} else {
				renewalTerm = 12 ;
			}
			
		}
		
		return renewalTerm;
	}
	
	private Map<String, ActiveService> getActiveServiceMap(
			QuoteProcess quoteProcess, String caNum, String configId)
			throws QuoteException {
		Map<String, ActiveService> activeServicMap = new HashMap<String, ActiveService>();
		if (caNum == null) {
			return activeServicMap;
		}

		List<ActiveService> activeServices = quoteProcess
				.retrieveLineItemsFromOrderNoTx(caNum, configId);

		if (activeServices == null || activeServices.size() < 1) {
			return activeServicMap;
		}
		
		for (ActiveService as : activeServices ){
			if (as.isActiveFlag() && as.isSbscrptnFlag() && !as.isRampupPart()){
				activeServicMap.put(as.getPartNumber(),as);
			}
		}

		return activeServicMap;

	}
}
