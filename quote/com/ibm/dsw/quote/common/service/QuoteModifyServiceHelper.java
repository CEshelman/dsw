package com.ibm.dsw.quote.common.service;

import is.domainx.User;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.Delegate;
import DswSalesLibrary.DocumentStatus;
import DswSalesLibrary.EndDateOverrideFlag;
import DswSalesLibrary.LineItemProcessor;
import DswSalesLibrary.ModificationType;
import DswSalesLibrary.NoItemPriceOnOutputFlag;
import DswSalesLibrary.NoRnwlReminderEmailsFlag;
import DswSalesLibrary.NoTaxOnQuoteOutputFlag;
import DswSalesLibrary.PaoBpAccess;
import DswSalesLibrary.PartnerAddress;
import DswSalesLibrary.PartnerFunction;
import DswSalesLibrary.QuoteModify;
import DswSalesLibrary.QuoteModifyHeader;
import DswSalesLibrary.QuoteModifyInput;
import DswSalesLibrary.QuoteModifyLineItem;
import DswSalesLibrary.QuoteModifyOutput;
import DswSalesLibrary.QuoteModifySalesComment;
import DswSalesLibrary.QuoteTerminationInfo;
import DswSalesLibrary.SendOutputToAdditionalEmailFlag;
import DswSalesLibrary.SendOutputToPrimaryContactFlag;
import DswSalesLibrary.SendOutputToRenewalContactFlag;
import DswSalesLibrary.SpecialBidApprover;
import DswSalesLibrary.SpecialBidHeader;
import DswSalesLibrary.StartDateOverrideFlag;
import DswSalesLibrary.TriggerPartnerEmailFlag;
import DswSalesLibrary.TriggerY9EmailFlag;
import DswSalesLibrary.UpsideTransactionIndFlag;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.InvalidWSInputException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.base.exception.WebServiceUnavailableException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfigurationFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemConfig;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.TacticCode;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.quote.submittedquote.contract.ExtendQuoteExpDateContract;
import com.ibm.dsw.quote.submittedquote.contract.QtDateContract;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteModifyServiceHelper<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-25
 */

public class QuoteModifyServiceHelper extends QuoteBaseServiceHelper {

    /**
     * 
     */
    public QuoteModifyServiceHelper() {
        super();
    }
    
    /**
     * Calling SAP QuoteModify RFC for sales quote 
     * @param qtHeader
     * @param expContract
     * @return true is successfully. false is no need to updated.
     * @throws WebServiceException
     */
    public boolean modifySQDateByService(QuoteHeader qtHeader, ExtendQuoteExpDateContract expContract) throws WebServiceException {
    	QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();
        boolean isNeedUpdate = setUpdatedFlag(qtHeader, expContract, qmHeader, qmInput);
        
        if(!isNeedUpdate) return false;
        
        if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        else
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());

        logContext.debug(this, "Calling modifySQExpirationDate.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());
        
        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
        
        return isNeedUpdate;
        
    }
    
    /**
     * Set the flag for quote modify web service if any field need to be updated
     * @param qtHeader
     * @param expContract
     * @param qmHeader
     * @param qmInput
     * @return: true, need to update by RFC.
     * @throws WebServiceException
     */
	private boolean setUpdatedFlag(QuoteHeader qtHeader, ExtendQuoteExpDateContract expContract, QuoteModifyHeader qmHeader,
			QuoteModifyInput qmInput)throws WebServiceException {
		boolean isNeedUpdate = false;
        Date expDate = expContract.getExpDate();
        if (expDate!=null && !expDate.equals(qtHeader.getQuoteExpDate())){
	    	qmHeader.setValidToDate(DateHelper.getDateByFormat(expDate, SAP_DATE_FORMAT));
	        logContext.debug(this, "ValidToDate = " + qmHeader.getValidToDate());
	        isNeedUpdate = true;
        }
        
        qmInput.setHeader(qmHeader);

        //14.4: Net Tcv Increase
        //    because this method will send line item data (setLineItemForCRAD will call qmInput.setLineItems() ), 
        //        it may effect the line item price, so have to 
        //        recalculate net tcv increase and send it out.
		//if(qmInput.getHeader() != null)
		//	qmInput.getHeader().setNetTcvIncrease( getTotalTcvIncrease(qtHeader) );
        //~14.4 Net Tcv Increase
		return isNeedUpdate;
	}
	
	/**
	 * set line items which need to be updated 
	 * @param qtHeader
	 * @param qmInput
	 * @param cradDate
	 * @throws InvalidWSInputException
	 * @throws WebServiceException
	 */
	private void setLineItemForCRAD(QuoteHeader qtHeader, QuoteModifyInput qmInput, Date cradDate)
			throws InvalidWSInputException, WebServiceException {
		Quote quote = this.getQuote(qtHeader);
		List<QuoteLineItem> qtLineItems = quote.getLineItemList();
		for (QuoteLineItem lineItem: qtLineItems) {
			if(null != qtHeader.getCustReqstArrivlDate() && null != lineItem.getLineItemCRAD()){
				if (lineItem.getLineItemCRAD().equals(qtHeader.getCustReqstArrivlDate()))
					lineItem.getModifiedProperty().setCrad(true);
			}
		}
		
		DswSalesLibrary.QuoteModifyLineItem[] lineItems = this.genQuoteModifyLineItems(quote);
		if(lineItems!=null && lineItems.length>0){
		    ArrayList qmLineItems = new ArrayList();
			for (int i = 0; i < lineItems.length; i++) {
				QuoteModifyLineItem qmfli = lineItems[i];
				qmfli.setRequestedArrivalDate(DateHelper.getDateByFormat(cradDate, SAP_DATE_FORMAT));
				qmLineItems.add(qmfli);
			}
			qmInput.setLineItems((QuoteModifyLineItem[]) qmLineItems.toArray(new QuoteModifyLineItem[0]));
		}
	}
	
