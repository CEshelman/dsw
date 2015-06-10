package com.ibm.dsw.quote.home.action;

import java.util.List;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SessionQuoteRightColumnBaseAction<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 5, 2007
 */

public abstract class QuoteRightColumnBaseAction extends BaseContractActionHandler {

    /**
     * This method implements the logic of right column.
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        //get creatorId from Contract
        QuoteBaseContract quoteBaseContract = (QuoteBaseContract) contract;
        String creatorId = quoteBaseContract.getUserId();
        
        // get session quote information for right column
        QuoteRightColumn sessionQuote = null;
        List headLineMsg = null;
        if (creatorId != null && !"".equals(creatorId)) {
	        QuoteProcess process = QuoteProcessFactory.singleton().create();
	        sessionQuote = process.getQuoteRightColumnInfo(creatorId);
	        
	        String applicationCode = null;
	        
	        if(QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equalsIgnoreCase(quoteBaseContract.getQuoteUserSession().getAudienceCode())){
	        	applicationCode = QuoteConstants.APP_CODE_SQO;
	        }else{
	        	applicationCode = QuoteConstants.APP_CODE_PGS_HEADLINE;
	        }
	        
	        headLineMsg = process.getSQOHeadLineMsg(applicationCode);
        }
        handler.addObject(ParamKeys.PARAM_QUOTE_RIGHTCOLUMN, sessionQuote);
        handler.addObject(ParamKeys.PARAM_SQO_HEADLINE_MSG, headLineMsg);
        handler.addObject(ParamKeys.PARAM_USER_OBJECT, quoteBaseContract.getUser());
        handler.setState(getState(contract));
        return executeProcess(contract, handler);
    }

    /**
     * To implement the action's logic in this method
     * @param contract
     * @param handler
     * @return
     * @throws QuoteException
     * @throws ResultBeanException
     */
    public abstract ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException;
    
    /**
     * Return the state code in this method.
     * @param contract
     * @return
     */
    protected abstract String getState(ProcessContract contract);

}
