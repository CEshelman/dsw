package com.ibm.dsw.quote.draftquote.viewbean.helper;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SubmittedPartTable;

public class SubmitPartTableElementUtil extends PartTableElementUtil {
	private static final long serialVersionUID = -4852697208145288057L;

	private SubmittedPartTable submittedPartTable;

	public SubmitPartTableElementUtil(Quote quote, SubmittedPartTable submittedPartTable) {
		super(quote, (PartPriceCommon)submittedPartTable);
		this.submittedPartTable = submittedPartTable;
	}

	/**
	 * @param item
	 * @return BP discount String Value for submit quote
	 */
	public String getBPDiscStrVal(QuoteLineItem item, boolean isPgsFlag){
		String bpPct = "";
		//if application is PGS
		if(isPgsFlag){
			if(!item.isReplacedPart()){
				if((item.getChnlOvrrdDiscPct() != null) && (item.getChnlOvrrdDiscPct() > 0) ){
			           bpPct =  DecimalUtil.formatTo5Number(item.getChnlOvrrdDiscPct()) + "%";
				 }else {
				       bpPct = DecimalUtil.formatTo5Number(item.getChnlStdDiscPct()) + "%";
				 }
			}else{
				QuoteLineItem masterItem = quote.getSaaSLineItem(item.getIRelatedLineItmNum());
				if(masterItem == null
					|| masterItem.getChnlStdDiscPct() == null
					|| masterItem.getChnlStdDiscPct().doubleValue() == 0.0){
					bpPct = DraftQuoteConstants.BLANK;
				}else{
					if((item.getChnlOvrrdDiscPct() != null) && (item.getChnlOvrrdDiscPct() > 0) ){
				           bpPct =  DecimalUtil.formatTo5Number(masterItem.getChnlOvrrdDiscPct()) + "%";
					 }else {
					       bpPct = DecimalUtil.formatTo5Number(masterItem.getChnlStdDiscPct()) + "%";
					 }
				}
			}
		//if application is SQO
		}else{
			if(!item.isReplacedPart()){
				bpPct = DecimalUtil.formatTo5Number(item.getChnlStdDiscPct()) + "%";
			}else{
				QuoteLineItem masterItem = quote.getSaaSLineItem(item.getIRelatedLineItmNum());
				if(masterItem == null
					|| masterItem.getChnlStdDiscPct() == null
					|| masterItem.getChnlStdDiscPct().doubleValue() == 0.0){
					bpPct = DraftQuoteConstants.BLANK;
				}else{
					bpPct = DecimalUtil.formatTo5Number(masterItem.getChnlStdDiscPct()) + "%";
				}
			}
		}
		return bpPct;
    }

	/**
	 * @param qli
	 * @param isTier2Reseller
	 * @return the standard discount percent for submitted quote line items
	 */
	public String getStdDiscStrVal(QuoteLineItem qli, boolean isTier2Reseller){
		String stdDiscStrVal = "";
		if(isTier2Reseller
	       && !ApplicationProperties.getInstance().getT2PriceAvailable()
	       && qli.isReplacedPart()){
			stdDiscStrVal = DraftQuoteConstants.BLANK;
	    }else {
	    	stdDiscStrVal = DecimalUtil.formatTo5Number(qli.getLineDiscPct()) + "%";
	    }
		return stdDiscStrVal;
    }

	/**
	 * @param qli
	 * @param lineItemBpTcvStrVal
	 * @return the BP TCV string value, if is replaced part, bold it
	 */
	public String getBpTcvStrVal(QuoteLineItem qli, String lineItemBpTcvStrVal){
		StringBuffer resultStr = new StringBuffer("");
		if(qli.isReplacedPart()){
			resultStr.append("<strong>");
		}
		resultStr.append(lineItemBpTcvStrVal);
		if(qli.isReplacedPart()){
    		resultStr.append("</strong>");
    	}
		return resultStr.toString();
    }

	/**
	 * @param qli
	 * @param lineItemBpRateStrVal
	 * @param shouldBoldFont
	 * @return the BP Rate string value, if shouldBoldFont, bold it
	 */
	public String getBpRateStrVal(QuoteLineItem qli, String lineItemBpRateStrVal, boolean shouldBoldFont){
		StringBuffer resultStr = new StringBuffer("");
		if(shouldBoldFont){
			resultStr.append("<strong>");
		}
		resultStr.append(lineItemBpRateStrVal);
		if(shouldBoldFont){
    		resultStr.append("</strong>");
    	}
		if(submittedPartTable.showBillingFrequency(qli) && qli.isSaasSubscrptnPart()){
			resultStr.append("<br />");
			resultStr.append(submittedPartTable.getBillgFrqncyDscr(qli));
		}
		return resultStr.toString();
    }

	/**
	 * @param qli
	 * @param resourceName
	 * @param locale
	 * @return get billing frequency string value for submitted quote line item
	 */
	public String getBillingFrequencyStrVal(QuoteLineItem qli, String resourceName, Locale locale){
		StringBuffer resultStr = new StringBuffer("");
		if(submittedPartTable.showBillingFrequency(qli)){
			resultStr.append("<div style=\"white-space: nowrap;\">");
			resultStr.append("<br />");
			resultStr.append(QuoteCommonUtil.getI18NString(resourceName, locale, PartPriceViewKeys.BILLING_FREQUENCY));
			resultStr.append("&nbsp;");
			resultStr.append(submittedPartTable.getBillgFrqncyDscr(qli));
			resultStr.append("</div>");
		}
		return resultStr.toString();
	}

