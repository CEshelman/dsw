package com.ibm.dsw.quote.export.action;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
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
 * This <code>ExportQuoteAsRTFAction</code> class.
 * 
 * @author:
 * 
 * Creation date: 2007-4-19
 */
public class ExportQuoteAsRTFAction extends ExportQuoteAction {

	

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.export.action.ExportQuoteAction#export(com.ibm.dsw.quote.export.contract.ExportContract, java.io.ByteArrayOutputStream)
	 */
	protected String export(ExportContract exportContract, ByteArrayOutputStream bos) throws ExportQuoteException, QuoteException {
		QuoteUserSession quoteUserSession = exportContract.getQuoteUserSession();
		//Export pricing type for PGS
		String downloadPricingType = exportContract.getDownloadPricingType();
		Boolean isTier2Reseller = QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO.equalsIgnoreCase(quoteUserSession.getBpTierModel());
		Boolean isPGSFlag = QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode());
		
		//for RTF Download
		exportContract.setRTFDownload(true);
		
		//String up2ReportingUserIds = quoteUserSession == null ? null	: quoteUserSession.getUp2ReportingUserIds();
		//Export draft/renewal quote by creatorID and submitted quote by webquoteNum
		String webQuoteNum = exportContract.isSubmittedQuote()? exportContract.getWebQuoteNum():StringUtils.EMPTY;
		ExportQuoteProcess eqProcess = ExportQuoteProcessFactory.sigleton().create();
		String webQuoteNumOnRTF = eqProcess.exportQuoteAsRTF(bos, webQuoteNum, quoteUserSession, isPGSFlag, isTier2Reseller, downloadPricingType, exportContract.getUser(), exportContract.getUserId());
		return webQuoteNumOnRTF;
	}
	
	protected String getState(ProcessContract contract) {
		return ExportQuoteStateKeys.STATE_QUOTE_RTF_DOWNLOAD;
	}
}
