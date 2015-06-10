package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>UpdateOppOwnerContract</code> class is contract for 'update
 * opportunity owner'.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-14
 */
public class UpdateOppOwnerContract extends QuoteBaseContract {

    String oppOwnerEmailAddr;

    String countryCode;

    String nodesId;

    String fullName;

    String firstName;

    String lastName;

    String phoneNum;

    String faxNum;

    /**
     * @return Returns the countryCode.
     */
    public String getCountryCode() {
        return countryCode;
    }
    /**
     * @param countryCode The countryCode to set.
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    /**
     * @return Returns the faxNum.
     */
    public String getFaxNum() {
        return faxNum;
    }
    /**
     * @param faxNum The faxNum to set.
     */
    public void setFaxNum(String faxNum) {
        this.faxNum = faxNum;
    }
    /**
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * @return Returns the fullName.
     */
    public String getFullName() {
        return fullName;
    }
    /**
     * @param fullName The fullName to set.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    /**
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * @return Returns the nodesId.
     */
    public String getNodesId() {
        return nodesId;
    }
    /**
     * @param nodesId The nodesId to set.
     */
    public void setNodesId(String nodesId) {
        this.nodesId = nodesId;
    }
    /**
     * @return Returns the phoneNum.
     */
    public String getPhoneNum() {
        return phoneNum;
    }
    /**
     * @param phoneNum The phoneNum to set.
     */
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    /**
     * @return Returns the oppOwnerEmailAddr.
     */
    public String getOppOwnerEmailAddr() {
        return oppOwnerEmailAddr;
    }

    /**
     * @param oppOwnerEmailAddr
     *            The oppOwnerEmailAddr to set.
     */
    public void setOppOwnerEmailAddr(String oppOwnerEmailAddr) {
        this.oppOwnerEmailAddr = oppOwnerEmailAddr;
    }

}
