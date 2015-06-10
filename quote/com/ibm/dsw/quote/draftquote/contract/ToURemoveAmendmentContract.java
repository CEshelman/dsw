package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class ToURemoveAmendmentContract extends QuoteBaseContract {

	private String lob;
	private boolean isOnlySaaSParts;
	private boolean isChannelQuote;
	private boolean isSubmittedQuote;
	private boolean disableFlag;
	
	private String touName;
	
	private String touURL;
	
	private String termsType;
	
	private String termsSubType;
	
	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public boolean isOnlySaaSParts() {
		return isOnlySaaSParts;
	}

	public void setOnlySaaSParts(boolean isOnlySaaSParts) {
		this.isOnlySaaSParts = isOnlySaaSParts;
	}

	public boolean isChannelQuote() {
		return isChannelQuote;
	}

	public void setChannelQuote(boolean isChannelQuote) {
		this.isChannelQuote = isChannelQuote;
	}

	public boolean isSubmittedQuote() {
		return isSubmittedQuote;
	}

	public void setSubmittedQuote(boolean isSubmittedQuote) {
		this.isSubmittedQuote = isSubmittedQuote;
	}

	private String webQuoteNum = null;
	private String attchmtSeqNum = null;

	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}

	public String getAttchmtSeqNum() {
		return attchmtSeqNum;
	}

	public void setAttchmtSeqNum(String attchmtSeqNum) {
		this.attchmtSeqNum = attchmtSeqNum;
	}
	
    public String getTouName() {
		return touName;
	}

	public void setTouName(String touName) {
		this.touName = touName;
	}

	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        webQuoteNum = parameters.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM);
        attchmtSeqNum = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_ATTCH_SEQ_NUM);
        
	 	lob = parameters.getParameterAsString("lob");
		isChannelQuote = parameters.getParameterAsBoolean("isChannelQuote");
		isSubmittedQuote = parameters.getParameterAsBoolean("isSubmittedQuote");  		 	
	 	isOnlySaaSParts = parameters.getParameterAsBoolean("isOnlySaaSParts");
	 	touName = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_TOU_NAME);
	 	touURL = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_TOU_URL);
	 	termsType = parameters.getParameterAsString(DraftQuoteParamKeys.TERMS_TYPE);
	 	termsSubType = parameters.getParameterAsString(DraftQuoteParamKeys.TERMS_SUBTYPE);
	 	
	 	disableFlag = parameters.getParameterAsBoolean("disableFlag");
    }

	public String getTouURL() {
		return touURL;
	}

	public void setTouURL(String touURL) {
		this.touURL = touURL;
	}

	public String getTermsSubType() {
		return termsSubType;
	}

	public void setTermsSubType(String termsSubType) {
		this.termsSubType = termsSubType;
	}

	public String getTermsType() {
		return termsType;
	}

	public void setTermsType(String termsType) {
		this.termsType = termsType;
	}

	public boolean isDisableFlag() {
		return disableFlag;
	}

	public void setDisableFlag(boolean disableFlag) {
		this.disableFlag = disableFlag;
	}

}
