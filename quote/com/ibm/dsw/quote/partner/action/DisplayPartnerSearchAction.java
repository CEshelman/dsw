package com.ibm.dsw.quote.partner.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.dsw.quote.partner.contract.DisplayPartnerSearchContract;
import com.ibm.dsw.quote.partner.process.PartnerProcess;
import com.ibm.dsw.quote.partner.process.PartnerProcessFactory;
import com.ibm.dsw.quote.partner.util.PartnerUtils;
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
 * The <code>AbstractPartnerSearchAction</code> class is the base action class
 * to display partner search.
 * 
 * @author: cuixg@cn.ibm.com
 * 
 * Created on: Mar 15, 2007
 */
public abstract class DisplayPartnerSearchAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        handler.setState(getState(contract));

        DisplayPartnerSearchContract c = (DisplayPartnerSearchContract) contract;

        if (!PartnerUtils.isValidLob(c.getLobCode()) || !PartnerUtils.isValidCountry(c.getCustCnt())) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }

        CacheProcess cp = CacheProcessFactory.singleton().create();
        PartnerProcess psp = PartnerProcessFactory.singleton().create();
        String  pageFrom=c.getPageFrom();
        if(pageFrom.equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
	       	 handler.addObject(DraftQuoteParamKeys.PAGE_FROM, pageFrom);
	       	 handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, c.getMigrationReqNum());
       }

        //only valid countries should be shown. if 0 valid country was
        // found, then show all country;
        List countryList = psp.findValidCountryList(c.getCustCnt(), c.getLobCode());

        handler.addObject(NewQuoteParamKeys.PARAM_COUNTRY_LIST, countryList);

        Country country;
        if (!StringUtils.isEmpty(c.getSavedCountry())) {
            country = cp.getCountryByCode3(c.getSavedCountry());
        } else {
            country = cp.getCountryByCode3(c.getCustCnt());
        }

        if (PartnerUtils.isNorthAmerica(country.getCode3())) {
            handler.addObject(PartnerParamKeys.PARAM_STATE_LIST, country.getStateList());
        } else {
            handler.addObject(PartnerParamKeys.PARAM_STATE_LIST, new ArrayList());
        }

        List authPortfolioList = psp.findCtrldProductPorfolios();
        handler.addObject(PartnerParamKeys.PARAM_AUTHORIZED_PORTFOLIO_LIST, authPortfolioList);

        handler.addObject(PartnerParamKeys.PARAM_LINE_OF_BUSINESS, c.getLobCode());
        handler.addObject(PartnerParamKeys.PARAM_CUST_COUNTRY, c.getCustCnt());
        handler.addObject(ParamKeys.PARAM_LOCAL, c.getLocale());
        addContractContentToResult(contract, handler);

        return handler.getResultBean();
    }

    protected abstract String getState(ProcessContract contract);

    protected void addContractContentToResult(ProcessContract contract, ResultHandler handler) {
        DisplayPartnerSearchContract c = (DisplayPartnerSearchContract) contract;
        
        handler.addObject(PartnerParamKeys.PARAM_SEARCH_TIER_TYPE, c.getSearchTierType());
        handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM, c.getWebQuoteNum());
        handler.addObject(ParamKeys.PARAM_IS_SBMT_QT, c.getIsSubmittedQuote());

        if (!c.isNew()) {
            if (c.isFromSearchByNum()) {
                handler.addObject(PartnerParamKeys.PARAM_SITE_NUM, c.getSavedCustNum());
            } else if (c.isFromSearchByPort()) {
                handler.addObject(PartnerParamKeys.PARAM_AUTHORIZED_PORT, c.getSavedAuthorizedPort());
                handler.addObject(PartnerParamKeys.PARAM_COUNTRY, c.getSavedCountry());
                handler.addObject(PartnerParamKeys.PARAM_MULTIPLE_PROD_CHK, c.getChkMultipleProd());
            } else {
                handler.addObject(PartnerParamKeys.PARAM_SITE_NAME, c.getSavedCustName());
                handler.addObject(PartnerParamKeys.PARAM_COUNTRY, c.getSavedCountry());
                handler.addObject(PartnerParamKeys.PARAM_STATE, c.getSavedState());
            }
        }
    }

}
