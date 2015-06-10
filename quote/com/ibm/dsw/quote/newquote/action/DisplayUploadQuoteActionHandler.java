package com.ibm.dsw.quote.newquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.home.action.QuoteRightColumnBaseAction;
import com.ibm.dsw.quote.newquote.config.NewQuoteStateKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayUploadQuoteActionHandler</code> is used for presenting
 * sales quote spreadsheet upload screen.
 * 
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: Mar 06, 2007
 */
public class DisplayUploadQuoteActionHandler extends QuoteRightColumnBaseAction {

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.action.SimpleContractAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
	 */
	protected String getState(ProcessContract contract) {
		return NewQuoteStateKeys.STATE_DISPLAY_UPLOAD_PAGE;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.action.QuoteRightColumnBaseAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
	 */
	public ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
	     return handler.getResultBean();
	}

}
