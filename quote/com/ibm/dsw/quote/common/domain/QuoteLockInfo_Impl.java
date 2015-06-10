/*
 * Created on 2010-5-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.domain;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuoteLockInfo_Impl implements QuoteLockInfo {

    private boolean lockedFlag;
    
    private String lockedBy;
    
    private String webQuoteNum;
    
    public boolean isLockedFlag() {
        return this.lockedFlag;
    }

    public String getLockedBy() {
        return this.lockedBy;
    }

    
    public void setLockedFlag(boolean lockedFlag) {
        this.lockedFlag = lockedFlag;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteLockInfo#getWebQuoteNum()
     */
    public String getWebQuoteNum() {
        // TODO Auto-generated method stub
        return this.webQuoteNum;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteLockInfo#setWebQuoteNum(java.lang.String)
     */
    public void setWebQuoteNum(String webQuoteNum) {
       this.webQuoteNum = webQuoteNum;
        
    }

}
