package com.ibm.dsw.quote.draftquote.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.AttachmentsContract;
import com.ibm.dsw.quote.draftquote.contract.ToUAmendmentContract;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.dsw.quote.mail.config.MailConstants;
import com.ibm.dsw.quote.mail.process.MailProcess;
import com.ibm.dsw.quote.mail.process.MailProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

public class AddToUAmendmentAction extends BaseContractActionHandler {

    private static final long serialVersionUID = 4417529517020147699L;

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
		
        logContext.debug(this, "Begin to execute AddAttachmentAction.executeBiz");
        ToUAmendmentContract tContract = (ToUAmendmentContract)contract;
        AttachmentsContract aContract = tContract.getaContract();
        handler.addObject(DraftQuoteParamKeys.PARAM_RETURN_CODE, DraftQuoteParamKeys.PARAM_RETURN_CODE_SUCCESS);
        
        Quote quote = null;
	        try {
	        	quote = QuoteProcessFactory.singleton().create().getQuoteForToUMailOutByQuoteNum(aContract.getWebQuoteNumber());	        	
			
	        } catch (NoDataException e) {
				logContext.error(this, "NoDataException occurred When find quote base info.quoteNumber:"+aContract.getWebQuoteNumber()+", error:"+e.getMessage());
				logContext.error(this, e);			
	        }
	        
