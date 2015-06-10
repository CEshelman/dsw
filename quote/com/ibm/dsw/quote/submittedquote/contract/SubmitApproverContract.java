package com.ibm.dsw.quote.submittedquote.contract;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.jade.util.UploadFile;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SubmitApproverContract<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public class SubmitApproverContract extends SubmittedQuoteBaseContract {
    private String apprvrAction = null;
    private String apprvrComments = null;
    private String webQuoteNum = null;
    private transient List files;
    private int approveLevel = -2;
    private String tncTxt;
    private String summaryTxt;
    private String returnReason = null;

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        String strTemp = parameters.getParameterAsString(SpecialBidParamKeys.APPROVE_LEVEL);
        if ( strTemp != null && !strTemp.equals("") )
        {
            approveLevel = Integer.parseInt(strTemp);
        }
        webQuoteNum = parameters.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM);
        apprvrComments = parameters.getParameterAsString(SpecialBidParamKeys.SPECIAL_BID_COMMENT_APPROVE);
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(apprvrComments)) == true){
        	apprvrComments= "";
        }
        apprvrAction = parameters.getParameterAsString(SubmittedQuoteParamKeys.PARAM_APPRVR_ACTION);
        tncTxt = parameters.getParameterAsString(SpecialBidParamKeys.EXPLANATION_TEXT);
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(tncTxt)) == true){
        	tncTxt = "";
        }
        summaryTxt = parameters.getParameterAsString(SpecialBidParamKeys.SECTION_JUST_TEXTS);
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(summaryTxt)) == true){
        	summaryTxt = "";
        }
        files = new ArrayList();
        returnReason = parameters.getParameterAsString(SpecialBidParamKeys.RETURN_REASON);
        for (int i = 1; i <= 3; i++) {
            String key = DraftQuoteParamKeys.PARAM_JUSTIFICATION_DOCUMENT + i;
            if (parameters.hasParameter(key)) {
                Object o = parameters.getParameter(key);
                UploadFile file = null;
                if (o instanceof UploadFile) {
                    file = (UploadFile) o;
                    files.add(jadeUploadFileToSpecialBidAttechment(file));
                } else if (o instanceof String) {
                    //System.err.println("can not find file:" + o);
                }
            }
        }
    }
    
    private QuoteAttachment jadeUploadFileToSpecialBidAttechment(UploadFile file){
        if(file == null){
            return null;
        }
        QuoteAttachment attachment = new QuoteAttachment();
        attachment.setQuoteNumber(webQuoteNum);
        attachment.setFileName(file.getFileName());
        attachment.setSavedToCM(false);
        attachment.setStageCode(QuoteAttachment.STATE_PENDING);
        attachment.setUploaded(file.isFileUploaded());
        attachment.setUploaderEmail(getUserId());
        attachment.setClassfctnCode(QuoteConstants.QT_ATTCHMNT_SPEL_BID);
        if(attachment.isUploaded()){
            attachment.setUploadPath(file.getFile().getPath());
        }
        attachment.setFileSize(file.getFile().length());
        attachment.setAttchmt(file.getFile());
        return attachment;
    }
    /**
     * @return Returns the apprvrAction.
     */
    public String getApprvrAction() {
        return apprvrAction;
    }
    /**
     * @return Returns the apprvrComments.
     */
    public String getApprvrComments() {
        return apprvrComments;
    }
     
    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    /**
     * @return Returns the files.
     */
    public List getFiles() {
        return files;
    }
    
    /**
     * @return Returns the approveLevel.
     */
    public int getApproveLevel() {
        return approveLevel;
    }
	/**
	 * @return Returns the summaryTxt.
	 */
	public String getSummaryTxt() {
		return summaryTxt;
	}
	/**
	 * @return Returns the tncTxt.
	 */
	public String getTncTxt() {
		return tncTxt;
	}
	public String getReturnReason() {
		return returnReason;
	}
 }
