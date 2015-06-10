/*
 * Created on 2007-9-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.contract;

/**
 * @author helenyu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpBidRemoveAttchmtContract extends PostSpecialBidTabContract{

    private String webQuoteNum = null;
    private String attchmtSeqNum = null;
    
    
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
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    /**
     * @param webQuoteNum The webQuoteNum to set.
     */
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
}
