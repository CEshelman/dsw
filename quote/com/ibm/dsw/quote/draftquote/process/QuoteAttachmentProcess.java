/*
 * Created on 2007-5-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface QuoteAttachmentProcess {
    
    public static final int SAVE_TO_CM_MAX_RETRY = 3;
    
    /**
     * Save the files to Content Management Server, 
     * and then automatically delete the uploaded 
     * temporary file in the app server.
     * @param doucments files collection. 
     * 		Element type: {@link com.ibm.dsw.quote.common.domain.CompressedQuoteAttachment}
     * @throws QuoteException
     */
    public void processQuoteAttachments(List doucments)throws QuoteException;
    
    /**
     * Save the files to Content Management Server,
     * and then judge whether delete the uploaded temporary file 
     * in the app server by the value of parameter <code>autoCleanTempFile</code>.
     * @param doucments doucments files collection. 
     * 		Element type: {@link com.ibm.dsw.quote.common.domain.CompressedQuoteAttachment}
     * @param autoCleanTempFile indicate whether delete the uploaded temporary file. true - delete; false - keep.
     * @throws QuoteException
     */
    public void processQuoteAttachments(List doucments, boolean autoCleanTempFile)throws QuoteException;
    
    public List getRQSalesCommentAttachments(String webQuoteNum) throws QuoteException;
    
    public void removeQuoteAttachment(String webQuoteNum, String attchSeqNum) throws QuoteException;
    
    public void removeQuoteAttachment(String webQuoteNum, String attchSeqNum, String userId) throws QuoteException;

    public QuoteAttachment getQuoteAttachmentInfo(String attchSeqNum) throws QuoteException;
    
}
