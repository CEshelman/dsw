package com.ibm.dsw.quote.export.process;

import is.domainx.User;

import java.io.OutputStream;
import java.util.Locale;
import java.util.TimeZone;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ExportQuoteProces.java</code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-11
 */

public interface ExportQuoteProcess {
    
   
    /**
     * @param os
     * @param user
     * @return
     * @throws ExportQuoteException
     * @throws QuoteException
     */
    public String exportQuoteAsSpreadSheet(OutputStream os, User user, QuoteUserSession quoteUserSession) throws ExportQuoteException,QuoteException ;
    
    public String exportUnderEvalQuoteAsSpreadSheet(OutputStream os, User user, QuoteUserSession quoteUserSession, String webQuoteNum) throws ExportQuoteException,QuoteException ;
	
	/**
	 * @param os
	 * @param webQuoteNum
	 * @param quoteUserSession
	 * @param isTier2Reseller 
	 * @param isPGSFlag 
	 * @param PGSFlag 
	 * @param downloadPricingType 
	 * @param user
	 * @return
	 * @throws ExportQuoteException
	 * @throws QuoteException
	 */
    
	public String exportQuoteAsRTF(OutputStream os, String webQuoteNum, QuoteUserSession quoteUserSession, Boolean isPGSFlag, Boolean isTier2Reseller, String downloadPricingType, User user, String userId) throws ExportQuoteException ,QuoteException;
	public String exportSubmittedQuoteAsSpreadSheet(OutputStream os, String webQuoteNum, String quoteUserSession, User user) throws ExportQuoteException ,QuoteException;
	public String exportQuoteExecSummaryAsRTF(OutputStream os, String webQuoteNum, String up2ReportingUserIds, String userId, User user, Locale locale, TimeZone timezone) throws ExportQuoteException, QuoteException;
	public String exportQuoteExecSummaryAsPDF(OutputStream os, String webQuoteNum, String up2ReportingUserIds, String userId, User user, Locale locale, TimeZone timezone) throws ExportQuoteException, QuoteException;
	
	public String exportQuoteAsNativeExcel(OutputStream os, User user) throws ExportQuoteException,QuoteException ;
	public String exportUnderEvalQuoteAsNativeExcel(OutputStream os, User user, QuoteUserSession quoteUserSession, String webQuoteNum) throws ExportQuoteException,QuoteException ;
	public String exportSubmittedQuoteAsNativeExcel(OutputStream os, String webQuoteNum, QuoteUserSession quoteUserSession, User user) throws ExportQuoteException ,QuoteException;
	
}
