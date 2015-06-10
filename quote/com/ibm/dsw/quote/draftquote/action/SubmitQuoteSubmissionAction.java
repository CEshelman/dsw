package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.CustomerCreateWebServiceFailureException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.UncheckedException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.util.ValidationHelper;
import com.ibm.dsw.quote.common.config.QuoteMessageKeys;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.CountrySignatureRuleFactory;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SpecialBidApprvrOjbect;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.CustomerCreateService;
import com.ibm.dsw.quote.common.service.QuickEnrollmentServiceHelper;
import com.ibm.dsw.quote.common.service.QuoteTimestampService;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteViewKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.contract.SubmitQuoteSubmissionContract;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcess;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory;
import com.ibm.dsw.quote.mail.process.MailProcess;
import com.ibm.dsw.quote.mail.process.MailProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
//import com.ibm.ead4j.jade.config.MessageKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmitQuoteSubmissionAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 17, 2007
 */

public class SubmitQuoteSubmissionAction extends BaseContractActionHandler {
    
    private static final long serialVersionUID = 914938662411331016L;
	public static final int MAX_CUST_ADDI_EMAIL_LENGTH = 80;

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
	
	private boolean validateFCTNonStdTC(SubmitQuoteSubmissionContract ct, Map vMap){
        QuoteHeader header = null;
        try{
        	QuoteProcess process = QuoteProcessFactory.singleton().create();
        	header = process.getQuoteHdrInfo(ct.getUserId());
        	
        } catch(Exception te){
        	//The exception should not happen, just in case
        	LogContextFactory.singleton().getLogContext().error(this, te.getMessage());
        	throw new UncheckedException();
        }
        
		boolean needFlag = ButtonDisplayRuleFactory.singleton().isDisplayFCTNonStdTermsConds(header);
		
		if(needFlag){
			if(StringUtils.isBlank(ct.getFctNonStdTermsConds())){
		        FieldResult fieldResult = new FieldResult();
                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.FCT_NON_STD_TERMS_CONDS_NOT_SELECTED);
                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, QuoteMessageKeys.FCT_NON_STD_TERMS_CONDS_HEADER);
		        vMap.put(DraftQuoteParamKeys.PARAM_FCT_NON_STD_TERMS_CONDS, fieldResult);
		        
				return false;
				
			} else {
				try{
					int temp = Integer.parseInt(ct.getFctNonStdTermsConds());
					
					//Must attach files
					if(QuoteConstants.FCTNonStdTermsConds.YES == temp){
				        QuoteProcess process = QuoteProcessFactory.singleton().create();
				        List fctNonStdTcAttachmentsList=process.getFctNonStdTcAttachments(header);
				        if(fctNonStdTcAttachmentsList==null||fctNonStdTcAttachmentsList.size()==0){
							FieldResult fieldResult = new FieldResult();
			                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.FCT_NON_STD_TERMS_CONDS_MUST_ATTACH_FILE);
			                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, QuoteMessageKeys.FCT_NON_STD_TERMS_CONDS_HEADER);
			                vMap.put(DraftQuoteParamKeys.PARAM_FCT_NON_STD_TERMS_CONDS, fieldResult);
			                return false;
				        }
						return true;
					} else {
						return true;
					}
				}catch(Exception e){
					//not a valid value, return error message
					FieldResult fieldResult = new FieldResult();
	                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.FCT_NON_STD_TERMS_CONDS_NOT_SELECTED);
	                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, QuoteMessageKeys.FCT_NON_STD_TERMS_CONDS_HEADER);
	                vMap.put(DraftQuoteParamKeys.PARAM_FCT_NON_STD_TERMS_CONDS, fieldResult);
	                
					return false;
				}
			}
		} else {
			return true;
		}
	}
	
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        SubmitQuoteSubmissionContract submitQuoteSubmissionCrt = (SubmitQuoteSubmissionContract)contract;
        int chkReqCustNo = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getChkReqCustNo()) ? 1 : 0;
        int chkReqCredChk = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getChkReqCredChk()) ? 1 : 0;
        int chkNoTax = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getChkNoTax()) ? 0 : 1;
        int chkIncFOL = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getChkFirmOrderLetter()) ? 1 : 0;
        int chkEmailQt = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getChkEmailQt()) ? 1 : 0; 
        int chkEmailQtPC = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getChkEmailQtPC()) ? 1 : 0;
        int chkEmailAddr = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getChkEmailAddr()) ? 1 : 0;
        int incldLineItmDtlQuoteFlg = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt
                .getChkNoPrcOrPoints()) ? 0 : 1;
        int quoteOutputTypeFlg = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt
                .getQuoteOutputTypeFlag()) ? 1 : 0;
        int quoteOutputOptionFlag = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getQuoteOutputOption()) ? 1 : 0;
        int budgetaryQuoteFlag = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getBudgetaryQuoteFlag()) ? 1 : 0;
        String saasBidIteratnQtInd = submitQuoteSubmissionCrt.getSaasBidIteratnQtInd();    
        String softBidIteratnQtInd = submitQuoteSubmissionCrt.getSoftBidIteratnQtInd();
        boolean noApprovalRequire = "true".equals(submitQuoteSubmissionCrt.getNoApprovalRequire());
        
        String quoteOutputType = quoteOutputTypeFlg == 1?"RATE":"TCV";
        //quoteOutputOption is payment schedule checkbox
        String quoteOutputOption = quoteOutputOptionFlag == 1?QuoteConstants.PAYMENT_SCHEDULE_CHECKBOX_VALUE:null;
        String addiCustEmail = chkEmailAddr == 0 ? null : submitQuoteSubmissionCrt.getCustEmailAddr();
        
        int chkEmailY9PartnerAddrList;
        if ( StringUtils.isBlank( submitQuoteSubmissionCrt.getChkEmailY9PartnerAddrList() ) )
        	chkEmailY9PartnerAddrList = 0;
        else 
        	chkEmailY9PartnerAddrList = submitQuoteSubmissionCrt.getChkEmailY9PartnerAddrList().equalsIgnoreCase("1") ? 1 : 0;
        
        int chkPAOBlock;
        if ( StringUtils.isBlank( submitQuoteSubmissionCrt.getChkPAOBlock() ) )
        	chkPAOBlock = 0;
        else 
        	chkPAOBlock = submitQuoteSubmissionCrt.getChkPAOBlock().equalsIgnoreCase("1") ? 1 : 0;
        
        int supprsPARegstrnEmailFlag;
        if ( StringUtils.isBlank( submitQuoteSubmissionCrt.getSupprsPARegstrnEmailFlag() ) )
            supprsPARegstrnEmailFlag = 0;
        else 
            supprsPARegstrnEmailFlag = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getSupprsPARegstrnEmailFlag()) ? 1 : 0;
        
        int chkEmailAddiPartnerAddr = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getChkEmailAddiPartnerAddr()) ? 1 : 0;
        String custEmailAddiPartnerAddr = submitQuoteSubmissionCrt.getCustEmailAddiPartnerAddr();
        if(chkEmailAddiPartnerAddr == 0) {
            custEmailAddiPartnerAddr = null;
        }
        
        if (submitQuoteSubmissionCrt.isHttpGETRequest()) {
            return this.handleInvalidHttpRequest(submitQuoteSubmissionCrt, handler);
        }
        
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        
        Quote quote = null;
        
        try {
            quote = quoteProcess.getDraftQuoteBaseInfo(submitQuoteSubmissionCrt.getUserId());
            quote.setPgsAppl(submitQuoteSubmissionCrt.isPGSEnv());
            if(StringUtils.isNotBlank(saasBidIteratnQtInd)){
            	quote.getQuoteHeader().setSaasBidIteratnQtInd(Integer.parseInt(saasBidIteratnQtInd));
            }
            if(StringUtils.isNotBlank(softBidIteratnQtInd)){
            	quote.getQuoteHeader().setSoftBidIteratnQtInd(Integer.parseInt(softBidIteratnQtInd));
            }
            quote.getQuoteHeader().setNoApprovalRequire(noApprovalRequire);
        } catch (NoDataException e) {
            logContext.error(this, "NoDataExceptoin accor when geting quote base info.");
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);     
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            throw e;
        }
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        boolean isSubmittedByEvaluator = false;
        if ( quote.getQuoteHeader().isPGSQuote() 
        		&& QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equalsIgnoreCase(submitQuoteSubmissionCrt.getQuoteUserSession().getAudienceCode()) 
        		&& quoteHeader.isAcceptedByEval() 
        		&& (quoteHeader.isAssignedEval(submitQuoteSubmissionCrt.getQuoteUserSession().getUserId()) || quoteHeader.isQuoteCntryEvaluator()) )
        {
        	quoteHeader.setSubmitByEvaluator(true);
        	isSubmittedByEvaluator = true;
        }
        
        int previousValue = quote.getCustomer().getSupprsPARegstrnEmailFlag();
        
        String preCreditCheckedQuoteNum = submitQuoteSubmissionCrt.getPreCreditCheckedQuoteNum() == null 
                            ? "" : submitQuoteSubmissionCrt.getPreCreditCheckedQuoteNum(); 
        
        //To SaaS parts,if quote has SaaS parts then suppress this to be available to the customer on Passport Advantage Online
		// 14.2 Monthly software (Do not allow a direct quote containing monthly
		// software to be orderable via PAO -hide the question seen below(same
		// logic as Saas to prevent monthly software))
		if (quote.getQuoteHeader().hasSaaSLineItem() || quote.getQuoteHeader().isHasMonthlySoftPart()) {
        	chkPAOBlock = 1;
        }
        Integer saaSStrmlndApprvlFlag = null;
    	if("true".equals(submitQuoteSubmissionCrt.getSaaSStrmlndApprvlFlag())){
    		saaSStrmlndApprvlFlag = new Integer(1);
    	}else{
    		saaSStrmlndApprvlFlag = new Integer(0);	
    	}
        
        //To post the submission screen to db2
        logContext.debug(this, "To post the submission screen to db2...");
        
        Integer fctNonStdTermsConds = submitQuoteSubmissionCrt.getFctNonStdTermsConds() == null? null : new Integer(submitQuoteSubmissionCrt.getFctNonStdTermsConds());
        quoteProcess.updateQuoteSubmission(submitQuoteSubmissionCrt.getUserId(), null, chkReqCustNo, chkReqCredChk,
                chkNoTax, chkIncFOL, chkEmailQt, chkEmailQtPC, chkEmailAddr, addiCustEmail, submitQuoteSubmissionCrt.getCustomMsg(),
                incldLineItmDtlQuoteFlg, chkEmailY9PartnerAddrList, custEmailAddiPartnerAddr, chkPAOBlock, supprsPARegstrnEmailFlag,
                preCreditCheckedQuoteNum, fctNonStdTermsConds, quoteOutputType, softBidIteratnQtInd, saasBidIteratnQtInd,saaSStrmlndApprvlFlag,quoteOutputOption,budgetaryQuoteFlag);
        
        //update contract infor
        quoteProcess.updateQuoteContactInsubmit(contract,quoteHeader);
        
        //To get quote details
        logContext.debug(this, "To get the quote details... ");
        
        try {
            quote = quoteProcess.getDraftQuoteDetailsForSubmit(submitQuoteSubmissionCrt.getUserId(), isSubmittedByEvaluator);
            quote.getQuoteHeader().setNoApprovalRequire(noApprovalRequire);
            quote.getQuoteHeader().setSubmitByEvaluator(isSubmittedByEvaluator);
        } catch (NoDataException e) {
            logContext.error(this, "NoDataExceptoin accor when geting quote details.");
            throw new QuoteException(e);       
        }
        
        boolean isNewSoldToCustomer = quote.getQuoteHeader().hasNewCustomer() && !quote.getQuoteHeader().hasExistingCustomer();
        boolean isNewSSPEndUserCustomer = quote.getQuoteHeader().getEndUserWebCustId() > 0 && StringUtils.isBlank(quote.getQuoteHeader().getEndUserCustNum());
        
        boolean allAreRdyToOrder = false;
        int routingFlagBeforeGridCheck = quote.getQuoteHeader().getApprovalRouteFlag();
        try {
            
            //If it's renewal quote, check the status
            try {
                if(quote.getQuoteHeader().isRenewalQuote()){
                    logContext.debug(this, "To call status check RFC to make sure the quote has not changed in SAP...");
                    QuoteTimestampService timestampService = new QuoteTimestampService();
                    boolean isConsistent = timestampService.execute(quote.getQuoteHeader().getRenwlQuoteNum(), quote
                            .getQuoteHeader().getSapIntrmdiatDocNum(), quote.getQuoteHeader().getRqModDate(), quote
                            .getQuoteHeader().getRqStatModDate());
                    if(!isConsistent){
                        logContext.error(this, "Not consistent, the quote status has be changed in SAP.");
                        throw new WebServiceFailureException("The quote status has be changed in SAP.", MessageKeys.MSG_QUOTE_TIMESTAMP_SERVICE_ERROR, 3);
                    }
                    //from renewal change request
                    if(QuoteConstants.QUOTE_STAGE_CODE_RQCHGREQ.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
                        quoteProcess.completeCustChgReqest(submitQuoteSubmissionCrt.getUserId(), quote.getQuoteHeader().getRenwlQuoteNum());
                    }
                }
            } catch (RemoteException e) {
                throw new WebServiceFailureException("Failed to call SAP to get the quote timestamp.", MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, e);
            } 
            

            //If there is a saved quote for the session quote, copy the session quote to saved quote
            quoteProcess.updateSavedQuoteWithSessionData(submitQuoteSubmissionCrt.getUserId());
            
            //For sales quote, if the quote has an new customer, call SAP to create it.
            try{
            if ( isNewSoldToCustomer )
            {
            	createCustomer(quote, submitQuoteSubmissionCrt.getUserId(), true);
            	
            }
            
            if ( isNewSSPEndUserCustomer )
            {
            	createCustomer(quote, submitQuoteSubmissionCrt.getUserId(), false);
            }
            }catch(CustomerCreateWebServiceFailureException e){
            	
            	logContext.debug(this, "Catch the CustomerCreateWebServiceFailureException in SubmitQuoteSubmissionAction method: executeBiz() -> createCustomer section. !!!!");
            	StringBuffer redirectUrl = new StringBuffer(HtmlUtil.getURLForAction(CustomerActionKeys.DISPLAY_CREATE_CUSTOMER));
            	redirectUrl = HtmlUtil.addURLParam(redirectUrl, ParamKeys.PARAM_COUNTRY, quote.getQuoteHeader().getCountry() == null ? "" : quote.getQuoteHeader().getCountry().getCode3());
            	redirectUrl = HtmlUtil.addURLParam(redirectUrl, ParamKeys.PARAM_LINE_OF_BUSINESS, quote.getQuoteHeader().getLob() == null ? "" : quote.getQuoteHeader().getLob().getCode());
            	redirectUrl = HtmlUtil.addURLParam(redirectUrl, ParamKeys.PARAM_QUOTE_NUM, quote.getQuoteHeader().getWebQuoteNum());
            	redirectUrl = HtmlUtil.addURLParam(redirectUrl, ParamKeys.PARAM_WEBCUST_ID, String.valueOf(quote.getQuoteHeader().getWebCustId()));
            	redirectUrl = HtmlUtil.addURLParam(redirectUrl, ParamKeys.PARAM_COUNTRY_CODE2, quote.getQuoteHeader().getCountry() == null ? "" : quote.getQuoteHeader().getCountry().getCode2());
            	redirectUrl = HtmlUtil.addURLParam(redirectUrl, ParamKeys.PARAM_IS_NEW_CUST, "true");
            	handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectUrl.toString());
            	logContext.debug(this, "quote.getQuoteHeader().getWebQuoteNum(): " + quote.getQuoteHeader().getWebQuoteNum());
            	logContext.debug(this, "quote.getQuoteHeader().getWebCustId(): " + quote.getQuoteHeader().getWebCustId());
            	logContext.debug(this, "quote.getQuoteHeader().getCountry().getCode2(): " + quote.getQuoteHeader().getCountry().getCode2());
            	logContext.debug(this, "redirectUrl.toString(): " + redirectUrl.toString());

                handler.setState(com.ibm.dsw.quote.base.config.StateKeys.STATE_REDIRECT_ACTION);
                handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
                handler.addObject(ParamKeys.PARAM_SAVE_MSG_2_HTTP_REQ_FLAG, Boolean.TRUE);
                 
                ResultBean resultBean = handler.getResultBean();                                   
      	        Locale locale = submitQuoteSubmissionCrt.getLocale();      	        
      	        HashMap vMap = e.getVMap();
      	        HashMap manualMsg = ValidationHelper.getManualValidationErrMsg(vMap, locale);
	            MessageBean validationMessagesBean = MessageBeanFactory.create(manualMsg);
                resultBean.setMessageBean(validationMessagesBean);
                                                       	             	                                  
                return resultBean;
            	
            }
            

            
            //Get all address details
            logContext.debug(this, "To get the all address details... ");
    		try {
    			if(quote.getQuoteHeader().isHasAppliancePartFlag()){
    				CustomerProcess custProcess = CustomerProcessFactory.singleton().create();
    				ApplianceLineItemAddrDetail detail = custProcess.findAddrLineItem(quoteHeader.getWebQuoteNum(),"", quote.getLineItemList(), quote.getQuoteHeader().isDisShipInstAdrFlag(), quote.getCustomer()); 
    				quote.setApplianceLineItemAddrDetail(detail);
    				quote.getQuoteHeader().setInstallAtOpt(detail.getInstallAtOpt());
    			}
    		} catch (TopazException e1) {
    	        logContext.error(this, "NoDataExceptoin accor when geting all address details.");
    	        throw new QuoteException(e1);      
    		}
    		
    		//Quick enrollment new CSRA customer
			// if(quoteHeader.isCSRAQuote()&& quoteHeader.hasNewCustomer()){
			// this.quickEnrollment(quote);
			// }

    		// associated with quoteProcess.updateQuoteContactInsubmit(contract) method , to update quote object infor
            resetPrimaryContact(quote,contract);
            
            allAreRdyToOrder = areAllApproversRdyToOrder(submitQuoteSubmissionCrt.getSpecialBidApprvrs());
            //all approvers are ready to order, update grid delegation flag to be true
            QuoteHeader header = quote.getQuoteHeader();
            header.setGridFlag(allAreRdyToOrder);
    		SpecialBidProcessFactory.singleton().create().updateSpecialBidGridDelegationFlag(header.getWebQuoteNum(), allAreRdyToOrder);
            
            
            this.preCallServiceToCreateQuote(submitQuoteSubmissionCrt,quote);
            
            // add following dummy code to simulate time out scenario to reproduce PL 176264
