package com.ibm.dsw.quote.draftquote.contract;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>AddLineItemContract</code> class.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 6, 2007
 */
public class AddLineItemContract extends PostPartPriceTabContract {
    private String partNum;
    

    public String getPartNum() {
        return partNum;
    }
    /**
     * @param partNum The partNum to set.
     */
    public void setPartNum(String partNum) {
        this.partNum = partNum;
    }
}
