package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class CreateNewQuoteContract extends QuoteBaseContract {


	private String chrgAgrmtNum = null;
	
	private String configId = null;
	
	private String customerNum = null;
	
	private String createType = null;
	
	private String isCreateNewQuote = null;

	public void load(Parameters parameters, JadeSession session) {
		super.load(parameters, session);
	}

	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}

	public void setChrgAgrmtNum(String chrgAgrmtNum) {
		this.chrgAgrmtNum = chrgAgrmtNum;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}
	
	public String getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}

	public String getCreateType() {
		return createType;
	}

	public void setCreateType(String createType) {
		this.createType = createType;
	}

	public String getIsCreateNewQuote() {
		return isCreateNewQuote;
	}

	public void setIsCreateNewQuote(String isCreateNewQuote) {
		this.isCreateNewQuote = isCreateNewQuote;
	}

}
