package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.DraftQuoteSubmitBaseContract;
import com.ibm.dsw.quote.draftquote.process.EvalProcessFactory;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.dsw.quote.mail.process.SpecialBidEmailHelper;
import com.ibm.dsw.spbid.common.ApprovalGroup;
import com.ibm.dsw.spbid.common.ApprovalGroupMember;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmitPGSQuoteAction<code> class.
 *    
 * @author: Owen Sun
 * 
 * Creation date: Oct 13, 2011
 */
public class SubmitPGSQuoteAction extends SubmitDraftQuoteBaseAction {

	private static final long serialVersionUID = -2495060969650402858L;

	/**
	 * override super method to set default submit parameters: approver, suppress PA new customer email, tax output, email partner
	 * 
	 */
	protected void excuteSubmitProcess(ProcessContract contract, ResultHandler handler, Quote quote, User user, List approverGroup)
	throws QuoteException {

		QuoteHeader header = quote.getQuoteHeader();
		DraftQuoteSubmitBaseContract baseContract = (DraftQuoteSubmitBaseContract) contract;
		Cookie sqoCookie = baseContract.getSqoCookie();
		if(isPANewCustomer(quote) ){
			// If a quote is not special bid, but the customer is a new PA customer also need to submit to STH for review. 
			logContext.debug(this, "submit to STH for new PA customer");
			this.submitQuoteForSTH(baseContract, quote,handler);
		}
		else if ( QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO.equalsIgnoreCase(baseContract.getQuoteUserSession().getBpTierModel()) ) 
		{
			// If quote submitted by T2, all routing to STH
			logContext.debug(this, "submit to STH for Tier2");
			this.submitQuoteForSTH(baseContract, quote,handler);
		}
		else if(header.getSpeclBidFlag() == 1 && header.getApprovalRouteFlag() == 1){
			if( isSubmitPGSLevel0SpBid(baseContract.getQuoteUserSession(),quote,sqoCookie)){
				//submitForSAP
				logContext.debug(this, "submit to sap for level 0 special bid");
				this.submitLevel0SpBidForSap(baseContract, handler, quote, user, approverGroup);
			}else{
				logContext.debug(this, "submit to STH for non-level 0 sepcial bid");
				this.submitQuoteForSTH(baseContract, quote, handler);
				
			}
		}else{
			Map submitParams = new HashMap();
			this.redirectAction(submitParams, handler);
		}
		
	}
	
	private void submitLevel0SpBidForSap(ProcessContract contract, ResultHandler handler, Quote quote, User user, List approverGroup){
		Map submitParams = new HashMap();
		if(approverGroup != null && !approverGroup.isEmpty()){
			
			//to filter duplicate groups for same level
			ArrayList levelList = new ArrayList();

			for (int i = 0; i < approverGroup.size(); i++) {
				
				ApprovalGroup group = (ApprovalGroup)approverGroup.get(i);
				
				if(levelList.contains(group.getType().getLevel())){
					continue;
				}

				String key = DraftQuoteParamKeys.PARAM_APPROVER_LEVEL + group.getType().getLevel();

				StringBuffer approverValue = new StringBuffer(group.getName());

				approverValue = approverValue.append(QuoteConstants.APPROVAL_INFO_DELIMITOR);

				ApprovalGroupMember[] groupMember = group.getMembers();

				if(groupMember != null && groupMember.length > 0){

					approverValue = approverValue.append(groupMember[0].getEmail()).append(QuoteConstants.APPROVAL_INFO_DELIMITOR).append(groupMember[0].getFirstName()).append(groupMember[0].getLastName());
					
					submitParams.put(key, approverValue.toString());
					
					levelList.add(group.getType().getLevel());
					
				}

			}
			
			//add isMultipleGroups and isOneLvlApprvrOnly to parameters map
			Boolean flag = (Boolean)(handler.getParameters().getParameter(DraftQuoteParamKeys.PARAM_SEND_MULTI_GRPS_NOTIF));
			logContext.info(this, "get PARAM_SEND_MULTI_GRPS_NOTIF from handler: " + flag);
			if ( flag != null )
			{
				submitParams.put(DraftQuoteParamKeys.PARAM_SEND_MULTI_GRPS_NOTIF, flag.toString());
			}
			flag = (Boolean)(handler.getParameters().getParameter(DraftQuoteParamKeys.PARAM_SEND_ONE_LVL_APPRVR_NOTIF));
			logContext.info(this, "get PARAM_SEND_ONE_LVL_APPRVR_NOTIF from handler: " + flag);
			if ( flag != null )
			{
				submitParams.put(DraftQuoteParamKeys.PARAM_SEND_ONE_LVL_APPRVR_NOTIF, flag.toString());
			}
		}
		
		this.redirectAction(submitParams,handler);
	}
	
	private void displayNoEvaluatorMess(DraftQuoteSubmitBaseContract contract, ResultHandler handler)
	{
		String msg = getI18NString(com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys.NO_EVALUATOR_SET_UP_FOR_THIS_COUNTRY_IN_PGS, I18NBundleNames.BASE_MESSAGES,
				contract.getLocale());
        handler.addMessage(msg, MessageBeanKeys.INFO);
		handler.addObject(ParamKeys.PARAM_UNDO_FLAG, "true");
	}
	
