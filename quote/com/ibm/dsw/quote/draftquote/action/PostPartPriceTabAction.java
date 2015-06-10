package com.ibm.dsw.quote.draftquote.action;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.OfferPriceException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.action.validator.PartPriceUIValidator;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract.LineItemParameter;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.StateKeys;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PostPartPriceTabAction</code>
 * 
 * 
 * @author: liuxinlx@cn.ibm.com
 * 
 * Creation date: 2007-3-20
 */
public class PostPartPriceTabAction extends PostDraftQuoteBaseAction {


    protected boolean innerValidate(ProcessContract contract){
        PartPriceUIValidator validator = PartPriceUIValidator.create(this, (PostPartPriceTabContract) contract);

        try {
            if (!validator.validate(contract,true)) {
                return false;
            }

        } catch (QuoteException e) {
            logContext.fatal(this, "validate part price data error:" + e.getMessage());
            return false;
        }

        return true;
    }
    
    /**
     * UI input validation
     */
    protected boolean validate(ProcessContract contract) {
        if (!super.validate(contract)) {
            return false;
        }
        
        HashMap map = new HashMap();
        PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;
        Date headerCRAD = ct.getCustReqstdArrivlDate();
        if (ct.getItems().size()!=0){
        	for (Object key :  ct.getItems().keySet()) {
        		LineItemParameter item = (LineItemParameter) ct.getItems().get(key);
        		if(item.appSendtoMFG!=null && item.appSendtoMFG&&(
        				(item.lineItemCRAD!=null&&!item.lineItemCRAD.equals(item.custReqArrlDate))|| item.lineItemCRAD==null && item.custReqArrlDate!=null) ){
        			Boolean isLineItemCRADValid = ct.isCustReqstdArrivlDateValid(item.custQliReqstdArrivlYear,item.custQliReqstdArrivlMonth
        				,item.custQliReqstdArrivlDay);
        			if (!isLineItemCRADValid){
    					FieldResult field = new FieldResult();
    			        field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_CUST_REQSTD_ARRIVL_DATE);
    			        field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, MessageKeys.CUST_REQSTD_ARRIVL_DATE);
    			        map.put(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_SUFFIX, field);
    			        addToValidationDataMap(ct, map);
    			        return false;
        			} 
        		}
        	}
        }
        return innerValidate(contract);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.action.PostDraftQuoteBaseAction#postDraftQuoteTab(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        
        PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;
        if(!checkPartLimitExceedLimit(ct, handler)){
            return;
        }

        innerPostPartPriceTab(ct, handler);
        
        // when delete appliance line item, call sp IU_QT_LINE_ITEM_ADDRESS to update ship-to/install-at address
        try{
            CustomerProcess custProcess = null;
			try {
				custProcess = CustomerProcessFactory.singleton().create();
			} catch (TopazException e) {
				logContext.fatal(this, e.getMessage());
	            throw new QuoteException(e);
			}
			custProcess.updateLineItemAddr(ct.getWebQuoteNum(), ct.getUserId()); 
        } catch (QuoteException e) {
            logContext.fatal(this, e.getMessage());
            throw new QuoteException(e);
        }
        
    }
    
    protected void innerPostPartPriceTab(ProcessContract contract, ResultHandler handler)
                         throws QuoteException{
        
        PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;
        PartPriceProcess process;
        try {
            process = PartPriceProcessFactory.singleton().create();
        } catch (QuoteException e) {
            logContext.fatal(this, e.getMessage());
            throw new QuoteException(e);
        }
        if (ct.getItems().values().size() == 0) {
            // only update expire date
            Quote quote = ct.getQuote();
            QuoteHeader header = quote.getQuoteHeader();
            try {
                if(!CommonServiceUtil.quoteIsDraftBidItrtn(header)){
                	QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
            		quoteProcess.updateQuoteHeaderCustPrtnrTab(ct.getUserId(), ct.getExpireDate(),
                            header.getFulfillmentSrc(), header.getRnwlPrtnrAccessFlag() == -1 ? null : String.valueOf(header.getRnwlPrtnrAccessFlag()), 
                            header.isResellerToBeDtrmndFlag() ? 1 : 0, header.isDistribtrToBeDtrmndFlag() ? 1 : 0, 
                            ct.getQuoteClassfctnCode(), ct.getStartDate(),
                            ct.getOemAgrmntType(), ct.getPymntTermsDays(), ct.getOemBidType(),
                            ct.getEstmtdOrdDate(), ct.getCustReqstdArrivlDate(), ct.getSspType());
                }
            } catch (QuoteException e1) {
                throw new QuoteException(e1);
            }
            logContext.debug(this, "no parts in the quote, no need to perform the post");
            return;

        }

        if (ct.isRenwalQuote() && !ct.isRqEditable()) {
            logContext.debug(this, "Renewal Qutoe, but not editable");
            return;
        }
        
        try{
            process.postPartPriceInfo(ct);
        }catch(OfferPriceException e){
            String errorMessage = getI18NString(e.getMessageKey(), I18NBundleNames.ERROR_MESSAGE, ct.getLocale());
            MessageBean mBean = MessageBeanFactory.create();
            mBean.addMessage(errorMessage, MessageBeanKeys.INFO);
            handler.setMessage(mBean);
        }
    }
    
    protected boolean checkPartLimitExceedLimit(PostPartPriceTabContract ct, ResultHandler handler){
        if(ct.isExceedLimit()){
            String errorMessage = getI18NString(DraftQuoteMessageKeys.PART_EXCEED_LIMIT_MSG, 
                                        MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, ct.getLocale());
            String formattedMsg = MessageFormat.format(errorMessage, 
                                          new String[]{PartPriceConfigFactory.singleton().getElaLimits() + "", PartPriceConfigFactory.singleton().getAppliLimits()+""});
            MessageBean mBean = MessageBeanFactory.create();
            
            mBean.addMessage(formattedMsg, MessageBeanKeys.ERROR);
            handler.setMessage(mBean);
            
			ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
			
            handler.setState(context.getValueAsString(StateKeys.JADE_UNDO_STATE_KEY));
            
            return false;
        }
        
        return true;
    }

}