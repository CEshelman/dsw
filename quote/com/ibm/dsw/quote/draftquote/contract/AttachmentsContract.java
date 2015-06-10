package com.ibm.dsw.quote.draftquote.contract;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.common.domain.CompressedQuoteAttachment;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.jade.util.UploadFile;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author George
 */
public class AttachmentsContract extends QuoteBaseContract {
    
    private static final long serialVersionUID = -3456197266784046275L;
    
    private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

	private transient List files;
    
    private String uploadManifest;
    
    private String webQuoteNumber;
    
    private String stage;
    
    private String secId;
    
    private String userName;
    
    private String fileInputFlag = null;
    //For db filed ATTCHMT_CLASSFCTN_CODE
    private String attCode;
    
    private String[] touURL;
    private String[] touName;//transmit the touName from page to QuoteAttachment
    private String attchmtSeqNum[];
    public void load(Parameters parameters, JadeSession session) {
        try {
            super.load(parameters, session);
            webQuoteNumber = parameters.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM);
            stage = parameters.getParameterAsString(ParamKeys.PARAM_STAGE);
            secId = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_SEC_ID);
            QuoteUserSession user = (QuoteUserSession) session.getAttribute(SessionKeys.SESSION_QUOTE_USER);
            userName = user.getFirstName() + "&nbsp;&nbsp;" + user.getLastName();
            fileInputFlag = parameters.getParameterAsString(SpecialBidParamKeys.FILE_INPUT_FLAG);
            attCode = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_ATT_CODE);
            files = new ArrayList();
            touURL = parameters.getParameterWithMultiValues(DraftQuoteParamKeys.PARAM_TOU_URL);
            touName = parameters.getParameterWithMultiValues(DraftQuoteParamKeys.PARAM_TOU_NAME);
            attchmtSeqNum = parameters.getParameterWithMultiValues(DraftQuoteParamKeys.PARAM_ATTCH_SEQ_NUM);
            for (int i = 1; i <= 20; i++) {
                String key = DraftQuoteParamKeys.PARAM_JUSTIFICATION_DOCUMENT + i;
                if (parameters.hasParameter(key)) {
                    Object o = parameters.getParameter(key);
                    UploadFile file = null;
                    if (o instanceof UploadFile) {
                        file = (UploadFile) o;
                        QuoteAttachment attachment = jadeUploadFileToAttechment(file);
                        if(null != touURL && touURL.length > (i-1)){
                        	attachment.setTouURL(touURL[i-1]);
                        }
                        if(null != touName && touName.length > (i-1)){
                        	attachment.setTouName(touName[i-1]);
                        }
                        if(null != attchmtSeqNum && attchmtSeqNum.length > (i-1)){
                        	attachment.setAttchmtSeqNum(attchmtSeqNum[i-1]);
                        }
                        files.add(attachment);
                    } else if (o instanceof String) {
                        //System.err.println("can not find file:" + o);
                    }
                }
            }
        } catch (Exception e) {
        	logContext.error(this, e);
        }
    }
    
    public String getStage() {
        return stage;
    }
    public List getFiles() {
        return files;
    }
    
    public String getUploadManifest() {
        return uploadManifest;
    }
    
    public String getWebQuoteNumber() {
        return webQuoteNumber;
    }
    
    public String getSecId() {
        return secId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    protected QuoteAttachment jadeUploadFileToAttechment(UploadFile file){
        if(file == null){
            return null;
        }
        CompressedQuoteAttachment attachment = new CompressedQuoteAttachment();
        attachment.setQuoteNumber(webQuoteNumber);
        attachment.setFileName(file.getFileName());
        attachment.setSavedToCM(false);
        attachment.setStageCode(QuoteAttachment.STATE_PENDING);
        attachment.setUploaded(file.isFileUploaded());
        attachment.setUploaderEmail(getUserId());
        if(attachment.isUploaded()){
            attachment.setUploadPath(file.getFile().getPath());
        }
        attachment.setFileSize(file.getFile().length());
        attachment.setAttchmt(file.getFile());
        attachment.setSecId(secId);
        return attachment;
    }
    /**
     * @param files The files to set.
     */
    public void setFiles(List files) {
        this.files = files;
    }
    
    public String getFileInputFlag() {
        return fileInputFlag;
    }
    
    public String getAttCode()
	{
		return attCode;
	}

	public String[] getTouURL() {
		return touURL;
	}

	public void setTouURL(String[] touURL) {
		this.touURL = touURL;
	}
}