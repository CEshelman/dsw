package com.ibm.dsw.quote.common.service;

import is.domainx.User;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.BudgetaryQuoteFlag;
import DswSalesLibrary.Comment;
import DswSalesLibrary.Delegate;
import DswSalesLibrary.DistributionChannelCode;
import DswSalesLibrary.DocumentStatus;
import DswSalesLibrary.LineItemProcessor;
import DswSalesLibrary.PartnerAddress;
import DswSalesLibrary.PartnerFunction;
import DswSalesLibrary.QuoteCreate;
import DswSalesLibrary.QuoteCreateHeader;
import DswSalesLibrary.QuoteCreateInput;
import DswSalesLibrary.QuoteCreateLineItem;
import DswSalesLibrary.QuoteCreateOutput;
import DswSalesLibrary.SpecialBidApprover;
import DswSalesLibrary.SpecialBidHeader;
import DswSalesLibrary.TriggerPartnerEmailFlag;
import DswSalesLibrary.TriggerY9EmailFlag;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
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
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemConfig;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.util.sort.PartSortUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>QuoteCreateServiceHelper<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-4-17
 */

public class QuoteCreateServiceHelper extends QuoteBaseServiceHelper {

    /**
     *
     */
    public QuoteCreateServiceHelper() {
        super();
    }

    public void callQuoteCreateService(User user, QuoteUserSession salesRep, Quote quote) throws WebServiceException {

        QuoteCreateOutput quoteCreateOutput = null;
        QuoteCreateInput quoteCreateInput = initQuoteCreateInput(user, salesRep, quote);

        try {
            ServiceLocator serviceLocator = new ServiceLocator();
            QuoteCreate quoteCreate = (QuoteCreate) serviceLocator.getServicePort(
                    CommonServiceConstants.QUOTE_CREATE_BINDING, QuoteCreate.class);
           quoteCreateOutput = quoteCreate.execute(quoteCreateInput);
       } catch (RemoteException e) {
            throw new WebServiceUnavailableException("The quote create service is unavailable now."
                    + getWebQuoteNumInfo(quote.getQuoteHeader()), e);
        } catch (ServiceLocatorException e) {
            throw new WebServiceUnavailableException("The quote create service is unavailable now."
                    + getWebQuoteNumInfo(quote.getQuoteHeader()), e);
        } catch (Exception e) {
            throw new WebServiceUnavailableException("The quote create service is unavailable now."
                    + getWebQuoteNumInfo(quote.getQuoteHeader()), e);
        }


        boolean hasError = quoteCreateOutput.getErrorFlag() == null ? false : quoteCreateOutput.getErrorFlag()
                .booleanValue();

        if (!hasError) {
            quote.getQuoteHeader().setSapIntrmdiatDocNum(quoteCreateOutput.getIdocNumber());
        } else {
            throw new WebServiceFailureException("Failed to call quote create service."
                    + getWebQuoteNumInfo(quote.getQuoteHeader()), MessageKeys.MSG_CREATE_QUOTE_SERVICE_ERROR);
        }
    }

    protected QuoteCreateInput initQuoteCreateInput(User user, QuoteUserSession salesRep, Quote quote) throws WebServiceException{
        QuoteCreateInput qcInput = new QuoteCreateInput();
        logContext.info(this, "Generating quote create input:");

		qcInput.setHeader(genQuoteCreateHeader(user, salesRep, quote));
		qcInput.setSpecialBidHeader(genSpecialBidHeader(quote));
		qcInput.setPartnerFunctions(genPartnerFunctions(quote));
		qcInput.setPartnerAddresses(genPartnerAddress(user, salesRep, quote));
		qcInput.setStatuses(genStatus(quote));
		qcInput.setComments(genQuoteComments(quote));
		qcInput.setDelegates(genDelegates(quote));
		qcInput.setSpecialBidApprovers(genSpecialBidApprovers(quote));
		qcInput.setLineItems(genLineItems(quote));
		qcInput.setLineItemProcessors(genLineItemProcessors(quote));

		return qcInput;
    }

    protected QuoteCreateHeader genQuoteCreateHeader(User user, QuoteUserSession salesRep, Quote quote) throws WebServiceException {
        QuoteCreateHeader qcHeader = new QuoteCreateHeader();

        if (quote == null || quote.getQuoteHeader() == null) {
			return qcHeader;
		}

        QuoteHeader qtHeader = quote.getQuoteHeader();
        Customer cust = quote.getCustomer();

        String distributorEmail = quote.getPayer() == null ? "" : quote.getPayer().getEmail();
        String salesOrg = quote.getCustomer() == null ? "" : quote.getCustomer().getSalesOrg();

        String quoteType = null;
        try {
        	quoteType = QuoteCommonUtil.mapSapSalesDocTypeByLob(quote);
		} catch (TopazException e) {
			e.printStackTrace();
			throw new WebServiceException("can't mapping lob to sap sales doc type from cache");
		}

        // may be H/J/00
        String distChannelCode = qtHeader.getSapDistribtnChnlCode();

        qcHeader.setQuoteId(qtHeader.getWebQuoteNum());
        qcHeader.setQuoteType(quoteType);

        if (qtHeader.isPAQuote() || qtHeader.isSSPQuote() || (qtHeader.isOEMQuote() && CommonServiceUtil.quoteHasRQLineItems(quote))) {
            if (cust != null && !cust.isAddiSiteCustomer()) {
				qcHeader.setContractNumber(qtHeader.getContractNum());
			}
        }

        qcHeader.setCurrencyCode(qtHeader.getCurrencyCode());

        if (StringUtils.isBlank(qtHeader.getExemptnCode())) {
			qcHeader.setOpportunityNumber(StringUtils.trimToEmpty(qtHeader.getOpprtntyNum()));
		} else {
			qcHeader.setExemptionCode(StringUtils.trimToEmpty(qtHeader.getExemptnCode()));
		}

        if (qtHeader.getQuoteStartDate()!= null) {
			qcHeader.setValidFromDate(DateHelper.getDateByFormat(qtHeader.getQuoteStartDate(), SAP_DATE_FORMAT));
		} else {
			throw new WebServiceFailureException("Failed to call quote create service."
                    + getWebQuoteNumInfo(quote.getQuoteHeader()), MessageKeys.MSG_QUOTE_START_DATE_ERR);
		}

        if (qtHeader.getQuoteExpDate()!= null) {
			qcHeader.setValidToDate(DateHelper.getDateByFormat(qtHeader.getQuoteExpDate(), SAP_DATE_FORMAT));
		} else {
			throw new WebServiceFailureException("Failed to call quote create service."
                    + getWebQuoteNumInfo(quote.getQuoteHeader()), MessageKeys.MSG_QUOTE_EXP_DATE_ERR);
		}

        if ((qtHeader.isPAQuote() || qtHeader.isPAEQuote() || qtHeader.isFCTQuote() || qtHeader.isSSPQuote() ||
        		qtHeader.isOEMQuote() || qtHeader.isFCTToPAQuote()) && (qtHeader.isHasAppMainPart()||qtHeader.isHasAppUpgradePart())){
        	qcHeader.setCustReqDeliveryDate(DateHelper.getDateByFormat(qtHeader.getCustReqstArrivlDate(), SAP_DATE_FORMAT));
        }

        qcHeader.setPretaxValue(qtHeader.getQuotePriceTot());
        qcHeader.setTaxValue(0.0);
        qcHeader.setTotalValue(qtHeader.getQuotePriceTot());

        if (qtHeader.isPAQuote() || qtHeader.isPAEQuote() || qtHeader.isOEMQuote() || qtHeader.isSSPQuote()) {
			qcHeader.setWebUrlForPAOSite(CommonServiceUtil.replaceAmps(ApplicationProperties.getInstance()
                    .getPaoSiteURL()));
		}

        qcHeader.setPriceLevel(qtHeader.getTranPriceLevelCode());
        qcHeader.setDistributorEmail(distributorEmail);
        qcHeader.setWebUrlForQuoteForSalesRep(CommonServiceUtil.replaceAmps(ApplicationProperties.getInstance()
                .getQuoteForSalesRepURL() + qtHeader.getWebQuoteNum()));
        if(qtHeader.isSSPQuote()){
        	qcHeader.setSspUsageIndicator(qtHeader.getSspType());
        }

        qcHeader.setDistributionChannelCode(DistributionChannelCode.fromString(distChannelCode));
        qcHeader.setNewCustomerFlag(qtHeader.hasNewCustomer());
        qcHeader.setCopyZ1Flag(qtHeader.getSendQuoteToPrmryCntFlag() == 1);
        qcHeader.setNoTaxOnQuoteOutputFlag(qtHeader.getInclTaxFinalQuoteFlag() == 0);


        //if PGS,set the deflaut false
		if (QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(salesRep
				.getAudienceCode())) {
			qcHeader.setFirmOrdLetterFlag( false );
		} else {
			qcHeader.setFirmOrdLetterFlag(qtHeader.getFirmOrdLtrFlag() == 1);
			if(qtHeader.getQuoteOutputType() == null){
				qcHeader.setQuoteOutputType("TCV");
			}else{
				qcHeader.setQuoteOutputType(qtHeader.getQuoteOutputType().trim());
			}
		}

        //initially set  QUOTE_OUTPUT_TYPE = null
        if(qcHeader.getQuoteOutputType()!=null &&
        		!"TCV".equals(qcHeader.getQuoteOutputType().trim()) &&
        		!"RATE".equals(qcHeader.getQuoteOutputType().trim())){
            qcHeader.setQuoteOutputType(null);
        }
        if(qtHeader.getQuoteOutputOption() != null && QuoteConstants.PAYMENT_SCHEDULE_CHECKBOX_VALUE.equals(qtHeader.getQuoteOutputOption().trim())){
        	qcHeader.setQuoteOutputOption(qtHeader.getQuoteOutputOption().trim());
        }else{
        	qcHeader.setQuoteOutputOption(null);
        }
        qcHeader.setNoItemPriceOnOutputFlag(Boolean.valueOf(qtHeader.getIncldLineItmDtlQuoteFlg() == 0));
        qcHeader.setSalesOrganization(salesOrg);
        qcHeader.setNoGsaFlag(Boolean.valueOf(qtHeader.getGsaPricngFlg() == 0));
        qcHeader.setHoldUrl(CommonServiceUtil.replaceAmps(ApplicationProperties.getInstance()
                .getHoldURL() + qtHeader.getWebQuoteNum()));

        qcHeader.setSendOutputToQuoteContactFlag(qtHeader.getSendQuoteToQuoteCntFlag() == 1);
        if (CommonServiceUtil.needSendAddiMailToCreator(qtHeader)) {
            qcHeader.setSendOutputToAdditionalEmailFlag(true);
            String additnEmail = CommonServiceUtil.genAddiEmailList(qtHeader, user, salesRep);
            qcHeader.setAdditionalEmail(additnEmail) ;
        }
        else {
            	qcHeader.setSendOutputToAdditionalEmailFlag(qtHeader.getSendQuoteToAddtnlCntFlag() == 1);
            	if (qtHeader.getSendQuoteToAddtnlCntFlag() == 1) {
					qcHeader.setAdditionalEmail(qtHeader.getAddtnlCntEmailAdr());
				}
        }


        //RTC task 539101, confirmed with SAP WEB should send PGS for quotes created from SAP but submitted in SQO
        //so send PGS as long as quote is PGS quote here
        if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(quote.getQuoteHeader().getAudCode())){
        	qcHeader.setOrderMethod(ORDER_METHOD_PGS);
        	qcHeader.setInterfaceId(ORDER_METHOD_PGS);
        }else{

        	// For OEM quote, order method code is ZOEM or ASL
	        if (qtHeader.isOEMQuote()) {
				qcHeader.setOrderMethod(qtHeader.getOrdgMethodCode());
			} else {
	            // For FCT to PA quote, order method code is FMP
	            // For other quotes, order method code is ZSQT
	            if (qtHeader.isMigration() || qtHeader.isSaasFCTToPAQuote()) {
					qcHeader.setOrderMethod(ORDER_METHOD_FCT_TO_PA);
				} else {
					qcHeader.setOrderMethod(ORDER_METHOD_SALES_QUOTE);
				}
	        }
        }
        qcHeader.setTriggerY9EmailFlag(TriggerY9EmailFlag.fromString(qtHeader.isSendQuoteToAddtnlPrtnrFlag() ? "Y"
                : "N"));
        qcHeader.setTriggerPartnerEmailFlag(TriggerPartnerEmailFlag.fromString(StringUtils.isNotBlank(qtHeader
                .getAddtnlPrtnrEmailAdr()) ? "Y" : "N"));
        qcHeader.setAdditionalEmail3(qtHeader.getAddtnlPrtnrEmailAdr());

