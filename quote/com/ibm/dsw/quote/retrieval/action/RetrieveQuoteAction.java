package com.ibm.dsw.quote.retrieval.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.CountrySignatureRuleFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuickEnrollmentServiceHelper;
import com.ibm.dsw.quote.retrieval.RetrieveQuoteResultCodes;
import com.ibm.dsw.quote.retrieval.config.RetrieveQuoteConstant;
import com.ibm.dsw.quote.retrieval.contract.RetrieveQuoteContract;
import com.ibm.dsw.quote.retrieval.exception.RetrieveQuoteException;
import com.ibm.dsw.quote.retrieval.process.RetrieveQuoteProcess;
import com.ibm.dsw.quote.retrieval.process.RetrieveQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteAction</code> class is to retrieve sales quote for
 * quote retrieval service.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 25, 2007
 */
public class RetrieveQuoteAction extends BaseContractActionHandler implements RetrieveQuoteConstant,
        RetrieveQuoteResultCodes {
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        RetrieveQuoteContract qtContract = (RetrieveQuoteContract) contract;
        handler.setState(getState(qtContract));

        Quote quote = null;

        try {
        			
        	logContext.info(this, "Retrieve quote [sapQuoteNum=" + qtContract.getSapQuoteNum() + "; sapQuoteIDoc="
                            + qtContract.getSapQuoteIDoc() + "; webQuoteNum=" + qtContract.getWebQuoteNum()
                            + "; fulfillment=" + qtContract.getFulfillment() + "; userID=" + qtContract.getUserID()
                            + "; userFlag=" + qtContract.getUserFlag() + "]");
            validateInputParams(qtContract);
            

            RetrieveQuoteProcess retrieveQTProcess = RetrieveQuoteProcessFactory.singleton().create();
            quote = retrieveQTProcess.retrieveSalesQuote(qtContract.getSapQuoteNum(), qtContract.getSapQuoteIDoc(),
                    qtContract.getWebQuoteNum(), qtContract.getFulfillment(), qtContract.getDocType(), qtContract
                            .getUserID(), qtContract.getUserFlag().booleanValue());

            handler.addObject(RetrieveQuoteConstant.E_QUOTE, quote);
            handler.addObject(RetrieveQuoteConstant.E_RESULT, new Integer(RetrieveQuoteResultCodes.SUCCESS));

			//ANDY: fix empty contract number when BP order quote in eorder
			//PL: 	AMMY-9TNL89 
			try {
				if (null != quote) {
					// Quick enrollment CSRA customer that no contract num
					if (quote.getQuoteHeader().isCSRAQuote()
							&& (quote.getQuoteHeader().hasNewCustomer() || quote.getQuoteHeader().getWebCtrctId() > 0)
							&& StringUtils.isEmpty(quote.getQuoteHeader().getContractNum())) {
						this.quickEnrollment(quote);
					}
				}
			} catch (Exception e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
	            handler.addObject(RetrieveQuoteConstant.E_RESULT, new Integer(RetrieveQuoteResultCodes.CUSTOMER_ENROLL_FAILD));
			} 
			
        } catch (RetrieveQuoteException rqe) {
            /*
             * this means validation failed
             */
            logContext.error(this, "ret quote exception in ret quote action handler");
            //logContext.debug(this, LogThrowableUtil.getStackTraceContent(rqe));
            handler.addObject(RetrieveQuoteConstant.E_RESULT, new Integer(rqe.getResultCode()));
        } catch (QuoteException qe) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(qe));
            handler.addObject(RetrieveQuoteConstant.E_RESULT, new Integer(RetrieveQuoteResultCodes.UNKNOWN_ERROR_CODE));
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            handler.addObject(RetrieveQuoteConstant.E_RESULT, new Integer(RetrieveQuoteResultCodes.UNKNOWN_ERROR_CODE));
        }

        return handler.getResultBean();
    }

    /**
     * To validate the input parameters
     * 
     * @param qtContract
     * @return true if the input is valid
     * @throws RetrieveQuoteException
     *             if error encounters.
     */
    private boolean validateInputParams(RetrieveQuoteContract qtContract) throws RetrieveQuoteException {
        boolean noInput = (qtContract.getSapQuoteNum() == null || qtContract.getSapQuoteNum().trim().length() == 0)
                && (qtContract.getSapQuoteIDoc() == null || qtContract.getSapQuoteIDoc().trim().length() == 0)
                && (qtContract.getWebQuoteNum() == null || qtContract.getWebQuoteNum().trim().length() == 0);

        boolean validInput = !noInput
                && (null != qtContract.getUserID() && qtContract.getUserID().trim().length() > 0 && null != qtContract
                        .getUserFlag());
        validInput = validInput
                && (null != qtContract.getFulfillment()
                        && (RetrieveQuoteConstant.FULFILLMENT_DIRECT.equals(qtContract.getFulfillment()) || RetrieveQuoteConstant.FULFILLMENT_CHANNEL
                                .equals(qtContract.getFulfillment())) && null != qtContract.getDocType() && (RetrieveQuoteConstant.DOCTYPE_QUOTE
                        .equals(qtContract.getDocType()) || RetrieveQuoteConstant.DOCTYPE_ORDER.equals(qtContract
                        .getDocType())));

        if (!validInput) {
            throw new RetrieveQuoteException(RetrieveQuoteResultCodes.REQUEST_NOT_VALID);
        }

        return validInput;
    }

    protected String getState(RetrieveQuoteContract qtContract) {
        return STATE_RETRIEVE_QUOTE;
    }
    
	/**
	 * To call SAP RFC to enrollment this new customer in SAP
	 * 
	 * @param quote
	 * @throws QuoteException
	 * @throws WebServiceFailureException
	 */
	private void quickEnrollment(Quote quote) throws QuoteException, WebServiceFailureException {
		logContext.debug(this, "To call SAP RFC to enrollment this new customer in SAP:..." + quote.getCustomer().getCustName());
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		String enrllmtNum = quoteProcess.getWebEnrollmentNum();
		boolean isRequireSignature = CountrySignatureRuleFactory.singleton().isRequireSignature(
				quote.getCustomer().getCountryCode());
		try {
			QuickEnrollmentServiceHelper service = new QuickEnrollmentServiceHelper();
			boolean isSuccessfull = service.callQuickEnrollmentService(quote, isRequireSignature, enrllmtNum);
			if (isSuccessfull) {
				quoteProcess.updateSapCtrctNum(quote.getQuoteHeader().getWebQuoteNum(), quote.getQuoteHeader().getContractNum());
			}
		} catch (WebServiceException e) {
			throw new WebServiceFailureException("Failed to call SAP to quick enroll the new custoemr.",
					MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, e);
		}
	}
}
