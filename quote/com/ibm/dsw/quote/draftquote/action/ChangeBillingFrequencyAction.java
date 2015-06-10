package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * 
 * @author xuyaojia@cn.ibm.com
 * 
 * Creation date: Jan 10, 2013
 */

public class ChangeBillingFrequencyAction extends PostPartPriceTabAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void innerPostPartPriceTab(ProcessContract contract, ResultHandler handler) throws QuoteException {

        PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;
        PartPriceProcess process = PartPriceProcessFactory.singleton().create();
        try{
            process.changeBillingFrequency(ct);
        }catch(QuoteException e){
            String  errorMessage = getI18NString(e.getMessageKey(), I18NBundleNames.ERROR_MESSAGE, ct.getLocale());
            MessageBean mBean = MessageBeanFactory.create();
            mBean.addMessage(errorMessage, MessageBeanKeys.INFO);
            handler.setMessage(mBean);
        }
    }
}
