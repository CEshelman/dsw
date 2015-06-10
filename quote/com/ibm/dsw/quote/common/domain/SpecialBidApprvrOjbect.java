package com.ibm.dsw.quote.common.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpecialBidApprvrOjbect<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: May 16, 2007
 */

public class SpecialBidApprvrOjbect {
    private String apprvrGrpName;
    private int apprvrLevel;
    private int rdyToOrder;
    private String apprvrEmail;
    private String apprvrFirstName;
    private String apprvrlastName;
    
    public String getApprvrEmail() {
        return apprvrEmail;
    }
    public void setApprvrEmail(String apprvrEmail) {
        this.apprvrEmail = apprvrEmail;
    }
    public String getApprvrFirstName() {
        return apprvrFirstName;
    }
    public void setApprvrFirstName(String apprvrFirstName) {
        this.apprvrFirstName = apprvrFirstName;
    }
    public String getApprvrGrpName() {
        return apprvrGrpName;
    }
    public void setApprvrGrpName(String apprvrGrpName) {
        this.apprvrGrpName = apprvrGrpName;
    }
    public int getApprvrLevel() {
        return apprvrLevel;
    }
    public void setApprvrLevel(int apprvrLevel) {
        this.apprvrLevel = apprvrLevel;
    }
    public int getRdyToOrder() {
        return rdyToOrder;
    }
    public void setRdyToOrder(int rdyToOrder) {
        this.rdyToOrder = rdyToOrder;
    }
    public String getApprvrlastName() {
        return apprvrlastName;
    }
    public void setApprvrlastName(String apprvrlastName) {
        this.apprvrlastName = apprvrlastName;
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("apprvrLevel = ").append(apprvrLevel).append("\n");                
        buffer.append("apprvrGrpName = ").append(apprvrGrpName).append("\n");
        buffer.append("apprvrEmail = ").append(apprvrEmail).append("\n");                        
        buffer.append("apprvrFirstName = ").append(apprvrFirstName).append("\n");          
        buffer.append("apprvrlastName = ").append(apprvrlastName).append("\n");                
        return buffer.toString();
    }
}
