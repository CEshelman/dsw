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

import java.util.List;

import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpInfo;

public interface ScwAddonTradeupInputParamConvertProcess {
	/**
	 * convert SCW input class to the class needed by ConfigratorProccess, one
	 * SCW transaction include more than one configuration , so return more than
	 * one contract
	 * 
	 * @param addOnTradeUpInfo
	 *            SCW input
	 * @return List<AddOrUpdateConfigurationContract>
	 *         'AddOrUpdateConfigurationContract' is needed by
	 *         ConfigratorProccess
	 */
	public List<AddOrUpdateConfigurationContract> convert(
			AddOnTradeUpInfo addOnTradeUpInfo);
}
