package com.ibm.dsw.quote.draftquote.contract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>OvrrdTranLevelCodeContract<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Aug 7, 2007
 */

public class OvrrdTranLevelCodeContract extends PostPartPriceTabContract {
    
    private String ovrrdTranLevelCode;
    
    public String getOvrrdTranLevelCode() {
        return ovrrdTranLevelCode;
    }
    public void setOvrrdTranLevelCode(String ovrrdTranLevelCode) {
        this.ovrrdTranLevelCode = ovrrdTranLevelCode;
    }
}
