/*
 * Created on 2007-5-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.activation.DataSource;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.CompressedQuoteAttachment;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.common.domain.QuoteAttachmentFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.service.CMServiceHelper;
import com.ibm.dsw.quote.draftquote.util.MimeTypeUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;
import com.ibm.ws.util.UUID;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuoteAttachmentProcess_Impl extends TopazTransactionalProcess implements QuoteAttachmentProcess {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidFinalProcess#processJustificationDocuments()
     */
    public void processQuoteAttachments(List documents, boolean autoCleanTempFile) throws QuoteException {

        LogContext logger = LogContextFactory.singleton().getLogContext();
        
        logger.debug(this,"Begin to process SpecialBid documents.");
        
        if(documents == null || documents.size() == 0){
            logger.debug(this,"No documents to process, return.");
            return;
        }
        for(int i=0;i<documents.size();i++){
            CompressedQuoteAttachment file = (CompressedQuoteAttachment)documents.get(i);
            if ( file.getMessage() != null )
            {
            	logger.info(this, "skip this file: " + file.getFileName() + "; " + file.getMessage());
            	continue;
            }
			if (!file.isCmprssdFileFlag()) {
				file.compress();
			}
            try {
               // this.beginTransaction();
                if(!file.isUploaded()){
                    continue;
                }
                file.setId(new UUID().toString());
                file.setStageCode(QuoteAttachment.STATE_PENDING);
                file.setSavedToCM(false);
                logger.debug(this,"Processing document: "+file.getFileName());
                if(!saveOrUpdateWebAttechment(file)){
                    logger.info(this,"Failed to set document status to \"PENDING\" for file: "+file.getFileName());
                    continue;
                }
                //create temp zipped file
                
                for(int j = 1; j<=SAVE_TO_CM_MAX_RETRY;j++){
                    logger.info(this,"["+j+"] Storing document: "+file.getFileName()+" to CM");
                    if(!saveDocumentToCM(file)){
                        if(j == SAVE_TO_CM_MAX_RETRY){
                            file.setStageCode(QuoteAttachment.STATE_FAILED);
                            if(!saveOrUpdateWebAttechment(file)){
                                logger.info(this, "Failed to set document status to \"FAILED\" for file: " + file.getFileName());
                            }
                        }
                    }else{
                        logger.debug(this,"Document: "+file.getFileName()+" has been stored to CM");
                        file.setStageCode(QuoteAttachment.STATE_LOADED);
                        file.setSavedToCM(true);
                        if(!saveOrUpdateWebAttechment(file)){
                            logger.info(this,"Failed to set document status to \"LOADED\" for file: "+file.getFileName());
                        }
                        break;
                    }
                }
                /*--
                 * Delete the old data(previously saved ToU Attachment), to avoid the redundancy of record 
                 * in ebiz1.web_quote_attchmt.
                 * To fix PL:HLSN-9A7587
                 */
                if(StringUtils.isNotEmpty(file.getAttchmtSeqNum())){
                	removeQuoteAttachment(file.getQuoteNumber(), file.getAttchmtSeqNum());
                }
                
            } catch (Throwable t) {
                logger.error(this, "Error occurred when trying to upload " + file.getAddByUserName());
                logger.error(this, t.getMessage());
                file.setStageCode(QuoteAttachment.STATE_FAILED);
            } finally {
            	if(autoCleanTempFile){//determine if need to delete the uploaded temporary file.
            		file.getAttchmt().delete();
            	}
            	if (!file.isCmprssdFileFlag()) {
            		file.getCompressedFile().delete();
            	}
            }
        }
    }
    
    @Override
	public void processQuoteAttachments(List documents) throws QuoteException {
    	processQuoteAttachments(documents, true);
	}

	class AttachmentDataSource implements DataSource
    {
        InputStream in = null;
        String contentType = null;
        String name = null;
        public AttachmentDataSource(String contentType, String name, InputStream in)
        {
            this.contentType = contentType;
            this.name = name;
            this.in = in;
        }
        public InputStream getInputStream() throws IOException {
            return in;
        }

        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        public String getContentType() {
            return contentType;
        }

        public String getName() {
            return name;
        }
        
    }
    
    protected boolean saveDocumentToCM(CompressedQuoteAttachment file) throws FileNotFoundException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        String quoteNum = file.getQuoteNumber();
        String seqNum = file.getId();
        final String fileName = file.getFileName();
        String fileSize = Long.toString(file.getCompressedSize());
        Date addDate = new Date();
        String addByUser = file.getUploaderEmail();
                
		int start = fileName.lastIndexOf(".");
		String suffix = fileName.substring(start+1, fileName.length());
		final String mimeType = MimeTypeUtil.getMimeTypeBySuffix(suffix);
		
		AttachmentDataSource ds = new AttachmentDataSource(MimeTypeUtil.getMimeTypeBySuffix(suffix), file.getUploadPath(), new FileInputStream(file.getCompressedFile()));
		logger.debug(this, "compressed file name: " + file.getCompressedFile().getAbsolutePath());
        CMServiceHelper helper = new CMServiceHelper();
		return helper.executeStore(quoteNum, seqNum, fileName, fileSize, addDate, addByUser, ds, mimeType);
    }

    
    protected boolean saveOrUpdateWebAttechment(QuoteAttachment file){
        throw new  UnsupportedOperationException("Method or operation not implemented!");
    }
    
    public List getRQSalesCommentAttachments(String webQuoteNum) throws QuoteException {
        List attachments = null;
        
        try {
            //begin the transaction
            this.beginTransaction();
            
            attachments = QuoteAttachmentFactory.singleton().getRQSalesCommentAttachments(webQuoteNum);

            //commit the transaction
            this.commitTransaction();
            
        } catch (NoDataException nde) {
            return null;
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        
        return attachments;
    }
    
    public void removeQuoteAttachment(String webQuoteNum, String attchSeqNum) throws QuoteException {
        this.removeQuoteAttachment(webQuoteNum, attchSeqNum, null);
    }


    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess#removeQuoteAttachment(java.lang.String, java.lang.String, java.lang.String)
     */
    public void removeQuoteAttachment(String webQuoteNum, String attchSeqNum, String userId) throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        logger.debug(this,"Begin to delete quote attchment " + attchSeqNum);
        logger.debug(this, "begin to get ref num");
        int refNum = 0;
        try
        {
	        this.beginTransaction();
	        try
	        {
	            refNum = QuoteAttachmentFactory.singleton().getAttchmtRefNum(attchSeqNum);
	        }
	        catch ( TopazException e )
	        {
	            logger.error(this, e);
	            throw new QuoteException(e.toString());
	        }
	        boolean deleteFromCM = false;
	        if ( refNum == 0 )
	        {
	        	logger.info(this, "The attach is not exists in quote db: " + attchSeqNum);
	        	return;
	        }
	        else if ( refNum > 1 )
	        {
	            deleteFromCM = true;
	            logger.debug(this, "refNum > 1, only delete data in db");
	        }
	        else
	        {
	            logger.debug(this, "refNum <= 1, begin delete cm");
	            try
	            {
	                CMServiceHelper helper = new CMServiceHelper();
	                for ( int i = 0; i < SAVE_TO_CM_MAX_RETRY; i++ )
	                {
	                    deleteFromCM = helper.deleteDocument(attchSeqNum);
	                    if ( deleteFromCM )
	                    {
	                        break;
	                    }
	                }
	            }
	            catch ( Throwable t )
	            {
	                logger.error(this, t);
	            }
	        }
	        
	        
	        try {
	            //begin the transaction
	            logger.debug(this, "begin to delete data in db: " + deleteFromCM);
	            if ( deleteFromCM )
	            {
	                //delete from db
	                QuoteAttachmentFactory.singleton().removeQuoteAttachment(webQuoteNum, attchSeqNum, userId, 0);
	            }
	            else
	            {
	                //update stage code to DEL_FAIL
	                QuoteAttachmentFactory.singleton().removeQuoteAttachment(webQuoteNum, attchSeqNum, userId, 1); 
	            }
	            //commit the transaction
	            this.commitTransaction();
	        }
	        catch ( TopazException tce )
	        {
	        	throw tce;
	        }
        }
        catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
    }
    
    public QuoteAttachment getQuoteAttachmentInfo(String attchSeqNum) throws QuoteException {
        QuoteAttachment att = null;
        try {
            //begin the transaction
           this.beginTransaction();
           att = QuoteAttachmentFactory.singleton().getQuoteAttachmentInfo(attchSeqNum);
           this.commitTransaction();

        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return att;
    }
   
}


