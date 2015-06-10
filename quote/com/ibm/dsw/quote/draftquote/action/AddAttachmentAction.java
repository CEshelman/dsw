package com.ibm.dsw.quote.draftquote.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.AttachmentsContract;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author George
 */
public abstract class AddAttachmentAction extends BaseContractActionHandler {

    private static final long serialVersionUID = -7770378342781908589L;

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        logContext.debug(this, "Begin to execute AddAttachmentAction.executeBiz");
        uploadFile(contract, handler);
        addContractToResult(contract, handler);
        logContext.debug(this, "End executing AddAttachmentAction.executeBiz");
        
        handler.setState(StateKeys.STATE_DISPLAY_ATTACH_FILES_TO_QUOTE);
        return handler.getResultBean();
    }
    
    /**
     * uploadFile
     * 
     * @param contract
     * @param handler
     */
    protected void uploadFile(ProcessContract contract, ResultHandler handler) throws QuoteException {
            //ResultHandler handler, List files, Locale locale) {

        logContext.debug(this, "Trying to save the user's attachments.");
        
        AttachmentsContract aContract = (AttachmentsContract) contract;
        List files = aContract.getFiles();
        
        if (files != null) {
            String classfctnCode = this.getAttchmtClassfctnCode(contract);
            for (int i = 0; i < files.size(); i++) {
                QuoteAttachment attchmt = (QuoteAttachment) files.get(i);
                attchmt.setClassfctnCode(classfctnCode);
            }
        }
        
        QuoteAttachmentProcessFactory.singleton().create().processQuoteAttachments(files);
        
        //test upload
//        for(int i=0;i<files.size();i++)
//        {
//            QuoteAttachment file = (QuoteAttachment)files.get(i);
//            file.setId(new com.ibm.ws.util.UUID().toString());
//            file.setStageCode(QuoteAttachment.STATE_LOADED);
//            file.setSavedToCM(true);
//            file.getAttchmt().delete();
//            file.setUploaded(true);
//        }
        
        handleFileUploadMessages(handler, files, aContract.getLocale(), aContract);
        
        logContext.debug(this, "User's attachments have been processed.");
    }
    
    protected String getAttchmtClassfctnCode(ProcessContract contract) throws QuoteException
    {
    	AttachmentsContract attachContract = (AttachmentsContract)contract;
    	if ( StringUtils.isEmpty(attachContract.getAttCode()) )
    	{
    		return getAttchmtClassfctnCode();
    	}
    	else 
    	{
    		String fcstnCod=null;
    		if("1".equalsIgnoreCase(attachContract.getAttCode())){
    			fcstnCod="FCT_NON_STD_TC";
    		}else{
    			throw new QuoteException("Can't identify  attachment-classification-code!!!!!");
    		}
    		
    		//TODO:here return your attachment code, if the att code is some unknow type, throw quote exception
    		return fcstnCod;
    	}
    }
    
    protected abstract String getAttchmtClassfctnCode();
    
    protected void handleFileUploadMessages(ResultHandler handler, List files, Locale locale, ProcessContract contract) {

        logContext.debug(this, "Trying to process attachments' messages.");

        if (files == null || files.size() == 0)
            return;

        List filesFailedToCommitToCMWI = new ArrayList();
        for (int i = 0; i < files.size(); i++) {
            QuoteAttachment attachment = (QuoteAttachment) files.get(i);
            if (!attachment.isUploaded()) {
                filesFailedToCommitToCMWI.add(attachment);
            } else if (attachment.isUploaded() && !attachment.isSavedToCM()) {
                attachment.setMessage("["
                        + attachment.getFileName()
                        + "] "
                        + this.getI18NString(DraftQuoteMessageKeys.MSG_FILE_CANNOT_BE_STORED,
                                I18NBundleNames.DRAFT_QUOTE_MESSAGES, locale));
                filesFailedToCommitToCMWI.add(attachment);
            }
        }

        if (filesFailedToCommitToCMWI.size() == 0) {
            handler.addMessage(this.getI18NString(DraftQuoteMessageKeys.MSG_FILE_UPLOAD_SUCCESS,
                    I18NBundleNames.BASE_MESSAGES, locale), MessageBeanKeys.SUCCESS);
        } else {
            handler.addMessage(this.getI18NString(DraftQuoteMessageKeys.MSG_FILES_UPLOAD_FAILED,
                    I18NBundleNames.ERROR_MESSAGE, locale), MessageKeys.LAYER_MSG_HEAD);
            for (int i = 0; i < filesFailedToCommitToCMWI.size(); i++) {
                handler.addMessage(((QuoteAttachment) filesFailedToCommitToCMWI.get(i)).getMessage(),
                        MessageKeys.LAYER_MSG_ITEM);
            }
        }
        logContext.debug(this, "Attachments' messages have been processed.");
    }
    
    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
        // You can add the contract object here or you can add it in the child class.
        AttachmentsContract aContract = (AttachmentsContract) contract;
        List files = aContract.getFiles();
        String webQuoteNum = aContract.getWebQuoteNumber();
        String userName = aContract.getUserName();
        
        handler.addObject(ParamKeys.PARAM_QUOTE_NUM, webQuoteNum);
        handler.addObject(ParamKeys.PARAM_STAGE, DisplayAddAttachmentsAction.STAGE_SUBMITTED);
        handler.addObject(DraftQuoteParamKeys.PARAM_ATTACHMENTS, files);
        handler.addObject(ParamKeys.PARAM_USER_NAME,userName);
    }

    protected boolean validate(ProcessContract contract) {
        logContext.debug(this, "Validating inputs.");
        HashMap vMap = new HashMap();
        AttachmentsContract aContract = (AttachmentsContract) contract;
        List attachments = aContract.getFiles();
        if ((attachments == null || attachments.size() == 0)) {
            logContext.debug(this, "Validation failed, we need at least one attachment to process!");
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,//TODO:
                    DraftQuoteMessageKeys.MSG_COMMENT_OR_ATTACHMENTS_REQUIRED);
            vMap.put(DraftQuoteParamKeys.PARAM_JUSTIFICATION_DOCUMENT + 1, fieldResult);
            addToValidationDataMap(contract, vMap);
            if(attachments == null){
                attachments = new ArrayList();
            }
            return true;
        }
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        for (int i = 0; i < attachments.size(); i++) {
            QuoteAttachment attachment = (QuoteAttachment) attachments.get(i);
            if (attachment.isUploaded()) {
                File f = attachment.getAttchmt();
                if (f.exists()) {
                    int fileSizeLimitation = ApplicationProperties.getInstance().getUploadFileMaxSize();
                    if (attachment.getFileSize() > fileSizeLimitation * 1024 * 1024) {
                        attachment.setUploaded(false);
                        f.delete();
                        attachment.setMessage("File ["
                                + attachment.getFileName()
                                + "] "
                                + appCtx.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES,
                                        ((QuoteBaseContract) contract).getLocale(),
                                        DraftQuoteMessageKeys.MSG_FILE_TOO_LARGE).replaceAll("\\{1\\}",
                                        String.valueOf(fileSizeLimitation)));
                    }else if (attachment.getFileSize() <= 0) {
                        attachment.setUploaded(false);
                        f.delete();
                        attachment.setMessage("File ["
                                + attachment.getFileName()
                                + "] "
                                + appCtx.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES,
                                        ((QuoteBaseContract) contract).getLocale(),
                                        DraftQuoteMessageKeys.MSG_FILE_SIZE_ZERO));
                    }
                } else {
                    attachment.setUploaded(false);
                    attachment.setMessage("File ["
                            + attachment.getFileName()
                            + "] "
                            + appCtx.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES,
                                    ((QuoteBaseContract) contract).getLocale(),
                                    DraftQuoteMessageKeys.MSG_FILE_UNABLE_READ));
                }
            } else {
                attachment
                        .setMessage("File ["
                                + attachment.getFileName()
                                + "] "
                                + appCtx.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES,
                                        ((QuoteBaseContract) contract).getLocale(),
                                        DraftQuoteMessageKeys.MSG_FILE_UNABLE_READ));
            }
        }

        return super.validate(contract);
    }
}