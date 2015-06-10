package com.ibm.dsw.quote.submittedquote.viewbean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.QuoteClassificationCodeFactory;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidViewKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SpecialBidCommon;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SubmittedPartTable;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>SubmittedQuoteBaseViewBean<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-4-28
 */

public abstract class SubmittedQuoteBaseViewBean extends DisplayQuoteBaseViewBean {
    public static final String SUBMITTED_QUOTE_EXEC_SUMMARY_TAB_ID = "5";

    public SpecialBidCommon common = null;

    protected Date quoteSubmittedDate = null;
    protected Date priorQtSbmtdDate = null;
    protected String overallStatus = null;

    protected String sapQuoteNum = null;
    public String sqoReference = null;


    protected boolean isDisplayEnterAddiSBJust = false;
    protected boolean isDisplaySubmitRequest = false;
    protected boolean isDisplayReqICN = false;
    protected boolean isDisplayReqPreCreditCheck = false;
    protected boolean isDisplayUpdateQuote = false;
    protected boolean isDisplayCreateCopy = false;
    protected boolean isDisplayCreateCopyCheckbox = true;
    protected boolean isDisplayOrder = false;
    protected boolean isDisplayCancelQuote = false;
    protected boolean isDisplayManualIntrvntnNeededMsg = false;
    protected boolean isDisplayQtProcessInSapMsg = false;
    protected boolean isDisplayNoCustEnrollMsg = false;
    protected boolean isDisplaySVPLevelNotMatchMsg = false;
    protected boolean isDisplayCtrctNumNotMatchMsg = false;
    protected boolean isDisplayHasOrderedMsg = false;
    protected boolean isDisplayCancelledQuoteMsg = false;
    protected boolean isDisplayChannelSQOrderMsg = false;
    protected boolean isDisplaySpecBidQuoteMsg = false;
    protected boolean isDisplaySubmitterAccessRequiredMsg = false;
    protected boolean isDisplayDldRichTextBtn = false;
    protected boolean isDisplayExprtSprdshtBtn = false;
    protected boolean isDisplayExportQuoteAsSpreadsheet = false;
    protected boolean isDisplayElaSQOrderMsg = false;
    protected boolean isDisplayCntryNotAllowOrderMsg = false;
    protected boolean isDisplayOEMNotAllowOrderMsg = false;
    protected boolean isDisplayEffDateInFtrMsg = false;

    protected boolean isDisplayApprAction = false;
    protected boolean isDisplayAddiComments = false;
    protected boolean isDisplaySaveComments = false;

    protected boolean isDisplayExpSBCannotOrderMsg = false;
    protected boolean isDisplaySpecBidNotActiveMsg = false;
    protected boolean isDisplaySBExpDatePassedMsg = false;
    protected boolean isDisplayExecSmmryMsg = false;
    protected boolean isDisplayObsltPartMsg = false;
    protected boolean isDisplayCancelApprovedBid = false;
    protected boolean isDisplayNotAllowOrderForCustBlockMsg = false; //not allow order as a blocked customer - sods2.customer.sales_ord_block = '01'
    protected boolean isCustomerMatchDistributor = true;
    protected boolean isDisplayFct2PaQuoteTerminationMsg = false;
    

	protected String saveAddiSBJustBtnParams = null;
    protected String submitAddiProcBtnParams = null;
    protected String updateQuoteBtnParams = null;
    protected String createCopyBtnParams = null;
    protected String orderBtnParams = null;
    protected String cancelQuoteBtnParams = null;
    protected String saveReviewCommnetsBtnParams = null;
    protected String submitApprovalCommentsBtnParams = null;
    protected String submitOtherCommentsBtnParams = null;

    protected String renewalQuoteNum = "";
    protected String renewalStatus = "";
    protected String secondaryStatus = "";
    protected Date renewalDueDate = null;
    protected Date specialBidExpDate = null;
    protected boolean isSpecialBid = false;

    protected boolean isDisplayCancelSpecialBid = false;
    protected boolean isDisplayUpdateSpecialBid = false;

    protected String hasActiveDraftQuote = "";

    protected boolean isDisplayOverrideExpDate = false;

    protected boolean isSPBidTab = false;

    protected String reviewerComments = "";

    protected String approverComments = "";

    protected String approverCommentsAdd = "";

    protected String submitterComments = "";

    protected String approverAddReviewerComments = "";

    protected String justComments = "";

    protected String tncComments = "";

    protected String approveLevel = "-2";

    protected boolean isTncAndSummaryEditable = false;

    protected boolean showExecSummaryTab = false;

    private String downloadFileURL = null;

    protected boolean isCpFromApprvdBid = false;

    //display create copy of approved sb for price increase button flag
    private boolean isDispCp4PrcIncrButton = false;

    protected boolean isApprover = false;

    private boolean isCopied4PrcIncrQuoteFlag = false;
    
    private boolean isExpDateExtendedFlag = false;
    
    private boolean isDispCp4ExpiryDateButton = false;

    private boolean isDispSubmitButton = false;

    private Date quoteAddDate = null;

    private boolean isDispBidIterButton = false;

    protected boolean isDisplayPymntTerm = false;

    protected int pymntTermsDays = 0;

    private boolean isBidIteratnQtFlag = false;

    private boolean isDisplaySaveQuoteAsDraft = false;

    private boolean isDisplayConv2StdCopyBtn = false;

    protected String submitAsFinalBtnParams = "";

    protected String saveQuoteAsDraftBtnParams = "";

    private String updateApproverURL = null;
    private String oemBidTypeDesc = "";

    private boolean isDispChgOutputOptionFlag = false;
    private boolean isDispChgOutputOptionWithDivestedPartMsg = false;

    private boolean isCopiedForOutputChangeFlag = false;


    private String popupAuditHistURL = null;

    private boolean hasSaaSLineItem = false;
    
    protected boolean isPGSSubmitter = false;

	protected transient Map actionText = new HashMap();

    private boolean isDisplayOrdWithoutOpptNumMsg = false;
    
    private boolean isModifyCRADWhenOrder = false;
    
    private Date custReqstdArrivlDate = null;
    
    protected List returnReasons = new ArrayList();
    
    protected boolean isDisplaySSPWithOutContract = false;
    
    protected boolean isDisplayOrderForDivestedPartMsg = false;
    protected boolean isDispCp4PrcIncr4DivestdPartMsg = false;
    protected boolean isDispBidIterDivestdPartMsg = false;
    public SubmittedPartTable partTable;
    private boolean showApproverDetails = false;
    
    private String expDateExtensionJustification;//add for 15.2 ,justification for expiration date extension 
   
	public boolean isDisplayOrdWithoutOpptNumMsg() {
		return isDisplayOrdWithoutOpptNumMsg;
	}

