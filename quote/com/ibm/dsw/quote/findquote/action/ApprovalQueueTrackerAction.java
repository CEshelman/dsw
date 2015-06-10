package com.ibm.dsw.quote.findquote.action;

import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.ApprovalQueueTrackerContract;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author wangxu@cn.ibm.com
 */
public class ApprovalQueueTrackerAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        ApprovalQueueTrackerContract approvalQueuecontract = (ApprovalQueueTrackerContract) contract;

        SearchResultList results = QuoteStatusProcessFactory.singleton().create().findApprovalQueue(
                approvalQueuecontract.getUserId(), approvalQueuecontract.getReportingSalesReps(),
                approvalQueuecontract.getQueueType(), approvalQueuecontract.getSortFilter(),
                approvalQueuecontract.getPageIndex(), getPageSize(), getEcareFlag(approvalQueuecontract));

        javax.servlet.http.Cookie cookie = approvalQueuecontract.getSqoCookie();
        QuoteCookie.setAprQueueTrackerType(cookie, approvalQueuecontract.getQueueType());
        QuoteCookie.setAprQueueTrackerSortFilter(cookie, approvalQueuecontract.getSortFilter());

        handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
        handler.setState(getState(contract));
        handler.addObject(ParamKeys.PARAM_LOCAL, approvalQueuecontract.getLocale());
        handler.addObject(FindQuoteParamKeys.APPROVAL_QUEUE_TRACKER_CONTRACT, approvalQueuecontract);
        handler.addObject(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC, CacheProcessFactory.singleton().create()
                .getCountryListAsCodeDescObj());

        return handler.getResultBean();
    }

    /**
     * @param findQuoteContract
     * @return
     */
    public String getEcareFlag(ApprovalQueueTrackerContract contract) {
        if (contract.getUser().getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_ECARE)
            return "1";
        return "0";
    }

    protected String getPageSize() {
        return FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE;
    }

    protected String getState(ProcessContract contract) {
        return FindQuoteStateKeys.STATE_DISPLAY_APPROVAL_QUEUE_TRACKER;
    }

    protected String getValidationForm() {
        return "loadApprovalQueue";
    }
}
