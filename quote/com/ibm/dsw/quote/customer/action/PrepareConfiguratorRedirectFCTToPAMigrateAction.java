package com.ibm.dsw.quote.customer.action;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

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
 * Creation date: 2012-6-07
 */
public class PrepareConfiguratorRedirectFCTToPAMigrateAction extends PrepareConfiguratorRedirectDataAddonTradeupAction{
	private transient LogContext logContext = LogContextFactory.singleton().getLogContext();
	
	protected boolean ifCreateNewQuote(Quote quote, String custNum, String caNum, PrepareConfiguratorRedirectDataBaseContract ct){
		String needNewQuote = ct.getNeedNewQuote();
		return !"0".equalsIgnoreCase(needNewQuote);
	}

	protected void assembleDataPack(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack) throws QuoteException,
    	ResultBeanException{
		
		super.assembleDataPack(ct, dataPack);
		dataPack.setConfigrtnActionCode(CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_FCTToPA);

		Quote quote = dataPack.getQuote();
		MigrationRequestProcessFactory.singleton().create().updateNewPAWebQuote(ct.getUserId(), quote.getQuoteHeader().getWebQuoteNum(), "1");
	}

//	public ConfigPreProcessor getPreProcessor(){
//		return new FctToPAPreProcessor();
//	}
	
}