	/**
	 * @param qli
	 * @param resourceName
	 * @param locale
	 * @return get Term string value for submitted quote line item
	 */
	public String getTermStrVal(QuoteLineItem qli, String resourceName, Locale locale){
		StringBuffer resultStr = new StringBuffer("");
		if(submittedPartTable.showTermSelection(qli)){
			resultStr.append("<div style=\"white-space: nowrap;\">");
			resultStr.append("<br />");
			if (qli.isReplacedPart()) {
				resultStr.append("<strong>");
				resultStr.append(QuoteCommonUtil.getI18NString(resourceName, locale, "original_term"));
				resultStr.append("</strong>");
			}else{
				resultStr.append("<strong>");
				resultStr.append(QuoteCommonUtil.getI18NString(resourceName, locale, PartPriceViewKeys.TERM));
				resultStr.append("</strong>");
			}
			resultStr.append("&nbsp;");
			resultStr.append(qli.getICvrageTerm() == null ? "" :qli.getICvrageTerm().intValue());
			resultStr.append("&nbsp;");
			resultStr.append(submittedPartTable.getCvrageTermUnit(qli));
			resultStr.append("</div>");
		}
		return resultStr.toString();
	}

	/**
	 * @param qli
	 * @param resourceName
	 * @param locale
	 * @return get Ramp Up Period string value for submitted quote line item
	 */
	public String getRampUpPeriodStrVal(QuoteLineItem qli, String resourceName, Locale locale){
		StringBuffer resultStr = new StringBuffer("");
		if(submittedPartTable.showRampUpPeriodForSubmitted(qli)){
			resultStr.append("<div style=\"white-space: nowrap;\">");
			resultStr.append("<br />");
			resultStr.append("<strong>");
			resultStr.append(QuoteCommonUtil.getI18NString(resourceName, locale, PartPriceViewKeys.RAMP_UP_PERIOD));
			resultStr.append("&nbsp;");
			resultStr.append(qli.getRampUpPeriodNum());
			resultStr.append("</strong>");
			resultStr.append("</div>");
		}
		return resultStr.toString();
	}

	/**
	 * @param qli
	 * @param resourceName
	 * @param locale
	 * @return get quantity string value for submitted quote line item
	 */
	public String getQtyStrVal(QuoteLineItem qli, String resourceName, Locale locale){
		StringBuffer resultStr = new StringBuffer("");
		if(submittedPartTable.showNAQty(qli)){
			resultStr.append("N/A");
		}else{
			if(submittedPartTable.showUpToSelection(qli)){
				resultStr.append("<strong>");
				resultStr.append(QuoteCommonUtil.getI18NString(resourceName, locale, PartPriceViewKeys.UP_TO));
				resultStr.append("</strong>");
				resultStr.append("<br />");
			}
			resultStr.append(qli.getPartQty()==null?"":qli.getPartQty().toString());
			if(submittedPartTable.showSaaSMultipleText(qli)){
				resultStr.append("<br />");
				resultStr.append(QuoteCommonUtil.getI18NString(resourceName, locale, PartPriceViewKeys.MULTIPLE_OF));
				resultStr.append("&nbsp;");
				resultStr.append(submittedPartTable.getSaaSMultipleText(qli));
			}
		}
		return resultStr.toString();
	}

	/**
	 * @param qli
	 * @param resourceName
	 * @param locale
	 * @return get Total Points string value for submitted quote line item
	 */
	public String getTotalPointsStrVal(QuoteLineItem qli, String resourceName, Locale locale){
		StringBuffer resultStr = new StringBuffer("");
		if(submittedPartTable.showSubmittedTotalPoints(qli)){
			double extPts = qli.getPartQty()== null ? qli.getContributionUnitPts() : qli.getContributionExtPts();
			resultStr.append(submittedPartTable.getFormattedPoint(extPts));
		}
		return resultStr.toString();
	}

	public String getRewalModelStrVal(QuoteLineItem qli, String resourceName,
			Locale locale) {
		StringBuffer resultStr = new StringBuffer("");
		if (submittedPartTable.showRenwlModeForSubscrptn(qli)
				|| submittedPartTable.showFixedModelForSubscrptn(qli)) {
			String renwlModCodeDesc = submittedPartTable.getRewalModCodeDesc(qli);
			resultStr.append("<div style=\"white-space: nowrap;\">");
			resultStr.append("<br />");
			resultStr.append(QuoteCommonUtil.getI18NString(resourceName,
					locale, PartPriceViewKeys.RENEWAL_MODEL_SUBSCRITON));
			resultStr.append("&nbsp;");
			if (StringUtils.isEmpty(renwlModCodeDesc)) {
				resultStr.append("");
			} else {
				resultStr.append(QuoteCommonUtil.getI18NString(resourceName,
						locale, renwlModCodeDesc));
			}
			resultStr.append("</div>");
		}
		return resultStr.toString();
	}
			
}
