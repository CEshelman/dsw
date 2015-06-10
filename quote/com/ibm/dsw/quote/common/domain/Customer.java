package com.ibm.dsw.quote.common.domain;

import java.util.List;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>Customer<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public interface Customer {

    public String getCustNum();

    public String getIbmCustNum();

    public String getCustName();

    public String getAddress1();

    public String getInternalAddress();

    public String getCity();    

    public String getState();

    public String getSapRegionCodeDscr();

    public String getPostalCode();

    public String getCountryCode();
    
    public String getSapContractNum();

    public String getCntEmailAdr();

    public String getCntFaxNumFull();

    public String getCntFirstName();

    public String getCntLastName();

    public String getCntPhoneNumFull();

    public String getCurrencyCode();

    public String getCustVatNum();

    public int getEmpTot();

    public String getIsoLangCode();

    public String getMktgEmailFlag();

    public int getSapCntId();

    public String getSapIntlPhoneNumFull();

    public String getSapWwIsuCode();

    public String getTempAccessNum();

    public String getUpgrLangCode();

    public int getWebCustId();

    public String getWebCustStatCode();

    public String getWebCustTypeCode();

    public String getSapRegionCode();
    
    public String getSapCntPrtnrFuncCode();
    
    public boolean getGsaStatusFlag();
    
    public List getContractList();
    
    public List getEnrolledCtrctList();
    
    public List getCsaContractList();
    
    public List getNoCsaContractList();
    
    public int getCtrctHasPreApprPrcLvlFlg();
    
    public String getPurchOrdReqrdFlag();

    public String getCustName2();

    public String getCustName3();
    
    public String getSalesOrg();
    
    public List getRdcNumList();

    public void setCustNum(String custNum);
    
    public boolean hasContract();
    
    public boolean hasActiveContract();

    public String getCustDesignation();
    
    public String getCustSetDesCode();
    
    public String getCustSetDesCode2();
    
    public String getAuthrztnGroup();
    
    public String getAgreementType();
    
    public String getTransSVPLevel();
    
    public boolean isPAWebCustomer();
    
    public boolean isAddiSiteCustomer();
    
    public boolean isACACustomer();
    
    public boolean isGOVCustomer();
    
    public boolean isSTDCustomer();
    
    public boolean isXSPCustomer();
    
    public int getPAIndCode();
    
    public boolean isActivePACust();
    
    public boolean isInactivePACust();
    
    public boolean isEnrolledCtrctTranPrcLvlValid();
    
    public boolean isEnrolledCtrctNumValid();
    
    public int getGovEntityIndCode();
    
    public String getGovEntityIndCodeDesc();
    
    public int getSupprsPARegstrnEmailFlag();
    
    public boolean isCustOrdBlcked();
    
    public String getCvrageGrpCodeDesc();
    
    public String getCustCvrageDesc();
    
    public String getInitWebAppBusDscr();
    
    public String getCurrWebAppBusDscr();
    
    public void setCustDesignation(String custDesignation)throws TopazException;
    
    public void setCustSetDesCode(String custSetDesCode) throws TopazException;
    
    public void setCustSetDesCode2(String custSetDesCode2) throws TopazException;
    
    public void setIbmCustNum(String ibmCustNum)throws TopazException;

    public void setCustName(String custNum)throws TopazException;

    public void setAddress1(String address1)throws TopazException;

    public void setInternalAddress(String internalAddress)throws TopazException;

    public void setCity(String city)throws TopazException;

    public void setSapRegionCodeDscr(String sapReignCodeDscr)throws TopazException;

    public void setPostalCode(String postalCode)throws TopazException;

    public void setCountryCode(String countryCode)throws TopazException;

    public void setSapContractNum(String contractNum)throws TopazException;
    
    public void setCntEmailAdr(String cntEmailAdr)throws TopazException;

    public void setCntFaxNumFull(String cntFaxNumFull)throws TopazException;

    public void setCntFirstName(String cntFirstName)throws TopazException;

    public void setCntLastName(String cntListName)throws TopazException;

    public void setCntPhoneNumFull(String cntPhoneNumFull)throws TopazException;

    public void setCurrencyCode(String currencyCode)throws TopazException;

    public void setCustVatNum(String custCatNum)throws TopazException;

    public void setEmpTot(int empTot)throws TopazException;

    public void setIsoLangCode(String isoLangCode)throws TopazException;

    public void setMktgEmailFlag(String mktgEmailFlag)throws TopazException;

    public void setSapCntId(int sapCntId);

    public void setSapIntlPhoneNumFull(String sapIntlPhoneNumFull) throws TopazException;

    public void setSapWwIsuCode(String sapWwIsuCode) throws TopazException;

    public void setTempAccessNum(String tempAccessNum);

    public void setUpgrLangCode(String upgrLangCode) throws TopazException;

    public void setWebCustId(int webCustId) throws TopazException;

    public void setWebCustStatCode(String webCustStatCode) throws TopazException;

    public void setWebCustTypeCode(String webTypeCode) throws TopazException;

    public void setSapRegionCode(String sapRegionCode) throws TopazException;
    
    public void setSapCntPrtnrFuncCode(String sapCntPrtnrFuncCode) throws TopazException;
    
    public void setGsaStatusFlag(boolean gsaStatusFlag) throws TopazException;
    
    public void setContractList(List contractList) throws TopazException;
    
    public void setRdcNumberList(List rdcNumList) throws TopazException;
    
    public void setAuthrztnGroup(String authrztnGroup) throws TopazException;
    
    public void setAgreementType(String sapCtrctVariantCode) throws TopazException;
    
    public void setTransSVPLevel(String volDiscLevelCode) throws TopazException;
    
    public void setGovEntityIndCode(int govEntityIndCode)throws TopazException;
    
    public void setGovEntityIndCodeDesc(String govEntityIndCodeDesc)throws TopazException;

    public void setSupprsPARegstrnEmailFlag(int supprsPARegstrnEmailFlag);
    
    public void setCustOrdBlcked(boolean flag);
    
    public void setCvrageGrpCodeDesc(String cvrageGrpCodeDesc)throws TopazException;
    
    public void setCustCvrageDesc(String custCvrageDesc)throws TopazException;
    
    public void setInitWebAppBusDscr(String initWebAppBusDscr)throws TopazException;
    
    public void setCurrWebAppBusDscr(String currWebAppBusDscr)throws TopazException;
    
    public List getCustomerGroupList();
    
    public void setCustomerGroupList(List customerGroupList);
    
    public void setRelatedwithBPFlag(boolean isRelatedFlag);
    
    public boolean isRelatedwithBPFlag();
    
    public String getCustomerType();
    
    public void setCustomerType(String customerType);
    
    public String getAddressType();
    
    public void setAddressType(String addressType);
    
    public void setCustName2(String custName2);
    
	public boolean isBPRelatedCust();

	public void setBPRelatedCust(boolean isBPRelatedCust);
	
	public boolean isCustOnlySSP();
	
	public boolean isGOECust();
	
	public boolean isMthlySwTermsAccptd();

	public void setMthlySwTermsAccptd(boolean isMthlySwTermsAccptd);
}
