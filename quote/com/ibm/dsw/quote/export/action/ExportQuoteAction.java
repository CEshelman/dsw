package com.ibm.dsw.quote.export.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.export.config.ExportQuoteParamKeys;
import com.ibm.dsw.quote.export.contract.ExportContract;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.bean.StateBean;
import com.ibm.ead4j.jade.bean.StateBeanFactory;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>ExportQuoteAction</code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-19
 */
public abstract class ExportQuoteAction extends BaseContractActionHandler {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
    	ByteArrayOutputStream bos = null;
    	try {
        	ExportContract exportContract = (ExportContract) contract;
            bos = new ByteArrayOutputStream();
//            ExportQuoteProcess eqProcess = ExportQuoteProcessFactory.sigleton().create();
//            String exportedQuoteNum = eqProcess.exportQuoteAsExcel(bos, userId);
            
            String exporttedQuoteNum = this.export(exportContract, bos);
            String exportType = (exportContract.isRTFDownload()?"RTF":"");
            
            handler.addObject(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_STR, bos.toString("UTF-8"));
            handler.addObject(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_NUM, exporttedQuoteNum);
            handler.addObject(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_DATA, bos.toByteArray());
            handler.addObject(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_RTF, exportType);
            
            addExtraParams(handler);
            
            logContext.debug(this, exporttedQuoteNum + " has been exported");
            handler.setState(getState(contract));
//        } catch (InvildPartQtyException ie) {
//        	 logContext.error(this, "InvildPartQtyException " + LogThrowableUtil.getStackTraceContent(ie));
//             return handleErrorMsg(contract, handler, ie.getMessageKey());
        } catch (ExportQuoteException e) {
            logContext.error(this, "ExportQuoteException " + LogThrowableUtil.getStackTraceContent(e));
             return handleErrorMsg(contract, handler, e.getMessageKey());
        }  catch (IOException ioe) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(ioe));
            throw new QuoteException(ioe) ;
        }finally{
        	try {
				bos.close();
			} catch (IOException e) {
				logContext.error(this, "Exception on closing ByteArrayOutputStream: " + LogThrowableUtil.getStackTraceContent(e));
			}
        }
        return handler.getResultBean();
    }

    /**
	 * @param exportContract
	 * @param bos
	 * @return
     * @throws QuoteException
	 */
	protected abstract String export(ExportContract exportContract, ByteArrayOutputStream bos) throws ExportQuoteException, QuoteException;

	/**
     * @param contract
     * @return
     */
	protected abstract String getState(ProcessContract contract) ;
    
    /**
     * @param contract
     * @param handler
     * @return
     * @throws ResultBeanException
     */
    private ResultBean handleErrorMsg(ProcessContract contract, ResultHandler handler, String mesgKey) throws ResultBeanException {
        QuoteBaseContract baseContract = (QuoteBaseContract) contract;
        Locale loc = baseContract.getLocale();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        StateBean stateBean = StateBeanFactory.create(context.getConfigParameter(
        		com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY));
        handler.setState(stateBean);
        String message = super.getI18NString(mesgKey,super.getBaseName(),loc);
        MessageBean mBean = MessageBeanFactory.create();
        mBean.addMessage(message, MessageBeanKeys.ERROR);
        handler.setMessage(mBean);
        return handler.getResultBean();
    }
    
    protected void addExtraParams(ResultHandler handler){
        
    }
}
