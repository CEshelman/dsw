package com.ibm.dsw.quote.submittedquote.viewbean;

import is.domainx.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.jdbc.ChannnelOverridenReasonCodeFactory_jdbc;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.domain.PriorComments;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SpecialBidCommon;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SubmittedQuoteSpecialBidTabViewBean</code> class .
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: May 8, 2007
 */
public class SubmittedQuoteSpecialBidTabViewBean extends SubmittedQuoteBaseViewBean {

    private static final long serialVersionUID = 3130368575242350603L;

	private String addReviewerURL = null;

    private String removeFileURL = null;

    private boolean editableFlag = false;

    private boolean pendingAppGrpFlag = false;

    private boolean adminFlag = false;

    private transient List tncHistory = new ArrayList();
    
    private transient List summaryHistory = new ArrayList();
    
    private transient PriorComments priorCmts = null;
    
    private List txtList = new ArrayList();
    
    private static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        priorCmts = (PriorComments)params.getParameter(SpecialBidParamKeys.PARAM_PRIOR_COMMENTS);
        txtList = (List)params.getParameter(SpecialBidParamKeys.PARAM_QUOTE_TXT_HISTORY);
        isSPBidTab = true;
        
//        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
//        String secondActionKey = appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);
//        String action2 = SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB;
        StringBuffer reviewerUrl = new StringBuffer("");
        
