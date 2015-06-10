/*
 * Created on 2007-5-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import com.ibm.dsw.cmi.CMDocumentType;
import com.ibm.dsw.cmi.CMWIHttpService;
import com.ibm.dsw.cmi.ItemType;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author helen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CMServiceHelper {
    
    /**
	* execute method to contact the CM SCA Module and store the Special bid attachment Doc to CM
	* 
	* Created By : Helen
	*
	* @param web quote number
	* @param attachment sequence number
	* @param attachment file name
	* @param attachment file size
	* @param attachment add date
	* @param attachment add by user name
	* @param attachment doc data source
	* @param attachment mime type
	* @return boolean indicate if the attachment is successfully stored to CM
	*/
    public boolean executeStore(String quoteNum, String attchmtSeqNum, final String attchmtFileName, String attchmtFileSize, Date addDate, String addByUser, DataSource ds, final String mimeType){
        LogContext log = LogContextFactory.singleton().getLogContext();
//        log.debug(this, "begin check memory use: " + (Runtime.getRuntime().freeMemory() / 1024) + ", " + attchmtFileName);
	    boolean isSuccess = false;
	    
	    log.debug(this, "Enter executeStore() method.");
	    
	    log.debug(this, "Input quote number is: " + quoteNum);
	    log.debug(this, "Input attachment sequence number is: " + attchmtSeqNum);
	    log.debug(this, "Input attachment file name is: " + attchmtFileName);
	    log.debug(this, "Input attachment file size is: " + attchmtFileSize);
	    log.debug(this, "Input add date is: " + addDate.toString());
	    log.debug(this, "Input add by user name is: " + addByUser);
	    log.debug(this, "Input mime type is: " + mimeType);
	    
		CMDocumentType sync = new CMDocumentType();
		CMDocumentType ack = null;
		try {
		    log.debug(this, "Begin to execute web service. ---syncCMDocuments()");
			ServiceLocator sLoc = new ServiceLocator();
			CMWIHttpService port = (CMWIHttpService) sLoc.getServicePort(CommonServiceConstants.CONTENT_MANAGER_BINDING, CMWIHttpService.class);
			
			sync.setItemType(ItemType.QUOTE_ITEM);
			sync.setWebQuoteNum(quoteNum);
			sync.setAttchmtSeqNum(attchmtSeqNum);
			sync.setAttchmtFileName(attchmtFileName);
			sync.setAttchmtFileSize(attchmtFileSize);
			Calendar c = new GregorianCalendar();
			c.setTime(addDate);
			sync.setAddDate(c);
			sync.setAddByUserName(addByUser);
			sync.setMimeType(mimeType);
			
			DataHandler dh = new DataHandler(ds);
			sync.setDocument(dh);
			
			ack = port.syncCMDocuments(sync, dh);
			
			if(ack != null){
			    if(ack.getStatusCode().intValue() == 0){
			        isSuccess = true;
			        log.debug(this, "Finished executing web service. Returned with status of " + ack.getStatusCode());
			    }else if(ack.getStatusCode().intValue() == -1){//error
			        log.debug(this, "CM System occurs an error. CM returns -1. Detail info: " + ack.getMessage());
			    }else if(ack.getStatusCode().intValue() == 1){//items found
			        log.debug(this, "Item already exist in Content Manager. CM returns 1. Detail info: " + ack.getMessage());
			    }else if(ack.getStatusCode().intValue() == 2){//param incorrect
			        log.debug(this, "Invalid Parameter(s). CM returns 2. Detail info: " + ack.getMessage());
			    }
			}
		} catch (RemoteException e) {
			log.error(this, e, "RemoteException: " + e.toString());
		} catch (ServiceLocatorException e) {
			log.error(this, e, "ServiceLocatorException: " + e.toString());
		} catch (Exception e) {
			log.error(this, e, "Exception: " + e.toString());
		}
		log.debug(this, "Exit executeStore() method.");
//		log.error(this, "after save to cm check memory use: " + (Runtime.getRuntime().freeMemory() / 1024));
		return isSuccess;		
	}

    /**
	* execute method to contact the CM SCA Module and store the Special bid attachment Doc to CM
	* 
	* Created By : Helen
	*
	* @param web quote number
	* @param attachment sequence number
	* @param attachment file name
	* @param attachment file size
	* @param attachment add date
	* @param attachment add by user name
	* @param attachment doc
	* @param attachment mime type
	* @return boolean indicate if the attachment is successfully stored to CM
	*/
