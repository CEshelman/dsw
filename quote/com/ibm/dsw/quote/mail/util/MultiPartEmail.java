package com.ibm.dsw.quote.mail.util;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ibm.dsw.quote.mail.config.MailConstants;
import com.ibm.dsw.quote.mail.exception.EmailException;
import com.ibm.ead4j.common.config.EAD4JBootstrapKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>MultiPartEmail.java</code> class is the utilities for sending
 * mail.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-2
 */
public class MultiPartEmail {
    /** To email adresses list */
    protected List toAddress = new ArrayList();

    /** CC email adresses list */
    protected List ccAddress = new ArrayList();

    /** Bcc email adresses list */
    protected List bccAddress = new ArrayList();

    /** The from address */
    protected InternetAddress fromAddress = null;

    protected String subject = null;

    protected String host = null;

    protected Session session = null;

    protected Multipart multipart = null;

    protected String DEFAULT_CHARSET = "UTF-8";
    
    private static ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
    protected static final String codeBase = appCtx.getConfigParameter(EAD4JBootstrapKeys.EAD4J_CODEBASE_PATH_KEY);
    
    protected static boolean mailEnabled = BooleanUtils.toBoolean(StringUtils.isEmpty(appCtx.getConfigParameter("mail.enable"))?"false":appCtx.getConfigParameter("mail.enable"));
    protected static List listEnabledAddresses;
    protected static boolean isUsingWhiteList = true;

    public MultiPartEmail() {
        multipart = new MimeMultipart();
    }

    public void setCharSet(String charset) {
        if (null != charset && !"".equals(charset))
            DEFAULT_CHARSET = charset;
    }

    public String getCharSet() {
        return DEFAULT_CHARSET;
    }

    public void setMailSession(Session session) {
        this.session = session;
    }

    /** store the given email address in toAddress list */
    public MultiPartEmail addTo(String toEmail) throws EmailException {
        if ( isNullMailAddress(toEmail) )
        {
            return this;
        }
        InternetAddress temp = createInternetAddress(toEmail);
        if ( !toAddress.contains(temp) )
        {
            toAddress.add(temp);
        }
        return this;
    }

    /** store the given email address in ccAddress list */
    public MultiPartEmail addCc(String ccEmail) throws EmailException {
        if ( isNullMailAddress(ccEmail) )
        {
            return this;
        }
        InternetAddress temp = createInternetAddress(ccEmail);
        if ( !ccAddress.contains(temp) )
        {
           ccAddress.add(temp);
        }
        return this;
    }

    /** store the given email address in bccAddress list */
    public MultiPartEmail addBcc(String bccEmail) throws EmailException {
        if ( isNullMailAddress(bccEmail) )
        {
            return this;
        }
        InternetAddress temp = createInternetAddress(bccEmail);
        if ( !bccAddress.contains(temp) )
        {
            bccAddress.add(temp);
        }
        return this;
    }

    /** set from address */
    public void setFrom(String fromAddress) throws EmailException {
        this.fromAddress = createInternetAddress(fromAddress);
    }

    /** set email context text */
    public void setText(String text) throws EmailException {
        try {
            MimeBodyPart textBody = this.createBodyPart();
            textBody.setText(text + "\n\n", DEFAULT_CHARSET);
            multipart.addBodyPart(textBody, 0);
        } catch (MessagingException e) {
            throw new EmailException(e);
        }

    }

    /** set email context text */
    public void setText(String templateName, Map params) throws EmailException {
        try {
            MimeBodyPart textBody = this.createBodyPart();
            textBody.setText(this.generateText(templateName, params) + "\n\n", DEFAULT_CHARSET);
            multipart.addBodyPart(textBody, 0);
        } catch (MessagingException e) {
            throw new EmailException(e);
        }

    }
    
    public void setHTML(String text) throws EmailException {
        try {
            MimeBodyPart textBody = this.createBodyPart();
            textBody.setText(text + "\n\n", DEFAULT_CHARSET);
            textBody.addHeader("Content-Type", "text/html; charset=utf-8");
            multipart.addBodyPart(textBody, 0);
        } catch (MessagingException e) {
            throw new EmailException(e);
        }

    }
    
