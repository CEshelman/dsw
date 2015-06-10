package com.ibm.dsw.quote.scw.userlookup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ScwUserLookupCAResult {
	
	//sap_sales_ord_num
	@XmlElement(name="CANum")
	private String chargeAgreement;
	
	@XmlElement(name="custNum")
	private String custNum;
	
	@XmlElement(name="contractNo")
	private String contractNo;
	
	@XmlElement(name="paymentType")
	private String paymentType;
	
	@XmlElement(name="profileID")
	private String profileID;
	
	@XmlElement(name="userAssociatedWithProfile")
	private String userAssociatedWithProfile;
	
	@XmlElement(name="purchaseOrderNumber")
	private String purchaseOrderNumber;

	@XmlElement(name="payer")
	private Payer payer;
	
	@XmlElement(name="cc_holder")
	private CCHolder holder;


	public void setHolder (CCHolder holder) {
		this.holder = holder;
	}
	
	public void setPayer (Payer payer) {
		this.payer = payer;
	}

	//	public String getChargeAgreement() {
	//	return chargeAgreement;
	//}

	public void setChargeAgreement(String chargeAgreement) {
		this.chargeAgreement = chargeAgreement;
	}

	//public String getPaymentType() {
	//	return paymentType;
	//}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	//	public String getProfileID() {
	//	return profileID;
	//}

	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}

	//	public boolean getUserAssociatedWithProfile() {
	//	return userAssociatedWithProfile;
	//}

	public void setUserAssociatedWithProfile(String userAssociatedWithProfile) {
		this.userAssociatedWithProfile = userAssociatedWithProfile;
	}

	//public String getPurchaseOrderNumber() {
	//	return purchaseOrderNumber;
	//}

	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}

	//public String getSoldToCustNum() {
	//	return custNum;
	//}

	public void setSoldToCustNum(String soldToCustNum) {
		this.custNum = soldToCustNum;
	}

	//public String getSapCtrctNum() {
	//	return contractNo;
	//}

	public void setSapCtrctNum(String sapCtrctNum) {
		this.contractNo = sapCtrctNum;
	}
}
