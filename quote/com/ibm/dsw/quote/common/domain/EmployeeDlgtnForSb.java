
package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>EmployeeDlgtnForSbFactory.java</code>
 * 
 * @author: zhangdy@cn.ibm.com
 * 
 * Created on: Apr 9, 2008
 */
public class EmployeeDlgtnForSb  {
    private String lob;

    private String migrationCode;

    private boolean forceSB;

    
    public EmployeeDlgtnForSb(String lob, String migrationCode, boolean forceSB) {
        super();
        this.lob = lob;
        this.migrationCode = migrationCode;
        this.forceSB = forceSB;
    }
    
    /**
     * @return Returns the forceSB.
     */
    public boolean isForceSB() {
        return forceSB;
    }
    
    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }
    
    /**
     * @return Returns the migrationCode.
     */
    public String getMigrationCode() {
        return migrationCode;
    }
}
