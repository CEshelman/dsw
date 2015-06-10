package com.ibm.dsw.quote.findquote.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByAppvlAttrContract;
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
 * The <code>DisplayFindByCountryAlterAction</code> class.
 * 
 * @author zyuyang@cn.ibm.com
 * 
 * Created on 2010-5-12
 */

public class DisplayFindQuoteByAppvlAttrAction extends DisplayFindQuoteAction {


    /**
     * 
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        DisplayFindQuoteByAppvlAttrContract findByAppvlAttrContract = (DisplayFindQuoteByAppvlAttrContract) contract;

        CacheProcess cp = CacheProcessFactory.singleton().create();
        String sbRegion = findByAppvlAttrContract.getSbRegion();
        
        if (!StringUtils.isEmpty(sbRegion)) {
            handler.addObject(FindQuoteParamKeys.PARAM_SP_BID_DISTRICTS, CacheProcessFactory.singleton().create()
                    .getSpBidDistrictsBySpBidRgn(sbRegion));

        } else {
            handler.addObject(FindQuoteParamKeys.PARAM_SP_BID_DISTRICTS, CacheProcessFactory.singleton().create()
                    .getAllSpBidDistricts());
        }

        handler.addObject(FindQuoteParamKeys.PARAM_SP_BID_GROUPS, CacheProcessFactory.singleton().create()
                .getSpBidGroups());
        handler.addObject(FindQuoteParamKeys.PARAM_SP_BID_TYPES, CacheProcessFactory.singleton().create()
                .getSpBidAppTypes());
        
        handler.addObject(FindQuoteParamKeys.PARAM_SP_BID_REGIONS, CacheProcessFactory.singleton().create()
                .getSpBidRegions());
        
        List countryList = CacheProcessFactory.singleton().create().getCountryList();

        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByAppvlAttrContract);

        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.DisplayFindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_BY_APPVLATTR;
    }
}
