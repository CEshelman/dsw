/*
 * Created on Feb 20, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.domain;

/**
 * @author minhuiy
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class QuoteRightColumn_Impl implements QuoteRightColumn {

    public String creatorId = "";

    public String sWebQuoteNum = "";

    public String sCustName = "";

    public int iNumOfParts = 0;

    public String sQuoteTypeCode = "";
    
    private int qtCopyType;

    /**
     * @return Returns the creatorId.
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * @return Returns the sQuoteTypeCode.
     */
    public String getSQuoteTypeCode() {
        return sQuoteTypeCode;
    }

    /**
     * @return Returns the iNumOfParts.
     */
    public int getINumOfParts() {
        return iNumOfParts;
    }

    /**
     * @return Returns the sCustName.
     */
    public String getSCustName() {
        return sCustName;
    }

    /**
     * @return Returns the sWebQuoteNum.
     */
    public String getSWebQuoteNum() {
        return sWebQuoteNum;
    }
    
    public int getQtCopyType(){
        return qtCopyType;
    }
    
    public void setQtCopyType(int qtCopyType) {
        this.qtCopyType = qtCopyType;
    }
}
