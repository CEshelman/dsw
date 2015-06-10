/*
 * Created on Feb 13, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;

/**
 * @author zhangln
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayStatusDetailExplanationContract extends QuoteBaseContract {
    private String sapDocNum;
    private String statusCode;
    
    /**
     * @return Returns the statusCode.
     */
    public String getStatusCode() {
        return statusCode;
    }
    /**
     * @param statusCode The statusCode to set.
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    /**
     * @return Returns the sapDocNum.
     */
    public String getSapDocNum() {
        return sapDocNum;
    }
    /**
     * @param sapDocNum The sapDocNum to set.
     */
    public void setSapDocNum(String statusType) {
        this.sapDocNum = statusType;
    }
}
