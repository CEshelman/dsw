package com.ibm.dsw.quote.draftquote.contract;

import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.dsw.quote.draftquote.viewbean.DraftRQSalesInfoTabViewBean;
import com.ibm.ead4j.common.bean.Result;
import com.ibm.ead4j.common.bean.View;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.config.ResultBeanKeys;
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
 * This <code>PostSalesInfoTabContract<code> class.
 *
 * @author: zhaoxw@cn.ibm.com
 *
 * Creation date: Mar 20, 2007
 */

public class PostSalesInfoTabContract extends PostDraftQuoteBaseContract {

    private String briefTitle;

    private String quoteDesc;

    private String busOrgCode;

    private String opprtntyNum;

    private String opprtntyNumSel;

    public String getOpprtntyNumSel() {
		return opprtntyNumSel;
	}

	public void setOpprtntyNumSel(String opprtntyNumSel) {
		this.opprtntyNumSel = opprtntyNumSel;
	}
	private String exemptnCode;

    private String oppNumRadio;

    private String oppOwnerEmailAddr;

    private String delegateId;

    private String markBOasDefault;

    private String upsideTrendTowardsPurch;

    private String salesOdds;

    private String[] tacticCodes;

    private String salesComments;

    private String detailComments;

    private String detailCommentsId;

    private String custReasCode;

    private String blockReminder;

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        tacticCodes = parameters.getParameterWithMultiValues(DraftQuoteParamKeys.PARAM_SI_TACTIC_CODES);
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        String undoResultKey = genUndoResultKey();
        Result previousResult = (Result) session.getAttribute(undoResultKey);

