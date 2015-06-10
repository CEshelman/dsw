package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.BaseI18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.PriceEngineUnAvailableException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ApproverRuleValidation;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.DraftQuoteSubmitBaseContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.spbid.common.ApprovalGroup;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SubmitDraftQuoteBaseAction</code> class .
 * 
 * 
 * 
 * Creation date: May 18, 2007
 */
public class SubmitDraftQuoteBaseAction extends DraftQuoteSubmitBaseAction {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.DraftQuoteSubmitBaseAction#validateQuote(is.domainx.User, com.ibm.dsw.quote.common.domain.Quote, javax.servlet.http.Cookie)
     */
    protected Map validateQuote(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException {
        if(quote.getQuoteHeader().isCopiedForOutputChangeFlag()
        		&& QuoteConstants.QUOTE_STAGE_CODE_CPCHGOUT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
        	return null;
        }
        if((quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()
                && QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode()))){
            return QuoteCapabilityProcessFactory.singleton().create().validateForSubmissionAsFinal(user, quote, cookie);
        }
        if((quote.getQuoteHeader().isExpDateExtendedFlag()
                && QuoteConstants.QUOTE_STAGE_CODE_CPEXDATE.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode()))){
        	return QuoteCapabilityProcessFactory.singleton().create().validateForSubmissionAsFinal(user, quote, cookie);
        }
        if (quote.getQuoteHeader().getSpeclBidFlag() == 1)
            return QuoteCapabilityProcessFactory.singleton().create()
                    .validateSubmissionForApproval(user, quote, cookie);
        else
            return QuoteCapabilityProcessFactory.singleton().create().validateForSubmissionAsFinal(user, quote, cookie);
    }

    protected String getState() {
        return DraftQuoteStateKeys.STATE_DISPLAY_QUOTE_SUBMISSION;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.DraftQuoteSubmitBaseAction#getValidMsgHead(java.util.Locale)
     */
    protected String getValidMsgHead(Locale locale) {
        return this.getI18NString(DraftQuoteMessageKeys.SUBMIT_FAILED_MSG, BaseI18NBundleNames.QUOTE_BASE, locale);
    }
    
    protected Quote getQuoteDetail(String creatorId,boolean isPGSEnv) throws NoDataException, QuoteException,
            PriceEngineUnAvailableException {

        Quote quote = super.getQuoteDetail(creatorId,isPGSEnv);
        QuoteHeader quoteHeader=quote.getQuoteHeader();
        PartPriceProcess partPriceProcess = PartPriceProcessFactory.singleton().create();
        partPriceProcess.getCurrentPriceForSubmit(quote, creatorId);
        
        QuoteProcess process = QuoteProcessFactory.singleton().create();
        quote.setQuoteUserAccess(process.getQuoteUserAccess(quote.getQuoteHeader().getWebQuoteNum(), creatorId, null));
        
        if (StringUtils.isNotBlank(quoteHeader.getWebQuoteNum())
				&& ButtonDisplayRuleFactory.singleton()
						.isDisplayFCTNonStdTermsConds(quoteHeader)) {
			
			List files = process.getFctNonStdTcAttachments(quote
					.getQuoteHeader());
			if (files != null) {
				for (int i = 0; i < files.size(); i++) {
					QuoteAttachment attachment = (QuoteAttachment) files.get(i);
					attachment.setRemoveAjaxURL(genRemoveAjaxURL(quote
							.getQuoteHeader().getWebQuoteNum(), attachment
							.getId()));
				}
				quote.setFctNonStdTcAttachmentsList(files);
			}
		}

        return quote;
    }

    protected void excuteSubmitProcess(ProcessContract contract, ResultHandler handler, Quote quote, User user, List approverGroup)
            throws QuoteException {

        QuoteHeader header = quote.getQuoteHeader();
        String preCreditCheckedQuoteNum = "";
        if (header.getSpeclBidFlag() == 1 
                && header.getApprovalRouteFlag() == 1
                && !header.isCopied4PrcIncrQuoteFlag()
                && !QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(header.getQuoteStageCode())){
            
            if(quote.getQuoteHeader().isBidIteratnQt() || quote.getQuoteHeader().isSaaSStrmlndApprvlFlag()){
                //prepare to call sp for the bid iteration submission validation
//                String approvalInfo = buildApprovalInfo(approverGroup);
                
                QuoteProcess process = QuoteProcessFactory.singleton().create();
                
                Set<String> bidIteratnReasons = new HashSet<String>();
                if( approverGroup != null && approverGroup.size() != 0 ){
                    
                	ApproverRuleValidation ruleValidation = process.filterApprover(header, approverGroup);
                    
                    //flag of if bid iteration can be submitted
                    boolean isBidIteratnSubmittable = ruleValidation.isBidIterFlag();
                    logContext.info(this, "returned approval info check result for bid iteration:" + isBidIteratnSubmittable);
                    List returnedApprovalInfo = ruleValidation.getApproveTypes();
                    
                    //0 means bid iteration is not allowed to be submitted, return not meet reasons when not allowed to submit
                    if(quote.getQuoteHeader().isBidIteratnQt() && !isBidIteratnSubmittable){
                        //redirect user to convert page
                    	
                    	if(ruleValidation.getSaasErrorCodeList() != null){
                    		bidIteratnReasons.addAll(ruleValidation.getSaasErrorCodeList());
                    	}
                    	if(ruleValidation.getSoftwareErrorCodeList()!= null){
                    		bidIteratnReasons.addAll(ruleValidation.getSoftwareErrorCodeList());
                    	}
                    	
                    	if(ruleValidation.getMonthlyErrorCodeList() != null){
                    		bidIteratnReasons.addAll(ruleValidation.getMonthlyErrorCodeList());
                    	}
                    	logContext.debug(this, "web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] bid iteration approver validation failure ");
                    	logContext.info(this, "returned approval info from sp S_QT_BID_ITERATN_VERIFY: " + returnedApprovalInfo);
                    	handler.addObject(ParamKeys.PARMA_BID_ITERATN_REASONS, bidIteratnReasons);
                        handler.setState(DraftQuoteStateKeys.STATE_NOT_MEET_BID_ITERATION);
                    }else{

                        // if quote can be submitted, update Soft & Saas bid iteration flag, but doesn't persist to DB for now. Will do persistent in submission action
                    	// approval validation interface won't update the two flags so can't update quote header from approval validation result
//                        header.setSaasBidIteratnQtInd(ruleValidation.getSaasValidationResult());
//                        header.setSoftBidIteratnQtInd(ruleValidation.getSoftwareValidationResult());
                        
                        //bid iteration can be submitted
                        //here is to refilter the returned groups
                        List expectedApprovers = new ArrayList();
                        for (int i = 0; i < approverGroup.size(); i++) {
                            ApprovalGroup currGroup = (ApprovalGroup) approverGroup.get(i);
                            
                            String type = currGroup.getType().getTypeName();
                            
                            if(returnedApprovalInfo.contains(type) ){
                                expectedApprovers.add(currGroup);
                            }
                            
                        }

                        
//                        handler.addObject(DraftQuoteParamKeys.NO_APPROVAL_REQUIRE, DraftQuoteParamKeys.NO_APPROVAL_REQUIRE);
                        if(expectedApprovers == null || expectedApprovers.size()<1){
                        	if((header.getSaasBidIteratnQtInd() == 1 && header.getSoftBidIteratnQtInd() == -1) || header.isSaaSStrmlndApprvlFlag()){
                        		handler.addObject(DraftQuoteParamKeys.NO_APPROVAL_REQUIRE, DraftQuoteConstants.VALUE_TRUE);
                        	}
                        }
//                        expectedApprovers.clear();
                        handler.addObject(DraftQuoteParamKeys.PARAM_APPROVAL_GROUPS, expectedApprovers);
                    }
                }else if(quote.getQuoteHeader().isBidIteratnQt()){
//                	bidIteratnReasons.add("todo");
                	handler.addObject(ParamKeys.PARMA_BID_ITERATN_REASONS, bidIteratnReasons);
                    logContext.info(this, "returned approval info is null from rule enginee");
                    handler.setState(DraftQuoteStateKeys.STATE_NOT_MEET_BID_ITERATION);
                }
            }

        }
        
        boolean isPreCreditCheckOptionDisplay = ButtonDisplayRuleFactory.singleton().isPreCreditCheckOptionDisplay(header);
        if(isPreCreditCheckOptionDisplay){
            QuoteProcess process = QuoteProcessFactory.singleton().create();
            int validMonths = PartPriceConfigFactory.singleton().getPreCrediteCheckValidMonths();
            preCreditCheckedQuoteNum = process.getPreCreditCheckedQuoteNum(header.getWebQuoteNum(), validMonths);
        }
        
        DraftQuoteSubmitBaseContract sbmtContract = (DraftQuoteSubmitBaseContract) contract;
        handler.addObject(ParamKeys.PARAM_QUOTE_USER_SESSION, sbmtContract.getQuoteUserSession());
        handler.addObject(ParamKeys.PARAM_USER_OBJECT, sbmtContract.getUser());
        handler.addObject(ParamKeys.PRE_CREDIT_CHECKED_QUOTE_NUM, preCreditCheckedQuoteNum);
    }
    
    //build the sp call parameter
    //the format of approval info parameter is discuss with Thomas team
    
    protected String genRemoveAjaxURL(String quoteNum, String fileNum){
      	 ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
          String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
          String urlPattern = appContext.getConfigParameter(ApplicationProperties.APPLICATION_URL_PATTERN);
          String targetAction = DraftQuoteActionKeys.REMOVE_ATTACHMENT_ACTION;
          StringBuffer sb = new StringBuffer();
          sb.append(urlPattern).append("?");
          sb.append(actionKey).append("=").append(targetAction);
          sb.append("&").append(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM).append("=").append(quoteNum);
          sb.append("&").append("attchmtSeqNum").append("=").append(fileNum);
          sb.append("&").append(DraftQuoteParamKeys.PARAM_ATT_CODE).append("=").append("1");
      	return sb.toString();
      }    
}
