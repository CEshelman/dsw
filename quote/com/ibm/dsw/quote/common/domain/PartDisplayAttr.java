package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.sql.Date;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.util.QuoteCommonUtil;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This
 * <code>PartDisplayAttr<code> class is to define the attribute for dispalying in page
 * There are four reasons that make a maint part to unrelated:
 * (1) no pa customer
 * (2) no associated license part
 * (3) summary of license qty doesn't match maint part quantity
 * (4) same parts appeiars more than once
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Mar 29, 2007
 */

public class PartDisplayAttr implements Serializable {

    //isRelatedMaint and isUnRelatedMaint is determined as subscription id and quantity match
    //Together with continuous coverage
    private boolean isRelatedMaint;

    private boolean isUnRelatedMaint;

    private int relatedItemSeqNum = 0;

    private boolean isFromRQ;

    private String renwlChgCode;

    private String renwlQuoteNum;

    //Below methods  describe  the reason that a maint part become a unrelated part
    private boolean isLicensePartExist = false;
    private boolean isQtyMatch = false;
    private int licensePartTotalQty = 0;  // if isLicensePartExist = true, this field is reasonable

    // for license/contract/renewal part
    private Date maintStartDate = null;
    private Date maintEndDate = null;

    //for licence, support, contract back dating
    private Date calEndDate = null;
    // for maint part

    private Date proEndDate = null;
    private Date nonProEndDate = null;

    private String revnStrmCode;

    private transient QuoteLineItem lineItem;

    //Below two attributes are determined as subscription id and quantity match
    //It is used to adjust maint part start date to one day after the associated license part end date
    private boolean associateMaintPart;
    private boolean unAssociateMaintPart;

    public PartDisplayAttr(QuoteLineItem item) {

        String code = item.getRevnStrmCode();
        if (null != code) {
            code = code.trim();
        }
        this.revnStrmCode = code;
        this.lineItem = item;
        String renewalQuoteNum = item.getRenewalQuoteNum();
        this.isFromRQ = (null != renewalQuoteNum) && !"".equals(renewalQuoteNum.trim());


    }

    public Date getStdStartDate(){
        return getStdStartDate(false);
    }
    
    public Date getStdStartDateForExport(){
    	if(lineItem.isApplncPart() && !QuoteCommonUtil.isShowDatesForApplnc(lineItem)){
    		return null;
    	}else{
    		return getStdStartDate(false);
    	}
    }

    public Date getStdStartDate(boolean applyCmprssCvrage){
    	
        if (!isMaintBehavior()) 
            return this.maintStartDate;
        else  {
            if(StringUtils.isNotBlank(lineItem.getRenewalQuoteNum())){
                if(applyCmprssCvrage)
                    return this.maintStartDate;
                else 
                    return lineItem.getMaintStartDate();
                
            }
            else
                return this.maintStartDate;
        }
    }
    

    public Date getStdEndDate(){
        if (!isMaintBehavior()) {
            return this.maintEndDate;
        } 
        else  {
            if(StringUtils.isNotBlank(lineItem.getRenewalQuoteNum())){
                return lineItem.getMaintEndDate();
            }
            else{
                if(lineItem.getProrateFlag()){
                    return this.proEndDate;
                }
                else{
                    return this.nonProEndDate;
                }
            }


        }
    }
    
    public Date getStdEndDateForExport(){
    	if(lineItem.isApplncPart() && !QuoteCommonUtil.isShowDatesForApplnc(lineItem)){
    		return null;
    	}else{
    		return getStdEndDate();
    	}
    }

    public void fillMaintDate(Date startDate, Date proEndDate, Date nonProEndDate) {
        this.maintStartDate = startDate;
        this.proEndDate = proEndDate;
        this.nonProEndDate = nonProEndDate;

    }

    public void fillMaintDate(Date startDate,Date endDate){
        this.maintStartDate = startDate;
        this.maintEndDate = endDate;
    }

