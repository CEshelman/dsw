/*
 * Created on 2007-5-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.viewbean;

import java.io.File;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author helen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AttachmentDownloadViewBean extends BaseViewBean {
    
    private transient File content = null;
    
    private String mimeType = null;
    
    private String fileName = null;
    
    public void collectResults(Parameters param) throws ViewBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
	    logContext.debug(this,"Begin to collect results for ViewBean.");
	    content = (File) param.getParameter(SubmittedQuoteParamKeys.PARAM_ATTACHMENT);
	    if(content == null ){
	        throw new ViewBeanException("Attachment Doc Content is null");
	    }
	    logContext.debug(this, "Got attachment document, it's size is: " + content.length());
	    mimeType = (String) param.getParameterAsString(SubmittedQuoteParamKeys.PARAM_MIME_TYPE);
	    fileName = (String) param.getParameterAsString(SubmittedQuoteParamKeys.PARAM_FILE_NAME);
	    if(mimeType != null && !"".equals(mimeType)){
	        logContext.debug(this, "Attachment mime type is: " + mimeType);
	    }
	    if(fileName != null && !"".equals(fileName)){
	        logContext.debug(this, "Attachment file name is: " + fileName);
	    }else{
	        fileName = "SpecialBidSupportAttachment";
	        logContext.debug(this, "Attachment file name is null, set it to: " + fileName);
	    }
	    super.collectResults(param);
		logContext.debug(this,"Finished executing collecting results.");
	}

    /**
     * @return Returns the content.
     */
    public File getContent() {
        return content;
    }
    
    /**
     * @return Returns the mimeType.
     */
    public String getMimeType() {
        return mimeType;
    }
    
    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }
}
