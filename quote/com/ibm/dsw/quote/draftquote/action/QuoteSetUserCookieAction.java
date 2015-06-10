/*
 * Created on Mar 29, 2007
 */
package com.ibm.dsw.quote.draftquote.action;

import is.service.SOAPServiceException;
import is.servlet.NotATelesalesUserException;
import is.servlet.UserNotFoundException;
import is.webauth.service.request.wrapper.WebAuthServicePrimaryContactWrapperProxyHelper;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.BaseI18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.common.domain.CountrySignatureRuleFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.ValidationMessageFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuickEnrollmentServiceHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.QuoteSetUserCookieContract;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author Lavanya
 */
public class QuoteSetUserCookieAction extends BaseContractActionHandler {
    
    public static final String VIEW_CUST_SW_ONLINE = "0";
    public static final String ORDER_SUBMITTED_SALES_QUOTE = "1";
    public static final String ORDER_SUBMITTED_RENEWAL_QUOTE = "2";
    public static final String TERMINATION_EMAIL_DB = "3";
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        QuoteSetUserCookieContract setUserCookieContract = (QuoteSetUserCookieContract) contract;
        String custNum = setUserCookieContract.getSiteNumber();
        String sapCtrctNum = setUserCookieContract.getAgreementNumber();
		is.domainx.User user = setUserCookieContract.getUser(); //getting from session
		
		String dest = setUserCookieContract.getDest();
		if (ORDER_SUBMITTED_SALES_QUOTE.equalsIgnoreCase(dest) || ORDER_SUBMITTED_RENEWAL_QUOTE.equalsIgnoreCase(dest)){
			QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
			QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();
	        Quote quote;
			try {
				quote = qProcess.getDraftQuoteBaseInfoByQuoteNum(setUserCookieContract.getWebQuoteNum());
				if (null != quote){
					Map validResult = qcProcess.validateTouForOrder(setUserCookieContract.getQuoteUserSession(), quote, setUserCookieContract.getSqoCookie());
					if (validResult != null && validResult.size() > 0){
						String missParts = (String) validResult.get(QuoteCapabilityProcess.SUBMIT_PART_LOST_TOU_MSG);
						if (!StringUtils.isBlank(missParts)) {
							validResult.put(QuoteCapabilityProcess.ORDER_PART_LOST_TOU_MSG, missParts);
						}
						
						validResult.remove(QuoteCapabilityProcess.SUBMIT_PART_LOST_TOU_MSG);
		                return handleValidateResult(validResult, handler, setUserCookieContract.getLocale());
		            }
					// Quick enrollment CSRA customer that no contract num
					if (quote.getQuoteHeader().isCSRAQuote()
							&& (quote.getQuoteHeader().hasNewCustomer() || quote.getQuoteHeader().getWebCtrctId() > 0)
							&& StringUtils.isEmpty(quote.getQuoteHeader().getContractNum())) {
						this.quickEnrollment(quote);
					}
				}
			} catch (NoDataException e) {
				logContext.error(this, e.getMessage());
			} catch (WebServiceFailureException e) {
				logContext.error(this, e.getMessage());
				e.printStackTrace();
			}
		}
		
		if ((user != null) && (user.isTelesalesUser())) {
		    
			logContext.debug(this,"custNum= " + custNum);
			logContext.debug(this,"sapCtrctNum= " + sapCtrctNum);
		
			WebAuthServicePrimaryContactWrapperProxyHelper pcWrapper = new WebAuthServicePrimaryContactWrapperProxyHelper();
			try {
			    String appName = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter("webauth.appname.for.sqo.prcbk");
				if(sapCtrctNum == null) {
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
		String redirectURL = generateRedirectURL(setUserCookieContract);
		logContext.debug(this, "This action will be redirected to " + redirectURL);
		handler.addObject(ParamKeys.PARAM_USER_OBJECT, user);
		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
		handler.setState(DraftQuoteStateKeys.STATE_SET_USER_COOKIE);
        return handler.getResultBean();
    }

    /**
     * @param contract
     * @return
     */
    private String generateRedirectURL(QuoteSetUserCookieContract contract) {
        //There will be a switch to handle different button
        String dest = contract.getDest();
        String redirectURL = null;

        if (VIEW_CUST_SW_ONLINE.equalsIgnoreCase(dest)) {
            redirectURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                    ApplicationProperties.VIEW_CUST_SW_ONLINE_URL);
        } else if (ORDER_SUBMITTED_SALES_QUOTE.equalsIgnoreCase(dest)) {
        	
        	//pass SAP quote number for PGS non immediate order 
        	if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(contract.getQuoteUserSession().getAudienceCode())){
        		redirectURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                        ApplicationProperties.SUBMITTED_SALES_QUOTE_ORDER_URL)
                        + contract.getQuoteNum();
        	}else{
        		redirectURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                        ApplicationProperties.SUBMITTED_SALES_QUOTE_ORDER_URL)
                        + contract.getWebQuoteNum();
        	}
            
        } else if (ORDER_SUBMITTED_RENEWAL_QUOTE.equalsIgnoreCase(dest)) {
            redirectURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                    ApplicationProperties.SUBMITTED_RENEWAL_QUOTE_ORDER_URL)
                    + contract.getRenewalQuoteNum();
        } else if (TERMINATION_EMAIL_DB.equalsIgnoreCase(dest)) {
            redirectURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                    ApplicationProperties.TERMINATION_EMAIL_URL)
                    + contract.getRenewalQuoteNum();
        } 
        
        return redirectURL;
    }
    
    protected ResultBean handleValidateResult(Map validResult, ResultHandler handler, Locale locale) {
        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        
        String msgHead = getValidMsgHead(locale);
        messageBean.addMessage(msgHead, MessageKeys.LAYER_MSG_HEAD);

        Set keySet = validResult.keySet();
        Iterator iter = keySet.iterator();
        
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = validResult.get(key);
            String param = null;
            
            // if the value is a string, it will be considered as the message parameter.
            if (value != null && value instanceof String) {
                param = (String) value;
            }

            String message = ValidationMessageFactory.singleton().getValidationMessage(key, locale, param);
            messageBean.addMessage(message, MessageKeys.LAYER_MSG_ITEM);
        }
        
        return resultBean;
    }

	/**
	 * To call SAP RFC to enrollment this new customer in SAP
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

    
    protected String getValidMsgHead(Locale locale) {
        return this.getI18NString(DraftQuoteMessageKeys.ORDER_FAILED_MSG, BaseI18NBundleNames.QUOTE_BASE, locale);
    }
}