	/**
	 * Get current quote by quote header
	 * @param qtHeader
	 * @return current quote including quote header and quote line
	 * @throws WebServiceException: when no quote line found.
	 */
	private Quote getQuote(QuoteHeader qtHeader)throws WebServiceException{
		Quote quote = new Quote(qtHeader);
		List lineItemList = null;
		try {
			lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
			        quote.getQuoteHeader().getWebQuoteNum());
		} catch (TopazException e) {
			throw new WebServiceException(e.getMessage());
		}
		quote.setLineItemList(lineItemList);
		return quote;
	}
    
    public void modifySQExpiratrionDate(QuoteHeader qtHeader) throws WebServiceException {
        
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();

        if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        else
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        
        qmHeader.setValidToDate(DateHelper.getDateByFormat(qtHeader.getQuoteExpDate(), SAP_DATE_FORMAT));
        qmInput.setHeader(qmHeader);

        logContext.debug(this, "Calling modifySQExpirationDate.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());
        logContext.debug(this, "ValidToDate = " + qmHeader.getValidToDate());

        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }

    public void modifySQStartDate(QuoteHeader qtHeader) throws WebServiceException {
        
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();

        if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        else
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        
        qmHeader.setValidFromDate(DateHelper.getDateByFormat(qtHeader.getQuoteStartDate(), SAP_DATE_FORMAT));
        qmInput.setHeader(qmHeader);

        logContext.debug(this, "Calling modifySQExpirationDate.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());
        logContext.debug(this, "ValidFromDate = " + qmHeader.getValidFromDate());

        QuoteModifyOutput output = executeQuoteModify(qmInput, qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }

    
    public void modifySQShipInstallAddress(QuoteHeader qtHeader,int handleInt, ApplianceLineItemAddrDetail applianceLineItemAddrDetail) throws WebServiceException {
        
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();

        if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        else
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        
        qmInput.setPartnerFunctions((PartnerFunction[]) setShipInstallAddress(handleInt,qtHeader,applianceLineItemAddrDetail).toArray(new PartnerFunction[0]));
        qmInput.setPartnerAddresses((PartnerAddress[]) setContactAddress(handleInt, qtHeader,applianceLineItemAddrDetail).toArray(new PartnerAddress[0]));
        qmInput.setHeader(qmHeader);

        logContext.debug(this, "Calling modifySQShipInstallAddress.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());
        logContext.debug(this, "ShipInstallAddress = " + qmInput.toString());

        QuoteModifyOutput output = executeQuoteModify(qmInput, qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }
    
    public boolean modifyRQSpecBidExpiratrionDate(QuoteHeader qtHeader, QuoteUserSession salesRep, QtDateContract expContract) throws WebServiceException {
        if(salesRep == null){
            throw new WebServiceException(
                    "QuoteUserSession is null, quote modify service can not be called successfully at this time."
                            + getWebQuoteNumInfo(qtHeader));
        }
        
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();
        boolean isNeedUpdate = this.setUpdatedFlag(qtHeader, expContract, qmHeader, qmInput);
        if(!isNeedUpdate) return false;
        
        qmHeader.setQuoteNumber(qtHeader.getRenwlQuoteNum());
        qmHeader.setLastModDateTime(CommonServiceUtil.getModDate(qtHeader.getRqModDate(), qtHeader.getRqStatModDate()));
        qmHeader.setModifiedBySerialNum(salesRep.getSerialNumber());
        qmHeader.setModifiedByCountryCode(salesRep.getCountryCode3());
        qmHeader.setPaoBpAccess(PaoBpAccess.fromString(qtHeader.getRnwlPrtnrAccessFlag() == 1 ? "Y" : "N"));
        if (expContract.getExpDate()!=null && !expContract.getExpDate().equals(qtHeader.getQuoteExpDate())){
        	qmHeader.setSpbdExpDate(DateHelper.getDateByFormat(expContract.getExpDate(), SAP_DATE_FORMAT));
        } else{
        	qmHeader.setSpbdExpDate(DateHelper.getDateByFormat(qtHeader.getQuoteExpDate(), SAP_DATE_FORMAT));
        }
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        
        qmInput.setHeader(qmHeader);
        
        logContext.debug(this, "Calling modifyRQExpirationDate.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "LastModDateTime = " + qmHeader.getLastModDateTime());
        logContext.debug(this, "ModifiedBySerialNum = " + qmHeader.getModifiedBySerialNum());
        logContext.debug(this, "ModifiedByCountryCode = " + qmHeader.getModifiedByCountryCode());
        logContext.debug(this, "PaoBpAccess = " + qmHeader.getPaoBpAccess());
        logContext.debug(this, "SpbdExpDate = " + qmHeader.getSpbdExpDate());

        QuoteModifyOutput output = executeQuoteModify(qmInput, qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
        return isNeedUpdate;
    }
    
    public void modifySQExempCodeAndOppNum(QuoteHeader qtHeader) throws WebServiceException {

        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();

        if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        else
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        
        qmHeader.setExemptionCode(qtHeader.getExemptnCode());
        qmHeader.setOpportunityNumber(qtHeader.getOpprtntyNum());
        
        qmInput.setHeader(qmHeader);
        
        logContext.debug(this, "Calling modifySQExempCodeAndOppNum.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());
        logContext.debug(this, "ExemptionCode = " + qmHeader.getExemptionCode());
        logContext.debug(this, "OpportunityNumber = " + qmHeader.getOpportunityNumber());

        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }
    
    public void modifySubmittedQuoteLineItemChange(Quote quote) throws WebServiceException {
    	QuoteHeader qtHeader = quote.getQuoteHeader();
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();

        if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        else
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
       
        qmInput.setHeader(qmHeader);
        qmInput.setLineItems(genQuoteModifyLineItems(quote));
        if ( hasMTMChange(quote.getLineItemList()) )
        {
        	logContext.debug(this, "has mtm change, begin to set partner fucntion");
        	qmInput.setPartnerFunctions((PartnerFunction[]) setShipInstallAddress(QUOTE_MODIFY_SHIPTO,qtHeader,quote.getApplianceLineItemAddrDetail()).toArray(new PartnerFunction[0]));
        }
        
        logContext.debug(this, "Calling modifySQLineItemCRAD.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());
        logContext.debug(this, "ExemptionCode = " + qmHeader.getExemptionCode());

        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());	
    }
    
    private boolean hasMTMChange(List items)
    {
    	if ( items == null )
    	{
    		return false;
    	}
    	for ( int i = 0; i < items.size(); i++ )
    	{
    		QuoteLineItem item = (QuoteLineItem)items.get(i);
    		if ( item.getModifiedProperty().isMtm() )
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    public void modifyRQExempCodeAndOppNum(QuoteHeader qtHeader, QuoteUserSession salesRep) throws WebServiceException {

        if(salesRep == null){
            throw new WebServiceException(
                    "QuoteUserSession is null, quote modify service can not be called successfully at this time."
                            + getWebQuoteNumInfo(qtHeader));
        }
        
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();

        qmHeader.setQuoteNumber(qtHeader.getRenwlQuoteNum());
        qmHeader.setLastModDateTime(CommonServiceUtil.getModDate(qtHeader.getRqModDate(), qtHeader.getRqStatModDate()));
        qmHeader.setModifiedBySerialNum(salesRep.getSerialNumber());
        qmHeader.setModifiedByCountryCode(salesRep.getCountryCode3());
        qmHeader.setPaoBpAccess(PaoBpAccess.fromString(qtHeader.getRnwlPrtnrAccessFlag() == 1 ? "Y" : "N"));
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        
        qmHeader.setExemptionCode(qtHeader.getExemptnCode());
        qmHeader.setOpportunityNumber(qtHeader.getOpprtntyNum());
        
        qmInput.setHeader(qmHeader);
        
        logContext.debug(this, "Calling modifyRQExempCodeAndOppNum.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "LastModDateTime = " + qmHeader.getLastModDateTime());
        logContext.debug(this, "ModifiedBySerialNum = " + qmHeader.getModifiedBySerialNum());
        logContext.debug(this, "ModifiedByCountryCode = " + qmHeader.getModifiedByCountryCode());
        logContext.debug(this, "PaoBpAccess = " + qmHeader.getPaoBpAccess());
        logContext.debug(this, "ExemptionCode = " + qmHeader.getExemptionCode());
        logContext.debug(this, "OpportunityNumber = " + qmHeader.getOpportunityNumber());

        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }
    
    public void callQuoteModifyService(User user, QuoteUserSession salesRep, Quote quote) throws WebServiceException,
            TopazException {
        
        QuoteModifyOutput output =  executeQuoteModify(initQuoteModifyInput(user, salesRep, quote), quote.getQuoteHeader());
        quote.getQuoteHeader().setSapIntrmdiatDocNum(output.getIdocNumber());
    }
    
    protected QuoteModifyOutput executeQuoteModify(QuoteModifyInput quoteModifyInput, QuoteHeader quoteHeader) throws WebServiceException {
        QuoteModifyOutput quoteModifyOutput = null;

        try {
            QuoteModifyHeader qmHeader = quoteModifyInput.getHeader(); 
            
            if(quoteHeader.isSalesQuote() && ButtonDisplayRuleFactory.singleton().isDisplayLAUplift(quoteHeader)){
                qmHeader.setProposalValidityDays(new Integer(CommonServiceUtil.getValidityDays(quoteHeader)));
                qmHeader.setPaymentTermDays(new Integer(quoteHeader.getPymTermsDays()));
            }
            ServiceLocator serviceLocator = new ServiceLocator();
            QuoteModify quoteModify = (QuoteModify) serviceLocator.getServicePort(
                    CommonServiceConstants.QUOTE_MODIFY_BINDING, QuoteModify.class);
            quoteModifyOutput = quoteModify.execute(quoteModifyInput);
        } catch (RemoteException e) {
            throw new WebServiceUnavailableException("The quote modify service is unavailable."
                    + getQuoteNumInfo(quoteModifyInput), e);
        } catch (ServiceLocatorException e) {
            throw new WebServiceUnavailableException("The quote modify service is unavailable."
                    + getQuoteNumInfo(quoteModifyInput), e);
        } catch (Exception e) {
            throw new WebServiceUnavailableException("The quote modify service is unavailable."
                    + getQuoteNumInfo(quoteModifyInput), e);
        }

        boolean hasError = quoteModifyOutput.getErrorFlag() == null ? false : quoteModifyOutput.getErrorFlag()
                .booleanValue();

        if (hasError)
            throw new WebServiceFailureException("Failed to call quote modify service." + getQuoteNumInfo(quoteModifyInput),
                    MessageKeys.MSG_MODIFY_QUOTE_SERVICE_ERROR);

        return quoteModifyOutput;
    }
    
    protected QuoteModifyInput initQuoteModifyInput(User user, QuoteUserSession salesRep, Quote quote)
            throws TopazException , WebServiceException {
        QuoteModifyInput qmi = new QuoteModifyInput();
        logContext.debug(this, "Generating quote modify input:");
        
        qmi.setHeader(genQuoteModifyHeader(user, salesRep, quote));
        qmi.setSpecialBidInfo(genSpecialBidHeader(quote));
        qmi.setTerminationInfo(genQuoteTerminationInfo(user, salesRep, quote));
        qmi.setSalesComment(genQuoteModifySalesComment(user, quote));
        qmi.setTacticCodes(genTacticCodes(quote));
        qmi.setPartnerFunctions(genPartnerFunctions(quote));
        qmi.setPartnerAddresses(genPartnerAddress(user, salesRep, quote));
        qmi.setStatuses(genDocumentStatuses(quote));
        qmi.setDelegates(genDelegates(quote));
        qmi.setSpecialBidApprovers(genSpecialBidApprovers(quote));
        qmi.setLineItems(genQuoteModifyLineItems(quote, true));
        qmi.setLineItemProcessors(genLineItemProcessors(quote, true));
        
        return qmi;
    }
    
    protected QuoteModifyHeader genQuoteModifyHeader(User user, QuoteUserSession salesRep, Quote quote)  throws WebServiceException {
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        
        if (quote == null || quote.getQuoteHeader() == null)
            return qmHeader;
        
        QuoteHeader qtHeader = quote.getQuoteHeader();
        if (qtHeader.isRenewalQuote() && salesRep == null) {
            throw new WebServiceException(
                    "QuoteUserSession is null, quote modify service can not be called successfully at this time."
                            + getWebQuoteNumInfo(qtHeader));
        }

        if (qtHeader.isSalesQuote())
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        else
            qmHeader.setQuoteNumber(qtHeader.getRenwlQuoteNum());
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        
        qmHeader.setOpportunityNumber(qtHeader.getOpprtntyNum());
        qmHeader.setExemptionCode(qtHeader.getExemptnCode());
        
        qmHeader.setSendOutputToPrimaryContactFlag(SendOutputToPrimaryContactFlag.fromString(qtHeader
                .getSendQuoteToPrmryCntFlag() == 1 ? "Y" : "N"));
        //New field for 9.1 release
        qmHeader.setHoldUrl(CommonServiceUtil.replaceAmps(ApplicationProperties.getInstance()
                .getHoldURL() + qtHeader.getWebQuoteNum()));
        
        qmHeader.setTriggerY9EmailFlag(TriggerY9EmailFlag.fromString(qtHeader.isSendQuoteToAddtnlPrtnrFlag() ? "Y"
                : "N"));
        qmHeader.setTriggerPartnerEmailFlag(TriggerPartnerEmailFlag.fromString(StringUtils.isNotBlank(qtHeader
                .getAddtnlPrtnrEmailAdr()) ? "Y" : "N"));
        if (StringUtils.isNotBlank(qtHeader.getAddtnlPrtnrEmailAdr()))
            qmHeader.setAdditionalEmail3(qtHeader.getAddtnlPrtnrEmailAdr());
        
        if (qtHeader.isSalesQuote() && StringUtils.isBlank(qtHeader.getSapQuoteNum())) {
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        }
        
        if (qtHeader.isSalesQuote()) {
            qmHeader.setValidToDate(DateHelper.getDateByFormat(qtHeader.getQuoteExpDate(), SAP_DATE_FORMAT));
        }
        
        if (qtHeader.isRenewalQuote()) {
            qmHeader.setLastModDateTime(CommonServiceUtil.getModDate(qtHeader.getRqModDate(), qtHeader.getRqStatModDate()));

            qmHeader.setModifiedBySerialNum(salesRep.getSerialNumber());
            qmHeader.setModifiedByCountryCode(salesRep.getCountryCode3());

            qmHeader.setSendOutputToRenewalContactFlag(SendOutputToRenewalContactFlag.fromString(qtHeader
                    .getSendQuoteToQuoteCntFlag() == 1 ? "Y" : "N"));
            
            if (CommonServiceUtil.needSendAddiMailToCreator(qtHeader)) {
                qmHeader.setSendOutputToAdditionalEmailFlag(SendOutputToAdditionalEmailFlag.fromString("Y"));
                String additnEmail = CommonServiceUtil.genAddiEmailList(qtHeader, user, salesRep);
                qmHeader.setAdditionalEmail(additnEmail) ;
            }
            else {
                qmHeader.setSendOutputToAdditionalEmailFlag(SendOutputToAdditionalEmailFlag.fromString(qtHeader
                    .getSendQuoteToAddtnlCntFlag() == 1 ? "Y" : "N"));
            
                if (qtHeader.getSendQuoteToAddtnlCntFlag() == 1)
                    qmHeader.setAdditionalEmail(qtHeader.getAddtnlCntEmailAdr());
            }
            qmHeader.setSalesOdds(qtHeader.getRenwlQuoteSalesOddsOode());
            qmHeader.setUpsideTransactionIndFlag(UpsideTransactionIndFlag.fromString(qtHeader
                    .getUpsideTrendTowardsPurch().equalsIgnoreCase("Y") ? "Y" : "N"));
            qmHeader.setNoTaxOnQuoteOutputFlag(NoTaxOnQuoteOutputFlag
                    .fromString(qtHeader.getInclTaxFinalQuoteFlag() == 0 ? "Y" : "N"));
            qmHeader.setPaoBpAccess(PaoBpAccess.fromString(qtHeader.getRnwlPrtnrAccessFlag() == 1 ? "Y" : "N"));
            
            qmHeader.setNoRnwlReminderEmailsFlag(NoRnwlReminderEmailsFlag.fromString(qtHeader.getBlockRnwlReminder() == 1 ? "Y" : "N"));
        }
        
        qmHeader.setNoItemPriceOnOutputFlag(NoItemPriceOnOutputFlag
                .fromString(qtHeader.getIncldLineItmDtlQuoteFlg() == 0 ? "Y" : "N"));
        
        
        logContext.debug(this, "Quote modify header begin:");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "OpportunityNumber = " + qmHeader.getOpportunityNumber());
        logContext.debug(this, "ExemptionCode = " + qmHeader.getExemptionCode());
        logContext.debug(this, "LastModDateTime = " + qmHeader.getLastModDateTime());
        logContext.debug(this, "ModifiedByCountryCode = " + qmHeader.getModifiedByCountryCode());
        logContext.debug(this, "AdditionalEmail = " + qmHeader.getAdditionalEmail());
        logContext.debug(this, "SalesOdds = " + qmHeader.getSalesOdds());
        logContext.debug(this, "SendOutputToRenewalContactFlag = " + qmHeader.getSendOutputToRenewalContactFlag());
        logContext.debug(this, "SendOutputToAdditionalEmailFlag = " + qmHeader.getSendOutputToAdditionalEmailFlag());
        logContext.debug(this, "UpsideTransactionIndFlag = " + qmHeader.getUpsideTransactionIndFlag());
        logContext.debug(this, "NoTaxOnQuoteOutputFlag = " + qmHeader.getNoTaxOnQuoteOutputFlag());
        logContext.debug(this, "PaoBpAccess = " + qmHeader.getPaoBpAccess());
        logContext.debug(this, "SendOutputToPrimaryContactFlag = " + qmHeader.getSendOutputToPrimaryContactFlag());
        logContext.debug(this, "HoldUrl = " + qmHeader.getHoldUrl());
        logContext.debug(this, "ValidToDate = " + qmHeader.getValidToDate());
        logContext.debug(this, "BlockRnwlReminder = " + qmHeader.getNoRnwlReminderEmailsFlag());
        return qmHeader;
    }

    protected SpecialBidHeader genSpecialBidHeader(Quote quote) {
        SpecialBidHeader spHeader = new SpecialBidHeader();
        return spHeader;
    }
    
    protected QuoteTerminationInfo genQuoteTerminationInfo(User user, QuoteUserSession salesRep, Quote quote) {
        QuoteTerminationInfo info = new QuoteTerminationInfo();
        
        if (quote == null || quote.getQuoteHeader() == null)
            return info;
        
        QuoteHeader qtHeader = quote.getQuoteHeader();
        
        info.setReasonCode(qtHeader.getRnwlTermntnReasCode());
        info.setContactEmail(salesRep == null ? user.getEmail():salesRep.getEmailAddress());
        info.setTerminationMessage(StringUtils.substring(CommonServiceUtil.replaceLineSeperator(qtHeader
                .getTermntnComments()), 0, DraftQuoteConstants.MAX_RQ_SALSE_COMM));
        
        if (salesRep != null) {
            info.setContactName(salesRep.getFullName());
            info.setPhoneNumber(salesRep.getPhoneNumber());
        }
        
        logContext.debug(this, "Quote termination info begin:");
        logContext.debug(this, "ReasonCode = " + info.getReasonCode());
        logContext.debug(this, "ContactName = " + info.getContactName());
        logContext.debug(this, "ContactEmail = " + info.getContactEmail());
        logContext.debug(this, "PhoneNumber = " + info.getPhoneNumber());
        logContext.debug(this, "TerminationMessage = " + info.getTerminationMessage());
        
        return info;
    }
    
    protected QuoteModifySalesComment genQuoteModifySalesComment(User user, Quote quote) {
        QuoteModifySalesComment comment = new QuoteModifySalesComment();
        
        if (quote == null || quote.getQuoteHeader() == null)
            return comment;
        
        QuoteHeader qtHeader = quote.getQuoteHeader();
        
        comment.setUserId(user.getUniqueID());
        comment.setText(StringUtils.substring(CommonServiceUtil.replaceLineSeperator(qtHeader.getSalesComments()), 0,
                DraftQuoteConstants.MAX_RQ_SALSE_COMM));
        
        logContext.debug(this, "Quote modify sales comment begin:");
        logContext.debug(this, "UserId = " + comment.getUserId());
        logContext.debug(this, "Text = " + comment.getText());
        
        return comment;
    }
    
    protected DswSalesLibrary.TacticCode[] genTacticCodes(Quote quote) {
        if (quote == null || quote.getQuoteHeader() == null)
            return new DswSalesLibrary.TacticCode[0];
        
        ArrayList codes = new ArrayList();
        List tcs = quote.getQuoteHeader().getTacticCodes();
        
        logContext.debug(this, "Tactic codes begin:");
        
        for (int i = 0; i < tcs.size(); i++) {
            TacticCode tc = (TacticCode) tcs.get(i);
            DswSalesLibrary.TacticCode dtc = new DswSalesLibrary.TacticCode();
            dtc.setMarketingTacticCode(tc.getTacticCode());
            codes.add(dtc);
            
            logContext.debug(this, "Tactic code " + i + ":");
            logContext.debug(this, "MarketingTacticCode = " + dtc.getMarketingTacticCode());
        }
        
        return (DswSalesLibrary.TacticCode[]) codes.toArray(new DswSalesLibrary.TacticCode[0]);
    }
    
    protected PartnerFunction[] genPartnerFunctions(Quote quote) {
        if (quote == null || quote.getQuoteHeader() == null)
            return new PartnerFunction[0];
        
        ArrayList funcList = new ArrayList();
        
        if (StringUtils.isNotBlank(quote.getQuoteHeader().getRselCustNum())) {
            PartnerFunction pf = new PartnerFunction();
            pf.setPartnerFunctionCode(CUST_PRTNR_FUNC_RSEL);
            pf.setPartnerCustomerNumber(quote.getQuoteHeader().getRselCustNum());
            funcList.add(pf);
        }
        
        if (StringUtils.isNotBlank(quote.getQuoteHeader().getPayerCustNum())) {
            PartnerFunction pf = new PartnerFunction();
            pf.setPartnerFunctionCode(CUST_PRTNR_FUNC_PAYER);
            pf.setPartnerCustomerNumber(quote.getQuoteHeader().getPayerCustNum());
            funcList.add(pf);
        }
        
        // set the sales rep info
		PartnerFunction prtnrFunctionSalesRep = new PartnerFunction();
		prtnrFunctionSalesRep.setPartnerFunctionCode(CUST_PRTNR_FUNC_SALES_REP);
		prtnrFunctionSalesRep.setPartnerCustomerNumber(String.valueOf(quote.getCustomer().getSapCntId()));
		funcList.add(prtnrFunctionSalesRep);
        
        logContext.debug(this, "Partner function begin:");
        logContext.debug(this, "RselCustNum = " + quote.getQuoteHeader().getRselCustNum());
        logContext.debug(this, "PayerCustNum = " + quote.getQuoteHeader().getPayerCustNum());
		
		return (PartnerFunction[]) funcList.toArray(new PartnerFunction[0]);
    }
    
    protected PartnerAddress[] genPartnerAddress(User user, QuoteUserSession salesRep, Quote quote) {
        if (user == null || quote == null || quote.getQuoteHeader() == null)
            return new PartnerAddress[0];
        
        ArrayList prtnrAddrList = new ArrayList();
        
        // set the sales rep address info
        PartnerAddress prtnrAddressSalesRep = new PartnerAddress();
        prtnrAddressSalesRep.setPartnerFunctionCode(CUST_PRTNR_FUNC_SALES_REP);
        prtnrAddressSalesRep.setPartnerCustomerNumber(String.valueOf(quote.getCustomer().getSapCntId()));
        
        // For all FCT RQs, send opp owner info for ZX
        QuoteHeader qhd = quote.getQuoteHeader();
        SalesRep oppOwner = quote.getOppOwner() ;
        if ( (qhd.isFCTQuote() || qhd.isFCTToPAQuote()) && oppOwner != null) {
		    prtnrAddressSalesRep.setName1(CommonServiceUtil.limitLength(oppOwner.getFirstName(), MAX_NAME_LENGTH));
		    prtnrAddressSalesRep.setName2(CommonServiceUtil.limitLength(oppOwner.getLastName(), MAX_NAME_LENGTH));
		    prtnrAddressSalesRep.setEmailAddress(CommonServiceUtil.limitLength(oppOwner.getEmailAddress(),
                    MAX_EMAIL_LENGTH));
		}
		else {
	        if (salesRep != null) {
	            prtnrAddressSalesRep.setName1(CommonServiceUtil.limitLength(salesRep.getFirstName(), MAX_NAME_LENGTH));
	            prtnrAddressSalesRep.setName2(CommonServiceUtil.limitLength(salesRep.getLastName(), MAX_NAME_LENGTH));
	            prtnrAddressSalesRep.setEmailAddress(CommonServiceUtil.limitLength(salesRep.getEmailAddress(),
	                    MAX_EMAIL_LENGTH));
	        } else {
	            prtnrAddressSalesRep.setName1(CommonServiceUtil.limitLength(user.getFirstName(), MAX_NAME_LENGTH));
	            prtnrAddressSalesRep.setName2(CommonServiceUtil.limitLength(user.getLastName(), MAX_NAME_LENGTH));
	            prtnrAddressSalesRep.setEmailAddress(CommonServiceUtil.limitLength(user.getEmail(), MAX_EMAIL_LENGTH));
	        }
		}
        prtnrAddrList.add(prtnrAddressSalesRep);

        return (PartnerAddress[]) prtnrAddrList.toArray(new PartnerAddress[0]);
    }
    
    protected Map compareStatusList(Map webStatusMap, Map sapStatusMap) {
        
        HashMap chgStatusMap = new HashMap();
        
        Iterator iter = sapStatusMap.keySet().iterator();
        while (iter.hasNext()) {
            String statusCode = (String) iter.next();
            if (!webStatusMap.containsKey(statusCode))
                chgStatusMap.put(statusCode, Boolean.FALSE);
        }
        
        iter = webStatusMap.keySet().iterator();
        while (iter.hasNext()) {
            String statusCode = (String) iter.next();
            if (!sapStatusMap.containsKey(statusCode))
                chgStatusMap.put(statusCode, Boolean.TRUE);
        }
        
        return chgStatusMap;
    }
    
    protected DocumentStatus[] genDocumentStatuses(Quote quote) throws TopazException {
        if (quote == null || quote.getQuoteHeader() == null)
            return new DocumentStatus[0];

        QuoteHeader header = quote.getQuoteHeader();
        HashMap statusMap = new HashMap();
        ArrayList dsl = new ArrayList();
        
        Map webPrimStatuses = this.getQuoteStatusCodes(quote.getAllWebPrimaryStatuses());
        Map webScndStatuses = this.getQuoteStatusCodes(quote.getAllWebSecondayStatuses());
        Map sapPrimStatuses = this.getQuoteStatusCodes(quote.getSapPrimaryStatusList());
        Map sapScndStatuses = this.getQuoteStatusCodes(quote.getSapSecondaryStatusList());
        
        // set the status info for icn request
        if (header.getReqstIbmCustNumFlag() == 1) {
            this.addDocumentStatus(dsl, RQ_STAT_ICN_REQUESTED, true);
            statusMap.put(RQ_STAT_ICN_REQUESTED, RQ_STAT_ICN_REQUESTED);
        }
        // set the status info for pre-credit check
        if (header.getReqstPreCreditCheckFlag() == 1) {
            this.addDocumentStatus(dsl, RQ_STAT_PRE_CRED_CHECK_REQUESTED, true);
            statusMap.put(RQ_STAT_PRE_CRED_CHECK_REQUESTED, RQ_STAT_PRE_CRED_CHECK_REQUESTED);
        }
        
        
        logContext.debug(this, "Document statuses begin:");
        logContext.debug(this, "ReqstIbmCustNumFlag = " + header.getReqstIbmCustNumFlag());
        logContext.debug(this, "ReqstPreCreditCheckFlag = " + header.getReqstPreCreditCheckFlag());
        
        Map primChgMap = this.compareStatusList(webPrimStatuses, sapPrimStatuses);
        
        if (!primChgMap.isEmpty() && webPrimStatuses != null) {
            Iterator iter = webPrimStatuses.keySet().iterator();
            
            while (iter.hasNext()) {
                String statusCode = (String) iter.next();
                
                if (!statusMap.containsKey(statusCode)) {
                    this.addDocumentStatus(dsl, statusCode, true);
                    statusMap.put(statusCode, statusCode);

                    logContext.debug(this, "Primary status " + statusCode + " : " + true);
                }
            }
        }

        Map scndChgMap = this.compareStatusList(webScndStatuses, sapScndStatuses);
        Iterator iter = scndChgMap.keySet().iterator();
        
        while (iter.hasNext()) {
            String status = (String) iter.next();
            boolean active = ((Boolean) scndChgMap.get(status)).booleanValue();
            
            if (!statusMap.containsKey(status)) {
                this.addDocumentStatus(dsl, status, active);
                statusMap.put(status, status);

                logContext.debug(this, "Secondary status " + status + " : " + active);
            }
        }
        
        // persist status changes to db
        this.persistWebStatusChange(quote, webPrimStatuses, webScndStatuses);
        
        return (DocumentStatus[]) dsl.toArray(new DocumentStatus[0]);
    }
    
    protected Delegate[] genDelegates(Quote quote) {
        return new Delegate[0];
    }
    
    protected SpecialBidApprover[] genSpecialBidApprovers(Quote quote) {
        return new SpecialBidApprover[0];
    }
    
    protected QuoteModifyLineItem[] genQuoteModifyLineItems(Quote quote, boolean sendModified) throws InvalidWSInputException {
    	return genQuoteModifyLineItems(quote, sendModified, null);
    }
    
    protected QuoteModifyLineItem[] genQuoteModifyLineItems(Quote quote, boolean sendModified, QuoteModifyHeader qmHeader) throws InvalidWSInputException {
        if (quote == null || quote.getQuoteHeader() == null
                || quote.getLineItemList() == null)
            return new QuoteModifyLineItem[0];
        
        if (quote.getQuoteHeader().isSalesQuote()) {
            try {
                CommonServiceUtil.updateRelatedItemSeqNum(quote);
            } catch (TopazException e) {
                logContext.error(this, "update quote line items' related sequence number fail!");
                throw new InvalidWSInputException(e);
            }
        }
        
        QuoteHeader header = quote.getQuoteHeader();
        List qtLineItems = quote.getLineItemList();
        ArrayList qmLineItems = new ArrayList();
        
        Map<String, Boolean> placeHolderFlagMap = new HashMap<String, Boolean>();
        Boolean placeHolderFlag;
        
        if (qtLineItems == null)
            return new QuoteModifyLineItem[0];
        
        logContext.debug(this, "Quote modify line items begin:");
        
        for (int i = 0; i < qtLineItems.size(); i++) {
            QuoteLineItem qtLineItem = (QuoteLineItem) qtLineItems.get(i);
            QuoteModifyLineItem qmLineItem = new QuoteModifyLineItem();
            
            String modType = CommonServiceUtil.getChangeType(qtLineItem);

            int itemNumber = 0;
            if ( qmHeader != null )
            {
            	logContext.debug(this, "header.getSapQuoteNum()=" + header.getSapQuoteNum());
            	logContext.debug(this, "qtLineItem.getDestSeqNum()=" + qtLineItem.getDestSeqNum());
            	logContext.debug(this, "qtLineItem.getSapLineItemSeqNum()=" + qtLineItem.getSapLineItemSeqNum());
            	logContext.debug(this, "header.isSalesQuote()= " + header.isSalesQuote());
            	//if itemNumber < 0, means no line item data replicated, should set quoteNum null
            	if (header.isSalesQuote()) {
	                if (StringUtils.isBlank(header.getSapQuoteNum()))
	                    itemNumber = qtLineItem.getDestSeqNum();
	                else
	                {
	                    itemNumber = qtLineItem.getSapLineItemSeqNum();
	                    if ( itemNumber < 0 )
	                    {
	                    	itemNumber = qtLineItem.getDestSeqNum();
	                    	qmHeader.setQuoteNumber(null);
	                    }
	                }
	            }
	            else {
	                itemNumber = qtLineItem.getSapLineItemSeqNum();
	                if (itemNumber < 0 )
	                {
	                    itemNumber = qtLineItem.getSeqNum();
	                    qmHeader.setQuoteNumber(null);
	                }
	            }
            }
            else
            {
	            if (header.isSalesQuote()) {
	                if (StringUtils.isBlank(header.getSapQuoteNum()))
	                    itemNumber = qtLineItem.getDestSeqNum();
	                else
	                    itemNumber = qtLineItem.getSapLineItemSeqNum();
	            }
	            else {
	                itemNumber = qtLineItem.getSapLineItemSeqNum();
	                if (itemNumber < 0)
	                    itemNumber = qtLineItem.getSeqNum();
	            }
            }
            
            if (itemNumber < 0)
                throw new InvalidWSInputException(
                        "Failed to call quote modify service due to invalid line item number: " + itemNumber
                                + getWebQuoteNumInfo(header));
            
            if(!sendModified){
                modType = PartPriceConstants.PartChangeType.SAP_PART_UPDATED;
            }
            if (sendModified && (PartPriceConstants.PartChangeType.SAP_PART_NO_CHANGES.equals(modType)
                    || StringUtils.isBlank(modType)))
                continue;
            
            int qty = CommonServiceUtil.getPartQty(qtLineItem);
            if (header.isRenewalQuote()) {
                if (header.isSubmittedQuote()) {
                    // for submitted renewal quote, only approver can update line item start date & end date
                    // so the quote must be special bid and it should be fully editable.
                    qty += qtLineItem.getOrderQty();
                }
                else {
	                boolean canEditRQ = quote.getQuoteAccess() == null ? false : quote.getQuoteAccess().isCanEditRQ();
	                boolean isPartRnwlBalGPE = (quote.getSapPrimaryStatus(RQ_STAT_PART_RNW_BAL_GPE) != null);

	                // for draft renewal quote, if it is editable, we send part_qty + order_qty
	                // else, we send part_qty
	                if (canEditRQ || isPartRnwlBalGPE) {
	                    int orderedQty = qtLineItem.getOrderQty();
	                    qty += orderedQty;
	                }
                }
            }
            
            Double unitPrice = qtLineItem.getLocalUnitProratedDiscPrc();
            Double totalPrice = qtLineItem.getLocalExtProratedDiscPrc();
            
            if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(quote.getQuoteHeader().getFulfillmentSrc())) {
                unitPrice = qtLineItem.getChannelUnitPrice();
                totalPrice = qtLineItem.getChannelExtndPrice();
            }
            
            qmLineItem.setItemNumber(itemNumber);           
            qmLineItem.setPartNumber(qtLineItem.getPartNum());
            qmLineItem.setQuantity(qty);
	        qmLineItem.setItemStartDate(DateHelper.getDateByFormat(qtLineItem.getMaintStartDate(), SAP_DATE_FORMAT));
	        qmLineItem.setItemEndDate(DateHelper.getDateByFormat(qtLineItem.getMaintEndDate(), SAP_DATE_FORMAT));
            
            //If it's a appliance part and do not show start/end date, then set setItemStartDate/setItemEndDate is null
            if (qtLineItem.isApplncPart() && (!QuoteCommonUtil.isShowDatesForApplnc(qtLineItem)) || qtLineItem.isSaasDaily() || (qtLineItem.isMonthlySoftwarePart() && ((MonthlySwLineItem)qtLineItem).isMonthlySwDailyPart())) {
         		 qmLineItem.setItemStartDate(DateHelper.getDateByFormat(null, SAP_DATE_FORMAT));
         		 qmLineItem.setItemEndDate(DateHelper.getDateByFormat(null, SAP_DATE_FORMAT));
         	}
            qmLineItem.setCurrencyCode(quote.getQuoteHeader().getCurrencyCode());
            qmLineItem.setUnitPrice(unitPrice);
            qmLineItem.setTotalPrice(totalPrice);
            qmLineItem.setModificationType(ModificationType.fromString(modType));
            if (header.isRenewalQuote()) {
                // The modification comment is only for renewal quotes
	            qmLineItem.setModificationComment(StringUtils.substring(qtLineItem.getComment(), 0,
                        DraftQuoteConstants.MAX_RQ_SALSE_COMM));
            }
            if (StringUtils.isNotBlank(qtLineItem.getPVUOverrideQtyIndCode()))
                qmLineItem.setVuQtyOverrideCode(qtLineItem.getPVUOverrideQtyIndCode());
            
            if (qtLineItem.getOvrrdExtPrice() != null){
            	qmLineItem.setOverrideExtPrice(qtLineItem.getOvrrdExtPrice());
            } else if (qtLineItem.getOverrideUnitPrc() != null){
            	if(!CommonServiceUtil.checkIsUsagePart(qtLineItem)){//for usage part(on demand, subscription overage, setup overage, daily part) special bid pricing will be sent to SAP in SPBD_OVERAGE)
            		logContext.info(this, qtLineItem.getPartNum() + " is not usage part, will populdate OverrideUnitPrice field.");
            		qmLineItem.setOverrideUnitPrice(qtLineItem.getOverrideUnitPrc());
            	}
            } else if (CommonServiceUtil.isValidDiscoutPct(qtLineItem.getLineDiscPct())){
            	qmLineItem.setDiscountPercent(new Double(DecimalUtil.roundAsDouble(qtLineItem.getLineDiscPct(), 3)));
            }
            
            if (quote.getQuoteHeader().isChannelQuote()) {
	            // if the channel margin is not null and greater than or equal as zero send to sap.
	            Double chnlOvrrdDiscPct = qtLineItem.getChnlOvrrdDiscPct();
	            if (chnlOvrrdDiscPct != null){
	                double dChnlOvrrdDiscPct = chnlOvrrdDiscPct.doubleValue();
	                if (dChnlOvrrdDiscPct >= 0.0)
	                    qmLineItem.setPrtnrDiscPct(new Double(DecimalUtil.roundAsDouble(dChnlOvrrdDiscPct , 3)));
	            } else {	            
		            // if this quote has new customer with agreement type = Additional site for an existing contract
		            // Set the BP override discount to be exactly the same as the standard BP discount 
		            if (quote.getCustomer().isAddiSiteCustomer()) {
		            	Double chnlStdDiscPct = qtLineItem.getChnlStdDiscPct();
			            if (chnlStdDiscPct != null){
			                double dChnlStdDiscPct = chnlStdDiscPct.doubleValue();
			                if (dChnlStdDiscPct >= 0.0)
			                    qmLineItem.setPrtnrDiscPct(new Double(DecimalUtil.roundAsDouble(dChnlStdDiscPct , 3)));
			            }
		            }
	            }
            }
            
            qmLineItem.setBackDateFlag(qtLineItem.getBackDatingFlag()?"Y":"N");
            
            if (quote.getQuoteHeader().isSalesQuote()) {                
                qmLineItem.setPartType(StringUtils.trimToEmpty(CommonServiceUtil.correctLineItemPartType(qtLineItem)));
                
                if (qtLineItem.getIRelatedLineItmNum() >= 0) {
                    //qmLineItem.setRelatedItemNumber(new Integer(qtLineItem.getIRelatedSapItemNum()));
                    qmLineItem.setRelatedItemNumber(new Integer(qtLineItem.getIRelatedLineItmNum()));
                }

                qmLineItem.setStartDateOverrideFlag(StartDateOverrideFlag.fromValue(BooleanUtils.toString(qtLineItem.getStartDtOvrrdFlg(), "Y", "N")));

                qmLineItem.setEndDateOverrideFlag(EndDateOverrideFlag.fromValue(BooleanUtils.toString((qtLineItem.getEndDtOvrrdFlg()||qtLineItem.getProrateFlag()), "Y", "N")));
            }
            
          //set Appliance parts
            if (qtLineItem.isApplncPart()){
                qmLineItem.setMachineType(qtLineItem.getMachineType());
                qmLineItem.setMachineModel(qtLineItem.getModel());
                qmLineItem.setSerialNumber(qtLineItem.getSerialNumber());
                qmLineItem.setSerialWarningFlag(PartPriceCommon.isShowApplncMTMMsg(qtLineItem));
                qmLineItem.setDeploymentId(qtLineItem.getDeployModel().getDeployModelId());
                if(qtLineItem.isRenewalPart()&& StringUtils.isNotBlank(qtLineItem.getRenewalDeploymtID())){
                	qmLineItem.setDeploymentId(StringUtils.trimToEmpty(qtLineItem.getRenewalDeploymtID()));
                }
            }
            
            //set the Non Ibm model and serial number
            if (qtLineItem.isDisplayModelAndSerialNum()) {
            	qmLineItem.setNonIbmModel(qtLineItem.getNonIBMModel());
            	qmLineItem.setNonIbmSerialNumber(qtLineItem.getNonIBMSerialNumber());
			}
          
            //ANDY since 14.2 if CSA quote, set HDR_AGREEMENT_AMENDED
            if(quote.getQuoteHeader().isCSRAQuote() 
            		|| quote.getQuoteHeader().isCSTAQuote()){
            	qmLineItem.setHdrAgrmentAmdFlag(null == qtLineItem.getHdrAgrmentAmdFlag() ? "" : (qtLineItem.getHdrAgrmentAmdFlag() == true ? "Y" : "N"));
                qmLineItem.setTouUrl(qtLineItem.getTouURL() == null ? "" : qtLineItem.getTouURL());
                qmLineItem.setTouUrlName(qtLineItem.getTouName() == null ? "" : qtLineItem.getTouName());
            }
            //ANDY before 14.2, if SAAS_TERM_COND_FLAG==2,did not send ToU data to SAP
//            if(quote.getQuoteHeader().getSaasTermCondCatFlag() != 2){
            if(quote.getQuoteHeader() != null && quote.getQuoteHeader().shouldSentTouUrltoSAP()){
            	qmLineItem.setAmendedTouFlag(null == qtLineItem.getAmendedTouFlag() ? "" : (qtLineItem.getAmendedTouFlag() == true ? "Y" : "N"));
                qmLineItem.setAmendedTouFlagB(null == qtLineItem.getAmendedTouFlagB() ? "" : (qtLineItem.getAmendedTouFlagB() == true ? "Y" : "N"));
                placeHolderFlag = placeHolderFlagMap.get(qtLineItem.getTouURL());
                if(null == placeHolderFlag && null!=qtLineItem.getTouURL()){
                	try {
            			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
            			placeHolderFlagMap.put(qtLineItem.getTouURL(), quoteProcess.isPlaceHolderTou(qtLineItem.getTouURL()));
            			placeHolderFlag = placeHolderFlagMap.get(qtLineItem.getTouURL());
            		} catch (QuoteException e) {
            			logContext.debug(this, e.getMessage());
            		}
                }
                if(null != placeHolderFlag && placeHolderFlag.booleanValue()){
    				qmLineItem.setAmendedTouFlag("Y");
    			}
                qmLineItem.setTouUrl(qtLineItem.getTouURL() == null ? "" : qtLineItem.getTouURL());
                qmLineItem.setTouUrlName(qtLineItem.getTouName() == null ? "" : qtLineItem.getTouName());
            }   
            
            logContext.debug(this, "ItemNumber = " + qmLineItem.getItemNumber());
            logContext.debug(this, "PartNumber = " + qmLineItem.getPartNumber());
            logContext.debug(this, "Quantity = " + qmLineItem.getQuantity());
            logContext.debug(this, "ItemStartDate = " + qmLineItem.getItemStartDate());
            logContext.debug(this, "ItemEndDate = " + qmLineItem.getItemEndDate());
            logContext.debug(this, "CurrencyCode = " + qmLineItem.getCurrencyCode());
            logContext.debug(this, "UnitPrice = " + qmLineItem.getUnitPrice());
            logContext.debug(this, "TotalPrice = " + qmLineItem.getTotalPrice());
            logContext.debug(this, "DiscountPercent = " + qmLineItem.getDiscountPercent());
            logContext.debug(this, "ModificationType = " + qmLineItem.getModificationType());
            logContext.debug(this, "ModificationComment = " + qmLineItem.getModificationComment());
            logContext.debug(this, "VuQtyOverrideCode = " + qmLineItem.getVuQtyOverrideCode());
            logContext.debug(this, "OverrideUnitPrice = " + qmLineItem.getOverrideUnitPrice());
            logContext.debug(this, "ChnlOvrrdDiscPct = " + qmLineItem.getPrtnrDiscPct());
            logContext.debug(this, "PartType = " + qmLineItem.getPartType());
            logContext.debug(this, "RelatedItemNumber = " + qmLineItem.getRelatedItemNumber());
            logContext.debug(this, "StartDateOverrideFlag = " + qmLineItem.getStartDateOverrideFlag());
            logContext.debug(this, "EndDateOverrideFlag = " + qmLineItem.getEndDateOverrideFlag());   
            logContext.debug(this, "AmendedTouFlag = " + qmLineItem.getAmendedTouFlag());   
            logContext.debug(this, "AmendedTouFlagB = " + qmLineItem.getAmendedTouFlagB());   
            logContext.debug(this, "TouUrl = " + qmLineItem.getTouUrl());   
            logContext.debug(this, "TouUrlName = " + qmLineItem.getTouUrlName());   
            logContext.debug(this, "HdrAgrmentAmdFlag = " + qmLineItem.getHdrAgrmentAmdFlag()); 
            qmLineItems.add(qmLineItem);
        }

        return (QuoteModifyLineItem[]) qmLineItems.toArray(new QuoteModifyLineItem[0]);
    }
    
    /**
     * Update submitted quote appliance part line item level CRAD, MTM/Ser, Deploy id
     */
    protected QuoteModifyLineItem[] genQuoteModifyLineItems(Quote quote) throws InvalidWSInputException {
        if (quote == null || quote.getQuoteHeader() == null
                || quote.getLineItemList() == null)
            return new QuoteModifyLineItem[0];

        QuoteHeader header = quote.getQuoteHeader();
        List qtLineItems = quote.getLineItemList();
        ArrayList qmLineItems = new ArrayList();
        
        if (qtLineItems == null)
            return new QuoteModifyLineItem[0];
        
        logContext.debug(this, "Quote modify submitted line item change begin:");
        
        for (int i = 0; i < qtLineItems.size(); i++) {
            QuoteLineItem qtLineItem = (QuoteLineItem) qtLineItems.get(i);
            
            if ( !(qtLineItem.getModifiedProperty().isCrad() || qtLineItem.getModifiedProperty().isMtm() || qtLineItem.getModifiedProperty().isDeployId() ) )
            {
            	continue;
            }
            
            QuoteModifyLineItem qmLineItem = new QuoteModifyLineItem();
            
            String modType = PartPriceConstants.PartChangeType.SAP_PART_UPDATED;

            int itemNumber = 0;

	        if (StringUtils.isBlank(header.getSapQuoteNum()))
	             itemNumber = qtLineItem.getDestSeqNum();
	        else
	             itemNumber = qtLineItem.getSapLineItemSeqNum();
	         
            if (itemNumber < 0)
                throw new InvalidWSInputException(
                        "Failed to call quote modify service due to invalid line item number: " + itemNumber + getWebQuoteNumInfo(header));

            int qty = CommonServiceUtil.getPartQty(qtLineItem);

            qmLineItem.setItemNumber(itemNumber);           
            qmLineItem.setPartNumber(qtLineItem.getPartNum());
            qmLineItem.setQuantity(qty);
            qmLineItem.setModificationType(ModificationType.fromString(modType));
            qmLineItem.setCurrencyCode(quote.getQuoteHeader().getCurrencyCode());
            
            
            logContext.debug(this, "ItemNumber = " + qmLineItem.getItemNumber());
            logContext.debug(this, "PartNumber = " + qmLineItem.getPartNumber());
            logContext.debug(this, "Quantity = " + qmLineItem.getQuantity());
            logContext.debug(this, "CurrencyCode = " + qmLineItem.getCurrencyCode()); 

            qmLineItem.setRequestedArrivalDate(DateHelper.getDateByFormat(qtLineItem.getLineItemCRAD(), SAP_DATE_FORMAT));
            logContext.debug(this, "lineitemCRAD = " + DateHelper.getDateByFormat(qtLineItem.getLineItemCRAD(), SAP_DATE_FORMAT));
        
            qmLineItem.setDeploymentId(StringUtils.trimToEmpty(qtLineItem.getDeployModel().getDeployModelId()));
            logContext.debug(this, "deploy id = " + qtLineItem.getDeployModel().getDeployModelId());
        
            qmLineItem.setMachineType(qtLineItem.getMachineType());
            qmLineItem.setMachineModel(qtLineItem.getModel());
            qmLineItem.setSerialNumber(qtLineItem.getSerialNumber());
            boolean serFlag = PartPriceCommon.isShowApplncMTMMsg(qtLineItem);
            qmLineItem.setSerialWarningFlag(serFlag);
                
            logContext.debug(this, "mtm type = " + qtLineItem.getMachineType());
            logContext.debug(this, "mtm model = " + qtLineItem.getModel());
            logContext.debug(this, "mtm ser = " + qtLineItem.getSerialNumber());
            logContext.debug(this, "mtm flag = " + serFlag);

            qmLineItems.add(qmLineItem);
        }

        return (QuoteModifyLineItem[]) qmLineItems.toArray(new QuoteModifyLineItem[0]);
    }
    
    protected LineItemProcessor[] genLineItemProcessors(Quote quote, boolean sendModified) {
        if (quote == null || quote.getQuoteHeader() == null
                || quote.getLineItemList() == null )
            return new LineItemProcessor[0];
        
        QuoteHeader header = quote.getQuoteHeader();
        List lineItemList = quote.getLineItemList();
        ArrayList processorList = new ArrayList();
        
        if (lineItemList == null || StringUtils.isNotBlank(header.getSapIntrmdiatDocNum()))
            return new LineItemProcessor[0];
        
        for (int i = 0; i < lineItemList.size(); i++) {
            QuoteLineItem qtLineItem = (QuoteLineItem) lineItemList.get(i);
            List lineItemConfigList = qtLineItem.getLineItemConfigs();
            
            String modType = CommonServiceUtil.getChangeType(qtLineItem);
            
            int itemNumber = 0;
            if (header.isSalesQuote()) {
                if (StringUtils.isBlank(header.getSapQuoteNum()))
                    itemNumber = qtLineItem.getDestSeqNum();
                else
                    itemNumber = qtLineItem.getSapLineItemSeqNum();
            }
            else {
                itemNumber = qtLineItem.getSapLineItemSeqNum();
                if (itemNumber < 0)
                    itemNumber = qtLineItem.getSeqNum();
            }
            
            if ((sendModified && (PartPriceConstants.PartChangeType.SAP_PART_NO_CHANGES.equals(modType)
                    || StringUtils.isBlank(modType)))
                    || lineItemConfigList == null)
                continue;
            
            if (PartPriceConstants.PartChangeType.SAP_PART_ADDED.equals(modType)
                    || (PartPriceConstants.PartChangeType.SAP_PART_UPDATED.equals(modType) 
                            && qtLineItem.getChgType().indexOf(PartPriceConstants.PartChangeType.PART_PVU_CHANGED) >= 0)) {
	            for (int j = 0; j < lineItemConfigList.size(); j++) {
	                QuoteLineItemConfig config = (QuoteLineItemConfig) lineItemConfigList.get(j);
	                LineItemProcessor processor = new LineItemProcessor();
	                
	                processor.setItemNumber(itemNumber);
	                processor.setProcessorCode(config.getProcrCode());
	                processor.setProcessorQty(config.getProcrTypeQty());
	                processor.setUnitDVUs((int) config.getCoreValueUnit());
	                processor.setExtendedDVUs(config.getExtndDVU());
	                
	                logContext.debug(this, "line item processor " + i + "." + j + ":");
	                logContext.debug(this, "ItemNumber = " + processor.getItemNumber());
	                logContext.debug(this, "ProcessorCode = " + processor.getProcessorCode());
	                logContext.debug(this, "ProcessorQty = " + processor.getProcessorQty());
	                logContext.debug(this, "UnitDVUs = " + processor.getUnitDVUs());
	                logContext.debug(this, "ExtendedDVUs = " + processor.getExtendedDVUs());
	                
	                processorList.add(processor);
	            }
            }
        }
        
        return (LineItemProcessor[]) processorList.toArray(new LineItemProcessor[0]);
    }
    
    public void modifyQuoteStatus(QuoteHeader qtHeader, ArrayList docStatList, QuoteUserSession salesRep)
            throws WebServiceException, TopazException {

        if (qtHeader.isRenewalQuote() && salesRep == null) {
            throw new WebServiceException(
                    "QuoteUserSession is null, quote modify service can not be called successfully at this time."
                            + getWebQuoteNumInfo(qtHeader));
        }
        
        DocumentStatus[] docStats = (DocumentStatus[]) docStatList.toArray(new DocumentStatus[0]);

        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();

        if (qtHeader.isSalesQuote()) { //sales quote
            if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
                qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
            else
                qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
            
            if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
                qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());

        }
        else { //renewal quote
            qmHeader.setQuoteNumber(qtHeader.getRenwlQuoteNum());
            qmHeader.setLastModDateTime(CommonServiceUtil.getModDate(qtHeader.getRqModDate(), qtHeader
                    .getRqStatModDate()));
            qmHeader.setPaoBpAccess(PaoBpAccess.fromString(qtHeader.getRnwlPrtnrAccessFlag() == 1 ? "Y" : "N"));
            qmHeader.setModifiedBySerialNum(salesRep.getSerialNumber());
            qmHeader.setModifiedByCountryCode(salesRep.getCountryCode3());
            
            if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
                qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
            
            this.persistWebStatusChange(qtHeader, docStatList);
        }
        qmInput.setHeader(qmHeader);
        qmInput.setStatuses(docStats);

        logContext.debug(this, "Calling modifyQuoteStatus.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());
        logContext.debug(this, "LastModDateTime = " + qmHeader.getLastModDateTime());
        logContext.debug(this, "ModifiedBySerialNum = " + qmHeader.getModifiedBySerialNum());
        logContext.debug(this, "ModifiedByCountryCode = " + qmHeader.getModifiedByCountryCode());
        for (int i = 0; i < docStats.length; i++) {
            logContext.debug(this, "Status to set----" + docStats[i].getStatusCode());
            logContext.debug(this, "Active Flag----" + docStats[i].isActiveFlag());
        }

        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }    
    
    /**
     * This method should ONLY be called by sales quote, first approval action.  Should not be called by renewal quote
     * if docStatList is not null, then that means this is a final approve action, also need to modify the quote status.
     * <strong>Note:</strong>first approval action will lead to send re-priced price of each line item.
     * @param quote
     * @param docStatList
     * @param salesRep
     * @throws WebServiceException     * 
     * 
     */
    public void updateQuoteStatusByApprvrAction(Quote quote, ArrayList docStatList) throws WebServiceException {
        if (quote == null || quote.getQuoteHeader() == null)
            return;

        QuoteHeader qtHeader = quote.getQuoteHeader();
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();

        if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
        {
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        }
        if ( StringUtils.isNotBlank(qtHeader.getOriginalIdocNum()) )
        {
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        }
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
        {
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        }

        logContext.debug(this, "Calling updateQuoteStatusByApprvrAction.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());

        qmInput.setHeader(qmHeader);
        qmInput.setLineItems(genQuoteModifyLineItems(quote, false, qmHeader));

        //14.4: Net Tcv Increase
        //    because this method will send line item data (calling qmi.setLineItems() ), 
        //        it may effect the line item price, so have to 
        //        recalculate net tcv increase and send it out.
        qmHeader.setNetTcvIncrease( getTotalTcvIncrease(quote) );
        //~14.4 Net Tcv Increase
        
        
        if ( docStatList != null && docStatList.size() > 0 )
        {
            DocumentStatus[] docStats = (DocumentStatus[]) docStatList.toArray(new DocumentStatus[0]);
            qmInput.setStatuses(docStats);
        }
		logContext.debug(this, "before call modify service QuoteNumber = " + qmHeader.getQuoteNumber());
        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }
    
    public void updateLineItemDate(Quote quote, QuoteUserSession salesRep) throws WebServiceException {
        if (quote == null || quote.getQuoteHeader() == null)
            return;
        
        logContext.debug(this, "Generating quote modify input for line item date updating:");
        
        QuoteHeader qtHeader = quote.getQuoteHeader();
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();
        
        if (qtHeader.isRenewalQuote() && salesRep == null) {
            throw new WebServiceException(
                    "QuoteUserSession is null, quote modify service can not be called successfully at this time."
                            + getWebQuoteNumInfo(qtHeader));
        }

        if (qtHeader.isSalesQuote()) {
            if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
                qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
            else
                qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
            
            if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
                qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        }
        else {
            qmHeader.setQuoteNumber(qtHeader.getRenwlQuoteNum());
            qmHeader.setLastModDateTime(CommonServiceUtil.getModDate(qtHeader.getRqModDate(), qtHeader
                    .getRqStatModDate()));
            qmHeader.setModifiedBySerialNum(salesRep.getSerialNumber());
            qmHeader.setModifiedByCountryCode(salesRep.getCountryCode3());
            qmHeader.setPaoBpAccess(PaoBpAccess.fromString(qtHeader.getRnwlPrtnrAccessFlag() == 1 ? "Y" : "N"));
            
            if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
                qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        }
        
        qmInput.setHeader(qmHeader);
        qmInput.setLineItems(genQuoteModifyLineItems(quote, true));
        
        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }
    
    public void updateQuotePartners(Quote quote) throws WebServiceException {
        
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();
        QuoteHeader qtHeader = quote.getQuoteHeader();
        
        if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        else
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());
        
//        String dstrbtrEmail = quote.getPayer() == null ? "" : quote.getPayer().getEmail();
//        if (StringUtils.isNotBlank(dstrbtrEmail)) {
//            qmHeader.setSendOutputToAdditionalEmailFlag(SendOutputToAdditionalEmailFlag.fromString("Y"));
//            qmHeader.setAdditionalEmail(dstrbtrEmail);
//        }
        qmHeader.setTriggerY9EmailFlag(TriggerY9EmailFlag.fromString(qtHeader.isSendQuoteToAddtnlPrtnrFlag() ? "Y"
                : "N"));
        qmHeader.setTriggerPartnerEmailFlag(TriggerPartnerEmailFlag.fromString(StringUtils.isNotBlank(qtHeader
                .getAddtnlPrtnrEmailAdr()) ? "Y" : "N"));
        if (StringUtils.isNotBlank(qtHeader.getAddtnlPrtnrEmailAdr()))
            qmHeader.setAdditionalEmail3(qtHeader.getAddtnlPrtnrEmailAdr());
        qmInput.setHeader(qmHeader);
        
        ArrayList funcList = new ArrayList();
        if (StringUtils.isNotBlank(quote.getQuoteHeader().getRselCustNum())) {
            PartnerFunction rselPF = new PartnerFunction();
            rselPF.setPartnerFunctionCode(CUST_PRTNR_FUNC_RSEL);
            rselPF.setPartnerCustomerNumber(quote.getQuoteHeader().getRselCustNum());
            funcList.add(rselPF);
        }
        if (StringUtils.isNotBlank(quote.getQuoteHeader().getPayerCustNum())) {
            PartnerFunction payerPF = new PartnerFunction();
            payerPF.setPartnerFunctionCode(CUST_PRTNR_FUNC_PAYER);
            payerPF.setPartnerCustomerNumber(quote.getQuoteHeader().getPayerCustNum());
            funcList.add(payerPF);
            
            PartnerFunction billToPF = new PartnerFunction();
            billToPF.setPartnerFunctionCode(CUST_PRTNR_FUNC_BILLTO);
            billToPF.setPartnerCustomerNumber(quote.getQuoteHeader().getPayerCustNum());
            funcList.add(billToPF);
        }
		qmInput.setPartnerFunctions((PartnerFunction[]) funcList.toArray(new PartnerFunction[0]));
		
		DocumentStatus ds = new DocumentStatus();
        ds.setStatusCode(SQ_STAT_PRTNR_TBD);
        ds.setActiveFlag(false);
        DocumentStatus[] dsl = new DocumentStatus[1];
        dsl[0] = ds;
        qmInput.setStatuses(dsl);

        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }
    
    protected String getQuoteNumInfo(QuoteModifyInput qmInput) {
        if (qmInput == null || qmInput.getHeader() == null)
            return "";
        
        QuoteModifyHeader qmHeader = qmInput.getHeader();
        if (StringUtils.isNotBlank(qmHeader.getQuoteNumber()))
            return " (SAP quote num: " + qmHeader.getQuoteNumber() + ")";
        else if (StringUtils.isNotBlank(qmHeader.getOrigIdocNum()))
            return " (SAP IDOC num: " + qmHeader.getOrigIdocNum() + ")";
        else
            return "";
    }
    
    /**
     * This method is added for approved bid cascading cancel only.
     */
    public void cancelQuote(QuoteHeader quoteHeader) throws WebServiceException {
        
        if (quoteHeader == null || !quoteHeader.isSalesQuote() || !quoteHeader.isCopied4ReslChangeFlag()) {
            logContext.debug(this, "Input quote data is invalid when cancelling quote.");
            return;
        }
        
        QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();

        //Set quote modify header information
        if (StringUtils.isNotBlank(quoteHeader.getSapQuoteNum()))
            qmHeader.setQuoteNumber(quoteHeader.getSapQuoteNum());
        else
            qmHeader.setOrigIdocNum(quoteHeader.getOriginalIdocNum());
        
        if (StringUtils.isNotBlank(quoteHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(quoteHeader.getSapIntrmdiatDocNum());
        
        qmInput.setHeader(qmHeader);
        
        // Set quote modify statuses
        ArrayList dsl = new ArrayList();
        this.addDocumentStatus(dsl, SQ_STAT_QT_TERMINATED, true);
        this.addDocumentStatus(dsl, SQ_STAT_SPECIAL_BID_CANCELLED, true);
        this.addDocumentStatus(dsl, SQ_STAT_SPECIAL_BID_APPROVED, false);
        
        qmInput.setStatuses((DocumentStatus[]) dsl.toArray(new DocumentStatus[0]));
        
        logContext.debug(this, "Calling quote modify service to cancel quote: " + quoteHeader.getWebQuoteNum());
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());
        logContext.debug(this, "PrevIdocNum = " + qmHeader.getPrevIdocNum());

        QuoteModifyOutput output = executeQuoteModify(qmInput,quoteHeader);
        quoteHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
    }
    
    public void updateTouAmendmentLineItems(Quote quote) throws WebServiceException {
		if (quote == null || quote.getQuoteHeader() == null){
			logContext.debug(this, "quote is null" );
			return;
		}
			

		QuoteModifyHeader qmHeader = new QuoteModifyHeader();
		QuoteModifyInput qmInput = new QuoteModifyInput();
		QuoteHeader qtHeader = quote.getQuoteHeader();

		if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
			qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
		else
			qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());

		if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
			qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());

		qmInput.setHeader(qmHeader);
		qmInput.setLineItems(genTouModifyLineItems(quote));

		QuoteModifyOutput output = executeQuoteModify(qmInput, qtHeader);
		qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
	}
    
    protected QuoteModifyLineItem[] genTouModifyLineItems(Quote quote) throws InvalidWSInputException {
        if (quote == null || quote.getQuoteHeader() == null
                || quote.getLineItemList() == null)
            return new QuoteModifyLineItem[0];

        QuoteHeader header = quote.getQuoteHeader();
        List qtLineItems = quote.getLineItemList();
        ArrayList qmLineItems = new ArrayList();
        
        Map<String, Boolean> placeHolderFlagMap = new HashMap<String, Boolean>();
        Boolean placeHolderFlag;
        
        if (qtLineItems == null)
            return new QuoteModifyLineItem[0];
        
        logContext.debug(this, "Quote modify TOU line item  begin:");
        
        for (int i = 0; i < qtLineItems.size(); i++) {
            QuoteLineItem qtLineItem = (QuoteLineItem) qtLineItems.get(i);
            QuoteModifyLineItem qmLineItem = new QuoteModifyLineItem();
            
            String modType = PartPriceConstants.PartChangeType.SAP_PART_UPDATED;

            int itemNumber = 0;

	        if (StringUtils.isBlank(header.getSapQuoteNum()))
	             itemNumber = qtLineItem.getDestSeqNum();
	        else
	             itemNumber = qtLineItem.getSapLineItemSeqNum();
	        
	        //  There is no SapLineItemSeqNum in LineItem before data replicated from SAP, so added below logic for ensure value of itemNumber is valid.
	        itemNumber = (0 > itemNumber) ? qtLineItem.getSeqNum() : itemNumber;
	        
            if (itemNumber < 0)
                throw new InvalidWSInputException(
                        "Failed to call quote modify service due to invalid line item number: " + itemNumber + getWebQuoteNumInfo(header));

            int qty = CommonServiceUtil.getPartQty(qtLineItem);

            qmLineItem.setItemNumber(itemNumber);           
            qmLineItem.setPartNumber(qtLineItem.getPartNum());
            qmLineItem.setQuantity(qty);
            qmLineItem.setModificationType(ModificationType.fromString(modType));
            qmLineItem.setCurrencyCode(quote.getQuoteHeader().getCurrencyCode());

            if(quote.getQuoteHeader().getSaasTermCondCatFlag() != 2){
            	qmLineItem.setAmendedTouFlag(null == qtLineItem.getAmendedTouFlag() ? "" : (qtLineItem.getAmendedTouFlag() == true ? "Y" : "N"));
                qmLineItem.setAmendedTouFlagB(null == qtLineItem.getAmendedTouFlagB() ? "" : (qtLineItem.getAmendedTouFlagB() == true ? "Y" : "N"));
                placeHolderFlag = placeHolderFlagMap.get(qtLineItem.getTouURL());
                if(null == placeHolderFlag){
                	try {
            			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
            			placeHolderFlagMap.put(qtLineItem.getTouURL(), quoteProcess.isPlaceHolderTou(qtLineItem.getTouURL()));
            			placeHolderFlag = placeHolderFlagMap.get(qtLineItem.getTouURL());
            		} catch (QuoteException e) {
            			logContext.debug(this, e.getMessage());
            		}
                }
                if(null != placeHolderFlag && placeHolderFlag.booleanValue()){
    				qmLineItem.setAmendedTouFlag("Y");
    			}
                qmLineItem.setTouUrl(qtLineItem.getTouURL() == null ? "" : qtLineItem.getTouURL());
                qmLineItem.setTouUrlName(qtLineItem.getTouName() == null ? "" : qtLineItem.getTouName());
            }   
            
            logContext.debug(this, "ItemNumber = " + qmLineItem.getItemNumber());
            logContext.debug(this, "PartNumber = " + qmLineItem.getPartNumber());
            logContext.debug(this, "Quantity = " + qmLineItem.getQuantity());
            logContext.debug(this, "CurrencyCode = " + qmLineItem.getCurrencyCode());
            logContext.debug(this, "AmendedTouFlag = " + qmLineItem.getAmendedTouFlag());   
            logContext.debug(this, "AmendedTouFlagB = " + qmLineItem.getAmendedTouFlagB());   
            logContext.debug(this, "TouUrl = " + qmLineItem.getTouUrl());   
            logContext.debug(this, "TouUrlName = " + qmLineItem.getTouUrlName()); 
            
            qmLineItems.add(qmLineItem);
        }

        return (QuoteModifyLineItem[]) qmLineItems.toArray(new QuoteModifyLineItem[0]);
    }
    
    /**
     * calculate total tcv increase from quote. Only be useful to addon/tradeup quote.<p>
     * If parameter quote has value of partsPricingConfigrtnsList property, then calculate 
     * total tcv increase with partsPricingConfigrtnsList;
     * Otherwise retrieve configuration list from DB2 with web quote number and then 
     * calculate total tcv increase.
     * </p>
     * @param quote to determine if addon/tradeup quote
     * @return total tcv increase or null
     * @since 14.4
     */
    @SuppressWarnings("unchecked")
	private Double getTotalTcvIncrease(Quote quote){
    	 if(quote != null && quote.getQuoteHeader() != null && quote.getQuoteHeader().isAddTrd()){
         	List<PartsPricingConfiguration> cnfgrtnList = null;
         	if(quote.getPartsPricingConfigrtnsList() == null || quote.getPartsPricingConfigrtnsList().isEmpty()){
         		try {
					cnfgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(quote.getQuoteHeader().getWebQuoteNum());
				} catch (TopazException e) {
					logContext.error(this, "findPartsPricingConfiguration by webQuoteNum error.");
					e.printStackTrace();
				}
         	}
         	else{
         		cnfgrtnList = quote.getPartsPricingConfigrtnsList();
         	}
         	return QuoteCommonUtil.calculateTotalNetTCVIncrease(cnfgrtnList);
    	 }
    	 else{
    		 return null;
    	 }
    }
    
    /**
     * calculate total tcv increase. Only be useful to addon/tradeup quote.<p>
     * Retrieve configuration list from DB2 with web quote number and then 
     * calculate total tcv increase.
     * @param quoteHeader to determine if addon/tradeup quote
     * @return total tcv increase or null
     * @since 14.4
     */
	@SuppressWarnings("unchecked")
	private Double getTotalTcvIncrease(QuoteHeader quoteHeader){
    	if( quoteHeader != null && quoteHeader.isAddTrd()){
    		try {
    			List<PartsPricingConfiguration> cnfgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(quoteHeader.getWebQuoteNum());
    			return QuoteCommonUtil.calculateTotalNetTCVIncrease(cnfgrtnList);
			} catch (TopazException e) {
				logContext.error(this, "findPartsPricingConfiguration by webQuoteNum error.");
				e.printStackTrace();
				return null;
			}
    	}
    	else return null;
    }
	
	
	   /**
     * Calling SAP QuoteModify RFC for sales quote 
     * @param qtHeader
     * @param expContract
     * @return true is successfully. false is no need to updated.
     * @throws WebServiceException
     */
    public boolean modifySQDateByService(QuoteHeader qtHeader, QtDateContract expContract) throws WebServiceException {
    	QuoteModifyHeader qmHeader = new QuoteModifyHeader();
        QuoteModifyInput qmInput = new QuoteModifyInput();
        boolean isNeedUpdate = setUpdatedFlag(qtHeader, expContract, qmHeader, qmInput);
        
        if(!isNeedUpdate) return false;
        if (StringUtils.isNotBlank(qtHeader.getSapQuoteNum()))
            qmHeader.setQuoteNumber(qtHeader.getSapQuoteNum());
        else
            qmHeader.setOrigIdocNum(qtHeader.getOriginalIdocNum());
        
        if (StringUtils.isNotBlank(qtHeader.getSapIntrmdiatDocNum()))
            qmHeader.setPrevIdocNum(qtHeader.getSapIntrmdiatDocNum());

        logContext.debug(this, "Calling modifySQExpirationDate.");
        logContext.debug(this, "QuoteNumber = " + qmHeader.getQuoteNumber());
        logContext.debug(this, "QuoteIdoc = " + qmHeader.getOrigIdocNum());
        
        QuoteModifyOutput output = executeQuoteModify(qmInput,qtHeader);
        qtHeader.setSapIntrmdiatDocNum(output.getIdocNumber());
        return isNeedUpdate;
    }
    
    /**
     * Set the flag for quote modify web service if any field need to be updated
     * @param qtHeader
     * @param expContract
     * @param qmHeader
     * @param qmInput
     * @return: true, need to update by RFC.
     * @throws WebServiceException
     */
	private boolean setUpdatedFlag(QuoteHeader qtHeader, QtDateContract expContract, QuoteModifyHeader qmHeader,
			QuoteModifyInput qmInput)throws WebServiceException {
		boolean isNeedUpdate = false;
        Date expDate = expContract.getExpDate();
        if (expDate!=null && !expDate.equals(qtHeader.getQuoteExpDate())){
	    	qmHeader.setValidToDate(DateHelper.getDateByFormat(expDate, SAP_DATE_FORMAT));
	        logContext.debug(this, "ValidToDate = " + qmHeader.getValidToDate());
	        isNeedUpdate = true;
        }
        
        Date startDate = expContract.getStartDate();
        if (startDate!=null && !startDate.equals(qtHeader.getQuoteStartDate())){
	        qmHeader.setValidFromDate(DateHelper.getDateByFormat(startDate, SAP_DATE_FORMAT));
	        logContext.debug(this, "ValidFromDate = " + qmHeader.getValidFromDate());
	        isNeedUpdate = true;
        }
        
        Date cradDate = expContract.getCradDate();
        if (cradDate!=null && !cradDate.equals(qtHeader.getCustReqstArrivlDate())){
	        qmHeader.setRequestedArrivalDate(DateHelper.getDateByFormat(cradDate, SAP_DATE_FORMAT));
	        logContext.debug(this, "RequestedArrivalDate = " + qmHeader.getRequestedArrivalDate());
	        setLineItemForCRAD(qtHeader, qmInput, cradDate);
	        isNeedUpdate = true;
        }
        qmInput.setHeader(qmHeader);

        //14.4: Net Tcv Increase
        //    because this method will send line item data (setLineItemForCRAD will call qmInput.setLineItems() ), 
        //        it may effect the line item price, so have to 
        //        recalculate net tcv increase and send it out.
		//if(qmInput.getHeader() != null)
		//	qmInput.getHeader().setNetTcvIncrease( getTotalTcvIncrease(qtHeader) );
        //~14.4 Net Tcv Increase
		return isNeedUpdate;
	}
}