        if ((qtHeader.isFCTQuote()|| qtHeader.isFCTToPAQuote()) && StringUtils.isNotBlank(qtHeader.getAcqrtnCode())) {
            qcHeader.setAcqCode(qtHeader.getAcqrtnCode());
        }

        //send validity days, payment terms to SAP for LA quotes
        if(qtHeader.isSalesQuote() && ButtonDisplayRuleFactory.singleton().isDisplayLAUplift(qtHeader)){
            qcHeader.setPaymentTermDays(new Integer(qtHeader.getPymTermsDays()));
            qcHeader.setProposalValidityDays(new Integer(CommonServiceUtil.getValidityDays(qtHeader)));
        }

        //if this value is not sent,SAP do not set the pricing date on the new quote to the pricing date on the original quote
        //if price is no need re-calculate ,you should sent this field.if not it will cause price discrepancy issue
        if(qtHeader.isCopiedForOutputChangeFlag()
        	||qtHeader.isCopied4ReslChangeFlag()||qtHeader.isExpDateExtendedFlag()){
        	qcHeader.setParentBidQuoteNum(qtHeader.getPriorSapQuoteNum());
        }

		if (qtHeader.isSalesQuote() && (qtHeader.hasSaaSLineItem() || qtHeader.isHasMonthlySoftPart())
				&& StringUtils.isNotBlank(qtHeader.getRefDocNum())) {
        	qcHeader.setChargeAgreement(qtHeader.getRefDocNum());
        }

