/*
 * Created on 2007-3-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.ps.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PartDetailsContract extends QuoteBaseContract {
    private String lob;
    private String country;
    private String currency;
    private String partNumber;
    
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getLob() {
        return lob;
    }
    public void setLob(String lob) {
        this.lob = lob;
    }
    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
}
