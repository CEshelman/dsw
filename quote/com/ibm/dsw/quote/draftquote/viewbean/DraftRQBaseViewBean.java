package com.ibm.dsw.quote.draftquote.viewbean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
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
 * The <code>DraftRQBaseViewBean</code> class.
 *
 * @author gancm@cn.ibm.com
 *
 * Created on 2007-4-10
 */
public abstract class DraftRQBaseViewBean extends DisplayQuoteBaseViewBean {

    public static final String SPECL_BID_TAB_ID = "3";

    protected Quote renewalQuote = null;
    protected transient QuoteHeader renewalQuoteHeader = null;

    protected String renewalStatus = "";
    protected String secondaryStatus = "";
    protected String renewalQuoteNum = "";
    protected String originalWebReference = "";
    protected String maintainDate = "";

    protected boolean isDisplayConvertToSalesQuote = false;
    protected boolean isDisplaySubmitAsFinal = false;
    protected boolean isDisplayOrder = false;
    protected boolean isDisplayCtrctNotActiveMsg = false;
    protected boolean isDisplayPartsNotActiveOrNoPricedMsg = false;
    protected boolean isDisplaySBNotPermittedMsg = false;
    protected boolean isDisplayDldRichTextBtn = false;
    protected boolean isDisplayBluePageUnavailableMsg = false;
    protected boolean isDisplayCreateSpeclBidBtn = false;
    protected boolean isDisplayObsltPartMsg = false;
    protected boolean isDisplayNotSpeclBidableMsg = false;
    private boolean orignFromCustChgReqFlag = false;
    private String searchCriteriaUrlParam;
    private String requestorEMail;
    protected boolean isDisplayNotAllowOrderForCustBlockMsg = false; //not allow order as a blocked customer - sods2.customer.sales_ord_block = '01'
    private boolean isDisplayCntryNotAllowOrderMsg = false;
    private boolean isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg = false;
    private boolean isDisplayNotMatchBPsInChargeAgreementFlag = false;

	public void collectResults(Parameters params) throws ViewBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(params);

        renewalQuote = (Quote) params.getParameter(ParamKeys.PARAM_QUOTE_OBJECT);
        renewalQuoteHeader = renewalQuote.getQuoteHeader();
        isRSVPSRPOnly = renewalQuoteHeader.isRSVPSRPOnly();
        isDisplayRSVPSRPOnly = GrowthDelegationUtil.isLobEligibleForGrowthDelegation(renewalQuote);
        // Get quote header information
        renewalQuoteNum = renewalQuoteHeader.getRenwlQuoteNum();
        originalWebReference = renewalQuoteHeader.getPriorQuoteNum();
        orignFromCustChgReqFlag = QuoteConstants.QUOTE_STAGE_CODE_RQCHGREQ.equalsIgnoreCase(renewalQuoteHeader.getQuoteStageCode());

        // Get renewal quote end date
		Date rqEndDate = renewalQuoteHeader.getRenwlEndDate();
		if (rqEndDate != null) {
		    Locale locale = this.getLocale();
		    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", locale);
		    maintainDate = dateFormat.format(rqEndDate);
		}

        // Get quote primary status
        List pStatusList = renewalQuote.getAllWebPrimaryStatuses();
        for (Iterator iter = pStatusList.iterator(); iter.hasNext();) {
            QuoteStatus pStatus = (QuoteStatus) iter.next();
            if (renewalStatus.length() > 0) {
                renewalStatus += "<br />";
            }
            renewalStatus += pStatus.getStatusCodeDesc();
        }
        // Get quote secondary status
        List sStatusList = renewalQuote.getAllWebSecondayStatuses();
        for (Iterator iter=sStatusList.iterator(); iter.hasNext(); ) {
            QuoteStatus sStatus = (QuoteStatus) iter.next();
            if (secondaryStatus.length() > 0) {
                secondaryStatus += "<br />";
            }
            secondaryStatus += sStatus.getStatusCodeDesc();
        }

