package com.ibm.dsw.quote.ps.action;

import java.util.List;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.ps.config.PartSearchMessageKeys;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.contract.PartSearchContract;
import com.ibm.dsw.quote.ps.domain.PartSearchDataFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>DisplaySearchCustomerAction</code>
 *
 *
 * @author: wnan@cn.ibm.com
 *
 * Creation date: Jan 26, 2007
 */
public class FindRelatedReplacementPartsAction extends PartSearchBaseAction {
    private static LogContext logger = LogContextFactory.singleton().getLogContext();
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.SimpleContractAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        boolean isRenewal = ((PartSearchContract) contract).isRenewal();
        boolean isSubmitted = ((PartSearchContract) contract).isSubmitted();
        String state = null;
        /*NOTE: Just ignore finding related parts*/
        if (isSubmitted && PartSearchParamKeys.GET_REPLACEMENT_PARTS.equals(((PartSearchContract) contract).getRetrievalType())) {
            state = DraftQuoteStateKeys.STATE_DISPLAY_REPLACEMENT_PARTS_SUBMITTED;
        } else if (isRenewal && PartSearchParamKeys.GET_REPLACEMENT_PARTS.equals(((PartSearchContract) contract).getRetrievalType())) {
            state = DraftQuoteStateKeys.STATE_DISPLAY_REPLACEMENT_PARTS_RENEWAL;
        } else {
            state = DraftQuoteStateKeys.STATE_DISPLAY_RELATED_PARTS;
        }
        return state;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean perform(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        PartSearchContract ct = (PartSearchContract)contract;

        String lob = ct.getLob();
        boolean addSuccess = ct.isAddSuccess();

        handler.addObject(PartSearchParamKeys.PARAM_PART_NUMBERS,ct.getPartNumber());
        handler.addObject(PartSearchParamKeys.PARAM_DATA_RETRIEVALTYPE, ct.getRetrievalType());
        handler.addObject(PartSearchParamKeys.TREE_NAME, PartSearchDataFactory.getTreeLevelType(lob, PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS));
        handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
        handler.addObject(ParamKeys.PARAM_ACQRTN_CODE, ct.getAcqrtnCode());
        handler.addObject(PartSearchParamKeys.PARAM_RENEWAL, Boolean.valueOf(ct.isRenewal()));
        handler.addObject(PartSearchParamKeys.PARAM_SUBMITTED, Boolean.valueOf(ct.isSubmitted()));

        handler.addObject(ParamKeys.PARAM_QUOTE_NUM, ct.getQuoteNum());
        handler.addObject(PartSearchParamKeys.PARAM_SEQ_NUM, ct.getSeqNum());
        handler.addObject(PartSearchParamKeys.PARAM_REPLACEMENT_FLAG, Boolean.valueOf(ct.isReplacementFlag()));
        handler.addObject(ParamKeys.PARAM_QUOTE_FLAG, ct.getQuoteFlag());

        CacheProcess cp = CacheProcessFactory.singleton().create();

        Country country = cp.getCountryByCode3(ct.getCountry());
        if(country != null){
            handler.addObject(ParamKeys.PARAM_COUNTRY, country);
            String currencyCode = ct.getCurrency();
            handler.addObject(ParamKeys.PARAM_CURRENCY,currencyCode);

            List currencyList = country.getCurrencyList();
            if (currencyList.size() == 0){
                throw new QuoteException("Country:"+ct.getCountry()+" has no default currency");
            }else{
            	CodeDescObj cntryCurrency = (CodeDescObj) currencyList.get(0);
            	handler.addObject(PartSearchParamKeys.PARAM_COUNTRY_CURRENCY, cntryCurrency);
                if (!currencyCode.equalsIgnoreCase((cntryCurrency).getCode())){
                    handler.addObject(PartSearchParamKeys.PARAM_SHOWHINT,Boolean.TRUE);
                }
            }

        }else{
            throw new QuoteException("The input country does not exist!");
        }

        handler.addObject(ParamKeys.PARAM_AUDIENCE, ct.getAudience());
        handler.addObject(ParamKeys.PARAM_STATE, DraftQuoteStateKeys.STATE_DISPLAY_RELATED_PARTS);

        handler.setState(getState(contract));

        if(addSuccess){
            String message = getI18NString(PartSearchMessageKeys.ADD_SUCCESS,I18NBundleNames.PART_SEARCH_BASE, ct.getLocale());
            ResultBean rb = handler.getResultBean();
            rb.getMessageBean().addMessage(message ,MessageBeanKeys.INFO);
            //System.err.println("Info message added!");
        }

        return handler.getResultBean();
    }

    protected boolean validate(ProcessContract contract) {
        return true;
    }

}
