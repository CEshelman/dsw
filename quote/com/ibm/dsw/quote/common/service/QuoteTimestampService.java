package com.ibm.dsw.quote.common.service;

import java.rmi.RemoteException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.QuoteTimestamp;
import DswSalesLibrary.QuoteTimestampInput;
import DswSalesLibrary.QuoteTimestampOutput;

import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteTimestampService<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 9, 2007
 */

public class QuoteTimestampService {

    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    public QuoteTimestampService() {
    }

    public boolean execute(String rnwlQuoteNum, String sapIDocNum, Date rqModDate, Date rqStatModDate) throws RemoteException{
        boolean sapConsistent = false;
        QuoteTimestampOutput output = callRenewalQuoteLastModRfc(rnwlQuoteNum, sapIDocNum);
        logContext.debug(this, "ReturnCode: " + output.getReturnCode());
        logContext.debug(this, "ReturnMessage: " + output.getReturnMessage());
        logContext.debug(this, "IdocProcessingStatus: " + output.getIdocProcessingStatus());
        logContext.debug(this, "SAP-rqModDate: " + output.getQuoteTimestamp());
        logContext.debug(this, "DB2-rqModDate: " + rqModDate);
        logContext.debug(this, "SAP-rqStatModDate: " + output.getQuoteStatusTimestamp());
        logContext.debug(this, "DB2-rqStatModDate: " + rqStatModDate);
        // Compare the quote info from DB and SAP
        if (!output.getErrorFlag().booleanValue()) {
            if (StringUtils.isNotEmpty(sapIDocNum) && !("0").equals(sapIDocNum)) {
                //Last IDOC has not been processed yet
                if (!CommonServiceConstants.SAPIDOC_C_CODE.equals(output.getIdocProcessingStatus())
                        && !CommonServiceConstants.SAPIDOC_T_CODE.equals(output.getIdocProcessingStatus())) {
                    logContext.debug(this, "Last IDOC has not been processed yet");
                    sapConsistent = false;
                    return sapConsistent;
                }
            }
            //compare the quote doc and status mod timestamps
            Date sapRqModDate = DateHelper.getDatefromString(output.getQuoteTimestamp());
            Date sapRqStatModDate = DateHelper.getDatefromString(output.getQuoteStatusTimestamp());
            if (rqModDate != null && rqModDate.compareTo(sapRqModDate) == 0) {
                if (rqStatModDate != null && rqStatModDate.compareTo(sapRqStatModDate) == 0) {
                    logContext.debug(this, "Last IDOC has been processed, and date comparison succeed");
                    sapConsistent = true; 
                }
            }
        } else {
            logContext.debug(this, "SAP call has error");
            sapConsistent = false; 
        }
        return sapConsistent;
    }
    private QuoteTimestampOutput callRenewalQuoteLastModRfc(String sRenQuoteNum, String sIdocNum) throws RemoteException {
        QuoteTimestampOutput output = new QuoteTimestampOutput();
        if (null == sRenQuoteNum) {
            throw new IllegalStateException("a null arg was passed to callRenewalQuoteLastModRfc");
        }

        logContext.debug(this, "callRenewalQuoteLastModRfc() start -- setting RenewalQuote [DOCNUMBER] number to "
                + sRenQuoteNum);

        try {
            ServiceLocator serviceLocator = new ServiceLocator();
            QuoteTimestamp quoteTimestamp = (QuoteTimestamp) serviceLocator.getServicePort(CommonServiceConstants.QUOTE_TIMESTAMP_BINDING, QuoteTimestamp.class);
            QuoteTimestampInput quoteTimestampInput = new QuoteTimestampInput();
            quoteTimestampInput.setQuoteNumber(sRenQuoteNum);
            if (null != sIdocNum && !sIdocNum.equals(""))
                quoteTimestampInput.setQuoteIdocNumber(sIdocNum);
            output = quoteTimestamp.execute(quoteTimestampInput);
            logContext.debug(this, "execute(QuoteTimestamp,QuoteTimestampInput): Finished QuoteTimestamp interaction execution");
        } catch (ServiceLocatorException e) {
            logContext.error(this, e, "execute(QuoteTimestamp,QuoteTimestampInput): Failed QuoteTimestamp interaction execution");
            output.setReturnCode("99");
        }
        return output;
    }
}
