package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;

public class DraftQuotePartPricingMonthlySwViewBean extends
		DraftQuotePartPricingViewBean {


	public Map getMonthlySwConfgrtns(){
		return quote.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap();
	}
	
	public List<MonthlySoftwareConfiguration> getMonthlySoftwareConfigurations(){
		return quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
	}
	
	public List getMonthlyLineItemList(){
		return quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
	}
	
	 public void collectResults(Parameters params) throws ViewBeanException {
		 super.collectResults(params);
	}
	 
		public boolean isDisplayMigFlagLink(){
			List monthlyLineItems = quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
			for (int i=0;i<monthlyLineItems.size();i++){
				QuoteLineItem lineItem = (QuoteLineItem)monthlyLineItems.get(i);
				if(isDisplayMigration(lineItem)){
					return true;
				}
			}
			return false;
		}
		
		public boolean isDisplayFlagLink(){
			List monthlyLineItems = quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
			for (int i=0;i<monthlyLineItems.size();i++){
				QuoteLineItem lineItem = (QuoteLineItem)monthlyLineItems.get(i);
				if(isDisplayMigration(lineItem)){
					return true;
				}
			}
			return false;
		}
		


	public String getEditTermFrequenciesURL(MonthlySoftwareConfiguration ppc){
		String chrgAgrmtNum = ppc.getChrgAgrmtNum() == null ? "" : ppc.getChrgAgrmtNum();
		String orgConfigId = ppc.getConfigrtnIdFromCa() == null ? "" : ppc.getConfigrtnIdFromCa();
		String editTermFrequenciesURL = HtmlUtil
				.getURLForAction(DraftQuoteActionKeys.BUILD_MONTHLY_SW_CONFIGUATOR)
				+ "&webQuoteNum="
				+ ppc.getWebQuoteNum()
				+ "&configrtnActionCode="
				+ ppc.getConfigrtnActionCode()
				+ "&configId="
				+ ppc.getConfigrtnId()
				+ "&chrgAgrmtNum="
 + chrgAgrmtNum + "&orgConfigId=" + orgConfigId;
		
		return editTermFrequenciesURL;
	}
	
	public String getMonthlyChargeDocumentStr() throws Exception {
		String refDocNum=getQuote().getQuoteHeader().getRefDocNum();
		return StringUtils.isBlank(refDocNum) ? getI18NString("appl.i18n.partprice",
				PartPriceViewKeys.NEW_CHARGE_DOCUMENT) : refDocNum;
	  }

	public boolean isShowHiddenInput4chargeDocument(MonthlySoftwareConfiguration ppc) {
		return StringUtils.isNotBlank(ppc.getChrgAgrmtNum());
	}

	@Override
	public Collection generateAutoRenewOptions(String displayLevel,
			List subLineItems, QuoteLineItem qli) {

		Collection renwalMdlOptions = null;

		if (isNeedUpdateLineItemRewalMdlOptons(qli, displayLevel)
				|| isNeedUpdateConfigRewalMdlOptons(subLineItems,
						displayLevel)) {
			ApplicationContext context = ApplicationContextFactory.singleton()
					.getApplicationContext();
			String label = context.getI18nValueAsString(
					I18NBundleNames.PART_PRICE_MESSAGES, locale,
					PartPriceViewKeys.TERMINATE_END_TERM);
			String value = PartPriceConstants.RenewalModelCode.T;
			renwalMdlOptions = new ArrayList();
			renwalMdlOptions.add(new SelectOptionImpl(label, value, true));

		} else {
			renwalMdlOptions = super.generateAutoRenewOptions(displayLevel,
					subLineItems, qli);
		}

		return renwalMdlOptions;
	}

	
	private boolean isNeedUpdateLineItemRewalMdlOptons(QuoteLineItem qli,String displayLevel){
		boolean isNeed = false;
		if (DraftQuoteParamKeys.DISPLAY_LEVEL_SUBSCRPTN
				.equals(displayLevel) && qli.getICvrageTerm()!= null && qli.getICvrageTerm().intValue() < 3
				&& qli.isMonthlySoftwarePart()){
			isNeed = true;
		}
		return isNeed;
	}
	
	private boolean isNeedUpdateConfigRewalMdlOptons(List subLineItems,String displayLevel){
		boolean isNeed = false;
		if(DraftQuoteParamKeys.DISPLAY_LEVEL_CONFIGURTN.equals(displayLevel)){
			 isNeed = true;
			for (Object obj :subLineItems ){
				QuoteLineItem lineItem = (QuoteLineItem) obj;
				if (!lineItem.isMonthlySoftwarePart() || (lineItem.getICvrageTerm() != null && lineItem.getICvrageTerm().intValue() >= 3
						&& !lineItem.isFixedRenwlMdl())){
					isNeed = false;
					break;
				}
			}
		}
		return isNeed ;
		
	}
	
}
