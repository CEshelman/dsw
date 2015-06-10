/*
 * Created on 2007-5-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;

/**
 * @author helen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AttachmentDownloadContract extends QuoteBaseCookieContract {

    private static final long serialVersionUID = 5884609533871696885L;
	private String attchmtSeqNum = null;
    private String downloadType = null;
    private String sapDocId = null;
    private String outputName = null;
    private String sapQuoteNum = null;
    private String itemType = null;
    
    
    public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	/**
     * @return Returns the attchmtSeqNum.
     */
    public String getAttchmtSeqNum() {
        return attchmtSeqNum;
    }
    /**
     * @param attchmtSeqNum The attchmtSeqNum to set.
     */
    public void setAttchmtSeqNum(String attchmtSeqNum) {
        this.attchmtSeqNum = attchmtSeqNum;
    }
    /**
     * @return Returns the downloadType.
     */
    public String getDownloadType() {
        return downloadType;
    }
    /**
     * @param downloadType The downloadType to set.
     */
    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }
    /**
     * @return Returns the sapDocId.
     */
    public String getSapDocId() {
        return sapDocId;
    }
    /**
     * @param sapDocId The sapDocId to set.
     */
    public void setSapDocId(String sapDocId) {
        this.sapDocId = sapDocId;
    }
    /**
     * @return Returns the outputName.
     */
    public String getOutputName() {
        return outputName;
    }
    /**
     * @param outputName The outputName to set.
     */
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }
    /**
     * @return Returns the sapQuoteNum.
     */
    public String getSapQuoteNum() {
        return sapQuoteNum;
    }
    /**
     * @param sapQuoteNum The sapQuoteNum to set.
     */
    public void setSapQuoteNum(String sapQuoteNum) {
        this.sapQuoteNum = sapQuoteNum;
    }
}
