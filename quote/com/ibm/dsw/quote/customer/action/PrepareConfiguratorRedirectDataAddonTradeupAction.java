package com.ibm.dsw.quote.customer.action;

import java.util.Iterator;
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
public class PrepareConfiguratorRedirectDataAddonTradeupAction extends PrepareConfiguratorRedirectDataBaseAction{
	
	protected boolean ifCreateNewQuote(Quote quote, String custNum, String caNum, PrepareConfiguratorRedirectDataBaseContract ct){
		return (StringUtils.isBlank(custNum)  || !ct.getCustomerNum().trim().equals(custNum.trim()) // if customer don't match, replace session quote
				|| (  (StringUtils.isNotBlank(caNum) || (StringUtils.isBlank(caNum) && quote.getQuoteHeader().isHasNewConfFlag()) ) && // session quote only need to be replaced when it contains new CA or exsisting CA 
						(
							(StringUtils.isNotBlank(ct.getChrgAgrmtNum()) && !ct.getChrgAgrmtNum().trim().equals(caNum)) //update to exsiting CA, which different with session CA 
							|| (StringUtils.isBlank(ct.getChrgAgrmtNum()) && StringUtils.isNotBlank(caNum)) //update to new CA,  which different with session existing CA (if normal, this case doesn't exist)
						)
					)
			  );
	}

	public Quote fetchCurrentQuote(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack)throws QuoteException{
		Quote quote = super.fetchCurrentQuote(ct, dataPack);
		String custNum = null;
		String caNum = null;
		if(quote!=null){
			if(quote.getCustomer()!=null){
				custNum = quote.getCustomer().getCustNum();
			}
			if(StringUtils.isNotBlank(quote.getQuoteHeader().getRefDocNum())){
				caNum = quote.getQuoteHeader().getRefDocNum().trim();
			}
		}
		
		if(quote == null || ifCreateNewQuote(quote, custNum, caNum, ct)){
			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
			quoteProcess.createQuoteFromOrderForConfigurator(ct.getChrgAgrmtNum(), ct.getUserId(), ct.getQuoteUserSession().getAudienceCode());
			quote = super.fetchCurrentQuote(ct, dataPack);
		}else{
			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
			quoteProcess.copyQuoteInfoFromOrderForConfigurator(ct.getChrgAgrmtNum(), quote.getQuoteHeader().getWebQuoteNum(), ct.getUserId(),  ct.getQuoteUserSession().getAudienceCode());
		}

		
		return quote;
	}
	
	protected void assembleDataPack(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack) throws QuoteException,
    	ResultBeanException{
		dataPack.setAddTradeFlag(CustomerConstants.CONFIGURATOR_ADDON_TRADEUP_FLAG_1);
		dataPack.setSourceType(CustomerConstants.CONFIGURATOR_SOURCE_TYPE_ADDONTRADEUP);

//		this.addReturnedParams(dataPack, CustomerParamKeys.PARAM_RETURN_TO_SQO_CONFIGRTNACTION, CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_AddTrd);
		dataPack.setConfigrtnActionCode(CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_AddTrd);
		
		List<ActiveService> activeServices = dataPack.getExistingPartsInCA();
    	activeServices = this.serviceFilter(activeServices, true);
		
		if(activeServices!=null){
			ActiveService srv = null;
			for(Iterator i = activeServices.iterator();i.hasNext();){
				srv = (ActiveService) i.next();
				if(!CustomerConstants.CONFIGURATOR_RAMPUP_FLAG_1.equals(srv.getRampupFlag())){
					dataPack.addActiveService(srv);
					srv.setActiveOnAgreementFlag(CustomerConstants.CONFIGURATOR_ACTIVEONAGREEMENTFLAG_FLAG_1);
				}
			}
		}

	}
	
}
