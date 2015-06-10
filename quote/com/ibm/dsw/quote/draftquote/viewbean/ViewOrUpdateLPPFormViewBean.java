package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PriorSSPriceContract;
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
public class ViewOrUpdateLPPFormViewBean extends BaseViewBean {
	private String quoteNumber;
	private String lineItemSeqNumber;
	private String quoteCurrencyCode;
	private String lppCurrencyCode;
	private String lpp;
	private String srcCode;
	private String renewalNum;
	private ArrayList<SelectOptionImpl> missingReasons;
	private PriorSSPriceContract contract;
	private String systemPriorPrice;
	private String gdPartFlag;

	public PriorSSPriceContract getContract() {
		return contract;
	}

	public void setContract(PriorSSPriceContract contract) {
		this.contract = contract;
	}

	public String getSrcCode() {
		return srcCode;
	}

	public void setSrcCode(String srcCode) {
		this.srcCode = srcCode;
	}

	public ArrayList<SelectOptionImpl> getMissingReasons() {
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

	public void collectResults(Parameters params) throws ViewBeanException {
		super.collectResults(params);
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM) != null) {
			this.setQuoteNumber(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM));
		}
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_RENEWAL_NUM) != null) {
			this.setRenewalNum(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_RENEWAL_NUM));
		}
		
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_LPP_CURRENCY_CODE) != null) {
			this.lppCurrencyCode = (params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_LPP_CURRENCY_CODE));
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
					.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_OLD_LPP).replace(",", "")); //the "," contain in the lpp value);
		}
		if (params
				.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_SRC_CODE) != null) {
			this.setSrcCode(params
					.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_SRC_CODE));
			 
		}
		if (params.getParameterAsString(DraftQuoteParamKeys.PARAM_SYSTEM_PRIOR_PRICE) != null){
			this.setSystemPriorPrice(params.getParameterAsString(DraftQuoteParamKeys.PARAM_SYSTEM_PRIOR_PRICE));
		}
		
		if (params.getParameterAsString(DraftQuoteParamKeys.PARAM_GROWTH_YTY_PART_FLAG) != null){
			this.setGdPartFlag(params.getParameterAsString(DraftQuoteParamKeys.PARAM_GROWTH_YTY_PART_FLAG));
		}
		
		if (params
				.getParameter(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_YTY_OBJECT) != null) {
			this.setContract((PriorSSPriceContract) params
					.getParameter(DraftQuoteParamKeys.PARAM_WEB_QUOTE_LINEITEM_YTY_OBJECT));
		}
		missingReasons= new ArrayList<SelectOptionImpl>();
		
		if(isCurrencyConversion() && (!"isAddedToLicPart".equals(gdPartFlag))){
			missingReasons.add(new SelectOptionImpl(MissingReasonType.TYPE_CURRENCY_CONVERSION_60, "60", true));
		} else {
			missingReasons.add(new SelectOptionImpl(MissingReasonType.TYPE_SITE_MIGRATION_10, "10", false));
			missingReasons.add(new SelectOptionImpl(MissingReasonType.TYPE_COMPLEX_PART_EVOLUTION_20, "20", false));
			missingReasons.add(new SelectOptionImpl(MissingReasonType.TYPE_COMPRESSION_30, "30", false));
			missingReasons.add(new SelectOptionImpl(MissingReasonType.TYPE_ZERO_VALUE_ORDE_40, "40", false));
			missingReasons.add(new SelectOptionImpl(MissingReasonType.TYPE_MULTIPLE_CURRENCIES_50, "50", false));
			missingReasons.add(new SelectOptionImpl(MissingReasonType.TYPE_CURRENCY_CONVERSION_60, "60", false));
			missingReasons.add(new SelectOptionImpl(MissingReasonType.TYPE_INCORRECT_SYSTEM_PRICE_70, "70", false));
			missingReasons.add(new SelectOptionImpl(MissingReasonType.TYPE_OTHER_100, "100", false));
			
			for(int i = 0; i < missingReasons.size(); i++){
				SelectOptionImpl so = (SelectOptionImpl)missingReasons.get(i);
				if(StringUtils.trimToEmpty(contract.getLppMissReas()).equals(so.getValue())){
					so.setSelected(true);
				}
			}
		}
	}
	
	public boolean isCurrencyConversion(){
		if(contract == null){
			return false;
		}
		if(gdPartFlag != null && "isAddedToLicPart".equals(gdPartFlag)){
			return true;
		}
		return DraftQuoteConstants.LPP_POPUP_SOURCE_CURRENCY_CONVERSION.equals(contract.getYtySrcCode());
	}
	
	public boolean isNoSystemComputedLPP(){
		if(contract == null){
			return false;
		}
		return DraftQuoteConstants.LPP_POPUP_SOURCE_NO_LPP.equals(contract.getYtySrcCode());
	}
	
	public String getLPPCurrencyCode(){
		return lppCurrencyCode;
	}
	
    public String getI18NString(String baseName, String key){
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
