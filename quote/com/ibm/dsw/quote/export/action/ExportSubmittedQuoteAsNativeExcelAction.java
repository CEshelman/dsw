package com.ibm.dsw.quote.export.action;

import java.io.ByteArrayOutputStream;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.export.config.ExportQuoteParamKeys;
import com.ibm.dsw.quote.export.config.ExportQuoteStateKeys;
import com.ibm.dsw.quote.export.contract.ExportContract;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;
import com.ibm.dsw.quote.export.process.ExportQuoteProcess;
import com.ibm.dsw.quote.export.process.ExportQuoteProcessFactory;
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
 * This <code>ExportSubmittedQuoteAsNativeExcelAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 24, 2010
 */

public class ExportSubmittedQuoteAsNativeExcelAction extends ExportQuoteAction {

    /* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
	 */
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
        handler.addObject(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_ISSUMITTED, "true");
		return super.executeBiz(contract, handler);
	}
	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.export.action.ExportQuoteAction#export(com.ibm.dsw.quote.export.contract.ExportContract, java.io.ByteArrayOutputStream)
	 */
	protected String export(ExportContract exportContract,
			ByteArrayOutputStream bos) throws ExportQuoteException,
			QuoteException {
		QuoteUserSession quoteUserSession = exportContract.getQuoteUserSession();
		//Export draft/renewal quote by creatorID and submitted quote by webquoteNum
		String webQuoteNum = exportContract.getWebQuoteNum();
		ExportQuoteProcess eqProcess = ExportQuoteProcessFactory.sigleton().create();
		String webQuoteNumOnExcel = eqProcess.exportSubmittedQuoteAsNativeExcel(bos, webQuoteNum, quoteUserSession, exportContract.getUser());
		return webQuoteNumOnExcel;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.export.action.ExportQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
	 */
	protected String getState(ProcessContract contract) {
        return ExportQuoteStateKeys.STATE_EXPORT_QUOTE_NATIVE_EXCEL;
	}

}