    public void markRelatedMaint(int relatedItemSeqNum) {
        this.isRelatedMaint = true;
        this.isUnRelatedMaint = false;
        this.relatedItemSeqNum = relatedItemSeqNum;
    }

    public void markUnRelatedMaint() {
        this.isUnRelatedMaint = true;
        this.isRelatedMaint = false;
        this.relatedItemSeqNum = 0;
    }

    public boolean isFtlPart() {
        return PartPriceConfigFactory.singleton().isFTLPart(revnStrmCode);
    }

    public boolean isSubsFtl() {
        return PartPriceConfigFactory.singleton().isSubFTLPart(revnStrmCode);
    }
    public boolean isSWMaintRenewal(){
        return this.isMaintBehavior() && ! this.isSubsFtl();  // this means it's normal maintenance part
    }
    public boolean isInitFtl() {

        return PartPriceConfigFactory.singleton().isInitFTLPart(revnStrmCode);
    }

    public boolean isOtherContract() {
        return PartPriceConfigFactory.singleton().isOtherContractPart(revnStrmCode);
    }



    public boolean isMaintBehavior() {
    	 return isMaint() || isApplncMaint();
    }

    public boolean isMaint(){
    	return PartPriceConfigFactory.singleton().isMaintenancePart(revnStrmCode);
    }

    /**
     * Some appliance part behavior like maint part.
     * @return
     */
    private boolean isApplncMaint(){
    	return lineItem.isApplncServicePackRenewal() || lineItem.isApplncRenewal();
    }


    public boolean isLicense(){
    	return PartPriceConfigFactory.singleton().isLicensePart(revnStrmCode);
    }

    /**
     * Some appliance part behavior like license part.
     * @return
     */
    private boolean isApplncLicense(){	
    	return lineItem.isApplncMain() || lineItem.isApplncServicePack();
    }

    /**
     * @return Returns the license.
     */
    public boolean isLicenseBehavior() {
        return isLicense() || isApplncLicense();

    }

    public boolean isTrmlsssr_OSSupt(){
        return  PartPriceConfigFactory.singleton().isTrmlsssr_OSSupt_Part(lineItem.getRevnStrmCode());
    }

    /**
     * @return Returns the license.
     */
    public boolean isSupport() {
        return PartPriceConfigFactory.singleton().isSupportPart(revnStrmCode);

    }

    /**
     * @return Returns the isRelatedMaint.
     */
    public boolean isRelatedMaint() {
        return isRelatedMaint;
    }

    /**
     * @param isRelatedMaint
     *            The isRelatedMaint to set.
     */
    public void setRelatedMaint(boolean isRelatedMaint) {
        this.isRelatedMaint = isRelatedMaint;
    }

    /**
     * @return Returns the isUnRelatedMaint.
     */
    public boolean isUnRelatedMaint() {
        return isUnRelatedMaint;
    }

    /**
     * @param isUnRelatedMaint
     *            The isUnRelatedMaint to set.
     */
    public void setUnRelatedMaint(boolean isUnRelatedMaint) {
        this.isUnRelatedMaint = isUnRelatedMaint;

    }

    /**
     * @return Returns the isRenewal.
     */
    public boolean isFromRQ() {
        return isFromRQ;
    }

    public Date getMaintStartDate() {

        return this.maintStartDate;
    }
    public Date getMaintEndDate(){
        return this.maintEndDate;
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("isLicense=" + this.isLicenseBehavior()).append("\n");
        buffer.append("isOtherContract=" + this.isOtherContract()).append("\n");
        buffer.append("isInitFtl=" + this.isInitFtl()).append("\n");
        buffer.append("isRelatedMaint=" + isRelatedMaint).append("\n");
        buffer.append("isUnRelatedMaint=" + isUnRelatedMaint).append("\n");
        buffer.append("associateMaintPart=" + associateMaintPart).append("\n");
        buffer.append("unAssociateMaintPart=" + unAssociateMaintPart).append("\n");

        if(this.isUnRelatedMaint){
            buffer.append("\tun-related part reason:"  ).append("\n");
            buffer.append("\t\t isLicensePartExist:" + this.isLicensePartExist  ).append("\n");
            buffer.append("\t\t isQtyMatch:" + this.isQtyMatch  ).append("\n");
        }
        buffer.append("isFromRQ=" + isFromRQ).append("\n");


        buffer.append("License/Contract/Renewal Part Maint start date = "+this.maintStartDate).append("\n");
        buffer.append("License/Contract/Renewal Part Maint end date = "+this.maintEndDate).append("\n");
        buffer.append("revenue stream code = "+revnStrmCode).append("\n");

        return buffer.toString();
    }

