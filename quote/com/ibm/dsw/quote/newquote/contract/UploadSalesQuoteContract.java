
package com.ibm.dsw.quote.newquote.contract;

import java.io.File;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.jade.util.UploadFile;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>UploadSalesQuoteContract</code> retrieves uploaded file from
 * JADE Parameters ojbect. 
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: Mar 07, 2007
 */

public class UploadSalesQuoteContract extends QuoteBaseContract implements DraftQuoteParamKeys {
	
	private transient File uploadedFile = null;
	
	private String uploadOption = null;
	
	public UploadSalesQuoteContract (){
	    super();
	}
	
   	 public void load(Parameters parameters, JadeSession session) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();       
   	    super.load(parameters, session);
        Object obj = parameters.getParameter(NewQuoteParamKeys.UPLOAD_FILE);
        
        if(obj != null && (obj instanceof UploadFile)){
            this.uploadedFile = ((UploadFile)obj).getFile();
        }else {
            logContext.info(this, "No file in coming request. " + obj);
            this.uploadedFile = null;
        }
        logContext.debug(this, super.toString());
    }
	 
	/**
	 * @return Returns the uploadedFile.
	 */
	public File getUploadedFile() {
		return uploadedFile;
	}
	
    /**
     * @return Returns the uploadOption.
     */
    public String getUploadOption() {
        return uploadOption;
    }
    /**
     * @param uploadOption The uploadOption to set.
     */
    public void setUploadOption(String uploadOption) {
        this.uploadOption = uploadOption;
    }
}
