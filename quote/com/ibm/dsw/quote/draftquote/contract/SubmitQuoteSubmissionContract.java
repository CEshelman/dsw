package com.ibm.dsw.quote.draftquote.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.common.domain.SpecialBidApprvrOjbect;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmitQuoteSubmissionContract<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 17, 2007
 */

public class SubmitQuoteSubmissionContract extends QuoteBaseContract {

    private String chkReqCustNo;
    private String chkReqCredChk;
    private String chkNoTax;
    private String chkEmailQt;
    private String chkEmailQtPC;
    private String chkEmailAddr;
    private String custEmailAddr;
    private String customMsg;
    private String chkNoPrcOrPoints;
    private String quoteOutputTypeFlag;
    private String chkEmailY9PartnerAddrList;
    private String chkEmailAddiPartnerAddr;
    private String custEmailAddiPartnerAddr;
    private transient List specialBidApprvrs = new ArrayList();
    private String validatePartnerEmailFields;
    private String chkPAOBlock;
    private String sendNoApprvrNotif;
    private String sendMultiGrpsNotif;
    private String sendOneLvlApprvrNotif;
    private String needSendAddiMailToCreator;
    private String creatorEmail;
    private String supprsPARegstrnEmailFlag;
    private String searchCriteriaUrlParam;
    private String copyFromSubmittedQuoteNum;
    private String preCreditCheckedQuoteNum;
    private String fctNonStdTermsConds;
    private transient Map submitParam;
    
	private String chkFirmOrderLetter;
	
    private String saasBidIteratnQtInd = null;    
    private String softBidIteratnQtInd = null;
    private String noApprovalRequire = null;
    private String saaSStrmlndApprvlFlag = null;
    private String quoteOutputOption;
    
    private String cntFirstName;
    private String cntLastName;
    private String cntPhoneNumFull;
    private String cntFaxNumFull;
    private String cntEmailAdr;
    private String sapCntPrtnrFuncCode;    
    
    public String getCntFirstName() {
		return cntFirstName;
	}

	public void setCntFirstName(String cntFirstName) {
		this.cntFirstName = cntFirstName;
	}

	public String getCntLastName() {
		return cntLastName;
	}

	public void setCntLastName(String cntLastName) {
		this.cntLastName = cntLastName;
	}

	public String getCntPhoneNumFull() {
		return cntPhoneNumFull;
	}

	public void setCntPhoneNumFull(String cntPhoneNumFull) {
		this.cntPhoneNumFull = cntPhoneNumFull;
	}

	public String getCntFaxNumFull() {
		return cntFaxNumFull;
	}

	public void setCntFaxNumFull(String cntFaxNumFull) {
		this.cntFaxNumFull = cntFaxNumFull;
	}

	public String getCntEmailAdr() {
		return cntEmailAdr;
	}

	public void setCntEmailAdr(String cntEmailAdr) {
		this.cntEmailAdr = cntEmailAdr;
	}

	public String getSapCntPrtnrFuncCode() {
		return sapCntPrtnrFuncCode;
	}

	public void setSapCntPrtnrFuncCode(String sapCntPrtnrFuncCode) {
		this.sapCntPrtnrFuncCode = sapCntPrtnrFuncCode;
	}

	private String budgetaryQuoteFlag;
    
    
    
    public String getBudgetaryQuoteFlag() {
		return budgetaryQuoteFlag;
	}

	public void setBudgetaryQuoteFlag(String budgetaryQuoteFlag) {
		this.budgetaryQuoteFlag = budgetaryQuoteFlag;
	}

	public String getNoApprovalRequire() {
		return noApprovalRequire;
	}

	public void setNoApprovalRequire(String noApprovalRequire) {
		this.noApprovalRequire = noApprovalRequire;
	}

	public String getSaasBidIteratnQtInd() {
		return saasBidIteratnQtInd;
	}

	public void setSaasBidIteratnQtInd(String saasBidIteratnQtInd) {
		this.saasBidIteratnQtInd = saasBidIteratnQtInd;
	}

	public String getSoftBidIteratnQtInd() {
		return softBidIteratnQtInd;
	}