    public String getHtml() throws MessagingException, IOException
    {
    	MimeBodyPart part = (MimeBodyPart)multipart.getBodyPart(0);
    	return part.getContent().toString();
    }

    public void setHTML(String templateName, Map params) throws EmailException {
        try {
            MimeBodyPart textBody = this.createBodyPart();
            textBody.setText(this.generateText(templateName, params) + "\n\n", DEFAULT_CHARSET);
            textBody.addHeader("Content-Type", "text/html; charset=utf-8");
            multipart.addBodyPart(textBody, 0);
        } catch (MessagingException e) {
            throw new EmailException(e);
        }

    }
    
    public String generateText(String templateName, Map params) throws EmailException {
        String text = "";
        StringWriter writer = null;
        try {
            Properties p = new Properties();
            p.setProperty("resource.loader", "class");
            p.setProperty("class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(p);
            Template template = Velocity.getTemplate("appl/config/templates/" + templateName);
            VelocityContext context = new VelocityContext();
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Entry entry = (Entry) it.next();
                context.put((String) entry.getKey(), entry.getValue());
            }
            writer = new StringWriter();
            template.merge(context, writer);
            text = writer.toString();
        } catch (Exception e) {
            throw new EmailException(e);
        }finally{
        	try {
				writer.close();
			} catch (IOException e) {
				LogContextFactory.singleton().getLogContext().error(this, e, "Error when generate text");
			}
        }
        return text;
    }

    /** set email subject */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** set smtp host name */
    public void setHost(String host) {
        this.host = host;
    }

    public MultiPartEmail attach(URL filePathURL, String fileName, String description) throws EmailException {
        try {
            filePathURL.openStream();
            return attach(new URLDataSource(filePathURL), fileName, description);
        } catch (IOException e) {
            throw new EmailException(e);
        }

    }

    /**
     * @param fileName
     * @return
     * @throws EmailException
     */
    public MultiPartEmail attach(String filePath, String name, String description) throws EmailException {
        return attach(new FileDataSource(filePath), name, description);
    }

    public void setupWhiteList() throws EmailException{
        SAXBuilder sb=new SAXBuilder();
        Document doc = null;
        if(StringUtils.isEmpty(appCtx.getConfigParameter("mail.whitelist.config.file"))){
            isUsingWhiteList = false;
            return;            
        }
        String configXMLFile = codeBase + "/" + appCtx.getConfigParameter("portal.settings.folder") + "/" + appCtx.getConfigParameter("mail.whitelist.config.file");
        try {
            doc = sb.build(configXMLFile);
            Element root = doc.getRootElement();
            List listEmailElement = root.getChildren();
            
            listEnabledAddresses = new ArrayList();
            for(int i=0;i<listEmailElement.size();i++){
                Element elementEmail = (Element)listEmailElement.get(i);
                if("true".equalsIgnoreCase(elementEmail.getAttributeValue("enable"))){
                    InternetAddress temp = createInternetAddress(elementEmail.getAttributeValue("address"));
                    if(!listEnabledAddresses.contains(temp)){
                        listEnabledAddresses.add(temp);
                    }
                }
            }   
        } catch (JDOMException e) {
            LogContextFactory.singleton().getLogContext().error(this, "Mail Addresses Config XML file parse error - " + configXMLFile);
            isUsingWhiteList = false;
            return;
        } catch (IOException e) {
            LogContextFactory.singleton().getLogContext().error(this, "Mail Addresses Config XML file read error - " + configXMLFile);
            isUsingWhiteList = false;
            return;
        } catch (Exception e){
            throw new EmailException(e);
        }
         
    }
    
    public void filterAddresses(List list) throws EmailException{
        if(isUsingWhiteList && listEnabledAddresses==null){
            setupWhiteList();
        }
        
        //iterate the input list to check if the email in this list is enable to sent mail via the config file
        for(int i=0;i<list.size();i++){
            if(!listEnabledAddresses.contains(list.get(i))){
                list.remove(i);
                i--;
            }
        }
    }
    