//	public boolean executeStore(String quoteNum, String attchmtSeqNum, final String attchmtFileName, String attchmtFileSize, Date addDate, String addByUser, final byte[] resource, final String mimeType){
//	    
//	    LogContext log = LogContextFactory.singleton().getLogContext();
//	    
//	    boolean isSuccess = false;
//	    
//	    log.info(this, "Enter executeStore() method.");
//	    
//	    log.debug(this, "Input quote number is: " + quoteNum);
//	    log.debug(this, "Input attachment sequence number is: " + attchmtSeqNum);
//	    log.debug(this, "Input attachment file name is: " + attchmtFileName);
//	    log.debug(this, "Input attachment file size is: " + attchmtFileSize);
//	    log.debug(this, "Input add date is: " + addDate.toString());
//	    log.debug(this, "Input add by user name is: " + addByUser);
//	    log.debug(this, "Input attachment file length is: " + resource.length);
//	    log.debug(this, "Input mime type is: " + mimeType);
//	    
//		CMDocumentType sync = new CMDocumentType();
//		CMDocumentType ack = null;
//		try {
//		    log.info(this, "Begin to execute web service. ---syncCMDocuments()");
//			ServiceLocator sLoc = new ServiceLocator();
//			CMWIHttpService port = (CMWIHttpService) sLoc.getServicePort(CommonServiceConstants.CONTENT_MANAGER_BINDING, CMWIHttpService.class);
//			
//			sync.setItemType(ItemType.QUOTE_ITEM);
//			sync.setWebQuoteNum(quoteNum);
//			sync.setAttchmtSeqNum(attchmtSeqNum);
//			sync.setAttchmtFileName(attchmtFileName);
//			sync.setAttchmtFileSize(attchmtFileSize);
//			Calendar c = new GregorianCalendar();
//			c.setTime(addDate);
//			sync.setAddDate(c);
//			sync.setAddByUserName(addByUser);
//			sync.setMimeType(mimeType);
//			
//			
//			DataSource ds = new DataSource(){
//				public InputStream getInputStream(){				
//					return new ByteArrayInputStream(resource) ;
//				}
//				
//				public OutputStream getOutputStream(){
//					return null;
//				}
//				
//				public String getContentType(){
//					return mimeType;
//				}
//				
//				public String getName(){
//					return attchmtFileName;
//				}
//			};
//			DataHandler dh = new DataHandler(ds);
//			sync.setDocument(dh);
//			
//			ack = port.syncCMDocuments(sync, dh);
//			
//			if(ack != null){
//			    if(ack.getStatusCode().intValue() == 0){
//			        isSuccess = true;
//			        log.info(this, "Finished executing web service. Returned with status of " + ack.getStatusCode());
//			    }else if(ack.getStatusCode().intValue() == -1){//error
//			        log.info(this, "CM System occurs an error. CM returns -1. Detail info: " + ack.getMessage());
//			    }else if(ack.getStatusCode().intValue() == 1){//items found
//			        log.info(this, "Item already exist in Content Manager. CM returns 1. Detail info: " + ack.getMessage());
//			    }else if(ack.getStatusCode().intValue() == 2){//param incorrect
//			        log.info(this, "Invalid Parameter(s). CM returns 2. Detail info: " + ack.getMessage());
//			    }
//			}
//		} catch (RemoteException e) {
//			log.error(this, e, "RemoteException: " + e.toString());
//		} catch (ServiceLocatorException e) {
//			log.error(this, e, "ServiceLocatorException: " + e.toString());
//		} catch (Exception e) {
//			log.error(this, e, "Exception: " + e.toString());
//		}
//		log.debug(this, "Exit executeStore() method.");
//		return isSuccess;		
//	}
	
	/**
	* execute method to contact the CM SCA Module and retrieve the Special bid attachment Doc
	* 
	* Created By : Helen
	*
	* @param attachment sequence number
	* @param attachment file's mime type
	* @param attachment file's name
	* @return attachment doc
	* @throws QuoteException
	*/
	public File executeRetrieve(String attchmtSeqNum, StringBuffer mimeType, StringBuffer fileName, boolean unzipFlag) throws QuoteException {

	    LogContext log = LogContextFactory.singleton().getLogContext();
	    
	    log.debug(this, "Enter executeRetrieve() method.");
	    
	    log.debug(this, "Input attachment sequence number is: " + attchmtSeqNum);
	    
		CMDocumentType GetCMDocument = new CMDocumentType();
		CMDocumentType ShowCMDocument = null;
		int cmStatus = Integer.MAX_VALUE;
		File fp = null;
		try {
		    log.debug(this, "Begin to execute web service. ---getCMDocuments()");
			ServiceLocator sLoc = new ServiceLocator();
			CMWIHttpService port = (CMWIHttpService) sLoc.getServicePort(CommonServiceConstants.CONTENT_MANAGER_BINDING, CMWIHttpService.class);			
			
			GetCMDocument.setItemType(ItemType.QUOTE_ITEM);
			GetCMDocument.setAttchmtSeqNum(attchmtSeqNum);
			
			ShowCMDocument = port.getCMDocuments(GetCMDocument, null);
			
			if(ShowCMDocument != null){
			    
			    mimeType.append(ShowCMDocument.getMimeType());
			    fileName.append(ShowCMDocument.getAttchmtFileName());
				log.debug(this, "Finished executing web service. Returned with status of " + ShowCMDocument.getStatusCode());
				log.debug(this, "The mime type of the attachment doc is: " + mimeType.toString());
				log.debug(this, "The file name of the attachment doc is: " + fileName.toString());
				
				if(ShowCMDocument.getStatusCode().intValue() == -1) {//error
					cmStatus = -1;
				    log.error(this, "CM System occurs an error. CM returns -1. Detail info: " + ShowCMDocument.getMessage());
				    throw new QuoteException("Failed to retrieve special bid attachment " + attchmtSeqNum + ". CM returns -1.");
				}else if(ShowCMDocument.getStatusCode().intValue() == 1){//items not found
					cmStatus = 1;
				    log.error(this, "Content Manager doesn't have what you need. CM returns 1. Detail info: " + ShowCMDocument.getMessage());
				    throw new QuoteException("Failed to retrieve special bid attachment " + attchmtSeqNum + ". CM returns 1.");
				}else if (ShowCMDocument.getStatusCode().intValue() == 2) {//param incorrect
					cmStatus = 2;
					log.error(this, "Invalid Parameter(s). CM returns 2. Detail info: " + ShowCMDocument.getMessage());
				    throw new QuoteException("Failed to retrieve special bid attachment " + attchmtSeqNum + ". CM returns 2.");
				}
				
				if(ShowCMDocument.getDocument()!=null){
				    InputStream in = ShowCMDocument.getDocument().getInputStream();			
					return this.saveToFile(in, unzipFlag);
				}
				
			}
		} catch (RemoteException e) {
			log.error(this, e, "RemoteException: " + e.toString());
			throw new QuoteException("Failed to retrieve special bid attachment " + attchmtSeqNum + ". RemoteException occurs.");
		} catch (ServiceLocatorException e) {
			log.error(this, e, "ServiceLocatorException: " + e.toString());
			throw new QuoteException("Failed to retrieve special bid attachment " + attchmtSeqNum + ". ServiceLocatorException occurs.");
		} catch (QuoteException e) {
			if(cmStatus == 1){
				throw new QuoteException(e.getMessage(), "msg_quote_attachment_not_exist", e);
			}
			else if(cmStatus != Integer.MAX_VALUE){
				throw new QuoteException(e.getMessage(), "msg_quote_attachment_unavailable", e);
			}
		} catch (Exception e) {
			log.error(this, e, "Exception: " + e.toString());
			throw new QuoteException("Failed to retrieve special bid attachment " + attchmtSeqNum + ". Exception occurs.");
		}
		log.debug(this, "Exit executeRetrieve() method.");
		return fp;		
	}
	
	
	public boolean deleteDocument(String attchmtSeqNum)
	{
	    LogContext log = LogContextFactory.singleton().getLogContext();
	    log.debug(this, "begin to delete attach " + attchmtSeqNum);
	    CMDocumentType cmDoc = new CMDocumentType();
	    cmDoc.setItemType(ItemType.QUOTE_ITEM);
	    cmDoc.setAttchmtSeqNum(attchmtSeqNum);
	    try
	    {
	        ServiceLocator sLoc = new ServiceLocator();
	        CMWIHttpService port = (CMWIHttpService) sLoc.getServicePort(CommonServiceConstants.CONTENT_MANAGER_BINDING, CMWIHttpService.class);
	        CMDocumentType[] arr = new CMDocumentType[1];
	        arr[0] = cmDoc;
	        CMDocumentType[] res = port.deleteCMDocuments(arr);
	        if ( res == null || res.length == 0 )
	        {
	            log.info(this, "delete attach " + attchmtSeqNum + ", return arr is empty");
	            return false;
	        }
	        int retCode = res[0].getStatusCode().intValue();
	        log.debug(this, "delete atach " + attchmtSeqNum + ", return code is " + retCode);
	        if ( retCode == 0 || retCode == 1 )
	        {
	            return true;
	        }
	        log.info(this, "delete attach " + attchmtSeqNum + ", return code is:" + retCode + ", message is: " + res[0].getMessage());
	        return false;
	    }
	    catch (RemoteException e) {
			log.error(this, e, "RemoteException: " + e.toString());
		} catch (ServiceLocatorException e) {
			log.error(this, e, "ServiceLocatorException: " + e.toString());
		} catch (Throwable e) {
			log.error(this, e, "Exception: " + e.toString());
		}
		return false;
	}
	/**
	* execute method to contact the CM SCA Module and retrieve the Special bid attachment Doc
	* 
	* Created By : Helen
	*
	* @param attachment sequence number
	* @param attachment file's mime type
	* @param attachment file's name
	* @return attachment doc
	* @throws QuoteException
	*/
	public File executeRetrieveQuoteOutput(String sapDocId, StringBuffer mimeType, boolean unzipFlag, String itemType) throws QuoteException {

	    LogContext log = LogContextFactory.singleton().getLogContext();
	    
	    log.debug(this, "Input SAP document id is: " + sapDocId);
	    
		CMDocumentType GetCMDocument = new CMDocumentType();
		CMDocumentType ShowCMDocument = null;
		File fp = null;
		int cmStatus = Integer.MAX_VALUE;
		try {
			ServiceLocator sLoc = new ServiceLocator();
			CMWIHttpService port = (CMWIHttpService) sLoc.getServicePort(CommonServiceConstants.CONTENT_MANAGER_BINDING, CMWIHttpService.class);			
			
		    if(itemType != null && itemType.equals(ItemType._UNSIGNED_TNC_ITEM)){
		    	GetCMDocument.setItemType(ItemType.UNSIGNED_TNC_ITEM);
		    }else{
		    	GetCMDocument.setItemType(ItemType.ARD_ITEM);
		    }
		    
			GetCMDocument.setSAPDocId(sapDocId);
			
			ShowCMDocument = port.getCMDocuments(GetCMDocument, null);
			
			if(ShowCMDocument != null){
			    
			    mimeType.append(ShowCMDocument.getMimeType());
			    
				if(ShowCMDocument.getStatusCode().intValue() == -1) {//error
					cmStatus = -1;
				    log.error(this, "CM System occurs an error. CM returns -1. Detail info: " + ShowCMDocument.getMessage());
				    throw new QuoteException("Failed to retrieve quote output, SAP document id " + sapDocId + ". CM returns -1.");
				}else if(ShowCMDocument.getStatusCode().intValue() == 1){//items not found
					cmStatus = 1;
				    log.error(this, "Content Manager doesn't have what you need. CM returns 1. Detail info: " + ShowCMDocument.getMessage());
				    throw new QuoteException("Failed to retrieve quote output, SAP document id " + sapDocId + ". CM returns 1.");
				}else if (ShowCMDocument.getStatusCode().intValue() == 2) {//param incorrect
					cmStatus = 2;
					log.error(this, "Invalid Parameter(s). CM returns 2. Detail info: " + ShowCMDocument.getMessage());
				    throw new QuoteException("Failed to retrieve quote output, SAP document id " + sapDocId + ". CM returns 2.");
				}
				
				if(ShowCMDocument.getDocument()!=null){
				    InputStream in = ShowCMDocument.getDocument().getInputStream();			
					fp = saveToFile(in, unzipFlag);
				}
				
			}
		} catch (RemoteException e) {
			log.error(this,"RemoteException: " + e.toString());
			throw new QuoteException("Failed to retrieve quote output, SAP document id " + sapDocId);
		} catch (ServiceLocatorException e) {
			log.error(this,"ServiceLocatorException: " + e.toString());
			throw new QuoteException("Failed to retrieve quote output, SAP document id " + sapDocId);
		} catch (QuoteException e) {
			if(cmStatus == 1){
				throw new QuoteException(e.getMessage(), "msg_quote_output_unavailable", e);
			}
			else if(cmStatus != Integer.MAX_VALUE){
				throw new QuoteException(e.getMessage(), "msg_quote_attachment_unavailable", e);
			}
		} catch (Exception e) {
			log.error(this,"Exception: " + e.toString());
			throw new QuoteException("Failed to retrieve quote output, SAP document id " + sapDocId);
		}
		log.debug(this, "Exit executeRetrieveQuoteOutput() method.");
		return fp;		
	}
	
	protected File saveToFile(InputStream in, boolean unzipFlag) throws IOException
	{
	    LogContext log = LogContextFactory.singleton().getLogContext();
	    log.debug(this, "unzipflag=" + unzipFlag);
	    File fp = File.createTempFile("att", ".tmp", new File(ApplicationProperties.getInstance().getUploadFileDir()));
	    log.debug(this, "tmp file path: " + fp.getAbsolutePath());
	    BufferedInputStream buffIn = null;
	    if ( unzipFlag )
	    {
	        ZipInputStream zipIns = new ZipInputStream(in);
	        ZipEntry entry = zipIns.getNextEntry();
	        buffIn = new BufferedInputStream(zipIns);
	    }
	    else
	    {
	        buffIn = new BufferedInputStream(in); 
	    }
	    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fp));
	    int ch = -1;
	    while ( (ch = buffIn.read()) != -1 )
	    {
	        out.write(ch);
	    }
	    out.flush();
	    out.close();
	    buffIn.close();
	    return fp;
	}
}
