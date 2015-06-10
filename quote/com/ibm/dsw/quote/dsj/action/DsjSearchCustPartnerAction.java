package com.ibm.dsw.quote.dsj.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.dsj.contract.DsjBaseContract;
import com.ibm.dsw.quote.newquote.config.NewQuoteStateKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.dsw.quote.partner.exception.PartnerNotFoundException;
import com.ibm.dsw.quote.partner.process.PartnerProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class DsjSearchCustPartnerAction extends DsjBaseAction {
	
	private static final long serialVersionUID = -1518140773713638721L;
	private static final String VALIDE_RETURN_CODE = "0";
	private static final String NOT_PARTNER_FOUND_RETURN_CODE = "1";

    public ResultBean executeBiz(DsjBaseContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
	
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
    	
        SearchResultList srl = null;
        
        try {
        	if (contract.getResellerQueryName() == null || "".equals(contract.getResellerQueryName())) {
        		handler.addObject( PartnerParamKeys.DSJ_PARTNER_SEARCH_RETURN_CODE, NOT_PARTNER_FOUND_RETURN_CODE );
        		logContext.debug(this, "Partner search name is null!");
        	} else {
        		srl = findPartner(contract);
        		if (srl != null && srl.getRealSize() == 1) {
        			handler.addObject( PartnerParamKeys.DSJ_PARTNER_SEARCH_LIST, srl );
        			handler.addObject( PartnerParamKeys.DSJ_PARTNER_SEARCH_RETURN_CODE, VALIDE_RETURN_CODE );
        		} else {
        			handler.addObject( PartnerParamKeys.DSJ_PARTNER_SEARCH_RETURN_CODE, NOT_PARTNER_FOUND_RETURN_CODE );
        		}
        	}
        } catch (PartnerNotFoundException nofe) {
        	logContext.debug(this, nofe.getMessage());
        }
    	
    	handler.setState( NewQuoteStateKeys.DSJ_SEARCH_PARTNER_RESULT_JSON );
    	
    	return handler.getResultBean();
    }
    
    private SearchResultList findPartner(DsjBaseContract contract) throws QuoteException {
    	
    	return PartnerProcessFactory.singleton().create().findResellers(contract.getLob(), contract.getCountry(), contract.getResellerQueryName(),
                contract.getCountry(), "", 0, Integer.parseInt("1"), contract.getWebQuoteNumber(), Integer.parseInt("0"));
    }
}