    /**
     * @return Returns the isQtyMatch.
     */
    public boolean isQtyMatch() {
        return isQtyMatch;
    }

    /**
     * @return Returns the renwlChgCode.
     */
    public String getRenwlChgCode() {
        return renwlChgCode;
    }
    /**
     * @param renwlChgCode The renwlChgCode to set.
     */
    public void setRenwlChgCode(String renwlChgCode) {
        this.renwlChgCode = renwlChgCode;
    }
    /**
     * @return Returns the isLicensePartExist.
     */
    public boolean isLicensePartExist() {
        return isLicensePartExist;
    }
    /**
     * @return Returns the licensePartTotalQty.
     */
    public int getLicensePartTotalQty() {
        return licensePartTotalQty;
    }
    /**
     * @return Returns the nonProEndDate.
     */
    public Date getNonProEndDate() {
        return nonProEndDate;
    }
    /**
     * @return Returns the proEndDate.
     */
    public Date getProEndDate() {
        return proEndDate;
    }
    /**
     * @return Returns the renwlQuoteNum.
     */
    public String getRenwlQuoteNum() {
        return renwlQuoteNum;
    }
    /**
     * @param renwlQuoteNum The renwlQuoteNum to set.
     */
    public void setRenwlQuoteNum(String renwlQuoteNum) {
        this.renwlQuoteNum = renwlQuoteNum;
    }
	/**
	 * @return Returns the calEndDate.
	 */
	public Date getCalEndDate() {
		return calEndDate;
	}
	/**
	 * @param calEndDate The calEndDate to set.
	 */
	public void setCalEndDate(Date calEndDate) {
		this.calEndDate = calEndDate;
	}
    /**
     * @return Returns the needAutoAdjustDates.
     */
    public boolean isAssociatedMaintPart() {
        return associateMaintPart;
    }
    /**
     * @param needAutoAdjustDates The needAutoAdjustDates to set.
     */
    public void markAssociatedMaintPart() {
        this.associateMaintPart = true;
    }

    public void markLicensePartExistQtyNotMatch(int licensePartTotalQty){
        this.licensePartTotalQty = licensePartTotalQty;
        this.isQtyMatch = false;
        this.isLicensePartExist = true;
    }

    public void markLicensePartNotExist(){
        this.licensePartTotalQty = 0;
        this.isQtyMatch = false;
        this.isLicensePartExist = false;
    }

    public void markLicensePartExistQtyMatch(int licensePartTotalQty){
        this.licensePartTotalQty = licensePartTotalQty;
        this.isQtyMatch = true;
        this.isLicensePartExist = true;
    }
    /**
     * @return Returns the noNeedAutoAdjustDates.
     */
    public boolean isUnAssociateMaintPart() {
        return unAssociateMaintPart;
    }
    /**
     * @param noNeedAutoAdjustDates The noNeedAutoAdjustDates to set.
     */
    public void markUnAssociateMaintPart() {
        this.unAssociateMaintPart = true;
    }

	public boolean isSysCalEndDate(){
	    return PartPriceConfigFactory.singleton().isSysCalEndDate(revnStrmCode)
	    	|| isApplncLicense();
	}
    public int getRelatedItemSeqNum() {
        return relatedItemSeqNum;
    }

    public boolean shouldNotAutoAdjustDates(){
    	return PartPriceConfigFactory.singleton().isRenewalLicensePart(revnStrmCode)
    	          && isFromRQ();
    }

}
