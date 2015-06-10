package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class EvalQuoteStatusContract extends QuoteBaseCookieContract {
	private static final long serialVersionUID = 1L;

	private String audCode;
	private String creatorId;
	private String isTeleSales;

	@Override
	public void load(Parameters parameters, JadeSession session) {
		// TODO Auto-generated method stub
		super.load(parameters, session);
		QuoteUserSession quoteUserSession = super.getQuoteUserSession();
		this.audCode = quoteUserSession.getAudienceCode();
		
		if(quoteUserSession.isTeleSales()){
			this.creatorId = quoteUserSession.getSiteNumber();
			this.isTeleSales = "1";
		} else {
			this.creatorId = quoteUserSession.getUserId();
			this.isTeleSales = "0";
		}
	}

	public String getAudCode() {
		return audCode;
	}

	public void setAudCode(String audCode) {
		this.audCode = audCode;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getIsTeleSales() {
		return isTeleSales;
	}

	public void setIsTeleSales(String isTeleSales) {
		this.isTeleSales = isTeleSales;
	}
}
