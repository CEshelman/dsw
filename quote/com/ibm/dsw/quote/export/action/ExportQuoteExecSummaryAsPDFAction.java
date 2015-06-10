/*
 * Created on Jun 10, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.export.action;

import java.io.ByteArrayOutputStream;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.export.config.ExportQuoteParamKeys;
import com.ibm.dsw.quote.export.config.ExportQuoteStateKeys;
import com.ibm.dsw.quote.export.contract.ExportContract;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;
import com.ibm.dsw.quote.export.process.ExportQuoteProcess;
import com.ibm.dsw.quote.export.process.ExportQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportQuoteExecSummaryAsPDFAction extends ExportQuoteAction {

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.export.action.ExportQuoteAction#export(com.ibm.dsw.quote.export.contract.ExportContract, java.io.ByteArrayOutputStream)
	 */
	protected String export(ExportContract exportContract,
			ByteArrayOutputStream bos) throws ExportQuoteException,
			QuoteException {
		ExportQuoteProcess eqProcess = ExportQuoteProcessFactory.sigleton().create();
		QuoteUserSession quoteUserSession = exportContract.getQuoteUserSession();
        String up2ReportingUserIds = quoteUserSession == null ? "" : quoteUserSession.getUp2ReportingUserIds();
		String exportedQuoteNum = eqProcess.exportQuoteExecSummaryAsPDF(bos, exportContract.getWebQuoteNum(), up2ReportingUserIds, exportContract.getUserId(), exportContract.getUser(), exportContract.getLocale(), exportContract.getTimeZone());
		return exportedQuoteNum;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.export.action.ExportQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
	 */
	protected String getState(ProcessContract contract) {
		return ExportQuoteStateKeys.STATE_EXPORT_QUOTE_EXEC_SUMMARY_AS_PDF;
	}
	
    protected void addExtraParams(ResultHandler handler){
        handler.addObject(ExportQuoteParamKeys.PARAM_EXEC_SUMMARY_DOWNLOAD_FILE_EXT, QuoteConstants.FileExtension.PDF);
    }

}
