/*
 * Created on 2007-4-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;

/**
 * @author Administrator
 *
 */
public class PartDetailsContract extends QuoteBaseContract {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1544027684983873118L;
	
	private String partNumber;
    private String displayType;
    private String lob;
    private String quoteNum;
    private String priceType;

    private String showEventBaseBiling;
    
    public String getPriceType() {
		return priceType;
	}
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	public String getDisplayType() {
        return displayType;
    }
    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }
    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    public String getQuoteNum() {
        return quoteNum;
    }
    public void setQuoteNum(String quoteNum) {
        this.quoteNum = quoteNum;
    }
    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }
    /**
     * @param lob The lob to set.
     */
    public void setLob(String lob) {
        this.lob = lob;
    }
    
    /**
     * Getter for showEventBaseBiling.
     * @return the showEventBaseBiling
     */
    public String getShowEventBaseBiling() {
        return this.showEventBaseBiling;
    }
    
    /**
     * Sets the showEventBaseBiling.
     * @param showEventBaseBiling the showEventBaseBiling to set
     */
    public void setShowEventBaseBiling(String showEventBaseBiling) {
        this.showEventBaseBiling = showEventBaseBiling;
    }

}
