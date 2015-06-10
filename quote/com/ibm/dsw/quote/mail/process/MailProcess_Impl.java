package com.ibm.dsw.quote.mail.process;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.appcache.domain.AcquisitionFactory;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ApproveComment;
import com.ibm.dsw.quote.common.domain.CountrySignatureRuleFactory;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.customer.config.CustomerMessageKeys;
import com.ibm.dsw.quote.mail.config.MailConstants;
import com.ibm.dsw.quote.mail.config.MailMessageKeys;
import com.ibm.dsw.quote.mail.config.MailViewKeys;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.dsw.quote.mail.util.MultiPartEmail;
import com.ibm.dsw.quote.mail.util.ServerDetailsUtil;
import com.ibm.dsw.quote.message.push.MessagePushFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MassDlgtnProcess_Impl</code> class is the abstract implementation
 * of MassDlgtnProcess
 * 
 * 
 * @author cuixg@cn.ibm.com </a> <br/>
 * 
 * Creation date: 2007-4-6
 */
public abstract class MailProcess_Impl extends TopazTransactionalProcess implements MailProcess {
    private static final String DSW_ONLINE_EMAIL = "Distributed_software_online@ibm.com";
    protected LogContext logContext = LogContextFactory.singleton().getLogContext();
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.mail.process.MailProcess#sendMail(com.ibm.dsw.quote.mail.util.MultiPartEmail)
     */
    public void sendMail(MultiPartEmail mail) throws QuoteException {
        mail.sendOut();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.mail.process.MailProcess#sendQuote()
     */
    public void sendQuote(String from, String to, String cc, String subject, String text, File file, String desc)
            throws EmailException {
        MultiPartEmail multiMail = new MultiPartEmail();
        multiMail.setFrom(from);

        StringTokenizer st = new StringTokenizer(to, ",");
        while (st.hasMoreTokens()) {
            String email = st.nextToken();
            if (StringUtils.isNotBlank(email)) {
                multiMail.addTo(email);
            }
        }

        st = new StringTokenizer(cc, ",");
        while (st.hasMoreTokens()) {
            String email = st.nextToken();
            if (StringUtils.isNotBlank(email)) {
                multiMail.addCc(email);
            }
        }

        multiMail.setSubject(subject);
        StringBuffer sb = new StringBuffer(text);
        sb.insert(0, "<div style=\"font-size:13px;font-family:Default Sans Serif;\">");
        sb.append("<p/>");
        sb.append("</div>");
        sb.append("<font color=\"#F7F7F7\">");
        sb.append(ServerDetailsUtil.getServerDetailAsString());
        sb.append("</font>");

        multiMail.setHTML(sb.toString());
        

        multiMail.attach(file.getAbsolutePath(), file.getName(), desc);

        multiMail.setHost(getMainHost());

        multiMail.sendOut();
    }

    public void sendAddtionalReview(String to, String cc, String quoteNum, String userId, String quoteTitle, String customerName,
            String comments, String bidCreator, String link, String mailTemplate) throws EmailException {
        Map paramMap = new HashMap();
        paramMap.put("qutoeNum", quoteNum);
        paramMap.put("userId", userId);
        paramMap.put("quoteTitle", quoteTitle);
        paramMap.put("customerName", customerName);
        paramMap.put("comments", comments);
        paramMap.put("bidCreator", bidCreator);
        paramMap.put("link", link);
        paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
        
        String subject = quoteNum + " SWG Special Bids: Your Bid Request for " + customerName + " requires your Additional Review";
        
        if ( mailTemplate.startsWith("pgs/") )
		{
			subject = quoteNum + " Your Quote Request for " + customerName + " requires your Additional Review";
		}
        
        send(to, cc, null, DSW_ONLINE_EMAIL, subject, mailTemplate, paramMap, true);
        this.logContext.info(this, "sended mail success for add reviewer: " + quoteNum + ";" 
        		+ to + ";" + cc);
        
        //create message and persist in DB
        String sendTo = to + "," + cc;
        String receivers = createMessage(quoteNum, subject, QuoteConstants.QUOTE_NOTIFICATION, sendTo);
        if (receivers != null) {
        	//send message to all receivers
        	MessagePushFactory.send(subject, receivers);
        }
    }

    public void sendCommentAdded(String approver, String creatorSubmitter, String quoteNum, String requestName, String customerName,
            String comments, String link, String lob, String mailTemplate) throws EmailException {
        Map paramMap = new HashMap();
        paramMap.put("qutoeNum", quoteNum);
        paramMap.put("requestName", requestName);
        paramMap.put("customerName", customerName);
        paramMap.put("comments", comments);
        paramMap.put("link", link);
        paramMap.put("lob", lob);
        paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
        
        String subject = quoteNum + " SWG Special Bids: Comments have been added to Bid Request for " + customerName;
        if ( mailTemplate.startsWith("pgs/") )
		{
			subject = quoteNum + " Comments have been added to Quote Request for " + customerName;
		}
        send(approver, creatorSubmitter, null, DSW_ONLINE_EMAIL, subject, mailTemplate, paramMap, true);
        this.logContext.info(this, "sended mail success for reviewer comments added: " + quoteNum + ";" 
        		+ approver + ";" + creatorSubmitter);
        
        //create message and persist in DB
        String sendTo = approver + "," + creatorSubmitter;
        String receivers = createMessage(quoteNum, subject, QuoteConstants.QUOTE_NOTIFICATION, sendTo);
        if(receivers != null) {
        	//send message to all receivers
        	MessagePushFactory.send(subject, receivers);
        }
    }
    
    public void sendCreateCustomer(String userId, QuoteHeader quoteHeader, Locale locale, Customer customer) throws EmailException {
        try {
            Date currDate = new Date();
            ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
            //Get current date
            SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy",locale);
            String sDate = sdfDate.format(currDate);
            //Get country description
            CacheProcess cacheprocess = CacheProcessFactory.singleton().create();
            Country cntry = cacheprocess.getCountryByCode3(customer.getCountryCode());
            String countryDesc = "";
            if (cntry != null){
                countryDesc = cntry.getDesc();
            }
            String stateDesc = cntry.getStateDescription(customer.getSapRegionCode());
            //Format customer info
            StringBuffer custInfo = new StringBuffer();
            custInfo.append(customer.getCustName());
            custInfo.append("    ");
            custInfo.append(sDate).append("\n");
            custInfo.append(customer.getAddress1()).append("\n");
            if(StringUtils.isNotBlank(customer.getInternalAddress())){
                custInfo.append(customer.getInternalAddress()).append("\n");
            }
//            custInfo.append(sDate).append("\n");
            custInfo.append(customer.getCity()).append(", ").append(stateDesc).append(" ").append(customer.getPostalCode()).append("\n");
            custInfo.append(countryDesc).append("\n");
            custInfo.append(customer.getCntPhoneNumFull());
            //Get eorder URL
            String eoderURL = appCtx.getConfigParameter(ApplicationProperties.EORDER_URL);
            boolean isRequireSignature = CountrySignatureRuleFactory.singleton().isRequireSignature(quoteHeader.getCountry().getCode2());
            String signature = "";
            if(isRequireSignature) {
                signature = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, CustomerMessageKeys.SIGNATURE_MSG) + "\n";
            }
            Map paramMap = new HashMap();
            paramMap.put("custNum", customer.getCustNum());
            paramMap.put("ibmCustNum", customer.getIbmCustNum());
            paramMap.put("svpLevel", quoteHeader.getTranPriceLevelCode());
            paramMap.put("tempAccessNum", customer.getTempAccessNum());
            paramMap.put("custInfo", custInfo.toString());
            paramMap.put("eorderURL", eoderURL);
            paramMap.put("signature", signature);
            paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));

