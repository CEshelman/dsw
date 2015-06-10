package com.ibm.dsw.quote.configurator.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class ConfiguratorPart implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648889180710072970L;

	private static final LogContext logger = LogContextFactory.singleton().getLogContext();
	
	private Integer partQty;
	private String partQtyStr;
	private Integer origQty;
	private Boolean include;
	private int rampUpPeriod = 0;
	private String partNum;
	private String partDscr;
	private String billingFrequencyCode;
	private transient List<BillingOption> availableBillingOptions;
	private Double price;
	private boolean onAgreement;
	private boolean activeOnAgreement;
	private String subId;
	private String subIdDscr;
	private transient List<Integer> tierdScalQtyList;
	private String sapMatlTypeCode;
	private String sapMatlTypeCodeGroupCode;
	private String sapMatlTypeCodeGroupDscr;
	private String pidDscr;
    private String wwideProdCode;
	private String wwideProdCodeDscr;
	private String pricingTierModel;
	private String svpLevelA;	
	private String svpLevelB;
	private String svpLevelD;
	private String svpLevelE;
	private String svpLevelF;
	private String svpLevelG;
	private String svpLevelH;
	private String svpLevelI;
	private String svpLevelJ;
	private String svpLevelED;
	private String svpLevelGV;
	private Integer rampUpDuration;
	private String rampUpDurationStr;
	private Integer term;
	private Integer totalTerm;
	private Integer tierQtyMeasre;
	private String billgUpfrntFlag;
	private String billgMthlyFlag;
	private String billgAnlFlag;
	private String billgQtrlyFlag;
	private String billgEvtFlag;
	private transient List<ConfiguratorPart> rampUpLineItems;
	
	//attributes for adding configuration to line item table only
	private boolean replacedFlag;
	//This is an attribute for replaced parts, newPart is the part replacing this part
	private ConfiguratorPart newPart;
	private Integer refDocLineNum;
	private Integer relatedCotermLineItmNum;
	private ConfiguratorPart associatedOveragePart;
	private ConfiguratorPart associatedDailyPart;
	private ConfiguratorPart associatedSubscrptnPart;
	private ConfiguratorPart associatedSubsumedSubscrptnPart;
	private String swProdBrandCode;
	private boolean deleted;
	private boolean checked;
	private boolean mustHaveQty;
	private String rampUpFlag;
	private String rampUpSeqNum;
	private Integer destObjSeqNum;
	private Integer relatedSeqNum;
	
	//Attributes added for co term
	private Date endDate;
	private Date renewalEndDate;
	
	//Attribute for displaying parts in grey/white style
	private int rowIndex;
	
	//Attribute for co-term determination logic
	private String configrtnId;
	
	//Attribute for displaying price 
    private String publshdPriceDurtnCode;
    private String publshdPriceDurtnCodeDscr;
    
    //Attribute for prices from original configuration
    private Double localSaasOvrageAmt;
    private Double saasTotCmmtmtVal;
    private Double localExtndPrice;
    
    //FCT TO PA Finalization
    private String orignlSalesOrdRefNum;
    private String orignlConfigrtnId;
    private Date earlyRenewalCompDate;
    private Date cotermEndDate;
    private Date nextRenwlDate;
    private Integer renwlCounter;
    
    //Notes://CAMDB10/85256B890058CBA6/5D441E2FE05EFAD085256D24005CD142/0C5E94CC14FCCA0585257A680011921A
    private String sapBillgFrqncyOptCode;
    private Integer renwlTermMths;
    private String renwlMdlCode;
    
    // Notes://CAMDB10/85256B890058CBA6/CD76522BA873968E85256D33004FBB0B/E40FC86B1D5BDBDD85257B09000D7523
    private Long rand;
    
    private boolean subsumedSubscrptn;
    
    //14.1 SDD
    private boolean divestedPart;

    private String scwItemNumber;;
  
    // Restricted part search
	private boolean restrictedPart;
	private boolean needShow;
	private String partCategoryId;
	
	// 15.3 Saas Hybrid offering
	private boolean hybridOfferg;

	public String getSubIdDscr() {
		return subIdDscr;
	}

	public void setSubIdDscr(String subIdDscr) {
		this.subIdDscr = subIdDscr;
	}

	public Double getLocalExtndPrice() {
		return localExtndPrice;
	}

	public void setLocalExtndPrice(Double localExtndPrice) {
		this.localExtndPrice = localExtndPrice;
	}

	public Double getSaasTotCmmtmtVal() {
		return saasTotCmmtmtVal;
	}

	public void setSaasTotCmmtmtVal(Double saasTotCmmtmtVal) {
		this.saasTotCmmtmtVal = saasTotCmmtmtVal;
	}

	public Double getLocalSaasOvrageAmt() {
		return localSaasOvrageAmt;
	}

	public void setLocalSaasOvrageAmt(Double localSaasOvrageAmt) {
		this.localSaasOvrageAmt = localSaasOvrageAmt;
	}

	public String getConfigrtnId() {
		return configrtnId;
	}

	public void setConfigrtnId(String configrtnId) {
		this.configrtnId = configrtnId;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getRenewalEndDate() {
		return renewalEndDate;
	}

	public void setRenewalEndDate(Date renewalEndDate) {
		this.renewalEndDate = renewalEndDate;
	}

	public Integer getDestObjSeqNum() {
		return destObjSeqNum;
	}

	public void setDestObjSeqNum(Integer destObjSeqNum) {
		this.destObjSeqNum = destObjSeqNum;
	}

	public Integer getRelatedSeqNum() {
		return relatedSeqNum;
	}

	public void setRelatedSeqNum(Integer relatedSeqNum) {
		this.relatedSeqNum = relatedSeqNum;
	}

	public ConfiguratorPart getAssociatedSubscrptnPart() {
		return associatedSubscrptnPart;
	}

	public void setAssociatedSubscrptnPart(ConfiguratorPart associatedSubscrptnPart) {
		this.associatedSubscrptnPart = associatedSubscrptnPart;
	}
	
	
	
	public ConfiguratorPart getAssociatedSubsumedSubscrptnPart() {
		return associatedSubsumedSubscrptnPart;
	}

	public void setAssociatedSubsumedSubscrptnPart(
			ConfiguratorPart associatedSubsumedSubscrptnPart) {
		this.associatedSubsumedSubscrptnPart = associatedSubsumedSubscrptnPart;
	}

	public String getPartQtyStr() {
		return partQtyStr;
	}

	public void setPartQtyStr(String partQtyStr) {
		this.partQtyStr = partQtyStr;
	}

	public Integer getTotalTerm() {
		return totalTerm;
	}

	public void setTotalTerm(Integer totalTerm) {
		this.totalTerm = totalTerm;
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public Integer getTierQtyMeasre() {
		return tierQtyMeasre;
	}

	public void setTierQtyMeasre(Integer tierQtyMeasre) {
		this.tierQtyMeasre = tierQtyMeasre;
	}

	public boolean isMustHaveQty() {
		return mustHaveQty;
	}

	public void markMustHaveQty() {
		this.mustHaveQty = true;
	}

	public boolean isDeleted() {
		if(deleted){
			return true;
		}
		
		if(PartPriceSaaSPartConfigFactory.singleton().showCheckBox(this)){
			if (!checked && this.partQty != null
					&& this.partQty.intValue() == 0) {
				return true;
			}
		}
		
		return false;
	}

	public void markDeleted() {
		this.deleted = true;
	}
	
	public void markChecked(){
		this.checked = true;
	}

	public String getSwProdBrandCode() {
		return swProdBrandCode;
	}

	public void setSwProdBrandCode(String swProdBrandCode) {
		this.swProdBrandCode = swProdBrandCode;
	}

	public ConfiguratorPart getAssociatedOveragePart() {
		return associatedOveragePart;
	}

	public void setAssociatedOveragePart(ConfiguratorPart associatedOveragePart) {
		this.associatedOveragePart = associatedOveragePart;
	}

	public ConfiguratorPart getAssociatedDailyPart() {
		return associatedDailyPart;
	}

	public void setAssociatedDailyPart(ConfiguratorPart associatedDailyPart) {
		this.associatedDailyPart = associatedDailyPart;
	}

	public Integer getRelatedCotermLineItmNum() {
		return relatedCotermLineItmNum;
	}

	public void setRelatedCotermLineItmNum(Integer relatedCotermLineItmNum) {
		this.relatedCotermLineItmNum = relatedCotermLineItmNum;
	}
	
	public boolean isPartReplaced() {
		return replacedFlag;
	}
	
	public void markAsReplaced(ConfiguratorPart newPart) {
		this.replacedFlag = true;
		
		setNewPart(newPart);
	}
	
	private void setNewPart(ConfiguratorPart newPart){
		if(newPart != null){
			this.newPart = newPart;
			newPart.setRefDocLineNum(this.getRefDocLineNum());
		}
	}
	
	public void markAsReplaced() {
		this.markAsReplaced(null);
	}
	
	public Integer getRefDocLineNum() {
		return refDocLineNum;
	}
	
	public void setRefDocLineNum(Integer refDocLineNum) {
		this.refDocLineNum = refDocLineNum;
	}
	
	public boolean isSetUp(){
		return ConfiguratorConstants.SAAS_PART_TYPE.SETUP.equals(getSapMatlTypeCode());
	}
	
	public boolean isAddiSetUp(){
		return ConfiguratorConstants.SAAS_PART_TYPE.ADDI_SETUP.equals(getSapMatlTypeCode());
	}
	
	public boolean isSubscrptn() {
		return ConfiguratorConstants.SAAS_PART_TYPE.SUBSCRPTN
				.equals(getSapMatlTypeCode()) && !isSubsumedSubscrptn();
	}
	
	public boolean isOvrage(){
		return ConfiguratorConstants.SAAS_PART_TYPE.SUBSCRPTN_OVRAGE.equals(getSapMatlTypeCode());
	}
	
	public boolean isDaily(){
		return ConfiguratorConstants.SAAS_PART_TYPE.DAILY.equals(getSapMatlTypeCode());
	}
	
	public boolean isOnDemand(){
		return ConfiguratorConstants.SAAS_PART_TYPE.ON_DEMAND.equals(getSapMatlTypeCode());
	}
	
	public boolean isHumanSrvs(){
		return ConfiguratorConstants.SAAS_PART_TYPE.HUMAN_SRVS.equals(getSapMatlTypeCode());
	}
	
	public boolean isSubsumedSubscrptn(){
		return this.subsumedSubscrptn;
	}

	public void setSubsumedSubscrptn(boolean subsumedSubscrptn){
		this.subsumedSubscrptn = subsumedSubscrptn;
	}
	
	public List<ConfiguratorPart> getRampUpLineItems() {
		return rampUpLineItems;
	}
	
	public void setRampUpLineItems(List<ConfiguratorPart> rampUpLineItems) {
		this.rampUpLineItems = rampUpLineItems;
	}
	
	public void addRampUpLineItem(ConfiguratorPart rampUpLineItem, int rampSeqNum) {
		if(rampUpLineItems == null){
			rampUpLineItems = new ArrayList<ConfiguratorPart>();
		}
		if(rampSeqNum == rampUpLineItems.size()){	//ie. 1=1
			rampUpLineItems.add(rampUpLineItem);
			
		}else if(rampSeqNum > rampUpLineItems.size()){	//ie. 3>1
			for(int i = rampUpLineItems.size();i<rampSeqNum;i++){
				ConfiguratorPart cp = new ConfiguratorPart();
				cp.setPartNum(this.getPartNum());
				rampUpLineItems.add(cp);
			}
			rampUpLineItems.add(rampUpLineItem);
			
		}else if(rampSeqNum < rampUpLineItems.size()){	//ie. 1<3
			rampUpLineItems.set(rampSeqNum, rampUpLineItem);
			
		}
		if(term != null && rampUpLineItem.getRampUpDuration() != null){		////calculate term;
			Integer duration = rampUpLineItem.getRampUpDuration();
			term = new Integer(term.intValue() + duration.intValue());
		}
	}
	
	public void delRampUpLineItem(int rampSeqNum) {
		if(rampUpLineItems == null){
			return;
		}
		
		rampUpLineItems.remove(rampSeqNum);
	}	
	
	public ConfiguratorPart getRampUpPart(int rampSeqNum){
		if(rampUpLineItems == null
				|| rampSeqNum < 0
				|| rampSeqNum >= rampUpLineItems.size()){
			return null;
		} else {
			return rampUpLineItems.get(rampSeqNum);
		}
	}
	
	public Integer getRampUpDuration() {
		return rampUpDuration;
	}
	
	public void setRampUpDuration(Integer rampUpDuration) {
		this.rampUpDuration = rampUpDuration;
	}
	
	public int getRampUpPeriod() {
		return rampUpPeriod;
	}
	
	public void setRampUpPeriod(int rampUpPeriod) {
		this.rampUpPeriod = rampUpPeriod;
	}
	
	public String getSvpLevelA() {
		return svpLevelA;
	}
	
	public void setSvpLevelA(String svpLevelA) {
		this.svpLevelA = svpLevelA;
	}
	
	public String getSvpLevelB() {
		return svpLevelB;
	}
	
	public void setSvpLevelB(String svpLevelB) {
		this.svpLevelB = svpLevelB;
	}
	
	public String getSvpLevelD() {
		return svpLevelD;
	}
	
	public void setSvpLevelD(String svpLevelD) {
		this.svpLevelD = svpLevelD;
	}
	
	public String getSvpLevelE() {
		return svpLevelE;
	}
	
	public void setSvpLevelE(String svpLevelE) {
		this.svpLevelE = svpLevelE;
	}
	
	public String getSvpLevelF() {
		return svpLevelF;
	}
	
	public void setSvpLevelF(String svpLevelF) {
		this.svpLevelF = svpLevelF;
	}
	
	public String getSvpLevelG() {
		return svpLevelG;
	}
	
	public void setSvpLevelG(String svpLevelG) {
		this.svpLevelG = svpLevelG;
	}
	
	public String getSvpLevelH() {
		return svpLevelH;
	}
	
	public void setSvpLevelH(String svpLevelH) {
		this.svpLevelH = svpLevelH;
	}
	
	public String getSvpLevelI() {
		return svpLevelI;
	}
	
	public void setSvpLevelI(String svpLevelI) {
		this.svpLevelI = svpLevelI;
	}
	
	public String getSvpLevelJ() {
		return svpLevelJ;
	}
	
	public void setSvpLevelJ(String svpLevelJ) {
		this.svpLevelJ = svpLevelJ;
	}
	
	public String getSvpLevelED() {
		return svpLevelED;
	}
	
	public void setSvpLevelED(String svpLevelED) {
		this.svpLevelED = svpLevelED;
	}
	
	public String getSvpLevelGV() {
		return svpLevelGV;
	}
	
	public void setSvpLevelGV(String svpLevelGV) {
		this.svpLevelGV = svpLevelGV;
	}

	public String getPricingTierModel() {
		return pricingTierModel;
	}
	
	public void setPricingTierModel(String pricingTierModel) {
		this.pricingTierModel = pricingTierModel;
	}
	
	public String getSapMatlTypeCode() {
		return sapMatlTypeCode;
	}
	
	public void setSapMatlTypeCode(String sapMatlTypeCode) {
		this.sapMatlTypeCode = sapMatlTypeCode;
	}
	
	public Integer getPartQty() {
		return partQty;
	}
	
	public void setPartQty(Integer partQty) {
		this.partQty = partQty;
	}
	
	public Boolean getInclude() {
		return include;
	}
	
	public void setInclude(Boolean include) {
		this.include = include;
	}
	
	public String getPartNum() {
		return partNum;
	}
	
	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}
	
	public String getBillingFrequencyCode() {
		return billingFrequencyCode;
	}
	
	public void setBillingFrequencyCode(String billingFrequencyCode) {
		this.billingFrequencyCode = billingFrequencyCode;
	}
	
	public List<BillingOption> getAvailableBillingOptions() {
		return availableBillingOptions;
	}
	
	public void setAvailableBillingOptions(List<BillingOption> availableBillingOptions) {
		this.availableBillingOptions = availableBillingOptions;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public Integer getOrigQty() {
		return origQty;
	}
	
	public void setOrigQty(Integer origQty) {
		this.origQty = origQty;
	}
	
	public boolean isOnAgreement() {
		return onAgreement;
	}
	
	public void setOnAgreement(boolean onAgreement) {
		this.onAgreement = onAgreement;
	}
	
	public boolean isActiveOnAgreement() {
		return activeOnAgreement;
	}
	
	public void setActiveOnAgreement(boolean activeOnAgreement) {
		this.activeOnAgreement = activeOnAgreement;
	}
	
	public String getPartDscr() {
		return partDscr;
	}
	
	public void setPartDscr(String partDscr) {
		this.partDscr = partDscr;
	}
	
	public String getSubId() {
		return subId;
	}
	
	public void setSubId(String subId) {
		this.subId = subId;
	}
	
	public List<Integer> getTierdScalQtyList() {
		return tierdScalQtyList;
	}
	
	public void setTierdScalQtyList(List<Integer> tierdScalQtyList) {
		this.tierdScalQtyList = tierdScalQtyList;
	}
	
	public String getPidDscr() {
		return pidDscr;
	}
	
	public void setPidDscr(String pidDscr) {
		this.pidDscr = pidDscr;
	}
	
	public String getWwideProdCodeDscr() {
		return wwideProdCodeDscr;
	}
	
	public void setWwideProdCodeDscr(String wwideProdCodeDscr) {
		this.wwideProdCodeDscr = wwideProdCodeDscr;
	}
	
	public String getSapMatlTypeCodeGroupCode() {
		return sapMatlTypeCodeGroupCode;
	}
	
	public void setSapMatlTypeCodeGroupCode(String sapMatlTypeCodeGroupCode) {
		this.sapMatlTypeCodeGroupCode = sapMatlTypeCodeGroupCode;
	}
	
	public String getSapMatlTypeCodeGroupDscr() {
		return sapMatlTypeCodeGroupDscr;
	}
	
	public void setSapMatlTypeCodeGroupDscr(String sapMatlTypeCodeGroupDscr) {
		this.sapMatlTypeCodeGroupDscr = sapMatlTypeCodeGroupDscr;
	}
	
	public String getWwideProdCode() {
		return wwideProdCode;
	}
	
	public void setWwideProdCode(String wwideProdCode) {
		this.wwideProdCode = wwideProdCode;
	}
	
	public String getRampUpFlag() {
		return rampUpFlag;
	}
	
	public boolean isRampUp(){
		return StringUtils.isBlank(rampUpFlag) ?
				          false : ConfiguratorConstants.RAMP_FLAG_YES.equals(rampUpFlag);
	}

	public void setRampUpFlag(String rampUpFlag) {
		this.rampUpFlag = rampUpFlag;
	}

	public String getRampUpSeqNum() {
		return rampUpSeqNum;
	}

	public void setRampUpSeqNum(String rampUpSeqNum) {
		this.rampUpSeqNum = rampUpSeqNum;
	}
	
	public ConfiguratorPart clone(){
		ConfiguratorPart part = new ConfiguratorPart();
		
		part.setSwProdBrandCode(this.getSwProdBrandCode());
		
		part.setPartQty(this.getPartQty());
		part.setBillingFrequencyCode(this.getBillingFrequencyCode());
		part.setRefDocLineNum(this.getRefDocLineNum());
		part.setRelatedCotermLineItmNum(this.getRelatedCotermLineItmNum());
		part.setSubId(this.getSubId());
		part.setPartNum(this.getPartNum());
		part.setSapMatlTypeCode(this.getSapMatlTypeCode());
		part.setTerm(this.getTerm());
		part.setTotalTerm(this.getTotalTerm());
		
		return part;
	}
	public String getBillgUpfrntFlag() {
		return billgUpfrntFlag;
	}

	public void setBillgUpfrntFlag(String billgUpfrntFlag) {
		this.billgUpfrntFlag = billgUpfrntFlag;
	}

	public String getBillgMthlyFlag() {
		return billgMthlyFlag;
	}

	public void setBillgMthlyFlag(String billgMthlyFlag) {
		this.billgMthlyFlag = billgMthlyFlag;
	}

	public String getBillgAnlFlag() {
		return billgAnlFlag;
	}

	public void setBillgAnlFlag(String billgAnlFlag) {
		this.billgAnlFlag = billgAnlFlag;
	}

	public String getBillgQtrlyFlag() {
		return billgQtrlyFlag;
	}

	public void setBillgQtrlyFlag(String billgQtrlyFlag) {
		this.billgQtrlyFlag = billgQtrlyFlag;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getPublshdPriceDurtnCode() {
		return publshdPriceDurtnCode;
	}

	public void setPublshdPriceDurtnCode(String publshdPriceDurtnCode) {
		this.publshdPriceDurtnCode = publshdPriceDurtnCode;
	}

	public String getPublshdPriceDurtnCodeDscr() {
		return publshdPriceDurtnCodeDscr;
	}

	public void setPublshdPriceDurtnCodeDscr(String publshdPriceDurtnCodeDscr) {
		this.publshdPriceDurtnCodeDscr = publshdPriceDurtnCodeDscr;
	}
	
	public void setReplacementRelation(){
		if(this.newPart != null){
			this.setRelatedSeqNum(newPart.getDestObjSeqNum());
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{partNum=" + getPartNum());
		sb.append(", isPartReplaced=" + isPartReplaced());
		sb.append(", refDocLineNum=" + getRefDocLineNum());
		sb.append(", endDate=" + getEndDate());
		sb.append(", renewalEndDate" + getRenewalEndDate());
		sb.append(", rampUpFlag=" + getRampUpFlag());
		sb.append(", term=" + getTerm());
		sb.append(", totalTerm=" + getTotalTerm());
		sb.append(", subId=" + getSubId());
		sb.append(", sapMatlTypeCode=" + getSapMatlTypeCode());
		sb.append(", destObjSeqNum=" + getDestObjSeqNum());
		sb.append(", relatedSeqNumm=" + getRelatedSeqNum());
		sb.append("} ");
		
		if(getRampUpLineItems() != null){
			sb.append("\n").append("Ramp up part list: {\n");
			for(ConfiguratorPart part : getRampUpLineItems()){
				sb.append("[").append(part.toString()).append("]\n");
			}
			sb.append("}");
		}
		
		return sb.toString();
	}

	public String getRampUpDurationStr() {
		return rampUpDurationStr;
	}

	public void setRampUpDurationStr(String rampUpDurationStr) {
		this.rampUpDurationStr = rampUpDurationStr;
	}

	public String getOrignlSalesOrdRefNum() {
		return orignlSalesOrdRefNum;
	}

	public void setOrignlSalesOrdRefNum(String orignlSalesOrdRefNum) {
		this.orignlSalesOrdRefNum = orignlSalesOrdRefNum;
	}

	public String getOrignlConfigrtnId() {
		return orignlConfigrtnId;
	}

	public void setOrignlConfigrtnId(String orignlConfigrtnId) {
		this.orignlConfigrtnId = orignlConfigrtnId;
	}

	public Date getEarlyRenewalCompDate() {
		return earlyRenewalCompDate;
	}

	public void setEarlyRenewalCompDate(Date earlyRenewalCompDate) {
		this.earlyRenewalCompDate = earlyRenewalCompDate;
	}

	public Date getCotermEndDate() {
		return cotermEndDate;
	}

	public void setCotermEndDate(Date cotermEndDate) {
		this.cotermEndDate = cotermEndDate;
	}

	public Date getNextRenwlDate() {
		return nextRenwlDate;
	}

	public void setNextRenwlDate(Date nextRenwlDate) {
		this.nextRenwlDate = nextRenwlDate;
	}

	public Integer getRenwlCounter() {
		return renwlCounter;
	}

	public void setRenwlCounter(Integer renwlCounter) {
		this.renwlCounter = renwlCounter;
	}

	public String getSapBillgFrqncyOptCode() {
		return sapBillgFrqncyOptCode;
	}

	public void setSapBillgFrqncyOptCode(String sapBillgFrqncyOptCode) {
		this.sapBillgFrqncyOptCode = sapBillgFrqncyOptCode;
	}

	public Integer getRenwlTermMths() {
		return renwlTermMths;
	}

	public void setRenwlTermMths(Integer renwlTermMths) {
		this.renwlTermMths = renwlTermMths;
	}

	public String getRenwlMdlCode() {
		return renwlMdlCode;
	}

	public void setRenwlMdlCode(String renwlMdlCode) {
		this.renwlMdlCode = renwlMdlCode;
	}

    /**
     * Getter for rand.
     * 
     * @return the rand
     */
    public Long getRand() {
        return this.rand;
    }

    /**
     * Sets the rand.
     * 
     * @param rand the rand to set
     */
    public void setRand(Long rand) {
        this.rand = rand;
    }

	public String getBillgEvtFlag() {
		return billgEvtFlag;
	}

	public void setBillgEvtFlag(String billgEvtFlag) {
		this.billgEvtFlag = billgEvtFlag;
	}
	
	public boolean isDivestedPart(){
		return this.divestedPart;
	}
	
	public void setDivestedPart(boolean divestedPart){
		this.divestedPart = divestedPart;
	}

    /**
     * DOC Comment method "setScwItemNumber".
     * 
     * @param itemNumber
     */
    public void setScwItemNumber(String itemNumber) {
        this.scwItemNumber = itemNumber;
    }

    /**
     * Getter for scwItemNumber.
     * 
     * @return the scwItemNumber
     */
    public String getScwItemNumber() {
        return this.scwItemNumber;
    }

	public boolean isRestrictedPart() {
		return restrictedPart;
	}

	public void setRestrictedPart(boolean restrictedPart) {
		this.restrictedPart = restrictedPart;
	}

	public boolean isNeedShow() {
		return needShow;
	}

	public void setNeedShow(boolean needShow) {
		this.needShow = needShow;
	}

	public String getPartCategoryId() {
		return partCategoryId;
	}

	public void setPartCategoryId(String partCategoryId) {
		this.partCategoryId = partCategoryId;
	}

	public boolean isHybridOfferg() {
		return hybridOfferg;
	}

	public void setHybridOfferg(boolean hybridOfferg) {
		this.hybridOfferg = hybridOfferg;
	}
}
