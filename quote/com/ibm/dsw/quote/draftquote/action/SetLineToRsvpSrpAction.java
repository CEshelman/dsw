package com.ibm.dsw.quote.draftquote.action;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
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
 * The <code>SetLineToRsvpSrpAction</code> class is to set line to rsp/srp
 * for a draft quote
 * 
 * @author <a href="mailto:liguotao@cn.ibm.com">guotao </a> <br/>
 *  
 */
public class SetLineToRsvpSrpAction extends PostPartPriceTabAction {

	private static final long serialVersionUID = -2415453285434840274L;
	 @Override
		protected void innerPostPartPriceTab(ProcessContract contract,
	            ResultHandler handler) throws QuoteException {
	        PostPartPriceTabContract ct = (PostPartPriceTabContract)contract;

	        if (ct.getItems().values().size() == 0) {
	            logContext.debug(this, "no parts in the quote, no need to perform the post");
	            return;

	        }
	        PartPriceProcess process;
	        process = PartPriceProcessFactory.singleton().create();
	        process.resetToRsvpSrp(ct);
	    }

	 
 }
