package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ViewQuoteTxtHistoryContract</code> class is holding view quote txt edit history parameters
 * 
 * @author qinfengc@cn.ibm.com
 * 
 * Created on 2010-12-21
 */
public class ViewQuoteTxtHistoryContract extends QuoteBaseCookieContract
{

	private static final long serialVersionUID = 913177966190009170L;
	
	private String quoteNum;
	private String txtTypeCode;
	public String getQuoteNum()
	{
		return quoteNum;
	}
	public void setQuoteNum(String quoteNum)
	{
		this.quoteNum = quoteNum;
	}
	public String getTxtTypeCode()
	{
		return txtTypeCode;
	}
	public void setTxtTypeCode(String txtTypeCode)
	{
		this.txtTypeCode = txtTypeCode;
	}
}
