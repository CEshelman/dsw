package com.ibm.dsw.quote.scw.addon.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name="lineItem")
@XmlAccessorType (XmlAccessType.FIELD)
public class RetrieveLineItem {

	private String lineItemNum;
	private String referringDocType;
	private String referringDocNumber;
	private String refDocLineItem;
	private String partNumber;
	private String quantity;
	private String itemStartDate;
	private String itemEndDate;
	private String currencyCode;
	private String billingOption;
	private String termValue;
	private String originalItem;
	private String replaceFlag;
	private String configId;
	private String addReasonCode;
	private String replacedReasonCode;
	private String newConfigFlag;
	private String originatingItemNumber;
	private String doNotOrderFlag;
	private String replacedTerm;
	private String earlyRnwlCompDate;
	private String renewType;
	private String extensionEligibilityDate;
	private String touUrlName;
	private String touUrl;
	private String subsumedSubscription;
	private Integer cotermLineSeqNum; 
	private String relatedItemNum;
	

	public String getLineItemNum() {
		return lineItemNum;
	}

	public void setLineItemNum(String lineItemNum) {
		if (StringUtils.isBlank(lineItemNum)) {
			return;
		}
		this.lineItemNum = lineItemNum;
	}

	public String getReferringDocType() {
		return referringDocType;
	}

	public void setReferringDocType(String referringDocType) {
		if (StringUtils.isBlank(referringDocType)) {
			return;
		}
		this.referringDocType = referringDocType;
	}

	public String getReferringDocNumber() {
		return referringDocNumber;
	}

	public void setReferringDocNumber(String referringDocNumber) {
		if (StringUtils.isBlank(referringDocNumber)) {
			return;
		}
		this.referringDocNumber = referringDocNumber;
	}

	public String getRefDocLineItem() {
		return refDocLineItem;
	}

	public void setRefDocLineItem(String refDocLineItem) {
		if (StringUtils.isBlank(refDocLineItem)) {
			return;
		}
		this.refDocLineItem = refDocLineItem;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		if (StringUtils.isBlank(partNumber)) {
			return;
		}
		this.partNumber = partNumber;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		if (StringUtils.isBlank(quantity)) {
			return;
		}
		this.quantity = quantity;
	}

	public String getItemStartDate() {
		return itemStartDate;
	}

	public void setItemStartDate(String itemStartDate) {
		if (StringUtils.isBlank(itemStartDate)) {
			return;
		}
		this.itemStartDate = itemStartDate;
	}

	public String getItemEndDate() {
		return itemEndDate;
	}

	public void setItemEndDate(String itemEndDate) {
		if (StringUtils.isBlank(itemEndDate)) {
			return;
		}
		this.itemEndDate = itemEndDate;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		if (StringUtils.isBlank(currencyCode)) {
			return;
		}
		this.currencyCode = currencyCode;
	}

	public String getBillingOption() {
		return billingOption;
	}

	public void setBillingOption(String billingOption) {
		if (StringUtils.isBlank(billingOption)) {
			return;
		}
		this.billingOption = billingOption;
	}

	public String getTermValue() {
		return termValue;
	}

	public void setTermValue(String termValue) {
		if (StringUtils.isBlank(termValue)) {
			return;
		}
		this.termValue = termValue;
	}

	public String getReplaceFlag() {
		return replaceFlag;
	}

	public void setReplaceFlag(String replaceFlag) {
		if (StringUtils.isBlank(replaceFlag)) {
			return;
		}
		this.replaceFlag = replaceFlag;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		if (StringUtils.isBlank(configId)) {
			return;
		}
		this.configId = configId;
	}

	public String getAddReasonCode() {
		return addReasonCode;
	}

	public void setAddReasonCode(String addReasonCode) {
		if (StringUtils.isBlank(addReasonCode)) {
			return;
		}
		this.addReasonCode = addReasonCode;
	}

	public String getReplacedReasonCode() {
		return replacedReasonCode;
	}

	public void setReplacedReasonCode(String replacedReasonCode) {
		if (StringUtils.isBlank(replacedReasonCode)) {
			return;
		}
		this.replacedReasonCode = replacedReasonCode;
	}

	public String getNewConfigFlag() {
		return newConfigFlag;
	}

	public void setNewConfigFlag(String newConfigFlag) {
		if (StringUtils.isBlank(newConfigFlag)) {
			return;
		}
		this.newConfigFlag = newConfigFlag;
	}

	public String getOriginatingItemNumber() {
		return originatingItemNumber;
	}

	public void setOriginatingItemNumber(String originatingItemNumber) {
		if (StringUtils.isBlank(originatingItemNumber)) {
			return;
		}
		this.originatingItemNumber = originatingItemNumber;
	}

	public String getDoNotOrderFlag() {
		return doNotOrderFlag;
	}

	public void setDoNotOrderFlag(String doNotOrderFlag) {
		if (StringUtils.isBlank(doNotOrderFlag)) {
			return;
		}
		this.doNotOrderFlag = doNotOrderFlag;
	}

	public String getReplacedTerm() {
		return replacedTerm;
	}

	public void setReplacedTerm(String replacedTerm) {
		if (StringUtils.isBlank(replacedTerm)) {
			return;
		}
		this.replacedTerm = replacedTerm;
	}

	public String getEarlyRnwlCompDate() {
		return earlyRnwlCompDate;
	}

	public void setEarlyRnwlCompDate(String earlyRnwlCompDate) {
		if (StringUtils.isBlank(earlyRnwlCompDate)) {
			return;
		}
		this.earlyRnwlCompDate = earlyRnwlCompDate;
	}

	public String getRenewType() {
		return renewType;
	}

	public void setRenewType(String renewType) {
		if (StringUtils.isBlank(renewType)) {
			return;
		}
		this.renewType = renewType;
	}

	public String getExtensionEligibilityDate() {
		return extensionEligibilityDate;
	}

	public void setExtensionEligibilityDate(String extensionEligibilityDate) {
		if (StringUtils.isBlank(extensionEligibilityDate)) {
			return;
		}
		this.extensionEligibilityDate = extensionEligibilityDate;
	}

	public String getTouUrlName() {
		return touUrlName;
	}

	public void setTouUrlName(String touUrlName) {
		if (StringUtils.isBlank(touUrlName)) {
			return;
		}
		this.touUrlName = touUrlName;
	}

	public String getTouUrl() {
		return touUrl;
	}

	public void setTouUrl(String touUrl) {
		if (StringUtils.isBlank(touUrl)) {
			return;
		}
		this.touUrl = touUrl;
	}

	public String getOriginalItem() {
		return originalItem;
	}

	public void setOriginalItem(String originalItem) {
		if (StringUtils.isBlank(originalItem)) {
			return;
		}
		this.originalItem = originalItem;
	}

	public String getSubsumedSubscription() {
		return subsumedSubscription;
	}

	public void setSubsumedSubscription(String subsumedSubscription) {
		if (StringUtils.isBlank(subsumedSubscription)) {
			return;
		}
		this.subsumedSubscription = subsumedSubscription;
	}

	public Integer getCotermLineSeqNum() {
		return cotermLineSeqNum;
	}

	public void setCotermLineSeqNum(Integer cotermLineSeqNum) {
		this.cotermLineSeqNum = cotermLineSeqNum;
	}

	public String getRelatedItemNum() {
		return relatedItemNum;
	}

	public void setRelatedItemNum(String relatedItemNum) {
		this.relatedItemNum = relatedItemNum;
	}
	
}
