package com.ibm.dsw.quote.submittedquote.viewbean;

import is.domainx.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.SalesOdds;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.domain.TacticCode;
import com.ibm.dsw.quote.dlgtn.config.DlgtnActionKeys;
import com.ibm.dsw.quote.dlgtn.config.DlgtnParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.promotion.config.PromotionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>SubmittedQuoteSalesInfoTabViewBean<code> class.
 *
 * @author: zhaoxw@cn.ibm.com
 *
 * Creation date: Apr 28, 2007
 */

public class SubmittedQuoteSalesInfoTabViewBean extends SubmittedQuoteBaseViewBean{
    public final static String YES = "Yes";
    public final static String NO = "No";
    transient List quoteDelegates;
    String businessOrgDesc;
    String boDefaultOption;
    String qcFullName = "";
    String qcPhone = "";
    String qcEmail = "";
    String qcCountry = "";
    String ooFullName = "";
    String ooPhone = "";
    String ooEmail = "";
    String ooCountry = "";
    String busOrgCode = "";
    String changeOppOwnerURL;
    String removeQuoteEditorURL;
    String addQuoteEditorURL;

    // Get default values from quote header
    String oppNumber = "";
    String quoteTitle = "";
    String quoteDesc = "";
    String oppOwnerEmail = "";
    String selectedExemptnCode = "";
    String qcCompany = "";

    boolean isDisplayQuoteCreator = false;
    boolean isDisplayOppOwner = false;
    boolean isDisplayQuoteEditors = false;

    // default exemption code,not user select
    String defaultExemptionCode;
    // user select,if the exemption code is not 80, the code is the same with default exemption code
    String exemptionCode;
    // default exemption code i18n key
    String exemptionCodeTextKey;

    boolean isEditable = false;

//  Current tracking information
    String currentTIUpsideTransaction = "";

    transient List currentTITacticCodes;

    String currentTISalesOdds = "";

    String currentTISalesComments = "";

    transient QuoteTxt detailComments = null;

    transient List salesCmmntAttchmnts = null;

    String attchmntDwnldURL = null;

    boolean isDisplayOppNumInput = false;

    private int currBlockReminder;

    transient List quotePromotions;
    boolean isDisplayQuotePromotions = false;
    String updatePrmtnURL ="";

