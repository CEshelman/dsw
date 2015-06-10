package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * PartPriceSaaSPartConfigFactory.java
 *
 * <p>
 * Copyright 2011 by IBM Corporation All rights reserved.
 * </p>
 * 
 * @author <a href="wxiaoli@cn.ibm.com">Vivian</a> <br/>
 * Mar 31, 2011
 */
public class PartPriceSaaSPartConfigFactory {
	
	private static String ON_AGRMNT = "on-charge-agreement";
	private static String ACTIVE_ON_AGRMNT = "active-on-charge-agreement";
	
	private static PartPriceSaaSPartConfigFactory singleton = null;

	private LogContext logContext = LogContextFactory.singleton()
			.getLogContext();

	public PartPriceSaaSPartConfigFactory() {
		loadConfig();
	}
	
	private DisplayUIConfig displayUIConfig = new DisplayUIConfig();
	private SaaSPartConfig saasPartConfig = new SaaSPartConfig();
	private List<ConfiguratorPartType> configuratorPartTypeList = new ArrayList<ConfiguratorPartType>();
	private boolean useTokenCache = false;
	
	public boolean shouldUseTokenCache(){
		return useTokenCache;
	}

	public static PartPriceSaaSPartConfigFactory singleton() {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		if (PartPriceSaaSPartConfigFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = PartPriceSaaSPartConfigFactory.class.getName();
				Class factoryClass = Class.forName(factoryClassName);
				PartPriceSaaSPartConfigFactory.singleton = (PartPriceSaaSPartConfigFactory) factoryClass
						.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(PartPriceSaaSPartConfigFactory.class, iae, iae
						.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(PartPriceSaaSPartConfigFactory.class, cnfe, cnfe
						.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(PartPriceSaaSPartConfigFactory.class, ie, ie.getMessage());
			}
		}
		return singleton;
	}


	/**
	 * void
	 * lood the SaaS part config
	 */
	protected void loadConfig() {

		try {
			logContext.debug(this, "Loading part price SaaS part config ");
			Element rootElement = PartPriceConfigFactory.singleton().getAllRootElements();
			loadDisplayUiConfig(rootElement);
			loadSaaSPartConfig(rootElement);
			loadConfiguratorPartConfig(rootElement);
			loadUseTokenCacheConfig(rootElement);
		} catch (Exception e) {
			e.printStackTrace();
			logContext.error(this, e,
					"Exception loading part price SaaS part config ");
		}
		logContext.debug(this, "Finished loading part price SaaS part config ");
	}
	
	private void loadUseTokenCacheConfig(Element root){
		Element ele = root.getChild("use-token-cache");
		
		if(ele != null){
			useTokenCache = Boolean.valueOf(ele.getText());
		}
	}


	protected void reset() {
		singleton = null;
	}
	
	private static class DisplayUIConfig{
		public List showQtyInputBox_PartTypeList = new ArrayList();
		public List showQtyInputBox_ExceptTierModelList = new ArrayList();
		public List showUpToDropDown_PartTypeList = new ArrayList();
		public List showUpToDropDown_TierModelList = new ArrayList();
		public List validateMltpl_PartTypeList = new ArrayList();
		public List noQty_PartTypeList = new ArrayList();
		public List showEntldUnitPrice_PartTypeList = new ArrayList();
		public List showBidUnitPrice_PartTypeList = new ArrayList();
		public List showEntldExtndPrice_PartTypeList = new ArrayList();
		public List showBidExtndPrice_PartTypeList = new ArrayList();
		public List canInputDiscount_PartTypeList = new ArrayList();
		public List canInputUnitOvrrdPrice_PartTypeList = new ArrayList();
		public List canInputExtndOvrrdPrice_PartTypeList = new ArrayList();
		public List showBillingFrqncy_PartTypeList = new ArrayList();
		public List showCommittedTerm_PartTypeList = new ArrayList();
		public List shldClcltOvrrdUnitPricPerDisc_PartTypeList = new ArrayList();
		public List usagePartTypes_PartTypeList = new ArrayList();
		private Map maximumDecimalPlacesMap = new HashMap();
		
		public List getShowQtyInputBox_PartTypeList() {
			return showQtyInputBox_PartTypeList;
		}
		public void setShowQtyInputBox_PartTypeList(List showQtyInputBox_PartTypeList) {
			this.showQtyInputBox_PartTypeList = showQtyInputBox_PartTypeList;
		}
		public List getShowQtyInputBox_ExceptTierModelList() {
			return showQtyInputBox_ExceptTierModelList;
		}
		public void setShowQtyInputBox_ExceptTierModelList(
				List showQtyInputBox_ExceptTierModelList) {
			this.showQtyInputBox_ExceptTierModelList = showQtyInputBox_ExceptTierModelList;
		}
		public List getShowUpToDropDown_PartTypeList() {
			return showUpToDropDown_PartTypeList;
		}
		public void setShowUpToDropDown_PartTypeList(List showUpToDropDown_PartTypeList) {
			this.showUpToDropDown_PartTypeList = showUpToDropDown_PartTypeList;
		}
		public List getShowUpToDropDown_TierModelList() {
			return showUpToDropDown_TierModelList;
		}
		public void setShowUpToDropDown_TierModelList(
				List showUpToDropDown_TierModelList) {
			this.showUpToDropDown_TierModelList = showUpToDropDown_TierModelList;
		}
		public List getValidateMltpl_PartTypeList() {
			return validateMltpl_PartTypeList;
		}
		public void setValidateMltpl_PartTypeList(List validateMltpl_PartTypeList) {
			this.validateMltpl_PartTypeList = validateMltpl_PartTypeList;
		}
		public List getNoQty_PartTypeList() {
			return noQty_PartTypeList;
		}
		public void setNoQty_PartTypeList(List noQty_PartTypeList) {
			this.noQty_PartTypeList = noQty_PartTypeList;
		}
		public List getShowEntldUnitPrice_PartTypeList() {
			return showEntldUnitPrice_PartTypeList;
		}
		public void setShowEntldUnitPrice_PartTypeList(
				List showEntldUnitPrice_PartTypeList) {
			this.showEntldUnitPrice_PartTypeList = showEntldUnitPrice_PartTypeList;
		}
		public List getShowBidUnitPrice_PartTypeList() {
			return showBidUnitPrice_PartTypeList;
		}
		public void setShowBidUnitPrice_PartTypeList(List showBidUnitPrice_PartTypeList) {
			this.showBidUnitPrice_PartTypeList = showBidUnitPrice_PartTypeList;
		}
		public List getShowEntldExtndPrice_PartTypeList() {
			return showEntldExtndPrice_PartTypeList;
		}
		public void setShowEntldExtndPrice_PartTypeList(
				List showEntldExtndPrice_PartTypeList) {
			this.showEntldExtndPrice_PartTypeList = showEntldExtndPrice_PartTypeList;
		}
		public List getShowBidExtndPrice_PartTypeList() {
			return showBidExtndPrice_PartTypeList;
		}
		public void setShowBidExtndPrice_PartTypeList(
				List showBidExtndPrice_PartTypeList) {
			this.showBidExtndPrice_PartTypeList = showBidExtndPrice_PartTypeList;
		}
		public List getCanInputDiscount_PartTypeList() {
			return canInputDiscount_PartTypeList;
		}
		public void setCanInputDiscount_PartTypeList(List canInputDiscount_PartTypeList) {
			this.canInputDiscount_PartTypeList = canInputDiscount_PartTypeList;
		}
		public List getCanInputUnitOvrrdPrice_PartTypeList() {
			return canInputUnitOvrrdPrice_PartTypeList;
		}
		public void setCanInputUnitOvrrdPrice_PartTypeList(
				List canInputUnitOvrrdPrice_PartTypeList) {
			this.canInputUnitOvrrdPrice_PartTypeList = canInputUnitOvrrdPrice_PartTypeList;
		}
		public List getCanInputExtndOvrrdPrice_PartTypeList() {
			return canInputExtndOvrrdPrice_PartTypeList;
		}
		public void setCanInputExtndOvrrdPrice_PartTypeList(
				List canInputExtndOvrrdPrice_PartTypeList) {
			this.canInputExtndOvrrdPrice_PartTypeList = canInputExtndOvrrdPrice_PartTypeList;
		}
		public List getShowBillingFrqncy_PartTypeList() {
			return showBillingFrqncy_PartTypeList;
		}
		public void setShowBillingFrqncy_PartTypeList(
				List showBillingFrqncy_PartTypeList) {
			this.showBillingFrqncy_PartTypeList = showBillingFrqncy_PartTypeList;
		}
		public List getShowCommittedTerm_PartTypeList() {
			return showCommittedTerm_PartTypeList;
		}
		public void setShowCommittedTerm_PartTypeList(
				List showCommittedTerm_PartTypeList) {
			this.showCommittedTerm_PartTypeList = showCommittedTerm_PartTypeList;
		}
		public List getShldClcltOvrrdUnitPricPerDisc_PartTypeList() {
			return shldClcltOvrrdUnitPricPerDisc_PartTypeList;
		}
		public void setShldClcltOvrrdUnitPricPerDisc_PartTypeList(
				List shldClcltOvrrdUnitPricPerDisc_PartTypeList) {
			this.shldClcltOvrrdUnitPricPerDisc_PartTypeList = shldClcltOvrrdUnitPricPerDisc_PartTypeList;
		}
		public List getUsagePartTypes_PartTypeList() {
			return usagePartTypes_PartTypeList;
		}
		public void setUsagePartTypes_PartTypeList(
				List usagePartTypes_PartTypeList) {
			this.usagePartTypes_PartTypeList = usagePartTypes_PartTypeList;
		}		
		public Map getMaximumDecimalPlacesMap() {
			return maximumDecimalPlacesMap;
		}
		public void setMaximumDecimalPlacesMap(Map maximumDecimalPlacesMap) {
			this.maximumDecimalPlacesMap = maximumDecimalPlacesMap;
		}		
	} //~end of class DisplayUIConfig

	/**
	 * @param root
	 * void
	 * load the SaaS parts display UI configuration
	 */
	private void loadDisplayUiConfig(Element root) {
		Element uiElement = root.getChild("display-ui-config");
		List childrenElems = uiElement.getChildren();
		
		if(childrenElems != null && childrenElems.size() > 0){
			for (int ei = 0; ei < childrenElems.size(); ei++) {
				Element childElem = (Element) childrenElems.get(ei);
				if(childElem.getName().equals("qty-field-config")){
					List qtyFieldElems = childElem.getChildren();
					if (qtyFieldElems != null && qtyFieldElems.size() > 0) {
						for (int i = 0; i < qtyFieldElems.size(); i++) {
							Element qtyFieldChildElem = (Element) qtyFieldElems.get(i);
							if(qtyFieldChildElem.getName().equals("show-qty-input-box")){
								String[] partTypes = qtyFieldChildElem.getChild(
									"part-type").getAttributeValue("value").split(",");
								List listPartType = new ArrayList();
								for (int j = 0; j < partTypes.length; j++) {
									listPartType.add(partTypes[j]);
								}
								displayUIConfig.setShowQtyInputBox_PartTypeList(listPartType);
								String[] tierModels = qtyFieldChildElem.getChild(
									"except-pricing-tier-model").getAttributeValue("value").split(",");
								List listTierModel = new ArrayList();
								for (int k = 0; k < tierModels.length; k++) {
									listTierModel.add(tierModels[k]);
								}
								displayUIConfig.setShowQtyInputBox_ExceptTierModelList(listTierModel);
							}
							if(qtyFieldChildElem.getName().equals("show-up-to-drop-down")){
								String[] partTypes = qtyFieldChildElem.getChild(
									"part-type").getAttributeValue("value").split(",");
								List listPartType = new ArrayList();
								for (int j = 0; j < partTypes.length; j++) {
									listPartType.add(partTypes[j]);
								}
								displayUIConfig.setShowUpToDropDown_PartTypeList(listPartType);
								String[] tierModels = qtyFieldChildElem.getChild(
									"pricing-tier-model").getAttributeValue("value").split(",");
								List listTierModel = new ArrayList();
								for (int k = 0; k < tierModels.length; k++) {
									listTierModel.add(tierModels[k]);
								}
								displayUIConfig.setShowUpToDropDown_TierModelList(listTierModel);
							}
							if(qtyFieldChildElem.getName().equals("validate-multiple")){
								String[] partTypes = qtyFieldChildElem.getChild(
									"part-type").getAttributeValue("value").split(",");
								List listPartType = new ArrayList();
								for (int j = 0; j < partTypes.length; j++) {
									listPartType.add(partTypes[j]);
								}
								displayUIConfig.setValidateMltpl_PartTypeList(listPartType);
							}
							if(qtyFieldChildElem.getName().equals("no-qty")){
								String[] partTypes = qtyFieldChildElem.getChild(
									"part-type").getAttributeValue("value").split(",");
								List listPartType = new ArrayList();
								for (int j = 0; j < partTypes.length; j++) {
									listPartType.add(partTypes[j]);
								}
								displayUIConfig.setNoQty_PartTypeList(listPartType);
							}
						} //~end of qtyFieldElems for
					} //~end of qtyFieldElems if
				} //~end of qty-field-config if
				if(childElem.getName().equals("show-entitled-unit-price")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setShowEntldUnitPrice_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("show-bid-unit-price")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setShowBidUnitPrice_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("show-entitled-extended-price")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setShowEntldExtndPrice_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("show-bid-extended-price")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setShowBidExtndPrice_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("can-input-discount")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setCanInputDiscount_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("can-input-unit-ovrrd-price")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setCanInputUnitOvrrdPrice_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("can-input-extended-ovrrd-price")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setCanInputExtndOvrrdPrice_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("show-billing-frqncy")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setShowBillingFrqncy_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("show-committed-term")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setShowCommittedTerm_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("calculate-unit-ovrrd-price-per-disc")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setShldClcltOvrrdUnitPricPerDisc_PartTypeList(listPartType);
				}
				if(childElem.getName().equals("usage-part-types")){
					String[] partTypes = childElem.getChild(
						"part-type").getAttributeValue("value").split(",");
					List listPartType = new ArrayList();
					for (int j = 0; j < partTypes.length; j++) {
						listPartType.add(partTypes[j]);
					}
					displayUIConfig.setUsagePartTypes_PartTypeList(listPartType);
				}				
				if(childElem.getName().equals("maximum-decimal-config")){
					Map mdpm = new HashMap();
					List configList = childElem.getChildren();
					for (Iterator it = configList.iterator(); it.hasNext();) {
						Element config = (Element) it.next();
						String partType = config.getChildTextTrim("part-type");
						String maxDecimal = config.getChildTextTrim("max-decimal");
						mdpm.put(partType, maxDecimal);
					}
					displayUIConfig.setMaximumDecimalPlacesMap(mdpm);
				}				
			} //~end of childrenElems for
		} //~end of childrenElems if 
		
	} //~end of loadPreventDateEditConfig method
	
	public boolean showQtyInputBox(QuoteLineItem qli){
		if(displayUIConfig.getShowQtyInputBox_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))
			&& !displayUIConfig.getShowQtyInputBox_ExceptTierModelList().contains(qli.getPricingTierModel())){
			return true;
		}
		return false;
	}
	
	public boolean showUpToDropDown(QuoteLineItem qli){
		if(displayUIConfig.getShowUpToDropDown_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))
			&& displayUIConfig.getShowUpToDropDown_TierModelList().contains(qli.getPricingTierModel())){
			return true;
		}
		return false;
	}
	
	public boolean needValidateMltpl(QuoteLineItem qli){
		if(displayUIConfig.getValidateMltpl_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean needValidateMltpl(ConfiguratorPart part){
		if(displayUIConfig.getValidateMltpl_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(part))){
			return true;
		}
		return false;
	}
	
	public boolean isNoQty(QuoteLineItem qli){
		if(displayUIConfig.getNoQty_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean showEntldUnitPrice(QuoteLineItem qli){
		if(displayUIConfig.getShowEntldUnitPrice_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean showBidUnitPrice(QuoteLineItem qli){
		if(displayUIConfig.getShowBidUnitPrice_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean showEntldExtndPrice(QuoteLineItem qli){
		if(displayUIConfig.getShowEntldExtndPrice_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean showBidExtndPrice(QuoteLineItem qli){
		if(displayUIConfig.getShowBidExtndPrice_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean canInputDiscount(QuoteLineItem qli){
		if(displayUIConfig.getCanInputDiscount_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean canInputUnitOvrrdPrice(QuoteLineItem qli){
		if(displayUIConfig.getCanInputUnitOvrrdPrice_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean canInputExtndOvrrdPrice(QuoteLineItem qli){
		if(displayUIConfig.getCanInputExtndOvrrdPrice_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean showBillingFrqncy(QuoteLineItem qli){
		if(displayUIConfig.getShowBillingFrqncy_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean showCommittedTerm(QuoteLineItem qli){
		if(displayUIConfig.getShowCommittedTerm_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public boolean showCommittedTerm(ConfiguratorPart part){
		if(displayUIConfig.getShowCommittedTerm_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(part))){
			return true;
		}
		return false;
	}
	
	public boolean shldClcltOvrrdUnitPricPerDisc(QuoteLineItem qli){
		if(displayUIConfig.getShldClcltOvrrdUnitPricPerDisc_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	
	public static class BillingFrequencyConfig{
		String optionsFrom;
		String defaultOption;
		boolean plusUpFront;
		public boolean isPlusUpFront() {
			return plusUpFront;
		}
		public void setPlusUpFront(boolean plusUpFront) {
			this.plusUpFront = plusUpFront;
		}
		public String getOptionsFrom() {
			return optionsFrom;
		}
		public void setOptionsFrom(String optionsFrom) {
			this.optionsFrom = optionsFrom;
		}
		public String getDefaultOption() {
			return defaultOption;
		}
		public void setDefaultOption(String defaultOption) {
			this.defaultOption = defaultOption;
		}
	}
	
	private static class ScenarioConfig{
		boolean addOnTradeUpFlag;
		Boolean coTermeFlag;
		String activeStatus;
		String label;
		BillingFrequencyConfig billingFrequencyConfig;
		
		public Boolean getCoTermeFlag() {
			return coTermeFlag;
		}
		public void setCoTermeFlag(Boolean coTermeFlag) {
			this.coTermeFlag = coTermeFlag;
		}
		public boolean isAddOnTradeUpFlag() {
			return addOnTradeUpFlag;
		}
		public void setAddOnTradeUpFlag(boolean addOnTradeUpFlag) {
			this.addOnTradeUpFlag = addOnTradeUpFlag;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getActiveStatus() {
			return activeStatus;
		}
		public void setActiveStatus(String activeStatus) {
			this.activeStatus = activeStatus;
		}
		public BillingFrequencyConfig getBillingFrequencyConfig() {
			return billingFrequencyConfig;
		}
		public void setBillingFrequencyConfig(
				BillingFrequencyConfig billingFrequencyConfig) {
			this.billingFrequencyConfig = billingFrequencyConfig;
		}
	}
	
	private static class ConfiguratorPartType{
		String typeCode;
		String dropDownPricingTierModel;
		
		//For quantity input box 
		boolean showQtyInputBox;
		String qtyInputBoxExceptPricingTierModel;
		
		boolean showOriginal;
		
		//For check box
		boolean showCheckBox;
		List<ScenarioConfig> disableCheckBoxConfigList;
		List<ScenarioConfig> showOriginalValueConfigList;
		
		//For billing frequency
		List<ScenarioConfig> showBillingFrequencyConfigList;
		
		//For ramp up
		List<ScenarioConfig> showRampUpConfigList;
		
		boolean showRampUpFlag;
		
		public boolean getShowRampUpFlag() {
			return showRampUpFlag;
		}
		public void setShowRampUp(boolean showRampUpFlag) {
			this.showRampUpFlag = showRampUpFlag;
		}
		public boolean isShowCheckBox() {
			return showCheckBox;
		}
		public void setShowCheckBox(boolean showCheckBox) {
			this.showCheckBox = showCheckBox;
		}
		public String getQtyInputBoxExceptPricingTierModel() {
			return qtyInputBoxExceptPricingTierModel;
		}
		public void setQtyInputBoxExceptPricingTierModel(
				String qtyInputBoxExceptPricingTierModel) {
			this.qtyInputBoxExceptPricingTierModel = qtyInputBoxExceptPricingTierModel;
		}
		public String getTypeCode() {
			return typeCode;
		}
		public void setTypeCode(String typeCode) {
			this.typeCode = typeCode;
		}
		public String getDropDownPricingTierModel() {
			return dropDownPricingTierModel;
		}
		public void setDropDownPricingTierModel(String dropDownPricingTierModel) {
			this.dropDownPricingTierModel = dropDownPricingTierModel;
		}
		public boolean isShowQtyInputBox() {
			return showQtyInputBox;
		}
		public void setShowQtyInputBox(boolean showQtyInputBox) {
			this.showQtyInputBox = showQtyInputBox;
		}
		public boolean isShowOriginal() {
			return showOriginal;
		}
		public void setShowOriginal(boolean showOriginal) {
			this.showOriginal = showOriginal;
		}
		public List<ScenarioConfig> getDisableCheckBoxConfigList(){
			return disableCheckBoxConfigList;
		}
		public void addDisableCheckBoxConfig(ScenarioConfig config){
			if(disableCheckBoxConfigList == null){
				disableCheckBoxConfigList = new ArrayList<ScenarioConfig>();
			}
			
			disableCheckBoxConfigList.add(config);
		}
		
		public List<ScenarioConfig> getShowOriginalValueConfigList(){
			return showOriginalValueConfigList;
		}
		public void addShowOriginalValueConfig(ScenarioConfig config){
			if(showOriginalValueConfigList == null){
				showOriginalValueConfigList = new ArrayList<ScenarioConfig>();
			}
			
			showOriginalValueConfigList.add(config);
		}
		
		public List<ScenarioConfig> getShowBillingFrequencyConfigList(){
			return showBillingFrequencyConfigList;
		}
		public void addShowBillingFrequencyConfig(ScenarioConfig config){
			if(showBillingFrequencyConfigList == null){
				showBillingFrequencyConfigList = new ArrayList<ScenarioConfig>();
			}
			
			showBillingFrequencyConfigList.add(config);
		}
		
		public List<ScenarioConfig> getShowRampUpConfigList(){
			return showRampUpConfigList;
		}
		public void addShowRampUpConfig(ScenarioConfig config){
			if(showRampUpConfigList == null){
				showRampUpConfigList = new ArrayList<ScenarioConfig>();
			}
			
			showRampUpConfigList.add(config);
		}
	}
	
	public boolean showDropDown(ConfiguratorPart part){
		for(ConfiguratorPartType type : configuratorPartTypeList){
			if(type.getTypeCode().equals(part.getSapMatlTypeCode())){
				String dropDownTierModelConfig = type.getDropDownPricingTierModel();
				String partPricingTierModel = part.getPricingTierModel();
				
				if(StringUtils.isBlank(type.getDropDownPricingTierModel())
						|| StringUtils.isBlank(part.getPricingTierModel())){
					continue;
				}
				
				if(dropDownTierModelConfig.indexOf(partPricingTierModel) != -1){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean showQtyInputBox(ConfiguratorPart part){
		for(ConfiguratorPartType type : configuratorPartTypeList){
			if(type.getTypeCode().equals(part.getSapMatlTypeCode())){
				if(!type.isShowQtyInputBox()){
					continue;
				}
				
				String qtyInputBoxExceptPricingTierModelConfig = type.getDropDownPricingTierModel();
				String partPricingTierModel = part.getPricingTierModel();
				
				if(StringUtils.isBlank(qtyInputBoxExceptPricingTierModelConfig) 
						|| StringUtils.isBlank(partPricingTierModel)
						|| (qtyInputBoxExceptPricingTierModelConfig.indexOf(partPricingTierModel) == -1)){
					return true;
				}
			}
		}
		
		return false;		
	}
	
	public boolean showCheckBox(ConfiguratorPart part){
		for(ConfiguratorPartType type : configuratorPartTypeList){
			if(type.getTypeCode().equals(part.getSapMatlTypeCode())){
				if(type.isShowCheckBox()){
					return true;
				}
			}
		}
		
		return false;		
	}
	
	public boolean disableCheckBox(boolean isAddOnTradeUp, ConfiguratorPart part){
		for(ConfiguratorPartType type : configuratorPartTypeList){
			if(type.getTypeCode().equals(part.getSapMatlTypeCode())){
				List<ScenarioConfig> list = type.getDisableCheckBoxConfigList();
				
				if(list == null){
					continue;
				}
				
				for(ScenarioConfig config : list){
					
					if(config.isAddOnTradeUpFlag() == isAddOnTradeUp){
						if(config.getActiveStatus().equals(ON_AGRMNT)
								&& part.isOnAgreement()){
							return true;
						}
						
						if(config.getActiveStatus().equals(ACTIVE_ON_AGRMNT)
								&& part.isActiveOnAgreement()){
							return true;
						}
					}
				}
			}
		}
		
		return false;		
	}
	
	public boolean showOriginalValue(boolean isAddOnTradeUp, ConfiguratorPart part){
		for(ConfiguratorPartType type : configuratorPartTypeList){
			if(type.getTypeCode().equals(part.getSapMatlTypeCode())){
				List<ScenarioConfig> list = type.getShowOriginalValueConfigList();
				
				if(list == null){
					continue;
				}
				
				for(ScenarioConfig config : list){
					if(config.isAddOnTradeUpFlag() == isAddOnTradeUp){
//						logContext.debug(this,"part.getPartNum():"+part.getPartNum());
//						logContext.debug(this,"type.getTypeCode():"+type.getTypeCode());
//						logContext.debug(this,"config.getActiveStatus():"+config.getActiveStatus());
//						logContext.debug(this,"part.isActiveOnAgreement():"+part.isActiveOnAgreement());
						if(config.getActiveStatus().equals(ON_AGRMNT)
								&& part.isOnAgreement()){
							return true;
						}
						
						if(config.getActiveStatus().equals(ACTIVE_ON_AGRMNT)
								&& part.isActiveOnAgreement()){
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean showBillingFrequency(boolean isAddOnTradeUp, ConfiguratorPart part){
		return (getBillingFrequencyConfig(isAddOnTradeUp, part) != null);
	}
	
	public boolean showRampUp(ConfiguratorPart part){
		for(ConfiguratorPartType type : configuratorPartTypeList){
			if(type.getTypeCode().equals(part.getSapMatlTypeCode())){
				return type.showRampUpFlag;
			}
		}
		
		return false;
	}
	
	public boolean showRampUpDropDown(boolean isAddOnTradeUp, boolean isCoTermed, ConfiguratorPart part){
		for(ConfiguratorPartType type : configuratorPartTypeList){
			if(type.getTypeCode().equals(part.getSapMatlTypeCode())){
				List<ScenarioConfig> list = type.getShowRampUpConfigList();
				
				if(list == null){
					continue;
				}
				
				for(ScenarioConfig config : list){
					if(config.isAddOnTradeUpFlag() == isAddOnTradeUp){
						if((config.getCoTermeFlag() != null)
								&& (config.getCoTermeFlag().booleanValue() == isCoTermed)){
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public BillingFrequencyConfig getBillingFrequencyConfig(boolean isAddOnTradeUp, ConfiguratorPart part){
		for(ConfiguratorPartType type : configuratorPartTypeList){
			if(type.getTypeCode().equals(part.getSapMatlTypeCode())){
				List<ScenarioConfig> list = type.getShowBillingFrequencyConfigList();
				if(list == null){
					continue;
				}
				
				for(ScenarioConfig config : list){
					if(config.isAddOnTradeUpFlag() == isAddOnTradeUp){
						return config.getBillingFrequencyConfig();
					}
				}
			}
		}
		
		return null;
	}
	
	public String getOriginalValueLabel(boolean isAddOnTradeUp, ConfiguratorPart part){
		for(ConfiguratorPartType type : configuratorPartTypeList){
			if(type.getTypeCode().equals(part.getSapMatlTypeCode())){
				List<ScenarioConfig> list = type.getShowOriginalValueConfigList();
				if(list == null){
					continue;
				}
				
				for(ScenarioConfig config : list){
					if(config.isAddOnTradeUpFlag() == isAddOnTradeUp){
						return config.getLabel();
					}
				}
			}
		}
		
		return "";
	}
	
	private static class SaaSPartConfig{
		Map SaaSPartTermMap = new HashMap();
		List showHeaderUpToList = new ArrayList();
		List showHeaderLevelList = new ArrayList();

		public Map getSaaSPartTermMap() {
			return SaaSPartTermMap;
		}

		public void setSaaSPartTermMap(Map saaSPartTermMap) {
			SaaSPartTermMap = saaSPartTermMap;
		}

		public List getShowHeaderUpToList() {
			return showHeaderUpToList;
		}

		public void setShowHeaderUpToList(List showHeaderUpToList) {
			this.showHeaderUpToList = showHeaderUpToList;
		}

		public List getShowHeaderLevelList() {
			return showHeaderLevelList;
		}

		public void setShowHeaderLevelList(List showHeaderLevelList) {
			this.showHeaderLevelList = showHeaderLevelList;
		}
	}
	
	/**
	 * @param root
	 * void
	 * load SaaS part config
	 */
	public void loadSaaSPartConfig(Element root){
		Element saasElement = root.getChild("saas-part-config");
		List childrenElems = saasElement.getChildren();
		if(childrenElems != null && childrenElems.size() > 0){
			for (int ei = 0; ei < childrenElems.size(); ei++) {
				Element childElem = (Element) childrenElems.get(ei);
				if(childElem.getName().equals("terms-config")){
					Map termsMap = new HashMap();
					List termsElems = childElem.getChildren();
					if (termsElems != null && termsElems.size() > 0) {
						for (int i = 0; i < termsElems.size(); i++) {
							//max-term-value element
							Element termsChildElem = (Element) termsElems.get(i);
							termsMap.put(termsChildElem.getChildTextTrim("indicator-code"),termsChildElem.getChildTextTrim("max-value"));
						}
						saasPartConfig.setSaaSPartTermMap(termsMap);
					}
				}
				if(childElem.getName().equals("part-price-detail-config")){
					List detailElems = childElem.getChildren();
					if (detailElems != null && detailElems.size() > 0) {
						for (int i = 0; i < detailElems.size(); i++) {
							Element detailChildElem = (Element) detailElems.get(i);
							if(detailChildElem.getName().equals("show-header-up-to")){
								String[] tierModels = detailChildElem.getChild(
									"tier-model").getAttributeValue("value").split(",");
								List listTierModels = new ArrayList();
								for (int j = 0; j < tierModels.length; j++) {
									listTierModels.add(tierModels[j]);
								}
								saasPartConfig.setShowHeaderUpToList(listTierModels);
							}
							if(detailChildElem.getName().equals("show-header-level")){
								String[] tierModels = detailChildElem.getChild(
									"tier-model").getAttributeValue("value").split(",");
								List listTierModels = new ArrayList();
								for (int j = 0; j < tierModels.length; j++) {
									listTierModels.add(tierModels[j]);
								}
								saasPartConfig.setShowHeaderLevelList(listTierModels);
							}
						}
					}
				}//~end of part-price-detail-config
			}
		}
	}
	
	private void loadConfiguratorPartConfig(Element root){
		Element configRoot = root.getChild("configurator-part-config");
		List childrenElems = configRoot.getChildren();
		if(childrenElems != null && childrenElems.size() > 0){
			for (int i = 0; i < childrenElems.size(); i++) {
				Element partTypeConfig = (Element) childrenElems.get(i);
				List children = partTypeConfig.getChildren();
				ConfiguratorPartType configuratorPartType = new ConfiguratorPartType();		
				configuratorPartType.setTypeCode(partTypeConfig.getAttributeValue("value"));	
				logContext.debug(this,"type code:"+partTypeConfig.getAttributeValue("value"));		
				configuratorPartType.setShowRampUp(Boolean.valueOf(partTypeConfig.getAttributeValue("showRampUp")).booleanValue());
				
				for(Iterator it = children.iterator(); it.hasNext(); ){
					Element child = (Element)it.next();
					if(child.getName().equals("show-drop-down")){
						configuratorPartType.
						    setDropDownPricingTierModel(child.getAttributeValue("pricing-tier-model"));
					
					} else if(child.getName().equals("show-input-box")){
						configuratorPartType.setShowQtyInputBox(true);
						configuratorPartType.setQtyInputBoxExceptPricingTierModel(
								child.getAttributeValue("except-pricing-tier-model"));
						
					} else if(child.getName().equals("show-original")){
						List scenarioListEle = child.getChildren();
						
						if(scenarioListEle != null && scenarioListEle.size() > 0){
							for(Iterator scenarioIt = scenarioListEle.iterator(); scenarioIt.hasNext(); ){
								Element scenario = (Element)scenarioIt.next();
								
								ScenarioConfig configObj = new ScenarioConfig();
								configObj.setAddOnTradeUpFlag(Boolean.valueOf(scenario.getAttributeValue("addOnTradeUpFlag")).booleanValue());
								configObj.setActiveStatus(scenario.getAttributeValue("active-status"));
								configObj.setLabel(scenario.getAttributeValue("label"));
								configuratorPartType.addShowOriginalValueConfig(configObj);
							}
						}
					} else if(child.getName().equals("show-billing-frqncy")){
						List scenarioListEle = child.getChildren();
						
						if(scenarioListEle != null && scenarioListEle.size() > 0){
							for(Iterator scenarioIt = scenarioListEle.iterator(); scenarioIt.hasNext(); ){
								Element scenario = (Element)scenarioIt.next();
								
								ScenarioConfig configObj = new ScenarioConfig();
								configObj.setAddOnTradeUpFlag(Boolean.valueOf(scenario.getAttributeValue("addOnTradeUpFlag")).booleanValue());
								
								Element listValueEle = scenario.getChild("list-value");
								BillingFrequencyConfig bfConfig = new BillingFrequencyConfig();
								bfConfig.setDefaultOption(listValueEle.getAttributeValue("default"));
								bfConfig.setOptionsFrom(listValueEle.getText());
								String plusUpFront = scenario.getAttributeValue("plusUpFront");
								if(StringUtils.isBlank(plusUpFront)){
									bfConfig.setPlusUpFront(false);
								} else {
									bfConfig.setPlusUpFront(Boolean.valueOf(plusUpFront));
								}
								
								configObj.setBillingFrequencyConfig(bfConfig);
								
								configuratorPartType.addShowBillingFrequencyConfig(configObj);
							}
						}
					} else if(child.getName().equals("show-checkbox")){
						configuratorPartType.setShowCheckBox(true);
					} else if(child.getName().equals("disable-checkbox")){
						List scenarioListEle = child.getChildren();
						
						if(scenarioListEle != null && scenarioListEle.size() > 0){
							for(Iterator scenarioIt = scenarioListEle.iterator(); scenarioIt.hasNext(); ){
								Element scenario = (Element)scenarioIt.next();
								
								ScenarioConfig configObj = new ScenarioConfig();
								configObj.setAddOnTradeUpFlag(Boolean.valueOf(scenario.getAttributeValue("addOnTradeUpFlag")).booleanValue());
								configObj.setActiveStatus(scenario.getAttributeValue("active-status"));
								configuratorPartType.addDisableCheckBoxConfig(configObj);
							}
						}
					} else if(child.getName().equals("show-ramp-up-drop-down")){
						List scenarioListEle = child.getChildren();
						
						if(scenarioListEle != null && scenarioListEle.size() > 0){
							for(Iterator scenarioIt = scenarioListEle.iterator(); scenarioIt.hasNext(); ){
								Element scenario = (Element)scenarioIt.next();
								
								ScenarioConfig configObj = new ScenarioConfig();
								configObj.setAddOnTradeUpFlag(Boolean.valueOf(scenario.getAttributeValue("addOnTradeUpFlag")).booleanValue());
								String coTermFlag = scenario.getAttributeValue("coTermFlag");
								
								if(coTermFlag != null){
									configObj.setCoTermeFlag(Boolean.valueOf(coTermFlag));
								} else {
									configObj.setCoTermeFlag(null);
								}
								
								configuratorPartType.addShowRampUpConfig(configObj);
							}
						}
					}
				}
				configuratorPartTypeList.add(configuratorPartType);
			}
		}
	}
	
	public int getSaaSPartTermMaxValue(String pricngIndCode){
		String strMaxValue = (String)saasPartConfig.getSaaSPartTermMap().get(pricngIndCode);
		if(strMaxValue != null && !strMaxValue.equals("")){
			return Integer.parseInt(strMaxValue);
		}
		return 60;
	}
	/**
	 * @param qli
	 * @return the max decimal rounding factor
	 * if there is no config for the part type, return -1
	 */
	public int getMaxDecimal(QuoteLineItem qli){
		Map mdpm=displayUIConfig.getMaximumDecimalPlacesMap();
		String strMaxDecimal = (String)mdpm.get(CommonServiceUtil.getSaaSPartType(qli));
		if(strMaxDecimal != null && !strMaxDecimal.equals("")){
			return Integer.parseInt(strMaxDecimal);
		}
		return -1;
	}	
	
	public boolean showHeaderUpTo(String tierModel){
		return saasPartConfig.getShowHeaderUpToList().contains(tierModel);
	}
	public boolean showHeaderLevel(String tierModel){
		return saasPartConfig.getShowHeaderLevelList().contains(tierModel);
	}
	public boolean isUsagePart(QuoteLineItem qli){
		if(displayUIConfig.getUsagePartTypes_PartTypeList().contains(CommonServiceUtil.getSaaSPartType(qli))){
			return true;
		}
		return false;
	}
	

}
