package com.ibm.dsw.quote.retrieval.process;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.AuditHistoryFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.EmptyPersister;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.SalesQuoteValidationRule;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.util.date.DateCalculator;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.util.sort.PartSortUtil;
import com.ibm.dsw.quote.retrieval.RetrieveQuoteResultCodes;
import com.ibm.dsw.quote.retrieval.config.RetrieveQuoteConstant;
import com.ibm.dsw.quote.retrieval.exception.RetrieveQuoteException;
import com.ibm.dsw.quote.retrieval.util.QuoteRetrievalPriceCalculator;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteProcess_Impl</code> class is the default
 * implementation for RetrieveQuoteProcess.
 * 
 * @author: <a href="tom_boulet@us.ibm.com">Tom Boulet</a>
 * 
 * Creation date: 2007-05-08
 */
public abstract class RetrieveQuoteProcess_Impl extends TopazTransactionalProcess implements RetrieveQuoteProcess {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    public Quote retrieveSalesQuote(String sapQuoteNum, String sapQuoteIDoc, String webQuoteNum, String fulfillment, String docType, String userID, boolean external) throws RetrieveQuoteException, QuoteException {
        Quote quote = null;
        Map validationResults;
        logContext.debug(this, "Doc type" + docType);
        logContext.debug(this, "about to call validate sales quote in ret quote process jdbc");
        validationResults = validateSalesQuote(sapQuoteNum, sapQuoteIDoc, webQuoteNum, fulfillment, userID, external);
    
        int spReturnStatus = ((Integer) validationResults.get("poGenStatus")).intValue();
              
        if (spReturnStatus == 0)
            spReturnStatus = 1000;
        switch (spReturnStatus) {
        case RetrieveQuoteResultCodes.SUCCESS:
            webQuoteNum = (String) validationResults.get("webQuoteNum");
            quote = getQuote(webQuoteNum, docType, external, userID);
            
            //eligFirmOrdExcptn
            
            quote.setEligFirmOrdExcptn( validationResults.get("eligFirmOrdExcptn") == null ? null : Integer.valueOf(validationResults.get("eligFirmOrdExcptn").toString()));
            
            //check if appliance parts involved
            boolean applPartFlag = false;
            List lineItems = quote.getLineItemList();
            if (lineItems != null) {
            	for(Iterator it = lineItems.iterator(); it.hasNext(); ) {
            		QuoteLineItem item = (QuoteLineItem) it.next();
            		if(item.isApplncPart()) {
            			applPartFlag = true;
            			break;
            		}
            	}
            }
            logContext.debug(this, "applPartFlag=" + applPartFlag);
            //add ApplianceLineItemAddrDetail to quote if this quote has appliance part
            if(applPartFlag){
            	ApplianceLineItemAddrDetail addr = getApplianceLineItemAddrDetail(webQuoteNum, quote);
            	quote.setApplianceLineItemAddrDetail(addr);
            }
            logContext.debug(this, "begin validate rule");
            SalesQuoteValidationRule rule = new SalesQuoteValidationRule(null, quote, null);
            
            //check if customer's contract number match with that in quote
            //when user create a new additioal site customer he need to input one contract number
            //SAP will return a contract number after customer get created
            //here we will check if the two contract numbers match
            if(!rule.verifyAgrmntNumForNewCtrct()){
                spReturnStatus = RetrieveQuoteResultCodes.CUSTOMER_CONTRACT_NUMBER_MISMATCH;
                logContext.debug(this, "1015 error,customer's contract number does not match");
                throw new RetrieveQuoteException(spReturnStatus);
            }
            
            //check if contract's SVP level not matching with that in quote
            if(!rule.verifyTransSVPLevelForNewCtrct()){
                spReturnStatus = RetrieveQuoteResultCodes.CUSTOMER_NOT_ENROLLED;
                logContext.debug(this, "1014 error, contract's SVP level not matching with that in quote.");
                throw new RetrieveQuoteException(spReturnStatus);
            }
            
            if(!isAllPartsHavePrice(quote.getLineItemList())){
                spReturnStatus = RetrieveQuoteResultCodes.PRICING_NOT_DETERMINED;
                logContext.debug(this, "1009 error, quote pricing can not be determined ");
                throw new RetrieveQuoteException(spReturnStatus);
            }

            break;
        default:
            throw (new RetrieveQuoteException(spReturnStatus));
        }     
        
    
        return quote;
    }
    
    protected abstract Map validateSalesQuote(String sapQuoteNum, String sapQuoteIDoc, String webQuoteNum, String fulfillment, String userID, boolean external) throws QuoteException ;
    
    
    protected boolean isAllPartsHavePrice(List lineItems){
        for(Iterator it = lineItems.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();
            
            if(!QuoteCommonUtil.isPartHasPrice(qli)){
                return false;
            }
        }
        
        return true;
    }
    
    protected Quote retriveQuoteFromDb(String webQuoteNum) throws QuoteException{
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        try {
            return process.getDraftQuoteDetailsForQuoteRetrieval(webQuoteNum);
        } catch (NoDataException nde) {
            throw new RetrieveQuoteException(nde, RetrieveQuoteResultCodes.QUOTE_NOT_FOUND);
        }
    }
    
