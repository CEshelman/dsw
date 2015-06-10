package com.ibm.dsw.quote.draftquote.viewbean;

import is.domainx.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteViewKeys;
import com.ibm.dsw.quote.submittedquote.util.QuoteOutputSubmissionUtil;
import com.ibm.dsw.spbid.common.ApprovalGroup;
import com.ibm.dsw.spbid.common.ApprovalGroupMember;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayQuoteSubmissionViewbean</code> class .
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 12, 2007
 */
public class DisplayQuoteSubmissionViewbean extends BaseViewBean {

    public String quoteSubmissionURL;

    public boolean isRenewalQuote = false;

    public boolean isOpenedStatus = false;
    
    public boolean isSpBid = false;

    public boolean isOutPutOptionsSuppressed = false;

    private boolean isDirect;

    private Quote quote;
    
    private transient Customer customer;

    private ApprovalGroup[] groupArray;

    private boolean displayApproversFlag;
    
    private boolean displayNoApprvlReqrdMsg = false;

    private boolean canSubmitted = true;
    
    private boolean isChannel;

    private boolean isNotSpec;
    
    private boolean isFCT = false;
    
    private boolean isPPSS = false;
    
    private boolean isDirectELAQuote = false;
    
    private boolean isCredOrderRebillQuote = false;
    
    private boolean isPreCreditCheckOptionDisplay = false;
    
    private String validateEmailFields;
    
    private Boolean sendNoApprvrNotif;
    
    private Boolean sendMultiGrpsNotif;
    
    private Boolean sendOneLvlApprvrNotif;
    
    private boolean hasAddiSiteCustomer = false;
    
    private boolean hasNewCust = false;
    
    private QuoteUserSession salesRep = null;
    
    private User user = null;
    
    private boolean isOEM = false;
    
    private boolean hasLotusLiveItem = false;
    
    private String searchCriteriaUrlParam;
    
    private String requestorEMail;
    
    
    private String preCreditCheckedQuoteNum;
    
    private boolean isPreCreditCheckBoxAvailable = false;
    
    private transient List cntList = null;
    
    private String qtCntctEmail = "";
    
    private String primCntctEmail = "";
    
    private String downloadFileURL = null;
    
    private String fctNonStdTermsCondsURL=null;
    
    private boolean hasSaaSLineItem = false;
    
    private boolean noApprovalRequire = false;
    
    
    private String qtCntctFirstName = "";
    private String qtCntctLastName = "";
    private String qtCntctPhone = "";
    private String qtCntctFax = "";
    private String qtsapCntPrtnrFuncCode = "";
    
	public String getDownloadFileURL()
	{
		return downloadFileURL;
	}

    public void collectResults(Parameters params) throws ViewBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(params);
        downloadFileURL = HtmlUtil.getURLForAction(ActionKeys.DOWNLOAD_ATTACHMENT);
        sendNoApprvrNotif = (Boolean) params.getParameter(DraftQuoteParamKeys.PARAM_SEND_NO_APPRVR_NOTIF);
        sendMultiGrpsNotif = (Boolean) params.getParameter(DraftQuoteParamKeys.PARAM_SEND_MULTI_GRPS_NOTIF);
        sendOneLvlApprvrNotif = (Boolean) params.getParameter(DraftQuoteParamKeys.PARAM_SEND_ONE_LVL_APPRVR_NOTIF);
        noApprovalRequire = params.getParameter(DraftQuoteParamKeys.NO_APPROVAL_REQUIRE)!=null;
        quote = (Quote) params.getParameter(ParamKeys.PARAM_QUOTE_OBJECT);
        salesRep = (QuoteUserSession) params.getParameter(ParamKeys.PARAM_QUOTE_USER_SESSION);
        user = (User) params.getParameter(ParamKeys.PARAM_USER_OBJECT);
        
        customer = quote.getCustomer();
        cntList = quote.getContactList();
        
