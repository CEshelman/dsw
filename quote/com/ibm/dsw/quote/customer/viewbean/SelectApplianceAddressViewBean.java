package com.ibm.dsw.quote.customer.viewbean;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.config.CustomerViewKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

public class SelectApplianceAddressViewBean extends CustomerViewBean {

	private static final long serialVersionUID = -1086744198954086348L;
	protected String addressType;
	protected String isSubmittedQuote;
	
	public final String LOB_NAME = ParamKeys.PARAM_LINE_OF_BUSINESS;
	public final String COUNTRY_NAME = ParamKeys.PARAM_COUNTRY;
	public final String QUOTE_NUM_NAME = ParamKeys.PARAM_QUOTE_NUM;
	public final String SEARCH_FOR_NAME = CustomerParamKeys.SEARCH_FOR;
	public final String MIGRATION_CODE_NAME = DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE;
	
	
	public static String getReturnUrl() {
		return HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB);
	}
	
	public String getCustomUrlParams() {
		String customeUrlParams = "&amp;country=" + escapeHtml(getCountry() ) + 
		"&amp;lob=" + escapeHtml(getLob() ) +
		"&amp;quoteNum=" + escapeHtml(getQuoteNum() ) +
		"&amp;addressType=" + escapeHtml(addressType);
		
		if(Boolean.valueOf(this.isSubmittedQuote))
			customeUrlParams += "&amp;isSubmittedQuote=true";
		return customeUrlParams;			
	}
	
	
	/** 
	 * Attribute search form 
	 */
	
	public final String ATTR_FORM_ID = CustomerViewKeys.CUST_SRCH_ATTR_FORM;
	public final String BYNAME_NAME = ParamKeys.PARAM_CUST_NAME;
	public final String BYNAME_INPUT_ID = ParamKeys.PARAM_CUST_NAME;
	public final String BYSTATE_NAME = ParamKeys.PARAM_STATE;
	
	public String attrFormUrl() {
		return addressSearchUrl(CustomerConstants.SEARCH_BY_ATTR);
	}
	
	
	/** 
	 * Customer number search form 
	 */
	
	public final String CUST_FORM_ID = CustomerViewKeys.CUST_SRCH_DSWID_FORM;
	public final String BYCUSTNUM_NAME = ParamKeys.PARAM_SITE_NUM;
	public final String BYCUSTNUM_INPUT_ID = ParamKeys.PARAM_SITE_NUM;
	
	public String customerNumFormUrl() {
		return addressSearchUrl(CustomerConstants.SEARCH_BY_SITE_NUM);
	}
	

	/** 
	 * Agreement number search form 
	 */
	
	public final String AGREEMENT_FORM_ID = CustomerViewKeys.CUST_SRCH_AGREEMENT_FORM;
	public final String BYAGREEMENT_NAME = ParamKeys.PARAM_AGREEMENT_NUM;
	
	/** Display agreement number search option only if 
	 *  sold-to Customer is in PA and quote is not in FCT */
	public boolean displayAgreementForm() {
		return isPA() && !isFCT();
	}
	
	public String agreementFormUrl() {
		return addressSearchUrl(CustomerConstants.SEARCH_BY_AGREEMENT);
	}
	
	/** Common functions */
	
	@Override
	public void collectResults(Parameters param) throws ViewBeanException {
		super.collectResults(param);
		addressType = (String) param.getParameter(ParamKeys.PARAM_ADDRESS_TYPE);
		isSubmittedQuote = (String) param.getParameter(ParamKeys.PARAM_IS_SBMT_QT);
	}
	
	protected String addressSearchUrl(String searchFor) {		
		String baseUrl = HtmlUtil.getURLForAction(CustomerActionKeys.APPL_ADDRESS_SEARCH);
		
		StringBuffer url = new StringBuffer(baseUrl);
		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_COUNTRY, getCountry() );
		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_QUOTE_NUM, getQuoteNum() );
		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, getLob() );
		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_ADDRESS_TYPE, addressType);
		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_SEARCH_FOR, searchFor);
		HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_AGREEMENT_NUM, getAgreementNumber());
		if(Boolean.valueOf(this.isSubmittedQuote))
			HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_IS_SBMT_QT, getIsSubmittedQuote());
		
		return url.toString();
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
