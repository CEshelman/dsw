package com.ibm.dsw.quote.submittedquote.action;

import java.util.List;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.DisplayOrderedItemDetailContract;
import com.ibm.dsw.quote.submittedquote.domain.OrderDetail;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class DisplayOrderedItemDetailAction extends BaseContractActionHandler {
    private static final LogContext logger = LogContextFactory.singleton().getLogContext();
    
	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		
		DisplayOrderedItemDetailContract detailContract = (DisplayOrderedItemDetailContract)contract;
		String webQuoteNum = detailContract.getQuoteNum();
		int destSeqNum = detailContract.getDestSeqNum();
		
        if (webQuoteNum != null) {
            handler.addObject(SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM, webQuoteNum);
        } else {
            throw new QuoteException("Missing webQuoteNum.");
        }
        
        Integer objDestSeqNum = new Integer(destSeqNum);
        
        handler.addObject(SubmittedQuoteParamKeys.PARAM_DEST_SEQ_NUM, objDestSeqNum);
		
        //TODO validate destSeqNum
        
        try {
    		SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
    		List<OrderDetail> orderDetailList = process.getOrderDetails(webQuoteNum, destSeqNum);

    		if(orderDetailList != null){
                handler.addObject(SubmittedQuoteParamKeys.PARAM_ORDERED_ITEM_DETAIL, orderDetailList);
            }
        } catch(Exception e){
            logger.error(this, e);
            throw new QuoteException(e);
        }
        handler.setState(getState(contract));
        
        return handler.getResultBean();
	}
	
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.SimpleContractAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return SubmittedQuoteStateKeys.STATE_DISPLAY_ORDERED_ITEM_DETAILS;
    }
}
