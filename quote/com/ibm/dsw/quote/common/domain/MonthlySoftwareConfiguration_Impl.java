/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Dec 8, 2013
 */
package com.ibm.dsw.quote.common.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * DOC class global comment. Detailled comment
 */
public abstract class MonthlySoftwareConfiguration_Impl extends BasicConfiguration_Impl implements MonthlySoftwareConfiguration {

    /**
     * 
     */
    private static final long serialVersionUID = -5844783600217852912L;

    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    public ServiceDateModType serviceDateModType;

    public Date serviceDate;

    public boolean termExtension;

    public boolean configEntireExtended;

    public Date endDate;

    public Integer globalTerm;

    public boolean needReconfigFlag;

    public String globalBillingFrequencyCode;
    
    public List<MonthlySwBrand> brandsList = new ArrayList();
    
    public List<String> chargeAgreementList = new ArrayList();
    //store the coterm configurations <ca num,coterm config list>
    public Map<String,List<CoTermConfiguration>> cotermMap = new HashMap();

    //monthly
    public boolean configrtnOvrrdn;

    public String configrtrConfigrtnId;
    
    public String ibmProdIdDscr;
    
    public boolean addNewMonthlySWFlag;
    
    public String chrgAgrmtNum;

	public String configrtnIdFromCa;

    /**
     * Getter for serviceDateModType.
     * 
     * @return the serviceDateModType
     */
    public ServiceDateModType getServiceDateModType() {
        return this.serviceDateModType;
    }


    /**
     * Getter for serviceDate.
     * 
     * @return the serviceDate
     */
    public Date getServiceDate() {
        return this.serviceDate;
    }


    /**
     * Getter for termExtension.
     * 
     * @return the termExtension
     */
    public boolean isTermExtension() {
        return this.termExtension;
    }


    /**
     * Getter for configEntireExtended.
     * 
     * @return the configEntireExtended
     */
    public boolean isConfigEntireExtended() {
        return this.configEntireExtended;
    }


    /**
     * Getter for endDate.
     * 
     * @return the endDate
     */
    public Date getEndDate() {
        return this.endDate;
    }


    /**
     * Getter for globalTerm.
     * 
     * @return the globalTerm
     */
    public Integer getGlobalTerm() {
        return this.globalTerm;
    }


    /**
     * Getter for needRecalculate.
     * 
     * @return the needRecalculate
     */
    public boolean isNeedReconfigFlag() {
        return this.needReconfigFlag;
    }


    /**
     * Getter for globalBillingFrequencyCode.
     * 
     * @return the globalBillingFrequencyCode
     */
    public String getGlobalBillingFrequencyCode() {
        return this.globalBillingFrequencyCode;
    }


	public List<MonthlySwBrand> getBrandsList() {
		return brandsList;
	}

	public List<String> getChargeAgreementList() {
		return chargeAgreementList;
	}

	public Map<String, List<CoTermConfiguration>> getCotermMap() {
		return cotermMap;
	}

	@Override
	public String getAllBrandDesc() {
		List brandsList = getBrandsList();
		if(brandsList != null && brandsList.size()> 0){
			Iterator<MonthlySwBrand> brandsIt = brandsList.iterator();
			StringBuilder s = new StringBuilder("");
			while(brandsIt.hasNext()){
				MonthlySwBrand msb = brandsIt.next();
				String brandDesc = msb.getBrandCodeDesc();
				s.append(brandDesc);
				s.append(", ");
			}
			return s.substring(0, s.length()-2);
		}
		return null;
	}	

	public boolean isConfigrtnOvrrdn(){
		return configrtnOvrrdn;
	}
	
	public String getConfigrtrConfigrtnId(){
		return configrtrConfigrtnId;
	}
	
	public boolean isAddNewMonthlySWFlag(){
		return this.addNewMonthlySWFlag;
	}

	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}

	public String getConfigrtnIdFromCa() {
		return configrtnIdFromCa;
	}
}
