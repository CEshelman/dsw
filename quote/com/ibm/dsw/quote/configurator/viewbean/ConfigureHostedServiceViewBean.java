package com.ibm.dsw.quote.configurator.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory.BillingFrequencyConfig;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.UIFormatter;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.config.ConfiguratorViewKeys;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPartGroup;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorProduct;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;


public class ConfigureHostedServiceViewBean extends ConfiguratorViewBean{

	
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        header = (ConfiguratorHeader)params.getParameter(ConfiguratorParamKeys.CONFIGURATOR_HEADER);
        header.setCntryCodeDscr(getCountryDesc(header.getCntryCode()));
        header.setCurrencyCodeDscr(getCurrencyDesc(header.getCntryCode(), header.getCurrencyCode()));
    }
    


	public Date getServiceEndDate(){
		return header.getEndDate();
	}
	
	public boolean showQtyInputBox(ConfiguratorPart part){
		return PartPriceSaaSPartConfigFactory.singleton().showQtyInputBox(part);
	}
	
	public boolean showQtyDropDown(ConfiguratorPart part){
		return PartPriceSaaSPartConfigFactory.singleton().showDropDown(part);
	}
	
	public boolean showCheckBox(ConfiguratorPart part){
	   return PartPriceSaaSPartConfigFactory.singleton().showCheckBox(part);
	}
	
	public boolean isCheckBoxChecked(ConfiguratorPart part){
		return part.isChecked();
	}
	
	public boolean disableCheckBox(ConfiguratorPart part){
		return PartPriceSaaSPartConfigFactory.singleton().disableCheckBox(header.isAddOnOrTradeUp(), part);
	}

	public boolean showOrigValue(ConfiguratorPart part){
		return PartPriceSaaSPartConfigFactory.singleton().showOriginalValue(header.isAddOnOrTradeUp(), part);
	}
	
	public boolean showMultipeOf(ConfiguratorPart part){
		return QuoteCommonUtil.needValidateSaaSMultiple(part);
	}
	
	public String getMultipleOfText(ConfiguratorPart part){
		return getI18NString(ConfiguratorViewKeys.MULTIPLE_OF, new String[]{part.getTierQtyMeasre().toString()});
	}
	
	public String getOrigQty(ConfiguratorPart part){
		if(part.getOrigQty() != null){
			return part.getOrigQty().toString();
		}
		
		return "";
	}
	

    
    public String getAddOnTradeUpFlag() {
        if(header.isAddOnOrTradeUp()){
        	return "1";
        } else {
        	return "0";
        }
    }
	
	public Collection getQtyDropDown(ConfiguratorPart part){
        Collection collection = new ArrayList();
        
        int qty = -1;
        if(part.getPartQty() != null){
        	qty = part.getPartQty();
        }
        
        List<Integer> qtyList = part.getTierdScalQtyList();
        String code = "" + qty;
        if(qtyList == null || qtyList.size() == 0){	//when tierd scale quantity list is null.
//        	collection.add(new SelectOptionImpl(code, code, true));
        	collection.add(new SelectOptionImpl("0", "0", true));
        	return collection;
        }
        
        Collections.sort(qtyList);
        if(code.equals("0"))	//when 0 is selected
        	collection.add(new SelectOptionImpl("0", "0", true));
        else
        	collection.add(new SelectOptionImpl("0", "0", false));
        for(Integer tierQty : qtyList){
        	String tierQtyStr = tierQty.toString();
        	
        	if(code.equals(tierQtyStr)){
        		collection.add(new SelectOptionImpl(tierQtyStr, tierQtyStr, true));
        	}else{
        		collection.add(new SelectOptionImpl(tierQtyStr, tierQtyStr, false));
        	}
        }
        return collection;
	}
	
	public boolean showServiceEndDate(){
		if(header.isAddOnOrTradeUp()){
			return true;
		}
		
		return false;
	}
	
	public boolean showRemoveCoTermAction(){
		if(!header.isAddOnOrTradeUp()
				&& header.isCoTermed()){
			return true;
		}
		
		return false;
	}
	
	public boolean showBillingFrequency(ConfiguratorPart part) throws QuoteException{
		return PartPriceSaaSPartConfigFactory.
		            singleton().showBillingFrequency(header.isAddOnOrTradeUp(), part);
	}
	
	public boolean showRampUp(ConfiguratorPart part){
		return PartPriceSaaSPartConfigFactory.
		            singleton().showRampUp(part);
	}
	
	public boolean showRampUpDropDown(ConfiguratorPart part){
		return PartPriceSaaSPartConfigFactory.
		            singleton().showRampUpDropDown(header.isAddOnOrTradeUp(), header.isCoTermed(), part);
	}
	
	public boolean showRampUpLineItems(ConfiguratorPart part){
		return (part.getRampUpLineItems() != null
				  && part.getRampUpLineItems().size() > 0);
	}
	
	public String getRampUpLineItemPartNumber(int index){
		return getI18NString(ConfiguratorViewKeys.RAMP_UP_LINE_ITEM_HEADER, new String[]{String.valueOf(index)});
	}
	/**
	 * refer to rtc#212987
	 * @param part
	 * @return
	 */
	public boolean showRampUpDurationConfirmMsg(ConfiguratorPart part){
		return (part.getRampUpLineItems() != null
				  && part.getRampUpLineItems().size() > 0);		
	}
	
	public List getBillingFrequencyOptions(ConfiguratorPart part) throws QuoteException{
		BillingFrequencyConfig config = PartPriceSaaSPartConfigFactory
		                                   .singleton().getBillingFrequencyConfig(header.isAddOnOrTradeUp(), part);
		
		List<BillingOption> options = new ArrayList<BillingOption>();
		String monthly = ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY;
		List<BillingOption> ops = part.getAvailableBillingOptions();
		if(ops != null && ops.size() > 0){
			options.addAll(ops);	//The new billing option rule has not been implemented, so just add all available options.
		}		
		String defaultBo = part.getBillingFrequencyCode();
		if(StringUtils.isBlank(defaultBo)){
			if(config.getDefaultOption().equals(monthly)){
				defaultBo = monthly;
			}
		}
		
		List collection = new ArrayList();
        Collections.sort(options, new Comparator() {
            public int compare(Object o1, Object o2) {
            	BillingOption op1 = (BillingOption) o1;
            	BillingOption op2 = (BillingOption) o2;
            	return op1.getMonths() - op2.getMonths();
            }
        });
        
        for(BillingOption bo : options){
    		if(StringUtils.equals(defaultBo, bo.getCode())){
    			collection.add(new SelectOptionImpl(bo.getCodeDesc(), bo.getCode(), true));
    		} else {
    			collection.add(new SelectOptionImpl(bo.getCodeDesc(), bo.getCode(), false));
    		}
        }
        
		return collection;
	}
	
	public List getRampUpOptions(ConfiguratorPart part) throws QuoteException{
		List options = new ArrayList();
		int defaultRampUp = part.getRampUpPeriod();

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
	
	public List getRampUpDurationOptions(ConfiguratorPart part) throws QuoteException{
		List options = new ArrayList();
		logger.debug(this, "part null:"+part);
		int defaultRampUpDuration = 0;
		if(part.getRampUpDuration() == null)
			defaultRampUpDuration = 1;
		else defaultRampUpDuration = part.getRampUpDuration();

		int max = 12;
	    for (int i = 1; i <= max; i++) {
		   if(i == defaultRampUpDuration){
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), true));
		   } else {
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), false));
		   }
        }
	   
	   return options;
	}
	
	public String getPlainTextBillingFrequency(List options){
		SelectOptionImpl so = null;
		String text = null;
		if(options != null && options.size()>0){
			so = (SelectOptionImpl)options.get(0);
			text = so.getLabel();
		}else{
			logger.info(this, "Data is missing for billing frequency text!!!");
			text = "N/A";
		}
		
		return text;
	}
	
	public String getBillingFrequencyValue(List options){
		SelectOptionImpl so = null;
		String value = null;
		if(options != null && options.size()>0){
			so = (SelectOptionImpl)options.get(0);
			value = so.getValue();
		}else{
			logger.info(this, "Data is missing for billing frequency value!!!");
			value = "N/A";
		}
		
		return value;
	}
	
	public String getPartPrice(ConfiguratorPart part){
		if(part.getPrice() == null){
			return "N/A";
		} else {
			return UIFormatter.formatPrice(part.getPrice(), getCntryCode());
		}
	}
	
    
    public List<ConfiguratorProduct> getProductList(){
    	return header.getProducts();
    }
    
    private int compareStringInLowerCase(String str1, String str2){
    	String lowStr1 = StringUtils.lowerCase(StringUtils.trimToEmpty(str1));
    	String lowStr2 = StringUtils.lowerCase(StringUtils.trimToEmpty(str2));
    	
    	return lowStr1.compareTo(lowStr2);
    }
    
    public List<ConfiguratorPart> sortForDisplay(List<ConfiguratorPart> list){
    	
    	//Each part in the parameter list has the same saas part type group(set up, subscription, on demand or human services)
    	if(list != null && list.size() > 0){
    		ConfiguratorPart cp = list.get(0);
    		
    		if(cp.isOnDemand() || cp.isHumanSrvs()){
    			Collections.sort(list, new Comparator<ConfiguratorPart>() {
    	        	public int compare(ConfiguratorPart cp1, ConfiguratorPart cp2){
    	        		return cp1.getPartDscr().compareTo(cp2.getPartDscr());
    	        	}
    			});
    			
    			int rowIndex = 0;
    			for(ConfiguratorPart part : list){
					part.setRowIndex(rowIndex);
					rowIndex++;
				}
    			
    			return list;
    		}
    		
    		List<ConfiguratorPart> result = new ArrayList<ConfiguratorPart>();
    		Map<String, SubIdPartGroup> map = groupBySubId(list);
    		
    		List<SubIdPartGroup> grpList = new ArrayList<SubIdPartGroup>();
    		grpList.addAll(map.values());
    		
    		//Firstly, sort parts by sub id description
    		Collections.sort(grpList, new SubIdPartGroupComparator());
    		
    		if(cp.isSetUp() || cp.isAddiSetUp()){
        		int rowIndex = 0;
        		SetupPartComparator comparator = new SetupPartComparator();

    			for(SubIdPartGroup grp : grpList){
    				List<ConfiguratorPart> sameSubIdList = grp.partList;
    				Collections.sort(sameSubIdList, comparator);
    				
    				for(ConfiguratorPart part :sameSubIdList){
    					part.setRowIndex(rowIndex);
    				}
    				
    				result.addAll(sameSubIdList);
    				rowIndex++;
    			}
    			
    			return result;
    		}
    		
    		if(cp.isSubscrptn() || cp.isOvrage() || cp.isDaily()){
        		int rowIndex = 0;
        		SubscrptnPartComparator comparator = new SubscrptnPartComparator();

    			for(SubIdPartGroup grp : grpList){
    				List<ConfiguratorPart> sameSubIdList = grp.partList;
    				Collections.sort(sameSubIdList, comparator);
    				
    				for(ConfiguratorPart part :sameSubIdList){
    					part.setRowIndex(rowIndex);
    				}
    				
    				result.addAll(sameSubIdList);
    				rowIndex++;
    			}
    			
    			return result;
    		}
    	}
    	
    	return list;
    }
    
    class SetupPartComparator implements Comparator{
    	public int compare(Object o1, Object o2){
    		ConfiguratorPart cp1 = (ConfiguratorPart)o1;
    		ConfiguratorPart cp2 = (ConfiguratorPart)o2;
    		
    		int index1 = getOrder(cp1);
    		int index2 = getOrder(cp2);
    		
    		int result = index1 - index2;
    		if(result != 0){
    			return result;
    		}
    		
    		return cp1.getPartDscr().compareTo(cp2.getPartDscr());
    	}
    	
    	private int getOrder(ConfiguratorPart part){
    		if(part.isSetUp()){
    			return 1;
    		}
            if (part.isSubsumedSubscrptn()) {
                return 2;
            }
    		if(part.isAddiSetUp()){
                return 3;
    		}
    		
    		//should never not happen
    		return 0;
    	}
    }
    
    class SubIdPartGroupComparator implements Comparator{
    	public int compare(Object o1, Object o2){
    		SubIdPartGroup grp1 = (SubIdPartGroup)o1;
    		SubIdPartGroup grp2 = (SubIdPartGroup)o2;
    		
    		return compareStringInLowerCase(grp1.subIdDscr, grp2.subIdDscr);
    	}
    }

    
    class SubscrptnPartComparator implements Comparator{
    	public int compare(Object o1, Object o2){
    		ConfiguratorPart cp1 = (ConfiguratorPart)o1;
    		ConfiguratorPart cp2 = (ConfiguratorPart)o2;
    		
    		
    		int index1 = getOrder(cp1);
    		int index2 = getOrder(cp2);
    		
      		int result = index1 - index2;
    		if(result != 0){
    			return result;
    		}
    		
    		return cp1.getPartDscr().compareTo(cp2.getPartDscr());
    	}
    	
    	private int getOrder(ConfiguratorPart part){
    		if(part.isSubscrptn()){
    			return 1;
    		}
    		
    		if(part.isOvrage()){
    			return 2;
    		}
    		
    		if(part.isDaily()){
    			return 3;
    		}
    		
    		//should never not happen
    		return 0;
    	}
    }
    
    private Map<String, SubIdPartGroup> groupBySubId(List<ConfiguratorPart> list){
    	
    	Map<String, SubIdPartGroup> map = new HashMap<String, SubIdPartGroup>();
    	for(ConfiguratorPart part : list){
    		SubIdPartGroup grp = map.get(part.getSubId());
    		
    		if(grp == null){
    			grp = new SubIdPartGroup(part.getSubId(), part.getSubIdDscr());
    			map.put(part.getSubId(), grp);
    		}
    		
    		grp.add(part);
    	}
    	
    	return map;
    }
    
    class SubIdPartGroup{
    	SubIdPartGroup(String subId, String subIdDscr){
    		this.subId = subId;
    		this.subIdDscr = subIdDscr;
    	}
    	
    	String subId;
    	String subIdDscr;
    	transient List<ConfiguratorPart> partList = new ArrayList<ConfiguratorPart>();
    	
    	void add(ConfiguratorPart part){
    		partList.add(part);
    	}
    }
    
    public String getConfigId(){
    	return StringEncoder.textToHTML(header.getConfigId());
    }
    
    public String getOrgConfigId(){
    	return StringEncoder.textToHTML(header.getOrgConfigId());
    }
    
    public String getChrgAgrmtNum(){
    	return StringEncoder.textToHTML(header.getChrgAgrmtNum());
    }
    
    public String getI18NString(String key){
    	return getI18NString(I18NBundleNames.CONFIGURATOR_MESSAGES, key);
    }
    
    public String getI18NString(String key, String[] args){
    	return getI18NString(I18NBundleNames.CONFIGURATOR_MESSAGES, key, args);
    }
    
    public String getAddConfigrtnURL(){
    	  ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
          String actionURL = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY) + "="
                               + DraftQuoteActionKeys.ADD_OR_UPDATE_CONFIGRTN;
          return actionURL;
    }
    
    public String getCancelConfigrtnURL(){
    	ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        StringBuffer actionURL = new StringBuffer();
        actionURL.append(appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY));
        actionURL.append("=");
        actionURL.append(DraftQuoteActionKeys.ADD_OR_UPDATE_CONFIGRTN);
        actionURL.append(",").append(CustomerParamKeys.PARAM_CONFIGURATOR_RedirectAction);
        actionURL.append("=").append(PrepareConfiguratorRedirectDataBaseContract.REDIRECT_ACTION_CANCELL);
        
        return actionURL.toString();
  }
    
    public String getOriginalValueLabel(ConfiguratorPart part){
    	String bundleKey = PartPriceSaaSPartConfigFactory.singleton()
    	                           .getOriginalValueLabel(header.isAddOnOrTradeUp(), part);
//    	logger.debug(this, "bundleKey:"+bundleKey);
    	return getI18NString(bundleKey);
    }
    
    public String getPartNumberHelpTextImgId(ConfiguratorProduct product, ConfiguratorPartGroup group){
    	return product.getWwideProdCode() + "_" + group.getSapMatlTypeCodeGroupCode() + "_partQty_fieldHelp";
    }
    
    public String getRampUpHelpTextImgId(ConfiguratorPart part){
    	return part.getPartNum() + "_fieldHelp";
    }
    
    public String getPartNumberHelpText(ConfiguratorPartGroup partGroup){
    	return getI18NString(partGroup.getSapMatlTypeCodeGroupCode() + ConfiguratorViewKeys.QTY_HDF_HELP_SUFFIX);
    }

	public String getCTFlag() {
		return StringEncoder.textToHTML(header.getCTFlag());
	}
	public String getOperationType() {
		return ConfiguratorConstants.OPERATION_TYPE_DISPLAY;
	}
	public String getConfigrtnActionCode(){
		return header.getConfigrtnActionCode();
	}
    public String getConfigHostedServiceURL(){
  	  ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionURL = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY) + "="
                             + DraftQuoteActionKeys.CONFIG_HOSTED_SERVICE;
        return actionURL;
    }
	public String[] getAvaliableBillingFrequencyOptions(){
		return header.getAvaliableBillingFrequencyOptions();
	}
	public String getPublshdPriceDurtnCodeDscr(ConfiguratorPart part){
		return part.getPublshdPriceDurtnCodeDscr();
	}
    public String getExistingPartNumbersInCA(){
    	return header.getExistingPartNumbersInCA();
    }
    public String getOverrideFlag() {
		return StringEncoder.textToHTML(header.getOverrideFlag());
	}
    public String getOverridePilotFlag() {
		return StringEncoder.textToHTML(header.getOverridePilotFlag());
	}
    public String getOverrideRstrctFlag() {
		return StringEncoder.textToHTML(header.getOverrideRstrctFlag());
	}    
    public String getCalcTerm() {
    	if(header.getCalcTerm() != null )
    		return header.getCalcTerm().toString();
        return "";
    }
    
    public String getTerm(){
    	if(header.getTerm() != null){
    		return header.getTerm().toString();
    	}
    	return "";
    }
	public boolean isForFCTToPAFinalization(){
		if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(getConfigrtnActionCode()))
			return true;
		return false;
	}
    public String getTermForFCTToPAFinalization() {
    	if(StringUtils.isNotBlank(header.getTermForFCTToPAFinalization()))
    		return header.getTermForFCTToPAFinalization();
        return "";
    }
    public boolean isHuamanServicePart(ConfiguratorPart part){
    	return part.isHumanSrvs();
    }
 

}
