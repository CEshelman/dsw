package com.ibm.dsw.quote.draftquote.action;


import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ApplyPriceBandOvrrdAction<code> class.
 *    
 * @author: xiaogy@cn.ibm.com
 * 
 * Creation date: 2009-8-17
 */

public class ApplyPriceBandOvrrdAction extends PostPartPriceTabAction {

    protected void innerPostPartPriceTab(ProcessContract contract,
            ResultHandler handler) throws QuoteException {
        PostPartPriceTabContract ct = (PostPartPriceTabContract)contract;
        QuoteHeader header = ct.getQuote().getQuoteHeader();
        if(!(header.isSalesQuote() && (header.isPAQuote() || header.isOEMQuote()) &&
                StringUtils.isNotBlank(header.getOvrrdTranLevelCode()))){
            return;
        }
        if (ct.getItems().values().size() == 0) {
            logContext.debug(this, "no parts in the quote, no need to perform the post");
            return;
        }

        PartPriceProcess process;
        process = PartPriceProcessFactory.singleton().create();

        process.applyPrcBandOvrrd(ct);
    }
}
