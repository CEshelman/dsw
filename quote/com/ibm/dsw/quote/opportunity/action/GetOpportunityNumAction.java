package com.ibm.dsw.quote.opportunity.action;

import java.util.List;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteStateKeys;
import com.ibm.dsw.quote.opportunity.contract.GetOpportunityNumContract;
import com.ibm.dsw.quote.opportunity.domain.Opportunity;
import com.ibm.dsw.quote.opportunity.exception.OpportunityDSException;
import com.ibm.dsw.quote.opportunity.process.OpportunityCommonProcess;
import com.ibm.dsw.quote.opportunity.process.OpportunityCommonProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

public class GetOpportunityNumAction extends BaseContractActionHandler implements NewQuoteStateKeys{

	private static final long serialVersionUID = -7951168763278491357L;
	private static final String VALIDE_RETURN_CODE= "0";
	private static final String NOT_CONNECT_RETURN_CODE= "1";

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		GetOpportunityNumContract getOpportunityNumContract = (GetOpportunityNumContract)contract;
		String webQuoteNum = getOpportunityNumContract.getWebQuoteNum();
		logContext.debug(this, "get opportunity numbers by webQuoteNum: " + webQuoteNum);
		OpportunityCommonProcess opportunityCommonProcess;
		opportunityCommonProcess = OpportunityCommonProcessFactory.singleton().create();
		try
		{	
			List<Opportunity> oppNumList = opportunityCommonProcess.getValidOpportunityListByWebQuote(webQuoteNum);
			handler.addObject( NewQuoteParamKeys.PARAM_OPPORTUNIRY_NUMS, oppNumList );
			handler.addObject( NewQuoteParamKeys.PARAM_RETURN_OPPNUM_CODE, VALIDE_RETURN_CODE);
		}
		catch (OpportunityDSException e){
			logContext.error(this, e.getMessage());
			handler.addObject( NewQuoteParamKeys.PARAM_RETURN_OPPNUM_CODE, NOT_CONNECT_RETURN_CODE);
		}

		handler.setState( NewQuoteStateKeys.OPPORTUNITY_NUM_JSON );
		return handler.getResultBean();
	}
	
}