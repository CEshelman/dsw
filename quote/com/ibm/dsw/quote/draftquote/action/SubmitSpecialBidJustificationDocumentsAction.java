/*
 * Created on 2007-4-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.action;

import java.io.File;
import java.util.List;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.SpecialBidCommentDocumentsContract;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubmitSpecialBidJustificationDocumentsAction extends BaseContractActionHandler {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        SpecialBidCommentDocumentsContract sbContract = (SpecialBidCommentDocumentsContract)contract;
        
        List files = sbContract.getFiles();
        
        QuoteAttachmentProcessFactory.singleton().create().processQuoteAttachments(files);
        
        handler.addObject(DraftQuoteParamKeys.PARAM_ATTACHMENTS, files);
        
        handler.addObject(ParamKeys.PARAM_LOCAL, ((QuoteBaseContract) contract).getLocale());
        
        handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_SPECIALBID_FINALIZATION_RESULT);
        
        return handler.getResultBean();
    }
    
    

    protected boolean validate(ProcessContract contract) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        SpecialBidCommentDocumentsContract sbContract = (SpecialBidCommentDocumentsContract)contract;
        List files = sbContract.getFiles();
        for(int i=0;i<files.size();i++){
            QuoteAttachment attachment = (QuoteAttachment) files.get(i);
            if(attachment.isUploaded()){
                File f = attachment.getAttchmt();
                if(f.exists()){
                    int fileSizeLimitation = ApplicationProperties.getInstance().getUploadFileMaxSize();
                    if(attachment.getFileSize() > fileSizeLimitation*1024*1024){
                        attachment.setUploaded(false);
                        f.delete();
                        attachment.setMessage("File ["+attachment.getFileName()+"] "+appCtx.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES,((QuoteBaseContract) contract).getLocale(),DraftQuoteMessageKeys.MSG_FILE_TOO_LARGE).replaceAll("\\{1\\}", String.valueOf(fileSizeLimitation)));
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
                }else{
                    attachment.setUploaded(false);
                    attachment.setMessage("File ["+attachment.getFileName()+"] "+appCtx.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES,((QuoteBaseContract) contract).getLocale(),DraftQuoteMessageKeys.MSG_FILE_UNABLE_READ));
                }
            }else{
                attachment.setMessage("File ["+attachment.getFileName()+"] "+appCtx.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES,((QuoteBaseContract) contract).getLocale(),DraftQuoteMessageKeys.MSG_FILE_UNABLE_READ));
            }
        }
        return true;
    }
}