	public boolean isPGSFlag() {
		return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode());
	}

    /* (non-Javadoc)
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        downloadFileURL = HtmlUtil.getURLForAction(ActionKeys.DOWNLOAD_ATTACHMENT);
        this.popupAuditHistURL = HtmlUtil.getURLForAction(ActionKeys.DISPLAY_QT_AUDIT_HIST);
        hasActiveDraftQuote = (String) params.getParameter(SubmittedQuoteParamKeys.HAS_ACTIVE_DRAFT_QUOTE);
        if(quote.getQuoteUserAccess() != null){
	        approveLevel = Integer.toString(quote.getQuoteUserAccess().getPendingAppLevel());
	        if (quote.getQuoteUserAccess().isCanViewExecSummry(user.getAccessLevel(QuoteConstants.APP_CODE_SQO))) {
	            showExecSummaryTab = true;
	        }
        }
        isApprover = user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER;
        
	    if (header.isRenewalQuote()) {
            // Get renewal quote header information
            renewalDueDate = header.getRenwlEndDate();

            // Get quote primary status
            List pStatusList = quote.getAllWebPrimaryStatuses();
            for (Iterator iter = pStatusList.iterator(); iter.hasNext();) {
                QuoteStatus pStatus = (QuoteStatus) iter.next();
                if (renewalStatus.length() > 0) {
                    renewalStatus += "<br />";
                }
                renewalStatus += pStatus.getStatusCodeDesc();
            }

            // Get quote secondary status
            List sStatusList = quote.getAllWebSecondayStatuses();
            for (Iterator iter = sStatusList.iterator(); iter.hasNext();) {
                QuoteStatus sStatus = (QuoteStatus) iter.next();
                if (secondaryStatus.length() > 0) {
                    secondaryStatus += "<br />";
                }
                secondaryStatus += sStatus.getStatusCodeDesc();
            }
        }

        sqoReference = header.getWebQuoteNum();
        quoteAddDate = header.getQuoteAddDate();
        quoteSubmittedDate = header.getSubmittedDate();
        renewalQuoteNum = header.getRenwlQuoteNum();
        priorQtSbmtdDate = header.getPriorQuoteSbmtDate();
        isCpFromApprvdBid = header.isCopied4ReslChangeFlag();
        pymntTermsDays = header.getPymTermsDays();
        oemBidTypeDesc = this.getOemBidTypeDescByCode(String.valueOf(header.getOemBidType()));
        hasSaaSLineItem = header.hasSaaSLineItem();
        expDateExtensionJustification = header.getExpDateExtensionJustification();
        if(quote != null && quote.getPayer() != null && quoteUserSession != null && quoteUserSession.getSiteNumber() != null){
	        if(isPGSFlag() && !quoteUserSession.getSiteNumber().equals(quote.getPayer().getCustNum())){
	        	isCustomerMatchDistributor = false;
	        }
        }
        	
        if (header.isSalesQuote()) {
            this.quoteExpDate = header.getQuoteExpDate();
            this.estmtdOrdDate = header.getEstmtdOrdDate();
            this.sapQuoteNum = header.getSapQuoteNum();
            this.custReqstdArrivlDate = header.getCustReqstArrivlDate();

            if (this.quoteExpDate != null) {
                Calendar expireDate = Calendar.getInstance();
                expireDate.setTime(this.quoteExpDate);
                quoteExpYear = expireDate.get(Calendar.YEAR);
                quoteExpMonth = expireDate.get(Calendar.MONTH) + 1;
                quoteExpDay = expireDate.get(Calendar.DAY_OF_MONTH);
            }

            this.getSalesQuoteDisplayRule();
        }
        else {
            this.specialBidExpDate = header.getQuoteExpDate();

            if (this.specialBidExpDate != null) {
                Calendar expireDate = Calendar.getInstance();
                expireDate.setTime(this.specialBidExpDate);
                quoteExpYear = expireDate.get(Calendar.YEAR);
                quoteExpMonth = expireDate.get(Calendar.MONTH) + 1;
                quoteExpDay = expireDate.get(Calendar.DAY_OF_MONTH);
            }

            this.getRenewalQuoteDisplayRule();
        }

        this.approverComments = params.getParameterAsString(SpecialBidParamKeys.APPROVER_COMMENT);
        approverCommentsAdd = params.getParameterAsString(SpecialBidParamKeys.APPROVER_COMMENT_ADD);
        this.submitterComments = params.getParameterAsString(SpecialBidParamKeys.SUBMITTER_COMMENT);
        reviewerComments = params.getParameterAsString(SpecialBidParamKeys.REVIEWER_COMMENT);
        approverAddReviewerComments = params.getParameterAsString(SpecialBidParamKeys.APVR_REVIEWER_COMMENT);
        justComments = params.getParameterAsString(SpecialBidParamKeys.JUST_COMMENTS);
        tncComments = params.getParameterAsString(SpecialBidParamKeys.TNC_COMMENTS);
        isTncAndSummaryEditable = (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO) && isDisplayEnterAddiSBJust)
        			|| isDisplayApprAction;
        returnReasons = (List) params.getParameter(ParamKeys.PARAM_RETURN_REASONS);

        //define update approver url
        StringBuffer approverUrl = new StringBuffer("");
        approverUrl.append(HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SUBMITTEDQT_UPDATE_APPROVER_SELECTION));
        updateApproverURL = approverUrl.toString();

        LogContext logContext = LogContextFactory.singleton().getLogContext();
        logContext.debug(this, "begin init special bid common");
        //here init special bid common
        // get common from current paream map, if null and special bid info not null, init common
        if ( this.isDoSpecialBidCommonInit() )
        {
	        common = (SpecialBidCommon) params.getParameter(SubmittedQuoteParamKeys.SPECIAL_BID_COMMON);
	        if ( common == null && quote.getSpecialBidInfo() != null )
	        {
	            String userId = getUser().getEmail();
	            try {
					common = new SpecialBidCommon(this.quote, userId);
				} catch (QuoteException e) {
					logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
				}
	        }
	        fillUserName(common);
        }
     
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
        
        partTable = new SubmittedPartTable(quote,getLocale());
        if( quote.getQuoteHeader().getSpeclBidFlag() == 1 && this.isApprover ){
            this.showApproverDetails = true;
        }else{
            return ;
        }
    
    }

    public boolean isDoSpecialBidCommonInit()
    {
        return false;
    }

    public boolean getDisplayUpdateApprFlag()
    {
        return false;
    }

    public String getUpdateApproverURL() {
        return updateApproverURL;
    }

    public SpecialBidCommon getCommon() {
        return common;
    }

    protected void fillUserName(SpecialBidCommon common) {
    	if ( common == null )
    	{
    	    return;
    	}
    	Map commentMap = common.getApproverCommentMap();
    	Map approversMap = common.getApproversMap();
    	Map chosenApproverMap = common.getChosenApproverMap();
    	Map map = new HashMap();
    	if (null != commentMap) {
            Iterator iterator = commentMap.values().iterator();
            while (iterator.hasNext()) {
        		List comments = (List)iterator.next();
        		if ( comments == null )
        		{
        		    continue;
        		}
        		Iterator itComments = comments.iterator();
        		while (itComments.hasNext())
        		{
	        			SpecialBidInfo.ApproverComment ac = (SpecialBidInfo.ApproverComment)itComments.next();
	        			if ( isNameEmpty(ac.approverName) )
	        			{
	        			    if ( map.get(ac.userEmail) == null )
	        			    {
	        			        ac.approverName = getBluePageFullName(ac.userEmail);
	        			        map.put(ac.userEmail, ac.approverName);
	        			    }
	        			    else
	        			    {
	        			        ac.approverName = (String)map.get(ac.userEmail);
	        			    }
	        			}
        		}
        	}
    	}

    	if (null != approversMap) {
            Iterator iterator = approversMap.values().iterator();
            while (iterator.hasNext()) {
        		List apprvrList = (List)iterator.next();
        		if ( apprvrList == null )
        		{
        		    continue;
        		}
        		Iterator it = apprvrList.iterator();
        		while (it.hasNext()){

	        			SpecialBidInfo.Approver appr = (SpecialBidInfo.Approver)it.next();
	        			if(isNameEmpty(appr.approverName))
	        			{
	        			    if ( map.get(appr.userEmail) == null )
	        			    {
	        			        appr.approverName = getBluePageFullName(appr.userEmail);
	        			        map.put(appr, appr.approverName);
	        			    }
	        			    else
	        			    {
	        			        appr.approverName = (String)map.get(appr.userEmail);
	        			    }
	        			}
        		    }
        	}
    	}

    	if (null != chosenApproverMap) {
            Iterator iterator = chosenApproverMap.values().iterator();
            while (iterator.hasNext()) {
        			SpecialBidInfo.ChosenApprover appr = (SpecialBidInfo.ChosenApprover)iterator.next();
        			if(isNameEmpty(appr.approverName))
        			{
        			    if ( map.get(appr.userEmail) == null )
        			    {
        			        appr.approverName = getBluePageFullName(appr.userEmail);
        			        map.put(appr, appr.approverName);
        			    }
        			    else
        			    {
        			        appr.approverName = (String)map.get(appr.userEmail);
        			    }
        			}
            }
    	}

    	List list = this.quote.getSpecialBidInfo().getReviewerComments();
    	if ( list != null )
    	{
    	    for ( int i = 0; i < list.size(); i++ )
    	    {
    	        SpecialBidInfo.ReviewerComment rc = (SpecialBidInfo.ReviewerComment)list.get(i);
    	        if(isNameEmpty(rc.getRequesterName()))
    			{
    			    if ( map.get(rc.getRequesterEmail()) == null )
    			    {
    			        rc.requesterName = getBluePageFullName(rc.getRequesterEmail());
    			        map.put(rc.requesterEmail, rc.requesterName);
    			    }
    			    else
    			    {
    			        rc.requesterName = (String)map.get(rc.requesterEmail);
    			    }
    			}

    	        if(isNameEmpty(rc.getReviewerName()))
    			{
    			    if ( map.get(rc.getReviewerEmail()) == null )
    			    {
    			        rc.reviewerName = getBluePageFullName(rc.getReviewerEmail());
    			        map.put(rc.reviewerEmail, rc.reviewerName);
    			    }
    			    else
    			    {
    			        rc.reviewerName = (String)map.get(rc.reviewerEmail);
    			    }
    			}
    	    }
    	}
    }

    public Map getActionText() {
		return actionText;
	}

	private boolean isNameEmpty(String name)
    {
        if ( StringUtils.isEmpty(name) )
        {
            return true;
        }
        if ( name.trim().length() == 1 )
        {
            return true;
        }
        return false;
    }

    protected String getBluePageFullName(String email) {
        if (StringUtils.isBlank(email))
            return "";

        BluePageUser bUser = BluePagesLookup.getBluePagesInfo(email);
        if (bUser != null) {
            	return StringUtils.trimToEmpty(bUser.getFullName());
            }
        else
        	return "";
    }

    protected void getRenewalQuoteDisplayRule() {
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        try {
            QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();
            Map rules = qcProcess.getSubmittedQuoteActionButtonsRule(quoteUserSession, quote);

            if (rules != null) {
                isDisplayOverrideExpDate = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_OVERRIDE_EXP_DATE))
                        .booleanValue();
                isDisplayEnterAddiSBJust = ((Boolean) rules
                        .get(QuoteCapabilityProcess.DISPLAY_SUBMITTER_ENTER_ADDI_SB_JUST)).booleanValue();
                isDisplayUpdateSpecialBid = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_UPDATE_SPEC_BID_BTN))
                        .booleanValue();
                isDisplayOrder = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_ORDER_BTN)).booleanValue();
                isDisplayCancelSpecialBid = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CANCEL_SPEC_BID_BTN))
                        .booleanValue();
                isDisplayApprAction = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ACTION))
                        .booleanValue();
                isDisplayAddiComments = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ADDI_COMMENTS))
                        .booleanValue();
                isDisplaySaveComments = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_REVIEWER_SAVE_COMMENTS))
                        .booleanValue();
                isDisplayManualIntrvntnNeededMsg = ((Boolean) rules
                        .get(QuoteCapabilityProcess.DISPLAY_MANUAL_INTRVNTN_NEEDED_MSG)).booleanValue();
                isDisplayQtProcessInSapMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_QT_PROCESS_IN_SAP_MSG))
                        .booleanValue();
                isDisplayHasOrderedMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_HAS_ORDERED_MSG))
                        .booleanValue();
                isDisplayCancelledQuoteMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CANCELLED_QT_MSG))
                        .booleanValue();
                isDisplaySpecBidQuoteMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SB_QT_MSG))
                        .booleanValue();
                isDisplaySubmitterAccessRequiredMsg = ((Boolean) rules
                        .get(QuoteCapabilityProcess.DISPLAY_SBMT_ACL_REQ_MSG)).booleanValue();
                isDisplayChannelSQOrderMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CHANNEL_QT_ORDER_MSG))
        				.booleanValue();
                isDisplayDldRichTextBtn = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN))
                        .booleanValue();
                isDisplayExpSBCannotOrderMsg = ((Boolean) rules
                        .get(QuoteCapabilityProcess.DISPLAY_EXPIRED_SB_QT_CANNOT_ORDERED_MSG)).booleanValue();
                isDisplaySpecBidNotActiveMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SB_NOT_ACTIVE_MSG))
                        .booleanValue();
                isDisplaySBExpDatePassedMsg = ((Boolean) rules
                        .get(QuoteCapabilityProcess.DISPLAY_EXPIRATION_DATE_PASSED_MSG)).booleanValue();
                isDisplayExecSmmryMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EXEC_SMMRY_MSG))
                        .booleanValue();
                isDisplayObsltPartMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG))
                        .booleanValue();
                isDisplayExprtSprdshtBtn = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EXPRT_SPRDSHT_BTN))
                        .booleanValue();
                isDisplayCancelApprovedBid = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CANCEL_APPROVED_BID)).booleanValue();
                isDisplayNotAllowOrderForCustBlockMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG)).booleanValue();
                isDisplayCntryNotAllowOrderMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG)).booleanValue();
                isDisplaySSPWithOutContract = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SSP_NO_CONTACT)).booleanValue();
            }

        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
        }

        // Set all sales quote action button conditions to false
        isDisplaySubmitRequest = false;
        isDisplayReqICN = false;
        isDisplayReqPreCreditCheck = false;
        isDisplayUpdateQuote = false;
        isDisplayCancelQuote = false;
        isDisplayCreateCopy = false;
        isDisplayNoCustEnrollMsg = false;
    }

    protected void getSalesQuoteDisplayRule() {
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        try {
            QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();
            Map rules = qcProcess.getSubmittedQuoteActionButtonsRule(quoteUserSession, quote);

            if (rules != null) {
                isDisplayOverrideExpDate = rules.get(QuoteCapabilityProcess.DISPLAY_OVERRIDE_EXP_DATE) != null?
                            ((Boolean)rules.get(QuoteCapabilityProcess.DISPLAY_OVERRIDE_EXP_DATE)).booleanValue():false;

                isDisplayEnterAddiSBJust = rules.get(QuoteCapabilityProcess.DISPLAY_SUBMITTER_ENTER_ADDI_SB_JUST) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SUBMITTER_ENTER_ADDI_SB_JUST)).booleanValue():false;

                isDisplaySubmitRequest = rules.get(QuoteCapabilityProcess.DISPLAY_SUBMITTER_SUBMIT_REQUEST) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SUBMITTER_SUBMIT_REQUEST)).booleanValue():false;

                isDisplayReqICN = rules.get(QuoteCapabilityProcess.DISPLAY_ICN_REQUIRED) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_ICN_REQUIRED)).booleanValue():false;

                isDisplayReqPreCreditCheck = rules.get(QuoteCapabilityProcess.DISPLAY_PRE_CREDIT_CHK_REQUIRED) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_PRE_CREDIT_CHK_REQUIRED)).booleanValue():false;

                isDisplayUpdateQuote = rules.get(QuoteCapabilityProcess.DISPLAY_UPDATE_QUOTE_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_UPDATE_QUOTE_BTN)).booleanValue():false;

                isDisplayCreateCopy = rules.get(QuoteCapabilityProcess.DISPLAY_CREATE_COPY_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CREATE_COPY_BTN)).booleanValue():false;

                isDisplayCreateCopyCheckbox = rules.get(QuoteCapabilityProcess.DISPLAY_CREATE_COPY_CHECKBOX_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CREATE_COPY_CHECKBOX_MSG)).booleanValue():false;

                isDisplayOrder = rules.get(QuoteCapabilityProcess.DISPLAY_ORDER_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_ORDER_BTN)).booleanValue():false;

                isDisplayCancelQuote = rules.get(QuoteCapabilityProcess.DISPLAY_CANCEL_QUOTE_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CANCEL_QUOTE_BTN)).booleanValue():false;

                isDisplayApprAction = rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ACTION) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ACTION)).booleanValue():false;

                isDisplayAddiComments = rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ADDI_COMMENTS) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_APPROVER_ADDI_COMMENTS)).booleanValue():false;

                isDisplaySaveComments = rules.get(QuoteCapabilityProcess.DISPLAY_REVIEWER_SAVE_COMMENTS) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_REVIEWER_SAVE_COMMENTS)).booleanValue():false;

                isDisplayManualIntrvntnNeededMsg = rules.get(QuoteCapabilityProcess.DISPLAY_MANUAL_INTRVNTN_NEEDED_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_MANUAL_INTRVNTN_NEEDED_MSG)).booleanValue():false;

                isDisplayQtProcessInSapMsg = rules.get(QuoteCapabilityProcess.DISPLAY_QT_PROCESS_IN_SAP_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_QT_PROCESS_IN_SAP_MSG)).booleanValue():false;

                isDisplayNoCustEnrollMsg = rules.get(QuoteCapabilityProcess.DISPLAY_NO_CUST_ENROLL_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NO_CUST_ENROLL_MSG)).booleanValue():false;

                isDisplayHasOrderedMsg = rules.get(QuoteCapabilityProcess.DISPLAY_HAS_ORDERED_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_HAS_ORDERED_MSG)).booleanValue():false;

                isDisplayCancelledQuoteMsg = rules.get(QuoteCapabilityProcess.DISPLAY_CANCELLED_QT_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CANCELLED_QT_MSG)).booleanValue():false;

                isDisplaySpecBidQuoteMsg = rules.get(QuoteCapabilityProcess.DISPLAY_SB_QT_MSG)!= null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SB_QT_MSG)).booleanValue():false;

                isDisplaySubmitterAccessRequiredMsg = rules.get(QuoteCapabilityProcess.DISPLAY_SBMT_ACL_REQ_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SBMT_ACL_REQ_MSG)).booleanValue():false;

                isDisplayChannelSQOrderMsg = rules.get(QuoteCapabilityProcess.DISPLAY_CHANNEL_QT_ORDER_MSG)!= null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CHANNEL_QT_ORDER_MSG)).booleanValue():false;

                isDisplayDldRichTextBtn = rules.get(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN)).booleanValue():false;

                isDisplayExecSmmryMsg = rules.get(QuoteCapabilityProcess.DISPLAY_EXEC_SMMRY_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EXEC_SMMRY_MSG)).booleanValue():false;

                isDisplayObsltPartMsg = rules.get(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG)).booleanValue():false;

                isDisplayExprtSprdshtBtn = rules.get(QuoteCapabilityProcess.DISPLAY_EXPRT_SPRDSHT_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EXPRT_SPRDSHT_BTN)).booleanValue():false;
                        
                isDisplayExportQuoteAsSpreadsheet = rules.get(QuoteCapabilityProcess.DISPLAY_EXPORT_QT_AS_SPREADSHEET_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EXPORT_QT_AS_SPREADSHEET_BTN)).booleanValue():false;     
                logContext.debug(this, "quote: " + quote.getQuoteHeader().getWebQuoteNum() + " isDisplayExportQuoteAsSpreadsheet: " + isDisplayExportQuoteAsSpreadsheet);        

                isDisplayElaSQOrderMsg = rules.get(QuoteCapabilityProcess.DISPLAY_ELA_QT_ORDER_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_ELA_QT_ORDER_MSG)).booleanValue():false;

                isDisplayCancelApprovedBid = rules.get(QuoteCapabilityProcess.DISPLAY_CANCEL_APPROVED_BID) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CANCEL_APPROVED_BID)).booleanValue():false;

                isDisplaySVPLevelNotMatchMsg = rules.get(QuoteCapabilityProcess.DISPLAY_SVP_LVL_NOT_MATCH_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SVP_LVL_NOT_MATCH_MSG)).booleanValue():false;

                isDisplayCtrctNumNotMatchMsg = rules.get(QuoteCapabilityProcess.DISPLAY_CTRCT_NUM_NOT_MATCH_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CTRCT_NUM_NOT_MATCH_MSG)).booleanValue():false;

                isDisplayCntryNotAllowOrderMsg = rules.get(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG)).booleanValue():false;

                isDisplayOEMNotAllowOrderMsg = rules.get(QuoteCapabilityProcess.DISPLAY_OEM_NOT_ALLOW_ORDER_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_OEM_NOT_ALLOW_ORDER_MSG)).booleanValue():false;

                isDisplayEffDateInFtrMsg = rules.get(QuoteCapabilityProcess.DISPLAY_EFF_DATE_IN_FTR_MSG) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EFF_DATE_IN_FTR_MSG)).booleanValue():false;

                isDispCp4PrcIncrButton = (rules.get(QuoteCapabilityProcess.DISPLAY_COPY_SB_FOR_PRICE_INCREASE)!= null?
                    ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_COPY_SB_FOR_PRICE_INCREASE)).booleanValue():false)
                    && ButtonDisplayRuleFactory.singleton().isDisplayCp4PrcIncr(quote.getQuoteHeader());

                isDispSubmitButton = rules.get(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN)).booleanValue():false;

                isDispBidIterButton = rules.get(QuoteCapabilityProcess.DISPLAY_BID_ITERATION_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_BID_ITERATION_BTN)).booleanValue():false;

                isDisplaySaveQuoteAsDraft = rules.get(QuoteCapabilityProcess.DISPLAY_SAVE_QT_AS_DRAFT_BTN) != null?
                                ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SAVE_QT_AS_DRAFT_BTN)).booleanValue():false;

                isDisplayConv2StdCopyBtn = rules.get(QuoteCapabilityProcess.DISPLAY_CONV_TO_STD_COPY_BTN) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CONV_TO_STD_COPY_BTN)).booleanValue():false;

                isDisplayNotAllowOrderForCustBlockMsg = rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG) != null?
                                ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG)).booleanValue():isDisplayNotAllowOrderForCustBlockMsg;

                isDispChgOutputOptionFlag = (rules.get(QuoteCapabilityProcess.DISPLAY_COPY_SB_FOR_CHNAGE_OUTPUT)!= null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_COPY_SB_FOR_CHNAGE_OUTPUT)).booleanValue():false)
                        &&ButtonDisplayRuleFactory.singleton().isDispChgOutputOption (quote.getQuoteHeader());
                isDispChgOutputOptionWithDivestedPartMsg = (rules.get(QuoteCapabilityProcess.NOT_COPY_CHNAGE_OUTPUT_FOR_DIVESTED_PART)!= null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.NOT_COPY_CHNAGE_OUTPUT_FOR_DIVESTED_PART)).booleanValue():false)
                        &&ButtonDisplayRuleFactory.singleton().isDispChgOutputOption (quote.getQuoteHeader());
                isPGSSubmitter = rules.get(QuoteCapabilityProcess.DISPLAY_PGS_SUBMITTER)!=null?
	                    ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_PGS_SUBMITTER)).booleanValue():false;

	            isDisplayOrdWithoutOpptNumMsg = (rules.get(QuoteCapabilityProcess.DISPLAY_ORD_WITHOUT_OPPTNUM_MSG))!=null?
	            	((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_ORD_WITHOUT_OPPTNUM_MSG)).booleanValue():false;

	            isDisplaySSPWithOutContract = rules.get(QuoteCapabilityProcess.DISPLAY_SSP_NO_CONTACT) != null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SSP_NO_CONTACT)).booleanValue():false;
                        
	            	
	            isDisplayOrderForDivestedPartMsg = rules.get(QuoteCapabilityProcess.NOT_ALLOW_ORDER_FOR_DIVESTED_PART)!=null?
			  		(Boolean)rules.get(QuoteCapabilityProcess.NOT_ALLOW_ORDER_FOR_DIVESTED_PART):false;
			  	isDispCp4PrcIncr4DivestdPartMsg = rules.get(QuoteCapabilityProcess.NOT_COPY_PRICE_INCREASE_FOR_DIVESTED_PART)!=null?
					  		(Boolean)rules.get(QuoteCapabilityProcess.NOT_COPY_PRICE_INCREASE_FOR_DIVESTED_PART):false;	
				isDispBidIterDivestdPartMsg = rules.get(QuoteCapabilityProcess.NOT_COPY_BID_ITERATION_FOR_DIVESTED_PART)!=null?
							  		(Boolean)rules.get(QuoteCapabilityProcess.NOT_COPY_BID_ITERATION_FOR_DIVESTED_PART):false;	
							  		
				isDispCp4ExpiryDateButton = (rules.get(QuoteCapabilityProcess.DISPLAY_COPY_FOR_EXPRIY_DATE)!= null?
						((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_COPY_FOR_EXPRIY_DATE)).booleanValue():false)
						&& ButtonDisplayRuleFactory.singleton().isDisplayCp4ExpiryDate(quote.getQuoteHeader());			  		
							  		
            
            }

            isCopied4PrcIncrQuoteFlag = quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag();

            isBidIteratnQtFlag = quote.getQuoteHeader().isBidIteratnQt();

            isCopiedForOutputChangeFlag = quote.getQuoteHeader().isCopiedForOutputChangeFlag();
            
            isExpDateExtendedFlag = quote.getQuoteHeader().isExpDateExtendedFlag();
            
	        isDisplayFct2PaQuoteTerminationMsg = (rules.get(QuoteCapabilityProcess.DISPLAY_FCTTOPA_QUOTE_TERMINATION_MSG))!=null?
			        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_FCTTOPA_QUOTE_TERMINATION_MSG)).booleanValue():false;
			        
			isModifyCRADWhenOrder = header.isDisShipInstAdrFlag()&&SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_PART_PRICE_TAB.equals(getDisplayTabAction());

        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
        }

        // Set all renewal quote action button conditions to false
        isDisplayCancelSpecialBid = false;
        isDisplayUpdateSpecialBid = false;
    }


    public boolean isDisplayOverrideExpDate() {
        return isDisplayOverrideExpDate;
    }

    public boolean isDisplayAcquisition() {
        return ((header.isSalesQuote() && (header.isFCTQuote() || header.isMigration()))
                || (header.isRenewalQuote() && header.isFCTQuote()));
    }

    public boolean isDisplayOrigSAPRnwlQuoteNum() {
        return (header.isSalesQuote() && header.isMigration() && StringUtils.isNotBlank(header.getRenwlQuoteNum()));
    }

    public String getCancelQuoteBtnParams() {
        return cancelQuoteBtnParams;
    }

    public String getCreateCopyBtnParams() {
        return createCopyBtnParams;
    }

    public String getOrderBtnParams() {
        return orderBtnParams;
    }
    
    protected String getEvaluationStatusDesc(QuoteHeader header)
    {
    	String desc = "";
    	ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        if ( header.isAcceptedByEval() )
        {
        	desc = appCtx.getI18nValueAsString(I18NBundleNames.NEW_QUOTE_BASE, locale, NewQuoteMessageKeys.S_ASGNEVAL);
        }
        else if ( header.isSubmittedForEval() )
        {
        	desc = appCtx.getI18nValueAsString(I18NBundleNames.NEW_QUOTE_BASE, locale, NewQuoteMessageKeys.S_TOBEEVAL);
        }
        else if ( header.isReturnForChgByEval() || header.isEditForRetChgByEval() )
        {
        	desc = appCtx.getI18nValueAsString(I18NBundleNames.NEW_QUOTE_BASE, locale, NewQuoteMessageKeys.S_RESEVAL);
        }
        else if ( header.isDeleteByEditor() )
        {
        	desc = appCtx.getI18nValueAsString(I18NBundleNames.NEW_QUOTE_BASE, locale, NewQuoteMessageKeys.S_DELTEVAL);
        }
        return desc;
    }

    public String getOverallStatus() {
        List overallStatuses = header.getQuoteOverallStatuses();
        if (overallStatuses != null) {
            StringBuffer sbOS = new StringBuffer();
            Iterator iterator = overallStatuses.iterator();
            while(iterator.hasNext()){
            	CodeDescObj codeDescObj = (CodeDescObj)iterator.next();
            	String codeDesc = "";
            	if (isPGSFlag()) {
            		if (codeDescObj.getCode().equalsIgnoreCase("QS010")) {
            			sbOS.append(codeDesc);
            		} else {
            			codeDesc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, codeDescObj.getCode());
                    	if (!iterator.hasNext()) {
                    		sbOS.append(codeDesc);
                    	} else {
                    		sbOS.append(codeDesc + ", ");
                    	}
            		}
            	} else {
            		codeDesc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, codeDescObj.getCode());
            		if (!iterator.hasNext()) {
                		sbOS.append(codeDesc);
                	} else {
                		sbOS.append(codeDesc + ", ");
                	}
            	}

            }
            overallStatus = sbOS.toString();
        }
        return overallStatus;
    }

    public String getQuoteStartDateAsString() {
        if (quoteStartDate == null)
            return "";
        else
            return DateHelper.getDateByFormat(quoteStartDate, "dd MMM yyyy");
    }

    public String getQuoteExpDate() {
        return DateHelper.getDateByFormat(quoteExpDate, "dd MMM yyyy");
    }

    public String getEstmtdOrdDate() {
        return DateHelper.getDateByFormat(estmtdOrdDate, "dd MMM yyyy");
    }

    public String getQuoteSubmittedDate() {
        return quoteSubmittedDate == null ? "" : DateHelper.formatToLocalTime(quoteSubmittedDate,
                "dd MMM yyyy HH:mm:ss zzz", this.getDisplayTimeZone(), locale);
    }

    public String getCustReqstdArrivlDate() {
        return custReqstdArrivlDate == null ? "" : DateHelper.getDateByFormat(custReqstdArrivlDate, "dd MMM yyyy");
    }
    
    public String getPriorQtSbmtdDate() {
        return priorQtSbmtdDate == null ? "" : DateHelper.formatToLocalTime(priorQtSbmtdDate,
                "dd MMM yyyy HH:mm:ss zzz", this.getDisplayTimeZone(), locale);
    }

    public boolean isCpFromApprvdBid() {
        return isCpFromApprvdBid;
    }

    public String getSapQuoteNum() {
        return sapQuoteNum;
    }

    public String getSaveAddiSBJustBtnParams() {
        return saveAddiSBJustBtnParams;
    }

    public String getSaveReviewCommnetsBtnParams() {
        return saveReviewCommnetsBtnParams;
    }

    public String getSqoReference() {
        return sqoReference;
    }

    public String getSubmitAddiProcBtnParams() {
        return submitAddiProcBtnParams;
    }

    public String getSubmitApprovalCommentsBtnParams() {
        return submitApprovalCommentsBtnParams;
    }

    public String getSubmitOtherCommentsBtnParams() {
        return submitOtherCommentsBtnParams;
    }

    public String getUpdateQuoteBtnParams() {
        return updateQuoteBtnParams;
    }

    public boolean isDisplayAddiComments() {
        return isDisplayAddiComments;
    }

    public boolean isDisplayApprAction() {
        return isDisplayApprAction;
    }

    public boolean isDisplayCancelQuote() {
        return isDisplayCancelQuote;
    }

    public boolean isDisplayCreateCopy() {
        return isDisplayCreateCopy;
    }

    public boolean isDisplayEnterAddiSBJust() {
        return isDisplayEnterAddiSBJust;
    }

    public boolean isDisplayCreateCopyCheckbox() {
		return isDisplayCreateCopyCheckbox;
	}

	public boolean isDisplayOrder() {
        return isDisplayOrder;
    }

    public boolean isDisplayReqICN() {
        return isDisplayReqICN;
    }

    public boolean isDisplayReqPreCreditCheck() {
        return isDisplayReqPreCreditCheck;
    }

    public boolean isDisplaySaveComments() {
        return isDisplaySaveComments;
    }

    public boolean isDisplaySubmitRequest() {
        return isDisplaySubmitRequest;
    }

    public boolean isDisplayUpdateQuote() {
        return isDisplayUpdateQuote;
    }

    public boolean isDisplayDldRichTextBtn() {
        return isDisplayDldRichTextBtn;
    }

    public String getRenewalQuoteNum() {
        return renewalQuoteNum;
    }

    public String getRenewalStatus() {
        return renewalStatus;
    }

    public String getSecondaryStatus() {
        return secondaryStatus;
    }

    public String getRenewalDueDate() {
        return DateHelper.getDateByFormat(renewalDueDate, "dd MMM yyyy");
    }

    public String getSpecialBidExpDate() {
        return DateHelper.getDateByFormat(specialBidExpDate, "dd MMM yyyy");
    }

    public boolean isSpecialBid(){
        return header.getSpeclBidFlag() == 1;
    }

    public String getGraphTabParams() {
        String params = "&amp;" + ParamKeys.PARAM_QUOTE_NUM + "=" + this.getWebQuoteNum();
        return params;
    }

    public String getRenewQuoteDetailsParams(){
        StringBuffer goBackURL = new StringBuffer();
        goBackURL.append(
                ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                        ActionHandlerKeys.JADE_ACTION_KEY)).append("=").append(getDisplayTabAction());
        HtmlUtil.addURLParam(goBackURL, SubmittedQuoteParamKeys.PARAM_QUOTE_NUM, getWebQuoteNum());

        StringBuffer renewQuoteDetailsURL = new StringBuffer(ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.RENEWAL_QUOTE_DETAIL_URL));
        HtmlUtil.addURLParam(renewQuoteDetailsURL, DraftRQParamKeys.PARAM_RPT_QUOTE_NUM, renewalQuoteNum);
        HtmlUtil.addURLParam(renewQuoteDetailsURL, DraftRQParamKeys.PARAM_RPT_P1, HtmlUtil.urlEncode(goBackURL.toString()));
        return renewQuoteDetailsURL.toString();
    }

    public boolean isDisplayCancelSpecialBid(){
        return isDisplayCancelSpecialBid;
    }

    public boolean isDisplayUpdateSpecialBid(){
        return isDisplayUpdateSpecialBid;
    }

    public boolean isDisplayExprtSprdshtBtn() {
        return isDisplayExprtSprdshtBtn;
    }
    
    public boolean isDisplayExportQuoteAsSpreadsheet() {
    	return isDisplayExportQuoteAsSpreadsheet;
    }

    public String hasActiveDraftQuote(){
        return hasActiveDraftQuote;
    }

    public boolean isDisplayManualIntrvntnNeededMsg() {
        return isDisplayManualIntrvntnNeededMsg;
    }

    public boolean isDisplayQtProcessInSapMsg() {
        return isDisplayQtProcessInSapMsg;
    }

    public boolean isDisplayHasOrderedMsg() {
        return isDisplayHasOrderedMsg;
    }

    public boolean isDisplayNoCustEnrollMsg() {
        return isDisplayNoCustEnrollMsg;
    }

    public boolean isDisplayCancelledQuoteMsg() {
        return isDisplayCancelledQuoteMsg;
    }

    public boolean isDisplayChannelSQOrderMsg() {
        return isDisplayChannelSQOrderMsg;
    }

    public boolean isDisplaySpecBidQuoteMsg() {
        return isDisplaySpecBidQuoteMsg;
    }

    public boolean isDisplaySubmitterAccessRequiredMsg() {
        return isDisplaySubmitterAccessRequiredMsg;
    }

    public boolean isDisplayExecSmmryMsg() {
        return isDisplayExecSmmryMsg;
    }

    public boolean isDisplayObsltPartMsg() {
        return isDisplayObsltPartMsg;
    }
    
    public boolean isModifyCRADWhenOrder() {
		return isModifyCRADWhenOrder;
	}

	protected String getRedirectActionURL(String action1, String action2, String[] paramKeys, String[] paramValues) {
        String actionURL = HtmlUtil.getURLForAction(action1);
        String redirectURL = HtmlUtil.getURLForAction(action2);
        StringBuffer params = new StringBuffer();

        for (int i = 0; i < paramKeys.length; i++) {
            params.append("&amp;").append(paramKeys[i]).append("=").append(paramValues[i]);
        }

        StringBuffer sb = new StringBuffer(actionURL);
        if (StringUtils.isNotBlank(redirectURL))
            sb.append("&amp;redirectURL=").append(redirectURL);
        if (params.length() > 0)
            sb.append(params.toString());
        return sb.toString();
    }
/**
 * 
 * Modification date:July 18,2013
 * @author: jiewbj@cn.ibm.com
 * Description: pass action2 this.getDisplayTabAction().
 */
    public String getUpdateExpDateActionURL() {
    	String actionURL =null;
		actionURL =  this.getActionURL(SubmittedQuoteActionKeys.UPDATE_QUOTE_DATE,
            null, null, null);
        StringBuffer sb = new StringBuffer(actionURL);
        HtmlUtil.addURLParam(sb, ParamKeys.PARAM_QUOTE_NUM, this.getWebQuoteNum());

        return sb.toString();
    }
    
    public String getExtendExpDateActionURL() {
        String actionURL =  this.getActionURL(SubmittedQuoteActionKeys.EXTEND_QUOTE_EXP_DATE,
                null, null, null);
        StringBuffer sb = new StringBuffer(actionURL);
        HtmlUtil.addURLParam(sb, ParamKeys.PARAM_QUOTE_NUM, this.getWebQuoteNum());

        return sb.toString();
    }
    
    public String getGoExtendExpDateActionURL() {
        String action2 = this.getDisplayTabAction();
        return this.genBtnParamsForAction(SubmittedQuoteActionKeys.GO_EXTEND_QUOTE_EXP_DATE, action2, null);
    }

    public String getUpdateQuoteActionURL() {
        String[] keys = { SubmittedQuoteParamKeys.PARAM_COPY_UPDATE_FLAG, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { "0", this.getWebQuoteNum() };
        return getRedirectActionURL(SubmittedQuoteActionKeys.COPY_UPDATE_SUBMITTED_QUOTE,
                DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB, keys, values);
    }

    public String getCreateCopyActionURL() {
        String[] keys = { SubmittedQuoteParamKeys.PARAM_COPY_UPDATE_FLAG, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { "1", this.getWebQuoteNum() };
        return getRedirectActionURL(SubmittedQuoteActionKeys.COPY_UPDATE_SUBMITTED_QUOTE,
                DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB, keys, values);
    }

    public String getSumbitApproverActionURL() {
    	if(isExpDateExtendedFlag()){
    		  String[] keys = { ParamKeys.PARAM_QUOTE_NUM };
  		    String[] values = { this.getWebQuoteNum() };
    		String params = ParamKeys.PARAM_QUOTE_NUM + "=" + this.getWebQuoteNum();
    		return this.getActionURL(SubmittedQuoteActionKeys.GO_EXTEND_QUOTE_EXP_DATE, 
        			SubmittedQuoteActionKeys.SUBMIT_APPROVER_ACTION, keys, values);
    	}else{
		    String action2 = this.getDisplayTabAction();
		    String[] keys = { ParamKeys.PARAM_QUOTE_NUM };
		    String[] values = { this.getWebQuoteNum() };
		//        return this.getActionURL(SubmittedQuoteActionKeys.SUBMIT_APPROVER_ACTION, action2, keys, values);
		    return this.getActionURL(SubmittedQuoteActionKeys.SUBMIT_APPROVER_ACTION, null, keys, values);
    	}
    }

    public String getCancelApprovedBidActionURL()
    {
        String action2 = this.getDisplayTabAction();
        String[] keys = { ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
//        return this.getActionURL(SubmittedQuoteActionKeys.SUBMIT_APPROVER_ACTION, action2, keys, values);
        return this.getActionURL(SubmittedQuoteActionKeys.CANCEL_APPROVD_BID_ACTION, null, keys, values);
    }

    public String getUpdateRQSBInfoURL() {
        String[] keys = { SubmittedQuoteParamKeys.PARAM_RNWL_QT_NUM, ParamKeys.PARAM_QUOTE_NUM, SubmittedQuoteParamKeys.REQUESTED_ACTION };
        String[] values = { this.getRenewalQuoteNum(), this.getWebQuoteNum(), this.getDisplayTabAction() };
        return this.getActionURL(SubmittedQuoteActionKeys.UPDATE_RQ_SBINFO, null, keys, values);
    }

    public String getCancelQuoteURL() {
        String action2 = this.getDisplayTabAction();
        String[] keys = { ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
        return this.getActionURL(SubmittedQuoteActionKeys.CANCEL_SUBMITTED_QUOTE, action2, keys, values);
    }

    public String getSubmitterAddiSBJustURL() {
        String action2 = this.getDisplayTabAction();
        String[] keys = { SubmittedQuoteParamKeys.PARAM_USER_ROLE, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_SUBMITTER, this.getWebQuoteNum() };
//        return this.getActionURL(SubmittedQuoteActionKeys.ADD_SB_COMMENT_ATTACHMENTS, action2, keys, values);
        return this.getActionURL(SubmittedQuoteActionKeys.ADD_SB_COMMENT_ATTACHMENTS, null, keys, values);
    }

    public String getReviewerCommentsURL() {
        String action2 = this.getDisplayTabAction();
        String[] keys = { SubmittedQuoteParamKeys.PARAM_USER_ROLE, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_REVIEWER, this.getWebQuoteNum() };
//        return this.getActionURL(SubmittedQuoteActionKeys.ADD_SB_COMMENT_ATTACHMENTS, action2, keys, values);
        return this.getActionURL(SubmittedQuoteActionKeys.ADD_SB_COMMENT_ATTACHMENTS, null, keys, values);
    }
    
    public String getAddRedirectReviewerURL() {
    	String[] keys = {ParamKeys.PARAM_QUOTE_NUM };
        String[] values = {this.getWebQuoteNum() };
        return this.getActionURL(SubmittedQuoteActionKeys.SUBMITTEDQT_ADD_REVIEWER, null, keys, values);
    }

    public String getApproverCommentsURL() {
        String action2 = this.getDisplayTabAction();
        String[] keys = { SubmittedQuoteParamKeys.PARAM_USER_ROLE, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { SubmittedQuoteConstants.SUBMITTED_QUOTE_USER_ROLE_APPROVER, this.getWebQuoteNum() };
//        return this.getActionURL(SubmittedQuoteActionKeys.ADD_SB_COMMENT_ATTACHMENTS, action2, keys, values);
        return this.getActionURL(SubmittedQuoteActionKeys.ADD_SB_COMMENT_ATTACHMENTS, null, keys, values);
    }

    public String getRedirectURL()
    {
        String action2 = this.getDisplayTabAction();
        String[] keys = {ParamKeys.PARAM_QUOTE_NUM };
        String[] values = {this.getWebQuoteNum() };
//        return this.getActionURL(SubmittedQuoteActionKeys.ADD_SB_COMMENT_ATTACHMENTS, action2, keys, values);
        return this.getActionURL(action2, null, keys, values);
    }

    public String getOrderURL() {
        String url = null;

        if (header.isSalesQuote()) {
        	
        	boolean isEditableMtm=isShowMTMSerial() ;
        	
        	if(this.isPGSFlag()){
        		
                if (isModifyCRADWhenOrder||isEditableMtm) {
                	/*
                    return this.genBtnParams(
                            SubmittedQuoteActionKeys.UPDATE_LINE_ITEM_CRAD_DATE,
                            ApplicationContextFactory.singleton().getApplicationContext()
                                    .getConfigParameter(
                            ApplicationProperties.SUBMITTED_SALES_QUOTE_ORDER_URL), null);
                            */
                	String params = SubmittedQuoteParamKeys.PARAM_QUOTE_NUM + "=" + getSqoReference()
                	+ ","+SubmittedQuoteParamKeys.PARAM_PO + "=" + "PGS"
                	+ ","+SubmittedQuoteParamKeys.PARAM_ISORDERNOW + "=" + "false"
                	+ ","+SubmittedQuoteParamKeys.PARAM_SAP_QUOTE_NUM + "=" + (getSapQuoteNum()==null?"":getSapQuoteNum())
                	+ ","+SubmittedQuoteParamKeys.PARAM_HASSAAS+ "=" + hasSaaSLineItem;
                	
                	url = this.genBtnParamsForAction(SubmittedQuoteActionKeys.UPDATE_LINE_ITEM_CRAD_DATE, 
                			SubmittedQuoteActionKeys.ORDER_SUBMITTED_QUOTE, params);
                	return url;
                }else{
                	//When order submitted quote, should be sure there is no missing tou.
            		/*return ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                            ApplicationProperties.SUBMITTED_SALES_QUOTE_ORDER_URL);
                            */
                    return this.genBtnParams(
                            SubmittedQuoteActionKeys.ORDER_SUBMITTED_QUOTE,
                            ApplicationContextFactory.singleton().getApplicationContext()
                                    .getConfigParameter(
                            ApplicationProperties.SUBMITTED_SALES_QUOTE_ORDER_URL), null);
                }
        	}

            String[] keys = { ParamKeys.PARAM_SITE_NUM, ParamKeys.PARAM_AGREEMENT_NUM,
                    SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM, ParamKeys.PARAM_DEST };
            String[] values = { siteNumber, agreementNum, webQuoteNum, "1"};
            if (isModifyCRADWhenOrder||isEditableMtm) {
            	url = this.getActionURL(SubmittedQuoteActionKeys.UPDATE_LINE_ITEM_CRAD_DATE,DraftQuoteActionKeys.SET_USER_COOKIE, keys, values);
            }else{
            	url = this.getActionURL(DraftQuoteActionKeys.SET_USER_COOKIE, null, keys, values);	
            }
            
        }
        else {
            String[] keys = { ParamKeys.PARAM_SITE_NUM, ParamKeys.PARAM_AGREEMENT_NUM,
                    SubmittedQuoteParamKeys.PARAM_RNWL_QT_NUM, ParamKeys.PARAM_DEST };
            String[] values = { siteNumber, agreementNum, renewalQuoteNum, "2" };
            url = this.getActionURL(DraftQuoteActionKeys.SET_USER_COOKIE, null, keys, values);
        }

        return url;
    }

    public String getUpdateICNPreCreditURL() {
        String action2 = this.getDisplayTabAction();
        String[] keys = { ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
        return this.getActionURL(SubmittedQuoteActionKeys.UPDATE_ICN_PRECREDIT, action2, keys, values);
    }

    public String getDldRichTextUrl() {
        String[] keys = { SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM, ParamKeys.PARAM_IS_SBMT_QT };
        String[] values = { this.getWebQuoteNum(), "true" };
        return this.getActionURL(SubmittedQuoteActionKeys.SUBMIT_QUOTE_RTF_DOWNLOAD, null, keys, values);
    }

    public String getExprtAsSprdshtUrl() {
        String[] keys = { SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
        return this.getActionURL(SubmittedQuoteActionKeys.EXPORT_SUBMITTED_QUOTE, null, keys, values);
    }
    
    //this is one for export as not read only sheet for under evaluation quote
    public String getExportUnderEvalAsSheetUrl()
    {
    	String[] keys = { SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
        return this.getActionURL(SubmittedQuoteActionKeys.EXPORT_EVAL_QUOTE, null, keys, values);
    }
    
    public String getExportUnderEvalAsSheetNativeUrl()
    {
    	String[] keys = { SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
        return this.getActionURL(SubmittedQuoteActionKeys.EXPORT_EVAL_QUOTE_NATIVE_EXCEL, null, keys, values);
    }
    
   

    public String getExprtAsNativeSprdshtUrl() {
        String[] keys = { SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
        return this.getActionURL(SubmittedQuoteActionKeys.EXPORT_SUBMITTED_QUOTE_NATIVE_EXCEL, null, keys, values);
    }


    public String getCopyForOutputChangeURL() {
        String[] keys = { ParamKeys.PARAM_QUOTE_NUM,
        				  SubmittedQuoteParamKeys.PARAM_COPY_UPDATE_FLAG,
        				  SubmittedQuoteParamKeys.PARAM_WEB_REF_FLAG,
        				  };
        String[] values = { this.getWebQuoteNum(),
        					"1",
        					QuoteConstants.QT_COPY_TYPE_OPTCHG_STR};
        return this.getRedirectActionURL(SubmittedQuoteActionKeys.COPY_UPDATE_SUBMITTED_QUOTE,
        		SubmittedQuoteActionKeys.SUBMIT_DRAFT_SQ_AS_FINAL, keys, values);
    }

    public String getPostTabAction(){
        return "";
    }

    public boolean isSPBidTab() {
        return isSPBidTab;
    }
    /**
     * @return Returns the isTncAndSummaryEditable.
     */
    public boolean isTncAndSummaryEditable() {
        return isTncAndSummaryEditable;
    }

    public boolean isDisplayExpSBCannotOrderMsg() {
        return isDisplayExpSBCannotOrderMsg;
    }
    public boolean isDisplaySpecBidNotActiveMsg() {
        return isDisplaySpecBidNotActiveMsg;
    }
    public boolean isDisplaySBExpDatePassedMsg() {
        return isDisplaySBExpDatePassedMsg;
    }
    /**
     * @return Returns the approveLevel.
     */
    public String getApproveLevel() {
        return approveLevel;
    }

    /**
     * @return Returns the reviewerComments.
     */
    public String getReviewerComments() {
    	if ( reviewerComments == null )
    	{
    		reviewerComments = "";
    	}
        return reviewerComments;
    }

    /**
	 * @return Returns the approverComments.
	 */
	public String getApproverComments() {
		if ( approverComments == null )
		{
			approverComments = "";
		}
		return approverComments;
	}
	/**
	 * @return Returns the submitterComments.
	 */
	public String getSubmitterComments() {
		if ( submitterComments == null )
		{
			submitterComments = "";
		}
		return submitterComments;
	}
	/**
	 * @return Returns the approverAddReviewerComments.
	 */
	public String getApproverAddReviewerComments() {
		if ( approverAddReviewerComments == null )
		{
			approverAddReviewerComments = "";
		}
		return approverAddReviewerComments;
	}
    /**
     * @return Returns the justComments.
     */
    public String getJustComments() {
        return justComments;
    }
    /**
     * @return Returns the tncComments.
     */
    public String getTncComments() {
        return tncComments;
    }

    public String getQuoteClassfctnCodeDesc() {
	    String code = header.getQuoteClassfctnCode();
	    CodeDescObj codeObj = QuoteClassificationCodeFactory.singleton().findByCode(code);
	    if (codeObj == null)
	        return this.getI18NString(MessageKeys.BUNDLE_APPL_I18N_QUOTE, SubmittedQuoteMessageKeys.NOT_CLASSIFIED);
	    else
	        return codeObj.getCodeDesc();
	}

	public String getGrapTabHiddenIDs () {
	    StringBuffer sb = new StringBuffer();
	    if (!showExecSummaryTab) {
	        sb.append(SUBMITTED_QUOTE_EXEC_SUMMARY_TAB_ID) ;
	    }
	    return sb.toString();
	}
    /**
     * @return Returns the downloadFileURL.
     */
    public String getDownloadFileURL() {
        return downloadFileURL;
    }
    /**
     * @return Returns the approverCommentsAdd.
     */
    public String getApproverCommentsAdd() {
        return approverCommentsAdd;
    }

    public boolean isDisplayElaSQOrderMsg() {
        return isDisplayElaSQOrderMsg;
    }
    /**
     * @return Returns the isDisplayCancelApprovedBid.
     */
    public boolean isDisplayCancelApprovedBid() {
        return isDisplayCancelApprovedBid;
    }

    public boolean isDisplaySVPLevelNotMatchMsg() {
        return isDisplaySVPLevelNotMatchMsg;
    }

    public boolean isDisplayCtrctNumNotMatchMsg() {
        return isDisplayCtrctNumNotMatchMsg;
    }
    /**
     * @return Returns the isDisplayCntryNotAllowOrderMsg.
     */
    public boolean isDisplayCntryNotAllowOrderMsg() {
        return isDisplayCntryNotAllowOrderMsg;
    }
    /**
     * @param isDisplayCntryNotAllowOrderMsg The isDisplayCntryNotAllowOrderMsg to set.
     */
    public void setDisplayCntryNotAllowOrderMsg(boolean isDisplayCntryNotAllowOrderMsg) {
        this.isDisplayCntryNotAllowOrderMsg = isDisplayCntryNotAllowOrderMsg;
    }
    /**
     * @return Returns the isDisplayOEMNotAllowOrderMsg.
     */
    public boolean isDisplayOEMNotAllowOrderMsg() {
        return isDisplayOEMNotAllowOrderMsg;
    }
    /**
     * @param isDisplayOEMNotAllowOrderMsg The isDisplayOEMNotAllowOrderMsg to set.
     */
    public void setDisplayOEMNotAllowOrderMsg(boolean isDisplayOEMNotAllowOrderMsg) {
        this.isDisplayOEMNotAllowOrderMsg = isDisplayOEMNotAllowOrderMsg;
    }
    /**
     * @return Returns the isDisplayEffDateInFtrMsg.
     */
    public boolean isDisplayEffDateInFtrMsg() {
        return isDisplayEffDateInFtrMsg;
    }
    /**
     * @param isDisplayEffDateInFtrMsg The isDisplayEffDateInFtrMsg to set.
     */
    public void setDisplayEffDateInFtrMsg(boolean isDisplayEffDateInFtrMsg) {
        this.isDisplayEffDateInFtrMsg = isDisplayEffDateInFtrMsg;
    }

    public String getCpApprdSB4PrcIncrURL() {
        //webRefFlag 3 means copy approved sb for price increase, sp will use this flag to update the quote stage code to CPYPRCINC
        String[] keys = { SubmittedQuoteParamKeys.PARAM_COPY_UPDATE_FLAG, SubmittedQuoteParamKeys.PARAM_WEB_REF_FLAG, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { "1", "3", this.getWebQuoteNum() };
        return getRedirectActionURL(SubmittedQuoteActionKeys.COPY_UPDATE_SUBMITTED_QUOTE,
                SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_PART_PRICE_TAB, keys, values);
    }
    
    public String getExpiryDateChangeURL() {
        String[] keys = { SubmittedQuoteParamKeys.PARAM_COPY_UPDATE_FLAG, SubmittedQuoteParamKeys.PARAM_WEB_REF_FLAG, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { "1",  QuoteConstants.QT_COPY_TYPE_EXPIRDATE_STR, this.getWebQuoteNum() };
        return getRedirectActionURL(SubmittedQuoteActionKeys.COPY_UPDATE_SUBMITTED_QUOTE,
        		SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB, keys, values);
    }
    /**
     * @return Returns the isDispCp4PrcIncrButton.
     */
    public boolean isDispCp4PrcIncrButton() {
        return isDispCp4PrcIncrButton;
    }
    /**
     * @param isDispCp4PrcIncrButton The isDispCp4PrcIncrButton to set.
     */
    public void setDispCp4PrcIncrButton(boolean isDispCp4PrcIncrButton) {
        this.isDispCp4PrcIncrButton = isDispCp4PrcIncrButton;
    }

    /**
     * @return Returns the isCopied4PrcIncrQuoteFlag.
     */
    public boolean isCopied4PrcIncrQuoteFlag() {
        return isCopied4PrcIncrQuoteFlag;
    }
    /**
     * @param isCopied4PrcIncrQuoteFlag The isCopied4PrcIncrQuoteFlag to set.
     */
    public void setCopied4PrcIncrQuoteFlag(boolean isCopied4PrcIncrQuoteFlag) {
        this.isCopied4PrcIncrQuoteFlag = isCopied4PrcIncrQuoteFlag;
    }
    /**
     * @return Returns the quoteAddDate.
     */
    public String getQuoteAddDate() {
        return quoteAddDate == null ? "" : DateHelper.formatToLocalTime( quoteAddDate,
                "dd MMM yyyy HH:mm:ss zzz",  this.getDisplayTimeZone(), locale);
    }
    /**
     * @param quoteAddDate The quoteAddDate to set.
     */
    public void setQuoteAddDate(Date quoteAddDate) {
        this.quoteAddDate = quoteAddDate;
    }

    public boolean isDispSubmitButton(){
        return this.isDispSubmitButton;
    }

    public boolean isApprover(){
        return isApprover;
    }

    /**
     * @param isDispSubmitButton The isDispSubmitButton to set.
     */
    public void setDispSubmitButton(boolean isDispSubmitButton) {
        this.isDispSubmitButton = isDispSubmitButton;
    }

    public boolean isCPPRCINCStage(){
        return 	QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(header.getQuoteStageCode());
    }
    /**
     * @return Returns the isDispBidIterButton.
     */
    public boolean isDispBidIterButton() {
        return isDispBidIterButton;
    }
    /**
     * @param isDispBidIterButton The isDispBidIterButton to set.
     */
    public void setDispBidIterButton(boolean isDispBidIterButton) {
        this.isDispBidIterButton = isDispBidIterButton;
    }

    public String getBidIterBtnURL() {
        //webRefFlag 4 means bid iteration, sp will use this flag to update the quote stage code to BIDITRQT
        String[] keys = { SubmittedQuoteParamKeys.PARAM_COPY_UPDATE_FLAG, SubmittedQuoteParamKeys.PARAM_WEB_REF_FLAG, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { "1", "4", this.getWebQuoteNum() };
        return getRedirectActionURL(SubmittedQuoteActionKeys.COPY_UPDATE_SUBMITTED_QUOTE,
                SubmittedQuoteActionKeys.DISPLAY_DRAFT_PART_PRICE_TAB, keys, values);
    }

    /**
     * @return Returns the isDisplayLAUplift.
     */
    public boolean isDisplayLAUplift() {
        return ButtonDisplayRuleFactory.singleton().isDisplayLAUplift(header);
    }

    /**
     * @return Returns the pymntTermsDays.
     */
    public int getPymntTermsDays() {
        return pymntTermsDays;
    }
    /**
     * @return Returns the isBidIteratnQtFlag.
     */
    public boolean isBidIteratnQtFlag() {
        return isBidIteratnQtFlag;
    }
    /**
     * @param isBidIteratnQtFlag The isBidIteratnQtFlag to set.
     */
    public void setBidIteratnQtFlag(boolean isBidIteratnQtFlag) {
        this.isBidIteratnQtFlag = isBidIteratnQtFlag;
    }
    /**
     * @return Returns the isDisplaySaveQuoteAsDraft.
     */
    public boolean isDisplaySaveQuoteAsDraft() {
        return isDisplaySaveQuoteAsDraft;
    }
    /**
     * @param isDisplaySaveQuoteAsDraft The isDisplaySaveQuoteAsDraft to set.
     */
    public void setDisplaySaveQuoteAsDraft(boolean isDisplaySaveQuoteAsDraft) {
        this.isDisplaySaveQuoteAsDraft = isDisplaySaveQuoteAsDraft;
    }

    public String getSubmitAsFinalBtnParams() {
    	 
		if(isExpDateExtensionQtFlag()){
		    submitAsFinalBtnParams= this.genBtnParamsForAction(SubmittedQuoteActionKeys.SUBMIT_EXTEND_QUOTE_EXP_DATE, SubmittedQuoteActionKeys.SUBMIT_DRAFT_SQ_AS_FINAL, null);
		}else{
	    	submitAsFinalBtnParams = getActionURL(SubmittedQuoteActionKeys.SUBMIT_DRAFT_SQ_AS_FINAL, null, null, null);
	    }
        
        return submitAsFinalBtnParams;
    }

    public String getSaveQuoteAsDraftBtnParams() {

        String[] keys = {ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
        if(isExpDateExtensionQtFlag()){
        	 saveQuoteAsDraftBtnParams= this.genBtnParamsForAction(SubmittedQuoteActionKeys.SAVE_EXTEND_QUOTE_EXP_DATE, this.getDisplayTabAction(), null);
        }else{
        	saveQuoteAsDraftBtnParams = getRedirectActionURL(SubmittedQuoteActionKeys.SAVE_BID_ITERATION, getDisplayTabAction(), keys, values);
        }
        return saveQuoteAsDraftBtnParams;
    }

    public boolean isBidIterEditJust(){
        return false;
    }
    /**
     * @return Returns the isDisplayConv2StdCopyBtn.
     */
    public boolean isDisplayConv2StdCopyBtn() {
        return isDisplayConv2StdCopyBtn;
    }
    /**
     * @param isDisplayConv2StdCopyBtn The isDisplayConv2StdCopyBtn to set.
     */
    public void setDisplayConv2StdCopyBtn(boolean isDisplayConv2StdCopyBtn) {
        this.isDisplayConv2StdCopyBtn = isDisplayConv2StdCopyBtn;
    }

    public String getConv2StdCopyBtnURL(){

        String[] keys = {ParamKeys.PARAM_FORWARD_FLAG, ParamKeys.PARAM_QUOTE_NUM };
        String[] values = {"true", this.webQuoteNum };

        return getActionURL(SubmittedQuoteActionKeys.CONVERT_TO_STD_COPY, null, keys, values);
    }

    public String getUpdateApprDisplayAction()
    {
        return "";
    }
    /**
     * @return Returns the oemBidTypeDesc.
     */
    public String getOemBidTypeDesc() {
        return oemBidTypeDesc;
    }
    /**
     * @return Returns the isDisplayOemBidType.
     */
    public boolean isDisplayOemBidType() {
        return super.isDisplayOemBidType() && (header.getOemBidType() != 0);
    }

	public boolean isDisplayNotAllowOrderForCustBlockMsg() {
		return isDisplayNotAllowOrderForCustBlockMsg;
	}

	public void setDisplayNotAllowOrderForCustBlockMsg(
			boolean isDisplayNotAllowOrderForCustBlockMsg) {
		this.isDisplayNotAllowOrderForCustBlockMsg = isDisplayNotAllowOrderForCustBlockMsg;
	}



    public boolean isDispChgOutputOptionFlag() {
		return isDispChgOutputOptionFlag;
	}
    
    public boolean isDispChgOutputOptionWithDivestedPartMsg(){
    	return isDispChgOutputOptionWithDivestedPartMsg;
    }

	/**
     * for bid iteration requirement
     * add this button on convert page,for user conveniently return to quote(Part/Pricing Tab)
     *
     * @return Returns the button URL.
     */
    public String getReturnToQuoteBtnURL() {

        String[] keys = { ParamKeys.BIDITRTN_POST };
        String[] values = { "TRUE" };

        return getActionURL(SubmittedQuoteActionKeys.DISPLAY_DRAFT_PART_PRICE_TAB, null, keys, values);
    }

    public boolean isSubmittedQuote(){
        return header.isSubmittedQuote();
    }

    public String getSubmitter(){
        if(StringUtils.isNotBlank(header.getSubmitterName())){
            return header.getSubmitterName() + " (" + header.getSubmitterId() + ")";
        }else{
            return header.getSubmitterId();
        }
    }

	public boolean isCopiedForOutputChangeFlag() {
		return isCopiedForOutputChangeFlag;
	}

	public void setCopiedForOutputChangeFlag(boolean isCopiedForOutputChangeFlag) {
		this.isCopiedForOutputChangeFlag = isCopiedForOutputChangeFlag;
	}

	public String getPopupAuditHistURL() {
		return popupAuditHistURL;
	}

	public boolean isDisplayNotSupportExprtSprdshtMsg(){
		return !isDisplayExprtSprdshtBtn && header.isOnlySaaSParts() && !this.isPAQuote() && !this.isPAEQuote() && !this.isFCTQuote() && !this.isSSP();
	}

	public boolean hasSaaSLineItem() {
		return hasSaaSLineItem;
	}

	public boolean isPGSSubmitter() {
		return isPGSSubmitter;
	}
	
    public boolean isCustomerMatchDistributor() {
		return isCustomerMatchDistributor;
	}

	public boolean isDisplayFct2PaQuoteTerminationMsg() {
		return isDisplayFct2PaQuoteTerminationMsg;
	}
	
	public List getApproveDataForPGSDisplay()
	{
		List retList = new ArrayList();
		List levelList = this.getCommon().getLevelList();
		boolean typeBegin = true;
		int curLevel = -1;
		for ( int i = 0; i < levelList.size(); i++ )
		{
			Integer level = (Integer)levelList.get(i);
			if ( curLevel != level.intValue() )
			{
				curLevel = level.intValue();
				typeBegin = true;
			}
			SpecialBidInfo.ChosenApprover chosenApprover = (SpecialBidInfo.ChosenApprover)this.getCommon().getChosenApproverMap().get(level);
			if ( SubmittedQuoteConstants.APPRVR_ACTION_APPRVL_PENDG.equals(chosenApprover.getLastAction()) && StringUtils.isBlank(chosenApprover.getSuperSedeApproveType()) )
			{
				SpecialBidInfo.ApproverComment appCmt = new SpecialBidInfo.ApproverComment();
				appCmt.approverName = (String)getCommon().getGroupMap().get(level);
				appCmt.userEmail = SubmittedQuoteConstants.APPRVR_ACTION_APPRVL_PENDG;
				appCmt.comment = new SpecialBidInfo.CommentInfo();
				appCmt.comment.comment = "";
				appCmt.idoc = "";
				appCmt.approverLvl = level.intValue();
				retList.add(appCmt);
				typeBegin = false;
			}
			List cmtsList = (List)getCommon().getApproverCommentMap().get(level);
			if ( cmtsList == null )
			{
				continue;
			}
			for ( int j = 0; j < cmtsList.size(); j++ )
			{
				SpecialBidInfo.ApproverComment appCmt = (SpecialBidInfo.ApproverComment)cmtsList.get(j);
				if ( typeBegin )
				{
					typeBegin = false;
					appCmt.approverName = (String)getCommon().getGroupMap().get(level);
				}
				else
				{
					appCmt.approverName = "";
				}
				appCmt.userEmail = appCmt.getComment().getAction();
				retList.add(appCmt);
			}
		}
		return retList;
	}
	   
    public List getReturnReasons() {
    	if(returnReasons != null){    		
    		Collections.sort(returnReasons, new Comparator(){
    			public int compare(Object a, Object b) {  
    				String one = ((CodeDescObj)a).getCode();  
    				String two = ((CodeDescObj)b).getCode();   
    				return one.compareTo(two);   
    			}  
    		});
    	}
		return returnReasons;
	}

	public boolean isPGSNewPAEnrolled() {
		if(header.isPAQuote()) {
			if (header.hasNewCustomer() && !header.isPGSNewPAEnrolled()) {
				return true;
			} else {
				return false;
			}				
		} else {
			return false;
		}
	}
	
	public boolean isSSP(){
		return header.isSSPQuote();
	}
	
	/**
     * Get the editable status of ship to / install at link
     * @return
     */
    public boolean isEditable(){
    	if(!isSameDistributor()){
    		if (!this.userIsAnyEditor()) return false;
    	} 
    	return this.getQuote().getQuoteHeader().isShipInstallEditable();
    }
    
    private boolean isSameDistributor(){
    	Partner payer = this.getQuote().getPayer();
    	if(null!=payer){
    		QuoteUserSession quoteUserSession = super.getQuoteUserSession();
    		if(StringUtils.equals(quoteUserSession.getSiteNumber(), payer.getCustNum())) {
    			return true;
    		}
    	}
    	return false;
    }
    
	private boolean userIsAnyEditor() {
		boolean isEditor = qtUserAccess == null ? false : qtUserAccess.isEditor();
		if(!isEditor) return false;
		
		boolean isSubmitter = false;
		if(StringUtils.equals(getQuoteUserSession().getAudienceCode(), QuoteConstants.APP_CODE_SQO)){
			isSubmitter = getUser().getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_SUBMITTER;
		} else {
			isSubmitter = true;
		}
		
		return isSubmitter;
	}
    
	public boolean isOnlySaaSParts(){
		return header.isOnlySaaSParts();
	}
	
	public boolean isChannelQuote(){
		return header.isChannelQuote();
	}
	
	public String getSubmittedQuoteStatus() {
		if (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.QUOTE_ON_HOLD)
				|| header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.READY_TO_ORDER)) {
			return "Active";
		} else if (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)) {
			return "OrderOnHold";
		} else {
			return "Inactive";
		}
	}

	public boolean showOmittedRenwalLine(){
		if (null != header && header.isOmittedLine()){
			return true;
		}
		return false;
	}
	
    public boolean haveDivestedPart(){
    	boolean haveDivestedPart = header.isHasDivestedPart();
    	return haveDivestedPart;
    }
    
	public boolean isDisplaySSPWithOutContract() {
		return isDisplaySSPWithOutContract;
	}
    
    public boolean isDisplayOrderForDivestedPartMsg(){ 
    	return isDisplayOrderForDivestedPartMsg;
    }
    public boolean isDispCp4PrcIncr4DivestdPartMsg(){ 
    	return isDispCp4PrcIncr4DivestdPartMsg;
    }
    public boolean isDispBidIterDivestdPartMsg(){ 
    	return isDispBidIterDivestdPartMsg;
    }
    
    

	public boolean isShowMTMSerial(){
    	if (!isEditable()) return false;
    	List lineItems = getSoftwareLineItems();
    	for (int i = 0; lineItems!=null&&i < lineItems.size(); i++) {
    		QuoteLineItem qli = (QuoteLineItem) lineItems.get(i);
    		if(partTable.isDisplayModelAndSerialNum(qli) ||partTable.showApplianceMtm(qli,!getQuote().getQuoteHeader().isSubmittedQuote(),getQuote().getQuoteHeader().isSalesQuote())||qli.isApplianceRelatedSoftware()){
                if ((!partTable.isDisplayModelAndSerialNum(qli) && partTable.showMachineType(qli)) || qli
                                .isApplianceRelatedSoftware()) {
                	if(1 != quote.getQuoteHeader().getRenwlQuoteSpeclBidFlag()){
        			return true;
                	}
        		}
        	}
    	}
    	
    	return false;
    }
	

    /**
     * @return Returns the showApproverDetails.
     */
    public boolean isShowApproverDetails() {
        return showApproverDetails;
    }
    
    public String getExpDateExtensionJustification(){
    	return expDateExtensionJustification;
    }
    public Boolean isExpDateExtensionDraftForSubmitterFlag(){
    	boolean allowDraftQuote=header.isExpDateExtendedFlag()&&(userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER)&&QuoteConstants.QUOTE_STAGE_CODE_CPEXDATE.equalsIgnoreCase(header.getQuoteStageCode());
    	return allowDraftQuote;
    }
    public Boolean isExpDateExtensionSubmittedForApproverFlag(){
    	boolean allowSubmittedForApprover=header.isExpDateExtendedFlag()&&(userAccess == QuoteConstants.ACCESS_LEVEL_APPROVER)&&(header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR)||header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS));
    	return allowSubmittedForApprover;
    }
    
    
    public Boolean isExpDateExtensionQtFlag(){
    	return header.isExpDateExtendedFlag();
    }
    
    public Boolean needPostExpDateExtension(){
    	return isExpDateExtensionDraftForSubmitterFlag()||isExpDateExtensionSubmittedForApproverFlag();
    }
    
    public long getStartQtExpDateMilliseconds(){
    	Date max=getMaxDate(header.getPriorQuoteExpDate(),new Date());
    	if(max!=null)
    		return max.getTime();
    	return 0;
    }
    
    public int getStartQtExpDateYear(){
    	Date max=getMaxDate(header.getPriorQuoteExpDate(),new Date());
    	if(max!=null){
    		
    		Calendar cal =  Calendar.getInstance();
    		cal.setTime(max);
    		return  cal.get(Calendar.YEAR);
    	}
    		
    	return 0;
    }
    public int getStartQtExpDateMonth(){
    	Date max=getMaxDate(header.getPriorQuoteExpDate(),new Date());
    	if(max!=null){
    		
    		Calendar cal =  Calendar.getInstance();
    		cal.setTime(max);
    		return  cal.get(Calendar.MONTH);
    	}
    		
    	return 0;
    }
    public int getStartQtExpDateDay(){
    	Date max=getMaxDate(header.getPriorQuoteExpDate(),new Date());
    	if(max!=null){
    		
    		Calendar cal =  Calendar.getInstance();
    		cal.setTime(max);
    		return  cal.get(Calendar.DAY_OF_MONTH);
    	}
    		
    	return 0;
    }
    private Date getMaxDate(Date first,Date second){
    	if(first==null||second==null)
    		return null;
    	
    	Calendar firstCal =  Calendar.getInstance();
    	firstCal.setTime(first);
    	Calendar secondCal =  Calendar.getInstance();
    	secondCal.setTime(second);
    	
    	if(first.before(second)){
    		return second;
    	}else{
    		return first;
    	}
    }
    public long getLastDayOfQuarterMilliseconds (){
    	if(header!=null&&header.getPriorQuoteExpDate()!=null)
    		return DateUtil.getLastDayOfQuarter(header.getPriorQuoteExpDate()).getTime();
    	
    	return 0;
    	
    }
    public int getLastDayOfQuarterYear (){
    	if(header!=null&&header.getPriorQuoteExpDate()!=null){
    		
    		Calendar cal =  Calendar.getInstance();
    		cal.setTime(DateUtil.getLastDayOfQuarter(header.getPriorQuoteExpDate()));
    		return  cal.get(Calendar.YEAR);
    	}
    	return 0;
    	
    }
    public int getLastDayOfQuarterMonth (){
    	if(header!=null&&header.getPriorQuoteExpDate()!=null){
    		
    		Calendar cal =  Calendar.getInstance();
    		cal.setTime(DateUtil.getLastDayOfQuarter(header.getPriorQuoteExpDate()));
    		return  cal.get(Calendar.MONTH);
    	}
    	return 0;
    	
    }
    public int getLastDayOfQuarterDay (){
    	if(header!=null&&header.getPriorQuoteExpDate()!=null){
    		
    		Calendar cal =  Calendar.getInstance();
    		cal.setTime(DateUtil.getLastDayOfQuarter(header.getPriorQuoteExpDate()));
    		return  cal.get(Calendar.DAY_OF_MONTH);
    	}
    	return 0;
    	
    }
    
    public Integer getOriginalQtExpDateYear (){
    	if(header!=null&&header.getPriorQuoteExpDate()!=null){
    		 
    		  Calendar expireDate = Calendar.getInstance();
             expireDate.setTime(header.getPriorQuoteExpDate());
             quoteExpYear = expireDate.get(Calendar.YEAR);
             return quoteExpYear;
    	}
    	return getCurrYear();
    	
    }
    
    public Collection generateExtensionDayOptions(int day) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        if (day == 0)
            collection.add(new SelectOptionImpl(sDay, "", true));
        else
            collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (day == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }
        return collection;
    }
    
    public Collection generateExtensionMonthOptions(int month) {
    	month++;
    	Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};

        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        if (month == 0)
            collection.add(new SelectOptionImpl(labelString, "", true));
        else
            collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (month == i)
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }
        Boolean webTerm = false;
    	return collection;

    }
    
    public Collection generateExtensionYearOptions(int year) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        boolean yearSelected = (year >= currYear && year <= (currYear + 5));
        int selectedYear = -1;
        for(int i = 0;i<6;i++){
        	if(year==currYear+i){
        		selectedYear = currYear+i;
        		break;	
        	}
        }
        collection.add(new SelectOptionImpl(sYear, "", !yearSelected));
        for(int i = 0;i<6;i++){
        	if(selectedYear == currYear+i){
        		collection.add(new SelectOptionImpl(String.valueOf(currYear+i), String.valueOf(currYear+i), true));
        	}else{
        		collection.add(new SelectOptionImpl(String.valueOf(currYear+i), String.valueOf(currYear+i), false));
        	}
        }
        return collection;
    }
    
    
    /**
     * @return Returns the isDispCp4PrcIncrButton.
     */
    public boolean isDispCp4ExpiryDateButton() {
        return isDispCp4ExpiryDateButton;
    }
    /**
     * @param isDispCp4PrcIncrButton The isDispCp4PrcIncrButton to set.
     */
    public void setDispCp4ExpiryDateButton(boolean isDispCp4ExpiryDateButton) {
        this.isDispCp4ExpiryDateButton = isDispCp4ExpiryDateButton;
    }
    
    /**
     * @return Returns the isExpDateExtendedFlag.
     */    
    public boolean  isExpDateExtendedFlag(){
    	return  isExpDateExtendedFlag;
    }
    /**
     * @param isExpDateExtendedFlag The isExpDateExtendedFlag to set.
     */
    public void setExpDateExtendedFlag(boolean  isExpDateExtendedFlag) {
        this. isExpDateExtendedFlag =  isExpDateExtendedFlag;
    }
    
    public boolean isExpiryDateOutsideCurrentQuarter(){
    	
    	Date quoteExpDate = header.getQuoteExpDate();
    	Date currentDate=new Date();
    	
    	Date lasetDay = DateUtil.getLastDayOfQuarter(currentDate);
    	Date firstDay = DateUtil.getFirstDayOfQuarter(currentDate);
     
    	if(quoteExpDate.after(lasetDay) || quoteExpDate.before(firstDay)){    		
    		return true;    		
    	}else    		
    	    return false;
    }
    
    
    public Collection generateExpDateExtensionDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);

        if (quoteExpDay == 0)
            collection.add(new SelectOptionImpl(sDay, "", true));
        else
            collection.add(new SelectOptionImpl(sDay, "", false));

        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (quoteExpDay == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0'), false));

        }
        return collection;
    }
    
    public Collection generateExpDateExtensionMonthOptions() {
    	Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};

        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        Date priorQuoteExpDate = header.getPriorQuoteExpDate();
    	 Calendar lasetDay = Calendar.getInstance();
    	 lasetDay.setTime(DateUtil.getLastDayOfQuarter(priorQuoteExpDate));
    	 int month = lasetDay.get(Calendar.MONTH)+1;
        if (month == 0)
            collection.add(new SelectOptionImpl(labelString, "", true));
        else
            collection.add(new SelectOptionImpl(labelString, "", false));
        
        for (int i = getStartQtExpDateMonth()+1; i <= month; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (month == i)
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, StringHelper.fillString(String.valueOf(i),2,'0'), false));
        }
        Boolean webTerm = false;
    	return collection;

    }
    public Collection generateExpDateExtensionYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);
        Date priorQuoteExpDate = header.getPriorQuoteExpDate();
	   	 Calendar cal = Calendar.getInstance();
	   	cal.setTime(priorQuoteExpDate);
	   	 int year = cal.get(Calendar.YEAR);
        
        collection.add(new SelectOptionImpl(sYear, "", true));
        collection.add(new SelectOptionImpl(String.valueOf(year), String.valueOf(year), true));
        return collection;
    }
    
    public String getPriorQuoteExpDate(){
    	 return DateHelper.getDateByFormat(header.getPriorQuoteExpDate(), "dd MMM yyyy");
    }
}
