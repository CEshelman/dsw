package com.ibm.dsw.quote.customerlist.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerList<code> class.
 *    
 * @author: xiaogy@cn.ibm.com
 * 
 * Creation date: 2008-6-24
 */

public class CustomerList 
{
    private String siteNumber;
    private String customerReferenceDataNumber;
    private String IBMCustomerNumber;
    private String customerName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String stateOrProvinceCode;
    private String stateOrProvinceDescription;
    private String postalCode;
    private String countryCode;
    private String countryDescription;
    private String currencyCode;
    private String currencyDescription;
    /**
     * @return Returns the currencyDescription.
     */
    public String getCurrencyDescription() {
        return currencyDescription;
    }
    /**
     * @param currencyDescription The currencyDescription to set.
     */
    public void setCurrencyDescription(String currencyDescription) {
        this.currencyDescription = currencyDescription;
    }
    /**
     * @return Returns the addressLine1.
     */
    public String getAddressLine1() {
        return addressLine1;
    }
    /**
     * @param addressLine1 The addressLine1 to set.
     */
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    /**
     * @return Returns the addressLine2.
     */
    public String getAddressLine2() {
        return addressLine2;
    }
    /**
     * @param addressLine2 The addressLine2 to set.
     */
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    /**
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }
    /**
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }
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
     * @return Returns the countryDescription.
     */
    public String getCountryDescription() {
        return countryDescription;
    }
    /**
     * @param countryDescription The countryDescription to set.
     */
    public void setCountryDescription(String countryDescription) {
        this.countryDescription = countryDescription;
    }
    /**
     * @return Returns the currencyCode.
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    /**
     * @param currencyCode The currencyCode to set.
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    /**
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }
    /**
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    /**
     * @return Returns the customerReferenceDataNumber.
     */
    public String getCustomerReferenceDataNumber() {
        return customerReferenceDataNumber;
    }
    /**
     * @param customerReferenceDataNumber The customerReferenceDataNumber to set.
     */
    public void setCustomerReferenceDataNumber(String customerReferenceDataNumber) {
        this.customerReferenceDataNumber = customerReferenceDataNumber;
    }
    /**
     * @return Returns the iBMCustomerNumber.
     */
    public String getIBMCustomerNumber() {
        return IBMCustomerNumber;
    }
    /**
     * @param customerNumber The iBMCustomerNumber to set.
     */
    public void setIBMCustomerNumber(String customerNumber) {
        IBMCustomerNumber = customerNumber;
    }
    /**
     * @return Returns the postalCode.
     */
    public String getPostalCode() {
        return postalCode;
    }
    /**
     * @param postalCode The postalCode to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    /**
     * @return Returns the siteNumber.
     */
    public String getSiteNumber() {
        return siteNumber;
    }
    /**
     * @param siteNumber The siteNumber to set.
     */
    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }
    /**
     * @return Returns the stateOrProvinceCode.
     */
    public String getStateOrProvinceCode() {
        return stateOrProvinceCode;
    }
    /**
     * @param stateOrProvinceCode The stateOrProvinceCode to set.
     */
    public void setStateOrProvinceCode(String stateOrProvinceCode) {
        this.stateOrProvinceCode = stateOrProvinceCode;
    }
    /**
     * @return Returns the stateOrProvinceDescription.
     */
    public String getStateOrProvinceDescription() {
        return stateOrProvinceDescription;
    }
    /**
     * @param stateOrProvinceDescription The stateOrProvinceDescription to set.
     */
    public void setStateOrProvinceDescription(String stateOrProvinceDescription) {
        this.stateOrProvinceDescription = stateOrProvinceDescription;
    }
}
