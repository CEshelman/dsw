package com.ibm.dsw.quote.draftquote.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.base.util.TimeZoneUtils;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.ToURemoveAmendmentContract;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.mail.config.MailConstants;
import com.ibm.dsw.quote.mail.process.MailProcess;
import com.ibm.dsw.quote.mail.process.MailProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

public class ToURemoveAmendmentAction extends BaseContractActionHandler {

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		ToURemoveAmendmentContract rmAttchmtContract = (ToURemoveAmendmentContract) contract;
        String webQuoteNum = rmAttchmtContract.getWebQuoteNum();
        String attchmtSeqNum = rmAttchmtContract.getAttchmtSeqNum();
        if (webQuoteNum == null || "".equals(webQuoteNum)) {
            logContext.error(this, "Error! Must provide web quote number for deleting.");
            throw new QuoteException("Must provide web quote number for deleting.");
        }
        if (attchmtSeqNum == null || "".equals(attchmtSeqNum)) {
            logContext.error(this, "Error! Must provide an attachment sequence number for deleting.");
            throw new QuoteException("Must provide an attachment sequence number for deleting.");
        }
        logContext.debug(this, "Remove special bid attachment. webQuoteNum=" + webQuoteNum + "; attchmtSeqNum="
                + attchmtSeqNum);
        
        QuoteAttachmentProcess process = QuoteAttachmentProcessFactory.singleton().create();
        process.removeQuoteAttachment(webQuoteNum, attchmtSeqNum);
        
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        if(!rmAttchmtContract.isDisableFlag()){
        	quoteProcess.updateTouFlag(rmAttchmtContract.getWebQuoteNum(), 0, rmAttchmtContract.getTouURL(), rmAttchmtContract.getTermsType(), rmAttchmtContract.getTermsSubType(), "", 2);
        }

        handler.addObject("lob", rmAttchmtContract.getLob());
        handler.addObject("isOnlySaaSParts", rmAttchmtContract.isOnlySaaSParts());
        handler.addObject("isChannelQuote", rmAttchmtContract.isChannelQuote());
        handler.addObject("isSubmittedQuote", rmAttchmtContract.isSubmittedQuote());
        
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        Quote quote;
		try {
			quote = qProcess.getDraftQuoteBaseInfoByQuoteNum(webQuoteNum);
			if (null != quote){
				QuoteHeader header = quote.getQuoteHeader();
				if(null != header){
					handler.addObject(ParamKeys.SAAS_TERM_COND_CAT_FLAG, header.getSaasTermCondCatFlag());
				}
				sendNotifyMail(rmAttchmtContract, quote);
			}
		} catch (NoDataException e) {
			logContext.error(this, e.getMessage());
		}
		
		handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_DRAFT_TOU_LIST);
		return handler.getResultBean();
	}

	private void sendNotifyMail(ToURemoveAmendmentContract rmAttchmtContract,  Quote quote) throws QuoteException{
    	
    	logContext.debug(this, "About sending out mail with the uploaded file when upload ToU...");
    	
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
    	paramMap.put("touName", rmAttchmtContract.getTouName() );//ToU Name depends on the submitted form

    	paramMap.put("sapQuoteNumber", StringUtils.defaultString(quote.getQuoteHeader().getSapQuoteNum()));
    	paramMap.put("sapOrderNumber", StringUtils.defaultString(quote.getQuoteHeader().getSapOrderNum()));
    	//date time
    	String currentTime = getCurrentFormattedTime();
    	paramMap.put("datetime", currentTime);
    	
		MailProcess mailProcess = MailProcessFactory.singleton().create();
		
    	//mail recipient: configured in property.xml, with key "tou.amendment.mail.recipient"
    	String to = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(MailConstants.MAIL_TOU_AMEENDMENT_RECIPIENT);
		String from = rmAttchmtContract.getUser().getEmail();
		mailProcess.sendTouRemove(to , null, null, from, paramMap);
	}
	
	
	/**
	 * return current time. format dd MMM yyyy at HH:mm:ss zzz
	 * @param locale
	 * @return
	 */
	private String getCurrentFormattedTime() {
		String datePattern = "dd MMM yyyy";
		String timePattern = "HH:mm:ss zzz";
		String date = DateUtil.formatDate(new Date(), datePattern, TimeZoneUtils.getTimeZone(), Locale.ENGLISH);
		String time = DateUtil.formatDate(new Date(), timePattern, TimeZoneUtils.getTimeZone(), Locale.ENGLISH);
		return date + " at " + time;
	}

}
