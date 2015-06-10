package com.ibm.dsw.quote.partner.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.MigrateRequest_Impl;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcess;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcessFactory;
import com.ibm.dsw.quote.partner.config.PartnerViewKeys;
import com.ibm.dsw.quote.partner.contract.SelectPartnerContract;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SelectPartnerAction</code> class is action for partner selection.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-12
 */
public class SelectPartnerAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        
        SelectPartnerContract partnerContract = (SelectPartnerContract) contract;
		if (partnerContract.getPageFrom().equalsIgnoreCase(
				DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)) {
			
			MigrateRequest migrateResqt = new MigrateRequest_Impl();
			MigrationRequestProcess migrationReqProcess = MigrationRequestProcessFactory
					.singleton().create();
			migrateResqt=migrationReqProcess.getMigrtnReqDetailByReqNum(partnerContract.getMigrationReqNum());
			migrateResqt.setRequestNum(partnerContract.getMigrationReqNum());
			//don't update SOLD_TO_CUST_NUM column
			migrateResqt.setSoldToCustNum("");
			
			//config a updateDistrFlg=1 in distributorList.jsp;if updateDistrFlg=1,update distributor partner
			if(partnerContract.getUpdateDistrFlg().equalsIgnoreCase("1")){
				migrateResqt.setPayerCustNum(partnerContract.getPartnerNum());
				migrateResqt.setReslCustNum("");
			}else{
				migrateResqt.setPayerCustNum("");
				migrateResqt.setReslCustNum(partnerContract.getPartnerNum());
			}

			migrateResqt.setSapCtrctNum("");
			migrateResqt.setFulfillmentSrc("CHANNEL");
			try {
				TransactionContextManager.singleton().begin();
				migrationReqProcess.updateMigrateInfByRequestNum(migrateResqt,
						partnerContract.getUserId());
				TransactionContextManager.singleton().commit();
			} catch (TopazException e) {
				logContext.error(this, e.getMessage());
				throw new QuoteException(e);
			} finally {
				try {
					TransactionContextManager.singleton().rollback();
				} catch (TopazException te) {
					logContext.error(this, te,
							"problems raised when doing rollback ");
				}
			}

        	handler.addObject(DraftQuoteParamKeys.PAGE_FROM, partnerContract.getPageFrom());
          	handler.addObject(ParamKeys.PARAM_MIGRATION_REQSTD_NUM, partnerContract.getMigrationReqNum());
    		handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
	                    .getURLForAction(DraftQuoteActionKeys.DISPLAY_FCT2PA_CUST_PARTNER));
    		handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
    	}else{
	        QuoteProcess process = QuoteProcessFactory.singleton().create();	        
	        try {
		        TransactionContextManager.singleton().begin();
		        process.updateQuotePartnerInfo(partnerContract.getWebQuoteNum(), partnerContract.getLob(), partnerContract
		                .getPartnerNum(), partnerContract.getPartnerType(), partnerContract.getUserId());
		        TransactionContextManager.singleton().commit();
	        } catch (TopazException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
				throw new QuoteException(e);
			} finally {
				try {
					TransactionContextManager.singleton().rollback();
				} catch (TopazException te) {
					logContext.error(this, LogThrowableUtil.getStackTraceContent(te)
							);
				}
			}	        
	        boolean isSubmittedQuote = partnerContract.isSubmittedQuote();
	        if (isSubmittedQuote) {
	            
	            StringBuffer url = new StringBuffer();
	            url.append(HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB));
	            url.append("&" + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM + "=" + partnerContract.getWebQuoteNum());
	            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, url.toString());
	        }
	        else {
	            // to determine whether session quote exist
	            QuoteRightColumn rightColumn = process.getQuoteRightColumnInfo(partnerContract.getUserId());
	            if (null == rightColumn) {
	                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
	                return handler.getResultBean();
	            }
	
	            // redirect to cust & partner tab action
	            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
	                    .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));
	        }
    	}
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#getValidationForm()
     */
    protected String getValidationForm() {
        return PartnerViewKeys.SELECT_PARTNER_FORM;
    }
}
