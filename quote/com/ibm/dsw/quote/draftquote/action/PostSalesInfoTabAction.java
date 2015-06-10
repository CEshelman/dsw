package com.ibm.dsw.quote.draftquote.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.domain.SalesOdds;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.dlgtn.config.DlgtnActionKeys;
import com.ibm.dsw.quote.dlgtn.config.DlgtnParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PostSalesInfoTabContract;
import com.ibm.dsw.quote.opportunity.exception.OpportunityDSException;
import com.ibm.dsw.quote.opportunity.process.OpportunityCommonProcess;
import com.ibm.dsw.quote.opportunity.process.OpportunityCommonProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>PostSalesInfoTabAction<code> class.
 *
 * @author: zhaoxw@cn.ibm.com
 *
 * Creation date: Mar 20, 2007
 */

public class PostSalesInfoTabAction extends PostDraftQuoteBaseAction {

    private static final int MAX_QT_TITLE_LENGTH = 75;
    private static final int MAX_QT_DESC_LENGTH = 255;

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.action.PostDraftQuoteBaseAction#postDraftQuoteTab(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        PostSalesInfoTabContract postContract = (PostSalesInfoTabContract) contract;
        String creatorId = postContract.getUserId();
        String webQuoteNum = postContract.getWebQuoteNum();
        String briefTitle = StringUtils.trimToEmpty(postContract.getBriefTitle());
        String quoteDesc = StringUtils.trimToEmpty(postContract.getQuoteDesc());
        String busOrgCode = postContract.getBusOrgCode();
        String upsideTrendTowardsPurch = postContract.getUpsideTrendTowardsPurch();
        String salesStageCode = postContract.getSalesOdds();
        String tacticCodes = getTracticCodes(postContract.getTacticCodes());
        String comments = postContract.getSalesComments();
        String markBOasDefault = postContract.getMarkBOasDefault();
        String detailComments = postContract.getDetailComments();
        String opprtntyNum = null;
        String exemptnCode = null;
        String quoteClassifctnCode = postContract.getQuoteClassfctnCode();
        String custReasCode = postContract.getCustReasCode();
        String blockRenewalReminderValue = postContract.getBlockReminder();
        int blockRenewalReminder = 0;
        int pymTermsDays = postContract.getPymntTermsDays();
        int oemBidType = postContract.getOemBidType();

        if("Y".equalsIgnoreCase(blockRenewalReminderValue)){
            blockRenewalReminder = 1;
        }


        if(DraftQuoteConstants.SELECT_RADIO_OPPNUM.equals(postContract.getOppNumRadio())){
        	opprtntyNum = postContract.getOpprtntyNumSel();
            exemptnCode = null;
        } else if(DraftQuoteConstants.OPPNUM_RADIO_OPPNUM.equals(postContract.getOppNumRadio())){
            opprtntyNum = postContract.getOpprtntyNum();
            exemptnCode = null;
        }else if (DraftQuoteConstants.OPPNUM_RADIO_80_EXEMPTNCODE.equals(postContract.getOppNumRadio())) {
            exemptnCode = StringUtils.trimToEmpty(postContract.getExemptnCode());
            opprtntyNum = null;
        }
        else if (DraftQuoteConstants.OPPNUM_RADIO_EXEMPTNCODE.equals(postContract.getOppNumRadio())){
            exemptnCode = postContract.getExemptnCode();
            opprtntyNum = null;
        }

        QuoteProcess process = QuoteProcessFactory.singleton().create();