     // Get contact information
        if (cntList != null && cntList.size() > 0) {
            QuoteContact qtCnt = (QuoteContact) cntList.get(0);
            if (qtCnt != null) {
            	
            	qtCntctFirstName = qtCnt.getCntFirstName();
                qtCntctLastName = qtCnt.getCntLastName();
                qtCntctPhone = qtCnt.getCntPhoneNumFull();
                qtCntctFax = qtCnt.getCntFaxNumFull();
            	
	            qtCntctEmail = qtCnt.getCntEmailAdr();
	            qtsapCntPrtnrFuncCode = qtCnt.getCntPrtnrFuncCode();
            }
        }

        quoteSubmissionURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.SUBMIT_QUOTE_FOR_CONFIRMATION);
        
        isRenewalQuote = quote.getQuoteHeader().getQuoteTypeCode().equals(QuoteConstants.QUOTE_TYPE_RENEWAL);
        if (isRenewalQuote) {
            isOpenedStatus = quote.containsWebPrimaryStatus(QuoteConstants.RENEWALSTATUS_CODE_OPEN)
                    || quote.containsWebPrimaryStatus(QuoteConstants.RENEWALSTATUS_CODE_BALOPEN);
        }
        isOutPutOptionsSuppressed = isRenewalQuote && !isOpenedStatus;

        isSpBid = quote.getQuoteHeader().getSpeclBidFlag() == 1;
        String fulfillmentSrc = quote.getQuoteHeader().getFulfillmentSrc();
        isDirect = fulfillmentSrc.equals(QuoteConstants.FULFILLMENT_DIRECT);
        isChannel = fulfillmentSrc.equals(QuoteConstants.FULFILLMENT_CHANNEL);
        isNotSpec = fulfillmentSrc.equals(QuoteConstants.FULFILLMENT_NOT_SPECIFIED);
        isFCT = quote.getQuoteHeader().isFCTQuote();
        isPPSS = quote.getQuoteHeader().isPPSSQuote();
        isOEM = quote.getQuoteHeader().isOEMQuote();
        List groups = (List) params.getParameter(DraftQuoteParamKeys.PARAM_APPROVAL_GROUPS);
        hasLotusLiveItem = quote.getQuoteHeader().hasLotusLiveItem();
        hasSaaSLineItem = quote.getQuoteHeader().hasSaaSLineItem();
        groupArray = new ApprovalGroup[QuoteConstants.APPROVAL_TYPE_MAX_LEVLE + 1];
        if (null != groups) {
            Iterator iterator = groups.iterator();
            while (iterator.hasNext()) {
                ApprovalGroup group = (ApprovalGroup) iterator.next();
                groupArray[group.getType().getLevel()] = group;
                this.fillApprGrpInfo(group);
            }
        }
        if (quote.getQuoteHeader().getSpeclBidFlag() == 1 
                && quote.getQuoteHeader().getApprovalRouteFlag() == 1
                && !quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()
                && !quote.getQuoteHeader().isCopiedForOutputChangeFlag()
                ) {
            displayApproversFlag = true;
        }
        Boolean isRuleEngineAvailable = (Boolean) params.getParameter(DraftQuoteParamKeys.RULE_ENGINE_NOT_AVAILABLE);
        if (isRuleEngineAvailable != null && !isRuleEngineAvailable.booleanValue()) {
            this.canSubmitted = false;
        }
        
        displayNoApprvlReqrdMsg = (quote.getQuoteHeader().getRenwlQuoteSpeclBidFlag() == 1
                && (quote.getQuoteHeader().getSpeclBidFlag() == 0)
                        || (quote.getQuoteHeader().getSpeclBidFlag() == 1
                                && quote.getQuoteHeader().getApprovalRouteFlag() == 0));
        
        // Set direct ELA quote flag
        isDirectELAQuote = quote.getQuoteHeader().isELAQuote() 
        	&& quote.getQuoteHeader().getFulfillmentSrc().equalsIgnoreCase(QuoteConstants.FULFILLMENT_DIRECT);
        
        if (quote.getQuoteHeader().getSpeclBidFlag() == 1) {
            isCredOrderRebillQuote = quote.getSpecialBidInfo().isCredAndRebill();
        }

        isPreCreditCheckOptionDisplay = ButtonDisplayRuleFactory.singleton().isPreCreditCheckOptionDisplay(
                quote.getQuoteHeader());

        if (customer != null) {
            hasAddiSiteCustomer = customer.isAddiSiteCustomer();
            hasNewCust = quote.getQuoteHeader().hasNewCustomer() && !quote.getQuoteHeader().hasExistingCustomer();
            primCntctEmail = customer.getCntEmailAdr();
        }

        this.searchCriteriaUrlParam = params.getParameterAsString(ParamKeys.PARAM_CUST_ACT_URL_PARAM);
        this.requestorEMail = params.getParameterAsString(ParamKeys.PARAM_CUST_ACT_REQUESTOR);
        
        preCreditCheckedQuoteNum = params.getParameterAsString(ParamKeys.PRE_CREDIT_CHECKED_QUOTE_NUM);
        if (ButtonDisplayRuleFactory.singleton().isDisplayFCTNonStdTermsConds(
				quote.getQuoteHeader())) {
			String areaCode = quote.getQuoteHeader().getCountry()
					.getSpecialBidAreaCode();
			logContext.debug(this, "areaCode=" + areaCode);

			fctNonStdTermsCondsURL = ApplicationProperties.getInstance()
					.getFctNonStdTermsCondsURL(areaCode);
		}
        logContext.debug(this, "canSubmitted=" + canSubmitted);
        logContext.debug(this, "isRequestedIBMCustomerNumber=" + isRequestedIBMCustomerNumber());
        logContext.debug(this, "isTaxOnQuoteOutput=" + isTaxOnQuoteOutput());
        logContext.debug(this, "isFCTToPA=" + isFCTToPA());
        logContext.debug(this, "isPA=" + QuoteOutputSubmissionUtil.isPA(quote));
        logContext.debug(this, "isPAE=" + QuoteOutputSubmissionUtil.isPAE(quote));
        logContext.debug(this, "isFCT=" + QuoteOutputSubmissionUtil.isFCT(quote));
        logContext.debug(this, "isEmailQuoteContact=" + isEmailQuoteContact());
        logContext.debug(this, "isEmailRQContact=" + isEmailRQContact());
        logContext.debug(this, "isEmailQuotePrimaryContact=" + isEmailQuotePrimaryContact());
        logContext.debug(this, "isEmailQuoteToAddress=" + isEmailQuoteToAddress());
        logContext.debug(this, "isDisplayApprovers=" + isDisplayApprovers());
        logContext.debug(this, "isRenewalQuote" + isRenewalQuote);
        logContext.debug(this, "isOpenStatus" + isOpenedStatus);
        logContext.debug(this, "isShowSpBidDescText=" + isShowSpBidDescText());
        logContext.debug(this, "isOutPutOptionsSuppressed=" + isOutPutOptionsSuppressed());
        logContext.debug(this, "isShowNonSpBidDescChannelText=" + isShowNonSpBidDescChannelText());
        logContext.debug(this, "isShowSpBidDescChannelText=" + isShowSpBidDescChannelText());
        logContext.debug(this, "isDirectELAQuote=" + isDirectELAQuote);
        logContext.debug(this, "isPreCreditCheckOptionDisplay=" + isPreCreditCheckOptionDisplay);
        logContext.debug(this, "searchCriteriaUrlParam=" + this.searchCriteriaUrlParam);
        logContext.debug(this, "requestorEMail=" + this.requestorEMail);
        logContext.debug(this, "preCreditCheckedQuoteNum=" + preCreditCheckedQuoteNum);
        logContext.debug(this, "fctNonStdTermsCondsURL=" + fctNonStdTermsCondsURL);
        logContext.debug(this, "isBudgetaryQuoteOutput=" + isBudgetaryQuoteOutput());
    }

	public DisplayQuoteSubmissionViewbean() {
        super();
    }

    /*
    public boolean isRequestedIBMCustomerNumber() {
        boolean gsaStatusFlag = customer == null ? false : customer.getGsaStatusFlag();
        
        // A new customer was created as the quote customer and it is not US Federal customer,
        // or an existing customer was selected, and that customer does not have an ICN.
        if ((quote.getQuoteHeader().hasNewCustomer() || (quote.getQuoteHeader().hasExistingCustomer() && StringUtils
                .isBlank(quote.getCustomer().getIbmCustNum())))
                && !gsaStatusFlag)
            return true;
        else
            return false;
    }
    */
    
    
    
    public boolean isTaxOnQuoteOutput() {
        return !isRenewalQuote ;
    }
    
    public boolean isLiPrcOrPointsOnQuoteOutput() {
        return QuoteOutputSubmissionUtil.isLiPrcOrPointsOnQuoteOutput(quote);
    }

	

	

	
	
	
	
	public boolean isCheckedQuoteOutputTypeFlag(){
		return ("RATE".equalsIgnoreCase(quote.getQuoteHeader().getQuoteOutputType()));
	}
	
	/**
	 * justify whether the lob is FCT-to-PA migration sales quotes
	 * @param no
	 * 
	 * @return true/false
	 */
	public boolean isFCTToPA() {

		return ((quote.getQuoteHeader().isPAEQuote() || quote.getQuoteHeader()
				.isPAQuote()) && quote.getQuoteHeader().isMigration());
	}

	
	
    public boolean isEmailQuoteContact() {
        return ((!isRenewalQuote && isDirect) || isShowSendQuoteToCustText()) ;
    }

    public boolean isEmailRQContact() {
        return (isRenewalQuote && (isDirect || isNotSpec) && isOpenedStatus && !isFCT);
    }

    public boolean isEmailQuotePrimaryContact() {
        return (((!isRenewalQuote && (isDirect || isNotSpec)) 
                || (isRenewalQuote && (isDirect || isNotSpec) && isOpenedStatus && !isFCT))
                || isShowSendQuoteToCustText()) ;
    }

    public boolean isEmailQuoteToAddress() {        
        return ((!isRenewalQuote && (isDirect || isNotSpec))
                || (isRenewalQuote && (isDirect || isNotSpec) && isOpenedStatus && !isFCT)
                || isShowSendQuoteToCustText()) ;
    }
    
    public boolean isEmailPartnerAddress() {
        return (quote.getQuoteHeader().isPAQuote() || quote.getQuoteHeader().isPAEQuote() || quote.getQuoteHeader().isOEMQuote() || quote.getQuoteHeader().isSSPQuote())
				&& isChannel && isSpBid 
				&& !quote.getQuoteHeader().isResellerToBeDtrmndFlag() 
				&& !quote.getQuoteHeader().isDistribtrToBeDtrmndFlag()
				;
    }
    public boolean isShowSendQuoteToCustText() {
        return !isRenewalQuote && isChannel && (quote.getQuoteHeader().isResellerToBeDtrmndFlag() || quote.getQuoteHeader().isDistribtrToBeDtrmndFlag());
    }
    public boolean isNoPartnerY9Email() {
        List emailList = new ArrayList();
        String distChannelCode = quote.getQuoteHeader().getSapDistribtnChnlCode();
        if (QuoteConstants.DIST_CHNL_HOUSE_ACCOUNT.equals(distChannelCode)){
        	if (quote.getReseller()==null){
        		return true;
        	} else {
        		 emailList = quote.getReseller().getY9EmailList();
        	}
            
        }else if (QuoteConstants.DIST_CHNL_DISTRIBUTOR.equals(distChannelCode)){
        	if (quote.getPayer()==null){
        		return true;
        	} else {
        		emailList = quote.getPayer().getY9EmailList();
        	}
            
        }
        if (emailList == null || emailList.size() == 0){
            return true;
        }
        return false;        
    }
    public String getPartnerY9EmailList() {
        return QuoteOutputSubmissionUtil.getPartnerY9EmailList(quote);
    }
    
    public String getDisplayTabAction() {
        return null;
    }

    public String getPostTabAction() {
        return null;
    }

    public ApprovalGroup[] getGroupArray() {
        return groupArray;
    }

    public boolean isDisplayApprovers() {
        return displayApproversFlag;
    }

    public boolean isCanSubmitted() {
        return canSubmitted;
    }

    public boolean isShowSpBidDescText() {
        boolean result = this.isSpBid && this.isDirect && !this.isRenewalQuote;
        return result;
    }

    public boolean isOutPutOptionsSuppressed() {
        return isOutPutOptionsSuppressed;
    }

    public boolean isRenewalQuote() {
        return isRenewalQuote;
    }
    public boolean isShowCustomizedTextForQuoteCoverMail(){
        return !isRenewalQuote && this.isDirect;
    }

    public Quote getQuote() {
        return quote;
    }
    public boolean isShowNonSpBidDescText() {
        boolean result = !this.isSpBid && (this.isDirect || this.isNotSpec) && !this.isRenewalQuote;
        return result;
    }
    public boolean isShowSpBidDescChannelText() {
        boolean result = this.isSpBid && this.isChannel && !this.isRenewalQuote;
        return result;
    }
    public boolean isShowNonSpBidDescChannelText() {
        boolean result = !this.isSpBid && this.isChannel && !this.isRenewalQuote;
        return result;
    }
    public boolean isDisplayNoApprvlReqrdMsg() {
        return displayNoApprvlReqrdMsg;
    }
    
    public boolean isDisplayQuoteOutput() {        
        return QuoteOutputSubmissionUtil.isDisplayQuoteSubmissionQuoteOutput(quote);
    }
    
    public String getValidateEmailFields() {
    	return ( isEmailPartnerAddress() && !isNoPartnerY9Email() && isDisplayQuoteOutput() )  ? "1" : "0";
    }
    
    public boolean isDisplayPAOBlockFlag() {        
        return QuoteOutputSubmissionUtil.isDisplayPAOBlockFlag(quote);
    }

    public boolean isDisablePAOQuestion() {
    	//return ButtonDisplayRuleFactory.singleton().isDisablePAOQuestion(quote.getQuoteHeader());
    	return false;
    }
    
    public boolean isPreCreditCheckOptionDisplay() {
        return isPreCreditCheckOptionDisplay;
    }

    public boolean isRequestedIBMCustomerNumber() {
        return QuoteOutputSubmissionUtil.isRequestedIBMCustomerNumber(quote);
    }
    
    public String getSendMultiGrpsNotif() {
        boolean send = sendMultiGrpsNotif == null ? false : sendMultiGrpsNotif.booleanValue();
        return send ? "true" : "false";
    }

    public String getSendNoApprvrNotif() {
        boolean send = sendNoApprvrNotif == null ? false : sendNoApprvrNotif.booleanValue();
        return send ? "true" : "false";
    }

    public String getSendOneLvlApprvrNotif() {
        boolean send = sendOneLvlApprvrNotif == null ? false : sendOneLvlApprvrNotif.booleanValue();
        return send ? "true" : "false";
    }
    
    public boolean isHasAddiSiteCustomer() {
        return hasAddiSiteCustomer;
    }
    
    public boolean isHasNewCust() {
        return hasNewCust;
    }
    
    public String needSendAddiMailToCreator() {
        boolean needSend = CommonServiceUtil.needSendAddiMailToCreator(quote.getQuoteHeader());
        return String.valueOf(needSend);
    }
    
    public String getCreatorEmail() {
        String userEmail = user == null ? "" : StringUtils.trimToEmpty(user.getEmail());
        String salesRepEmail = salesRep == null ? "" : StringUtils.trimToEmpty(salesRep.getEmailAddress());
        String creatorEmail = StringUtils.isEmpty(salesRepEmail) ? userEmail : salesRepEmail;

        return creatorEmail;
    }
    
    protected void fillApprGrpInfo(ApprovalGroup group) {
        if (group == null || group.getMembers() == null)
            return;
        
        for (int i = 0; i < group.getMembers().length; i++) {
            fillApprvrInfo(group.getMembers()[i]);
        }
    }
    
    protected void fillApprvrInfo(ApprovalGroupMember member) {
        if (StringUtils.isBlank(member.getEmail()))
            return;
        
        if (StringUtils.isBlank(member.getFirstName()) && StringUtils.isBlank(member.getLastName())) {
            String email = member.getEmail();
            BluePageUser bUser = BluePagesLookup.getBluePagesInfo(email);
            
            if (bUser != null) {
                member.setFirstName(StringUtils.trimToEmpty(bUser.getFirstName()));
                member.setLastName(StringUtils.trimToEmpty(bUser.getLastName()));
            }
        }
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
    
    public boolean isDisplaySuppressPAEnrollEmail() {
        //fix PL JKEY-86AP9N 
    	//fix PL The sections for new PA/PAE customer should be hidden for CSA quote because CSA customer will not have access on SWSO
        return  !(quote.getQuoteHeader().isCSTAQuote() || quote.getQuoteHeader().isCSRAQuote())
        		 && (quote.getQuoteHeader().isPAQuote() || quote.getQuoteHeader().isPAEQuote()) 
                 && (hasNewCust //new created quote with new customer, no sold_to_cust_num yet
                      || (quote.getQuoteHeader().hasNewCustomer() && (quote.getCustomer().getSupprsPARegstrnEmailFlag() == 1)));//sold_to_cust_num is copied from original quote with new customer
    }
    
    public boolean isOrignFromCustChgReqFlag(){
        return QuoteConstants.QUOTE_STAGE_CODE_RQCHGREQ.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode());
    }
    
    public String getCustEmailAddr(){
        return quote.getCustomer().getCntEmailAdr();
    }
    
    public String getIbmGlobalCreditSystemURL(){
        return ApplicationProperties.getInstance().getIbmGlobalCreditSystemURL();
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
    /**
     * @return Returns the requestorEMail.
     */
    public String getRequestorEMail() {
        if (StringUtils.isNotBlank(requestorEMail)) {
            return HtmlUtil.urlDecode(requestorEMail);
        } else {
            return "";
        }
    }
    /**
     * @param requestorEMail The requestorEMail to set.
     */
    public void setRequestorEMail(String requestorEMail) {
        this.requestorEMail = requestorEMail;
    }
    
    public boolean isDisplayGCSURL(){
        return ButtonDisplayRuleFactory.singleton().isDisplayGCSURL(quote.getQuoteHeader());
    }
    
    
    public String getPreCreditCheckedQuoteNum(){
        return preCreditCheckedQuoteNum;
    }
    
    public boolean isPreCreditCheckBoxAvailable(){
        if("".equals(preCreditCheckedQuoteNum) || preCreditCheckedQuoteNum == null){
            isPreCreditCheckBoxAvailable = true;
        }else{
            isPreCreditCheckBoxAvailable = false;
        }
        return isPreCreditCheckBoxAvailable;
        
    }
    
    public boolean isDisplayFctNonStdTermsConds(){
    	return ButtonDisplayRuleFactory.singleton().isDisplayFCTNonStdTermsConds(quote.getQuoteHeader());
    }
    
    public String getFctNonStdTermsCondsURL(){
        return fctNonStdTermsCondsURL;
    }
    
    public String getQtCntctEmail() {
        return StringUtils.isBlank(qtCntctEmail) ? "" :  StringUtils.trimToEmpty(qtCntctEmail) ;
    }
    
    public String getPrimCntctEmail() {
        return StringUtils.isBlank(primCntctEmail) ? "" : "(" + StringUtils.trimToEmpty(primCntctEmail) + ")";
    }
    

    public boolean isFctNonStdTermsCondsYesChecked(){
    	return QuoteOutputSubmissionUtil.isFctNonStdTermsCondsYesChecked(quote);
    }
    
    public boolean isFctNonStdTermsCondsNoChecked(){
    	return QuoteOutputSubmissionUtil.isFctNonStdTermsCondsNoChecked(quote);
    }

	public boolean isNoApprovalRequire() {
		return noApprovalRequire;
	}

	public void setNoApprovalRequire(boolean noApprovalRequire) {
		this.noApprovalRequire = noApprovalRequire;
	}
    
    public boolean isShowPaySchedFlag(){
        if (QuoteOutputSubmissionUtil.hasEventBasedBilling(quote)) {
            return false;
        }
    	return ((QuoteOutputSubmissionUtil.isPA(quote) || QuoteOutputSubmissionUtil.isPAE(quote) || QuoteOutputSubmissionUtil.isFCT(quote) || isFCTToPA()) && (QuoteOutputSubmissionUtil.hasSaaSLineItem(quote)||quote.getQuoteHeader().isHasMonthlySoftPart()));
    }
    
    public boolean isDisplayFOL(){
    	return QuoteOutputSubmissionUtil.isDisplayInclFirmOrdLtr(quote);
    }
    
    public boolean isDisplayQuoteSubmitQuoteOutputType(){
    	return QuoteOutputSubmissionUtil.isDisplayQuoteSubmitQuoteOutputType(quote);
    }
    
	public String getImpliedGrowth() {
		return DecimalUtil.formatTo5Number((quote.getQuoteHeader() != null && quote.getQuoteHeader().getImpldGrwthPct() != null) ? quote.getQuoteHeader().getImpldGrwthPct() : 0);
	}
	
	public boolean isBudgetaryQuoteOutput(){
		return ((QuoteOutputSubmissionUtil.isPA(quote)||QuoteOutputSubmissionUtil.isPAE(quote) || QuoteOutputSubmissionUtil.isFCT(quote))
				&& (QuoteOutputSubmissionUtil.hasSaaSLineItem(quote)||quote.getQuoteHeader().isHasMonthlySoftPart())
				&& quote.getQuoteHeader().getFulfillmentSrc().equalsIgnoreCase(QuoteConstants.FULFILLMENT_DIRECT));
					
	}
	
	public String getDefCoverText(){
		String defaultMsg = "";
		if(this.getQuote().getQuoteHeader().isSSPQuote()){
			ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
			defaultMsg = context.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES, locale, DraftQuoteViewKeys.MSG_SUBMIT_SSP_MSG_DEF);
		}
        return defaultMsg;
	}

	public String getQtCntctFirstName() {
		return StringUtils.isBlank(qtCntctFirstName) ? "" :  StringUtils.trimToEmpty(qtCntctFirstName);
	}

	public void setQtCntctFirstName(String qtCntctFirstName) {
		this.qtCntctFirstName = qtCntctFirstName;
	}

	public String getQtCntctLastName() {
		return StringUtils.isBlank(qtCntctLastName) ? "" :  StringUtils.trimToEmpty(qtCntctLastName);
	}

	public void setQtCntctLastName(String qtCntctLastName) {
		this.qtCntctLastName = qtCntctLastName;
	}

	public String getQtCntctPhone() {
		return StringUtils.isBlank(qtCntctPhone) ? "" :  StringUtils.trimToEmpty(qtCntctPhone);
	}

	public void setQtCntctPhone(String qtCntctPhone) {
		this.qtCntctPhone = qtCntctPhone;
	}

	public String getQtCntctFax() {
		return StringUtils.isBlank(qtCntctFax) ? "" :  StringUtils.trimToEmpty(qtCntctFax);
	}

	public void setQtCntctFax(String qtCntctFax) {
		this.qtCntctFax = qtCntctFax;
	}

	public void setQtCntctEmail(String qtCntctEmail) {
		this.qtCntctEmail = qtCntctEmail;
	}
	
	 public String getQtsapCntPrtnrFuncCode() {
			return qtsapCntPrtnrFuncCode;
	}

	public void setQtsapCntPrtnrFuncCode(String qtsapCntPrtnrFuncCode) {
			this.qtsapCntPrtnrFuncCode = qtsapCntPrtnrFuncCode;
	}
	
}
