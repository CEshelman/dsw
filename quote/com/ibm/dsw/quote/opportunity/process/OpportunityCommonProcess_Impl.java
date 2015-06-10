package com.ibm.dsw.quote.opportunity.process;

import java.util.List;

import com.ibm.dsw.quote.opportunity.domain.Opportunity;
import com.ibm.dsw.quote.opportunity.exception.OpportunityDSException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>OpportunityCommonProcess</code> class. 
 * 
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2012-02-15
 */
public abstract class OpportunityCommonProcess_Impl extends  TopazTransactionalProcess implements OpportunityCommonProcess{
	
	/**
	 *  fetch all valid opportunity number and name by the EMP internet ID. need to connect EIW database.
	 * @param webQuoteNum
	 * @return Opportunity object List
	 * @throws OpportunityDSException
	 */
	public abstract List<Opportunity> getValidOpportunityListByWebQuote(String webQuoteNum)throws OpportunityDSException;
	
	/**
	 *  used to judge if the provided opportunity number is valid. need to connect EIW database.
	 * @param opptNum
	 * @return if true, the opportunity number is valid, else is invalid.
	 * @throws OpportunityDSException
	 */
	public abstract boolean isValidOpptNum(String opptNum)throws OpportunityDSException;
}
