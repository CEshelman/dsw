package com.ibm.dsw.quote.newquote.action;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteViewKeys;
import com.ibm.dsw.quote.newquote.contract.NewSalesQuoteContract;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidInputException;
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
 * This <code>NewSalesQuoteAction<code> class.
 *    
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Mar 14, 2007
 */
public class NewSalesQuoteAction extends BaseContractActionHandler {

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        NewSalesQuoteContract newSalesQuoteContract = (NewSalesQuoteContract) contract;
        logContext.debug(this, newSalesQuoteContract.toString());

        CacheProcess CacheProcess = CacheProcessFactory.singleton().create();
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();

        String cntryCode = newSalesQuoteContract.getCountry();
        String lobCode = newSalesQuoteContract.getLob();
        String progMigrationCode = "";
        
        // only FCT quote or FCT 2 PA quote has acquisition code.
        String acqstnCode = "";
        if (CustomerConstants.LOB_FCT.equalsIgnoreCase(lobCode)
                || QuoteConstants.MIGRTN_CODE_FCT_TO_PA.equalsIgnoreCase(lobCode)) {
            acqstnCode = newSalesQuoteContract.getAcquisition();
        }
        
        if (StringUtils.isBlank(cntryCode) || StringUtils.isBlank(lobCode)) {
            throw new NewQuoteInvalidInputException();
        }
        
        CodeDescObj lob = null;
        if (QuoteConstants.MIGRTN_CODE_FCT_TO_PA.equalsIgnoreCase(lobCode)) {
            lobCode = CustomerConstants.LOB_PAUN;
            progMigrationCode = QuoteConstants.MIGRTN_CODE_FCT_TO_PA;
            
            if (StringUtils.isBlank(acqstnCode))
                throw new NewQuoteInvalidInputException();
        }
        
        Country country = CacheProcess.getCountryByCode3(cntryCode);
        lob = CacheProcess.getLOBByCode(lobCode);

        quoteProcess.createNewSessionQuote(country, lob, acqstnCode, newSalesQuoteContract.getUserId(), progMigrationCode, "", newSalesQuoteContract.getQuoteUserSession().getAudienceCode());

        if (NewQuoteParamKeys.PARAM_MARK_DEFAULT_VALUE.equalsIgnoreCase(newSalesQuoteContract.getMarkAsDefault())) {
            //find cookie
            Cookie cookie = newSalesQuoteContract.getSqoCookie();
            //set values
            QuoteCookie.setCountryCookieValue(cookie, newSalesQuoteContract.getCountry());
            QuoteCookie.setLOBCookieValue(cookie, newSalesQuoteContract.getLob());
            QuoteCookie.setAcquisitionCookieValue(cookie, newSalesQuoteContract.getAcquisition());
        }

        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));

        return handler.getResultBean();
    }

    protected String getValidationForm() {
        return NewQuoteViewKeys.FORM_NEW_SALES_QUOTE;
    }

}
