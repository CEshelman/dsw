/*
 * Created on 2010-4-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RselCtrldDistribtn implements Serializable {
    
    private String sapMatlGrp5CondCode;
    private String tier1CustNum;
    private String sapGovRselFlag;

    /**
     * @return Returns the sapGovRselFlag.
     */
    public String getSapGovRselFlag() {
        return sapGovRselFlag;
    }
    /**
     * @param sapGovRselFlag The sapGovRselFlag to set.
     */
    public void setSapGovRselFlag(String sapGovRselFlag) {
        this.sapGovRselFlag = sapGovRselFlag;
    }
    /**
     * @return Returns the sapMatlGrp5CondCode.
     */
    public String getSapMatlGrp5CondCode() {
        return sapMatlGrp5CondCode;
    }
    /**
     * @param sapMatlGrp5CondCode The sapMatlGrp5CondCode to set.
     */
    public void setSapMatlGrp5CondCode(String sapMatlGrp5CondCode) {
        this.sapMatlGrp5CondCode = sapMatlGrp5CondCode;
    }
    /**
     * @return Returns the tier1CustNum.
     */
    public String getTier1CustNum() {
        return tier1CustNum;
    }
    /**
     * @param tier1CustNum The tier1CustNum to set.
     */
    public void setTier1CustNum(String tier1CustNum) {
        this.tier1CustNum = tier1CustNum;
    }
}
