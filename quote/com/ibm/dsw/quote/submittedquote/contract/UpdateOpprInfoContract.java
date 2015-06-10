package com.ibm.dsw.quote.submittedquote.contract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>UpdateOpprInfoContract</code> class is the process contract for
 * opportunity info updating.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 15, 2007
 */
public class UpdateOpprInfoContract extends SubmittedQuoteBaseContract {
    private String oppNumRadio;

    private String opprtntyNum;
    
    private String opprtntyNumSel;

    private String exemptnCode;

    /**
     * @return Returns the oppNumRadio.
     */
    public String getOppNumRadio() {
        return oppNumRadio;
    }

    /**
     * @param oppNumRadio
     *            The oppNumRadio to set.
     */
    public void setOppNumRadio(String oppNumRadio) {
        this.oppNumRadio = oppNumRadio;
    }

    /**
     * @return Returns the opprtntyNum.
     */
    public String getOpprtntyNum() {
        return opprtntyNum;
    }

    /**
     * @param opprtntyNum
     *            The opprtntyNum to set.
     */
    public void setOpprtntyNum(String opprtntyNum) {
        this.opprtntyNum = opprtntyNum;
    }

    /**
     * @return Returns the exemptnCode.
     */
    public String getExemptnCode() {
        return exemptnCode;
    }

    /**
     * @param exemptnCode
     *            The exemptnCode to set.
     */
    public void setExemptnCode(String exemptnCode) {
        this.exemptnCode = exemptnCode;
    }

	public String getOpprtntyNumSel() {
		return opprtntyNumSel;
	}

	public void setOpprtntyNumSel(String opprtntyNumSel) {
		this.opprtntyNumSel = opprtntyNumSel;
	}

}
