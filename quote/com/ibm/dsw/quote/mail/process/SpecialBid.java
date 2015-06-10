/**
 * SpecialBid.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf160638.12 v101006191000
 */

package com.ibm.dsw.quote.mail.process;

public class SpecialBid  {
    private java.lang.String quoteNumber;
    private java.lang.String quoteTitle;
    private java.lang.String customerName;
    private java.lang.String url;
    private java.lang.String quoteCreatorFirstName;
    private java.lang.String quoteCreatorLastName;
    private java.lang.String quoteCreatorEmail;
    private java.lang.String delagatesEmails;
    private java.lang.String administratorsEmails;
    private java.lang.String reviewersEmails;
    private java.lang.String bidExpirationDate;
    private java.lang.String expDateExtensionJustification;
    private String submitterEmail;
    private boolean isPGSQuote = false;
    private String partnerURL;
    private boolean isSQOEnv = true;
    
    public String getPartnerURL() {
		return partnerURL;
	}

	public void setPartnerURL(String partnerURL) {
		this.partnerURL = partnerURL;
	}

	public boolean isPGSQuote() {
		return isPGSQuote;
	}

	public boolean isSQOEnv() {
		return isSQOEnv;
	}

	public void setSQOEnv(boolean isSQOEnv) {
		this.isSQOEnv = isSQOEnv;
	}

	public void setPGSQuote(boolean isPGSQuote) {
		this.isPGSQuote = isPGSQuote;
	}

	public SpecialBid() {
    }

    public java.lang.String getQuoteNumber() {
        return quoteNumber;
    }

    public void setQuoteNumber(java.lang.String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }

    public java.lang.String getQuoteTitle() {
        return quoteTitle;
    }

    public void setQuoteTitle(java.lang.String quoteTitle) {
        this.quoteTitle = quoteTitle;
    }

    public java.lang.String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(java.lang.String customerName) {
        this.customerName = customerName;
    }

    public java.lang.String getUrl() {
        return url;
    }

    public void setUrl(java.lang.String url) {
        this.url = url;
    }

    public java.lang.String getQuoteCreatorFirstName() {
        return quoteCreatorFirstName;
    }

    public void setQuoteCreatorFirstName(java.lang.String quoteCreatorFirstName) {
        this.quoteCreatorFirstName = quoteCreatorFirstName;
    }

    public java.lang.String getQuoteCreatorLastName() {
        return quoteCreatorLastName;
    }

    public void setQuoteCreatorLastName(java.lang.String quoteCreatorLastName) {
        this.quoteCreatorLastName = quoteCreatorLastName;
    }

    public java.lang.String getQuoteCreatorEmail() {
        return quoteCreatorEmail;
    }

    public void setQuoteCreatorEmail(java.lang.String quoteCreatorEmail) {
        this.quoteCreatorEmail = quoteCreatorEmail;
    }
    public String getSubmitterEmail() {
        return submitterEmail;
    }
    public void setSubmitterEmail(String submitterEmail) {
        this.submitterEmail = submitterEmail;
    }
    public java.lang.String getDelagatesEmails() {
        return delagatesEmails;
    }

    public void setDelagatesEmails(java.lang.String delagatesEmails) {
        this.delagatesEmails = delagatesEmails;
    }

    public java.lang.String getAdministratorsEmails() {
        return administratorsEmails;
    }

    public void setAdministratorsEmails(java.lang.String administratorsEmails) {
        this.administratorsEmails = administratorsEmails;
    }

    public java.lang.String getReviewersEmails() {
        return reviewersEmails;
    }

    public void setReviewersEmails(java.lang.String reviewersEmails) {
        this.reviewersEmails = reviewersEmails;
    }

    public java.lang.String getBidExpirationDate() {
        return bidExpirationDate;
    }

    public void setBidExpirationDate(java.lang.String bidExpirationDate) {
        this.bidExpirationDate = bidExpirationDate;
    }

	public java.lang.String getExpDateExtensionJustification() {
		return expDateExtensionJustification;
	}

	public void setExpDateExtensionJustification(
			java.lang.String expDateExtensionJustification) {
		this.expDateExtensionJustification = expDateExtensionJustification;
	}

}