//          try{
//              logContext.debug(this, "web quote number: " +  quote.getQuoteHeader().getWebQuoteNum() + ", add following dummy code to simulate time out scenario to reproduce PL 176264, sleep 300000ms.");
//              Thread.sleep(60000);
//          }catch(Throwable t){
//          	t.printStackTrace();
//          }
            //Call SAP to create the quote.
            quoteProcess.callServicesToCreateQuote(submitQuoteSubmissionCrt.getUserId(), submitQuoteSubmissionCrt
                    .getUser(), submitQuoteSubmissionCrt.getQuoteUserSession(), quote);
            
        } catch (WebServiceException e) {
        	
        	
            logContext.error(this, "Web service call error!! To update quote stage to SAVED in db2...");
            throw new QuoteException("Web service call error!!", e.getMessageKey(), e);
        }
        
        String quoteStageCode = quote.getQuoteHeader().getQuoteStageCode();
        
        //Update the quote stage
        logContext.debug(this, "To update quote stage to SAPQUOTE or SAPSBQT in db2...");
        quoteProcess.updateQuoteStageForSubmission(submitQuoteSubmissionCrt.getUserId(), quote);
        
        //evaluation submit addition data save
        if ( quoteHeader.isSubmitByEvaluator() )
        {
        	quoteProcess.updateBPQuoteStage(submitQuoteSubmissionCrt.getUserId(), quoteHeader.getWebQuoteNum(), QuoteConstants.QUOTE_STAGE_CODE_SUBMIT_BY_EVAL, 0, 0, quote.getSpecialBidInfo().getEvaltnComment());
        }
        
        handler.addObject(ParamKeys.PARAM_QUOTE_OBJECT, quote);
        handler.addObject(ParamKeys.PARAM_SHOW_EMAIL_MSG_FLAG, false);
        //If special bid, notifies work flow engine of the new special bid
        //For grid, we will still need to use below method to persis approvers
        if(quote.getQuoteHeader().getSpeclBidFlag() == 1 && routingFlagBeforeGridCheck == 1){
            SpecialBidProcess specialBidProcess = SpecialBidProcessFactory.singleton().create();
            //To persister the approver info to db2
            if (submitQuoteSubmissionCrt.getSpecialBidApprvrs() != null
                    && submitQuoteSubmissionCrt.getSpecialBidApprvrs().size() > 0) {
            	
                logContext.debug(this, "To post approver selection");
                if (submitQuoteSubmissionCrt.isPGSEnv()) {
                	specialBidProcess.processSpecialBidApproverSelection(quote.getQuoteHeader().getWebQuoteNum(),
                            submitQuoteSubmissionCrt.getSpecialBidApprvrs(), submitQuoteSubmissionCrt.getUserId(), true);
                } else {
                	specialBidProcess.processSpecialBidApproverSelection(quote.getQuoteHeader().getWebQuoteNum(),
                            submitQuoteSubmissionCrt.getSpecialBidApprvrs(), submitQuoteSubmissionCrt.getUserId(), false);
                }
                
                //To Notify specialBid work flow
                logContext.debug(this, "To Notify specialBid work flow");
                
                //skip special bid email work flow if all approvers are ready to order
        	   try {
                   specialBidProcess.createSpecialBidWorkFlowProcess(quote, submitQuoteSubmissionCrt
                          .getSpecialBidApprvrs(), quote.getSpecialBidInfo().getSpBidJustText(), submitQuoteSubmissionCrt.getUserId(), 
                          submitQuoteSubmissionCrt.getUser().getFirstName() + "," + submitQuoteSubmissionCrt.getUser().getLastName(), submitQuoteSubmissionCrt.isSQOEnv());
                   
                  }catch(QuoteException e){
                  	logContext.error(this, "Error to send special bid approval process email.");
                  	handler.addObject(ParamKeys.PARAM_SHOW_EMAIL_MSG_FLAG, true);
                  }
            } else {
                logContext.debug(this, "There is no any selected approver, will not notify specail bid work flow.");
            }

    	    User user = submitQuoteSubmissionCrt.getUser();
    	    // Send special bid approver email
    	    executeSendApprovalEmail(contract, handler, quote, user);

            logContext.debug(this, "To load the special bid finalization screen");
            
            if(!submitQuoteSubmissionCrt.isPGSEnv() && allAreRdyToOrder){
            	if(!QuoteConstants.QUOTE_STAGE_CODE_RQCHGREQ.equalsIgnoreCase(quoteStageCode)){
            		handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_QUOTE_CONFIRMATION_READY_ORDER);    
            	}else{
            		prepareBack2Reporting(handler, submitQuoteSubmissionCrt, quote.getQuoteHeader().getRenwlQuoteNum());
            	}
            }else{
            	if(!QuoteConstants.QUOTE_STAGE_CODE_RQCHGREQ.equalsIgnoreCase(quoteStageCode)){
            		handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SPECIALBID_FINALIZATION);    
            	}else{
            		prepareBack2Reporting(handler, submitQuoteSubmissionCrt, quote.getQuoteHeader().getRenwlQuoteNum());
            	}
            }
        } else {
            logContext.debug(this, "To load quote submit confirmation screen");
            if(!QuoteConstants.QUOTE_STAGE_CODE_RQCHGREQ.equalsIgnoreCase(quoteStageCode)){
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_QUOTE_CONFIRMATION);
            }else{
                prepareBack2Reporting(handler, submitQuoteSubmissionCrt, quote.getQuoteHeader().getRenwlQuoteNum());
            }
        }
        //For pgs quote, no need do this update
        if(quote.getQuoteHeader().getSpeclBidFlag() == 1 && !quote.getQuoteHeader().isPGSQuote() )
        {
        	boolean isTriggerTNC = quote.getQuoteHeader().isTriggerTC();
            logContext.debug(this, " tnc falg: " + isTriggerTNC);
            try
            {
                logContext.debug(this, "update tnc and clp: " + quote.getQuoteHeader().isTriggerTC());
            SpecialBidProcessFactory.singleton().create().updateSpecialBid(quote.getQuoteHeader().getWebQuoteNum(), 
                    isTriggerTNC ? 1 : 0, 
                    submitQuoteSubmissionCrt.getUserId());
            }
            catch ( Exception e )
            {
                logContext.error(this, "Failed to update tnc and clp flag");
            }
        }
        // add following dummy code to simulate time out scenario to reproduce PL 176264
