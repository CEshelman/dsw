package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.mail.config.MailActionKeys;
import com.ibm.dsw.quote.mail.config.MailParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
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
 * This <code>DraftSQBaseViewBean<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-3-9
 */

public abstract class DraftSQBaseViewBean extends DisplayQuoteBaseViewBean {

    protected boolean isDisplaySubmitForApproval = false;
    protected boolean isDisplaySaveQuoteAsDraft = false;
    protected boolean isDisplayExportQuoteAsSpreadsheet = false;
    protected boolean isDisplayEmailDraftQuote = false;
    protected boolean isDisplaySubmitAsFinal = false;
    protected boolean isDisplayOrder = false;
    protected boolean isDisplayNoCustOrPartMsg = false;
    protected boolean isDisplayCtrctNotActiveMsg = false;
    protected boolean isDisplayPartNoPricingMsg = false;
    protected boolean isDisplayChannelSQOrderMsg = false;
    protected boolean isDisplayNewCopyButton = false;
    protected boolean isDisplaySBNotPermittedMsg = false;
    protected boolean isDisplayCnvtToPAEBtn = false;
    protected boolean isDisplayDldRichTextBtn = false;
    protected boolean isDisplayObsltPartMsg = false;
    protected boolean isDisplayNonPACustMsg = false;
    protected boolean isDisplayEffDateInFtrMsg = false;
    protected boolean isDisplayCntryNotAllowOrderMsg = false;
    protected boolean isDisplayOEMNotAllowOrderMsg = false;
    protected boolean isDisplayPymntTerm = false;
    protected boolean isPymntDaysRadioChecked = false;
    protected boolean isDisplayOrderForDivestedPartMsg = false;
    protected boolean isDisplayAssgnCrtAgrmntNoRefreshedMsg = false;

    //To accept the Quote in the Quote Actions Section and added by Shawn 
    protected boolean isDisplayAccept = false;
    
    protected int maxExpirationDays= 0;
    protected String pymntTermsDays = "";

    protected String saveQuoteAsDraftBtnParams = "";
    protected String exportAsSpreadsheetBtnParams = "";
    protected String emailDraftQuoteBtnParams = "";
    protected String submitForApprovalBtnParams = "";

    protected String orderBtnParams = "";
    protected String saveAsNewDraftQuoteParams="";
    protected String convertToPAEBtnParams="";
    private String submitAsFinalBtnParams = "";
    private boolean isDisplayConv2StdCp = false;
    private boolean isDisplayNotAllowOrderForCustBlockMsg = false; //not allow order as a blocked customer - sods2.customer.sales_ord_block = '01'
    private boolean isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg = false;
    private boolean isDisplayNotMatchBPsOnChargeAgreementFlag = false;
    private boolean hasSaaSLineItem = false;
    protected boolean isPGSSubmitter = false;
    private boolean isDisplayOrdWithoutOpptNumMsg = false;
    private boolean isDisplayFct2PaQuoteTerminationMsg = false;
    private boolean isDisplayCannotSubmitWithSoftpartInPGSMsg = false;
    private boolean isDisplaySoftwarePartsWithoutAppIncIdMsg=false;
    private boolean isDisplayEvalOptSection = false;
    private boolean isSSPAddTrdQuote = false;
	private boolean priceLevelNotSet = false;

	public boolean isPriceLevelNotSet() {
		return priceLevelNotSet;
	}

	public void setPriceLevelNotSet(boolean priceLevelNotSet) {
		this.priceLevelNotSet = priceLevelNotSet;
	}

	public boolean isSSPAddTrdQuote() {
		return isSSPAddTrdQuote;
	}

	public void setSSPAddTrdQuote(boolean isSSPAddTrdQuote) {
		this.isSSPAddTrdQuote = isSSPAddTrdQuote;
	}

	public boolean isDisplayOrdWithoutOpptNumMsg() {
		return isDisplayOrdWithoutOpptNumMsg;
	}
	
