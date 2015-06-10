/*
 * Created on 2007-5-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.action;

import java.io.File;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.common.service.CMServiceHelper;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.AttachmentDownloadContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author helen
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AttachmentDownloadAction extends BaseContractActionHandler {

    private static final long serialVersionUID = -4039845720657294211L;
	protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
    private static String QUOTE_OUTPUT_FILE_SUFFIX = ".pdf";

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        logContext.debug(this, "Enter executeBiz() method.");
        AttachmentDownloadContract attachContract = (AttachmentDownloadContract)contract;
        

        String downloadType = attachContract.getDownloadType();
        
        logContext.debug(this, "Begin to execute web service.");
        CMServiceHelper service = new CMServiceHelper();
        StringBuffer mimeType = new StringBuffer();
        StringBuffer fileName = new StringBuffer();
        File content = null;
        try
        {
	        if(downloadType != null && downloadType.equals(SubmittedQuoteParamKeys.QUOTE_OUTPUT)){
	            String sapDocId = attachContract.getSapDocId();
	            if(sapDocId == null || "".equals(sapDocId)){
	                logContext.error(this, "Error! Must provide a sap document id for retrieving.");
	                throw new QuoteException("Must provide a sap document id for retrieving.");
	            }
	            logContext.debug(this, "Sap document id: " + sapDocId);
	            content = service.executeRetrieveQuoteOutput(sapDocId, mimeType, false, attachContract.getItemType());
	            fileName.append(attachContract.getOutputName()).append(" ")
	            	.append(attachContract.getSapQuoteNum())
	            	.append(QUOTE_OUTPUT_FILE_SUFFIX);
	        }else{
	            String attchmtSeqNum = attachContract.getAttchmtSeqNum();
	            if(attchmtSeqNum == null || "".equals(attchmtSeqNum)){
	                logContext.error(this, "Error! Must provide an attachment sequence number for retrieving.");
	                throw new QuoteException("Must provide an attachment sequence number for retrieving.");
	            }
	            logContext.debug(this, "Attachment sequence number: " + attchmtSeqNum);
	            boolean unzipFlag = this.checkUnZip(attchmtSeqNum);
	            content = service.executeRetrieve(attchmtSeqNum, mimeType, fileName, unzipFlag);
	        }
	        logContext.debug(this, "Finished executing web service.");
	        
	        handler.addObject(SubmittedQuoteParamKeys.PARAM_ATTACHMENT, content);
	        handler.addObject(SubmittedQuoteParamKeys.PARAM_MIME_TYPE, mimeType.toString());
	        handler.addObject(SubmittedQuoteParamKeys.PARAM_FILE_NAME, fileName.toString());
	        
	        logContext.debug(this, "Set state to: " + SubmittedQuoteStateKeys.STATE_RETRIEVE_ATTACHMENT);
	        handler.setState(SubmittedQuoteStateKeys.STATE_RETRIEVE_ATTACHMENT);
	        
	        logContext.debug(this, "Exit executeBiz() method.");
        }
        catch ( TopazException e )
        {
            logContext.error(this, e.getMessage());
            if ( content != null )
            {
                content.delete();
            }
            throw new QuoteException(e);
        }
        catch ( QuoteException e )
        {
            logContext.error(this, e.getMessage());
            if ( content != null )
            {
                content.delete();
            }
            throw e;
        }
        return handler.getResultBean();
    }
    
    boolean checkUnZip( String attchmtSeqNum) throws TopazException, QuoteException
    {
        QuoteAttachment att = QuoteAttachmentProcessFactory.singleton().create().getQuoteAttachmentInfo(attchmtSeqNum);
        logContext.debug(this, "get att from db: " + att);
        if (att == null) {
            return false;
        }
        logContext.debug(this, "att cmprssd file flag from db: "
                + att.isCmprssdFileFlag());
        return att.isCmprssdFileFlag();
    }
}
