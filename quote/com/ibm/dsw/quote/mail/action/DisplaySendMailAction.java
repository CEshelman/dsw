package com.ibm.dsw.quote.mail.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.mail.config.MailParamKeys;
import com.ibm.dsw.quote.mail.config.MailStateKeys;
import com.ibm.dsw.quote.mail.contract.DisplaySendMailContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayMailAction</code> class is to simply show the send email
 * page
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Apr 4, 2007
 */
public class DisplaySendMailAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {

        DisplaySendMailContract c = (DisplaySendMailContract) contract;
        handler.addObject(MailParamKeys.PARAM_SRC_ACTION, c.getSrcAction());
        handler.setState(MailStateKeys.STATE_SEND_MAIL);
        return handler.getResultBean();
    }

}
