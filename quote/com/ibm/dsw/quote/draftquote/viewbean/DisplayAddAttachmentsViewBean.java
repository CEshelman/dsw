/*
 * Created on 2007-7-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.base.util.UUIDHelper;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.draftquote.action.DisplayAddAttachmentsAction;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author helenyu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayAddAttachmentsViewBean extends BaseViewBean {

	private static final long serialVersionUID = 3378376973748906362L;

	private transient List filesFailedToCommitToCMWI;
    
    private boolean showMoreFileInputs = false;
    
    private String webQuoteNum;
    
    private String stage;
    
    private String uploadFileUUID;
    
    private String secId;
    
    private String userName;
    
    private int fileListSize = 0;
    
    private transient List files = null;
    
    private String downloadFileURL = null;
    
    private String removeFileURL = null;
    
    private String attCode = "";
    
    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
    /* (non-Javadoc)
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters params) throws ViewBeanException {       
        webQuoteNum = params.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM);
        stage = params.getParameterAsString(ParamKeys.PARAM_STAGE);
        secId = params.getParameterAsString(DraftQuoteParamKeys.PARAM_SEC_ID);
        userName = params.getParameterAsString(ParamKeys.PARAM_USER_NAME);
        attCode = params.getParameterAsString(DraftQuoteParamKeys.PARAM_ATT_CODE);
        
        downloadFileURL = HtmlUtil.getURLForAction(ActionKeys.DOWNLOAD_ATTACHMENT);
        if(StringUtils.equals(stage, DisplayAddAttachmentsAction.STAGE_FINALIZATION)){
            showMoreFileInputs = true;
        }
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        filesFailedToCommitToCMWI = new ArrayList();
        files = (List)params.getParameter(DraftQuoteParamKeys.PARAM_ATTACHMENTS);
       
        if ( files == null )
        {
            files = new ArrayList();
        }
       
        Locale locale = (Locale)params.getParameter(ParamKeys.PARAM_LOCAL);
        if(files != null){
            fileListSize = files.size();
            for(int i = 0; i<files.size();i++){
                QuoteAttachment attachment = (QuoteAttachment)files.get(i);
                removeFileURL = this.genRemoveAttachmentURL(attCode, StringEncoder.textToHTML(attachment.getQuoteNumber()),
                		StringEncoder.textToHTML(attachment.getId()));
                String removeAjaxURL = this.genRemoveAjaxURL(attCode, StringEncoder.textToHTML(attachment.getQuoteNumber()),
                		StringEncoder.textToHTML(attachment.getId()));
                attachment.setRemoveAjaxURL(removeAjaxURL);
                attachment.setRemoveURL(removeFileURL);
                attachment.setDownloadURL(downloadFileURL + "&amp;attchmtSeqNum=" + StringEncoder.textToHTML(attachment.getId()));
                logContext.debug(this,"QuoteAttachment name "+attachment.getFileName()+" remove URL:" + removeFileURL + "; down url: " + attachment.getDownloadURL());
                if(!attachment.isUploaded()){
                    filesFailedToCommitToCMWI.add(attachment);
                }else if(attachment.isUploaded() && !attachment.isSavedToCM()){
                    attachment.setMessage("File ["+attachment.getFileName()+"] "+appCtx.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES, locale, DraftQuoteMessageKeys.MSG_FILE_CANNOT_BE_STORED));
                    filesFailedToCommitToCMWI.add(attachment);
                }
        	}
        }else{
            files = new ArrayList();
        
        }
        logContext.debug(this,"End of AttachFilesToQuoteViewBean.collectResults");
        super.collectResults(params);
    }
    
    
    /**
     * @return Returns the showMoreFileInputs.
     */
    public boolean isShowMoreFileInputs() {
        return showMoreFileInputs;
    }
    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    
    /**
     * @return Returns the filesFailedToCommitToCMWI.
     */
    public List getFilesFailedToCommitToCMWI() {
        return filesFailedToCommitToCMWI;
    }
    
    /**
     * @return Returns the stage.
     */
    public String getStage() {
        return stage;
    }
    
    /**
     * @return Returns the uploadFileUUID.
     */
    public String getUploadFileUUID() {
        if(uploadFileUUID == null){
            uploadFileUUID = new UUIDHelper().generate().toString();
        }
        return uploadFileUUID;
    }
    
    /**
     * @return Returns the sectionSeq.
     */
    public String getSecId() {
        return secId;
    }
    
    /**
     * @return Returns the userId.
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @return Returns the fileListSize.
     */
    public int getFileListSize() {
        return fileListSize;
    }
    /**
     * @return Returns the files.
     */
    public List getFiles() {
        return files;
    }
    /**
     * @return Returns the downloadFileURL.
     */
    public String getDownloadFileURL() {
        return downloadFileURL;
    }
    
    protected String genRemoveAttachmentURL(String attCode, String quoteNum, String fileNum){
    	if ( StringUtils.isEmpty(attCode) )
    	{
    		return genRemoveAttachmentURL(quoteNum, fileNum);
    	}
    	return attCode;
    }
    
    protected String genRemoveAjaxURL(String attCode, String quoteNum, String fileNum){
    	if ( StringUtils.isEmpty(attCode) )
    	{
    		return "";
    	}
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        String urlPattern = appContext.getConfigParameter(ApplicationProperties.APPLICATION_URL_PATTERN);
        String targetAction = DraftQuoteActionKeys.REMOVE_ATTACHMENT_ACTION;
        StringBuffer sb = new StringBuffer();
        sb.append(urlPattern).append("?");
        sb.append(actionKey).append("=").append(targetAction);
        sb.append("&").append(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM).append("=").append(quoteNum);
        sb.append("&").append("attchmtSeqNum").append("=").append(fileNum);
        sb.append("&").append(DraftQuoteParamKeys.PARAM_ATT_CODE).append("=").append(attCode);
        logContext.debug(this, sb.toString());
    	return sb.toString();
    }
    
    protected String genRemoveAttachmentURL(String quoteNum, String fileNum) {
        String targetParams = ","+DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM+"=" + quoteNum + ",attchmtSeqNum=" + fileNum;
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        StringBuffer sb = new StringBuffer();
        String targetAction = DraftQuoteActionKeys.REMOVE_ATTACHMENT_ACTION;
        String secondAction = DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB;
        
        sb.append(actionKey).append("=").append(targetAction);
        if (StringUtils.isNotBlank(secondAction))
            sb.append("," + appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY) + "=").append(
                    secondAction);
        if (StringUtils.isNotBlank(targetParams))
            sb.append(",").append(targetParams);
        return sb.toString();
    }
    
    public String getAttCode()
	{
		return attCode;
	}
}