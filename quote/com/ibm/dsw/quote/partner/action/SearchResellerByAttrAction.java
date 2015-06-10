package com.ibm.dsw.quote.partner.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
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
 * The <code>SearchResellerByAttrAction</code> class is to search resellers by
 * attributes and display the result.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public class SearchResellerByAttrAction extends PartnerSearchAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.action.PartnerSearchAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        
        SearchPartnerContract spCtrct = (SearchPartnerContract) contract;
        if ("true".equalsIgnoreCase(spCtrct.getIsSubmittedQuote()))
            return PartnerStateKeys.STATE_DISPLAY_SBMTD_QT_RSEL_SEARCH_RESULT;
        else
            return PartnerStateKeys.STATE_DISPLAY_RESELLER_SEARCH_RESULT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.action.PartnerSearchAction#executePartnerSearch(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected SearchResultList findPartner(SearchPartnerContract contract) throws QuoteException {
        SearchPartnerContract c = (SearchPartnerContract) contract;
        
        if(!c.getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
            
        return PartnerProcessFactory.singleton().create().findResellers(c.getLobCode(), c.getCustCnt(), c.getName(),
                c.getCountry(), c.getState(), c.getTierType(), Integer.parseInt(c.getPageIndex()), c.getWebQuoteNum(),Integer.parseInt(c.getFct2PAMigrtnFlag()));
    
        }else{
            return PartnerProcessFactory.singleton().create().findResellers(c.getLobCode(), c.getCustCnt(), c.getName(),
                    c.getCountry(), c.getState(), c.getTierType(), Integer.parseInt(c.getPageIndex()), c.getMigrationReqNum(),Integer.parseInt(c.getFct2PAMigrtnFlag()));
        
        }
        }

    protected String getValidationForm() {
        return PartnerViewKeys.SEARCH_RESELLER_BY_ATTR_FORM;
    }
}
