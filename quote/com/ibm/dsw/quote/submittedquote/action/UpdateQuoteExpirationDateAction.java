package com.ibm.dsw.quote.submittedquote.action;

import java.util.Date;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.submittedquote.contract.QtDateContract;
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
 * This <code>UpdateQuoteExpirationDateAction<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: Sep 11, 2012
 */

public class UpdateQuoteExpirationDateAction extends BaseContractActionHandler {
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
		ResultBeanException {

        QtDateContract expContract = (QtDateContract) contract;
		String webQuoteNum = expContract.getQuoteNum();
		QuoteUserSession salesRep = expContract.getQuoteUserSession();

//        boolean isQtExpDateValid = expContract.isQtExpDateValid();
//        if ( !isQtExpDateValid )
//            return handleInvalidDate(handler, expContract.getLocale());
		
		Date expDate = expContract.getExpDate();
	    
		logContext.debug(this, "update expiration date of quote["+webQuoteNum+"] to "+expContract.getExpirationMonth()+"/"+expContract.getExpirationDay()+"/"+expContract.getExpirationYear());
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		quoteProcess.updateExpICNCRD(webQuoteNum, expDate, null, null, salesRep.getUserId(), null);

		handler.setState(StateKeys.STATE_AJAX_JSON_RESULT);
		return handler.getResultBean();
	
	}
    

}
