package com.ibm.dsw.quote.export.action;

import java.io.ByteArrayOutputStream;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.export.contract.ExportContract;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;
import com.ibm.dsw.quote.export.process.ExportQuoteProcess;
import com.ibm.dsw.quote.export.process.ExportQuoteProcessFactory;

public class ExportUnderEvalQuoteAsSpreadSheetAction extends ExportQuoteAsSpreadSheetAction{
	protected String export(ExportContract exportContract,
			ByteArrayOutputStream bos) throws ExportQuoteException, QuoteException {
		QuoteUserSession quoteUserSession = exportContract.getQuoteUserSession();
		
		exportContract.setXMLSpreadsheetDownload(true);
		ExportQuoteProcess eqProcess = ExportQuoteProcessFactory.sigleton().create();
        String exportedQuoteNum = eqProcess.exportUnderEvalQuoteAsSpreadSheet(bos, exportContract.getUser(), quoteUserSession, exportContract.getWebQuoteNum());
		return exportedQuoteNum;
	}
}