        reviewerUrl.append(HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SUBMITTEDQT_ADD_REVIEWER));
        
        //HtmlUtil.addURLParam(reviewerUrl, secondActionKey, action2);
        //HtmlUtil.addURLParam(approverUrl, secondActionKey, action2);
        addReviewerURL = reviewerUrl.toString();
        
        //addReviewerURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SUBMITTEDQT_ADD_REVIEWER);
        //updateApproverURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.SUBMITTEDQT_UPDATE_APPROVER_SELECTION);
        
        removeFileURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.REMOVE_SUBMITTED_QUOTE_ATTACHMENT);
        // If the editable version of this screen will display
        User user = (User) params.getParameter(ParamKeys.PARAM_USER_OBJECT);
        editableFlag = (user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER)
                && this.quote.getQuoteUserAccess().isAnyAppTypMember();

        //The current user is in an approver group assigned to the bid,
        //and that group?s approval is the next one pending
        pendingAppGrpFlag = editableFlag && this.quote.getQuoteUserAccess().isPendingAppTypMember();
        //or the current user is an administrator of the bid?s geo
        List adminList = (List) params.getParameter(ParamKeys.PARAM_ADMIN_LIST_OBJECT);
        if (adminList != null) {
            Iterator iterator = adminList.iterator();
            while (iterator.hasNext()) {
                String admin = (String) iterator.next();
                if (admin != null && admin.equalsIgnoreCase(user.getEmail())) {
                    adminFlag = true;
                    break;
                }
            }
        }

        List quoteTxtHistory = (List)params.getParameter(SpecialBidParamKeys.PARAM_QUOTE_TXT_HISTORY);
        initHistoryData(quoteTxtHistory);
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        logContext.debug(this, "\nuser's priviledges to special bid approval :\nedit flag = " + editableFlag
                + "\npending group flag = " + pendingAppGrpFlag + "\nadmin flag = " + adminFlag);
    }
    
    public boolean isDoSpecialBidCommonInit()
    {
        return true;
    }
    
    private void initHistoryData(List list)
    {
        if ( list == null )
        {
            return;
        }
        for ( int i = 0; i < list.size(); i++ )
        {
            QuoteTxt quoteTxt = (QuoteTxt)list.get(i);
            if ( quoteTxt.getQuoteTextTypeCode().equals(SpecialBidInfo.CommentInfo.CREDIT_J) )
            {
                this.tncHistory.add(quoteTxt);
            }
            else
            {
                this.summaryHistory.add(quoteTxt);
            }
        }
    }
    
    public boolean getDisplayUpdateApprFlag()
    {
        return true;
    }
    
    public String getRemoveFileURL() {
        return removeFileURL;
    }
    
    public String getDisplayTabAction() {
        return SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB;
    }

    public boolean getEditableFlag() {
        return editableFlag;
    }

    public String getAddReviewerURL() {
        return addReviewerURL;
    }
    
    public List getTxtList(){
        Collections.sort(txtList, new  Comparator(){
            public int compare(Object o1, Object o2) {
                QuoteTxt item1 = (QuoteTxt) o1;
                QuoteTxt item2 = (QuoteTxt) o2;
                return item2.getQuoteTextId() - item1.getQuoteTextId();
            }
        });
    	return txtList;
    }

    /**
     * @return Returns the adminFlag.
     */
    public boolean isAdminFlag() {
        return adminFlag;
    }

    /**
     * @return Returns the pendingAppGrpFlag.
     */
    public boolean isPendingAppGrpFlag() {
        return pendingAppGrpFlag;
    }

    public Map getActionText() {
        return actionText;
    }
    
    protected SpecialBidCommon getSpecialBidCommon()
    {
    	return common;
    }
    
    /**
     * @return Returns the summaryHistory.
     */
    public List getSummaryHistory() {
        return summaryHistory;
    }
    /**
     * @return Returns the tncHistory.
     */
    public List getTncHistory() {
        return tncHistory;
    }
    
    public String getFinanceCost()
    {
        try
        {
        	Double financeRate = quote.getSpecialBidInfo().getFinanceRate();
        	Double progRBD = quote.getSpecialBidInfo().getProgRBD();
        	Double incrRBD = quote.getSpecialBidInfo().getIncrRBD();
        	Double d2 = null;
        	if (progRBD == null && incrRBD==null && financeRate !=null){
        		d2 = financeRate;
        	}else if (progRBD != null && incrRBD != null){
        		d2 = progRBD + incrRBD;
        	}
            
            if ( d2 == null )
            {
                d2 = new Double(0.0);
            }
            double d = d2.doubleValue();
            double total = quote.getQuoteHeader().getQuotePriceTot();
            double ret = d * total / 100;
            return DecimalUtil.format(ret);
        }
        catch ( Exception e )
        {
        	logContext.error(this, e);
        }
        return "";
    }
    
    public String getUpdateCataAction() {
//        String action2 = this.getDisplayTabAction();
        String[] keys = { ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
//        return this.getActionURL(SubmittedQuoteActionKeys.SUBMIT_APPROVER_ACTION, action2, keys, values);
        return this.getActionURL(SubmittedQuoteActionKeys.UPDATE_CATEGORY, null, keys, values);
    }
    
    public String getUpdateSalesPlayNumAction()
    {
    	String[] keys = { ParamKeys.PARAM_QUOTE_NUM };
        String[] values = { this.getWebQuoteNum() };
        return this.getActionURL(SubmittedQuoteActionKeys.UPDATE_SALES_PLAY_NUM, null, keys, values);
    }
      
    public boolean isDisplayCompCoverNote()
    {
        return common.spBidCondition.isCmprssCvrageQuote();
    }
    
    public boolean isBidIterEditJust()
    {
        return this.isBidIteratnQtFlag() && !this.getQuote().getQuoteHeader().isSubmittedQuote();
    }
    
    public String getSubmitAsFinalBtnParams() {
        if ( !this.isBidIteratnQtFlag() )
        {
            return super.getSubmitAsFinalBtnParams();
        }
        String[] keys = { DraftQuoteParamKeys.PARAM_BTN_NAME, ParamKeys.PARAM_FORWARD_FLAG };
        String[] values = { "1", "true" };
        submitAsFinalBtnParams = getActionURL(SubmittedQuoteActionKeys.POST_DRAFTQT_SPECIAL_BID_TAB, DraftQuoteActionKeys.SUBMIT_DRAFT_SQ_AS_FINAL, keys, values);
        
        return submitAsFinalBtnParams; 
    }
    
    public String getUpdateApprDisplayAction()
    {
        return SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB;
    }
    public PriorComments getPriorCmts() {
        return priorCmts;
    }
    public String getPriorQuoteLink(String webQuoteNum)
    {
        return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB) + "&amp;quoteNum=" + webQuoteNum;
    }
    
    public String getViewTxtHistoryLink(String txtTypeCode)
    {
    	return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.VIEW_TXT_HISTORY) + "&amp;quoteNum=" + webQuoteNum + "&amp;" 
    		+ SpecialBidParamKeys.PARAM_QUOTE_TXT_TYPE + "=" + txtTypeCode;
    }
    
    public boolean isDisplayChannelOverrideDiscountReason(){
    	Quote quote=getQuote();   	
    	return quote.getQuoteHeader().isChannelQuote() && QuoteCommonUtil.isChannelOverrideDiscount(quote);
    }
    
    public String getChannelOverrideDiscountReason() throws TopazException{
    	String val = getQuote().getSpecialBidInfo().getChannelOverrideDiscountReasonCode();
    	String reason="N/A";
    	if (StringUtils.isNotBlank(val)) {
    		reason=ChannnelOverridenReasonCodeFactory_jdbc.getInstance().findReasonCodeByCode(val).getCodeDesc()== null ? "":
    			ChannnelOverridenReasonCodeFactory_jdbc.getInstance().findReasonCodeByCode(val).getCodeDesc();
    	}
    	
    	return reason;
    }
}
