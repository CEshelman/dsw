package com.ibm.dsw.quote.mail.contract;


/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). 
 * 
 * The <code>SendMailContract</code> class is contract for send mail action
 * 
 * @author cuixg@cn.ibm.com
 *
 * Created on Apr 4, 2007
 */
public class SendMailContract extends DisplaySendMailContract {
    private String to;
    private String cc;
    private String subject;
    private String customText;

    /**
     * @return Returns the cc.
     */
    public String getCc() {
        return cc;
    }
    /**
     * @param cc The cc to set.
     */
    public void setCc(String cc) {
        this.cc = cc;
    }
    /**
     * @return Returns the text.
     */
    public String getCustomText() {
        return customText;
    }
    /**
     * @param content The content to set.
     */
    public void setCustomText(String customText) {
        this.customText = customText;
    }
    /**
     * @return Returns the subject.
     */
    public String getSubject() {
        return subject;
    }
    /**
     * @param subject The subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    /**
     * @return Returns the to.
     */
    public String getTo() {
        return to;
    }
    /**
     * @param to The to to set.
     */
    public void setTo(String to) {
        this.to = to;
    }
}
