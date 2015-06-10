package com.ibm.dsw.quote.mail.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.common.validator.ValidatorMessageKeys;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.export.exception.InvildPartQtyException;
import com.ibm.dsw.quote.export.process.ExportQuoteProcessFactory;
import com.ibm.dsw.quote.mail.config.MailMessageKeys;
import com.ibm.dsw.quote.mail.config.MailParamKeys;
import com.ibm.dsw.quote.mail.config.MailViewKeys;
import com.ibm.dsw.quote.mail.contract.SendMailContract;
import com.ibm.dsw.quote.mail.process.MailProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SendMailAction</code> class is to send mail with quote attched to
 * any user
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Apr 4, 2007
 */
public class SendMailAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	
    	LogContext logger = LogContextFactory.singleton().getLogContext();
    	 
        SendMailContract c = (SendMailContract) contract;

        StringBuffer sb = new StringBuffer();

        sb.append(getI18NString(MailMessageKeys.MAIL_QUOTE_COMMENT, MailViewKeys.MESSAGE_BASE_NAME, c.getLocale()));
        sb.append("\n\r");
        sb.append(c.getCustomText());

        String msg;
        
        ByteArrayOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            bos = new ByteArrayOutputStream();
            String webQuoteNum = ExportQuoteProcessFactory.sigleton().create().exportQuoteAsSpreadSheet(bos, c.getUser(), c.getQuoteUserSession());
            File attachFile = new File(System.getProperty("java.io.tmpdir"), "ExportQuote_" + webQuoteNum + ".xls");
            fos = new FileOutputStream(attachFile);
            bos.writeTo(fos);
            
            MailProcessFactory.singleton().create().sendQuote(c.getUserId(), c.getTo(), c.getCc(), c.getSubject(),
                    sb.toString(), attachFile, "EXCEL file");

            attachFile.delete();
            msg = HtmlUtil.getTranMessageParam(MailViewKeys.MESSAGE_BASE_NAME, MailMessageKeys.MAIL_QUOTE_SUCCESS,
                    true, null);
        } catch (InvildPartQtyException ie) {
        	logger.error(this, LogThrowableUtil.getStackTraceContent(ie));
            msg = HtmlUtil.getTranMessageParam(super.getBaseName(), ie.getMessageKey(), false, null);
            logger.error(this, msg);
        } catch (Exception e) {
        	logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            msg = HtmlUtil.getTranMessageParam(MailViewKeys.MESSAGE_BASE_NAME, MailMessageKeys.MAIL_QUOTE_ERROR, false,
                    null);
        }finally{
        	try {
				fos.close();
			} catch (IOException e) {
				logger.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
            try {
				bos.close();
			} catch (IOException e) {
				logger.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }

        StringBuffer sb2 = new StringBuffer();
        sb2.append(HtmlUtil.getURLForAction(c.getSrcAction()));
        sb2.append("&");
        sb2.append(ParamKeys.PARAM_TRAN_MSG);
        sb2.append("=");
        sb2.append(msg);

        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, sb2.toString());
        //System.err.println(sb.toString());
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }

    protected boolean validate(ProcessContract contract) {
        if (!super.validate(contract)) {
            return false;
        }
        SendMailContract c = (SendMailContract) contract;
        EmailValidator ev = EmailValidator.getInstance();
        HashMap vMap = new HashMap();

        StringTokenizer st = new StringTokenizer(c.getTo(), ",");

        if (!st.hasMoreTokens()) {
            FieldResult fieldResult = new FieldResult();
            fieldResult.setMsg(MessageKeys.BUNDLE_BASE_I18N_VALIDATORMESSAGES,
                    ValidatorMessageKeys.VALIDATION_INVALID_EMAIL_KEY);
            fieldResult.addArg(c.getTo(), false);
            vMap.put(MailParamKeys.PARAM_TO, fieldResult);
        }

        while (st.hasMoreTokens()) {
            String email = st.nextToken();
            if (!ev.isValid(email)) {
                FieldResult fieldResult = new FieldResult();
                fieldResult.setMsg(MessageKeys.BUNDLE_BASE_I18N_VALIDATORMESSAGES,
                        ValidatorMessageKeys.VALIDATION_INVALID_EMAIL_KEY);
                fieldResult.addArg(email, false);
                vMap.put(MailParamKeys.PARAM_TO, fieldResult);
            }
        }

        if (StringUtils.isNotBlank(c.getCc())) {

            st = new StringTokenizer(c.getCc(), ",");

            if (!st.hasMoreTokens()) {
                FieldResult fieldResult = new FieldResult();
                fieldResult.setMsg(MessageKeys.BUNDLE_BASE_I18N_VALIDATORMESSAGES,
                        ValidatorMessageKeys.VALIDATION_INVALID_EMAIL_KEY);
                fieldResult.addArg(c.getCc(), false);
                vMap.put(MailParamKeys.PARAM_CC, fieldResult);
            }

            while (st.hasMoreTokens()) {
                String email = st.nextToken();
                if (!ev.isValid(email)) {
                    FieldResult fieldResult = new FieldResult();
                    fieldResult.setMsg(MessageKeys.BUNDLE_BASE_I18N_VALIDATORMESSAGES,
                            ValidatorMessageKeys.VALIDATION_INVALID_EMAIL_KEY);
                    fieldResult.addArg(email, false);
                    vMap.put(MailParamKeys.PARAM_CC, fieldResult);
                }
            }

        }

        //XXX current validator can't support multi message to one field

        if (vMap.size() > 0) {
            addToValidationDataMap(contract, vMap);
            return false;
        } else {
            return true;
        }
    }

    protected String getValidationForm() {
        return MailViewKeys.FORM_SEND_MAIL;
    }

}
