package com.ibm.dsw.quote.draftquote.action;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteViewKeys;
import com.ibm.dsw.quote.draftquote.contract.PostCustPrtnrTabContract;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PostCustPrtnrTabActionA<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 13, 2007
 */

public class PostCustPrtnrTabAction extends PostDraftQuoteBaseAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.action.PostDraftQuoteBaseAction#postDraftQuoteTab(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        PostCustPrtnrTabContract postCustPrtnrTabContract = (PostCustPrtnrTabContract) contract;
        String quoteType = postCustPrtnrTabContract.getQuoteType();
        String creatorId = postCustPrtnrTabContract.getUserId();
        Date expireDate = postCustPrtnrTabContract.getExpireDate();
        String fulfillmentSrc = postCustPrtnrTabContract.getFullfillmentSrc();
        String quoteClassfctnCode = postCustPrtnrTabContract.getQuoteClassfctnCode();
        Date startDate = postCustPrtnrTabContract.getStartDate();
        String oemAgrmntType = postCustPrtnrTabContract.getOemAgrmntType();
        int oemBidType = postCustPrtnrTabContract.getOemBidType();       
        String sspType =postCustPrtnrTabContract.getSspType();
        int pymTermsDays = postCustPrtnrTabContract.getPymntTermsDays();
        Date estmtdOrdDate = postCustPrtnrTabContract.getEstmtdOrdDate();
        Date custReqstdArrivlDate = postCustPrtnrTabContract.getCustReqstdArrivlDate();
        String installAtOption = postCustPrtnrTabContract.getInstallAtOption();
        String shipToOption = postCustPrtnrTabContract.getShipToOption();
        
        
        if (QuoteConstants.QUOTE_TYPE_RENEWAL.equalsIgnoreCase(quoteType)) {
            String partnerAccess = postCustPrtnrTabContract.getPartnerAccess();            
            QuoteProcess process = QuoteProcessFactory.singleton().create();
            // the reseller tbd flag & distributor tbd flag of renewal quote are always 0.
            process.updateQuoteHeaderCustPrtnrTab(creatorId, expireDate, fulfillmentSrc, partnerAccess, 0, 0,
                    quoteClassfctnCode, startDate, oemAgrmntType, pymTermsDays, -1, estmtdOrdDate, custReqstdArrivlDate,sspType);
        } 
        else if (QuoteConstants.QUOTE_TYPE_SALES.equalsIgnoreCase(quoteType)) {
            int resellerToBeDetermined = QuoteConstants.CHECKBOX_CHECKED.equals(postCustPrtnrTabContract
                    .getResellerToBeDetermined()) ? 1 : 0;
            int distributorToBeDetermined = 0;
            if ("1".equals(postCustPrtnrTabContract.getDistributorToBeDetermined()))
                distributorToBeDetermined = 1;
            else if ("2".equals(postCustPrtnrTabContract.getDistributorToBeDetermined()))
                distributorToBeDetermined = 2;

            QuoteProcess process = QuoteProcessFactory.singleton().create();
            process.updateQuoteHeaderCustPrtnrTab(creatorId, expireDate, fulfillmentSrc, null, resellerToBeDetermined,
                    distributorToBeDetermined, quoteClassfctnCode, startDate, oemAgrmntType, pymTermsDays, oemBidType, estmtdOrdDate, custReqstdArrivlDate,sspType);

            // if all the fields are empty, then don't update them
            if (StringUtils.isEmpty(postCustPrtnrTabContract.getCntFirstName())
                    && StringUtils.isEmpty(postCustPrtnrTabContract.getCntLastName())
                    && StringUtils.isEmpty(postCustPrtnrTabContract.getCntFaxNumFull())
                    && StringUtils.isEmpty(postCustPrtnrTabContract.getCntPhoneNumFull())
                    && StringUtils.isEmpty(postCustPrtnrTabContract.getCntEmailAdr())) {
                logContext.debug(this,
                        "cntFirstName, cntLastName, cntFaxNumFull, cntPhoneNumFull and cntEmailAdr are empty.");
            } else {
                // to update the quote contact
                process.updateQuoteContact(postCustPrtnrTabContract);
            }
        }
        
        /**
         * update customer install at option status.
         */
		QuoteHeader qtHeader = this.getQuoteHeader(postCustPrtnrTabContract);
		if (qtHeader.isDisShipInstAdrFlag()) {
			try {
				String webQuotNum = qtHeader.getWebQuoteNum();
				int installAt = Integer.parseInt(installAtOption);
				int shipTo = Integer.parseInt(shipToOption);
				boolean flag = CustomerProcessFactory.singleton().create().updateCustInstallAtOpt(webQuotNum, installAt, shipTo);
				if (!flag)
					logContext.info(this, "Update customer install option failed.");
			} catch (TopazException e) {
				logContext.error(this, e.getMessage());
				throw new QuoteException(
						"error executing customer process for updateCustInstallAtOpt ",
						e);
			}
		}
    }
    
    private QuoteHeader getQuoteHeader(PostCustPrtnrTabContract contract) throws QuoteException {
        
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        QuoteHeader qtHeader = null;
        
        try {
            qtHeader = process.getQuoteHdrInfo(contract.getUserId());
        } catch (NoDataException nde) {
            throw new QuoteException("Quote header is not found for the login user " + contract.getUserId());
        }
        
        return qtHeader;
    }
    
    protected String getValidationForm() {
        return DraftQuoteViewKeys.POST_CUST_PARTNER_FORM;
    }
}