        if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(salesRep.getAudienceCode())){
        	/*WEB URL*/        	qcHeader.setWebUrlForPAOSite(null);
        	/*REPWEBURL*/	qcHeader.setWebUrlForQuoteForSalesRep(null);
        	/*HOLDURL*/			qcHeader.setHoldUrl(null);
//        	/*No Tax*/				qcHeader.setNoTaxOnQuoteOutputFlag(false);
        	/*COPYZ1*/			qcHeader.setCopyZ1Flag(false);
        	/*TRIGGER_QTCONTACT*/	qcHeader.setSendOutputToQuoteContactFlag(false);
        	/*TRIGGER_Y9*/		qcHeader.setTriggerY9EmailFlag(TriggerY9EmailFlag.fromString("Y"));

        	//fix PL Notes://CAMDB10/85256B890058CBA6/3E21B390B4B09B6285256CA20059CFDF/BDE732D9C91B78818525795F00804D32
        	//populate quote submitter email address to SAP
        	/*TRIGGER_ADD*/	qcHeader.setSendOutputToAdditionalEmailFlag(true);
        	/*ADDEMAIL*/		qcHeader.setAdditionalEmail(salesRep.getEmailAddress());

        	/*ADDEMAIL2*/		qcHeader.setDistributorEmail(null);
        	/*TRIGGER_PARNR*/	qcHeader.setTriggerPartnerEmailFlag(TriggerPartnerEmailFlag.fromString("N"));
        	/*ADDEMAIL3*/		qcHeader.setAdditionalEmail3(null);
        	/*PartnerAddresses*/

        	
        }
        String budgetaryQuoteFlag = qtHeader.getBudgetaryQuoteFlag() == 1 ? DswSalesLibrary.BudgetaryQuoteFlag._Y : DswSalesLibrary.BudgetaryQuoteFlag._N;
        qcHeader.setBudgetaryQuoteFlag(BudgetaryQuoteFlag.fromString(budgetaryQuoteFlag));
        //add agreementType  history data is null,agreementType=qtHeader.lob.getCode()
        String sLob = qtHeader.getLob() == null ? "" : qtHeader.getLob().getCode();
        String agreementType=qtHeader.getAgrmtTypeCode();

        if(StringUtils.isBlank(agreementType)){
        	if(QuoteConstants.LOB_PA.equals(sLob)||QuoteConstants.LOB_PAE.equals(sLob)||QuoteConstants.LOB_FCT.equals(sLob))
        		agreementType=sLob;	
        }
        if(StringUtils.isBlank(agreementType) ||  AGREEMENT_TYPE_CODE.equals(agreementType.trim()))
        	agreementType= QuoteConstants.LOB_PA;
        if(StringUtils.isNotBlank(agreementType))
        	qcHeader.setAgreementType(agreementType);
        
        //14.4 net TCV increase: add on / trade up quote send tcv increase data
        if(quote != null && qtHeader.isAddTrd() ){
            // qtHeader.isAddTrd() gets value from EBIZ1.WEB_QUOTE_CONFIGRTN.CONFIGRTN_ACTION_CODE, so monthly configuration does not involved in. 
        	qcHeader.setNetTcvIncrease(QuoteCommonUtil.calculateTotalNetTCVIncrease(quote.getPartsPricingConfigrtnsList()));
        }
        
        logContext.debug(this, "Quote create header begin:");
        logContext.debug(this, "QuoteId = " + qtHeader.getWebQuoteNum());
        logContext.debug(this, "QuoteType = " + quoteType);
        logContext.debug(this, "ContractNumber = " + qtHeader.getContractNum());
        logContext.debug(this, "CurrencyCode = " + qtHeader.getCurrencyCode());
        logContext.debug(this, "OpportunityNumber = " + qtHeader.getOpprtntyNum());
        logContext.debug(this, "ExemptionCode = " + qtHeader.getExemptnCode());
        logContext.debug(this, "ValidFromDate = " + DateHelper.getDateByFormat(qtHeader.getQuoteStartDate(), SAP_DATE_FORMAT));
        logContext.debug(this, "ValidToDate = " + DateHelper.getDateByFormat(qtHeader.getQuoteExpDate(), SAP_DATE_FORMAT));
        logContext.debug(this, "PretaxValue = " + qtHeader.getQuotePriceTot());
        logContext.debug(this, "TaxValue = " + 0);
        logContext.debug(this, "TotalValue = " + qtHeader.getQuotePriceTot());
        logContext.debug(this, "WebUrlForPAOSite = " + ApplicationProperties.getInstance().getPaoSiteURL());
        logContext.debug(this, "PriceLevel = " + qtHeader.getTranPriceLevelCode());
        logContext.debug(this, "AdditionalEmail = " + qtHeader.getAddtnlCntEmailAdr());
        logContext.debug(this, "DistributorEmail = " + distributorEmail);
        logContext.debug(this, "WebUrlForQuoteForSalesRep = " + ApplicationProperties.getInstance().getQuoteForSalesRepURL());
        logContext.debug(this, "DistributionChannelCode = " + DistributionChannelCode.fromString(distChannelCode));
        logContext.debug(this, "NewCustomerFlag = " + qtHeader.hasNewCustomer());
        logContext.debug(this, "CopyZ1Flag = " + (qtHeader.getSendQuoteToPrmryCntFlag() == 1));
        logContext.debug(this, "NoTaxOnQuoteOutputFlag = " + (qtHeader.getInclTaxFinalQuoteFlag() == 0));
        logContext.debug(this, "FirmOrdLtrFlag = " + (qtHeader.getFirmOrdLtrFlag() == 0));
        logContext.debug(this, "SalesOrganization = " + salesOrg);
        logContext.debug(this, "NoGsaFlag = " + (qtHeader.getGsaPricngFlg() == 0));
        logContext.debug(this, "HoldUrl = " + ApplicationProperties.getInstance().getQuoteForSalesRepURL());
        logContext.debug(this, "SendOutputToQuoteContactFlag = " + (qtHeader.getSendQuoteToQuoteCntFlag() == 1));
        logContext.debug(this, "SendOutputToAdditionalEmailFlag = " + (qtHeader.getSendQuoteToAddtnlCntFlag() == 1));
        logContext.debug(this, "Acq code = " + qtHeader.getAcqrtnCode());
        logContext.debug(this, "Payment terms = " + qtHeader.getPymTermsDays());
        logContext.debug(this, "Validity days = " + qcHeader.getProposalValidityDays());
        logContext.debug(this, "ParentBidQuoteNum = " + qcHeader.getParentBidQuoteNum());
        logContext.debug(this, "chargeAgreementNum = " + qcHeader.getChargeAgreement());
        logContext.debug(this, "custReqstdArrivlDate = " + qtHeader.getCustReqstArrivlDate());
        logContext.debug(this, "quoteOutputOption = " + qcHeader.getQuoteOutputOption());
        logContext.debug(this, "budgetaryQuoteFlag = " + qcHeader.getBudgetaryQuoteFlag());
        logContext.debug(this, "agreementType = " + qcHeader.getAgreementType());
        logContext.debug(this, "netTCVIncrease = " + qcHeader.getNetTcvIncrease());
        return qcHeader;
    }

    protected SpecialBidHeader genSpecialBidHeader(Quote quote) {
        SpecialBidHeader spHeader = new SpecialBidHeader();
        return spHeader;
    }

    protected PartnerFunction[] genPartnerFunctions(Quote quote) {

        if (quote == null || quote.getQuoteHeader() == null) {
			return new PartnerFunction[0];
		}

        ArrayList funcList = new ArrayList();
        QuoteHeader qhd = quote.getQuoteHeader();
        String fulfillmentSrc = qhd.getFulfillmentSrc().trim();

        // set the sold-to info
        String soldToCustNum = qhd.getSoldToCustNum();
		PartnerFunction prtnrFunctionSoldTo = new PartnerFunction();
		prtnrFunctionSoldTo.setPartnerFunctionCode(CUST_PRTNR_FUNC_SOLD_TO);
		prtnrFunctionSoldTo.setPartnerCustomerNumber(soldToCustNum);
		funcList.add(prtnrFunctionSoldTo);

		// set the bill to info
		String billToCustNum = "";
		if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSrc)) {
            billToCustNum = qhd.getPayerCustNum();
            if (qhd.isDistribtrToBeDtrmndFlag() || qhd.isSingleTierNoDistributorFlag()) {
                billToCustNum = qhd.getSoldToCustNum();
            }
        }
		else {
		    if (qhd.isFCTQuote() && StringUtils.isNotBlank(qhd.getPayerCustNum())) {
				billToCustNum = qhd.getPayerCustNum();
			} else {
				billToCustNum = qhd.getSoldToCustNum();
			}
		}
		PartnerFunction prtnrFunctionBillTo = new PartnerFunction();
		prtnrFunctionBillTo.setPartnerFunctionCode(CUST_PRTNR_FUNC_BILLTO);
		prtnrFunctionBillTo.setPartnerCustomerNumber(billToCustNum);
		funcList.add(prtnrFunctionBillTo);

		// set the reseller info
		String resellerCustNum = "";
        if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSrc)) {
            resellerCustNum = qhd.getRselCustNum();
            if (qhd.isResellerToBeDtrmndFlag()) {
                resellerCustNum = qhd.getSoldToCustNum();
            }
        }
        else {
   		    if (qhd.isFCTQuote() && StringUtils.isNotBlank(qhd.getPayerCustNum())) {
				resellerCustNum = qhd.getPayerCustNum();
			} else {
				resellerCustNum = qhd.getSoldToCustNum();
			}
        }
        PartnerFunction prtnrFunctionReseller = new PartnerFunction();
        prtnrFunctionReseller.setPartnerFunctionCode(CUST_PRTNR_FUNC_RSEL);
        prtnrFunctionReseller.setPartnerCustomerNumber(resellerCustNum);
        funcList.add(prtnrFunctionReseller);

		// set the payer info
		String payerCustNum = "";
		if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSrc)) {
            payerCustNum = qhd.getPayerCustNum();
            if (qhd.isDistribtrToBeDtrmndFlag() || qhd.isSingleTierNoDistributorFlag()) {
                payerCustNum = qhd.getSoldToCustNum();
            }
        }
		else {
		    if (qhd.isFCTQuote() && StringUtils.isNotBlank(qhd.getPayerCustNum())) {
				payerCustNum = qhd.getPayerCustNum();
			} else {
				payerCustNum = qhd.getSoldToCustNum();
			}
		}
		PartnerFunction prtnrFunctionPayer = new PartnerFunction();
		prtnrFunctionPayer.setPartnerFunctionCode(CUST_PRTNR_FUNC_PAYER);
		prtnrFunctionPayer.setPartnerCustomerNumber(payerCustNum);
		funcList.add(prtnrFunctionPayer);

		// set the sales rep info
		PartnerFunction prtnrFunctionSalesRep = new PartnerFunction();
		prtnrFunctionSalesRep.setPartnerFunctionCode(CUST_PRTNR_FUNC_SALES_REP);
		prtnrFunctionSalesRep.setPartnerCustomerNumber(String.valueOf(quote.getCustomer().getSapCntId()));
		funcList.add(prtnrFunctionSalesRep);

		// set the customer primary contact info
		PartnerFunction prtnrFunctionPyContact = new PartnerFunction();
		prtnrFunctionPyContact.setPartnerFunctionCode(CUST_PRTNR_FUNC_QUOTE_CONTACT);
		prtnrFunctionPyContact.setPartnerCustomerNumber(String.valueOf(quote.getCustomer().getSapCntId()));
		funcList.add(prtnrFunctionPyContact);

		// set ZG for OEM/SSP end user
		String endUserCustNum = qhd.getEndUserCustNum();
		if ((qhd.isOEMQuote()||qhd.isSSPQuote()) && endUserCustNum != null && endUserCustNum.trim().length()>0)	{
		   PartnerFunction prtnrFunctionZG = new PartnerFunction();
		   prtnrFunctionZG.setPartnerFunctionCode(CUST_PRTNR_FUNC_ZG);
		   prtnrFunctionZG.setPartnerCustomerNumber(endUserCustNum);
		funcList.add(prtnrFunctionZG);
		}
		
		// get all ship to appliance address detail
		ApplianceLineItemAddrDetail applianceLineItemAddrDetail = quote.getApplianceLineItemAddrDetail();
		if(applianceLineItemAddrDetail == null){
			// set the default ship-to info
			String shipToCustNum = qhd.getSoldToCustNum();
			PartnerFunction prtnrFunctionShipTo = new PartnerFunction();
			prtnrFunctionShipTo.setPartnerFunctionCode(CUST_PRTNR_FUNC_SHIPTO);
			prtnrFunctionShipTo.setPartnerCustomerNumber(shipToCustNum);
			prtnrFunctionShipTo.setItemNumber(0);
			funcList.add(prtnrFunctionShipTo);
		}
		funcList.addAll((ArrayList<PartnerFunction>)setShipInstallAddress(0,quote.getQuoteHeader(),applianceLineItemAddrDetail));
		
		logContext.debug(this, "Partner function begin:");
		logContext.debug(this, "soldToCustNum = " + soldToCustNum);
		logContext.debug(this, "payerCustNum = " + payerCustNum);
		logContext.debug(this, "billToCustNum = " + billToCustNum);
		logContext.debug(this, "sapCntId = " + quote.getCustomer().getSapCntId());

		return (PartnerFunction[]) funcList.toArray(new PartnerFunction[0]);
    }

    protected PartnerAddress[] genPartnerAddress(User user, QuoteUserSession salesRep, Quote quote) {
        if (user == null || quote == null || quote.getQuoteHeader() == null) {
			return new PartnerAddress[0];
		}

        ArrayList prtnrAddrList = new ArrayList();

        // set the sales rep address info
        if(!QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(salesRep.getAudienceCode())){
	        PartnerAddress prtnrAddressSalesRep = new PartnerAddress();
	        prtnrAddressSalesRep.setPartnerFunctionCode(CUST_PRTNR_FUNC_SALES_REP);
	        prtnrAddressSalesRep.setPartnerCustomerNumber(String.valueOf(quote.getCustomer().getSapCntId()));

	        // For PA/PAE/FCT/OEM quotes, send opp owner info for ZX
	        QuoteHeader qhd = quote.getQuoteHeader();
	        SalesRep oppOwner = quote.getOppOwner() ;
	        if ((qhd.isFCTQuote() || qhd.isFCTToPAQuote() || qhd.isPAQuote() || qhd.isPAEQuote() || qhd.isOEMQuote() || qhd.isSSPQuote())
	                && oppOwner != null) {
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
        }

        // set the quote contact address info
        List cntList = quote.getContactList();
        if (cntList != null && cntList.size() > 0) {
            QuoteContact cnt = (QuoteContact) cntList.get(0);
			PartnerAddress prtnrAddressQuoteContact = new PartnerAddress();
			prtnrAddressQuoteContact.setPartnerFunctionCode(CUST_PRTNR_FUNC_QUOTE_CONTACT);
	        // always send empty string for partner-id for ZX and ZW since SAP does not need it
	        prtnrAddressQuoteContact.setPartnerCustomerNumber(String.valueOf(quote.getCustomer().getSapCntId()));
	        prtnrAddressQuoteContact.setName1(CommonServiceUtil.limitLength(cnt.getCntFirstName(), MAX_NAME_LENGTH));
	        prtnrAddressQuoteContact.setName2(CommonServiceUtil.limitLength(cnt.getCntLastName(), MAX_NAME_LENGTH));
	        prtnrAddressQuoteContact.setTelephoneNumber(CommonServiceUtil.limitLength(cnt.getCntPhoneNumFull(), MAX_PHONE_LENGTH));
	        prtnrAddressQuoteContact.setFaxNumber(CommonServiceUtil.limitLength(cnt.getCntFaxNumFull(), MAX_FAX_LENGTH));
	        prtnrAddressQuoteContact.setEmailAddress(CommonServiceUtil.limitLength(cnt.getCntEmailAdr(), MAX_EMAIL_LENGTH));
	        prtnrAddrList.add(prtnrAddressQuoteContact);
        }
        
		// get all ship to appliance address detail
		ApplianceLineItemAddrDetail applianceLineItemAddrDetail = quote.getApplianceLineItemAddrDetail();
		prtnrAddrList.addAll((ArrayList<PartnerFunction>)setContactAddress(0, quote.getQuoteHeader(),applianceLineItemAddrDetail));
		
        return (PartnerAddress[]) prtnrAddrList.toArray(new PartnerAddress[0]);
    }

    protected DocumentStatus[] genStatus(Quote quote){
        if (quote == null || quote.getQuoteHeader() == null) {
			return new DocumentStatus[0];
		}

        ArrayList docStatList = new ArrayList();
        QuoteHeader header = quote.getQuoteHeader();
        boolean isSpecBid = (header.getSpeclBidFlag() == 1);

        this.addDocumentStatus(docStatList, SQ_STAT_SUBMITTED, true); //E0003

        if (header.getReqstIbmCustNumFlag() == 1) {
			this.addDocumentStatus(docStatList, SQ_STAT_ICN_REQUESTED, true); //E0001
        }

        if (header.getReqstPreCreditCheckFlag() == 1) {
			this.addDocumentStatus(docStatList, SQ_STAT_PRECREDIT_REQUESTED, true); //E0002
		}

        if (isSpecBid) {
            // if approval route flag is 1 and it is not copied from an approved bid, send E0013 to SAP
            if (header.getApprovalRouteFlag() == 1 && !header.isCopied4ReslChangeFlag()
            		&& !header.isCopied4PrcIncrQuoteFlag()	//prevent copied quote with increased pricing from sending E0013
            		&& !header.isCopiedForOutputChangeFlag()
            		&& !header.isNoApprovalRequire()
            	) {
				this.addDocumentStatus(docStatList, SQ_STAT_SPECIAL_BID_REQUESTED, true); //E0013
            }
            // else sent E0006 to SAP
            else {
                this.addDocumentStatus(docStatList, SQ_STAT_SPECIAL_BID_APPROVED, true); //E0006
            }


            // If ELA quote, send E0029
            if (header.isELAQuote()) {
                this.addDocumentStatus(docStatList, SQ_STAT_ELA, true); //E0029
            }
            // If rebill credited order, send E0030
            if (quote.getSpecialBidInfo().isCredAndRebill()) {
                this.addDocumentStatus(docStatList, SQ_STAT_REBILL, true); //E0030
            }
		}

        if (header.isPPSSQuote()) {
			this.addDocumentStatus(docStatList, SQ_STAT_PPSS, true); //E0020
		}

        if (header.isResellerToBeDtrmndFlag() || header.isDistribtrToBeDtrmndFlag()) {
			this.addDocumentStatus(docStatList, SQ_STAT_PRTNR_TBD, true); // E0027
        }

        // If PAO block flag selected, send E0031
        if (!header.isFCTQuote() && !header.isChannelQuote()) {
	        if (header.getPAOBlockFlag() == 1) {
				this.addDocumentStatus(docStatList, SQ_STAT_PAO_BLOCK, true); //E0031
	        }
        }
        /*// do not send E0022 any more . since 14.2
        if(header.getSaasTermCondCatFlag() == 2
        		//ANDY since release 14.2 if CSRA or CSTA quote, do not send E0022
        		&& !header.isCSRAQuote() 
        		&& !header.isCSTAQuote()){
        	this.addDocumentStatus(docStatList, SQ_STAT_SPECIAL_BID_TS_CS_HOLD, true); //E0022
        }
        */

        logContext.debug(this, "Document status begin:");
		logContext.debug(this, "ReqstIbmCustNumFlag = " + header.getReqstIbmCustNumFlag());
		logContext.debug(this, "ReqstPreCreditCheckFlag = " + header.getReqstPreCreditCheckFlag());
		logContext.debug(this, "isPPSSQuote = " + header.isPPSSQuote());
		logContext.debug(this, "isSpecBid = " + isSpecBid);
		logContext.debug(this, "isResellerToBeDtrmndFlag = " + header.isResellerToBeDtrmndFlag());
		logContext.debug(this, "isDistribtrToBeDtrmndFlag = " + header.isDistribtrToBeDtrmndFlag());
		logContext.debug(this, "PAOBlockFlag = " + header.getPAOBlockFlag());
		logContext.debug(this, "SaasTermCondCatFlag = " + header.getSaasTermCondCatFlag());

        return (DocumentStatus[]) docStatList.toArray(new DocumentStatus[0]);
    }

    protected Comment[] genQuoteComments(Quote quote) {
        if (quote == null || quote.getQuoteHeader() == null) {
			return new Comment[0];
		}

        ArrayList commentList = new ArrayList();
        String sComments = quote.getQuoteHeader().getQSubmitCoverText();

        if (StringUtils.isNotBlank(sComments)) {
        	sComments = CommonServiceUtil.replaceLineSeperator(sComments);
        	ArrayList<String> list = (ArrayList<String>)splitAtSpace(sComments);
        	for(String commentStr:list){
        		Comment comment = new Comment();
        		comment.setItemNum(0);
        		comment.setTextId(TEXT_IDENTIFIER_HEADER_LEVEL);
        		comment.setCommentText(commentStr);
        		commentList.add(comment);
        	}
        }


        logContext.debug(this, "Comment begin:");
		logContext.debug(this, "Comment = " + sComments);

		List lineItemList = quote.getSaaSLineItems();

		for (int i = 0; i < lineItemList.size(); i++) {

			QuoteLineItem qtLineItem = (QuoteLineItem) lineItemList.get(i);
			Comment comment = new Comment();
			comment.setItemNum(qtLineItem.getDestSeqNum());
			comment.setTextId(TEXT_IDENTIFIER_ITEM_LEVEL);
			String prodIdDscr = null;
			PartsPricingConfiguration cli = QuoteCommonUtil.getPartsPricingConfigurationById(qtLineItem.getConfigrtnId(), quote);
			if (cli != null) {
				prodIdDscr = cli.getIbmProdIdDscr();
			}
			if (prodIdDscr != null && prodIdDscr.length() > MAX_COMMENT_LINE_LENGTH) {
				Comment commentSub = new Comment();
				commentSub.setItemNum(qtLineItem.getDestSeqNum());
				commentSub.setTextId(TEXT_IDENTIFIER_ITEM_LEVEL);
				commentSub.setCommentText(prodIdDscr.substring(0,MAX_COMMENT_LINE_LENGTH));
				commentList.add(commentSub);
				prodIdDscr = prodIdDscr.substring(MAX_COMMENT_LINE_LENGTH);
			}
			comment.setCommentText(prodIdDscr);

			commentList.add(comment);
		}

        return (Comment[]) commentList.toArray(new Comment[0]);
    }
    
	protected List<String> splitAtSpace(String str){
    	List<String> list= new ArrayList<String>();
    	str = CommonServiceUtil.replaceLineSeperator(str);
    	String space = " ";
    	int lenRemain = str.length();
    	for (int i = 0; i < MAX_COMMENT_LINE_COUNT; i++) {
    		int len = 0;
    		if(lenRemain > MAX_COMMENT_LINE_LENGTH ){
    			len = MAX_COMMENT_LINE_LENGTH - 1;
    			while(len > 0){
    				if(space.equals(String.valueOf(str.charAt(len)))){
    					len ++;
    					break;
    				}
    				len --;
    			}
    		}
    		len = len > 0? len:(lenRemain > MAX_COMMENT_LINE_LENGTH ? MAX_COMMENT_LINE_LENGTH : lenRemain);
    		String subStr = str.substring(0, len);
    		str = str.substring(len);
    		list.add(subStr);
    		lenRemain = lenRemain - len;
    		if (lenRemain <= 0) {
				break;
			}
    	}
    	 return list;
    }
	
    protected Delegate[] genDelegates(Quote quote) {
        return new Delegate[0];
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected QuoteCreateLineItem[] genLineItems(Quote quote) throws WebServiceException {
        if (quote == null || quote.getQuoteHeader() == null
                || quote.getLineItemList() == null) {
			return new QuoteCreateLineItem[0];
		}

        List lineItemList = quote.getLineItemList();
		QuoteCommonUtil.buildSaaSLineItemsWithRampUp(CommonServiceUtil.getSaaSLineItemList(lineItemList));
        try {
            quote.getMonthlySwQuoteDomain().processMonthlyPartsForQutoeCreateService(lineItemList);
        } catch (TopazException e) {
            throw new WebServiceException(
                    "can NOT process monthly parts correctly."
                    + getWebQuoteNumInfo(quote.getQuoteHeader()), e);
        }
        PartSortUtil.sortByDestSeqNumber(lineItemList);
        ArrayList qcLineItemList = new ArrayList();
        Customer cust = quote.getCustomer();

        logContext.debug(this, "Quot create line items begin:");

        boolean isPAQuote = quote.getQuoteHeader().isPAQuote();
        boolean isFCTQuote = quote.getQuoteHeader().isFCTQuote() ;
        String contractNum = quote.getQuoteHeader().getContractNum();
        
        Map<String, Boolean> placeHolderFlagMap = new HashMap<String, Boolean>();
        Boolean placeHolderFlag;
        
        for (int i = 0; i < lineItemList.size(); i++) {
            Object obj = lineItemList.get(i);
            if (!(obj instanceof QuoteLineItem)) {
				continue;
			}

            QuoteLineItem qtLineItem = (QuoteLineItem) obj;
            QuoteCreateLineItem qcLineItem = new QuoteCreateLineItem();

            Double unitPrice = qtLineItem.getLocalUnitProratedDiscPrc();
            Double totalPrice = qtLineItem.getLocalExtProratedDiscPrc();

            if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(quote.getQuoteHeader().getFulfillmentSrc())) {
                unitPrice = qtLineItem.getChannelUnitPrice();
                totalPrice = qtLineItem.getChannelExtndPrice();
            }

            //qcLineItem.setItemNumber(qtLineItem.getSeqNum());
            qcLineItem.setItemNumber(qtLineItem.getDestSeqNum());
            qcLineItem.setReplacedReasonCode(qtLineItem.getReplacedReasonCode());
            qcLineItem.setAddReasonCode(qtLineItem.getAddReasonCode());

            if (!StringUtils.isBlank(qtLineItem.getRenewalQuoteNum())) {
                qcLineItem.setReferenceQuoteType(QUOTE_ITEM_REF_DOC_TYPE_RQ_PARTS);
                qcLineItem.setReferenceNumber(qtLineItem.getRenewalQuoteNum());
                qcLineItem.setReferenceQuoteItemNumber(qtLineItem.getRenewalQuoteSeqNum());
            }
            else {
                if ((isPAQuote && contractNum != null && !contractNum.equals("") && PartPriceConstants.PartTypeCode.PACTRCT.equals(qtLineItem.getPartTypeCode()))
                || (isPAQuote  && contractNum != null && !contractNum.equals("") && PartPriceConstants.PartTypeCode.PADOCMED.equals(qtLineItem.getPartTypeCode()))
                || (isFCTQuote && contractNum != null && !contractNum.equals(""))) {
                if (cust != null && !cust.isAddiSiteCustomer()) {
                    qcLineItem.setReferenceQuoteType(QUOTE_ITEM_REF_DOC_TYPE_PS_PARTS);
                    qcLineItem.setReferenceNumber(StringUtils.trimToEmpty(contractNum));
                }
                qcLineItem.setReferenceQuoteItemNumber(0);
            }
            }

            qcLineItem.setPartNumber(qtLineItem.getPartNum());
            qcLineItem.setQuantity(CommonServiceUtil.getPartQty(qtLineItem));
	        qcLineItem.setItemStartDate(DateHelper.getDateByFormat(qtLineItem.getMaintStartDate(), SAP_DATE_FORMAT));
	        qcLineItem.setItemEndDate(DateHelper.getDateByFormat(qtLineItem.getMaintEndDate(), SAP_DATE_FORMAT));
            qcLineItem.setCurrencyCode(quote.getQuoteHeader().getCurrencyCode());
            qcLineItem.setUnitPrice(unitPrice);
            qcLineItem.setRequestedArrivalDate(DateHelper.getDateByFormat(qtLineItem.getLineItemCRAD(), SAP_DATE_FORMAT));

            qcLineItem.setTaxValue(new Double(0.0));
            qcLineItem.setTotalPrice(totalPrice);
            if (StringUtils.isNotBlank(qtLineItem.getPVUOverrideQtyIndCode())) {
				qcLineItem.setVuQtyOverrideCode(qtLineItem.getPVUOverrideQtyIndCode());
			}

            if (qtLineItem.getOvrrdExtPrice() != null){
            	qcLineItem.setOverrideExtPrice(qtLineItem.getOvrrdExtPrice());
            } else if (qtLineItem.getOverrideUnitPrc() != null){
            	if(!CommonServiceUtil.checkIsUsagePart(qtLineItem)){//for usage part(on demand, subscription overage, setup overage, daily part) special bid pricing will be sent to SAP in SPBD_OVERAGE
            		logContext.debug(this, qtLineItem.getPartNum() + " is not usage part, will populdate OverrideUnitPrice field.");
            		qcLineItem.setOverrideUnitPrice(qtLineItem.getOverrideUnitPrc());
            	}
            } else if (CommonServiceUtil.isValidDiscoutPct(qtLineItem.getLineDiscPct())){
            	qcLineItem.setDiscountPercent(new Double(DecimalUtil.roundAsDouble(qtLineItem.getLineDiscPct(), 3)));
            }

            if (quote.getQuoteHeader().isChannelQuote()) {
	            // if the channal margin is not null and great or equal as zero send to sap.
	            Double chnlOvrrdDiscPct = qtLineItem.getChnlOvrrdDiscPct();
	            if (chnlOvrrdDiscPct != null){
	                double dChnlOvrrdDiscPct = chnlOvrrdDiscPct.doubleValue();
	                if (dChnlOvrrdDiscPct >= 0.0) {
						qcLineItem.setPrtnrDiscPct(new Double(DecimalUtil.roundAsDouble(dChnlOvrrdDiscPct , 3)));
					}
	            } else {
		            // if this quote has new customer with agreement type = Additional site for an existing contract
		            // Set the BP override discount to be exactly the same as the standard BP discount
		            if (quote.getCustomer().isAddiSiteCustomer()) {
		            	Double chnlStdDiscPct = qtLineItem.getChnlStdDiscPct();
			            if (chnlStdDiscPct != null){
			                double dChnlStdDiscPct = chnlStdDiscPct.doubleValue();
			                if (dChnlStdDiscPct >= 0.0) {
								qcLineItem.setPrtnrDiscPct(new Double(DecimalUtil.roundAsDouble(dChnlStdDiscPct , 3)));
							}
			            }
		            }
	            }
            }

            qcLineItem.setBackDateFlag(Boolean.valueOf(qtLineItem.getBackDatingFlag()));

            qcLineItem.setPartType(StringUtils.trimToEmpty(CommonServiceUtil.correctLineItemPartType(qtLineItem)));

            // for SaaS ramp up scenario,reuse this field

			if (qtLineItem.getIRelatedLineItmNum() >= 0) {
				//13.4 Kenexa . send related subsumedscription lineItem Num for set up part
				if (qtLineItem.isSaasSetUpPart()){
					qcLineItem.setRelatedItemNumber(qtLineItem.getIRelatedLineItmNum());
				}
				
				else if (!qtLineItem.isReplacedPart()) {
					qcLineItem.setRelatedItemNumber(new Integer(qtLineItem.getIRelatedLineItmNum()));
				}
			}

			qcLineItem.setStartDateOverrideFlag(BooleanUtils
					.toBooleanObject(qtLineItem.getStartDtOvrrdFlg()));

            qcLineItem.setEndDateOverrideFlag(BooleanUtils.toBooleanObject(qtLineItem.getEndDtOvrrdFlg()|| qtLineItem.getProrateFlag()));

           if(qtLineItem.isSaasPart()){
                logContext.debug(this, qtLineItem.getPartNum() + " is SaaS part.");
                if (qtLineItem.isSaasDaily()) {
                    qcLineItem.setItemStartDate(DateHelper.getDateByFormat(null, SAP_DATE_FORMAT));
                    qcLineItem.setItemEndDate(DateHelper.getDateByFormat(null, SAP_DATE_FORMAT));
                }

               qcLineItem.setBillFreq(qtLineItem.getBillgFrqncyCode());

               //per quote create RFC spec, only send SPBD_OVERAGE for Saas overage parts (usage part but not daily part)
               //<usage-part-types><part-type value="ON_DMND,SUBSCRPTN_OVRAGE,SETUP_OVRAGE,DLY"/></usage-part-types>
               if(CommonServiceUtil.checkIsUsagePart(qtLineItem) && !qtLineItem.isSaasDaily()){

            	   logContext.debug(this, qtLineItem.getPartNum() + " is not daily part, will populdate SPBD_OVERAGE field.");

            	   //please see WI:176789#19
            	   //Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/A4D84527BE0538C28525797D00466776
                   if (qtLineItem.getOverrideUnitPrc() != null) {
                	   qcLineItem.setSpbdOveragePrc(qtLineItem.getLocalUnitProratedDiscPrc());
                   }

                   //fix PL JKEY-8PRNXG, RTC task 176270
                   //Per Joanne, if the part is an overage part AND the replacement flag is set - do not populate the SPBD_OVERAGE value during quote create.
//            	   if(qtLineItem.isReplacedPart()){
//            		   if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(quote.getQuoteHeader().getFulfillmentSrc())) {
//            			   if(qtLineItem.isSaasSetUpOvragePart() || qtLineItem.isSaasSubscrptnOvragePart()||qtLineItem.isSaasOnDemand()){
//            				   qcLineItem.setSpbdOveragePrc(qtLineItem.getChannelUnitPrice());
//            			   }
//            		   }
//            	   }

               }

               qcLineItem.setItemTerm(qtLineItem.getICvrageTerm());

               /*
                * Logic for populating the PAYMENT_PLAN field in the quote create RFC call is below:
					1) Leave payment plan field blank if part is not a SaaS setup part.  (leave blank for Software parts too)
					2) If billing option for SaaS setup part is "upfront" - leave it blank
					3) If billing option for SaaS setup part is not "upfront" - send Y.
                */
               if (qtLineItem.isSaasSetUpPart()
						&& !QuoteConstants.SaaSBillFreq.UPFRONT
								.equalsIgnoreCase(qtLineItem
										.getBillgFrqncyCode())) {
					qcLineItem.setPaymentPlan(Boolean.TRUE);
				}

			   if (qtLineItem.isReplacedPart()) {
					qcLineItem.setReferenceQuoteType(QUOTE_ITEM_REF_DOC_TYPE_RL_PARTS);
					qcLineItem.setReferenceNumber(quote.getQuoteHeader().getRefDocNum());
					qcLineItem.setReferenceQuoteItemNumber(qtLineItem.getRefDocLineNum());
				}

			   qcLineItem.setConfigId(qtLineItem.getConfigrtnId());
               qcLineItem.setItemReplacedFlag(qtLineItem.isReplacedPart());
               if (!qtLineItem.isReplacedPart()) {
            	   qcLineItem.setOriginalItem(qtLineItem.getRefDocLineNum());
               }
               if (!qtLineItem.isSaasSubsumedSubscrptnPart()){
                   qcLineItem.setCotermItem(qtLineItem.getRelatedCotermLineItmNum());
               }
               qcLineItem.setRampUpFlag(qtLineItem.isRampupPart());

               if (qtLineItem.isBeRampuped() || qtLineItem.isRampupPart()) {
            	   qcLineItem.setTotalTerm(qtLineItem.getCumCvrageTerm());
               }
               qcLineItem.setWebMigratedFlag(qtLineItem.isWebMigrtdDoc());

                if (qtLineItem.isSaasSubscrptnPart() || qtLineItem.isSaasSubsumedSubscrptnPart()) {
                    qcLineItem.setRenewType(qtLineItem.getRenwlMdlCode());
               }
               
               //'Y' if this is a Saas setup part with a subsumed subscription OR A Saas subsumed subscription part 
               //'N' or Blank for all others
				if ((qtLineItem.isSaasSetUpPart() && qtLineItem
						.getSetUpRelatedSubsumedPart() != null)
						|| qtLineItem.isSaasSubsumedSubscrptnPart()) {
					qcLineItem.setSubsumedIndicatorFlag(Boolean.TRUE);
				} else {
					qcLineItem.setSubsumedIndicatorFlag(Boolean.FALSE);
				}
           }

           //set Appliance parts
           mapApplianceLineItem(qtLineItem, qcLineItem);
           
           //set monthly software parts
			if (qtLineItem.isMonthlySoftwarePart()) {
				mapMonthlySwLineItem(qtLineItem, qcLineItem, quote);
			}
           
         //set the Non Ibm model and serial number
           if (qtLineItem.isDisplayModelAndSerialNum()) {
				qcLineItem.setNonIbmModel(qtLineItem.getNonIBMModel());
				qcLineItem.setNonIbmSerialNumber(qtLineItem.getNonIBMSerialNumber());
			}

			if (qtLineItem.isSaasPart()) {
				qcLineItem.setEarlyRnwlCompDate(DateHelper.getDateByFormat(qtLineItem.getEarlyRenewalCompDate(), SAP_DATE_FORMAT));
				qcLineItem.setRefCaNum(qtLineItem.getOrignlSalesOrdRefNum());
				qcLineItem.setRefConfigId(qtLineItem.getOrignlConfigrtnId());

				setServiceExtDate(qcLineItem, quote, qtLineItem);
			}

            //DSW 10.6 release, new added renewal counter flag if renewal model code is not C
            if(qtLineItem.isSaasRenwl() && ! PartPriceConstants.RenewalModelCode.C.equals(qtLineItem.getRenwlMdlCode())){
        	   qcLineItem.setRenewalCounter(BooleanUtils.toIntegerObject(qtLineItem.isSaasRenwl()));
            }
            
            //ANDY since 14.2 if CSA quote, set HDR_AGREEMENT_AMENDED
            if(quote.getQuoteHeader().isCSRAQuote() 
            		|| quote.getQuoteHeader().isCSTAQuote()){
            	qcLineItem.setHdrAgrmentAmdFlag(qtLineItem.getHdrAgrmentAmdFlag());
                qcLineItem.setTouUrl(qtLineItem.getTouURL() == null ? "" : qtLineItem.getTouURL());
                qcLineItem.setTouUrlName(qtLineItem.getTouName() == null ? "" : qtLineItem.getTouName());
            }
            //ANDY before 14.2 if SAAS_TERM_COND_FLAG==2,did not send ToU data to SAP
            //if(quote.getQuoteHeader().getSaasTermCondCatFlag() != 2){
            if(quote.getQuoteHeader() != null && quote.getQuoteHeader().shouldSentTouUrltoSAP()){
            	qcLineItem.setAmendedTouFlag(qtLineItem.getAmendedTouFlag());
                qcLineItem.setAmendedTouFlagB(qtLineItem.getAmendedTouFlagB());
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
                	qcLineItem.setAmendedTouFlag(true);
    			}
                qcLineItem.setTouUrl(qtLineItem.getTouURL() == null ? "" : qtLineItem.getTouURL());
                qcLineItem.setTouUrlName(qtLineItem.getTouName() == null ? "" : qtLineItem.getTouName());
            }          
            
            
            logContext.debug(this, "line item " + i + ":");
            logContext.debug(this, "ItemNumber = " + qcLineItem.getItemNumber());
            logContext.debug(this, "ReferenceQuoteType = " + qcLineItem.getReferenceQuoteType());
            logContext.debug(this, "ReferenceNumber = " + qcLineItem.getReferenceNumber());
            logContext.debug(this, "ReferenceQuoteItemNumber = " + qcLineItem.getReferenceQuoteItemNumber());
            logContext.debug(this, "PartNumber = " + qcLineItem.getPartNumber());
            logContext.debug(this, "Quantity = " + qcLineItem.getQuantity());
            logContext.debug(this, "ItemStartDate = " + qcLineItem.getItemStartDate());
            logContext.debug(this, "ItemEndDate = " + qcLineItem.getItemEndDate());
            logContext.debug(this, "CurrencyCode = " + qcLineItem.getCurrencyCode());
            logContext.debug(this, "UnitPrice = " + qcLineItem.getUnitPrice());
            logContext.debug(this, "DiscountPercent = " + qcLineItem.getDiscountPercent());
            logContext.debug(this, "TaxValue = " + qcLineItem.getTaxValue());
            logContext.debug(this, "TotalPrice = " + qcLineItem.getTotalPrice());
            logContext.debug(this, "VuQtyOverrideCode = " + qcLineItem.getVuQtyOverrideCode());
            logContext.debug(this, "OverrideUnitPrice = " + qcLineItem.getOverrideUnitPrice());
            logContext.debug(this, "ChnlOvrrdDiscPct = " + qcLineItem.getPrtnrDiscPct());
            logContext.debug(this, "PartType = " + qcLineItem.getPartType());
            logContext.debug(this, "RelatedItemNumber = " + qcLineItem.getRelatedItemNumber());
            logContext.debug(this, "StartDateOverrideFlag = " + qcLineItem.getStartDateOverrideFlag());
            logContext.debug(this, "EndDateOverrideFlag = " + qcLineItem.getEndDateOverrideFlag());
            logContext.debug(this, "BillFreq = " + qcLineItem.getBillFreq());
            logContext.debug(this, "SpbdOveragePrc = " + qcLineItem.getSpbdOveragePrc());
            logContext.debug(this, "ItemTerm = " + qcLineItem.getItemTerm());
            logContext.debug(this, "PaymentPlan = " + qcLineItem.getPaymentPlan());
            logContext.debug(this, "ConfigId = " + qcLineItem.getConfigId());
            logContext.debug(this, "ReplacedFlag = " + qcLineItem.getItemReplacedFlag());
            logContext.debug(this, "OriginalItem = " + qcLineItem.getOriginalItem());
            logContext.debug(this, "CotermItem = " + qcLineItem.getCotermItem());
            logContext.debug(this, "RampUpFlag = " + qcLineItem.getRampUpFlag());
            logContext.debug(this, "TotalTerm = " + qcLineItem.getTotalTerm());
            logContext.debug(this, "renewalCounter = " + (qcLineItem.getRenewalCounter() == null? "no value" : qcLineItem.getRenewalCounter().intValue()));
            logContext.debug(this, "renewType = " + qcLineItem.getRenewType());
            logContext.debug(this, "amendedFlag = " + qcLineItem.getAmendedTouFlag());
            logContext.debug(this, "amendedFlagB = " + qcLineItem.getAmendedTouFlagB());
            logContext.debug(this, "touUrlName = " + qcLineItem.getTouUrlName());
            logContext.debug(this, "touUrl = " + qcLineItem.getTouUrl());
            qcLineItemList.add(qcLineItem);
        }

        return (QuoteCreateLineItem[]) qcLineItemList.toArray(new QuoteCreateLineItem[0]);
    }

	protected void mapApplianceLineItem(QuoteLineItem qtLineItem, QuoteCreateLineItem qcLineItem) {
		if (qtLineItem.isApplncPart()) {
			if (!QuoteCommonUtil.isShowDatesForApplnc(qtLineItem)) {
				qcLineItem.setItemStartDate(DateHelper.getDateByFormat(null, SAP_DATE_FORMAT));
				qcLineItem.setItemEndDate(DateHelper.getDateByFormat(null, SAP_DATE_FORMAT));
			}
			qcLineItem.setPartType(null);
			qcLineItem.setMachineType(qtLineItem.getMachineType());
			qcLineItem.setMachineModel(qtLineItem.getModel());
			qcLineItem.setSerialNumber(qtLineItem.getSerialNumber());
			qcLineItem.setPocConvFlag(qtLineItem.isApplncPocInd());
			qcLineItem.setPriorPocFlag(qtLineItem.isApplncPriorPoc());
            qcLineItem.setSerialWarningFlag(PartPriceCommon.isShowApplncMTMMsg(qtLineItem));
            if(qtLineItem.isDeploymentAssoaciatePart()){
            	qcLineItem.setDeploymentId(StringUtils.trimToEmpty(qtLineItem.getDeployModel().getDeployModelId()));
            }else if(qtLineItem.isRenewalPart()&& StringUtils.isNotBlank(qtLineItem.getRenewalDeploymtID())){
            	qcLineItem.setDeploymentId(StringUtils.trimToEmpty(qtLineItem.getRenewalDeploymtID()));
            }
			if (!qtLineItem.isHasApplncId()) {
				qcLineItem.setConfigId(null);
			} else {
				qcLineItem.setConfigId(qtLineItem.getConfigrtnId());
			}
			// ownership transfer part is not sent to SAP
			if (qtLineItem.isOwerTransferPart()){
				qcLineItem.setPriorPocFlag(null);
				qcLineItem.setRequestedArrivalDate(DateHelper.getDateByFormat(null, SAP_DATE_FORMAT));
			}
		}
	}

	private void setServiceExtDate(QuoteCreateLineItem qcLineItem, Quote quote, QuoteLineItem qtLineItem) {
		// TODO Saas 10.4 and 10.6 Need do more test to check if those value be set correctly.
        if (!qtLineItem.isSaasSubscrptnPart() && !qtLineItem.isSaasSubsumedSubscrptnPart()) {
            return;
        }
		PartsPricingConfiguration ppc = QuoteCommonUtil.getPartsPricingConfigurationById(qcLineItem.getConfigId(), quote);
		if (ServiceDateModType.RS.equals(ppc.getServiceDateModType())) {
			qcLineItem.setReqServiceStartDate(DateHelper.getDateByFormat(ppc.getServiceDate(), SAP_DATE_FORMAT));
			//qcLineItem.setRelatedExtensionItem(qcLineItem.getRelatedItemNumber());
		} else if (ServiceDateModType.LS.equals(ppc.getServiceDateModType())) {
			qcLineItem.setLatestAccptStartDate(DateHelper.getDateByFormat(ppc.getServiceDate(), SAP_DATE_FORMAT));
			//qcLineItem.setRelatedExtensionItem(qcLineItem.getRelatedItemNumber());
		} else if (ServiceDateModType.CE.equals(ppc.getServiceDateModType())) {
			if (ppc.isTermExtension()){
				qcLineItem.setServiceEndDate(DateHelper.getDateByFormat(ppc.getServiceDate(), SAP_DATE_FORMAT));
			}
			//qcLineItem.setRelatedExtensionItem(qcLineItem.getRelatedItemNumber());
		}



		// 1.The term extension flag should only be set on the subscription line items.
		// 2.The related extension line item number should only be populated on subscription line items and should be the
		//   SAP Line item sequence number.
		if(qtLineItem.isSaasSubscrptnPart() || qtLineItem.isSaasSubsumedSubscrptnPart()){
			if(!qtLineItem.isReplacedPart()){
				//Notes://CAMDB10/85256B890058CBA6/CD76522BA873968E85256D33004FBB0B/B37EAA69070159AF85257B41006462B7
				if((PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode())
						|| PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ppc.getConfigrtnActionCode()))
					&& ServiceDateModType.CE.equals(ppc.getServiceDateModType()) 
					&& ppc.isTermExtension()){					
					qcLineItem.setTermExtensionFlag(true);
				}
				if(ppc.isTermExtension()){
					qcLineItem.setRelatedExtensionItem(qtLineItem.getRelatedCotermLineItmNum());
					qcLineItem.setCotermItem(null);
				}
			}else{
				if(ppc.isTermExtension()){
					if(qtLineItem.getRefDocLineNum() == null){
						qcLineItem.setReferenceQuoteType(null);
						qcLineItem.setReferenceQuoteItemNumber(null);
						qcLineItem.setReferenceNumber(null);
					}
				}
			}
		}
		qcLineItem.setExtEligibilityDate(DateHelper.getDateByFormat(qtLineItem.getExtensionEligibilityDate(), SAP_DATE_FORMAT));
	}

    protected LineItemProcessor[] genLineItemProcessors(Quote quote) {
        if (quote == null || quote.getQuoteHeader() == null
                || quote.getLineItemList() == null) {
			return new LineItemProcessor[0];
		}

        List lineItemList = quote.getLineItemList();
        ArrayList processorList = new ArrayList();

        logContext.debug(this, "Line item processors begin:");

        for (int i = 0; i < lineItemList.size(); i++) {
            Object obj = lineItemList.get(i);
            if (!(obj instanceof QuoteLineItem)) {
				continue;
			}
            QuoteLineItem qtLineItem = (QuoteLineItem) obj;
            List lineItemConfigList = qtLineItem.getLineItemConfigs();

            if (lineItemConfigList == null) {
				continue;
			}

            for (int j = 0; j < lineItemConfigList.size(); j++) {
                QuoteLineItemConfig config = (QuoteLineItemConfig) lineItemConfigList.get(j);
                LineItemProcessor processor = new LineItemProcessor();

                //processor.setItemNumber(config.getQuoteLineItemSecNum());
                processor.setItemNumber(qtLineItem.getDestSeqNum());
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

        return (LineItemProcessor[]) processorList.toArray(new LineItemProcessor[0]);
    }

    protected SpecialBidApprover[] genSpecialBidApprovers(Quote quote) {
        return new SpecialBidApprover[0];
    }
    
    protected void mapMonthlySwLineItem(QuoteLineItem qtLineItem, QuoteCreateLineItem qcLineItem, Quote quote) {
    	if(qtLineItem.isMonthlySoftwarePart()){
            logContext.debug(this, qtLineItem.getPartNum() + " is monthly software part.");
            if (((MonthlySwLineItem)qtLineItem).isMonthlySwDailyPart()) {
                qcLineItem.setItemStartDate(DateHelper.getDateByFormat(null, SAP_DATE_FORMAT));
                qcLineItem.setItemEndDate(DateHelper.getDateByFormat(null, SAP_DATE_FORMAT));
            }

           qcLineItem.setBillFreq(qtLineItem.getBillgFrqncyCode());

           if(CommonServiceUtil.checkIsUsagePart(qtLineItem) && !((MonthlySwLineItem)qtLineItem).isMonthlySwDailyPart()){

        	   logContext.debug(this, qtLineItem.getPartNum() + " is not daily part, will populdate SPBD_OVERAGE field.");

               if (qtLineItem.getOverrideUnitPrc() != null) {
            	   qcLineItem.setSpbdOveragePrc(qtLineItem.getLocalUnitProratedDiscPrc());
               }


           }

           qcLineItem.setItemTerm(qtLineItem.getICvrageTerm());


		   if (qtLineItem.isReplacedPart()) {
				qcLineItem.setReferenceQuoteType(QUOTE_ITEM_REF_DOC_TYPE_RL_PARTS);
				qcLineItem.setReferenceNumber(quote.getQuoteHeader().getRefDocNum());
				qcLineItem.setReferenceQuoteItemNumber(qtLineItem.getRefDocLineNum());
			}

		   qcLineItem.setConfigId(qtLineItem.getConfigrtnId());
           qcLineItem.setItemReplacedFlag(qtLineItem.isReplacedPart());
			if (!qtLineItem.isReplacedPart() && !((MonthlySwLineItem) qtLineItem).isMonthlySwDailyPart()) {
        	   qcLineItem.setOriginalItem(qtLineItem.getRefDocLineNum());
           }
           qcLineItem.setCotermItem(qtLineItem.getRelatedCotermLineItmNum());
			qcLineItem.setRampUpFlag(((MonthlySwLineItem) qtLineItem).isRampUpIndicator4QuoteCreateService());

           if (qtLineItem.isBeRampuped() || qtLineItem.isRampupPart()) {
        	   qcLineItem.setTotalTerm(qtLineItem.getCumCvrageTerm());
           }
           qcLineItem.setWebMigratedFlag(qtLineItem.isWebMigrtdDoc());

            if (((MonthlySwLineItem)qtLineItem).isMonthlySwSubscrptnPart()) {
                qcLineItem.setRenewType(qtLineItem.getRenwlMdlCode());
           }
           
			qcLineItem.setSubsumedIndicatorFlag(Boolean.FALSE);
			

			qcLineItem.setEarlyRnwlCompDate(DateHelper.getDateByFormat(qtLineItem.getEarlyRenewalCompDate(), SAP_DATE_FORMAT));
			qcLineItem.setRefCaNum(qtLineItem.getOrignlSalesOrdRefNum());
			qcLineItem.setRefConfigId(qtLineItem.getOrignlConfigrtnId());

		
			if(qtLineItem.isSaasRenwl() && ! PartPriceConstants.RenewalModelCode.C.equals(qtLineItem.getRenwlMdlCode())){
	        	   qcLineItem.setRenewalCounter(BooleanUtils.toIntegerObject(qtLineItem.isSaasRenwl()));
	            }
    	}
    }
    
}