    public void collectResults(Parameters params) throws ViewBeanException {

        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(params);

        detailComments = (QuoteTxt) params.getParameter(SubmittedQuoteParamKeys.PARAM_RQ_DETAIL_SALES_COMMENTS);
        salesCmmntAttchmnts = (List) params.getParameter(SubmittedQuoteParamKeys.PARAM_ATTACHMENT_LIST);
        attchmntDwnldURL = HtmlUtil.getURLForAction(ActionKeys.DOWNLOAD_ATTACHMENT);

        if (quote != null) {
            QuoteHeader qh = quote.getQuoteHeader();
            try {
                //Set isEditable
                this.setIsEditable(quote, user);

                //Opportunity information section
                //Quote editor and Opportunity owner
                CacheProcess process = CacheProcessFactory.singleton().create();

	            // Set quote creator attributes
	            if (quote.getCreator() != null) {
	                setQCFullName(quote.getCreator().getFullName());
	                setQCPhone(quote.getCreator().getPhoneNumber());
	                setQCEmail(quote.getCreator().getEmailAddress());

	                Country cntry = process.getCountryByCode3(quote.getCreator().getCountryCode());
	                if (cntry != null)
	                    setQCCountry(cntry.getDesc());
	            }

	            // Set opportunity owner attributes
	            if (quote.getOppOwner() != null) {
	                setOOFullName(quote.getOppOwner().getFullName());
	                setOOPhone(quote.getOppOwner().getPhoneNumber());
	                setOOEmail(quote.getOppOwner().getEmailAddress());
	                Country cntry = process.getCountryByCode3(quote.getOppOwner().getCountryCode());
	                if (cntry != null)
	                    setOOCountry(cntry.getDesc());
	            } else if (quote.getCreator() != null) {
	                setOOFullName(quote.getCreator().getFullName());
	                setOOPhone(quote.getCreator().getPhoneNumber());
	                setOOEmail(quote.getCreator().getEmailAddress());
	                Country cntry = process.getCountryByCode3(quote.getCreator().getCountryCode());
	                if (cntry != null)
	                    setOOCountry(cntry.getDesc());
	            }

	            // Set delegates list
	            setQuoteDelegates(quote.getDelegatesList());

	            setQuotePromotions(quote.getPromotionsList());

	            // Set quote header attributes
	            if (qh != null) {
	                setBusOrgCode(qh.getBusOrgCode());
	                oppNumber = qh.getOpprtntyNum();
	                quoteTitle = qh.getQuoteTitle();
	                quoteDesc = qh.getQuoteDscr();
	                oppOwnerEmail = qh.getOpprtntyOwnrEmailAdr();
	                selectedExemptnCode = qh.getExemptnCode();

	                CodeDescObj businessOrg = process.getBusinessOrgByCode(qh.getBusOrgCode());
	                if (businessOrg != null)
	                    businessOrgDesc = businessOrg.getCodeDesc();
	            }
                if (this.isRenewalQuote()) {
                    //Tracking information
                    String currentTIUpsideTransactionInt = qh.getUpsideTrendTowardsPurch();
                    if (currentTIUpsideTransactionInt != null){
                        if (currentTIUpsideTransactionInt.equals(DraftQuoteConstants.UPSIDE_TRANSACTION_YES)){
                            currentTIUpsideTransaction = YES;
                        } else if (currentTIUpsideTransactionInt.equals(DraftQuoteConstants.UPSIDE_TRANSACTION_NO)){
                            currentTIUpsideTransaction = NO;
                        }
                    }

                    currentTITacticCodes = new ArrayList();
                    List currentTITacticCodeList = qh.getTacticCodes();
                    if (currentTITacticCodeList != null) {
                        Iterator iter = currentTITacticCodeList.iterator();
                        while (iter.hasNext()) {
                            TacticCode ctc = (TacticCode) iter.next();
                            CodeDescObj tactic = process.getTacticByCode(StringUtils.trimToEmpty(ctc.getTacticCode()));
                            if (tactic != null) {
                                currentTITacticCodes.add(tactic);
                            }
                        }
                    }

                    SalesOdds salesOdds = process.getSalesOddsByRenwlQuoteSalesOddsCode(qh.getRenwlQuoteSalesOddsOode());
                    if (salesOdds != null) {
                        currentTISalesOdds = salesOdds.getRenwlQuoteSalesOddsCodeDesc();
                    }
                    currentTISalesComments = qh.getSalesComments();

                    this.currBlockReminder = qh.getBlockRnwlReminder();
                }



            	if(null != quote.getQuoteHeader() &&
            			QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(quote.getQuoteHeader().getAudCode())){
            		// PGS quote, get site number from web_quote_user_details table by creator-id of quote header
            		setQcCompany(quote.getQuoteHeader().getQcCompany());
            		setQCCountry(quote.getQuoteHeader().getQcCountry());
            		setQCPhone(null);
            	}

                if (this.isSalesQuote()) {
                    this.setExemptionInfo(quote);
                    //if opp num input appear
                    if (quote.getCustomer() != null) {
                        isDisplayOppNumInput = true;
                    }
                }

	        } catch (QuoteException qe) {
	            logContext.error(this, qe.getMessage());
	            throw new ViewBeanException("error getting data from cache");
	        }

        } // End if (quote != null)
    }

    /**
     * @param businessOrg
     *            The businessOrganization to set.
     */
    public void setQuoteDelegates(List delegates) {
        this.quoteDelegates = delegates;
    }

    /**
     * @return Returns the businessOrganization.
     */
    public List getQuoteDelegates() {

        return quoteDelegates;
    }

