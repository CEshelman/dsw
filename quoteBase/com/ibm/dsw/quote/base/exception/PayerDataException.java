package com.ibm.dsw.quote.base.exception;

import com.ibm.dsw.quote.base.config.ErrorKeys;

public class PayerDataException extends PriceEngineUnAvailableException {

	private boolean isPayerDataIssue = false;

	private String payerDataIssueMsg;

	public PayerDataException(boolean isPayerDataIssue, String payerDataIssueMsg) {
		this.isPayerDataIssue = isPayerDataIssue;
		this.payerDataIssueMsg = payerDataIssueMsg;

		if (isPayerDataIssue) {
			messageKey = ErrorKeys.MSG_PRICE_ENGINE_UNAVAILABLE_FOR_PAYER_DATA_ISSUE;
		}
	}

	public boolean isPayerDataIssue() {
		return isPayerDataIssue;
	}

	public String getPayerDataIssueMsg() {
		return payerDataIssueMsg;
	}
}
