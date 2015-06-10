package com.ibm.dsw.quote.draftquote.action;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ApplianceLineItem;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.LineItemAddress;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.contract.LineItemAddressDetailsContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

public class LineItemAddressDetailsAction extends BaseContractActionHandler {

	private static final long serialVersionUID = 2729356600145389437L;

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException 
	{		
		logContext.debug(this, "Fetching line item address details");
		LineItemAddressDetailsContract c = (LineItemAddressDetailsContract) contract;
		String webQuoteNum = c.getWebQuoteNum()==null?"":c.getWebQuoteNum();
		String bpSiteNum = "";
		if(c.isPGSEnv()){
			bpSiteNum = c.getQuoteUserSession().getSiteNumber();
		}
		List<LineItemAddress> liAddresses = null;
		try{
			liAddresses = new ArrayList<LineItemAddress>();
			
			List quoteLineItems = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(webQuoteNum);
			
			Quote quote = QuoteProcessFactory.singleton().create().getQuoteForApplianceAddress(webQuoteNum);
			
			CustomerProcess custProcess = CustomerProcessFactory.singleton().create();
			ApplianceLineItemAddrDetail list = custProcess.findLineItemAddr(webQuoteNum,bpSiteNum, quoteLineItems, true, quote.getCustomer());
			List itemlist = list.getLineItemsList();
			for(Object obj : itemlist){
				logContext.debug(this, "QuoteLineItemSeqNum="+ ((ApplianceLineItem)obj).getQuoteLineItemSeqNum() +"  QuoteSectnSeqNum,="+((ApplianceLineItem)obj).getQuoteSectnSeqNum());
				LineItemAddress lia = new LineItemAddress();
				lia.copy((ApplianceLineItem)obj);
				liAddresses.add(lia);
			}
			
		} catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException("error executing topaz process", e);
        } 
		handler.addObject(ParamKeys.LINE_ITEM_ADDRESSES, liAddresses);
		handler.setState(StateKeys.STATE_LINE_ITEM_ADDRESSES_JSON);
		return handler.getResultBean();
	}

}
