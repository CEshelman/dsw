package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
/**
 * @author J Zhang
 */
public class SubmittedPriorSSPriceContract extends SubmittedQuoteBaseContract {
	private String quoteNumber;
	private String lineSeqNum;
	
    private String lpp; 				// System computed prior S&S
	private String localUnitPriceLpp;	// Sales rep computed S&S
	private String lppMissReas;			// lpp missing reasons
	private String justTxt;				// Justification text
	private String sapSalesOrdNum;		// Sales order number
	private String priorQuoteNum;		// Quote/Special bid number
	private String partQty;				// Part quality
	private String partNum;				// Part number
	private String soldToCustNum;		// S&S site number
	
	private String currencyCode;
	private String quoteCurrencyCode;
	private String type="DEFAULT"; 
	private String renewalNum;

	public String getQuoteCurrencyCode() {
		return quoteCurrencyCode;
	}

	public void setQuoteCurrencyCode(String quoteCurrencyCode) {
		this.quoteCurrencyCode = quoteCurrencyCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLpp() {
		return lpp;
	}

	public void setLpp(String lpp) {
		this.lpp = lpp;
	}
	
	public String getLocalUnitPriceLpp() {
		return localUnitPriceLpp;
	}
	
	public void setLocalUnitPriceLpp(String localUnitPriceLpp) {
		this.localUnitPriceLpp = localUnitPriceLpp;
	}
	
	public String getLppMissReason() {
		return lppMissReas;
	}

	public void setLppMissReason(String lppMissReas) {
		this.lppMissReas = lppMissReas;
	}

	public String getJustificationText() {
		return justTxt;
	}

	public void setJustificationText(String justTxt) {
		this.justTxt = justTxt;
	}

	public String getSapSalesOrdNum() {
		return sapSalesOrdNum;
	}

	public void setSapSalesOrdNum(String sapSalesOrdNum) {
		this.sapSalesOrdNum = sapSalesOrdNum;
	}

	public String getPriorQuoteNum() {
		return priorQuoteNum;
	}

	public void setPriorQuoteNum(String priorQuoteNum) {
		this.priorQuoteNum = priorQuoteNum;
	}
	
	public String getPartQuality() {
		return partQty;
	}

	public void setPartQuality(String partQty) {
		this.partQty = partQty;
	}

	public String getPartNum() {
		return partNum;
	}

	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}
	
	public String getSoldToCustNum() {
		return soldToCustNum;
	}

	public void setSoldToCustNum(String soldToCustNum) {
		this.soldToCustNum = soldToCustNum;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getLineSeqNum() {
		return lineSeqNum;
	}

	public void setLineSeqNum(String lineSeqNum) {
		this.lineSeqNum = lineSeqNum;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}
	
	public void load(Parameters parameters, JadeSession session) {
		super.load(parameters, session);
	}

	public String getRenewalNum() {
		return renewalNum;
	}

	public void setRenewalNum(String renewalNum) {
		this.renewalNum = renewalNum;
	}
	
	
}