        try{
        	
	        // update agreement type
        	String newAgrmtType = tContract.getAgrmtType();
        	String oldAgrmtType = quote.getQuoteHeader().getAgrmtTypeCode();
        	if(StringUtils.isBlank(newAgrmtType)){
        		newAgrmtType = oldAgrmtType;
        	}
        	boolean iRet = true;
	        if (!StringUtils.isBlank(newAgrmtType) && !newAgrmtType.equals(oldAgrmtType)) {// if agreement type not changed, only call update agreement SP
	        	iRet = QuoteProcessFactory.singleton().create().updateQuoteAgrmtTypeByTou(aContract.getWebQuoteNumber(), newAgrmtType, tContract.getCurrentContractNum());
	        	handler.addObject(DraftQuoteParamKeys.PARAM_RETURN_CODE, "2");
	        
	        	if (iRet) {
	        		handler.addObject(ParamKeys.PARAM_AGREEMENT_TYPE, newAgrmtType);
	        	} else {
	        		handler.addObject(DraftQuoteParamKeys.PARAM_RETURN_CODE, DraftQuoteParamKeys.PARAM_RETURN_CODE_FAILURE);
	        	}
	        }else if(null == tContract.getAudCode() || QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equalsIgnoreCase(tContract.getAudCode())){// if agreement type not changed, will save tou list settings and other actions
	        	// ToU send mail implementation. by Andy(li rui), 2013-7-15
	        	//1. overload processQuoteAttachments() method, do not auto delete the uploaded file.
	        	uploadFile(aContract, handler);
	        	//2. step through the uploaded files, send out an email which attached a file.
	        	sendMail(aContract.getUser().getEmail(), aContract.getWebQuoteNumber(), aContract.getFiles(),quote);
	        	
	        	QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
	    		quoteProcess.updateTouFlag(aContract.getWebQuoteNumber(), tContract.getSaasTermCondCatFlag(), tContract.getTouURLs(), tContract.getTermsTypes(), tContract.getTermsSubTypes(), tContract.getRadioFlags(), 0);

	    		if(tContract.getNoFileTous() !=null && !tContract.getNoFileTous().equals("") && (tContract.getYesFlags()!= null && !"".equals(tContract.getYesFlags()))){
	    			quoteProcess.updateWarningTouFlag(aContract.getWebQuoteNumber(),aContract.getUserId(),tContract.getYesFlags(),tContract.getNoFileTous());
	    		}	  
	    		
				if (tContract.isSubmittedQuote()) {
					try {
						List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(aContract.getWebQuoteNumber());
						quote.setLineItemList(lineItemList);
						logContext.debug(this, "Calling SAP QuoteModify RFC...");
						QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();
						quoteModifyService.updateTouAmendmentLineItems(quote);

					} catch (WebServiceException e1) {
						logContext.error(this, e1);
						throw new QuoteException(e1);
					} catch (TopazException e2) {
						logContext.error(this,
								LogThrowableUtil.getStackTraceContent(e2));
						throw new QuoteException(e2);
					}
				}
	        }
        	logContext.debug(this, "End executing AddAttachmentAction.executeBiz");    
        	
        	if(!SecurityUtil.isValidInput("lob",tContract.getLob(),"Alpha", 20, true)){
         		throw new QuoteException("Invalid lob value");
         	}
         	if(!SecurityUtil.isValidInput("agreementType",newAgrmtType,"DirectoryName", 20, true)){
         		throw new QuoteException("Invalid agreementType value");
         	}         	
         	if(!SecurityUtil.isValidInput("isOnlySaaSParts",String.valueOf(tContract.isOnlySaaSParts()),"Alpha", 5, true)){
         		throw new QuoteException("Invalid isOnlySaaSParts value");
         	}
         	if(!SecurityUtil.isValidInput("isSubmittedQuote",String.valueOf(tContract.isSubmittedQuote()),"Alpha", 5, true)){
         		throw new QuoteException("Invalid isSubmittedQuote value");
         	}
         	if(!SecurityUtil.isValidInput("isSpecialBid",String.valueOf(tContract.isSpecialBid()),"Alpha", 5, true)){
         		throw new QuoteException("Invalid isSpecialBid value");
         	}
         	
        	handler.addObject("lob", tContract.getLob());
        	handler.addObject("isOnlySaaSParts", tContract.isOnlySaaSParts());
//        	handler.addObject("isChannelQuote", tContract.isChannelQuote());
        	handler.addObject("isSubmittedQuote", tContract.isSubmittedQuote());
        	handler.addObject("isSpecialBid", tContract.isSpecialBid());
        	handler.addObject("agreementType", newAgrmtType);
        	handler.addObject("CSATermsCount", new Integer(1));
        	handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_DRAFT_TOU_LIST);
        	return handler.getResultBean();
        } finally{
            //3. clean the uploaded temporary file.
        	cleanTemporaryFile(aContract.getFiles());
        }
    }

    /**
     * uploadFile
     * 
     * @param contract
     * @param handler
     */
    protected void uploadFile(AttachmentsContract aContract, ResultHandler handler) throws QuoteException {
            //ResultHandler handler, List files, Locale locale) {

        logContext.debug(this, "Trying to save the user's attachments.");
        List files = aContract.getFiles();
        String classfctnCode = QuoteConstants.QT_ATTCHMNT_TOU_AMENDMENT;
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                QuoteAttachment attchmt = (QuoteAttachment) files.get(i);
                attchmt.setClassfctnCode(classfctnCode);
            }
        }
        
        // ToU send mail implementation. by Andy(li rui), 2013-7-15
        //overload processQuoteAttachments() method, do not auto delete the uploaded file.
        //QuoteAttachmentProcessFactory.singleton().create().processQuoteAttachments(files);
        QuoteAttachmentProcessFactory.singleton().create().processQuoteAttachments(files, false);
        handleFileUploadMessages(handler, files, aContract.getLocale(), aContract);
        
        logContext.debug(this, "User's attachments have been processed.");
    }
    
    protected void handleFileUploadMessages(ResultHandler handler, List files, Locale locale, ProcessContract contract) {

        logContext.debug(this, "Trying to process attachments' messages.");

        if (files == null || files.size() == 0)
            return;

        List filesFailedToCommitToCMWI = new ArrayList();
        StringBuffer fileNamesBuffer = new StringBuffer();
        for (int i = 0; i < files.size(); i++) {
            QuoteAttachment attachment = (QuoteAttachment) files.get(i);
            if (!attachment.isUploaded()) {
                filesFailedToCommitToCMWI.add(attachment);
            } else if (attachment.isUploaded() && !attachment.isSavedToCM()) {
                attachment.setMessage("["
                        + attachment.getFileName()
                        + "] "
                        + this.getI18NString(DraftQuoteMessageKeys.MSG_FILE_CANNOT_BE_STORED,
                                I18NBundleNames.DRAFT_QUOTE_MESSAGES, locale));
                filesFailedToCommitToCMWI.add(attachment);
            }else{
            	fileNamesBuffer.append(attachment.getFileName()).append(", ");
            }
        }
        handler.addObject("attachNames", fileNamesBuffer.substring(0, fileNamesBuffer.length()-2));

        if (filesFailedToCommitToCMWI.size() == 0) {
            handler.addMessage(this.getI18NString(DraftQuoteMessageKeys.MSG_FILE_UPLOAD_SUCCESS,
                    I18NBundleNames.BASE_MESSAGES, locale), MessageBeanKeys.SUCCESS);
        } else {
            handler.addMessage(this.getI18NString(DraftQuoteMessageKeys.MSG_FILES_UPLOAD_FAILED,
                    I18NBundleNames.ERROR_MESSAGE, locale), MessageKeys.LAYER_MSG_HEAD);
            for (int i = 0; i < filesFailedToCommitToCMWI.size(); i++) {
                handler.addMessage(((QuoteAttachment) filesFailedToCommitToCMWI.get(i)).getMessage(),
                        MessageKeys.LAYER_MSG_ITEM);
            }
        }
        logContext.debug(this, "Attachments' messages have been processed.");
    }
    
    private void cleanTemporaryFile(List<QuoteAttachment> files){
    	if(files == null || files.size() == 0) return;
    	logContext.debug(this, "About cleaning the uploaded temp file when upload ToU...");
    	Iterator<QuoteAttachment> it = files.iterator();
    	try {
			while(it.hasNext()){
				QuoteAttachment attachment = (QuoteAttachment)it.next();
				attachment.getAttchmt().delete();
			}
		} catch (Exception e) {
			logContext.error(this, "Faild to delete the temp file after upload ToU file." + e.getMessage());
		}
    }
    
    void sendMail(String from, String webQuoteNumber, List<QuoteAttachment> files ,Quote quote) throws  QuoteException{
    	//if(files == null || files.size() == 0) return;
    	logContext.debug(this, "Prepare quote data for sending mail");
    	
    	//mail recipient: configured in property.xml, with key "tou.amendment.mail.recipient"
    	String to = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(MailConstants.MAIL_TOU_AMEENDMENT_RECIPIENT);
   	
    	logContext.debug(this, "About sending out mail with the uploaded file when upload ToU...");
    //	MailProcessFactory.singleton=null;
    	MailProcess mailProcess = MailProcessFactory.singleton().create();
    	Iterator it = files.iterator();
    	
    	//construct the value for filling the template
    	Map<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("customerName",quote.getCustomer().getCustName());  
    	paramMap.put("customerNumber", quote.getCustomer().getCustNum());
    	
    	//sales rep : if not submitted, adopt creator's name/email, otherwise, adopt submitter's name/mail
    	if(quote.getQuoteHeader().isSubmittedQuote()){
        	paramMap.put("salesRepName", quote.getQuoteHeader().getSubmitterName());
        	paramMap.put("salesRepEmail", quote.getQuoteHeader().getSubmitterId());
    	}
    	else{
        	paramMap.put("salesRepName", quote.getQuoteHeader().getCreatorName());
        	paramMap.put("salesRepEmail", quote.getQuoteHeader().getCreatorEmail());
    	}

    	paramMap.put("opportunityOwnerName", quote.getQuoteHeader().getOpprtntyOwnrName());
    	paramMap.put("opportunityOwnerEmail", quote.getQuoteHeader().getOpprtntyOwnrEmailAdr()); 
    	//TODO ANDY: send amended ToU: sapQuoteNumber, sapOrderNumber
    	paramMap.put("sapQuoteNumber", StringUtils.defaultString(quote.getQuoteHeader().getSapQuoteNum()));
    	paramMap.put("sapOrderNumber", StringUtils.defaultString(quote.getQuoteHeader().getSapOrderNum()));
    	 	
    	while(it.hasNext()){
    		QuoteAttachment attachment = (QuoteAttachment)it.next();
    		paramMap.put("touName", attachment.getTouName() );//ToU Name depends on the submitted form
    		mailProcess.sendTouAmendment(to, null, null,from, paramMap, 
    				attachment.getAttchmt().getPath(), 
    				attachment.getFileName());
    	}
    }
    
}
