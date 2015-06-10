package com.ibm.dsw.quote.findquote.action;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayEvaluatorQueueContract;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcessFactory;
import com.ibm.dsw.quote.home.action.QuoteRightColumnBaseAction;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author wangxu@cn.ibm.com
 */
public class DisplayEvaluatorQueueAction extends QuoteRightColumnBaseAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
    	DisplayEvaluatorQueueContract evaluatorQueuecontract = (DisplayEvaluatorQueueContract) contract;

        SearchResultList results = QuoteStatusProcessFactory.singleton().create().findEvaluatorQueue(
        		evaluatorQueuecontract.getUserId(), evaluatorQueuecontract.getQueueType(), 
        		evaluatorQueuecontract.getSortFilter(),evaluatorQueuecontract.getSearchType() ,evaluatorQueuecontract.getSearchInfo(),evaluatorQueuecontract.getPageIndex(), getPageSize());

        if (evaluatorQueuecontract.getMarkFilterDefault() != null
                && !evaluatorQueuecontract.getMarkFilterDefault().equals("")) {
            javax.servlet.http.Cookie cookie = evaluatorQueuecontract.getSqoCookie();
            QuoteCookie.setAprQueueType(cookie, evaluatorQueuecontract.getQueueType());
            QuoteCookie.setAprQueueSortFilter(cookie, evaluatorQueuecontract.getSortFilter());
            QuoteCookie.setAprSearchType(cookie, evaluatorQueuecontract.getSearchType());
            
        }

        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        handler.setState(getState(contract));
        handler.addObject(ParamKeys.PARAM_LOCAL, evaluatorQueuecontract.getLocale());
        handler.addObject(FindQuoteParamKeys.DISPLAY_EVALUATOR_QUEUE_CONTRACT, evaluatorQueuecontract);
        handler.addObject(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC, CacheProcessFactory.singleton().create()
                .getCountryListAsCodeDescObj());
        return handler.getResultBean();
    }

    /**
     * @param findQuoteContract
     * @return
     */
    public String getEcareFlag(DisplayEvaluatorQueueContract contract) {
        if (contract.getUser().getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_ECARE)
            return "1";
        return "0";
    }

    protected String getPageSize() {
        return FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE;
    }

    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_EVALUATOR_QUEUE;
    }

    protected String getValidationForm() {
        return "loadApprovalQueue";
    }
}
