package com.ibm.dsw.quote.submittedquote.viewbean;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.domain.OrderDetail;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

public class DisplayOrderedItemDetailViewBean extends BaseViewBean {
	
	String webQuoteNum = "";
	int default_destSeqNum = 0;
	Integer destSeqNum = new Integer(default_destSeqNum);
	List<OrderDetail> orderDetailList = new ArrayList();
		
    public void collectResults(Parameters params) throws ViewBeanException {
    	super.collectResults(params);
    	webQuoteNum = params.getParameterAsString(SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM);
    	Object obj = params.getParameter(SubmittedQuoteParamKeys.PARAM_DEST_SEQ_NUM);
    	if ((obj != null) && (obj instanceof Integer))
    		destSeqNum = (Integer)obj;
    	Object value = params.getParameter(SubmittedQuoteParamKeys.PARAM_ORDERED_ITEM_DETAIL);
    	if (value != null)
    		orderDetailList = (List<OrderDetail>)value;
    }
    
    public List<OrderDetail> getOrderDetailList() {
    	return this.orderDetailList;
    }
    
    public String getWebQuoteNum() {
    	return this.webQuoteNum;
    }
    
    public int getDestSeqNum() {
    	if (this.destSeqNum != null)
    		return this.destSeqNum.intValue();
    	else
    		return default_destSeqNum;
    }
}
