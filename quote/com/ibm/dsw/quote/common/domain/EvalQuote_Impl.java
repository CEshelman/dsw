package com.ibm.dsw.quote.common.domain;

public class EvalQuote_Impl implements EvalQuote {
	private static final long serialVersionUID = 1L;

	private String webQuoteNum;
	private String quoteTypeCode;
	private String soldToCustNum;
	private String custName;
	private String evalEmailAdr;
	private String quoteTitle;
	private String quoteDscr;
	private String quoteStageCode;
	private String fullName;
	private String progCode;
	private String agrmtTypeCode;
	
	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}

	public String getQuoteTypeCode() {
		return quoteTypeCode;
	}

	public void setQuoteTypeCode(String quoteTypeCode) {
		this.quoteTypeCode = quoteTypeCode;
	}

	public String getSoldToCustNum() {
		return soldToCustNum;
	}

	public void setSoldToCustNum(String soldToCustNum) {
		this.soldToCustNum = soldToCustNum;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getEvalEmailAdr() {
		return evalEmailAdr;
	}

	public void setEvalEmailAdr(String evalEmailAdr) {
		this.evalEmailAdr = evalEmailAdr;
	}

	public String getQuoteTitle() {
		return quoteTitle;
	}

	public void setQuoteTitle(String quoteTitle) {
		this.quoteTitle = quoteTitle;
	}

	public String getQuoteDscr() {
		return quoteDscr;
	}

	public void setQuoteDscr(String quoteDscr) {
		this.quoteDscr = quoteDscr;
	}

	public String getQuoteStageCode() {
		return quoteStageCode;
	}

	public void setQuoteStageCode(String quoteStageCode) {
		this.quoteStageCode = quoteStageCode;
	}

	@Override
	public void setMode(int mode) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public int getMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void markAsModified() throws Exception {
		// TODO Auto-generated method stub
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getProgCode() {
		return progCode;
	}

	public void setProgCode(String progCode){
		this.progCode = progCode;
	}	 
	
	public String getAgrmtTypeCode() {
		return agrmtTypeCode;
	}

	public void setAgrmtTypeCode(String agrmtTypeCode){
		this.agrmtTypeCode = agrmtTypeCode;
	}	 
}
