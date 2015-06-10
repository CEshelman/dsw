package com.ibm.dsw.quote.draftquote.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfigurationFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.contract.AddEntirePartsContract;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;


/**
 * AddEntirePartsAction.java
 *
 * <p>
 * Copyright 2013 by IBM Corporation All rights reserved.
 * </p>
 * 
 * @author <a href="lchlcd@cn.ibm.com">Jovo</a> <br/>
 * May 30, 2013
 */
public class AddEntirePartsAction extends BaseContractActionHandler {

	LogContext logContext = LogContextFactory.singleton().getLogContext();
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
    ResultBeanException {
    	
    	AddEntirePartsContract addEntirpartsContract = (AddEntirePartsContract) contract;
    	String webQuoteNum = addEntirpartsContract.getWebQuoteNum();
        String configurationId = addEntirpartsContract.getConfigurationId();
        String caNumber = addEntirpartsContract.getChargeAgreementNum();
        
        PartPriceProcess process;
        try {
            process = PartPriceProcessFactory.singleton().create();
            
            process.addEntirParts(webQuoteNum, caNumber,configurationId, "0");
            
            loadQuote(addEntirpartsContract);
            TransactionContextManager.singleton().begin();
            process.postPartPriceInfo(addEntirpartsContract);
            TransactionContextManager.singleton().commit();
        } catch (TopazException e) {
            logContext.fatal(this, e.getMessage());
            throw new QuoteException(e);
        }finally {
            try {
                TransactionContextManager.singleton().rollback();
            } catch (TopazException te) {
                logContext.error(this, te, "problems raised when doing rollback ");
            }
        }
		if (addEntirpartsContract.isHttpGETRequest()) {
		    return this.handleInvalidHttpRequest(contract, handler);
		}
		
		handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		
		
		
		return handler.getResultBean();
	
	}
    
    private void loadQuote(PostPartPriceTabContract ct) throws QuoteException {

    	long start = System.currentTimeMillis();

        String creatorId = ct.getUserId();

        QuoteProcess process = QuoteProcessFactory.singleton().create();

        try {
            Quote quote = process.getDraftQuoteBaseInfo(creatorId);
            QuoteHeader header = quote.getQuoteHeader();
            if (StringUtils.isNotBlank(header.getRselCustNum())) {
                logContext.debug(this, "To retrieve Reseller by number: " + header.getRselCustNum());
                Partner reseller = PartnerFactory.singleton().findPartnerByNum(header.getRselCustNum(), header.getLob().getCode());
                quote.setReseller(reseller);
            }

            getLineItems(quote);
            List masterLineItems = CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(quote.getLineItemList(), true);
            List SaaSLineItems = CommonServiceUtil.getSaaSLineItemList(quote.getLineItemList());
            List confgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(quote.getQuoteHeader().getWebQuoteNum());
            
            quote.setMasterSoftwareLineItems(masterLineItems);
            quote.setSaaSLineItems(SaaSLineItems);
            quote.setPartsPricingConfigrtnsList(confgrtnList);
            QuoteCommonUtil.buildSaaSLineItemsWithRampUp(quote.getSaaSLineItems());
    		quote.setMasterSaaSLineItems(CommonServiceUtil.getMasterSaaSLineItemList(quote.getSaaSLineItems()));
            quote.setPartsPricingConfigrtnsMap(QuoteCommonUtil.getPartsPricingConfigurations(quote.getMasterSaaSLineItems(), confgrtnList));
            
            quote.getQuoteHeader().setHasRampUpPartFlag(CommonServiceUtil.getHasRampUpPartFlag(quote.getMasterSaaSLineItems()));
            
            //Appliance
            List applncMains = CommonServiceUtil.getApplncMainPart(quote.getLineItemList());
            List applncUpgradeParts = CommonServiceUtil.getApplncUpgradePart(quote.getLineItemList());
            quote.setApplncMains(applncMains);
            quote.setApplncUpgradeParts(applncUpgradeParts);
            
            //14.2 GD associate maintenance parts to license parts
            GDPartsUtil.checkLicAndMaitAssociation(quote);
            ct.setQuote(quote);

        } catch (NoDataException e) {
            logContext.error(this, "Get draft qutoe base info error: " + e.getMessage());
        } catch (TopazException e) {
            logContext.error(this, "Get draft qutoe base info error: " + e.getMessage());
        }

        logContext.debug(this, "PartPriceUIValidator.loadQuote time dump : "
        		                + (System.currentTimeMillis() - start));
    }
    
    private void getLineItems(Quote quote) throws TopazException {
        try {

            List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                    quote.getQuoteHeader().getWebQuoteNum());

            quote.setLineItemList(lineItemList);

        } catch (TopazException te) {
            throw te;
        } catch (Exception ex) {
            throw new TopazException("An unexpected error occurred while getting line items. Cause: " + ex);
        }
    }

}
