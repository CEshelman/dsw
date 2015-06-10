/*
 * Created on 2007-7-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.viewbean;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.viewbean.DisplayAddAttachmentsViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author helenyu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AttachFilesToQuoteViewBean extends DisplayAddAttachmentsViewBean {
    
    private String userRole;
    
    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    public void collectResults(Parameters params) throws ViewBeanException {       
        userRole = params.getParameterAsString(ParamKeys.PARAM_USER_ROLE);
        
        logContext.debug(this,"End of AttachFilesToQuoteViewBean.collectResults");
        super.collectResults(params);
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    protected String genRemoveAttachmentURL(String quoteNum, String fileNum) {
    	String targetParams = ","+DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM+"=" + StringEncoder.textToHTML(quoteNum) 
    		+ ",attchmtSeqNum=" + StringEncoder.textToHTML(fileNum);
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        StringBuffer sb = new StringBuffer();
        String targetAction = DraftQuoteActionKeys.REMOVE_SB_ATTACHMENT_ACTION;
        String secondAction = DraftQuoteActionKeys.DISPLAY_SPECIAL_BID_TAB;
        
        sb.append(actionKey).append("=").append(targetAction);
        if (StringUtils.isNotBlank(secondAction))
            sb.append("," + appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY) + "=").append(
                    secondAction);
        if (StringUtils.isNotBlank(targetParams))
            sb.append(",").append(targetParams);
        return sb.toString();
    }
}