	public boolean isDisplaySoftwarePartsWithoutAppIncIdMsg(){
		return isDisplaySoftwarePartsWithoutAppIncIdMsg;
	}
	
	public void collectResults(Parameters params) throws ViewBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(params);
        maxExpirationDays = header.getQuoteExpDays();
        hasSaaSLineItem = header.hasSaaSLineItem();
        originalWebReference = header.getPriorQuoteNum();
        if(header.getPymTermsDays() > DraftQuoteConstants.PYMNT_TERMS_STAND_DAYS) {
            isPymntDaysRadioChecked = true;
            pymntTermsDays = String.valueOf(header.getPymTermsDays());
        }
        
        if(header.isSSPQuote() && header.isAddTrd()){
    		this.isSSPAddTrdQuote = true;
        }

        try {
            // Determine whether display buttons or not
            QuoteCapabilityProcess qcProcess = QuoteCapabilityProcessFactory.singleton().create();
            Map rules = qcProcess.getDraftQuoteActionButtonsRule(quoteUserSession, quote, getSalesRep());
            if (rules != null) {
                isDisplaySaveQuoteAsDraft = rules.get(QuoteCapabilityProcess.DISPLAY_SAVE_QT_AS_DRAFT_BTN) != null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SAVE_QT_AS_DRAFT_BTN)).booleanValue():false;
                isDisplayNewCopyButton = rules.get(QuoteCapabilityProcess.DISPLAY_NEW_COPY_BTN)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NEW_COPY_BTN)).booleanValue():false;
                isDisplayExportQuoteAsSpreadsheet = rules.get(QuoteCapabilityProcess.DISPLAY_EXPORT_QT_AS_SPREADSHEET_BTN) != null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EXPORT_QT_AS_SPREADSHEET_BTN)).booleanValue():false;
                isDisplayEmailDraftQuote = rules.get(QuoteCapabilityProcess.DISPLAY_EMAIL_DRAFT_QUOTE_BTN)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EMAIL_DRAFT_QUOTE_BTN)).booleanValue():false;
                isDisplaySubmitForApproval = rules.get(QuoteCapabilityProcess.DISPLAY_SUBMIT_FOR_APPR_BTN)!= null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SUBMIT_FOR_APPR_BTN)).booleanValue():false;
                isDisplaySubmitAsFinal = rules.get(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN)).booleanValue():false;
                isDisplayOrder = rules.get(QuoteCapabilityProcess.DISPLAY_ORDER_BTN)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_ORDER_BTN)).booleanValue():false;
                isDisplayNoCustOrPartMsg = rules.get(QuoteCapabilityProcess.DISPLAY_NO_CUST_OR_PART_MSG)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NO_CUST_OR_PART_MSG)).booleanValue():false;
                isDisplayCtrctNotActiveMsg = rules.get(QuoteCapabilityProcess.DISPLAY_CTRCT_NOT_ACTIVE_MSG)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CTRCT_NOT_ACTIVE_MSG)).booleanValue():false;
                isDisplayPartNoPricingMsg = rules.get(QuoteCapabilityProcess.DISPLAY_PART_NO_RPC_MSG)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_PART_NO_RPC_MSG)).booleanValue():false;
                isDisplayChannelSQOrderMsg = rules.get(QuoteCapabilityProcess.DISPLAY_CHANNEL_QT_ORDER_MSG)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CHANNEL_QT_ORDER_MSG)).booleanValue():false;
                isDisplaySBNotPermittedMsg = rules.get(QuoteCapabilityProcess.DISPLAY_SB_NOT_PERMITTED_MSG)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_SB_NOT_PERMITTED_MSG)).booleanValue():false;
                isDisplayCnvtToPAEBtn = rules.get(QuoteCapabilityProcess.DISPLAY_CNVT_TO_PAE_BTN)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CNVT_TO_PAE_BTN)).booleanValue():false;
                isDisplayDldRichTextBtn = rules.get(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN) !=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN)).booleanValue():false;
                isDisplayObsltPartMsg = rules.get(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG)).booleanValue():false;
                isDisplayNonPACustMsg = rules.get(QuoteCapabilityProcess.DISPLAY_NON_PA_CUST_MSG) != null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NON_PA_CUST_MSG)).booleanValue():false;
                isDisplayEffDateInFtrMsg = rules.get(QuoteCapabilityProcess.DISPLAY_EFF_DATE_IN_FTR_MSG) != null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EFF_DATE_IN_FTR_MSG)).booleanValue():false;
                isDisplayCntryNotAllowOrderMsg = rules.get(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG)!= null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG)).booleanValue():false;
                isDisplayOEMNotAllowOrderMsg = rules.get(QuoteCapabilityProcess.DISPLAY_OEM_NOT_ALLOW_ORDER_MSG)!=null?
                                              ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_OEM_NOT_ALLOW_ORDER_MSG)).booleanValue():false;

                isDisplayConv2StdCp = rules.get(QuoteCapabilityProcess.DISPLAY_CONV_TO_STD_COPY_BTN)!=null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CONV_TO_STD_COPY_BTN)).booleanValue():false;


                isDisplayNotAllowOrderForCustBlockMsg = rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG)!=null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG)).booleanValue():isDisplayNotAllowOrderForCustBlockMsg;
                isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg =  rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG)!=null?
                        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG)).booleanValue():false;

                isPGSSubmitter = rules.get(QuoteCapabilityProcess.DISPLAY_PGS_SUBMITTER)!=null?
	                    ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_PGS_SUBMITTER)).booleanValue():false;

                setDisplayNotMatchBPsOnChargeAgreementFlag(rules.get(QuoteCapabilityProcess.DISPLAY_NOT_MATCH_BPS_ON_CHARGE_AGREEMENT_FOR_CHNNL_QT_MSG)!=null?
                                ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_NOT_MATCH_BPS_ON_CHARGE_AGREEMENT_FOR_CHNNL_QT_MSG)).booleanValue():false);

	            isDisplayOrdWithoutOpptNumMsg = (rules.get(QuoteCapabilityProcess.DISPLAY_ORD_WITHOUT_OPPTNUM_MSG))!=null?
		            	((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_ORD_WITHOUT_OPPTNUM_MSG)).booleanValue():false;
		            	
		        isDisplayFct2PaQuoteTerminationMsg = (rules.get(QuoteCapabilityProcess.DISPLAY_FCTTOPA_QUOTE_TERMINATION_MSG))!=null?
				        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_FCTTOPA_QUOTE_TERMINATION_MSG)).booleanValue():false;

		        isDisplayCannotSubmitWithSoftpartInPGSMsg = (rules.get(QuoteCapabilityProcess.DISPLAY_CANNOT_SUBMIT_WITH_SOFTPART_IN_PGS_MSG))!=null?
					        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_CANNOT_SUBMIT_WITH_SOFTPART_IN_PGS_MSG)).booleanValue():false;
				
			    isDisplayEvalOptSection = (rules.get(QuoteCapabilityProcess.DISPLAY_EVALUATOR_OPTIONS_SECTION))!=null?
				        ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_EVALUATOR_OPTIONS_SECTION)).booleanValue():false;
			
				//added by Shawn
				isDisplayAccept = rules.get(QuoteCapabilityProcess.DISPLAY_ACCEPT_QUOTE) != null?
				            ((Boolean) rules.get(QuoteCapabilityProcess.DISPLAY_ACCEPT_QUOTE)).booleanValue():false;
				            
	            isDisplaySoftwarePartsWithoutAppIncIdMsg=rules.get(QuoteCapabilityProcess.SOFTWARE_PART_WITHOUT_APPINC_ID)!=null?
	            		(Boolean)rules.get(QuoteCapabilityProcess.SOFTWARE_PART_WITHOUT_APPINC_ID):false;
	            		
	            //added by Ma Chao
        		isDisplayOrderForDivestedPartMsg = rules.get(QuoteCapabilityProcess.NOT_ALLOW_ORDER_FOR_DIVESTED_PART)!=null?
             		 	(Boolean)rules.get(QuoteCapabilityProcess.NOT_ALLOW_ORDER_FOR_DIVESTED_PART):false;
				priceLevelNotSet = rules.get(QuoteCapabilityProcess.PRICE_LEVEL_NOT_SET) != null ? (Boolean) rules
						.get(QuoteCapabilityProcess.PRICE_LEVEL_NOT_SET) : false;

            }

        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
        }
    }

    public boolean isDisplayEmailDraftQuote() {
        return isDisplayEmailDraftQuote;
    }

    public boolean isDisplayExportQuoteAsSpreadsheet() {
        return isDisplayExportQuoteAsSpreadsheet;
    }

    public boolean isDisplaySaveQuoteAsDraft() {
        return isDisplaySaveQuoteAsDraft && !header.isLockedFlag();
    }

    public boolean isDisplaySubmitForApproval() {
        return isDisplaySubmitForApproval;
    }

    public boolean isDisplaySubmitAsFinal() {
        return isDisplaySubmitAsFinal;
    }

    public boolean isDisplayOrder() {
        return isDisplayOrder;
    }

    public boolean isDisplayAccept() {
        return isDisplayAccept;
    }
    
    public boolean isDisplayNewCopyButton() {
        return isDisplayNewCopyButton;
    }

    public int getMaxExpirationDays() {
        return maxExpirationDays;
    }

    public boolean isDisplayCtrctNotActiveMsg() {
        return isDisplayCtrctNotActiveMsg;
    }

    public boolean isDisplayNoCustOrPartMsg() {
        return isDisplayNoCustOrPartMsg;
    }

    public boolean isDisplayPartNoPricingMsg() {
        return isDisplayPartNoPricingMsg;
    }

    public boolean isDisplayChannelSQOrderMsg() {
        return isDisplayChannelSQOrderMsg;
    }

    public boolean isDisplaySBNotPermittedMsg() {
        return isDisplaySBNotPermittedMsg;
    }

    public boolean isDisplayCnvtToPAEBtn() {
        return isDisplayCnvtToPAEBtn;
    }

    public boolean isDisplayDldRichTextBtn() {
        return isDisplayDldRichTextBtn;
    }

    public boolean isDisplayObsltPartMsg() {
        return isDisplayObsltPartMsg;
    }

    public boolean isDisplayNonPACustMsg() {
        return isDisplayNonPACustMsg;
    }

    public boolean isDisplayEffDateInFtrMsg() {
        return isDisplayEffDateInFtrMsg;
    }

    public String getSaveQuoteAsDraftBtnParams() {
        String redirectURL = HtmlUtil.getURLForAction(this.getDisplayTabAction());
        saveQuoteAsDraftBtnParams = genBtnParams(this.getPostTabAction(), redirectURL,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=1," + ParamKeys.PARAM_FORWARD_FLAG + "=true");
        return saveQuoteAsDraftBtnParams;
    }

    public String getExportAsSpreadsheetBtnParams() {
        exportAsSpreadsheetBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                DraftQuoteActionKeys.EXPORT_QUOTE ,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=2");
        return exportAsSpreadsheetBtnParams;
    }

    public String getExportAsNativeExcelBtnParams() {
        exportAsSpreadsheetBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                DraftQuoteActionKeys.EXPORT_QUOTE_NATIVE_EXCEL ,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=9");
        return exportAsSpreadsheetBtnParams;
    }

    public String getEmailDraftQuoteBtnParams() {
        emailDraftQuoteBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                MailActionKeys.DISPLAY_SEND_MAIL,
                MailParamKeys.PARAM_SRC_ACTION + "=" + this.getDisplayTabAction() + ","
                + DraftQuoteParamKeys.PARAM_BTN_NAME + "=3");
        return emailDraftQuoteBtnParams;
    }

    public String getSubmitForApprovalBtnParams() {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.SUBMIT_DRAFT_SQ_FOR_APPROVAL);
        submitForApprovalBtnParams = genBtnParams(this.getPostTabAction(), redirectURL,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=4," + ParamKeys.PARAM_FORWARD_FLAG + "=true," 
                + ParamKeys.PARAM_CURRENT_TAB_DISPLAY_ACTION + "=" + this.getDisplayTabAction());
        return submitForApprovalBtnParams;
    }

    public String getOrderBtnParams() {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.ORDER_DRAFT_QUOTE);
        String btnParams = this.genBtnParams(this.getPostTabAction(), redirectURL,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=6," + ParamKeys.PARAM_FORWARD_FLAG + "=true");
        return btnParams;
    }

    //for "save as new draft quote"
    public String getSaveAsNewDraftQuoteParams(){
        String redirectURL = HtmlUtil.getURLForAction(this.getDisplayTabAction());
        saveAsNewDraftQuoteParams = genBtnParams(this.getPostTabAction(),redirectURL,
                DraftQuoteParamKeys.PARAM_BTN_NAME+"=8," + ParamKeys.PARAM_FORWARD_FLAG + "=true");
        return saveAsNewDraftQuoteParams;
    }

    /**
     * @return Returns the convertToPAEBtnParams.
     */
    public String getConvertToPAEBtnParams() {
        convertToPAEBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                DraftQuoteActionKeys.CONVERT_QUOTE,
                MailParamKeys.PARAM_SRC_ACTION + "=" + this.getDisplayTabAction());
        return convertToPAEBtnParams;
    }

     /**
     * @return Returns the downloadRichTextBtnParams.
     */
    public String getDldRichTextBtnParams() {
        String dldBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                DraftQuoteActionKeys.DRAFT_QUOTE_RTF_DOWNLOAD,
                ParamKeys.PARAM_IS_SBMT_QT + "=false");
        return dldBtnParams;
    }

    public boolean isDisplayAcquisition() {
        return (header.isFCTQuote() || header.isMigration());
    }

    public boolean isDisplayOrigSAPRnwlQuoteNum() {
        return (header.isMigration() && StringUtils.isNotBlank(header.getRenwlQuoteNum()));
    }

    public String getOrigSAPRnwlQuoteNum() {
        return header.getRenwlQuoteNum();
    }

    public boolean isDisplayCntryNotAllowOrderMsg() {
        return isDisplayCntryNotAllowOrderMsg;
    }

    public boolean isDisplayOEMNotAllowOrderMsg() {
        return isDisplayOEMNotAllowOrderMsg;
    }

    public String getOrigRenewQuoteDetailUrl(){
        StringBuffer goBackURL = new StringBuffer();
        goBackURL.append(ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ActionHandlerKeys.JADE_ACTION_KEY)).append("=").append(getDisplayTabAction());

        String baseURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.RENEWAL_QUOTE_DETAIL_URL);
        StringBuffer origRenewQuoteDetailURL = new StringBuffer(baseURL);
        HtmlUtil.addURLParam(origRenewQuoteDetailURL, DraftRQParamKeys.PARAM_RPT_QUOTE_NUM, header.getRenwlQuoteNum());
        HtmlUtil.addURLParam(origRenewQuoteDetailURL, DraftRQParamKeys.PARAM_RPT_P1, goBackURL.toString());
        return origRenewQuoteDetailURL.toString();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("quoteExpDay = ").append(quoteExpDay).append("\n");
        buffer.append("quoteExpMonth = ").append(quoteExpMonth).append("\n");
        buffer.append("quoteExpYear = ").append(quoteExpYear).append("\n");
        buffer.append("isEmpty = ").append(isEmpty).append("\n");
        buffer.append("isDisplaySubmitForApproval = ").append(isDisplaySubmitForApproval).append("\n");
        buffer.append("isDisplaySaveQuoteAsDraft = ").append(isDisplaySaveQuoteAsDraft).append("\n");
        buffer.append("isDisplayExportQuoteAsSpreadsheet = ").append(isDisplayExportQuoteAsSpreadsheet).append("\n");
        buffer.append("isDisplayEmailDraftQuote = ").append(isDisplayEmailDraftQuote).append("\n");
        buffer.append("isDisplaySubmitAsFinal = ").append(isDisplaySubmitAsFinal).append("\n");
        buffer.append("isDisplayOrder = ").append(isDisplayOrder).append("\n");
        buffer.append("isDisplayNoCustOrPartMsg = ").append(isDisplayNoCustOrPartMsg).append("\n");
        buffer.append("isDisplayCtrctNotActiveMsg = ").append(isDisplayCtrctNotActiveMsg).append("\n");
        buffer.append("isDisplayPartNoPricingMsg = ").append(isDisplayPartNoPricingMsg).append("\n");
        buffer.append("isDisplayChannelSQOrderMsg = ").append(isDisplayChannelSQOrderMsg).append("\n");
        buffer.append("isDisplayCnvtToPAEBtn = ").append(isDisplayCnvtToPAEBtn).append("\n");
        buffer.append("isDisplayDldRichTextBtn = ").append(isDisplayDldRichTextBtn).append("\n");
        buffer.append("maxExpirationDays = ").append(maxExpirationDays).append("\n");
        buffer.append("formName = ").append(formName).append("\n");

        buffer.append("lob = ").append(lob).append("\n");
        buffer.append("isPA = ").append( isPA() ).append("\n");
        buffer.append("isPAE = ").append( isPAE() ).append("\n");
        buffer.append("isPPSS = ").append( isPPSS() ).append("\n");
        buffer.append("isFCT = ").append( isFCT() ).append("\n");

        buffer.append("isDisplayAccept = ").append(isDisplayAccept).append("\n");
        
        return buffer.toString();
    }

    public String getSubmitAsFinalBtnParams() {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.SUBMIT_DRAFT_SQ_AS_FINAL);
        submitAsFinalBtnParams = genBtnParams(this.getPostTabAction(), redirectURL,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=5," + ParamKeys.PARAM_FORWARD_FLAG + "=true,"
                + ParamKeys.PARAM_CURRENT_TAB_DISPLAY_ACTION + "=" + this.getDisplayTabAction());
        return submitAsFinalBtnParams;
    }

    /**
     * @return Returns the isDisplayLAUplift.
     */
    public boolean isDisplayLAUplift() {
        return ButtonDisplayRuleFactory.singleton().isDisplayLAUplift(header);
    }
    /**
     * @return Returns the isPymntDaysRadioChecked.
     */
    public boolean isPymntDaysRadioChecked() {
       return isPymntDaysRadioChecked;
    }
    /**
     * @return Returns the pymntTermsDays.
     */
    public String getPymntTermsDays() {
        return pymntTermsDays;
    }
    /**
     * @return Returns the isDisplayConv2StdCp.
     */
    public boolean isDisplayConv2StdCp() {
        return isDisplayConv2StdCp;
    }
    /**
     * @param isDisplayConv2StdCp The isDisplayConv2StdCp to set.
     */
    public void setDisplayConv2StdCp(boolean isDisplayConv2StdCp) {
        this.isDisplayConv2StdCp = isDisplayConv2StdCp;
    }

    public String getConv2StdCopyBtnURL(){

        String redirectURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.CONVERT_TO_STD_COPY);
        return genBtnParams(this.getPostTabAction(), redirectURL,
                ParamKeys.PARAM_FORWARD_FLAG + "=true," + ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum);
    }

	public boolean isDisplayNotAllowOrderForCustBlockMsg() {
		return isDisplayNotAllowOrderForCustBlockMsg;
	}

	public void setDisplayNotAllowOrderForCustBlockMsg(
			boolean isDisplayNotAllowOrderForCustBlockMsg) {
		this.isDisplayNotAllowOrderForCustBlockMsg = isDisplayNotAllowOrderForCustBlockMsg;
	}

	public boolean isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg() {
		return isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg;
	}

	public void setDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg(
			boolean isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg) {
		this.isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg = isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg;
	}

	public boolean isDisplayEstmtdOrdDate() {
		return header.isAddTrdOrCotermFlag();
	}

	public boolean isDisplayNotSupportExprtSprdshtMsg(){
		return !isDisplayExportQuoteAsSpreadsheet && header.isOnlySaaSParts() && !this.isPAQuote() && !this.isPAEQuote() && !this.isFCTQuote()&& !this.isSSP();
	}

	public boolean hasSaaSLineItem() {
		return hasSaaSLineItem;
	}

	public void setDisplayNotMatchBPsOnChargeAgreementFlag(
			boolean isDisplayNotMatchBPsOnChargeAgreementFlag) {
		this.isDisplayNotMatchBPsOnChargeAgreementFlag = isDisplayNotMatchBPsOnChargeAgreementFlag;
	}

	public boolean isDisplayNotMatchBPsOnChargeAgreementFlag() {
		return isDisplayNotMatchBPsOnChargeAgreementFlag;
	}

	public boolean isPGSSubmitter() {
		return isPGSSubmitter;
	}

	public boolean isDisplayFct2PaQuoteTerminationMsg() {
		return isDisplayFct2PaQuoteTerminationMsg;
	}
	
	public boolean isDisplayCannotSubmitWithSoftpartInPGSMsg(){
		return isDisplayCannotSubmitWithSoftpartInPGSMsg;
	}
	
	public boolean isDisplayEvalOptSection(){
		return isDisplayEvalOptSection;
	}
	
	public String getAcceptQuoteURL() {
		String redirectURL = HtmlUtil.getURLForAction(this.getDisplayTabAction());
        String retUrl = genBtnParams(this.getPostTabAction(), redirectURL,
                DraftQuoteParamKeys.PARAM_BTN_NAME + "=10," + ParamKeys.PARAM_FORWARD_FLAG + "=true");
        return retUrl;
	}
	
	public boolean isOnlySaaSParts(){
		return header.isOnlySaaSParts();
	}
	
	public boolean isChannelQuote(){
		return header.isChannelQuote();
	}
	
    public boolean isSubmittedQuote(){
        return header.isSubmittedQuote();
    }
    
	public String getEditConfigurationFlag() {
		return quote.getConfigurationEditParams() == null ? "" : quote.getConfigurationEditParams().getEditConfigurationFlag();
	}
    
	public boolean showOmittedRenwalLine(){
		if (null != header && header.isOmittedLine()){
			return true;
		}
		return false;
	}
	
	public String getReviewUpdateOmitLineURL(){
	    return HtmlUtil.getURLForAction(DraftQuoteActionKeys.CREATE_OMIT_RENEWAL_LINE, DraftQuoteActionKeys.DISPLAY_OMITTED_LINEITEM);
	}
	
	public String getCalculateOmitLineURL(){
	    return HtmlUtil.getURLForAction(DraftQuoteActionKeys.RECALCULATE_GROWTH_DELEGATION, DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB);
	}
	
	//added by Ma Chao
	public boolean isDisplayOrderForDivestedPartMsg(){ 
		return isDisplayOrderForDivestedPartMsg;
	}
	
    public boolean isDisplayAssgnCrtAgrmntNoRefreshedMsg() {
		if (header.isPAQuote() && header.hasExistingCustomer() && cust != null
 && quote.getQuoteHeader().isCreateCtrctFlag()
				&& StringUtils.isEmpty(this.getAgreementNum())
				&& priceLevelNotSet) {
    		isDisplayAssgnCrtAgrmntNoRefreshedMsg = true;
		}

		return isDisplayAssgnCrtAgrmntNoRefreshedMsg;
	}

}