	public void setSoftBidIteratnQtInd(String softBidIteratnQtInd) {
		this.softBidIteratnQtInd = softBidIteratnQtInd;
	}

	public Map getSubmitParam() {
		return submitParam;
	}

	public void setSubmitParam(Map submitParam) {
		this.submitParam = submitParam;
	}

	public String getFctNonStdTermsConds() {
		return fctNonStdTermsConds;
	}

	public void setFctNonStdTermsConds(String fctNonStdTermsConds) {
		this.fctNonStdTermsConds = fctNonStdTermsConds;
	}

	public void load(Parameters parameters, JadeSession session) {
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.load(parameters, session);
        SpecialBidApprvrOjbect specialBidApprvrOjbect;
        
        //PGS submit action will pass this map 
        this.submitParam = parameters.getParameter(DraftQuoteParamKeys.PARAM_REDIRECT_PARAMS) instanceof Map? (Map)parameters.getParameter(DraftQuoteParamKeys.PARAM_REDIRECT_PARAMS):null;
		
        if(submitParam != null){
        	
        	//retrieve the default value for these parameters for PGS quotes
        	//submit action is responsible for setting value for these parameters
        	this.supprsPARegstrnEmailFlag = (String)submitParam.get(DraftQuoteParamKeys.PARAM_SUPPRS_PA_REGSTRN_EMAIL);
        	this.chkNoTax = (String) submitParam.get(DraftQuoteParamKeys.PARAM_CHK_NO_TAX);
        	this.chkEmailY9PartnerAddrList = (String) submitParam.get(DraftQuoteParamKeys.PARAM_CHK_EMAIL_Y9_PARTNER_ADDR_LIST);
        	this.sendMultiGrpsNotif = (String)submitParam.get(DraftQuoteParamKeys.PARAM_SEND_MULTI_GRPS_NOTIF);
        	this.sendOneLvlApprvrNotif = (String)submitParam.get(DraftQuoteParamKeys.PARAM_SEND_ONE_LVL_APPRVR_NOTIF);
        	
        	for (int i = 0; i <= QuoteConstants.APPROVAL_TYPE_MAX_LEVLE; i++) {
        		String key = DraftQuoteParamKeys.PARAM_APPROVER_LEVEL + i;
        		specialBidApprvrOjbect = new SpecialBidApprvrOjbect();
        		if (submitParam.containsKey(key)) {
        			String values = (String)submitParam.get(key);
        			values = values == null ? "" : values;
        			String[] tokens = values.split(QuoteConstants.APPROVAL_INFO_DELIMITOR);
        			specialBidApprvrOjbect.setApprvrLevel(i);

        			if(tokens != null && tokens.length > 0){
        				specialBidApprvrOjbect.setApprvrGrpName(tokens[0]);
        			}
        			if(tokens != null && tokens.length > 1){
        				specialBidApprvrOjbect.setApprvrEmail(tokens[1]);
        			}
        			if(tokens != null && tokens.length > 2){
        				specialBidApprvrOjbect.setApprvrFirstName(tokens[2]);
        			}
        			if(tokens != null && tokens.length > 3){
        				specialBidApprvrOjbect.setApprvrlastName(tokens[3]);
        			}
        			if(StringUtils.isNotBlank(specialBidApprvrOjbect.getApprvrGrpName()) && StringUtils.isNotBlank(specialBidApprvrOjbect.getApprvrEmail())){
        				logContext.info(this , "PGS quote submit parameters: " + specialBidApprvrOjbect.toString());
        				specialBidApprvrs.add(specialBidApprvrOjbect);
        			}
        		}
        	}
        	
        }else{
        	for (int i = 0; i <= QuoteConstants.APPROVAL_TYPE_MAX_LEVLE; i++) {
        		String key = DraftQuoteParamKeys.PARAM_APPROVER_LEVEL + i;
        		String key2 = DraftQuoteParamKeys.PARAM_APPROVER_READY_TO_ORDER_FLAG + i;
        		specialBidApprvrOjbect = new SpecialBidApprvrOjbect();
        		if (parameters.hasParameter(key)) {
        			if (parameters.hasParameter(key2)) {
        				int values2 = parameters.getParameterAsInt(key2);
        				specialBidApprvrOjbect.setRdyToOrder(values2);
        			}
        			String values = parameters.getParameterAsString(key);
        			values = values == null ? "" : values;
        			String[] tokens = values.split(QuoteConstants.APPROVAL_INFO_DELIMITOR);
        			specialBidApprvrOjbect.setApprvrLevel(i);
        			

        			if(tokens != null && tokens.length > 0){
        				specialBidApprvrOjbect.setApprvrGrpName(tokens[0]);
        			}
        			if(tokens != null && tokens.length > 1){
        				specialBidApprvrOjbect.setApprvrEmail(tokens[1]);
        			}
        			if(tokens != null && tokens.length > 2){
        				specialBidApprvrOjbect.setApprvrFirstName(tokens[2]);
        			}
        			if(tokens != null && tokens.length > 3){
        				specialBidApprvrOjbect.setApprvrlastName(tokens[3]);
        			}
        			if(StringUtils.isNotBlank(specialBidApprvrOjbect.getApprvrGrpName()) && StringUtils.isNotBlank(specialBidApprvrOjbect.getApprvrEmail())){
        				logContext.debug(this,specialBidApprvrOjbect.toString());
        				specialBidApprvrs.add(specialBidApprvrOjbect);
        			}
        		}
        	}
        }
    }
    
