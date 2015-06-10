package com.ibm.dsw.quote.partner.action;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerMessageKeys;
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
 * The <code>SearchResellerByNumAction</code> class is to search resellers and
 * display the result.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public class SearchResellerByNumAction extends PartnerSearchAction {

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
        int tierType = 0;
        try {
            tierType = Integer.parseInt(c.getSearchTierType());
        }
        catch (Exception e) {
            tierType = 0;
        }
        
        if(!c.getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
        
	        return PartnerProcessFactory.singleton().create().findResellers(c.getLobCode(), c.getCustCnt(), c.getNum(),
	                tierType, Integer.parseInt(c.getPageIndex()), c.getWebQuoteNum(), c.getQuoteUserSession().getAudienceCode(),Integer.parseInt(c.getFct2PAMigrtnFlag()));
	     }else{
        	   return PartnerProcessFactory.singleton().create().findResellers(c.getLobCode(), c.getCustCnt(), c.getNum(),
                       tierType, Integer.parseInt(c.getPageIndex()), c.getMigrationReqNum(), c.getQuoteUserSession().getAudienceCode(),Integer.parseInt(c.getFct2PAMigrtnFlag()));
              
        }
        
        }

    protected String getValidationForm() {
        return PartnerViewKeys.SEARCH_RESELLER_BY_NUM_FORM;
    }
    
    public String getNotFoundMessage(ProcessContract contract) {
        SearchPartnerContract spCtrct = (SearchPartnerContract) contract;
        if (spCtrct.getHasCtrldPartAsBoolean())
            return getI18NString(PartnerMessageKeys.MSG_ERR_PARTNER_NOT_AUTH, I18NBundleNames.BASE_MESSAGES, spCtrct
                    .getLocale());
        else
            return getI18NString(PartnerMessageKeys.MSG_ERR_NO_PARTNER, I18NBundleNames.ERROR_MESSAGE, spCtrct
                    .getLocale());
    }
    

    protected boolean validate(ProcessContract contract) {

      SearchPartnerContract c = (SearchPartnerContract) contract;
      String siteNum = c.getNum();

      if (siteNum !=null ) {
    	  c.setNum(siteNum.trim());
      }
      
        return super.validate(contract);
        
    }

}
