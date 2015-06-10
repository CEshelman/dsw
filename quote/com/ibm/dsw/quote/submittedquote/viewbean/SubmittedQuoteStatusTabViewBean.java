package com.ibm.dsw.quote.submittedquote.viewbean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.common.service.WorkflowDetailServiceHelper.StatusDetail;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQViewKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.util.QuoteOutputSubmissionUtil;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>SubmittedQuoteStatusTabViewBean<code> class.
 *
 * @author: doris_yuen@us.ibm.com
 *
 * Creation date: 2007-5-4
 */

public class SubmittedQuoteStatusTabViewBean extends SubmittedQuoteBaseViewBean {
    private static final String STATUS_CODE = "statusCode";

    private static final String STATUS_DESC = "statusDesc";

    private static final String STATUS_DATE = "date";

    private static final String STATUS_PEND_ACTION = "pendAction";

    private static final String STATUS_ACTION_OWNER = "actionOwner";

    protected String termReason;

    protected boolean displaySpecBidStatus = false;

    protected boolean displayQuoteStatus = false;

    protected boolean displayOrderDetails = false;

    protected boolean displayEOLPartsMessage = false;

    private transient Map orderWorkFlowStatus;

    private transient Map quoteWorkFlowStatus;

    private transient Map actionText = new HashMap();

    private transient List quoteOutput;

    private transient List customerRelatedDocInfoList;

    private transient List quotePrecheckStatusList;

    protected boolean displayQuoteOutput = false ;

    protected boolean displayCustomerRelatedDocInfo = false ;

    protected boolean accessBlockStatus = false;

    protected boolean hasParts = false;

    protected boolean displayBlockStatusLink = false;

    private String fctNonStdTermsCondsURL=null;//Edward:url.

    private transient List cntList = null;

    private transient Customer customer;

    private String qtCntctEmail = "";

