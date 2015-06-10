package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.DisplayToUQuoteContract;
import com.ibm.dsw.quote.draftquote.process.QuoteToUProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteToUProcessFactory;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.partner.util.PartnerUtils;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DisplayDraftToUQuoteAction<code> class.
 *    
 * @author: whliul@cn.ibm.com
 * 
 * Creation date: May 31, 2013
 */
public class DisplayToUQuoteAction extends BaseContractActionHandler{

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
        DisplayToUQuoteContract displayDraftToUQuoteContract= (DisplayToUQuoteContract ) contract;

        User user = displayDraftToUQuoteContract.getUser();
        
        QuoteToUProcess quoteToUProcess= QuoteToUProcessFactory.singleton().create();
        HashMap results = quoteToUProcess.lookUpToUList(displayDraftToUQuoteContract.getWebQuoteNum());

//        handler.addObject(FindQuoteParamKeys.FIND_TOU_RESULTS, results);
        if(QuoteCommonUtil.isDecimalNumber(displayDraftToUQuoteContract.getWebQuoteNum())){
        	handler.addObject(ParamKeys.PARAM_QUOTE_NUM, displayDraftToUQuoteContract.getWebQuoteNum());
        }
        
        handler.addObject(FindQuoteParamKeys.DISPLAY_DRAFT_CONTRACT, displayDraftToUQuoteContract);
        
        handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, results);
        if(PartnerUtils.isValidLob(displayDraftToUQuoteContract.getLob())){
        	handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, displayDraftToUQuoteContract.getLob());
        }else{
        	//handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, CustomerConstants.LOB_PA);
        }
        
        handler.addObject(ParamKeys.IS_ONLY_SAAS_PARTS, displayDraftToUQuoteContract.isOnlySaaSParts());
        //handler.addObject(ParamKeys.IS_CHANNEL_QUOTE, displayDraftToUQuoteContract.isChannelQuote());
        handler.addObject(ParamKeys.IS_SUBMITTED_QUOTE, displayDraftToUQuoteContract.isSubmittedQuote());
        
        String quoteStatus = displayDraftToUQuoteContract.getQuoteStatus();
        handler.addObject(ParamKeys.SAAS_TERM_COND_CAT_FLAG, displayDraftToUQuoteContract.getSaasTermCondCatFlag());
         
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        int CSATermsCount = 0;
        Quote quote;
		try {
			quote = qProcess.getDraftQuoteBaseInfoByQuoteNum(displayDraftToUQuoteContract.getWebQuoteNum());
			if (null != quote){
				QuoteHeader header = quote.getQuoteHeader();
				if(null != header){
					handler.addObject(DraftQuoteParamKeys.PARAM_CURRENT_CONTRACT_NUM, header.getContractNum());
					String agrmtTypeCode = header.getAgrmtTypeCode();
					//lirui @ 20140409 :
					//for legacy submitted data: adapt to new CSA UI
					if(header.isSubmittedQuote() && (agrmtTypeCode == null || "".equals(agrmtTypeCode))){
						if(header.getSaasTermCondCatFlag()==2){
							agrmtTypeCode = QuoteConstants.LOB_CSTA;
						}else{
							agrmtTypeCode = displayDraftToUQuoteContract.getLob();
						}
					}
					//lirui @ 20140409 ~
					setContractNums(handler, qProcess, displayDraftToUQuoteContract.getWebQuoteNum(), agrmtTypeCode, header.getContractNum());
					if(QuoteConstants.LOB_CSRA.equals(agrmtTypeCode) || QuoteConstants.LOB_CSTA.equals(agrmtTypeCode)){
						CSATermsCount = quoteToUProcess.getCountOfCsaTerms();
					}
					handler.addObject(ParamKeys.PARAM_AGREEMENT_TYPE, header.getAgrmtTypeCode());
					handler.addObject(ParamKeys.SAAS_TERM_COND_CAT_FLAG, header.getSaasTermCondCatFlag());
					handler.addObject(ParamKeys.IS_SPECIAL_BID, header.getSpeclBidFlag() == 1 ? true : false);
					if(header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR)
							||header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)
							||header.isAcceptedByEval() //S_ASGNEVAL Under evaluation 
							||header.isSubmittedForEval()){  //S_TOBEEVAL Awaiting assignment
						quoteStatus = "Active";
					}
					
					if(header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)){
						quoteStatus = "OrderOnHold";
					}
				}
			}
			
			// Only submitters have priv to update the ToUs in the overlay.  All other roles should be read only.  
			// Jeanne to concur, All other roles should be read only
			if ((displayDraftToUQuoteContract.isSQOEnv() && QuoteConstants.ACCESS_LEVEL_SUBMITTER != user.getAccessLevel(QuoteConstants.APP_CODE_SQO))
					//||(displayDraftToUQuoteContract.isPGSEnv() && QuoteConstants.ACCESS_LEVEL_SUBMITTER != user.getAccessLevel(QuoteConstants.APP_CODE_PGS))
					) {
				quoteStatus = "Inactive";
			}
		} catch (NoDataException e) {
			logContext.error(this, e.getMessage());
		} catch (QuoteException e) {
			logContext.error(this, e.getMessage());
		}
		
		 if(!SecurityUtil.isValidInput("lob",displayDraftToUQuoteContract.getLob(),"Alpha", 20, true)){
			    throw new QuoteException("Invalid lob value");
	     	 }
		 if(!SecurityUtil.isValidInput("webQuoteNum",displayDraftToUQuoteContract.getWebQuoteNum(),"Numeric", 20, true)){
			    throw new QuoteException("Invalid webQuoteNum value");
	     }
		 if(!SecurityUtil.isValidInput("quoteStatus",quoteStatus,"Alpha", 20, true)){
			    throw new QuoteException("Invalid quoteStatus value");
	     }
		 if(!SecurityUtil.isValidInput("actionCode",displayDraftToUQuoteContract.getActionCode(),"Alpha", 20, true)){
			    throw new QuoteException("Invalid actionCode value");
	     }
		 if(!SecurityUtil.isValidInput("agrmtTypeCode",displayDraftToUQuoteContract.getAgrmtTypeCode(),"Alpha", 20, true)){
			    throw new QuoteException("Invalid agrmtTypeCode value");
	     }
		 if(!SecurityUtil.isValidInput("isOnlySaaSParts",String.valueOf(displayDraftToUQuoteContract.isOnlySaaSParts()),"Alpha", 5, true)){
			    throw new QuoteException("Invalid isOnlySaaSParts value");
	     }
		 if(!SecurityUtil.isValidInput("isChannelQuote",String.valueOf(displayDraftToUQuoteContract.isChannelQuote()),"Alpha", 5, true)){
			    throw new QuoteException("Invalid isChannelQuote value");
	     }
		 if(!SecurityUtil.isValidInput("isSubmittedQuote",String.valueOf(displayDraftToUQuoteContract.isSubmittedQuote()),"Alpha", 5, true)){
			    throw new QuoteException("Invalid isSubmittedQuote value");
	     }
		 
		handler.addObject(DraftQuoteParamKeys.PARAM_CSA_TERMS_COUNT, CSATermsCount);
		handler.addObject(ParamKeys.QUOTE_STATUS, quoteStatus);
		handler.addObject(DraftQuoteParamKeys.ACTION_CODE, displayDraftToUQuoteContract.getActionCode());
        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_DRAFT_TOU_LIST);
        return handler.getResultBean();
	}

	@SuppressWarnings("unchecked")
	private void setContractNums(ResultHandler handler, QuoteProcess qProcess, String webQuoteNum, String agrmtTypeCode, String currentContractNum) throws QuoteException {
		Customer customer = qProcess.findCustForExistCtrctCust(webQuoteNum, "");
		
		if (QuoteConstants.LOB_PA.equals(agrmtTypeCode) || QuoteConstants.LOB_PAE.equals(agrmtTypeCode)) {
			if (StringUtils.isBlank(currentContractNum) && null!= customer) {
				List<Contract> PAContracts = customer.getNoCsaContractList();
				if (PAContracts != null && PAContracts.size() > 0) {
					handler.addObject(DraftQuoteParamKeys.PARAM_PA_CONTRACT_NUM, PAContracts.get(0).getSapContractNum());
				}
			} else {
				handler.addObject(DraftQuoteParamKeys.PARAM_PA_CONTRACT_NUM, currentContractNum);
			}
			
			if (null!= customer) {
				List<Contract> CSAContracts = customer.getCsaContractList();
				if (CSAContracts != null && CSAContracts.size() > 0) {
					handler.addObject(DraftQuoteParamKeys.PARAM_CSRA_CONTRACT_NUM, CSAContracts.get(0).getSapContractNum());
				}
			}
			
		} else if (QuoteConstants.LOB_CSRA.equals(agrmtTypeCode) || QuoteConstants.LOB_CSTA.equals(agrmtTypeCode)) {
			if (StringUtils.isBlank(currentContractNum) && null!= customer) {
				List<Contract> CSAContracts = customer.getCsaContractList();
				if (CSAContracts != null && CSAContracts.size() > 0) {
					handler.addObject(DraftQuoteParamKeys.PARAM_CSRA_CONTRACT_NUM, CSAContracts.get(0).getSapContractNum());
				}
			} else {
				handler.addObject(DraftQuoteParamKeys.PARAM_CSRA_CONTRACT_NUM, currentContractNum);
			}
			
			if (null!= customer) {
				List<Contract> PAContracts = customer.getNoCsaContractList();
				if (PAContracts != null && PAContracts.size() > 0) {
					handler.addObject(DraftQuoteParamKeys.PARAM_PA_CONTRACT_NUM, PAContracts.get(0).getSapContractNum());
				}
			}
			
		}
	}
}
