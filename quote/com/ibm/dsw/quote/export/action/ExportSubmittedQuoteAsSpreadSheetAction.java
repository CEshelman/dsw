/*
 * Created on Feb 27, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportSubmittedQuoteAsSpreadSheetAction extends ExportQuoteAction {
	
	
	
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
		String up2ReportingUserIds = quoteUserSession == null ? null	: quoteUserSession.getUp2ReportingUserIds();
		//Export draft/renewal quote by creatorID and submitted quote by webquoteNum
		String webQuoteNum = exportContract.getWebQuoteNum();
		ExportQuoteProcess eqProcess = ExportQuoteProcessFactory.sigleton().create();
		String webQuoteNumOnExcel = eqProcess.exportSubmittedQuoteAsSpreadSheet(bos, webQuoteNum, up2ReportingUserIds, exportContract.getUser());
		return webQuoteNumOnExcel;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.export.action.ExportQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
	 */
	protected String getState(ProcessContract contract) {
        return ExportQuoteStateKeys.STATE_EXPORT_QUOTE;
	}

}
