package com.ibm.dsw.quote.draftquote.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.draftquote.contract.OvrrdTranLevelCodeContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>OvrrdTranLevelCodeAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Aug 7, 2007
 */

public class OvrrdTranLevelCodeAction extends PostPartPriceTabAction {
    
    protected void innerPostPartPriceTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        super.innerPostPartPriceTab(contract, handler);
        OvrrdTranLevelCodeContract ovrrdTranLevelCodeContract = (OvrrdTranLevelCodeContract) contract;
        
        try {
            TransactionContextManager.singleton().begin();
            QuoteHeader quoteHeader = QuoteHeaderFactory.singleton().findByCreatorID(ovrrdTranLevelCodeContract.getUserId());
            if (!StringUtils.trimToEmpty(quoteHeader.getOvrrdTranLevelCode()).equals(
                    ovrrdTranLevelCodeContract.getOvrrdTranLevelCode())) {
                quoteHeader.setRecalcPrcFlag(1);
            }
            quoteHeader.setOvrrdTranLevelCode(ovrrdTranLevelCodeContract.getOvrrdTranLevelCode());
            PartPriceProcessFactory.singleton().create().updateQuoteHeader(quoteHeader, ovrrdTranLevelCodeContract.getUserId());

            TransactionContextManager.singleton().commit();
        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            try {
                TransactionContextManager.singleton().rollback();
            } catch (TopazException te) {
                logContext.error(this, te, "problems raised when doing rollback ");
            }
        }
    }
}
