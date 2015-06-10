package com.ibm.dsw.quote.customer.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.customer.contract.CustomerSearchContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class ApplianceAddressSearchDswIdAction extends CustomerSearchDSWIDAction {

	private static final long serialVersionUID = -6689123620656914918L;
	
    protected Object getEObject(ProcessContract contract) throws QuoteException {
        CustomerSearchContract csContract = (CustomerSearchContract) contract;

        int startPos = getStartPos(csContract);
        String country = getCountry(csContract);
        String searchLob = getSearchLob(csContract);
        String dswIcnRcdID = StringUtils.trimToEmpty(csContract.getSiteNumber() );
        String ctrctNum = StringUtils.trimToEmpty(csContract.getAgreementNumber() );
        
        boolean searchPayer = false;
        String progMigrtnCode = csContract.getProgMigrtnCode();
        String audienceCode = csContract.getQuoteUserSession().getAudienceCode();
        String siteNumber = csContract.getQuoteUserSession().getSiteNumber();

        CustomerSearchResultList resultList = null;
        try {
        	CustomerProcess process = CustomerProcessFactory.singleton().create();
            resultList = process.searchCustomerByDswIcnRcdID(ctrctNum, dswIcnRcdID, 
            		country, searchLob, startPos, searchPayer, progMigrtnCode, 
            		audienceCode, siteNumber);
        } catch (TopazException e) {
        	LogContextFactory.singleton().getLogContext().error(this, e.getMessage() );
            throw new QuoteException("error executing topaz process", e);
        }
        return resultList;
    }
    

}
