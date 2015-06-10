package com.ibm.dsw.quote.dsj.action;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.dsj.contract.DsjBaseContract;
import com.ibm.dsw.quote.dsj.contract.DsjDisplayDraftQuoteContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
import org.apache.commons.lang.StringUtils;

public class DsjDisplayDraftQuoteAction extends DsjBaseAction {

	public ResultBean executeBiz(DsjBaseContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		// TODO Auto-generated method stub
		DsjDisplayDraftQuoteContract ct = (DsjDisplayDraftQuoteContract)contract;
        QuoteProcess process = QuoteProcessFactory.singleton().create();

        String creatorId = ct.getUserId();
        String customerNum = ct.getSiteNumber();
        String contractNum = ct.getAgreementNumber();
        String currency = ct.getCurrency();
        String endUserFlag = null;
        if(StringUtils.isNotBlank(customerNum)){
            process.updateQuoteHeaderCustInfo(creatorId, null, customerNum, contractNum, -1, currency,endUserFlag);
        }
        
        String webQuoteNumber = ct.getWebQuoteNumber();
        String partnerLob = ct.getPartnerLob();
        String partnerNum = ct.getPartnerNum();
        String partnerType = ct.getPartnerType();

        if(StringUtils.isNotBlank(partnerNum)){
            try {
    	        TransactionContextManager.singleton().begin();
    	        process.updateQuotePartnerInfo(webQuoteNumber, partnerLob, partnerNum, partnerType, creatorId);
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
        	
        }      
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, HtmlUtil
                .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        return handler.getResultBean();
	}
}
