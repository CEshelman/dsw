package com.ibm.dsw.quote.customer.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.ead4j.jade.bean.ResultBeanException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PrepareConfiguratorRedirectDataBaseAction<code> class.
 * This action is responsible for recieving informations from 'brand new' or 'Add-on/tread-up' and prepare parameters sending to CPQ(or SQO configuration) 
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2011-6-16
 */
public class PrepareConfiguratorRedirectDataCopyConfigAction extends PrepareConfiguratorRedirectDataAddonTradeupAction{
	

	protected boolean ifCreateNewQuote(Quote quote, String custNum, String caNum, PrepareConfiguratorRedirectDataBaseContract ct){
		return (StringUtils.isBlank(custNum)  || !ct.getCustomerNum().trim().equals(custNum.trim()) // if customer don't match, replace session quote
				|| (  (StringUtils.isNotBlank(caNum) || (StringUtils.isBlank(caNum) && quote.getQuoteHeader().isHasNewConfFlag()) ) && // session quote only need to be replaced when it contains new CA or exsisting CA 
						(
							(StringUtils.isNotBlank(ct.getTarChrgAgrmtNum()) && !ct.getTarChrgAgrmtNum().trim().equals(caNum)) //copy to exsiting CA, which different with session CA 
							|| (StringUtils.isBlank(ct.getTarChrgAgrmtNum()) && StringUtils.isNotBlank(caNum)) //copy to new CA,  which different with session existing CA
						)
					)
			  );
	}

	public Quote fetchCurrentQuote(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack)throws QuoteException{
		return super.fetchCurrentQuote(ct, dataPack);
	}
	
	protected void assembleDataPack(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack) throws QuoteException,
    	ResultBeanException{

		dataPack.setAddTradeFlag(CustomerConstants.CONFIGURATOR_ADDON_TRADEUP_FLAG_0);
		dataPack.setSourceType(CustomerConstants.CONFIGURATOR_SOURCE_TYPE_COPY);
//        this.addReturnedParams(dataPack, CustomerParamKeys.PARAM_RETURN_TO_SQO_CHRG_AGR_NUM, ct.getTarChrgAgrmtNum());
        dataPack.setChrgAgrmtNum(ct.getTarChrgAgrmtNum());
//        this.addReturnedParams(dataPack, CustomerParamKeys.PARAM_RETURN_TO_SQO_CONFIGRTNACTION, CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_NewNCt);
        dataPack.setConfigrtnActionCode(CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_NewNCt);
		
        dataPack.setConfigId(null); // copy always use new configuration id in SQO

		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		List<ActiveService> activeServices = quoteProcess.retrieveLineItemsFromOrder(ct.getChrgAgrmtNum(), ct.getConfigId());
		if(CustomerConstants.CONFIGURATOR_FLAG_FALSE.equals(ct.getCopyFromActiveCAFlag())){
	    	activeServices = this.serviceFilter(activeServices, false);
		}else{
	    	activeServices = this.serviceFilter(activeServices, true);
		}
		if(activeServices!=null){
			for(ActiveService as: activeServices){
				as.setActiveOnAgreementFlag(CustomerConstants.CONFIGURATOR_ACTIVEONAGREEMENTFLAG_FLAG_0);
			}
			dataPack.addActiveService(activeServices);
		}
		
	}
}