    /**
     * @param lob
     *            The busOrgCode to set.
     */
    public void setBusOrgCode(String busOrgCode) {
        this.busOrgCode = busOrgCode;
    }

    /**
     * @return Returns the busOrgCode.
     */
    public String getBusOrgCode() {
        return busOrgCode;
    }

    /**
     * @param fullName
     *            The quote creator full name to set.
     */
    public void setQCFullName(String fullName) {
        this.qcFullName = fullName;
    }

    /**
     * @return Returns the quote creator full name.
     */
    public String getQCFullName() {
        return qcFullName;
    }

    /**
     * @param fullName
     *            The quote creator phone number to set.
     */
    public void setQCPhone(String phoneNum) {
        this.qcPhone = phoneNum;
    }

    /**
     * @return Returns the quote creator phone number.
     */
    public String getQCPhone() {
        return qcPhone;
    }

    /**
     * @param fullName
     *            The quote creator emaile to set.
     */
    public void setQCEmail(String email) {
        this.qcEmail = email;
    }

    /**
     * @return Returns the quote creator email.
     */
    public String getQCEmail() {
        return qcEmail;
    }

    /**
     * @param fullName
     *            The quote creator country to set.
     */
    public void setQCCountry(String country) {
        this.qcCountry = country;
    }

    /**
     * @return Returns the quote creator country.
     */
    public String getQCCountry() {
        return qcCountry;
    }

    /**
     * @param fullName
     *            The opportunity owner full name to set.
     */
    public void setOOFullName(String fullName) {
        this.ooFullName = fullName;
    }

    /**
     * @return Returns the opportunity owner full name.
     */
    public String getOOFullName() {
        return ooFullName;
    }

    /**
     * @param fullName
     *            The opportunity owner phone number to set.
     */
    public void setOOPhone(String phoneNum) {
        this.ooPhone = phoneNum;
    }

    /**
     * @return Returns the opportunity owner phone number.
     */
    public String getOOPhone() {
        return ooPhone;
    }

    /**
     * @param fullName
     *            The opportunity owner emaile to set.
     */
    public void setOOEmail(String email) {
        this.ooEmail = email;
    }

    /**
     * @return Returns the opportunity owner email.
     */
    public String getOOEmail() {
        return ooEmail;
    }

    /**
     * @param fullName
     *            The opportunity owner country to set.
     */
    public void setOOCountry(String country) {
        this.ooCountry = country;
    }

    /**
     * @return Returns the opportunity owner country.
     */
    public String getOOCountry() {
        return ooCountry;
    }

    /**
     * @return Returns the opportunity number.
     */
    public String getOppNumber() {
        return oppNumber == null ? "" : oppNumber;
    }

    /**
     * @return Returns the opportunity owner email.
     */
    public String getOppOwnerEmail() {
        return oppOwnerEmail == null ? "" : oppOwnerEmail;
    }

    /**
     * @return Returns the quote title.
     */
    public String getQuoteTitle() {
        return quoteTitle == null ? "" : quoteTitle.trim();
    }

    /**
     * @return Returns the quote desc.
     */
    public String getQuoteDesc() {
        return quoteDesc == null ? "" : quoteDesc.trim();
    }

