package com.ibm.dsw.quote.scw.userlookup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ScwUserLookupConfigDetailResult { 

	@XmlElement(name="partNum")
	private String partNum;
	
	@XmlElement(name="custNum")
	private String custNum;
	
	@XmlElement(name="contractNo")
	private String contractNo;
	
	@XmlElement(name="CANum")
	private String chargeAgreement;
	
	@XmlElement(name="configID")
	private String configrtnID;
		
	@XmlElement(name="lineItemSeqNum")
	private Integer lineItemSeqNum;
	
	@XmlElement(name="partQty")
	private Double partQty;
	
	@XmlElement(name="sapBillingFrequencyOptCode")
	private String sapBillingFrequencyOptCode;
	
	@XmlElement(name="coverageTerm")
	private Integer coverageTerm;
	
	@XmlElement(name="remainingTerm")
	private Integer remainingTerm;
	
	@XmlElement(name="renewModelCode")
	private String renewModelCode;
	
	@XmlElement(name="saasTotalCommitmentValue")
	private Double saasTotalCommitmentValue;
	
	@XmlElement(name="partEndDate")
	private String endDate;
	
	@XmlElement(name="currentPrice")
	private String currentPrice;
	
    private boolean rampUp;

	//	public Integer getLineItemSeqNum() {
	//	return lineItemSeqNum;
	//}

	public void setLineItemSeqNum(Integer lineItemSeqNum) {
		this.lineItemSeqNum = lineItemSeqNum;
	}

	//public String getPartNum() {
	//	return partNum;
	//}

	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}

	//public Double getPartQty() {
	//	return partQty;
	//}

	public void setPartQty(Double partQty) {
		this.partQty = partQty;
	}

	//public String getSapBillingFrequencyOptCode() {
	//	return sapBillingFrequencyOptCode;
	//}

	public void setSapBillingFrequencyOptCode(String sapBillingFrequencyOptCode) {
		this.sapBillingFrequencyOptCode = sapBillingFrequencyOptCode;
	}

	//public Integer getCoverageTerm() {
	//	return coverageTerm;
	//}

	public void setCoverageTerm(Integer coverageTerm) {
		this.coverageTerm = coverageTerm;
	}
		
	//public Integer getRemainingTerm() {
	//	return remainingTerm;
	//}

	public void setRemainingTerm(Integer remainingTerm) {
		this.remainingTerm = remainingTerm;
	}

	//public String getRenewModelCode() {
	//	return renewModelCode;
	//}

	public void setRenewModelCode(String renewModelCode) {
		this.renewModelCode = renewModelCode;
	}

	//public Double getSaasTotalCommitmentValue() {
	//	return saasTotalCommitmentValue;
	//}

	public void setSaasTotalCommitmentValue(Double saasTotalCommitmentValue) {
		this.saasTotalCommitmentValue = saasTotalCommitmentValue;
	}

	//public String getEndDate() {
	//	return endDate;
	//}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
		
	}

	//public String getSoldToCustNum() {
	//	return custNum;
	//}

	public void setSoldToCustNum(String soldToCustNum) {
		this.custNum = soldToCustNum;
	}

	//public String getSapSalesOrdNum() {
	//	return chargeAgreement;
	//}

	public void setSapSalesOrdNum(String sapSalesOrdNum) {
		this.chargeAgreement = sapSalesOrdNum;
	}

	//public String getSapCtrctNum() {
	//	return contractNo;
	//}

	public void setSapCtrctNum(String sapCtrctNum) {
		this.contractNo = sapCtrctNum;
	}

	public String getConfigrtnID() {
		return configrtnID;
	}

	public void setConfigrtnID(String configrtnID) {
		this.configrtnID = configrtnID;
	}

	//public String getCurrentPrice() {
	//	return currentPrice;
	//}

	public void setCurrentPrice(String currentPrice) {
		this.currentPrice = currentPrice;
	}

    /**
     * Getter for chargeAgreement.
     * 
     * @return the chargeAgreement
     */
    public String getChargeAgreement() {
        return this.chargeAgreement;
    }

    /**
     * Getter for partNum.
     * 
     * @return the partNum
     */
    public String getPartNum() {
        return this.partNum;
    }

    /**
     * Getter for renewModelCode.
     * 
     * @return the renewModelCode
     */
    public String getRenewModelCode() {
        return this.renewModelCode;
    }

    /**
     * Getter for rampUp.
     * 
     * @return the rampUp
     */
    public boolean isRampUp() {
        return this.rampUp;
    }

    /**
     * Sets the rampUp.
     * 
     * @param rampUp the rampUp to set
     */
    public void setRampUp(boolean rampUp) {
        this.rampUp = rampUp;
    }

    /**
     * Getter for endDate.
     * 
     * @return the endDate
     */
    public String getEndDate() {
        return this.endDate;
    }

}
