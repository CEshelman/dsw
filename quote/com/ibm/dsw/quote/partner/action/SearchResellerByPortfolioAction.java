package com.ibm.dsw.quote.partner.action;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.partner.config.PartnerStateKeys;
import com.ibm.dsw.quote.partner.config.PartnerViewKeys;
import com.ibm.dsw.quote.partner.contract.SearchPartnerContract;
import com.ibm.dsw.quote.partner.process.PartnerProcessFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SearchResellerByPortfolioAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2008-2-28
 */

public class SearchResellerByPortfolioAction extends PartnerSearchAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.partner.action.PartnerSearchAction#getState()
     */
    protected String getState(ProcessContract contract) {
        
        SearchPartnerContract spCtrct = (SearchPartnerContract) contract;
        if ("true".equalsIgnoreCase(spCtrct.getIsSubmittedQuote()))
            return PartnerStateKeys.STATE_DISPLAY_SBMTD_QT_RSEL_SEARCH_RESULT;
        else
            return PartnerStateKeys.STATE_DISPLAY_RESELLER_SEARCH_RESULT;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.partner.action.PartnerSearchAction#findPartner(com.ibm.dsw.quote.partner.contract.SearchPartnerContract)
     */
    protected SearchResultList findPartner(SearchPartnerContract contract) throws QuoteException {
        String portfolios = contract.getAuthorizedPort();
        int multipleProdFlag = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(contract.getChkMultipleProd()) ? 1 : 0;
        return PartnerProcessFactory.singleton().create().findResellersByPortfolio(portfolios, contract.getLobCode(),
                contract.getCountry(), Integer.parseInt(contract.getPageIndex()), multipleProdFlag);
    }
    
    protected String getValidationForm() {
        return PartnerViewKeys.SEARCH_RESELLER_BY_PORTFOLIO;
    
    }

}
