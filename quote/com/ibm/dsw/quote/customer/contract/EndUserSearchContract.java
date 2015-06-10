package com.ibm.dsw.quote.customer.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>EndUserSearchContract<code> class.
 *    
 * @author: doris_yuen@us.ibm.com
 * 
 * Creation date: Dec. 15, 2009
 */

public class EndUserSearchContract extends CustomerBaseContract {

    private String siteNumber;

    private String customerName;

    private String startPos; // for pagination

    private String currency;
    
    private String state;
    
    private String findAllCntryCusts;
    
    private String cusCountry;
    

	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
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
    
    public String getFindAllCntryCusts() {
		return findAllCntryCusts;
	}

	public void setFindAllCntryCusts(String findAllCntryCusts) {
		this.findAllCntryCusts = findAllCntryCusts;
	}

	public String getCusCountry() {
		return cusCountry;
	}

	public void setCusCountry(String cusCountry) {
		this.cusCountry = cusCountry;
	}
	
	
}
