package com.ibm.dsw.quote.findquote.action;

import java.util.ArrayList;

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
 * @author zyuyang@cn.ibm.com
 * 
 * Created on 2010-5-12
 */
public class DisplayFindQuoteByAppvlAttrAlterAction extends BaseParameterActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseParameterActionHandler#executeBiz(com.ibm.ead4j.jade.util.Parameters)
     */
    public ResultBean executeBiz(Parameters params) throws QuoteException, ResultBeanException {

        ResultHandler handler = new ResultHandler(params);

        String sbRegion = params.getParameterAsString(FindQuoteParamKeys.SB_REGION);

        if (sbRegion != null && !sbRegion.equals("")) {

            handler.addObject(FindQuoteParamKeys.PARAM_SP_BID_DISTRICTS, CacheProcessFactory.singleton().create()
                    .getSpBidDistrictsBySpBidRgn(sbRegion));

        }else if(sbRegion.equals("")){
            handler.addObject(FindQuoteParamKeys.PARAM_SP_BID_DISTRICTS, CacheProcessFactory.singleton().create()
                    .getAllSpBidDistricts());
        }else {
            handler.addObject(FindQuoteParamKeys.PARAM_SP_BID_DISTRICTS, new ArrayList());
        }
        handler.setState(FindQuoteStateKeys.STATE_ALTER_DISPLAY_FIND_BY_APPVLATTR);
        return handler.getResultBean();
    }

}
