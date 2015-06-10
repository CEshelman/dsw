package com.ibm.dsw.quote.common.service;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.DocumentStatus;
import DswSalesLibrary.QuoteStatusChange;
import DswSalesLibrary.QuoteStatusChangeInput;

import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteStatusChangeService<code> class.
 *    
 * @author: lthalla@us.ibm.com
 * 
 * Creation date: May 08, 2007
 */
public class QuoteStatusChangeService extends QuoteBaseServiceHelper {

	public static final String SAP_SUCCESS_RET_CODE = "001";

	//public static final String SAP_OPPNUM_SUCCESS_CODE = "013";

	public void execute(QuoteHeader quoteHeader) throws WebServiceException {
		//		build the input structure for calling the service
		ArrayList docStatList = new ArrayList();

		if (quoteHeader.getReqstIbmCustNumFlag() == 1) {
			this.addDocumentStatus(docStatList, SQ_STAT_ICN_REQUESTED, true);
		}
		if (quoteHeader.getReqstPreCreditCheckFlag() == 1) {
			this.addDocumentStatus(docStatList, SQ_STAT_PRECREDIT_REQUESTED, true);
		}

		DocumentStatus[] docStats = (DocumentStatus[]) docStatList.toArray(new DocumentStatus[0]);

		QuoteStatusChangeInput qsInput = getQscInput(quoteHeader.getSapIntrmdiatDocNum(), quoteHeader.getSapQuoteNum(),
                docStats);
        makeWSSAPCall(qsInput);
	}
	

	public void execute4Cancel(Quote quote) throws WebServiceException, TopazException {
	    
		DocumentStatus docStats = null;				
		ArrayList docStatList = new ArrayList();
		
		QuoteHeader quoteHeader = quote.getQuoteHeader();;
		
		if (quoteHeader.isSalesQuote()){		    
		    
		    if (quoteHeader.isCopied4ReslChangeFlag()) {
		        // if it is a derived quote copied from an approved bid
		        // activate status E0007, E0009, inactivate status E0006
		        
		        this.addDocumentStatus(docStatList, SQ_STAT_QT_TERMINATED, true);
		        this.addDocumentStatus(docStatList, SQ_STAT_SPECIAL_BID_CANCELLED, true);
		        this.addDocumentStatus(docStatList, SQ_STAT_SPECIAL_BID_APPROVED, false);
		    }
		    else {
				//Set active quote status
		        this.addDocumentStatus(docStatList, SQ_STAT_QT_TERMINATED, true);
		        
				if (quoteHeader.getSpeclBidFlag() == 1) {	
				    //Set active quote status
			        this.addDocumentStatus(docStatList, SQ_STAT_SPECIAL_BID_CANCELLED, true);
			        
					//Set inactive quote status
			        if (quote.containsSapPrimaryStatus(SQ_STAT_SPECIAL_BID_REQUESTED))
			            this.addDocumentStatus(docStatList, SQ_STAT_SPECIAL_BID_REQUESTED, false);
			        else if (quote.containsSapPrimaryStatus(SQ_STAT_SPECIAL_BID_APPROVED))
			            this.addDocumentStatus(docStatList, SQ_STAT_SPECIAL_BID_APPROVED, false);
				}
		    }
		} else if(quoteHeader.isRenewalQuote()){
		    //Set active quote status
	        this.addDocumentStatus(docStatList, RQ_STAT_SPECIAL_BID_CANCELLED, true);
	        
            this.addDocumentStatus(docStatList, RQ_STAT_SPECIAL_BID_APPROVED, false);
            this.addDocumentStatus(docStatList, RQ_STAT_SPECIAL_BID_REQUESTED, false);
            this.addDocumentStatus(docStatList, RQ_STAT_SPECIAL_BID_EXPIRED, false);

            this.persistWebStatusChange(quoteHeader, docStatList);
		}
		
		makeWSSAPCall(getQscInput(quoteHeader.getSapIntrmdiatDocNum(), quoteHeader.getSapQuoteNum(),
                (DocumentStatus[]) docStatList.toArray(new DocumentStatus[0])));
	}
	