	private void submitQuoteForSTH(DraftQuoteSubmitBaseContract contract, Quote quote, ResultHandler handler )throws QuoteException{
		String webQuoteNum = quote.getQuoteHeader().getWebQuoteNum();
		//if is federal customer, can't submit for evaluation
		if ( quote.getCustomer().getGsaStatusFlag() )
		{
			logContext.info(this, "quote blocked submit for evaluation for federal customer: " + webQuoteNum + ":" + quote.getCustomer().getCustNum());
			this.displayNoEvaluatorMess(contract, handler);
			return;
		}
		
		String userId = contract.getUserId();
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		
		//here add logic for check no evaluator
		if ( !quote.getQuoteHeader().isHasEvaluator() && StringUtils.isBlank(quote.getQuoteHeader().getEvalEmailAdr()) )
		{
			displayNoEvaluatorMess(contract, handler);
			logContext.info(this, "Unable to submit this quote to STH for no evaluator: " + webQuoteNum + ", userId=" + userId);
			return;
		}
		//update
		QuoteProcess quoteProcess = null;
		try {
			quoteProcess = QuoteProcessFactory.singleton().create();
		} catch (QuoteException e) {
			e.printStackTrace();
		}
		if(StringUtils.isBlank(quoteHeader.getEvalEmailAdr()) ){
			quoteProcess.updateBPQuoteStage(userId,quoteHeader.getWebQuoteNum(),QuoteConstants.QUOTE_STAGE_CODE_SUBMITTED_FOR_EVAL,1,1,"");
		}else{
			quoteProcess.updateBPQuoteStage(userId,quoteHeader.getWebQuoteNum(),QuoteConstants.QUOTE_STAGE_CODE_ACCEPTED_EVAL,1,1,"");
		}
		//submitForSTH 
		handler.addObject(ParamKeys.PARAM_SHOW_EMAIL_MSG_FLAG, false);
		//send emial
		try{
			sendEmailWhenSubmitForSTH(contract,quote);
		} catch (EmailException e) {
			logContext.error(this, "Send mail to evaluator for bp submit special bid to STH. quote num: "+webQuoteNum+ ", userId=" + userId);
		}
		
		handler.setState(DraftQuoteActionKeys.SUBMIT_QUOTE_FOR_CONFIRMATION_TO_STH);

	}
	
	/**
	 * Send mail to evaluator for bp submit special bid to STH
	 * @param contract
	 * @param quote
	 * @return
	 * @throws QuoteException
	 */
	private boolean sendEmailWhenSubmitForSTH(
			DraftQuoteSubmitBaseContract contract, Quote quote) throws QuoteException {
		
		QuoteHeader header = quote.getQuoteHeader();
		
		String evalEmail = header.getEvalEmailAdr();
		if(StringUtils.isEmpty(evalEmail)){
			evalEmail = getEvalList(header.getCountry().getCode3());
		}
		
		String quoteNum = header.getWebQuoteNum();
		String requestName = header.getQuoteTitle();
		String customerName = quote.getCustomer().getCustName();
		
		String bidCreator = quote.getCreator().getFullName();
		String bidCreatorEmail = quote.getCreator().getEmailAddress();
		
		//bp name it's the name of the creator's site
		String bpName = quote.getPayer() == null ? "" : quote.getPayer().getCustNameFull();
		if(QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO.equalsIgnoreCase(contract.getQuoteUserSession().getBpTierModel() )){
			bpName = quote.getReseller() == null ? "" : quote.getReseller().getCustNameFull();
		}
		
		String opprtntyNum = header.getOpprtntyNum();
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy");
		String quoteExpDate =  sdfDate.format(header.getQuoteExpDate());
		
		SpecialBidEmailHelper helper = new SpecialBidEmailHelper();
		return helper.submitForSTHApproval(evalEmail,quoteNum,requestName,customerName,
				bidCreator,bidCreatorEmail, bpName, opprtntyNum,quoteExpDate, quote);
	}

	/**
	 * get all evaluator for this country
	 * @param code3
	 * @return
	 * @throws QuoteException
	 */
	private String getEvalList(String code3) throws QuoteException {
		
		List<String> evalList = EvalProcessFactory.singleton().create().getEvalsByCntry(code3);
		StringBuffer sb = new StringBuffer(2000);
		if(evalList!=null && evalList.size()>0){
			for(String evalEmail : evalList){
				sb.append(evalEmail).append(",");
			}
			if(sb.length()>0)
				return sb.toString().substring(0, sb.length()-1);
		}
		return null;
	}

	private boolean isPANewCustomer(Quote quote){
		 if ( quote.getQuoteHeader().isPAQuote() ){
			 return quote.getQuoteHeader().hasNewCustomer() && !quote.getQuoteHeader().hasExistingCustomer();
		  }
		 return false;
	}
	
	private void redirectAction(Map submitParams,ResultHandler handler){
		String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.SUBMIT_QUOTE_FOR_CONFIRMATION);
		handler.addObject(DraftQuoteParamKeys.PARAM_REDIRECT_URL, redirectURL);
		submitParams.put(DraftQuoteParamKeys.PARAM_SUPPRS_PA_REGSTRN_EMAIL, "on");
		submitParams.put(DraftQuoteParamKeys.PARAM_CHK_NO_TAX, "");
		submitParams.put(DraftQuoteParamKeys.PARAM_CHK_EMAIL_Y9_PARTNER_ADDR_LIST, "1");
		handler.addObject(DraftQuoteParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
		//put the submit parameters, redirectAction.jsp will set it into http request
		handler.addObject(DraftQuoteParamKeys.PARAM_REDIRECT_PARAMS, submitParams);
		handler.setState(DraftQuoteStateKeys.STATE_REDIRECT_ACTION);
	}

}