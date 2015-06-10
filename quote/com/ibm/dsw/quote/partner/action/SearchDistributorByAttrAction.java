package com.ibm.dsw.quote.partner.action;

import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerStateKeys;
import com.ibm.dsw.quote.partner.config.PartnerViewKeys;
import com.ibm.dsw.quote.partner.contract.SearchPartnerContract;
import com.ibm.dsw.quote.partner.process.PartnerProcessFactory;
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
 * The <code>SearchDistributorByAttrAction</code> class is to search
 * distriutors by attribute and display the result.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public class SearchDistributorByAttrAction extends PartnerSearchAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.action.PartnerSearchAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        
        SearchPartnerContract spCtrct = (SearchPartnerContract) contract;
        if ("true".equalsIgnoreCase(spCtrct.getIsSubmittedQuote()))
            return PartnerStateKeys.STATE_DISPLAY_SBMTD_QT_DSTRBTR_SEARCH_RESULT;
        else
            return PartnerStateKeys.STATE_DISPLAY_DISTRIBUTOR_SEARCH_RESULT;
    }

    @Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
    	SearchPartnerContract spCtrct = (SearchPartnerContract) contract;
    	Map<String,String> validMap = new HashMap<String,String>();
		validMap.put(ParamKeys.PARAM_IS_SBMT_QT, spCtrct.getIsSubmittedQuote());
		validMap.put(ParamKeys.PARAM_QUOTE_NUM, spCtrct.getWebQuoteNum());
		validMap.put(ParamKeys.PARAM_COUNTRY, spCtrct.getCustCnt());
		validMap.put(ParamKeys.PARAM_LINE_OF_BUSINESS, spCtrct.getLobCode());
		validMap.put(ParamKeys.PARAM_SEARCH_METHOD, spCtrct.getSearchMethod());
		
		if(!com.ibm.dsw.quote.common.domain.ApplianceAddress.validParams(validMap)){
			logContext.error(this, "HttpRequest params are incorrect.");
            throw new QuoteException("HttpRequest params are incorrect.");
		}
    	
    	
		return super.executeBiz(contract, handler);
	}

	/*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.action.PartnerSearchAction#executePartnerSearch(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected SearchResultList findPartner(SearchPartnerContract contract) throws QuoteException {
        SearchPartnerContract c = (SearchPartnerContract) contract;
        if(!c.getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
        	
        	return PartnerProcessFactory.singleton().create().findDistributors(c.getLobCode(), c.getCustCnt(), c.getName(),
                c.getCountry(), c.getState(), Integer.parseInt(c.getPageIndex()), c.getWebQuoteNum(),Integer.parseInt(c.getFct2PAMigrtnFlag()));
        }else{
            return PartnerProcessFactory.singleton().create().findDistributors(c.getLobCode(), c.getCustCnt(), c.getName(),
                    c.getCountry(), c.getState(), Integer.parseInt(c.getPageIndex()), c.getMigrationReqNum(),Integer.parseInt(c.getFct2PAMigrtnFlag()));
            
        }
    }

    protected String getValidationForm() {
        return PartnerViewKeys.SEARCH_DISTRIBUTOR_BY_ATTR_FORM;
    }
}
