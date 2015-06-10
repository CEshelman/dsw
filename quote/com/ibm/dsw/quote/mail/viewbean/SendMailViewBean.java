package com.ibm.dsw.quote.mail.viewbean;

import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.mail.config.MailActionKeys;
import com.ibm.dsw.quote.mail.config.MailParamKeys;
import com.ibm.dsw.quote.mail.config.MailViewKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SendMailViewBean</code> class is the view bean for sending mail.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Apr 4, 2007
 */
public class SendMailViewBean extends BaseViewBean {
    private String srcAction;

    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        srcAction = params.getParameterAsString(MailParamKeys.PARAM_SRC_ACTION);
    }

    public String getBaseName() {
        return MailViewKeys.MESSAGE_BASE_NAME;
    }

    public String getActionUrl() {
        return HtmlUtil.getURLForAction(MailActionKeys.SEND_MAIL);
    }

    /**
     * @return Returns the srcAction.
     */
    public String getSrcAction() {
        return srcAction;
    }

    public String getPkSrcAction() {
        return MailParamKeys.PARAM_SRC_ACTION;
    }
}