	public void execute4RemoveStatus(QuoteHeader quoteHeader, String statusCodeTBR) throws WebServiceException, TopazException {
	    
		DocumentStatus docStats = null;				
		ArrayList docStatList = new ArrayList();
		
        this.addDocumentStatus(docStatList, statusCodeTBR, false);
		
		makeWSSAPCall(getQscInput(quoteHeader.getSapIntrmdiatDocNum(), quoteHeader.getSapQuoteNum(),
                (DocumentStatus[]) docStatList.toArray(new DocumentStatus[0])));
	}

	private QuoteStatusChangeInput getQscInput(String idocNum, String quoteNum,
			DocumentStatus[] docStats) {
		QuoteStatusChangeInput qscInput = new QuoteStatusChangeInput();
		qscInput.setIdocNumber(idocNum);
		if (quoteNum != null && !quoteNum.equals(""))
			qscInput.setQuoteNumber(quoteNum);
		qscInput.setStatuses(docStats);
		logContext.debug(this,
				"Printing QuoteStatusChange service input values----");
		logContext.debug(this, "Quote Number---" + quoteNum);
		logContext.debug(this, "IDoc Number---" + idocNum);
		for (int i = 0; i < docStats.length; i++) {
			logContext.debug(this, "Status to set----"
					+ docStats[i].getStatusCode());
			logContext.debug(this, "Active Flag----"
					+ docStats[i].isActiveFlag());
		}
		logContext.debug(this, "End initializing quote status change service");

		return qscInput;
	}

	private void makeWSSAPCall(QuoteStatusChangeInput quoteStatusChangeInput) throws WebServiceException {

        try {
            ServiceLocator sLoc = new ServiceLocator();
            QuoteStatusChange port = (QuoteStatusChange) sLoc.getServicePort(
                    CommonServiceConstants.QUOTE_STATUS_CHANGE_BINDING, QuoteStatusChange.class);

            DswSalesLibrary.IdocResponse[] responseArray = port.execute(quoteStatusChangeInput);

            //loop thru the table and check return codes
            if (responseArray != null) {
                for (int i = 0; i < responseArray.length; i++) {
                    DswSalesLibrary.IdocResponse idocResponse = responseArray[i];

                    logContext.debug(this, "Printing SAP return values----");
                    logContext.debug(this, "Web Ref=" + idocResponse.getWebRefenceNumber());
                    logContext.debug(this, "DocNum=" + idocResponse.getIdocNumber());
                    logContext.debug(this, "Error Flag=" + idocResponse.getErrorFlag());
                    logContext.debug(this, "Return Code=" + idocResponse.getReturnCode());
                    logContext.debug(this, "Return Message=" + idocResponse.getReturnMessage());

                    if (idocResponse.getErrorFlag().booleanValue()) {
                        logContext.error(this, "Return Message=" + idocResponse.getReturnMessage());
                        throw new WebServiceException("Return Message=" + idocResponse.getReturnMessage());
                    }
                }
            }
            else {
                logContext.debug(this, "No SAP output object returned.");
            }
        } catch (RemoteException e) {
            logContext.error(this, "SAP : Quote status change service " + e.toString()
                    + getQuoteNumInfo(quoteStatusChangeInput));
            throw new WebServiceException("The quote status change service is unavailable now."
                    + getQuoteNumInfo(quoteStatusChangeInput), e);
        } catch (ServiceLocatorException e) {
            logContext.error(this, "SAP : Quote status change service " + e.toString()
                    + getQuoteNumInfo(quoteStatusChangeInput));
            throw new WebServiceException("The quote status change service is unavailable now."
                    + getQuoteNumInfo(quoteStatusChangeInput), e);
        }
    }
	
	protected String getQuoteNumInfo(QuoteStatusChangeInput qscInput) {
        if (qscInput == null)
            return "";
        
        if (StringUtils.isNotBlank(qscInput.getQuoteNumber()))
            return " (SAP quote num: " + qscInput.getQuoteNumber() + ")";
        else if (StringUtils.isNotBlank(qscInput.getIdocNumber()))
            return " (SAP IDOC num: " + qscInput.getIdocNumber() + ")";
        else
            return "";
    }

}
