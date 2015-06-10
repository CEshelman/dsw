/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Ivy (duanqf@cn.ibm.com)
 * 
 * Creation date: July 2, 2014
 */
package com.ibm.dsw.quote.scw.addon.process.convert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.dao.SaasConfiguratorDaoFactory;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpConfiguration;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpInfo;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpLineItem;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class ScwAddonTradeupInputParamConvertProcess_Impl implements
		ScwAddonTradeupInputParamConvertProcess {

	protected LogContext logContext = LogContextFactory.singleton().getLogContext();

	private AddOnTradeUpInfo addOnTradeUpInfo;

	public List<AddOrUpdateConfigurationContract> convert(
			AddOnTradeUpInfo addOnTradeUpInfo) {
		this.addOnTradeUpInfo = addOnTradeUpInfo;		
		List<AddOnTradeUpConfiguration> configList=addOnTradeUpInfo.getConfigurations();
		List<AddOrUpdateConfigurationContract> contractList = new ArrayList<AddOrUpdateConfigurationContract>();
		// convert every configuration to contract
		AddOrUpdateConfigurationContract contract;		
		for (AddOnTradeUpConfiguration config : configList) {
			contract = convertConfigToContract(config);
			contractList.add(contract);
		}
		return contractList;
	}
	
	/**
	 * convert a configuration to a AddOrUpdateConfigurationContract
	 * 
	 * @param config
	 *            current configuration
	 * @param items
	 *            the line items in the configuration
	 * @return
	 */
	private AddOrUpdateConfigurationContract convertConfigToContract(
			AddOnTradeUpConfiguration config ) {
		List<AddOnTradeUpLineItem> itemsOfConfig=config.getLineItems();
		AddOrUpdateConfigurationContract contract = new AddOrUpdateConfigurationContract();
		loadQuoteBaseContract(config, itemsOfConfig, contract);
		loadPrepareConfiguratorRedirectDataBaseContract(config, itemsOfConfig, contract);
		loadConfiguratorBaseContract(config, itemsOfConfig, contract);
		loadForPostConfiguratorBaseContract(config, itemsOfConfig, contract);
		return contract;
	}

	

	/**
	 * load date according to QuoteBaseContract
	 */
	private void loadQuoteBaseContract(AddOnTradeUpConfiguration config,
			List<AddOnTradeUpLineItem> items,
			AddOrUpdateConfigurationContract contract) {
		// 1.local
		contract.setLocale(LocaleHelperImpl.getDefaultDSWLocale());
		// 2.userId
		contract.setUserId(addOnTradeUpInfo.getUserId());
		// 3.configuratorType decide use SQO or PGS
		contract.setConfiguratorType(AddOrUpdateConfigurationContract.CONFIGURATOR_TYPE_SQO);
	}

	/**
	 * load date according to PrepareConfiguratorRedirectDataBaseContract
	 */
	private void loadPrepareConfiguratorRedirectDataBaseContract(
			AddOnTradeUpConfiguration config, List<AddOnTradeUpLineItem> items,
			AddOrUpdateConfigurationContract contract) {
		String configId = config.getConfigId();
		int cotermFlag;
		String cotermFlagStr = "", configrtnActionCode = "";
		String updateCAConfigCode = config.getUpdateCAConfigCode();
		// 1. configurationID
		contract.setConfigId(configId);
		// 1.1 original configurationId
		contract.setOrgConfigId(config.getRefConfigId());
		// 2. addOnTradeUpFlag
		if (AddOnTradeUpConfiguration.CONFIGCODE_U.equals(updateCAConfigCode)) {
			contract.setAddOnTradeUpFlag(PartPriceConstants.ADD_ON_TRADE_UP_FLAG);
		}
		// 3. product_id
		contract.setPid(StringUtils.substring(configId, 0, 7));
		// 4. coterm flag
		cotermFlag = config.getCoTermFlag();
		cotermFlagStr = String.valueOf(cotermFlag);
		contract.setCTFlag(cotermFlagStr);
		// 5. overrideFlag
		contract.setOverrideFlag(ConfiguratorConstants.OVERRIDE_FLAG_NO);
		// --auto_load
		contract.setOverridePilotFlag(ConfiguratorConstants.OVERRIDE_PILOT_FLAG_NO);
		// --auto_load
		contract.setOverrideRstrctFlag(ConfiguratorConstants.OVERRIDE_RSTRCT_FLAG_NO);
		// compute configrtnActionCode
		if (AddOnTradeUpConfiguration.CONFIGCODE_A.equals(updateCAConfigCode)
				&& PartPriceConstants.CO_TERM_FLAG.equals(cotermFlagStr)) {
			configrtnActionCode = PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT;
		} else if (AddOnTradeUpConfiguration.CONFIGCODE_A.equals(updateCAConfigCode)
				&& !PartPriceConstants.CO_TERM_FLAG.equals(cotermFlagStr)) {
			configrtnActionCode = PartPriceConstants.ConfigrtnActionCode.NEW_CA_NCT;
		} else if (AddOnTradeUpConfiguration.CONFIGCODE_U.equals(updateCAConfigCode)) {
			configrtnActionCode = PartPriceConstants.ConfigrtnActionCode.ADD_TRD;
		}
		contract.setConfigrtnActionCode(configrtnActionCode);
        // set updateCAConfigCode
        contract.setUpdateCAConfigCode(updateCAConfigCode);
	}

	/**
	 * load date according to ConfiguratorBaseContract
	 */
	private void loadConfiguratorBaseContract(AddOnTradeUpConfiguration config,
			List<AddOnTradeUpLineItem> items,
			AddOrUpdateConfigurationContract contract) {
		// 1.webQuoteNum
		contract.setWebQuoteNum(addOnTradeUpInfo.getWebQuoteNum());
		contract.setChrgAgrmtNum(addOnTradeUpInfo.getChargeAgreementNumber());
		// 2. term
		String term=config.getTermLength();
		contract.setTerm(Integer.parseInt(term));
		try {
			List<ConfiguratorPart> allPartsFrmPid = SaasConfiguratorDaoFactory.singleton().create()
					.findPartsByWebQuoteNumPID4Scw(
							addOnTradeUpInfo.getWebQuoteNum(),
							contract.getPid());
			contract.setAllPartsFrmPid(allPartsFrmPid);
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
		}
		/**
		 * <pre>
		 *  avaliableBillingFrequencyOptions : use to buildConfiguratorHeader method. so not need.
		 *  calcTerm : not method use this value, so not need
		 *  CaEndDate extract from db , so not need.
		 * </pre>
		 */

	}

	/**
	 * load data according to ConfiguratorBaseContract post method
	 */
	private void loadForPostConfiguratorBaseContract(AddOnTradeUpConfiguration config,
			List<AddOnTradeUpLineItem> items,
			AddOrUpdateConfigurationContract contract) {
		ConfiguratorPart part;
		String partNum;
		String quantity;
		for (AddOnTradeUpLineItem item : items) {
			partNum = item.getPartNumber();
			if (!contract.ishavePart(partNum)) {
				part = new ConfiguratorPart();
				// 1.set partNum
				part.setPartNum(partNum);
				// 2. set quantity
				quantity = item.getQuantity();
				part.setPartQtyStr(quantity);
				// 3.Part quantity is 0, thus should be deleted
				if (quantity != null && quantity.length() == 0) {
					part.markDeleted();
				} else if(!quantity.matches("\\d*")||Long.parseLong(quantity)>2147483647) {
					part.setPartQty(1);
				}else if(Integer.parseInt(quantity)==0){
					part.markDeleted();
				}else{
					part.setPartQty(Integer.parseInt(quantity));
				}
				part.markMustHaveQty();
				// 4.set Billing Frequncy code
				part.setBillingFrequencyCode(item.getBillFrequency());

                // 5. set itemNumber
                part.setScwItemNumber(item.getItemNumber());
				contract.addPartToMap(part);

			}

		}
	}
}