    protected ApplianceLineItemAddrDetail getApplianceLineItemAddrDetail(String webQuoteNum, Quote quote) throws QuoteException{   	
        try 
        {
        	ApplianceLineItemAddrDetail ads = null;
        	CustomerProcess process = CustomerProcessFactory.singleton().create();
        	logContext.debug(this, "quote.getQuoteHeader().isHasAppliancePartFlag()=" + quote.getQuoteHeader().isHasAppliancePartFlag());
        	if ( quote.getQuoteHeader().isHasAppliancePartFlag() )
        	{
        		ads = process.findLineItemAddr(webQuoteNum,"", quote.getLineItemList(), quote.getQuoteHeader().isDisShipInstAdrFlag(), quote.getCustomer());
        	}
        	else
        	{
        		ads = new ApplianceLineItemAddrDetail();
        	}
        	
        	return ads;
        } catch (TopazException e) {
            throw new QuoteException("error executing topaz process", e);
        }
    }

    protected Quote getQuote(String webQuoteNum, String docType, boolean external, String userID) throws QuoteException {
        Quote quote = null;
    
        try {
            this.beginTransaction();
    
            /*
             * the following is done just like loading a quote load header, get
             * line items, get customer, distributor, reseller, status
             */
            quote = this.retriveQuoteFromDb(webQuoteNum);
            
            PartSortUtil.sortByDestSeqNumber(quote.getLineItemList());
            
            if (quote.getQuoteHeader().getSpeclBidFlag() == 0) {
                
                setEmtpyPersister(quote);    
                boolean pwsResult = calculatePrice(quote, docType, external);
                
            } else {
                // for FCT quote, adjust date but don't save to db2, only write the audit log 
                // for others, do nothing
                // For PA/PAE , also adjust date  per ebiz ticket SDIN-7E5ND7
                //if (quote.getQuoteHeader().isFCTQuote()) {
                setEmtpyPersister(quote);
                adjustDateAndWriteAuditHistory(quote, userID);
                //}
            }
    
            this.commitTransaction();
        } catch (TopazException te) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(te));
            throw new QuoteException(te);
        } finally {
            this.rollbackTransaction();
        }
    
        return quote;
    }

    private void setEmtpyPersister(Quote quote) {
    
        Persister emptyPersister = new EmptyPersister();
        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            item.setPersister(emptyPersister);
        }
    
    }

    protected void adjustDateAndWriteAuditHistory(Quote quote, String userID) throws TopazException {
        // Andy : the code has been moved to
        // QuoteRetrievalPriceCalculator.calculateDate() method
        DateCalculator calculator = DateCalculator.create(quote);
    
        calculator.calculateDate();
    
        Iterator iter = quote.getLineItemList().iterator();
        while (iter.hasNext()) {
            QuoteLineItem item = (QuoteLineItem) iter.next();
            // user override the date in draft quote
            if (item.getStartDtOvrrdFlg() || item.getEndDtOvrrdFlg()) {
                continue;
            }
    
            if (!DateUtil.isYMDEqual(item.getMaintStartDate(), item.getPartDispAttr().getStdStartDate())) {
                AuditHistoryFactory.singleton().createAuditHistory(quote.getQuoteHeader().getWebQuoteNum(),
                        new Integer(item.getSeqNum()), // line item seq numbr
                        userID, RetrieveQuoteConstant.CHG_LI_ST_DT_QRWS, // user action
                        DateUtil.formatDate(item.getMaintStartDate()), // old value
                        DateUtil.formatDate(item.getPartDispAttr().getStdStartDate()) // new value
                        );
            }
            if (!DateUtil.isYMDEqual(item.getMaintEndDate(), item.getPartDispAttr().getStdEndDate())) {
                AuditHistoryFactory.singleton().createAuditHistory(quote.getQuoteHeader().getWebQuoteNum(),
                        new Integer(item.getSeqNum()), // line item seq numbr
                        userID, RetrieveQuoteConstant.CHG_LI_END_DT_QRWS, // user action
                        DateUtil.formatDate(item.getMaintEndDate()), // old value
                        DateUtil.formatDate(item.getPartDispAttr().getStdEndDate()) // new value
                        );
            }
        }
    
        calculator.setLineItemDates();
    
    }

    /**
     * @param quote
     */
    protected boolean calculatePrice(Quote quote, String docType, boolean external) throws TopazException, RetrieveQuoteException {
    
        QuoteRetrievalPriceCalculator pc = null;
    
        if (RetrieveQuoteConstant.DOCTYPE_ORDER.equals(docType)) {
            pc = new QuoteRetrievalPriceCalculator(quote, QuoteConstants.ORDER_DOC_CAT, external);
    
        } else {
            pc = new QuoteRetrievalPriceCalculator(quote, QuoteConstants.QUOTE_DOC_CAT, external);
    
        }
        pc.calculatePrice();

        return true;
    
    }
    //protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     * Invokes the PricingService to check prices
     * @param quote the <code>Quote</code> to be evaluated
     * @return true if successfuly
     */
    /*
     *  removed by Andy 2007-6-11, this method is never used, and it refences a obsolete 
     * class com.ibm.dsw.quote.common.util.PricingServerHelper
     public boolean priceCheck(Quote quote) {
     try{
     PricingServiceHelper helper = new PricingServiceHelper( );
     helper.updateQuoteWithPriceInfo( quote );
     return true;
     }catch( Exception exception ){
     logContext.error( "Failed to perform price check.", exception );
     return false;
     }
     }
     */

    /**
     * @param quote
     */
    


}
