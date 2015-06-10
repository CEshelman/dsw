package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.Locale;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;


/**
 * 
 * @author jason
 * 
 */
public class AddLPPFormViewBean extends BaseViewBean {
	private String quoteNumber;
	private String lineItemSeqNumber;
	private String quoteCurrencyCode;
	private String lppCurrencyCode;
	private String lpp;
	private String srcCode;
	private String renewalNum;
	private ArrayList missingReasons;
	private Locale locale;
	private String priorPrice;
	private String systemPriorPrice;
	private String gdPartFlag;

	public String getSrcCode() {
		return srcCode;
	}

	public void setSrcCode(String srcCode) {
		this.srcCode = srcCode;
	}

	public ArrayList getMissingReasons() {
		return missingReasons;
	}

	public void setMissingReasons(ArrayList missingReasons) {
		this.missingReasons = missingReasons;
	}

	public String getLpp() {
		return lpp;
	}

	public void setLpp(String lpp) {
		this.lpp = lpp;
	}

	public String getQuoteCurrencyCode() {
		return quoteCurrencyCode;
	}

	public void setQuoteCurrencyCode(String quoteCurrencyCode) {
		this.quoteCurrencyCode = quoteCurrencyCode;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	public String getLineItemSeqNumber() {
		return lineItemSeqNumber;
	}

	public void setLineItemSeqNumber(String lineItemSeqNumber) {
		this.lineItemSeqNumber = lineItemSeqNumber;
	}
	
	public String getLPPCurrencyCode() {
		return lppCurrencyCode;
	}

	public void collectResults(Parameters params) throws ViewBeanException {
		locale = (Locale)params.getParameter(ParamKeys.PARAM_LOCAL);
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM) != null) {
			this.setQuoteNumber(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM));
		}
		if (params.getParameterAsString(DraftQuoteParamKeys.LINE_SEQ_NUM) != null) {
			this.setLineItemSeqNumber(params
					.getParameterAsString(DraftQuoteParamKeys.LINE_SEQ_NUM));
		}
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_CURRENCY_CODE) != null) {
			this.setQuoteCurrencyCode(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_CURRENCY_CODE));
		}
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP) != null) {
			this.setLpp(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP));
		}
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_PRIOR_PRICE) != null) {
			this.setPriorPrice(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_PRIOR_PRICE));
		}
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_RENEWAL_NUM) != null) {
			this.setRenewalNum(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_RENEWAL_NUM));
		}
		if (params.getParameter(DraftQuoteParamKeys.PARAM_SYSTEM_PRIOR_PRICE) != null ){
			this.setSystemPriorPrice((String)params.getParameter(DraftQuoteParamKeys.PARAM_SYSTEM_PRIOR_PRICE));
		}
		if (params.getParameter(DraftQuoteParamKeys.PARAM_GROWTH_YTY_PART_FLAG) != null ){
			this.setGdPartFlag(params.getParameterAsString(DraftQuoteParamKeys.PARAM_GROWTH_YTY_PART_FLAG));
		}
		
		
		lppCurrencyCode = params.getParameterAsString(DraftQuoteParamKeys.PARAM_LPP_CURRENCY_CODE);
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_SRC_CODE) != null) {
			this.setSrcCode(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_SRC_CODE));
			missingReasons= new ArrayList();
			// TODO: Please update to get the list from ebiz1.web_appl_cnstnt
			if (DraftQuoteConstants.LPP_POPUP_SOURCE_CURRENCY_CONVERSION.equals(srcCode)) {
				missingReasons.add(new SelectOptionImpl("Currency conversion", "60", true));
			} else {
				missingReasons.add(new SelectOptionImpl("Site migration", "10", true));
				missingReasons.add(new SelectOptionImpl("Complex part evolution", "20", false));
				missingReasons.add(new SelectOptionImpl("Compression", "30", false));
				missingReasons.add(new SelectOptionImpl("Zero value order", "40", false));
				missingReasons.add(new SelectOptionImpl("Multiple currencies", "50", false));
				missingReasons.add(new SelectOptionImpl("Currency conversion", "60", false));
				missingReasons.add(new SelectOptionImpl("Incorrect system price", "70", false));
				missingReasons.add(new SelectOptionImpl("Other", "100", false));
			}
		}
	}
	
	public boolean isNoSystemComputedLPP(){
		return DraftQuoteConstants.LPP_POPUP_SOURCE_NO_LPP.equals(srcCode);
	}
	
	public boolean isCurrencyConversion(){
		if(gdPartFlag != null && "isAddedToLicPart".equals(gdPartFlag)){
			return true;
		}
		return DraftQuoteConstants.LPP_POPUP_SOURCE_CURRENCY_CONVERSION.equals(srcCode);
	}
	
    public String getI18NString(String baseName, String key){
    	;
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        
        String value = appCtx.getI18nValueAsString(baseName, locale, key);

        if (value == null || value.equals("")) {
            value = key;
        }

        return value;
    }

	public String getRenewalNum() {
		return renewalNum;
	}

	public void setRenewalNum(String renewalNum) {
		this.renewalNum = renewalNum;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getPriorPrice() {
		return priorPrice;
	}

	public void setPriorPrice(String priorPrice) {
		this.priorPrice = priorPrice;
	}

	public String getSystemPriorPrice() {
		return systemPriorPrice;
	}

	public void setSystemPriorPrice(String systemPriorPrice) {
		this.systemPriorPrice = systemPriorPrice;
	}

	public String getGdPartFlag() {
		return gdPartFlag;
	}

	public void setGdPartFlag(String gdPartFlag) {
		this.gdPartFlag = gdPartFlag;
	}

	
	
	
	
	
    
    
}
