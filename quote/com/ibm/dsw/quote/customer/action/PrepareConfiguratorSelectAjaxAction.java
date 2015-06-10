package com.ibm.dsw.quote.customer.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PrepareConfiguratorRedirectDataBaseAction<code> class.
 * This action is responsible for fetching configurator selection information
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2011-11-03
 */
public class PrepareConfiguratorSelectAjaxAction extends BaseContractActionHandler{
	private transient LogContext logContext = LogContextFactory.singleton().getLogContext();
	

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		
		PrepareConfiguratorRedirectDataBaseContract ct = (PrepareConfiguratorRedirectDataBaseContract)contract;
		Quote quote = this.fetchCurrentQuote(ct);

        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        int configIndcator = quoteProcess.isProdSuppbyCPQ(ct.getPid(),quote.getQuoteHeader().getWebQuoteNum(), ct.getQuoteUserSession().getAudienceCode());
        
        handler.addObject(CustomerParamKeys.PARAM_CONFIG_INDICATOR, Integer.toString(configIndcator));
		
        handler.setState(CustomerStateKeys.STATE_CONF_SELECT_RESULT);
		return handler.getResultBean();
	}
	

	public Quote fetchCurrentQuote(PrepareConfiguratorRedirectDataBaseContract ct)throws QuoteException{
		Quote quote = this.fetchCurrentExistQuote(ct);
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
		
		if(quote == null || (  ("AD".equals(ct.getCallingType()) || "CP".equals(ct.getCallingType())) && ifCreateNewQuote(quote, custNum, caNum, ct))){
			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
			quoteProcess.createQuoteFromOrderForConfigurator(ct.getChrgAgrmtNum(), ct.getUserId(), ct.getQuoteUserSession().getAudienceCode());
			quote = this.fetchCurrentExistQuote(ct);
		}

		
		return quote;
	}
	
	public Quote fetchCurrentExistQuote(PrepareConfiguratorRedirectDataBaseContract ct)throws QuoteException{
		 	QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();

	        Quote quote = null;
	        
	        try {
	            quote = quoteProcess.getDraftQuoteBaseInfo(ct.getUserId());
	        } catch (NoDataException e) {
	            logContext.error(this, "NoDataExceptoin accor when geting quote base info.");
	            logContext.error(this, e.getMessage());
	            return null; 
	        } catch (QuoteException e) {
	            logContext.error(this, e.getMessage());
	            throw e;
	        }
	        
	        return quote;
	}

	private boolean ifCreateNewQuote(Quote quote, String custNum, String caNum, PrepareConfiguratorRedirectDataBaseContract ct){
		return (StringUtils.isBlank(custNum)  || !ct.getCustomerNum().trim().equals(custNum.trim()) // if customer don't match, replace session quote
				|| (  (StringUtils.isNotBlank(caNum) || (StringUtils.isBlank(caNum) && quote.getQuoteHeader().isHasNewConfFlag()) ) && // session quote only need to be replaced when it contains new CA or exsisting CA 
						(
							(StringUtils.isNotBlank(ct.getChrgAgrmtNum()) && !ct.getChrgAgrmtNum().trim().equals(caNum)) //update to exsiting CA, which different with session CA 
							|| (StringUtils.isBlank(ct.getChrgAgrmtNum()) && StringUtils.isNotBlank(caNum)) //update to new CA,  which different with session existing CA (if normal, this case doesn't exist)
						)
					)
			  );
	}
	
    
}
