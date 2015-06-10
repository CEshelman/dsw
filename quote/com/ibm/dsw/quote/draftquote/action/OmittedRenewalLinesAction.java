package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * <p>
 * Copyright 2006 by IBM Corporation All rights reserved.
 * </p>
 * 
 * <p>
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * </p>
 * 
 * The <code>OmittedRenewalLinesAction</code> class is to Review/update omitted renewal lines
 * for a draft quote
 * 
 * @author <a href="mailto:luoyafei@cn.ibm.com">Grover </a> <br/>
 *  
 */
public class OmittedRenewalLinesAction extends PostDraftQuoteBaseAction {

	private static final long serialVersionUID = -2415453285434840274L;

	protected void postDraftQuoteTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
        PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;
		if (null != ct.getWebQuoteNum()){
			TimeTracer tracer = TimeTracer.newInstance();
			PartPriceProcess process = PartPriceProcessFactory.singleton().create();
			process.reviewUpdateOmittedRenewalLine(ct.getWebQuoteNum());
			tracer.dump();
		}
    }
 }
