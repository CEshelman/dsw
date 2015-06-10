package com.ibm.dsw.quote.pvu.domain.jdbc;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.ibm.dsw.quote.pvu.domain.VUConfig_Impl;

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
public class VUConfig_jdbc extends VUConfig_Impl implements Serializable {

    /**
     * @param procrCode
     * @param procrVendCode
     * @param procrVendCodeDesc
     * @param procrBrandCode
     * @param procrBrandCodeDesc
     * @param procrTypeCode
     * @param procrTypeCodeDesc
     * @param coreValUnit
     * @param procrTypeQTY
     * @param extndDVU
     */
    public VUConfig_jdbc(String procrCode, String procrVendCode,
            String procrBrandCode, String procrTypeCode, String coreValUnit,
            int procrTypeQTY, int extndDVU) {
        super();
        this.procrCode = procrCode;
        this.procrVendCode = procrVendCode;
        this.procrBrandCode = procrBrandCode;
        this.procrTypeCode = procrTypeCode;
        this.coreValUnit = coreValUnit;
        this.procrTypeQTY = procrTypeQTY;
        this.extndDVU = extndDVU;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
