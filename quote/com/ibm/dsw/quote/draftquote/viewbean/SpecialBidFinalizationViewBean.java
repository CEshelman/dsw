/*
 * Created on 2007-4-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.viewbean.QuoteViewBean;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpecialBidFinalizationViewBean extends QuoteViewBean{
    
    private transient List filesFailedToCommitToCMWI;
    
    protected Quote quote = null;
    
    protected transient QuoteHeader qh = null;
    
    private String webQuoteNum;
    
    private String sapDocNumber;
    
    private boolean isNewCustomer;
    
    private String passcode;
  
    private boolean isShowErrorEmailMsgFlag = false;    
    
    private boolean isCpFromApprvdBid = false;
    
    private boolean isCopied4PrcIncrQuoteFlag = false;
    
    protected boolean supprsPARegstrnEmailFlag = false;
    

    private boolean isCopiedForOutputChangeFlag = false;
    
    
    public void collectResults(Parameters params) throws ViewBeanException {
        
        filesFailedToCommitToCMWI = new ArrayList();
        
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        
        List files = (List)params.getParameter(DraftQuoteParamKeys.PARAM_ATTACHMENTS);
        
        Locale locale = (Locale)params.getParameter(ParamKeys.PARAM_LOCAL);
        
        quote = (Quote) params.getParameter(ParamKeys.PARAM_QUOTE_OBJECT);
        if(null != params.getParameter(ParamKeys.PARAM_SHOW_EMAIL_MSG_FLAG)){
        	isShowErrorEmailMsgFlag = (Boolean)params.getParameter(ParamKeys.PARAM_SHOW_EMAIL_MSG_FLAG);
        }
        if (quote == null || quote.getQuoteHeader() == null)
            return;
               
        qh = quote.getQuoteHeader();
        
        webQuoteNum = qh.getWebQuoteNum();
        sapDocNumber = qh.getSapIntrmdiatDocNum();
        isCpFromApprvdBid = qh.isCopied4ReslChangeFlag();
        
        Customer cust = quote.getCustomer();
        
        if (cust != null && cust.getWebCustId()>0) {
            isNewCustomer = true;
            passcode = cust.getTempAccessNum();
            supprsPARegstrnEmailFlag = cust.getSupprsPARegstrnEmailFlag() == 1;
        }
        
        if(files != null){
            for(int i = 0; i<files.size();i++){
                QuoteAttachment attachment = (QuoteAttachment)files.get(i);
                if(!attachment.isUploaded()){
                    filesFailedToCommitToCMWI.add(attachment);
                }else if(attachment.isUploaded() && !attachment.isSavedToCM()){
                    attachment.setMessage("File ["+attachment.getFileName()+"] "+appCtx.getI18nValueAsString(I18NBundleNames.DRAFT_QUOTE_MESSAGES, locale, DraftQuoteMessageKeys.MSG_FILE_CANNOT_BE_STORED));
                    filesFailedToCommitToCMWI.add(attachment);
                }
        	}
        }
        
        this.isCopied4PrcIncrQuoteFlag = quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag();
        this.isCopiedForOutputChangeFlag = quote.getQuoteHeader().isCopiedForOutputChangeFlag();
        
        super.collectResults(params);
    }
    
    public List getFilesFailedToCommitToCMWI() {
        if(filesFailedToCommitToCMWI == null){
            filesFailedToCommitToCMWI = new ArrayList();
        }
        return filesFailedToCommitToCMWI;
    }
    
    
    public boolean isNewCustomer() {
        return isNewCustomer;
    }
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    public String getSapDocNumber() {
        return sapDocNumber;
    }
	public boolean isShowErrorEmailMsgFlag() {
		return isShowErrorEmailMsgFlag;
	}	
    public String getPasscode() {
        return passcode;
    }
    
    public boolean isCpFromApprvdBid() {
        return isCpFromApprvdBid;
    }
    /**
     * @return Returns the isCopied4PrcIncrQuoteFlag.
     */
    public boolean isCopied4PrcIncrQuoteFlag() {
        return isCopied4PrcIncrQuoteFlag;
    }
    /**
     * @param isCopied4PrcIncrQuoteFlag The isCopied4PrcIncrQuoteFlag to set.
     */
    public void setCopied4PrcIncrQuoteFlag(boolean isCopied4PrcIncrQuoteFlag) {
        this.isCopied4PrcIncrQuoteFlag = isCopied4PrcIncrQuoteFlag;
    }
    //fix pl :The sections for new PA/PAE customer should be hidden for CSA quote because CSA customer will not have access on SWSO.
    public boolean isDisplayCustCofirmation() {
        return !(qh.isCSRAQuote()||qh.isCSTAQuote())&&!qh.isSSPQuote() && isNewCustomer && passcode != null && passcode.length()> 0 && !supprsPARegstrnEmailFlag;
    }

	public boolean isCopiedForOutputChangeFlag() {
		return isCopiedForOutputChangeFlag;
	}

	public void setCopiedForOutputChangeFlag(boolean isCopiedForOutputChangeFlag) {
		this.isCopiedForOutputChangeFlag = isCopiedForOutputChangeFlag;
	}
    
    
}
