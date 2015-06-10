package com.ibm.dsw.quote.customer.contract;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.common.domain.ApplianceAddress;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class ApplianceAddressContract extends QuoteBaseCookieContract {
	private static final long serialVersionUID = 8914087795740519734L;
	/**
	 * 
	 */
	private String quoteNum;
	private String addressType;
	private String country;
	private String lob;
	private String custNum;
	private String nonApplianceSecId;
	private String agreementNumber;
	private boolean readOnly = false;
	private String isSubmittedQuote;
	private List<ApplianceAddress> addressList;
	

	public String getAgreementNumber() {
		return agreementNumber;
	}

	public void setAgreementNumber(String agreementNumber) {
		this.agreementNumber = agreementNumber;
	}

	public String getQuoteNum() {
		return quoteNum;
	}

	public void setQuoteNum(String quoteNum) {
		this.quoteNum = quoteNum;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}
	
	public String getCustNum() {
		return custNum;
	}

	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}

	public String getNonApplianceSecId() {
		return nonApplianceSecId;
	}

	public void setNonApplianceSecId(String nonApplianceSecId) {
		this.nonApplianceSecId = nonApplianceSecId;
	}

	public List<ApplianceAddress> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<ApplianceAddress> addressList) {
		this.addressList = addressList;
	}
	
	public boolean getReadOnly() { return readOnly; }
	public void setReadOnly(String readOnly) { this.readOnly = Boolean.valueOf(readOnly); };

	// collect all parameter form page, every parameter is suffixed by "_(sequence number)"
	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        List<ApplianceAddress> addressList = new ArrayList<ApplianceAddress>();
        for(int i = 0 ;parameters.hasParameter(DraftQuoteParamKeys.PARAM_CNT_FIRST_NAME+"_"+i); i++ ){
        	ApplianceAddress address = new ApplianceAddress();
        	address.setCntFirstName(parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_CNT_FIRST_NAME+"_"+i));
        	address.setCntLastName(parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_CNT_LASE_NAME+"_"+i));
        	address.setSapIntlPhoneNumFull(parameters.getParameterAsString(ParamKeys.PARAM_CNT_PHONE+"_"+i));
        	address.setCustNum(parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_CUST_NUM+"_"+i));
        	address.setSapCntId(parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_SAP_CNT_ID+"_"+i));
        	address.setCntId(parameters.getParameterAsInt(DraftQuoteParamKeys.PARAM_CNT_ID+"_"+i));
        	address.setWebCustId(parameters.getParameterAsInt(DraftQuoteParamKeys.PARAM_WEB_CUST_ID+"_"+i));
	        address.setSecId(parameters.getParameterAsInt((DraftQuoteParamKeys.PARAM_SEC_ID+"_"+i)));
	        address.setQuoteLineItemSeqNumStr(parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_QUOTE_LINE_ITEM_SEQ_NUM+"_"+i));
	        addressList.add(address);
        }
        this.addressList = addressList;
        this.isSubmittedQuote = parameters.getParameterAsString(ParamKeys.PARAM_IS_SBMT_QT);
    }

	/**
	 * @return the isSubmittedQuote
	 */
	public String getIsSubmittedQuote() {
		return isSubmittedQuote;
	}

	/**
	 * @param isSubmittedQuote the isSubmittedQuote to set
	 */
	public void setIsSubmittedQuote(String isSubmittedQuote) {
		this.isSubmittedQuote = isSubmittedQuote;
	}
    
}
