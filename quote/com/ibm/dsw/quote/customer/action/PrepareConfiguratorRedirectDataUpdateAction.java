package com.ibm.dsw.quote.customer.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataUpdateContract;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.ead4j.jade.bean.ResultBeanException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PrepareConfiguratorRedirectDataUpdateAction<code> class.
 * This action is responsible for recieving informations from 'brand new' or 'Add-on/tread-up' and prepare parameters sending to CPQ(or SQO configuration) 
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2011-6-16
 */
public class PrepareConfiguratorRedirectDataUpdateAction extends PrepareConfiguratorRedirectDataBaseAction{

	protected void assembleDataPack(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack) throws QuoteException,
    	ResultBeanException{
		dataPack.setSourceType(CustomerConstants.CONFIGURATOR_SOURCE_TYPE_REEDIT);
		PrepareConfiguratorRedirectDataUpdateContract ctu =  (PrepareConfiguratorRedirectDataUpdateContract) ct;

		List<ActiveService> activeServices = dataPack.getExistingPartsInCA();
		activeServices = this.serviceFilter(activeServices, true);

	    dataPack.setConfigrtnActionCode(ct.getConfigrtnActionCode());
		Set<String> activeSrvSet = new HashSet();
		if(activeServices!=null && activeServices.size()>0){
				for(ActiveService as: activeServices){
					activeSrvSet.add(as.getPartNumber().trim());
				}
		}
		
		String addOnTradeUpFlag = ctu.getAddOnTradeUpFlag();
		dataPack.setAddTradeFlag(addOnTradeUpFlag);
		dataPack.setConfigurationID(ctu.getCpqConfigurationID());
		
		List allItemList = ctu.getActiveServices();
		HashSet replacedPartSet = new HashSet();
		if(allItemList!=null&&allItemList.size()>0){
			ActiveService as = null;
			for(Iterator i = allItemList.iterator();i.hasNext();){
				as = (ActiveService) i.next();
				if("1".equals(as.getReplaced())){
					replacedPartSet.add(as.getPartNumber());
					i.remove();
					continue;
				}
				if(activeSrvSet.contains(as.getPartNumber())){
					as.setActiveOnAgreementFlag(CustomerConstants.CONFIGURATOR_ACTIVEONAGREEMENTFLAG_FLAG_1);
				}else{
					as.setActiveOnAgreementFlag(CustomerConstants.CONFIGURATOR_ACTIVEONAGREEMENTFLAG_FLAG_0);
				}
				if("1".equals(addOnTradeUpFlag)){
					if("1".equals(as.getRampupFlag())){
						i.remove();
						continue;
					}
				}
			}
			
		}
		
		if(allItemList!=null&&allItemList.size()>0){
			dataPack.addActiveService(allItemList);
		}
		

		if(CustomerParamKeys.CPQ.equals(dataPack.getConfiguratorType()) && activeServices != null){
			for(ActiveService as: activeServices){
				if(!replacedPartSet.contains(as.getPartNumber())){
					dataPack.addActiveService(as);
				}
			}
		}
		
	}
}
