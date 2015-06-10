package com.ibm.dsw.quote.partner.action;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.partner.config.PartnerMessageKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.dsw.quote.partner.contract.SearchPartnerContract;
import com.ibm.dsw.quote.partner.exception.PartnerNotFoundException;
import com.ibm.dsw.quote.partner.util.PartnerUtils;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
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
 * The <code>PartnerSearchAction</code> class is the base action class for
 * partner search.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public abstract class PartnerSearchAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        handler.setState(getState(contract));

        SearchPartnerContract c = (SearchPartnerContract) contract;

        if (!PartnerUtils.isValidLob(c.getLobCode()) || !PartnerUtils.isValidCountry(c.getCustCnt())) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }

        handler.addObject(PartnerParamKeys.PARAM_LINE_OF_BUSINESS, c.getLobCode());
        handler.addObject(PartnerParamKeys.PARAM_CUST_COUNTRY, c.getCustCnt());

        handler.addObject(PartnerParamKeys.PARAM_SITE_NAME, c.getName());
        handler.addObject(PartnerParamKeys.PARAM_COUNTRY, c.getCountry());
        handler.addObject(PartnerParamKeys.PARAM_STATE, c.getState());

        handler.addObject(PartnerParamKeys.PARAM_TIER1_RESELLER, c.getTier1());
        handler.addObject(PartnerParamKeys.PARAM_TIER2_RESELLER, c.getTier2());

        handler.addObject(PartnerParamKeys.PARAM_SITE_NUM, c.getNum());
        handler.addObject(PartnerParamKeys.PAREAM_SEARCH_METHOD, c.getSearchMethod());

        handler.addObject(PartnerParamKeys.PARAM_AUTHORIZED_PORT, c.getAuthorizedPort());
        handler.addObject(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM, c.getWebQuoteNum());
        handler.addObject(PartnerParamKeys.PARAM_SEARCH_TIER_TYPE, c.getSearchTierType());
        handler.addObject(ParamKeys.PARAM_IS_SBMT_QT, c.getIsSubmittedQuote());
        handler.addObject(PartnerParamKeys.PARAM_MULTIPLE_PROD_CHK, c.getChkMultipleProd());
        
        String  pageFrom=c.getPageFrom();
        if(pageFrom.equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
	       	 handler.addObject(DraftQuoteParamKeys.PAGE_FROM, pageFrom);
	       	 handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, c.getAgreementNum());
	       	 handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, c.getMigrationReqNum());
	       	 handler.addObject(DraftQuoteParamKeys.SAP_CUST_NUM, c.getSapCustNum());
	       	 handler.addObject(DraftQuoteParamKeys.SAP_CTRCT_NUM, c.getSapCtrctNum());
	       	 handler.addObject(DraftQuoteParamKeys.HIGHLIGHT_ID, c.getHighlightId());
       }
        
        
        SearchResultList srl = null;

        try {
            srl = findPartner(c);
        } catch (PartnerNotFoundException nofe) {
            String msg = getNotFoundMessage(contract);
            handler.addMessage(msg, MessageBeanKeys.INFO);
            return handler.getUndoResultBean();
        }

        //if country not blank, that is to search by attribute
        if (!StringUtils.isBlank(c.getCountry())) {
            Country country = CacheProcessFactory.singleton().create().getCountryByCode3(c.getCountry());
            handler.addObject(PartnerParamKeys.PARAM_COUNTRY_NAME, country.getDesc());

            if (StringUtils.isBlank(c.getState())) {
                handler.addObject(PartnerParamKeys.PARAM_STATE_NAME, "");
            } else {
                for (Iterator iter = country.getStateList().iterator(); iter.hasNext();) {
                    CodeDescObj state = (CodeDescObj) iter.next();
                    if (c.getState().equals(state.getCode())) {
                        handler.addObject(PartnerParamKeys.PARAM_STATE_NAME, state.getCodeDesc());
                        break;
                    }
                }
            }
        }

        handler.addObject(PartnerParamKeys.PARAM_PARTNER_LIST, srl);
        handler.addObject(ParamKeys.PARAM_LOCAL, c.getLocale());

        return handler.getResultBean();
    }
    
    public String getNotFoundMessage(ProcessContract contract) {
        SearchPartnerContract c = (SearchPartnerContract) contract;
        return getI18NString(PartnerMessageKeys.MSG_ERR_NO_PARTNER, I18NBundleNames.ERROR_MESSAGE, c.getLocale());
    }

    protected abstract String getState(ProcessContract contract);

    protected abstract SearchResultList findPartner(SearchPartnerContract contract) throws QuoteException;

}
