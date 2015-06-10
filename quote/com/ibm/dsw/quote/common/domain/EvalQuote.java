package com.ibm.dsw.quote.common.domain;

public interface EvalQuote extends Domain {
	public String getWebQuoteNum(); 
	public void setWebQuoteNum(String webQuoteNum);
	public String getQuoteTypeCode();
	public void setQuoteTypeCode(String quoteTypeCode);
	public String getSoldToCustNum();
	public void setSoldToCustNum(String soldToCustNum);
	public String getCustName();
	public void setCustName(String custName);
	public String getEvalEmailAdr();
	public void setEvalEmailAdr(String evalEmailAdr);
	public String getQuoteTitle();
	public void setQuoteTitle(String quoteTitle);
	public String getQuoteDscr();
	public void setQuoteDscr(String quoteDscr);
	public String getQuoteStageCode();
	public void setQuoteStageCode(String quoteStageCode);
	public String getFullName();
	public void setFullName(String fullName);
	public String getProgCode();
	public void setProgCode(String progCode);
	public String getAgrmtTypeCode();
	public void setAgrmtTypeCode(String agrmtTypeCode);
}