        try {
            // Determine whether display buttons or not
            QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();
            Map rules = qcProcess.getDraftQuoteActionButtonsRule(quoteUserSession, renewalQuote, getSalesRep());

            if (rules != null) {
                isDisplayConvertToSalesQuote = ((Boolean) rules
                        .get(QuoteCapabilityProcess.DISPLAY_CNVRT_TO_SALES_QT_BTN)).booleanValue();
                isDisplaySubmitAsFinal = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN))
                        .booleanValue();
                isDisplayOrder = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_ORDER_BTN)).booleanValue();
                isDisplayCtrctNotActiveMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CTRCT_NOT_ACTIVE_MSG))
                        .booleanValue();
                isDisplayPartsNotActiveOrNoPricedMsg = ((Boolean) rules
                        .get(QuoteCapabilityProcess.DISPLAY_PART_NO_RPC_MSG)).booleanValue();
                isDisplaySBNotPermittedMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SB_NOT_PERMITTED_MSG))
        				.booleanValue();
                isDisplayDldRichTextBtn = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN))
                        .booleanValue();
                isDisplayBluePageUnavailableMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_BLUE_PAGE_UNAVAILABLE_MSG))
                		.booleanValue();
                isDisplayCreateSpeclBidBtn = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CREATE_SPECL_BID_BTN))
                        .booleanValue();
                isDisplayObsltPartMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG))
                        .booleanValue();
                isDisplayNotSpeclBidableMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NOT_SPECL_BIDABLE_MSG))
                        .booleanValue();
                isDisplayNotAllowOrderForCustBlockMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG))
                		.booleanValue();
                isDisplayCntryNotAllowOrderMsg = ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG)).booleanValue();
                isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg =  rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG)!=null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG)).booleanValue():false;
                isDisplayNotMatchBPsInChargeAgreementFlag =  rules.get(QuoteCapabilityProcess.DISPLAY_NOT_MATCH_BPS_ON_CHARGE_AGREEMENT_FOR_CHNNL_QT_MSG)!=null?
                                ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NOT_MATCH_BPS_ON_CHARGE_AGREEMENT_FOR_CHNNL_QT_MSG)).booleanValue():false;
            }
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
        }

        searchCriteriaUrlParam = params.getParameterAsString(ParamKeys.PARAM_CUST_ACT_URL_PARAM);
        requestorEMail = params.getParameterAsString(ParamKeys.PARAM_CUST_ACT_REQUESTOR);
    }

    public boolean isDisplaySubmitAsFinal() {
        return isDisplaySubmitAsFinal;
    }

    public boolean isDisplayOrder() {
        return isDisplayOrder;
    }

    public boolean isDisplayCtrctNotActiveMsg() {
        return isDisplayCtrctNotActiveMsg;
    }

    public boolean isDisplaySBNotPermittedMsg() {
        return isDisplaySBNotPermittedMsg;
    }

    public boolean isDisplayDldRichTextBtn() {
        return isDisplayDldRichTextBtn;
    }

    public boolean isDisplayBluePageUnavailableMsg() {
        return isDisplayBluePageUnavailableMsg;
    }

    public boolean isDisplayInitMgrtnQuoteBtn() {
        return renewalQuoteHeader.isFCTQuote();
    }

    public boolean isDisplayObsltPartMsg() {
        return isDisplayObsltPartMsg;
    }

    public boolean isDisplayNotSpeclBidableMsg() {
        return isDisplayNotSpeclBidableMsg;
    }

    public String getOrigRenewQuoteDetailParams(){
        StringBuffer goBackURL = new StringBuffer();
        goBackURL.append(ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ActionHandlerKeys.JADE_ACTION_KEY)).append("=").append(getDisplayTabAction());

        String baseURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.RENEWAL_QUOTE_DETAIL_URL);
        StringBuffer origRenewQuoteDetailURL = new StringBuffer(baseURL);
        HtmlUtil.addURLParam(origRenewQuoteDetailURL, DraftRQParamKeys.PARAM_RPT_QUOTE_NUM, renewalQuoteNum);
        HtmlUtil.addURLParam(origRenewQuoteDetailURL, DraftRQParamKeys.PARAM_RPT_P1, goBackURL.toString());
        String btnParams = genBtnParams(this.getPostTabAction(), origRenewQuoteDetailURL.toString() , null);

        return btnParams;
    }

    public String getConvertToSalesQuoteBtnParams() {
        String btnParams = this.genBtnParamsForAction(this.getPostTabAction(),
                DraftQuoteActionKeys.CONVERT_TO_SQ, DraftQuoteParamKeys.PARAM_BTN_NAME + "=7,srcAction=" + getDisplayTabAction());
        return btnParams;
    }

    public String getCreateSpeclBidBtnParams() {
        String btnParams = this.genBtnParamsForAction(this.getPostTabAction(),
                DraftQuoteActionKeys.CONVERT_RQ_TO_SPECL_BID,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=7,srcAction=" + getDisplayTabAction());
        return btnParams;
    }

    public String getInitMigrationQuoteBtnParams() {
        String btnParams = this.genBtnParamsForAction(this.getPostTabAction(),
                DraftQuoteActionKeys.EXPORT_QUOTE, null);
        return btnParams;
    }

    public String getSubmitAsFinalBtnParams() {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.SUBMIT_DRAFT_RQ_AS_FINAL);
        String btnParams = this.genBtnParams(this.getPostTabAction(), redirectURL,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=5," + ParamKeys.PARAM_FORWARD_FLAG + "=true");
        return btnParams;
    }

    public String getComplChgReqBtnParams() {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.SUBMIT_DRAFT_RQ_AS_FINAL);
        String btnParams = this.genBtnParams(this.getPostTabAction(), redirectURL,
                 ParamKeys.PARAM_FORWARD_FLAG + "=true," + ParamKeys.PARAM_CUST_ACT_URL_PARAM + "=" + this.getSearchCriteriaUrlParam()
                 + "," + ParamKeys.PARAM_CUST_ACT_REQUESTOR + "=" + this.getRequestorEMail());
        return btnParams;
    }

    public String getSubmitForApprovalBtnParams() {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.SUBMIT_DRAFT_RQ_FOR_APPROVAL);
        String submitForApprovalBtnParams = this.genBtnParams(this.getPostTabAction(), redirectURL,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=4," + ParamKeys.PARAM_FORWARD_FLAG + "=true");
        return submitForApprovalBtnParams;
    }

    public String viewDetailedRenewalQuote() { 
        String dldBtnParams = genBtnParamsForAction(this.getPostTabAction(), null,
        		DraftQuoteParamKeys.PARAM_BTN_NAME + "=11," +  ParamKeys.PARAM_IS_SBMT_QT + "=false");
        return dldBtnParams;
    }
    
    public String getDldRichTextBtnParams() {
        String dldBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                DraftQuoteActionKeys.DRAFT_QUOTE_RTF_DOWNLOAD,
                ParamKeys.PARAM_IS_SBMT_QT + "=false");
        return dldBtnParams;
    }

    public String getOrderBtnParams() {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.ORDER_DRAFT_QUOTE);
        String btnParams = this.genBtnParams(this.getPostTabAction(), redirectURL,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=6," + ParamKeys.PARAM_FORWARD_FLAG + "=true");
        return btnParams;
    }

    public boolean isDisplayConvertToSalesQuote() {
        return isDisplayConvertToSalesQuote;
    }

    public boolean isDisplayCreateSpeclBidBtn() {
        return isDisplayCreateSpeclBidBtn;
    }

    public boolean isDisplayPartsNotActiveOrNoPricedMsg() {
        return isDisplayPartsNotActiveOrNoPricedMsg;
    }

    public String getMaintainDate() {
        return notNullString(maintainDate);
    }

    public Quote getRenewalQuote() {
        return renewalQuote;
    }

    public String getRenewalQuoteNum() {
        return notNullString(renewalQuoteNum);
    }

    public String getRenewalStatus() {
        return notNullString(renewalStatus);
    }

    public String getSecondaryStatus() {
        return notNullString(secondaryStatus);
    }

    public QuoteHeader getRenewalQuoteHeader() {
        return renewalQuoteHeader;
    }

    public String notNullString(String string){
        return StringUtils.trimToEmpty(string);
    }

    public String getGrapTabHiddenIDs() {
        return SPECL_BID_TAB_ID;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("isEmpty = ").append(isEmpty).append("\n");
        buffer.append("isDisplaySubmitAsFinal = ").append(isDisplaySubmitAsFinal).append("\n");
        buffer.append("isDisplayOrder = ").append(isDisplayOrder).append("\n");
        buffer.append("isDisplayCtrctNotActiveMsg = ").append(isDisplayCtrctNotActiveMsg).append("\n");
        buffer.append("isDisplayDldRichTextBtn = ").append(isDisplayDldRichTextBtn).append("\n");
        buffer.append("lob = ").append(lob).append("\n");
        buffer.append("isPA = ").append(isPA()).append("\n");
        buffer.append("isPAE = ").append(isPAE()).append("\n");

        return buffer.toString();
    }

    /**
     * @return Returns the isRelabelSubmitBtn.
     */
    public boolean isOrignFromCustChgReqFlag() {
        return orignFromCustChgReqFlag;
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
        return requestorEMail;
    }
    /**
     * @param requestorEMail The requestorEMail to set.
     */
    public void setRequestorEMail(String requestorEMail) {
        this.requestorEMail = requestorEMail;
    }


	public boolean isDisplayNotAllowOrderForCustBlockMsg() {
		return isDisplayNotAllowOrderForCustBlockMsg;
	}

	public void setDisplayNotAllowOrderForCustBlockMsg(
			boolean isDisplayNotAllowOrderForCustBlockMsg) {
		this.isDisplayNotAllowOrderForCustBlockMsg = isDisplayNotAllowOrderForCustBlockMsg;
	}

	 public boolean isDisplayCntryNotAllowOrderMsg() {
        return isDisplayCntryNotAllowOrderMsg;
    }

	public boolean isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg() {
		return isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg;
	}

	public void setDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg(
			boolean isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg) {
		this.isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg = isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg;
	}

	public boolean isDisplayNotMatchBPsInChargeAgreementFlag() {
		return isDisplayNotMatchBPsInChargeAgreementFlag;
	}

	public void setDisplayNotMatchBPsInChargeAgreementFlag(
			boolean isDisplayNotMatchBPsInChargeAgreementFlag) {
		this.isDisplayNotMatchBPsInChargeAgreementFlag = isDisplayNotMatchBPsInChargeAgreementFlag;
	}

}
