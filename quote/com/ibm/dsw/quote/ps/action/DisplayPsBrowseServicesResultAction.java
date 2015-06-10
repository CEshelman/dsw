package com.ibm.dsw.quote.ps.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfigurationFactory;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.config.PartSearchStateKeys;
import com.ibm.dsw.quote.ps.contract.PartSearchContract;
import com.ibm.dsw.quote.ps.domain.PartSearchServiceResult;
import com.ibm.dsw.quote.ps.process.PartSearchProcess;
import com.ibm.dsw.quote.ps.process.PartSearchProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2011 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayPsBrowseServicesResultAction</code> 
 * 
 * 
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: June 03, 2011
 */
public class DisplayPsBrowseServicesResultAction  extends PartSearchBaseAction {

	 protected String getState(ProcessContract contract) {   
	        return PartSearchStateKeys.STATE_DISPLAY_PARTSEARCH_BROWSE_SERVICES_RESULT;
	 }
	 
	 public ResultBean perform(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
	        PartSearchContract psCt = (PartSearchContract)contract;
	        String lob = psCt.getLob();
	        handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
	        CacheProcess cp = CacheProcessFactory.singleton().create();
	        Country country = cp.getCountryByCode3(psCt.getCountry());
	        if(country != null){
	            handler.addObject(ParamKeys.PARAM_COUNTRY, country);
	            String currencyCode = psCt.getCurrency();
	            handler.addObject(ParamKeys.PARAM_CURRENCY,currencyCode);
	            
	            List currencyList = country.getCurrencyList();
	            if (currencyList.size() == 0){
	                throw new QuoteException("Country:"+psCt.getCountry()+" has no default currency");
	            }else{
	            	CodeDescObj cntryCurrency = (CodeDescObj) currencyList.get(0);
	            	handler.addObject(PartSearchParamKeys.PARAM_COUNTRY_CURRENCY, cntryCurrency);
	                if (!currencyCode.equalsIgnoreCase(cntryCurrency.getCode())){
	                    handler.addObject(PartSearchParamKeys.PARAM_SHOWHINT,Boolean.TRUE);
	                }
	            }
	        }else{
	            throw new QuoteException("The input country does not exist!");
	        }
	        
	        if(psCt.getQuoteFlag() != null && StringHelper.hasNonRegularChar(psCt.getQuoteFlag())){
		    	   throw new QuoteException("Invalid request parameters. Quote flag are illegal entries.");
		       }
	        
	        List<MonthlySoftwareConfiguration> confgrtnList;
			try {
				confgrtnList = MonthlySoftwareConfigurationFactory.singleton().findMonthlySwConfiguration(psCt.getQuoteNum());
			
	        if(confgrtnList !=null && confgrtnList.size() > 0){
	        	for( MonthlySoftwareConfiguration confgrt  : confgrtnList){
	        		if(confgrt.isAddNewMonthlySWFlag()){
	        			psCt.setIsAddNewMonthlySWFlag("true");
	        			psCt.setChrgAgrmtNum(confgrt.getChrgAgrmtNum());
	        			psCt.setConfigrtnActionCode(confgrt.getConfigrtnActionCode());
	        			psCt.setConfigrtnId(confgrt.getConfigrtnId());
	        			psCt.setOrgConfigId(confgrt.getConfigrtnIdFromCa());
	        			break;
	        		}
	        	}
	        }
			} catch (TopazException e) {
				 logContext.error(this, e.getMessage());
				
			}
	        
			// validate  chrgAgrmtNum ---numeric
			if(StringUtils.isNotBlank(psCt.getChrgAgrmtNum()) && !StringUtils.isNumeric(psCt.getChrgAgrmtNum())){
				throw new QuoteException("Invalid chrgAgrmtNum value.");
			}
			// validate  configrtnId ---numeric
			if(StringUtils.isNotBlank(psCt.getConfigrtnId()) && !StringUtils.isNumeric(psCt.getConfigrtnId())){
				throw new QuoteException("Invalid configrtnId value.");
			}
			// validate  orgConfigId ---numeric
			if(StringUtils.isNotBlank(psCt.getOrgConfigId()) && !StringUtils.isNumeric(psCt.getOrgConfigId())){
				throw new QuoteException("Invalid orgConfigId value.");
			}
			// validate  configrtnActionCode ---alpha
			if(StringUtils.isNotBlank(psCt.getConfigrtnActionCode()) && !StringUtils.isAlpha(psCt.getConfigrtnActionCode())){
				throw new QuoteException("Invalid configrtnActionCode value.");
			}
			// validate  isAddNewMonthlySWFlag ---alpha
			if(StringUtils.isNotBlank(psCt.getIsAddNewMonthlySWFlag()) && !StringUtils.isAlpha(psCt.getIsAddNewMonthlySWFlag())){
				throw new QuoteException("Invalid isAddNewMonthlySWFlag value.");
			}
			
	        handler.addObject(ParamKeys.PARAM_AUDIENCE, psCt.getAudience());
	        handler.addObject(ParamKeys.PARAM_QUOTE_NUM, psCt.getQuoteNum());
	        handler.addObject(ParamKeys.PARAM_QUOTE_FLAG, psCt.getQuoteFlag());
	        handler.addObject(ParamKeys.PARAM_CUST_NUM, psCt.getCustomerNumber());
	        handler.addObject(ParamKeys.PARAM_SAP_CONTRACT_NUM, psCt.getSapContractNum());
	        handler.addObject(ParamKeys.PARAM_ADD_NEW_MONTHLY_SW, psCt.getIsAddNewMonthlySWFlag());
	        handler.addObject(ParamKeys.PARAM_CHRG_AGRMT_NUM, psCt.getChrgAgrmtNum());
	        handler.addObject(ParamKeys.PARAM_CONFIGRTN_ID, psCt.getConfigrtnId());
	        handler.addObject(ParamKeys.PARAM_CONFIGRTN_ACTION_CODE, psCt.getConfigrtnActionCode());
	        handler.addObject(ParamKeys.PARAM_ORG_CONFIG_ID, psCt.getOrgConfigId());
	        handler.setState(getState(psCt));
	             
	        try{
	        	PartSearchProcess partProcess = PartSearchProcessFactory.singleton().create();
	        	PartSearchServiceResult partSearchServiceResult = partProcess
	        		.getPartSearchServiceResults(psCt.getUserId());
	        	handler.addObject(PartSearchParamKeys.BROWSE_SERVICES_RESULTS,partSearchServiceResult.getServices());
	        	handler.addObject(PartSearchParamKeys.BROWSE_SERVICES_AGREEMENTS,partSearchServiceResult.getAgreements());
	        	handler.addObject(PartSearchParamKeys.BROWSE_SERVICES_BRANDS_LIST, partSearchServiceResult.getProdBrandsList());
	        	handler.addObject(PartSearchParamKeys.BROWSE_SERVICES_HAS_CONF_AGR, partSearchServiceResult.isHasConfigrtn());
	        	handler.addObject(PartSearchParamKeys.BROWSE_SERVICES_CONFIGURED_PIDS, partSearchServiceResult.getConfiguredPids());
	        }catch (TopazException e) {
	            logContext.error(this, e.getMessage());
	            throw new QuoteException(e);
	        }
	        return handler.getResultBean();
	    }

}
