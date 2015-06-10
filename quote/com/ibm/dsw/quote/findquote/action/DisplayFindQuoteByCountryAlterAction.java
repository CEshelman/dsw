package com.ibm.dsw.quote.findquote.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseParameterActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByCountryAlterAction</code> class.
 * 
 * @author wangxu@cn.ibm.com
 * 
 * Created on 2007-5-22
 */
public class DisplayFindQuoteByCountryAlterAction extends BaseParameterActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseParameterActionHandler#executeBiz(com.ibm.ead4j.jade.util.Parameters)
     */
    public ResultBean executeBiz(Parameters params) throws QuoteException, ResultBeanException {

        ResultHandler handler = new ResultHandler(params);
        String countryCode = params.getParameterAsString(FindQuoteParamKeys.COUNTRY);
        String subRegionCode = params.getParameterAsString(FindQuoteParamKeys.SUB_REGION);
        Country country = null;
        handler.addObject(FindQuoteParamKeys.COUNTRY, countryCode);

        if(subRegionCode != null && StringUtils.isEmpty(countryCode)){
            if(subRegionCode.trim().equals("")){
                handler.addObject(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC, CacheProcessFactory.singleton().create()
                        .getCountryListAsCodeDescObj());
            }else{
                handler.addObject(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC, CacheProcessFactory.singleton().create()
                        .getCountryListAsCodeDescObj(subRegionCode));//(subRegionCode)
            }
            handler.addObject("requestFrom","subRegion");
        }else if(StringUtils.isEmpty(subRegionCode) && StringUtils.isNotEmpty(countryCode)){
            country = CacheProcessFactory.singleton().create().getCountryByCode3(countryCode);
            handler.addObject(FindQuoteParamKeys.PARAM_STATE_LIST, country.getStateList());
            handler.addObject("requestFrom","country");
        }
        
        handler.setState(FindQuoteStateKeys.STATE_ALTER_DISPLAY_FIND_BY_COUNTRY);
        return handler.getResultBean();
    }

}
