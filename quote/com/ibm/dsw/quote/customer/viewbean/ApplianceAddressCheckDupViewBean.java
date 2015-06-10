package com.ibm.dsw.quote.customer.viewbean;

import java.util.List;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class ApplianceAddressCheckDupViewBean extends BaseViewBean {
	private static final long serialVersionUID = 1L;

	private String country;
	private String lob;
	private String quoteNum;
	private String addressType;
	private String companyName1;
	private String companyName2;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String postalCode;
	private String cntFirstName;
	private String cntLastName;
	private String cntPhoneNumFull;
	private transient List customers = null;
	private String isSubmittedQuote;

	public void collectResults(Parameters param) throws ViewBeanException {
		super.collectResults(param);

		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		SearchResultList resultList = (SearchResultList) param
				.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
		if (resultList != null) {
			customers = resultList.getResultList();

			if (logCtx instanceof QuoteLogContextLog4JImpl) {
				if (((QuoteLogContextLog4JImpl) logCtx).isDebug(this)) {
					logCtx.debug(this, "getResultList is not null");
				}
			}
		} else {
			if (logCtx instanceof QuoteLogContextLog4JImpl) {
				if (((QuoteLogContextLog4JImpl) logCtx).isDebug(this)) {
					logCtx.debug(this, "getResultList is null");
				}
			}
		}

		this.setLob((String) param
				.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS));
		this.setCountry((String) param.getParameter(ParamKeys.PARAM_COUNTRY));
		this.setQuoteNum((String) param.getParameter(ParamKeys.PARAM_QUOTE_NUM));
		this.setAddressType((String) param
				.getParameter(ParamKeys.PARAM_ADDRESS_TYPE));
		this.setCompanyName1((String) param
				.getParameter(ParamKeys.PARAM_COMPANY_NAME1));
		this.setCompanyName2((String) param
				.getParameter(ParamKeys.PARAM_COMPANY_NAME2));
		this.setAddress1((String) param.getParameter(ParamKeys.PARAM_ADDRESS1));
		this.setAddress2((String) param.getParameter(ParamKeys.PARAM_ADDRESS2));
		this.setCity((String) param.getParameter(ParamKeys.PARAM_CITY));
		this.setState((String) param.getParameter(ParamKeys.PARAM_STATE));
		this.setPostalCode((String) param
				.getParameter(ParamKeys.PARAM_POSTAL_CODE));
		this.setCntFirstName((String) param
				.getParameter(ParamKeys.PARAM_CNT_FNAME));
		this.setCntLastName((String) param
				.getParameter(ParamKeys.PARAM_CNT_LNAME));
		this.setCntPhoneNumFull((String) param
				.getParameter(ParamKeys.PARAM_CNT_PHONE));
		this.setIsSubmittedQuote((String)param.getParameter(ParamKeys.PARAM_IS_SBMT_QT));
	}

	/**
	 * @return Returns the address1.
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1
	 *            The address1 to set.
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return Returns the address2.
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2
	 *            The address2 to set.
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @param addressType
	 *            The addressType to set.
	 */
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	/**
	 * @return Returns the addressType.
	 */
	public String getAddressType() {
		return addressType;
	}

	/**
	 * @return Returns the cntFirstName.
	 */
	public String getCntFirstName() {
		return cntFirstName;
	}

	/**
	 * @param cntFirstName
	 *            The cntFirstName to set.
	 */
	public void setCntFirstName(String cntFirstName) {
		this.cntFirstName = cntFirstName;
	}

	/**
	 * @return Returns the cntLastName.
	 */
	public String getCntLastName() {
		return cntLastName;
	}

	/**
	 * @param cntLastName
	 *            The cntLastName to set.
	 */
	public void setCntLastName(String cntLastName) {
		this.cntLastName = cntLastName;
	}

	/**
	 * @return Returns the cntPhoneNumFull.
	 */
	public String getCntPhoneNumFull() {
		return cntPhoneNumFull;
	}

	/**
	 * @param cntPhoneNumFull
	 *            The cntPhoneNumFull to set.
	 */
	public void setCntPhoneNumFull(String cntPhoneNumFull) {
		this.cntPhoneNumFull = cntPhoneNumFull;
	}

	/**
	 * @return Returns the companyName1.
	 */
	public String getCompanyName1() {
		return companyName1;
	}

	/**
	 * @param companyName
	 *            The companyName1 to set.
	 */
	public void setCompanyName1(String companyName1) {
		this.companyName1 = companyName1;
	}

	/**
	 * @return Returns the companyName2.
	 */
	public String getCompanyName2() {
		return companyName2;
	}

	/**
	 * @param companyName
	 *            The companyName2 to set.
	 */
	public void setCompanyName2(String companyName2) {
		this.companyName2 = companyName2;
	}

	/**
	 * @return Returns the country.
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            The country to set.
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return Returns the lob.
	 */
	public String getLob() {
		return lob;
	}

	/**
	 * @param lob
	 *            The lob to set.
	 */
	public void setLob(String lob) {
		this.lob = lob;
	}

	/**
	 * @return Returns the postalCode.
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode
	 *            The postalCode to set.
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return Returns the quoteNum.
	 */
	public String getQuoteNum() {
		return quoteNum;
	}

	/**
	 * @param quoteNum
	 *            The quoteNum to set.
	 */
	public void setQuoteNum(String quoteNum) {
		this.quoteNum = quoteNum;
	}

	/**
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return Returns the customers.
	 */
	public List getCustomers() {
		return customers;
	}

	public String getSelectApplianceAddressURL(String customerNum) {

		String baseUrl = HtmlUtil.getURLForAction(CustomerActionKeys.DISPLAY_APPLIANCE_ADDRESS);
    	StringBuffer url = new StringBuffer(baseUrl);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_CUSTNUM, customerNum);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_ADDRESS_TYPE, addressType);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_COUNTRY, country);

    	return url.toString();
	}

	public String getReturnToQuoteURL() {
		return HtmlUtil
				.getURLForAction(DraftQuoteActionKeys.DISPLAY_CURRENT_DRAFT_QUOTE);
	}

	public String getIsSubmittedQuote() {
		return isSubmittedQuote;
	}

	public void setIsSubmittedQuote(String isSubmittedQuote) {
		this.isSubmittedQuote = isSubmittedQuote;
	}
	
	
}