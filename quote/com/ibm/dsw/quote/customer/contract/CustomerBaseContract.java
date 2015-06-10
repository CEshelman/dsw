/*
 * Created on Feb 22, 2007
 */
package com.ibm.dsw.quote.customer.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;


/**
 * @author Lavanya
 */
public class CustomerBaseContract extends QuoteBaseContract{
    //TODO is this the right class to extend?
    
    private String lob;
    private String country;
    private String quoteNum;
    private String progMigrtnCode;
    private String agreementNumber;
    private String siteNumber;
    private String webCustId;
    private String countryCode2;
    
	private  String migrationReqNum = "";
	private  String agreementNum = "";
	private String sapCustNum ;
	private String sapCtrctNum;
	private String highlightId;
	private  String pageFrom="";
	private String cusCountry;
	private String endUserFlag;
	
    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }
    /**
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
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
     * @return Returns the quoteNum.
     */
    public String getQuoteNum() {
        return quoteNum;
    }
    /**
     * @param quoteNum The quoteNum to set.
     */
    public void setQuoteNum(String quoteNum) {
        this.quoteNum = quoteNum;
    }
    
    public String getProgMigrtnCode() {
        return progMigrtnCode;
    }
    
    public void setProgMigrtnCode(String progMigrtnCode) {
        this.progMigrtnCode = progMigrtnCode;
    }

    public String getAgreementNumber() {
        return agreementNumber;
    }

    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }
    
    public String getSiteNumber() {
        return siteNumber;
    }
    
    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }
    
    public String getWebCustId() {
        return webCustId;
    }
    
    public void setWebCustId(String webCustId) {
        this.webCustId = webCustId;
    }
    
    public int getWebCustIdAsInt() {
        int iCustId = 0;
        try {
            iCustId = Integer.parseInt(getWebCustId());
        } catch (NumberFormatException e) {
            iCustId = 0;
        }
        return iCustId;
    }
    /**
     * @return Returns the countryCode2.
     */
    public String getCountryCode2() {
        return countryCode2;
    }
    /**
     * @param countryCode2 The countryCode2 to set.
     */
    public void setCountryCode2(String countryCode2) {
        this.countryCode2 = countryCode2;
    }
    
	public String getMigrationReqNum() {
		return migrationReqNum;
	}

	public void setMigrationReqNum(String migrationReqNum) {
		this.migrationReqNum = migrationReqNum;
	}

	public String getAgreementNum() {
		return agreementNum;
	}

	public void setAgreementNum(String agreementNum) {
		this.agreementNum = agreementNum;
	}

	public String getSapCustNum() {
		return sapCustNum;
	}

	public void setSapCustNum(String sapCustNum) {
		this.sapCustNum = sapCustNum;
	}

	public String getSapCtrctNum() {
		return sapCtrctNum;
	}

	public void setSapCtrctNum(String sapCtrctNum) {
		this.sapCtrctNum = sapCtrctNum;
	}

	public String getHighlightId() {
		return highlightId;
	}

	public void setHighlightId(String highlightId) {
		this.highlightId = highlightId;
	}
	
	public String getPageFrom() {
		return (pageFrom == null ? "" : pageFrom);
	}
	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}
	public String getCusCountry() {
		return cusCountry;
	}
	public void setCusCountry(String cusCountry) {
		this.cusCountry = cusCountry;
	}
	public String getEndUserFlag() {
		return endUserFlag;
	}
	public void setEndUserFlag(String endUserFlag) {
		this.endUserFlag = endUserFlag;
	}
}