    /**
     * @return true .
     */
    public boolean isSiebelOppNumChecked() {
        if( StringUtils.isBlank(selectedExemptnCode) && StringUtils.isNotBlank(oppNumber) ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return true .
     */
    public boolean isCustInitChecked() {
    	if( StringUtils.isBlank(oppNumber) && StringUtils.isNotBlank(selectedExemptnCode) 
        		&& (selectedExemptnCode.equalsIgnoreCase(DraftQuoteConstants.EXEMPTNCODE_80)
        				|| (!selectedExemptnCode.equalsIgnoreCase(DraftQuoteConstants.EXEMPTNCODE_80)
        					&&	selectedExemptnCode.equalsIgnoreCase(defaultExemptionCode))
        					)
        					) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return true if any of the quote creator attributes is not blank
     */
    public boolean isDisplayQuoteCreator() {

        if (StringUtils.isNotBlank(qcFullName) || StringUtils.isNotBlank(qcPhone) || StringUtils.isNotBlank(qcEmail)
                || StringUtils.isNotBlank(qcCountry)) {

            isDisplayQuoteCreator = true;
        }

        return isDisplayQuoteCreator;
    }

    /**
     * @return true if any of the opp. owner attributes is not blank
     */
    public boolean isDisplayOppOwner() {

        if (StringUtils.isNotBlank(ooFullName) || StringUtils.isNotBlank(ooPhone) || StringUtils.isNotBlank(ooEmail)
                || StringUtils.isNotBlank(ooCountry)) {

            isDisplayOppOwner = true;
        }

        return isDisplayOppOwner;
    }

    /**
     * @return true if isDisplayQuoteCreator or isDisplayOppOwner is true
     */
    public boolean isDisplayQuoteCreatorOppOwner() {

        if (isDisplayQuoteCreator() || isDisplayOppOwner()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return true if quoteDelegates' size is greater than 0
     */
    public boolean isDisplayQuoteEditors() {

        if (quoteDelegates != null) {
            if (quoteDelegates.size() > 0) {
                isDisplayQuoteEditors = true;
            }
        }

        return isDisplayQuoteEditors;
    }

    /**
     * @return Returns the addQuoteEditorURL.
     */
    public String getAddQuoteEditorURL() {
        StringBuffer url = new StringBuffer();
        getActionURL(url, DlgtnActionKeys.ADD_QUOTE_DELEGATE, getDisplayTabAction());
        HtmlUtil.addURLParam(url, SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM, getWebQuoteNum());
        HtmlUtil.addURLParam(url, SubmittedQuoteParamKeys.PARAM_QUOTE_NUM, getWebQuoteNum());
        return url.toString();
    }

    /**
     * @return Returns the changeOppOwnerURL.
     */
    public String getChangeOppInfoURL() {
        StringBuffer url = new StringBuffer();
        getActionURL(url, SubmittedQuoteActionKeys.UPDATE_OPPR_INFO, getDisplayTabAction());
        HtmlUtil.addURLParam(url, SubmittedQuoteParamKeys.PARAM_QUOTE_NUM, getWebQuoteNum());
        return url.toString();
    }

    /**
     * @return Returns the removeQuoteEditorURL.
     */
    public String getRemoveQuoteEditorURL(String delegateId) {
        StringBuffer url = new StringBuffer();
        getActionURL(url, DlgtnActionKeys.REMOVE_QUOTE_DELEGATE, getDisplayTabAction());
        HtmlUtil.addURLParam(url, DlgtnParamKeys.PARAM_DELEGATEID, delegateId);
        HtmlUtil.addURLParam(url, SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM, getWebQuoteNum());
        HtmlUtil.addURLParam(url, SubmittedQuoteParamKeys.PARAM_QUOTE_NUM, getWebQuoteNum());
        return url.toString();
    }

    private void getActionURL(StringBuffer url, String action1, String action2){
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String secondActionKey = appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);
        url.append(HtmlUtil.getURLForAction(action1));
        HtmlUtil.addURLParam(url, secondActionKey, action2);
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftSQBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        return SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SALES_INFO_TAB;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public String getBusinessOrgDesc() {
        return notNullString(businessOrgDesc);
    }
    private void setExemptionInfo(Quote quote) throws QuoteException{
    	String[] exemptionInfoArray = initExemptionInfo(quote);
    	this.defaultExemptionCode = exemptionInfoArray[0];
        this.exemptionCodeTextKey = exemptionInfoArray[1];
        this.exemptionCode = exemptionInfoArray[2];
    }

    public String getDefaultExemptionCode(){
    	return defaultExemptionCode;
    }
    public String getExemptionCode() {
        return exemptionCode;
    }
    public String getExemptionCodeTextKey() {
        return exemptionCodeTextKey;
    }
    /**
     * @return Returns the currentTISalesComments.
     */
    public String getCurrentTISalesComments() {
        return notNullString(currentTISalesComments);
    }

    /**
     * @return Returns the currentTISalesOdds.
     */
    public String getCurrentTISalesOdds() {
        return notNullString(currentTISalesOdds);
    }

    /**
     * @return Returns the currentTITacticCodes.
     */
    public List getCurrentTITacticCodes() {
        return currentTITacticCodes;
    }

    /**
     * @return Returns the currentTIUpsideTransaction.
     */
    public String getCurrentTIUpsideTransaction() {
        return notNullString(currentTIUpsideTransaction);
    }
    public String notNullString(String string){
        return StringUtils.trimToEmpty(string);
    }

    private void setIsEditable(Quote quote, User user){
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        QuoteUserAccess qtUserAccess = quote.getQuoteUserAccess();
        boolean isSubmitter = user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_SUBMITTER;
        boolean isEditor = qtUserAccess == null ? false : qtUserAccess.isEditor();
        boolean overStatus = (!quoteHeader
                .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG)
                && !quoteHeader.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_REJECTED)
                && !quoteHeader.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)
                && !quoteHeader.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED)
                && !quoteHeader.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.BILLED_ORDER)
                && !quoteHeader.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED)
                && !quoteHeader.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS)
                && !quoteHeader.isCopied4PrcIncrQuoteFlag()
                && !quoteHeader.isBidIteratnQt()
                && !quoteHeader.isDeleteByEditor()
                && !quoteHeader.isExpDateExtendedFlag()
//                && !quoteHeader.isSubmittedForEval()
//                && !quoteHeader.isReturnForChgByEval()
//                && !quoteHeader.isAcceptedByEval()
//                && !quoteHeader.isEditForRetChgByEval()
                );
        if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode())){
        	isEditable = overStatus;
        }
        else {
        	isEditable = isSubmitter && isEditor && overStatus;
        }
    }

    public boolean isDisplayOppNumInput() {
        return isDisplayOppNumInput;
    }

    public String getOppNumInfo() {
        String oppNumInfo = "";
        if(isSiebelOppNumChecked()){
            oppNumInfo = oppNumber == null ? "" : oppNumber;
        }
        if(isCustInitChecked()){
            if(this.isPGSFlag()){
            	oppNumInfo = "";
            }else{
            	ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
             	oppNumInfo = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, "exemption_code") + " " + exemptionCode;
            }
        }
        return oppNumInfo;
    }

    public String getDetailComments() {
        String text = detailComments == null ? "" : detailComments.getQuoteText();
        return text == null ? "" : text;
    }

    public List getSalesCmmntAttchmnts() {
        return salesCmmntAttchmnts;
    }

    public String getAttchmntDwnldURL() {
        return attchmntDwnldURL;
    }

    public String getRQSalesTrackingURL(){
        StringBuffer goBackURL = new StringBuffer();
        goBackURL.append(
                ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                        ActionHandlerKeys.JADE_ACTION_KEY)).append("=").append(getDisplayTabAction());
        HtmlUtil.addURLParam(goBackURL, SubmittedQuoteParamKeys.PARAM_QUOTE_NUM, getWebQuoteNum());

        StringBuffer rqSalesTrackURL = new StringBuffer(ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.RENEWAL_QUOTE_SALES_TRACKING_URL));
        HtmlUtil.addURLParam(rqSalesTrackURL, DraftRQParamKeys.PARAM_RPT_QUOTE_NUM, renewalQuoteNum);
        HtmlUtil.addURLParam(rqSalesTrackURL, DraftRQParamKeys.PARAM_RPT_P1, HtmlUtil.urlEncode(goBackURL.toString()));

        return rqSalesTrackURL.toString();
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("isEditable = ").append(isEditable).append("\n");
        buffer.append("isSalesQuote = ").append(isSalesQuote()).append("\n");
        buffer.append("isRenewalQuote = ").append(isRenewalQuote()).append("\n");
        buffer.append("isDisplayOppNumInput = ").append(isDisplayOppNumInput).append("\n");
        buffer.append("exemptionCode = ").append(exemptionCode).append("\n");
        buffer.append("exemptionCodeTextKey = ").append(exemptionCodeTextKey).append("\n");
        buffer.append("defaultExemptionCode = ").append(defaultExemptionCode).append("\n");
        return buffer.toString();
    }

    public boolean isDispBlockReminder(){
        return getQuote().getQuoteHeader().isRenewalQuote()
                  &&!getQuote().getQuoteHeader().isPPSSQuote();
    }

    /**
     * @return Returns the currBlockReminder.
     */
    public int getCurrBlockReminder() {
        return currBlockReminder;
    }

    public String getSubmitAsFinalBtnParams() {
        if (!this.isBidIteratnQt()) {
            return super.getSubmitAsFinalBtnParams();
        }
        submitAsFinalBtnParams = getActionURL(SubmittedQuoteActionKeys.POST_SUBMITTED_SALES_INFO_TAB,
                DraftQuoteActionKeys.SUBMIT_DRAFT_SQ_AS_FINAL, null, null);

        return submitAsFinalBtnParams;
    }
    public String getConv2StdCopyBtnURL(){
        if (!this.isBidIteratnQt()) {
            return super.getConv2StdCopyBtnURL();
        }
        String[] keys = {ParamKeys.PARAM_FORWARD_FLAG, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = {"false", this.getWebQuoteNum() };

        return getActionURL(SubmittedQuoteActionKeys.POST_SUBMITTED_SALES_INFO_TAB, SubmittedQuoteActionKeys.CONVERT_TO_STD_COPY, keys, values);
    }

    public String getSaveQuoteAsDraftBtnParams() {
        if (!this.isBidIteratnQt()) {
            return super.getSaveQuoteAsDraftBtnParams();
        }

        String[] keys = {ParamKeys.PARAM_REDIRECT_URL, ParamKeys.PARAM_QUOTE_NUM};
        String[] values = { HtmlUtil.getURLForAction(getDisplayTabAction()), this.getWebQuoteNum() };

        saveQuoteAsDraftBtnParams = getActionURL(SubmittedQuoteActionKeys.POST_SUBMITTED_SALES_INFO_TAB, SubmittedQuoteActionKeys.SAVE_BID_ITERATION, keys, values);

        return saveQuoteAsDraftBtnParams;
    }

    public String getPostTabAction(){
        return SubmittedQuoteActionKeys.POST_SUBMITTED_SALES_INFO_TAB;
    }

    public List getQuotePromotions() {
		return quotePromotions;
	}

	public void setQuotePromotions(List quotePromotions) {
		this.quotePromotions = quotePromotions;
	}

    public boolean isDisplayQuotePromotions() {
        if(quotePromotions == null || quotePromotions.size() == 0){
        	isDisplayQuotePromotions = false;
        }else{
        	isDisplayQuotePromotions = ButtonDisplayRuleFactory.singleton().isDisplayPromotions(header);
        }
        return isDisplayQuotePromotions;
    }

    public boolean canUpdatePromotion(){
    	return this.quotePromotions != null && this.quotePromotions.size() > 0 && isDisplayApprAction();
    }
    public String getUpdatePrmtnURL(){

    	String[] keys = { ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };

        return getActionURL(PromotionKeys.ADD_UPDATE_QUOTE_PROMOTION, this.getDisplayTabAction(), keys, values);
    }

    /**
     * @param fullName
     *            The quote creator qcCompany to set.
     */
    public String getQcCompany() {
		return qcCompany;
	}
    /**
     * @return Returns the quote creator qcCompany.
     */
	public void setQcCompany(String qcCompany) {
		this.qcCompany = qcCompany;
	}

	public boolean isDoSpecialBidCommonInit(){
        return true;
    }
}
