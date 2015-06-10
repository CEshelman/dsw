package com.ibm.dsw.quote.customer.viewbean;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;

public class DisplayApplianceAddressViewBean extends CustomerViewBean {

	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private String custName1;
	private String custName2;
	private String address1;
	private String address2;
	private String city;
	private String postalCode;
	private String phoneNum;
	private String addressType;
	private boolean isNeedFillForState = false;
	private String isSubmittedQuote;
 	private boolean isPostalCodeRequired = false;

	public String getReturnToQuoteURL(){
		if(Boolean.valueOf(this.getIsSubmittedQuote())){
			return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB)+"&quoteNum="+this.getQuoteNum();
		} else {
			return HtmlUtil .getURLForAction(DraftQuoteActionKeys.DISPLAY_CURRENT_DRAFT_QUOTE);
		}
	}

	@Override
	public void collectResults(Parameters param) throws ViewBeanException {
		super.collectResults(param);

		if (QuoteConstants.IND.equalsIgnoreCase(super.getCountry())
				|| QuoteConstants.USA.equalsIgnoreCase(super.getCountry())
				|| QuoteConstants.CAN.equalsIgnoreCase(super.getCountry())) {
			this.setNeedFillForState(true);
		}
		
		this.setAddressType((String)param.getParameter(ParamKeys.PARAM_ADDRESS_TYPE));
		this.setAddress1((String)param.getParameter(ParamKeys.PARAM_ADDRESS1));
		this.setAddress2((String)param.getParameter(ParamKeys.PARAM_ADDRESS2));
		this.setFirstName((String)param.getParameter(ParamKeys.PARAM_CNT_FNAME));
		this.setLastName((String)param.getParameter(ParamKeys.PARAM_CNT_LNAME));
		this.setCity((String)param.getParameter(ParamKeys.PARAM_CITY));
		this.setState((String)param.getParameter(ParamKeys.PARAM_STATE));
		this.setCountry((String)param.getParameter(ParamKeys.PARAM_COUNTRY));
		this.setPostalCode((String)param.getParameter(ParamKeys.PARAM_POSTAL_CODE));
		this.setPhoneNum((String)param.getParameter(ParamKeys.PARAM_CNT_PHONE));
		this.setCustName1((String)param.getParameter(ParamKeys.PARAM_COMPANY_NAME1));
		this.setCustName2((String)param.getParameter(ParamKeys.PARAM_COMPANY_NAME2));
		this.setIsSubmittedQuote((String)param.getParameter(ParamKeys.PARAM_IS_SBMT_QT));
		
		Country countryObject = this.getCountryObj();
		this.setPostalCodeRequired(countryObject.getPostalCodeRequiredFlag());
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCustName1() {
		return custName1;
	}

	public void setCustName1(String custName1) {
		this.custName1 = custName1;
	}

	public String getCustName2() {
		return custName2;
	}

	public void setCustName2(String custName2) {
		this.custName2 = custName2;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public boolean isNeedFillForState() {
		return isNeedFillForState;
	}

	public void setNeedFillForState(boolean isNeedFillForState) {
		this.isNeedFillForState = isNeedFillForState;
	}
	
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getSearchApplianAddrUrl() {
		String url = "&amp;country=" + escapeHtml(this.getCountry()) + "&amp;lob="
				+ escapeHtml(this.getLob()) + "&amp;quoteNum="
				+ escapeHtml(this.getQuoteNum()) + "&amp;addressType="
				+ escapeHtml(this.getAddressType());
		
		if(Boolean.valueOf(this.isSubmittedQuote))
			url = url+"&amp;isSubmittedQuote="
			+ escapeHtml(this.getIsSubmittedQuote());
		return url;
	}
	
	public String getCusCountryForShow(){
		String str_country = null;
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		CacheProcess cProcess = null;
		Country cntryObj = null;
		try {
			cProcess = CacheProcessFactory.singleton().create();
			cntryObj = cProcess.getCountryByCode3(this.getCountry());
			str_country = cntryObj.getDesc();
		} catch (QuoteException e) {
			logContext.error(this, e.getMessage());

		}
		return str_country;
	}
	
	public String getCheckDupApplianceAddressUrl() {
		return HtmlUtil.getURLForAction(CustomerActionKeys.CHECK_DUP_APPLIANCE_ADDRESS);
	}

	public String getIsSubmittedQuote() {
		return isSubmittedQuote;
	}

	public void setIsSubmittedQuote(String isSubmittedQuote) {
		this.isSubmittedQuote = isSubmittedQuote;
	}
	
	public String getLink(){
		String linkID = "current_draft_quote";
		String iSubmittedQuote = this.getIsSubmittedQuote();
		if(Boolean.valueOf(iSubmittedQuote)){
			linkID = "quote_status";
		}
		return linkID;
	}
	
	private Country getCountryObj() throws ViewBeanException{
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		Country countryObject = null;
		try {
			CacheProcess process = CacheProcessFactory.singleton().create();
			countryObject = process.getCountryByCode3(this.getCountry());
		} catch (QuoteException e) {
			logContext.error(this, e.getMessage());
            throw new ViewBeanException("error getting data from cache");
		}
		
		return countryObject;
	}

	public boolean isPostalCodeRequired() {
		return isPostalCodeRequired;
	}

	public void setPostalCodeRequired(boolean isPostalCodeRequired) {
		this.isPostalCodeRequired = isPostalCodeRequired;
	}
	
	
}
