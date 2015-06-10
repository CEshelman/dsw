/**
 * 
 */
package com.ibm.dsw.quote.configurator.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.common.util.UIFormatter;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.config.ConfiguratorViewKeys;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorForm;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorProduct;
import com.ibm.dsw.quote.configurator.domain.MonthlySwOnDemandConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwSubscrptnConfiguratorPart;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @ClassName: MonthlySwConfigurtorViewBean
 * @author Frank
 * @Description: TODO
 * @date Dec 19, 2013 2:49:51 PM
 *
 */
public class MonthlySwConfigurtorViewBean extends ConfiguratorViewBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MonthlySwConfiguratorForm configuratorForm = null;
	
	private String notFoundPartListStr="";
	private String alreadyExistedPartListStr="";
	private String processedPartListStr="";
	
	public void collectResults(Parameters params) throws ViewBeanException {
		super.collectResults(params);
		configuratorForm = (MonthlySwConfiguratorForm)params.getParameter(ParamKeys.PARAM_CONFIGURATOR_FORM);
		if (configuratorForm == null){
			return;
		}
		super.header = configuratorForm.getConfigHeader();
		header.setCntryCodeDscr(getCountryDesc(header.getCntryCode()));
        header.setCurrencyCodeDscr(getCurrencyDesc(header.getCntryCode(), header.getCurrencyCode()));
        notFoundPartListStr = params.getParameterAsString(ConfiguratorParamKeys.notFoundRestrictedPartList);
        alreadyExistedPartListStr = params.getParameterAsString(ConfiguratorParamKeys.existedRestriectedPartList);
        processedPartListStr = params.getParameterAsString(ConfiguratorParamKeys.neededProcessRestrictedPartList);
        
	}
	
	public List<MonthlySwConfiguratorProduct> getMonthlySwProducts(){
		return configuratorForm.getMonthlySwProducts();
	}
	
	public List<MonthlySwConfiguratorProduct> getUpdatedMonthlySwProducts(){
		return configuratorForm.getUpdatedMonthlySwProducts();
	}
	
	public List<MonthlySwConfiguratorProduct> getAddionMonthlySwProducts(){
		return configuratorForm.getAddionMonthlySwProducts();
	}
	
	public List<MonthlySwConfiguratorProduct> getNoChangedMonthlySwProducts(){
		return configuratorForm.getNoChangedMonthlySwProducts();
	}
	
	public boolean hasConfiguratorProduct (){
		return getMonthlySwProducts() != null && getMonthlySwProducts().size() >0;
	}
	
	 public String getI18NString(String key){
	    	return getI18NString(I18NBundleNames.CONFIGURATOR_MESSAGES, key);
	 }
	
	public String getPartPrice(MonthlySwConfiguratorPart configuratorPart){
		if(configuratorPart.getPrice() == null){
			return "N/A";
		} else {
			return UIFormatter.formatPrice(configuratorPart.getPrice(), getCntryCode());
		}
		
	}
	
	public String getQtyCheckBoxName (MonthlySwConfiguratorPart configuratorPart){
		return configuratorPart.getPartKey()+ ConfiguratorParamKeys.qtyCheckBoxSuffix;
	}
	
	public String getQtyInputBoxName (MonthlySwConfiguratorPart configuratorPart){
		return configuratorPart.getPartKey() + ConfiguratorParamKeys.qtySuffix;
	}
	
	public String getTermName (MonthlySwConfiguratorPart configuratorPart){
		return  configuratorPart.getPartKey() + ConfiguratorParamKeys.termSuffix;
	}
	
	public String getBillingFrequencyName (MonthlySwConfiguratorPart configuratorPart){
		return  configuratorPart.getPartKey()+ ConfiguratorParamKeys.billingFrequencySuffix;
	}
	
	public String getTierQtyMeasreName (MonthlySwConfiguratorPart configuratorPart){
		return  configuratorPart.getPartKey() + ConfiguratorParamKeys.tierQtyMeasreSuffix;
	}
	
	public String getPartNumberHelpTextImgId(String pid , String matlTypeCode){
		return pid + "_" + matlTypeCode + "_partQty_fieldHelp";
	}
	
	public String getPartNumberHelpText(String matlTypeCode){
		return getI18NString(matlTypeCode + ConfiguratorViewKeys.QTY_HDF_HELP_SUFFIX);
	}
	
	public String getAddConfigrtnURL(){
   	  ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
         String actionURL = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY) + "="
                              + DraftQuoteActionKeys.SUBMIT_MONTHLY_SW_CONFIGUATOR;
         return actionURL;
	}
	
    public String getSearchRestrictedPartsURL(){
  	  	ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionURL = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY) + "="
                             + DraftQuoteActionKeys.SEARCH_RESTRICTED_PARTS_FOR_CONFIGURATOR;
        return actionURL;
    }
   
   public String getCancelConfigrtnURL(){
   	ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
       StringBuffer actionURL = new StringBuffer();
       actionURL.append(appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY));
       actionURL.append("=");
       actionURL.append(DraftQuoteActionKeys.SUBMIT_MONTHLY_SW_CONFIGUATOR);
       actionURL.append(",").append("redirectAction");
       actionURL.append("=").append(PrepareConfiguratorRedirectDataBaseContract.REDIRECT_ACTION_CANCELL);
       
       return actionURL.toString();
 }
   
	public Collection getTermList(Integer defaltTerm) {
		Collection options = new ArrayList();
		int defaultTerm = defaltTerm == null ? 12 : defaltTerm.intValue();
		int totalMonths = 60;
		
	   for (int i = 1; i <= totalMonths; i++) {
		   if(i == defaultTerm){
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), true));
		   } else {
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), false));
		   }
        }
	   
	   return options;
	}
	
	public String getLineItemTerm(Integer term) {
		String result = term == null ? "" : term.intValue() + "";
		return result;
	}

	public boolean showMultipeOf(MonthlySwConfiguratorPart part){
		return (part.getTierQtyMeasre() != null);
	}
	
	public String getMultipleOfText(MonthlySwConfiguratorPart part){
		return getI18NString(ConfiguratorViewKeys.MULTIPLE_OF, new String[]{part.getTierQtyMeasre().toString()});
	}
	public String getAllWwideProdCodeDscr(MonthlySwConfiguratorProduct prod){
		StringBuilder allWwideProdCodeDscr = new StringBuilder(500);
		List<MonthlySwOnDemandConfiguratorPart> onDemandParts =  prod.getOnDemandParts();
		List<MonthlySwSubscrptnConfiguratorPart> subscrptnParts = prod.getSubscrptnParts();
		Set allWwideProdCodeDscrSet = new HashSet();
		if(onDemandParts != null && onDemandParts.size() > 0){
			for (Iterator iterator = onDemandParts.iterator(); iterator
					.hasNext();) {
				MonthlySwOnDemandConfiguratorPart onDemandPart = (MonthlySwOnDemandConfiguratorPart) iterator.next();
				allWwideProdCodeDscrSet.add(onDemandPart.getWwideProdCodeDscr());
			}
		}
		if(subscrptnParts != null && subscrptnParts.size() > 0){
			for (Iterator iterator = subscrptnParts.iterator(); iterator
					.hasNext();) {
				MonthlySwSubscrptnConfiguratorPart subscrptnPart = (MonthlySwSubscrptnConfiguratorPart) iterator.next();
				allWwideProdCodeDscrSet.add(subscrptnPart.getWwideProdCodeDscr());
				if(subscrptnPart.getDailyPart() != null){
					allWwideProdCodeDscrSet.add(subscrptnPart.getDailyPart().getWwideProdCodeDscr());
				}
				if(subscrptnPart.getOveargePart() != null){
					allWwideProdCodeDscrSet.add(subscrptnPart.getOveargePart().getWwideProdCodeDscr());
				}
			}
		}
		if(allWwideProdCodeDscrSet.isEmpty()){
			return "";
		}else{
			for (Iterator iterator = allWwideProdCodeDscrSet.iterator(); iterator
					.hasNext();) {
				String wwideProdCodeDscr = (String) iterator.next();
				allWwideProdCodeDscr.append(wwideProdCodeDscr);
				allWwideProdCodeDscr.append(", ");
			}
			String result = allWwideProdCodeDscr.toString();
			return new String(result.substring(0, result.lastIndexOf(", ")));
		}
	}

	public boolean isShowGlobalTerm(List<MonthlySwConfiguratorProduct> productsList) {
		for (MonthlySwConfiguratorProduct product : productsList) {
			if (product.hasSubscrptnParts()) {
				return true;
			}
		}
		return false;
	}

	public String getRampUpPeriodName(MonthlySwConfiguratorPart configuratorPart){
		
		return configuratorPart.getPartKey() + ConfiguratorParamKeys.rampUpPeriodSuffix;
	}
	
	public String getRampUpHelpTextImgId(MonthlySwConfiguratorPart configuratorPart){
		return configuratorPart.getPartNum() + "_rampUp_fieldHelp";
	}

    public String getRampUpLineItemPartNumber(int index) {
        return getI18NString(ConfiguratorViewKeys.RAMP_UP_LINE_ITEM_HEADER, new String[] { String.valueOf(index) });
    }

	public String getConfigrationId() {
		return header.getConfigId();
	}
	
	public String getOrgConfigId() {
		return header.getOrgConfigId();
	}

	public String getNotFoundPartListStr() {
		return notFoundPartListStr;
	}

	public void setNotFoundPartListStr(String notFoundPartListStr) {
		this.notFoundPartListStr = notFoundPartListStr;
	}

	public String getAlreadyExistedPartListStr() {
		return alreadyExistedPartListStr;
	}

	public void setAlreadyExistedPartListStr(String alreadyExistedPartListStr) {
		this.alreadyExistedPartListStr = alreadyExistedPartListStr;
	}

	public String getProcessedPartListStr() {
		return processedPartListStr;
	}

	public void setProcessedPartListStr(String processedPartListStr) {
		this.processedPartListStr = processedPartListStr;
	}
	public String getPartNumStr(List<MonthlySwConfiguratorProduct> productsList) {
		String seperator_comma = ",";
		StringBuilder sb = new StringBuilder();
		if (productsList != null && productsList.size() != 0) {
			for (MonthlySwConfiguratorProduct monthlySwConfigPrduct : productsList) {
				List<MonthlySwSubscrptnConfiguratorPart> monthlySubscrpnList = monthlySwConfigPrduct
						.getSubscrptnParts();
				List<MonthlySwOnDemandConfiguratorPart> monthlyOndemandList = monthlySwConfigPrduct
						.getOnDemandParts();
				if(monthlySubscrpnList!=null&&monthlySubscrpnList.size()!=0){
					for (MonthlySwSubscrptnConfiguratorPart mthlySwSubscrpnConfigPart : monthlySubscrpnList) {
					sb.append(mthlySwSubscrpnConfigPart.getPartNum()
							+ seperator_comma);
					}
				}
				if (monthlyOndemandList != null
						&& monthlyOndemandList.size() != 0) {
					for (MonthlySwOnDemandConfiguratorPart mthlySwOndemandConfigPart : monthlyOndemandList) {
					sb.append(mthlySwOndemandConfigPart.getPartNum()
							+ seperator_comma);
					}
				}
			}
		}
		return sb.toString();
	}
	
	public String getPartListStr4SkipTermValidation(){
		List<MonthlySwConfiguratorProduct> updateList = configuratorForm.getUpdatedMonthlySwProducts();
		List<MonthlySwConfiguratorProduct> noChangeList = configuratorForm.getNoChangedMonthlySwProducts();

		return getPartNumStr(updateList)+getPartNumStr(noChangeList);
	}

}
