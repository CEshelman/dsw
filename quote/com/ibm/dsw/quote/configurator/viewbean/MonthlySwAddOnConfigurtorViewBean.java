package com.ibm.dsw.quote.configurator.viewbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.appcache.domain.BillingOptionFactory;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwAddonTradeupConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorProduct;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.ps.config.PartSearchActionKeys;
import com.ibm.dsw.quote.pvu.config.VUDBConstants;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.topaz.exception.TopazException;


/**
 * @ClassName: MonthlySwAddOnConfigurtorViewBean
 * @author Ma Chao
 * @Description: TODO
 * @date Jan 09, 2014 12:30:00 PM
 *
 */

public class MonthlySwAddOnConfigurtorViewBean extends MonthlySwConfigurtorViewBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1289402833339432681L;
	
	public void collectResults(Parameters params) throws ViewBeanException {
		super.collectResults(params);
	}

    public String getBrowsePartsURL() {
        String browsePartsURL = null;
        String targetAction = DraftQuoteActionKeys.SUBMIT_MONTHLY_SW_CONFIGUATOR;
        String redirectAction = PartSearchActionKeys.DISPLAY_PARTSEARCH_BROWSE_RESULT;
        String partSearchParams = getPartSearchParams();
        browsePartsURL = genLinkURLsAndParams(targetAction + ",redirectAction=" + redirectAction + partSearchParams, null, null);
        return browsePartsURL;
    }
		 
    public String getFindPartsURL() {
        String findPartsURL = null;
        String targetAction = DraftQuoteActionKeys.SUBMIT_MONTHLY_SW_CONFIGUATOR;
        String redirectAction = PartSearchActionKeys.DISPLAY_PARTSEARCH_FIND;
        String partSearchParams = getPartSearchParams();
        findPartsURL = genLinkURLsAndParams(targetAction + ",redirectAction=" + redirectAction + partSearchParams, null, null);
        return findPartsURL;
    }
    
    private String getPartSearchParams() {
        String partSearchParamsURL;

        partSearchParamsURL = "&country=" + getCntryCode() + 
        "&currency=" + getCurrencyCode() + 
        "&lob=" + getLob() + 
        "&audience=" + getAudience().trim() + 
        "&acqrtnCode=" + (getAcqrtnCode() == null ? "" : getAcqrtnCode()) + 
        "&quoteNum=" + getWebQuoteNum() +
        "&progMigrationCode=" + getProgMigrationCode() + 
        "&quoteFlag=" + DraftQuoteParamKeys.PARAM_QUOTE_FLAG_SOFTWARE + 
        "&renewal=" + VUDBConstants.RENEWAL_QUOTE.equals(getQuoteTypeCode().trim()) +
        "&chrgAgrmtNum=" + getChrgAgrmtNum() +
        "&configrtnActionCode=" + PartPriceConstants.ConfigrtnActionCode.ADD_TRD +
        "&orgConfigId=" + getOrgConfigId();

        if (null != getCustomerNumber() && null != getSapContractNum()) {
            partSearchParamsURL += "&customerNumber=" + getCustomerNumber();
            partSearchParamsURL += "&sapContractNum=" + getSapContractNum();
        }

        return partSearchParamsURL;
    }

    private String genLinkURLsAndParams(String targetAction, String secondAction, String targetParams) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        StringBuffer sb = new StringBuffer();

        sb.append(actionKey).append("=").append(targetAction);
        if (StringUtils.isNotBlank(secondAction))
            sb.append("," + appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY) + "=").append(
                    secondAction);
        if (StringUtils.isNotBlank(targetParams))
            sb.append(",").append(targetParams);
        return sb.toString();
    }
	
    public String getBillingFrequency(MonthlySwConfiguratorPart part) throws TopazException{
    	Map<String, BillingOption> billingOptionMap = BillingOptionFactory.singleton().getBillingOptionMap();
    	MonthlySwActionConfiguratorPart monthlySwActionConfiguratorPart = part.getConfiguratorActionPart();
    	if (StringUtils.isBlank(monthlySwActionConfiguratorPart.getSapBillgFrqncyOptCode()))
			return null;
		if (billingOptionMap == null || billingOptionMap.values().size() == 0)
			return null;
		BillingOption billingOption = billingOptionMap.get(monthlySwActionConfiguratorPart.getSapBillgFrqncyOptCode());
		if (billingOption == null)
			return null;
		
		return billingOption.getCodeDesc();
    }
    
    public String getApplyChangesURL(){
    	String targetAction = DraftQuoteActionKeys.SUBMIT_MONTHLY_SW_CONFIGUATOR;
		String secondAction = DraftQuoteActionKeys.BUILD_MONTHLY_SW_CONFIGUATOR + ",webQuoteNum=" + getWebQuoteNum()
				+ ",configrtnActionCode=" + PartPriceConstants.ConfigrtnActionCode.ADD_TRD + ",configId=" + getConfigrationId()
				+ ",chrgAgrmtNum=" + getChrgAgrmtNum();
    	
    	String applyChangesURL = genLinkURLsAndParams(targetAction, secondAction, null);
    	
    	return applyChangesURL;
    }
    
	public String getOrigQty(MonthlySwConfiguratorPart part){
		MonthlySwActionConfiguratorPart monthlySwActionConfiguratorPart = part.getConfiguratorActionPart();
		
		if(monthlySwActionConfiguratorPart.getPartQty() != null){
			return monthlySwActionConfiguratorPart.getPartQty().toString();
		}
		
		return "";
	}
	
	public List getBillingFrequencyOptions(MonthlySwConfiguratorPart configuratorPart){
		MonthlySwAddonTradeupConfiguratorPart addonTradeupConfiguratorPart = getMonthlySwAddonTradeupConfiguratorPart(configuratorPart);
		
		String defaultBillingCode = configuratorPart.getBillingFrequencyCode();
		
		if (StringUtils.isBlank(defaultBillingCode)){
			defaultBillingCode =ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY;
		}
		
		List<BillingOption> billingOptions = addonTradeupConfiguratorPart.getBillingOptions();
		
		List collection = new ArrayList();
        Collections.sort(billingOptions, new Comparator() {
            public int compare(Object o1, Object o2) {
            	BillingOption op1 = (BillingOption) o1;
            	BillingOption op2 = (BillingOption) o2;
            	return op1.getMonths() - op2.getMonths();
            }
        });
        
        for(BillingOption bo : billingOptions){
    		if(StringUtils.equals(defaultBillingCode, bo.getCode())){
    			collection.add(new SelectOptionImpl(bo.getCodeDesc(), bo.getCode(), true));
    		} else {
    			collection.add(new SelectOptionImpl(bo.getCodeDesc(), bo.getCode(), false));
    		}
        }
        
        return collection;
	}
	
	public MonthlySwAddonTradeupConfiguratorPart getMonthlySwAddonTradeupConfiguratorPart(MonthlySwConfiguratorPart configuratorPart){
		
		MonthlySwAddonTradeupConfiguratorPart addonTradeupConfiguratorPart = null;
		
		if (configuratorPart != null ){
			
			MonthlySwActionConfiguratorPart actionConfiguratorPart = configuratorPart.getConfiguratorActionPart();
			
			if (actionConfiguratorPart instanceof MonthlySwAddonTradeupConfiguratorPart){
				addonTradeupConfiguratorPart = (MonthlySwAddonTradeupConfiguratorPart) actionConfiguratorPart;
			}
		}
		
		// avoid null point exception
		if (addonTradeupConfiguratorPart == null) {
			addonTradeupConfiguratorPart = new MonthlySwAddonTradeupConfiguratorPart(configuratorPart );
		}
		
		return addonTradeupConfiguratorPart;
	}
	
	public boolean hasAddonTradeupConfiguratorProduct(){
		return (getUpdatedMonthlySwProducts() != null && getUpdatedMonthlySwProducts().size() >0) 
				|| 
			   (getAddionMonthlySwProducts() != null && getAddionMonthlySwProducts().size() >0) 
			    ||
			   (getNoChangedMonthlySwProducts() != null && getNoChangedMonthlySwProducts().size() >0);
	}
	
    public List getRampUpOptions(MonthlySwConfiguratorPart configuratorPart) throws QuoteException {
        // DOTO by jack, should remove below content. It's just for debug and test.
        List options = new ArrayList();
		MonthlySwAddonTradeupConfiguratorPart addonTradeupConfiguratorPart= getMonthlySwAddonTradeupConfiguratorPart(configuratorPart);
		
		int defaultRampUp  = addonTradeupConfiguratorPart.getRampUpPeriod();

		int max = 4;
	    for (int i = 0; i <= max; i++) {
		   if(i == defaultRampUp){
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), true));
		   } else {
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), false));
		   }
        }

        return options;
    }
	
	public boolean isShowGlobalTerm() {
		for (MonthlySwConfiguratorProduct product : getAddionMonthlySwProducts()) {
			if (product.hasSubscrptnParts()) {
				return true;
			}
		}
		return false;
	}
	
	public String getChrgAgrmtNum(){
		return header.getChrgAgrmtNum();
	}
	
}
