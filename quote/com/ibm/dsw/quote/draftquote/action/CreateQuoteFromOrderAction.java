package com.ibm.dsw.quote.draftquote.action; 

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.DBConstants;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.CreateQuoteFromOrderContract;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.ps.domain.PartSearchPart;
import com.ibm.dsw.quote.ps.domain.PartSearchResult;
import com.ibm.dsw.quote.ps.process.PartSearchProcess;
import com.ibm.dsw.quote.ps.process.PartSearchProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>CreateQuoteFromOrderAction<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: Oct 18, 2010
 */
public class CreateQuoteFromOrderAction extends BaseContractActionHandler {

    @Override
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        CreateQuoteFromOrderContract cqContract = (CreateQuoteFromOrderContract) contract;
        Quote quote = null;
        QuoteHeader quoteHeader = null;
        String orderNum = cqContract.getOrderNum();
        String userId = cqContract.getUserId();
        int errorCode = 0;
        Map result = new HashMap();
        String webQuoteNum = "";
        List allLineItemList;
        
        if (StringUtils.isBlank(orderNum) || (StringUtils.isBlank(userId))) {
            return handleInvalidInputParams(handler, cqContract.getLocale());
        }
        
        try {
            QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
            result = qProcess.createQuoteFromOrder(userId, orderNum);
            errorCode = (Integer)result.get("errorCode");
            webQuoteNum = (String)result.get("webQuoteNum");
            //if create quote failed then bring into error msg page
            if (DBConstants.DB2_SP_RETURN_LOB_INVALID == errorCode || DBConstants.DB2_SP_RETURN_ACQUISITION_INVALID == errorCode) {
                handler.setState(DraftQuoteStateKeys.STATE_CREATE_FROM_ORDER_ERROR);
                logContext.info(this, "parameter from reporting side p1:" + cqContract.getBackToOrdHistRptUrlParam());
                handler.addObject(ParamKeys.PARAM_CREATE_QT_FROM_ORDER_URL, cqContract.getBackToOrdHistRptUrlParam());
            }
            //if create quote successfully then display cust/parts tab
            if (StringUtils.isNotEmpty(webQuoteNum)) {
                
                String redirectURL = (String)getRedirectURL();
                
                handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
                handler.setState(StateKeys.STATE_REDIRECT_ACTION);
                //get quote header and line items info
                quote = qProcess.getQuoteDetailsForCreateQtFromOrder(webQuoteNum);
                
                quoteHeader = quote.getQuoteHeader();
                CodeDescObj quoteLob = quoteHeader.getLob();
                String lob = StringUtils.trimToEmpty(quoteLob.getCode());
                
                allLineItemList = quote.getLineItemList();
                List allLineItemNumList = this.getPartNumList(allLineItemList);
                List inValidLineItemPartNumList = new ArrayList();
                List inValidLineItemPartSeqNumList = new ArrayList();
                
                //validate parts:provide error msg to show invalid parts on cust/partner tab,and delete invalid parts against quote num
                if (!allLineItemList.isEmpty()) {
                    logContext.debug(this, "Get all validte parts info against quote num: " + webQuoteNum);
                    // get valid parts list
                    PartSearchProcess partProcess = PartSearchProcessFactory.singleton().create();
                    PartSearchResult selectedParts = partProcess.searchPartsByNumber(this.concatAllPartsNumAsStr(allLineItemList),
                            StringUtils.EMPTY, lob, quoteHeader.getAcqrtnCode(), quoteHeader.getCountry().getCode3(),
                            NewQuoteDBConstants.AUDIENCE);
                    
                    logContext.debug(this, "Parts search successful!!");
                    // compare valid parts list with all line items to determine
                    // invalid parts list
                    for (Iterator partIt = selectedParts.getParts().iterator(); partIt.hasNext();) {
                        // remove valid partNum
                        PartSearchPart part = (PartSearchPart) partIt.next();
                        String partNum = StringUtils.trimToEmpty(part.getPartID());
                        allLineItemNumList.remove(partNum);
                    }

                    // initialize invalid parts list
                    inValidLineItemPartNumList = allLineItemNumList;
                    
                    logContext.debug(this, "All invalid parts: " + inValidLineItemPartNumList.toString());
                    
                    if (!inValidLineItemPartNumList.isEmpty()) {
                        // if there are invalid parts then add them to URL
                        redirectURL = HtmlUtil.addURLParam(new StringBuffer(redirectURL),
                                NewQuoteParamKeys.PARAM_INVALID_PARTS_WARNING,
                                generateInvalidPartInfo(inValidLineItemPartNumList)).toString();
                        // get invalid parts sequence num by part num
                        for (Iterator inValidPartIter = inValidLineItemPartNumList.iterator(); inValidPartIter.hasNext();) {
                            String inValidPartNum = inValidPartIter.next().toString().trim();
                            for (Iterator it = allLineItemList.iterator(); it.hasNext();) {
                                QuoteLineItem qli = (QuoteLineItem) it.next();
                                String lineItemNum = qli.getPartNum();
                                if (lineItemNum.equals(inValidPartNum)) {
                                    inValidLineItemPartSeqNumList.add(qli.getSeqNum());
                                    continue;
                                }
                            }
                        }
                        // if there are invalid parts then delete them from db
                        if (!inValidLineItemPartSeqNumList.isEmpty()) {
                            logContext.debug(this, "delete all invalid parts from DB");
                            qProcess.delInvalidLineItemsByWebQuoteNum(webQuoteNum, inValidLineItemPartSeqNumList);
                        }
                    }

                }
                
                handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
                
            }
            
           
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            throw e;
        }catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }catch (RemoteException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
        return handler.getResultBean();
    }
    private Object getRedirectURL() {
        // TODO Auto-generated method stub
        return HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB);
    }
    
    /**
     * Generate parts num string separate by ',' from part num list
     * @param partNumList
     * @return
     */
    public String concatAllPartsNumAsStr(List partNumList) {
        String partNums = "";
        if(null != partNumList && !partNumList.isEmpty()){
            StringBuffer buffer = new StringBuffer();
            for(Iterator it = partNumList.iterator(); it.hasNext();){
                QuoteLineItem qli = (QuoteLineItem)it.next();
                String partNum = qli.getPartNum();
                buffer.append(partNum + NewQuoteDBConstants.DELIMIT);
            }
            partNums = buffer.toString();
            partNums = partNums.substring(0, partNums.length()-1);
        }
        return partNums;
    }
    /**
     * Get undupicated parts num list by All line items object list
     * @param partList
     * @return
     */
    public List getPartNumList(List partList) {
        List partNumsList = new ArrayList();
        if(null != partList && !partList.isEmpty()){
            for(Iterator it = partList.iterator(); it.hasNext();){
                QuoteLineItem qli = (QuoteLineItem)it.next();
                String partNum = qli.getPartNum();
                if(!partNumsList.contains(partNum)) {
                    partNumsList.add(partNum);
                }
            }
        }
        return partNumsList;
    }
    private String generateInvalidPartInfo(List invalidPartList){
        StringBuffer partsBuffer = new StringBuffer();
      
        if( null != invalidPartList && !invalidPartList.isEmpty()){
            for(Iterator it = invalidPartList.iterator(); it.hasNext();) {
                partsBuffer.append(it.next() + NewQuoteDBConstants.PART_DELIMIT);
            }
        }
        
         return  partsBuffer.reverse().deleteCharAt(0).reverse().toString();
      
    }
   
    protected ResultBean handleInvalidInputParams(ResultHandler handler, Locale locale) {
        
        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        String errorMsg = this.getI18NString(NewQuoteMessageKeys.MSG_URL_PARAM_NOT_VALID,
                MessageKeys.BUNDLE_APPL_I18N_NEW_QUOTE, locale);
        messageBean.addMessage(errorMsg, MessageBeanKeys.ERROR);
        resultBean.setMessageBean(messageBean);
        handler.setState(com.ibm.ead4j.jade.config.StateKeys.JADE_UNDO_STATE_KEY);
        return resultBean;

    }
}
 