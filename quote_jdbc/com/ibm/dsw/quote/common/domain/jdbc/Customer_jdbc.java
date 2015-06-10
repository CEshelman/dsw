package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>Customer_jdbc<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public class Customer_jdbc extends Customer_Impl implements PersistentObject, Serializable {
    
    transient CustomerPersister persister;
    
    private transient List customerGroupList = null;
    
    /**
     *  
     */
    public Customer_jdbc() {
        super();
        persister = new CustomerPersister(this);
    }

    /**
     *  
     */
    public Customer_jdbc(String customerNum) {
        super();
        this.custNum = customerNum;
        persister = new CustomerPersister(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        // don't need to delete a customer
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
    }


    /**
     * @param address1 The address1 to set.
     */
    public void setAddress1(String address1) throws TopazException {
        this.address1 = address1;
        persister.setDirty();
    }
    /**
     * @param city The city to set.
     */
    public void setCity(String city) throws TopazException {
        this.city = city;
        persister.setDirty();
    }
    /**
     * @param cntEmailAdr The cntEmailAdr to set.
     */
    public void setCntEmailAdr(String cntEmailAdr) throws TopazException {
        this.cntEmailAdr = cntEmailAdr;
        persister.setDirty();
    }
    /**
     * @param cntFaxNumFull The cntFaxNumFull to set.
     */
    public void setCntFaxNumFull(String cntFaxNumFull) throws TopazException {
        this.cntFaxNumFull = cntFaxNumFull;
        persister.setDirty();
    }
    /**
     * @param cntFirstName The cntFirstName to set.
     */
    public void setCntFirstName(String cntFirstName) throws TopazException {
        this.cntFirstName = cntFirstName;
        persister.setDirty();
    }
    /**
     * @param cntLastName The cntLastName to set.
     */
    public void setCntLastName(String cntLastName) throws TopazException {
        this.cntLastName = cntLastName;
        persister.setDirty();
    }
    /**
     * @param cntPhoneNumFull The cntPhoneNumFull to set.
     */
    public void setCntPhoneNumFull(String cntPhoneNumFull) throws TopazException {
        this.cntPhoneNumFull = cntPhoneNumFull;
        persister.setDirty();
    }
    /**
     * @param countryCode The countryCode to set.
     * @throws TopazException
     */
    public void setCountryCode(String countryCode) throws TopazException {
        this.countryCode = countryCode;
        persister.setDirty();
    }
    /**
     * @param currencyCode The currencyCode to set.
     * @throws TopazException
     */
    public void setCurrencyCode(String currencyCode) throws TopazException {
        this.currencyCode = currencyCode;
        persister.setDirty();
    }
    /**
     * @param custName The custName to set.
     * @throws TopazException
     */
    public void setCustName(String custName) throws TopazException {
        this.custName = custName;
        persister.setDirty();
    }
    /**
     * @param custNum The custNum to set.
     * @throws TopazException
     */
    public void setCustNum(String custNum){
        this.custNum = custNum;
    }
    /**
     * @param custVatNum The custVatNum to set.
     * @throws TopazException
     */
    public void setCustVatNum(String custVatNum) throws TopazException {
        this.custVatNum = custVatNum;
        persister.setDirty();
    }
    /**
     * @param empTot The empTot to set.
     * @throws TopazException
     */
    public void setEmpTot(int empTot) throws TopazException {
        this.empTot = empTot;
        persister.setDirty();
    }
    /**
     * @param ibmCustNum The ibmCustNum to set.
     * @throws TopazException
     */
    public void setIbmCustNum(String ibmCustNum) throws TopazException {
        this.ibmCustNum = ibmCustNum;
        persister.setDirty();
    }
    /**
     * @param internalAddress The internalAddress to set.
     * @throws TopazException
     */
    public void setInternalAddress(String internalAddress) throws TopazException {
        this.internalAddress = internalAddress;
        persister.setDirty();
    }
    /**
     * @param isoLangCode The isoLangCode to set.
     * @throws TopazException
     */
    public void setIsoLangCode(String isoLangCode) throws TopazException {
        this.isoLangCode = isoLangCode;
        persister.setDirty();
    }
    /**
     * @param mktgEmailFlag The mktgEmailFlag to set.
     * @throws TopazException
     */
    public void setMktgEmailFlag(String mktgEmailFlag) throws TopazException {
        this.mktgEmailFlag = mktgEmailFlag;
        persister.setDirty();
    }
    /**
     * @param postalCode The postalCode to set.
     * @throws TopazException
     */
    public void setPostalCode(String postalCode) throws TopazException {
        this.postalCode = postalCode;
        persister.setDirty();
    }
    /**
     * @param sapCntId The sapCntId to set.
     * @throws TopazException
     */
    public void setSapCntId(int sapCntId) {
        this.sapCntId = sapCntId;
    }
    /**
     * @param sapContractNum The sapContractNum to set.
     * @throws TopazException
     */
    public void setSapContractNum(String sapContractNum) throws TopazException {
        this.sapContractNum = sapContractNum;
        persister.setDirty();
    }
    /**
     * @param sapIntlPhoneNumFull The sapIntlPhoneNumFull to set.
     * @throws TopazException
     */
    public void setSapIntlPhoneNumFull(String sapIntlPhoneNumFull) throws TopazException {
        this.sapIntlPhoneNumFull = sapIntlPhoneNumFull;
        persister.setDirty();
    }
    /**
     * @param sapRegionCode The sapRegionCode to set.
     * @throws TopazException
     */
    public void setSapRegionCode(String sapRegionCode) throws TopazException {
        this.sapRegionCode = sapRegionCode;
        persister.setDirty();
    }
    /**
     * @param sapRegionCodeDscr The sapRegionCodeDscr to set.
     * @throws TopazException
     */
    public void setSapRegionCodeDscr(String sapRegionCodeDscr) throws TopazException {
        this.sapRegionCodeDscr = sapRegionCodeDscr;
        persister.setDirty();
    }
    /**
     * @param sapWwIsuCode The sapWwIsuCode to set.
     * @throws TopazException
     */
    public void setSapWwIsuCode(String sapWwIsuCode) throws TopazException {
        this.sapWwIsuCode = sapWwIsuCode;
        persister.setDirty();
    }
    /**
     * @param tempAccessNum The tempAccessNum to set.
     * @throws TopazException
     */
    public void setTempAccessNum(String tempAccessNum) {
        this.tempAccessNum = tempAccessNum;
    }
    /**
     * @param upgrLangCode The upgrLangCode to set.
     * @throws TopazException
     */
    public void setUpgrLangCode(String upgrLangCode) throws TopazException {
        this.upgrLangCode = upgrLangCode;
        persister.setDirty();
    }
    /**
     * @param webCustId The webCustId to set.
     * @throws TopazException
     */
    public void setWebCustId(int webCustId) throws TopazException {
        this.webCustId = webCustId;
        persister.setDirty();
    }
    /**
     * @param webCustStatCode The webCustStatCode to set.
     * @throws TopazException
     */
    public void setWebCustStatCode(String webCustStatCode) throws TopazException {
        this.webCustStatCode = webCustStatCode;
        persister.setDirty();
    }
    /**
     * @param webCustTypeCode The webCustTypeCode to set.
     * @throws TopazException
     */
    public void setWebCustTypeCode(String webCustTypeCode) throws TopazException {
        this.webCustTypeCode = webCustTypeCode;
        persister.setDirty();
    }
    /**
     * @param sapCntPrtnrFuncCode The sapCntPrtnrFuncCode to set.
     * @throws TopazException
     */
    public void setSapCntPrtnrFuncCode(String sapCntPrtnrFuncCode) throws TopazException {
        this.sapCntPrtnrFuncCode = sapCntPrtnrFuncCode;
        persister.setDirty();
    }
    public void setGsaStatusFlag(boolean gsaStatusFlag) throws TopazException {
        this.gsaStatusFlag = gsaStatusFlag;
        persister.setDirty();
    }
    
    public void setAuthrztnGroup(String authrztnGroup) throws TopazException {
        this.authrztnGroup = authrztnGroup;
        persister.setDirty();
    }
    
    public void setAgreementType(String agreementType) throws TopazException {
        this.agreementType = agreementType;
        persister.setDirty();
    }
    
    public void setTransSVPLevel(String transSVPLevel) throws TopazException {
        this.transSVPLevel = transSVPLevel;
        persister.setDirty();
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.Customer#setContractList(java.util.List)
     */
    public void setContractList(List contractList) throws TopazException {
        this.contractList = (ArrayList) contractList;
    }
    
    public void setRdcNumberList(List rdcNumList) throws TopazException {
        this.rdcNumList = rdcNumList;
    }
    
    /**
     * @param contract
     */
    public void addContract(Contract contract) {
        this.contractList.add(contract);
    }
    
    public void addEnrolledCtrct(Contract contract) {
        this.enrolledCtrctList.add(contract);
    }
    
    public void addCsaContractList(Contract contract) {
        this.csaContractList.add(contract);
    }
    
    public void addNoCsaContractList(Contract contract) {
        this.noCsaContractList.add(contract);
    }
    
    public void addRdcNumber(String rdcNum) {
        rdcNumList.add(rdcNum);
    }

    /**
     * @param custDesignation The custDesignation to set.
     * @throws TopazException
     */
    public void setCustDesignation(String custDesignation) throws TopazException {
        this.custDesignation = custDesignation;
        persister.setDirty();
    }
    
    public void setGovEntityIndCode(int govEntityIndCode)throws TopazException {
        this.govEntityIndCode = govEntityIndCode;
        persister.setDirty();
    }
    /**
     * @param govEntityIndCodeDesc The govEntityIndCodeDesc to set.
     * @throws TopazException
     */
    public void setGovEntityIndCodeDesc(String govEntityIndCodeDesc) throws TopazException {
        this.govEntityIndCodeDesc = govEntityIndCodeDesc;
        persister.setDirty();
    }
    

    public List getCustomerGroupList(){
    	return this.customerGroupList;
    }
    
    public void setCustomerGroupList(List customerGroupList){
    	this.customerGroupList = customerGroupList;
    }
   
}
