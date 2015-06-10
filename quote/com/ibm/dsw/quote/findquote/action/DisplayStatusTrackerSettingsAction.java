package com.ibm.dsw.quote.findquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayStatusTrackerContract;
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
 * The <code>DisplayStatusTrackerSettingsAction</code> class.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on 2007-5-10
 */
public class DisplayStatusTrackerSettingsAction extends DisplayFindQuoteAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.findquote.action.DisplayFindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        DisplayStatusTrackerContract findByIBMerContract = (DisplayStatusTrackerContract) contract;

        handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByIBMerContract);
        return handler.getResultBean();
    }
    
    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_STATUS_TRACKER_SETTINGS;
    }
}