    private String primCntctEmail = "";

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);

        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_ADD_APRVR_COMMENT,
                SpecialBidViewKeys.MSG_APPRVR_ACTION_ADD_APRVR_COMMENT);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_APPROVE, SpecialBidViewKeys.MSG_APPRVR_ACTION_APPROVE);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_APPRVL_PENDG,
                SpecialBidViewKeys.MSG_APPRVR_ACTION_APPRVL_PENDG);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_REJECT, SpecialBidViewKeys.MSG_APPRVR_ACTION_REJECT);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_ADD_INFO,
                SpecialBidViewKeys.MSG_APPRVR_ACTION_RETURN_FOR_ADD_INFO);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_RETURN_FOR_CHANGES,
                SpecialBidViewKeys.MSG_APPRVR_ACTION_RETURN_FOR_CHANGES);
        actionText.put(SubmittedQuoteConstants.APPRVR_ACTION_CANCEL_APPROVED_BID, SpecialBidViewKeys.MSG_APPRVR_ACTION_CANCEL_APPROVED_BID);

        termReason = params.getParameterAsString(DraftRQViewKeys.RQ_TERM_REASON);
        orderWorkFlowStatus = (Map) params.getParameter(SubmittedQuoteParamKeys.WORKFLOW_DETAIL_ORDER);
        quoteWorkFlowStatus = (Map) params.getParameter(SubmittedQuoteParamKeys.WORKFLOW_DETAIL_QUOTE);
        quoteOutput = (List) params.getParameter(SubmittedQuoteParamKeys.QUOTE_OUTPUT);
        customerRelatedDocInfoList = (List) params.getParameter(SubmittedQuoteParamKeys.CUSTOMER_RELATED_DOCUMENTS);
        setQuotePrecheckStatusList((List) params.getParameter(SubmittedQuoteParamKeys.QUOTE_PRECK_STATUS));

        if(quoteOutput != null && quoteOutput.size() > 0){
            displayQuoteOutput = true;
        }

        if(customerRelatedDocInfoList != null && customerRelatedDocInfoList.size() > 0){
            displayCustomerRelatedDocInfo = true;
        }

        if (quote != null) {

            if(quote.getSubmittedQuoteAccess() != null && quote.getSubmittedQuoteAccess().isAccessBlockStatus()){
                accessBlockStatus = true;
            }

            if(quote.getQuoteUserAccess() != null && quote.getQuoteUserAccess().isEditor() && !header.hasSaaSLineItem()){
                displayBlockStatusLink = true;
            }

            if (header != null) {
                if (isPGSFlag()) {
                	if (isPGSQuote()) {
                		displaySpecBidStatus = (header.getSpeclBidFlag() == 1);
                	} else {
                		displaySpecBidStatus = false;
                	}
                } else {
                	displaySpecBidStatus = (header.getSpeclBidFlag() == 1);
                }

                if (StringUtils.isNotBlank(header.getSapQuoteNum()) && quote.getSapPrimaryStatusList() != null
                        && quote.getSapSecondaryStatusList() != null) {
                    if ((quote.getSapPrimaryStatusList().size() + quote.getSapSecondaryStatusList().size()) > 0) {
                        displayQuoteStatus = true;
                    }
                }

                if(quote.getSubmittedQuoteAccess().isNoStatusInOneHour() || quote.getSubmittedQuoteAccess().isNoStatusOverOneHour()){
                    displayQuoteStatus = true;
                }

                if (StringUtils.isNotBlank(header.getSapIntrmdiatDocNum())) {
                    if (quote.getOrders() != null && quote.getOrders().size() > 0) {
                        displayOrderDetails = true;
                    }
                }

                if(header.getHasObsoletePartsFlag()){
                    displayEOLPartsMessage = true;
                }

                hasParts = quote.getQuoteHeader().getHasPartsFlag();
                //Edward:get url.
                if (ButtonDisplayRuleFactory.singleton().isDisplayFCTNonStdTermsConds(
        				quote.getQuoteHeader())) {
        			String areaCode = quote.getQuoteHeader().getCountry()
        					.getSpecialBidAreaCode();

        			fctNonStdTermsCondsURL = ApplicationProperties.getInstance()
        					.getFctNonStdTermsCondsURL(areaCode);
        		}

            }

            cntList = quote.getContactList();
			customer = quote.getCustomer();

			// Get contact information
			if (cntList != null && cntList.size() > 0) {
				QuoteContact qtCnt = (QuoteContact) cntList.get(0);
				if (qtCnt != null) {
					qtCntctEmail = qtCnt.getCntEmailAdr();
				}
			}
			if (customer != null) {
				primCntctEmail = customer.getCntEmailAdr();
			}
        }

    }

    public boolean isDoSpecialBidCommonInit()
    {
        return true;
    }

    public boolean isDisplaySpecialBidStatus() {
        return displaySpecBidStatus;
    }

    public boolean isDisplayQuoteStatus() {
        return displayQuoteStatus;
    }

    public boolean isDisplayOrderDetails() {
        return displayOrderDetails;
    }

    public String getTermReason() {
        return notNullString(termReason);
    }

    public String notNullString(String string) {
        return StringUtils.trimToEmpty(string);
    }

    public String getViewCustRptHostedServicesURL() {
    	if(isPGSFlag()) {
    		String url = HtmlUtil.getURLForPGSReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_HTSV_RPT_PGS);
    		StringBuffer sb = new StringBuffer(url);
            HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM_PGS, this.getSiteNumber());
            HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM_PGS, this.getAgreementNum());
            return sb.toString();
    	}else {
    		String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_HTSV_RPT);
    		StringBuffer sb = new StringBuffer(url);
            HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, this.getSiteNumber());
            HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, this.getAgreementNum());
            return sb.toString();
    	}
    }

    /**
     * @return Returns the hasParts.
     */
    public boolean isHasParts() {
        return hasParts;
    }
    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftSQBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        return SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB;
    }

    public List getQuoteStatus() {
        List result = new ArrayList();
        fillCreationStatus(result);
        fillStatus(quote.getSapPrimaryStatusList(), result);
        fillStatus(quote.getSapSecondaryStatusList(), result);
        return result;

    }

    private void fillStatus(List status, List result) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        for (Iterator iter = status.iterator(); iter.hasNext();) {
            QuoteStatus obj = (QuoteStatus) iter.next();
            Map detail = new HashMap();
            detail.put(STATUS_CODE, obj.getStatusCode());
            detail.put(STATUS_DESC, obj.getStatusCodeDesc());
            detail.put(STATUS_DATE, DateHelper.getDateByFormat(obj.getModifiedDate(), "dd MMM yyyy"));
            //append comment of "terminated by + user name" to E0007 and E0009
            if(quote.getQuoteHeader().isSalesQuote()){
	            if(SubmittedQuoteConstants.SQ_SPECIAL_BID_TERMINATED_STATUS.equals(obj.getStatusCode())
	                    ||SubmittedQuoteConstants.SQ_SPECIAL_BID_CANCELLED_STATUS.equals(obj.getStatusCode())){
	                detail.put(STATUS_PEND_ACTION, appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, SubmittedQuoteMessageKeys.TERMINATED_BY)
	                        + " " + quote.getSubmittedQuoteAccess().getCancelledBy());
	            }
            }
            if (quoteWorkFlowStatus != null) {
                StatusDetail sd = (StatusDetail) quoteWorkFlowStatus.get(obj.getStatusCode());
                if (sd != null) {
                    detail.put(STATUS_PEND_ACTION, sd.getWorkItemText());
                    detail.put(STATUS_ACTION_OWNER, sd.getRecName());
                }
            }
            result.add(detail);
        }
    }
    
    private Map generateUnderEvalStatus(QuoteHeader header)
    {
        Map detail = new HashMap();
        detail.put(STATUS_CODE, header.getQuoteStageCode());
        String desc = this.getEvaluationStatusDesc(header);
        detail.put(STATUS_DESC, desc);
        detail.put(STATUS_DATE, DateHelper.getDateByFormat(header.getEvalDate(), "dd MMM yyyy"));
        detail.put(STATUS_PEND_ACTION, "");
        detail.put(STATUS_ACTION_OWNER, "");
        return detail;
    }

    private void fillCreationStatus(List result) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        if ( quote.getQuoteHeader().isUnderEvaluation() )
        {
        	Map evalStatus = generateUnderEvalStatus(quote.getQuoteHeader());
        	result.add(evalStatus);
        	return;
        }
        String status;
        if(quote.getSubmittedQuoteAccess().isNoStatusInOneHour()){
            status = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, SubmittedQuoteMessageKeys.CREATION_IN_PROGRESS);
            Map detail = generateQuoteStatusDetailMap(null, status);
            result.add(detail);
        }
        if(quote.getSubmittedQuoteAccess().isNoStatusOverOneHour()){
            status = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, SubmittedQuoteMessageKeys.CREATION_ON_HOLD);
            Map detail = generateQuoteStatusDetailMap(null, status);
            result.add(detail);
        }
        if(quote.getQuoteHeader().isPAQuote() && quote.getQuoteHeader().isSalesQuote()){
        	if( cust.isAddiSiteCustomer() && !cust.isEnrolledCtrctNumValid()){
        		status = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, SubmittedQuoteMessageKeys.STATUS_TAB_NO_CUST_ENROLL_MSG_5);
        		Map detail = generateQuoteStatusDetailMap(null, status);
                result.add(detail);
        	}else if(quote.getSubmittedQuoteAccess().isNoCustEnroll()){
				if (!(quote.getQuoteHeader().isCSRAQuote() || quote.getQuoteHeader().isCSTAQuote())) {
                if(cust.isSTDCustomer() && header.hasNewCustomer()){
                    status = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, SubmittedQuoteMessageKeys.STATUS_TAB_NO_CUST_ENROLL_MSG);
                }else{
                    status = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, SubmittedQuoteMessageKeys.STATUS_TAB_NO_CUST_ENROLL_MSG_2);
                }
                Map detail = generateQuoteStatusDetailMap(null, status);
                result.add(detail);
				}
            }else{
                if(header.getWebCustId() > 0 || header.isCreateCtrctFlag()){
                    if ((cust.isGOVCustomer() || cust.isACACustomer() || cust.isXSPCustomer())
                            && !cust.isEnrolledCtrctTranPrcLvlValid()) {
                        status = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, SubmittedQuoteMessageKeys.STATUS_TAB_NO_CUST_ENROLL_MSG_3);
                        Map detail = generateQuoteStatusDetailMap(null, status);
                        result.add(detail);
                    }else if(cust.isAddiSiteCustomer() && !cust.isEnrolledCtrctNumValid()) {
                        status = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, SubmittedQuoteMessageKeys.STATUS_TAB_NO_CUST_ENROLL_MSG_4);
                        Map detail = generateQuoteStatusDetailMap(null, status);
                        result.add(detail);
                    }
                }
            }
        }
    }

    private Map generateQuoteStatusDetailMap(String code, String desc){
        Date currDate = new Date(System.currentTimeMillis());
        Map detail = new HashMap();
        detail.put(STATUS_CODE, code);
        detail.put(STATUS_DESC, desc);
        detail.put(STATUS_DATE, DateHelper.getDateByFormat(currDate, "dd MMM yyyy"));
        detail.put(STATUS_PEND_ACTION, "");
        detail.put(STATUS_ACTION_OWNER, "");
        return detail;
    }

    /**
     * @return Returns the orderWorkFlowStatus.
     */
    public Map getOrderWorkFlowStatus() {
        return orderWorkFlowStatus;
    }

    public Map getActionText() {
        return actionText;
    }

    public boolean getEditableFlag() {
        // always rturn false to use special bid jsp
        return false;
    }

    public String getStatusDetailExplanationURL(){
        return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_STATUS_DETEAIL_EXPLANATION);
    }

    public String getHelpImageLink(){
        return "//w3.ibm.com/ui/v8/images/icon-help-contextual-dark.gif";
    }

    public boolean isDisplayEOLPartsMessage() {
        return displayEOLPartsMessage;
    }

    public List getQuoteOutput(){
        return quoteOutput;
    }

    public String getQuoteOutputDownloadLink(){
        StringBuffer link = new StringBuffer();
        link.append(HtmlUtil.getURLForAction(ActionKeys.DOWNLOAD_ATTACHMENT));
        HtmlUtil.addURLParam(link,SubmittedQuoteParamKeys.DOWNLOAD_TYPE,SubmittedQuoteParamKeys.QUOTE_OUTPUT);
        return link.toString();
    }

    public boolean isDisplayQuoteOutput() {
        return displayQuoteOutput;
    }

    public boolean isAccessBlockStatus() {
        return accessBlockStatus;
    }

    public String getRemoveStatusLink(){
        StringBuffer link = new StringBuffer();
        link.append(HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.REMOVED_QUOTE_STATUS));
        HtmlUtil.addURLParam(link,ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY),SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB);
        HtmlUtil.addURLParam(link,SubmittedQuoteParamKeys.PARAM_QUOTE_NUM,header.getWebQuoteNum());
        return link.toString();
    }

    public String getSendNewCustomerMailLink(){
        StringBuffer link = new StringBuffer();
        link.append(HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SEND_NEW_CUSTOMER_EMAIL));
        HtmlUtil.addURLParam(link,ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY),SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_STATUS_TAB);
        HtmlUtil.addURLParam(link,SubmittedQuoteParamKeys.PARAM_QUOTE_NUM,header.getWebQuoteNum());
        return link.toString();
    }
    public String getOrderHistoryLink(){
    	return ApplicationProperties.getInstance().getOrderHistoryDetailURL();
    }

    /*
     * Belows for quote submission section
     */

    public boolean isRequestedIBMCustomerNumber() {
        return QuoteOutputSubmissionUtil.isRequestedIBMCustomerNumber(quote);
    }

    public boolean getRequestedIBMCustomerNumberFlagValue() {
        return QuoteOutputSubmissionUtil.getRequestedIBMCustomerNumberFlagValue(quote) ;
    }

    public boolean isPreCreditCheckOptionDisplay() {
    	return QuoteOutputSubmissionUtil.isPreCreditCheckOptionDisplay(quote);
    }

    public boolean getPreCreditCheckOptionDisplayFlagValue() {
        return QuoteOutputSubmissionUtil.getPreCreditCheckOptionDisplayFlagValue(quote);
    }

    public boolean isTaxOnQuoteOutput() {
        return QuoteOutputSubmissionUtil.isTaxOnQuoteOutput(quote);
    }

    public boolean getTaxOnQuoteOutputFlagValue() {
        return QuoteOutputSubmissionUtil.getTaxOnQuoteOutputFlagValue(quote);
    }

    public boolean isLiPrcOrPointsOnQuoteOutput() {
        return QuoteOutputSubmissionUtil.isLiPrcOrPointsOnQuoteOutput(quote);
    }

    public boolean isInclFirmOrdLtr() {
        return QuoteOutputSubmissionUtil.isInclFirmOrdLtr(quote);
    }

    public boolean isDisplayInclFirmOrdLtr() {
        return QuoteOutputSubmissionUtil.isDisplayInclFirmOrdLtr(quote);
    }
    
    public boolean isInclPaymentSchedule() {
        return QuoteOutputSubmissionUtil.isInclPaymentSchedule(quote);
    }
    
    public boolean isDisplayInclPaymentSchedule() {
        return QuoteOutputSubmissionUtil.isDisplayInclPaymentSchedule(quote);
    }
    
    public boolean getLiPrcOrPointsOnQuoteOutputFlagValue() {
        return QuoteOutputSubmissionUtil.getLiPrcOrPointsOnQuoteOutputFlagValue(quote);
    }

    public boolean isEmailQuoteContact() {
        return QuoteOutputSubmissionUtil.isEmailQuoteContact(quote);
    }

    public boolean getEmailQuoteContactFlagValue() {
        return QuoteOutputSubmissionUtil.getEmailQuoteContactFlagValue(quote);
    }

    public boolean isEmailQuotePrimaryContact() {
        return QuoteOutputSubmissionUtil.isEmailQuotePrimaryContact(quote);
    }

    public boolean getEmailQuotePrimaryContactFlagValue(){
        return QuoteOutputSubmissionUtil.getEmailQuotePrimaryContactFlagValue(quote);
    }

    public boolean isEmailQuoteToAddress() {
        return QuoteOutputSubmissionUtil.isEmailQuoteToAddress(quote);
    }

    public boolean getEmailQuoteToAddressFlagValue() {
        return QuoteOutputSubmissionUtil.getEmailQuoteToAddressFlagValue(quote);
    }

    public boolean isDisplayPAOBlockFlag() {
        return QuoteOutputSubmissionUtil.isDisplayPAOBlockFlag(quote);
    }

    public boolean getDisplayPAOBlockFlagVaule() {
        return QuoteOutputSubmissionUtil.getDisplayPAOBlockFlagVaule(quote);
    }

    public boolean isEmailRQContact() {
        return QuoteOutputSubmissionUtil.isEmailRQContact(quote);
    }

    public boolean getEmailRQContactFlagValue() {
        return QuoteOutputSubmissionUtil.getEmailRQContactFlagValue(quote);
    }

    public boolean isEmailPartnerAddress() {
        return QuoteOutputSubmissionUtil.isEmailPartnerAddress(quote);
    }

    public boolean getEmailPartnerY9ListAddressFlagValue(){
        return QuoteOutputSubmissionUtil.getEmailPartnerY9ListAddressFlagValue(quote);
    }
    public boolean getEmailPartnerInputAddressFlagValue(){
        return QuoteOutputSubmissionUtil.getEmailPartnerInputAddressFlagValue(quote);
    }

    public boolean isDisplayQuoteSubmissionQuoteOutput() {
        return QuoteOutputSubmissionUtil.isDisplayQuoteSubmissionQuoteOutput(quote);
    }

    public boolean isNoPartnerY9Email() {
        return QuoteOutputSubmissionUtil.isNoPartnerY9Email(quote);
    }

    public String getPartnerY9EmailList() {
        return QuoteOutputSubmissionUtil.getPartnerY9EmailList(quote);
    }

    /**
     * check if it should display "E-mail the approved special bid business
     * partner notification to the following address" input box
     *
     * @return true for display, false for not display
     */
    public boolean isDisplayParterAddressInput() {
        return QuoteOutputSubmissionUtil.isDisplayParterAddressInput(quote);
    }

    /**
     * @return Returns the customerRelatedDocInfoList.
     */
    public List getCustomerRelatedDocInfoList() {
        return customerRelatedDocInfoList;
    }
    /**
     * @param customerRelatedDocInfoList The customerRelatedDocInfoList to set.
     */
    public void setCustomerRelatedDocInfoList(List customerRelatedDocInfoList) {
        this.customerRelatedDocInfoList = customerRelatedDocInfoList;
    }
    /**
     * @return Returns the displayCustomerRelatedDocInfo.
     */
    public boolean isDisplayCustomerRelatedDocInfo() {
        return displayCustomerRelatedDocInfo;
    }
    /**
     * @param displayCustomerRelatedDocInfo The displayCustomerRelatedDocInfo to set.
     */
    public void setDisplayCustomerRelatedDocInfo(boolean displayCustomerRelatedDocInfo) {
        this.displayCustomerRelatedDocInfo = displayCustomerRelatedDocInfo;
    }

	public void setQuotePrecheckStatusList(List quotePrecheckStatusList) {
		this.quotePrecheckStatusList = quotePrecheckStatusList;
	}

	public List getQuotePrecheckStatusList() {
		return quotePrecheckStatusList;
	}

	public boolean isDisplayBlockStatusLink() {
        return displayBlockStatusLink;
    }

    public void setDisplayBlockStatusLink(boolean displayBlockStatusLink) {
        this.displayBlockStatusLink = displayBlockStatusLink;
    }

    public boolean isDisplayFctNonStdTermsConds(){
    	return QuoteOutputSubmissionUtil.isDisplayFctNonStdTermsConds(quote);
    }

    public boolean isFctNonStdTermsCondsYesChecked(){
    	return QuoteOutputSubmissionUtil.isFctNonStdTermsCondsYesChecked(quote);
    }

    public boolean isFctNonStdTermsCondsNoChecked(){
    	return QuoteOutputSubmissionUtil.isFctNonStdTermsCondsNoChecked(quote);
    }

    public String getFctNonStdTermsCondsURL(){
    	return fctNonStdTermsCondsURL;
    }

    public String getQtCntctEmail() {
        return StringUtils.isBlank(qtCntctEmail) ? "" : "(" + StringUtils.trimToEmpty(qtCntctEmail) + ")";
    }

    public String getPrimCntctEmail() {
        return StringUtils.isBlank(primCntctEmail) ? "" : "(" + StringUtils.trimToEmpty(primCntctEmail) + ")";
    }

    public boolean isCheckedQuoteOutputTypeFlag(){
		return ("RATE".equalsIgnoreCase(quote.getQuoteHeader().getQuoteOutputType()));
	}
    
    public boolean isDisplayQuoteSubmitQuoteOutputType(){
    	return QuoteOutputSubmissionUtil.isDisplayQuoteSubmitQuoteOutputType(quote);
    }
    
    public boolean isBudgetaryQuoteOutput(){
		return ((QuoteOutputSubmissionUtil.isPA(quote)||QuoteOutputSubmissionUtil.isPAE(quote) || QuoteOutputSubmissionUtil.isFCT(quote))
				&& QuoteOutputSubmissionUtil.hasSaaSLineItem(quote)
				&& quote.getQuoteHeader().getFulfillmentSrc().equalsIgnoreCase(QuoteConstants.FULFILLMENT_DIRECT));
					
	}
    
    public boolean getBudgetaryQuoteOutputFlagValue(){
    	 return QuoteOutputSubmissionUtil.getBudgetaryQuoteOutputFlagValue(quote);
    }
}
