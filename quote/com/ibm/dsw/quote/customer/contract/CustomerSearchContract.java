package com.ibm.dsw.quote.customer.contract;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerSearchContract<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */

public class CustomerSearchContract extends CustomerBaseContract {

    private String siteNumber;

    private String agreementNumber;

    private String customerName;

    private String contractOption;

    private String anniversary;

    private String findActiveCusts;

    private String startPos; // for pagination

    private String currency;
    
    private String searchFor;
    
    private String state;
    
    private String findAllCntryCusts;
    
    private String eraseECFlag;
    
    private String addressType = "0"; // 0=ship-to (default); 1=install-at
    
    private String endUserFlag;
    

	public String getEndUserFlag() {
		return endUserFlag;
	}

	public void setEndUserFlag(String endUserFlag) {
		this.endUserFlag = endUserFlag;
	}

	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }
    
    /**
     * @return Returns the agreementNumber.
     */
    public String getAgreementNumber() {
        return agreementNumber;
    }

    /**
     * @param agreementNumber
     *            The agreementNumber to set.
     */
    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    /**
     * @return Returns the anniversary.
     */
    public String getAnniversary() {
        return anniversary;
    }

    /**
     * @param anniversary
     *            The anniversary to set.
     */
    public void setAnniversary(String anniversary) {
        this.anniversary = anniversary;
    }

    /**
     * @return Returns the contractOption.
     */
    public String getContractOption() {
        return contractOption;
    }

    /**
     * @param contractOption
     *            The contractOption to set.
     */
    public void setContractOption(String contractOption) {
        this.contractOption = contractOption;
    }

    /**
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName
     *            The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return Returns the siteNumber.
     */
    public String getSiteNumber() {
        return siteNumber;
    }

    /**
     * @param siteNumber
     *            The siteNumber to set.
     */
    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }

    /**
     * @return Returns the findActiveCusts.
     */
    public String getFindActiveCusts() {
        if (StringUtils.isBlank(findActiveCusts))
        {
        	if ( QuoteConstants.LOB_SSP.equalsIgnoreCase(this.getLob()) )
        	{
        		return CustomerConstants.NUMBER_1;
        	}
            return CustomerConstants.ALL_CUSTS;
        }
        else
            return findActiveCusts;
    }

    /**
     * @param findActiveCusts
     *            The findActiveCusts to set.
     */
    public void setFindActiveCusts(String findActiveCusts) {
        this.findActiveCusts = findActiveCusts;
    }

    /**
     * @return Returns the startPos.
     */
    public String getStartPos() {
        return startPos;
    }

    /**
     * @param startPos
     *            The startPos to set.
     */
    public void setStartPos(String startPos) {
        this.startPos = startPos;
    }

    /**
     * @return Returns the currency.
     */
    public String getCurrency() {
        return currency;
    }
    /**
     * @param currency The currency to set.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    /**
     * @return Returns the searchFor.
     */
    public String getSearchFor() {
        return searchFor;
    }
    /**
     * @param searchFor The searchFor to set.
     */
    public void setSearchFor(String searchFor) {
        this.searchFor = searchFor;
    }
    /**
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }
    /**
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }
    
    
    /**
     * @return Returns the findAllCntryCusts.
     */
    public String getFindAllCntryCusts() {
        return findAllCntryCusts;
    }
    /**
     * @param findAllCntryCusts The findAllCntryCusts to set.
     */
    public void setFindAllCntryCusts(String findAllCntryCusts) {
        this.findAllCntryCusts = findAllCntryCusts;
    }

	public String getEraseECFlag() {
		return eraseECFlag;
	}

	public void setEraseECFlag(String eraseECFlag) {
		this.eraseECFlag = eraseECFlag;
	}
	
	public void setAddressType(String aType) { addressType = aType; }
	public String getAddressType() { return addressType; }
	
    
    
}
