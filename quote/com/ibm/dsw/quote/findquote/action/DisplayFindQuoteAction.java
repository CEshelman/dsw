package com.ibm.dsw.quote.findquote.action;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteClassificationCodeFactory;
import com.ibm.dsw.quote.common.util.LOBListUtil;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author wangxu@cn.ibm.com
 */
public abstract class DisplayFindQuoteAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	QuoteBaseContract quoteBaseContract = (QuoteBaseContract)contract;
        handler.addObject(FindQuoteParamKeys.OVERALL_STATUS_LIST, CacheProcessFactory.singleton().create()
                .findOverallStatus(quoteBaseContract.getLocale()));
        handler.addObject(FindQuoteParamKeys.LOB_LIST, LOBListUtil.getLobs(quoteBaseContract.getLocale(),quoteBaseContract.getQuoteUserSession().getAudienceCode()));
        handler.addObject(FindQuoteParamKeys.CLASSIFICATION_LIST, QuoteClassificationCodeFactory.singleton().getAllQuoteClassfctnCodes());
        handler.addObject(FindQuoteParamKeys.ACTUATION_LIST, CacheProcessFactory.singleton().create().getAcquisitionList());
        handler.setState(getState(contract));
        handler.addObject(ParamKeys.PARAM_LOCAL, ((QuoteBaseContract) contract).getLocale());
        return executeProcess(contract, handler);
    }

    protected abstract ResultBean executeProcess(ProcessContract contract, ResultHandler handler)
            throws QuoteException, ResultBeanException;

    protected abstract String getState(ProcessContract contract);

}