    public void sendOut() throws EmailException {
        Session mailSession = this.getMailsession();
        MimeMessage message = new MimeMessage(mailSession);
        
        try {
            // Put parts in message
        	String mailAppName = appCtx.getConfigParameter(MailConstants.MAIL_APP);
        	String mailEnvName = appCtx.getConfigParameter(MailConstants.MAIL_ENV);
        	String mailBccAddress = appCtx.getConfigParameter(MailConstants.MAIL_BCC_ADDRESS);
        	if (!mailEnvName.equalsIgnoreCase("Production")) {
        		this.subject = mailEnvName + " " + mailAppName + " Testing: " + this.subject;
        	} else {
        		StringTokenizer st = new StringTokenizer(mailBccAddress, ",");
                while (st.hasMoreTokens()) {
                    String email = st.nextToken();
                    if (StringUtils.isNotBlank(email)) {
                        addBcc(email);
                    }
                }
        	}
            message.setSubject(this.subject, DEFAULT_CHARSET);
            message.setFrom(this.fromAddress);
            message.setContent(multipart);
            if(mailEnabled) { 
                message.addRecipients(Message.RecipientType.TO, (Address[]) toAddress.toArray(new Address[0]));
                message.addRecipients(Message.RecipientType.CC, (Address[]) ccAddress.toArray(new Address[0]));
                message.addRecipients(Message.RecipientType.BCC, (Address[]) bccAddress.toArray(new Address[0]));                
                //Send the message    
                Transport.send(message);
            } else {
                if(isUsingWhiteList){
                    filterAddresses(toAddress);
                    filterAddresses(ccAddress);
                    filterAddresses(bccAddress);
                    if(!(toAddress.size()==0&&ccAddress.size()==0&&bccAddress.size()==0)){
                        message.addRecipients(Message.RecipientType.TO, (Address[]) toAddress.toArray(new Address[0]));
                        message.addRecipients(Message.RecipientType.CC, (Address[]) ccAddress.toArray(new Address[0]));
                        message.addRecipients(Message.RecipientType.BCC, (Address[]) bccAddress.toArray(new Address[0]));                
                        //Send the message    
                        Transport.send(message); 
                    }
                }

                LogContextFactory.singleton().getLogContext().info(this,"From :" + toAddress
                        + " Subject: " + message.getSubject());
            }
        } catch (SendFailedException fe) {
            LogContextFactory.singleton().getLogContext().error(this, "invalidAddresses: " + fe.getInvalidAddresses());
        } catch (MessagingException e) {
            throw new EmailException("Failed to send out.", e);
        }

    }

    /**
     * @param source
     * @return
     * @throws EmailException
     */
    private MultiPartEmail attach(DataSource source, String fileName, String description) throws EmailException {
        try {
            BodyPart attachBody = this.createBodyPart();
            attachBody.setDataHandler(new DataHandler(source));
            attachBody.setFileName(fileName);
            attachBody.setDescription(description);
            multipart.addBodyPart(attachBody);
        } catch (MessagingException e) {
            throw new EmailException(e);
        }
        return this;
    }

    protected MimeBodyPart createBodyPart() {
        return new MimeBodyPart();
    }

    /**
     * @return
     * @throws EmailException
     */
    private Session getMailsession() throws EmailException {
        if (this.session == null) {

            if (this.host == null && "".equals(this.host)) {
                throw new EmailException("Please set mail session or SMTP host before send out.");
            }

            //Wrap system properties
            Properties props = System.getProperties();

            //Setup mail server
            props.put("mail.smtp.host", host);

            //Create mail session
            this.session = Session.getInstance(props);
        }
        return this.session;
    }
    
    private boolean isNullMailAddress(String email)
    {
        if ( StringUtils.isBlank(email) || email.trim().equalsIgnoreCase("NULL") )
        {
            return true;
        }
        return false;
    }

    /**
     * @param email
     * @return
     * @throws EmailException
     */
    protected InternetAddress createInternetAddress(String email) throws EmailException {
        if (StringUtils.isBlank(email))
            throw new EmailException("The given email address is blank. ");
        try {
            return new InternetAddress(email.trim());
        } catch (AddressException e) {
            throw new EmailException(email + "is not a valid internet email address. ", e);
        }

    }
}
