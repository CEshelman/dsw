package com.ibm.dsw.quote.common.process;

import java.sql.Timestamp;

import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * @author Vivian
 *	Sep 20, 2012
 */
public abstract class BusinessRuleProcess_Impl extends TopazTransactionalProcess implements
	BusinessRuleProcess {

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