        // if all the fields are empty, then don't update them
        if (StringUtils.isBlank(postContract.getBriefTitle())
                && StringUtils.isBlank(postContract.getQuoteDesc())
                && StringUtils.isBlank(postContract.getBusOrgCode())
                && StringUtils.isBlank(postContract.getOpprtntyNum())
                && StringUtils.isBlank(postContract.getOpprtntyNumSel())
                && StringUtils.isBlank(postContract.getOppNumRadio())
                && StringUtils.isBlank(postContract.getUpsideTrendTowardsPurch())
                && StringUtils.isBlank(postContract.getSalesOdds())
                && !(postContract.getTacticCodes()!=null && postContract.getTacticCodes().length>0)
                && StringUtils.isBlank(postContract.getSalesComments())
                && postContract.getExpireDate() == null
                && StringUtils.isBlank(postContract.getQuoteClassfctnCode())
                && StringUtils.isBlank(postContract.getCustReasCode())) {
            logContext.info(this, "There isn't any input.");
        } else {
            // to update the quote contact
            String salesOddsCode = "";
            if (StringUtils.isNotBlank(salesStageCode)) {
                logContext.info(this, "Get sales odds with sales stage code " + salesStageCode + ", cust reason code "
                        + custReasCode);
                CacheProcess cacheProcess = CacheProcessFactory.singleton().create();
                SalesOdds salesOdds = cacheProcess.getSalesOddsByCode(salesStageCode, custReasCode);
                if (salesOdds != null)
                    salesOddsCode = salesOdds.getRenwlQuoteSalesOddsCode();
            }
            logContext.debug(this, "The sales odds code is " + salesOddsCode);

            process.updateQuoteHeaderSalesInfoTab(creatorId, postContract.getExpireDate(), briefTitle, quoteDesc,
                    busOrgCode, opprtntyNum, exemptnCode, upsideTrendTowardsPurch, salesOddsCode, tacticCodes,
                    comments, quoteClassifctnCode, salesStageCode, custReasCode, postContract.getStartDate(),
                    postContract.getOemAgrmntType(), blockRenewalReminder, pymTermsDays, oemBidType, postContract.getEstmtdOrdDate(),postContract.getCustReqstdArrivlDate(),postContract.getSspType());

        }

        if (QuoteConstants.QUOTE_TYPE_RENEWAL.equalsIgnoreCase(postContract.getQuoteType())) {
            QuoteTxt qtCmmnt = process.getRenewalQuoteDetailComment(webQuoteNum);
            int cmmntId = qtCmmnt == null ? -1 : qtCmmnt.getQuoteTextId();

            if (StringUtils.isNotBlank(detailComments) || cmmntId != -1) {
                int txtId = process.updateQuoteComment(webQuoteNum, cmmntId, QuoteConstants.RQ_DETAIL_SALES_COMMENTS,
                        detailComments, creatorId, "0");
            }
        }

