/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Dec 9, 2013
 */
package com.ibm.dsw.quote.common.domain;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * DOC class global comment. Detailled comment
 */
public interface MonthlySoftwareConfiguration extends BasicConfiguration {

    /**
     * Getter for serviceDateModType.
     * 
     * @return the serviceDateModType
     */
    public abstract ServiceDateModType getServiceDateModType();

    /**
     * Sets the serviceDateModType.
     * 
     * @param serviceDateModType the serviceDateModType to set
     */
    public abstract void setServiceDateModType(ServiceDateModType serviceDateModType) throws TopazException;

    /**
     * Getter for serviceDate.
     * 
     * @return the serviceDate
     */
    public abstract Date getServiceDate();

    /**
     * Sets the serviceDate.
     * 
     * @param serviceDate the serviceDate to set
     */
    public abstract void setServiceDate(Date serviceDate) throws TopazException;

    /**
     * Getter for termExtension.
     * 
     * @return the termExtension
     */
    public abstract boolean isTermExtension();

    /**
     * Sets the termExtension.
     * 
     * @param termExtension the termExtension to set
     */
    public abstract void setTermExtension(boolean termExtension) throws TopazException;

    /**
     * Getter for configEntireExtended.
     * 
     * @return the configEntireExtended
     */
    public abstract boolean isConfigEntireExtended();

    /**
     * Sets the configEntireExtended.
     * 
     * @param configEntireExtended the configEntireExtended to set
     */
    public abstract void setConfigEntireExtended(boolean configEntireExtended) throws TopazException;

    /**
     * Getter for endDate.
     * 
     * @return the endDate
     */
    public abstract Date getEndDate();

    /**
     * Sets the endDate.
     * 
     * @param endDate the endDate to set
     */
    public abstract void setEndDate(Date endDate) throws TopazException;

    /**
     * Getter for globalTerm.
     * 
     * @return the globalTerm
     */
    public abstract Integer getGlobalTerm();

    /**
     * Sets the globalTerm.
     * 
     * @param globalTerm the globalTerm to set
     */
    public abstract void setGlobalTerm(Integer globalTerm) throws TopazException;

    /**
     * Getter for needReconfigFlag.
     * 
     * @return the needReconfigFlag
     */
    public abstract boolean isNeedReconfigFlag();

    /**
     * Sets the needReconfigFlag.
     * 
     * @param needReconfigFlag the needRecalculate to set
     */
    public abstract void setNeedReconfigFlag(boolean needReconfigFlag) throws TopazException;

    /**
     * Getter for globalBillingFrequencyCode.
     * 
     * @return the globalBillingFrequencyCode
     */
    public abstract String getGlobalBillingFrequencyCode();

    /**
     * Sets the globalBillingFrequencyCode.
     * 
     * @param globalBillingFrequencyCode the globalBillingFrequencyCode to set
     */
    public abstract void setGlobalBillingFrequencyCode(String globalBillingFrequencyCode) throws TopazException;
    
	public List<MonthlySwBrand> getBrandsList();
	
	public List<String> getChargeAgreementList();

	public Map<String, List<CoTermConfiguration>> getCotermMap();
	
	public String getAllBrandDesc();
	
	//monthly
	public boolean isConfigrtnOvrrdn();

	public String getConfigrtrConfigrtnId();

	public boolean isAddNewMonthlySWFlag();
	
	public String getChrgAgrmtNum();
	
    public void setChrgAgrmtNum(String chrgAgrmtNum) throws TopazException;

	public void setAddNewMonthlySWFlag(boolean addNewMonthlySWFlag) throws TopazException;
	
	public void delete() throws TopazException;
	
	public void setDirty() throws TopazException;
	
	public void setConfigrtnIdFromCa(String configrtnIdFromCa) throws TopazException;

	public String getConfigrtnIdFromCa();

}
