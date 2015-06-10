/*
 * Created on 2007-5-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.domain;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuoteAttachment implements Serializable{
    
    private static final long serialVersionUID = -6569569784804744107L;
	public static final String STATE_PENDING = "CM_PENDG";
    public static final String STATE_LOADED = "CM_LOADED";
    public static final String STATE_FAILED = "CM_FAILED";
    
    private String id;
    private String quoteNumber;
    private String attchmtSeqNum;
    private String uploaderEmail;
    private String stageCode;
    private String fileName;
    private String uploadPath;
    private boolean isUploaded;
    private boolean isSavedToCM;
    private String message;
    private long fileSize;
    private transient File attchmt;
    private String secId;
    private Date addDate;
    private String addByUserName;
    private String classfctnCode;
    
    private String removeURL;
    private String downloadURL;
    private boolean cmprssdFileFlag = false;
    
    private boolean overLastApproveAction = true;
    
    private String removeAjaxURL;
	private String touURL;
	private String touName;
    
    public String getRemoveAjaxURL()
	{
		return removeAjaxURL;
	}
	public void setRemoveAjaxURL(String removeAjaxURL)
	{
		this.removeAjaxURL = removeAjaxURL;
	}
	/**
     * @return Returns the sectionSeq.
     */
    public String getSecId() {
        return secId;
    }
    /**
     * @param sectionSeq The sectionSeq to set.
     */
    public void setSecId(String secId) {
        this.secId = secId;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getQuoteNumber() {
        return quoteNumber;
    }
    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }
    public String getUploaderEmail() {
        return uploaderEmail;
    }
    public void setUploaderEmail(String uploaderEmail) {
        this.uploaderEmail = uploaderEmail;
    }
    public String getStageCode() {
        return stageCode;
    }
    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public boolean isSavedToCM() {
        return isSavedToCM;
    }
    public void setSavedToCM(boolean isSavedToCM) {
        this.isSavedToCM = isSavedToCM;
    }
    public boolean isUploaded() {
        return isUploaded;
    }
    public void setUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getUploadPath() {
        return uploadPath;
    }
    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }
    
    /**
     * @return Returns the fileSize.
     */
    public long getFileSize() {
        return fileSize;
    }
    /**
     * @param fileSize The fileSize to set.
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    /**
     * @return Returns the attchmt.
     */
    public File getAttchmt() {
        return attchmt;
    }
    /**
     * @param attchmt The attchmt to set.
     */
    public void setAttchmt(File attchmt) {
        this.attchmt = attchmt;
    }
    /**
     * @return Returns the removeURL.
     */
    public String getRemoveURL() {
        return removeURL;
    }
    /**
     * @param removeURL The removeURL to set.
     */
    public void setRemoveURL(String removeURL) {
        this.removeURL = removeURL;
    }
    /**
     * @return Returns the downloadURL.
     */
    public String getDownloadURL() {
        return downloadURL;
    }
    /**
     * @param downloadURL The downloadURL to set.
     */
    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }
    
    public Date getAddDate() {
        return addDate;
    }
    
    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
    
    public String getAddByUserName() {
        return addByUserName;
    }
    
    public void setAddByUserName(String addByUserName) {
        this.addByUserName = addByUserName;
    }
    
    public boolean isCMLoaded() {
        return STATE_LOADED.equalsIgnoreCase(stageCode);
    }
    
    public boolean isCMPending() {
        return STATE_PENDING.equalsIgnoreCase(stageCode);
    }
    
    public boolean isCMFailed() {
        return STATE_FAILED.equalsIgnoreCase(stageCode);
    }
    
    public String getClassfctnCode() {
        return classfctnCode;
    }
    
    public void setClassfctnCode(String classfctnCode) {
        this.classfctnCode = classfctnCode;
    }
    /**
     * @return Returns the overLastApproveAction.
     */
    public boolean isOverLastApproveAction() {
        return overLastApproveAction;
    }
    /**
     * @param overLastApproveAction The overLastApproveAction to set.
     */
    public void setOverLastApproveAction(boolean overLastApproveAction) {
        this.overLastApproveAction = overLastApproveAction;
    }
    public boolean isCmprssdFileFlag() {
        return cmprssdFileFlag;
    }
    public void setCmprssdFileFlag(boolean cmprssdFileFlag) {
        this.cmprssdFileFlag = cmprssdFileFlag;
    }
	public String getTouURL() {
		return touURL;
	}
	public void setTouURL(String touURL) {
		this.touURL = touURL;
	}
	public String getTouName() {
		return touName;
	}
	public void setTouName(String touName) {
		this.touName = touName;
	}
	public String getAttchmtSeqNum() {
		return attchmtSeqNum;
	}
	public void setAttchmtSeqNum(String attchmtSeqNum) {
		this.attchmtSeqNum = attchmtSeqNum;
	}
	
}
