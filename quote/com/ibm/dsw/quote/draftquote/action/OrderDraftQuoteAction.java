package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;
import is.service.SOAPServiceException;
import is.servlet.NotATelesalesUserException;
import is.servlet.UserNotFoundException;
import is.webauth.service.request.wrapper.WebAuthServicePrimaryContactWrapperProxyHelper;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.BaseI18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.PriceEngineUnAvailableException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.CountrySignatureRuleFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuickEnrollmentServiceHelper;
import com.ibm.dsw.quote.common.service.QuoteTimestampService;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.DraftQuoteSubmitBaseContract;
import com.ibm.dsw.quote.draftquote.contract.OrderDraftQuoteContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>OrderDraftQuoteAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-16
 */

public class OrderDraftQuoteAction extends DraftQuoteSubmitBaseAction {
    
    protected Map validateQuote(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException {
        
        QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();
        Map validateResult = qcProcess.validateForOrder(user, quote, cookie);

        return validateResult;
    }

    protected String getState() {
        return DraftQuoteStateKeys.STATE_SET_USER_COOKIE;
    }
    
    protected Quote getQuoteDetail(String creatorId,boolean isPGSEnv ) throws NoDataException, QuoteException,
            PriceEngineUnAvailableException {
        Quote quote = super.getQuoteDetail(creatorId,isPGSEnv);
        PartPriceProcess partPriceProcess = PartPriceProcessFactory.singleton().create();
        partPriceProcess.getCurrentPriceForOrder(quote, creatorId);
        
        return quote;
    }
    
    /**
     * @param contract
     * @param handler
     */
    protected void excuteSubmitProcess(ProcessContract contract, ResultHandler handler, Quote quote, User user, List approverGroup)
            throws QuoteException {
        DraftQuoteSubmitBaseContract baseContract = (DraftQuoteSubmitBaseContract) contract;
        
        QuoteHeader header = quote.getQuoteHeader();
        if (header.getLob() == null || StringUtils.isBlank(header.getLob().getCode())) {
            logContext.debug(this, "Line of business is not known.");
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return;
        }
        
        if (quote.getQuoteHeader().isRenewalQuote()) {
            logContext.debug(this, "To call quote timestamp service.");
            try {
                QuoteTimestampService timestampService = new QuoteTimestampService();
                boolean isConsistent = timestampService.execute(header.getRenwlQuoteNum(), header.getSapIntrmdiatDocNum(),
                        header.getRqModDate(), header.getRqStatModDate());
                isConsistent = true;
                if (!isConsistent) {
                    logContext.debug(this, "Renewal quote status inconsistent.");
                    this.handleRnwlQuoteInconsistent(contract, handler);
                    return;
                }
            } catch (RemoteException e1) {
                logContext.error(this, "Call quote timestamp service failed" + e1);
            }
        }
        
        //Get all address details
        logContext.debug(this, "To get the all address details... ");
		try {
			if(quote.getQuoteHeader().isHasAppliancePartFlag()){
				CustomerProcess custProcess = CustomerProcessFactory.singleton().create();
				ApplianceLineItemAddrDetail detail = custProcess.findAddrLineItem(quote.getQuoteHeader().getWebQuoteNum(),"", quote.getLineItemList(), quote.getQuoteHeader().isDisShipInstAdrFlag(), quote.getCustomer()); 
				quote.setApplianceLineItemAddrDetail(detail);
				quote.getQuoteHeader().setInstallAtOpt(detail.getInstallAtOpt());
			}
		} catch (TopazException e1) {
	        logContext.error(this, "NoDataExceptoin accor when geting all address details.");
	        throw new QuoteException(e1);      
		}      
		
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        boolean serviceFailed = false;
        OrderDraftQuoteContract orderContract = (OrderDraftQuoteContract) contract;
        QuoteUserSession salesRep = orderContract.getQuoteUserSession();
        
        int PAOBlockFlag= quote.getQuoteHeader().getPAOBlockFlag();
        //To SaaS parts,if quote has SaaS parts then suppress this to be available to the customer on Passport Advantage Online
        if(quote.getQuoteHeader().hasSaaSLineItem()) {
        	PAOBlockFlag = 1;
        	quote.getQuoteHeader().setPAOBlockFlag(PAOBlockFlag);
        }
        try {
            qProcess.orderDraftQuote(user, salesRep, quote);
            //update PAOBlockFlag to webQuote
            qProcess.updateQuoteSubmission(quote.getQuoteHeader().getCreatorId(), quote.getQuoteHeader().getWebQuoteNum(), quote.getQuoteHeader().getReqstIbmCustNumFlag(),
            		quote.getQuoteHeader().getReqstPreCreditCheckFlag(), quote.getQuoteHeader().getInclTaxFinalQuoteFlag(), quote.getQuoteHeader().getFirmOrdLtrFlag(),
            		quote.getQuoteHeader().getSendQuoteToQuoteCntFlag() , quote.getQuoteHeader().getSendQuoteToPrmryCntFlag(),
            		quote.getQuoteHeader().getSendQuoteToAddtnlCntFlag(), quote.getQuoteHeader().getAddtnlCntEmailAdr(), "",quote.getQuoteHeader().getIncldLineItmDtlQuoteFlg(), 
            		0, quote.getQuoteHeader().getAddtnlPrtnrEmailAdr(), PAOBlockFlag,quote.getCustomer().getSupprsPARegstrnEmailFlag(),
            		"", quote.getQuoteHeader().getFctNonStdTermsConds(), quote.getQuoteHeader().getQuoteOutputType(),String.valueOf(quote.getQuoteHeader().getSoftBidIteratnQtInd()),
            		 String.valueOf(quote.getQuoteHeader().getSaasBidIteratnQtInd()), quote.getQuoteHeader().isSaaSStrmlndApprvlFlag()==true ? 1:0, 
            		quote.getQuoteHeader().getQuoteOutputOption(), quote.getQuoteHeader().getBudgetaryQuoteFlag());
            
            // Update quote stage code and clear session quote
            logContext.debug(this, "To update the quote stage and clear the session quote.");
            qProcess.updateQuoteStageForSubmission(baseContract.getUserId(), quote);
            
			// Quick enrollment CSRA customer that no contract num
			if (quote.getQuoteHeader().isCSRAQuote()
					&& (quote.getQuoteHeader().hasNewCustomer() || quote.getQuoteHeader().getWebCtrctId() > 0)
					&& StringUtils.isEmpty(quote.getQuoteHeader().getContractNum())) {
				this.quickEnrollment(quote);
			}
        } catch (WebServiceException e) {
            logContext.info(this, LogThrowableUtil.getStackTraceContent(e));
            serviceFailed = true;
        }
        
        if (serviceFailed) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
        }
        else {
	        String redirectURL = this.getQuoteOrderURL(quote,salesRep.getAudienceCode());
	        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
	        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
	        
	        this.getPrimaryContact(orderContract, handler, quote, user);
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.DraftQuoteSubmitBaseAction#getValidMsgHead()
     */
    protected String getValidMsgHead(Locale locale) {
        return this.getI18NString(DraftQuoteMessageKeys.ORDER_FAILED_MSG, BaseI18NBundleNames.QUOTE_BASE, locale);
    }
    
    protected void handleRnwlQuoteInconsistent(ProcessContract contract, ResultHandler handler) {
        MessageBean mBean = MessageBeanFactory.create();
        String message = this.getI18NString(DraftQuoteMessageKeys.RNWL_QUOTE_INCONSISTENT_MSG,
                MessageKeys.BUNDLE_APPL_I18N_QUOTE, this.locale);
        mBean.addMessage(message, MessageBeanKeys.INFO);
        
        handler.setMessage(mBean);
        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
    }
    
    protected String getQuoteOrderURL(Quote quote,String audienceCode) {
        String url="";
        if (quote.getQuoteHeader().isRenewalQuote()) {
            url = ApplicationProperties.getInstance().getDraftRenewalQuoteOrderURL(); 
        }else if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(audienceCode)){
        	url = HtmlUtil.getQuoteOrderURLForPGS();
        }else {
            url = ApplicationProperties.getInstance().getDraftSalesQuoteOrderURL();
        }
        
        url = url + quote.getQuoteHeader().getWebQuoteNum();
        logContext.debug(this, "Redirect to "+url);
        
        return url;
    }
    
    protected void getPrimaryContact(OrderDraftQuoteContract orderContract, ResultHandler handler, Quote quote, User user) {
        String custNum = quote.getQuoteHeader().getSoldToCustNum();
        String sapCtrctNum = quote.getQuoteHeader().getContractNum();
		
		if ((user != null) && (user.isTelesalesUser())) {
		    
			logContext.debug(this, "custNum= " + custNum);
			logContext.debug(this, "sapCtrctNum= " + sapCtrctNum);
		
			WebAuthServicePrimaryContactWrapperProxyHelper pcWrapper = new WebAuthServicePrimaryContactWrapperProxyHelper();
			try {
			    String appName = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter("webauth.appname.for.sqo.prcbk");
				if(StringUtils.isBlank(sapCtrctNum)) {
				    user = pcWrapper.getPrimaryContact(appName, user, custNum);
				} else {
				    user = pcWrapper.getPrimaryContact(appName, user, custNum, sapCtrctNum);
				}
				logContext.debug(this,"Updated user= " + user.toString());
			} catch (SOAPServiceException e) {
			    logContext.error(this, e, "updateWebAuthCookie - problem with webservice or invalid webauthUser -- data issue");
			} catch( UserNotFoundException e ) {
			    logContext.error(this, e, "updateWebAuthCookie - user wasn't processed correctly");
			} catch( RemoteException e ) {
			    logContext.error(this, e, "updateWebAuthCookie - error reaching webservice or internal exception in ws transport");
			} catch( NotATelesalesUserException e )	{
			    logContext.error(this, e, "updateWebAuthCookie - per pricebook user is teleasales, per web-auth user isn't a telesales user");
			} catch (UnsupportedEncodingException e) {
				logContext.error(this, e, "updateWebAuthCookie - got unsupported encoding in WebAuth ServicePrimary Contact calling");
			}
						
		}
		// return to a new page to set cookie then redirect to software online.
		handler.addObject(ParamKeys.PARAM_USER_OBJECT, user);
    }

	/**
	 * To call SAP RFC to enrollment this  customer in SAP 
	 * 
	 * @param quote
	 * @throws QuoteException
	 * @throws WebServiceFailureException
	 */
	protected void quickEnrollment(Quote quote) throws QuoteException, WebServiceFailureException {
		logContext.debug(this, "To call SAP RFC to enrollment this new customer in SAP:..." + quote.getCustomer().getCustName());
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		String enrllmtNum = quoteProcess.getWebEnrollmentNum();
		boolean isRequireSignature = CountrySignatureRuleFactory.singleton().isRequireSignature(
				quote.getCustomer().getCountryCode());
		try {
			QuickEnrollmentServiceHelper service = new QuickEnrollmentServiceHelper();
			boolean isSuccessfull = service.callQuickEnrollmentService(quote, isRequireSignature, enrllmtNum);
			if (isSuccessfull) {
				quoteProcess.updateSapCtrctNum(quote.getQuoteHeader().getWebQuoteNum(), quote.getQuoteHeader().getContractNum());
			}
		} catch (WebServiceException e) {
			throw new WebServiceFailureException("Failed to call SAP to quick enroll the new custoemr.",
					MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, e);
		}
	}

}
