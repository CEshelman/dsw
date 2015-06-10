package com.ibm.dsw.quote.customer.viewbean;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
	

public class ApplianceAddressResultsViewBean extends CustomerResultsViewBean {

	private static final long serialVersionUID = 811176108947848917L;
	public static final String BLANK = "&nbsp;";
	
	String addressType, siteNum;
	private String isSubmittedQuote;
	
	
	public void collectResults(Parameters param) throws ViewBeanException {
		quoteNum = (String) param.getParameter(ParamKeys.PARAM_QUOTE_NUM);
		lob = (String) param.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS);
		country = (String) param.getParameter(ParamKeys.PARAM_COUNTRY);
		addressType = (String) param.getParameter(ParamKeys.PARAM_ADDRESS_TYPE);
		searchFor = (String) param.getParameter(ParamKeys.PARAM_SEARCH_FOR);
		
        countryDesc = ((Country) param.getParameter(ParamKeys.PARAM_COUNTRY_OBJECT)).getDesc();
        customerName = (String) param.getParameter(ParamKeys.PARAM_CUST_NAME);
        state = (String) param.getParameter(ParamKeys.PARAM_STATE);
        siteNumber = (String) param.getParameter(ParamKeys.PARAM_SITE_NUM);
        agreementNumber = (String) param.getParameter(ParamKeys.PARAM_AGREEMENT_NUM);
        isSubmittedQuote = (String)param.getParameter(ParamKeys.PARAM_IS_SBMT_QT);
        
		startPos = ((Integer) param.getParameter(ParamKeys.PARAM_START_POSITION)).intValue() + 1;
      
		CustomerSearchResultList resultList = (CustomerSearchResultList) param.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
		if (resultList != null) {
			customers = resultList.getResultList();
			customerCount = resultList.getResultCount();
			displayLob = resultList.getLob();
			endPos = startPos + customers.size() - 1;
			if (customerCount == 0) {
				startPos = 0; endPos = 0;
			}
		}
	}
	
	
	private StringBuffer getDisplayParams(StringBuffer url) {
		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_COUNTRY, country);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_ADDRESS_TYPE, addressType);
    	if(Boolean.valueOf(this.isSubmittedQuote))
    		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_IS_SBMT_QT, "true");
    	return url;
	}
	
	private StringBuffer getSearchParams(StringBuffer url) {
		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_SEARCH_FOR, searchFor);
		HtmlUtil.addURLParam(url, ParamKeys.PARAM_CUST_NAME, customerName);
		HtmlUtil.addURLParam(url, ParamKeys.PARAM_STATE, state);
		HtmlUtil.addURLParam(url, ParamKeys.PARAM_SITE_NUM, siteNum);
		return url;
	}
	
	public String getChangeCriteriaURL() {
		String baseUrl = HtmlUtil.getURLForAction(
				CustomerActionKeys.DISPLAY_SEARCH_APPL_ADDRESS);
    	StringBuffer url = new StringBuffer(baseUrl);
    	return getDisplayParams(url).toString();
	}
	
	public String getTabParams() {
        StringBuffer tabParam = new StringBuffer();
        HtmlUtil.addURLParam(tabParam, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
        HtmlUtil.addURLParam(tabParam, ParamKeys.PARAM_COUNTRY, country);
        HtmlUtil.addURLParam(tabParam, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
        HtmlUtil.addEncodeURLParam(tabParam, ParamKeys.PARAM_ADDRESS_TYPE, addressType);
    	if(Boolean.valueOf(this.isSubmittedQuote))
    		HtmlUtil.addEncodeURLParam(tabParam, ParamKeys.PARAM_IS_SBMT_QT, "true");
        return tabParam.toString();
	}
	
	/** Page navigation */
	
	public boolean isDisplayPrevious() { return startPos > 1; }
	
	public String getUrlPrevious() {
		String baseUrl = HtmlUtil.getURLForAction(CustomerActionKeys.APPL_ADDRESS_SEARCH);
		StringBuffer url = new StringBuffer(baseUrl);
		url =  getSearchParams(getDisplayParams(url) );
		
		int prePos = startPos - CustomerConstants.PAGE_ROW_COUNT - 1;		
		String urlPrevious = url.toString() + HtmlUtil.addURLParam(null, ParamKeys.PARAM_START_POSITION, String.valueOf(prePos) );
		return urlPrevious;
	}
	
	public boolean isDisplayNext() { return endPos < customerCount;  }
	
	public String getUrlNext() {
		String baseUrl = HtmlUtil.getURLForAction(CustomerActionKeys.APPL_ADDRESS_SEARCH);
		StringBuffer url = new StringBuffer(baseUrl);
		url =  getSearchParams(getDisplayParams(url) );

		String urlNext = url.toString() + HtmlUtil.addURLParam(null, ParamKeys.PARAM_START_POSITION, String.valueOf(endPos) ).toString();
		return urlNext;
	}
	
	public String getReturnUrl() {
		String baseUrl = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB);
		if(Boolean.valueOf(this.isSubmittedQuote))
			baseUrl = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB);
    	return String.format("%s&amp;quoteNum=%s", baseUrl, escapeHtml(quoteNum) );
	}
	
	public boolean isEmptyCustomers() {
		return customers == null || customers.size() == 0;
	}
	
	/** Get contract list, or create a new list with a single dummy contract. 
	 * Used for uniformly iterating over Customer's contracts */
	public List<?> getContracts(Customer c) {
		List contracts = c.getContractList();
		if (contracts == null) contracts = new ArrayList<Contract>(1);
		if (contracts.isEmpty() ) {
			Contract dummy = new Contract(BLANK, BLANK, BLANK, BLANK);
			contracts.add(dummy);
		}
		
		return contracts;
	}
	
	public String getResultsMsgKey() {
		return (isEmptyCustomers() ) ? "no_customer_data_hint" : "selected_customer_data_hint";
	}
	
	public String getGsaStatusFlagKey(Customer c) {
		return (c.getGsaStatusFlag() ) ? "yes" : "no";
	}
	
	
	public boolean displayAgreementCol() {
		return isPA() || isOEM() || isSSPType();
	}
	
	public boolean displayAgreementNumCol() {
		return !displayAgreementCol() && isFCT();
	}

    protected String getChangeCriteriaBaseUrl() {
		String baseUrl = HtmlUtil.getURLForAction(
				CustomerActionKeys.DISPLAY_SEARCH_APPL_ADDRESS);
    	StringBuffer url = new StringBuffer(baseUrl);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_COUNTRY, country);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_ADDRESS_TYPE, addressType);

    	return url.toString();
    }
    
    public String getSelectCustomerURL(String customerNum) {
    	String baseUrl = HtmlUtil.getURLForAction(CustomerActionKeys.DISPLAY_APPLIANCE_ADDRESS);
    	StringBuffer url = new StringBuffer(baseUrl);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_CUSTNUM, customerNum);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_ADDRESS_TYPE, addressType);
    	HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_COUNTRY, country);
    	if(Boolean.valueOf(this.isSubmittedQuote))
    		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_IS_SBMT_QT, "true");
    	
    	return url.toString();
    }

    public String getLinkID(){
    	String linkID = "current_draft_quote";
    	if(Boolean.valueOf(this.isSubmittedQuote))
    		linkID = "quote_status";    	
    	return linkID;
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