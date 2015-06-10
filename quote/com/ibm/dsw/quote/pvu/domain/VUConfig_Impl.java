package com.ibm.dsw.quote.pvu.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code><code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-19
 */
public abstract class VUConfig_Impl implements VUConfig {
    
    public String procrCode = null ;
    
    public String procrVendCode = null ;
    
    public String procrBrandCode = null ;
    
    public String procrTypeCode = null ;
    
    public String coreValUnit = null ;
    
    public int procrTypeQTY  ;
    
    public int extndDVU  ;

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.domain.VUConfig#getCoreValUnit()
     */
    public String getCoreValUnit() {
        return this.coreValUnit;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.domain.VUConfig#getExtndDVU()
     */
    public int getExtndDVU() {
        return this.extndDVU;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.domain.VUConfig#getProcrBrandCode()
     */
    public String getProcrBrandCode() {
        return this.procrBrandCode;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.domain.VUConfig#getProcrCode()
     */
    public String getProcrCode() {
        return this.procrCode;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.domain.VUConfig#getProcrTypeCode()
     */
    public String getProcrTypeCode() {
        return this.procrTypeCode;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.domain.VUConfig#getProcrTypeQTY()
     */
    public int getProcrTypeQTY() {
        return this.procrTypeQTY;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.pvu.domain.VUConfig#getProcrVendCode()
     */
    public String getProcrVendCode() {
        return this.procrVendCode;
    }

    public String toString() {
        return new ToStringBuilder(this).append(procrCode).append(procrVendCode)
        .append(procrBrandCode).append(procrTypeCode).append(coreValUnit)
        .append(procrTypeQTY).append(extndDVU).toString();
        
    }
}