        if (previousResult != null)  {
            View previousView = previousResult.getView();

            if (previousView != null && previousView instanceof DraftRQSalesInfoTabViewBean) {
                String sWebQuoteNum = this.getWebQuoteNum();
                String sDetailComments = this.getDetailComments();
                String sQuoteType = this.getQuoteType();
                DraftRQSalesInfoTabViewBean slsInfoView = (DraftRQSalesInfoTabViewBean) previousView;

                if (QuoteConstants.QUOTE_TYPE_RENEWAL.equalsIgnoreCase(sQuoteType)) {
                    slsInfoView.setDetailComments(sDetailComments);
                    try {
                        QuoteAttachmentProcess attchProcess = QuoteAttachmentProcessFactory.singleton().create();
                        List attachments = attchProcess.getRQSalesCommentAttachments(sWebQuoteNum);
                        slsInfoView.setSalesCmmntAttachments(attachments);
                    } catch (QuoteException e) {
                        logContext.error(this, "QuoteException catched while fetching attachments for renewal quote "
                                + sWebQuoteNum);
                    }
                }

                // Refresh sales stage code & cust reason code drop-down list
                slsInfoView.setPostSalesOddsInfo(salesOdds, custReasCode);

            }
        }
    }

    public String genUndoResultKey() {
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String key = context.getConfigParameter(ResultBeanKeys.JADE_PREVIOUS_RESULT_KEY);
        String prefix = context.getConfigParameter(FrameworkKeys.JADE_NAME_SPACE_PREFIX);
        if (prefix == null || prefix.length() == 0) {
            prefix = FrameworkKeys.JADE_NAME_SPACE_PREFIX_DEFAULT;
        }
        return prefix + key;
    }

    /**
     * @return Returns the delegateId.
     */
    public String getDelegateId() {
        return delegateId;
    }
    /**
     * @param delegateId The delegateId to set.
     */
    public void setDelegateId(String delegateId) {
        this.delegateId = delegateId;
    }
    /**
     * @return Returns the oppOwnerEmailAddr.
     */
    public String getOppOwnerEmailAddr() {
        return oppOwnerEmailAddr;
    }
    /**
     * @param oppOwnerEmailAddr The oppOwnerEmailAddr to set.
     */
    public void setOppOwnerEmailAddr(String oppOwnerEmailAddr) {
        this.oppOwnerEmailAddr = oppOwnerEmailAddr;
    }
    /**
     * @return Returns the briefTitle.
     */
    public String getBriefTitle() {
        return briefTitle;
    }
    /**
     * @param briefTitle The briefTitle to set.
     */
    public void setBriefTitle(String briefTitle) {
        this.briefTitle = briefTitle;
    }
    /**
     * @return Returns the busOrgCode.
     */
    public String getBusOrgCode() {
        return busOrgCode;
    }
    /**
     * @param busOrgCode The busOrgCode to set.
     */
    public void setBusOrgCode(String busOrgCode) {
        this.busOrgCode = busOrgCode;
    }
    /**
     * @return Returns the exemptnCode.
     */
    public String getExemptnCode() {
        return exemptnCode;
    }
    /**
     * @param exemptnCode The exemptnCode to set.
     */
    public void setExemptnCode(String exemptnCode) {
        this.exemptnCode = exemptnCode;
    }
    /**
     * @return Returns the opprtntyNum.
     */
    public String getOpprtntyNum() {
        return opprtntyNum;
    }
    /**
     * @param opprtntyNum The opprtntyNum to set.
     */
    public void setOpprtntyNum(String opprtntyNum) {
        this.opprtntyNum = opprtntyNum;
    }
    /**
     * @return Returns the quoteDesc.
     */
    public String getQuoteDesc() {
        return quoteDesc;
    }
    /**
     * @param quoteDesc The quoteDesc to set.
     */
    public void setQuoteDesc(String quoteDesc) {
        this.quoteDesc = quoteDesc;
    }
    /**
     * @return Returns the oppNumRadio.
     */
    public String getOppNumRadio() {
        return oppNumRadio;
    }
    /**
     * @param oppNumRadio The oppNumRadio to set.
     */
    public void setOppNumRadio(String oppNumRadio) {
        this.oppNumRadio = oppNumRadio;
    }
    /**
     * @return Returns the markBOasDefault.
     */
    public String getMarkBOasDefault() {
        return markBOasDefault;
    }
    /**
     * @param markBOasDefault The markBOasDefault to set.
     */
    public void setMarkBOasDefault(String markBOasDefault) {
        this.markBOasDefault = markBOasDefault;
    }
    /**
     * @return Returns the comments.
     */
    public String getSalesComments() {
        return salesComments;
    }
    /**
     * @param comments The comments to set.
     */
    public void setSalesComments(String salesComments) {
        this.salesComments = salesComments;
    }
    /**
     * @return Returns the salesOdds.
     */
    public String getSalesOdds() {
        return salesOdds;
    }
    /**
     * @param salesOdds The salesOdds to set.
     */
    public void setSalesOdds(String salesOdds) {
        this.salesOdds = salesOdds;
    }
    /**
     * @return Returns the tacticCodes.
     */
    public String[] getTacticCodes() {
        return tacticCodes;
    }
    /**
     * @return Returns the upsideTrendTowardsPurch.
     */
    public String getUpsideTrendTowardsPurch() {
        return upsideTrendTowardsPurch;
    }
    /**
     * @param upsideTrendTowardsPurch The upsideTrendTowardsPurch to set.
     */
    public void setUpsideTrendTowardsPurch(String upsideTrendTowardsPurch) {
        this.upsideTrendTowardsPurch = upsideTrendTowardsPurch;
    }

    public String getDetailComments() {
        return detailComments;
    }

    public void setDetailComments(String detailComments) {
        this.detailComments = detailComments;
    }

    public String getDetailCommentsId() {
        return detailCommentsId;
    }

    public void setDetailCommentsId(String detailCommentsId) {
        this.detailCommentsId = detailCommentsId;
    }

    public String getCustReasCode() {
        return custReasCode;
    }

    public void setCustReasCode(String custReasCode) {
        this.custReasCode = custReasCode;
    }
    /**
     * @return Returns the blockReminder.
     */
    public String getBlockReminder() {
        return blockReminder;
    }
    /**
     * @param blockReminder The blockReminder to set.
     */
    public void setBlockReminder(String blockReminder) {
        this.blockReminder = blockReminder;
    }
}
