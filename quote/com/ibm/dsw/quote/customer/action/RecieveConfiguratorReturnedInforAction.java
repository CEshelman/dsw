package com.ibm.dsw.quote.customer.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

public class RecieveConfiguratorReturnedInforAction extends BaseContractActionHandler{
	
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		PrepareConfiguratorRedirectDataBaseContract ct = (PrepareConfiguratorRedirectDataBaseContract)contract;
		
		String redirectAction = DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB;
        
//        String redirectURL = collectRederectURL(redirectAction,ct.getWebQuoteNum());
//        handler.addObject(ParamKeys.PARAM_REDIRECT_URL,redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		return handler.getResultBean();
	}
	    

    protected String collectRederectURL(String jadeAction, String webQuoteNum){
        StringBuffer redirectURL = new StringBuffer(HtmlUtil.getURLForAction(jadeAction));
        HtmlUtil.addURLParam(redirectURL, ParamKeys.PARAM_QUOTE_NUM, webQuoteNum);
        return redirectURL.toString();
    }

    

}
