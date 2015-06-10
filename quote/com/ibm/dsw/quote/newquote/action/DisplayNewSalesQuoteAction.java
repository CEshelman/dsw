package com.ibm.dsw.quote.newquote.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.appcache.domain.AcqCodeDesc;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.home.action.QuoteRightColumnBaseAction;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteStateKeys;
import com.ibm.dsw.quote.newquote.contract.DisplayNewSalesQuoteContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayNewSalesQuoteAction</code>
 * 
 * @author chenzhh@cn.ibm.com
 *  
 */
public class DisplayNewSalesQuoteAction extends QuoteRightColumnBaseAction {


	private static final long serialVersionUID = -5231668064108042862L;

	protected String getState(ProcessContract contract) {
        return NewQuoteStateKeys.STATE_DISPLAY_NEW_QUOTE;
    }

    public ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        DisplayNewSalesQuoteContract displayNewSalesQuoteContract = (DisplayNewSalesQuoteContract) contract;

        List countries = CacheProcessFactory.singleton().create().getCountryList();
        List lobs = CacheProcessFactory.singleton().create().getLOBList();
        List acqstns = filterAcqstns(CacheProcessFactory.singleton().create().getAcquisitionList());
        
        List displayLobs = new ArrayList();
        if (lobs != null) {
            for (int i = 0; i < lobs.size(); i++) {
                CodeDescObj lob = (CodeDescObj) lobs.get(i);
                // remove PA & PAE
                if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob.getCode())
                        || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob.getCode()))
                    continue;
                displayLobs.add(lob);
            }

        }
        
        handler.addObject(NewQuoteParamKeys.PARAM_COUNTRY_LIST, countries);
        handler.addObject(NewQuoteParamKeys.PARAM_LOB_LIST, displayLobs);
        handler.addObject(NewQuoteParamKeys.PARAM_ACQUISITION_LIST, acqstns);

        handler.addObject(NewQuoteParamKeys.PARAM_DEFAULT_COUNTRY, displayNewSalesQuoteContract.getDefaultCountry());
        handler.addObject(NewQuoteParamKeys.PARAM_DEFAULT_LOB, displayNewSalesQuoteContract.getDefaultLOB());
        handler.addObject(NewQuoteParamKeys.PARAM_DEFAULT_ACQSTN, displayNewSalesQuoteContract.getDefaultAcquisition());

        return handler.getResultBean();
    }
    
    protected List filterAcqstns(List list)
    {
    	List retList = new ArrayList();
    	if ( list == null )
    	{
    		return retList;
    	}
    	for ( int i = 0; i < list.size(); i++ )
    	{
    		AcqCodeDesc acq = (AcqCodeDesc)list.get(i);
    		if ( acq.isEffFlag() )
    		{
    			retList.add(acq);
    		}
    	}
    	return retList;
    }
    
    protected String getI18NString(String basename, Locale locale, String key) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        return appCtx.getI18nValueAsString(basename, locale, key);
    }    
}
