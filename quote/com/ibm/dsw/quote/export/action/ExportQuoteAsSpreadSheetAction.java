package com.ibm.dsw.quote.export.action;

import java.io.ByteArrayOutputStream;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.export.config.ExportQuoteStateKeys;
import com.ibm.dsw.quote.export.contract.ExportContract;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;
import com.ibm.dsw.quote.export.process.ExportQuoteProcess;
import com.ibm.dsw.quote.export.process.ExportQuoteProcessFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ExportQuoteAsSpreadSheetAction</code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-11-19
 */
public class ExportQuoteAsSpreadSheetAction extends ExportQuoteAction {

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.export.action.ExportQuoteAction#export(com.ibm.dsw.quote.export.contract.ExportContract, java.io.ByteArrayOutputStream)
	 */
	protected String export(ExportContract exportContract,
			ByteArrayOutputStream bos) throws ExportQuoteException, QuoteException {
		QuoteUserSession quoteUserSession = exportContract.getQuoteUserSession();
		
		exportContract.setXMLSpreadsheetDownload(true);
		ExportQuoteProcess eqProcess = ExportQuoteProcessFactory.sigleton().create();
        String exportedQuoteNum = eqProcess.exportQuoteAsSpreadSheet(bos, exportContract.getUser(), quoteUserSession);
		return exportedQuoteNum;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.export.action.ExportQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
	 */
	protected String getState(ProcessContract contract) {
        return ExportQuoteStateKeys.STATE_EXPORT_QUOTE;
	}
}
