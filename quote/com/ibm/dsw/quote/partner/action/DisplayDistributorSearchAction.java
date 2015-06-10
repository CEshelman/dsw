package com.ibm.dsw.quote.partner.action;

import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerStateKeys;
import com.ibm.dsw.quote.partner.contract.DisplayPartnerSearchContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayDistributorSearchAction</code> class is to display
 * distributor search.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public class DisplayDistributorSearchAction extends DisplayPartnerSearchAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.action.AbstractPartnerSearchAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        
        DisplayPartnerSearchContract dpsCtrct = (DisplayPartnerSearchContract) contract;
        if ("true".equalsIgnoreCase(dpsCtrct.getIsSubmittedQuote()))
            return PartnerStateKeys.STATE_DISPLAY_SBMTD_QT_DSTRBTR_SEARCH;
        else
            return PartnerStateKeys.STATE_DISPLAY_DISTRIBUTOR_SEARCH;
    }
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
    ResultBeanException {

		DisplayPartnerSearchContract dpsCtrct = (DisplayPartnerSearchContract) contract;
		Map<String,String> validMap = new HashMap<String,String>();
		if(!dpsCtrct.getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
			validMap.put(ParamKeys.PARAM_QUOTE_NUM, dpsCtrct.getWebQuoteNum());
		}
		validMap.put(ParamKeys.PARAM_COUNTRY, dpsCtrct.getCustCnt());
		validMap.put(ParamKeys.PARAM_LINE_OF_BUSINESS, dpsCtrct.getLobCode());
		validMap.put(ParamKeys.PARAM_IS_SBMT_QT, dpsCtrct.getIsSubmittedQuote());
		if(!com.ibm.dsw.quote.common.domain.ApplianceAddress.validParams(validMap)){
			logContext.error(this, "HttpRequest params are incorrect.");
            throw new QuoteException("HttpRequest params are incorrect.");
		}
		ResultBean superResult = super.executeBiz(contract, handler);
		
		return superResult;
    }

}