        if(QuoteConstants.CHECKBOX_CHECKED.equalsIgnoreCase(markBOasDefault)){
            Cookie sqoCookie = postContract.getSqoCookie();
            QuoteCookie.setBusinessOrg(sqoCookie, busOrgCode);
        }
    }

    /**
     * @param tacticCodes2
     * @return
     */
    private String getTracticCodes(String[] tacticCodes) {
        StringBuffer sb = new StringBuffer();
        if(tacticCodes != null && tacticCodes.length > 0){
            sb.append(tacticCodes[0]);
            for(int i=1; i<tacticCodes.length; i ++ ){
                sb.append(",").append(tacticCodes[i]);
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    protected boolean validate(ProcessContract contract) {
        boolean valid = super.validate(contract);

        if (valid) {
            PostSalesInfoTabContract postContract = (PostSalesInfoTabContract) contract;
            String title = postContract.getBriefTitle();
            HashMap map = new HashMap();

            if (title != null) {
				try {
					int length = title.getBytes("UTF-8").length;
	                if (length > MAX_QT_TITLE_LENGTH) {
	                    valid = false;
	                    FieldResult field = new FieldResult();
	                    field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_BRIEF_TITLE_TOO_LONG);
	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.BRIEF_TITLE);
	                    map.put(DraftQuoteParamKeys.PARAM_SI_BRIEF_TITLE, field);
	                }
				} catch (UnsupportedEncodingException e) {
					logContext.error(e, e.getMessage());
				}
            }

            String description = postContract.getQuoteDesc();
            if (description != null) {
				try {
	                int length = description.getBytes("UTF-8").length;
	                if (length > MAX_QT_DESC_LENGTH) {
	                    valid = false;
	                    FieldResult field = new FieldResult();
	                    field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_QUOTE_DESC_TOO_LONG);
	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.FULL_DESC);
	                    map.put(DraftQuoteParamKeys.PARAM_SI_FULL_DSCR, field);
	                }
				} catch (UnsupportedEncodingException e) {
					logContext.error(e, e.getMessage());
				}
            }
            String sc = postContract.getSalesComments();
            if (sc != null) {
				try {
	                int length = sc.getBytes("UTF-8").length;
	                if (length > DraftQuoteConstants.MAX_RQ_SALSE_COMM) {
	                    valid = false;
	                    FieldResult field = new FieldResult();
	                    field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_SALES_COMMENTS_TOO_LONG);
	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.SALES_COMMENTS);
	                    map.put(DraftQuoteParamKeys.PARAM_SI_SALES_COMMENTS, field);
	                }
				} catch (UnsupportedEncodingException e) {
					logContext.error(e, e.getMessage());
				}
            }

            String opprtntyNum = null;
            String exemptnCode = null;
            boolean checkRadioFlag = true;
            if(DraftQuoteConstants.SELECT_RADIO_OPPNUM.equals(postContract.getOppNumRadio())){
            	opprtntyNum = postContract.getOpprtntyNumSel();
            } else if(DraftQuoteConstants.OPPNUM_RADIO_OPPNUM.equals(postContract.getOppNumRadio())){
                opprtntyNum = postContract.getOpprtntyNum();
                if(!StringUtils.isBlank(opprtntyNum)){
                	logContext.debug(this, "validate opportunity number from EIW:" + opprtntyNum);
                    try {
                    	QuoteProcess process = QuoteProcessFactory.singleton().create();
    					QuoteHeader quoteHeader = process.getQuoteHdrInfoByWebQuoteNum(postContract.getWebQuoteNum());
    					if(null != quoteHeader){
    						OpportunityCommonProcess oppProcess;
    	    				oppProcess = OpportunityCommonProcessFactory.singleton().create();

        					boolean isValidOpptNum = oppProcess.isValidOpptNum(opprtntyNum);
        					if(!isValidOpptNum){
        						valid = false;
        	                    FieldResult field = new FieldResult();
        	                    field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_OPP_NUM);
        	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.OPP_NUM);
        	                    map.put(DraftQuoteParamKeys.PARAM_SI_SIEBEL_INPUT, field);
        					}
    					}
    				} catch (OpportunityDSException e) {
						valid = false;
	                    FieldResult field = new FieldResult();
	                    String[] arg = {opprtntyNum};
	                    String oppMsg = getI18NString(DraftQuoteMessageKeys.OPP_CANNOT_VALIDATE_FOR_EIW,I18NBundleNames.BASE_MESSAGES,postContract.getLocale(),arg);
	                    field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, oppMsg);
	                    field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.OPP_NUM);
	                    map.put(DraftQuoteParamKeys.PARAM_SI_SIEBEL_INPUT, field);
    					logContext.error(this, e.getMessage());
    				} catch (NoDataException e1) {
    					logContext.error(this, e1.getMessage());
    				} catch (QuoteException e1) {
    					logContext.error(this, e1.getMessage());
    				}
                }

            }else if (DraftQuoteConstants.OPPNUM_RADIO_EXEMPTNCODE.equals(postContract.getOppNumRadio())){
                exemptnCode = postContract.getExemptnCode();
            }else{
            	checkRadioFlag = false;
            }

        	if(checkRadioFlag && (StringUtils.isBlank(opprtntyNum)) && (StringUtils.isBlank(exemptnCode))){
        		valid = false;
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.ENTER_OPP_EXMP_CODE_MSG);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.OPP_NUM);
                map.put(DraftQuoteParamKeys.PARAM_SI_SIEBEL_INPUT, field);
            }

            addToValidationDataMap(postContract, map);
        }

        return valid;
    }

    protected HashMap getRedirerctParams(ProcessContract contract){
        HashMap params = new HashMap();
        PostSalesInfoTabContract postContract = (PostSalesInfoTabContract) contract;
        String redirectURL = postContract.getRedirectURL();
        String oppOwnerEmailAddr = StringUtils.trimToEmpty(postContract.getOppOwnerEmailAddr());
        String delegateId = StringUtils.trimToEmpty(postContract.getDelegateId());
        if(StringUtils.isNotEmpty(redirectURL)){
            if(redirectURL.indexOf(DraftQuoteActionKeys.UPDATE_OPP_OWNER)!=-1){
                params.put(DraftQuoteParamKeys.PARAM_OPP_OWNER_EMAIL, oppOwnerEmailAddr);
            }
            if(redirectURL.indexOf(DlgtnActionKeys.ADD_QUOTE_DELEGATE)!=-1){
                params.put(DlgtnParamKeys.PARAM_DELEGATEID, delegateId);
            }
        }
        return params;
    }
}
