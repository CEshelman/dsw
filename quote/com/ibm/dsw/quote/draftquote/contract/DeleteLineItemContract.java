package com.ibm.dsw.quote.draftquote.contract;


/**
 * DeleteLineItemContract.java
 *
 * <p>
 * Copyright 2011 by IBM Corporation All rights reserved.
 * </p>
 * 
 * @author <a href="wxiaoli@cn.ibm.com">Vivian</a> <br/>
 * Mar 25, 2011
 */
public class DeleteLineItemContract extends PostPartPriceTabContract {
    private String partSeqNum;
    private String partNum;
    

    /**
     * @return
     * String
     */
    public String getPartSeqNum() {
        return partSeqNum;
    }
    
    /**
     * @param partSeqNum
     * void
     */
    public void setPartSeqNum(String partSeqNum) {
        this.partSeqNum = partSeqNum;
    }

	public String getPartNum() {
		return partNum;
	}

	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}
}
