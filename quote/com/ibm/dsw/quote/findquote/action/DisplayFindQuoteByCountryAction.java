package com.ibm.dsw.quote.findquote.action;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByCountryContract;
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
 * The <code>DisplayFindQuoteByCountryAction</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-4-28
 */
public class DisplayFindQuoteByCountryAction extends DisplayFindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.DisplayFindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        DisplayFindQuoteByCountryContract findByCountryContract = (DisplayFindQuoteByCountryContract) contract;

        CacheProcess cp = CacheProcessFactory.singleton().create();
        Country country = null;
        String countryCode = findByCountryContract.getCountry();
        String subRegion = findByCountryContract.getSubRegion();

        if(!StringUtils.isEmpty(subRegion)){
            handler.addObject(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC, CacheProcessFactory.singleton().create()
                    .getCountryListAsCodeDescObj(subRegion));
        }else{
            handler.addObject(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC, CacheProcessFactory.singleton().create()
                    .getCountryListAsCodeDescObj());
        }
        
        if (!StringUtils.isEmpty(countryCode)) {
            country = cp.getCountryByCode3(countryCode);
        }
        if (country != null) {
            handler.addObject(FindQuoteParamKeys.PARAM_STATE_LIST, country.getStateList());
        } else {
            handler.addObject(FindQuoteParamKeys.PARAM_STATE_LIST, new ArrayList());
        }
        
        handler.addObject(FindQuoteParamKeys.SUB_REGION_LIST,CacheProcessFactory.singleton().create().getAllSubRegion());

        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByCountryContract);

        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.DisplayFindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_BY_COUNTRY;
    }
}
