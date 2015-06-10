/**
 * Copyright 2012 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidBaseAction_jdbc<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: January 16, 2012
 */

package com.ibm.dsw.quote.relatedbid.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.relatedbid.contract.RelatedBidsContract;
import com.ibm.dsw.quote.relatedbid.domain.RelatedBidFactory;
import com.ibm.dsw.quote.relatedbid.process.RelatedBidProcess;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidBaseAction<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 14, 2013 
 */
 
public class RelatedBidsAction extends BaseContractActionHandler {

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
               throws QuoteException, ResultBeanException {
    		RelatedBidsContract csContract = (RelatedBidsContract) contract;
    		
    		RelatedBidProcess process = RelatedBidFactory.singleton().create();
    		List list = null;
    		
    		if(StringUtils.isNotBlank(csContract.getWebQuoteNum())){
    			list = process.searchRelatedbyNum(csContract.getWebQuoteNum());
    		}
			
            handler.addObject(ParamKeys.RELATED_BIDS_LIST, list);
            
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_RELATED_BIDS);
            
            return handler.getResultBean();
		}
}
