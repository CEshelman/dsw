package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.process.Amendment;
import com.ibm.dsw.quote.draftquote.process.ToU;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

public class DisplayDraftToUListViewBean extends BaseViewBean{

	transient Map<String, String> noAssociatedPartList = null;
	
	transient Set<ToU> touList = null;
	
	transient List<Amendment> amendmentList = null;
	
	transient Map<String, List<Amendment>> tou2amendmentMap = null;
	
	private String returnCode;
	
	
	
	private String webQuoteNum;
	
	private String lob;

	private boolean isOnlySaaSParts;

	//private boolean isChannelQuote;
	
	private boolean isSubmittedQuote;
	
	private int saasTermCondCatFlag;
	
	private String currentContractNum;
	
	private String PAContractNum;
	
	private String CSRAContractNum;
	
	private String agrmtTypeCode;
	
	private String quoteStatus;
	
	private boolean isSpecialBid;
	
	private String attachNames;
	
	private String actionCode;
	
	private int CSATermsCount;
	
	public Map getNoAssociatedPartList() {
		return noAssociatedPartList;
	}


	public String getAttachNames() {
		return attachNames;
	}


	public Set<ToU> getTouList() {
		return touList;
	}

	public List<Amendment> getAmendmentList() {
		return amendmentList;
	}

	public Map getTou2amendmentMap() {
		return tou2amendmentMap;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public String getLob() {
		return lob;
	}

	public boolean isOnlySaaSParts() {
		return isOnlySaaSParts;
	}

//	public boolean isChannelQuote() {
//		return isChannelQuote;
//	}

	public boolean isSubmittedQuote() {
		return isSubmittedQuote;
	}
	
	public boolean isSpecialBid() {
		return isSpecialBid;
	}

	public String getActionCode() {
		return actionCode;
	}


	@Override
	public void collectResults(Parameters params) throws ViewBeanException {
		super.collectResults(params);
		
		lob = (String) params.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS);
		isOnlySaaSParts = (Boolean) params.getParameter(ParamKeys.IS_ONLY_SAAS_PARTS);
		//isChannelQuote = (Boolean) params.getParameter(ParamKeys.IS_CHANNEL_QUOTE);
		isSubmittedQuote = (Boolean) params.getParameter(ParamKeys.IS_SUBMITTED_QUOTE);
		webQuoteNum = (String) params.getParameter(ParamKeys.PARAM_QUOTE_NUM);
        returnCode = (String) params.getParameter(DraftQuoteParamKeys.PARAM_RETURN_CODE);
        if(null != params.getParameter(DraftQuoteParamKeys.SAAS_TERM_CONDCAT_FLAG)){
        	saasTermCondCatFlag = (Integer) params.getParameter(DraftQuoteParamKeys.SAAS_TERM_CONDCAT_FLAG);
        }
        currentContractNum = (String) params.getParameter(DraftQuoteParamKeys.PARAM_CURRENT_CONTRACT_NUM);
        PAContractNum = (String) params.getParameter(DraftQuoteParamKeys.PARAM_PA_CONTRACT_NUM);
        CSRAContractNum = (String) params.getParameter(DraftQuoteParamKeys.PARAM_CSRA_CONTRACT_NUM);
        agrmtTypeCode = (String) params.getParameter(ParamKeys.PARAM_AGREEMENT_TYPE);
        quoteStatus = (String) params.getParameter(ParamKeys.QUOTE_STATUS);
        actionCode = (String) params.getParameter(DraftQuoteParamKeys.ACTION_CODE);
        CSATermsCount = (Integer) params.getParameter(DraftQuoteParamKeys.PARAM_CSA_TERMS_COUNT);
        
        /*when remove amendment, param isSpecialBid is null*/
        if(params.getParameter(ParamKeys.IS_SPECIAL_BID) != null)
        	isSpecialBid = (Boolean) params.getParameter(ParamKeys.IS_SPECIAL_BID);
		attachNames = (String) params.getParameter("attachNames");
		Map results = (Map) params.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
        
        if (results != null) {
        	noAssociatedPartList = (Map) results.get("noAssociatedPartList");
        	touList = (Set) results.get("touList");
        	amendmentList = (List) results.get("amendmentList");
        }
        
        tou2amendmentMap = new HashMap<String, List<Amendment>>();
        List<Amendment> tmpList;
        if (amendmentList != null) {
            for (Amendment amendment : amendmentList) {
        		
        		tmpList = tou2amendmentMap.get(amendment.getTermsCondsTypeCode()+amendment.getDocID());
        		if (null != tmpList) {
        			tmpList.add(amendment);
        		} else {
        			tmpList = new ArrayList<Amendment>();
        			tmpList.add(amendment);
        			tou2amendmentMap.put(amendment.getTermsCondsTypeCode()+amendment.getDocID(), tmpList);
        		}
            }
        }
        SecurityUtil.htmlEncode(lob);
        SecurityUtil.htmlEncode(String.valueOf(isOnlySaaSParts));
        SecurityUtil.htmlEncode(String.valueOf(isSubmittedQuote));
        SecurityUtil.htmlEncode(String.valueOf(webQuoteNum));
        SecurityUtil.htmlEncode(String.valueOf(returnCode));
        SecurityUtil.htmlEncode(String.valueOf(currentContractNum));
        SecurityUtil.htmlEncode(String.valueOf(PAContractNum));
        SecurityUtil.htmlEncode(String.valueOf(CSRAContractNum));
        SecurityUtil.htmlEncode(String.valueOf(agrmtTypeCode));
        SecurityUtil.htmlEncode(String.valueOf(quoteStatus));
        SecurityUtil.htmlEncode(String.valueOf(actionCode));
        SecurityUtil.htmlEncode(String.valueOf(CSATermsCount));
	}

	public int getSaasTermCondCatFlag() {
		return saasTermCondCatFlag;
	}

	public void setSaasTermCondCatFlag(int saasTermCondCatFlag) {
		this.saasTermCondCatFlag = saasTermCondCatFlag;
	}
	
	public String getCurrentContractNum() {
		return currentContractNum;
	}

	public void setCurrentContractNum(String currentContractNum) {
		this.currentContractNum = currentContractNum;
	}

	public String getAgrmtTypeCode() {
		return agrmtTypeCode;
	}

	public void setAgrmtTypeCode(String agrmtTypeCode) {
		this.agrmtTypeCode = agrmtTypeCode;
	}

	public String getQuoteStatus() {
		return quoteStatus;
	}

	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}

	public String getPAContractNum() {
		return PAContractNum;
	}


	public void setPAContractNum(String pAContractNum) {
		PAContractNum = pAContractNum;
	}


	public String getCSRAContractNum() {
		return CSRAContractNum;
	}


	public void setCSRAContractNum(String cSRAContractNum) {
		CSRAContractNum = cSRAContractNum;
	}


	public int getCSATermsCount() {
		return CSATermsCount;
	}


	public void setCSATermsCount(int cSATermsCount) {
		CSATermsCount = cSATermsCount;
	}
}