    public String getChkEmailAddr() {
        return chkEmailAddr;
    }
    public void setChkEmailAddr(String chkEmailAddr) {
        this.chkEmailAddr = chkEmailAddr;
    }
    public String getChkEmailQt() {
        return chkEmailQt;
    }
    public void setChkEmailQt(String chkEmailQt) {
        this.chkEmailQt = chkEmailQt;
    }
    public String getChkEmailQtPC() {
        return chkEmailQtPC;
    }
    public void setChkEmailQtPC(String chkEmailQtPC) {
        this.chkEmailQtPC = chkEmailQtPC;
    }
    public String getChkNoTax() {
        return chkNoTax;
    }
    public void setChkNoTax(String chkNoTax) {
        this.chkNoTax = chkNoTax;
    }
    public String getChkReqCredChk() {
        return chkReqCredChk;
    }
    public void setChkReqCredChk(String chkReqCredChk) {
        this.chkReqCredChk = chkReqCredChk;
    }
    public String getChkReqCustNo() {
        return chkReqCustNo;
    }
    public void setChkReqCustNo(String chkReqCustNo) {
        this.chkReqCustNo = chkReqCustNo;
    }
    public String getCustEmailAddr() {
        return custEmailAddr;
    }
    public void setCustEmailAddr(String custEmailAddr) {
        this.custEmailAddr = custEmailAddr;
    }
    public String getCustomMsg() {
        return customMsg;
    }
    public void setCustomMsg(String customMsg) {
        this.customMsg = customMsg;
    }
    public List getSpecialBidApprvrs() {
        return specialBidApprvrs;
    }
    public String getChkNoPrcOrPoints() {
        return chkNoPrcOrPoints;
    }
    public void setChkNoPrcOrPoints(String chkNoPrcOrPoints) {
        this.chkNoPrcOrPoints = chkNoPrcOrPoints;
    }
    public String getChkEmailAddiPartnerAddr() {
        return chkEmailAddiPartnerAddr;
    }
    public void setChkEmailAddiPartnerAddr(String chkEmailAddiPartnerAddr) {
        this.chkEmailAddiPartnerAddr = chkEmailAddiPartnerAddr;
    }
    public String getChkEmailY9PartnerAddrList() {
        return chkEmailY9PartnerAddrList;
    }
    public void setChkEmailY9PartnerAddrList(String chkEmailY9PartnerAddrList) {
        this.chkEmailY9PartnerAddrList = chkEmailY9PartnerAddrList;
    }
    public String getCustEmailAddiPartnerAddr() {
        return custEmailAddiPartnerAddr;
    }
    public void setCustEmailAddiPartnerAddr(String custEmailAddiPartnerAddr) {
        this.custEmailAddiPartnerAddr = custEmailAddiPartnerAddr;
    }
    public String getValidatePartnerEmailFields() {
        return validatePartnerEmailFields;
    }
    public void setValidatePartnerEmailFields(String validatePartnerEmailFields) {
        this.validatePartnerEmailFields = validatePartnerEmailFields;
    }	
    public String getChkPAOBlock() {
        return chkPAOBlock;
    }
    public void setChkPAOBlock(String chkPAOBlock) {
        this.chkPAOBlock = chkPAOBlock;
    }    
    public String getSendMultiGrpsNotif() {
        return sendMultiGrpsNotif;
    }
    public void setSendMultiGrpsNotif(String sendMultiGrpsNotif) {
        this.sendMultiGrpsNotif = sendMultiGrpsNotif;
    }
    public String getSendNoApprvrNotif() {
        return sendNoApprvrNotif;
    }
    public void setSendNoApprvrNotif(String sendNoApprvrNotif) {
        this.sendNoApprvrNotif = sendNoApprvrNotif;
    }
    public String getSendOneLvlApprvrNotif() {
        return sendOneLvlApprvrNotif;
    }
    public void setSendOneLvlApprvrNotif(String sendOneLvlApprvrNotif) {
        this.sendOneLvlApprvrNotif = sendOneLvlApprvrNotif;
    }
    public String getNeedSendAddiMailToCreator() {
        return needSendAddiMailToCreator;
    }
    public void setNeedSendAddiMailToCreator(String needSendAddiMailToCreator) {
        this.needSendAddiMailToCreator = needSendAddiMailToCreator;
    }
    public boolean needSendAddiMailToCreator() {
        return "true".equalsIgnoreCase(getNeedSendAddiMailToCreator());
    }
    public String getCreatorEmail() {
        return creatorEmail;
    }
    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }
    /**
     * @return Returns the supprsPARegstrnEmailFlag.
     */
    public String getSupprsPARegstrnEmailFlag() {
        return supprsPARegstrnEmailFlag;
    }
    /**
     * @param supprsPARegstrnEmailFlag The supprsPARegstrnEmailFlag to set.
     */
    public void setSupprsPARegstrnEmailFlag(String supprsPARegstrnEmailFlag) {
        this.supprsPARegstrnEmailFlag = supprsPARegstrnEmailFlag;
    }
    /**
     * @return Returns the searchCriteriaUrlParam.
     */
    public String getSearchCriteriaUrlParam() {
        return searchCriteriaUrlParam;
    }
    /**
     * @param searchCriteriaUrlParam The searchCriteriaUrlParam to set.
     */
    public void setSearchCriteriaUrlParam(String searchCriteriaUrlParam) {
        this.searchCriteriaUrlParam = searchCriteriaUrlParam;
    }

	public String getCopyFromSubmittedQuoteNum() {
		return copyFromSubmittedQuoteNum;
	}

	public void setCopyFromSubmittedQuoteNum(String copyFromSubmittedQuoteNum) {
		this.copyFromSubmittedQuoteNum = copyFromSubmittedQuoteNum;
	}

    public String getPreCreditCheckedQuoteNum() {
        return preCreditCheckedQuoteNum;
    }

    public void setPreCreditCheckedQuoteNum(String preCreditCheckedQuoteNum) {
        this.preCreditCheckedQuoteNum = preCreditCheckedQuoteNum;
    }

	public String getChkFirmOrderLetter() {
		return chkFirmOrderLetter;
	}

	public void setChkFirmOrderLetter(String chkFirmOrderLetter) {
		this.chkFirmOrderLetter = chkFirmOrderLetter;
	}

	public String getQuoteOutputTypeFlag() {
		return quoteOutputTypeFlag;
	}

	public void setQuoteOutputTypeFlag(String quoteOutputTypeFlag) {
		this.quoteOutputTypeFlag = quoteOutputTypeFlag;
	}

	public String getSaaSStrmlndApprvlFlag() {
		return saaSStrmlndApprvlFlag;
	}

	public void setSaaSStrmlndApprvlFlag(String saaSStrmlndApprvlFlag) {
		this.saaSStrmlndApprvlFlag = saaSStrmlndApprvlFlag;
	}

	public String getQuoteOutputOption() {
		return quoteOutputOption;
	}

	public void setQuoteOutputOption(String quoteOutputOption) {
		this.quoteOutputOption = quoteOutputOption;
	}
	
	
    
}