            //Generate the mail's subject
            StringBuffer subject = new StringBuffer();
            subject.append(appCtx.getI18nValueAsString(MailViewKeys.MESSAGE_BASE_NAME, locale,
                    MailMessageKeys.MAIL_TEMPPASSWD_SUBJECT_PART1)).append(" ");
            subject.append(customer.getCustName()).append(" ");
            subject.append(appCtx.getI18nValueAsString(MailViewKeys.MESSAGE_BASE_NAME, locale,
                    MailMessageKeys.MAIL_TEMPPASSWD_SUBJECT_AT)).append(" ");
            subject.append(customer.getAddress1());
            subject.append(appCtx.getI18nValueAsString(MailViewKeys.MESSAGE_BASE_NAME, locale,
                    MailMessageKeys.MAIL_TEMPPASSWD_SUBJECT_PART2));
            
            //Send out this mail to sales rep:
            //show order URL for PA/PAE and hide it for PPSS/FCT - eBiz #KHAK-7MZK9K
            if( QuoteConstants.LOB_FCT.endsWith(quoteHeader.getLob().getCode()) ){
                send(userId, null, null, DSW_ONLINE_EMAIL, subject.toString(), MailConstants.MAIL_TEMPLATE_CUSTOMER_CREATE, paramMap, true);
            } else if ( QuoteConstants.LOB_PPSS.endsWith(quoteHeader.getLob().getCode()) ) {
                send(userId, null, null, DSW_ONLINE_EMAIL, subject.toString(), MailConstants.MAIL_TEMPLATE_CUSTOMER_CREATE_PPSS, paramMap, true);
            } else if (QuoteConstants.LOB_OEM.endsWith(quoteHeader.getLob().getCode())) {
                send(userId, null, null, DSW_ONLINE_EMAIL, subject.toString(), MailConstants.MAIL_TEMPLATE_CUSTOMER_CREATE_OEM, paramMap, true);
            }else {
                send(userId, null, null, DSW_ONLINE_EMAIL, subject.toString(), MailConstants.MAIL_TEMPLATE_CUSTOMER_CREATE_PAE, paramMap, true);
            }
            
        } catch (QuoteException e) {
            throw new EmailException();
        }
    }

    private void send(String to, String from, String subject, String template, Map paramMap) throws EmailException {
        MultiPartEmail multiMail = new MultiPartEmail();
        StringTokenizer st = new StringTokenizer(to, ",");
        while (st.hasMoreTokens()) {
            String email = st.nextToken();
            if (StringUtils.isNotBlank(email)) {
                multiMail.addTo(email);
            }
        }
        multiMail.setFrom(from);
        multiMail.setSubject(subject);
        multiMail.setText(template, paramMap);
        multiMail.setHost(getMainHost());
        multiMail.sendOut();

    }
    
    private void send(String to, String cc, String bcc, String from, String subject, String template, Map paramMap, boolean htmlEnable) throws EmailException {
    	MultiPartEmail multiMail = new MultiPartEmail();
        if ( StringUtils.isNotBlank(to) )
        {
	        StringTokenizer st = new StringTokenizer(to, ",");
	        while (st.hasMoreTokens()) {
	            String email = st.nextToken();
	            if (StringUtils.isNotBlank(email)) {
	                multiMail.addTo(email);
	            }
	        }
        }
        if(StringUtils.isNotBlank(cc)){
        	StringTokenizer st1 = new StringTokenizer(cc, ",");
            while (st1.hasMoreTokens()) {
                String email = st1.nextToken();
                if (StringUtils.isNotBlank(email)) {
                    multiMail.addCc(email);
                }
            }
        }
        if(StringUtils.isNotBlank(bcc)){
        	StringTokenizer st2 = new StringTokenizer(bcc, ",");
            while (st2.hasMoreTokens()) {
                String email = st2.nextToken();
                if (StringUtils.isNotBlank(email)) {
                    multiMail.addBcc(email);
                }
            }
        }
        multiMail.setFrom(from);
        multiMail.setSubject(subject);
        if(htmlEnable)
            multiMail.setHTML(template, paramMap);
        else
            multiMail.setText(template, paramMap);
        multiMail.setHost(getMainHost());
        
        this.logContext.info(this, "------------------mail send begin-----------------");
        this.logContext.info(this, "to=" + to);
        this.logContext.info(this, "cc=" + cc);
        this.logContext.info(this, "subject=" + subject);
        try{
        this.logContext.info(this, "content:" + multiMail.getHtml());
        }
        catch ( Throwable t )
        {
        	
        	this.logContext.debug(this, t.toString());
        	t.printStackTrace();
        }
        
        this.logContext.info(this, "------------------mail send end-----------------");
        
        multiMail.sendOut();

    }
    private MultiPartEmail createMultiPartEmail(String to, String cc, String bcc, String from, String subject, String template, Map paramMap, boolean htmlEnable) throws EmailException {
    	MultiPartEmail multiMail = new MultiPartEmail();
    	if ( StringUtils.isNotBlank(to) )
    	{
    		StringTokenizer st = new StringTokenizer(to, ",");
    		while (st.hasMoreTokens()) {
    			String email = st.nextToken();
    			if (StringUtils.isNotBlank(email)) {
    				multiMail.addTo(email);
    			}
    		}
    	}
    	if(StringUtils.isNotBlank(cc)){
    		StringTokenizer st1 = new StringTokenizer(cc, ",");
    		while (st1.hasMoreTokens()) {
    			String email = st1.nextToken();
    			if (StringUtils.isNotBlank(email)) {
    				multiMail.addCc(email);
    			}
    		}
    	}
    	if(StringUtils.isNotBlank(bcc)){
    		StringTokenizer st2 = new StringTokenizer(bcc, ",");
    		while (st2.hasMoreTokens()) {
    			String email = st2.nextToken();
    			if (StringUtils.isNotBlank(email)) {
    				multiMail.addBcc(email);
    			}
    		}
    	}
    	multiMail.setFrom(from);
    	multiMail.setSubject(subject);
    	if(htmlEnable)
    		multiMail.setHTML(template, paramMap);
    	else
    		multiMail.setText(template, paramMap);
    	multiMail.setHost(getMainHost());
    	
    	this.logContext.info(this, "------------------mail send begin-----------------");
    	this.logContext.info(this, "to=" + to);
    	this.logContext.info(this, "cc=" + cc);
    	this.logContext.info(this, "subject=" + subject);
    	try{
    		if(logContext.isDebug())
    			this.logContext.info(this, "content:" + multiMail.getHtml());
    	}
    	catch ( Throwable t )
    	{
    		
    		this.logContext.info(this, t.toString());
    		t.printStackTrace();
    	}
    	
    	this.logContext.info(this, "------------------mail send end-----------------");
    	
    	return multiMail;
    	
    }

    private String getMainHost() {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        return appContext.getConfigParameter(ApplicationProperties.MAIL_HOST);
    }
    

    public void sendNoApprovers(List userEmailList, String cc, String link, QuoteHeader quoteHeader) throws EmailException {
        String quoteNum = quoteHeader.getWebQuoteNum();
        String custName = quoteHeader.getCustName();
        String quoteTitle = quoteHeader.getQuoteTitle();
        String geo = quoteHeader.getCountry().getSpecialBidAreaCode();
        String lob = quoteHeader.getLob().getCodeDesc();
        String acquisition = "";
        
        try {
            if (quoteHeader.isFCTQuote()) {
                String acqstnCode = quoteHeader.getAcqrtnCode();
                CodeDescObj codeObj = AcquisitionFactory.singleton().getAcquisitionByCode(acqstnCode);
                acquisition = codeObj == null ? "" : codeObj.getCodeDesc();
            }
        } catch (TopazException e) {
            throw new EmailException(e);
        }
        
        StringBuffer subject = new StringBuffer("Action Needed: No approvers assigned to ");
        subject.append(geo).append(" ");
        
        if ( quoteHeader.isFCTToPAQuote() ) {
        	subject.append("Flexible Contract Type").append(" ");
        } else {
        	subject.append(lob).append(" ");
        }
        
        if (StringUtils.isNotBlank(acquisition))
            subject.append("(").append(acquisition).append(") ");
        subject.append("quote ").append(quoteNum);
        String template = "noApprovers.vm";
        
        sendSpecialBidEmail(userEmailList, cc, quoteNum, custName, subject.toString(), quoteTitle, link, template,
                true);
        this.logContext.info(this, "sended mail success for no approver when quote submit: " + quoteNum + ";" 
        		+ (userEmailList == null ? "" : userEmailList.toString()) + ";" + cc);
    }
    
    public void sendOneApproverNotification(List toList, String cc, String quoteNum, String custName,
            String quoteTitle, String link) throws EmailException {
        String subject = "Action Needed: Only one approver level assigned to " + quoteNum;
        sendSpecialBidEmail(toList, cc, quoteNum, custName, subject, quoteTitle, link,
                MailConstants.MAINT_TEMPLATE_ONE_LEVEL_APPROVER, true);
        this.logContext.info(this, "sended mail success for one level approver when quote submit: " + quoteNum + ";" 
        		+ (toList == null ? "" : toList.toString()) + ";" + cc);
    }

    /**
     * @param userEmailList
     * @param cc
     * @param quoteNum
     * @param custName
     * @param subject
     * @param quoteTitle
     * @param link
     * @throws EmailException
     */
    private void sendSpecialBidEmail(List userEmailList, String cc, String quoteNum, String custName, String subject,
            String quoteTitle, String link, String template, boolean htmlEnable) throws EmailException {
        MultiPartEmail multiMail = new MultiPartEmail();

        multiMail.setFrom(DSW_ONLINE_EMAIL);
        Iterator iterator = userEmailList.iterator();
        while (iterator.hasNext()) {
            multiMail.addTo((String) iterator.next());
        }
        
        if(StringUtils.isNotBlank(cc)){
        	StringTokenizer st1 = new StringTokenizer(cc, ",");
            while (st1.hasMoreTokens()) {
                String email = st1.nextToken();
                if (StringUtils.isNotBlank(email)) {
                    multiMail.addCc(email);
                }
            }
        }
        
        if (htmlEnable)
            link = StringUtils.replace(link, "&", "&amp;");

        multiMail.setSubject(subject);
        Map paramMap = new HashMap();
        paramMap.put("quoteNum", quoteNum);
        paramMap.put("custName", custName);
        paramMap.put("quoteTitle", quoteTitle);
        paramMap.put("link", link);
        
        if (htmlEnable) {
            paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
            multiMail.setHTML(template, paramMap);
        }
        else {
            multiMail.setText(template, paramMap);
        }

        multiMail.setHost(getMainHost());

        multiMail.sendOut();
    }

    public void sendMultiSameLevelGroups(List userEmailList, String cc, String quoteNum, String custName,
            String quoteTitle, String link) throws EmailException {
    	custName = custName.trim();
    	String subject = "Action Needed: Multiple approver groups at the same level assigned to " + quoteNum;
        String template = "multiSameLevelGroups.vm";
        sendSpecialBidEmail(userEmailList, cc, quoteNum, custName, subject, quoteTitle, link, template, true);
        this.logContext.info(this, "sended mail success for mutiple same level groups when quote submit: " + quoteNum + ";" 
        		+ (userEmailList == null ? "" : userEmailList.toString()) + ";" + cc);
    }

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendBidRequestApprovalForm(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendBidRequestApprovalForm(String toAddressList, String ccAddressList, String bidNum, String bidTitle, String customerName, String salesRepName, String salesRepEmail, String url, String mailTemplate, String extendedDate, String extendedReason) throws EmailException {
		// TODO Auto-generated method stub
		customerName = customerName.trim();
		String subject = bidNum + " SWG Special Bids: A Bid Request for "+ customerName + " requires your Approval";
		if ( mailTemplate.startsWith("pgs/") )
		{
			subject = bidNum + " A Partner Quote Request for "+ customerName + " requires your Approval";
		}
		Map paramMap = new HashMap();
		paramMap.put("bidNum", bidNum);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("salesRepName", StringEscapeUtils.escapeHtml(salesRepName));
		paramMap.put("salesRepEmail", StringEscapeUtils.escapeHtml(salesRepEmail));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("extendedDate", StringEscapeUtils.escapeHtml(extendedDate));
		paramMap.put("extendedReason", extendedReason);
		
		send(toAddressList, ccAddressList, null, DSW_ONLINE_EMAIL, subject,mailTemplate, paramMap, true);

        //create message and persist in DB
		String sendTo = toAddressList + "," + ccAddressList;
        String receivers = createMessage(bidNum, subject, QuoteConstants.QUOTE_NOTIFICATION, sendTo);
        if(receivers != null) {
        	//send message to all receivers
        	MessagePushFactory.send(subject, receivers);
        }
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendBidAddlInfoProvidedForm(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendBidAddlInfoProvidedForm(String toAddressList, String ccAddressList, String bidNum, String bidTitle, String customerName, String comments, String url, String mailTemplate) throws EmailException {
		// TODO Auto-generated method stub
		customerName = customerName.trim();
		String subject = bidNum + " SWG Special Bids: Additional Information has been added to Bid Request for " + customerName;
		if ( mailTemplate.startsWith("pgs/") )
		{
			subject = bidNum + " Additional Information has been added to Quote Request for " + customerName;
		}
		Map paramMap = new HashMap();
		paramMap.put("bidNum", bidNum);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("comments", comments);
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		
		send(toAddressList, ccAddressList, null, DSW_ONLINE_EMAIL, subject, mailTemplate, paramMap, true);
		this.logContext.info(this, "sended mail success for additional info provided: " + bidNum + ";" 
         		+ toAddressList + ";" + ccAddressList);
		
        //create message and persist in DB
		String sendTo = toAddressList + "," + ccAddressList;
        String receivers = createMessage(bidNum, subject, QuoteConstants.QUOTE_NOTIFICATION, sendTo);
        if(receivers != null) {
        	//send message to all receivers
        	MessagePushFactory.send(subject, receivers);
        }
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendBidRejectedForm(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendBidRejectedForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, String url, String mailTemplate,String returnReason) throws EmailException {
		// TODO Auto-generated method stub
		customerName = customerName.trim();
		String subject = bidNumber + " SWG Special Bids: Your Bid Request for " + customerName + " has been Rejected";
		if ( mailTemplate.startsWith("pgs/") )
		{
			subject = bidNumber + " Your Quote Request for " + customerName + " has been Rejected";
		}
		Map paramMap = new HashMap();
		paramMap.put("bidNum", bidNumber);
		paramMap.put("approverComments", approverComments);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		paramMap.put("returnReason", returnReason);
		
		send(toAddressList, ccAddressList, null, DSW_ONLINE_EMAIL, subject, mailTemplate, paramMap, true);
		this.logContext.info(this, "sended mail success for reject: " + bidNumber + ";" 
        		+ toAddressList + ";" + ccAddressList);
		
        //create message and persist in DB
		String sendTo = toAddressList + "," + ccAddressList;
        String receivers = createMessage(bidNumber, subject, QuoteConstants.QUOTE_NOTIFICATION, sendTo);
        if(receivers != null) {
        	//send message to all receivers
        	MessagePushFactory.send(subject, receivers);
        }
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendBidReturnedForChangesForm(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendBidReturnedForChangesForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, String url, String mailTemplate,String returnReason) throws EmailException {
		// TODO Auto-generated method stub
		customerName = customerName.trim();
		String subject = bidNumber + " SWG Special Bids: Your Bid Request for " + customerName + " has been Returned for Changes.";
		if ( mailTemplate.startsWith("pgs/") )
		{
			subject = bidNumber + " Your Quote Request for " + customerName + " has been Returned for Changes.";
		}
		Map paramMap = new HashMap();
		paramMap.put("bidNum", bidNumber);
		paramMap.put("approverComments", approverComments);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		paramMap.put("returnReason", returnReason);
		
		send(toAddressList, ccAddressList, null, DSW_ONLINE_EMAIL, subject, mailTemplate, paramMap, true);
		this.logContext.info(this, "sended mail success for return for change: " + bidNumber + ";" 
        		+ toAddressList + ";" + ccAddressList);

        //create message and persist in DB
		String sendTo = toAddressList + "," + ccAddressList;
        String receivers = createMessage(bidNumber, subject, QuoteConstants.QUOTE_NOTIFICATION, sendTo);
        if(receivers != null) {
        	//send message to all receivers
        	MessagePushFactory.send(subject, receivers);
        }
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendBidRequestAddlInfoForm(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendBidRequestAddlInfoForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, String url, String mailTemplate,String returnReasonDesc) throws EmailException {
		// TODO Auto-generated method stub
		customerName = customerName.trim();
		String subject = bidNumber + " SWG Special Bids: Your Bid Request for " + customerName + " has been returned for Additional information.";
		if ( mailTemplate.startsWith("pgs/") )
		{
			subject = bidNumber + " Your Quote Request for " + customerName + " has been returned for Additional information.";
		}
		Map paramMap = new HashMap();
		paramMap.put("bidNum", bidNumber);
		paramMap.put("approverComments", approverComments);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		paramMap.put("returnReason", returnReasonDesc);

		
		send(toAddressList, ccAddressList, null, DSW_ONLINE_EMAIL, subject,mailTemplate, paramMap, true);	
		this.logContext.info(this, "sended mail success for return for additional info: " + bidNumber + ";" 
        		+ toAddressList + ";" + ccAddressList);
		
        //create message and persist in DB
		String sendTo = toAddressList + "," + ccAddressList;
        String receivers = createMessage(bidNumber, subject, QuoteConstants.QUOTE_NOTIFICATION, sendTo);
        if(receivers != null) {
        	//send message to all receivers
        	MessagePushFactory.send(subject, receivers);
        }
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendBidNotifyOfFinalApprovalForm(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendBidNotifyOfFinalApprovalForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, 
			String expirationDate, List<ApproveComment> allApprCmts, String mailTemplate) throws EmailException {
		// TODO Auto-generated method stub
		customerName = customerName.trim();
		String subject = bidNumber + " SWG Special Bids: Your Bid Request for " + customerName + " has been Approved";
		if ( mailTemplate.startsWith("pgs/") )
		{
			subject = bidNumber + " Your Quote Request for " + customerName + " has been Approved";
		}
		Map paramMap = new HashMap();
		paramMap.put("bidNum", bidNumber);
		paramMap.put("approverComments", approverComments);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		if (expirationDate==null)
			expirationDate = "";
		paramMap.put("expirationDate", StringEscapeUtils.escapeHtml(expirationDate));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		paramMap.put("allApprCmts", allApprCmts);
		
		String rofLink = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
				ApplicationProperties.HOME_RELATED_LINK_ROF_RUL);
		
		paramMap.put("gofLink", StringEscapeUtils.escapeHtml(rofLink));
		send(toAddressList, ccAddressList, null, DSW_ONLINE_EMAIL, subject,mailTemplate, paramMap, true);
		this.logContext.info(this, "sended mail success for final approve: " + bidNumber + ";" 
        		+ toAddressList + ";" + ccAddressList);
		
        //create message and persist in DB
		String sendTo = toAddressList + "," + ccAddressList;
        String receivers = createMessage(bidNumber, subject, QuoteConstants.QUOTE_NOTIFICATION, sendTo);
        if(receivers != null) {
        	//send message to all receivers
        	MessagePushFactory.send(subject, receivers);
        }
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendBidCancelledForm(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendBidCancelledForm(String toAddressList, String ccAddressList, String bidNumber, String approverComments, String bidTitle, String customerName, String salesRepName, String salesRepEmail, String url, String mailTemplate) throws EmailException {
		// TODO Auto-generated method stub
		customerName = customerName.trim();
		String subject = bidNumber + " SWG Special Bids: Bid Request for " + customerName + " has been cancelled.";
		if ( mailTemplate.startsWith("pgs/") )
		{
			subject = bidNumber + " Quote Request, " + bidNumber + " has been cancelled.";
		}
		Map paramMap = new HashMap();
		paramMap.put("bidNum", bidNumber);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("salesRepName", StringEscapeUtils.escapeHtml(salesRepName));
		paramMap.put("salesRepEmail", StringEscapeUtils.escapeHtml(salesRepEmail));
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		
		send(toAddressList, ccAddressList, null, DSW_ONLINE_EMAIL, subject, mailTemplate, paramMap, true);
		this.logContext.info(this, "sended mail success for bid cancel: " + bidNumber + ";" 
        		+ toAddressList + ";" + ccAddressList);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendBidEscalateForm(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void sendBidEscalateForm(String toAddressList, String ccAddressList, String pendDays, String bidNumber, String approverComments, String bidTitle, String customerName, String salesRepName, String salesRepEmail, String url, String extendedDate, String extendedReason) throws EmailException {
		// TODO Auto-generated method stub
		customerName = customerName.trim();
		String subject = "*REMINDER* " + bidNumber + " SWG Special Bids: A Bid Request for " + customerName + " requires your Approval";
		Map paramMap = new HashMap();
		paramMap.put("pendDays", StringEscapeUtils.escapeHtml(pendDays));
		paramMap.put("bidNum", bidNumber);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("salesRepName", StringEscapeUtils.escapeHtml(salesRepName));
		paramMap.put("salesRepEmail", StringEscapeUtils.escapeHtml(salesRepEmail));
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		paramMap.put("extendedDate", StringEscapeUtils.escapeHtml(extendedDate));
		paramMap.put("extendedReason", extendedReason);
		
		send(toAddressList, ccAddressList, null, DSW_ONLINE_EMAIL, subject,MailConstants.MAIL_TEMPLATE_BID_REMINDER, paramMap, true);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendEmailInvalidInputProvidedForm(java.lang.String, java.lang.String, java.lang.String, java.lang.Exception)
	 */
	public void sendEmailInvalidInputProvidedForm(String bidNumber, String originalToAddressList, String originalCcAddressList, Exception e) throws EmailException {
		// TODO Auto-generated method stub
		String subject = "Invalid Input Provided sending email from Special Bids";
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendEmailGeneralExceptionForm(java.lang.String, java.lang.String, java.lang.String, java.lang.Exception)
	 */
	public void sendEmailGeneralExceptionForm(String bidNumber, String originalToAddressList, String originalCcAddressList, Exception e) throws EmailException {
		// TODO Auto-generated method stub
		String subject = "General exception sending email from Special Bids";
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.mail.process.MailProcess#sendSbidsProcessGeneralExceptionForm(java.lang.String, java.lang.Exception)
	 */
	public void sendSbidsProcessGeneralExceptionForm(String bidNumber, Exception e) throws EmailException {
		// TODO Auto-generated method stub
		String subject = "General exception in the Special Bids ApprovalProcess";
	}

	private String handleSpecialChar(String str) {
		
		if (str==null) return str;
		
	   	int i = 0;
	   	int pos = str.indexOf("<", i);
	   	int len = str.length();
	   	
	   	while ((pos != -1)) {
//	   		System.out.println("test: "+i+" "+pos+" "+len);	
	   		if (pos+1 == len) return str;

	   		if (str.charAt(pos+1)!=' ') {
	   			str = str.substring(0,pos+1) + " " + str.substring(pos+1);
//	   			System.out.println("test2: |"+str+"|");
	   		    ++len;
	   		}
			    i = pos + 2;
			    pos = str.indexOf("<", i);
	   	}
	   		
		return str;
	   }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.mail.process.MailProcess#sendApproverSaveJustAndNotify(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void sendApproverSaveJustAndNotify(String toAddress, String ccAddress, String webQuoteNum, String bidTitle, String customerName, String url, String approverName, String userId, String mailTemplate) throws EmailException {
    	customerName = customerName.trim();
    	String subject = webQuoteNum + " SWG Special Bids: Your Bid Request for " +  customerName + " has been modified by the approver";
        if ( mailTemplate.startsWith("pgs/") )
		{
			subject = webQuoteNum + " Your Quote Request for " + customerName + " has been modified by the approver";
		}
		Map paramMap = new HashMap();
		paramMap.put("bidNum", webQuoteNum);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("approverName", StringEscapeUtils.escapeHtml(approverName));
		paramMap.put("approverId", StringEscapeUtils.escapeHtml(userId));
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		
		send(toAddress, ccAddress, null, DSW_ONLINE_EMAIL, subject,mailTemplate, paramMap, true); 
		this.logContext.info(this, "sended mail success for approver save just and notify submitter: " + webQuoteNum + ";" 
        		+ toAddress + ";" + ccAddress);
    }

    public void notifyPendingApproverForSupersedeApprove(String toAddress, String webQuoteNum, String apprvrAction, int apprvrLevel, 
            String bidTitle, String customerName, String url, String approverName, Locale locale, String mailTemplate) throws EmailException{
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        String quoteStatusStr = appCtx.getI18nValueAsString(MessageKeys.BUNDLE_APPL_I18N_QUOTE, locale, SubmittedQuoteViewKeys.MSG_APPROVE_ACTION + apprvrAction.toLowerCase());
        customerName = customerName.trim();
        String subject = webQuoteNum + " SWG Special Bids: Bid Request for " + customerName + " has been " + quoteStatusStr + " at a higher level";
        if ( mailTemplate.startsWith("pgs/") )
		{
			subject = webQuoteNum + " Your Quote Request for " + customerName + " has been " + quoteStatusStr + " at a higher level";
		}
		Map paramMap = new HashMap();
		paramMap.put("quoteStatusStr", quoteStatusStr);
		paramMap.put("bidNum", webQuoteNum);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("approverName", StringEscapeUtils.escapeHtml(approverName));
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		
		send(toAddress, null, null, DSW_ONLINE_EMAIL, subject, mailTemplate, paramMap, true); 

        //create message and persist in DB
        String receivers = createMessage(webQuoteNum, subject, QuoteConstants.QUOTE_NOTIFICATION, toAddress);
        if(receivers != null) {
        	//send message to all receivers
        	MessagePushFactory.send(subject, receivers);
        }
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.mail.process.MailProcess#sendCancelApprovedBid(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void sendCancelApprovedBid(String toAddress, String ccAddress, String webQuoteNum, String bidTitle, String customerName, String url, String approverName, String userId, String apprvrComments, String mailTemplate) throws EmailException {
    	customerName = customerName.trim();
    	String subject = webQuoteNum + " SWG Special Bids: Your Bid Request for " + customerName + " has been Cancelled.";
        if ( mailTemplate.startsWith("pgs/") )
		{
			subject =  "Quote Request, " + webQuoteNum + " has been Cancelled.";
		}
		Map paramMap = new HashMap();
		paramMap.put("bidNum", webQuoteNum);
		paramMap.put("approverComments", apprvrComments);
		paramMap.put("bidTitle", StringEscapeUtils.escapeHtml(bidTitle));
		paramMap.put("customerName", handleSpecialChar(customerName));
		paramMap.put("url", StringEscapeUtils.escapeHtml(url));
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		
		send(toAddress, ccAddress, null, DSW_ONLINE_EMAIL, subject, mailTemplate, paramMap, true);
		this.logContext.info(this, "sended mail success for cacle approved bid " + webQuoteNum + ";" 
        		+ toAddress + ";" + ccAddress);
    }

	@Override
	public void sendEmailForm(String to, String cc, String bcc,  String subject, String template, Map paramMap) throws EmailException {
		
		paramMap.put("serverDetail", StringEscapeUtils.escapeHtml(ServerDetailsUtil.getServerDetailAsString()));
		send( to,  cc,  bcc,  DSW_ONLINE_EMAIL,  subject,  template,  paramMap,  true);
		this.logContext.info(this, "sended mail success for subject: " + subject + ";" 
        		+ to + ";" + cc);
	}
	
	
	private void sendTouAmendmentMail(String to, String cc, String bcc,String from,
			Map<String, String> paramMap, String attachmentFilePath, String attachmentFileName)
			throws EmailException {
		if(StringUtils.isEmpty(to)){
			logContext.warning(this, "!!! mail recipient is empty. please check the configuration 'mail.tou.amendment.recipient' in property.xml. process will abord.");
			return;
		}
		String template = MailConstants.MAIL_TEMPLATE_TOUADMENT_FORCUSTOMERS;
		
		//construct mail subject
		String customerName = (String)paramMap.get("customerName");
		String customerNumber = (String)paramMap.get("customerNumber");
		String subject = String.format("Terms of Use Amendment for %1$s (%2$s)",customerName, customerNumber);
		
		//is the opportunity owner same as sales rep ?
		String opportunityOwnerEmail = StringUtils.trim((String)paramMap.get("opportunityOwnerEmail"));
		String salesRepEmail = StringUtils.trim((String) paramMap.get("salesRepEmail"));
		paramMap.put("opportunityOwnerSameAsSalesRep", 
				String.valueOf(StringUtils.equalsIgnoreCase(opportunityOwnerEmail, salesRepEmail)));
		
		//construct mail message 
		MultiPartEmail email = createMultiPartEmail(to, cc, bcc, from, subject, template, paramMap, true);
		if(attachmentFilePath != null && !attachmentFilePath.isEmpty()){
			email.attach(attachmentFilePath, attachmentFileName, subject);
		}
		//send out the mail
		email.sendOut();
	}
	
	@Override
	public void sendTouAmendment(String to, String cc, String bcc,String from,
			Map paramMap, String attachmentFilePath, String attachmentFileName) throws EmailException{
		paramMap.put("operation", "ADD");
		sendTouAmendmentMail(to,cc,bcc,from, paramMap, attachmentFilePath, attachmentFileName);	
	}
	
	@Override
	public void sendTouRemove(String to, String cc, String bcc,String from,
			Map<String, String> paramMap) throws EmailException {
		paramMap.put("operation", "REMOVE");
		sendTouAmendmentMail(to,cc,bcc,from, paramMap, null, null);	
	}
	
	public abstract String createMessage(String quoteNumber, String content, String messageType, String receivers);
}
