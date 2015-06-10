package com.ibm.dsw.automation.common;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.*;
import javax.mail.util.ByteArrayDataSource;



public class MailSender {

	  public static void sendhtml(Map param) {
		 
		    try {
		    	Properties mailConfigProp=(Properties) param.get("mailConfig");
		        String to = mailConfigProp.getProperty("mail.address.tolist");
			    String from = mailConfigProp.getProperty("mail.address.from");
			    String mailhost =mailConfigProp.getProperty("mail.host");
			    String subject=mailConfigProp.getProperty("mail.subject").replace("?",param.get("env").toString() );
			    String mailFooterMessage=mailConfigProp.getProperty("mail.footer.message");

		      Properties props = System.getProperties();
		      //  assume we're using SMTP
		      if (mailhost != null)
		        props.put("mail.smtp.host", mailhost);

		      // Get a Session object
		      Session session = Session.getInstance(props, null);

		      // construct the message
		      Message msg = new MimeMessage(session);
		      
		      // create and fill the first message part
		      MimeBodyPart mbp1 = new MimeBodyPart();
		      collect(mbp1,param);
		      
		      
	/*	      MimeBodyPart mbp2 = new MimeBodyPart();
		      mbp2.setText(mailFooterMessage);*/

		      // create the Multipart and its parts to it
		      Multipart mp = new MimeMultipart();
		      mp.addBodyPart(mbp1);
		     // mp.addBodyPart(mbp2);


		      // add the Multipart to the message
		      msg.setContent(mp);
	
		     // msg.setFileName("test_report.png");
		      if (from != null)
		        msg.setFrom(new InternetAddress(from));
		      else
		        msg.setFrom();

		      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
		      msg.setSubject(subject);

		      msg.setHeader("X-Mailer", "sendhtml");
		      msg.setSentDate(new Date());
		      
		      // send the thing off
		      Transport.send(msg);

		      System.out.println("\nMail was sent successfully.");


		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		  }

	  public static void collect( MimeBodyPart msg,Map param) throws MessagingException, IOException {
		
		    String subject="mailHeader";
		    StringBuffer sb = new StringBuffer();
		    
		    String mailMessage=((Properties) param.get("mailConfig")).getProperty("mail.message").replace("?",param.get("env").toString() );
		    
		    sb.append("<HTML>\n");
		    sb.append("<HEAD>\n");
		    sb.append("<TITLE>\n");
		    sb.append(subject + "\n");
		    sb.append("</TITLE>\n");
		    sb.append("</HEAD>\n");
		    sb.append("<BODY>\n");		      
		     
			sb.append(mailMessage);
			
			sb.append("<table border=\"1\" cellpadding=\"20\" align=\"left\">\n");
			sb.append("<tr>\n");
			sb.append("<th colspan=\"5\"  align=\"left\" bgcolor=\"#C4E1FF\">\n");
			sb.append("DSW Webdriver reporting summary");
			sb.append("<th>\n");
			sb.append("</tr>\n");
			sb.append("<tr>\n");
			sb.append("<th rowspan=\"2\" cellpadding=\"40\"  bgcolor=\"#C4E1FF\" align=\"left\">\n");
			sb.append("<a href=\"http://9.125.140.77:8000/" +
					param.get("env") +
					"/index.html\">Please click here for detail</a>\n");
			sb.append("</th>\n");
			sb.append("<th bgcolor=\"#C4E1FF\" nowrap=\"nowrap\">Passed</th>\n");
			sb.append("<th bgcolor=\"#C4E1FF\" nowrap=\"nowrap\">Skipped</th>\n");
			sb.append("<th bgcolor=\"#C4E1FF\" nowrap=\"nowrap\">Failed</th>\n");
			sb.append("<th bgcolor=\"#C4E1FF\" nowrap=\"nowrap\">Pass Rate</th>\n");
			sb.append("</tr>\n");
	
			sb.append("<tr>\n");
			sb.append("<td bgcolor=\"#88EE88\">\n");
			sb.append(param.get("passedTestsCnt"));
			sb.append("</td>\n");
			sb.append("<td bgcolor=\"#d0d0d0\">\n");
			sb.append(param.get("skippedTestsCnt"));
			sb.append("</td>\n");
			sb.append("<td bgcolor=\"red\">\n");
			sb.append(param.get("failedTestsCnt"));
			sb.append("</td>\n");
			sb.append("<td bgcolor=\"#d0d0d0\">\n");
			sb.append(param.get("passRate"));
			sb.append("</td>\n");
			sb.append("</tr>\n");
			sb.append("</table>\n");
			sb.append("<br/>\n");
		    sb.append("</BODY>\n");
		    sb.append("</HTML>\n");
		    
		    System.out.println(sb.toString());
	

		    msg.setDataHandler(new DataHandler(new ByteArrayDataSource(sb.toString(), "text/html")));
		  }

}
