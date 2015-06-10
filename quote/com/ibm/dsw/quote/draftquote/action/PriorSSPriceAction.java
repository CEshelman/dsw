package com.ibm.dsw.quote.draftquote.action;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PriorSSPriceContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * 
 * @author jason
 * 
 */
public class PriorSSPriceAction extends BaseContractActionHandler {

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		return null;
	}

	protected boolean validateInput(ProcessContract contract,
			ResultHandler handler) throws QuoteException, ResultBeanException {
		PriorSSPriceContract ct = (PriorSSPriceContract) contract;
		if (ct.getQuoteNumber() == null || "".equals(ct.getQuoteNumber())) {
			return false;
		}
		if (ct.getLineSeqNum() == null || "".equals(ct.getLineSeqNum())) {
			return false;
		}
		// validate the input value from pop-up window
		if (!checkManualLPP(handler, ct)) {
			return false;
		}
		if (!checkJustTxt(handler, ct)) {
			return false;
		}
		if (!checkOrderNumber(handler, ct)) {
			return false;
		}
		if (!checkPartQty(handler, ct)) {
			return false;
		}
		if (!checkPartNumbers(handler, ct)) {
			return false;
		}
		if (!checkSiteNumbers(handler, ct)) {
			return false;
		}
		return true;
	}

	private boolean checkSiteNumbers(ResultHandler handler,
			PriorSSPriceContract ct) {
		String pattern = "^[A-Za-z0-9]{7,10}$";
		String siteNum = ct.getSoldToCustNum();
		StringBuffer sbSiteNum=new StringBuffer();
		Locale locale = ct.getLocale();
		if (StringUtils.isNotBlank(siteNum)) {
			siteNum = siteNum.trim();
			if (siteNum.indexOf(",") == -1) {
				if (!matcher(pattern, siteNum) ) {
					handler.addObject(
							DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
							this.getI18NString(DraftQuoteMessageKeys.PRIOR_SS_SITE_NUMBERS_BE_SEVEN_TO_TEN_CHARACTERS, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale));
					return false;
				}
				sbSiteNum.append(siteNum.trim());
			} else {
				String[] siteNums = siteNum.split(",");
				for (int i = 0; i < siteNums.length; i++) {
					String tempSiteNum=siteNums[i].trim();
					if (!matcher(pattern, tempSiteNum) ) {
						handler.addObject(
								DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
								this.getI18NString(DraftQuoteMessageKeys.PRIOR_SS_SITE_NUMBERS_BE_SEVEN_TO_TEN_CHARACTERS, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale));
						return false;
					}
					if ((i == siteNums.length - 1)) {
						sbSiteNum.append(tempSiteNum.trim());
						break;
					}
					sbSiteNum.append(tempSiteNum.trim());
					sbSiteNum.append(",");
				}
			}
		}
		ct.setSoldToCustNum(sbSiteNum.toString());
		return true;
	}

	private boolean checkPartNumbers(ResultHandler handler,
			PriorSSPriceContract ct) {
		String pattern = "^[A-Za-z0-9]{7}$";
		String partNum = ct.getPartNum();
		StringBuffer sbPartNum=new StringBuffer();
		Locale locale = ct.getLocale();
		if (StringUtils.isNotBlank(partNum)) {
			partNum = partNum.trim();
			if (partNum.indexOf(",") == -1) {
				if (!matcher(pattern, partNum)) {
					handler.addObject(
							DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
							this.getI18NString(DraftQuoteMessageKeys.PRIOR_SS_PART_NUMBERS_BE_SEVEN_CHARACTERS, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale));
					return false;
				}
				sbPartNum.append(partNum.trim());
			} else {
				String[] partNums = partNum.split(",");
				for (int i = 0; i < partNums.length; i++) {
					String tempPartNum=partNums[i].trim();
					if (!matcher(pattern, tempPartNum)) {
						handler.addObject(
								DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
								this.getI18NString(DraftQuoteMessageKeys.PRIOR_SS_PART_NUMBERS_BE_SEVEN_CHARACTERS, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale));
						return false;
					}
					if ((i == partNums.length - 1)) {
						sbPartNum.append(tempPartNum.trim());
						break;
					}
					sbPartNum.append(tempPartNum.trim());
					sbPartNum.append(",");
				}
			}
		}
		ct.setPartNum(sbPartNum.toString());
		return true;
	}

	private boolean checkPartQty(ResultHandler handler, PriorSSPriceContract ct) {
		String pattern = "^\\d+$";
		Locale locale = ct.getLocale();
		if (StringUtils.isNotBlank(ct.getPartQty())) {
			if (!matcher(pattern, ct.getPartQty().trim())) {
				handler.addObject(
						DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
						this.getI18NString(DraftQuoteMessageKeys.PRIOR_SS_PART_QUANTITY_POSITIVE, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale));
				return false;
			}
		}
		return true;
		
	}

	private boolean checkOrderNumber(ResultHandler handler,
			PriorSSPriceContract ct) {
		if(DraftQuoteConstants.LPP_POPUP_SOURCE_CURRENCY_CONVERSION.equals(ct.getYtySrcCode())){
			return true;
		}
		if(StringUtils.isBlank(ct.getGdPartFlag()) ){
			Locale locale = ct.getLocale();
			if (!StringUtils.isNotBlank(ct.getSapSalesOrdNum())) {
				handler.addObject(
						DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
						this.getI18NString(DraftQuoteMessageKeys.PRIOR_SS_ORDER_NUMBER_REQUIRED, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale));
				return false;
			}
		}
		return true;
	}

	private boolean checkJustTxt(ResultHandler handler, PriorSSPriceContract ct) {
		Locale locale = ct.getLocale();
		if (!StringUtils.isNotBlank(ct.getJustTxt())) {
			handler.addObject(
					DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
					this.getI18NString(DraftQuoteMessageKeys.PRIOR_SS_PRICE_JUST_REQUIRED, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale));
			return false;
		}
		return true;
	}

	private boolean checkManualLPP(ResultHandler handler,
			PriorSSPriceContract ct) {
		Locale locale = ct.getLocale();
		if (!StringUtils.isNotBlank(ct.getLocalUnitPriceLpp())) {
			handler.addObject(
					DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
					this.getI18NString(DraftQuoteMessageKeys.SALES_REP_COMPUTED_PRIOR_SS_PRICE_REQUIRED, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale));
			return false;
		}
		
		//String pattern = "^\\d+(\\.\\d{1,3})?$";
		String pattern = "^(\\d{1,19})?$|^(\\d{1,15})(\\.\\d{1,4})?$";
		if (!matcher(pattern, ct.getLocalUnitPriceLpp().trim())) {
			handler.addObject(
					DraftQuoteParamKeys.PARAM_WEB_QUOTE_YTY_ERROR_MSG_OR_LPP,
					this.getI18NString(DraftQuoteMessageKeys.SALES_REP_COMPUTED_PRIOR_SS_PRICE_BE_NUMBER, MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale));
			return false;
		}
		return true;
	}

	private boolean matcher(String pattern, String value) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(value);
		while (m.find()) {
			return true;
		}
		return false;
	}

}