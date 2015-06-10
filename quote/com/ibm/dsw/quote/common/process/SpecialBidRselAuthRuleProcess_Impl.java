package com.ibm.dsw.quote.common.process;

import java.sql.Timestamp;

import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpecialBidRselAuthRuleProcess_Impl<code> class.
 *    
 * @author: changwei@cn.ibm.com
 * 
 * Creation date: Apr 21, 2009
 */

public abstract class SpecialBidRselAuthRuleProcess_Impl extends TopazTransactionalProcess implements
        SpecialBidRselAuthRuleProcess {

    private String xrule = null;

    private Timestamp lastUpdateTime = null;

    /**
     * @return Returns the lastUpdateTime.
     */
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }
    /**
     * @param lastUpdateTime The lastUpdateTime to set.
     */
    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    /**
     * @return Returns the xrule.
     */
    public String getXrule() {
        return xrule;
    }
    /**
     * @param xrule The xrule to set.
     */
    public void setXrule(String xrule) {
        this.xrule = xrule;
    }
}
