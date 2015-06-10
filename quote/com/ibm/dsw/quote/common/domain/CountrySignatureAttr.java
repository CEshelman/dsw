package com.ibm.dsw.quote.common.domain; 
/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>CountrySignatureAttr<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: 2010-6-11
 */
public class CountrySignatureAttr {
    private String sapCode;
    private String supportRegion;
    private String supportSubRegion;
    private String required;
    private String CountryName;
    private boolean isEUMember;

    /**
     * @return Returns the isEUMember.
     */
    public boolean isEUMember() {
        return isEUMember;
    }
    /**
     * @param isEUMember The isEUMember to set.
     */
    public void setEUMember(boolean isEUMember) {
        this.isEUMember = isEUMember;
    }
    /**
     * @return Returns the countryName.
     */
    public String getCountryName() {
        return CountryName;
    }
    /**
     * @param countryName The countryName to set.
     */
    public void setCountryName(String countryName) {
        CountryName = countryName;
    }
    /**
     * @return Returns the required.
     */
    public String getRequired() {
        return required;
    }
    /**
     * @param required The required to set.
     */
    public void setRequired(String required) {
        this.required = required;
    }
    /**
     * @return Returns the sapCode.
     */
    public String getSapCode() {
        return sapCode;
    }
    /**
     * @param sapCode The sapCode to set.
     */
    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }
    /**
     * @return Returns the supportRegion.
     */
    public String getSupportRegion() {
        return supportRegion;
    }
    /**
     * @param supportRegion The supportRegion to set.
     */
    public void setSupportRegion(String supportRegion) {
        this.supportRegion = supportRegion;
    }
    /**
     * @return Returns the supportSubRegion.
     */
    public String getSupportSubRegion() {
        return supportSubRegion;
    }
    /**
     * @param supportSubRegion The supportSubRegion to set.
     */
    public void setSupportSubRegion(String supportSubRegion) {
        this.supportSubRegion = supportSubRegion;
    }
}
 