//        try{
//            logContext.info(this, "web quote number: " +  quote.getQuoteHeader().getWebQuoteNum() + ", add following dummy code to simulate time out scenario to reproduce PL 176264, sleep 300000ms.");
//            Thread.sleep(300000);
//        }catch(Throwable t){
//        	t.printStackTrace();
//        }
        
        //Send the rep submitting the quote an e-mail confirming the customer creation
        // new created quote with new customer: no sold_to_cust_num yet, sold_to_cust_num is updated into web_quote table after customer create
        // copied quote with new customer: the sold_to_cust_num is already copied from original quote
        // ignore the new customer email for PGS quotes
        if(!QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(submitQuoteSubmissionCrt.getQuoteUserSession().getAudienceCode())
        		&& !QuoteConstants.LOB_SSP.equalsIgnoreCase(quote.getQuoteHeader().getLob().getCode())   //add by 14.1 ssp, not send mail for new sold to ssp customer
        		&& quote.getQuoteHeader().hasNewCustomer() && supprsPARegstrnEmailFlag == 0 && !isMailSent(quote.getQuoteHeader(), previousValue, isNewSoldToCustomer)){
            MailProcess mailProcess = MailProcessFactory.singleton().create();
            logContext.debug(this, "Send customer create mail to sales rep: " + submitQuoteSubmissionCrt.getUserId());
           
            try{
            mailProcess.sendCreateCustomer(submitQuoteSubmissionCrt.getUserId(), quote.getQuoteHeader(),
                    submitQuoteSubmissionCrt.getLocale(), quote.getCustomer());
            handler.addObject(ParamKeys.PARAM_PA_REG_EMAIL_SEND, "true");
            
            }catch(QuoteException e){
            	logContext.error(this, "Error to send new customer creation email.");
            	handler.addObject(ParamKeys.PARAM_SHOW_EMAIL_MSG_FLAG, true);
            }
           
        }
        
        return handler.getResultBean();
    }
    
    private void resetPrimaryContact(Quote quote, ProcessContract contract) throws QuoteException {
		List cntList = quote.getContactList();
        if (cntList != null && cntList.size() > 0) {
            QuoteContact cnt = (QuoteContact) cntList.get(0);
			SubmitQuoteSubmissionContract submitQuoteSubmissionCrt = (SubmitQuoteSubmissionContract) contract;
			try {
				cnt.setCntFirstName(submitQuoteSubmissionCrt.getCntFirstName());
				cnt.setCntLastName(submitQuoteSubmissionCrt.getCntLastName());
				cnt.setCntPhoneNumFull(submitQuoteSubmissionCrt.getCntPhoneNumFull());
				cnt.setCntFaxNumFull(submitQuoteSubmissionCrt.getCntFaxNumFull());
				cnt.setCntEmailAdr(submitQuoteSubmissionCrt.getCntEmailAdr());
				cnt.setCntPrtnrFuncCode(submitQuoteSubmissionCrt.getSapCntPrtnrFuncCode());
			} catch (TopazException e) {
				logContext.error(this, "Update quote primary contact infor Exceptoin accor when submitting this quote.");
    	        throw new QuoteException(e); 
			}
			
        }
	}

	protected void createCustomer(Quote quote, String userId, boolean isSoldToCustomer) throws QuoteException, WebServiceFailureException, CustomerCreateWebServiceFailureException
    {
    	logContext.debug(this, "To call SAP RFC to create the new customer in SAP:..." + isSoldToCustomer);
        try {
        	QuoteHeader quoteHeader = quote.getQuoteHeader();
            CustomerCreateService service = new CustomerCreateService();
            boolean isSuccessfull = false;
            if(!isSoldToCustomer){
            	isSuccessfull = service.execute(quote.getEndUser());
            }else{
            	isSuccessfull = service.execute(quote.getCustomer());
            }
            if(isSuccessfull){
            	Customer customer;
            	if(!isSoldToCustomer){
            		customer =  quote.getEndUser();
            		quote.getQuoteHeader().setEndUserCustNum(customer.getCustNum());
            		logContext.info(this, "Successfully create end user : " + customer.getCustNum());
            	}else{
            		customer =  quote.getCustomer();
            		quote.getQuoteHeader().setCustomerNum(customer.getCustNum());
            		logContext.debug(this, "Successfully create customer : " + customer.getCustNum());
            	}
                
                //Save the customer/end user data to db2.
            	QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
                quoteProcess.updateCustomerCreate(quote.getQuoteHeader().getWebQuoteNum(), customer, userId );
            } else {
                logContext.error(this, "Failed to create customer.");
                throw new WebServiceFailureException("Failed to call SAP to create the new custoemr.", MessageKeys.MSG_CREATE_CUATOMER_SERVICE_ERROR, 2);
            }
        } catch (RemoteException e) {
            throw new WebServiceFailureException("Failed to call SAP to create the new custoemr.", MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, e);
        } catch(CustomerCreateWebServiceFailureException e){       	        	              	
        	throw e;
        }
        
        
    }
    
    protected void quickEnrollment(Quote quote) throws QuoteException, WebServiceFailureException{
	    logContext.debug(this, "To call SAP RFC to enrollment this new customer in SAP:..." + quote.getCustomer().getCustName());
    	QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		String enrllmtNum = quoteProcess.getWebEnrollmentNum();
		boolean isRequireSignature = CountrySignatureRuleFactory.singleton().isRequireSignature(quote.getCustomer().getCountryCode()); 
	    try{
	    	QuickEnrollmentServiceHelper service = new QuickEnrollmentServiceHelper();
	    	boolean isSuccessfull = service.callQuickEnrollmentService(quote,isRequireSignature,enrllmtNum);
	    	if(isSuccessfull){
				quoteProcess.updateSapCtrctNum(quote.getCustomer().getCustNum(), quote.getQuoteHeader().getContractNum());
	    	}
	    }catch (WebServiceException e) {
	        throw new WebServiceFailureException("Failed to call SAP to quick enroll the new custoemr.", MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, e);
	    } 
    }
    
    protected void preCallServiceToCreateQuote(SubmitQuoteSubmissionContract submitQuoteSubmissionCrt,Quote quote){
    	//do nothing here for now.
    	//allow subclass to do special thing pre calling SAP.
    }
    
    /**
     * @return
     */
    private boolean isMailSent(QuoteHeader qh, int previousValue, boolean isNewCustomer) {
        
        //new quote with new cutomer
        if(isNewCustomer){
            return false;
        }
        
        //copied quote
        if(qh.hasNewCustomer()){
           //mail already sent 
           if(previousValue == 0){
             return true;   
           }else {
               return false;             
           }           
        }
        return true;
    }

    /**
     * @param handler
     * @param redirectURL
     */
    private void prepareBack2Reporting(ResultHandler handler, SubmitQuoteSubmissionContract submitQuoteSubmissionCrt, String quoteNum) {
        
        String redirectURL = HtmlUtil.getURLForReporting(ActionKeys.CUSTOMER_ACTION_SEARCH_RESULT);
        
        redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), DraftRQParamKeys.PARAM_RPT_SEARCH_CRITERIA_URL_PARAM, submitQuoteSubmissionCrt.getSearchCriteriaUrlParam()).toString();
        
        redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), DraftRQParamKeys.PARAM_RPT_ERROR_FLAG, "0").toString();
        
        redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL), DraftRQParamKeys.PARAM_RPT_QUOTE_NUM, quoteNum).toString();
        
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);        
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
    }

    protected String getValidationForm() {
        return DraftQuoteViewKeys.QUOTE_SUBMISSION_FORM;
    }

    protected boolean validate(ProcessContract contract) {

        boolean valid = super.validate(contract);

        if (valid) {
            HashMap vMap = new HashMap();
	        SubmitQuoteSubmissionContract submitQuoteSubmissionCrt = (SubmitQuoteSubmissionContract)contract;
	        String validatePartnerEmailFields = submitQuoteSubmissionCrt.getValidatePartnerEmailFields();
	        int chkEmailAddr = QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(submitQuoteSubmissionCrt.getChkEmailAddr()) ? 1 : 0;
	        String custEmailAddr = submitQuoteSubmissionCrt.getCustEmailAddr();
	        List approvers = submitQuoteSubmissionCrt.getSpecialBidApprvrs();
            
	        if (approvers != null && approvers.size() > 0) {
                for (int i = 0; i < approvers.size(); i++) {
                    Object apprvr = approvers.get(i);
                    if (apprvr instanceof SpecialBidApprvrOjbect) {
                        SpecialBidApprvrOjbect approver = (SpecialBidApprvrOjbect) apprvr;
                        if (approver.getApprvrGrpName().equalsIgnoreCase("Select1")) {
        		            FieldResult fieldResult = new FieldResult();
        		            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_APPROVER_NOT_SELECTED);
        		            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.SELECT_AN_APPROVER);
        		            vMap.put(DraftQuoteParamKeys.PARAM_APPROVER_LEVEL + approver.getApprvrLevel(), fieldResult);
        		            valid = false;
                        }
                    }
                }
                
            }
            
	        if ("1".equalsIgnoreCase(validatePartnerEmailFields)) {
		        String chkEmailY9PartnerAddrList = submitQuoteSubmissionCrt.getChkEmailY9PartnerAddrList();
		        
		        if (chkEmailY9PartnerAddrList == null) {
		            FieldResult fieldResult = new FieldResult();
		            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_Y9_PARTNER_EMAIL_NOT_SELECTED);
		            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.BUS_PARTNER_NOTIFICATION);
		            vMap.put(DraftQuoteParamKeys.PARAM_CHK_EMAIL_Y9_PARTNER_ADDR_LIST, fieldResult);
		            valid = false;
		        } 
		        
		        String chkEmailAddiPartnerAddr = submitQuoteSubmissionCrt.getChkEmailAddiPartnerAddr();
		        String custEmailAddiPartnerAddr = submitQuoteSubmissionCrt.getCustEmailAddiPartnerAddr();
		        if ( QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(chkEmailAddiPartnerAddr) && StringUtils.isBlank(custEmailAddiPartnerAddr)) {
		            FieldResult fieldResult = new FieldResult();
		            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ADDI_PARTNER_EMAIL_EMPTY);
		            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.BUS_PARTNER_NOTIFICATION);
		            vMap.put(DraftQuoteParamKeys.PARAM_EMAIL_ADDI_PARTNER_ADDR, fieldResult);
		            valid = false;
		        }
	        }
	        
	        if (chkEmailAddr == 1) {
	            boolean needSend = submitQuoteSubmissionCrt.needSendAddiMailToCreator();
	            String creatorEmail = submitQuoteSubmissionCrt.getCreatorEmail();
	            
	            boolean custEmailValid = true;
	            if (!needSend || StringHelper.containsIgnoreCase(custEmailAddr, creatorEmail))
	                custEmailValid = (custEmailAddr.length() <= MAX_CUST_ADDI_EMAIL_LENGTH);
	            else {
	                if (custEmailAddr.endsWith(";"))
	                    custEmailValid = (custEmailAddr.length() + creatorEmail.length() <= MAX_CUST_ADDI_EMAIL_LENGTH);
	                else
	                    custEmailValid = (custEmailAddr.length() + creatorEmail.length() + 1 <= MAX_CUST_ADDI_EMAIL_LENGTH);
	            }
	            
	            if (!custEmailValid) {
		            if (needSend) {
	                    FieldResult fieldResult = new FieldResult();
	                    fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_CUST_ADDI_EMAIL_INVLD);
	                    fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_DRAFT_QUOTE, DraftQuoteViewKeys.MSG_SUBMIT_CHK_UPD_MAIL);
	                    vMap.put(DraftQuoteParamKeys.PARAM_CUST_ADDI_EMAIL_ADDR, fieldResult);
	                    
		            }
		            else {
	                    FieldResult fieldResult = new FieldResult();
	                    fieldResult.setMsg(MessageKeys.BUNDLE_BASE_I18N_VALIDATORMESSAGES, MessageKeys.MSG_MAXLENGTH);
	                    fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_DRAFT_QUOTE, DraftQuoteViewKeys.MSG_SUBMIT_CHK_UPD_MAIL);
	                    fieldResult.addArg(String.valueOf(MAX_CUST_ADDI_EMAIL_LENGTH), false);
	                    vMap.put(DraftQuoteParamKeys.PARAM_CUST_ADDI_EMAIL_ADDR, fieldResult);
		            }
		            valid = false;
	            }
	        }
	        
	        if(!validateFCTNonStdTC(submitQuoteSubmissionCrt, vMap)){
	        	valid = false;
	        }
	        
	        addToValidationDataMap(submitQuoteSubmissionCrt, vMap);
        }
        
        return valid;
    }

    protected void executeSendApprovalEmail(ProcessContract contract, ResultHandler handler, Quote quote, User user)
            throws QuoteException {

		// load approval groups
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		SubmitQuoteSubmissionContract sbmtCtrct = (SubmitQuoteSubmissionContract) contract;
		boolean isNoApprvrs = "true".equalsIgnoreCase(sbmtCtrct.getSendNoApprvrNotif());
		boolean isMultipleGroups = "true".equalsIgnoreCase(sbmtCtrct.getSendMultiGrpsNotif());
		boolean isOneLvlApprvrOnly = "true".equalsIgnoreCase(sbmtCtrct.getSendOneLvlApprvrNotif());
        boolean isPGSQuote = quote.getQuoteHeader().isPGSQuote();
        boolean isSubmittedOnSQO = quote.getQuoteHeader().isSubmittedOnSQO();

		if (isNoApprvrs || isMultipleGroups || isOneLvlApprvrOnly) {
		    logContext.debug(this, "begin to send email");
		    MailProcess mailProcess = MailProcessFactory.singleton().create();
		    
		    String cc = sbmtCtrct.getUserId();
		    List to = CacheProcessFactory.singleton().create().getSpecialBidAdmins(
		            quoteHeader.getCountry().getSpecialBidAreaCode().trim());

		    String quoteNum = quoteHeader.getWebQuoteNum();
		    String custName = quoteHeader.getCustName();
		    String quoteTitle = quoteHeader.getQuoteTitle();
		    
		    String link = HtmlUtil.getQuoteFullUrl(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB)
		            + "&quoteNum=" + quote.getQuoteHeader().getWebQuoteNum();
		    logContext.debug(this, "to list: " + to.toString());
		    logContext.debug(this, "cc list: " + cc);

		    if(isPGSQuote && !isSubmittedOnSQO) {
		    	cc=quote.getQuoteHeader().getSubmitterId();
		    }
             try{
			    if (isNoApprvrs) {
			        mailProcess.sendNoApprovers(to, cc, link, quoteHeader);
			    }
			    if (isMultipleGroups) {
			        mailProcess.sendMultiSameLevelGroups(to, cc, quoteNum, custName, quoteTitle, link);
			    }
			    if (isOneLvlApprvrOnly) {
			        mailProcess.sendOneApproverNotification(to, null, quoteNum, custName, quoteTitle, link);
			    }
             }catch(QuoteException e){
            	logContext.error(this, "Error to send no approver/one level approver only email.");
            	handler.addObject(ParamKeys.PARAM_SHOW_EMAIL_MSG_FLAG, true);
            }
		    logContext.debug(this, "complete sending email");
		}
    }
    
    private boolean areAllApproversRdyToOrder(List approvers){
    	if(approvers == null || approvers.size() == 0){
    		return false;
    	}
        int rdyToOrderCount = 0;
        for(Iterator it = approvers.iterator(); it.hasNext(); ){
        	SpecialBidApprvrOjbect approver = (SpecialBidApprvrOjbect)it.next();
        	
        	if(approver.getRdyToOrder() == 1){
        		rdyToOrderCount++;
        	}
        }
        
        if(rdyToOrderCount == approvers.size()){
        	return true;
        }
        
        return false;
    }

}
