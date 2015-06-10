package com.ibm.dsw.quote.partner.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SelectPartnerContract</code> class is contract for partner
 * selection.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-12
 */
public class SelectPartnerContract extends QuoteBaseContract {
    
    String lob = null;

    String partnerNum = null;
    
    String partnerType = null;
    
    String webQuoteNum = null;
    
    String isSubmittedQuote = null;
    
    private String migrationReqNum;
	private String agreementNum;
	private String sapCustNum;
	private String sapCtrctNum;
	private String highlightId;
	private String pageFrom;
	private String updateDistrFlg;

    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }

    /**
     * @param lob
     *            The lob to set.
     */
    public void setLob(String lob) {
        this.lob = lob;
    }

    /**
     * @return Returns the partnerNum.
     */
    public String getPartnerNum() {
        return partnerNum;
    }

    /**
     * @param partnerNum
     *            The partnerNum to set.
     */
    public void setPartnerNum(String partnerNum) {
        this.partnerNum = partnerNum;
    }
    /**
     * @return Returns the isReseller.
     */
    public String getPartnerType() {
        return partnerType;
    }
    /**
     * @param partnerType The isReseller to set.
     */
    public void setPartnerType(String partnerType) {
        this.partnerType = partnerType;
    }
    
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
    
    public String getIsSubmittedQuote() {
        return isSubmittedQuote;
    }
    
    public void setIsSubmittedQuote(String isSubmittedQuote) {
        this.isSubmittedQuote = isSubmittedQuote;
    }
    
    public boolean isSubmittedQuote() {
        return "true".equalsIgnoreCase(this.isSubmittedQuote);
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

	public String getUpdateDistrFlg() {
		return (updateDistrFlg == null ? "0" : updateDistrFlg);
	}

	public void setUpdateDistrFlg(String updateDistrFlg) {
		this.updateDistrFlg = updateDistrFlg;
	}
	
	
}
